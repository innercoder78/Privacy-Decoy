# Post-AG-1 Candidate 2 — controlled Android semantics above a lower boundary

**Research/access date:** 2026-09-30. **Candidate:** 2 of five under
[ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md).
**Disposition:** **UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED**.

## 1. Scope and question

Can NewBlackbox/NEXTVM-style application and component semantics exist above a
separate mandatory lower boundary without restoring forbidden genuine authority?

**Inference:** the decomposition is conceptually coherent only if the lower
owner constrains the semantics runtime as well as the guest, and every external
adapter preserves that constraint. The reviewed pins do not implement that
decomposition. They retain host objects, genuine-service fallback, ordinary
host-app process authority, and bypassable interception. Copying either runtime
unchanged would not meet PD's constraints. Their logical package/component
models are not proven inherently inseparable from those unsafe adapters, however.
Whether a redesigned adapter can retain useful Android semantics above a
permitted stronger boundary remains Unknown. No such lower authority was
identified; no bounded prototype review is earned by this result.

**Verified source observation:** work began from fetched main
`2599a98204b53b0dc85a45e40ecdae450fff45b1`, tree
`ce956f0a9f704db95e587c71b9113a04deb2bd70`, on branch
`research/post-ag1-c2-controlled-runtime-boundary`. The initial checkout was
clean, and eight pre-existing stashes were preserved. This is documentation-only
research: no upstream code was copied into PD, built, executed, installed, or
integrated; no bypass code or prototype was written.

Candidates 3–5 remain pending/not performed. Candidate 4's possible syscall/Binder
mechanism is an **Unknown dependency**, not an assumed solution and not researched
here. There is no overall redesign outcome. Canonical production Roadmap PR 6
remains unstarted and unauthorized. PD-REQ-001..095 remain unchanged; none is
marked satisfied.

### Evidence vocabulary

* **S — Verified source observation:** inspected implementation at the exact
  ledger pin; proves source structure, not runtime success or complete coverage.
* **R — Preserved repository evidence:** an existing PD observation/disposition,
  with its original experiment and scope; not rerun in this study.
* **P — Verified Android/platform fact:** cited official API/documented behavior,
  not a measurement on every OEM or OS build.
* **U — Upstream claim:** README, comment, or upstream assessment; not PD security
  evidence, even when the claim describes an intended isolation property.
* **I — Inference:** consequence reasoned from the cited observations.
* **H — Design hypothesis:** an unimplemented obligation or possible decomposition.
* **? — Unknown:** not established in the specified scope; never success.

These labels apply to tables too. IDs N1–N20, T1–T17, S1–S5 and P1–P4 resolve
to exact paths and symbols in section 20. Assertions about reference behavior
are limited to the reviewed paths; helper definitions do not prove call-site
reachability, successful injection, or runtime deployment.

## 2. Candidate 1 carry-forward

**R:** [Candidate 1](post-ag1-candidate-1-os-process-compartment.md) remains
**REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY**. Ordinary isolated
services offer bounded OS restrictions, including separate credentials and
selected direct-data/socket denials. They still expose genuine framework/property
surfaces and do not authorize DEX content. A virtualization runtime placed inside
that same compartment cannot make those missing authorities appear.

**R:** [AG-1C](ag1-runtime-executable-code.md) positively demonstrated all four
direct-loader milestones on API 35 Google APIs x86_64 debug at head
`55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`. The helper was bypassed in an isolated
service. That tested failure is not Unknown. [AG-1 closeout](ag1-feasibility-closeout.md)
remains **FAILED**; the [ADR-0007 handoff](../architecture-admission-gated-runtime.md)
is historical, not the forward runtime. The prior STOP recommendation,
S1 FALSIFIED/follow-up BLOCKED, engine provenance dispositions, VirtualSpace
disqualification, and network Known Gaps remain intact.

**H:** Candidate 1's bounded OS restrictions may be defense in depth. What is
still missing is mandatory control over permitted genuine data, executable
introduction, raw native operations, capabilities, and all descendants, together
with useful Android semantics. No rejected process flag is silently promoted.

## 3. Reference/source scope

The [catalog](../open-source-reference-catalog.md) pins are unchanged:

| Role / repository | Exact reviewed commit | Scope |
|---|---|---|
| Primary: ALEX5402/NewBlackbox | `89b59836c66f173756a4ae258cf379a957649820` | Slots, components, runtime/context, proxy fallthrough, IO/JNI/property hooks, WebView, VPN and native build inputs |
| Primary: TanvirHossain2/NEXTVM | `f581a6642596db396a6fe606addd735979403b6b` | Stubs, context/loaders, component lifecycle, Binder/GMS/accounts, native interception, splits and failure paths |
| Supporting: josskixg/renjana | `14302a57cd66114c6979acf7ca56c97f845a6841` | Split/resources, launch lifecycle and explicit no-isolation fallback; Pine dependency |
| Supporting: obadadallo95/mirro-android-virtualization | `74e6a1e3ea1898b2e2a705d7c8b3e730059023b1` | Upstream authority post-mortem and analyzer/semantics distinction |
| Supporting: iofomo/binderceptor | `7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61` | JNI-to-opaque-core boundary only |

**S:** exact-ref archives were retrieved from GitHub into a temporary research
directory outside PD and inspected as text. No current upstream HEAD replaced a
pin. No ten-repository resurvey or complete new provenance audit was conducted.
**R:** VirtualSpace remains a negative/disqualified exact-pin reference only;
its [preserved evidence](architecture-discovery-virtualspace-static-falsification.md)
was read, not reopened as a candidate. The prior
[architecture synthesis](architecture-discovery-synthesis.md) retains its scope.

**U:** NewBlackbox's sandbox/compatibility descriptions and NEXTVM's broad
isolation language are claims. Source comments describing all-call interception
or isolated accounts do not override the positive fallthrough paths below.

## 4. NewBlackbox architecture findings

### Process and component model

**S — N1–N4:** `ProxyManifest.FREE_COUNT` is 50. Host-declared `:pN` Activities,
services, job services and providers supply process/component slots. The reviewed
manifest uses ordinary process declarations, not isolated tenant services.
`BProcessManagerService.startProcessLocked` distinguishes logical `buid`/`bpid`
from `app.uid = Process.myUid()`, starts the slot through a provider call, and
receives an `IBActivityThread` Binder. Process death bookkeeping removes records
and notifications. **I/P:** these are host-app processes and logical identities,
not separately installed guest kernel tenants [P1; Candidate 1]. No numeric UID
was measured in this study.

**S — N3–N6, N17:** proxy providers bootstrap `BActivityThread`; proxy services
delegate callbacks to `AppServiceDispatcher`; Activity stubs and the HCallback
hook route target records. ActivityManager hooks rewrite/reroute services,
providers, broadcasts, receiver registration and IntentSender paths. Jobs obtain
virtual job mappings and ultimately invoke the system scheduler; their failure
paths include genuine-system retries. Providers are installed reflectively and
per-provider exceptions can be ignored. This supplies useful routing structure,
not evidence of complete component semantics or fail-closed startup.

### Loading, context and Binder

**S — N4, N7:** `BlackBoxCore` retains a static host `Context` and exposes
`getContext()`. `BActivityThread.handleBindApplication` obtains a package context
and `LoadedApk`, changes framework bind metadata, initializes native IO, creates
the Application, installs providers, then invokes Application `onCreate`.
Application construction/attach and native initializers are earlier execution
points than `onCreate`. Host framework/runtime state already exists. **I:**
this ordering is not a mandatory below-guest pre-code gate.

**S — N4:** active `createPackageContext` returns null on failure; it must not be
misreported as always returning host context. The same file defines
`createMinimalPackageContext`/`createWrappedBaseContext` helpers that can return
host-backed package manager/resources/classloader/application context or the
host context itself. Their presence is verified; universal reachability from
the ordinary bind path is **Unknown**. Active bind code separately retries
`makeApplication` and ultimately throws if creation cannot succeed.

