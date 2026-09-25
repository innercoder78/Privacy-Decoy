#!/usr/bin/env python3
import importlib.util
import pathlib
import unittest

spec = importlib.util.spec_from_file_location('evidence', pathlib.Path(__file__).with_name('ag1-dynamic-evidence.py'))
evidence = importlib.util.module_from_spec(spec)
spec.loader.exec_module(evidence)


def transcript(direct='DIRECT:OBSERVED:1111:0'):
    lines = []
    for i, name in enumerate(sorted(evidence.CASES), 1):
        fields = {'id': 'PrivacyDecoyPrototype', 'class': 'com.privacydecoy.research.ag1.Ag1DynamicCodeTests',
                  'test': name, 'numtests': '8', 'current': str(i)}
        for code in ('1', '0'):
            if code == '0':
                lines += ['INSTRUMENTATION_STATUS: ag1c=' + (evidence.CASES[name] or direct), 'INSTRUMENTATION_STATUS_CODE: 2']
                fields['stream'] = '.'
            lines += [f'INSTRUMENTATION_STATUS: {key}={value}' for key, value in fields.items()]
            lines += ['INSTRUMENTATION_STATUS_CODE: ' + code]
    return ('\n'.join(lines) + '\nINSTRUMENTATION_RESULT: stream=Tests run: 8, Failures: 0\n\nINSTRUMENTATION_CODE: -1\n').encode()


class EvidenceTests(unittest.TestCase):
    def test_success(self):
        self.assertEqual(len(evidence.parse(transcript(), '0')), 8)

    def test_direct_failures_are_observations(self):
        for stages, category in [('0000', 1), ('1000', 2), ('1100', 3), ('1110', 4), ('1110', 5), ('1111', 5)]:
            with self.subTest(stages=stages):
                self.assertEqual(len(evidence.parse(transcript(f'DIRECT:OBSERVED:{stages}:{category}'), '0')), 8)

    def test_malformed_and_missing_never_pass(self):
        original = transcript()
        mutations = [original[:-26], original + b'INSTRUMENTATION_CODE: -1\n', b'x' * 65537,
            original.replace(b'CODE: 0', b'CODE: -2', 1),
            original.replace(b'CODE: 0', b'CODE: -3', 1),
            original.replace(b'CHANGED:DENIED:0000:0', b'CHANGED:DENIED:1000:0'),
            original.replace(b'DIRECT:OBSERVED:1111:0', b'DIRECT:OBSERVED:0100:0'),
            original.replace(b'DIRECT:OBSERVED:1111:0', b'DIRECT:OBSERVED:0000:0'),
            original.replace(b'current=1', b'current=2', 1),
            original.replace(b'testChangedBytes', b'testUnknownName', 1),
            original.replace(b'Failures: 0', b'Failures: 1'),
            original.replace(b'ag1c=CHANGED', b'secret=CHANGED', 1),
            original + b'arbitrary exception or private text\n',
            original.replace(b'INSTRUMENTATION_STATUS_CODE: 2', b'INSTRUMENTATION_STATUS: ag1c=duplicate\nINSTRUMENTATION_STATUS_CODE: 2', 1)]
        for raw in mutations:
            with self.subTest(size=len(raw)):
                with self.assertRaises(ValueError): evidence.parse(raw, '0')

    def test_timeout(self):
        with self.assertRaises(ValueError): evidence.parse(transcript(), '124')

    def test_partial_observations_preserved_without_success(self):
        observed = {}
        with self.assertRaises(ValueError): evidence.parse(transcript() + b'private text\n', '0', observed)
        self.assertEqual(observed['testDirectAndroidLoader'], 'DIRECT:OBSERVED:1111:0')
        self.assertNotIn('private text', str(observed))


if __name__ == '__main__':
    unittest.main()
