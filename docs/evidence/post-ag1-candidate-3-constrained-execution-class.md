# Post-AG-1 Candidate 3 — technically constrained execution classes

**Research/access date:** 2026-10-01. **Candidate:** 3 of five under
[ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md).
**Disposition:** **UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY**.

## 1. Question, answer and scope

Can Privacy Decoy define a useful narrower Protected execution class whose
exclusions can be established before launch and technically prevented after
launch on ordinary non-rooted Android?

**Inference (I): not established.** Restricting the admitted APK inventory does
not restrict the authority of the ART process that subsequently executes it.
Java/Kotlin-only packaging, no detected native libraries, no loader references,
one declared process, and no detected WebView/SDK use do not establish durable
runtime exclusions. The tested AG-1C direct loader remains a positive bypass.
No proposed class is Protected-eligible and none qualifies for prototype review.

**Design hypothesis (H):** a fixed-artifact, single-process, foreground utility
class could retain useful Activities, resources and local state while excluding
app-controlled native loading, subsequent executable introduction, WebView,
external SDK services and background dispatch. Examples are a local calculator,
unit converter or form-based reference tool, not an assertion that any existing
APK qualifies. This is an application-semantics hypothesis, not approved product
scope. Enforcing its exclusions and safely retaining its framework, storage and
Binder operations requires an independent authority that has not been found.

**Unknown:** whether a permitted lower execution/capability boundary can make
that combination useful and mandatory. This is the reason for the candidate's
Unknown disposition. The scan-only and ordinary-ART variants are insufficient;
their failure is not softened to Unknown. Conversely, rejecting every possible
restricted execution model would go beyond this evidence. Candidate 4 remains
unperformed, and even a future syscall restriction would not automatically
establish before-use DEX admission or remove genuine values already in memory.

**Repository/source observation (R):** fetched main was verified as
`ca5899a9d8bc338bf9a8c4e9abbb1bb6998d6cff`, tree
`b7fa197fb2033c5db636e23123572ab53d300662`. The working tree was clean; eight
existing stashes were preserved. The requested new branch is
`research/post-ag1-c3-constrained-execution-class`. An initial sandbox-denied
fetch was retried successfully before comparing the fetched revision; stale
FETCH_HEAD was not accepted as a baseline.

This record changes documentation only. No prototype, Android/runtime/test/
workflow/dependency code, requirement, binary, engine integration, experiment,
build or device run is included. Candidates 4–5 remain pending. No overall
redesign exit outcome is selected. Canonical production Roadmap PR 6 remains
unstarted and unauthorized. No ordinary apps, real accounts or private data were
used. PD-REQ-001..095 remain unchanged; no requirement is marked satisfied.

### Evidence vocabulary

* **V — verified Android/platform fact:** current official documentation within
  its API/target scope; not a new observation on a device.
* **R — repository/source observation:** preserved PD evidence or exact AOSP
  implementation inspected here; source structure is not an OEM runtime result.
* **U — upstream claim:** reference-project descriptions, not PD security evidence.
* **I — inference:** a consequence of cited facts, with scope stated.
* **H — design hypothesis:** an unimplemented class, rule or proof obligation.
* **Unknown:** absent or insufficient evidence; never a pass.

These labels apply throughout the matrices. D1–D16 and A1–A10 resolve in the
source ledger. Current documentation was accessed on the date above; exact AOSP
source uses `android-17.0.0_r1`. Historical API 35 execution is separately scoped.
No generic Linux feature is treated as available to an Android application.

## 2. Governing evidence and prior candidates

**R:** the [charter](../post-ag1-enforcement-boundary-redesign.md),
[failed architecture handoff](../architecture-admission-gated-runtime.md),
[requirements](../requirements.md), [threat model](../threat-model.md),
[acceptance criteria](../acceptance-criteria-1.0.md), and
[platform matrix](../platform-support.md) retain their obligations. The threat
includes hostile code, reflection, native/JNI, dynamic code, helpers, stale
handles and early entry. An app signature or language label does not make it trusted.

**R:** [Candidate 1](post-ag1-candidate-1-os-process-compartment.md) remains
**REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY**. Its isolated UID and
selected kernel denials are real, but genuine Build/property access, permitted
system surfaces and unadmitted DEX remain. Removing native libraries or declaring
one process does not remove those Java-accessible authorities. The isolated
service is not promoted to a sufficient boundary for a smaller class.

**R:** [Candidate 2](post-ag1-candidate-2-controlled-runtime-lower-boundary.md)
remains **UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED**. Component, resource and
package models may inform a future semantics layer; its missing lower owner and
unsafe genuine-service adapters remain unresolved. Candidate 3 asks whether
scope restriction itself removes that dependency. **I: it does not in the
reviewed ART/Android execution model.**

**R:** [AG-1A](ag1-admission-analysis.md) establishes bounded identity and positive
inventory, not absence of dynamic behavior, complete split closure or production
archive hardening. [AG-1C](ag1-runtime-executable-code.md) and the
[AG-1 closeout](ag1-feasibility-closeout.md) establish the direct-loader failure;
AG-1 remains **FAILED**. Historical slice IN PROGRESS statements, helper positives,
READY ordering, S1 FALSIFIED/follow-up BLOCKED, source-provenance dispositions,
VirtualSpace disqualification, and network Known Gaps are preserved exactly.
This record does not revise any historical file or result.

## 3. What a class-membership claim would require

**H:** membership must bind the exact base/split/executable set, transitive
runtime dependencies, actual API/OEM/ABI/ART configuration and admission
generation. Before any guest class initialization or component constructor,
PD must establish both an admission predicate and an immutable runtime policy
that makes excluded transitions impossible or denies them before use.

**I:** a scanner can reject a positively detected feature conservatively, but
absence of `.so`, loader names, reflection strings, WebView declarations or
subprocess references cannot establish the behavioral predicate. WebView use
does not require a guest WebView component declaration. SDKs can supply calls;
strings/bytes may be computed or decrypted. A no-network rule does not forbid
generating executable bytes. A hash fixes the admitted bytes, not every future
behavior those bytes can compute.

**H/Unknown:** a sound closed-world verifier could be a materially different
admission mechanism, but it would need to establish all reachable instructions,
dispatch targets, reflection, callbacks, native transitions and library effects,
and prevent introduction of unverified code after checking. No such verifier,
complete trusted-library model or useful accepted Android app set is established
here. This is not a claim that static verification is impossible; string scanning
and ordinary DEX type verification are not that proof. Routine rewriting or
re-signing cannot be assumed as its implementation.