**S — N8–N10:** `HookManager` registers a broad collection of framework/service
proxies, including activity/package, location, accounts, connectivity, DNS,
storage, alarms, jobs, telephony, display, and resources. Registration is not
successful injection or full coverage. `ClassInvocationStub.invoke` invokes the
retained base object for missing/disabled method hooks. `BinderInvocationStub`
replaces service-cache entries but its `transact` delegates directly to
`mBaseBinder.transact`. **I:** callers using raw transactions avoid the Java
method-hook map; receiver-side Android checks still apply. This is positive
source delegation, not a claim that every transaction is permitted by Android.

**S — N5–N6:** package resolution tries virtual metadata but `ResolveIntent` and
`ResolveService` fall through to genuine PM on a miss; `GetPackageInfo` can use
the original for open packages. AM provider handling retains genuine provider
holders; `BindServiceCommon` can invoke the original on error. **I:** host
package/context replacement helps satisfy genuine Android attribution, while
retaining the host's authority. It does not create an independent guest identity.

### IO, properties, JNI and dynamic execution

**S — N11–N15:** `IOCore` maps guest data/native-library/external-storage paths
and selected `/proc/*/cmdline` paths. Unmatched paths remain unchanged; paths
already containing the virtual-root marker bypass translation. Rule setup catches
exceptions and continues to enable native IO. `UnixFileSystemHook` substitutes
selected JNI entry points and tolerates individual hook failures. In contrast,
`FileSystemHook.init` resolves `open`/`open64` addresses but does not install the
defined replacement functions in that reviewed function. Do not credit this
function with working general libc-open confinement.

**S — N13–N15:** `VirtualSpoof` attempts to install a Dobby hook on
`__system_property_get`, returns selected fixed descriptions, and delegates
unmatched properties to the original. JNI Binder identity rewriting changes
reported values, not kernel credentials. `DexFileHook` makes selected files
read-only before forwarding `openDexFileNative`; it is a compatibility adaptation,
not a PD content-admission decision. `VMClassLoaderHook` filters selected class
names, then calls the original. `ClassLoaderProxy.getWho()` returns null, causing
the common injection routine to return early; annotated methods and fallback
loader lists are not proof of installed loader interception. **I:** neither
file read-only adaptation nor these hooks prevents the AG-1C memory-loader path.
Actual direct-loader execution in this pin is **Unknown**, not newly tested.

### WebView, network, secondary processes and packages

**S — N4, N16:** the active bind path calls `WebView.setDataDirectorySuffix` on
applicable APIs. `WebViewProxy.getWho()` returns null; its constructor/settings
handlers cannot be credited as automatically installed by the common injector.
**I/?:** directory separation is useful compatibility, not renderer, SDK,
network, resource or cross-process containment evidence.

**S — N1, N18:** the manifest requests INTERNET and declares `ProxyVpnService`,
which extends `VpnService` and attempts establishment from its start path.
**R/I:** this built-in VPN behavior is unusable for PD under PD-REQ-003/070/080;
removing it would not establish independent external-VPN routing. Connectivity
proxies and shared host UID do not attribute/contain every Java/native/helper
packet. All-producer route control remains **Unknown**.

**S/I — N2, N19:** named guest processes are allocated from host slots; arbitrary
native children are not proven to traverse that allocator. Package/storage
machinery models guest metadata and copies a base artifact/native libraries;
reviewed resource code adds the base path. **Unknown:** complete split-set,
multidex, signing/update atomicity, secondary-process initialization, native
descendant cleanup and release-equivalent compatibility. No absence claim about
every unreviewed split path is inferred.

## 5. NEXTVM architecture findings

### Slots, context, packages and component lifecycle

**S — T1–T3:** the manifest declares host `:pN` Activity/service/provider/receiver
stubs and a service process; `VirtualProcessManager` allocates slots.
`VirtualMultiProcessManager.createProcess` also creates logical records with
generated virtual PID/UID values. Its record allocation is not an OS credential
change. **I/P:** ordinary host process declarations and the PM proxy's actual
`Process.myUid()` handling establish the host-app authority model [T5; P1].
Neither a record, name, classloader nor directory is a kernel tenant.

**S — T4:** `VirtualContext` redirects files/cache/databases and labels the guest
package, but wraps a real context. `getSystemService` delegates to that context
or its superclass; PM uses the superclass expecting global hooks; application
context/classloader/resources can fall back to the base when guest objects are
absent. Service and broadcast methods call the superclass after metadata changes.
**I:** virtual context is a compatibility facade over real framework authority,
not a capability-safe object graph or a new protection domain.

**S — T5–T8:** `BinderProxyManager` substitutes AM/PM singleton objects and logs
installation failures without making every such failure fatal. AM switches
recognized operations to guest routing; **unhandled ActivityManager calls reach
the genuine system**. Its catch path unwraps and **swallows `SecurityException`**
by returning null; non-security failures retry the original and can then return
null. PM likewise has original-service delegation and compatibility exception
handling. This preserves and strengthens the catalog's observation; it is not
reclassified as safe synthetic behavior.

**S — T9–T10:** Application management constructs/attaches guest classes, initializes
providers before Application `onCreate`, and handles provider failures individually.
Provider and broadcast managers supply virtual authority/receiver maps and local
dispatch. Declared stub methods themselves include null/empty implementations;
the existence of a stub does not prove the full dispatcher path works.
**I/?:** early constructor/attach/SDK/native execution must already be bounded;
reflection setup and callback ordering do not establish that boundary.

### GMS and genuine-account fallback

**S — T7, T11:** `GmsBinderBridge` binds real host GMS, retains its Binder, rewrites
package fields in requests/replies, and forwards transactions. This includes
dynamic-action binding and Messenger rewrite fallback. `VirtualGmsManager`
maintains virtual accounts, but `getVirtualGoogleAccounts` includes global
virtual accounts and returns null for an empty result.

**S — T7:** `AccountManagerProxyHandler.handleGetAccountsByType` explicitly uses
that null result as a reason to try host Google accounts through
`AccountManager.get(context).getAccountsByType`; non-empty host results are
returned. Other account methods, including token-related calls, can delegate to
the original. **I:** this is a positive host-account fallback path requiring
redesign for PD, even if Android denies or filters accounts on a particular
device. **Unknown:** actual returned account data, permissions, recursive proxy
interactions, remote GMS callbacks and all transitive handles; no real accounts
were used. Upstream isolated-account comments are **U**, not contrary evidence.

### Native interception and loading

**S — T12–T14:** native source implements real ELF relocation/GOT patching for
`open`, `openat`, `access`, `stat`, `lstat`, `readlink`, `fopen` and
`__system_property_get`. Original pointers remain available; unmatched properties
use the real function. The loaded-library iterator skips the linker, its own
library, ART and other named objects. `nativeInit` sets initialized and returns
true even when `install_plt_hooks` fails, with Java-only fallback. Kotlin loading
also tolerates unavailable native code. A success return is not complete hook
coverage, much less confinement.

**I:** raw syscall instructions need not pass any patched GOT entry, Java method,
Binder proxy or path wrapper. Existing Android kernel denials still apply; no
new PD-owned raw-syscall authority is established. Newly loaded library timing,
alternate property APIs, inherited mappings, tampering and descendants remain
**Unknown**. No bypass was implemented.

**S — T15:** `VirtualClassLoader.createClassLoader` builds a `PathClassLoader`
path from base plus existing splits, sets native search paths, and attempts
linker-namespace setup. It makes an APK read-only and supplies framework-parent
loading. **I:** none of this authorizes later guest-created loaders or executable
memory. Comments about loader coverage are **U**, not a proof that every multidex
or packed-code path is mediated. Unknown split paths are not admission success.

### WebView, background, secondary and package/network behavior

