# Development guidance

Phase II is active. Read [ADR-0010](../docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md), the [requirements](../docs/phase-ii-requirements.md), [threat model](../docs/phase-ii-threat-model.md), [acceptance criteria](../docs/phase-ii-acceptance-criteria.md), and [roadmap](../docs/phase-ii-roadmap.md). Phase I ended **C. NO CREDIBLE BOUNDARY** and AG-1 remains failed; preserve all historical failures, Unknowns, ADRs, and evidence.

Privacy Decoy must never declare, implement, start, bind to, depend on, or otherwise use `VpnService`. The external VPN fixture is a separate test application and must never become an `:app` dependency. Do not infer safety or mediation from successful execution or absent static indicators. Do not commit secrets, signing keys, private data, APK/AAB/SO output, caches, or IDE state. Add no dependency without provenance, license, binary, security, and TCB review.

## Active validation

Use JDK 17 and run the wrapper from `android/`. The `Android Phase II foundation` workflow has path-aware `baseline` and `artifact-analyzer` jobs. The baseline validates the minimal app, reusable `probe-app` and `research-native` probes, neutral controlled fixtures, isolated external VPN fixture, wrapper checksum/modes, release manifest, and absence of signing material. Analyzer validation checks deterministic inventory/hashes, positive indicator detection, Unknown-on-absence semantics, and safe malformed-input failure.

```sh
./gradlew --no-daemon :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
python3 tools/test-artifact-analyzer.py
bash tools/run-artifact-fixtures.sh
```

The Java, dynamic-code, early-init/direct-loader, and secondary-DEX fixtures are adversarial inputs, not an admission policy or privacy proof. `probe-app` and `research-native` likewise preserve probe surfaces without asserting containment. Do not install fixtures into production user state.

Keep the root limited to `.github/`, `android/`, `docs/`, and genuine repository-wide files. Android material belongs under `android/`; documentation belongs under `docs/`. The Gradle wrapper JAR is intentionally committed and must match SHA-256 `497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`.
