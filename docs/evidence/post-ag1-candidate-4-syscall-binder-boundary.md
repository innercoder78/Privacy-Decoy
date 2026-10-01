# Post-AG-1 Candidate 4 — ordinary-app-accessible syscall/Binder restrictions

**Research/access date:** 2026-10-01. **Candidate:** 4 of five under
[ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md).

## 1. Scope and answer

Does ordinary non-rooted Android expose a mechanism PD can install before
hostile guest execution that supplies a mandatory lower syscall and/or Binder
authority boundary without prohibited deployment assumptions?

**Inference (I): Android exposes a concrete additional seccomp-filter path to
ordinary application code in the inspected source configuration. This is real
kernel enforcement below direct syscall instructions, not another hook. Its
sufficiency as the required PD boundary is not established.** Classic filters
can deny operations using syscall metadata; they cannot authorize DEX contents,
read pathname strings, distinguish Binder transaction targets inside pointed-to
buffers, or remove genuine data already in the address space. Broad denials and
selective Android compatibility create different, unresolved proof obligations.

**Disposition:** **UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED**.
Unknown is not success. No Protected execution class or overall ADR-0008 outcome
is selected. No separate bounded prototype review is proposed by this record.

**Repository evidence (R):** preflight verified fetched main
`1a1d0056b28ca1fc9e5abc4f986d6561d09a9582`, tree
`4508ca45fa4b5df9c308bcce8287608159fa39dc`. The initial clean checkout was the
Candidate 3 branch; its tree matched this baseline. Eight existing stashes were
preserved. A sandbox-denied fetch was retried successfully before accepting
FETCH_HEAD. The new branch is
`research/post-ag1-c4-syscall-binder-boundary`.

Only this record and the charter's research-progress section change. No Android,
runtime, prototype, test, workflow, dependency, requirement, ADR, roadmap, prior
evidence or binary changes are included. No builds, device runs or experiments
were performed. Candidate 5 remains pending/not performed; production Roadmap
PR 6 remains unstarted and unauthorized. AG-1 remains FAILED.

### Evidence vocabulary and scope

* **V — Verified Android/platform fact:** current official Android documentation
  within its stated scope; not a new OEM measurement.
* **A — Android/AOSP source observation:** exact release-tag implementation,
  principally `android-17.0.0_r1` (API 37), with an explicitly scoped API 31 input.
* **K — Android kernel source/config observation:** the named Android common
  kernel tag and configuration inputs; not a claim about every shipped kernel.
* **U — Upstream Linux claim:** generic semantics, separately cross-checked in
  Android source where used for an Android conclusion.
* **R — Repository evidence:** preserved PD records and their original scope.
* **I — Inference:** consequence of the cited evidence, not a new experiment.
* **H — Design hypothesis:** an unimplemented policy or necessary contract.
* **Unknown:** evidence insufficient or absent; never a pass.

These categories apply throughout the tables. Source IDs resolve in section 22.
The kernel implementation anchor is `android14-6.1-2024-08_r1`; a second arm64
GKI configuration is inspected at `android16-6.12-2025-06_r16`. These are exact
source samples, not claims that API 37 ships either kernel, nor the newest
available revisions. Android API level does not uniquely identify a kernel,
backport set, ART module, compiled policy, target SDK or ABI.

## 2. Prior-candidate reconciliation

**R:** the [charter](../post-ag1-enforcement-boundary-redesign.md),
[failed runtime handoff](../architecture-admission-gated-runtime.md),
[requirements](../requirements.md), [threat model](../threat-model.md),
[acceptance criteria](../acceptance-criteria-1.0.md), and
[platform matrix](../platform-support.md) govern this record without amendment.

| Prior record | Unchanged disposition | What Candidate 4 changes, and what it does not |
|---|---|---|
| [Candidate 1](post-ag1-candidate-1-os-process-compartment.md) | REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY | A/K/I: a PD-installed filter adds a concrete possible owner to the direct-syscall row and can further deny filesystem, proc/sys opens, sockets, process creation, executable mappings and Binder ioctl entry. These are prospective policy effects, not revised test results. Native/JNI effects can be restricted where they reach denied syscalls. Executable admission, framework caches/properties, useful components, management/deputy safety, revocation and TCB closure remain unresolved. The isolated process alone is not resurrected. |
| [Candidate 2](post-ag1-candidate-2-controlled-runtime-lower-boundary.md) | UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED | H: syscall restrictions make it possible to state concrete obligations for a semantics adapter: no direct opens/socket creation, no usable Binder transaction path, immutable approved descriptors, all-thread setup. Whether a useful adapter can operate under those rules becomes a more precise future evidence question. Genuine-service forwarding and host-context fallback remain unacceptable; this does not pass Candidate 2. |
| [Candidate 3](post-ag1-candidate-3-constrained-execution-class.md) | UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY | I: limiting syscall effects does not establish no-new-DEX, no-app-native provenance, or a sound class-membership rule. Interpretation and existing executable mappings survive relevant denials. The proposed Android utility class is still unresolved; no class passes. |

**R:** [PR 4](pr4-containment-prototype.md) showed selected isolated-UID positives
and genuine Build, proc/sys/property, Context and component gaps on API 35
Google APIs x86_64 debug. A direct native open reported absent paths for the
manager sentinels; absence was not proven permission denial. Its fixed child
retained the isolated UID. No result is rewritten as complete native containment.
[PR 5](pr5-network-feasibility.md) retains physical-egress/VPN-loss and other
Known Gaps. [AG-1C](ag1-runtime-executable-code.md) retains its positive direct
loader bypass; the [closeout](ag1-feasibility-closeout.md) remains FAILED.
Historical IN PROGRESS/pending statements remain historical, not current passes.

**R/I:** the [reference catalog](../open-source-reference-catalog.md) remains
unchanged. Mirro/NEXTVM inventory, NewBlackbox/NEXTVM semantics, Renjana lifecycle,
XPrivacyLua coverage and SpoofMyDevice Persona concepts are supporting ideas.
Binderceptor supplies no established kernel policy; its opaque-core problem
remains. ByteHook/ShadowHook do not intercept arbitrary direct syscall
instructions. VirtualSpace remains disqualified at its reviewed pin. No engine,
dependency or source incorporation is selected.

## 3. Android process/kernel baseline

**V:** Android's UID sandbox covers native and managed code; platform seccomp
has restricted app syscalls since Android 8 [D1]. Both the Android 12 and 17
CDDs require configurable syscall filtering from multithreaded programs and
identify seccomp-BPF with TSYNC as AOSP's implementation [D2–D3, section 9.7
C-0-6]. This supports the baseline, not every optional seccomp extension.

**A:** `SetUpSeccompFilter` selects app/system/app-zygote policy in zygote
specialization. It installs while privilege is still present, before UID drop;
its comment explains why setting no-new-privileges at that earlier point would
interfere with the SELinux transition [A1]. That system-only startup function is
not PD's API. `seccomp_policy.cpp` selects architecture-specific generated
classic-BPF filters and installs through `prctl(PR_SET_SECCOMP,
SECCOMP_MODE_FILTER, ...)` [A2]. Ordinary release assumptions include enforcing
SELinux; the source's permissive-debug branch is not an accepted deployment.

**A/I:** by the time a PD Java service starts, ART, Bionic, framework classes,
process state and Binder can already exist. `AppRuntime::onZygoteInit` starts the
Binder thread pool; `ProcessState` opens the driver and maps transaction memory;
`ActivityThread` attaches to ActivityManager before application callbacks
[A10–A13]. A PD-owned worker is therefore not necessarily a single-threaded,
descriptor-empty process at its first callback. Trusted platform/PD startup can
precede guest execution, but must never initialize guest providers, classes,
native constructors or SDKs as part of that startup.

## 4. Seccomp availability to an ordinary app

The evidence chain is Android-specific:

1. **A:** API 37 Bionic `Android.bp` builds the app policy from `SYSCALLS.TXT`,
   common and app allowlists, common and app blocklists, and priority inputs
   [A3]. `prctl` is in `SYSCALLS.TXT`; `seccomp` is explicitly in the common
   allowlist for all listed architectures. Neither is in the inspected
   blocklists [A4–A6]. The API 31 common allowlist also explicitly includes
   `seccomp` [A7]. This corrects the incomplete inference one would get from
   examining only `SECCOMP_ALLOWLIST_APP.TXT`.
2. **A/K:** public Bionic `sys/prctl.h` declares `prctl`; raw `seccomp` is reached
   through the syscall interface, not an evidenced dedicated libc `seccomp`
   wrapper [A8, A4–A5]. The kernel `prctl` dispatch accepts
   `PR_SET_NO_NEW_PRIVS` with value 1 and zero trailing arguments, and routes
   `PR_SET_SECCOMP` to its filter implementation [K2]. No signature permission
   or manifest privilege is required by those paths.