**S — T7, T16–T17:** resource handling adds APK/split assets and has WebView
resource compatibility helpers using the host provider's APK paths. Jobs/alarms
have per-instance tracking and original-system delegation; the job handler may
return success after scheduling exceptions. Connectivity proxies implement
policy-dependent results and original-service calls; the source default is
`FULL_ACCESS`, and the app requests INTERNET. These paths are not packet denial.

**S — T16:** `VirtualEngine.installApp` discovers/copies splits; a copy failure
can retain the original split path. **I:** this is a positive mutable/external
artifact dependency to redesign, not proof of an immutable admitted generation.
**S/I — T3:** secondary-process records and per-process loader/Application maps
are useful semantics but do not prove all native children are registered or
restricted. **Unknown:** all-entry WebView/SDK/background/native setup, complete
base/split/resource/update semantics, API/OEM behavior, immutable dynamic-code
admission, route/VPN inheritance, and hostile process-tree revocation.

## 6. Supporting reference findings

**S — S1–S2:** Renjana's `GuestInfoCache` builds resources with APK asset paths;
its launch path caches instance metadata and starts lifecycle support. On stub
launch failure, `InstanceLauncher` tries a direct launch and can return
`FallbackNoIsolation`. Pine initialization/dependency paths do not supply a
kernel boundary. **I:** split/lifecycle organization is reusable as an idea;
continuing outside isolation is disqualifying on a PD mandatory surface and
must be removed in any separate redesign. This does not revise its catalog status.

**U — S3:** Mirro's pinned README and architecture assessment explain that
in-process package/context semantics do not own Android UID, Binder attribution,
signing, service ownership or native authority; the project describes itself as
research with product development concluded. **I/R:** that negative lesson is
consistent with the source-derived authority model here and PD's own failures.
Mirro's compatibility/profile results are not new PD measurements or a repair
of S1. Its analyzer, capability and loader-graph concepts remain supporting ideas.

**S — S4–S5:** Binderceptor's visible JNI resolves `binderceptor_call` from
`libifmabinderceptor-core.so`; ARM64 and ARMv7 core binaries are bundled.
**R/?:** the security-critical core's source completeness/provenance remains
unresolved. It informs Binder architecture concepts only. ByteHook/ShadowHook
are interception tools, not the missing lower boundary. VirtualSpace remains
disqualified and is not re-surveyed or selected.

## 7. Semantics versus enforcement decomposition

```text
Android host / kernel / system services
                    |
    [mandatory lower boundary: NOT ESTABLISHED]
        |           | narrow, authenticated broker operations
        |           +---- supervisor/policy outside hostile authority
        |
    [controlled Android-semantics runtime]
        |
    [hostile guest, including its Java/native/dynamic code]
```

**H:** guest-side runtime code is not trusted to retain exclusive authority once
hostile native code shares its address space. Security decisions and secrets
must be outside that authority. The lower owner must constrain runtime operations
too; an exemption based only on a Java class, call stack, library or supposed
trusted function is not an independently enforced principal. A broker cannot
execute guest callbacks outside the compartment for compatibility.

| Operation | Semantics work (S) | Can guest skip it? (I) | Genuine authority retained (S/I) | Required lower control (H) / transfer risk |
|---|---|---|---|---|
| Launch/component dispatch | Slots, intents, Application/provider callbacks [N1–N6; T1–T10] | Direct calls, initialization and background paths need not request a new launch | Host ActivityThread, system scheduler, context and callbacks | Gate every entry and callback before code; an outside host stub must never execute guest code |
| Package/service query | Virtual metadata and proxy results [N5–N10; T4–T8] | Raw Binder, base objects, unhandled methods | Real PM/AM and service Binders | Deny unrestricted handles/transactions; copied virtual metadata only |
| Files/storage/proc/sys | Path maps and selected hooks [N11–N15; T12–T15] | Direct syscall, relative/descriptor access, mappings | Host UID access, open FDs and mapped files | Confine actual objects and operations, not just strings; no broad directory/device FD grant |
| Identity/properties | Selected values and context identity [N13; T4,T12] | Framework caches, alternate native APIs or existing property memory | Genuine Build fields and property pages | Prevent first access to forbidden values; later hooks cannot erase copied data |
| Executable introduction | Startup loader, readonly adaptation [N4,N14–N15; T15] | Direct memory loader/native loading | ART, executable content and native runtime | Authoritative before-use decision plus protected content identity; readable unadmitted bytes may become interpreted DEX |
| Network/GMS | Connectivity views, service bridges [N18; T7,T11] | Direct sockets, other Binder deputies, WebView/SDK | Host INTERNET, GMS and returned endpoints | Deny uncontrolled egress and host state; do not return unrestricted socket/Network/session authority |

**I:** logical package tables, intent translation and resource inventories can
be separated conceptually from authority. Their current adapters are not safe
interfaces. A future refusal to perform an unsafe operation may make an app
incompatible; it must never activate the genuine fallback. Whether enough useful
semantics survive all such refusals is **Unknown**, not an authorized scope cut.

## 8. Genuine-handle and confused-deputy analysis

**S/I:** N4–N10 and T4–T11 show why a proxy is insufficient when its object graph
or replies retain genuine capabilities. The following is an authority inventory,
not a claim every handle type was observed escaping in either reference.
**P:** Android supports FD transfer/duplication and network-specific socket
operations [P3–P4]. A Java object is not automatically an unrestricted kernel
capability; actual permissions and remote checks remain relevant.

| Object/authority | Source basis and future use without PD mediation | Necessary contract (H); remaining scope |
|---|---|---|
| Android `Context` | S: retained/wrapped host context [N4,N7; T4]. I: reaches base managers, resolvers, files and framework state | Guest-safe facade backed only by bounded endpoints; same-process reflection/native reachability must be independently constrained; ? complete graph |
| Binder objects/callbacks | S: original objects retained and raw transact forwarded [N9–N10; T6,T11]. I: callback/reply may open another authority route | Authenticate caller/session; enumerate methods, reply objects and callbacks; no arbitrary transact deputy; ? transitive graph |
| `IBinder` service handles | S: cached real service and GMS Binders. I: future calls bypass service discovery or Java hook selection | Deny genuine handles or constrain every transaction below guest; discovery denial alone insufficient |
| File descriptors | P/I: parcelable/duplicable FDs [P3], provider `openFile` [T10] | Grant only reviewed narrow objects/rights; no host directory/device/file capability outside policy; ? inherited/duplicated FD inventory |
| Sockets | I/R: an existing socket can outlive creation/path mediation; Candidate 1 preserved received-socket concern | Keep egress broker-owned or independently constrain use; close/revoke all holders; ? route and lifecycle proof |
| `Network` objects | P/I: network-specific binding/socket factory [P4]; T7 delegates some connectivity results | No unreviewed network selection or socket factory; creation and use both subject to external-VPN policy; ? complete producer graph |
| Package-manager objects | S: host/superclass PM and original fallthrough [N5; T4–T5] | Virtual universe with no host enumeration fallback; ? cached manager objects across hooks |
| Provider handles | S: AM acquisition and local provider dispatch [N6; T10] | Per-operation URI/grant checks and bounded replies; inspect FDs, cursor windows, callbacks and subsequent calls; ? remote providers |
| GMS/account objects | S: host GMS tunnel and explicit host-account fallback [T7,T11] | No automatic host accounts, tokens, identifiers or sessions; controlled Real only; ? all GMS replies and OEM/account permissions |
| Mapped memory | I/R: property/runtime state or shared mappings may already contain genuine data; historical AG-1C transferred DEX in shared memory | Scrub/exclude before guest entry; immutable admitted executable snapshots; no manager secrets in shared pages; ? preexisting mappings |
| Native library handles | S/I: native search paths/JNI and genuine platform symbols [N12–N15; T12–T15] enable future calls beyond wrappers | Constrain executable introduction, symbols' effects and memory access below guest; a loader handle is not confinement; ? constructor/linker coverage |