**V/R/I:** Android does not provide a usable Java `SecurityManager` sandbox for
untrusted code inside one VM [D6]. The inspected `Runtime.load0` has the Java
security-manager check commented out and proceeds toward native loading [A6].
A filtering parent loader or a helper controls its own entry path; it does not
make PD the owner of all platform loader construction, framework calls or
already available objects. Non-SDK restrictions constrain particular interfaces
by platform/target rules [D15]; they are not a PD-configurable public-API denylist.
Reflection into public loader methods need not evade a hidden-API restriction.

## 4. Proposed classes and both membership questions

Each row is an H class definition, evaluated using R/V evidence and I reasoning.
The labels below are research outcomes, not PD-REQ-020 capability classifications.
None of the exclusions is an approved reduction in product scope.

| Class | Useful behavior intended to remain | Reliable pre-code identification? | Technically prevented after launch? | Result |
|---|---|---|---|---|
| C3-A: Java/Kotlin packaging, no packaged app `.so` | Conventional managed UI and platform services | Bounded ZIP/ELF/DEX inventory is possible; language provenance and future behavior are not proven by absence | No; platform native implementation, public loaders and service authority remain [A1–A8] | Insufficient as a Protected rule; full behavioral membership Unknown |
| C3-B: no app-controlled native machine code | Managed Android app using a reviewed platform-native allowlist | Requires transitive native-entry and SDK inventory, not just archive inspection; complete closure Unknown | No mandatory prevention of all later app-native introduction established; platform JNI already executes | Insufficient on ordinary ART; lower authority Unknown |
| C3-C: fixed admitted executable set, no dynamic code or guest-created/custom loaders | Packaged multidex/base/splits and normal components | Exact supplied bytes can be identified; proof of no later loader behavior Unknown | AG-1C bypasses the helper; readonly files and parent-loader filtering do not repair it | Scan/helper version contradicted by known direct path; replacement gate Unknown |
| C3-D: one process, no WebView/separately executing SDKs or native children | Foreground managed UI without browser, push or helper dependencies | Effective manifest processes can be inventoried; absence of future undeclared helpers cannot be established | Manifest selection is not no-exec policy; WebView disable is process-local and partial [D8–D10; A9] | Useful narrowing, but not complete prevention; Unknown |
| C3-E: intersection B+C+D, fixed artifacts, foreground only, bounded storage/UI brokers | Local calculator/converter/reference/form utility with real Activities, resources and state | Must prove all preceding exclusions plus adapter closure before first code; Unknown | Would require mandatory executable and capability control independent of guest; none identified | Conceptually useful H; enforcement depends on unestablished lower authority |
| C3-F: verified computation subset with copied input/output only | A transformation/calculation task hosted by PD | Sound verifier and closed imports could define membership in principle; none evidenced | A separate interpreter/capability machine might enforce it; no implementation or Android API availability established | H/Unknown; a DEX method invocation alone is not Android application support |

**I:** “no custom class loaders” must mean no guest-created loader or post-admission
executable extension. Forbidding every `BaseDexClassLoader` would also forbid
ordinary Android startup class loading and the referenced semantics runtimes.
Allowing a startup loader requires proving its complete immutable input set and
preventing later extensions; naming one approved loader instance is insufficient.

## 5. Admission observability matrices

Runtime detection below means a possible observation surface, not a guaranteed
complete interceptor. No newly implemented detection is claimed. In scope cells,
**M** means the repository's API 31–37 investigation range, physical ARM64 primary
and x86_64 emulator engineering only; OEM/release-equivalent coverage is Unknown.
**S37** means only the exact Android 17 AOSP source inspected here. No row's
Unknown is converted to Unsupported or N/A merely because admission forbids it.

### Native execution exclusions

| Prohibited behavior | Import-time evidence available | Runtime detection available | Runtime prevention mechanism | Authority owner | Bypass/manufacture path | False-negative risk | API/OEM/ABI scope | Resulting classification |
|---|---|---|---|---|---|---|---|---|
| Packaged app `.so` | ZIP entries, ELF headers, ABI and native declarations; AG-1A bounded inventory R | Loaded-library observations could corroborate presence; complete coverage Unknown | PD may reject detected artifacts before launch; no lasting runtime no-native rule | PD importer for rejection; ART/linker/OS for execution | ELF in assets, later split, generated or opaque payload | High for behavior inferred from filename absence | M; ELF/linker ABI-specific | Presence observable; durable exclusion Unknown |
| Native implementation reached through platform/SDK Java | DEX/native-method and dependency inventory, incomplete platform closure | JNI/path instrumentation possible on selected routes; complete closure Unknown | No blanket ban compatible with normal platform execution established [A8] | Android/runtime and SDK implementations | Java file/network/framework calls enter existing native code | High if managed source is treated as non-native execution | S37 implementation; M effects Unknown | Literal zero-native class contradicted; bounded allowlist Unknown |
| `System.load` / `System.loadLibrary` | Direct calls and strings can be found | Selected Java/native-load hooks; all-route observation Unknown | Linker/path/OS checks; D7 readonly-file rule for `System.load`; no PD content predicate [A6–A7] | ART/linker/Android | Computed path, public reflection, SDK call, newly loaded DEX | High for string/call scan | Target 37 rule; M path/ABI/OEM behavior Unknown | No complete app-native exclusion established |
| `dlopen` / equivalent native loading | ELF imports may show use; no import need be present initially | Linker/ShadowHook-style observations are partial references | Namespace, DAC/MAC and linker validation constrain particular loads; no PD admission owner | Android linker/kernel | Native code after another entry, existing SDK/native bridge; not necessarily Java load helper | High for import-only coverage | S37 A7; M actual library reachability Unknown | Unknown; not an observed new native bypass |
| Generated/downloaded native binary | Embedded ELF/opaque indicators only; future bytes unavailable | File/mapping observation may miss memory-only paths | Android 10 home-directory exec restrictions are real [D5]; not a blanket code-origin ban | Android kernel/linker; PD lower owner Unknown | New content satisfying allowed path/ABI conditions; generation needs no network | High; exact successful route Unknown | Target 29+ rule, M policy/ABI varies | Some paths restricted; universal exclusion Unknown |
| Executable memory / generated machine instructions | JIT/native references are clues, not closure | Mapping changes may be observed; race-free attribution Unknown | S37 app policy permits execmem [A10]; no PD selective denial established | Android kernel/runtime | App-native or SDK JIT path after initial native entry; ART itself uses machine execution | High if no packaged ELF is equated with no executable memory | S37 allow rule, not compiled OEM policy proof | No blanket prevention; selective enforcement Unknown |
| Native-created threads | Native imports and SDK inventory, incomplete | Thread snapshots are after creation; all-entry detection Unknown | No evidenced guest-specific no-thread boundary; Android runtime also has threads | Android/runtime; PD owner Unknown | Existing JNI/SDK or later native code; Java Thread also has native implementation | High for package-based inference | M; JNI/ABI/ART details Unknown | Blanket zero-native-thread class unusable; app-created exclusion Unknown |
| Direct syscalls outside mediation | Native disassembly/imports cannot prove future absence | Selected tracing/hooks do not cover all instructions | Android baseline kernel denials remain; no stronger PD control established | Kernel/Android; possible lower owner Unknown | Machine code skips Java/libc/PLT wrappers, still subject to kernel checks | High; no syscall experiment performed | M/ABI/OEM; Candidate 4 unperformed | Unknown; hooks are not confinement |

