# Development guidance

## Project status

**Active Privacy Decoy implementation is suspended** under the explicit owner decision in [ADR-0012](../docs/decisions/ADR-0012-suspend-active-development-and-establish-pdva-successor.md). See the canonical [project status](../docs/project-status.md). Unsolicited forward architectural implementation must not infer authorization from the old roadmap. Future implementation resumes only after an explicit owner decision/ADR and reassessment of Android/platform assumptions. Historical and security documentation fixes may still be valid; historical evidence must not be rewritten.

[Privacy Decoy Virtual Android (PDVA)](https://github.com/innercoder78/privacy-decoy-virtual-android) is a separate repository and product lineage and must not be modified as part of work in this repository.

The retained rules below describe the historical Phase II contract and research validation; forward implementation guidance is conditional on owner reactivation. Read [ADR-0010](../docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md), the [requirements](../docs/phase-ii-requirements.md), [threat model](../docs/phase-ii-threat-model.md), [acceptance criteria](../docs/phase-ii-acceptance-criteria.md), and [roadmap](../docs/phase-ii-roadmap.md). Phase I ended **C. NO CREDIBLE BOUNDARY** and AG-1 remains failed; preserve all historical failures, Unknowns, ADRs, and evidence.

Privacy Decoy must never declare, implement, start, bind to, depend on, or otherwise use `VpnService`. The external VPN fixture is a separate test application and must never become an `:app` dependency. Do not infer safety or mediation from successful execution or absent static indicators. Do not commit secrets, signing keys, private data, APK/AAB/SO output, caches, or IDE state. Add no dependency without provenance, license, binary, security, and TCB review.

## Retained research validation

Under the explicit owner decision in [ADR-0011](../docs/decisions/ADR-0011-adopt-emulator-first-phase-ii-development.md), Android Studio's Android 17 / API 37 emulator is the normal development and research environment for roadmap Stages 3–15. Emulator/debug evidence may support forward research and implementation when the applicable stage gate is satisfied; it remains research evidence, and Unknown never becomes success. Stage-specific STOP conditions still apply. If a surface cannot be meaningfully evaluated in an emulator, record that limitation and any earlier physical evidence needed for its research gate.

Physical validation before Stage 16 is encouraged when useful, but is not required merely to continue development. Mandatory Stage 16 physical qualification revalidates applicable claims on stock non-rooted Android 17 / API 37 ARM64 across multiple supported devices/OEMs with release-equivalent Manager and transformed builds and exact scoped evidence. It must pass for the supported scope before Stage 17 independent security/supply-chain review and Stage 18 production/release consideration. No production claim may come from emulator-only testing; physical failures or material differences invalidate affected evidence/coverage and require fixing, narrowing, honest Unknown/Unsupported classification, or REDESIGN/STOP.

Use JDK 17 and run the wrapper from `android/`. The `Android Phase II foundation` workflow has path-aware `baseline` and `artifact-analyzer` jobs. The baseline validates the minimal app, reusable `probe-app` and `research-native` probes, neutral controlled fixtures, isolated external VPN fixture, wrapper checksum/modes, release manifest, and absence of signing material. Analyzer validation checks deterministic inventory/hashes, positive indicator detection, Unknown-on-absence semantics, and safe malformed-input failure.

```sh
./gradlew --no-daemon :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
python3 tools/test-artifact-analyzer.py
bash tools/run-artifact-fixtures.sh
```

The Java, dynamic-code, early-init/direct-loader, and secondary-DEX fixtures are adversarial inputs, not an admission policy or privacy proof. `probe-app` and `research-native` likewise preserve probe surfaces without asserting containment. Do not install fixtures into production user state.

For artifact inspection, set `ANDROID_HOME` or `ANDROID_SDK_ROOT` to the official SDK and install build-tools `36.0.0`, matching CI. The analyzer deliberately selects that signer even if newer build-tools are installed or on PATH; missing pinned tooling fails analysis. Keep `apkanalyzer` available through the SDK command-line tools. On Windows, use an SDK path without spaces for its batch launcher.

Keep the root limited to `.github/`, `android/`, `docs/`, and genuine repository-wide files. Android material belongs under `android/`; documentation belongs under `docs/`. The Gradle wrapper JAR is intentionally committed and must match SHA-256 `497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`.
