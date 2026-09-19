# PR 4: Adversarial containment research prototype

Date: 2026-09-19. Base commit: `4dd8cdf3eb5f224cc404e188f9b7678798604ff9`.
Implementation branch: `codex/pr4-adversarial-containment`. Tested implementation
commit / CI run: **pending validation**.

**No production containment engine is selected. Ordinary protected apps are not
safe to use. PR 6 remains the mandatory user feasibility/STOP decision.**

## Objective and architecture

Falsify the assumptions in ADR-0001 with project-owned, synthetic fixtures.
`:probe-app` generates an APK with a reflection entrypoint, Application/provider
markers and a tiny native library. The APK is never installed by this experiment.
Gradle copies it unchanged to generated androidTest assets. Test infrastructure
hashes the APK before/after, extracts only `classes.dex`, and transfers read-only
SharedMemory to a debug-only non-exported `isolatedProcess` service. Public
`bindIsolatedService` uses a fresh instance name per session.

`ResearchBoundary` is the disposable internal interface; `ResearchSession` owns
the manager-side `SessionPolicy` and Binder broker. Registration records
Binder-observed UID **and PID**, never a supplied UID. Session ID, epoch, operation
allowlist, revocation and death are checked on every broker request. One session
gets one payload invocation. Broker capabilities contain no durable secret.
The tests deliberately transfer A's actual broker to B and attempt an A claim.
Death is observed with `linkToDeath`, old state is terminal, and replacement gets
a new session identity. No automatic retry or restart exists; the pure policy
permits at most two bind attempts and the adapter closes on its first bind failure.
IPC is serialized to one outstanding call with a ten-second deadline; timeout
revokes/unbinds the session. A probe initialization error also revokes authority.

The prototype gate has `VerifiedForPrototype`, `Unsupported`, and `Unknown` for
three narrowly scoped **research preconditions**: controlled fixture,
isolated transport, and broker identity. Test infrastructure explicitly chooses
the controlled experiment profile. Missing/Unknown/Unsupported coverage, malformed
sessions and failed initialization prevent binding and payload dispatch. This is
not a production coverage matrix and does not label framework/native mediation
verified. Any future ordinary-app launch must remain blocked on those unknowns.

After payload execution begins, all isolated-process observations are untrusted
self-reports. Binder identity and persistent manager sentinel comparisons provide
independent checks for selected claims, not a complete hostile-native audit.

## Important negative hypotheses and limits

These are **source-derived expectations until the device suite runs**, not
fabricated emulator findings:

- DEX class loading does not supply normal Application/ContentProvider creation,
  manifest dispatch, package Context, resources or a virtual package universe.
- Build values are not mediated. Settings, fixed package queries and selected
  services may expose host state or deny access; either outcome is reported as a
  category, not a claim of complete coverage.
- A bootstrap DEX parent excludes management implementation classes from ordinary
  payload resolution. The deliberately supplied host Context is a separate
  capability whose concrete classloader can expose host classes. The dedicated
  `testKnownGap...` test explicitly expects this gap. Classloader selection is not
  a hostile-native memory boundary, and this prototype is unsuitable as a general
  protected-app runtime.
- The debug harness's own JNI library is distinct from a library packaged in the
  uninstalled fixture APK. The test checks that the fixture contains its ABI's
  native library, then probes ordinary `System.loadLibrary` with DEX-only loading.
  No custom linker/extraction workaround is used.
- Revocation denies the research broker. It does not retroactively mediate
  framework handles, syscalls, or native threads already running in a compromised
  process. Close/unbind and death are lifecycle experiments, not complete
  revocation of every platform capability.
- The fixed `/system/bin/id -u` subprocess attempt has a two-second deadline and
  returns only same-UID, denial/failure or timeout. No arbitrary shell is accepted.

## Platform, artifacts and native provenance

Configured CI target: Google APIs Android **API 35 / x86_64 / debug**, official
Android emulator; actual run status is recorded below. Build baseline remains
compile/target SDK 37, provisional min SDK 31, AGP 9.4.0, Gradle 9.6.0 and JDK 17.
Native builds pin official NDK **27.2.12479018** and CMake **3.22.1**. SDK-manager
installation/build success is required to verify package availability; configuration
alone is not availability evidence. C/C++ is entirely project-owned. Both new
modules build x86_64 and arm64-v8a; compilation is not ARM64 device evidence.