### Java/DEX execution exclusions

| Prohibited behavior | Import-time evidence available | Runtime detection available | Runtime prevention mechanism | Authority owner | Bypass/manufacture path | False-negative risk | API/OEM/ABI scope | Resulting classification |
|---|---|---|---|---|---|---|---|---|
| `InMemoryDexClassLoader` | DEX references/strings; future buffers not proven absent | AG-1C milestones observe the direct route; helper does not intercept it | No PD mandatory gate at construction/resolution/init/entry [A1–A5] | ART; PD helper optional | Received/generated DEX buffer and direct platform constructor | High for negative scan | Public API 26+, M; observed API 35 x86_64 debug only | Known historical bypass, not Unknown; replacement prevention Unknown |
| `DexClassLoader` | Call/DEX/JAR/APK paths may be found | Selected loader/file observations; complete detection Unknown | Target-34 readonly-file enforcement [D4], not PD admission | ART/Android | Accessible valid file loaded without PD helper, including SDK invocation | High for computed/reflected paths | API 3+; M actual outcomes Unknown | Untested route Unknown, no inherited AG-1C coverage |
| `PathClassLoader` / other `BaseDexClassLoader` paths | Startup graph/base/splits and direct calls | Initial-loader inventory; complete mutation monitoring Unknown | Initial snapshot binding helps only that loader's inputs; no all-loader gate | ART and startup runtime | New instance, additional permitted code paths, shared-library/SDK loaders | High if startup graph assumed complete forever | D3, S37 A2–A4; M Unknown | Unknown; startup use is not exclusive authority |
| `DexFile` direct use | Class/method references and executable entries | Selected native-opening observations possible | DEX verification/open checks; no PD classification callback [A4–A5] | ART | Lower-level opening/class definition where accessible | High if only constructor names scanned | Deprecated API 26, removal not implied [D16]; M Unknown | Unknown; no direct experiment |
| Generated/downloaded DEX/JAR/APK | Embedded content/known endpoint or generator clues | Byte transfer/file observation not automatically executable classification | No before-use all-origin decision established | PD decision logic; ART actually executes | Bytes computed/decrypted/received then memory or file loader | High; no network permission is not no generation | M; API 35 receipt path observed | Known AG-1C received-DEX path; others Unknown |
| Reflection into loader APIs | Literal names may be found; computed names not closed | Instrumented reflective calls can log selected use | Non-SDK lists restrict particular private interfaces, not public loaders [D15] | ART/platform; no PD reflection policy | Resolve public constructor/method dynamically | High for absent strings | M; hidden API scope depends on target/platform | Unknown prevention; no universal reflection bypass claimed |
| SDK-supplied loader | SDK signatures/dependencies and class graph | Selected SDK/loader callbacks; complete visibility Unknown | No blanket SDK provenance-to-execution deny rule | SDK calls platform loader; Android decides | Transitive bundled or delivered SDK creates loader | High for renamed/obfuscated/transitive SDKs | M plus exact SDK version | Unknown; direct-framework evidence not inherited |
| Encrypted/packed code revealed after launch | Packer/opaque indicators can cause refusal | Plaintext may appear only in runtime memory | Conservative prelaunch Unknown blocks Protected; no decrypt-to-execute gate established | PD admission for refusal; ART/runtime for use | Data becomes DEX/native code after arbitrary computation | High for absent known packer signature | M, format/SDK-specific | Unknown and launch-blocking; no safety score |

### Process and authority-bearing subsystem exclusions

| Prohibited behavior | Import-time evidence available | Runtime detection available | Runtime prevention mechanism | Authority owner | Bypass/manufacture path | False-negative risk | API/OEM/ABI scope | Resulting classification |
|---|---|---|---|---|---|---|---|---|
| Secondary manifest processes | Effective application/component process attributes over supplied splits [D8] | Supervisor component/PID inventory, bounded | Reject declarations or decline PD dispatch; not a general process-creation deny | PD admission, Android component manager | Later split or paths unrelated to guest manifest | Low for parsed supplied declarations; high for behavioral inference | M; complete split closure Unknown | Bounded declaration exclusion possible; no-expansion Unknown |
| Subprocess/exec | `Runtime.exec`, `ProcessBuilder`, executable references | Selected spawn observations; polling is late | OS path/credential restrictions, not blanket no-subprocess [A6; D5] | Android/kernel | Computed commands or SDK invokes platform process API | High if no process declaration used as evidence | M; Candidate 1 preserves API 35 fixed-child observation | Universal no-child claim contradicted by prior scope; class control Unknown |
| Native-created processes | ELF imports/static calls, incomplete | Process/UID observation; atomic pre-entry detection Unknown | Baseline Android restrictions; no additional Candidate 3 policy | Android/kernel | Native creation bypasses PD component allocator; inherits relevant authority | High for native opacity | M/ABI/OEM inheritance Unknown | Unknown; Candidate 4 availability not assumed |
| WebView renderer/helper processes | WebView/SDK/resources references; no required guest component declaration | Renderer callbacks and provider inventory, partial [D10] | Pre-entry `disableWebView()` rejects ordinary initialization in that process [D9; A9]; not universal subsystem confinement | Framework process-local state; external processes Android-owned | Other process/deputy/embedded renderer; hostile tamper resistance Unknown | High for scan-only; disable state is narrower positive evidence | API 28+; M provider/OEM/native scope Unknown | Partial platform prevention credited; full exclusion Unknown |
| SDK/background helpers | Manifest components, SDK graph and known bindings | Service/PID/job inventory, no complete graph established | No general “no helpers” policy; PD can reject known unsupported dependencies | Android/services/SDK plus PD dispatcher | Runtime binding, callback, native helper or remote deputy | High for transitive/remote behavior | M plus SDK/provider version | Unknown |
| GMS/Firebase/other external SDK authority | Package/API/dependency and initializer clues | Selected Binder/service observations; complete closure Unknown | Reject known dependency or deny PD broker request; genuine alternate paths remain | Host services/SDK and Android permissions | SDK/renamed code calls service via retained or acquired handle | High; absence of vendor names not proof | M, exact installed service/version/permissions | Unknown; no claim all SDKs require exclusion |
| Providers/initializers | Manifest providers and startup metadata; code dependencies [D11] | Startup ordering trace only if observed before code | Refuse unsupported startup; do not dispatch guest provider before gate | PD bootstrap plus Android | Constructor/attach/static init or manual SDK initialization | High if only Application.onCreate considered | M, library-specific | Explicit unsupported dependency may be rejected; all-entry control Unknown |
| Jobs/alarms | Declared services/receivers and references, incomplete | Scheduler/broker records, callbacks | PD can omit its own scheduling adapter; no all-path mandatory deny established | Android schedulers and PD brokers | Direct service call, stale PendingIntent, SDK/background delivery | High for runtime scheduling | M/API/target/OEM lifecycle-specific | Unknown, not Unsupported merely by policy |
| Background services | Service/process declarations and startup references | Component/supervisor records, partial | Platform background limits constrain availability; no PD policy substitute | Android plus hypothetical PD dispatcher | Bound service, permitted callback or restart outside selected entry | High for treating foreground-only UI as enforcement | M; limits vary | Unknown |
| Package-installed splits | Supplied base/split hashes, manifest/signing metadata | Package/update notification and inventory; before-use coverage Unknown | PD generation invalidation can refuse its own launch; Android installation is not PD admission | Package installer/Android; PD importer | Legitimate new signed split enters a different executable set | High for assuming supplied set is forever complete | M; installed host and imported guest distinguished | Unknown complete gate; artifact change requires new admission |
| Runtime-delivered modules | SDK/delivery references, existing module inventory | Delivery callbacks are partial [D12] | No all-loader pre-use stop established; downloaded data can also be code | Delivery service/SDK/ART; PD owner missing | On-demand/deferred module then code/resource loading | High for delayed/opaque content | M plus delivery environment/version | Unknown; no assumption Play delivery works in imported guest |

