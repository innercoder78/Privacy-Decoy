# Development guidance

Keep pull requests cohesive, small enough to review, and limited to their roadmap
scope. PR 1 established the Android foundation; PR 2 established the clean-root
layout; PR 3 records requirements and research but implements no protection.

- Never commit credentials, tokens, signing keys, keystores, passwords, or other
  secrets. Keep local SDK configuration and environment files untracked.
- Never commit generated APKs, AABs, build outputs, caches, or IDE state. The standard
  binary `android/gradle/wrapper/gradle-wrapper.jar` is intentionally committed.
- Prefer platform APIs. Justify every additional dependency and permission.
- Avoid unrelated formatting, cleanup, and refactors. Do not modify synced
  `sources/` material as part of unrelated development.
- Privacy and security behavior requires reproducible evidence and traceability
  to stable requirements. Intended behavior alone does not justify a claim.
- Run the README build, lint, and unit-test commands. Add focused tests with
  relevant behavior changes; do not manufacture production abstractions for tests.
  Do not hide lint errors in a baseline.
- Maintain stable IDs in the [requirements register](../docs/requirements.md) and
  trace changes to the [threat model](../docs/threat-model.md), tests, and evidence.
  The [engine assessment](../docs/engine-assessment.md), [platform
  matrix](../docs/platform-support.md), and [ADR](../docs/decisions/ADR-0001-engine-prototype-direction.md)
  define the research handoff. PR 4 and PR 5 produce containment and networking
  evidence; PR 6 is the mandatory feasibility STOP decision.

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

## PR 4 controlled containment research

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
do not treat a green observational test as a privacy guarantee. Both CI jobs
must pass before merge readiness. PR 5 remains separate; PR 6 remains mandatory.