**H/I:** broker requests bind OS-observed caller, instance, artifact generation,
epoch, operation and parameters. Guest-supplied package/PID strings cannot supply
authorization. Every reply needs an authority check, including nested Parcels,
FDs, callbacks and shared memory. A broker using its host context on arbitrary
guest-selected methods is a confused deputy even when no genuine handle leaves it.
Closing only the broker's FD or dropping a Java wrapper does not revoke copies
or already disclosed data. Hostile in-process code must not reach management
credentials, peer state or coverage decisions at all.

## 9. Failure and fallback analysis

| Reviewed path | Verified behavior (S) | PD implication (I), not a rewritten upstream guarantee |
|---|---|---|
| NewBlackbox common invocation [N8–N10] | Missing/disabled hooks use original; raw Binder transact forwards | Mandatory default must deny; replacing method lists alone leaves raw route |
| NewBlackbox package/service miss [N5–N6] | Real resolution/binding can follow virtual miss or error | Positive genuine fallback; requires adapter redesign |
| NewBlackbox bootstrap/providers [N4] | Application retries then fatal failure; provider exceptions can be ignored; host-context helpers also exist | Some local fail-stop behavior, no global fail-closed claim; distinguish helper reachability from active path |
| NewBlackbox IO/property/WebView [N11–N16] | Partial setup tolerated, unknown properties delegated; some proxy injection exits early | Missing hooks cannot be treated as complete mediation; protected admission must block |
| NewBlackbox jobs [N17] | Virtual scheduling failure can try system/identity compatibility paths; selected errors return failure | Mixed failure behavior; retries cannot leave policy boundary |
| NEXTVM AM/PM [T5–T6,T8] | Original fallthrough, swallowed SecurityException, retries and null returns | Known unsafe mandatory fallback; swallowing a denial does not grant OS permission but hides failure |
| NEXTVM accounts/GMS [T7,T11] | Host-account query on virtual miss; real GMS Binder tunnel | Positive forbidden-host-fallback design; not merely an unknown isolation claim |
| NEXTVM native init [T12–T14] | Java-only continuation and native success return despite failed installation | Success-shaped return cannot establish native protection |
| NEXTVM jobs/resources/splits [T7,T16–T17] | Some scheduling failures return success; resource compatibility fallbacks; original split path retained on copy error | Requires immutable artifacts, honest failure reporting and no wider-authority retry |
| Renjana launch [S1] | Failed stub route can launch directly without isolation | Explicitly disallowed for Protected Mode; not salvageable by a warning label |

**H:** failure of setup, broker, policy, routing, artifact identity or mandatory
coverage must deny before dispatch. Recovery is bounded and begins a fresh
admission/session generation. No fallback to host behavior, no silently disabled
isolation, and no retry in a more authoritative process is permitted.

## 10. Mandatory AG-1C direct-loader trace

| Step | Observation / bypass analysis | Required lower action and evidence category |
|---|---|---|
| 1. Admitted guest executes | R: AG-1B/C READY and initial execution; S: references supply launch/load semantics | H: all mandatory authority installed before even constructor/attach/provider/native code; ? no new boundary identified |
| 2. Guest obtains or creates previously unadmitted DEX | R: AG-1C received separate DEX; I: guest computation can also manufacture bytes | H: immutable content admission before use; blocking downloads alone is insufficient; generated route not newly measured |
| 3. Guest directly invokes `InMemoryDexClassLoader` | R: constructor succeeded without PD helper; P: public buffer loader [P2]. S/I: component routing, path hooks and reviewed loader adaptations are not a mandatory admission check | H: independently owned loader/execution authority must deny or classify before use; ? ordinary-app mechanism not established |
| 4. Class resolves | R: independently observed milestone; I: no Activity/Service/PM request is inherently required | H: earlier authoritative gate must already have acted; Android DEX validity is not PD authorization |
| 5. Static initializer runs | R: fixed initialized marker observed; I: already guest executable behavior | H: too late to call a later detection pre-code protection; must have blocked/classified first |
| 6. Entry runs | R: fixed entry result observed, no secondary helper grant | R/I: PD-REQ-091 failed on this tested path; ordinary teardown is not discovery-triggered safe revocation |

**S/I:** NewBlackbox's file-loader readonly hook/name filtering and NEXTVM's
startup `PathClassLoader` do not establish observation or denial of this direct
memory-loader sequence [N14–N15; T15]. No mandatory memory-loader admission
mechanism was found in the reviewed paths. **Unknown:** exact runtime outcome
if the old fixture were run inside either upstream pin; no such experiment was
performed. This Unknown must not downgrade the proven historical PD bypass.

**H/I:** a lower boundary that merely limits what the new code can do may reduce
consequences, but cannot silently leave previously unadmitted or opaque execution
Protected. PD-REQ-091 still needs a before-use block/classification, plus safe
termination/revocation on positive known-unsafe discovery. Merely denying creation
of executable native pages may not stop ART interpreting DEX in readable memory.
The content/loader decision, guest inability to alter it, race-free immutable
bytes, and all relevant execution modes must be established separately. No
Candidate 4 syscall-number filter is presumed to answer this question.

## 11. Native/direct-syscall trace

1. **S/I:** guest native code can be loaded through the references' native search
   and JNI machinery [N4,N12–N15; T9,T12–T15]; full device success is not asserted.
2. **I:** once executing, guest-controlled instructions can request a kernel
   operation without entering Java hooks, Binder Java proxies, libc/PLT hooks or
   controlled path wrappers. No bypass implementation is needed for this
   structural distinction.
3. **I/R:** Android's existing UID, SELinux and platform restrictions still decide
   the operation. Raw syscalls bypass function interception, not kernel policy.
   Candidate 1 already records why that baseline is insufficient for PD.
4. **H:** a stronger owner would have to deny/control actual file/device/Binder,
   memory/executable, property, socket and process effects, including inherited
   objects and threads/children, without trusting guest-side hook state.
5. **?:** no separate permitted owner was established here. Dobby, GOT/PLT,
   ByteHook or ShadowHook cannot be credited with raw-syscall confinement.
   Candidate 4 remains the separate, unperformed ordinary-app-access question.

**I:** a previously mapped genuine value can be read without a new syscall;
even perfect interception of future opens cannot retroactively remove it.
Native code sharing runtime memory can also target hook tables/original pointers.
Pre-entry state and independently protected broker authority therefore matter
alongside syscall coverage. Complete arbitrary-native containment remains Unknown.

## 12. Android-semantics matrix

All entries describe **S** organization plus **I/H** authority consequences;
the last column is explicitly **Unknown**, never a compatibility or protection
pass. Source groups refer to sections 4–5 and the ledger.

