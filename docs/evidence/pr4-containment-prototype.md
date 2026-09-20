# PR 4: Adversarial containment research prototype

Date: 2026-09-20 (UTC). Base commit: `4dd8cdf3eb5f224cc404e188f9b7678798604ff9`.
Implementation branch: `codex/pr4-adversarial-containment`. Tested implementation
commit: `8386a4e86d588e5f50abf724798f9aca87f0cec3`.
[PR CI run 35487976187](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35487976187)
and [push CI run 35487974084](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35487974084)
both passed normal validation and containment instrumentation. Each executed
**nine device tests with zero failures or skips**; XML discovery validation
confirmed the count. The observation table below is from the PR run.
This document records that tested implementation; subsequent evidence-only edits
do not change the source under test.

Earlier runs caught invalid service-instance names, a multidex fixture packaging
mismatch, and a sentinel assertion that required permission denial instead of
also accepting path-absent. Those issues were corrected and rerun; earlier
failed runs are not counted as passing evidence. The final sentinel assertion
still rejects accessible or ambiguous outcomes, and management independently
checks that its existing sentinel remains unchanged.

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

The controlled fixture explicitly disables multidex, and the test rejects APKs
with additional DEX entries. This is a bounded single-DEX experiment, not support
for arbitrary multidex or split packages. The source artifact is built with that
configuration; it is never repacked or re-signed during testing. Instance names
are validated against the platform's ASCII letter/digit/underscore/period contract.

`ResearchBoundary` is the disposable internal interface; `ResearchSession` owns
the manager-side `SessionPolicy` and Binder broker. Registration records
Binder-observed UID **and PID**, never a supplied UID. Session ID, epoch, operation
allowlist, revocation and death are checked on every broker request. One session
gets one payload invocation. Broker capabilities contain no durable secret.
The tests deliberately transfer A's actual broker to B and attempt an A claim.
Death is observed with `linkToDeath`, old state is terminal, and replacement gets
a new session identity. No application-managed retry or restart loop exists; the pure policy
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

## Observed gaps and limits

The device run above reproduced these adverse observations. Passing a `KnownGap`
test means the gap was observed, not that privacy was protected:

- Application and ContentProvider markers remained false. The declared provider,
  package Context and resources were unavailable; full component execution and a
  virtual package universe are not implemented.
- Build fingerprint equality with management was true. The fixed platform package
  query and activity-service lookup were visible. Settings access was
  denied-or-unavailable, with no positive ID equality claim. This combination is
  evidence of incomplete host-state mediation, not a complete interface inventory.
- A bootstrap DEX parent excludes management implementation classes from ordinary
  payload resolution. The deliberately supplied host Context is a separate
  capability whose concrete classloader can expose host classes. The dedicated
  `testKnownGap...` test reproduced this gap. Classloader selection is not
  a hostile-native memory boundary, and this prototype is unsuitable as a general
  protected-app runtime.
- The debug harness's own JNI library is distinct from a library packaged in the
  uninstalled fixture APK. The test checks that the fixture contains its ABI's
  native library, then probes ordinary `System.loadLibrary` with DEX-only loading.
  That imported-library load returned false. The harness JNI executed separately;
  no custom linker/extraction workaround is used.
- Revocation denies the research broker. It does not retroactively mediate
  framework handles, syscalls, or native threads already running in a compromised
  process. Close/unbind and death are lifecycle experiments, not complete
  revocation of every platform capability.
- The fixed `/system/bin/id -u` subprocess completed with the same isolated UID.
  It has a two-second deadline. This establishes one subprocess observation, not
  complete subprocess mediation. No arbitrary shell is accepted.

The sanitized observation from the recorded run was:

