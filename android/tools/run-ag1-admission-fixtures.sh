#!/usr/bin/env bash
set -euo pipefail

# Static-only AG-1A runner: it deliberately contains no adb/install invocation.
root=$(cd "$(dirname "$0")/.." && pwd)
cd "$root"
./gradlew --no-daemon \
  :test-apps:ag1-java-fixture:lintDebug \
  :test-apps:ag1-java-fixture:assembleDebug \
  :test-apps:ag1-dynamic-fixture:lintDebug \
  :test-apps:ag1-dynamic-fixture:assembleDebug \
  :probe-app:assembleDebug

tmp=$(mktemp -d)
trap 'rm -rf -- "$tmp"' EXIT
analyzer=tools/ag1-admission-analyzer.py

analyze_and_assert() {
  local label=$1
  local apk=$2
  shift 2
  local output="$tmp/$label.json"
  python3 "$analyzer" --base "$apk" --json > "$output"
  python3 - "$output" "$@" <<'PY'
import json, sys
report = json.load(open(sys.argv[1], encoding="utf-8"))
codes = {item["code"] for item in report["findings"]}
assert report["admission"] == "EXPERIMENTAL_ELIGIBLE", report["admission"]
assert "RUNTIME_MEDIATION_UNPROVEN" in codes
assert set(sys.argv[2:]) <= codes, (sys.argv[2:], sorted(codes))
assert report["admission"] != "PROTECTED_ELIGIBLE"
PY
}

analyze_and_assert java test-apps/ag1-java-fixture/build/outputs/apk/debug/ag1-java-fixture-debug.apk
analyze_and_assert dynamic test-apps/ag1-dynamic-fixture/build/outputs/apk/debug/ag1-dynamic-fixture-debug.apk DYNAMIC_DEX_LOADER_REFERENCE NATIVE_LOAD_REFERENCE
analyze_and_assert native probe-app/build/outputs/apk/debug/probe-app-debug.apk APP_CONTROLLED_NATIVE_PRESENT

printf 'malformed synthetic fixture\n' > "$tmp/malformed.apk"
if python3 "$analyzer" --base "$tmp/malformed.apk" --json > "$tmp/malformed.json"; then
  echo 'malformed artifact unexpectedly succeeded' >&2
  exit 1
fi
python3 - "$tmp/malformed.json" <<'PY'
import json, sys
report = json.load(open(sys.argv[1], encoding="utf-8"))
assert report["admission"] == "INCOMPATIBLE"
assert report["admission"] != "PROTECTED_ELIGIBLE"
PY
echo 'AG-1A static fixture assertions passed; no APK was installed or executed.'
