# Privacy Decoy

Privacy Decoy is a planned **Android-only, root-free privacy container/mediation
project**. The intended product would mediate applications' access to sensitive
device information. Roadmap PR 6 selected **REDESIGN**; technical feasibility remains
subject to another explicit gate. Privacy takes precedence over compatibility.

## Current status

Privacy Decoy has **no proven production privacy boundary** and no production
protected-app execution. The ADR-0006 architecture synthesis recommended **STOP
UNDER CURRENT GOALS** for unrestricted arbitrary hostile-app execution under the
families it evaluated. That recommendation remains valid historical evidence.

After a ten-project source/reference review identified a narrower admission-gated
hypothesis, the project owner explicitly chose **CONTINUE** on 2026-09-22 through
[ADR-0007](docs/decisions/ADR-0007-admission-gated-controlled-runtime.md). The
selected investigation is a PD-owned admission engine and controlled runtime with
fail-closed Protected Mode plus a separately labeled Experimental & Unproven
Compatibility Mode. Read the [technical architecture handoff](docs/architecture-admission-gated-runtime.md)
and [open-source reference catalog](docs/open-source-reference-catalog.md) before
architecture or reuse work. No third-party engine is selected wholesale.

**AG-1 — Admission-Gated Controlled Runtime Feasibility Checkpoint** is next.
Canonical production Roadmap PR 6 remains unstarted and pending AG-1; passing
AG-1 would not itself prove production privacy. Only controlled fixtures and test
applications are authorized during AG-1. Ordinary private user data and real
accounts remain prohibited.

Historical evidence is preserved: S1 is **FALSIFIED**, its follow-up is
**BLOCKED**, Blacks-BlackBox is **STOPPED_UNRESOLVED**, and the exact reviewed
VirtualSpace pin is **DISQUALIFIED — exact pinned candidate**. The ADR-0006 STOP
synthesis is not retroactively rewritten. The restored [canonical 46-PR
roadmap](docs/canonical-roadmap-1.0.md) retains mandatory STOP gates at PR 5 and
PR 20. Requirements [PD-REQ-001 through PD-REQ-095](docs/requirements.md) govern
future work.

Privacy Decoy remains Android-only, root-free and non-privileged: no production
Magisk/Xposed/LSPosed, custom ROM, production ADB, guest root, routine APK
rewriting/re-signing, or built-in PD `VpnService`. An external VPN remains the
policy model. Arbitrary hostile native containment is unresolved; ByteHook or
ShadowHook-style function interception is not a kernel sandbox. Compatibility,
static scan success, and Experimental execution are not Protected evidence.

The existing repository contains foundation and controlled research harnesses,
not a privacy product. The PR 4 containment and PR 5 networking fixtures remain
research-only; the separate external-VPN fixture never forwards traffic and is
not part of Privacy Decoy. Version `0.1.0-dev` is a development identifier, not a
public or production release.

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