| Surface | Useful reviewed semantics | Authority required | Host/genuine dependency | Bypass/fallback risk | Lower-boundary dependency | Remaining Unknown |
|---|---|---|---|---|---|---|
| `Application` | N4/T9 construction, attach and callbacks | Class loading and startup | ART/LoadedApk/context | Code before `onCreate`; retries | Before-constructor/attach gate | All early paths |
| Activities | N1–N6/T1–T3 stubs and intent/class routing | Window/task/lifecycle | Host-declared Activity and framework | Host callbacks or raw AM | UI endpoint cannot run guest outside boundary | Usable safe UI bridge |
| Services | N3,N6/T1,T6 routing and virtual records | Bind/start/callback | Host Service/AM | Genuine binding and returned Binder | Bound every callback/handle | Full service identity/lifecycle |
| Providers | N3–N6/T9–T10 bootstrap and authority maps | Data/grants/FDs | Resolver/provider framework | Raw holder, FD or ignored init failure | URI and returned-capability control | Cursor/FD/callback closure |
| BroadcastReceivers | N6/T10 registered/static delivery models | Registration and asynchronous callbacks | Host broadcasts/AM | Genuine registration/host data | Re-admit delivery, filter payloads | All system/sticky/death paths |
| Jobs | N17/T7 job mapping/tracking | System scheduling | Host scheduler/stubs | Original retry or synthetic success | Schedule and dispatch under same epoch | Reliable job delivery/revocation |
| Alarms | N8/T7 proxy organization/tracking | Timers/PendingIntent | Genuine alarm service | Unhandled calls and stale tokens | Broker-owned tokens, revalidation | Death/reboot race coverage |
| Resources | N19/T17 asset/resource construction | File/mapping/config access | Host Resources/AssetManager | Genuine configuration/base fallback | Approved immutable inputs | OEM resource/overlay behavior |
| Base/splits | N19/T15–T17 base and split inventory/loading | Exact artifact set | Host/original APK paths | Split-copy fallback | Generation-bound immutable set | Complete signed split closure |
| Multidex | T15 base/split classpath; N4 LoadedApk | All executable DEX | Platform loader | Later loader outside initial set | Before-use executable decision | Full loader graph/native-created DEX |
| Secondary processes | N2/T3 slot and process maps | OS credentials/inheritance | Host process pool | Logical IDs do not constrain children | Same mandatory policy before child code | Arbitrary fork/exec/cleanup |
| WebView | N4,N16 suffix; T17 resources | Provider/renderer/network/storage | Genuine WebView implementation | Provider and SDK extra paths | Every process/helper/handle bounded | Renderer routes/state/lifecycle |
| SDK initialization | N4/T9 provider/Application order | Code and service access | Framework, GMS and native libraries | Early/transitive initialization | All-code pre-entry and content gate | Full SDK path coverage |
| GMS | N8/T11 proxy/bridge organization | Host service/account/session | Genuine host GMS | T7 host accounts and raw replies | Narrow explicit policy; no broad bridge | Safe useful GMS subset |
| Native initialization | N12–N15/T9,T12–T15 JNI/linker setup | Machine execution and mappings | ART/linker/system libraries | Constructor/raw syscall/hook tamper | Below-guest executable/operation control | API/ABI/OEM coverage |
| Storage | N11/T4 per-instance path APIs | Actual file objects/rights | Host-owned directories | Raw open/FD/mmap bypass | Kernel/object-level isolation | Cross-instance persistent state |
| Package visibility | N5/T5 virtual metadata | PM queries/intent resolution | Original PM retained | Host miss fallback | Virtual universe, genuine queries denied | OEM/alternate query closure |
| Binder/services | N8–N10/T5–T8 proxies | Transactions/returned handles | Original services/cache | Raw transact/base reference | Handle plus transaction enforcement | Complete transitive service graph |
| Background work | N3,N17/T7,T9–T10 lifecycle models | Re-entry/scheduling/process | Android scheduling/GMS/SDK | Restart without fresh mandatory setup | Revalidate every route, bound descendants | Push/reboot/supervisor-death coverage |

## 13. Lower-boundary interface and setup requirements

**H:** these are necessary interface guarantees, not an implementation proposal
or a claim that a particular Android/Linux mechanism can supply them.

| Required guarantee | What semantics can own itself | What requires an independent authority owner | Evidence state |
|---|---|---|---|
| No direct genuine Build/property access | Generate coherent virtual values | Prevent genuine fields/pages/alternate APIs being accessible before and after entry | ? not established; R Candidate 1 exposure |
| No unrestricted genuine Binder/system-service handles | Define virtual methods/metadata | Deny discovery, inherited/raw handles and forbidden transactions/deputies | ? no complete owner; S positive forwarding |
| No direct filesystem/storage bypass | Translate names and organize virtual directories | Enforce actual object access, FD use, mappings and peer/manager separation | ? path hooks insufficient |
| PD-REQ-091 executable admission | Inventory artifacts and calculate decisions | Make before-use decision unavoidable across ART, JNI, native and dynamic paths | R AG-1C failed; ? replacement |
| Raw native/syscall containment | Implement cooperative wrappers | Deny forbidden effects regardless of instruction origin | ? Candidate 4 dependency unperformed |
| No unmediated traffic | Model virtual network state and broker requests | Constrain all egress producers/handles and required external-VPN routes | ? network Known Gaps retained |
| Children/secondary processes inherit policy | Track logical component/process identity | Apply restrictions before any child code and prevent relaxation/escape | ? no hostile descendant proof |
| Safe raw FD/Binder/socket transfer | Return copied values, narrow tokens; validate schemas | Restrict transferable object rights and future use, including duplication | ? complete capability graph |
| Protection before providers/Application/native init | Order virtual startup callbacks | Establish restrictions before constructors, attach, loading or host-executed guest callbacks | ? no reference mandatory gate |
| No genuine fallback on runtime failure | Implement denial and bounded recovery | Ensure bypassing or corrupting local error logic cannot gain authority | S incompatible upstream fallbacks; H redesign |
| Revocation/management isolation | Maintain epochs and reject new broker calls | Protect policy/secrets; terminate/revoke all holders despite hostile code | ? safe teardown and race coverage |

**H — ordering:** external supervisor establishes artifact generation and all
mandatory prerequisites; the lower owner constrains the compartment and removes
forbidden inherited state; trusted bootstrap creates only approved virtual
objects; only then may any guest loading/initialization occur. Every background
entry, child and restart repeats applicable validation. Unknown setup denies
entry. Supervisor/broker death stops new authority and requires independently
enforced safe teardown; a death callback in guest memory is insufficient.

**H/? — supported-configuration hypothesis:** any future review must identify an
ordinary-app-accessible mechanism on the actual API/OEM/ABI combination before
claiming support. The unchanged [platform matrix](../platform-support.md) is
API 31–37 investigation, ARM64 physical non-rooted/release-equivalent primary,
x86_64 emulator engineering only, with Google/AOSP, Samsung and another OEM
requiring separate evidence. No supported combination or execution class is
established here. An observable missing mechanism, unresolved hook/hidden-API
dependency, incomplete artifact set, forbidden handle, or unverified route
blocks admission; static absence of dangerous imports is not an enforced exclusion.

No root, privileged install, production ADB, patched OS, routine rewrite/re-sign,
full guest Android for convenience, or PD `VpnService` is proposed. External VPN
remains required by default; unverifiable routing blocks traffic, never physical
fallback. These constraints are obligations, not evidence that this design works.

## 14. Required authority matrix

| Boundary | Enforcing mechanism | Owner | Guest bypass analysis | Evidence/category/scope | Remaining Unknowns |
|---|---|---|---|---|---|
| Executable-code authority | Initial loader/admission semantics; mandatory gate absent | ART/Android for execution; hypothetical lower owner for PD decision | Direct memory loader avoids component/helper path | R AG-1C positive; S N14–N15/T15; H section 10 | Before-use gate and immutable bytes |
| Framework/Java | Proxy/wrapper value substitution | Guest-side runtime; real framework underneath | Base objects, caches, reflection/native memory | S N4–N10/T4–T8; I direct authority remains | Complete framework/Build denial |
| Binder/services/providers | Selected proxies and virtual maps; Android remote checks | Runtime plus Android; independent PD owner missing | Raw transact, original handles, callbacks | S N6,N9–N10/T6–T11 | Transitive capability/transaction closure |
| Native/JNI | Selected JNI/Dobby/GOT hooks | In-process reference runtime | Guest machine code need not use hooks | S N12–N15/T12–T15; I | Mandatory native operation/content control |
| Direct syscalls | Android baseline only; no stronger mechanism identified | Android/kernel; candidate owner Unknown | Bypasses user-space interception, not kernel denials | R Candidate 1; I section 11 | Ordinary-app mechanism; Candidate 4 unperformed |
| Filesystem | Path translation/JNI/PLT wrappers | Runtime; kernel retains actual access decision | Direct open/relative paths/FDs/mappings | S N11–N15/T4,T12 | Complete namespace/object control |
| `/proc` | Selected redirected/synthetic observations | Runtime above actual procfs | Alternate calls, paths, own mappings | S N11/T12; R Candidate 1 | Complete forbidden-observation denial |
| `/sys` | Selected path/interception machinery, no complete denial | Runtime/Android baseline | Direct permitted kernel-file reads | S T12; R Candidate 1; I | Actual API/OEM file policy and mediation |
| Properties | Selected property hook/value tables | In-process runtime; genuine property system | Other APIs, original pointer, cached/mapped values | S N13/T12; R Candidate 1 | All paths and preloaded-state removal |
| Networking | Connectivity proxies; external-VPN obligation; no new egress boundary | Host Android/VPN and prospective broker | Direct sockets, Network objects, native/SDK/deputies | S N1,N18/T1,T7; R prior network gaps | All producers, routes, inheritance and revocation |
| Storage | Virtual directories/context APIs | Host UID storage plus runtime | Same-UID direct paths/FDs bypass logical instance IDs | S N11,N19/T4; I | Manager/peer isolation and persistent-state proof |
| Lifecycle/components | Stub routing and virtual callback managers | Runtime plus genuine Android scheduling | Early callbacks, secondary/background entry or genuine fallback | S N1–N6,N17/T1–T10 | Pre-code gate and useful safe semantics |
| Management isolation | Logical instance maps; hypothetical external supervisor | References share host-app identity; future lower owner Unknown | Host context/deputies or same-authority storage expose state | S N2,N7/T3–T4; H section 8 | Independently protected secrets/policy/peers |
| Dynamic code | Startup classpath/readonly compatibility only | Runtime/ART; no mandatory PD content owner | New DEX/JNI/executable memory outside initial inventory | R AG-1C; S N14–N15/T15 | All later introduction and safe stop |
| Third-party TCB | No integration; future exact-source/audit gate | PD governance and platform vendors | Opaque hooks/core cannot be trusted by reference status | S N20/S4–S5/T14; R catalog | Source completeness, provenance, maintainability/integration decision |

