#!/usr/bin/env bash
set -euo pipefail
# Never enable shell tracing: stdin carries synthetic private fixtures.
set +x
API_LEVEL="${API_LEVEL:-35}"; ABI="${ABI:-x86_64}"; IMAGE="${IMAGE:-google_apis}"; AVD="pd-pr8-${API_LEVEL}-$$"
SDK="${ANDROID_HOME:?ANDROID_HOME required}"; ADB="$SDK/platform-tools/adb"; EMULATOR="$SDK/emulator/emulator"
REPORT="$PWD/app/build/reports/managed-profile"
USER_ID=""; EMU_PID=""; COMPLETE=false
cleanup() {
    local result=$? cleanup_failed=false
    set +e
    if [[ -n "$USER_ID" ]]; then "$ADB" shell pm remove-user "$USER_ID" >/dev/null || cleanup_failed=true; fi
    if [[ -n "$EMU_PID" ]]; then kill "$EMU_PID"; wait "$EMU_PID" 2>/dev/null; fi
    "$SDK/cmdline-tools/latest/bin/avdmanager" delete avd -n "$AVD" >/dev/null 2>&1 || cleanup_failed=true
    if [[ $result != 0 || $COMPLETE != true || $cleanup_failed == true ]]; then
        printf 'PD_S1_HARNESS=FAIL\nPD_S1_OUTCOME=INCONCLUSIVE\n'
        exit 1
    fi
    python3 tools/managed-profile-evidence.py --root "$REPORT" --output "$REPORT/report.json"
    exit $?
}
trap cleanup EXIT
rm -rf "$REPORT"; mkdir -p "$REPORT"/{parent,tenant_a,tenant_b}
./gradlew --no-daemon :app:assembleDebug :test-apps:managed-profile-controller:assembleDebug :test-apps:managed-profile-probe:assembleTenantADebug :test-apps:managed-profile-probe:assembleTenantBDebug
echo no | "$SDK/cmdline-tools/latest/bin/avdmanager" create avd --force -n "$AVD" -k "system-images;android-${API_LEVEL};${IMAGE};${ABI}"
"$EMULATOR" -avd "$AVD" -no-window -no-audio -no-snapshot -wipe-data > "$REPORT/emulator.log" 2>&1 & EMU_PID=$!
"$ADB" wait-for-device
for ((attempt=0; attempt<180; attempt++)); do
    [[ $("$ADB" shell getprop sys.boot_completed | tr -d '\r') == 1 ]] && break
    sleep 2
done
[[ $("$ADB" shell getprop sys.boot_completed | tr -d '\r') == 1 ]]
CTRL=com.privacydecoy.research.profilecontroller
PA=com.privacydecoy.research.managedprobe.a; PB=com.privacydecoy.research.managedprobe.b
A=test-apps/managed-profile-probe/build/outputs/apk/tenantA/debug/managed-profile-probe-tenantA-debug.apk
B=test-apps/managed-profile-probe/build/outputs/apk/tenantB/debug/managed-profile-probe-tenantB-debug.apk
"$ADB" install app/build/outputs/apk/debug/app-debug.apk >/dev/null
USER_ID=$("$ADB" shell pm create-user --profileOf 0 --managed PD-PR8 | sed -n 's/.* user id \([0-9]*\).*/\1/p' | tr -d '\r')
[[ "$USER_ID" =~ ^[1-9][0-9]*$ ]]
"$ADB" install --user "$USER_ID" test-apps/managed-profile-controller/build/outputs/apk/debug/managed-profile-controller-debug.apk >/dev/null
DPM_RESULT=$("$ADB" shell dpm set-profile-owner --user "$USER_ID" "$CTRL/.ResearchAdminReceiver")
[[ "$DPM_RESULT" == *Success* ]]
"$ADB" shell am start-user -w "$USER_ID" >/dev/null
"$ADB" shell am broadcast --include-stopped-packages --user "$USER_ID" -a com.privacydecoy.research.COMPLETE_SETUP -n "$CTRL/.SetupReceiver" >/dev/null
owner_cat() { "$ADB" exec-out run-as "$2" --user "$1" cat "$3"; }
package_uid() {
    local value
    value=$("$ADB" shell pm list packages -U --user "$1" "$2" | tr -d '\r' | sed -n "s/^package:$2 uid:\([0-9]*\)$/\1/p")
    [[ "$value" =~ ^[0-9]+$ ]] || return 1
    printf '%s' "$value"
}
owner_cat "$USER_ID" "$CTRL" files/setup-state > "$REPORT/setup-state"
PACKAGES=$("$ADB" shell pm list packages --user "$USER_ID")
[[ "$PACKAGES" != *managedprobe* ]]
printf 'schema=2\ndpm_success=true\nprobes_absent=true\ntarget_shell_user=%s\ncontroller_package_uid=%s\n' \
    "$USER_ID" "$(package_uid "$USER_ID" "$CTRL")" > "$REPORT/checkpoint"
# Validate the target-profile checkpoint BEFORE installation, not after the experiment.
python3 tools/managed-profile-evidence.py --root "$REPORT" --check-setup
"$ADB" install --user 0 "$A" >/dev/null
"$ADB" install -r --user "$USER_ID" "$A" >/dev/null
"$ADB" install --user "$USER_ID" "$B" >/dev/null

