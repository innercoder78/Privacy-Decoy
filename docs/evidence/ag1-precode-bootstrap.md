# AG-1B admission-bound launch and pre-code bootstrap

**Status: AG-1 IN PROGRESS — AG-1B PRE-CODE BOOTSTRAP SLICE**

## Bounded research question

Can Privacy Decoy bind an exact AG-1A artifact generation, admission outcome,
requested execution class, and consent generation to a fresh isolated research
session; establish a trusted bootstrap barrier before guest artifact bytes or
classes are exposed to that session; and fail closed on stale, mismatched,
revoked, or unauthorized launch state?

This is supplemental evidence toward AG-1 questions 3, 7, and 8, not canonical
production Roadmap PR 6 and not AG-1 completion. It builds on merged AG-1A at
`cd9b0c1fc07500b3c053841ef8ba67fdc28655e3`. Historical PR4, PR5, AG-1A, S1/S2,
ADR-0006, and ADR-0007 evidence is preserved unchanged. Requirement traceability:
PD-REQ-015, 019, 021, 027, 044, 045, 057, 085–090, 093, and 095. This slice does
not globally satisfy any of those requirements.

## Exact artifact and execution authority

The runner builds `ag1-precode-fixture`, invokes the unchanged AG-1A analyzer on
that exact APK, and requires `EXPERIMENTAL_ELIGIBLE`, one base, sole `classes.dex`,
no packaged native library, and no detected dynamic-loader/native-load/opaque
payload indicator. Absence of an indicator is not proof of mediation. AG-1A's
`RUNTIME_MEDIATION_UNPROVEN` remains applicable. No real fixture is
`PROTECTED_ELIGIBLE`.

Gradle copies the unchanged APK into generated androidTest assets and compares
source/copy SHA-256. The runner also compares the analyzed hash against the
source APK, generated asset, and asset embedded in the test APK after assembly.
Device instrumentation independently hashes the embedded APK before DEX
extraction or binding. The manager clones those bounded bytes, hashes them again,
and independently recomputes AG-1A's `ag1a-2` single-base canonical generation
identity from SHA-256 and byte size. A changed analyzer generation schema requires
an explicit update; the current runner fails closed on a version mismatch.

Analyzer arguments are trusted research test inputs from the manager's runner,
not guest assertions and not a production admission-record signature. The
manager owns the immutable admission snapshot, requested execution class,
synthetic consent, session ID/epoch, and revocation state. Guest-supplied values
never update them. APK bytes stay in the manager; the isolated process receives
only bounded metadata until READY.

| Admission / request | Research launch rule |
|---|---|
| Experimental eligible / Protected | Block before binding or transfer |
| Experimental eligible / Experimental | Require exact generation and SHA, explicit Experimental consent, current positive consent epoch, active consent, and exact artifact bytes |
| Known unsafe / either | Unconditional hard stop; no override |
| Incompatible / either | Unconditional hard stop; no override |
| Unknown/malformed state | Deny |
| Hypothetical Protected eligible / Protected | Represented only in pure JVM policy tests; not real fixture evidence |

Consent is synthetic test consent only. It contains generation, SHA, epoch, and
the Experimental execution class. It is not a production UI or persisted consent
store. Generation A's consent cannot authorize B, even when bytes are otherwise
claimed equal. Consent revocation is terminal and checked at validation,
registration, READY, transfer reservation, and consumption; revoked records
cannot authorize another session. Manager session revocation, close, and observed
process death revoke the bound consent. A replacement requires fresh synthetic
consent and a fresh isolated session. The manager serializes transfer with session
revocation; the one-use transfer reservation is consumed by the isolated
process before reading the descriptor. This does not revoke arbitrary platform
handles or already-running hostile code.

## Bootstrap barrier and controlled execution