## 15. Third-party TCB and provenance implications

**I:** copying the architecture would potentially put bootstrap, class/resource
loaders, package parsers, stubs/dispatchers, original-service adapters, native
hooks/JNI, Binder marshalling, storage translators, GMS/account bridges, process
supervision and every authority-bearing broker into the security-relevant review scope.
Guest-side helpers must not be the final hostile-native authority owner. Bugs in
an external adapter can enlarge authority even if lower direct syscalls are denied.

**S — N20:** NewBlackbox's `Android.mk` links prebuilt Dobby static archives
for ARM64/ARMv7 into `blackbox`, plus xDL; the property hook depends on Dobby.
The tree also contains JAR assets and app AARs. A root license does not establish
source-to-binary correspondence, inherited grants, or transitive provenance.
Its `RuntimeHook.cpp` definition is not listed in the reviewed explicit native
source list, so it is not credited as an active library-load barrier.

**S — T14:** NEXTVM's reviewed hook build compiles `native-hook.cpp` into
`nextvm-native` and links Android log/dl with the configured NDK/C++ runtime.
The retrieved tree's extension inventory found no bundled `.so`, `.a` or `.aar`;
the Gradle wrapper JAR is not a runtime containment component. This is a bounded
tree observation, not a full dependency/SBOM/license/source-correspondence audit.
Toolchain, downloaded/transitive dependencies, hidden-API helpers, GMS reliance
and generated native outputs still require separate review.

**S/R — S2,S4–S5:** Renjana/Pine's ART/native and transitive dependencies need
their own provenance review; Binderceptor's opaque core remains unresolved.
No opaque security-critical binary may enter the proposed PD TCB. No reference
is selected, audit-cleared or integration-approved here. PD-REQ-094's separate
explicit integration decision remains mandatory even for selective source reuse.

## 16. Requirement traceability

The unchanged [requirements](../requirements.md), [threat model](../threat-model.md),
and [acceptance criteria](../acceptance-criteria-1.0.md) govern all consequences.
No row is a satisfaction statement.

| Requirement | Research consequence (R/S/I/H/?) |
|---|---|
| PD-REQ-021 | Mandatory Unknown prevents Protected execution; positive host fallbacks require redesign and denial, not a compatibility warning |
| PD-REQ-081 | WebView, SDK, GMS, JNI, dynamic and alternate paths remain independently unproven; framework proxy coverage cannot be inherited |
| PD-REQ-083 | No acceptance pass, physical/release matrix or independent security review; source research alone supplies none |
| PD-REQ-087 | No Protected-eligible class established; native/opaque/dynamic behavior cannot be presumed safe |
| PD-REQ-090 | Historical AG-1C bypass retains affected-path hard stop without Experimental override; not a universal classification of all apps |
| PD-REQ-091 | Before-use executable decision and safe discovery-triggered termination remain missing; constrained consequences alone do not admit DEX |
| PD-REQ-093 | Claims, source, inference, hypothesis and Unknown are separated; no hook-count, static absence or numerical safety score is protection |
| PD-REQ-094 | Exact pins inform design only; Dobby/Pine/native dependencies and opaque Binderceptor core remain subject to separate audit/integration gate |
| PD-REQ-095 | No product UI is implemented; compatibility/Experimental evidence cannot count as Protected evidence |
| PD-REQ-002, 006–010, 080 | No privilege, production ADB, routine artifact rewriting, convenience guest OS or engine selection repairs the missing boundary |
| PD-REQ-011–016, 027, 041, 044–046 | Genuine capabilities, same-host UID, early callbacks, native/secondary/background paths, stale handles and revocation require independent ownership |
| PD-REQ-026, 029–031, 036–037, 065, 071, 073, 076 | Controlled Real is not passthrough; no host GPS/account/data fallback, persona-implied sharing, or synthetic descriptor as real runtime identity |
| PD-REQ-003, 032–034, 058, 070, 092 | PD VPN prohibited; required external VPN defaults ON; all producers/routes need independent evidence and fail closed |
| PD-REQ-038, 040, 050–051, 086, 089 | Split-copy/original-path behavior needs immutable validated generations and atomic lifecycle; updates cannot inherit admission/consent |
| PD-REQ-019–020, 057, 059–060, 078, 082, 084–085, 088 | Scoped evidence, redacted diagnostics, future review, roadmap gates and classification stay mandatory; no private data or experimental override is authorized |

## 17. Remaining Unknowns

* A concrete permitted lower mechanism that controls genuine framework/property
  state, native operations and all inherited/transferred authority simultaneously.
  Candidate 4's availability/outcome is not assumed.
* An unavoidable executable admission/classification decision for memory DEX,
  interpreted/JIT code, native loading and generated code, with immutable input
  identity and safe revocation. AG-1C's tested failure itself is already known.
* Whether safe context/Binder/UI/GMS/resource adapters can retain useful Android
  application semantics after all host fallback and unrestricted handles are removed.
* Complete call/reply/callback/FD/mapping/Network graph, broker confused-deputy
  resistance, peer/management separation and hostile descendant termination.
* Early Application/constructor/provider/SDK/native setup and every restart,
  background, push, job, alarm, renderer and child entry under the same boundary.
* API 31–37/ART/OEM/ABI/release portability, hidden API behavior, full split/resource
  lifecycle, all-producer external-VPN routing and independent evidence.
* Full source/license/provenance/dependency/native-binary audits and future
  integration decisions. No existing catalog disposition changes.

## 18. Potential future falsification experiment — NOT AUTHORIZED

**H:** only after separate research identifies a concrete permitted authority
owner could a bounded prototype review be meaningful. Its first question would
be whether that owner remains mandatory with a semantics adapter present, not
whether a popular app launches. No experiment is authorized or implemented here.

That later review would require controlled synthetic fixtures for direct memory
DEX (separate construction/resolution/initialization/entry observations), native
operations bypassing wrappers, raw/cached Binder, transferred/duplicated FDs,
property/cache reads, background/child initialization and broker death/revocation.
Independent observations would include kernel-observed UID/process authority,
persistent sentinel state, external packet evidence and pre-entry ordering;
runtime success logs alone would not suffice. Any unadmitted before-decision
execution, genuine mandatory-state exposure, host fallback, external-route leak
or surviving forbidden capability would falsify the proposed guarantee and stop
the experiment. A denied operation would need a valid positive control and
attribution to the named mandatory owner, not an incidental compatibility crash.

