#!/usr/bin/env bash
set -euo pipefail
# One disposable experiment, no security-assertion retries, no guest APK install.
root=$(cd "$(dirname "$0")/.." && pwd)
cd "$root"
: "${ANDROID_HOME:?Android SDK required}"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/build-tools/36.0.0:$ANDROID_HOME/platform-tools:$PATH"
tmp=$(mktemp -d)
emulator_pid=''
cleanup() {
  if [[ -n "$emulator_pid" ]]; then
    timeout 5 adb -s emulator-5556 emu kill >/dev/null 2>&1 || true
    kill "$emulator_pid" 2>/dev/null || true
    for _ in $(seq 1 10); do
      kill -0 "$emulator_pid" 2>/dev/null || break
      sleep 1
    done
    kill -KILL "$emulator_pid" 2>/dev/null || true
    wait "$emulator_pid" 2>/dev/null || true
  fi
  rm -rf -- "$tmp"
  # These are only generated APK products of this runner, never source/evidence.
  for directory in app/build probe-app/build test-apps/ag1-precode-fixture/build; do
    if [[ -d "$directory" ]]; then find "$directory" -type f -name '*.apk' -delete; fi
  done
}
trap cleanup EXIT
trap 'exit 1' INT TERM
fixture=test-apps/ag1-precode-fixture/build/outputs/apk/debug/ag1-precode-fixture-debug.apk
./gradlew --no-daemon :test-apps:ag1-precode-fixture:assembleDebug >"$tmp/fixture-build.log" 2>&1 || {
  echo 'AG-1B fixture build failed'; exit 1;
}
python3 tools/ag1-admission-analyzer.py --base "$fixture" --json >"$tmp/admission.json" 2>"$tmp/analyzer.log" || {
  echo 'AG-1B admission analysis failed'; exit 1;
}
python3 - "$tmp/admission.json" "$tmp/identity" <<'PY'
import json, pathlib, re, sys
r = json.loads(pathlib.Path(sys.argv[1]).read_text())
assert r['admission'] == 'EXPERIMENTAL_ELIGIBLE', 'Conservative admission required'
assert any(f['code'] == 'RUNTIME_MEDIATION_UNPROVEN' for f in r['findings']), 'Runtime limitation must remain explicit'
assert r['analyzer_version'] == 'ag1a-2', 'Analyzer generation schema changed'
assert len(r['artifacts']) == 1, 'Single base required'
a = r['artifacts'][0]
assert a['role'] == 'base' and a['dex_entries'] == ['classes.dex'], 'Sole DEX required'
assert not a['native_libraries'], 'Java-only fixture required'
assert not any(f['code'] in {'DYNAMIC_DEX_LOADER_REFERENCE','IN_MEMORY_DEX_LOADER_REFERENCE',
    'PATH_CLASS_LOADER_REFERENCE','NATIVE_LOAD_REFERENCE','OPAQUE_EXECUTABLE_PAYLOAD'} for f in r['findings']), 'Unexpected executable mechanism'
assert all(re.fullmatch('[0-9a-f]{64}', s) for s in (r['generation_id'],a['sha256'])), 'Bounded identity required'
pathlib.Path(sys.argv[2]).write_text(r['generation_id']+'\n'+a['sha256']+'\n')
PY
mapfile -t identity < "$tmp/identity"
./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest >"$tmp/manager-build.log" 2>&1 || {
  echo 'AG-1B manager/test build failed'; exit 1;
}
python3 - "$fixture" "$tmp/admission.json" <<'PY'
import hashlib, json, pathlib, sys, zipfile
expected=json.loads(pathlib.Path(sys.argv[2]).read_text())['artifacts'][0]['sha256']
source=pathlib.Path(sys.argv[1]).read_bytes()
asset=pathlib.Path('app/build/generated/ag1Assets/ag1-precode-fixture-debug.apk').read_bytes()
with zipfile.ZipFile('app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk') as z:
    embedded=z.read('assets/ag1-precode-fixture-debug.apk')
assert all(hashlib.sha256(b).hexdigest()==expected for b in (source,asset,embedded)), 'Analyzed/embedded bytes mismatch'
PY
[[ "$(uname -s)" == Linux && -r /dev/kvm && -w /dev/kvm ]] || { echo 'AG-1B Linux/KVM unavailable'; exit 1; }
export ANDROID_USER_HOME="$tmp/android-user"
export ANDROID_EMULATOR_HOME="$ANDROID_USER_HOME"
export ANDROID_AVD_HOME="$tmp/avd"
mkdir -p "$ANDROID_USER_HOME" "$ANDROID_AVD_HOME"
echo no | avdmanager create avd --name privacy_decoy_ag1b --path "$ANDROID_AVD_HOME/ag1b.avd" \
  --package 'system-images;android-35;google_apis;x86_64' --device pixel_2 >"$tmp/avd.log" 2>&1