Separate `com.privacydecoy.research.ag1` classes avoid changing PR4
`SessionPolicy` semantics. A debug-only, non-exported `isolatedProcess` service
uses a fresh `bindIsolatedService` instance. The debug process declaration denies
INTERNET. Main/release capabilities are unchanged.

1. Manager validates exact APK/generation, admission, execution class, and consent.
2. A fresh service receives metadata plus a narrow authority Binder, without APK
   bytes, DEX bytes, or guest class names selected by a caller.
3. Registration records Binder-observed UID and PID, both distinct from the
   manager. The service binds its endpoint to the observed manager UID/PID.
4. The manager verifies exact metadata again and all six positive research
   prerequisites: artifact, admission, execution class, identity, session, and
   guest transfer gate. Missing/Unknown prerequisites deny READY.
5. The service records monotonic READY and acknowledges it. The manager requires
   that acknowledgement before reserving the single transfer.
6. Only now the manager extracts the sole bounded DEX, creates SharedMemory,
   removes the writable mapping, and sets read-only protection.
7. The service verifies exact session metadata and consumes current manager
   authorization before accessing the descriptor. It checks size and DEX digest,
   maps read-only, and constructs `InMemoryDexClassLoader` with a platform-only
   parent. No writable executable cache is created.
8. It resolves fixed fixture classes and deliberately calls the synthetic
   provider callback, Application callback, and target entry in that order.

The asserted timeline is:

```text
READY < bytes accepted <= loader start <= first class resolution
      <= provider static init <= provider callback
      <= Application static init <= Application callback
      <= entry static init <= target entry
```

All fixture events must also be strictly later than READY. Service counters
record loader creation, resolution attempts, and completed invocation. No guest
classloader, guest class resolution, static initializer, callback, or entrypoint
may occur before READY. The manager also records extraction and transfer counts.
Each successful session allows one invocation only. IPC and binding have
ten-second deadlines; errors revoke authority, and there is no restart/retry
loop. Manager authority death kills the controlled isolated process.

The Java-only fixture comprises `PreCodeProvider`, `PreCodeApplication`, and
`PreCodeEntry`. It has no native library, permissions, network access, telemetry,
dynamic loading, accounts, or private data. Its outputs are monotonic event
timestamps and a fixed provider constant; no raw device values are emitted.
Names describe synthetic callbacks, not Android framework component startup.
The APK is never installed, rewritten, or re-signed by this experiment. No guest
Context, resource virtualization, or genuine host-data access is supplied.

## Negative and positive tests

Ten JVM tests exercise the pure launch/bootstrap policy, including the four
outcomes, separate execution classes, missing/wrong/stale/revoked consent,
artifact and generation mismatch, all missing prerequisites, early transfer,
UID/PID/session/epoch mismatch, one-use authorization, revocation, death, update
invalidation, and Unknown/malformed authority. A hypothetical future Protected
state appears only in this pure model.

The `ag1runtime` suite discovers exactly eight instrumentation tests:

| Test | Scope |
|---|---|
| `testLaunchPolicyHardStopsBeforeBinding` | Real Experimental admission requested as Protected; absent/stale/wrong consent; Known unsafe and Incompatible for both execution classes; zero binds/extractions/transfers |
| `testArtifactAndGenerationMismatchBeforeBinding` | SHA mismatch, requested-generation mismatch, changed embedded bytes, and forged mutually matching generation/consent; all denied before binding |
| `testUpdatedGenerationCannotInheritConsent` | New generation rejects old consent; a revoked record cannot authorize another session |
| `testMissingPrerequisiteNeverReady` | Each omitted prerequisite and an Unknown set fail; actual service records no READY and zero guest loads |
| `testRunBeforeReadyHasZeroGuestClassLoads` | Manager execute and direct metadata-only service RUN before READY are denied |
| `testRevocationAndReplayBeforeTransfer` | Revocation after READY prevents extraction/transfer; direct RUN, repeated stale request, and re-arm denied; zero loads |
| `testProcessDeathInvalidatesAuthorization` | Actual process kill and Binder death invalidate authority; replacement rejects old session identity |
| `testExactExperimentalExecutionAfterBarrier` | The only positive execution: exact analyzed bytes, distinct observed UID/PID, ordered monotonic events, one invocation, second execution/replay rejected, guest remains uninstalled |

