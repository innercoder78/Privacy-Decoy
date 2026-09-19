# Privacy Decoy

Privacy Decoy is a planned **Android-only, root-free privacy container/mediation
project**. The intended product would mediate applications' access to sensitive
device information. Technical feasibility remains subject to the canonical
roadmap's **PR 6 feasibility gate**. Privacy takes precedence over compatibility.

## Current status

This repository contains the PR 1 development foundation, PR 2 clean Android
project layout, and PR 3 security/design evidence. **Privacy containment and
spoofing are not implemented or verified.** There is no protected-app execution,
virtualization, Decoy Persona, VPN enforcement, or verified privacy/security
boundary. Version `0.1.0-dev` (version code 1) is a development identifier; this
work does not represent a public or production release. PR 3's analysis is not
protection and selects no production containment engine. PR 6 remains the
mandatory feasibility/STOP gate.

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
The unit-test task and JUnit infrastructure are available; there are currently
no pure-Kotlin components warranting tests, so the task may report `NO-SOURCE`.
This does not constitute security testing. Lint errors fail the build without
a baseline. The debug APK is generated under `app/build/outputs/apk/debug/`
and must not be committed. Debug builds use ordinary development signing;
there is no production signing configuration.

## Foundation defaults and limits

The application requests no permissions and declares only its launcher Activity.
It has no networking, telemetry, services, receivers, providers, or privacy
runtime. Cleartext application traffic and application backup are disabled.
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