**I:** rows with enforceable import refusal do not answer runtime prevention for
accepted inputs. No row asserts a successful native attack from arbitrary Java;
native loading needs valid content, ABI, entry linkage and permitted mappings.
Those conditions are materially different from a demonstrated universal denial.

## 6. AG-1C authority trace

**R:** run #150 at head `55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`, used API 35 Google APIs x86_64 debug.
The fixture was admitted experimentally before the later positive discovery;
there was no real Protected-eligible device fixture. The proposed constrained
class below is hypothetical and does not relabel that historical classification.

| Step | Path and evidence | Mechanism preventing execution before this step |
|---|---|---|
| 1. Admitted constrained-class app starts | H: exact artifacts accepted and READY reached; R: AG-1B/C bounded initial ordering | H: manager can refuse initial dispatch; all-exclusion prerequisites are Unknown, so Protected launch must currently be refused |
| 2. It manufactures or receives unadmitted DEX bytes | R: historical fixture received separate DEX through read-only shared memory; I: local computation can manufacture bytes without downloading | No evidenced blanket prevention of ordinary byte creation/receipt; transport integrity is not admission |
| 3. It calls `InMemoryDexClassLoader` | R: `LOADER_CONSTRUCTED=1`; V: public buffer constructor [D1]; R: A1–A5 delegation | None in the tested helper architecture; proposed no-loader declaration supplies no mandatory gate |
| 4. Class resolution occurs | R: `CLASS_RESOLVED=1`; A2–A5 class lookup/definition | No PD authorization on the direct route; DEX validity and resolution are not PD content classification |
| 5. Static initialization occurs | R: `STATIC_INITIALIZED=1`, separately observed initialized field | None; observing initialization afterward cannot count as pre-execution protection |
| 6. Entry executes | R: `ENTRY_INVOKED=1`, fixed entry result | None; the before-use decision has already been bypassed |

**R/I:** source path at S37 is `InMemoryDexClassLoader` →
`BaseDexClassLoader(ByteBuffer[], ...)` → `DexPathList.initByteBufferDexPath` →
`DexFile.openInMemoryDexFiles` → ART `DexFile_openInMemoryDexFilesNative`.
Lookup reaches class definition through the same loader/DexFile machinery
[A1–A5]. No PD callback is in these inspected paths. Source review is not a new
API 37 device result; the historical `1111` bypass remains positive evidence
only for its exact tested route.

**R/I:** A5 initially allocates DEX data as readable/writable memory and passes
it to ART's DEX-opening machinery. Banning new executable native pages cannot
by itself be credited as banning DEX execution: existing ART interpreters can
consume bytecode as data. The exact execution mode and a future restriction's
effect remain Unknown. A loader/content authority must act before use, and
must bind immutable bytes rather than a filename or a post-execution notification.

**I:** the new code does not gain a new UID merely by loading. It retains the
process's permitted framework/Binder/files/mappings and existing handles.
Confining some effects does not retroactively admit the executable content under
PD-REQ-091. Static “no dynamic loading detected” cannot establish membership.
The historical direct route requires the PD-REQ-090 hard stop without Experimental
override; other loaders and untested native paths remain Unknown, not universally
known unsafe.

## 7. Native exclusion trace

What technically prevents hostile Java code from causing native machine code to
execute after admission? **I: no such blanket prevention is established, and
literal zero native execution conflicts with ordinary Android runtime use.**
Distinguish running trusted native implementation from executing arbitrary
app-controlled machine instructions; the former does not prove the latter.

1. **R:** packaged-native detection finds supplied ELF/native entries, not every
   future executable [AG-1A]. A missing `.so` is only an inventory observation.
2. **R/V:** platform code already contains native entry points: the inspected
   libcore `Linux` implementation declares native file, mapping, socket and exec
   methods [A8]. These are implementation evidence, not a recommendation to use
   hidden APIs. Public Java operations can depend on native runtime/library
   implementations; Android's kernel sandbox applies to both [D13]. SDK calls
   may add their own native dependencies. Native effects do not start only at
   an app's `System.load` call.
3. **V/R:** `System.load`/`loadLibrary` are documented entry routes [D6]. Runtime
   resolves classloader/path context and enters native loading [A6]; ART's
   `LoadNativeLibrary` invokes `OpenNativeLibrary` and, when supplied, `JNI_OnLoad`
   before returning [A7]. A post-return check is too late for initialization.
   ELF constructors and transitive libraries also need pre-entry coverage;
   complete constructor/linker closure is Unknown here.
4. **V/R/I:** Android 10 prevents the documented app-home `execve` case [D5];
   Android 17 target-37 `System.load` readonly-file requirements [D7] and linker
   namespace/path checks constrain specific introductions. A6 also contains
   runtime feature/compatibility checks. None compares content against PD's
   admitted generation. A readonly file need not be admitted, and a failed
   particular path cannot establish absence of every path.