3. **K:** `seccomp_prepare_filter` accepts `no_new_privs` OR namespace
   `CAP_SYS_ADMIN`; the ordinary-app route uses the former. It copies/verifies
   the supplied classic BPF program and attaches it in the kernel [K1]. This
   is not a privileged `bpf()` eBPF program-loading operation. Restrictions on
   unprivileged eBPF are not evidence that classic seccomp-BPF is unavailable.
4. **K:** the inspected `security_task_prctl` path and capability hook leave
   these options to the kernel dispatcher; the inspected SELinux hooks do not
   install a separate `task_prctl` veto [K2–K4]. The seccomp-install path has
   no requirement to acquire a PD-specific SELinux type. This observation
   does not remove SELinux checks on later file, Binder or process operations.
5. **K:** arm64 selects `HAVE_ARCH_SECCOMP_FILTER`; `arch/Kconfig` defaults
   seccomp and its filter support on when dependencies hold. The GKI defconfig
   enables NET and SELinux [K5]. Absence of an explicit `CONFIG_SECCOMP=y`
   line in a minimal defconfig is not absence from the resolved configuration.

**I:** in this AOSP/common-kernel configuration an ordinary installed app can
self-restrict after specialization: set no-new-privileges, then install an
additional filter using raw `seccomp(SECCOMP_SET_MODE_FILTER, flags, ...)` or
the public `prctl` route. Existing Android seccomp does not inherently block
these installation calls. This is a source-established path, not a PD device
observation or an OEM-wide supported API contract.

**K/I:** `prctl` installs on the calling thread and has no TSYNC flag. Raw
`seccomp` with `SECCOMP_FILTER_FLAG_TSYNC` is the relevant all-thread path [K1].
`SECCOMP_MODE_STRICT` is not an alternative Android runtime sandbox: the process
already has filter mode, switching modes is disallowed, and strict mode's tiny
syscall set would not supply Android semantics. `no_new_privs` alone limits
privilege gains; it denies neither existing file/Binder authority nor DEX loading.

**V/I:** CDD 9.7 C-0-3 prohibits exposing configuration of platform security
features to users/developers [D2–D3]. It must not be misread as evidence that an
app cannot add self-restriction: C-0-6 and the explicit AOSP installation path
support that distinction. PD is not proposing to weaken Android's platform
policy, edit SELinux policy, or invoke hidden zygote setters.

## 5. Setup, inheritance, ownership and tamper resistance

**H — necessary sequence, not an implemented bootstrap:** an outside manager
binds a fresh, unshared PD-owned worker; only trusted PD/platform code executes.
The bootstrap establishes an immutable artifact/session generation and the
complete inherited capability/memory inventory. It prepares any narrow IPC,
removes forbidden authority, sets no-new-privileges, and installs the final
filter across the entire thread group. It treats any unsupported feature,
non-success return, stale setup or incomplete capability inventory as fatal.
Only after that barrier may it load or initialize any guest code. READY sent
after guest entry would be too late; the guest's later self-report is not proof.

**K:** TSYNC checks every sibling thread for a compatible filter ancestry,
then synchronizes under kernel locks. Plain TSYNC returns 0 on success, a
positive offending thread ID for certain synchronization failures, or an error;
“nonnegative means success” is wrong. A divergent filter tree can prevent
synchronization. The reviewed kernel propagates no-new-privileges with the
filter, and `copy_seccomp` copies current state under the shared signal lock
during task creation [K1, K6]. This provides a credible future-thread/clone/fork
inheritance model, including a concurrent creation synchronized by those locks.
**U/K:** filters survive permitted exec; no ordinary uninstall/reset operation
exists [U1; K1–K2, K6].

**I:** successful TSYNC does not retroactively filter syscalls already entered.
A pre-existing Binder read, queued callback, FD transfer or other pending kernel
operation can complete later. Draining/quiescing startup activity and preventing
its delivery of forbidden capabilities are additional obligations. A thread
snapshot or `PR_GET_SECCOMP == FILTER` cannot prove the intended policy on every
thread or absence of such operations. No race-free ART/framework-wide quiescence
procedure has been established here.

| Event / owner | Enforcement consequence | Remaining obligation |
|---|---|---|
| PD trusted bootstrap installs static hard-deny filter | K: copied kernel program becomes active at successful installation; guest cannot edit the original userspace buffer to change it | H/Unknown: prove installation before every guest instruction and on all threads |
| Guest adds another filter | K/I: cannot turn an earlier KILL/ERRNO denial into an allowed syscall; can further deny/kill its own execution and PD helpers in the same thread group | H: keep management/broker outside guest memory and thread group; prevent further install calls if notification policy relies on their absence |
| Guest jumps into trusted native/library code | I: syscall still evaluated under the calling task's filter; instruction-address allowlists do not establish caller provenance | Unknown: all retained operations, code-reuse and writable runtime state |
| Guest uses an unfiltered sibling | I: raw native memory/thread manipulation or queued work can avoid a per-thread-only policy | No such thread may be assumed inaccessible; TSYNC success plus pre-entry closure is required, otherwise Unknown |
| Future Binder/ART/native thread | K: inherits creator's filter; existing pool must already have been synchronized | Unknown: pending pre-filter calls and platform runtime compatibility |
| Child fork/clone/exec | K/U: inherits restrictions when permitted; exec does not clear them | H: deny unnecessary creation; enumerate descendants and inherited objects. Inheritance alone is not safe termination |
| Android launches another component/service process | I: system/zygote creates it, not the filtered guest | Fresh bootstrap/filter barrier required; no automatic inheritance from the requester |
| Process death/restart | K/I: dead task's filter is gone; a replacement zygote child has platform baseline only | Fresh admission/epoch/setup; never reuse old READY or numeric UID as authority |
| Supervisor/broker death | I: static denials remain, but allowed computations and retained genuine FDs/handles continue | Unknown: independent whole-execution termination, capability revocation and bounded restart |
| Guest attacks same-process PD helper | I: seccomp distinguishes tasks/metadata, not PD and guest objects sharing memory | Security decisions/secrets remain outside hostile address space; helper health cannot authorize wider fallback |

**I:** an OS denial remains mandatory even when Java, libc, PLT, hooks or
signal handlers are bypassed. Catching a seccomp signal does not perform the
denied syscall. That does not make every other allowed syscall safe. Privileged
ptrace suspension or a compromised kernel is not an ordinary-app escape path
accepted by this model. Resource exhaustion may stop service; it must never
trigger a restart with weaker policy.

## 6. Seccomp expressiveness

**K:** `seccomp_data` contains syscall number, audit architecture, instruction
pointer and six 64-bit argument values. The BPF verifier restricts loads to that
metadata; it cannot dereference user pointers or inspect kernel file objects
[K1, K7]. BPF here means classic seccomp-BPF, not arbitrary eBPF helpers.

| Input/surface | What the filter can decide | What it cannot establish / consequence (I) |
|---|---|---|
| Syscall number and architecture | Distinguish syscall/ABI and deny unknown architectures | Same integer can mean different calls across ABIs; compat/multiplexed paths require explicit handling |
| Instruction pointer | Compare numeric call-site address | Hostile code can invoke permitted existing sites; no DEX/native origin, call-stack or content authorization |
| Integer arguments and flags | Mask/compare values such as socket family, protection bits and direct clone flags | Must handle truncation, signedness and argument layout per ABI; nested `clone3` flags are pointed-to data |
| FD number | Compare numeric argument | No path, inode, socket family, Binder target or durable object identity; close/dup/reuse/transfer can change the object occupying that number |
| `openat` and related calls | Deny whole operation or constrain scalar dirfd/flags | Pathname is a pointer. A permitted dirfd is not a subtree guarantee: absolute paths, `..`, symlinks and alternate opens matter. `openat2`'s policy fields are also pointed-to |
| `ioctl` | Deny all calls or particular request integers/FD numbers | Cannot parse pointed-to structures, recognize a genuine object behind a reused FD, or infer transaction semantics |
| Binder | Deny `BINDER_WRITE_READ` at syscall entry, with ABI-correct encodings | Cannot see nested commands, service handle, transaction code, Parcel data, embedded objects or FD transfers |
| Sockets | Deny creation/family/type and connect/send/receive syscall families | Destination sockaddr and ancillary-FD content are pointed-to; generic read/write can use existing sockets |
| Memory mapping | Check `mmap`/`mprotect` scalar protection flags, sizes and FDs | No content provenance; existing mappings and interpreted code remain; aliases and other mapping operations need policy |
| Process creation | Deny fork/vfork/clone/clone3/exec families, or inspect direct clone flags | Cannot read `clone3` structures/exec strings; system-created components are outside local inheritance |

