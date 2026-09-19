#!/usr/bin/env bash
set -euo pipefail
# Dedicated disposable CI AVD; never install the probe APK itself.
: "${ANDROID_HOME:?Android SDK required}"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
avd="privacy-decoy-pr4"
echo no | "$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager" create avd --force \
  --name "$avd" --package 'system-images;android-35;google_apis;x86_64' --device pixel_2
mkdir -p app/build/reports/containment
"$ANDROID_HOME/emulator/emulator" -avd "$avd" -port 5554 -no-window -no-audio \
  -no-boot-anim -no-snapshot -wipe-data -gpu swiftshader_indirect \
  >app/build/reports/containment/emulator.log 2>&1 &
emulator_pid=$!
trap 'adb -s emulator-5554 emu kill >/dev/null 2>&1 || true; kill "$emulator_pid" 2>/dev/null || true' EXIT
export ANDROID_SERIAL=emulator-5554
timeout 180 adb wait-for-device
booted=false
for _ in $(seq 1 120); do
  if [[ "$(adb shell getprop sys.boot_completed | tr -d '\r')" == 1 ]]; then booted=true; break; fi
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
