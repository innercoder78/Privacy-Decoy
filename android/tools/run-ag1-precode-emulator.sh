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
timeout 240 adb shell am instrument -w -r -e suite ag1runtime \
  -e ag1Generation "${identity[0]}" -e ag1Sha "${identity[1]}" -e ag1Admission EXPERIMENTAL_ELIGIBLE \
  com.privacydecoy.app.test/com.privacydecoy.research.PrototypeTestRunner >"$tmp/instrumentation.log" 2>&1
python3 - "$tmp/instrumentation.log" <<'PY'
import pathlib, re, sys
text=pathlib.Path(sys.argv[1]).read_text()
codes=re.findall(r'^INSTRUMENTATION_STATUS_CODE: (-?\d+)\s*$',text,re.M)
assert codes == ['1','0']*8, 'Expected eight starts/passes, zero failures or skips'
assert 'Tests run: 8, Failures: 0' in text, 'Missing complete suite result'
assert re.search(r'^INSTRUMENTATION_CODE: -1\s*$',text,re.M), 'Instrumentation did not finish successfully'
assert 'INSTRUMENTATION_FAILED' not in text, 'Instrumentation failed'
print('AG-1B: eight device tests, zero failures/skips; API 35 Google APIs x86_64 debug; Experimental only.')
PY
[[ -z "$(adb shell pm path com.privacydecoy.ag1.precode | tr -d '\r')" ]]