**I:** blanket syscall denial is useful for a narrowly defined effect. Selective
path, service or content mediation requires another authority. A numeric FD
allowlist works only with a proven immutable object set and no replacement,
duplication or acquisition path; ordinary Android's evolving FD table does not
supply that proof. A default-deny policy must cover ABI alternatives and indirect
kernel interfaces, not just familiar libc names. For example, allowing an
asynchronous-I/O submission interface without accounting for its delegated file
operations could undermine an open/read policy; no such interface is assumed safe.

## 7. Seccomp user-notification feasibility

**A/K:** user notification is relevant, not dismissed as Linux-only: Android's
allowlist permits `seccomp`, and the inspected Android common 6.1 implementation
contains `SECCOMP_RET_USER_NOTIF`, `NEW_LISTENER`, notification ioctls, CONTINUE
and ADDFD under filter support [A5, K1, K7]. Installation uses the same
no-new-privileges-or-capability gate; no additional root requirement is evident
in that source. There is no separate `CONFIG_SECCOMP_USER_NOTIF` prerequisite in
this implementation. This does not establish reliable ordinary-app brokerage
across API 31–37 or every OEM.

| Question | Exact evidence and consequence |
|---|---|
| Listener creation/ownership | K: `init_listener` creates an anonymous-inode file; NEW_LISTENER installs a close-on-exec FD in the installing task [K1]. H: trusted bootstrap must transfer it to an outside PD broker and close every guest-side copy before guest execution. CLOEXEC does not prevent fork inheritance or duplication. |
| Separate broker process | K/I: operations are through the listener FD, not a requirement that the listener stay in the target thread. Android descriptor transfer is possible in general [D7; A14–A15]. Unknown: exact SELinux FD-use/ioctl/receive and isolation-domain compatibility for this listener, trusted transfer ordering and deployment. Kernel presence alone is insufficient. |
| TSYNC with NEW_LISTENER | K: this 6.1 source rejects TSYNC + NEW_LISTENER unless TSYNC_ESRCH is also supplied, avoiding confusion between a listener FD and failed TID [K1]. Older/backported flag combinations cannot be presumed equivalent. |
| Pointer/target memory access | K: notifications carry register metadata, not copied path/Parcel bytes. `process_vm_readv` uses `mm_access(...PTRACE_MODE_ATTACH_REALCREDS)` [K8]; UID/dumpability/ptrace and SELinux checks remain. A manager with a different isolated UID is not thereby granted read access. Unknown: permitted complete cross-UID memory transport without weakening management separation. |
| CONTINUE | K/U: continues the target syscall with its live pointer contents. Another thread or shared mapping can mutate data after checking [K7, U1]. Copying a pathname into the broker does not make CONTINUE use that copy. Such inspection-then-continue is not a race-free security policy. |
| Emulation instead of CONTINUE | H: deny the original and perform an authorized operation on broker-owned, validated data, returning bounded results. Unknown: safe memory transfer, exact Android semantics, identity/credential differences and full syscall emulation. Arbitrary host-context operations would be a confused deputy. |
| FD injection | K: ADDFD exists in this pin, including SETFD and SEND flags [K1, K7]. It transfers a usable object, not merely a sanitized name. H: any injected object must remain safe after duplication, broker death and policy revocation; no raw network/host-data grant by default. |
| Guest controls listener | I: it can forge responses/CONTINUE for notification-covered calls; therefore listener isolation is essential. An outside PID alone does not prove the guest lacks a retained copy, retrieval path or broker deputy. |
| Filter stacking nuance | K: the UAPI warns that the most recently selected USER_NOTIF action can override earlier notification handling [K7]. This exact kernel additionally rejects a second live listener in an ancestor chain (`has_duplicate_listener`, EBUSY) [K1]; do not claim that attack automatically succeeds while the first listener is live. After detach, that condition changes. H: seal further seccomp installs and PR_SET_SECCOMP before guest entry; do not generalize hard-deny monotonicity to arbitrary notification/TRACE designs. |
| Listener death/close | K: after the last listener reference closes, pending and later notified operations receive ENOSYS in this pin [K1]. This denies those operations, not all guest execution; retained listener copies prevent that last-close condition. A hung live broker can leave calls blocked. |
| Target death | K/I: requests can become invalid; check notification IDs and handle races [K1]. Prior broker side effects are not automatically rolled back; PID reuse is not session identity. |
| Portability | Unknown: actual optional-action/flag/ADDFD availability, backports, listener SELinux interactions and cross-UID memory access on every API/kernel/OEM. Neither examined CDD mandates this complete notification protocol [D2–D3]. |

**I:** hard-deny filters can provide a mandatory floor independent of a broker.
Notification is not accepted here as a complete Binder/path sandbox or portable
solution. No ptrace privilege, permissive SELinux, hidden API, production ADB or
same-address-space “trusted supervisor” is proposed to fill the gaps.

## 8. Binder kernel and service-manager authority

**A/K:** app policy inherits Binder device access and call/transfer rules.
`domain.te` allows the Binder device and an unprivileged ioctl set; app policy
permits calls to service/app domains [A14–A15]. `ProcessState` opens `/dev/binder`,
configures it and maps receive memory; `IPCThreadState` submits `binder_write_read`
through `ioctl(..., BINDER_WRITE_READ, &bwr)` [A10–A11]. The driver copies that
outer structure, then reads command buffers and transaction objects [K9–K10].
Java Binder proxies are above this path.

**K:** the driver uses process credentials and kernel reference tables. Binder
handles are per-process references to nodes, not PD guest-package identities.
Kernel SELinux hooks check Binder call/impersonation, object transfer and file
transfer; file use is separately checked [K4, K9]. These rules do not inspect
the PD Persona or admission generation. Same-PD-UID process names cannot create
separate principals; isolated processes improve baseline credential separation.

**A:** service-manager lookup uses `canFind` and `allowIsolated` checks [A16].
Discovery restrictions are not complete capability restrictions: callbacks,
transaction replies, cached framework objects and already transferred handles
can convey authority independently of a new lookup. Candidate 1's exact-source
record also preserves pre-supplied package/permission handles and permitted
activity/display access. Receiving a handle is not proof that every method is
authorized, but neither is it harmless because a discovery path was denied.

**I:** ordinary apps can implement authorization in their own Binder endpoints;
no reviewed public ordinary-app API installs a per-process kernel policy such
as “only PD's Binder object, these methods, these reply objects.” Custom SELinux
Binder/domain/service-manager policies require platform authority, outside this
deployment. `BINDER_WRITE_READ` multiplexes calls, replies, reference management
and receive work. Allowing that request for PD IPC does not select only PD IPC.
Blocking unrelated Binder ioctl codes does not solve this.

**K/I:** a seccomp denial of the ABI-correct `BINDER_WRITE_READ` request can stop
new driver entries even from raw machine code; a blanket ioctl deny is stronger
but also affects unrelated devices/runtime services. Request-number policies
must cover compat encodings, all usable Binder devices and alternate relevant
operations. Classic BPF cannot inspect the pointed-to transaction payload.
Already-entered operations and mapped/received data require separate accounting.
No complete all-OEM Binder cutoff or transaction policy was tested here.

## 9. Broker-only Binder model

**H:** guest holds no useful direct Binder authority and uses only narrow PD
operations, with policy/credentials retained in another process.

| Proposed approach | Assessment |
|---|---|
| Close Binder FD and omit Java service proxies | I: insufficient. Framework may already have driver state, duplicates, handles and in-flight calls; permitted open/ioctl can reacquire authority. Closing a library-owned descriptor can also corrupt runtime assumptions. |
| Deny opening `/dev/binder` by pathname in classic BPF | K/I: unavailable; pathname bytes are not visible. Deny all relevant opens or use another evidenced object/path boundary. Do not mistake a numeric pointer comparison for path policy. |
| Deny all useful Binder ioctl entry across all threads | K/H: a concrete kernel denial for covered request numbers, even with an existing descriptor. Unknown: safe handling of outstanding transactions, runtime survival and full device/ABI surface. Also denies direct PD Binder transport. |
| Permit only a “broker Binder FD” | I: a Binder driver FD is not a single remote-object capability. Allowed command buffers can address other reachable references or service-manager context. FD-number checks cannot distinguish these. |
| Pass only a narrow PD Binder object | V/I: useful endpoint capability whose server can check each method [D6]. It does not remove the process's existing driver/lookup authority. Returned objects, callbacks or FDs can widen the graph. |
| Static Binder denial plus copied input/output over a pre-established non-Binder channel | H: conceivable for a computation worker with tightly controlled descriptors; no Android-app semantics established. Unix recvmsg/SCM_RIGHTS, descriptor duplication and an overly broad deputy must not reintroduce authority. |
| Notification-mediated Binder ioctl | H/Unknown: pointer copying, concurrent mutation, kernel Binder identity/state, callbacks/replies, FD injection and cross-UID access remain unresolved. CONTINUE after inspection is not sufficient. |