5. **R/I:** app-policy execmem permission exists at S37 [A10]. That does not prove
   arbitrary Java can directly place and call machine instructions; it does
   disprove treating this baseline policy as a blanket no-executable-memory
   rule. Generated/downloaded native content still needs a reachable load or
   execution route. All-ABI/OEM prevention remains Unknown.
6. **R/I:** already-loaded native code does not need a new load decision to be
   invoked again. ART keeps library/classloader associations and checks prior
   load results [A7]. Native symbols are not universally callable from arbitrary
   Java: method registration, classloader association, visibility and platform
   restrictions still matter. Public reflection can reach accessible Java native
   entry routes; JNI/private reflection limits are not assumed bypassed [D15].
7. **I/Unknown:** app-controlled native instructions, if introduced, can create
   threads, request permitted process creation or issue syscalls without PLT/
   Java wrappers. They remain subject to Android kernel denials. No new native
   bypass was run here, and no raw-syscall confinement is claimed. Any necessary
   extra syscall/Binder authority is an Unknown Candidate 4 dependency.

**H:** a defensible “no app-controlled native code” class would need a complete
allowlist of permitted platform/SDK native effects, immutable executable origins,
unavoidable pre-initialization decisions, and protection of the policy from guest
tampering. Blocking every JNI/native effect would also remove essential Android
semantics. No concrete mechanism meeting that selective contract is established.

## 8. Useful Android-app semantics after narrowing

The table covers every plausible class, using grouped columns only where their
intended semantics coincide. **H** throughout: “retain” describes desired support,
not tested compatibility or enforced mediation. **Unknown** applies to every
retained operation's safe adapter until separately established. C3-F is included
to show why a task executor cannot silently substitute for an imported Android app.

| Surface | C3-A / C3-B managed packaging or no app-native | C3-C fixed executable set | C3-D one process/no browser/helpers | C3-E combined foreground utility | C3-F computation subset |
|---|---|---|---|---|---|
| `Application` | Retain constructor/attach/onCreate; platform native allowed only by reviewed policy in B | All early classes in admitted closure | One lifecycle; early SDK side effects still matter | Minimal Application desired; gate before construction/attach | No ordinary Application lifecycle established |
| Activities | Retain real UI semantics; not provided by isolated worker alone | Classes/resources fixed beforehand | Single-process UI intended | Required for useful form/calculator app; safe window/input bridge Unknown | PD-owned UI is not guest Activity support |
| Services | Retain only independently mediated paths | Service code fixed, callbacks gated | Same-process service potentially allowed; remote helpers excluded | Excluded by proposed foreground contract; actual denial Unknown | No Android service support |
| Providers | Retain with data/handle policy | All initializer/provider code admitted first | Local providers possible, no helper authority | Guest providers excluded; local state through narrow storage adapter; enforcement Unknown | No provider registration/URI semantics |
| BroadcastReceivers | Retain mediated delivery | Receiver code fixed; re-admit every entry | Same-process delivery only, no helper creation | Exclude asynchronous receiver entry; blocks normal integrations | Copied input event only, not Android broadcasts |
| Resources | Platform rendering/native dependencies remain | Immutable base/config resources required | Ordinary non-WebView UI assets retained | Text/layout/drawables and configuration needed; no genuine fallback | Supplied data only; not Android Resources contract |
| Base/splits | Full validation needed even without ELF | All executable splits fixed; later changes invalidate generation | All declarations across splits checked | Prefer complete base or fixed validated split set; no live modules | Fixed subset inputs only; APK semantics absent |
| Multidex | Managed apps may still have multiple DEX | Allow all pre-admitted DEX; forbid later additions | Not inherently excluded by one process | Can retain pre-admitted multidex; no arbitrary count-based safety rule | Verifier/import closure Unknown |
| Jobs | Need scheduling and re-entry mediation | Job code fixed but authority still needs gate | Same-process jobs conceptually possible; helpers prohibited | Excluded; loses deferred work and sync | No JobScheduler semantics |
| Alarms | Need token/epoch/re-entry policy | Fixed callback code not enough | One process does not deny alarms | Excluded; loses scheduled reminders | No AlarmManager semantics |
| WebView | Packaging rule alone permits platform WebView; B needs native/provider effects review | Provider executable dependencies complicate fixed closure | Excluded; process-local disable credited, universal guarantee Unknown | Excluded; no browser/auth or embedded web UI claim | No WebView |
| SDK initialization | Every transitive Java/native effect in scope | SDK code/modules must be fixed and admitted | Exclude helper/browser-dependent SDKs, not every library by name | Only audited local libraries without excluded effects hypothesized | Only verifier-modeled imports, no normal SDK assumption |
| Storage | Java APIs still invoke real filesystem/native code | Immutable executable inputs do not isolate mutable user data | Single process does not isolate manager/peer state | Per-instance local state via safe adapter required, Unknown | Bounded copied input/output; no guest storage API |
| Package visibility | Virtual universe still required | Fixed DEX does not block real PM observations | No helpers does not block PM/Binder queries | Limited virtual package universe needed even for simple app | No package API unless explicitly modeled |
| Binder/services | Full mandatory mediation remains | Existing handles not removed by content restrictions | Some component expansion reduced, direct authority remains | UI/storage service needs must be brokered without raw authority; Unknown | Copied narrow messages only, enforcement Unknown |
| Background work | Must cover threads, callbacks, restart and revocation | Pre-admitted code may still run asynchronously | No extra process still permits work in existing process | No background entry; safe stop on leaving allowed lifecycle must be proven | Bounded job completion/cancellation; not app background semantics |

**V/R/I:** App Startup's provider discovers initializers and their dependencies,
and manual initialization is separately possible [D11]. Merely removing startup
metadata would not prohibit equivalent code from running later; artifact
rewriting is not proposed. Excluding every provider/SDK is not necessary for all
possible classes, but early and transitive effects cannot be omitted from a proof.

**V/I:** on-demand/deferred feature delivery is a documented separate source of
code/resources [D12]. Android package validation and same-signer updates do not
constitute PD admission. An imported guest is not automatically an installed
Play-managed package; actual delivery compatibility is Unknown. Already admitted
multidex/configuration splits need not be banned merely to make the class appear
smaller. Later executable changes require a new validated generation before use.

**I:** C3-E could support useful local interaction if UI, resource, input,
lifecycle and persistent-state adapters can be made safe. It loses push, sync,
reminders, browser-based login, embedded web content and apps requiring services
or provider integration. This is a substantial compatibility reduction, not a
passing class manufactured by deleting inconvenient requirements. Even a local
calculator can query genuine Build/framework state unless an authority prevents
it. C3-F may be useful as a PD feature, but without the application surfaces above
it is not evidence of Android application support.

## 9. Authority ownership, setup and revocation

