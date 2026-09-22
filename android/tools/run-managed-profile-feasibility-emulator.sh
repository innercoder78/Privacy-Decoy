#!/usr/bin/env bash
set -euo pipefail
# Never enable shell tracing: stdin carries synthetic private fixtures.
set +x
API_LEVEL="${API_LEVEL:-35}"; ABI="${ABI:-x86_64}"; IMAGE="${IMAGE:-google_apis}"; AVD="pd-pr8-${API_LEVEL}-$$"
SDK="${ANDROID_HOME:?ANDROID_HOME required}"; ADB="$SDK/platform-tools/adb"; EMULATOR="$SDK/emulator/emulator"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export ANDROID_USER_HOME="$PWD/app/build/android-user-managed-profile"
export ANDROID_EMULATOR_HOME="$ANDROID_USER_HOME"
export ANDROID_AVD_HOME="$PWD/app/build/avd-managed-profile"
export ANDROID_SERIAL=emulator-5554
REPORT="$PWD/app/build/reports/managed-profile"
USER_ID=""; EMU_PID=""; COMPLETE=false; BOOTED=false

# One attempt per setup/measurement command; timeout is not a security retry.
device() {
    local limit=60
    [[ "$1" == install ]] && limit=120
    timeout --kill-after=2 "$limit" "$ADB" -s "$ANDROID_SERIAL" "$@"
}

startup_diagnostics() {
    # Same categories as the proven containment/network startup checks, but no raw lines.
    python3 - "$REPORT/emulator.log" <<'PY'
import pathlib
import sys
path = pathlib.Path(sys.argv[1])
try:
    with path.open('rb') as stream:
        head = stream.read(65536)
        stream.seek(max(0, path.stat().st_size - 65536))
        log = (head + stream.read(65536)).decode('utf-8', errors='replace').lower()
except OSError:
    log = ''
categories = {
    'insufficient-disk-space': ['not enough disk space', 'not enough space', 'insufficient disk', 'no space left'],
    'missing-library': ['error while loading shared libraries', 'cannot open shared object'],
    'gpu-startup-failed': ['failed to initialize opengl', 'invalid gpu', 'vulkan initialization failed'],
    'acceleration-unavailable': ['kvm is not installed', 'kvm permission denied', 'requires hardware acceleration'],
    'avd-or-image-missing': ['unknown avd name', 'cannot find avd', 'broken avd system path', 'cannot find system image'],
    'avd-path-conflict': ['running multiple emulators with the same avd', 'address already in use'],
}
found = [name for name, patterns in categories.items() if any(p in log for p in patterns)]
print('PD_S1_STARTUP_DIAGNOSTIC=' + ','.join(found or ['unclassified-startup-failure']))
PY
}

startup_failure() {
    printf 'PD_S1_STARTUP=%s\n' "$1"  # Call sites supply fixed categories only.
    startup_diagnostics
    exit 1
}

cleanup() {
    local result=$? cleanup_failed=false attempt
    set +e
    # No device-dependent cleanup if boot never succeeded; every adb call is bounded.
    if [[ "$BOOTED" == true && -n "$USER_ID" ]]; then
        if [[ $(timeout --kill-after=1 3 "$ADB" -s "$ANDROID_SERIAL" get-state 2>/dev/null) == device ]]; then
            timeout --kill-after=2 15 "$ADB" -s "$ANDROID_SERIAL" shell pm remove-user "$USER_ID" >/dev/null 2>&1 || cleanup_failed=true
        else
            cleanup_failed=true
        fi
    fi
    if [[ -n "$EMU_PID" ]]; then
        if [[ "$BOOTED" == true ]]; then
            timeout --kill-after=1 5 "$ADB" -s "$ANDROID_SERIAL" emu kill >/dev/null 2>&1
        fi
        kill "$EMU_PID" 2>/dev/null
        for ((attempt=0; attempt<5; attempt++)); do
            kill -0 "$EMU_PID" 2>/dev/null || break
            sleep 1
        done
        if kill -0 "$EMU_PID" 2>/dev/null; then
            kill -KILL "$EMU_PID" 2>/dev/null || cleanup_failed=true
        fi
        # Never wait indefinitely for a child, including after unsuccessful startup.
    fi
    if [[ -f "$ANDROID_AVD_HOME/$AVD.ini" ]]; then
        timeout --kill-after=2 30 "$SDK/cmdline-tools/latest/bin/avdmanager" delete avd -n "$AVD" >/dev/null 2>&1 || cleanup_failed=true
    fi
    [[ "$cleanup_failed" == false ]] || printf 'PD_S1_CLEANUP=FAILED\n'
    if [[ $result != 0 || $COMPLETE != true || $cleanup_failed == true ]]; then
        printf 'PD_S1_HARNESS=FAIL\nPD_S1_OUTCOME=INCONCLUSIVE\n'
        exit 1
    fi
    python3 tools/managed-profile-evidence.py --root "$REPORT" --output "$REPORT/report.json"
    exit $?
}
trap cleanup EXIT
rm -rf "$REPORT"; mkdir -p "$REPORT"/{parent,tenant_a,tenant_b} "$ANDROID_USER_HOME" "$ANDROID_AVD_HOME"
[[ "$API_LEVEL" == 35 && "$ABI" == x86_64 && "$IMAGE" == google_apis ]] || startup_failure unsupported-evidence-target
./gradlew --no-daemon :app:assembleDebug :test-apps:managed-profile-controller:assembleDebug :test-apps:managed-profile-probe:assembleTenantADebug :test-apps:managed-profile-probe:assembleTenantBDebug
echo no | timeout --kill-after=2 120 "$SDK/cmdline-tools/latest/bin/avdmanager" create avd --force \
    --name "$AVD" --path "$ANDROID_AVD_HOME/$AVD.avd" \
    --package "system-images;android-${API_LEVEL};${IMAGE};${ABI}" --device pixel_2