Pre-bind failures cannot load guest classes because no isolated service exists.
Bound negative cases independently check manager extraction/transfer counts and
service byte/loader/resolution/invocation counters. Direct negative RUN probes
contain metadata only, so they do not themselves violate the no-bytes-before-READY
invariant. Assertion messages are fixed and do not print hashes, times, raw
identities, arbitrary exceptions, or private paths.

## Reproduction, CI, and interpretation

`bash android/tools/run-ag1-precode-emulator.sh` builds/analyzes the fixture,
checks byte identity, builds the manager/test packages, boots one disposable API
35 Google APIs x86_64 emulator, installs only those two packages, and invokes
only `ag1runtime`. It requires exactly eight starts/passes, zero failures/skips,
and successful instrumentation completion. It performs no retry of a failed
security assertion. It terminates the emulator and removes temporary logs,
admission output, disposable AVD, and generated APKs. No evidence artifact is
uploaded. The distinct `admission-runtime-feasibility` job uses Ubuntu 24.04,
JDK 17, pinned wrapper/SDK/NDK/CMake, KVM, and a 45-minute timeout.

Classifier validation covers docs-only, analyzer-only, AG-1B runtime-only,
AG-1B fixture, shared runner, network-only, managed-only, and unknown Android
paths. The shared instrumentation runner selects containment, network, and
AG-1 runtime. Unknown/shared paths include all suites. AG-1B fixture/runner paths
also select static admission; AG-1B policy paths do not unnecessarily select
historical containment/network/managed suites. Existing suite discovery and
defaults remain unchanged.

Local Desktop validation on 2026-09-23 used JDK 17.0.20.1, SDK 37.0, Build Tools
36.0.0, NDK 27.2.12479018, CMake 3.22.1, and the unchanged Gradle 9.6.0 wrapper.
The incomplete local toolchain was restored using official temporary downloads;
no toolchain binary was added to Git. A Windows SDK launcher path/quoting problem
was resolved in temporary tooling without changing AG-1A.

* The analyzer, network-evidence, and managed-profile Python suites passed
  **13 + 19 + 25 tests**.
* `bash -n` passed for the AG-1A runner, AG-1B runner, and CI classifier. Embedded
  runner Python snippets parsed, and all eight representative classifier inputs
  produced the required suite selections.
* App lint, unit tests, debug assembly, instrumentation assembly, and fixture
  lint/debug assembly passed. All **24 app JVM tests** passed without failures,
  errors, or skips: 10 AG-1B, 9 historical network, and 5 historical session tests.
  The fixture's valid unit-test task reported `NO-SOURCE`; its guest behavior is
  tested by the app instrumentation suite. Lint reported no errors: one existing
  app tool-version warning and two fixture warnings for backup-rule guidance and
  its intentionally absent launcher icon.
* The merged release manifest passed `verify-release-manifest.py`, with no
  networking permissions or research components.
* The unchanged analyzer inspected the real built fixture successfully and
  established package/version/signing metadata. Its only finding was
  `RUNTIME_MEDIATION_UNPROVEN`; admission was `EXPERIMENTAL_ELIGIBLE`. Source,
  generated copy, and embedded test asset had the same SHA-256:
  `4b9c2d76dff8599b655c71aa447fd067b3e6e10cb9492d607a12f1342c549572`.
  The generation was
  `bc3a05de2d1ade45c12e9d0dacb1571b59511ca6da84cedd6946afb905fd0168`.
  These identify this local controlled build, not byte reproducibility across
  different debug-signing environments.