```text
PD manager: exact artifacts + class predicate + policy/session generation
             |
             | Protected dispatch requires every mandatory prerequisite
             v
  independent executable/capability owner: NOT ESTABLISHED
             |
  hypothetical constrained semantics runtime + hostile guest
             |
  ART/framework/native code, genuine handles, Android services/kernel
```

**H:** before any guest class use, provider, Application constructor/attach,
native initializer or background callback, the independent owner must install
the exclusion policy, inventory/remove forbidden inherited handles and genuine
state, protect manager/peer secrets, and expose only reviewed operations.
Unknown setup blocks Protected dispatch. WebView disabling, if used, must occur
before initialization in every relevant process; A9's ordinary process-local
flag is not evidence that hostile native code cannot tamper or invoke another
subsystem. No public re-enable method or successful private-reflection bypass
is claimed by this record.

**H/Unknown:** new processes, callbacks, modules and restarts must either be
impossible under the class policy or repeat the same before-code checks. Admission
generation changes invalidate prior authority and consent. A dead/stale supervisor
or broker must deny new operations and independently stop/revoke all affected
execution and capability holders. Guest-side death callbacks, PID polling,
ordinary test teardown and closing only the broker's FD copy do not establish
that guarantee. Already disclosed bytes cannot be revoked.

**I:** without that owner, reducing surface count leaves the retained surfaces
under the same Android authority as before. The precise missing authority is
both executable-content control and control of effects/handles; a candidate
that solves only one still cannot support Protected Mode.

### Required 15-row authority matrix

| Boundary | Enforcing mechanism | Owner | Guest bypass analysis | Evidence/category/scope | Remaining Unknowns |
|---|---|---|---|---|---|
| Executable-code authority | Import rejection and initial identity binding; no mandatory later gate | PD importer initially; ART afterward | Direct memory loader avoids helper and class label | R AG-1C API 35; R A1–A5 S37; H C3-C/E | Unknown replacement owner, immutable before-use decisions |
| Framework/Java | Android public API and baseline sandbox; proposed restricted imports unimplemented | Android/ART; PD lower owner Unknown | Direct Build/framework calls and existing objects survive packaging restrictions | V D6,D13,D15; R Candidate 1; I | Unknown complete API/dispatch closure and genuine-state exclusion |
| Binder/services/providers | Android caller/permission checks; proposed narrow broker | Android services/kernel; PD for its own endpoint | Retained/raw Binders, callback objects or deputies avoid selected wrappers | R Candidates 1–2; H C3-E | Unknown complete transaction/reply/FD graph and denial owner |
| Native/JNI | Linker/runtime/OS checks; no app-native content policy | Android/ART/linker | Platform JNI persists; later permitted load need not ask PD | R A6–A8; V D5–D7; I | Unknown selective native-effect allowlist and pre-initializer enforcement |
| Direct syscalls | Android baseline kernel controls only | Kernel/platform; extra PD authority Unknown | Machine code bypasses hooks, not kernel denials | R Candidate 1; R A10; I | Unknown ordinary-app lower mechanism; Candidate 4 pending |
| Filesystem | Existing DAC/MAC/mount restrictions; proposed storage broker | Android/kernel and PD broker | Java/native paths and existing FDs remain outside a path-only rule | R Candidate 1/A8; H C3-E | Unknown object/descriptor closure and allowed-path privacy |
| `/proc` | Existing per-path OS rules, no class-derived new policy | Android/kernel | Permitted self/system observations still reachable without packaged native code | R Candidate 1 bounded observations; I | Unknown full kernel/OEM surface and denial/mediation |
| `/sys` | Existing SELinux/DAC restrictions | Android/kernel | Java file APIs can reach permitted files; no `.so` exclusion changes that | R Candidate 1; R A8; I | Unknown complete OEM paths and mandatory observation control |
| Properties | Platform property access and genuine framework caches | Android/runtime | Java Build values do not require app-native library introduction | R Candidate 1 historical/source evidence; I | Unknown preloaded-state removal and mandatory Persona substitution |
| Networking | Selected OS denials; required external-VPN policy; no new routing mechanism | Android/kernel, broker and external VPN | SDK/helper/Binder deputies or granted socket authority remain distinct producers | R prior candidate/network Known Gaps; H | Unknown all-producer attribution, route verification and revocation |
| Storage | Initial artifact immutability differs from user-data isolation | PD importer; Android filesystem; proposed broker | Mutable data, raw FDs or same-authority paths evade logical per-app directory policy | R AG-1A/Candidate 2; H C3-E | Unknown manager/peer separation and persistent-state evidence |
| Lifecycle/components | Manifest inventory/PD dispatch refusal; no all-entry class barrier | Android managers and hypothetical PD supervisor | Early constructors/providers, jobs, callbacks and descendants bypass selected launch gate | V D8,D11–D12; R prior candidates | Unknown useful safe Activity lifecycle and all-entry prevention |
| Management isolation | Separate UID/process pattern can reduce access; narrow authenticated IPC required | Android/kernel plus PD manager | Same-authority runtime or broad deputy/handle may expose policy or secrets | R Candidate 1 bounded positives; H | Unknown complete hostile-code/deputy protection and stale authority |
| Dynamic code | Readonly-file rules and DEX validation; no PD all-loader admission | ART/Android; PD helper optional | Generated/received bytes and SDK loaders avoid scan-derived class policy | V D1–D4,D7,D16; R AG-1C/A1–A7 | Unknown other loaders/native paths; known direct failure unchanged |
| Third-party TCB | No integration; exact-source/provenance review required for future use | PD governance/platform vendors | Catalog references cannot confer runtime confinement or trust | R catalog/Candidate 2; U claims separated | Unknown future verifier/adapter dependency closure and auditability |

## 10. Platform scope and source/TCB implications

**R/H:** the unchanged investigation matrix is API 31–37; physical non-rooted
ARM64/release-equivalent evidence is required, with Google/AOSP, Samsung and
another OEM separately scoped. Current build settings remain minSdk 31,
targetSdk 37, compileSdk 37. No supported device/configuration is established.
For an imported guest, the effective process target/ART compatibility state
cannot simply be inferred from the guest manifest: host runtime identity and
any target adaptation would need exact evidence.

| Scope | Evidence credited | Limit |
|---|---|---|
| API 31–33 within M | Public loaders/OS baseline predate range; D5's target-29 rule | No per-device class prevention demonstrated |
| API 34+ with target 34+ | D4 readonly dynamic executable files | Integrity hardening, not fixed-artifact admission; memory DEX separate |
| API 35 Google APIs x86_64 debug | Historical AG-1C `1111` bypass | No physical ARM64 or all-loader inference |
| API 36 and other untested configurations | Public contracts and preserved Candidate 1 comparisons | New runtime outcomes Unknown |
| API 37 / Android 17 | Exact S37 loader/native/WebView/app-policy source; D7 target-37 native readonly rule | Feature/ART/kernel/OEM deployment Unknown; not a new experiment |
| All OEMs/ABIs/release builds | No new execution evidence | Hidden APIs, native linkage, platform/ART updates and subtree cleanup Unknown |