NONCE=$(python3 -c 'import secrets; print(secrets.token_hex(32))')
NONCE_HASH=$(printf '%s' "$NONCE" | python3 -c 'import sys,hashlib,base64; print(base64.urlsafe_b64encode(hashlib.sha256(sys.stdin.buffer.read()).digest()).decode().rstrip("="))')
owner_hash() {
    local value
    value=$("$ADB" exec-out run-as "$2" --user "$1" sha256sum "$3" | cut -d' ' -f1 | tr -d '\r')
    [[ "$value" =~ ^[0-9a-f]{64}$ ]] || return 1
    printf '%s' "$value"
}
seed() {
    local name=$1 user=$2 pkg=$3 path=$4 before actual
    "$ADB" shell run-as "$pkg" --user "$user" mkdir -p "${path%/*}"
    # Double quoting survives adb's remote shell, so redirection occurs under run-as.
    python3 -c 'import secrets,sys; sys.stdout.write(secrets.token_hex(32))' | \
        "$ADB" shell -T run-as "$pkg" --user "$user" sh -c "'cat > $path'"
    before=$(owner_hash "$user" "$pkg" "$path")
    printf 'schema=2\nshell_user=%s\npackage_uid=%s\nexisted_before=true\nowner_readable_before=true\nbefore_hash=%s\n' \
        "$user" "$(package_uid "$user" "$pkg")" "$before" > "$REPORT/fixture-$name"
    if [[ "$name" != management ]]; then
        printf '%s' "$NONCE" | "$ADB" shell -T run-as "$pkg" --user "$user" sh -c "'cat > files/test-nonce'"
        actual=$(owner_cat "$user" "$pkg" files/test-nonce)
        [[ "$actual" == "$NONCE" ]]
        # All peer targets are pre-created managed-profile fixtures, including parent baseline.
        printf '%s' "$USER_ID" | "$ADB" shell -T run-as "$pkg" --user "$user" sh -c "'cat > files/peer-shell-user'"
        [[ $(owner_cat "$user" "$pkg" files/peer-shell-user) == "$USER_ID" ]]
        printf 'nonce_seeded=true\nnonce_hash=%s\n' "$NONCE_HASH" >> "$REPORT/fixture-$name"
    fi
}
seed management 0 com.privacydecoy.app no_backup/pr8-sentinel
seed parent 0 "$PA" files/sentinel
seed tenant_a "$USER_ID" "$PA" files/sentinel
seed tenant_b "$USER_ID" "$PB" files/sentinel
unset NONCE
# No probe component executes until ALL owner reads, nonce checks and fixture hashes succeed.
invoke() {
    local user=$1 pkg=$2
    "$ADB" shell am start -W --user "$user" -a android.intent.action.VIEW -d pd-research://run -n "$pkg/com.privacydecoy.research.managedprobe.ProbeActivity" >/dev/null
    "$ADB" shell am broadcast --user "$user" -a com.privacydecoy.research.PROBE -n "$pkg/com.privacydecoy.research.managedprobe.ProbeReceiver" >/dev/null
}
invoke 0 "$PA"; invoke "$USER_ID" "$PA"; invoke "$USER_ID" "$PB"
pull() {
    local user=$1 pkg=$2 name=$3 event attempt
    for event in provider application activity service receiver; do
        for ((attempt=0; attempt<30; attempt++)); do
            "$ADB" shell run-as "$pkg" --user "$user" test -s "files/evidence-$event" && break
            sleep 1
        done
        owner_cat "$user" "$pkg" "files/evidence-$event" > "$REPORT/$name/evidence-$event"
    done
}
pull 0 "$PA" parent; pull "$USER_ID" "$PA" tenant_a; pull "$USER_ID" "$PB" tenant_b
after() {
    local hash
    hash=$(owner_hash "$2" "$3" "$4")
    printf 'owner_readable_after=true\nafter_hash=%s\n' "$hash" >> "$REPORT/fixture-$1"
}
after management 0 com.privacydecoy.app no_backup/pr8-sentinel
after parent 0 "$PA" files/sentinel
after tenant_a "$USER_ID" "$PA" files/sentinel
after tenant_b "$USER_ID" "$PB" files/sentinel
owner_cat "$USER_ID" "$CTRL" files/setup-state > "$REPORT/setup-state-after"
# Coarse process-death check for BOTH tenants. No FD/job/socket/recovery claim.
UID_A=$(package_uid "$USER_ID" "$PA"); UID_B=$(package_uid "$USER_ID" "$PB")
BEFORE_STOP=$("$ADB" shell ps -A -o UID,NAME)
for uid in "$UID_A" "$UID_B"; do grep -Eq "^[[:space:]]*$uid[[:space:]]" <<< "$BEFORE_STOP"; done
"$ADB" shell am stop-user -w -f "$USER_ID" >/dev/null
AFTER_STOP=$("$ADB" shell ps -A -o UID,NAME)
for uid in "$UID_A" "$UID_B"; do ! grep -Eq "^[[:space:]]*$uid[[:space:]]" <<< "$AFTER_STOP"; done
"$ADB" shell am start-user -w "$USER_ID" >/dev/null
printf 'PD_S1_PROFILE_STOP=PROCESS_DEATH_OBSERVED\n'
COMPLETE=true
