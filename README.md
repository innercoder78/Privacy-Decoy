# Privacy Decoy

Privacy Decoy is an Android-only research project. Phase I concluded **C. NO CREDIBLE BOUNDARY** and AG-1 remains failed. Historical ADRs and evidence preserve that result. Phase II follows [ADR-0010](docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md); the current tree is only a conservative foundation and does **not** implement or prove privacy mediation.

Privacy Decoy runs on supported stock Android without root, Magisk, Xposed/LSPosed, a custom ROM, privileged installation, production ADB, a patched kernel, or a guest Android system. Privacy Decoy never implements or uses `VpnService`; the separate `external-vpn-fixture` is test-only and is not a dependency of the app. Network geography is External.

## Active Android tree

* `app` — minimal Privacy Decoy application foundation: one launcher activity, no permissions, services, receivers, providers, networking, or runtime mediation.
* `probe-app` and `research-native` — reusable adversarial Java/framework/native probes; they are not privacy evidence.
* `test-apps/*-fixture` — controlled Java, dynamic-code, early-initialization/direct-loader, secondary-DEX, and isolated external-VPN fixtures.
* `tools/artifact-analyzer.py` — static inventory/risk-presence analysis for supplied controlled APKs. Absence of an indicator is not a safety or coverage result; unavailable executable behavior remains Unknown.
* `tools/network-evidence.py` — independent packet-evidence utility retained for generic analysis, not a PD-owned VPN architecture.

## Build and validation

Use JDK 17 and the committed wrapper:

```sh
cd android
./gradlew --no-daemon :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
python3 tools/test-artifact-analyzer.py
bash tools/run-artifact-fixtures.sh
```

CI builds/lints/tests every active app, probe, and fixture module, verifies the release manifest and wrapper, and runs artifact-analyzer tests when relevant. Documentation-only changes do not trigger Android builds. Generated APKs, AABs, shared libraries, and build output must not be committed. No production signing configuration or mediation runtime exists.

See [development guidance](.github/CONTRIBUTING.md), the [Phase II roadmap](docs/phase-ii-roadmap.md), [requirements](docs/phase-ii-requirements.md), [threat model](docs/phase-ii-threat-model.md), and [acceptance criteria](docs/phase-ii-acceptance-criteria.md).