| Observation | Result and interpretation |
|---|---|
| Management identity | UID 10209 / PID 4401 (ephemeral test identity) |
| Native isolated identity | UID/GID 99004 / PID 4480; distinct from management |
| Java sentinel read/write | Denied-or-unavailable; persistent manager bytes unchanged |
| Native sentinel read/write opens | 2 / 2: path absent from the isolated caller's view; no access, but not a permission-denied errno |
| Own `/proc/self/maps` open | 0: accessible; contents not read or logged |
| Known manager `/proc/.../maps` open | 1: permission denied |
| Fixed CPU-online sysfs open | 0: accessible; contents not read or logged |
| SDK system property | Presence true; value not returned or logged |
| Build / host Context | Host Build equality and host Context class availability true: **Gap** |
| Fixed package / activity service | Visible / visible: **Gap** in general mediation |
| Settings | Denied-or-unavailable; equality false does not establish a synthetic value |
| Fixed subprocess | Same isolated UID |

The precise reason for native ENOENT was not established by this experiment.
The test checks the sentinel in the manager before and after execution; an absent
path reported by the isolated caller must not be described as a missing fixture.
Selected accessible proc/sys/property surfaces are gaps for future mediation
analysis, even though no raw contents are returned by this controlled probe.

## Platform, artifacts and native provenance

Exercised CI target: Google APIs Android **API 35 / x86_64 / debug**, official
Android emulator; actual run status is recorded below. Build baseline remains
compile/target SDK 37, provisional min SDK 31, AGP 9.4.0, Gradle 9.6.0 and JDK 17.
Native builds pin official NDK **27.2.12479018** and CMake **3.22.1**. SDK-manager
installation and local/CI builds succeeded for these exact packages. C/C++ is
entirely project-owned. Both new modules build x86_64 and arm64-v8a;
compilation is not ARM64 device evidence.

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
`50e3366bfc18c4ea5533dc0be8d5a6c9f6fdae5871865d38db891890c9e49641` in the
local build at `415f4c1`. DEX inspection confirms the entrypoint and helper classes
are in its sole `classes.dex`; the former multidex build omitted them from the
transferred DEX. Debug signing is environment-specific, so this digest is local
artifact evidence, not a promise of byte-identical APKs across signing hosts.
The merged release manifest contains no research service, query or permissions.

No emulator/API/ABI was exercised locally: `emulator -accel-check` returned code 6,
"Android Emulator hypervisor driver is not installed on this machine". System
virtualization settings were not changed. The API 35 x86_64 CI emulator executed
the device suite; the table records the results at the commit above.

| Exact test name | Intended evidence / interpretation | Observed result |
|---|---|---|
| `SessionPolicyTest.unknownUnsupportedAndMissingCoverageBlockBeforeDispatch` | All nonverified mandatory states deny dispatch | Passed locally |
| `SessionPolicyTest.malformedSessionAndFailedInitializationBlock` | Invalid input and failed initialization deny | Passed locally |
| `SessionPolicyTest.crossSessionStaleCallerAndUnknownOperationAreRejected` | Caller UID/PID, session, epoch and operation validation | Passed locally |
| `SessionPolicyTest.revocationAndDeathAreTerminalEvenWhenUidIsReused` | No stale authorization recovery | Passed locally |
| `SessionPolicyTest.retriesAreBoundedAndManagerUidCannotRegister` | Bounded attempts and manager UID denial | Passed locally |
| `PrototypeTests.testUnknownCoverageBlocksBeforeProbeExecution` | Gate denies binding/dispatch; no entry callback | Passed in CI |
| `PrototypeTests.testSeparateIsolatedInstancesRejectCrossSessionStaleAndMalformedRequests` | Different Binder-observed identities; actual transferred capability rejected | Passed in CI |
| `PrototypeTests.testIsolatedSessionDeathDoesNotRestoreAuthority` | Kill/death/replacement and stale claim denial | Passed in CI |
| `PrototypeTests.testRevokedSessionBlocksBeforeEntrypoint` | Live service has zero invocation after revocation | Passed in CI |
| `PrototypeTests.testMalformedDexFailsInitializationWithoutEntrypoint` | Invalid DEX never invokes controlled entry; failure must be in DEX-loader phase | Passed in CI |
| `PrototypeTests.testArtifactDerivedDexExecutesWhilePackageRemainsUninstalled` | DEX entry, distinct UID, restricted parent, unchanged APK | Passed in CI |
| `PrototypeTests.testKnownGapUninstalledArtifactHasNoNormalLifecycleOrNativeLibraryPath` | Reproduce missing package lifecycle and imported-native support | Passed in CI: **Gap reproduced** |
| `PrototypeTests.testJavaAndNativeDirectOpenCannotReadOrMutateManagementSentinel` | Java/native non-access plus persistent manager-state comparison | Passed in CI; native opens returned absent |
| `PrototypeTests.testKnownGapHostBuildContextAndSanitizedPlatformObservations` | Reproduce unmediated Build/Context; report sanitized package/service/native/subprocess outcomes | Passed in CI: **Gap reproduced** |

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
terminates the emulator. Both jobs passed for the implementation recorded above.
No API 31–37, OEM, physical-device or production-release support follows
from one debug emulator run.