"$ANDROID_HOME/emulator/emulator" -avd privacy_decoy_ag1b -port 5556 -no-window -no-audio \
  -no-boot-anim -no-snapshot -wipe-data -gpu software -memory 2048 -cores 2 -partition-size 2048 \
  >"$tmp/emulator.log" 2>&1 &
emulator_pid=$!
export ANDROID_SERIAL=emulator-5556
adb start-server >"$tmp/adb.log" 2>&1
booted=false
for _ in $(seq 1 120); do
  kill -0 "$emulator_pid" 2>/dev/null || { echo 'AG-1B emulator startup failed'; exit 1; }
  if [[ "$(timeout 3 adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)" == 1 ]]; then booted=true; break; fi
  sleep 2
done
[[ "$booted" == true ]] || { echo 'AG-1B emulator boot timed out'; exit 1; }
[[ "$(adb shell getprop ro.build.version.sdk | tr -d '\r')" == 35 ]]
[[ "$(adb shell getprop ro.product.cpu.abi | tr -d '\r')" == x86_64 ]]
adb install -t app/build/outputs/apk/debug/app-debug.apk >"$tmp/install.log" 2>&1
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk >>"$tmp/install.log" 2>&1
[[ -z "$(adb shell pm path com.privacydecoy.ag1.precode | tr -d '\r')" ]]
instrumentation_exit=0
timeout 240 adb shell am instrument -w -r -e suite ag1runtime \
  -e ag1Generation "${identity[0]}" -e ag1Sha "${identity[1]}" -e ag1Admission EXPERIMENTAL_ELIGIBLE \
  com.privacydecoy.app.test/com.privacydecoy.research.PrototypeTestRunner >"$tmp/instrumentation.log" 2>&1 || instrumentation_exit=$?
python3 - "$tmp/instrumentation.log" "$instrumentation_exit" <<'PY'
import pathlib, re, sys
# Never echo transcript fragments. Even printable text can contain private data.
names = sorted((
    'testLaunchPolicyHardStopsBeforeBinding',
    'testArtifactAndGenerationMismatchBeforeBinding',
    'testUpdatedGenerationCannotInheritConsent',
    'testMissingPrerequisiteNeverReady',
    'testRunBeforeReadyHasZeroGuestClassLoads',
    'testRevocationAndReplayBeforeTransfer',
    'testProcessDeathInvalidatesAuthorization',
    'testExactExperimentalExecutionAfterBarrier',
))
safe_messages = {
    'death setup bind failed', 'death setup arm failed', 'death setup observation failed',
    'death observation failed', 'replacement bind failed', 'replacement arm failed',
    'stale claim probe failed', 'replacement observation failed',
    'bounded analyzer arguments missing', 'real admission must be Experimental only',
    'embedded artifact differs from analyzer', 'embedded generation differs from analyzer',
    'invalid launch crossed manager gate', 'denied state exposed guest bytes',
    'negative evidence missing', 'negative path reached guest code',
    'negative path transferred guest bytes', 'bootstrap denied',
    'missing prerequisite reached READY', 'missing prerequisite recorded READY',
    'unknown prerequisites reached READY', 'service accepted pre-READY RUN',
    'revoked service accepted RUN', 'replayed session accepted RUN', 'revoked session rearmed',
    'death retained authority', 'replacement denied', 'replacement reused process',
    'dead claim regained authority', 'isolated identity equals manager',
    'exact Experimental bootstrap denied', 'controlled execution failed',
    'pre-code ordering violated', 'fixture events missing', 'controlled event preceded barrier',
    'execution was not exactly once', 'second invocation authorized',
    'service replay authorized', 'replay ran guest again', 'controlled guest must never be installed',
}
safe_exceptions = {
    'IllegalStateException', 'IllegalArgumentException', 'SecurityException',
    'NullPointerException', 'RemoteException', 'DeadObjectException', 'TimeoutException',
    'ExecutionException', 'InterruptedException', 'IOException', 'FileNotFoundException',
    'ClassNotFoundException', 'NoSuchMethodException', 'InvocationTargetException',
    'IllegalAccessException', 'ErrnoException', 'RuntimeException', 'unknown',
}
def detail(value, name):
    prefix = name + ': '
    if not value.startswith(prefix):
        return 'redacted'
    value = value[len(prefix):]
    if len(value) > 100 or re.fullmatch(r'[A-Za-z :\-]+', value) is None:
        return 'redacted'
    if value in safe_messages:
        return value
    prefix = 'platform-or-harness-exception:'
    if value.startswith(prefix) and value[len(prefix):] in safe_exceptions:
        return value
    return 'redacted'

malformed = False
try:
    with pathlib.Path(sys.argv[1]).open('rb') as source:
        raw = source.read(256 * 1024 + 1)
    malformed = len(raw) > 256 * 1024
    text = raw[:256 * 1024].decode('utf-8', errors='strict')
