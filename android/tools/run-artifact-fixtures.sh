#!/usr/bin/env bash
set -euo pipefail
root=$(cd "$(dirname "$0")/.." && pwd); cd "$root"
./gradlew --no-daemon :test-apps:java-surface-fixture:assembleDebug :test-apps:dynamic-code-surface-fixture:assembleDebug :probe-app:assembleDebug
tmp=$(mktemp -d); trap 'rm -rf -- "$tmp"' EXIT
analyzer=tools/artifact-analyzer.py
check() { local apk=$1; shift; python3 "$analyzer" --base "$apk" --json > "$tmp/report.json"; python3 - "$tmp/report.json" "$@" <<'PY'
import json,sys
r=json.load(open(sys.argv[1],encoding='utf-8')); codes={x['code'] for x in r['findings']}
assert r['structural_status']=='VALID'; assert r['capability_coverage']=='Unknown'; assert 'RUNTIME_MEDIATION_UNPROVEN' in codes
assert set(sys.argv[2:]) <= codes
PY
}
check test-apps/java-surface-fixture/build/outputs/apk/debug/java-surface-fixture-debug.apk
check test-apps/dynamic-code-surface-fixture/build/outputs/apk/debug/dynamic-code-surface-fixture-debug.apk DYNAMIC_DEX_LOADER_REFERENCE NATIVE_LOAD_REFERENCE
check probe-app/build/outputs/apk/debug/probe-app-debug.apk APP_CONTROLLED_NATIVE_PRESENT
printf 'malformed\n' > "$tmp/bad.apk"
if python3 "$analyzer" --base "$tmp/bad.apk" --json > "$tmp/bad.json"; then exit 1; fi
python3 - "$tmp/bad.json" <<'PY'
import json,sys
assert json.load(open(sys.argv[1]))['structural_status']=='INVALID'
PY
echo 'Controlled artifact inventory assertions passed; no APK was installed or executed.'
