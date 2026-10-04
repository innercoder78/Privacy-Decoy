#!/usr/bin/env bash
set -euo pipefail
root=$(cd "$(dirname "$0")/.." && pwd); cd "$root"
./gradlew --no-daemon :test-apps:java-surface-fixture:assembleDebug :test-apps:dynamic-code-surface-fixture:assembleDebug :probe-app:assembleDebug
tmp=$(mktemp -d); trap 'rm -rf -- "$tmp"' EXIT
analyzer=tools/artifact-analyzer.py
report_diagnostic() {
  python3 - "$1" "$2" <<'PY'
import json, sys
report=json.load(open(sys.argv[1], encoding="utf-8"))
record=report["artifacts"][0] if report["artifacts"] else {}
established={
    "package": bool(record.get("package")),
    "signers": bool(record.get("signer_sha256_digests")),
    "version_code": bool(record.get("version_code")),
    "base_split_identity": record.get("split_identity_state") != "unknown",
}
codes=sorted(item["code"] for item in report["findings"])
print(f"artifact fixture {sys.argv[2]}: status={report['structural_status']} "
      f"metadata_established={established} finding_codes={codes}", file=sys.stderr)
PY
}
analyze_valid() {
  local label=$1 apk=$2; shift 2
  local output="$tmp/$label.json" rc
  set +e; python3 "$analyzer" --base "$apk" --json > "$output"; rc=$?; set -e
  if [[ $rc -ne 0 ]]; then report_diagnostic "$output" "$label"; return 1; fi
  python3 - "$output" "$@" <<'PY'
import json,sys
r=json.load(open(sys.argv[1],encoding='utf-8')); codes={x['code'] for x in r['findings']}
assert r['structural_status']=='VALID', r['structural_status']
assert r['capability_coverage']=='Unknown'
assert r['split_completeness']=='Unknown'
assert 'RUNTIME_MEDIATION_UNPROVEN' in codes
assert set(sys.argv[2:]) <= codes, (sys.argv[2:], sorted(codes))
record=r['artifacts'][0]
assert record['package'] and record['signer_sha256_digests'] and record['version_code']
assert record['split_identity_state']=='absent'
PY
}
analyze_valid java test-apps/java-surface-fixture/build/outputs/apk/debug/java-surface-fixture-debug.apk
analyze_valid dynamic test-apps/dynamic-code-surface-fixture/build/outputs/apk/debug/dynamic-code-surface-fixture-debug.apk DYNAMIC_DEX_LOADER_REFERENCE NATIVE_LOAD_REFERENCE
analyze_valid native probe-app/build/outputs/apk/debug/probe-app-debug.apk APP_CONTROLLED_NATIVE_PRESENT

# A readable controlled APK with deliberately skipped SDK metadata must remain UNKNOWN/exit 2.
set +e
python3 "$analyzer" --base test-apps/java-surface-fixture/build/outputs/apk/debug/java-surface-fixture-debug.apk --without-sdk-metadata --json > "$tmp/unknown.json"
unknown_rc=$?
set -e
[[ $unknown_rc -eq 2 ]] || { report_diagnostic "$tmp/unknown.json" unknown; exit 1; }
python3 - "$tmp/unknown.json" <<'PY'
import json,sys
r=json.load(open(sys.argv[1],encoding='utf-8'))
assert r['structural_status']=='UNKNOWN'
assert r['capability_coverage']=='Unknown' and r['split_completeness']=='Unknown'
PY

printf 'malformed\n' > "$tmp/bad.apk"
set +e; python3 "$analyzer" --base "$tmp/bad.apk" --without-sdk-metadata --json > "$tmp/bad.json"; bad_rc=$?; set -e
[[ $bad_rc -eq 1 ]] || { report_diagnostic "$tmp/bad.json" malformed; exit 1; }
python3 - "$tmp/bad.json" <<'PY'
import json,sys
assert json.load(open(sys.argv[1]))['structural_status']=='INVALID'
PY
echo 'Controlled artifact inventory assertions passed; no APK was installed or executed.'