**A/I:** ART's computation and ordinary Android application lifecycle are
different requirements. Denying direct Binder can break ActivityManager
attachment/dispatch, windows/display, providers, package queries, scheduling and
SDK services even if some pure computation still works [A10–A13; R Candidates
1–3]. An unfiltered Binder thread left in the guest process is not a safe
compatibility escape: native code shares that address space and can target it.

**H/I:** a broker must keep genuine objects and return only approved data or
revocable narrow operations. Callback code supplied by the guest must execute
inside the same confinement, never inside the broker. Broker death stops its
endpoint, but FD copies, other returned service objects and disclosed bytes can
outlive it. With Binder use still permitted, a hostile guest can attempt direct
service-manager transactions subject to Android's baseline policy. No PD-only
lookup guarantee is established. A toy computation channel is not a silently
approved replacement for Android application semantics.

## 10. Native/direct-syscall trace

**I, grounded in K1/K7/K9 and A4–A6:** hostile machine instructions can enter
the kernel without libc, PLT, Java, ByteHook, ShadowHook or PD helpers. The
kernel evaluates the task's active seccomp policy before the requested syscall
operation, followed by applicable Android DAC/MAC/driver/service checks. This
is a lower boundary for denied entries, not evidence that all allowed effects
are mediated.

| Direct path | Does the candidate act below it? | If permitted, genuine authority remaining |
|---|---|---|
| `openat`/open variants; read/pread/readv | Yes for covered syscall metadata/FD policy | Permitted paths under Android credentials, genuine proc/sys/system files, existing/transferred FDs; path strings not inspected |
| socket/connect/sendto/sendmsg; UDP | Yes for denied creation/use families and scalar family/type | Permitted destination/connected socket, DNS/deputy paths and generic write/writev/sendfile-style use of retained sockets |
| clone/fork/vfork/clone3/exec | Yes for covered calls; inherited filters remain | Additional filtered tasks can keep capabilities alive; unfiltered system-created helpers are separate authority |
| mmap/mprotect and mapping alternatives | Yes for denied mapping/protection transitions | Allowed genuine file/shared memory, executable regions, existing native instructions; readable DEX interpreted by ART |
| Property access | Yes for new denied file/socket syscalls | A property read can be a memory read from an already mapped area or cached Build value, with no new syscall to filter [A17] |
| Binder ioctl | Yes at new ioctl entry for covered ABI/request encodings | Permitted command stream reaches all Android-authorized handles/services, replies and callbacks, not only PD's proxy |
| Other devices/files, `/proc`, `/sys` | Yes for covered opens/ioctls/reads | Any allowed object exposes its real state subject to platform policy; pre-existing device FDs/shared mappings remain |
| Pure userspace/framework/vDSO operation | No new syscall may occur | Existing values, code execution and cached state are not erased by a syscall filter [U1; A17] |

**Unknown:** a complete minimal syscall/descriptor policy compatible with the
required Android class, every ABI alias/driver path, and independent hostile
native validation. Source-level enforceability of a denied syscall is credited;
arbitrary-native PD containment is not claimed.

## 11. Filesystem and other materially relevant facilities

| Mechanism | Classification and owner | Finding, inheritance and limit |
|---|---|---|
| UID/DAC application sandbox | V/R: platform baseline; Android owns credential allocation | Includes native code; isolated service selection offers different credentials. Guest cannot select manager UID. Same-UID process separation alone is insufficient; passed objects/deputies remain [D1; Candidate 1]. |
| SELinux app/isolated domains | A/K: platform-owned mandatory baseline; manifest selects predefined process model | Ordinary app cannot install a new PD policy or arbitrary domain transition. Kernel checks survive raw syscalls; permitted genuine properties/Binder/file use remain [A14–A15, A18; K4]. |
| Platform mount/storage namespaces | R: zygote/system policy; not a PD-configurable filesystem view | Historical isolated mount restrictions help, but no ordinary app authority to replace proc/sys/property filesystem or mount a guest root is established [A1; Candidate 1]. |
| User/mount namespaces | K: feature/privilege dependent; not an established ordinary-app sandbox | Inspected 6.1 GKI config omits USER_NS, whose Kconfig defaults n; 6.12 sample also omits it. Creating mount namespaces requires CAP_SYS_ADMIN in the relevant user namespace [K5, K11–K13]. `unshare`/`setns` syscall presence alone is not that capability. Mount/chroot are also blocked by inspected app seccomp inputs [A6]. OEM enabling a feature does not establish usable PD access. |
| `no_new_privs` | A/K: public libc operation and ordinary self-restriction | Kernel-owned after app sets it; inherited and not cleared by an ordinary reset/exec. Prerequisite for unprivileged filters, not path, Binder, memory-content or privacy mediation [A8, K1–K2, K6; U1]. |
| Additional classic seccomp | A/K: raw syscall/public prctl plus kernel feature | PD configures a policy on its trusted worker before guest entry; kernel enforces. No whole-process safety claim without section 5 ordering and allowed-authority closure. |
| Landlock | A/K/Unknown: raw syscall/kernel LSM, configuration-dependent | API 37 common allowlist explicitly includes the three Landlock calls; API 31 common input does not [A5, A7]. Inspected arm64 GKI 6.1 and 6.12 defconfigs do not enable SECURITY_LANDLOCK; 6.1 Kconfig does not default it on [K5, K11, K14]. The source exists, but these inputs do not establish a deployed enabled LSM. No complete app-access guarantee across API 31–37 is found. |

**K/I — Landlock detail:** the inspected 6.12 `landlock_restrict_self` applies
to the calling thread and requires no-new-privileges or CAP_SYS_ADMIN [K15].
Where enabled and syscall-reachable, it could add monotonic filesystem rights
restrictions rather than pathname inspection by seccomp. Existing threads,
pre-opened objects, handled-rights/ABI differences and future descendants still
need an exact policy account; it is not seccomp TSYNC or a Binder/DEX policy.
Neither examined CDD identifies Landlock as the required mechanism [D2–D3].
No Landlock deployment, intermediate first-enabled API, OEM feature or full
filesystem sandbox is inferred from the API 37 allowlist. A kernel feature and
an allowed syscall must both be present before application usability is claimed.

## 12. Network authority

**K/H:** a final all-thread filter can technically deny new `socket` calls
(or selected scalar domains/types), `connect` and covered UDP/send paths even
when hostile native code issues direct syscalls. API 37 includes x86 direct
socket entries as well as Bionic's legacy socketcall path; an ABI-complete policy
must consider both [A4–A5]. Denying `connect` alone does not stop unconnected UDP.

**I:** socket-creation denial alone does not stop use of inherited/accepted/
transferred sockets. Generic read/write, vectored I/O and relevant transfer
operations can use already-held descriptors. Binder or Unix ancillary messages
can deliver socket FDs; numeric FD allowlists can be defeated by object reuse
unless all acquisition/replacement routes are closed. Denying direct network
families while leaving an arbitrary Unix/Binder network deputy available does
not deny egress.

**V/R:** public per-process INTERNET declarations and isolated-process baseline
socket restrictions are complementary reductions [D8; Candidate 1]. Android's
network guide explicitly limits what the per-process declaration proves.
**I:** DNS may be produced by a local resolver/deputy, direct UDP/TCP, an SDK,
WebView or encrypted application protocol. This record supplies no new Android
resolver, QUIC/Cronet, helper or packet observation; the PR 5 Unknowns remain.

**H/R:** broker-owned networking retains **Require VPN for protected apps = ON**,
all-producer attribution, independently evidenced routing, and fail-closed
denial when required routes cannot be verified. The broker is a traffic producer
under its actual Android identity, not automatically each logical guest's VPN
identity. There is no physical-network fallback and no PD `VpnService`.
Platform always-on/lockdown and per-app routing are separate controls [D9].
No unrestricted socket, Network object, arbitrary service tunnel or host-context
networking is silently moved into a broker. Guest socket denial does not solve
the historical VPN-loss race or production route-verification problem.

## 13. AG-1C and executable-code admission

**R:** AG-1C's API 35 Google APIs x86_64 debug run #150 observed construction,
resolution, static initialization and entry for previously unadmitted DEX
without `Ag1ExecutableAuthorization`. That is a Known bypass of the historical
admission boundary. It is not relabeled Unknown, and no new execution is claimed.