The wrapper remains Git blob
`b1b8ef56b44f16b14dc800fa8103a6d89abb526f`, SHA-256
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`;
`android/gradlew` remains mode `100755`.

The required Linux/KVM environment was unavailable on the local Windows host.
No local emulator or security-assertion retry was run. The eight device tests
describe intended, falsifiable checks until a scoped device run passes. No
exact-head GitHub CI success is asserted here. **CI pending after publication.**

## What remains Unknown

Even a passing controlled device run establishes only exact artifact/authorization
binding and this pre-code research ordering on the specified debug emulator.
Service/fixture observations after guest execution begins are not independent
proof against an arbitrary hostile payload. Binder identity and manager-side
byte/extraction/transfer checks corroborate narrower claims only.

AG-1B does **not** establish actual Protected eligibility, complete
framework/Binder/provider mediation, true Android provider/Application lifecycle
virtualization, native guest containment, raw-syscall containment, dynamic
executable-code blocking, Persona implementation, external-VPN enforcement for a
product runtime, AG-1 completion, or production privacy. It does not implement
the general AG-1 runtime executable-code gate: unexpected post-launch DEX/native
introduction is later work/Unknown. No guest `.so` executes. No ByteHook,
ShadowHook, Pine, external engine, or other runtime dependency/source is added.

Only controlled synthetic data is used; no ordinary private data or real accounts.
Canonical PR 5's owner gate has not occurred. Canonical production PR 6 remains
unauthorized. AG-1 remains in progress, and this slice does not authorize AG-1C.

## First exact-head AG-1B device run

For PR #21 head `6dbe461ba3202b758c5ce512bdc30d4d5534377b`, Actions run
#142 passed `changes`, `validate`, `containment-prototype`,
`admission-feasibility`, `network-feasibility`, and
`managed-profile-feasibility`. `admission-runtime-feasibility` failed after
reaching the AG-1B emulator runner. The surfaced diagnostic was only the generic
assertion `Expected eight starts/passes, zero failures or skips`; cleanup removed
the redirected instrumentation transcript. The exact device-test failure at
that head therefore remains **Unknown**. These results do not establish an
AG-1B runtime-policy defect or AG-1B device success.

This diagnostic-only revision prints a bounded summary before result validation:
the eight allowlisted test names, observed status labels, start/pass/failure
flags, final test totals, final instrumentation code, and presence of an
instrumentation-failed marker. Fixed result labels distinguish device failures,
invalid test counts, invalid status sequences, and incomplete termination.
An unsuccessful instrumentation command still reaches the summary parser and
still fails the job.

Failure details require an exact known assertion message or an allowlisted
exception category, at most 100 characters, using only ASCII letters, spaces,
colons, and hyphens. Other details become `redacted`. Unknown test names, raw
transcripts, stack traces, paths, hashes, identities, and timestamps are never
printed. Transcript input and summary output are bounded; malformed, oversized,
unexpected, duplicate, missing, skipped, or unsuccessful results fail closed.
Exactly eight recognized starts and passes, zero failures/skips, exact successful
totals, successful termination, and absence of `INSTRUMENTATION_FAILED` remain
mandatory. No security assertion or AG-1B Java policy/test semantics is weakened,
and no security-test retry is added. Local Linux/KVM device reproduction remains
unavailable. No AG-1B device success is claimed; the next exact-head CI run must
provide the missing evidence.

Local diagnostic validation passed shell syntax, all 57 existing Python tests,
and 16 synthetic parser cases, including clean passes, a recognized failure,
the missing eighth test, unexpected status, missing final code, and unsafe detail
redaction. Additional cases covered skips, duplicate fields/completion, unknown
names, oversized input/details, exception allowlisting, printable secret text,
the failed marker, and command timeout. Synthetic transcripts remain uncommitted
temporary build products.

## Second exact-head AG-1B device run

For PR #21 head `21ba3176ea97e1521a4ab32f337d85758cb62b0a`, Actions run
#143 passed seven of eight AG-1B device tests. Only
`testProcessDeathInvalidatesAuthorization` failed, with bounded detail
`platform-or-harness-exception:IllegalStateException`. The exact lifecycle
phase remains **Unknown** at that head. The other jobs (`changes`, `validate`,
`containment-prototype`, `admission-feasibility`, `network-feasibility`, and
`managed-profile-feasibility`) passed; `admission-runtime-feasibility` failed.

This revision adds fixed failure labels for initial bind, initial arm,
pre-death observation, death observation, replacement bind, replacement arm,
stale-claim probe, and replacement observation. Only an unexpected
`IllegalStateException` in those phases becomes a fixed assertion message;
the original exception message and cause are not preserved. Existing assertions
and intentional execution-denial checks remain unchanged. The parser allowlist
adds only those eight fixed messages, with all sanitization and strict result
validation unchanged. Process-death, authorization, and bootstrap behavior are
unchanged. No AG-1B success is claimed yet.

Local validation passed shell syntax, all 57 existing Python tests, and
`:app:lintDebug`, `:app:testDebugUnitTest`, and `:app:assembleDebugAndroidTest`.
Eight temporary synthetic instrumentation transcripts independently exercised
the final parser: every new fixed phase message was emitted unchanged within
the existing bounds and produced an unsuccessful overall result. No synthetic
transcript is committed. Local Linux/KVM device reproduction remains unavailable;
the next exact-head CI run must establish the failing lifecycle phase.

## Third exact-head AG-1B device run

For PR #21 head `b7324c2a5132021e0f825e047511ee4068315b3b`, Actions run
#144 passed seven of eight AG-1B device tests. Only
`testProcessDeathInvalidatesAuthorization` failed, with the exact fixed phase
`death observation failed`. Initial bind, arm, and pre-death observation
therefore completed. The other jobs (`changes`, `validate`,
`containment-prototype`, `admission-feasibility`, `network-feasibility`, and
`managed-profile-feasibility`) passed; `admission-runtime-feasibility` failed.

The old implementation relied on callback/latch observation after issuing KILL.
This revision retains callback-based Binder death notification and adds liveness
observation of the exact old Binder proxy captured before the single-shot KILL.
A monotonic ten-second deadline and latch waits of at most 50 milliseconds bound
the observation loop. A false `isBinderAlive()` or `pingBinder()` result marks
the policy DEAD and signals the latch; elapsed time alone never establishes
death. Missing death evidence still fails with `AG-1 death not observed`.
An IPC exception during KILL succeeds only if that captured Binder is observed
dead; a live target's exception propagates. A `linkToDeath()` registration
exception now signals the latch as well as marking the policy DEAD.

These checks follow the [Android IBinder contract](https://developer.android.com/reference/android/os/IBinder).
Later reconnection assignments cannot replace the target being observed, and
the existing terminal DEAD policy cannot authorize a replacement. No
launch-policy, consent, pre-code ordering, or guest-execution assertion is
weakened. The instrumentation test and its phase labels remain unchanged.
No new exact-head CI success or AG-1B device success is claimed.

Local validation passed `:app:lintDebug`, `:app:testDebugUnitTest` (24 tests,
zero failures/errors/skips), `:app:assembleDebug`, and
`:app:assembleDebugAndroidTest`, all 57 existing Python tests, and AG-1B runner
shell syntax. The local Windows host still lacks Linux/KVM support, so no local
emulator run or security-test retry was performed. Exact-head device validation
remains pending after publication.

## Fourth exact-head AG-1B device run

For PR #21 head `618b829cf80666e4bd5ab1ef82c5bc4d3d5d098c`, Actions run
#145 passed seven of eight AG-1B device tests. Only
`testProcessDeathInvalidatesAuthorization` failed; the phase remained
`death observation failed`. Callback and captured-Binder `isBinderAlive()` /
`pingBinder()` observation did not establish death within the bounded window.
All other historical/baseline jobs (`changes`, `validate`,
`containment-prototype`, `admission-feasibility`, `network-feasibility`, and
`managed-profile-feasibility`) passed; `admission-runtime-feasibility` failed.

This revision changes only the controlled debug/research service's process-death
injection. The authorized KILL branch marks the service terminal, captures its
own PID, calls `Process.killProcess(pid)` once, and immediately calls
`Runtime.getRuntime().halt(0)` if execution continues. If termination returns
or an exception reaches the existing handler, the service remains terminal and
does not report acceptance. ARM/RUN remain denied in that terminal state.
The hard VM termination fallback is confined to KILL; normal lifecycle and
positive controlled execution are unchanged.

KILL remains single-shot. Manager-side authorization and positive death proof
are unchanged, including the callback latch, captured old Binder, liveness
checks, bounded deadline, and hard failure without observed death. No security
assertion is weakened. This tests the controlled death-injection hypothesis;
no AG-1B success is claimed before exact-head CI passes.

Local validation passed `:app:lintDebug`, `:app:testDebugUnitTest` (24 tests,
zero failures/errors/skips), `:app:assembleDebug`, and
`:app:assembleDebugAndroidTest`, all 57 existing Python tests, and AG-1B runner
shell syntax. Linux/KVM support remains unavailable on the local Windows host;
no local emulator run or security-test retry was performed. CI pending after
publication.

## Fifth exact-head AG-1B device run

For PR #21 head `401ffae5b4e8d56cfddc380b02beee6686a7012e`, Actions run
#146 passed seven of eight AG-1B device tests. Only
`testProcessDeathInvalidatesAuthorization` failed, still at
`death observation failed`. All historical/baseline jobs (`changes`, `validate`,
`containment-prototype`, `admission-feasibility`, `network-feasibility`, and
`managed-profile-feasibility`) passed; `admission-runtime-feasibility` failed.
Hard service-side termination did not execute successfully through that transport.

Code review identified KILL's `IBinder.FLAG_ONEWAY` transport as incompatible
with the service's exact manager UID/PID check for post-INIT transactions.
[Android's Binder contract](https://developer.android.com/reference/kotlin/android/os/Binder#getCallingPid())
states that one-way calls receive no calling PID and expose PID zero. Together
with the unchanged service check, this establishes why KILL was denied before
its termination branch. This is a controlled research-session binding check,
not a claim that PID alone is a production security identifier.

KILL now uses a synchronous transaction with flags zero and a reply Parcel so
the existing identity check can operate. A normal return reads the Binder
exception status and then throws the fixed `RemoteException`
`AG-1 death injection returned`. Both Parcels are recycled. A transaction failure
still reaches the existing manager logic, which accepts death only with positive
evidence from the captured old Binder. No PID-zero bypass or weaker UID/PID
check is introduced. Single-shot KILL, service termination, manager observation,
and all death assertions remain intact. No AG-1B success is claimed before
the next exact-head CI proves it.

The real fixture remains Experimental only; AG-1B does not establish Protected
eligibility. AG-1 remains IN PROGRESS. Native containment, dynamic executable-code
control, and full framework/Binder mediation remain Unknown. Canonical PR 5's
owner gate remains pending; canonical production PR 6 remains unauthorized.

Local validation passed `:app:lintDebug`, `:app:testDebugUnitTest` (24 tests,
zero failures/errors/skips), `:app:assembleDebug`, and
`:app:assembleDebugAndroidTest`, all 57 existing Python tests, and AG-1B runner
shell syntax. Final review confirmed synchronous flags zero, reply recycling,
normal-return failure, no PID-zero bypass, and unchanged service identity logic.
Linux/KVM remains unavailable on the local Windows host, so no local emulator
run or security-test retry was performed. CI pending after publication.
