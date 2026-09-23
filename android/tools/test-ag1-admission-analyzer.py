#!/usr/bin/env python3
"""Standard-library tests for the AG-1A analyzer model and ZIP scanner."""

import importlib.util
import io
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest
import zipfile

MODULE_PATH = Path(__file__).with_name("ag1-admission-analyzer.py")
SPEC = importlib.util.spec_from_file_location("ag1_admission_analyzer", MODULE_PATH)
analyzer = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(analyzer)

def apk(path, dex=b"dex\n035\0controlled"):
    with zipfile.ZipFile(path, "w") as archive:
        archive.writestr("classes.dex", dex)
        archive.writestr("AndroidManifest.xml", b"synthetic-unit-fixture")

class AdmissionAnalyzerTest(unittest.TestCase):
    def test_generation_ignores_names_and_split_order_but_tracks_bytes(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            base = root / "base.apk"; renamed_base = root / "renamed-base.apk"
            a = root / "a.apk"; renamed_a = root / "renamed-a.apk"
            b = root / "b.apk"; renamed_b = root / "renamed-b.apk"
            apk(base); renamed_base.write_bytes(base.read_bytes())
            apk(a, b"a"); renamed_a.write_bytes(a.read_bytes())
            apk(b, b"b"); renamed_b.write_bytes(b.read_bytes())
            first = analyzer.analyze(base, [a, b], use_sdk=False)
            second = analyzer.analyze(renamed_base, [renamed_b, renamed_a], use_sdk=False)
            self.assertEqual(first["generation_id"], second["generation_id"])
            apk(b, b"changed")
            self.assertNotEqual(first["generation_id"], analyzer.analyze(base, [a, b], use_sdk=False)["generation_id"])

    def test_malformed_is_incompatible_and_never_protected(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "bad.apk"; path.write_bytes(b"not a zip")
            result = analyzer.analyze(path, [], use_sdk=False)
            self.assertEqual("INCOMPATIBLE", result["admission"])
            self.assertIn("APK_PARSE_FAILED", {x["code"] for x in result["findings"]})

    def test_missing_base_is_hard_failure(self):
        with self.assertRaises(FileNotFoundError):
            analyzer.analyze("/definitely/not/an/apk", [], use_sdk=False)

    def test_readable_zip_without_android_manifest_is_incompatible(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "not-an-apk.zip"
            with zipfile.ZipFile(path, "w") as archive:
                archive.writestr("classes.dex", b"dex unit fixture")
            result = analyzer.analyze(path, [], use_sdk=False)
            self.assertEqual("INCOMPATIBLE", result["admission"])
            self.assertIn("APK_MANIFEST_MISSING", {x["code"] for x in result["findings"]})

    def test_native_dynamic_and_opaque_signals(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "signals.apk"
            with zipfile.ZipFile(path, "w") as archive:
                archive.writestr("AndroidManifest.xml", b"synthetic-unit-fixture")
                archive.writestr("classes.dex", b"DexClassLoader System.loadLibrary ProcessBuilder")
                archive.writestr("lib/arm64-v8a/libfixture.so", b"ELF unit fixture")
                archive.writestr("assets/secondary.jar", b"unit payload")
            result = analyzer.analyze(path, [], use_sdk=False)
            codes = {x["code"] for x in result["findings"]}
            self.assertTrue({"APP_CONTROLLED_NATIVE_PRESENT", "DYNAMIC_DEX_LOADER_REFERENCE", "NATIVE_LOAD_REFERENCE", "SUBPROCESS_REFERENCE", "OPAQUE_EXECUTABLE_PAYLOAD"} <= codes)
            self.assertEqual("EXPERIMENTAL_ELIGIBLE", result["admission"])

    def test_absence_never_removes_runtime_unknown_or_grants_protected(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "plain.apk"; apk(path)
            result = analyzer.analyze(path, [], use_sdk=False)
            self.assertEqual("EXPERIMENTAL_ELIGIBLE", result["admission"])
            self.assertIn("RUNTIME_MEDIATION_UNPROVEN", {x["code"] for x in result["findings"]})

    def test_unproven_metadata_is_unknown_and_experimental(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "unknown.apk"; split = Path(directory) / "unknown-split.apk"
            apk(path); apk(split, b"split")
            result = analyzer.analyze(path, [split], use_sdk=False)
            findings = {x["code"]: x for x in result["findings"]}
            self.assertEqual("EXPERIMENTAL_ELIGIBLE", result["admission"])
            self.assertNotEqual("KNOWN_UNSAFE", result["admission"])
            for code in ("ARTIFACT_PACKAGE_UNPROVEN", "ARTIFACT_SIGNER_UNPROVEN", "ARTIFACT_VERSION_UNPROVEN"):
                self.assertEqual("Unknown", findings[code]["coverage_implication"])
                self.assertTrue(findings[code]["blocks_protected"])
                self.assertTrue(findings[code]["permits_experimental"])
            self.assertIn("ARTIFACT_SPLIT_IDENTITY_UNPROVEN", findings)

    def test_positive_metadata_mismatches_are_incompatible(self):
        template = {"role": "base", "package": "p", "signer_sha256": "s", "version_code": "1", "version_name": "one", "split_name": None}
        for key in ("package", "signer_sha256", "version_code", "version_name"):
            with self.subTest(key=key):
                base = dict(template)
                split = dict(template, role="split", split_name="config.test")
                split[key] = "different"
                findings = []
                compatible = analyzer.evaluate_metadata([base, split], findings)
                self.assertFalse(compatible)
                self.assertEqual("INCOMPATIBLE", analyzer.classify(compatible, findings))

    def test_classifier_represents_four_outcomes_without_conflation(self):
        unsupported = [analyzer.finding("UNSUPPORTED_CAPABILITY", "blocked capability", "test", "Unsupported", False)]
        self.assertEqual("INCOMPATIBLE", analyzer.classify(False, []))
        self.assertEqual("KNOWN_UNSAFE", analyzer.classify(True, [], mandatory_bypass=True))
        self.assertEqual("PROTECTED_ELIGIBLE", analyzer.classify(True, [], runtime_proven=True))
        self.assertEqual("EXPERIMENTAL_ELIGIBLE", analyzer.classify(True, unsupported))
        self.assertNotEqual("KNOWN_UNSAFE", analyzer.classify(True, unsupported))
        self.assertEqual(set(analyzer.OUTCOMES), {"PROTECTED_ELIGIBLE", "EXPERIMENTAL_ELIGIBLE", "KNOWN_UNSAFE", "INCOMPATIBLE"})

    def test_json_model_has_no_absolute_path(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "public-name.apk"; apk(path)
            encoded = json.dumps(analyzer.analyze(path, [], use_sdk=False), sort_keys=True)
            self.assertNotIn(directory, encoded)
            self.assertIn('"basename": "public-name.apk"', encoded)

if __name__ == "__main__":
    unittest.main(verbosity=2)
