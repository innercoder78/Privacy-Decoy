#!/usr/bin/env python3
"""Standard-library tests for the fail-honest APK artifact inventory."""
import importlib.util, json, os, subprocess, sys, tempfile, unittest, zipfile
from pathlib import Path
from unittest.mock import patch
MODULE_PATH = Path(__file__).with_name("artifact-analyzer.py")
SPEC = importlib.util.spec_from_file_location("artifact_analyzer", MODULE_PATH)
analyzer = importlib.util.module_from_spec(SPEC); SPEC.loader.exec_module(analyzer)
def apk(path, dex=b"dex\n035\0controlled"):
    with zipfile.ZipFile(path, "w") as archive:
        archive.writestr("classes.dex", dex); archive.writestr("AndroidManifest.xml", b"synthetic")
def metadata(role="base", **changes):
    value={"role":role,"package":"p","signer_sha256_digests":["s"],"version_code":"1",
           "version_name":None,"split_name":None,"split_identity_state":"absent"}
    value.update(changes); return value
class ArtifactAnalyzerTest(unittest.TestCase):
    def test_deterministic_hash_inventory_is_structurally_unknown_without_sdk(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); base=root/'base.apk'; copy=root/'copy.apk'; apk(base); copy.write_bytes(base.read_bytes())
            a=analyzer.analyze(base, [], use_sdk=False); b=analyzer.analyze(copy, [], use_sdk=False)
            self.assertEqual(a['generation_id'], b['generation_id']); self.assertEqual(a['artifacts'][0]['sha256'], b['artifacts'][0]['sha256'])
            self.assertEqual('UNKNOWN',a['structural_status'])
            apk(copy, b'changed'); self.assertNotEqual(a['generation_id'], analyzer.analyze(copy, [], use_sdk=False)['generation_id'])
    def test_presence_inventory_and_unknown_boundary(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/'signals.apk'
            with zipfile.ZipFile(path,'w') as z:
                z.writestr('AndroidManifest.xml',b'x'); z.writestr('classes.dex',b'InMemoryDexClassLoader System.loadLibrary ProcessBuilder WebView'); z.writestr('lib/arm64-v8a/x.so',b'ELF'); z.writestr('assets/code.jar',b'x')
            result=analyzer.analyze(path, [], use_sdk=False); codes={x['code'] for x in result['findings']}
            self.assertTrue({'IN_MEMORY_DEX_LOADER_REFERENCE','NATIVE_LOAD_REFERENCE','SUBPROCESS_REFERENCE','WEBVIEW_REFERENCE','APP_CONTROLLED_NATIVE_PRESENT','OPAQUE_EXECUTABLE_PAYLOAD'} <= codes)
            self.assertEqual('Unknown', result['capability_coverage']); self.assertEqual('UNKNOWN', result['structural_status'])
    def test_split_completeness_always_unknown_without_positive_evidence(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); base=root/'base.apk'; split=root/'split.apk'; apk(base); apk(split,b'split')
            self.assertEqual('Unknown', analyzer.analyze(base, [], use_sdk=False)['split_completeness'])
            self.assertEqual('Unknown', analyzer.analyze(base, [split], use_sdk=False)['split_completeness'])
            self.assertNotEqual('N/A', analyzer.analyze(base, [], use_sdk=False)['split_completeness'])
    def test_absence_never_claims_safety(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/'plain.apk'; apk(path); result=analyzer.analyze(path, [], use_sdk=False)
            self.assertEqual('Unknown', result['capability_coverage']); self.assertIn('RUNTIME_MEDIATION_UNPROVEN',{x['code'] for x in result['findings']})
            self.assertNotIn('safe', json.dumps(result).lower())
    def test_malformed_and_missing_fail_safely(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/'bad.apk'; path.write_bytes(b'bad'); result=analyzer.analyze(path, [], use_sdk=False)
            self.assertEqual('INVALID',result['structural_status']); self.assertIn('APK_PARSE_FAILED',{x['code'] for x in result['findings']})
        with self.assertRaises(FileNotFoundError): analyzer.analyze('/missing/apk',[],use_sdk=False)
    def test_required_metadata_controls_valid_unknown_and_invalid(self):
        complete=metadata(); findings=[]
        self.assertTrue(analyzer.evaluate_metadata([complete],findings))
        self.assertEqual('VALID',analyzer.structural_status(True,True,[complete]))
        for key in ('package','signer_sha256_digests','version_code'):
            with self.subTest(key=key):
                incomplete=metadata(**{key:None}); findings=[]
                self.assertTrue(analyzer.evaluate_metadata([incomplete],findings))
                self.assertEqual('UNKNOWN',analyzer.structural_status(True,True,[incomplete]))
        unknown_identity=metadata(split_identity_state='unknown'); self.assertEqual('UNKNOWN',analyzer.structural_status(True,True,[unknown_identity]))
        self.assertEqual('VALID',analyzer.structural_status(True,True,[metadata(version_name=None)]))
    def test_positive_mismatches_are_invalid(self):
        for key,value,code in (('package','q','ARTIFACT_PACKAGE_MISMATCH'),('signer_sha256_digests',['q'],'ARTIFACT_SIGNER_MISMATCH'),('version_code','2','ARTIFACT_VERSION_MISMATCH')):
            base=metadata(); split=metadata('split',split_name='feature',split_identity_state='present',**{key:value}); findings=[]
            self.assertFalse(analyzer.evaluate_metadata([base,split],findings)); self.assertIn(code,{x['code'] for x in findings})
            self.assertEqual('INVALID',analyzer.structural_status(True,False,[base,split]))
    def test_role_and_duplicate_split_contradictions_are_invalid(self):
        cases=([metadata(split_name='feature',split_identity_state='present')],
               [metadata(),metadata('split',split_identity_state='absent')],
               [metadata(),metadata('split',split_name='same',split_identity_state='present'),metadata('split',split_name='same',split_identity_state='present')])
        for records in cases:
            findings=[]; self.assertFalse(analyzer.evaluate_metadata(records,findings)); self.assertEqual('INVALID',analyzer.structural_status(True,False,records))
    def test_signer_digests_preserved(self):
        output=('Signer #1 certificate DN: C=US, O=Android, CN=Android Debug\n'
                'Signer #1 certificate SHA-256 digest: ' + 'ab' * 32 + '\n'
                'Signer (minSdkVersion=33, maxSdkVersion=35) certificate SHA-256 digest: ' + ':'.join(['CD'] * 32) + '\n'
                'Signer #1 public key SHA-256 digest: 00:11\n')
        self.assertEqual(['ab' * 32, 'cd' * 32], analyzer.signer_digests(output))
    def test_unrecognized_or_incomplete_signer_evidence_is_unknown(self):
        # Observed official 37.0.0 output; deliberately not the pinned grammar.
        for output in ('V2 Signer: certificate SHA-256 digest: ' + 'ab' * 32,
                       'Signer #1 certificate SHA-256 digest: AA:BB',
                       'warning: Signer #1 certificate SHA-256 digest: ' + 'ab' * 32,
                       'Signer #1 certificate SHA-256 digest: ' + 'ab' * 32 + '\n'
                       'Signer #2 certificate SHA-256 digest: unavailable'):
            with self.subTest(output=output):
                self.assertEqual([], analyzer.signer_digests(output))
                self.assertEqual('UNKNOWN', analyzer.structural_status(
                    True, True, [metadata(signer_sha256_digests=None)]))
    def test_pinned_signer_wins_over_path_and_newer_sdk(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); executable='apksigner.bat' if os.name == 'nt' else 'apksigner'
            pinned=root/'build-tools'/'36.0.0'/executable
            newer=root/'build-tools'/'37.0.0'/executable
            for path in (pinned,newer):
                path.parent.mkdir(parents=True); path.touch()
            for variable in ('ANDROID_HOME','ANDROID_SDK_ROOT'):
                with patch.dict(os.environ,{variable:d},clear=True), patch.object(analyzer.shutil,'which',return_value=str(newer)):
                    self.assertEqual(str(pinned), analyzer.tool('apksigner'))
            pinned.unlink()
            with patch.dict(os.environ,{'ANDROID_HOME':d},clear=True), patch.object(analyzer.shutil,'which',return_value=str(newer)):
                with self.assertRaises(RuntimeError): analyzer.tool('apksigner')
    def test_successful_stdout_and_failed_commands_are_distinct(self):
        line='Signer #1 certificate SHA-256 digest: ' + 'ab' * 32
        self.assertEqual(["ab" * 32], analyzer.signer_digests(analyzer.command(
            [sys.executable,'-c','print(' + repr(line) + ')'])))
        # Stderr remains diagnostics; the observed pinned tool writes to stdout.
        self.assertEqual('', analyzer.command([sys.executable,'-c',
            'import sys; print(' + repr(line) + ', file=sys.stderr)']))
        with self.assertRaises(RuntimeError):
            analyzer.command([sys.executable,'-c','import sys; print(' + repr(line) + '); sys.exit(1)'])
    def test_signer_command_failure_does_not_establish_metadata(self):
        for result in (subprocess.CompletedProcess([],1,'Signer #1 certificate SHA-256 digest: ' + 'ab' * 32,''),
                       subprocess.CompletedProcess([],0,'V2 Signer: certificate SHA-256 digest: ' + 'ab' * 32,'')):
            record=metadata(signer_sha256_digests=None)
            with patch.object(analyzer.subprocess,'run',return_value=result):
                self.assertFalse(analyzer.add_sdk_metadata(record,Path('fixture.apk'),'apkanalyzer','apksigner'))
            self.assertIsNone(record['signer_sha256_digests'])
            self.assertEqual('UNKNOWN',analyzer.structural_status(True,True,[record]))
    def test_json_has_no_absolute_path(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/'public.apk'; apk(path); encoded=json.dumps(analyzer.analyze(path,[],use_sdk=False))
            self.assertNotIn(d,encoded); self.assertIn('public.apk',encoded)
if __name__ == '__main__': unittest.main(verbosity=2)
