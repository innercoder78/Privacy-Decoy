# Post-AG-1 comparative enforcement-boundary synthesis

**Prepared:** 2026-10-02. **Selected charter exit: C. NO CREDIBLE BOUNDARY.**
**Recommendation to Tony: STOP under the current constraints and bounded phase evidence.**
**OWNER HANDOFF REQUIRED.** This recommendation is not a new owner decision.

## 1. Scope and authority

This is the comparative synthesis required by [ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md)
and the [research charter](../post-ag1-enforcement-boundary-redesign.md). It compares
the five completed records against the charter's mandatory conditions. It does
not conduct a sixth candidate search, reopen architecture discovery, repeat the
ten-project survey, implement a prototype, or perform the separate owner handoff.

**Synthesis answer — inference:** the evidence establishes neither a concrete
credible route through every mandatory enforcement boundary nor a technically
enforceable useful narrower execution class under unchanged requirements.
Selected lower restrictions are real. Their authority does not extend to the
missing before-use executable-content decision or all genuine state already
accessible inside the runtime. Those omissions cannot be compensated by stronger
compatibility, more correct helper decisions, or successes in other rows.

PD-REQ-001..095, product scope, charter exit rules, ADR-0008, individual candidate
dispositions and historical evidence remain unchanged. No requirement or authority
row is marked satisfied. No engine, source integration, dependency, implementation,
experiment or further research phase is authorized here. AG-1 remains FAILED;
production Roadmap PR 6 remains unstarted and unauthorized.

## 2. Fixed evidence baseline

**Repository observation:** fetched main is
`23faf816df6e849cc2c14ebde6c35d5e8dfabc7e`, tree
`2b67adb40aa25a2287b32bc9edc7a7f18a73393c`, the merge of GitHub PR #29.
The initial checkout was the clean Candidate 5 branch at
`4bea385b4f18c829a9c26e340b231e15f8814bd4` with that same tree. Eight existing
stashes were preserved. A sandbox-denied fetch was retried successfully before
checking FETCH_HEAD; the stale value was not accepted. The requested new branch
is `research/post-ag1-comparative-synthesis`. Only this evidence document and the
charter's research-progress portion change.

The supplied baseline reports Android foundation run #166, event push, conclusion
success, no open PRs, and operational GitHub Status. These are user-supplied
publication context, not newly collected runtime/security evidence. No Actions
poll is needed for this synthesis. Historical observation workflows retain their
own exact heads and scopes; a green workflow does not reverse an adverse result.