[[ -f "$ANDROID_AVD_HOME/$AVD.ini" && -f "$SDK/system-images/android-35/google_apis/x86_64/system.img" ]] || startup_failure avd-or-image-missing
timeout --kill-after=2 10 "$ADB" start-server >/dev/null 2>&1 || startup_failure adb-server-unavailable
# A pre-existing emulator on the fixed port must never satisfy this experiment.
CONNECTED=$(timeout --kill-after=1 3 "$ADB" devices) || startup_failure adb-server-unavailable
if grep -Eq '^emulator-5554[[:space:]]' <<< "$CONNECTED"; then startup_failure avd-path-conflict; fi
unset CONNECTED
"$EMULATOR" -avd "$AVD" -port 5554 -no-window -no-audio -no-boot-anim \
    -no-snapshot -wipe-data -gpu software -memory 2048 -cores 2 -partition-size 2048 \
    > "$REPORT/emulator.log" 2>&1 & EMU_PID=$!
BOOT_DEADLINE=$((SECONDS + 300)); NEXT_PROGRESS=$((SECONDS + 30))
printf 'PD_S1_STARTUP=WAITING_FOR_BOOT\n'
while (( SECONDS < BOOT_DEADLINE )); do
    kill -0 "$EMU_PID" 2>/dev/null || startup_failure emulator-exited-before-boot
    if [[ $(timeout --kill-after=1 3 "$ADB" -s "$ANDROID_SERIAL" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true) == 1 ]]; then
        BOOTED=true
        break
    fi
    if (( SECONDS >= NEXT_PROGRESS )); then
        printf 'PD_S1_STARTUP=WAITING_FOR_BOOT\n'
        NEXT_PROGRESS=$((SECONDS + 30))
    fi
    sleep 2
done
[[ "$BOOTED" == true ]] || startup_failure boot-timeout
kill -0 "$EMU_PID" 2>/dev/null || startup_failure emulator-exited-before-setup
[[ $(device shell getprop ro.build.version.sdk | tr -d '\r') == 35 ]] || startup_failure wrong-api
[[ $(device shell getprop ro.product.cpu.abi | tr -d '\r') == x86_64 ]] || startup_failure wrong-abi
printf 'Engineering evidence target: API 35 / x86_64 / debug / Google APIs emulator\n'
CTRL=com.privacydecoy.research.profilecontroller
PA=com.privacydecoy.research.managedprobe.a; PB=com.privacydecoy.research.managedprobe.b
A=test-apps/managed-profile-probe/build/outputs/apk/tenantA/debug/managed-profile-probe-tenantA-debug.apk
B=test-apps/managed-profile-probe/build/outputs/apk/tenantB/debug/managed-profile-probe-tenantB-debug.apk
device install app/build/outputs/apk/debug/app-debug.apk >/dev/null
USER_ID=$(device shell pm create-user --profileOf 0 --managed PD-PR8 | sed -n 's/.* user id \([0-9]*\).*/\1/p' | tr -d '\r')
[[ "$USER_ID" =~ ^[1-9][0-9]*$ ]]
device install --user "$USER_ID" test-apps/managed-profile-controller/build/outputs/apk/debug/managed-profile-controller-debug.apk >/dev/null
DPM_RESULT=$(device shell dpm set-profile-owner --user "$USER_ID" "$CTRL/.ResearchAdminReceiver")
[[ "$DPM_RESULT" == *Success* ]]
device shell am start-user -w "$USER_ID" >/dev/null
device shell am broadcast --include-stopped-packages --user "$USER_ID" -a com.privacydecoy.research.COMPLETE_SETUP -n "$CTRL/.SetupReceiver" >/dev/null
owner_cat() { device exec-out run-as "$2" --user "$1" cat "$3"; }
package_uid() {
    local value
    value=$(device shell pm list packages -U --user "$1" "$2" | tr -d '\r' | sed -n "s/^package:$2 uid:\([0-9]*\)$/\1/p")
    [[ "$value" =~ ^[0-9]+$ ]] || return 1
    printf '%s' "$value"
}
owner_cat "$USER_ID" "$CTRL" files/setup-state > "$REPORT/setup-state"
PACKAGES=$(device shell pm list packages --user "$USER_ID")
[[ "$PACKAGES" != *managedprobe* ]]
printf 'schema=2\ndpm_success=true\nprobes_absent=true\ntarget_shell_user=%s\ncontroller_package_uid=%s\n' \
    "$USER_ID" "$(package_uid "$USER_ID" "$CTRL")" > "$REPORT/checkpoint"
