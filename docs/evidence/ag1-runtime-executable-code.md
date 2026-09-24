AG-1 IN PROGRESS — AG-1C DYNAMIC EXECUTABLE-CODE COMPATIBILITY SLICE

Supplemental research from main `7b0138af5cfbfed1162cd90b5186dc363903b1a3`.
Only Privacy Decoy synthetic fixtures and fixed test values are used. No real
accounts, private data, network access, device identifiers, filesystem discovery,
native executable experiment, or external runtime engine is introduced.
This slice addresses PD-REQ-085 through PD-REQ-095 without modifying the requirements.

## Fixture and admission identity

The existing AG-1B `debug` fixture, runner, instrumentation assertions, and prior
evidence are preserved. Its runner deliberately rejects dynamic-loader references.
Therefore AG-1C adds a separate `dynamic` build variant of that same source module:
the original provider, Application, and entry fixtures plus `DirectDexProbe` in
`src/dynamic`. The direct probe is absent from the AG-1B debug artifact. The AG-1C
variant is built normally, analyzed by the unchanged AG-1A analyzer, and bound to
its own exact artifact hash and admission generation. There is no APK rewriting
or re-signing. Neither variant is installed as an ordinary app.

`ag1-secondary-dex-fixture` is a separate source-only Android build module with
one source class, `SecondaryDexPayload`. Its static initializer sets the fixed
value 73; its entry method returns 91. The generated APK is only a build envelope
for `classes.dex`, never an installed application. No primary module depends on
the secondary module. Only instrumentation assets contain its extracted DEX.
Generated APK/DEX/JAR/class files are build outputs and are not committed.

The runner verifies the source primary APK, generated asset, and embedded test
asset against the analyzer's exact SHA-256. It parses DEX class definitions to
prove that the secondary class is defined in the secondary executable and absent
from the primary executable; merely finding the class-name string would be wrong
because the primary intentionally references that name. It also rejects embedding
the secondary DEX as a primary ZIP entry, verifies the secondary instrumentation
asset against its separately built executable, and verifies that changing a byte
changes its digest. Device instrumentation independently checks both content
digests and the primary admission generation before binding.

The real primary remains `EXPERIMENTAL_ELIGIBLE`. On this local Windows build,
the unchanged analyzer established package/version/signing metadata and reported
`DYNAMIC_DEX_LOADER_REFERENCE`, `IN_MEMORY_DEX_LOADER_REFERENCE`, and
`RUNTIME_MEDIATION_UNPROVEN`. Static references do not establish mediation.

Local synthetic identities (debug signing/build environment specific):

| Artifact | Identity |
|---|---|
| Primary APK SHA-256 | `047ec6dceeca59e880325e603e24e26842a53b703c337fc6bde98a1ca69bc409` |
| Primary admitted generation | `b39eedae567c93d24df1e549c2779b8fbd2d3693778131938a60b87f23f2a0f5` |
| Secondary DEX SHA-256 | `5ebff5f4c8428ab5747003b34bbf9786e82e27c6e89f6597aa4c00c0a638d4fb` |

## Trusted path

`Ag1ExecutableAuthorization` is a pure Java, manager-owned research model. It
binds one expected content digest to one admission/session policy object. A
decision requires the exact admitted generation, session identifier and epoch,
actual bytes' SHA-256, DEX kind, Experimental execution class, active consent,
unrevoked live session, and unused authorization. A grant consumes the authority
atomically; another session cannot borrow that object. Filename is never an
authorization input. No Protected policy is modeled as device eligibility.

The manager snapshots the supplied bytes and hashes that snapshot. Rejections
return before shared-memory transfer or secondary loader construction. The
accepted snapshot is transferred read-only, checked again in the isolated
process, and loaded by the trusted helper. The primary first completes the same
AG-1B bind, READY, bounded transfer, and fixed pre-code event sequence.

| Test | Required controlled-path result | Local evidence |
|---|---|---|
| Exact expected identity | AUTHORIZED, one-use consumption | JVM pass; device pending |
| Changed byte | DENIED | JVM pass; device pending |
| Unknown identity | DENIED | JVM pass; device pending |
| Stale artifact generation | DENIED | JVM pass; device pending |
| Stale session generation / wrong session identity | DENIED | JVM pass; device pending |
| Revoked/dead session | DENIED | JVM pass; device pending |
| Consumed authorization replay | DENIED | JVM pass, including concurrent consumers; device pending |
| Wrong kind/execution class or borrowed authority | DENIED | JVM pass |

Device negatives independently compare isolated-process secondary attempt
counters and stage observations before and after denial. Fresh negatives require
zero prior secondary stages; replay requires unchanged counters after its one
accepted invocation. Evidence for denials reports **per-attempt stage deltas**,
so replay's zero does not erase the first accepted execution. No denied attempt
may construct a loader, resolve or initialize the class, or invoke its entry.
The exact trusted-path case also requires the generated payload to complete all
four stages, providing a validity control separate from the direct observation.

This model governs only callers that explicitly use this helper. It is not an
Android loader interceptor. Request-time revocation checks do not establish
termination of already executing code or arbitrary concurrent hostile behavior.

## Direct Android loader experiment

After the ordinary AG-1B READY and primary execution sequence, instrumentation
supplies the separately generated secondary DEX through read-only shared memory.
The isolated process calls the admitted variant's `DirectDexProbe.observe`.
That fixture directly constructs `InMemoryDexClassLoader`, resolves the secondary
class without initialization, reads its non-constant initialized field, and
invokes its fixed entry method. Its parent loader excludes manager classes.
This route never calls `Ag1ExecutableAuthorization` or consumes a secondary grant.
The descriptor and digest checks establish transport integrity, not executable
authorization on this route.