| Required sequence | Candidate 4 decision point |
|---|---|
| 1. Admitted guest manufactures or receives unadmitted DEX | I: denying network/file acquisition cannot prevent computation from producing bytes. Allowed copied input can also contain code. No seccomp content predicate exists. |
| 2. Guest directly constructs `InMemoryDexClassLoader` | V/A: public memory-buffer loader [D5]; native ART opening allocates/copies DEX memory [A19]. Syscall filters see resulting allocations, not “PD admitted digest.” |
| 3. Class resolves | I: loader verification/resolution is not PD classification. A filter permitting required runtime work need not distinguish this new class. |
| 4. Static initializer runs | I: ART can interpret DEX using existing native interpreter code and readable DEX. A new PROT_EXEC mapping is not inherently required for that interpretation. Blanket allocation failure may crash loading but is not selective admission. |
| 5. Entry executes | I: resulting code can remain under the same syscall restrictions while violating PD-REQ-091's before-use admission obligation. Exact behavior under any proposed filter is untested, not a new observation. |

**I:** syscall confinement limits effects; it does not decide executable content.
No earlier authoritative PD decision is inserted into this sequence by classic
seccomp or Binder denial. Valid/read-only code is not automatically admitted
code. Execution of unadmitted content inside a restricted process still needs
before-use block/classification and safe termination/revocation on known-unsafe
discovery. A later SIGSYS, broker rejection or normal teardown cannot retroactively
satisfy that requirement.

**H/Unknown:** a separate unavoidable executable-admission mechanism must own
the runtime's execution/loader transitions and immutable accepted content, or
establish an enforceable verified instruction/import class before use. It must
cover interpretation, every allowed native transition, dynamic/SDK loaders and
opaque introductions, and be protected from guest tampering. No such mechanism
is supplied or selected here. Hooks, helpers and negative static scanning do not
fill the gap. AG-1 remains FAILED; the affected Known bypass retains the
PD-REQ-090 hard stop without an Experimental override.

## 14. Native loading and executable memory

| Code category / operation | What a syscall policy can restrict | What remains unestablished |
|---|---|---|
| Admitted platform/runtime native code | Future syscall effects under the same task filter | No native-call exemption can rely solely on library address; platform code may expose genuine values or serve guest requests |
| Admitted PD native code | Preload before final policy; subject its later syscalls to the filter | It shares guest memory in that worker; admission does not make its data/control state tamper-proof |
| Packaged guest native code | Deny later executable mappings/loading prerequisites | No syscall-number distinction between this ELF and subsequently introduced ELF; complete initial admission/constructor ordering Unknown |
| Subsequent native content / `dlopen` | Constrain underlying opens, maps and protections | `dlopen` is a userspace operation, not a syscall. Permitted file-backed mapping/FD paths can still load code; denial may also break required runtime libraries |
| `mmap(PROT_EXEC)` / file-backed code | Deny executable protection and/or mapping calls by flags | Cannot hash/admit bytes or inspect file provenance; executable existing mappings persist |
| `mprotect` / writable-to-executable changes | Deny additions of execute permission, or broader transitions | Must cover aliases, remapping and all relevant APIs; no complete policy or ART compatibility demonstrated |
| `memfd_create` | Deny creation of anonymous file descriptors | A memfd is not itself an execution decision; permitting it with later executable maps is a content path, denying it can break runtime/IPC uses |
| JIT/runtime executable mappings | Broad deny can stop new executable mapping transitions | May break ART JIT, WebView, SDK JITs, native linking or runtime operation; no selective admitted-code provenance predicate |
| Already mapped native code | Later denied syscalls still constrained | Existing machine instructions and code reuse can execute; original mappings/aliases and in-memory data must be inventoried |

**A/K/I:** AOSP app policy permits runtime executable-memory uses; seccomp's
metadata has no content-origin field [A15; K7]. A blanket no-new-executable-page
rule is therefore an additional restriction with compatibility cost, not a
distinction between platform, PD, packaged guest and newly introduced code.
Even a successful native mapping denial leaves the DEX interpretation problem.
No selective native-admission policy, complete W^X argument, or Protected
arbitrary-native execution class is established.

## 15. Android-semantics compatibility

This is **I/H** analysis grounded in Android startup/Binder source [A10–A16],
the public Binder contract [D6], and the detailed unchanged Candidates 1–3
semantics inventories. No row is a compatibility test or permission to restore
genuine-host fallback. “Broker possible” describes an architectural hypothesis;
safe complete brokerage remains Unknown.

| Surface | Binder/syscall authority needed | Safe permission / genuine authority risk | Brokerage and remaining Unknowns |
|---|---|---|---|
| `Application` | ART allocations/loads; hosting framework attachment; early initializers | Minimal trusted PD startup can precede filter; guest constructor/attach must follow it. Broad runtime allowances do not admit content | H: controlled dispatch; Unknown complete guest context and early SDK closure |
| Activities | Task/window/display/input/lifecycle IPC, graphics/device FDs | Blanket Binder/ioctl denial breaks ordinary activity machinery; allowing it exposes genuine system endpoints | H: outside PD UI exchanging copied events; Unknown actual guest Activity semantics and input/resource fidelity |
| Services | AM start/bind, Binder pool, replies/death callbacks | Allowing general service Binder restores remote authority; isolated hosting is not guest installation | H: broker-owned registration and gated callbacks; Unknown full service identity/lifecycle |
| Providers | Acquisition/publication, URI permissions, cursor/shared memory/FDs | Raw provider replies can transfer durable genuine data/capabilities; baseline isolated restrictions also apply | H: narrow copied data/storage methods; Unknown cursor/URI/reply closure |
| BroadcastReceivers | Registration, delivery Binder, queued callbacks | Genuine/sticky payloads and restart routes can evade selected launch helpers | H: broker sanitizes and gates every delivery; Unknown full ordering/death semantics |
| Jobs | Scheduler Binder, JobService component entry, later process start | Guest local filter does not follow system-created process automatically | H: broker owns scheduling and fresh dispatch; Unknown quotas/restart/cancellation |
| Alarms | Alarm service, PendingIntent/Binder tokens, deferred entry | A durable token or unrestricted scheduling deputy can outlive policy | H: broker-owned epoch-bound tokens; Unknown revocation/reboot races |
| Package manager | Genuine PM Binder/cache and APK metadata access | Allowed lookup reveals Android-authorized genuine package state, not PD's virtual universe | H: copied virtual metadata only; Unknown complete alternate query/intent paths |
| ActivityManager | Attach/application thread and component transactions | Allowing its Binder transaction path gives real framework authority subject to Android checks | H: bounded adapter; Unknown method/reply/callback closure without unsupported hidden APIs |
| Resources | APK/asset opens/maps, configuration/display data, possible graphics IPC | Blanket opens/mapping denial breaks lazy loads; genuine configuration may already be cached | H: immutable pre-admitted assets and virtual configuration; Unknown useful complete rendering path |
| Splits | Complete artifact access, loader/resource updates | Broad post-entry opens/loaders permit unadmitted executable changes | H: fixed validated generation or stop/re-admit; Unknown complete split and dependency semantics |
| Multidex | ART DEX loader/memory work | Permitting the runtime operations also permits other DEX absent an admission owner | H: pre-admitted DEX closure; Unknown enforcement against later loaders |
| WebView | Provider code, renderer/helpers, Binder, shared memory, sockets/JIT | Broad allowances reopen multiple authority/process paths; caller filter does not automatically confine external renderer | H: explicit exclusion or separately evidenced adapter; Unknown safe useful support |
| SDK initialization | Provider/Application/native callbacks and service/files/network effects | SDK name/static absence does not prove no early or later effect | H: complete admitted dependencies; Unknown dynamic and transitive execution/authority |
| Storage | open/read/write/mmap, provider FD transfer | Permitted path/FD may reach manager/peer or host data; numeric directory mapping is insufficient | H: isolated credentials plus broker-owned persistent objects; Unknown independent state/revocation evidence |
| Package visibility | PM/service queries, installed-code paths and cached metadata | Android visibility rules do not provide PD's virtual package universe | H: deny genuine paths and supply bounded metadata; Unknown full closure |
| Binder/services | Driver ioctls, handles, callbacks/replies, transferred FDs | Allow WRITE_READ does not select safe services; block it also blocks PD Binder transport | H: non-Binder narrow transport or another proven lower mediator; Unknown usable application model |
| GMS | External service Binder/account/session/callback/network authority | General bridge restores a genuine deputy; permission is not PD Real authorization | H: narrow explicitly mediated subset or exclusion; Unknown safe subset and host-state separation |
| Secondary processes | clone/exec or system-created components; inherited FDs | Direct children can inherit filter, external launches do not; shared capabilities persist | H: deny or fresh isolated bootstrap; Unknown comprehensive descendant teardown |
| Background work | Threads, queues, jobs/alarms/push, restart | Same-process work requires all-thread restriction; new process needs new barrier | H: every delivery revalidates generation; Unknown full lifecycle/resource bounds |

