#!/usr/bin/env bash
set -euo pipefail
baseline=false
analyzer=false
while IFS= read -r path; do
  [[ -z "$path" ]] && continue
  case "$path" in
    README.md|docs/*|.github/CONTRIBUTING.md) ;;
    android/tools/artifact-analyzer.py|android/tools/test-artifact-analyzer.py) analyzer=true ;;
    android/tools/run-artifact-fixtures.sh|android/test-apps/*|android/app/*|android/probe-app/*|android/research-native/*|android/settings.gradle.kts|android/build.gradle.kts|android/gradle.properties|android/gradle/*|android/gradlew|android/gradlew.bat)
      baseline=true; [[ "$path" == android/tools/run-artifact-fixtures.sh || "$path" == android/test-apps/* ]] && analyzer=true ;;
    .github/workflows/android.yml|.github/scripts/classify-ci-changes.sh|android/*|.github/*)
      baseline=true; analyzer=true ;;
    *) baseline=true; analyzer=true ;;
  esac
done
printf 'baseline=%s\nanalyzer=%s\n' "$baseline" "$analyzer"
printf 'CI_CHANGESET baseline=%s analyzer=%s\n' "$baseline" "$analyzer" >&2
