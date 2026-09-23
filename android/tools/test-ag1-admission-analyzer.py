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
    def test_generation_is_stable_and_split_order_independent(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory); base = root / "base.apk"; a = root / "a.apk"; b = root / "b.apk"
            apk(base); apk(a, b"a"); apk(b, b"b")
            first = analyzer.analyze(base, [a, b], use_sdk=False)
            second = analyzer.analyze(base, [b, a], use_sdk=False)
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

    def test_native_dynamic_and_opaque_signals(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "signals.apk"
            with zipfile.ZipFile(path, "w") as archive:
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
