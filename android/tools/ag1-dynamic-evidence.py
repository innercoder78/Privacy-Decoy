#!/usr/bin/env python3
"""Strict, bounded AG-1C instrumentation protocol; never print transcript fragments."""
import pathlib
import re
import sys

CASES = {
    'testChangedBytes': 'CHANGED:DENIED:0000:0',
    'testDirectAndroidLoader': None,
    'testExactIdentity': 'EXACT:AUTHORIZED:1111:0',
    'testReplay': 'REPLAY:DENIED:0000:0',
    'testRevokedSession': 'REVOKED:DENIED:0000:0',
    'testStaleArtifact': 'STALE_ARTIFACT:DENIED:0000:0',
    'testStaleSession': 'STALE_SESSION:DENIED:0000:0',
    'testUnknownIdentity': 'UNKNOWN:DENIED:0000:0',
}
REASONS = ('COMPLETE', 'SECURITY_EXCEPTION', 'CLASS_NOT_FOUND', 'LINKAGE_ERROR',
           'REFLECTION_OR_RUNTIME_EXCEPTION', 'FIXED_VALUE_MISMATCH')


def parse(raw, exit_code, observed=None):
    if len(raw) > 65536:
        raise ValueError()
    text = raw.decode('utf-8', errors='strict')
    fields, events, evidence = {}, [], {}
    total = final = False
    current = None
    for line in text.splitlines():
        if not line.strip():
            continue
        if line.startswith('INSTRUMENTATION_STATUS: '):
            key, sep, value = line[24:].partition('=')
            if not sep or key in fields or key not in {'id', 'class', 'test', 'numtests', 'current', 'stream', 'ag1c'}:
                raise ValueError()
            fields[key] = value
        elif line.startswith('INSTRUMENTATION_STATUS_CODE: '):
            code = line[29:]
            if total or final:
                raise ValueError()
            if code == '2':
                if set(fields) != {'ag1c'} or current is None or current in evidence:
                    raise ValueError()
                value = fields['ag1c']
                expected = CASES[current]
                if expected is not None and value != expected:
                    raise ValueError()
                if expected is None:
                    match = re.fullmatch(r'DIRECT:OBSERVED:(0000|1000|1100|1110|1111):([0-5])', value)
                    if not match or (match[2] == '0' and match[1] != '1111'):
                        raise ValueError()
                evidence[current] = value
                if observed is not None:
                    observed[current] = value
            elif code in {'1', '0'}:
                name = fields.get('test')
                if name not in CASES or fields.get('id') != 'PrivacyDecoyPrototype' or fields.get('class') != 'com.privacydecoy.research.ag1.Ag1DynamicCodeTests':
                    raise ValueError()
                if fields.get('numtests') != '8' or fields.get('current') != str(sorted(CASES).index(name) + 1):
                    raise ValueError()
                if code == '1':
                    if current is not None or set(fields) != {'id', 'class', 'test', 'numtests', 'current'}:
                        raise ValueError()
                    current = name
                else:
                    if current != name or name not in evidence or fields.get('stream') != '.' or set(fields) != {'id', 'class', 'test', 'numtests', 'current', 'stream'}:
                        raise ValueError()
                    current = None
                events.append((name, code))
            else:
                raise ValueError()
            fields = {}
        elif line == 'INSTRUMENTATION_RESULT: stream=Tests run: 8, Failures: 0':
            if total or final or fields or current is not None:
                raise ValueError()
            total = True
        elif line == 'INSTRUMENTATION_CODE: -1':
            if not total or final or fields:
                raise ValueError()
            final = True
        else:
            raise ValueError()
    if exit_code != '0' or fields or not final or events != [(name, code) for name in sorted(CASES) for code in ('1', '0')] or set(evidence) != set(CASES):
        raise ValueError()
    return evidence


def main():
    evidence = {}
    valid = True
    try:
        with pathlib.Path(sys.argv[1]).open('rb') as source:
            parse(source.read(65537), sys.argv[2], evidence)
    except (OSError, ValueError, IndexError):
        valid = False
    for name in sorted(evidence):
        scenario, decision, stages, reason = evidence[name].split(':')
        print(f'AG1C_CASE {scenario} {decision}')
        for stage, occurred in zip(('LOADER_CONSTRUCTED', 'CLASS_RESOLVED', 'STATIC_INITIALIZED', 'ENTRY_INVOKED'), stages):
            print(f'AG1C_STAGE {scenario} {stage}={occurred}')
        print(f'AG1C_CATEGORY {scenario} {REASONS[int(reason)]}')
    if not valid:
        print('AG1C_RESULT INVALID_OR_FAILED_INSTRUMENTATION PARTIAL_OBSERVATIONS_ONLY')
        return 1
    if evidence['testDirectAndroidLoader'] == 'DIRECT:OBSERVED:1111:0':
        print('AG1C_INTERPRETATION TESTED_DIRECT_PATH_NOT_MEDIATED')
    else:
        print('AG1C_INTERPRETATION DIRECT_FAILURE_REQUIRES_CAUSE_REVIEW_NOT_MEDIATION_EVIDENCE')
    print('AG1C_RESULT OBSERVATION_COMPLETE EXPERIMENTAL_ONLY')
    return 0


if __name__ == '__main__':
    sys.exit(main())