# Validate the target-profile checkpoint BEFORE installation, not after the experiment.
python3 tools/managed-profile-evidence.py --root "$REPORT" --check-setup
device install --user 0 "$A" >/dev/null
device install -r --user "$USER_ID" "$A" >/dev/null
device install --user "$USER_ID" "$B" >/dev/null

NONCE=$(python3 -c 'import secrets; print(secrets.token_hex(32))')
NONCE_HASH=$(printf '%s' "$NONCE" | python3 -c 'import sys,hashlib,base64; print(base64.urlsafe_b64encode(hashlib.sha256(sys.stdin.buffer.read()).digest()).decode().rstrip("="))')
owner_hash() {
    local value
    value=$(device exec-out run-as "$2" --user "$1" sha256sum "$3" | cut -d' ' -f1 | tr -d '\r')
    [[ "$value" =~ ^[0-9a-f]{64}$ ]] || return 1
    printf '%s' "$value"
}
seed() {
    local name=$1 user=$2 pkg=$3 path=$4 before actual
    device shell run-as "$pkg" --user "$user" mkdir -p "${path%/*}"
    # Double quoting survives adb's remote shell, so redirection occurs under run-as.
    python3 -c 'import secrets,sys; sys.stdout.write(secrets.token_hex(32))' | \
        device shell -T run-as "$pkg" --user "$user" sh -c "'cat > $path'"
    before=$(owner_hash "$user" "$pkg" "$path")
    printf 'schema=2\nshell_user=%s\npackage_uid=%s\nexisted_before=true\nowner_readable_before=true\nbefore_hash=%s\n' \
        "$user" "$(package_uid "$user" "$pkg")" "$before" > "$REPORT/fixture-$name"
    if [[ "$name" != management ]]; then
        printf '%s' "$NONCE" | device shell -T run-as "$pkg" --user "$user" sh -c "'cat > files/test-nonce'"
        actual=$(owner_cat "$user" "$pkg" files/test-nonce)
        [[ "$actual" == "$NONCE" ]]
        # All peer targets are pre-created managed-profile fixtures, including parent baseline.
        printf '%s' "$USER_ID" | device shell -T run-as "$pkg" --user "$user" sh -c "'cat > files/peer-shell-user'"
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
    device shell am start -W --user "$user" -a android.intent.action.VIEW -d pd-research://run -n "$pkg/com.privacydecoy.research.managedprobe.ProbeActivity" >/dev/null
    device shell am broadcast --user "$user" -a com.privacydecoy.research.PROBE -n "$pkg/com.privacydecoy.research.managedprobe.ProbeReceiver" >/dev/null
}
invoke 0 "$PA"; invoke "$USER_ID" "$PA"; invoke "$USER_ID" "$PB"
pull() {
    local user=$1 pkg=$2 name=$3 event attempt
    for event in provider application activity service receiver; do
        for ((attempt=0; attempt<30; attempt++)); do
            timeout --kill-after=1 3 "$ADB" -s "$ANDROID_SERIAL" shell run-as "$pkg" --user "$user" test -s "files/evidence-$event" && break
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
BEFORE_STOP=$(device shell ps -A -o UID,NAME)
for uid in "$UID_A" "$UID_B"; do grep -Eq "^[[:space:]]*$uid[[:space:]]" <<< "$BEFORE_STOP"; done
device shell am stop-user -w -f "$USER_ID" >/dev/null
AFTER_STOP=$(device shell ps -A -o UID,NAME)
for uid in "$UID_A" "$UID_B"; do ! grep -Eq "^[[:space:]]*$uid[[:space:]]" <<< "$AFTER_STOP"; done
device shell am start-user -w "$USER_ID" >/dev/null
printf 'PD_S1_PROFILE_STOP=PROCESS_DEATH_OBSERVED\n'
COMPLETE=true
