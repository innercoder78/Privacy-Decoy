#!/usr/bin/env python3
"""Synthetic classifier tests: no emulator outcome is inferred from these fixtures."""
import base64
import hashlib
import importlib.util
import json
from pathlib import Path
import subprocess
import sys
import tempfile
import unittest

SCRIPT = Path(__file__).with_name("managed-profile-evidence.py")
spec = importlib.util.spec_from_file_location("managed_evidence", SCRIPT)
evidence = importlib.util.module_from_spec(spec)
spec.loader.exec_module(evidence)


def digest(value):
    return base64.urlsafe_b64encode(hashlib.sha256(value.encode()).digest()).decode().rstrip("=")


def write(path, values):
    path.write_text("".join(f"{key}={value}\n" for key, value in values.items()), encoding="ascii", newline="\n")


class ClassifierTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        setup = dict(schema="2", setup_complete="true", profile_owner="true", managed_profile="true",
                     receiver_disabled="true", controller_uid="1010010", user_serial="17")
        write(self.root / "setup-state", setup)
        write(self.root / "setup-state-after", setup)
        write(self.root / "checkpoint", dict(schema="2", dpm_success="true", probes_absent="true",
              target_shell_user="10", controller_package_uid="1010010"))
        for index, name in enumerate(evidence.FIXTURES):
            uid = str(10001 + index if index < 2 else 1010000 + index)
            f = dict(schema="2", shell_user="0" if index < 2 else "10", package_uid=uid,
                     existed_before="true", owner_readable_before="true", owner_readable_after="true",
                     before_hash="a" * 64, after_hash="a" * 64)
            if name != "management":
                f.update(nonce_seeded="true", nonce_hash=digest("synthetic-test-nonce"))
                (self.root / name).mkdir()
                for event in evidence.EVENTS:
                    r = dict(schema="2", event=event, uid=uid, user_serial="0" if index < 2 else "17",
                             pid="100", native_uid=uid, native_gid=uid, nonce_source="SEEDED_TEST",
                             nonce_hash=f["nonce_hash"], native_pid_present="true", native_thread="true",
                             property_available="true", property_matches_java_build="true")
                    r.update({key: "PERMISSION_DENIED" for key in evidence.ACCESS})
                    r.update({key: "ACCESSIBLE" for key in evidence.OBSERVATIONS})
                    r.update({key: digest(name + key) for key in evidence.SURFACES})
                    write(self.root / name / ("evidence-" + event), r)
            write(self.root / ("fixture-" + name), f)

    def edit(self, relative, **changes):
        path = self.root / relative
        values = evidence.read(path)
        values.update(changes)
        write(path, values)

    def all_events(self, group, **changes):
        for event in evidence.EVENTS:
            self.edit(group + "/evidence-" + event, **changes)

    def assert_result(self, harness, outcome):
        report = evidence.evaluate(self.root)
        self.assertEqual((report["harness"], report["outcome"]), (harness, outcome))

    def test_complete_slice(self):
        self.assert_result("PASS", "SURVIVED_CURRENT_SLICE")

    def test_namespace_hidden_existing_fixtures(self):
        self.all_events("tenant_a", **{k: "ABSENT" for k in evidence.ACCESS})
        self.assert_result("PASS", "SURVIVED_CURRENT_SLICE")

    def test_every_mandatory_surface_in_either_tenant(self):
        for group in ("tenant_a", "tenant_b"):
            for key in evidence.SURFACES[:7]:
                with self.subTest(group=group, surface=key):
                    self.all_events(group, **{key: digest("parent" + key)})
                    self.assert_result("PASS", "FALSIFIED")
                    self.all_events(group, **{key: digest(group + key)})

    def test_all_java_native_read_write_falsifications_even_later_events(self):
        for group in ("tenant_a", "tenant_b"):
            for key in evidence.ACCESS:
                with self.subTest(group=group, access=key):
                    self.edit(group + "/evidence-service", **{key: "ACCESSIBLE"})
                    self.assert_result("PASS", "FALSIFIED")
                    self.edit(group + "/evidence-service", **{key: "PERMISSION_DENIED"})

    def test_shared_uid_is_architecture_failure(self):
        uid = evidence.read(self.root / "fixture-tenant_a")["package_uid"]
        self.edit("fixture-tenant_b", package_uid=uid)
        self.all_events("tenant_b", uid=uid, native_uid=uid, native_gid=uid)
        self.assert_result("PASS", "FALSIFIED")

    def test_changed_sentinel_is_architecture_failure(self):
        self.edit("fixture-management", after_hash="b" * 64)
        self.assert_result("PASS", "FALSIFIED")

    def test_required_checkpoint_failures(self):
        mutations = [("fixture-tenant_b", "existed_before", "false"),
                     ("fixture-tenant_a", "nonce_seeded", "false"),
                     ("fixture-tenant_b", "nonce_hash", digest("wrong nonce")),
                     ("fixture-management", "owner_readable_before", "false"),
                     ("fixture-tenant_a", "shell_user", "0"),
                     ("setup-state", "profile_owner", "false"),
                     ("setup-state", "receiver_disabled", "false"),
                     ("setup-state", "controller_uid", "20000"),
                     ("checkpoint", "dpm_success", "false"),
                     ("checkpoint", "probes_absent", "false")]
        for path, key, value in mutations:
            with self.subTest(path=path, key=key):
                original = (self.root / path).read_bytes()
                self.edit(path, **{key: value})
                self.assert_result("FAIL", "INCONCLUSIVE")
                (self.root / path).write_bytes(original)

    def test_bad_lifecycle_record_cannot_hide_behind_good_first_record(self):
        changes = dict(uid="55555", native_uid="55555", user_serial="99", surface_0_hash=digest("changed"),
                       nonce_source="HOST", nonce_hash="missing", java_peer_read="BLOCKED",
                       native_management_read="OTHER_ERROR", native_peer_write="OTHER_ERROR",
                       property_available="maybe", native_thread="false", event="unknown", schema="1")
        for group in evidence.GROUPS:
            path = group + "/evidence-service"
            original = (self.root / path).read_bytes()
            for key, value in changes.items():
                with self.subTest(group=group, key=key):
                    self.edit(path, **{key: value})
                    self.assert_result("FAIL", "INCONCLUSIVE")
                    (self.root / path).write_bytes(original)

    def test_missing_record(self):
        (self.root / "tenant_a/evidence-provider").unlink()
        self.assert_result("FAIL", "INCONCLUSIVE")

    def test_missing_field_with_unexpected_replacement(self):
        path = self.root / "tenant_a/evidence-provider"
        values = evidence.read(path)
        del values["java_peer_read"]
        values["unexpected"] = "true"
        write(path, values)
        self.assert_result("FAIL", "INCONCLUSIVE")

    def test_duplicate_event(self):
        self.edit("tenant_a/evidence-service", event="provider")
        self.assert_result("FAIL", "INCONCLUSIVE")

    def test_filename_mismatch(self):
        (self.root / "tenant_a/evidence-service").rename(self.root / "tenant_a/evidence-wrong")
        self.assert_result("FAIL", "INCONCLUSIVE")

    def test_duplicate_key_malformed_and_unsafe_output(self):
        path = self.root / "tenant_a/evidence-service"
        original = path.read_bytes()
        for extra in (b"uid=100\n", b"event=service\n", b"broken\n", b"unsafe=/raw/path\n", b"x=\x1bsecret\n", b"x=\xff\n", b"\v", b"\r"):
            with self.subTest(extra=extra):
                path.write_bytes(original + extra)
                self.assert_result("FAIL", "INCONCLUSIVE")
        path.write_bytes(original)

    def test_missing_nonce_field(self):
        path = self.root / "tenant_a/evidence-service"
        values = evidence.read(path)
        del values["nonce_hash"]
        write(path, values)
        self.assert_result("FAIL", "INCONCLUSIVE")

    def test_wrong_profile_even_when_stable(self):
        self.all_events("tenant_a", user_serial="0")
        self.assert_result("FAIL", "INCONCLUSIVE")

    def test_proc_unknown_is_not_denial(self):
        self.all_events("tenant_a", proc_host="OTHER_ERROR")
        report = evidence.evaluate(self.root)
        self.assert_result("PASS", "SURVIVED_CURRENT_SLICE")
        self.assertEqual(report["observations"]["tenant_a"]["proc_host"], "Unknown")

    def test_cli_both_dimensions_and_exit_status(self):
        for valid in (True, False):
            if not valid:
                (self.root / "tenant_b/evidence-activity").unlink()
            result = subprocess.run([sys.executable, str(SCRIPT), "--root", str(self.root),
                                     "--output", str(self.root / "report.json")], capture_output=True, text=True)
            expected = ("PASS", "SURVIVED_CURRENT_SLICE") if valid else ("FAIL", "INCONCLUSIVE")
            self.assertEqual(result.returncode, 0 if valid else 1)
            self.assertIn("PD_S1_HARNESS=" + expected[0], result.stdout)
            self.assertIn("PD_S1_OUTCOME=" + expected[1], result.stdout)
            report = json.loads((self.root / "report.json").read_text())
            self.assertEqual((report["harness"], report["outcome"]), expected)


if __name__ == "__main__":
    unittest.main()
