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
  # These are only generated executable products of this runner, never source/evidence.
  for directory in app/build probe-app/build test-apps/ag1-precode-fixture/build test-apps/ag1-secondary-dex-fixture/build; do
    if [[ -d "$directory" ]]; then find "$directory" -type f \( -name '*.apk' -o -name '*.dex' -o -name '*.jar' -o -name '*.class' \) -delete; fi
  done
}
trap cleanup EXIT
trap 'exit 1' INT TERM
fixture=test-apps/ag1-precode-fixture/build/outputs/apk/dynamic/ag1-precode-fixture-dynamic.apk
./gradlew --no-daemon :test-apps:ag1-precode-fixture:assembleDebug :test-apps:ag1-precode-fixture:assembleDynamic >"$tmp/fixture-build.log" 2>&1 || {
  echo 'AG-1C fixture build failed'; exit 1;
}
python3 tools/ag1-admission-analyzer.py --base "$fixture" --json >"$tmp/admission.json" 2>"$tmp/analyzer.log" || {
  echo 'AG-1C admission analysis failed'; exit 1;
}
if ! python3 - "$tmp/admission.json" "$tmp/identity" 2>"$tmp/identity-error.log" <<'PY'
import json, pathlib, re, sys
r = json.loads(pathlib.Path(sys.argv[1]).read_text())
assert r['admission'] == 'EXPERIMENTAL_ELIGIBLE', 'Conservative admission required'
assert any(f['code'] == 'RUNTIME_MEDIATION_UNPROVEN' for f in r['findings']), 'Runtime limitation must remain explicit'
assert r['analyzer_version'] == 'ag1a-2', 'Analyzer generation schema changed'
assert len(r['artifacts']) == 1, 'Single base required'
a = r['artifacts'][0]
assert a['role'] == 'base' and a['dex_entries'] == ['classes.dex'], 'Sole DEX required'
assert not a['native_libraries'], 'Java-only fixture required'
assert any(f['code'] == 'IN_MEMORY_DEX_LOADER_REFERENCE' for f in r['findings']), 'Explicit direct probe required'
assert all(re.fullmatch('[0-9a-f]{64}', s) for s in (r['generation_id'],a['sha256'])), 'Bounded identity required'
pathlib.Path(sys.argv[2]).write_text(r['generation_id']+'\n'+a['sha256']+'\n')
PY
then echo 'AG1C_BUILD ADMISSION_IDENTITY_FAILED'; exit 1; fi
mapfile -t identity < "$tmp/identity"
./gradlew --no-daemon :test-apps:ag1-secondary-dex-fixture:assembleDebug >"$tmp/secondary-build.log" 2>&1 || {
  echo 'AG-1C secondary build failed'; exit 1;
}
./gradlew --no-daemon :app:assembleDebug :app:assembleDebugAndroidTest >"$tmp/manager-build.log" 2>&1 || {
  echo 'AG-1C manager/test build failed'; exit 1;
}
if ! python3 - "$fixture" "$tmp/admission.json" 2>"$tmp/separation-error.log" <<'PY'
import hashlib, json, pathlib, sys, zipfile
expected=json.loads(pathlib.Path(sys.argv[2]).read_text())['artifacts'][0]['sha256']
source=pathlib.Path(sys.argv[1]).read_bytes()
asset=pathlib.Path('app/build/generated/ag1DynamicAssets/ag1-precode-fixture-dynamic.apk').read_bytes()
with zipfile.ZipFile('app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk') as z:
    embedded=z.read('assets/ag1-precode-fixture-dynamic.apk')
assert all(hashlib.sha256(b).hexdigest()==expected for b in (source,asset,embedded)), 'Analyzed/embedded bytes mismatch'
with zipfile.ZipFile('test-apps/ag1-secondary-dex-fixture/build/outputs/apk/debug/ag1-secondary-dex-fixture-debug.apk') as z:
    assert [n for n in z.namelist() if n.endswith('.dex')] == ['classes.dex']
    secondary = z.read('classes.dex')
with zipfile.ZipFile('app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk') as z:
    assert z.read('assets/ag1-secondary.dex') == secondary