Binary preflight preserves Android tree
`f9fa05e50c9151dd0580c95ff42b8eb5c6f87577`, wrapper blob
`b1b8ef56b44f16b14dc800fa8103a6d89abb526f`, wrapper SHA-256
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`, and
`android/gradlew` blob `249efbb032ce46a80c687c0723eb172e85f6a136`, mode `100755`.

### Evidence vocabulary and reconciliation

In every table, **R** means preserved repository observation/governance;
**S** means inherited source-supported mechanism at the original record's exact
pin; **U** means upstream claim only; **I** means synthesis inference; **H** means
unimplemented design hypothesis; **Unknown** means missing or insufficient
evidence. Proposed mechanisms remain H even when a constituent primitive is S.
This document adds comparative reasoning, not new platform facts or runtime results.
The ledger in section 22 resolves C1–C5 and the historical abbreviations.

AG-1C run #150 used head `55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`, API 35 Google APIs x86_64 debug.
PR4 and S1 likewise retain API 35 Google APIs x86_64 debug scope. PR5's packet
observations are bounded emulator research, including a separate external VPN
fixture; they are not physical-device/release-equivalent evidence.

C1 preserves API 31/36/37 source distinctions. C3/C4 primarily inspect
`android-17.0.0_r1`; C4 also uses `android-12.0.0_r1`, Android common kernel
`android14-6.1-2024-08_r1` and configuration sample
`android16-6.12-2025-06_r16`. No API 37 shipping-kernel identity follows from those
samples. C2's exact upstream pins and source-versus-claim distinctions remain
controlling. No fresh external source was necessary to resolve a contradiction.

Dated statements that later candidates or synthesis were pending describe their
record's publication stage. Their dispositions and observations remain intact;
this synthesis records current phase progress without rewriting history. C4's
additional syscall-denial mechanism adds bounded positive evidence beyond C1's
baseline and the earlier discovery phase. It does not retroactively change their
scope, nor support the blanket claim that ordinary apps have no lower restriction.

The canonical PR5 recommendation remains **STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**.
Tony's subsequent decision remains **REDESIGN**. S1 remains **FALSIFIED**, its
follow-up **BLOCKED**, S2 audit-cleared no engine, Blacks-BlackBox remains
**STOPPED_UNRESOLVED**, and VirtualSpace's reviewed exact pin remains disqualified.

## 3. Exact candidate dispositions

| Candidate | Fixed historical disposition |
|---|---|
| [C1 — OS/process compartment](post-ag1-candidate-1-os-process-compartment.md) | REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY |
| [C2 — controlled semantics above a lower boundary](post-ag1-candidate-2-controlled-runtime-lower-boundary.md) | UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED |
| [C3 — constrained execution classes](post-ag1-candidate-3-constrained-execution-class.md) | UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY |
| [C4 — syscall/Binder restrictions](post-ag1-candidate-4-syscall-binder-boundary.md) | UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED |
| [C5 — hybrid architecture](post-ag1-candidate-5-hybrid-architecture.md) | REJECTED — HYBRID DOES NOT CLOSE MANDATORY AUTHORITY |

C2's possible separation of semantics from authority, C3's possible enforced
class, and C4's partial lower mechanisms remain open questions in their original
scope. A phase-level absence of a qualifying architecture does not change those
records to rejected, passed, or universally impossible.

## 4. Comparative candidate matrix

The comparison is split into aligned tables to keep each question readable.
The first table repeats the fixed dispositions beside the authority comparison;
the second completes the boundary and readiness comparison. There is no ranking, vote, weight, numerical score or safety score.

| Candidate | Exact disposition | Proposed authority owner | Strongest positive evidence | Decisive negative evidence | Mandatory Known bypasses | Mandatory Unknowns |
|---|---|---|---|---|---|---|
| C1 | REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY | S: Android UID/DAC/SELinux/platform sandbox; PD owns narrow broker decisions | R PR4 distinct Binder-observed UID/PID, selected sentinel non-access and broker authorization; S isolated restrictions | R AG-1C inside an isolated service; PR4/S1 genuine-state exposure; S service flags do not supply content/Persona control | Historical direct loader; genuine Build/Context and selected proc/sys/property exposure in scoped prior probes | Complete Binder/FD graph, arbitrary native containment, descendants/revocation, useful imported-app lifecycle, OEM and VPN coverage |
| C2 | UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED | H: independent lower owner beneath semantics runtime; not identified. Reviewed adapters retain Android/host authority | S exact-pin component/package/resource organization; I logical semantics could be separated from unsafe adapters | S genuine Binder/context/account fallback and bypassable hooks in reviewed paths; no independent content/state owner | R AG-1C remains untreated; S raw transact/original-service and host-account fallback paths, not newly executed attacks | Safe redesigned adapters, lower authority, transitive callbacks/FDs, useful semantics, routing and teardown |
| C3 | UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY | PD importer owns initial rejection; H runtime exclusion owner/verifier absent | R bounded artifact inventory; H useful fixed-artifact foreground utility; S process-local WebView disable has limited effect | I negative APK scan is not behavioral membership; ordinary ART retains public loaders and genuine framework state | R direct-loader bypass remains despite no-new-code intent; R genuine Build unaffected by packaging restrictions | Sound closed-world membership, native/executable exclusions, all-entry closure, safe UI/storage/SDK subset |
| C4 | UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED | S kernel enforces PD-installed additional seccomp policy; Android owns baseline services; H outside broker | S Android-specific no-new-privileges/filter path, TSYNC and inheritance, hard denial below raw instructions | S metadata filter lacks content/path/Parcel semantics; I existing code/caches and interpreted DEX not governed by content admission | R historical loader and genuine-state bypasses untreated; no new filtered-runtime bypass experiment claimed | Complete ABI policy, quiescence, notification transfer/memory/races, device access, useful Binder-free semantics, termination/VPN |
| C5 | REJECTED — HYBRID DOES NOT CLOSE MANDATORY AUTHORITY | H outside manager/policy/storage/component/network brokers plus isolated ART worker; S kernel deny floor | I concrete trust partition and copied-data protocol clarify composition; inherits bounded R/S positives | I composition leaves direct ART content authority and cached genuine state outside mandatory PD decisions | R AG-1C Known path untreated by retained ART design; genuine-state problem survives a future-syscall-only policy | Pre-entry object/pending-work closure, pipe identity, useful Android adapters, native exclusion, death/revocation, all-producer no-fallback |

| Candidate | AG-1C status | Native/direct-syscall status | Binder status | Persona/genuine-state status | Networking status | Android semantics/useful class | Prototype readiness and A/B conclusion |
|---|---|---|---|---|---|---|---|
| C1 | Known historical bypass; no new before-use gate | Kernel baseline real; selected direct native observations remain; complete containment Unknown | Restricted discovery and service checks; inherited/returned genuine handles remain | Genuine Build/property state not replaced by UID isolation | Selected direct socket creation denied; received sockets/deputies and PR5 gaps remain | PD isolated-service lifecycle is not imported Application/Activity/provider support | No. A lacks executable/Persona owners; B has no enforced useful class |
| C2 | No unavoidable gate in reviewed loader/helper paths; upstream exact runtime outcome untested | Hooks bypassable by raw instructions; stronger lower owner absent | Selected proxies retain raw/genuine fallthrough; safe redesigned graph Unknown | Value facade does not own base objects/caches | Connectivity views/GMS bridge do not own packets; PD VpnService path prohibited | Rich source organization; safe retained semantics above independent authority Unknown | No. A depends on unnamed owner; B needs enforceable exclusions, not adapter optimism |
| C3 | Fixed-set declaration does not prevent historical direct route | No packaged ELF does not exclude platform native effects or later app-native content | One process/managed language does not remove genuine service authority | Even a local utility can read genuine Build | Foreground/no-browser intent is not all-producer enforcement | C3-A–F assessed; C3-E useful in principle, C3-F lacks Android app semantics | No. B membership and runtime exclusions unestablished; A missing same authorities |
| C4 | Seccomp effect denial is not executable-content admission | Concrete owner for selected denied entries; no complete arbitrary-native policy | Hard new-ioctl cutoff possible; selective Parcel/object policy absent, pending work unresolved | Reads of cached/mapped state may need no syscall | Can deny selected direct calls; broker VPN-loss and fallback not solved | Hard Binder/open/mapping denial may remove required Android behavior | No complete architecture. A/B lack content/state/class closure despite testable primitive |
| C5 | Earliest unavoidable PD decision for new bytes: none established | Stronger deny-floor H; existing code/memory, allowed effects and deputies remain | Proposed complete direct cutoff plus copied pipes; no proven drain or safe full adapters | Outside Persona owner supplies values but cannot own all direct cached reads | Outside broker remains separate producer; inherits PR5 Known Gaps | Desired C3-E utility still lacks enforceable no-new-code and safe UI/storage | No. Neither A nor B follows from composition; C5 rejection alone is not the phase decision |

## 5. Cross-candidate 15-row authority synthesis

Exactly the charter's established rows follow. “Permitted” credits only the named
scoped primitive, not an implemented configuration. “Retained” describes what a
useful Android class needs; an intended exclusion has no reliably-excluded status
without technical enforcement. No complete mandatory row is credibly closed.

| Boundary | Strongest evidence across C1–C5 | Strongest negative evidence | Concrete authority owner, if any | Permitted under constraints? | Hostile bypass state | Useful class retains surface? | Final synthesis classification |
|---|---|---|---|---|---|---|---|
| Executable-code authority | R AG-1A/B/C initial identity and correct helper grants | R AG-1C direct construction/resolution/init/entry; I C4/C5 add no content predicate | PD owns initial/helper grants; no unavoidable later PD owner identified; ART executes | Initial mechanism yes; replacement owner absent | Known historical bypass untreated; replacement Unknown | Yes, admitted app code plus prevention/classification of additions | Known bypass; no permitted owner identified for mandatory before-use gate |
| Framework/Java | S C2 component/metadata organization; H controlled facade | R PR4/S1 genuine Build; S C2 base-object/fallback paths | Android/ART own direct state; PD only its facade | Facade yes; mandatory control of all reads unestablished | Known genuine-state exposure; complete replacement unresolved | Yes, even minimal UI utility needs runtime/framework behavior | Known bypass; no complete state owner identified |
| Binder/services/providers | S C4 hard syscall-entry denial; R PR4 narrow authenticated endpoints | S inherited handles, opaque WRITE_READ payload, C2 forwarding; pending transactions | Kernel for selected denial; Android remote checks; PD for own endpoint | Scoped denial/endpoint yes; custom SELinux policy prohibited | New matching denial cannot be skipped; full graph unresolved | Useful app needs service effects via safe adapter or enforced exclusion | Concrete but insufficient; Unknown semantic/capability closure |
| Native/JNI | S C4 matching kernel denials apply to raw machine instructions | R proc/sys/property access; I shared helper memory and allowed effects remain | Kernel for syscall effects; ART/linker for loading; selective content owner absent | Additional restrictions source-supported; general containment unestablished | Selected denied effects credibly constrained in source; whole row unresolved | Platform native execution remains; app-native exclusion must be enforced | Concrete but insufficient; Unknown |
| Direct syscalls | S Android-specific seccomp/TSYNC/inheritance chain | S metadata only; I uncovered ABI paths, unfiltered tasks and pending work undermine incomplete policy | Kernel enforcing PD pre-entry policy | Yes in inspected configuration, device/OEM scope Unknown | Matching hard denial not bypassed by avoiding libc; complete policy unresolved | Runtime needs allowed calls even without app-native libraries | Concrete but insufficient; Unknown complete policy |
| Filesystem | S UID/DAC/MAC plus prospective broad acquisition denial | S path pointers invisible to classic BPF; retained FD/alias/deputy authority | Android/kernel and PD's own storage broker | Baseline/additional deny yes; custom namespace/LSM usability not established | Unresolved object/descriptor closure | Resources and local state need safe access semantics | Concrete but insufficient; Unknown |
| `/proc` | R manager maps denied; S possible broader syscall acquisition deny | R PR4 self maps accessible; existing FD/copied state persists | Kernel for covered operations; no full virtual observation owner | Scoped deny yes | Known historical exposure; full future exclusion unresolved | Raw proc need not be supported, but exclusion must cover alternate access | Known bypass in baseline; Unknown complete exclusion |
| `/sys` | S baseline labels and possible broad acquisition denial | R PR4 CPU sysfs accessible | Kernel for covered file/device operations | Scoped deny yes | Known historical exposure; retained objects/OEM paths unresolved | Raw sysfs may be excluded only with enforcement | Known bypass in baseline; Unknown complete exclusion |
| Properties | H outside Persona values; S deny future backing access | R PR4 property presence/Build equality, S mapped property reads; I caches need no syscall | Android/Bionic/ART own genuine state; PD only supplied copies | Synthetic values yes; complete cache owner absent | Known genuine-state exposure; removal unresolved | Coherent descriptors or denial needed even for utility | Known bypass; no complete state owner identified |
| Networking | R PR5 scoped packet/lockdown/socket evidence; S C4 direct deny | R physical egress on VPN loss without lockdown, split/exclusion/allowBypass | Kernel for worker calls; broker for owned sockets; Android/external VPN for routes | No PD VpnService; external VPN default required; source denials permitted | Known historical gaps; no-fallback/all-producer closure unresolved | Required policy persists; networkless class needs enforced no traffic | Concrete but insufficient; Known Gaps and Unknown |
| Storage | R persistent sentinel non-access/unchanged bytes; S distinct UID | Native ENOENT not proven permission denial; raw FD grants and same-UID adapters can cross intended boundary | Android/kernel; PD broker for its objects | Yes, scoped; complete copied-storage model H | Full host/peer/management and persistent-state closure unresolved | Useful utility needs isolated local state | Concrete but insufficient; Unknown |
| Lifecycle/components | R AG-1B initial READY ordering and manager epochs; S filter inheritance | R PR4 missing imported lifecycle; S pending pre-filter work/system-created processes; no whole-tree stop | PD dispatch/session state; Android process lifecycle; no complete revocation owner established | Scoped mechanisms yes | Unresolved early/re-entry, child, stale handle and death races | Application/Activities/resources/recreation retained; exclusions must be enforced | Concrete but insufficient; Unknown |
| Management isolation | R distinct UID/PID, narrow session checks, sentinel observations | R host Context exposure; S/H broad deputies/FDs and mutable in-worker helpers | Android/kernel plus outside PD manager/brokers | Yes; same-UID broker processes collectively TCB | Bounded positives; complete hostile/deputy protection unresolved | Always mandatory | Concrete but insufficient; Unknown |
| Dynamic code | R trusted secondary helper decisions; S restrictive mapping possibilities | R direct unadmitted DEX executes; I readable interpreted code distinct from new executable pages | ART/linker; PD helper optional; content owner missing | Helper/denials yes; replacement missing | Known direct bypass; other introductions Unknown | Must prevent or classify before use, even if declared excluded | Known bypass; no permitted before-use owner identified |
| Third-party TCB | R exact-pin catalog and selective reuse/audit governance | S opaque Binderceptor core, bundled provenance gaps, genuine fallback; U claims not proof | PD integration/review governance, platform vendors; no selected engine | Reviewed ideas yes; unresolved opaque critical binaries prohibited | Future trust/dependency closure unresolved; not a new exploit result | Any indispensable component needs provenance/security review | Concrete governance but insufficient runtime evidence; Unknown |

## 6. AG-1C executable-authority synthesis

The [AG-1C observation](ag1-runtime-executable-code.md#first-exact-head-ag-1c-device-result)
is positive evidence, not merely absent coverage:

1. The admitted synthetic guest ran in an isolated service after the bounded
   initial READY sequence.
2. Separately built, previously unadmitted DEX became available through read-only
   shared memory. Hash/transport checks did not authorize those bytes on this route.
3. The guest directly used `InMemoryDexClassLoader`, without the PD helper.
4. Loader construction succeeded (`LOADER_CONSTRUCTED=1`).
5. The class resolved (`CLASS_RESOLVED=1`).
6. Static initialization occurred (`STATIC_INITIALIZED=1`).
7. The entry executed (`ENTRY_INVOKED=1`) without a secondary helper grant.

**I — collective answer:** no completed candidate supplies an unavoidable PD
decision for those bytes before use. C1's sandbox already surrounded the failed
historical path. C2's startup loaders/proxies are not compulsory later admission.
C3's declared fixed set has no runtime owner. C4 governs syscall metadata, not
DEX identity; C5 composes the same platform ART with no additional content owner.

The evidence does not claim the old fixture was run under C4/C5's unimplemented
filters. Its exact behavior there is untested. That does not convert the existing
Known bypass into Unknown or provide a mechanism treating it. Incidental failure
from denied allocation, missing ABI, invalid input or broken runtime is not
selective admission and does not establish a useful execution class.

S C3/C4 identify the readable-memory/ART interpretation distinction: denying
new native executable pages cannot by itself establish denial of bytecode consumed
by existing interpreter code. Denying downloads cannot prevent computed bytes.
Effect confinement after execution is not executable admission. A later denied
syscall, helper rejection, notification or normal teardown cannot undo already
executed initialization. The before-use obligation and safe discovery-triggered
termination/revocation remain under PD-REQ-091; the affected Known bypass retains
PD-REQ-090's hard stop without an Experimental override.

## 7. Genuine framework/cache synthesis

R S1 observed all seven mandatory Build fields equal to parent values for both
tenants at head `69f0352510a55d92dcf4a408aa524cc0532788f9` in runs #100/#101.
Its managed-profile boundary was FALSIFIED despite useful OS isolation and
normal lifecycle. R PR4 separately observed Build fingerprint equality, host
Context class exposure, selected package/activity visibility, own maps and CPU
sysfs accessibility, and property presence. Sentinel non-access did not negate
those observations.

S C1 explains why isolated credentials restrict private data without replacing
permitted genuine framework/property values. S C2 finds retained base objects
and property fallbacks; C3's packaging exclusions do not change Java-visible
Build. C4's filter acts at new syscall entry, while C5 explicitly retains the
problem of cached framework fields and mapped property areas.

**I:** no candidate owns every read that requires neither a new Binder transaction
nor a new syscall. An outside Persona broker can generate correct values while
the guest reads a genuine cached value directly. Replacing a facade or blocking
future opens does not erase a previously copied value. Complete pre-entry removal
or prevention while retaining useful runtime behavior is unestablished. This is
an independent blocker to A/B, not merely an untested OEM variant. It does not
assert every possible cache on every device was observed leaking.

## 8. Native/direct-syscall synthesis

S C4 sections 4–6 establish a concrete Android-specific self-restriction route
in the inspected configuration: no-new-privileges, additional classic seccomp,
all-thread TSYNC with exact success checking, and inheritance for permitted
future tasks. The kernel copies/enforces the filter; raw instructions cannot
evade a matching hard denial by avoiding Java, libc, PLT, ByteHook or ShadowHook.
Static hard-deny monotonicity must not be generalized to arbitrary notification
or TRACE designs. No PD installation/device observation was performed by C4.

The filter can own covered syscall entry using ABI, number and scalar metadata:
selected opens, socket operations, process creation, mapping/protection changes
and Binder ioctl entry can be denied. It cannot inspect a pathname, admitted
digest, nested Binder Parcel, enduring FD object identity or guest-versus-trusted
call provenance. Already mapped code, memory reads, userspace computation and
allowed operations remain. Native code in the worker can corrupt its helpers;
kernel filter storage and an outside manager are different trust boundaries.

S/H C4/C5 require pre-entry all-thread setup, no forbidden inherited FDs/mappings,
and closure of already-entered operations. TSYNC does not cancel a syscall that
has already entered. New system/zygote-created components do not inherit the
requesting guest's filter automatically. Filter inheritance alone does not prove
whole-descendant termination. Arbitrary-native containment, selective native
provenance/exclusion and a useful complete ABI/OEM policy remain Unknown.

No-native-package scans do not exclude platform JNI or later native introduction.
Conversely, platform native execution is not proof that arbitrary Java can invoke
every native symbol. The synthesis preserves both limits rather than inventing
a successful native attack or universal denial.

## 9. Binder/capability synthesis

S C1/C4 distinguish discovery restrictions from genuine handles inherited,
cached or received through binding, replies and callbacks. Android's remote
permission/UID/SELinux checks still apply; possession is neither unrestricted
permission nor proof of safe PD mediation. C2's reviewed genuine fallthrough
is positive source evidence of unsafe adapters, not a new runtime observation.

S C4's hard denial of covered new `BINDER_WRITE_READ` entries is a real lower
cutoff possibility. Allowing that ioctl on a supposed “broker Binder FD” does
not restrict it to one remote object or method. Classic BPF cannot parse the
transaction or its nested objects. Denial also removes ordinary PD Binder IPC
and much Android lifecycle machinery. Pending transactions, mapped receive data,
all devices/ABI encodings, duplicates and reacquisition need separate closure.

H C5 selects pre-established copied-data pipes instead of reopening general
Binder for compatibility. This avoids ancillary FD transfer on ordinary pipe
reads but still needs sealed endpoint identity, bounded parsing, no duplication/
replacement/reopening route, and trusted session binding. It is unimplemented.
A raw FD, socket, Network, provider, GMS/account object or unrestricted callback
can transfer authority past later broker checks. Closing a broker's copy does
not revoke guest copies or disclosed bytes. Guest callbacks must remain inside
the confined process; executing them in a broker would cross the boundary.

Authenticated guests still cannot choose arbitrary host paths, URIs, Binder
transactions, native loads or network operations. Those brokers would be confused
deputies even if no raw object left them. C4 notification adds unresolved listener
ownership, cross-UID memory access and pointer races; inspect-then-CONTINUE does
not freeze mutable input. No controlled semantics layer closes these obligations
without mandatory underlying authority.

## 10. Filesystem/storage synthesis

R PR4's independent manager sentinel comparison and separate credentials provide
useful bounded evidence. Native absent-path results are not rewritten as proven
permission denial. S C1/C4 show real baseline DAC/MAC restrictions and prospective
broader syscall denials. They do not supply a complete PD-selectable synthetic
filesystem, `/proc`, `/sys` or property universe.

Path rewriting is not object isolation. Classic BPF sees pointer/scalar metadata,
not path strings; a numeric dirfd does not constrain absolute paths, traversal,
symlinks or descriptor replacement. Existing FDs, aliases and mappings can outlive
path denial. H C5's copied, tenant/epoch-bound storage records reduce grants but
do not establish useful File/SQLite/mmap semantics, persistent peer isolation,
atomic update/removal, backup/transfer safety or all retained-object closure.

C4's sampled configuration does not establish enabled/usable Landlock or an
ordinary-app custom mount/user namespace. Allowlisted syscalls alone are not an
enabled kernel feature. Prohibited custom system/SELinux/privileged mechanisms
cannot repair the missing owner. No new filesystem candidate is opened here.

## 11. Networking synthesis

**No Privacy Decoy VpnService. Require VPN for protected apps = ON remains the
default. Unverifiable required VPN or route state blocks execution/traffic;
physical-network fallback is forbidden.**

R PR5 retains independent Java/native IPv4, controlled DNS-wire and preliminary
IPv6 observations; narrow caller/session/generation authorization; broker-owned
socket closure; reconnect and genuine provider replacement; and scoped verified
external-lockdown no-fixed-egress evidence. Its Known Gaps remain physical egress
on VPN loss without lockdown despite callback/snapshot checks, per-app exclusion,
split routing and explicit allowBypass. A timeout/error did not mean no packet.
The final historical PR #5 run at `29723d89052267f64b49af91613d88e0263c8610`
passed 13/13 cases as recorded in the canonical PR5 package; that was not a
production no-fallback pass.

S C1/C4 can reduce direct worker socket creation/effects. H C5 moves requested
networking to a broker whose actual Android UID/package, not virtual guest name,
determines routing. The worker's filter does not constrain that broker or remote
GMS/service deputies. Existing/transferred sockets and generic descriptor I/O
must be covered independently. UI, renderer, SDK/helper, background and secondary
processes cannot inherit packet evidence merely from the worker's deny policy.

No candidate adds evidence that route-check-plus-send is atomic, that product
verification of external lockdown is reliable, or that every destination/producer
has no fallback. General resolver behavior, QUIC/Cronet, complete IPv6 and
helper/background attribution remain Unknown in their original scope. UDP is
not QUIC evidence, a controlled DNS packet is not general resolver evidence,
and VPN presence is not full-tunnel/provider-trust evidence. Brokerage does not
close the historical gap by renaming the traffic producer.

## 12. Android-semantics/useful-class synthesis

R PR4 executed a single DEX entry while imported Application/provider markers did
not run. R AG-1B's ordered synthetic callbacks are useful pre-code evidence, not
installed-package lifecycle. S C2's component slots, logical package tables,
resources, split handling and callbacks provide much richer organization, but
their real Context/Binder/GMS fallbacks cannot be inherited as safe adapters.

A useful retained Android utility needs at least Application/Activity lifecycle,
resources, admitted code/splits and isolated local state. H C3-E/C5 name that
class. C4's strongest Binder/open/mapping denials remove ordinary mechanisms
used by windows, resources, providers, scheduling and storage; permitting broad
genuine authority to recover compatibility loses the intended mediation.
Safe copied-event UI and storage adapters are unestablished.

Services, providers, receivers, jobs, alarms, WebView, SDK/GMS, secondary processes
and background work must each be safely retained or technically excluded before
entry and afterward. One manifest process or no detected dependency is not such
exclusion. An outside trusted UI cannot execute guest View/Activity callbacks.
Copied-input calculation with no guest Android component/resource/storage model
may be useful computation, but it collapses the proposed application class to
a toy computation worker for this product comparison. It is not imported Android
application support and cannot silently reduce product scope.

## 13. Positive evidence retained

| Retained positive | Evidence and useful role | Why it does not complete mandatory authority |
|---|---|---|
| Admission/artifact identity | R AG-1A exact supplied bytes, inventory and immutable generation concepts | Complete splits/signing lineage/opaque behavior remain Unknown; hash is not future behavior proof |
| Manager-owned session/generation state | R AG-1B and PR4/5 fresh caller/session/epoch checks, stale/replay/revoked denials | Owns requests and initial dispatch, not all direct runtime calls or running native code |
| Pre-code ordering | R AG-1B READY before byte transfer and controlled callback sequence | No all-app constructor/provider/native/helper/background closure |
| Isolated UID/process separation | R PR4 identities and persistent sentinels; S C1 platform baseline | Does not synthesize genuine values or admit executable content |
| Narrow broker authorization | R PR4/5 bounded operations and owned resources | Full handle/deputy graph and general revocation unestablished |
| Controlled semantics/reference organization | S C2 logical packages/components/resources; catalog reuse ideas | Compatibility adapters retain unsafe authority; redesigned safe semantics remain H |
| Persona modeling | R catalog/SpoofMyDevice stable scoped values and policy concepts | A value source is not authority over every genuine-state read |
| Additional seccomp filtering | S C4 Android-specific access, TSYNC and inheritance | Source-only scope; no content/Parcel/path policy, cache removal or complete Android contract |
| Hard denial of selected direct syscall effects | S C4 kernel evaluates raw instructions regardless of user-space hooks | Allowed effects, existing mappings, pending calls and deputies remain |
| External-VPN evidence methods | R PR5 independent capture, positive controls, bounded protocol attribution | Methods revealed Known Gaps; no generic production route guarantee |
| Fail-closed governance | R explicit Unknown refusal, known-bypass hard stops, exact evidence/class separation | Policy intent and models are not complete runtime enforcement |
| Selective open-source ideas | R unchanged catalog: Mirro/NEXTVM inventory; NewBlackbox/NEXTVM semantics; Renjana lifecycle; XPrivacyLua coverage; SpoofMyDevice Persona; Binderceptor IPC; ByteHook/ShadowHook observation; VirtualSpace/Mirro negative lessons | No wholesale engine selection or source integration; opaque/provenance and security review gates remain |

**I:** STOP does not mean nothing worked. These pieces own different, limited
decisions. Combining them never makes the direct content or cached-state read
request a missing PD decision. Composition also adds capability-transfer,
broker, startup and lifecycle obligations. Positive support cannot cancel a
mandatory untreated bypass elsewhere.

## 14. Known negatives versus Unknowns and architectural blockers

| Kind | Concrete finding | Could a bounded experiment answer it? | Exit consequence |
|---|---|---|---|
| Unknown implementation property | C4 additional filter/TSYNC exact return, inheritance and supported optional flags on a named device | In principle yes, after separate authorization and a fixed policy question; no experiment specified or authorized here | Can refine one primitive; cannot establish A/B without missing content/state owners |
| Unknown implementation property | Fixed pipe/FD setup, pending Binder work, broker death behavior under a fully specified protocol | Particular properties could be falsified once the contract exists; complete contract remains unestablished | Not permission to invent the architecture while prototyping |
| Known negative evidence | AG-1C previously unadmitted DEX executed directly without helper | Already positively observed; repeating helper tests does not resolve it | Untreated mandatory bypass defeats A and B for retained model |
| Known negative evidence | S1/PR4 genuine Build/state; C2 exact-source genuine-service/account fallback | Existing observation/source evidence retains its scope; new tests cannot reclassify it as Unknown | Distinct genuine-state/adapter blockers |
| Known negative evidence | PR5 VPN-loss physical egress, exclusion/split/allowBypass | Already observed in scoped network configuration | Cannot infer no fallback from snapshots or moved sockets |
| Missing authority owner | Mandatory pre-use content classification across direct ART/other permitted execution | No: testing an unspecified owner is not an implementation experiment | No complete hypothesis eligible for A or prototype review |
| Missing authority owner | Control over every genuine cached/mapped-state read while retaining useful runtime | No concrete complete mechanism supplied; an unnamed repair is architecture invention | A/B remain unestablished even assuming all syscall denials work |
| Prohibited mechanism | Custom SELinux/system/root/privileged policy, production ADB, patched kernel, prohibited engine deployment or PD VpnService as repair | Availability would not change prohibition; no exception authorized | Cannot count toward permitted A/B authority |
| Configuration/access Unknown | Landlock enablement, notification FD/SELinux access, cross-UID memory transport and kernel backports | Specific access properties may be testable; CONTINUE mutation limits are structural, not erased by a success run | Partial alternative facilities cannot supply content/Persona authority |
| Compatibility uncertainty | Safe Activity/resource/storage behavior with strong Binder/open denial; SDK/WebView/GMS and callbacks | A defined adapter/class could be tested later; it is not yet complete | Launch success cannot substitute for enforcement; toy worker cannot establish B |
| Physical/release evidence missing | API 31–37 ARM64 non-rooted release-equivalent matrix, Google/AOSP, Samsung and another OEM | Requires scoped actual evidence and independent review, not source inference | No support/release claim; missing measurements are additional to architectural blockers |
| Supply-chain evidence missing | Exact future TCB source, native/transitive provenance and maintainability | Requires audit and explicit integration decision, not a compatibility run | No engine/reference promoted into trusted production authority |

Unknown is not success and is not automatically a known failure. C follows the
absence of a qualifying permitted conjunction at the bounded phase's endpoint,
not a claim that every Unknown is insoluble.

## 15. Prototype-readiness analysis

The question is whether a complete enough enforcement hypothesis already exists
that a small bounded prototype could falsify it under the charter. It is not
whether one can devise a test for one mechanism. A prototype that must invent
the executable-content owner during implementation fails this readiness test.

| Candidate | Complete enough Protected architecture hypothesis? | Reason and bounded distinction |
|---|---|---|
| C1 | No | Existing process boundary retains content and genuine-state failures; another isolated-service test supplies no missing owner |
| C2 | No | Stronger lower boundary is an unnamed dependency; useful semantics above it remain conditional |
| C3 | No | Class predicate lacks mandatory runtime exclusion owner, including direct DEX/native transitions and retained genuine state |
| C4 | No | Selected syscall denials are concrete and individually falsifiable; complete content/state/capability/Android policy is absent |
| C5 | No | More explicit trust partition still has no unavoidable pre-use content gate and no complete cached-state control; adapters and setup unproven |

No candidate is prototype-ready under this charter. No prototype review,
implementation, fixture, executable policy or new experiment is authorized.

## 16. Narrower-class analysis

The following exhausts the potentially useful classes already considered in C3,
including C5's reuse of C3-E. These are H class descriptions, not eligible apps
or approved reductions. Each row needs both observable membership and lasting
runtime prevention; rejecting a detected feature does not establish the latter.

| Existing proposed class | Observable pre-execution membership rule | Runtime enforcement of exclusions / fail-closed result | AG-1C treatment | Native treatment | WebView/SDK/GMS treatment | Lifecycle/process treatment | Android semantics remaining | Missing lower authority / B result |
|---|---|---|---|---|---|---|---|---|
| C3-A: managed packaging, no packaged app .so | ZIP/DEX/ELF inventory can observe supplied entries; language/future behavior not proven | No runtime no-native/no-loader rule follows; Unknown blocks Protected | Public loader remains; Known path untreated | Platform native remains; concealed/later native not excluded by no .so | Still reachable; absence of names is not closure | Manifest inventory is not all-entry prevention | Conventional managed UI/services intended | Behavioral membership/content/genuine-state owner missing; no B |
| C3-B: no app-controlled machine code | Complete transitive native-effect/dependency allowlist required; not established | No mandatory pre-initializer/later-native exclusion supplied | Native exclusion alone leaves direct DEX | Reviewed platform native allowed in principle; arbitrary app-native exclusion Unknown | SDK native/helper effects need independent closure, not vendor-name scan | Constructors/JNI/threads and later entries remain in scope | Managed app plus reviewed platform effects intended | Selective native/content/capability owner missing; no B |
| C3-C: fixed admitted code, no later/custom loaders | Exact base/split/DEX bytes observable; closed future behavior unproven | Initial hash/helper cannot enforce all later use; unknown setup must refuse | Directly contradicts helper-only no-new-code rule | Native origins/constructors also require unavoidable gate | Delivered modules/SDK/renderer code must be fixed or denied, enforcement Unknown | Fixed code still schedules callbacks/processes; separate gates required | Pre-admitted multidex/splits/components intended | Before-use content owner missing; no B |
| C3-D: one process, no WebView/helpers | Effective manifest/process/dependency inventory; no proof of undeclared future behavior | S process-local disableWebView limits ordinary initialization only; no universal helper/process exclusion | One process still contains ART loader | JNI/native child creation not excluded by manifest | WebView/remote SDK/GMS intended excluded; complete runtime prevention absent | No all-path child, job/alarm, callback/restart deny established | Foreground managed UI; possibly local components intended | Content plus subsystem/entry/capability owner missing; no B |
| C3-E / C5 utility: intersection B+C+D, foreground, fixed artifacts, bounded UI/storage | Exact immutable set plus positively established exclusion and adapter closure required; no accepted set demonstrated | Each B/C/D exclusion unresolved; no safe READY. Foreground-only declaration is not enforcement | No subsequent-code owner; Known path untreated | Necessary platform native retained; app-native exclusion unestablished | WebView/GMS/helper services excluded in intent; audited local SDK subset Unknown | Guest services/providers/receivers/jobs/alarms/background/process expansion intended excluded; every restart needs fresh boundary, not established | Useful calculator/converter/form app needs Application, Activities, resources, local state | Missing content/cache owners and safe Android adapters; no B |
| C3-F: verified computation subset, copied I/O | Sound instruction/import verifier and complete trusted-library model could define membership in principle; none evidenced | Separate interpreter/capability machine only H; no implemented exclusion/fail-closed evidence | Could require different execution model; no established treatment supplied | Only verified imports intended; native transition control unestablished | No ordinary WebView/SDK/GMS semantics assumed | Bounded job/cancel model, not Android app lifecycle; enforcement Unknown | Calculation/transformation; no guest Activity/provider/resource/storage contract | New owner would have to be invented; toy computation worker is not imported Android app support; no B |

No class meets B collectively or individually. “No prohibited behavior observed
in the APK” remains an inventory result, not enforceable membership. Nor does
this prove a future sound verifier impossible; none is supplied by this phase.
No app class is silently marked reliably excluded, Unsupported or N/A merely
because the proposed policy forbids it. Tony's explicit later approval would
still be required for any actual product-scope reduction; none occurs here.

## 17. Requirements traceability

All references are to the unchanged [requirements](../requirements.md). The
evidence descriptions below are not completion or capability-coverage labels.
Where several kinds coexist, positives remain credited and contradictory
evidence remains decisive. Protected eligibility is not established by any row.

| Requirement(s) | Positive supporting evidence | Contradiction / Unknown / missing owner | Protected/gate consequence |
|---|---|---|---|
| PD-REQ-021 | R bounded pre-entry refusal/session models | Mandatory direct-code/state failures and coverage Unknowns remain | Block before Protected code; unsupported paths must fail closed |
| PD-REQ-080 | S ordinary-app isolated/seccomp pieces; R no integration | Prohibited privilege/rewriting/convenience guest/VPN mechanisms cannot repair missing owners | No scope drift or exception; constraints unchanged |
| PD-REQ-081 | S lower denied-syscall mechanism extends beyond wrappers | SDK/GMS/WebView/native/dynamic/alternate paths not independently closed | Direct framework or helper success grants no inherited coverage |
| PD-REQ-083 | R scoped methods and explicit acceptance checklist | No complete boundary/useful class, physical/release matrix or completed independent review | Protection/release gate remains unmet |
| PD-REQ-086 | R AG-1A supplied-artifact identity and positive inventory | Required split completeness, signing lineage, opaque dependencies and hostile archive hardening Unknown | Initial analysis is necessary evidence, not sufficient eligibility |
| PD-REQ-087 | R precisely bound policy/class concepts | No real Protected-eligible class; app-native/opaque/dynamic paths not presumed safe | Mandatory Unknown and untreated bypass block Protected eligibility |
| PD-REQ-090 | R known-bypass classification and modeled hard stops | AG-1C contradicts continued affected-path execution; universal app classification unsupported | Known affected path hard stop; no Experimental override |
| PD-REQ-091 | R exact helper grants/denials; S effect restrictions | Known direct-loader bypass; mandatory before-use content owner missing; safe live-code revocation Unknown | Decisive architectural block to A/B and Protected execution |
| PD-REQ-093 | R scoped evidence; S/I limits made explicit | Static absence, upstream claims, compatibility or filter availability cannot prove mediation | Unknown remains Unknown; no safety scoring |
| PD-REQ-094 | R pinned selective-reference catalog | Future TCB license/provenance/native/dependency/security review and explicit integration decision missing | No engine/source approval; opaque critical binaries excluded pending resolution |
| PD-REQ-095 | R bounded policy distinction and conservative documentation | End-to-end launch/runtime/UI/Ledger/diagnostic distinction Unknown | Experimental results never count toward Protected evidence |
| PD-REQ-022–028, 064–068, 077 | R Persona scope/model ideas and outside policy H | Genuine cached-state authority absent; persistence/coherence/transactional transitions Unknown | Persona claims cannot grant Protected eligibility or shared tenant authority |
| PD-REQ-029–031, 035–037, 071–076, 079 | H copied controlled values and narrow virtual metadata | R/S genuine framework/service fallback; complete location/sensor/power/account/identifier/native closure Unknown | Real remains mediated; no host GPS or permission-based personal-data fallback; descriptive API is not runtime capability |
| PD-REQ-011–012, 041–043 | R UID/sentinel/broker positives; S baseline isolation | Complete manager/peer/host storage, FD/mapping/deputy, backup/transfer/removal coverage Unknown | Block unsupported mandatory access; no full storage/management claim |
| PD-REQ-013–016 | S selected Binder/raw-syscall hard-denial possibility | No complete semantic Binder/native/object policy; caches and OEM/ABI paths unresolved | Native hooks and process names are insufficient authority |
| PD-REQ-015, 027, 044–048, 056 | R initial READY/session epochs, bounded endpoints; S filter inheritance | All early initialization/re-entry, system-created helpers, pending work and hostile termination Unknown | No stale/restart grant; no broader callback authority; fail-safe revocation required |
| PD-REQ-003, 032–034, 058, 070, 092 | R independent packet and owned-socket results; S direct deny | PR5 Known Gaps; all-producer attribution and external-route verification Unknown | Default external VPN and no physical fallback unchanged; no PD VpnService |
| PD-REQ-038, 040, 050–051, 086, 089 | R immutable generation and changed/stale denial concepts | Full base/split/signing/update atomicity, renewed admission and safe state rollback Unknown | Updates/re-analysis cannot inherit old admission or consent |
| PD-REQ-002, 006–010, 019–020, 057–060, 078, 082, 084–085, 088 | R scoped research, public-surface secrecy and governance | No production deployment/review proof; known failures distinct from Unknown-only Experimental | Requirements and canonical gates intact; no ordinary apps/private data/release claim authorized |

## 18. Direct A/B/C exit-criteria matrix

These are mandatory conjunctions. A positive in one condition cannot compensate
for another missing owner or bypass; candidate count is irrelevant.

| Exit | Required element | Collective evidence assessment | Criterion result |
|---|---|---|---|
| A | At least one concrete permitted candidate with authority for every mandatory boundary | C1/C5 have untreated failures; C2/C3 depend on unnamed owners; C4 supplies partial effect authority | Not established |
| A | AG-1C/direct executable admission before use | Section 6: no unavoidable PD content decision supplied | Untreated Known bypass; defeats A |
| A | Native/direct syscalls | C4 real hard-deny mechanism; complete allowed-effect/native policy missing | Concrete but insufficient |
| A | Binder/services/providers | Possible hard cutoff, but no complete inherited/pending/callback/FD/broker graph or useful safe adapter | Unknown |
| A | Filesystem, proc, sys, properties and genuine state | Baseline exposures; broad future-call denial cannot erase cached/mapped values | Missing complete state/object authority |
| A | Networking/external VPN | PR5 Known Gaps preserved; broker routing/no-fallback unresolved | Not established |
| A | Management isolation | Bounded UID/endpoint/sentinel positives, complete deputy/secret protection Unknown | Not established for complete model |
| A | Lifecycle/revocation | Initial ordering/epochs and source inheritance; no complete all-entry/hostile termination account | Unknown |
| A | Useful Android execution class | Rich unsafe adapters or restrictive worker with missing semantics; no closed useful contract | Not established |
| A | No untreated known mandatory bypass; concrete owner in every row | Direct content and genuine cached-state owners absent | Mandatory condition fails; A not selected |
| B | Useful smaller execution class | C3-E useful in principle; C3-F lacks imported-app semantics | Hypothesis only |
| B | Observable pre-execution membership | Initial artifact/manifest inventory possible; behavioral/exclusion closure not established | Not established |
| B | Technically enforced runtime exclusions | C3 B/C/D restrictions lack complete owner; C4 effects do not enforce content provenance | Not established |
| B | Fail-closed behavior | Manager refusal models positive; continued all-path enforcement/revocation unproven | Not established as a useful running class |
| B | No static-absence-only membership criterion | Scan-only versions fail this condition; a sound closed-world alternative is unnamed/unproven | No qualifying membership supplied |
| B | No untreated AG-1C bypass | Narrow packaging/process/foreground labels leave direct ART route | Mandatory condition fails |
| B | No required prohibited mechanism | Ordinary primitives available in part; prohibited mechanisms cannot fill remaining holes | No complete permitted class established |
| B | Scope approval and separate prototype review remain separate | Neither reduction nor prototype authorized; no qualifying class to recommend | B not selected |
| C | Bounded five-category phase completed | C1–C5 records complete, individually preserved and collectively compared | Yes, authorized candidate scope exhausted |
| C | No qualifying A or B identified under current constraints/evidence | Missing content/state owners plus unresolved capability/semantics/network/lifecycle conjunction | Yes, evidence supports C |
| C | STOP recommendation with limits and Unknowns, distinct from owner response | Sections 14–21 preserve positive evidence, limits, missing proof and owner boundary | C. NO CREDIBLE BOUNDARY selected |

“Exhausted” means the authorized five-category records and this comparison are
complete. It does not mean every possible architecture has been investigated.
C2–C4's Unknowns do not force A/B, and C5's rejection alone does not force C.
Even assuming C4's device installation and selected hard denials succeed, the
content and cached-state owners are still missing. This makes the result more
than a request for additional implementation measurements.

## 19. Evidence limits

The selected outcome does **not** prove that Android can never support Privacy
Decoy; that every future Android version has identical mechanisms; that every
possible interpreter, VM or runtime architecture has been examined; that all
apps are Known unsafe; or that research outside this bounded ADR-0008 phase is
metaphysically impossible. It establishes only that the authorized bounded phase
did not identify a qualifying permitted boundary under current constraints and
evidence. No indefinite new search follows from remaining Unknowns.

No source-only claim becomes runtime evidence; no x86_64 debug observation becomes
ARM64 physical/release-equivalent support. The unchanged investigation range is
API 31–37, with Google/AOSP, Samsung and another OEM requiring separate evidence.
Actual ART/kernel/ABI/compiled policy and optional features remain configuration
questions. The historical direct loader is Known bypass in its tested scope;
other loaders, native introductions and opaque behavior are not all universally
classified unsafe. Upstream isolation claims remain U. No protection or release
claim, requirement completion, provenance clearance or implementation permission
is produced by documentation review.

## 20. Selected charter exit

**C. NO CREDIBLE BOUNDARY**

**I — evidence-backed phase result:** no permitted ordinary-device root-free
mechanism identified in the five completed candidates supplies the authority
required for the mandatory Privacy Decoy model, and no useful technically
enforceable narrower class is established. The decisive executable-content
authority gap survives each candidate and their concrete composition. Genuine
cached-state control is independently absent; Binder/capability, useful semantics,
native completeness, lifecycle and external-VPN closure remain insufficient.

Under the charter, this terminates the bounded research with a **STOP
recommendation to Tony**. It does not record Tony accepting STOP or changing his
ADR-0008 REDESIGN decision. It does not weaken requirements, reduce scope, select
an engine, reopen discovery or authorize a prototype. Positive mechanisms and
remaining Unknowns are retained for honest evidence, not automatic continuation.

## 21. Owner-handoff boundary

All five candidate records and the comparative synthesis are complete. The
separate owner handoff remains pending/not performed. After this PR is merged,
the separate next task is to prepare that handoff; this document does not perform
it or record a new Tony decision. ADR-0008's owner decision remains REDESIGN.
The historical canonical PR5 recommendation and AG-1 FAILED remain unchanged.

**Phase outcome: C. NO CREDIBLE BOUNDARY — STOP recommendation under the charter.**
**OWNER HANDOFF REQUIRED.** No prototype or next implementation/research phase is
authorized by this PR. Production Roadmap PR 6 remains unstarted and unauthorized.

## 22. Evidence/source ledger

All sources are repository evidence at the starting main/tree in section 2.
Linked candidate source ledgers preserve exact external revisions, symbols,
access dates and API/kernel scope. They are inherited evidence, not newly
accessed external sources or newly audited dependencies. No external research
was needed for this synthesis.

| ID | Repository evidence | Comparative use and controlling scope |
|---|---|---|
| Charter / ADR | [Charter](../post-ag1-enforcement-boundary-redesign.md), [ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md) | R governance: five candidates, synthesis, separate handoff, unchanged A/B/C conditions and REDESIGN owner decision |
| C1 | [OS/process compartment](post-ag1-candidate-1-os-process-compartment.md) | Sections 2, 5–12, 15–18: R/S UID/SELinux/Binder/property/runtime and semantics limits; exact API31/36/37 ledger |
| C2 | [Controlled semantics](post-ag1-candidate-2-controlled-runtime-lower-boundary.md) | Sections 4–14, 17–20: S exact NewBlackbox/NEXTVM and supporting pins, handle/fallback analysis; conditional decomposition, no execution |
| C3 | [Constrained classes](post-ag1-candidate-3-constrained-execution-class.md) | Sections 3–9, 12–14: H C3-A–F and R/S observability/prevention limits; android-17.0.0_r1 ART/native source |
| C4 | [Syscall/Binder boundary](post-ag1-candidate-4-syscall-binder-boundary.md) | Sections 4–18, 20–22: S Android-specific filter access, TSYNC/inheritance, metadata/notification limits and exact kernel/config samples |
| C5 | [Hybrid architecture](post-ag1-candidate-5-hybrid-architecture.md) | Sections 3–20, 24–26: H concrete isolated-ART/copy-broker composition, capabilities, cached state/content gaps; rejected composition only |
| AG-1A | [Admission analysis](ag1-admission-analysis.md) | R bounded artifact identity and positive inventory, as preserved by closeout/C3/C5; no complete behavioral absence proof |
| AG-1B | [Pre-code bootstrap](ag1-precode-bootstrap.md) | R controlled READY, manager generation/session and synthetic ordering, as preserved by closeout; no general lifecycle proof |
| AG-1C | [Runtime executable code](ag1-runtime-executable-code.md) | R run #150 exact head/tree, API35 Google APIs x86_64 debug; helper decisions and separate direct-loader 1111 Known bypass |
| AG closeout | [AG-1 feasibility closeout](ag1-feasibility-closeout.md) | R nine-question FAILED checkpoint, affected-path hard stop and untested-path Unknown distinction |
| Canonical PR5 | [Feasibility decision](canonical-pr5-feasibility-decision.md) | R historical STOP recommendation, scoped containment/network acceptance assessment, subsequent Tony REDESIGN |
| PR4 | [Containment prototype](pr4-containment-prototype.md) | R head 8386a4e86d588e5f50abf724798f9aca87f0cec3, nine API35 x86_64 debug cases; identity/broker/sentinel positives and genuine-state/component gaps |
| PR5 | [Network feasibility](pr5-network-feasibility.md) | R scoped packet, producer, route/socket/lockdown evidence and preserved Known Gaps; exact-head history remains in source |
| S1 | [Managed-profile boundary](pr8-managed-profile-boundary.md) | R head 69f0352510a55d92dcf4a408aa524cc0532788f9, API35 debug runs #100/#101; seven Build equalities per tenant, FALSIFIED/follow-up BLOCKED |
| Earlier synthesis | [Architecture-discovery synthesis](architecture-discovery-synthesis.md) | R bounded ADR-0006 STOP history, unavailable/prohibited mechanisms and semantics distinctions; not a new search or universal proof |
| References | [Open-source catalog](../open-source-reference-catalog.md) | R/S/U ten exact pins and original dispositions, selective ideas only; no resurvey, engine or integration |
| Failed handoff | [Admission-gated runtime architecture](../architecture-admission-gated-runtime.md) | R/H failed ADR-0007 hypothesis and supporting concepts; not selected forward architecture |
| Normative | [Requirements](../requirements.md), [threat model](../threat-model.md), [acceptance criteria](../acceptance-criteria-1.0.md) | R unchanged PD-REQ-001..095, hostile-code model, complete Protected acceptance and evidence rules |
| Platform / roadmap | [Platform support](../platform-support.md), [roadmap reconciliation](../roadmap-reconciliation.md) | R actual API/OEM/ABI/release scope, canonical numbering and production gates, unchanged |

Validation is limited to documentation scope, local Markdown links, table/row
integrity, exact dispositions/outcome, changed-line review and binary invariants.
No Android build, runtime/test/workflow/dependency change, device experiment or
Actions polling supplies new security evidence. Commit/tree/remote publication
details are reported separately. **OWNER HANDOFF REQUIRED; production Roadmap
PR 6 remains unstarted and unauthorized.**