**I:** copied-input computation is a meaningful kernel restriction scenario,
but no complete PD Android-app boundary is established by it. A local utility
with Application, Activities, resources and persistent state still needs genuine
state exclusion, safe UI/storage adapters and content admission. Those cannot
be inferred from a successful syscall deny. This record does not reduce product
scope to a toy executor.

## 16. API/kernel/OEM matrix

| Scope / change point | Evidence | Limit |
|---|---|---|
| API 31 / Android 12 baseline | V: CDD multithreaded filtering/TSYNC baseline [D2]. A: common allowlist includes raw seccomp [A7] | No PD filter run; API level alone does not guarantee USER_NOTIF, newer flags, ADDFD or Landlock |
| API 32–36 | R: historical API 35 debug observations remain; kernel versions/backports vary | No exhaustive per-release policy diff or first-introduction claim. No manufactured API-by-API support table |
| API 37 / Android 17 | A: full inspected app policy inputs permit prctl/seccomp and include Landlock calls, clone3/openat2 and x86 socket alternatives [A3–A6]; V: CDD retains C-0-6 [D3] | Generated OEM filter, actual kernel configuration, runtime module and feature behavior untested; allowlist presence is not kernel implementation |
| Android common `android14-6.1-2024-08_r1` | K: seccomp/filter defaults, TSYNC, USER_NOTIF, CONTINUE, ADDFD, TSYNC_ESRCH and Binder source [K1–K10] | Exact implementation sample, not all Android 14 devices and not an API 37 kernel claim |
| Android common `android16-6.12-2025-06_r16` | K: arm64 GKI configuration does not enable Landlock or USER_NS; Landlock syscall source exists [K11, K15] | Minimal config/source, not a built/booted image or claim about every later GKI branch |
| AOSP/Google/Pixel | A/K: source plus R old Google APIs x86_64 emulator observations | No new physical Pixel or release-equivalent ARM64 evidence |
| Samsung and another OEM | Unknown | Compiled SELinux, Binder extensions, kernel/backports, ABI and release behavior not independently verified |
| ARM64 vs compat/x86 | A/K: architecture-selected filters and per-ABI syscall/ioctl encoding | Must reject unknown ABI/aliases; x86 multiplexing cannot be silently covered by ARM64 evidence |

**Unknown:** exact intermediate changes in availability beyond the endpoint
observations. No kernel feature lacking a demonstrated compatibility guarantee
is promoted into API-wide support. Landlock first reachability, optional notifier
protocols and an enabled/usable OEM LSM remain configuration questions.

## 17. Public/deployment accessibility matrix

| Mechanism | Access classification | Exact basis / deployment conclusion |
|---|---|---|
| Android baseline seccomp | Kernel feature; system/zygote configured | A1–A3/D1: ordinary app inherits it; PD cannot weaken it or select zygote's internal policy API |
| `prctl(PR_SET_NO_NEW_PRIVS)` | NDK/public libc exposure; ordinary-app self-restriction in inspected configuration | A8/K2–K4: no root/signature permission in path; not itself a sandbox |
| `prctl(PR_SET_SECCOMP, FILTER)` | Public libc/kernel feature; ordinary-app-accessible additional per-thread filter | A4/A8/K1–K4: no-new-privileges route; no all-thread flag |
| raw `seccomp` / TSYNC | Raw syscall allowed by app policy; kernel feature; no dedicated public SDK policy service | A3–A7/K1: concrete app install path, all-thread compatibility/return checks required; OEM execution Unknown |
| USER_NOTIF / NEW_LISTENER / ADDFD | Raw syscall and ioctl/kernel feature; optional support and cross-process access Unknown | K1/K7: present with NNP path in sampled kernel; no universal API/OEM brokerage guarantee |
| Binder/AIDL endpoint and FD IPC | Public SDK/NDK IPC capability, ordinary app | D6–D7/A10–A11/K9: endpoint authorization possible; no kernel-wide interception permission follows |
| `/dev/binder` and driver ioctls | Raw device/kernel interface subject to SELinux, used by ordinary processes | A10/A14–A15/K9–K10: lower entry can be denied by an added filter; broad access not a PD semantic policy |
| Custom Binder SELinux/service-manager policy | Platform/system policy; not ordinary-app configurable | A14–A16/K4: installation/custom policy needs prohibited system/OEM/root authority; public endpoint logic is narrower |
| Isolated service / normal named process | Public manifest/SDK; ordinary app selects predefined model | R Candidate 1: different isolated credentials vs same-UID named process; neither chooses arbitrary domain/UID |
| Per-process INTERNET | Public manifest controlled; documented target 30+ | D8: useful baseline reduction, not descriptor/deputy or VPN routing proof |
| Mount/user namespaces | Kernel feature; capability/config dependent; ordinary-app usability not established | K5/K11–K13 and A6: no permitted custom filesystem namespace authority demonstrated |
| Landlock | Raw syscalls in API 37 policy; kernel LSM; OEM/config-dependent/Unknown | A5/K11/K14–K15: source plus syscall allowlist does not establish an enabled reachable LSM |
| Runtime/Binder internal setters, reflective hidden APIs | Hidden/non-SDK, or system/zygote-only | A1/A12–A13: implementation descriptions are evidence, not proposed production entry points |
| Root, patched Android, custom SELinux, privileged app/production ADB | Prohibited deployment assumptions | R requirements/ADR-0008: cannot repair an ordinary-app gap |

## 18. Required authority matrix

The mechanism evaluated is additional pre-entry all-thread seccomp over Android's
existing sandbox, with optional brokerage explicitly unestablished. No blank
cell or Unknown is a pass; no safety score is assigned.

| Boundary | Enforcing mechanism | Owner | Guest bypass analysis | Evidence/category/scope | Remaining Unknowns |
|---|---|---|---|---|---|
| Executable-code authority | No content-admission predicate in seccomp | ART executes; PD admission owner missing | Unadmitted DEX can be interpreted under allowed runtime work | R AG-1C; A19/K7/I, exact source vs old API 35 execution | Mandatory replacement admission and safe revocation Unknown |
| Framework/Java | OS sandbox and syscall effects only | Android/ART/kernel | Cached genuine Build/objects and pure userspace calls need no new syscall | A12–A13/A17; R Candidate 1; I | Complete genuine-state removal and useful API closure Unknown |
| Binder/services/providers | SELinux baseline; potential hard ioctl denial | Kernel/platform; PD chooses extra deny | Allowed WRITE_READ has opaque transaction contents; callbacks/replies widen handles | A10–A16/K4/K9–K10; I | Safe useful brokerage, pending calls and OEM transaction graph Unknown |
| Native/JNI | Additional per-task syscall filter plus DAC/MAC | Kernel; PD bootstrap | Direct instructions cannot skip a denied entry, but permitted operations and existing memory remain | A3–A8/K1/K7; I | Complete arbitrary-native confinement/provenance and teardown Unknown |
| Direct syscalls | Classic BPF on syscall metadata, TSYNC/inheritance | Kernel with PD-supplied policy | Bypasses hooks, not installed denials; unfiltered thread/process or uncovered syscall remains a route | K1/K6–K7; A3–A7; I | Minimal complete ABI/OEM policy and installation observations Unknown |
| Filesystem | UID/DAC/SELinux; prospective broad syscall deny | Android/kernel, PD bootstrap/broker | Permitted paths, FD reuse/transfer and mappings defeat string/FD-number assumptions | A14–A15/K7; R PR 4; I | Full object graph; enabled usable path LSM and safe storage Unknown |
| `/proc` | Platform per-file rules; deny new opens/reads if covered | Kernel/platform | Existing FDs/memory and any permitted opens still reveal genuine state | R PR 4/Candidate 1; K7/I | All paths, metadata syscalls, kernel/OEM exposure Unknown |
| `/sys` | Platform labels/DAC; potential broad file denial | Kernel/platform | Generic permitted reads are not Persona mediation | R PR 4/Candidate 1; A14–A15/K7/I | Full OEM device/sysfs graph and usable denials Unknown |
| Properties | Android property policy, potential acquisition denial | Android/Bionic/kernel | Cached fields/mapped property areas readable without syscall | A17; R Candidate 1 and PR 4 property observations; I | Pre-entry state exclusion and coherent Persona replacement Unknown |
| Networking | Direct syscall deny plus baseline isolation; required external VPN separately | Kernel, broker, external VPN | Inherited/transferred sockets, generic I/O and deputies remain | A4–A5/K7; D8–D9; R PR 5 | All-producer attribution, routing/revocation and independent packet evidence Unknown |
| Storage | Separate credentials plus proposed broker-owned objects | Android/kernel and external PD broker | Same-UID paths, broad grants, mappings and copies outlive policy | R PR 4/Candidates 1–2; K7/I | Persistent peer/management isolation and complete revocation Unknown |
| Lifecycle/components | Pre-entry TSYNC barrier and fresh process admission hypothesis | PD bootstrap/supervisor; Android managers | System-created helpers do not inherit guest filter; pending callbacks can predate installation | A12–A13/K1/K6; H/I | Race-free startup, full components and death/restart closure Unknown |
| Management isolation | Separate UID/address space and narrow endpoint authorization | Android/kernel and outside PD manager | In-worker PD code is mutable by guest native code; broad deputy/FD transfer exposes authority | R PR 4; K4/K8; H/I | All secret/capability/deputy paths and hostile teardown Unknown |
| Dynamic code | Effects remain filtered; no all-loader content gate | Kernel for effects, ART/linker for execution | Generated DEX/interpreted code or permitted native maps avoid helper admission | R AG-1C; A19/K7/I | Known historical bypass remains; other loaders/native introductions Unknown |
| Third-party TCB | No integration; exact-source/audit governance | PD governance and platform vendors | Reference hook/semantics libraries do not confer lower authority | R catalog/ADR-0008; A/K scoped source | Future policy/bootstrap/broker dependencies, maintainability and independent review Unknown |