## Requirement traceability

No requirements are globally completed or weakened. States below describe this
prototype's current evidence, not the normative register.

| Requirement | Current state | Test/source scope and remaining question |
|---|---|---|
| PD-REQ-008 | Preliminary evidence | Unchanged uninstalled single-DEX APK entry executed; full app, signatures, multidex, split sets and production import remain gaps |
| PD-REQ-011 | Preliminary evidence | Binder-observed UID/PID differ from management and between instances; hostile native/resource boundaries remain partial at best |
| PD-REQ-012 | Partial | Sentinel persistent-state and transferred broker checks; no real management databases/secrets or cross-tenant storage exercised |
| PD-REQ-013 | Gap | Only a narrow research broker is checked; no general Binder/provider mediation |
| PD-REQ-014 | Partial / Gap | Harness JNI executed; selected proc/sys/property surfaces accessible, sentinel absent, manager maps denied; imported native load unavailable |
| PD-REQ-015 | Partial | Lifecycle, subprocess, instance and death experiments implemented; ordinary component lifecycle missing |
| PD-REQ-016 | Not exercised | Public APIs only in this implementation; hidden/private/OEM inventory and matrix still open |
| PD-REQ-021 | Preliminary evidence | JVM and device pre-code research gates deny nonverified coverage; production mandatory coverage remains Unknown and must block |
| PD-REQ-027 | Partial | Narrow broker epoch/revocation tests only; cached framework/native capabilities unmediated |
| PD-REQ-030 | Gap | Fixed sanitized queries; no virtual package universe |
| PD-REQ-031 | Gap | Settings/Context/service observations only; no host-service mediation |
| PD-REQ-041 | Preliminary evidence | Synthetic Java/native non-access and unchanged persistent bytes; broad storage/cross-tenant semantics not established |
| PD-REQ-044 | Partial | Research dispatch gate and negative tests; no full-app early component/native lifecycle runtime |
| PD-REQ-045 | Partial | Bounded state-machine tests and actual death/revocation checks pass; hostile process exhaustion and adversarial timing remain open |
| PD-REQ-057 | Preliminary evidence | Five JVM and nine real device tests pass, including positive/adversarial/negative/lifecycle cases; broader devices and hostile workloads remain open |
| PD-REQ-060 | Partial | Debug replacement interface and project-owned provenance; full SBOM, vulnerability/license audit remain open |

## PR 5 handoff and PR 6 implications

PR 5 must inventory the management process, every isolated instance and any
successful fixed subprocess; establish attribution, independent packet evidence,
external-VPN verification, route changes and physical-route denial. No networking
containment is implemented here and no VpnService is authorized.

PR 6 must confront the difference between **uninstalled artifact-derived DEX
execution**, **full Android application execution**, **framework/API mediation**
and **native imported-code support**. The observed distinct UIDs and sentinel
non-access cannot fill the lifecycle and host-state gaps. No production engine or
feasibility outcome is selected. Options remain more research, redesign, explicit
unsupported status or STOP, with an explicit user decision.

Platform references: [isolated-service binding](https://developer.android.com/reference/android/content/Context#bindIsolatedService(android.content.Intent,int,java.lang.String,java.util.concurrent.Executor,android.content.ServiceConnection)),
[in-memory DEX loader](https://developer.android.com/reference/dalvik/system/InMemoryDexClassLoader),
[Android sandbox](https://source.android.com/docs/security/app-sandbox).
These explain the APIs; they are not substitutes for test observations.