with zipfile.ZipFile(sys.argv[1]) as z:
    # Verify class definitions, not references: the primary intentionally names the payload.
    dex = z.read('classes.dex')
    import struct
    def classes(data):
        string_count, strings, type_count, types = struct.unpack_from('<4I', data, 56)
        count, offset = struct.unpack_from('<2I', data, 96)
        assert count < 10000 and offset + count * 32 <= len(data)
        names = set()
        for i in range(count):
            type_id = struct.unpack_from('<I', data, offset + i * 32)[0]
            assert type_id < type_count
            string_id = struct.unpack_from('<I', data, types + type_id * 4)[0]
            assert string_id < string_count
            cursor = struct.unpack_from('<I', data, strings + string_id * 4)[0]
            while data[cursor] & 128: cursor += 1
            cursor += 1
            names.add(data[cursor:data.index(0, cursor)])
        return names
    payload = b'Lcom/privacydecoy/ag1/secondary/SecondaryDexPayload;'
    assert payload in classes(secondary) and payload not in classes(dex)
    assert not any(z.read(n) == secondary for n in z.namelist() if not n.endswith('/'))
changed = bytearray(secondary); changed[-1] ^= 1
assert hashlib.sha256(changed).digest() != hashlib.sha256(secondary).digest()
pathlib.Path(sys.argv[2]).with_name('secondary-identity').write_text(hashlib.sha256(secondary).hexdigest())
print('AG1C_BUILD PRIMARY_ANALYZED SECONDARY_SEPARATE CONTENT_IDENTITY_VERIFIED')
PY
then echo 'AG1C_BUILD ARTIFACT_SEPARATION_FAILED'; exit 1; fi
[[ "$(uname -s)" == Linux && -r /dev/kvm && -w /dev/kvm ]] || { echo 'AG-1C Linux/KVM unavailable'; exit 1; }
export ANDROID_USER_HOME="$tmp/android-user"
export ANDROID_EMULATOR_HOME="$ANDROID_USER_HOME"
export ANDROID_AVD_HOME="$tmp/avd"
mkdir -p "$ANDROID_USER_HOME" "$ANDROID_AVD_HOME"
echo no | avdmanager create avd --name privacy_decoy_ag1c --path "$ANDROID_AVD_HOME/ag1c.avd" \
  --package 'system-images;android-35;google_apis;x86_64' --device pixel_2 >"$tmp/avd.log" 2>&1
"$ANDROID_HOME/emulator/emulator" -avd privacy_decoy_ag1c -port 5556 -no-window -no-audio \
  -no-boot-anim -no-snapshot -wipe-data -gpu software -memory 2048 -cores 2 -partition-size 2048 \
  >"$tmp/emulator.log" 2>&1 &
emulator_pid=$!
export ANDROID_SERIAL=emulator-5556
adb start-server >"$tmp/adb.log" 2>&1
booted=false
for _ in $(seq 1 120); do
  kill -0 "$emulator_pid" 2>/dev/null || { echo 'AG-1C emulator startup failed'; exit 1; }
  if [[ "$(timeout 3 adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)" == 1 ]]; then booted=true; break; fi
  sleep 2
done
[[ "$booted" == true ]] || { echo 'AG-1C emulator boot timed out'; exit 1; }
[[ "$(adb shell getprop ro.build.version.sdk | tr -d '\r')" == 35 ]]
[[ "$(adb shell getprop ro.product.cpu.abi | tr -d '\r')" == x86_64 ]]
adb install -t app/build/outputs/apk/debug/app-debug.apk >"$tmp/install.log" 2>&1
adb install -t app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk >>"$tmp/install.log" 2>&1
[[ -z "$(adb shell pm path com.privacydecoy.ag1.precode | tr -d '\r')" ]]
secondary_sha=$(cat "$tmp/secondary-identity")
instrumentation_exit=0
timeout 240 adb shell am instrument -w -r -e suite ag1dynamic \
  -e ag1Generation "${identity[0]}" -e ag1Sha "${identity[1]}" -e ag1Admission EXPERIMENTAL_ELIGIBLE -e ag1SecondarySha "$secondary_sha" \
  com.privacydecoy.app.test/com.privacydecoy.research.PrototypeTestRunner >"$tmp/instrumentation.log" 2>&1 || instrumentation_exit=$?
python3 tools/ag1-dynamic-evidence.py "$tmp/instrumentation.log" "$instrumentation_exit"
[[ -z "$(adb shell pm path com.privacydecoy.ag1.precode | tr -d '\r')" ]]
[[ -z "$(adb shell pm path com.privacydecoy.ag1.secondary | tr -d '\r')" ]]
