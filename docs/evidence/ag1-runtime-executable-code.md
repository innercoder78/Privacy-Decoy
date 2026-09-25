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

Before execution, AG-1A classified the real primary as `EXPERIMENTAL_ELIGIBLE`
because runtime mediation was unproven. This records the static admission result,
not continued eligibility after the subsequently demonstrated runtime bypass.
On the local Windows build,
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

| Test | Required controlled-path result | Evidence |
|---|---|---|
| Exact expected identity | AUTHORIZED, one-use consumption | JVM pass; run #150 device observation confirmed |
| Changed byte | DENIED | JVM pass; run #150 device observation confirmed |
| Unknown identity | DENIED | JVM pass; run #150 device observation confirmed |
| Stale artifact generation | DENIED | JVM pass; run #150 device observation confirmed |
| Stale session generation / wrong session identity | DENIED | JVM pass for both; run #150 confirmed stale session generation |
| Revoked/dead session | DENIED | JVM pass for both; run #150 confirmed revoked session |
| Consumed authorization replay | DENIED | JVM pass, including concurrent consumers; run #150 confirmed sequential replay denial |
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

**Historical local-host context:** at initial publication, the local direct-loader
device result was pending because the Windows Desktop host lacked Linux/KVM.
No local device experiment or assertion retry was performed. Compilation and JVM
results were not device observations. The subsequent exact-head CI device
experiment completed in run #150 and demonstrated the direct-loader bypass
recorded below; AG-1C device evidence is no longer pending for that tested head.

## First exact-head AG-1C device result