## 19. Requirement traceability

All entries below are **R/I/H research consequences**. PD-REQ-001..095 remain
unchanged; no requirement is marked satisfied.

| Requirement | Bounded consequence |
|---|---|
| PD-REQ-021 | Remains Unknown for complete mandatory coverage; supports a future pre-entry deny path, never Protected launch on Unknown |
| PD-REQ-081 | Supports a future evidence path below native wrappers for denied syscalls; SDK/WebView/GMS/deputy and pure-memory paths remain Unknown |
| PD-REQ-083 | Not addressed as a completed acceptance gate; no physical/release matrix, complete mediation or independent review |
| PD-REQ-087 | Remains Unknown; no Protected-eligible Android execution class established by raw syscall availability |
| PD-REQ-090 | Historical Known AG-1C bypass retains hard stop, without Experimental override; filter source does not erase it |
| PD-REQ-091 | Contradicts seccomp-alone admission claim; not addressed by metadata filtering. Separate before-use executable owner remains Unknown |
| PD-REQ-093 | Supports honest source/device/inference separation; negative scanning and incidental crashes cannot prove mediation |
| PD-REQ-095 | Not addressed as implemented UX; no Experimental compatibility promoted to Protected evidence |
| PD-REQ-002, 006–010, 080 | Supports a future ordinary-app self-restriction evidence path; root/system/custom policy, production ADB, rewriting and convenience guest Android remain prohibited |
| PD-REQ-011–012 | Supports future kernel/UID isolation evidence; management/peer secrets and deputy/capability closure remain Unknown |
| PD-REQ-013–014, 016 | Supports hard-denial analysis below Binder/native entry; semantic mediation, genuine-memory exposure and complete API/OEM/ABI coverage remain Unknown |
| PD-REQ-015, 027, 044–046 | Supports TSYNC/inheritance and fresh-start obligations; pending operations, all-entry ordering, revocation and supervisor-death handling remain Unknown |
| PD-REQ-026, 028–031, 065, 071–076 | Not addressed as complete Persona/controlled-Real policy; permitting genuine values/services contradicts exclusive mediation, even when Android allows them |
| PD-REQ-038, 050–051, 086, 089 | Supports immutable artifact/generation prerequisites; filter policy does not establish split closure, safe updates or renewed executable admission |
| PD-REQ-041–043 | Supports future storage-denial/object-isolation evidence; FD copies, mappings, backup/transfer and removal/revocation remain Unknown |
| PD-REQ-003, 032–034, 058, 070, 092 | Supports direct-socket-denial evidence only; external VPN default, all-producer attribution, no fallback and independent packet obligations remain unchanged |
| PD-REQ-019–020, 057, 059–060, 078, 082, 084–085, 094 | Supports scoped documentation and publication hygiene; no new tests, support claim, supply-chain approval or independent security review |

## 20. Remaining Unknowns and review decision

* Exact release-equivalent app installation/TSYNC behavior on the API 31–37,
  physical ARM64, AOSP/Google, Samsung and other-OEM matrix; optional kernel
  action/flag support and compiled policy variations.
* A complete policy retaining useful Android application semantics while
  preventing every direct genuine file/device/Binder/network authority path,
  including all ABI alternatives, pending operations and descriptor reuse.
* Race-free trusted startup before guest constructors/providers/native/SDK code,
  all-thread synchronization, capability draining, future components and restarts.
* Secure listener transfer and ownership, permitted cross-UID target-memory
  handling, race-free emulation and exact notification/LSM compatibility.
* Enabled and ordinary-app-usable Landlock or another suitable path boundary on
  actual supported devices, including thread and pre-opened-object semantics.
* Genuine preloaded/cached/mapped state exclusion, independent executable-content
  admission, native provenance and a technically enforceable useful class.
* Supervisor/broker death, every descendant and outstanding operation, retained
  objects and safe revocation; independently verified external-VPN routing for
  all brokers/helpers and network protocol paths.

**I:** these Unknowns do not soften AG-1C's Known bypass or the positive genuine
state findings. Conversely, the additional seccomp path prevents the blanket
claim that no ordinary-app-accessible lower restriction exists. Neither
extreme is supported.

**No separate bounded prototype review is proposed.** A raw-syscall denial can
be made falsifiable in isolation, but source review has not connected a closed
pre-entry authority inventory and useful Android semantics to the required
executable-admission and Persona obligations. A toy calculation that loses all
framework services would not establish that connection. Therefore no potential
future experiment specification, fixture, script or implementation is added.
Any later review would first need that concrete bounded contract and independent
observations capable of distinguishing intentional enforcement from invalid
inputs, missing ABI/service, incidental denial or runtime failure. This record
does not authorize it or start Candidate 5.

## 21. Candidate disposition

**UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED**

**I:** AOSP and Android common-kernel source establish an ordinary-app additional
seccomp path with a genuine lower enforcement point and an inheritance model.
They do not establish a useful complete mandatory PD syscall/Binder boundary.
Classic BPF lacks transaction/path/content visibility, Android startup retains
threads/handles/state, notification adds unresolved authority/race/portability
conditions, and DEX interpretation remains a separate admission problem.

Candidate 1 remains rejected; Candidates 2 and 3 remain Unknown. AG-1 remains
FAILED, Candidate 5 remains pending, no overall redesign outcome is selected,
and canonical production Roadmap PR 6 remains unauthorized. PD-REQ-001..095,
all historical evidence, catalog dispositions and external-VPN invariants are
unchanged. There is no prototype, engine selection or dependency integration.

## 22. Source ledger

Every external source below was accessed **2026-10-01**. Documentation is the
current official page on that date unless versioned. Every AOSP/kernel file link
pins the exact inspected tag; related files in one row share its category and
scope. Gitiles HTML was unreliable in the web reader, so exact-tag TEXT endpoints
were retrieved and decoded for source inspection. No source was built/executed.
Guessed CTS SeccompTest file paths returned 404 and are not evidence; no CTS
execution or generated OEM filter is claimed.

### Official Android and upstream documentation

