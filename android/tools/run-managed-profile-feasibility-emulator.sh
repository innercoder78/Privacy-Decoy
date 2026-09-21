#!/usr/bin/env bash
set -euo pipefail
API_LEVEL="${API_LEVEL:-35}"; ABI="${ABI:-x86_64}"; IMAGE="${IMAGE:-google_apis}"; AVD="pd-pr8-${API_LEVEL}-$$"
SDK="${ANDROID_HOME:?ANDROID_HOME required}"; ADB="$SDK/platform-tools/adb"; EMULATOR="$SDK/emulator/emulator"; REPORT="$PWD/app/build/reports/managed-profile"
USER_ID=""; EMU_PID=""
cleanup(){ set +e; [[ -n "$USER_ID" ]] && "$ADB" shell pm remove-user "$USER_ID" >/dev/null; [[ -n "$EMU_PID" ]] && kill "$EMU_PID"; "$SDK/cmdline-tools/latest/bin/avdmanager" delete avd -n "$AVD" >/dev/null 2>&1; }
trap cleanup EXIT
rm -rf "$REPORT"; mkdir -p "$REPORT"/{parent,tenant-a,tenant-b}
./gradlew --no-daemon :app:assembleDebug :test-apps:managed-profile-controller:assembleDebug :test-apps:managed-profile-probe:assembleTenantADebug :test-apps:managed-profile-probe:assembleTenantBDebug
echo no | "$SDK/cmdline-tools/latest/bin/avdmanager" create avd --force -n "$AVD" -k "system-images;android-${API_LEVEL};${IMAGE};${ABI}"
"$EMULATOR" -avd "$AVD" -no-window -no-audio -no-snapshot -wipe-data & EMU_PID=$!
"$ADB" wait-for-device; until [[ $("$ADB" shell getprop sys.boot_completed | tr -d '\r') == 1 ]];do sleep 2;done
CTRL=test-apps/managed-profile-controller/build/outputs/apk/debug/managed-profile-controller-debug.apk
A=test-apps/managed-profile-probe/build/outputs/apk/tenantADebug/managed-profile-probe-tenantA-debug.apk
B=test-apps/managed-profile-probe/build/outputs/apk/tenantBDebug/managed-profile-probe-tenantB-debug.apk
"$ADB" install -r app/build/outputs/apk/debug/app-debug.apk >/dev/null
printf synthetic | "$ADB" shell run-as com.privacydecoy.app sh -c 'cat > no_backup/pr8-sentinel'
SENTINEL_BEFORE=$("$ADB" exec-out shell run-as com.privacydecoy.app sha256sum no_backup/pr8-sentinel | cut -d' ' -f1)
USER_ID=$("$ADB" shell pm create-user --profileOf 0 --managed PD-PR8 | sed -n 's/.* user id \([0-9]*\).*/\1/p' | tr -d '\r'); [[ "$USER_ID" =~ ^[0-9]+$ ]]
"$ADB" install --user "$USER_ID" "$CTRL" >/dev/null
"$ADB" shell dpm set-profile-owner --user "$USER_ID" com.privacydecoy.research.profilecontroller/.ResearchAdminReceiver
"$ADB" shell am start-user "$USER_ID"; "$ADB" shell am broadcast --user "$USER_ID" -a com.privacydecoy.research.COMPLETE_SETUP -n com.privacydecoy.research.profilecontroller/.SetupReceiver >/dev/null
"$ADB" shell dumpsys device_policy | grep -q 'com.privacydecoy.research.profilecontroller'
"$ADB" exec-out shell run-as --user "$USER_ID" com.privacydecoy.research.profilecontroller cat shared_prefs/research_state.xml | grep -q 'name="setup_complete" value="true"'
! "$ADB" shell pm list packages --user "$USER_ID" | grep -q managedprobe
"$ADB" install --user 0 "$A" >/dev/null; "$ADB" install --user "$USER_ID" "$A" >/dev/null; "$ADB" install --user "$USER_ID" "$B" >/dev/null
invoke(){ local user=$1 pkg=$2; "$ADB" shell am start --user "$user" -a android.intent.action.VIEW -d pd-research://run -n "$pkg/.ProbeActivity" >/dev/null; "$ADB" shell am broadcast --user "$user" -a com.privacydecoy.research.PROBE -n "$pkg/.ProbeReceiver" >/dev/null; }
invoke 0 com.privacydecoy.research.managedprobe.a; invoke "$USER_ID" com.privacydecoy.research.managedprobe.a; invoke "$USER_ID" com.privacydecoy.research.managedprobe.b
pull(){ local user=$1 pkg=$2 dest=$3; for e in provider application activity service receiver;do "$ADB" exec-out shell run-as --user "$user" "$pkg" cat "files/evidence-$e" > "$dest/evidence-$e";done; }
pull 0 com.privacydecoy.research.managedprobe.a "$REPORT/parent";pull "$USER_ID" com.privacydecoy.research.managedprobe.a "$REPORT/tenant-a";pull "$USER_ID" com.privacydecoy.research.managedprobe.b "$REPORT/tenant-b"
python3 tools/managed-profile-evidence.py --parent "$REPORT/parent" --tenant-a "$REPORT/tenant-a" --tenant-b "$REPORT/tenant-b" --output "$REPORT/report.json"
SENTINEL_AFTER=$("$ADB" exec-out shell run-as com.privacydecoy.app sha256sum no_backup/pr8-sentinel | cut -d' ' -f1); [[ "$SENTINEL_BEFORE" == "$SENTINEL_AFTER" ]]
TENANT_UID=$("$ADB" shell pm list packages -U --user "$USER_ID" com.privacydecoy.research.managedprobe.a | sed -n 's/.*uid:\([0-9]*\).*/\1/p' | tr -d '\r'); [[ "$TENANT_UID" =~ ^[0-9]+$ ]]
"$ADB" shell am stop-user -f "$USER_ID"; ! "$ADB" shell ps -A -o UID,NAME | grep -E "^[[:space:]]*$TENANT_UID[[:space:]]"; "$ADB" shell am start-user "$USER_ID"
