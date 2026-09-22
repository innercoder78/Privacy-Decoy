#!/usr/bin/env bash
set -euo pipefail

# Read one repository-relative path per line. Unknown paths intentionally select
# every suite so a newly added Android or security boundary cannot lose coverage.
baseline=false
containment=false
network=false
managed=false

while IFS= read -r path; do
  [[ -z "$path" ]] && continue

  case "$path" in
    README.md|docs/*|.github/CONTRIBUTING.md)
      ;;

    android/app/src/androidTest/java/com/privacydecoy/research/NetworkTests.java|\
    android/app/src/debug/java/com/privacydecoy/research/FixedNetworkProbe.java|\
    android/app/src/debug/java/com/privacydecoy/research/NetworkGate.java|\
    android/app/src/debug/java/com/privacydecoy/research/NetworkResearchBrokerService.java|\
    android/app/src/debug/java/com/privacydecoy/research/NetworkWire.java|\
    android/app/src/testDebug/java/com/privacydecoy/research/NetworkGateTest.java|\
    android/test-apps/external-vpn-fixture/*|\
    android/tools/network-evidence.py|\
    android/tools/test-network-evidence.py|\
    android/tools/run-network-feasibility-emulator.sh)
      baseline=true
      network=true
      ;;

    android/test-apps/managed-profile-controller/*|\
    android/test-apps/managed-profile-probe/*|\
    android/tools/managed-profile-evidence.py|\
    android/tools/test-managed-profile-evidence.py|\
    android/tools/run-managed-profile-feasibility-emulator.sh)
      baseline=true
      managed=true
      ;;

    android/probe-app/*|\
    android/research-native/*|\
    android/app/src/androidTest/java/com/privacydecoy/research/PrototypeTestRunner.java|\
    android/app/src/androidTest/java/com/privacydecoy/research/PrototypeTests.java|\
    android/tools/run-containment-emulator.sh)
      baseline=true
      containment=true
      ;;

    *)
      baseline=true
      containment=true
      network=true
      managed=true
      ;;
  esac
done

printf 'baseline=%s\n' "$baseline"
printf 'containment=%s\n' "$containment"
printf 'network=%s\n' "$network"
printf 'managed=%s\n' "$managed"
printf 'CI_CHANGESET baseline=%s containment=%s network=%s managed=%s\n' \
  "$baseline" "$containment" "$network" "$managed" >&2
