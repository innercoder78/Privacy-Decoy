#!/usr/bin/env python3
"""Standard-library tests for the neutral APK artifact inventory."""
import importlib.util, json, tempfile, unittest, zipfile
from pathlib import Path
from unittest import mock
MODULE_PATH = Path(__file__).with_name("artifact-analyzer.py")
SPEC = importlib.util.spec_from_file_location("artifact_analyzer", MODULE_PATH)
analyzer = importlib.util.module_from_spec(SPEC); SPEC.loader.exec_module(analyzer)
def apk(path, dex=b"dex\n035\0controlled"):
    with zipfile.ZipFile(path, "w") as archive:
        archive.writestr("classes.dex", dex); archive.writestr("AndroidManifest.xml", b"synthetic")
class ArtifactAnalyzerTest(unittest.TestCase):
    def test_deterministic_hash_inventory(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); base=root/'base.apk'; copy=root/'copy.apk'; apk(base); copy.write_bytes(base.read_bytes())
            a=analyzer.analyze(base, [], use_sdk=False); b=analyzer.analyze(copy, [], use_sdk=False)
            self.assertEqual(a['generation_id'], b['generation_id']); self.assertEqual(a['artifacts'][0]['sha256'], b['artifacts'][0]['sha256'])
            apk(copy, b'changed'); self.assertNotEqual(a['generation_id'], analyzer.analyze(copy, [], use_sdk=False)['generation_id'])
    def test_presence_inventory_and_unknown_boundary(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/'signals.apk'
            with zipfile.ZipFile(path,'w') as z:
                z.writestr('AndroidManifest.xml',b'x'); z.writestr('classes.dex',b'InMemoryDexClassLoader System.loadLibrary ProcessBuilder WebView'); z.writestr('lib/arm64-v8a/x.so',b'ELF'); z.writestr('assets/code.jar',b'x')
            result=analyzer.analyze(path, [], use_sdk=False); codes={x['code'] for x in result['findings']}
            self.assertTrue({'IN_MEMORY_DEX_LOADER_REFERENCE','NATIVE_LOAD_REFERENCE','SUBPROCESS_REFERENCE','WEBVIEW_REFERENCE','APP_CONTROLLED_NATIVE_PRESENT','OPAQUE_EXECUTABLE_PAYLOAD'} <= codes)
            self.assertEqual('Unknown', result['capability_coverage']); self.assertEqual('VALID', result['structural_status'])
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
    def test_split_metadata_mismatch_invalid(self):
        base={'role':'base','package':'p','signer_sha256_digests':['s'],'version_code':'1','version_name':'v','split_name':None,'split_identity_state':'absent'}
        split=dict(base,role='split',package='q',split_name='feature',split_identity_state='present'); findings=[]
        self.assertFalse(analyzer.evaluate_metadata([base,split],findings)); self.assertIn('ARTIFACT_PACKAGE_MISMATCH',{x['code'] for x in findings})
    def test_signer_digests_preserved(self):
        output='Signer #1 certificate SHA-256 digest: AA:BB\nSigner #2 certificate SHA-256 digest: DD:CC\n'
        self.assertEqual(['aabb','ddcc'], analyzer.signer_digests(output))
    def test_json_has_no_absolute_path(self):
        with tempfile.TemporaryDirectory() as d:
            path=Path(d)/'public.apk'; apk(path); encoded=json.dumps(analyzer.analyze(path,[],use_sdk=False))
            self.assertNotIn(d,encoded); self.assertIn('public.apk',encoded)
if __name__ == '__main__': unittest.main(verbosity=2)