**R/I:** selectively reuse the unchanged
[ten-project catalog](../open-source-reference-catalog.md), without repeating a
competitor survey or selecting an engine:

| Reference and exact catalog pin | Idea used here | What it cannot establish |
|---|---|---|
| Mirro `74e6a1e3ea1898b2e2a705d7c8b3e730059023b1` | APK/native/split inventory, loader graph, explicit opaque capability reporting | Negative scan as proof; upstream authority post-mortem is U, not new PD execution |
| NEXTVM `f581a6642596db396a6fe606addd735979403b6b` | Analyzer/package inventory and component/resource organization | Source observations preserved in Candidate 2 include host delegation; no exclusive executable or syscall owner |
| NewBlackbox `89b59836c66f173756a4ae258cf379a957649820` | Application/provider/Activity/service semantics as compatibility work | Hooks, readonly adaptations and process slots are not compulsory class restrictions |
| XPrivacyLua `85a1e498d5a9dbb902ca3d83e987ae6eec377d7a` | Privacy path/coverage inventory | No native confinement; Xposed deployment remains prohibited |
| ByteHook `a8bd254f6e53022b65136f40d10ae3763b6ef8ad` | Selected PLT observation/interception vocabulary | No raw-syscall confinement or unbypassable code-origin policy |
| ShadowHook `593f491be68799f03ee3bab71ce5908846437147` | Native/linker/initializer observation vocabulary | No kernel sandbox or proof all hostile native routes traverse a hook |

These are R references to already reviewed catalog/source findings, not fresh
upstream execution or provenance audits. Broad upstream isolation descriptions
remain U. Other catalog dispositions are unchanged. Future TCB would include
the verifier/admission engine, bootstrap, semantics adapters, executable-policy
owner, external brokers, supervisor and indispensable Android/native components.
Source completeness, license, transitive dependencies, bundled binaries and
maintainability need separate review and an explicit integration decision.
An opaque security-critical component cannot become trusted because the class
is small. Nothing is integrated by this record.

## 11. Requirement traceability

All consequences below are I/R/H research findings. No requirement is satisfied,
weakened, renumbered or changed.

| Requirement | Candidate consequence |
|---|---|
| PD-REQ-021 | Every unresolved mandatory exclusion blocks Protected launch before code; calling a feature excluded does not make it reliably Unsupported |
| PD-REQ-081 | Platform/SDK JNI, GMS/Firebase, WebView, wrappers and later code need independent coverage; managed packaging cannot inherit it |
| PD-REQ-083 | Acceptance remains unsatisfied; no release/physical matrix, complete mediation or independent Android/native security review |
| PD-REQ-087 | No precisely evidenced Protected class established; app-native/opaque/dynamic behavior not presumed safe |
| PD-REQ-090 | Known AG-1C mandatory bypass retains hard stop without Experimental override; no universal Known-unsafe classification of all apps |
| PD-REQ-091 | New executable content needs an unavoidable before-use decision; readonly/valid code is not admitted code; safe discovery-triggered termination Unknown |
| PD-REQ-093 | Static absence, compatibility, SDK/ELF inventory and packaging labels do not prove runtime class membership; opaque behavior Unknown; no score |
| PD-REQ-095 | Experimental results do not count as Protected evidence; no product UI/support claim changes |
| PD-REQ-002, 006–010, 080 | No root, privilege, production ADB, patched OS, routine rewrite/re-sign or convenience guest Android may repair missing authority implicitly |
| PD-REQ-011–016, 027, 041 | Hostile Java remains able to request real operations; native/process/handle/manager isolation and revocation obligations survive class narrowing |
| PD-REQ-026, 028–031, 065, 071–076 | Retained capabilities still need coherent Persona and controlled Real; no host GPS/account/data fallback or permission-based automatic disclosure |
| PD-REQ-038, 044–046, 050–051, 086, 089 | Complete artifact identity, early initialization, every entry, safe update and new-generation revalidation remain necessary |
| PD-REQ-003, 032–034, 058, 070, 092 | No PD VpnService; required external VPN defaults ON; all traffic producers and route changes require independent fail-closed evidence; no physical fallback |
| PD-REQ-019–020, 057, 059–060, 078, 082, 084–085, 094 | Scoped adversarial evidence, public-surface secrecy, bounded diagnostics, independent review and integration/governance gates remain open |
| PD-REQ-088 | Unknown-only Experimental classification remains distinct; a known bypass or incompatibility cannot be overridden by consent |

## 12. Remaining Unknowns and decisive future evidence

**Unknown:** the concrete ordinary-app-accessible owner of both executable
admission and retained capability authority; a useful sound verifier/closed
library model; prevention of all native introduction; all loaders and SDK
routes; safe UI/resource/storage/Binder adapters; inherited mappings and handles;
helper/thread/descendant policy inheritance and teardown; API/OEM/ABI/ART/release
portability; complete split/dependency closure; external-VPN routing and all
producers; supply-chain and independent security review.

**H — prerequisites for a later separately authorized review:** name the actual
mechanism and owner, establish permitted Android access, enumerate every exclusion
and retained adapter, and provide an immutable pre-entry policy plus failure and
revocation contract. Candidate 4 is only a possible research dependency; this
record neither investigates additional syscall/Binder filters nor assumes they
solve DEX interpretation, genuine cached data or Android UI semantics.

**H — falsification conditions, not experiments authorized here:** a later review
would need controlled synthetic valid payloads and separate observations of
construction, resolution, static initialization and entry; reflected/SDK/opaque
introductions; valid native initialization and existing-native effects; generated
code/memory; manifest and native children; renderer/helpers; initializer and
background entry; split replacement; stale handles and supervisor/broker death.
Independent process/UID observations, persistent sentinels and external packet
evidence must corroborate enforcement before guest effects. Any unadmitted code
use, forbidden genuine value, uncontrolled helper, stale capability or fallback
falsifies the claimed class. Invalid bytecode, missing ABI, unavailable SDK,
incidental crash or normal teardown is not a successful denial by the named owner.

**H:** compatibility would also need a controlled synthetic Android utility
exercising Application, Activity recreation, resources, admitted multidex/splits,
local state and safe lifecycle termination. A single invoked DEX method cannot
serve as that evidence. No tests, fixtures, runners or prototype were created.

## 13. Source ledger

