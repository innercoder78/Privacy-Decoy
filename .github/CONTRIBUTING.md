# Development guidance

Phase II is the active epoch. Read [ADR-0010](../docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md),
the [product contract](../docs/phase-ii-requirements.md), [threat
model](../docs/phase-ii-threat-model.md), and [acceptance
criteria](../docs/phase-ii-acceptance-criteria.md) before forward work.

Phase I ended **C. NO CREDIBLE BOUNDARY** under its former contract and AG-1
remains failed. Preserve historical failures, Unknowns, ADRs, and evidence. Do not
resume old canonical Roadmap PR 6 or reinterpret a passing observational test as
privacy evidence.

Transformation and re-signing are now the selected Phase II direction, while
ordinary production remains root-free on stock supported Android. This does not
prove the architecture and does not authorize runtime work outside the active
[Phase II roadmap](../docs/phase-ii-roadmap.md).

* Privacy Decoy must never declare, implement, use, or depend on Android
  `VpnService`, occupy the active VPN slot, or implement local-VPN interception.
* Keep compatibility separate from coverage. Unknown never means success, launch
  success never proves mediation, and no privacy claim may exceed scoped evidence.
* Never silently expose genuine host data for compatibility. Real is explicit,
  scoped, visible, revocable controlled disclosure.
* Never commit credentials, tokens, signing keys, keystores, passwords, private
  user data, production fixtures, or raw sensitive values.
* Never commit generated APKs, AABs, native libraries, build outputs, caches, or
  IDE state. The standard Gradle wrapper JAR is intentionally committed.
* Add no dependency without exact source, license, provenance, binary, security,
  and TCB review. A catalog reference is not approval.
* Keep changes cohesive and trace them to stable requirements, threats, acceptance
  evidence, and roadmap gates. Do not weaken tests or hide lint errors.
* Preserve original source-app `targetSdkVersion` by default. Any retargeting is
  structural and needs specific compatibility rationale and evidence.

## Repository layout

Keep the repository root deliberately clean:

```text
Privacy-Decoy/
├── .github/
├── android/
├── docs/
├── .editorconfig
├── .gitattributes
├── .gitignore
└── README.md
```

Android-specific source, Gradle files, wrappers, configuration, and tooling belong
under `android/`. Project documentation belongs under the root-level `docs/`
directory. Repository-wide files such as `README.md`, `.gitignore`,
`.gitattributes`, and `.editorconfig` may remain at the root. Future pull requests
must not add root-level files unless they genuinely apply to the repository as a
whole.

Use JDK 17 and run the committed wrapper from `android/`. To regenerate it
with trusted Gradle 9.6.0, run the following from `android/`:

```sh
gradle wrapper --gradle-version 9.6.0 --distribution-type bin --gradle-distribution-sha256-sum bbaeb2fef8710818cf0e261201dab964c572f92b942812df0c3620d62a529a01
```

Validate the binary JAR against the
[official Gradle checksum reference](https://gradle.org/release-checksums/):
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`.
Do not reconstruct or encode the JAR as text. CI pins official GitHub actions
to immutable SHAs with their release tags in comments; update them deliberately.

No open-source license has been selected by this PR.

## Historical Phase I PR 5 network research harness

This section preserves maintenance instructions for the Phase I PR 5 harness.
Its test semantics and recorded results remain valid historical evidence. The
harness may remain useful until later repository triage and migration, but it is
not an active Phase II production gate. Phase II follows the
[Phase II roadmap](../docs/phase-ii-roadmap.md).

Run `bash tools/run-network-feasibility-emulator.sh` on the disposable Linux SDK/KVM
runner for the independent network experiment. Its twelve mandatory cases and
optional platform-verified lockdown case are separate from the nine containment
tests. For local build/unit validation run
`./gradlew testDebugUnitTest :test-apps:external-vpn-fixture:assembleDebug`
from `android/` for the fail-closed gate and separate fixture build. Device route
claims require the disposable API 35 x86_64 environment plus independent filtered
packet capture; never infer them from broker or fixture self-report. APKs, native
libraries, captures, reports, AVDs, and logs stay untracked. The fixture is an
external test application, not a Privacy Decoy VPN. Passing a `KnownGap` device
test means the adverse behavior was reproduced, not that protection succeeded.

## Historical Phase I PR 4 containment research harness

This section preserves maintenance instructions for the Phase I PR 4 harness.
Its test semantics and recorded results remain valid historical evidence. The
harness may remain useful until later repository triage and migration, but it is
not an active Phase II production gate.

Install official SDK packages `ndk;27.2.12479018` and `cmake;3.22.1` in addition
to the foundation toolchain. For the CI emulator also install `emulator`,
`platform-tools`, and `system-images;android-35;google_apis;x86_64`.

From `android/`, run the normal validation plus
`./gradlew :app:assembleDebugAndroidTest :app:connectedDebugAndroidTest` against
a controlled device. On Linux, `bash tools/run-containment-emulator.sh` creates
and wipes the dedicated `privacy-decoy-pr4` AVD, bounds boot waiting, verifies
API/ABI and test discovery, and shuts down the emulator on exit. Do not use that
AVD name for personal work. Do not install `probe-app-debug.apk`: Gradle copies
the unchanged generated artifact under `app/build/generated/probeAssets`.

The custom platform Instrumentation runner avoids a new Maven dependency and
emits the standard per-test status protocol consumed by connectedAndroidTest.
Passing `testKnownGap...` tests means an adverse observation was reproduced.
Record actual results in [PR 4 evidence](../docs/evidence/pr4-containment-prototype.md);
do not treat a green observational test as a privacy guarantee. The historical
PR 4 and PR 5 harnesses remain separate. The old canonical production Roadmap
PR 6 is historical and must not resume; forward work follows the
[Phase II roadmap](../docs/phase-ii-roadmap.md).