`:research-native` is only a debug dependency of the manager app. It uses JNI,
`getuid/getgid/getpid`, direct `SYS_openat` for the synthetic sentinel and fixed
`/proc/self/maps`, manager PID `/proc/.../maps`, `/sys/devices/system/cpu/online`,
and a presence-only `ro.build.version.sdk` property query. Open attempts return
0 accessible / 1 permission denied / 2 absent / 3 other error. No raw file,
property, process-map, package-inventory or identifier contents are emitted.
Sentinel writes are synthetic; the manager compares the actual persistent bytes
after each execution and deletes the sentinel in a finally block.

The fixture performs no networking, requests no permissions and contains no user
data. Hash comparisons use a fresh nonce; only equality booleans leave the test.
The manifest's single fixed query makes manager-side non-installation checks
meaningful under package visibility filtering. No QUERY_ALL_PACKAGES is added.

All APKs, `.so`, native intermediates, generated assets and reports are build
outputs, never committed. The existing wrapper JAR is unchanged: Git blob
`b1b8ef56b44f16b14dc800fa8103a6d89abb526f`, SHA-256
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`.
`android/gradlew` remains mode 100755. Main/release manifest, backup exclusions,
cleartext policy and MainActivity remain unchanged. No new Maven test/runtime
dependency, engine framework, hidden API, network permission or telemetry is used.
Project licensing remains undecided as in PR 3; no third-party engine is vendored.

## Exact tests and results

Local Windows validation initially lacked Java/SDK. A temporary official Temurin
JDK 17.0.20.1+1 and Android SDK were installed, including the exact NDK/CMake
versions above. `--version`, `lintDebug`, `testDebugUnitTest`, `assembleDebug`,
`:app:assembleDebugAndroidTest` and `:app:processReleaseMainManifest` passed.
All five JVM tests executed with zero failures/skips. Native libraries built for
x86_64 and arm64-v8a. Lint has zero errors; the two retained warnings concern the
deliberately unchanged Gradle version and intentional sanitized Settings ID probe.
The generated source APK and test asset have identical SHA-256
`2705f1828b0ad8793fdc8f61ba80d12b867ac9a348c2a1e961a9bea6a9c50fee` in this local build.
The merged release manifest contains no research service, query or permissions.

No emulator/API/ABI was exercised locally: `emulator -accel-check` returned code 6,
"Android Emulator hypervisor driver is not installed on this machine". System
virtualization settings were not changed. Device results below remain pending CI.

| Exact test name | Intended evidence / interpretation | Observed result |
|---|---|---|
| `SessionPolicyTest.unknownUnsupportedAndMissingCoverageBlockBeforeDispatch` | All nonverified mandatory states deny dispatch | Passed locally |
| `SessionPolicyTest.malformedSessionAndFailedInitializationBlock` | Invalid input and failed initialization deny | Passed locally |
| `SessionPolicyTest.crossSessionStaleCallerAndUnknownOperationAreRejected` | Caller UID/PID, session, epoch and operation validation | Passed locally |
| `SessionPolicyTest.revocationAndDeathAreTerminalEvenWhenUidIsReused` | No stale authorization recovery | Passed locally |
| `SessionPolicyTest.retriesAreBoundedAndManagerUidCannotRegister` | Bounded attempts and manager UID denial | Passed locally |
| `PrototypeTests.testUnknownCoverageBlocksBeforeProbeExecution` | Gate denies binding/dispatch; no entry callback | Not run |
| `PrototypeTests.testSeparateIsolatedInstancesRejectCrossSessionStaleAndMalformedRequests` | Different Binder-observed identities; actual transferred capability rejected | Not run |
| `PrototypeTests.testIsolatedSessionDeathDoesNotRestoreAuthority` | Kill/death/replacement and stale claim denial | Not run |
| `PrototypeTests.testRevokedSessionBlocksBeforeEntrypoint` | Live service has zero invocation after revocation | Not run |
| `PrototypeTests.testMalformedDexFailsInitializationWithoutEntrypoint` | Invalid DEX never invokes controlled entry | Not run |
| `PrototypeTests.testArtifactDerivedDexExecutesWhilePackageRemainsUninstalled` | DEX entry, distinct UID, restricted parent, unchanged APK | Not run |
| `PrototypeTests.testKnownGapUninstalledArtifactHasNoNormalLifecycleOrNativeLibraryPath` | Reproduce missing package lifecycle and imported-native support; **Gap** if reproduced | Not run |
| `PrototypeTests.testJavaAndNativeDirectOpenCannotReadOrMutateManagementSentinel` | Java/native denial plus persistent manager-state comparison | Not run |
| `PrototypeTests.testKnownGapHostBuildContextAndSanitizedPlatformObservations` | Reproduce unmediated Build/Context; report sanitized package/service/native/subprocess outcomes | Not run |

JVM sources are under `app/src/testDebug`; device sources under `app/src/androidTest`.
The platform-only Instrumentation runner emits standard start/pass/failure events.
CI additionally parses result XML and fails on zero/missing/skipped tests. Device
failures and `KnownGap` observations must never be renamed into protection claims.

Validation commands from `android/`:

```sh
./gradlew --version
./gradlew lintDebug
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew :app:assembleDebugAndroidTest
bash tools/run-containment-emulator.sh
```

The normal CI job remains; a separate bounded 45-minute job installs official
tooling, boots/waits for API 35 x86_64, runs `:app:connectedDebugAndroidTest` and
terminates the emulator. CI must build and execute these tests before merge
readiness. No API 31–37, OEM, physical-device or production-release support follows
from one debug emulator run.

## Requirement traceability

No requirements are globally completed or weakened. States below describe this
prototype's current evidence, not the normative register.

| Requirement | Current state | Test/source scope and remaining question |
|---|---|---|
| PD-REQ-008 | Unknown | Artifact-derived DEX/integrity test; full app, signatures, split sets and production import remain gaps |
| PD-REQ-011 | Unknown | Binder UID/PID and separate instances; hostile native/resource boundaries remain partial at best |
| PD-REQ-012 | Unknown | Sentinel persistent-state and transferred broker tests; no real management databases/secrets exercised |
| PD-REQ-013 | Gap | Only a narrow research broker is checked; no general Binder/provider mediation |
| PD-REQ-014 | Unknown | Harness JNI/direct open/proc/sys/property tests; arbitrary imported native execution remains a gap hypothesis |
| PD-REQ-015 | Partial | Lifecycle, subprocess, instance and death experiments implemented; ordinary component lifecycle missing |
| PD-REQ-016 | Not exercised | Public APIs only in this implementation; hidden/private/OEM inventory and matrix still open |
| PD-REQ-021 | Unknown | Pre-code research gate tests; production mandatory coverage remains Unknown and must block |
| PD-REQ-027 | Partial | Narrow broker epoch/revocation tests only; cached framework/native capabilities unmediated |
| PD-REQ-030 | Gap | Fixed sanitized queries; no virtual package universe |
| PD-REQ-031 | Gap | Settings/Context/service observations only; no host-service mediation |
| PD-REQ-041 | Unknown | Synthetic Java/native sentinel test only; broad storage/cross-tenant semantics not established |
| PD-REQ-044 | Partial | Research dispatch gate and negative tests; no full-app early component/native lifecycle runtime |
| PD-REQ-045 | Unknown | Bounded state machine, bind timeout, death and revocation tests; hostile process exhaustion still open |
| PD-REQ-057 | Partial | Controlled positive/adversarial/negative/lifecycle suites exist; device execution pending |
| PD-REQ-060 | Partial | Debug replacement interface and project-owned provenance; full SBOM, vulnerability/license audit remain open |

## PR 5 handoff and PR 6 implications

PR 5 must inventory the management process, every isolated instance and any
successful fixed subprocess; establish attribution, independent packet evidence,
external-VPN verification, route changes and physical-route denial. No networking
containment is implemented here and no VpnService is authorized.

PR 6 must confront the difference between **uninstalled artifact-derived DEX
execution**, **full Android application execution**, **framework/API mediation**
and **native imported-code support**. Distinct UIDs and sentinel denial, if
observed, cannot fill the lifecycle and host-state gaps. No production engine or
feasibility outcome is selected. Options remain more research, redesign, explicit
unsupported status or STOP, with an explicit user decision.

Platform references: [isolated-service binding](https://developer.android.com/reference/android/content/Context#bindIsolatedService(android.content.Intent,int,java.lang.String,java.util.concurrent.Executor,android.content.ServiceConnection)),
[in-memory DEX loader](https://developer.android.com/reference/dalvik/system/InMemoryDexClassLoader),
[Android sandbox](https://source.android.com/docs/security/app-sandbox).
These explain the APIs; they are not substitutes for test observations.