| ID / source URL | Source type / revision | API/kernel scope | Supported claim / category |
|---|---|---|---|
| D1 — [Application Sandbox](https://source.android.com/docs/security/app-sandbox) | Official Android security documentation; current | Android baseline, seccomp from 8 | V: UID/kernel sandbox includes native code and platform seccomp |
| D2 — [Android 12 CDD, section 9.7](https://source.android.com/docs/compatibility/12/android-12-cdd#97_security_features) | Official compatibility definition; Android 12 | API 31 | V: multithreaded configurable syscall filtering/TSYNC baseline; platform policy restrictions |
| D3 — [Android 17 CDD, section 9.7](https://source.android.com/docs/compatibility/17/android-17-cdd#97_security_features) | Official compatibility definition; Android 17 | API 37 | V: same scoped kernel filtering obligation; no complete notification/Landlock protocol guarantee identified |
| D5 — [InMemoryDexClassLoader](https://developer.android.com/reference/dalvik/system/InMemoryDexClassLoader) | Official API reference; current | API 26+, present throughout 31–37 | V: public memory-buffer loader, not PD admission |
| D6 — [IBinder](https://developer.android.com/reference/android/os/IBinder) | Official API reference; current | Core IPC predates 31 | V: transactions, object references, pool/reentrant callbacks and endpoint death |
| D7 — [ParcelFileDescriptor](https://developer.android.com/reference/android/os/ParcelFileDescriptor) | Official API reference; current | Descriptor IPC predates 31 | V: transferable/duplicable FD authority, not universal revocation |
| D8 — [Manage network usage](https://developer.android.com/develop/connectivity/network-ops/managing) | Official developer guide; current | Target 30+ per-process INTERNET | V: public declaration and its limited guarantee |
| D9 — [VPN guide](https://developer.android.com/develop/connectivity/vpn) | Official developer guide; current | Always-on 24+; project 31–37 | V: per-app/always-on/lockdown routing, separate from guest syscall filtering |
| U1 — [Seccomp BPF](https://docs.kernel.org/6.1/userspace-api/seccomp_filter.html) | Upstream Linux kernel documentation; version 6.1 | Generic Linux 6.1 semantics only | U: inheritance/exec, notifications, pointer/TOCTOU and vDSO limits; Android access established separately by A/K sources |

### Exact AOSP source

All following rows are **A — Android/AOSP source observations**. Except A7,
revision is **`android-17.0.0_r1`**, scope **API 37 source, not OEM runtime**.

| ID / source URLs | Source type / exact revision | Supported symbols/claims |
|---|---|---|
| A1 — [Zygote.cpp](https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/jni/com_android_internal_os_Zygote.cpp) | AOSP frameworks/base; API 37 tag above | SetUpSeccompFilter/SpecializeCommon; pre-UID filter and SELinux transition ordering |
| A2 — [seccomp_policy.cpp](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/seccomp/seccomp_policy.cpp) | AOSP Bionic; API 37 | Architecture selection, app/system filters and prctl install_filter |
| A3 — [libc/Android.bp](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/Android.bp), [genseccomp.py](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/tools/genseccomp.py) | AOSP build/generator source; API 37 | Actual app filter input composition and ABI generation, not a generated device binary |
| A4 — [SYSCALLS.TXT](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SYSCALLS.TXT) | AOSP Bionic syscall inputs; API 37 | prctl, memory/file/process interfaces and x86 socketcall exposure |
| A5 — [common allowlist](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SECCOMP_ALLOWLIST_COMMON.TXT), [app allowlist](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SECCOMP_ALLOWLIST_APP.TXT) | AOSP Bionic policy inputs; API 37 | seccomp explicitly all architectures; Landlock, clone3/openat2 and x86 alternatives |
| A6 — [common blocklist](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SECCOMP_BLOCKLIST_COMMON.TXT), [app blocklist](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SECCOMP_BLOCKLIST_APP.TXT) | AOSP Bionic policy inputs; API 37 | Installation calls not removed; mount/chroot and other privileged operations removed |
| A7 — [API 31 common allowlist](https://android.googlesource.com/platform/bionic/+/android-12.0.0_r1/libc/SECCOMP_ALLOWLIST_COMMON.TXT) | AOSP Bionic; **android-12.0.0_r1 / API 31** | Explicit raw seccomp entry; no Landlock entry in this input; not complete generated OEM policy |
| A8 — [sys/prctl.h](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/include/sys/prctl.h) | AOSP public libc header; API 37 | Public prctl declaration; no hidden zygote dependency |
| A10 — [ProcessState.cpp](https://android.googlesource.com/platform/frameworks/native/+/android-17.0.0_r1/libs/binder/ProcessState.cpp) | AOSP libbinder; API 37 | Default device, open_driver, receive mmap, pool startup |
| A11 — [IPCThreadState.cpp](https://android.googlesource.com/platform/frameworks/native/+/android-17.0.0_r1/libs/binder/IPCThreadState.cpp) | AOSP libbinder; API 37 | talkWithDriver and BINDER_WRITE_READ pointer-based command transport |
| A12 — [app_main.cpp](https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/cmds/app_process/app_main.cpp), [AndroidRuntime.cpp](https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/jni/AndroidRuntime.cpp) | AOSP app startup; API 37 | onZygoteInit/nativeZygoteInit and pre-app Binder pool |
| A13 — [ActivityThread.java](https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/java/android/app/ActivityThread.java) | AOSP framework; API 37 | attachApplication, handleBindApplication and early framework/component context |
| A14 — [domain.te](https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/domain.te) | AOSP SELinux policy; API 37 | Binder device/ioctl baseline, property/file access and platform domain constraints |
| A15 — [app.te](https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/app.te) | AOSP SELinux policy; API 37 | App Binder call/transfer macros, passed FD use, runtime executable memory |
| A16 — [ServiceManager.cpp](https://android.googlesource.com/platform/frameworks/native/+/android-17.0.0_r1/cmds/servicemanager/ServiceManager.cpp) | AOSP service manager; API 37 | tryGetBinder/canFind and allowIsolated; lookup distinct from existing handles |
| A17 — [system_properties.cpp](https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/system_properties/system_properties.cpp) | AOSP Bionic; API 37 | Init/Find/Read, property-area access and memory-backed values |
| A18 — [isolated_app_all.te](https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/isolated_app_all.te) | AOSP SELinux policy; API 37 | Passed app-data FD operations, service discovery and device restrictions |
| A19 — [dalvik_system_DexFile.cc](https://android.googlesource.com/platform/art/+/android-17.0.0_r1/runtime/native/dalvik_system_DexFile.cc) | AOSP ART; API 37 | DexFile_openInMemoryDexFilesNative/AllocateDexMemoryMap; no PD content predicate |

### Exact Android common-kernel source/configuration

All following rows are **K — Android kernel source/config observations**. K1–K10,
K12–K14 use **`android14-6.1-2024-08_r1`**; K11/K15 use
**`android16-6.12-2025-06_r16`**. Their scope is the respective kernel source or
arm64 GKI configuration inputs, not a universal Android API or shipping image.

| ID / source URLs | Source type / revision | Supported symbols/claims |
|---|---|---|
| K1 — [kernel/seccomp.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/kernel/seccomp.c) | Android common kernel implementation; 6.1 tag above | Filter verification, NNP gate, precedence, TSYNC/locking, listeners, duplicate-listener check, detach/ENOSYS, ADDFD |
| K2 — [kernel/sys.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/kernel/sys.c) | Android common kernel; 6.1 | PR_SET_NO_NEW_PRIVS validation, security_task_prctl and seccomp dispatch |
| K3 — [security/commoncap.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/security/commoncap.c) | Android common capability LSM; 6.1 | cap_task_prctl default delegation and capability enforcement |
| K4 — [security/selinux/hooks.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/security/selinux/hooks.c) | Android common SELinux; 6.1 | Binder call/impersonate/object/FD checks, hook registration, no task_prctl hook here |
| K5 — [arm64 GKI defconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/arch/arm64/configs/gki_defconfig), [arch/Kconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/arch/Kconfig), [arm64/Kconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/arch/arm64/Kconfig) | Android common config inputs; 6.1 | NET/SELinux enabled; architecture seccomp selection/defaults; no explicit USER_NS/Landlock enabling |
| K6 — [kernel/fork.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/kernel/fork.c) | Android common task creation; 6.1 | copy_seccomp/NNP under signal lock, mm_access access check |
| K7 — [linux/seccomp.h](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/include/uapi/linux/seccomp.h) | Android common UAPI; 6.1 | seccomp_data metadata, actions/flags, CONTINUE/stacking warning, notification/ADDFD structures |
| K8 — [process_vm_access.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/mm/process_vm_access.c) | Android common memory access; 6.1 | process_vm_rw_core uses PTRACE_MODE_ATTACH_REALCREDS; listener is not memory-read permission |
| K9 — [drivers/android/binder.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/drivers/android/binder.c) | Android common Binder driver; 6.1 | ioctl/command processing, credential/reference state, transaction and object/file security checks |
| K10 — [android/binder.h](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/include/uapi/linux/android/binder.h) | Android common Binder UAPI; 6.1 | WRITE_READ encoding, pointer-containing buffers, commands/transaction and object representations |
| K11 — [arm64 GKI defconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android16-6.12-2025-06_r16/arch/arm64/configs/gki_defconfig) | Android common config input; **6.12 tag above** | No explicit Landlock/USER_NS enabling; not a resolved OEM configuration |
| K12 — [init/Kconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/init/Kconfig) | Android common configuration; 6.1 | USER_NS default n; feature presence cannot be assumed |
| K13 — [kernel/nsproxy.c](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/kernel/nsproxy.c) | Android common namespaces; 6.1 | Namespace creation/unshare capability checks |
| K14 — [Landlock Kconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/security/landlock/Kconfig), [security/Kconfig](https://android.googlesource.com/kernel/common/+/refs/tags/android14-6.1-2024-08_r1/security/Kconfig) | Android common LSM configuration; 6.1 | Optional Landlock config; LSM order/boot selection distinct from feature compilation |
| K15 — [Landlock syscalls.c](https://android.googlesource.com/kernel/common/+/refs/tags/android16-6.12-2025-06_r16/security/landlock/syscalls.c) | Android common Landlock implementation; **6.12** | restrict_self calling-thread and NNP/capability contract; source presence alone not deployment |

**Validation scope:** documentation/diff, link/source reachability and unchanged
binary checks only. Publication commit/tree/remote verification is reported
separately. No Android enforcement or compatibility test was run for this record.