except (OSError, UnicodeError):
    text = ''; malformed = True
failed_marker = 'INSTRUMENTATION_FAILED' in text
events, totals, finals = [], [], []
pending = {}
observed = {name: [] for name in names}
details = {name: [] for name in names}
known_codes = {'1', '0', '-1', '-2', '-3', '-4'}
def status_label(value):
    # Preserve bounded numeric status codes, never arbitrary protocol payloads.
    return value if re.fullmatch(r'-?(?:0|[1-9][0-9]?)', value) else 'invalid'

unknown_test = False
for line in text.splitlines():
    if not line:
        continue
    if len(line) > 2048:
        malformed = True
        continue
    if line.startswith('INSTRUMENTATION_STATUS: '):
        key, separator, value = line[len('INSTRUMENTATION_STATUS: '):].partition('=')
        if not separator or key not in {'id', 'class', 'test', 'numtests', 'current', 'stream', 'stack'} or key in pending:
            malformed = True
        else:
            pending[key] = value
    elif line.startswith('INSTRUMENTATION_STATUS_CODE: '):
        code = line[len('INSTRUMENTATION_STATUS_CODE: '):]
        name = pending.get('test')
        if len(events) >= 32:
            malformed = True
            pending = {}
            continue
        events.append((name, code))
        if name not in observed:
            unknown_test = True
        else:
            observed[name].append(status_label(code))
            if code in {'-1', '-2'} and len(details[name]) < 2:
                details[name].append(detail(pending.get('stack', ''), name))
            if (pending.get('id') != 'PrivacyDecoyPrototype'
                    or pending.get('class') != 'com.privacydecoy.research.ag1.Ag1RuntimeTests'
                    or pending.get('numtests') != '8'
                    or pending.get('current') != str(names.index(name) + 1)):
                malformed = True
        if code not in known_codes or finals or totals:
            malformed = True
        pending = {}
    elif line.startswith('INSTRUMENTATION_RESULT: stream='):
        value = line[len('INSTRUMENTATION_RESULT: stream='):]
        match = re.fullmatch(r'Tests run: ([0-8]), Failures: ([0-8])', value)
        totals.append(match.groups() if match else None)
        if not match or pending or finals:
            malformed = True
    elif line.startswith('INSTRUMENTATION_CODE: '):
        value = line[len('INSTRUMENTATION_CODE: '):]
        finals.append(status_label(value))
        if pending:
            malformed = True
    else:
        # Unknown fields, continuations, stack traces and raw command errors never print.
        malformed = True
if pending:
    malformed = True

# Bounded output: eight fixed names, at most eight status labels and two details each.
for name in names:
    codes = observed[name]
    started, passed = '1' in codes, '0' in codes
    failed = any(code in {'-1', '-2'} for code in codes)
    result = 'FAIL' if failed else 'PASS' if codes == ['1', '0'] else 'MISSING' if not codes else 'INCOMPLETE'
    statuses = ','.join(codes[:8]) or 'none'
    if len(codes) > 8:
        statuses += ',overflow'
    print(f'AG1B_TEST name={name} codes={statuses} started={str(started).lower()} '
          f'passed={str(passed).lower()} failed={str(failed).lower()} result={result}')
    for value in details[name]:
        print('AG1B_DETAIL ' + value)
if len(totals) == 1 and totals[0] is not None:
    print(f'AG1B_TOTAL Tests run: {totals[0][0]}, Failures: {totals[0][1]}')
else:
    print('AG1B_TOTAL missing-or-invalid')
print('AG1B_FINAL code=' + (finals[0] if len(finals) == 1 else 'missing-or-invalid')
      + ' instrumentation_failed=' + str(failed_marker).lower())

reasons = []
if any(code in {'-1', '-2'} for codes in observed.values() for code in codes):
    reasons.append('device-test-failure')
if unknown_test or any(codes.count('1') != 1 or len(codes) != 2 for codes in observed.values()):
    reasons.append('test-count-invalid')
expected = [(name, code) for name in names for code in ('1', '0')]
if malformed or events != expected:
    reasons.append('status-sequence-invalid')
if totals != [('8', '0')]:
    reasons.append('test-count-invalid')
if finals != ['-1'] or failed_marker or len(sys.argv) != 3 or sys.argv[2] != '0':
    reasons.append('instrumentation-incomplete')
for reason in dict.fromkeys(reasons):
    print('AG1B_RESULT ' + reason)
if reasons:
    raise SystemExit(1)
print('AG1B_RESULT pass')
print('AG-1B: eight device tests, zero failures/skips; API 35 Google APIs x86_64 debug; Experimental only.')
PY
[[ -z "$(adb shell pm path com.privacydecoy.ag1.precode | tr -d '\r')" ]]