## 19. Candidate disposition

**Inference:** unchanged NewBlackbox/NEXTVM structures expose genuine authority
and compatibility fallback and cannot be accepted as PD's boundary. Nevertheless,
logical component/package/resource semantics need not inherently possess the
same host authority as their current adapters. The two-layer decomposition is a
conditional design hypothesis, with no concrete permitted lower owner or proven
safe adapter identified. The stronger rejection would overstate the evidence
about all possible separations; prototype candidacy would overstate feasibility.

This candidate record does not relax the charter's rejection of a design that
actually has no lower boundary: neither reference is proposed to run unchanged.
It records the specific unresolved stronger-boundary dependency under Candidate
2's authorized disposition rules. Unknown blocks Protected execution and is not
success. Candidates 3–5 remain pending, the overall redesign outcome is undecided,
and neither a prototype nor production PR 6 is authorized.

**UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED**

## 20. Source ledger

All external accesses were on 2026-09-30. Exact source links below are immutable
GitHub paths at the section 3 pins. The five source archives were inspected
without executing upstream code. Official documentation is a live reference;
historical platform/experiment evidence remains scoped in Candidate 1 and AG-1C.

| ID / repository at exact section 3 pin | Inspected path(s) | Relevant symbols / scope |
|---|---|---|
| N1 — ALEX5402/NewBlackbox | [Bcore/src/main/AndroidManifest.xml](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/AndroidManifest.xml) | Host process declarations, INTERNET, ProxyVpnService |
| N2 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyManifest.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyManifest.java); [Bcore/src/main/java/top/niunaijun/blackbox/core/system/BProcessManagerService.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/core/system/BProcessManagerService.java) | FREE_COUNT, getProcessName, startProcessLocked, initAppProcessL, onProcessDie |
| N3 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyActivity.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyActivity.java); [Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyService.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyService.java); [Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyContentProvider.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyContentProvider.java); [Bcore/src/main/java/top/niunaijun/blackbox/fake/service/HCallbackProxy.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/service/HCallbackProxy.java) | onCreate, onBind, call, handleLaunchActivity, handleCreateService |
| N4 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/app/BActivityThread.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/app/BActivityThread.java) | handleBindApplication, createPackageContext, createMinimalPackageContext, createWrappedBaseContext, installProviders, createJobService |
| N5 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/service/IPackageManagerProxy.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/service/IPackageManagerProxy.java) | ResolveIntent, ResolveService, GetPackageInfo |
| N6 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/service/IActivityManagerProxy.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/service/IActivityManagerProxy.java) | GetContentProvider, BindServiceCommon, BroadcastIntent, receiver and IntentSender hooks |
| N7 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/BlackBoxCore.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/BlackBoxCore.java) | getContext, host UID and context initialization |
| N8 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/hook/HookManager.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/hook/HookManager.java) | init, injectAll, registered service hooks and error handling |
| N9 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/hook/ClassInvocationStub.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/hook/ClassInvocationStub.java) | injectHook, invoke, missing/disabled hook behavior |
| N10 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/hook/BinderInvocationStub.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/hook/BinderInvocationStub.java) | queryLocalInterface, transact, replaceSystemService |
| N11 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/core/IOCore.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/core/IOCore.java) | enableRedirect, redirectPath, proc |
| N12 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/core/NativeCore.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/core/NativeCore.java); [Bcore/src/main/cpp/BoxCore.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/BoxCore.cpp); [Bcore/src/main/cpp/Hook/BinderHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Hook/BinderHook.cpp); [Bcore/src/main/cpp/JniHook/JniHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/JniHook/JniHook.cpp) | Native initialization, JNI replacement, getCallingUid |
| N13 — ALEX5402/NewBlackbox | [Bcore/src/main/cpp/Utils/VirtualSpoof.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Utils/VirtualSpoof.cpp); [Bcore/src/main/cpp/Hook/FileSystemHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Hook/FileSystemHook.cpp); [Bcore/src/main/cpp/Hook/UnixFileSystemHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Hook/UnixFileSystemHook.cpp) | Property constructor/Dobby hook, FileSystemHook.init, selected JNI path hooks |
| N14 — ALEX5402/NewBlackbox | [Bcore/src/main/cpp/Hook/DexFileHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Hook/DexFileHook.cpp); [Bcore/src/main/cpp/Hook/VMClassLoaderHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Hook/VMClassLoaderHook.cpp); [Bcore/src/main/cpp/Hook/RuntimeHook.cpp](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Hook/RuntimeHook.cpp) | openDexFileNative, setFileReadonly, findLoadedClass; nativeLoad definition not credited as active build path |
| N15 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/service/ClassLoaderProxy.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/service/ClassLoaderProxy.java) | getWho, initializeFallbackClassLoaders, LoadClass |
| N16 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/service/WebViewProxy.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/service/WebViewProxy.java) | getWho and annotated compatibility handlers; N4 is the active suffix call |
| N17 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/fake/service/IJobServiceProxy.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/fake/service/IJobServiceProxy.java) | Schedule, scheduleWithUIDSpoofing and genuine retries |
| N18 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyVpnService.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/proxy/ProxyVpnService.java) | VpnService superclass, onStartCommand, establishVpn |
| N19 — ALEX5402/NewBlackbox | [Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/PackageManagerCompat.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/PackageManagerCompat.java); [Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/installer/CopyExecutor.java](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/java/top/niunaijun/blackbox/core/system/pm/installer/CopyExecutor.java) | baseCodePath resource handling, native/base artifact copy |
| N20 — ALEX5402/NewBlackbox | [Bcore/src/main/cpp/Android.mk](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Android.mk); [Bcore/src/main/cpp/Dobby/arm64-v8a/libdobby.a](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Dobby/arm64-v8a/libdobby.a); [Bcore/src/main/cpp/Dobby/armeabi-v7a/libdobby.a](https://github.com/ALEX5402/NewBlackbox/blob/89b59836c66f173756a4ae258cf379a957649820/Bcore/src/main/cpp/Dobby/armeabi-v7a/libdobby.a) | Prebuilt Dobby linkage and explicit native source list; archive existence only, not binary execution/audit |
| T1 — TanvirHossain2/NEXTVM | [app/src/main/AndroidManifest.xml](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/app/src/main/AndroidManifest.xml); [app/src/main/java/com/nextvm/app/stub/StubComponents.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/app/src/main/java/com/nextvm/app/stub/StubComponents.kt) | Host pN stubs, INTERNET; empty/null base stub methods |
| T2 — TanvirHossain2/NEXTVM | [core/virtualization/src/main/java/com/nextvm/core/virtualization/engine/VirtualProcessManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/virtualization/src/main/java/com/nextvm/core/virtualization/engine/VirtualProcessManager.kt); [core/virtualization/src/main/java/com/nextvm/core/virtualization/engine/StubRegistry.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/virtualization/src/main/java/com/nextvm/core/virtualization/engine/StubRegistry.kt) | Slot allocation and stub mapping |
| T3 — TanvirHossain2/NEXTVM | [core/virtualization/src/main/java/com/nextvm/core/virtualization/process/VirtualMultiProcessManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/virtualization/src/main/java/com/nextvm/core/virtualization/process/VirtualMultiProcessManager.kt) | createProcess, generated virtual PID/UID, process/loader/Application maps |
| T4 — TanvirHossain2/NEXTVM | [core/sandbox/src/main/java/com/nextvm/core/sandbox/VirtualContext.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/sandbox/src/main/java/com/nextvm/core/sandbox/VirtualContext.kt) | Storage overrides, getSystemService, getPackageManager, getApplicationContext and base delegation |
| T5 — TanvirHossain2/NEXTVM | [core/binder/src/main/java/com/nextvm/core/binder/proxy/PackageManagerProxy.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/binder/src/main/java/com/nextvm/core/binder/proxy/PackageManagerProxy.kt) | invoke, invokeOriginal, checkUidPermission and host GMS metadata |
| T6 — TanvirHossain2/NEXTVM | [core/binder/src/main/java/com/nextvm/core/binder/proxy/ActivityManagerProxy.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/binder/src/main/java/com/nextvm/core/binder/proxy/ActivityManagerProxy.kt) | invoke, genuine default, swallowed SecurityException, original retry and component routing |
| T7 — TanvirHossain2/NEXTVM | [core/binder/src/main/java/com/nextvm/core/binder/proxy/SystemServiceProxyManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/binder/src/main/java/com/nextvm/core/binder/proxy/SystemServiceProxyManager.kt) | AccountManagerProxyHandler.handleGetAccountsByType; connectivity, alarm and JobSchedulerProxyHandler paths |
| T8 — TanvirHossain2/NEXTVM | [core/binder/src/main/java/com/nextvm/core/binder/BinderProxyManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/binder/src/main/java/com/nextvm/core/binder/BinderProxyManager.kt) | installAllProxies, installActivityManagerProxy, installPackageManagerProxy |
| T9 — TanvirHossain2/NEXTVM | [core/virtualization/src/main/java/com/nextvm/core/virtualization/lifecycle/VirtualApplicationManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/virtualization/src/main/java/com/nextvm/core/virtualization/lifecycle/VirtualApplicationManager.kt) | bindApplication, createApplication, attachBaseContext, initContentProviders |
| T10 — TanvirHossain2/NEXTVM | [core/services/src/main/java/com/nextvm/core/services/component/VirtualContentProviderManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/services/src/main/java/com/nextvm/core/services/component/VirtualContentProviderManager.kt); [core/services/src/main/java/com/nextvm/core/services/component/VirtualBroadcastManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/services/src/main/java/com/nextvm/core/services/component/VirtualBroadcastManager.kt) | installProvider/openFile, authority tables, receiver registration and dispatch |
| T11 — TanvirHossain2/NEXTVM | [core/services/src/main/java/com/nextvm/core/services/gms/GmsBinderBridge.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/services/src/main/java/com/nextvm/core/services/gms/GmsBinderBridge.kt); [core/services/src/main/java/com/nextvm/core/services/gms/VirtualGmsManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/services/src/main/java/com/nextvm/core/services/gms/VirtualGmsManager.kt) | connectService, GmsBinderProxy.transact, Messenger fallback, getVirtualGoogleAccounts |
| T12 — TanvirHossain2/NEXTVM | [core/hook/src/main/cpp/native-hook.cpp](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/hook/src/main/cpp/native-hook.cpp) | g_hook_entries, install_hooks_callback, install_plt_hooks, nativeInit, property original fallback |
| T13 — TanvirHossain2/NEXTVM | [core/hook/src/main/java/com/nextvm/core/hook/NativeHookBridge.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/hook/src/main/java/com/nextvm/core/hook/NativeHookBridge.kt) | Native library loading, initialize/nativeInit failure handling and path rules |
| T14 — TanvirHossain2/NEXTVM | [core/hook/src/main/cpp/CMakeLists.txt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/hook/src/main/cpp/CMakeLists.txt); [core/hook/build.gradle.kts](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/hook/build.gradle.kts); [gradle/libs.versions.toml](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/gradle/libs.versions.toml) | Source-built nextvm-native, Android log/dl, NDK/STL and declared dependency versions |
| T15 — TanvirHossain2/NEXTVM | [core/apk/src/main/java/com/nextvm/core/apk/VirtualClassLoader.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/apk/src/main/java/com/nextvm/core/apk/VirtualClassLoader.kt) | createClassLoader, split/native paths, initSharedLibraryFields, createLinkerNamespace |
| T16 — TanvirHossain2/NEXTVM | [core/virtualization/src/main/java/com/nextvm/core/virtualization/engine/VirtualEngine.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/virtualization/src/main/java/com/nextvm/core/virtualization/engine/VirtualEngine.kt) | Initialization and installApp split-copy/original-path fallback |
| T17 — TanvirHossain2/NEXTVM | [core/virtualization/src/main/java/com/nextvm/core/virtualization/lifecycle/VirtualResourceManager.kt](https://github.com/TanvirHossain2/NEXTVM/blob/f581a6642596db396a6fe606addd735979403b6b/core/virtualization/src/main/java/com/nextvm/core/virtualization/lifecycle/VirtualResourceManager.kt) | Resource construction/configuration, ensureWebViewResources |
| S1 — josskixg/renjana | [app/src/main/java/com/fesu/renjana/core/InstanceLauncher.kt](https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/core/InstanceLauncher.kt); [app/src/main/java/com/fesu/renjana/virtual/GuestInfoCache.kt](https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/virtual/GuestInfoCache.kt) | Stub/direct launch fallback; createResourcesForApk and split asset paths |
| S2 — josskixg/renjana | [app/src/main/java/com/fesu/renjana/hooks/PineHookManager.kt](https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/src/main/java/com/fesu/renjana/hooks/PineHookManager.kt); [app/build.gradle](https://github.com/josskixg/renjana/blob/14302a57cd66114c6979acf7ca56c97f845a6841/app/build.gradle) | Pine initialize and core/xposed dependency declarations |
| S3 — obadadallo95/mirro-android-virtualization | [README.md](https://github.com/obadadallo95/mirro-android-virtualization/blob/74e6a1e3ea1898b2e2a705d7c8b3e730059023b1/README.md); [docs/FULL_VIRTUALIZATION_ARCHITECTURE_AUDIT.md](https://github.com/obadadallo95/mirro-android-virtualization/blob/74e6a1e3ea1898b2e2a705d7c8b3e730059023b1/docs/FULL_VIRTUALIZATION_ARCHITECTURE_AUDIT.md) | Upstream status and authority assessment only (U), not new PD device results |
| S4 — iofomo/binderceptor | [jni/native/jni/src/binderceptor_native.cpp](https://github.com/iofomo/binderceptor/blob/7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61/jni/native/jni/src/binderceptor_native.cpp) | Native JNI wrappers resolve binderceptor_call from opaque core |
| S5 — iofomo/binderceptor | [app/libs/arm64-v8a/libifmabinderceptor-core.so](https://github.com/iofomo/binderceptor/blob/7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61/app/libs/arm64-v8a/libifmabinderceptor-core.so); [app/libs/armeabi-v7a/libifmabinderceptor-core.so](https://github.com/iofomo/binderceptor/blob/7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61/app/libs/armeabi-v7a/libifmabinderceptor-core.so) | Bundled opaque core existence, not audited implementation |

| Official platform reference | Scope used |
|---|---|
| P1 — [Android processes and threads](https://developer.android.com/guide/components/processes-and-threads) | Manifest process selection and Android component process lifecycle; read with Candidate 1 UID evidence |
| P2 — [InMemoryDexClassLoader](https://developer.android.com/reference/dalvik/system/InMemoryDexClassLoader) | Public memory-buffer loader; no inference of PD admission |
| P3 — [ParcelFileDescriptor](https://developer.android.com/reference/android/os/ParcelFileDescriptor) | Parcelled descriptors and duplication; transfer is distinct from pathname mediation |
| P4 — [Network](https://developer.android.com/reference/android/net/Network) | Network-specific socket binding/socket factory; not automatically an unrestricted or VPN-safe grant |

**Validation scope:** 39 local documentation link targets were checked; all 66
raw-content counterparts of the pinned GitHub source URLs returned HTTP 200,
and the four official platform references were accessed. All 15 required authority
rows have populated cells;
source findings were reviewed against the listed exact snapshots. No Android tests,
builds or workflows were run for this documentation-only record. Binary invariants
remain the required wrapper blob/SHA-256 and executable gradlew mode; publication
verification is reported separately. CI pending after publication.
