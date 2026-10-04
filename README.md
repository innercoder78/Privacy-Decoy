# Privacy Decoy

Privacy Decoy is a planned Android-only, root-free privacy-mediation project.
**Phase II is the active architecture epoch.** It selects transformation-first
hybrid mediation for stock, non-rooted Android 17/API 37, but no Phase II runtime
has been implemented or proven secure.

## Current status and governing documents

Phase I ended **C. NO CREDIBLE BOUNDARY** under its former fail-closed Protected
contract. AG-1 remains failed, candidate outcomes remain as recorded, and Unknown
has not become success. Historical ADRs, requirements, tests, and evidence remain
immutable evidence. The old canonical production Roadmap PR 6 is not resumed.

[ADR-0010](docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md)
and the [Phase II requirements](docs/phase-ii-requirements.md) govern forward
work. The selected direction collects legitimately available installed base and
split APKs, analyzes them, deterministically rewrites package structure and known
mediation call sites, injects a generic Persona runtime, rebuilds, signs with a
stable per-clone identity, and installs a genuinely separate package/UID with
fresh private state. Transformation and re-signing are authorized architecture,
not evidence that mediation works.

Persona remains central with **Real, Decoy, Empty, and Deny** modes. Real means
explicit controlled disclosure, never silent host passthrough. A permission
firewall should remove unnecessary direct genuine-data authority. Java, native,
SDK, WebView, Binder, reflection, and dynamic-code paths require separately
scoped evidence. ByteHook or ShadowHook-style interception is not a kernel
sandbox. Compatibility and privacy coverage are separate, Unknown is never
success, and no claim may exceed tested coverage.

**Privacy Decoy never uses Android `VpnService`, never occupies the active VPN
slot, and never implements local-VPN interception.** Users remain free to run an
independent external VPN. Network geography is External. The initial architecture
performs no automatic public-IP lookup and does not use genuine GPS to compare a
Persona with network location.

The repository still contains a foundation and historical research harnesses,
not a working privacy product. It does not advertise APK transformation,
mediation, fresh cloning, or Persona protection as implemented. See the [Phase II
threat model](docs/phase-ii-threat-model.md), [acceptance
criteria](docs/phase-ii-acceptance-criteria.md), [roadmap](docs/phase-ii-roadmap.md),
[historical migration register](docs/phase-ii-requirements-migration.md), and
[repository triage plan](docs/phase-ii-repository-triage.md).

## Build and validate

Prerequisites:

- JDK 17, with `JAVA_HOME` set.
- Android SDK command-line tools and SDK Platform 37 (package
  `platforms;android-37.0`), plus Build Tools 36.0.0.
- `ANDROID_HOME` pointing to the SDK, or an untracked `android/local.properties`
  containing `sdk.dir=/path/to/android-sdk`.
- Network access for the initial build-tool/dependency downloads.
- Official Android NDK `27.2.12479018` and CMake `3.22.1` for research probes.
  CI installs these exact SDK packages; no compiled native library is committed.

The committed wrapper uses Gradle 9.6.0. Android Gradle Plugin 9.4.0 supplies
built-in Kotlin support. Both `compileSdk` and `targetSdk` are 37.
**`minSdk = 31` is a provisional initial build baseline, not a final supported
platform commitment.** The provisional investigation matrix records the evidence
still required; this documentation PR does not change any SDK value.

The Android project lives in `android/`. From the repository root:

```sh
cd android
./gradlew --version
./gradlew lintDebug
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

On Windows PowerShell, first run `Set-Location android`, then use
`.\gradlew.bat` in place of `./gradlew`.
The debug unit tests exercise the research session state machine. Device tests
run with `./gradlew :app:connectedDebugAndroidTest` on a controlled emulator;
`bash tools/run-containment-emulator.sh` creates a disposable API 35 x86_64 AVD
after its official system image is installed. The generated probe APK is a test
asset and must **not** be installed. Lint errors fail the build without
a baseline. The debug APK is generated under `app/build/outputs/apk/debug/`
and must not be committed. Debug builds use ordinary development signing;
there is no production signing configuration.

Historical Phase I PR 5 gate tests run as part of `testDebugUnitTest`. The dedicated
`network-feasibility` CI job runs the API 35 network suite with fixed host servers,
a separate dropping VPN, and independent emulator packet capture. Generated
captures are filtered to fixed headers, never uploaded, and deleted after analysis. The separate fixture can
be generated with `./gradlew :test-apps:external-vpn-fixture:assembleDebug`; its
APK is generated output and must not be committed or treated as a built-in VPN.
See [PR 5 evidence](docs/evidence/pr5-network-feasibility.md).
These harness semantics and results remain historical evidence, not active Phase
II production gates. Forward work follows the [Phase II
roadmap](docs/phase-ii-roadmap.md).

## Foundation defaults and limits

The release/main application requests no permissions and declares only its launcher Activity.
It has no networking, telemetry, services, receivers, providers, or privacy
runtime. Cleartext application traffic and application backup are disabled.
The debug manifest adds a non-exported isolated research service and one fixed
probe-package visibility query. Research native code is a debug-only dependency.
MainActivity and the release manifest do not expose the prototype.
API 31+ data-extraction rules explicitly exclude every documented app storage
domain from cloud backup and device transfer. Legacy full backup is also
disabled; the current minimum SDK does not support Android 11 or earlier.

These settings are conservative defaults, not proof of behavior on every device.
Android documents that `allowBackup=false` alone can leave device transfer
enabled on some manufacturers' devices. OEM/platform backup and transfer behavior,
including newer transfer modes, requires later validation under the Phase II
roadmap before sensitive state or privacy claims are introduced. No
cross-platform transfer counterpart is configured for this Android-only app.

See [Android backup semantics](https://developer.android.com/identity/data/autobackup),
[AGP compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes),
[development guidance](.github/CONTRIBUTING.md), and the comprehensive
[historical requirements register](docs/requirements.md), [Phase II threat model](docs/phase-ii-threat-model.md),
[engine assessment](docs/engine-assessment.md), [platform investigation
matrix](docs/platform-support.md), and [Phase II architecture
ADR](docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md).