Fixed observations are `LOADER_CONSTRUCTED`, `CLASS_RESOLVED`,
`STATIC_INITIALIZED`, and `ENTRY_INVOKED`. Initialization is observed by reading
the known initialized field; entry completion when its invocation returns. Fixed
values are checked separately, so a value mismatch does not erase a completed
stage. Milestones are recorded only after each operation completes. Failure categories
are `SECURITY_EXCEPTION`, `CLASS_NOT_FOUND`, `LINKAGE_ERROR`,
`REFLECTION_OR_RUNTIME_EXCEPTION`, or `FIXED_VALUE_MISMATCH`; no arbitrary
exception message or stack trace is published. A missing fixture method or IPC
failure fails the harness rather than pretending to be a loader denial.

**Local direct-loader result: pending.** The local Windows Desktop host does not
provide the required Linux/KVM execution environment. No local device experiment
or assertion retry was performed. Compilation and JVM results are not device
observations. The dedicated API 35 Google APIs x86_64 CI run must provide them.

## Interpretation

The direct test's success condition is a complete, structurally consistent
observation, independent of whether Android executes the payload. If all four
stages occur, the runner prints `TESTED_DIRECT_PATH_NOT_MEDIATED`: the current
controlled runtime does not establish mediation of that tested direct-loader
path. The result must remain available to the architecture review, without
adding another interception mechanism to make the experiment succeed.

If execution does not complete, the stage prefix and bounded category are
reported. A platform exception category alone is **not** a demonstrated Android
restriction, deliberate PD decision, or mediation. The runner explicitly emits
`DIRECT_FAILURE_REQUIRES_CAUSE_REVIEW_NOT_MEDIATION_EVIDENCE`. The trusted-path
validity control helps distinguish invalid payload/configuration from direct
loader behavior, but any remaining cause must be investigated before a stronger
claim. No non-execution reason or successful protection result is asserted here.

## Loader inventory

| Java/DEX surface | This slice | Evidence status |
|---|---|---|
| `InMemoryDexClassLoader` | Explicit trusted-helper and direct-fixture experiments | Device pending; no mediation established |
| `DexClassLoader` | No direct experiment | Unknown |
| `PathClassLoader` | No direct experiment | Unknown |
| `BaseDexClassLoader` | No separate direct experiment | Unknown; superclass relationships do not confer coverage |
| `DexFile` | No direct experiment | Unknown |

No class-loader family-wide or complete Java/DEX coverage is inferred.

## Reproduction and CI

Run `bash android/tools/run-ag1-dynamic-code-emulator.sh` on Linux with KVM,
the pinned SDK/toolchain, and API 35 Google APIs x86_64 image. It builds/analyzes
the primary, retains its exact generation, builds the secondary independently,
verifies separation, and builds manager/test APKs. One disposable emulator
installs only those two PD packages; neither primary nor secondary is installed.
The runner invokes eight AG-1C tests once and terminates the emulator on exit.
Temporary evidence, AVD state, and generated APK/DEX/JAR/class outputs in the
involved module build directories are cleaned. No raw log artifact is uploaded.

The parser requires the exact ordered eight starts/passes, one bounded observation
per test, exact totals, successful command exit and instrumentation completion.
Missing, duplicate, skipped, malformed, unexpected, or failed results fail the
job. Previously validated observations can still print as explicitly partial
evidence when later instrumentation fails; that never produces a passing result.

`ag1dynamic` selects `admission-dynamic-code-feasibility`. AG-1C-only sources
select baseline, admission, and AG-1C without network, managed-profile, or
containment suites. Shared AG-1 runtime paths select both runtime suites;
analyzer/primary-fixture changes additionally select admission. Unknown relevant
paths stay fail-safe. Docs-only changes remain lightweight.

Local validation on 2026-09-24:

* App lint, JVM tests, debug and instrumentation APK assembly passed using the
  existing JDK 17.0.20.1, Gradle 9.6.0, SDK 37.0, and Build Tools 36.0.0.
* All 31 app JVM tests passed with zero failures/errors/skips: 24 existing tests
  plus seven new authorization-model tests. The model tests use pure JDK logic.
* Both primary variants passed lint/assembly; secondary lint/assembly passed.
  Fixture unit-test tasks report `NO-SOURCE`; device behavior belongs to the
  instrumentation suite. No lint baseline or weakened assertion was added.
* Existing Python suites passed: admission analyzer 13, network evidence 19,
  managed-profile evidence 25. Five new parser tests cover valid execution,
  bounded direct failure categories, malformed/missing/duplicate/skipped/unsafe
  input, timeout, and preserving partial observations without success.
* Shell syntax checks passed for AG-1A, AG-1B, AG-1C, and the CI classifier.
  Both embedded Python snippets executed against the real generated artifacts,
  including admission identity, class-definition separation, asset equality,
  and changed-byte digest checks. Eleven representative CI path selections passed.

Wrapper Git blob remains `b1b8ef56b44f16b14dc800fa8103a6d89abb526f`, SHA-256
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`;
`android/gradlew` remains mode `100755`. CI pending after publication.

## Limitations

* Other Java loader paths remain Unknown unless directly tested.
* Native loading and native containment remain Unknown.
* Raw syscall containment remains Unknown.
* Full Binder/framework mediation remains Unknown.
* Fixture/service observations are controlled synthetic evidence, not independent
  evidence against arbitrary hostile guest tampering.
* No Protected eligibility is established; Experimental compatibility cannot
  count as Protected coverage. No safety score is assigned.
* No external runtime dependency, engine, hook library, privilege requirement,
  APK rewriting, VPN service, or native interception is added.
* AG-1 remains in progress. Canonical Roadmap PR 5 owner gate remains pending.
  Canonical production PR 6 remains unauthorized. This slice authorizes no
  follow-on native experiment.
