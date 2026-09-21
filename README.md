# Privacy Decoy

Privacy Decoy is a planned **Android-only, root-free privacy container/mediation
project**. The intended product would mediate applications' access to sensitive
device information. Roadmap PR 6 selected **REDESIGN**; technical feasibility remains
subject to another explicit gate. Privacy takes precedence over compatibility.

## Current status

This repository contains the PR 1 development foundation, PR 2 clean Android
project layout, PR 3 security/design evidence, and the PR 4 adversarial research
harness. PR 5's controlled networking feasibility research is merged: a debug-only
trusted broker, fail-closed gate model, and a separate external-VPN test fixture.
It does **not** provide production protected networking. The fixture's
`VpnService` belongs to `com.privacydecoy.externalvpnfixture`, is not included in
Privacy Decoy, and never forwards traffic. **Privacy containment and
spoofing are not implemented or verified.** There is no protected-app execution,
virtualization, Decoy Persona, VPN enforcement, or verified privacy/security
boundary. Version `0.1.0-dev` (version code 1) is a development identifier; this
work does not represent a public or production release. PR 3's analysis is not
protection and selects no production containment engine.

The **Roadmap PR 6** gate decision is **A — REDESIGN**, selected on 2026-09-21.
Product implementation remains paused; the current research prototype will not
proceed unchanged as the product. The next phase is containment/runtime
architecture redesign and evidence work, with another explicit feasibility gate
required before ordinary third-party-app implementation. No production engine
has been selected. Ordinary protected apps, real accounts, and private user data
remain prohibited. GitHub **PR #7** corresponds to Roadmap PR 6 because GitHub
PR #6 was consumed by the earlier corrective stacked PR merged into PR #5. The
[decision record](docs/decisions/ADR-0002-feasibility-stop-gate.md) contains the
rationale, preserved constraints, and roadmap consequences.

PR 4 builds a controlled, uninstalled probe APK and a debug-only isolated-service
experiment. Artifact-derived DEX execution is not full Android app execution.
The harness investigates UID/storage isolation, Binder authority, native syscalls,
host-state leakage and missing lifecycle semantics; it is not a functioning
privacy container. See the [scoped evidence and validation status](docs/evidence/pr4-containment-prototype.md).
Never use this prototype with ordinary protected apps, private data, or accounts.
The same restriction applies to the PR 5 networking harness. Route experiments
without independent device/pcap evidence remain Unknown; green unit tests are not
route evidence.

The production design goal requires no root, Magisk, Xposed, LSPosed, custom ROM,
or ordinary dependence on ADB. Privacy Decoy itself must not use Android
`VpnService`. These constraints do not establish feasibility or protection.

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

PR 5 pure gate tests run as part of `testDebugUnitTest`. The dedicated
`network-feasibility` CI job runs the API 35 network suite with fixed host servers,
a separate dropping VPN, and independent emulator packet capture. Generated
captures are filtered to fixed headers, never uploaded, and deleted after analysis. The separate fixture can
be generated with `./gradlew :test-apps:external-vpn-fixture:assembleDebug`; its
APK is generated output and must not be committed or treated as a built-in VPN.
See [PR 5 evidence](docs/evidence/pr5-network-feasibility.md).

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
including newer transfer modes, requires later validation under the canonical
roadmap before sensitive state or protection claims are introduced. No
cross-platform transfer counterpart is configured for this Android-only app.

See [Android backup semantics](https://developer.android.com/identity/data/autobackup),
[AGP compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes),
[development guidance](.github/CONTRIBUTING.md), and the comprehensive
[requirements register](docs/requirements.md), [threat model](docs/threat-model.md),
[engine assessment](docs/engine-assessment.md), [platform investigation
matrix](docs/platform-support.md), and [prototype-direction
ADR](docs/decisions/ADR-0001-engine-prototype-direction.md).
