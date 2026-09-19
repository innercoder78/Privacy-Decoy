#!/usr/bin/env bash
set -euo pipefail
# Dedicated disposable CI AVD; never install the probe APK itself.
: "${ANDROID_HOME:?Android SDK required}"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export ANDROID_USER_HOME="$PWD/app/build/android-user"
export ANDROID_EMULATOR_HOME="$ANDROID_USER_HOME"
export ANDROID_AVD_HOME="$PWD/app/build/avd"
mkdir -p "$ANDROID_USER_HOME" "$ANDROID_AVD_HOME"
avd="privacy-decoy-pr4"
echo no | "$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager" create avd --force \
  --name "$avd" --path "$ANDROID_AVD_HOME/$avd.avd" \
  --package 'system-images;android-35;google_apis;x86_64' --device pixel_2
[[ -f "$ANDROID_AVD_HOME/$avd.ini" ]]
[[ -f "$ANDROID_HOME/system-images/android-35/google_apis/x86_64/system.img" ]]
mkdir -p app/build/reports/containment
"$ANDROID_HOME/emulator/emulator" -avd "$avd" -port 5554 -no-window -no-audio \
  -no-boot-anim -no-snapshot -wipe-data -gpu software -memory 2048 -cores 2 -partition-size 2048 \
  >app/build/reports/containment/emulator.log 2>&1 &
emulator_pid=$!
trap 'adb -s emulator-5554 emu kill >/dev/null 2>&1 || true; kill "$emulator_pid" 2>/dev/null || true' EXIT
export ANDROID_SERIAL=emulator-5554
adb start-server
booted=false
for _ in $(seq 1 120); do
  if ! kill -0 "$emulator_pid" 2>/dev/null; then
    # Startup diagnostics only; never dump raw emulator logs/host metadata.
    python3 - <<'PY'
import pathlib
import re
raw = pathlib.Path('app/build/reports/containment/emulator.log').read_text(errors='replace')
log = raw.lower()
categories = {
    'insufficient-disk-space': ['not enough disk space', 'not enough space', 'insufficient disk', 'no space left'],
    'missing-library': ['error while loading shared libraries', 'cannot open shared object'],
    'gpu-startup-failed': ['failed to initialize opengl', 'invalid gpu', 'vulkan initialization failed'],
    'acceleration-unavailable': ['kvm is not installed', 'kvm permission denied', 'requires hardware acceleration'],
    'avd-or-image-missing': ['unknown avd name', 'cannot find avd', 'broken avd system path'],
    'avd-path-conflict': ['running multiple emulators with the same avd'],
}
found = [name for name, patterns in categories.items() if any(p in log for p in patterns)]
print('Emulator exited before boot: ' + ', '.join(found or ['unclassified-startup-failure']))
for line in raw.splitlines():
    if not re.search(r'\b(ERROR|FATAL|PANIC)\b|error:', line, re.I):
        continue
    if re.search(r'fingerprint|serial|uuid|token|secret|android_id|property', line, re.I):
        continue
    line = re.sub(r'/[^\s,;)]*', '<path>', line)
    line = re.sub(r'\b(?:\d{1,3}\.){3}\d{1,3}\b|\b[0-9a-fA-F]{8,}\b', '<redacted>', line)
    print('Sanitized startup error: ' + line[:400])
PY
    exit 1
  fi
  if [[ "$(timeout 3 adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)" == 1 ]]; then booted=true; break; fi
  sleep 2
done
[[ "$booted" == true ]] || { echo 'Emulator boot timed out'; exit 1; }
[[ "$(adb shell getprop ro.build.version.sdk | tr -d '\r')" == 35 ]]
[[ "$(adb shell getprop ro.product.cpu.abi | tr -d '\r')" == x86_64 ]]
echo 'Engineering evidence target: API 35 / x86_64 / debug / Google APIs emulator'
./gradlew --no-daemon :app:connectedDebugAndroidTest
# Fail if runner/task wiring silently stops discovering tests.
python3 - <<'PY'
import pathlib
import xml.etree.ElementTree as ET
reports = list(pathlib.Path('app/build/outputs/androidTest-results/connected').rglob('TEST-*.xml'))
cases = [c for p in reports for c in ET.parse(p).getroot().iter('testcase')]
assert len(cases) >= 9, f'Expected at least 9 real instrumentation tests; found {len(cases)}'
assert not any(c.find('failure') is not None or c.find('error') is not None or c.find('skipped') is not None for c in cases)
print(f'Confirmed {len(cases)} device tests executed without failures or skips')
PY