Official documentation below was accessed 2026-10-01. It is live documentation,
not an immutable release snapshot. AOSP files were retrieved from exact-tag
Gitiles TEXT endpoints into a temporary research directory and inspected as text;
no source was built or executed. A temporary 503 on A2 was retried successfully.
No claim relies on a failed retrieval. Prior candidate/catalog facts are linked
as preserved repository evidence instead of presented as new upstream findings.

| ID | Official source | Claim and scope |
|---|---|---|
| D1 | [InMemoryDexClassLoader](https://developer.android.com/reference/dalvik/system/InMemoryDexClassLoader) | V: buffer loader, API 26; multiple buffers 27 and native-library-path overload 29 |
| D2 | [DexClassLoader](https://developer.android.com/reference/dalvik/system/DexClassLoader) | V: DEX-bearing JAR/APK loading; API 3+; not a PD gate |
| D3 | [PathClassLoader](https://developer.android.com/reference/dalvik/system/PathClassLoader) | V: file-path class loading; startup versus later-loader distinction is I |
| D4 | [Android 14 target changes](https://developer.android.com/about/versions/14/behavior-changes-14#safer-dynamic-code-loading) | V: target-34+ readonly dynamic files; does not authorize content (I) |
| D5 | [Android 10 target changes](https://developer.android.com/about/versions/10/behavior-changes-10) | V: target-29+ app-home exec and writable-file executable-mapping restrictions; no blanket no-native inference |
| D6 | [System API](https://developer.android.com/reference/java/lang/System) | V: load/loadLibrary; setSecurityManager rejects non-null manager and Android does not support this in-VM sandbox |
| D7 | [Android 17 target changes](https://developer.android.com/about/versions/17/behavior-changes-17) | V: target-37 native System.load readonly rule; not all native routes |
| D8 | [Processes and threads](https://developer.android.com/guide/components/processes-and-threads) | V: component process attributes, threads and restart lifecycle |
| D9 | [WebView.disableWebView](https://developer.android.com/reference/android/webkit/WebView#disableWebView()) | V: API 28+, process-local prevention of ordinary WebView use, exception if already initialized |
| D10 | [Manage WebView objects](https://developer.android.com/develop/ui/views/layout/webapps/managing-webview) | V: separate renderer lifecycle/importance in multiprocess mode |
| D11 | [App Startup](https://developer.android.com/topic/libraries/app-startup) | V: InitializationProvider metadata/dependency discovery and manual initialization |
| D12 | [On-demand feature delivery](https://developer.android.com/guide/playcore/feature-delivery/on-demand) | V: on-demand/deferred module delivery; imported-guest compatibility Unknown |
| D13 | [Application Sandbox](https://source.android.com/docs/security/app-sandbox) | V: OS-level application sandbox covers native and managed code |
| D14 | [Security checklist](https://developer.android.com/privacy-and-security/security-tips) | V: Android VM is not the application security boundary; dynamically loaded code shares app permissions |
| D15 | [Non-SDK restrictions](https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces) | V: platform/target-dependent restrictions including reflection/JNI; public API distinction |
| D16 | [DexFile](https://developer.android.com/reference/dalvik/system/DexFile) | V: deprecated API, not proof the underlying execution path is absent |

All A entries are **R**, exact `android-17.0.0_r1`, not universal API/OEM contracts.
The source files and relevant symbols are sufficient to review the asserted path
without relying on moving line numbers.

| ID | Exact AOSP source | Relevant symbols/observation |
|---|---|---|
| A1 | [libcore InMemoryDexClassLoader.java](https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/InMemoryDexClassLoader.java) | Constructors delegate buffers/library path to base |
| A2 | [libcore BaseDexClassLoader.java](https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java) | ByteBuffer constructor, initByteBufferDexPath, findClass, findLibrary |
| A3 | [libcore DexPathList.java](https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/DexPathList.java) | initByteBufferDexPath creates DexFile; element class lookup |
| A4 | [libcore DexFile.java](https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/DexFile.java) | Buffer constructor, openInMemoryDexFiles/native delegate, loadClassBinaryName/defineClassNative |
| A5 | [ART dalvik_system_DexFile.cc](https://android.googlesource.com/platform/art/+/android-17.0.0_r1/runtime/native/dalvik_system_DexFile.cc) | AllocateDexMemoryMap, DexFile_openInMemoryDexFilesNative, separate file DCL enforcement |
| A6 | [libcore Runtime.java](https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/ojluni/src/main/java/java/lang/Runtime.java) | load0/loadLibrary0/nativeLoad; readonly native checks with flags; exec delegates to ProcessBuilder |
| A7 | [ART java_vm_ext.cc](https://android.googlesource.com/platform/art/+/android-17.0.0_r1/runtime/jni/java_vm_ext.cc) | JavaVMExt::LoadNativeLibrary, existing-library checks, OpenNativeLibrary, JNI_OnLoad before return |
| A8 | [libcore Linux.java](https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/luni/src/main/java/libcore/io/Linux.java) | Native open/read/socket/mmap/exec methods; implementation observation, not SDK access claim |
| A9 | [framework WebViewFactory.java](https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/java/android/webkit/WebViewFactory.java) | disableWebView, sWebViewDisabled, provider lock and getProvider check; state is inside current process |
| A10 | [SELinux private/app.te](https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/app.te) | appdomain execmem allow and executable mapping rules; not complete compiled policy/OEM proof |

## 14. Validation scope and candidate disposition

Validation checked all 41 local Markdown link occurrences across the two changed
documents, all 12 candidate-record tables for consistent widths and nonempty
cells, and the exact 15 required authority rows. All 16 official documentation
pages and 10 exact-tag AOSP source files were accessed. Every changed line and
link was reviewed; Candidate 1/2 records and requirements remain byte-for-byte
unchanged. The wrapper blob/SHA-256 and gradlew blob/mode match the required
baseline. No Android tests, builds, workflows or runtime experiments are run for this record.
Publication commit/tree/remote verification is reported separately; CI is not
polled. No broader requirement or release validation is implied.

**I:** scan-derived native/loader/process/SDK exclusions do not remove the
missing authority. AG-1C remains a known direct bypass, literal Java-only/native-
free execution is not normal Android semantics, and the real readonly/WebView
controls have narrower scope than a complete Protected class. The combined
foreground utility remains a useful conceptual application class only if a
separate mandatory executable and capability boundary can enforce every exclusion
and safely support the retained semantics. That boundary is unestablished.

Unknown is not success. Candidate 1 remains rejected; Candidate 2 remains Unknown;
AG-1 remains FAILED. Candidates 4–5 are pending, no overall redesign outcome is
selected, no engine or dependency is selected/integrated, no scope/requirement
change is approved, and neither a prototype nor production PR 6 is authorized.

**UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY**