[Actions run #150](https://github.com/innercoder78/Privacy-Decoy/actions/runs/36057346901)
(run ID `36057346901`) completed at exact PR head
`55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`. The dedicated
`admission-dynamic-code-feasibility` job exercised API 35 Google APIs x86_64.
All eight AG-1C instrumentation cases completed successfully **as observations**.
The workflow conclusion was SUCCESS: `changes`, `validate`,
`containment-prototype`, `network-feasibility`, `managed-profile-feasibility`,
`admission-feasibility`, `admission-runtime-feasibility`, and
`admission-dynamic-code-feasibility` all succeeded at that head.

The runner reported:

```text
AG1C_BUILD PRIMARY_ANALYZED SECONDARY_SEPARATE CONTENT_IDENTITY_VERIFIED
```

The trusted-path observations were:

| Case | Decision | Stage sequence | Category |
|---|---|---|---|
| CHANGED | DENIED | 0000 | COMPLETE |
| REPLAY | DENIED | 0000 | COMPLETE |
| REVOKED | DENIED | 0000 | COMPLETE |
| STALE_ARTIFACT | DENIED | 0000 | COMPLETE |
| STALE_SESSION | DENIED | 0000 | COMPLETE |
| UNKNOWN | DENIED | 0000 | COMPLETE |
| EXACT | AUTHORIZED | 1111 | COMPLETE |

Stage order is loader construction, class resolution, static initialization,
and entry invocation. Denial rows report per-attempt stage deltas, including
zero additional stages for replay after its earlier authorized invocation.
The exact-authorized path completed all four stages. `COMPLETE` denotes completion
of the observation; it does not turn a denied case into executed code or establish
protection outside the trusted helper.

The separate direct-loader observation was:

```text
AG1C_CASE DIRECT OBSERVED
AG1C_STAGE DIRECT LOADER_CONSTRUCTED=1
AG1C_STAGE DIRECT CLASS_RESOLVED=1
AG1C_STAGE DIRECT STATIC_INITIALIZED=1
AG1C_STAGE DIRECT ENTRY_INVOKED=1
AG1C_CATEGORY DIRECT COMPLETE
AG1C_INTERPRETATION TESTED_DIRECT_PATH_NOT_MEDIATED
AG1C_RESULT OBSERVATION_COMPLETE EXPERIMENTAL_ONLY
```

The admitted synthetic guest directly used Android's standard
`InMemoryDexClassLoader` without passing through `Ag1ExecutableAuthorization`.
Previously unadmitted secondary DEX reached all four stages (`1111`), including
entry invocation. This is positive evidence of a bypass of the executable
authorization boundary for the exact tested direct-loader path, not merely
missing evidence or Unknown coverage for that path. The workflow succeeded
because the experiment completed and classified its observation correctly.
**The runtime-mediation security hypothesis for this path did not succeed.**

## Interpretation

**AG-1C falsified the current runtime-mediation hypothesis for the tested direct
InMemoryDexClassLoader path.** The current runtime did not mediate that path:
unadmitted secondary executable content reached entry without the trusted
executable authorization decision. No incidental exception, invalid-DEX failure,
or Android restriction prevented execution in this observation.

The pre-execution static result and later runtime discovery must remain distinct.
AG-1A's `EXPERIMENTAL_ELIGIBLE` result reflected unproven runtime mediation before
execution. AG-1C then positively demonstrated the tested bypass. The earlier
static classification cannot override this later evidence or authorize continued
execution despite a known-unsafe discovery.

* **PD-REQ-087:** the architecture cannot claim Protected eligibility while this
  demonstrated direct-loader path remains available and unmediated. Experimental
  compatibility cannot count as Protected evidence.
* **PD-REQ-090:** positively established mandatory bypasses/known-unsafe results
  require a hard stop, with no Experimental override. Recording the fixture's
  historical admission class does not weaken that rule or grant a new exception.
* **PD-REQ-091:** newly introduced executable content must be blocked or classified
  before use wherever enforceable, and positive known-unsafe discovery during
  execution requires safe termination or revocation. Here, unadmitted DEX reached
  entry invocation. The current AG-1C runtime experiment does **not** satisfy
  that requirement for this tested path. Normal test teardown is not evidence of
  the required discovery-triggered termination or revocation.
* **PD-REQ-093:** static analysis, compatibility, and absence of other detections
  cannot establish mediation or replace this positive runtime evidence. Other
  loader paths retain Unknown coverage; no universal Java-loader bypass is inferred.

The canonical AG-1 checkpoint asks whether unexpected dynamic executable content
can be blocked or demoted before execution, and whether a controlled Java/Kotlin
fixture attempting unexpected executable loading can be stopped. For this direct
`InMemoryDexClassLoader` path, the current architecture failed that falsification
probe. The trusted helper's correct decisions do not rescue the failed direct-path
hypothesis, and a passing observation workflow does not mean AG-1 has passed.

This PR preserves the result and selects no remediation or interception mechanism.
It does not decide that the project should continue despite the failure. Failure
favors STOP rather than weakening Protected Mode; the canonical Roadmap PR 5
STOP/owner gate remains pending and production PR 6 remains unauthorized.

## Loader inventory

| Java/DEX surface | This slice | Evidence status |
|---|---|---|
| `InMemoryDexClassLoader` | Trusted-helper and direct-fixture paths exercised on the exact-head run #150 device | Direct unadmitted DEX reached construction, resolution, initialization, and entry (`1111`, COMPLETE); bypass demonstrated, current runtime does not mediate this tested direct path |
| `DexClassLoader` | No direct experiment | Unknown |
| `PathClassLoader` | No direct experiment | Unknown |
| Other `BaseDexClassLoader` paths | No separate direct experiment | Unknown; superclass relationships do not confer coverage |
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
`android/gradlew` remains mode `100755`. The initial publication-time note was
"CI pending after publication." Run #150 subsequently completed the exact-head
device experiment described above; that result is not pending. It applies to
the tested implementation head, not an assertion that later revisions have
already completed CI.

## Limitations

* Other Java loader paths remain Unknown unless directly tested.
* Native loading and native containment remain Unknown.
  Arbitrary native containment remains unresolved; ByteHook/ShadowHook are not
  kernel sandboxes.
* Raw syscall containment remains Unknown.
* Full Binder/framework mediation remains Unknown.
* Fixture/service observations are controlled synthetic evidence, not independent
  evidence against arbitrary hostile guest tampering.
* No Protected eligibility is established; Experimental compatibility cannot
  count as Protected coverage. This is not a production privacy result, and no
  broader Protected claim is permitted. No safety score is assigned.
* No external runtime dependency, engine, hook library, privilege requirement,
  APK rewriting, VPN service, or native interception is added.
* AG-1 remains incomplete and in progress. Canonical Roadmap PR 5 STOP/owner gate remains pending.
  Canonical production PR 6 remains unauthorized. This slice authorizes no
  follow-on native experiment.
