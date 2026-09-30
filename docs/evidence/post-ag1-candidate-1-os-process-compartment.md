# Post-AG-1 Candidate 1 — OS/process-enforced guest compartment

**Research/access date:** 2026-09-30. **Candidate:** 1 of five under
[ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md).
**Disposition:** **REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY**.

## 1. Scope and question

Does ordinary non-rooted Android expose a process configuration that holds
guest-controlled application code, denies direct mandatory genuine authority
below that code, and exposes only PD-mediated capabilities?

**Inference:** the reviewed isolated-service family supplies genuine OS isolation,
but not the required conjunction of Persona mediation, executable admission,
Android semantics, and safe capability revocation. Direct genuine Build/property
authority and the unmediated DEX path are decisive; missing OEM evidence is not
the reason to relabel those findings Unknown. Rejection concerns this candidate
under current requirements, not every possible future Android mechanism or the
overall redesign outcome.

**Repository/source observation:** preflight found a clean working tree, preserved
all existing stashes, fetched only `refs/heads/main`, and verified
`dd23c696e1d0d8dc6a83f4cc8872a06333f4e9df`, tree
`a10eacb254db2f4aaaf0fc383e93bed9ad40b6e2`. Branch:
`research/post-ag1-c1-process-boundary`. This record changes documentation only.
No prototype, Android change, build, device/emulator experiment, circumvention
implementation, dependency integration, or third-party target was used.

The investigation range remains API 31–37, prioritizing API 37; build settings
remain minSdk 31, targetSdk 37, compileSdk 37. Eventual release evidence still
requires physical ARM64 non-rooted devices. Candidates 2–5 remain pending/not
performed. Ordinary-app-selectable additional syscall/Binder filters belong to
Candidate 4; this record examines Android's existing process baseline, not a
new filter design. Narrow execution classes remain Candidate 3's later question.

### Evidence vocabulary and limits

* **Verified Android/platform fact (V):** documented public Android behavior,
  within the cited API/target scope; not a tested OEM claim.
* **Repository/source observation (R):** preserved repository observations or
  implementation inspected at a named AOSP tag. AOSP source is not an SDK contract.
* **Upstream claim (U):** a reference project's claim, retained as a claim only.
* **Inference (I):** an explicitly reasoned consequence of cited facts/observations.
* **Design hypothesis (H):** an unimplemented proposed restriction or broker rule.
* **Unknown (?):** evidence absent or insufficient for the specified scope.

These abbreviations apply to tables as well as prose. Source identifiers D1–D15
and A1–A26 resolve in the source ledger. All external accesses were on 2026-09-30.
The current source baseline is `android-17.0.0_r1` (API 37); API 31 and API 36
policy comparisons use `android-12.0.0_r1` and `android-16.0.0_r1`. Retrieved source
was read as text, never built or executed. No Samsung/other-OEM runtime behavior
is inferred from these tags.

## 2. Prior-evidence reconciliation

**R — historical containment prototype:** [PR 4](pr4-containment-prototype.md)
ran nine controlled tests on API 35 Google APIs x86_64 debug. It demonstrated
artifact-derived single-DEX execution in distinct Binder-observed UID/PID
instances, narrow broker identity/epoch checks, selected Java/native sentinel
non-access, and unchanged manager sentinel bytes. Native absent-path results
were not proven permission denials. Own process maps, a CPU sysfs file, and an SDK
property were accessible; genuine Build equality, host Context class exposure,
and selected package/activity-service visibility were Known Gaps. A fixed
subprocess retained the isolated UID. Imported APK Application/provider markers
did not run; the APK's ordinary native-library path was unavailable, while the
harness JNI library executed. None establishes full Android semantics, complete
management/peer containment, Persona mediation, or arbitrary-native revocation.

**R/I — S1:** [managed-profile S1](pr8-managed-profile-boundary.md) remains
**FALSIFIED**: seven mandatory Build fields matched parent state for both tenants.
Different UIDs separate authority over private data, not the values supplied by
platform Build/property APIs. Candidate 1's own historical Build exposure and
the current Build/property source below reinforce that distinction. No S1 result
is relabeled Unknown and its gated follow-up remains BLOCKED.

**R/H — AG-1B reuse:** [AG-1 closeout](ag1-feasibility-closeout.md) preserves
artifact/generation binding, one-use session authority, READY-before-byte-transfer,
and ordered synthetic callbacks. Manager-side admission, policy decisions,
revocation state, and narrow brokers remain reusable outside hostile memory.
Guest-side bootstrap is trusted only before guest execution; its classloader,
checks, and death callback cannot become an independent hostile-native boundary.
Synthetic provider/Application calls are not installed-component lifecycle proof.

**R — AG-1C counterexample:** [AG-1C](ag1-runtime-executable-code.md) run #150,
head `55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`, observed all four direct-loader
milestones on API 35 Google APIs x86_64 debug. This already ran in an
`isolatedProcess` service. `Ag1BootstrapService.secondary` invoked the admitted
fixture's `DirectDexProbe.observe` without `Ag1ExecutableAuthorization`.
Transport hashing/read-only shared memory did not admit the secondary executable.
AG-1 remains **FAILED**, including this positive escape from the helper's
authority; it was not an escape from Android's UID sandbox.

**R — source inspection scope:** the unchanged
[isolated service](../../android/app/src/debug/java/com/privacydecoy/research/IsolatedProbeService.java),
[bootstrap service](../../android/app/src/debug/java/com/privacydecoy/research/ag1/Ag1BootstrapService.java),
[direct fixture](../../android/test-apps/ag1-precode-fixture/src/dynamic/java/com/privacydecoy/ag1/precode/DirectDexProbe.java),
and [native probe](../../android/research-native/src/main/cpp/probe.c) corroborate
what was measured. They were not modified or rerun.

**R — continuing constraints:** [network evidence](pr5-network-feasibility.md)
retains Known Gaps; native/direct-syscall containment remains unresolved.
The [canonical PR 5 package](canonical-pr5-feasibility-decision.md),
[previous synthesis](architecture-discovery-synthesis.md), and
[failed ADR-0007 handoff](../architecture-admission-gated-runtime.md) are history,
not a pass. Their dated pending/in-progress statements are superseded by closeout
and ADR-0008 governance, without editing those records. No factual correction to
a historical observation is required by this research.

## 3. Authoritative Android/AOSP findings

**V:** `isolatedProcess` describes a service with no permissions of its own [D1].
It does not promise a synthetic environment. Android's sandbox applies below
both Java and native code; SELinux and platform seccomp add mandatory baseline
restrictions [D4, D13].

**R:** effective policy includes `isolated_app.te`, `isolated_app_all.te`, inherited
`appdomain`/`domain` rules, macro expansion, DAC, mounts, and service-side checks
[A1–A7]. Reading only the isolated file would miss permitted executable memory,
Binder transfers, system/APK reads, and public properties. SELinux `neverallow`
is a policy-build constraint, not a separate runtime filter: the loaded policy's
allow/deny decisions are enforced by the kernel. An exception to a neverallow
is not itself an allow rule.

**R/I:** the ordinary isolated service is the strongest concrete permitted
configuration examined here: non-exported PD service, distinct instance per
guest, manager/broker in another UID, no shared-isolated flags, no guest code in
an app zygote, and no raw network/private-data handles granted. Even this strict
variant retains the decisive genuine-state and executable-admission problems.
Combining service flags does not remove those authorities.

## 4. Public-API/deployment analysis

| Mechanism | Access/deployment classification | Candidate consequence and evidence |
|---|---|---|
| `isolatedProcess` / `bindIsolatedService` | V: manifest-controlled/public SDK, ordinary-app accessible; flag API 16, instance binding API 29 | Distinct instances are possible, not guest package installation [D1, D2, D3; A8]. |
| Ordinary `android:process` | V/R: public manifest, ordinary app | A second process alone retains application UID; not a manager/guest tenant boundary [D1, D12; A8]. |
| `externalService` / `BIND_EXTERNAL_SERVICE` | V/R: public API/manifest, API 24; exported isolated-service constraints | Changes service ownership/binding attribution; does not import an arbitrary APK or add a denial policy [D3; A9]. |
| `useAppZygote` / `zygotePreloadName` | V: public manifest/callback, API 29 | Startup/preload optimization; shared preloaded state adds TCB/ordering obligations [D3, D6; A10]. |
| Shared isolated instances | V: public binding flags; API 34 caller-scoped sharing, API 35 package/process sharing | Co-locates services in one process/authority; exclude from the strict variant [D2; A9]. |
| API 37 `nativeService` | V/R: public manifest/NDK; source feature flags also present | Omits ART startup, has restricted NDK surface; not complete app semantics or content admission [D3, D15; A11]. OEM/flag deployment remains Unknown. |
| Per-process INTERNET denial | V/R: manifest, ordinary app; documented for target 30+ | Useful network reduction; source removes permission GIDs. Same UID/storage and transferred handles remain concerns [D10; A8]. |
| Binder/FD broker | V/R: public IPC/Parcelable mechanisms | Ordinary app can retain resources and expose narrow operations; no blanket revocation of transferred genuine handles [D8, D9; A6]. |
| Choose custom UID / `startIsolatedProcess` internals | R: internal/non-SDK system orchestration | PD requests instances, not arbitrary credential values [A8, A12]. Hidden calls are not a production dependency. |
| Select/modify SELinux domain or platform mount policy | R: platform policy/system installation, not ordinary-app control | Manifest selects a predefined service model; cannot install a PD-specific deny policy [A4, A7, A10]. Prohibited privilege cannot support this candidate. |
| `isolated_compute_app` | R/?: platform-selected relaxed domain; no ordinary PD selector established | Source exceptions cannot be generalized to regular `isolated_app`; access suitability Unknown and no stronger boundary identified [A2, A4]. |
| Platform seccomp | V/R: automatically applied baseline, not a public configurable service policy | Useful native containment floor; no PD admission/Persona predicate [D4; A13, A14]. Additional filters deferred to Candidate 4. |
| Device/profile owner, root/system policy, OEM-private mechanisms | R/?: different administrative/privileged deployment, or OEM-specific/Unknown | Not needed for the ordinary isolated primitive; none accepted as a repair. S1 already failed Persona. |
| SDK Sandbox | V: public SDK model from API 33/Ad Services extension 3; unsupported/deprecated at 37 | Declared SDK loading, not arbitrary guest-app hosting; no rescue [D14]. |

**I:** no signature/privileged permission, production ADB, root, system image
modification, or hidden-API exploitation is proposed. Absence of an evidenced
ordinary API for a required capability is not permission to use an internal one.

## 5. Process/UID/SELinux authority

**R:** `ProcessList.newProcessRecordLocked` allocates isolated UIDs; ordinary
isolated startup skips package supplementary permission GIDs and uses no external
storage mount mode. `Process` defines regular isolated app IDs 99000–99999 and
legacy app-zygote allocation 90000–98999 within Android's user-relative identity
scheme [A8, A12]. These are implementation ranges, not IDs PD chooses or durable
tenant identifiers. API 37 also has a flag-controlled SafeSetID allocation path;
the old range is not a universal app-zygote promise [A8].

**R:** `seapp_contexts` maps `_isolated` to `isolated_app`, with a distinct
platform-controlled compute exception; it uses `levelFrom=user` [A4]. Separate
isolated UIDs do not imply a distinct SELinux type or unique per-instance MLS
category. Kernel UID/DAC isolation, process memory separation, and broker caller
checks therefore remain important even where services share a domain type.

**R/I:** zygote specialization applies credentials, storage setup, seccomp, and
SELinux context before normal app entry [A13]. Those restrictions do not depend
on a guest voluntarily using PD Java wrappers. They constrain newly loaded DEX
and JNI too, but only for operations actually denied by Android. They do not
replace permitted Build data, scrub inherited memory, or authorize content.

**R/H:** an app zygote can preload a classloader/library and fork isolated
children [D6; A10]. Its UID-changing authority is restricted by platform policy;
the child remains isolated, not the manager UID. PD must not place hostile code,
manager secrets, or broad descriptors in that preload process. Preload state is
inherited; a later per-guest READY handshake cannot undo earlier execution.
Shared-isolated flags deliberately remove separation among co-resident services;
they are unsuitable for independent hostile tenants. Unshared instances support
separate OS identities but not durable guest installations.

## 6. Binder/service authority

**R:** service discovery is restricted, not absent. At API 31, the pinned policy
permits finding activity, display, and WebView-update services [A1]. API 37 adds
`activity_structured_service` [A2, A3]; ActivityManager registers that service
conditionally on its feature flag [A15]. `ServiceManager.tryGetService` delegates
Binder lookup to `tryGetBinder`, which combines SELinux `canFind` with the
registration `allowIsolated` check; listing has its own
`canList` check [A16]. Its isolated-UID helper in this pin checks 99000–99999,
whereas Java `Process.isIsolatedUid` also recognizes the app-zygote range [A12].
Do not collapse those separate checks into an identical all-range contract;
SELinux restrictions still apply.

**R:** discovery is not the full handle inventory. During binding,
`ActivityManagerService.getCommonServicesLocked(true)` explicitly supplies
`package` and `permissionmgr` Binder handles [A15]. Thus a discovery deny does
not establish that the framework lacks a genuine PackageManager handle.
The app policy permits Binder calls/transfers to app and service domains;
`binder_call` also permits reply transfer and FD use [A5, A6]. Those rules do
not authorize every remote method: receiver permissions, UID, attribution,
SELinux labels and method checks still apply.

**R:** ActivityManager and DisplayManager register with isolated access enabled;
DisplayManager's `getDisplayInfo` consults actual display information subject to
access checks [A15, A17]. This is genuine platform authority, not PD Persona
mediation. `ContentProviderHelper.getContentProvider` and publication reject
isolated callers; ActivityManager rejects isolated callers for operations such
as opening content URIs and obtaining IntentSenders [A15, A18]. A Binder object
being reachable proves neither unrestricted access nor harmlessness. Exact OEM
transaction inventories remain **Unknown**.

**H/I:** a safe narrow PD endpoint could return bounded copied values and keep
genuine provider/session/socket/file objects in the broker. It must authenticate
Binder-observed caller identity plus fresh session/generation, validate operation
and parameters, and recheck revocation. Returning an unrestricted genuine Binder
or writable FD transfers authority outside future PD requests. A deputy that
executes arbitrary guest-selected calls as the manager is not narrow mediation.
Service-manager denial and kernel Binder checks are OS boundaries; substituting
PD Java proxies for permitted genuine objects is not an OS-enforced requirement
that hostile code use those proxies. Complete safe brokerage remains **Unknown**.

## 7. Filesystem/storage/proc/sys/property authority

| Surface | Evidence and consequence |
|---|---|
| PD management, host-private and peer-private data paths | R: isolated policy forbids opening app-data files; DAC credentials differ. `ProcessList` also omits isolated app data from mount maps [A2, A8]. I: real support for manager separation, conditional on not granting equivalent handles/deputies; complete product evidence Unknown. |
| Package/APK and system paths | R: inherited app/domain policy permits installed APK read/map/execute and system-file access [A5, A7]. I: private-data isolation is not concealment of all code paths or system metadata. Reachability still depends on path, label, DAC and mount; not a claim every installed APK is enumerable. |
| External/shared storage | R: isolated policy forbids direct directory traversal/open but permits operations on passed regular-file descriptors [A1–A3]. I: descriptor access is distinct from path access and can expose genuine bytes. |
| `/proc` | R: domain rules allow self inspection and selected files such as CPU information; app policy denies several cross-process/network-statistics surfaces [A5, A7]. PR 4 read-opened its own maps and failed manager maps. I: neither a full `/proc` deny nor a virtual process universe. |
| `/sys` | R: isolated neverallows have named exceptions; inherited rules actually permit CPU sysfs reads [A2, A7]. I: real hardware information remains; an exception alone would not prove access. Full path/OEM coverage Unknown. |
| Properties | R: `domain.te` allows reading build/exported property types; `Build` obtains model/manufacturer and related fields from platform properties [A7, A19]. Writes are separately constrained. I: read-only genuine state is still a mandatory Persona exposure; a broker cannot erase a direct read. |
| Inherited/open FDs and mapped memory | V/R: descriptor parceling and policy permit selected received file/socket authority [D9; A2, A3, A6]. I: denying pathname open does not revoke an already held descriptor or remove already copied/mapped bytes. |

**I:** path invisibility can reduce discovery without proving confidentiality of
every handle. UID/DAC, SELinux file permissions, namespace hiding and broker
authorization are separate mechanisms. PD has no evidenced ordinary manifest
option here to replace `/proc`, `/sys`, or the property universe with Persona
data. Host/package paths must not contain secrets merely because guest-private
storage is denied. No real filesystem contents or host values were collected.

## 8. Executable-code and AG-1C analysis

**V/R:** public `InMemoryDexClassLoader` accepts DEX buffers (API 26+, with later
overloads) [D5]. At API 37, the source delegates through `BaseDexClassLoader`;
`DexPathList.initByteBufferDexPath`, `DexFile.openInMemoryDexFiles`, and ART's
`DexFile_openInMemoryDexFilesNative` construct/open memory-backed DEX without a
PD authorization callback [A20–A23]. DEX validation/type safety is not admission
to a PD artifact generation. The historical direct path establishes actual
execution at API 35; source analysis is not a new API 37 execution result.

### Mandatory step-by-step authority path

1. **H/R:** admitted guest code runs in an unshared isolated service after the
   manager's READY decision. AG-1B/C already used that architecture.
2. **R/I:** it receives or creates previously unadmitted DEX bytes. AG-1C supplied
   separately built bytes by controlled IPC; generation in memory is a separate
   possible introduction, not a new measured path. Network denial does not
   forbid computation or receipt of ordinary bytes.
3. **R:** it calls `InMemoryDexClassLoader` directly, bypassing the PD helper.
   This was positively observed and the current public/source path remains [D5;
   A20–A23].
4. **R:** the new class resolves; AG-1C recorded resolution separately from
   initialization. Android loader validation is not PD consent/classification.
5. **R:** static initialization runs; AG-1C positively observed its fixed marker.
6. **R:** entry code executes; AG-1C positively observed the entry result. Normal
   teardown did not demonstrate discovery-triggered termination.
7. **I from A2–A8, A13–A14:** the new code retains the same isolated UID, loaded
   SELinux policy, platform seccomp restrictions, mount view and existing
   capabilities. Loading bytes alone grants no manager UID or platform privilege.
8. **R/I:** public Build/property values, permitted proc/sys/system reads, runtime
   code-loading mechanisms, reachable permitted Binder methods and granted
   handles remain directly available. These are inside Android's allowed
   authority but outside the required exclusive PD mediation.
9. **R/I:** PD-REQ-091 is already violated for the observed direct path when
   previously unadmitted content reaches use/execution, no later than static
   initialization. The sandbox was active throughout; it supplies no earlier
   executable-content decision. Limiting the consequences does not retroactively
   admit the bytes or keep opaque introduction Protected.
10. **H/?:** repair would require an authoritative pre-use content decision or a
    technically enforced different execution model, plus control of remaining
    mandatory genuine authority and safe revocation. No reviewed service flag
    supplies this. Hooks, static absence, and another helper are not a repair.
    Candidate 3/4/5 mechanisms are not researched or authorized here.

**V/R/I:** other standard DEX/JAR/APK loaders require accessible content and
appropriate file/runtime conditions; direct tests beyond the historical loader
remain **Unknown**. Android 14 target-34+ read-only dynamic-file rules and Android
17 target-37+ read-only `System.load` rules mitigate tampering [D7, D11]. They
neither compare content to PD admission nor eliminate memory-backed DEX. Read-only
unadmitted code is still unadmitted. Generated/downloaded bytes are not made
safe by their origin, and denying downloads does not deny generated code.

**R/I:** app policy permits `execmem`, executable app tmpfs mappings, and installed
code mapping [A5, A6]. Native libraries/JNI and dynamic machine code are therefore
not categorically excluded by isolated service selection. Path/linker restrictions
can stop particular loads without controlling all executable introduction. API
37's native-service variant omits ART initialization; the Java sequence is not
its normal entry path, but arbitrary native content admission is still not
established and mandatory native property/filesystem surfaces remain. It cannot
be credited with solving AG-1C for Android applications by removing their runtime.

## 9. Native/direct-syscall analysis

**R/V:** zygote installs Android's app seccomp filter during specialization;
Bionic selects architecture-specific filter data [A13, A14; D4]. SELinux and
UID checks still apply when code calls the kernel without libc/Java hooks. A raw
syscall does not inherently bypass those kernel controls. The app policy still
allows operations needed by ART/native workloads, including executable memory
and selected filesystem/property observations [A5–A7]. This is baseline native
sandboxing, not complete PD native containment.

**R/?:** Bionic's `SYSCALLS.TXT` and app allowlist are policy-generation inputs
[A24]. They include execution/memory operations; source review did not establish
a PD-selectable per-guest syscall/content allowlist. Generated filter output,
kernel configuration, runtime feature flags, ABI differences, and all OEM images
were not validated. No generic Linux capability is presumed available to an
Android app. Candidate 4 is responsible for separately evaluating additional
ordinary-app restrictions; no negative conclusion about that unperformed work
is inferred here.

**I:** direct native access to allowed build properties or CPU information is a
privacy problem even when no sandbox escape occurs. Restricting Binder discovery
does not remove inherited handles; namespace hiding does not revoke file FDs;
seccomp syscall-number filtering is not Persona semantics. **Unknown:** complete
hostile JNI, linker, executable-memory, thread, descendant and revocation
coverage across API/ABI/OEM. ByteHook/ShadowHook do not resolve these Unknowns.

## 10. Networking

**R:** pinned isolated policies forbid creation of the enumerated non-AF_UNIX
socket classes, including TCP/UDP, while regular isolated-app policy permits
using TCP/UDP sockets received from app domains [A1–A3]. This is a meaningful
below-guest direct-socket boundary, not merely lack of a Java permission check.
AF_UNIX remains policy-constrained IPC, not unrestricted Internet authority.
No claim that an isolated process has zero network capability follows once a
socket or network-capable deputy is supplied.

**V/R:** per-process INTERNET declarations are also public [D10]. `ProcessList`
removes GIDs associated with denied permissions [A8]. That is useful but does
not give ordinary same-UID processes isolated storage or remove transferred
socket authority. This record does not assume it supplies complete hostile
native egress control on every kernel/OEM.

**H/I:** broker-owned sockets with only narrow request/reply operations could
use the isolated direct-creation denial without returning FDs, `Network` objects,
or unrestricted networking services. This supports a future evidence path for
one capability, not a complete broker-only network proof: all inherited IPC,
other reachable deputies, helpers and child processes require an inventory.
The historical broker retained its sockets and demonstrated bounded denial and
closure. Passing a raw socket would undermine ongoing request-level control.

**R/V/I:** **Require VPN for protected apps = ON** and **no Privacy Decoy
VpnService** remain mandatory. A networking broker becomes a traffic producer
under its own Android identity; guest logical identity is not automatically a
per-app VPN identity. Android's external always-on/lockdown and per-app routing
are separate mechanisms [D12a]. Historical PR 5 still records physical egress
on VPN loss without lockdown, split/exclusion/allowBypass Known Gaps, and
Unknown QUIC/Cronet, resolver and producer coverage. Callbacks/snapshots cannot
make a route-check-plus-send atomic. Unverified required routes must block; no
physical fallback is proposed. Isolated socket denial alone repairs none of
the broker's external-VPN verification and lifecycle gaps.

## 11. Process lifecycle/inherited capabilities

**R/I:** `domain.te` permits same-domain process creation and self resources;
PR 4 observed one fixed `id` subprocess retaining its isolated UID [A7]. Thus
the candidate cannot assume guest code stays in one thread or one process.
Creation/exec remain constrained by the platform policy, executable labels,
seccomp and credentials. A permitted child is not automatically a new independent
tenant. Inherited FDs, sockets, environment and copied memory remain part of its
authority; exec/close-on-exec details must be tracked separately. Complete
Android-specific descendant inheritance/cleanup evidence is **Unknown** beyond
that bounded historical observation; no production kill-tree guarantee is made.

**V/R/H:** bindings have death/reconnection lifecycle [D2, D8]. UID allocation is
temporary and reusable [A8]. A restarted service must receive fresh admission,
session/epoch and capability authorization before guest dispatch, even if its
numeric UID matches a dead session. The manager must reject stale capabilities
and stop issuing new ones after revocation or broker failure. Broker death
notifications are useful, but a callback in hostile memory is not trustworthy
termination enforcement.

**I/?:** revoking an endpoint can block future broker operations; it does not
retract data already disclosed or arbitrary descriptor copies. Closing only a
broker's copy does not establish closure of the guest's descriptor. Unbinding a
service is not independent evidence that every native thread/descendant has died.
OS-confirmed teardown of all capability holders, race-safe restart, and broker
failure behavior remain **Unknown** for a general hostile workload. App-zygote
preloads also survive across individual child sessions until that zygote ends.

## 12. Android-semantics compatibility

**V/R:** Android components belong to installed packages/manifests [D1, D12].
The isolated service belongs to PD; loading an imported class does not register
its package with Android. `ActivityThread` initializes the hosting Application
and service, while provider access/publication has isolated-caller checks
[A18, A25]. Host startup can precede PD's guest READY barrier; it must remain TCB.

| Semantic surface | Retained behavior, gap, and authority consequence |
|---|---|
| `Application` | R/I: hosting PD Application can initialize; imported Application is not automatically its package's Application. PR 4 missing guest marker and AG-1B synthetic calls do not prove normal lifecycle [A25; prior evidence]. |
| ContentProviders | R/I: acquisition/publication rejects isolated callers [A18]; imported provider registration is absent. Broker emulation is not platform installation. |
| Activities | V/I: no isolated-Activity manifest counterpart was identified in this service model. An Activity in the host process is outside the guest compartment; a guest UI bridge would need separate evidence [D1, D12]. |
| Services | V/R: real lifecycle for declared isolated PD service; not arbitrary guest service registration/identity [D1; A9]. |
| BroadcastReceivers | I/?: class loading does not install receiver intent filters; safe delivery/registration and identity are unestablished. No host-executed guest callback can be assumed isolated [D12; A25]. |
| Jobs | I/?: no imported package JobScheduler identity or safe guest JobService routing established. Host proxy would own scheduling and must re-admit delivery. |
| Alarms | R/I/?: isolated IntentSender restrictions and missing guest identity prevent treating ordinary alarms/PendingIntents as transparently retained [A15]. Broker semantics and restart gating Unknown. |
| WebView | R/I/?: policy supports platform WebView renderer needs [A3], not a complete WebView embedding app inside this guest service. Browser, renderer, provider, storage and network coverage remain Unknown. |
| SDK initialization | R/I: SDKs can execute Java/native initialization within loaded code, but original package Context/services and safe early ordering are not established. Passing host Context already caused a PR 4 gap. |
| Splits/resources | R/I/?: Android's installed host split machinery is not import-time complete-set validation or guest resource installation. No arbitrary guest split semantics demonstrated [A25]. |
| Multidex | V/I: multiple DEX buffers are supported [D5]; that does not install components or admit future DEX. PR 4 intentionally exercised single DEX only. |
| Native initialization | R/I/?: platform supports native execution; `.so` availability/linker paths, early constructors and content control need separate evidence. Harness JNI success and imported-library failure are different observations. |
| Secondary processes | R/I: distinct isolated service instances possible; guest manifest process names do not automatically create protected OS identities. Shared flags lose separation [A8, A9]. |
| Background execution | V/I/?: platform service limits apply [D1]; safe delivery after death, reboot, jobs/alarms and broker recovery remains Unknown. Compatibility retries cannot bypass admission. |
| API 37 native-only service | V/I: supports a limited native service API, not ART Application/Activity/provider semantics [D15]. Removing ART does not establish a general imported-app execution class. |

**I:** a useful isolated worker (for example, processing supplied bytes) is
possible, but no technically enforced Protected application class is established
here. A toy method call is insufficient. Native-only operation is not credited
as a silently narrowed product; Candidate 3 is still pending.

## 13. API/OEM matrix

| Scope/change point | Evidence | Effect and remaining limit |
|---|---|---|
| API 31 baseline / Android 12 | V/R: isolation API predates baseline; `android-12.0.0_r1` policy [A1]; instance binding/app zygote available since 29 [D2, D3] | Three discoverable service exceptions; direct-data-open/non-Unix-socket creation restrictions with FD/socket transfer allowances. No full Persona/content gate. |
| API 33 SDK Sandbox | V: SDK-specific API introduced; extension availability separate [D14] | Not an imported-app replacement; no Candidate 1 upgrade. |
| API 34 | V: shared-isolated binding/manifest opt-in; target-34+ dynamic files read-only [D2, D7] | Co-resident authority and tamper mitigation, not per-tenant or PD content admission. |
| API 35 | V/R: package-scoped sharing [D2]; historical API 35 debug emulator observations | Positive AG-1C bypass and prior gaps retain exact scope; no physical/release inference. |
| API 36 / Android 16 source comparison | R: `android-16.0.0_r1` splits common isolated rules and ordinary/compute domains [A26] | Regular domain retains three discovery exceptions. Sysfs exception set differs from API 31 (including FUSE/page-size related types); first introduction not established. |
| Current API 37 / Android 17 | V/R: current docs and `android-17.0.0_r1` | Adds activity-structured discovery exception and conditional registration; native-only service/zygote; target-37 native DCL read-only rule; SDK Sandbox unsupported [A2, A11, A15; D11, D14, D15]. SafeSetID/feature flags are source details, not universal device assertions. |
| API 37 networking change | V: local-network permission requirement documented for target 37 [D11] | Does not admit DEX, replace isolated-domain policy, or prove broker external-VPN routing. |
| API 32 and other unlisted intervals | ?: no additional material transition established by this bounded comparison | No fabricated per-release continuity or exhaustive diff claim. |
| AOSP/Google | R: release-tag implementation and old API 35 Google emulator evidence only | Current physical ARM64/release-equivalent implementation, ART updates and configuration Unknown. |
| Samsung and other OEMs, all proposed APIs | Unknown | No authoritative OEM-specific enforcement evidence collected; no inferred portability from AOSP or emulator. |

## 14. Ten-project reference implications

**R/U/I:** use only the unchanged [pinned catalog](../open-source-reference-catalog.md).
NewBlackbox `89b59836c66f173756a4ae258cf379a957649820` and NEXTVM
`f581a6642596db396a6fe606addd735979403b6b` inform process/component organization;
their host-app slots were not demonstrated as independent kernel tenant
boundaries. NEXTVM's broad isolation language remains **U**, not evidence.
Binderceptor `7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61` informs IPC interception,
not filesystem/syscall confinement; opaque core provenance remains unresolved.
Mirro `74e6a1e3ea1898b2e2a705d7c8b3e730059023b1` retains the negative lesson
that in-process semantics do not own decisive Android authority.

**R/I:** ByteHook `a8bd254f6e53022b65136f40d10ae3763b6ef8ad` and ShadowHook
`593f491be68799f03ee3bab71ce5908846437147` remain interception references, not OS
process sandboxes. Renjana, XPrivacyLua and SpoofMyDevice can inform lifecycle,
coverage and Persona design without granting authority; VirtualSpace's exact-pin
disqualification remains unchanged. No survey was repeated, no catalog
disposition changed, and no engine/source/dependency was incorporated.

## 15. Authority matrix

The strict unshared regular isolated-service variant in section 3 is evaluated.
No row establishes requirement satisfaction. V/R/I/H/? have the meanings in
section 1; Unknown is explicit, not an empty pass.

| Boundary | Enforcing mechanism | Owner | Guest circumvention analysis | Evidence available/category/scope | Remaining Unknowns |
|---|---|---|---|---|---|
| Executable-code authority | Android loader validation; no PD content gate | ART/OS; PD owns only its helper | Direct valid unadmitted DEX avoids helper without changing UID | R: AG-1C API 35 positive bypass; A20–A23 API 37 source | Unknown: alternative authoritative admission mechanism; not supplied by flags |
| Framework/Java | Isolated permissions; ordinary platform classes | Android | Build and permitted framework state remain direct; proxy use not mandatory | V/R/I: D5, A19; PR 4 genuine Build | Unknown: complete API/OEM surface and safe alternative runtime |
| Binder/services/providers | SELinux discovery/call rules; service UID checks; PD endpoint checks | Kernel/service manager/system services; PD for own broker | Cached/transferred genuine handles bypass discovery; remote checks still apply | R: A2, A5, A6, A15–A18; PR 4 narrow broker | Unknown: full method/deputy/callback inventory and OEM services |
| Native/JNI | UID/MAC/platform seccomp/linker constraints | Android/kernel | Raw code still subject to OS denials, but permitted native observations need no hook | R/I: A5–A7, A13–A14; bounded PR 4 JNI | Unknown: complete arbitrary-native containment and revocation |
| Direct syscalls | Kernel DAC/MAC, baseline seccomp | Android/kernel | Bypasses API interception, not kernel denials; allowed syscall authority remains | V/R/I: D4, A13–A14, A24 | Unknown: ABI/OEM exact profiles and additional PD-selectable restrictions |
| Filesystem | DAC, SELinux types, platform mounts | Android/kernel; broker grants FDs | Denied pathname cannot simply be opened; supplied FD/system path is separate authority | R: A2, A5, A7, A8 | Unknown: full reachable path/descriptor/mount inventory |
| `/proc` | Per-file MAC/DAC/process visibility | Android/kernel | Self/selected files remain direct; manager maps denial is bounded | R: A5, A7; PR 4 API 35 | Unknown: full per-kernel/OEM surface and descendant observations |
| `/sys` | Type-specific MAC, named exceptions | Android/kernel | Allowed CPU paths expose real state without PD | R: A1, A2, A7; PR 4 | Unknown: remaining sysfs labels/paths on OEM images |
| Properties | Property type read/write policy; Build runtime | Android | Permitted build properties/Build remain genuine; write denial is not mediation | R/I: A7, A19; S1 and PR 4 | Unknown: full OEM/property inventory; no PD Persona namespace |
| Networking | No direct non-Unix socket creation in reviewed isolated policy; narrow broker hypothesis | Android/kernel; PD broker; external VPN | Transferred sockets/deputies add egress; broker routing still separate | R/H: A1–A3; PR 5 Known Gaps; D12a | Unknown: all producers, VPN verification/races, QUIC/Cronet, OEM behavior |
| Storage | No app-data direct opens/external traversal; descriptor operations allowed | Android/kernel; PD storage broker | Passing private/peer data FDs defeats intended confidentiality/operation gating | R/I: A1–A3, A8; PR 4 sentinel | Unknown: complete peer persistence, revocation and storage semantics |
| Lifecycle/components | Android declared-service lifecycle; PD admission/session gate | Android and PD supervisor | Imported manifests are not installed; startup/background/descendants need separate gating | V/R/I: D1, D12; A8, A9, A18, A25 | Unknown: usable full-app execution class and complete teardown |
| Management isolation | Different UID/process, MAC/DAC; authenticated narrow IPC | Android/kernel and PD | Host Context/overbroad brokers/FD grants can reintroduce authority; shared UID variant rejected | R/H: PR 4; A2, A8 | Unknown: all secrets/resources, deputy paths, hostile native races |
| Dynamic code | Same process credentials constrain effects; no admission decision | Android/ART; PD helper optional | Generated/received bytes can use direct memory loader; read-only files still unadmitted | V/R/I: D5, D7, D11; A20–A23; AG-1C | Unknown: other loaders/native introduction; known tested failure unchanged |
| Third-party TCB | No integrated engine; platform plus hypothetical PD bootstrap/brokers | Android/platform vendors and PD | Reference tools do not confer confinement; opaque core cannot become trusted by citation | R/I: pinned catalog, unchanged dependencies | Unknown: future source/provenance/security audit; no component selected |

## 16. Requirement traceability

Normative [PD-REQ-001..095](../requirements.md), the [threat model](../threat-model.md),
[canonical amendment](../canonical-audit-integration.md),
[acceptance criteria](../acceptance-criteria-1.0.md), and
[platform matrix](../platform-support.md) remain unchanged. **R/I:** the following
are research consequences, never satisfied/completed requirement states.

| Requirement | Candidate consequence |
|---|---|
| PD-REQ-021 | Contradicts admission to Protected execution: mandatory known failures and Unknowns remain; pre-code denial is still required. |
| PD-REQ-081 | Remains Unknown for full SDK/WebView/native/alternate-path coverage; direct-API results cannot be inherited. |
| PD-REQ-083 | Not addressed as a completed acceptance gate; no physical/release matrix or independent Android/native security review. |
| PD-REQ-087 | Contradicts a Protected-eligible claim for this proposed general runtime; no defensible class is established. |
| PD-REQ-090 | Positive AG-1C bypass retains the affected artifact/path hard stop, with no Experimental override. Not a universal classification of all apps. |
| PD-REQ-091 | Contradicts the candidate's executable gate: sandbox permissions do not classify/admit new code before use; termination/revocation remains unproven. |
| PD-REQ-093 | Supports honest evidence separation; static absence, source permissions and compatibility cannot establish complete mediation. |
| PD-REQ-095 | Not addressed as implemented UX; experimental compatibility provides no Protected evidence. |
| PD-REQ-002, 006, 007, 080 | Supports a permitted public isolated-service research path, but prohibited privilege cannot repair missing authority. |
| PD-REQ-008, 009, 015, 044, 086 | Artifact identity/bootstrap ideas remain reusable; imported full-app lifecycle, splits and execution control remain Unknown. No rewriting exception. |
| PD-REQ-011, 012, 041 | UID/data restrictions support a future evidence path; complete management/peer storage and descriptor safety remain Unknown. |
| PD-REQ-013, 014, 016, 027 | Direct permitted Binder/native/system surfaces contradict exclusive mediation; full inventory, OEM scope and revocation remain Unknown. |
| PD-REQ-026, 028, 030, 031, 073 | UID separation does not provide coherent Persona, controlled Real, or virtual package/host-service universe; contradicts candidate sufficiency. |
| PD-REQ-003, 032, 033, 034, 058, 070, 092 | Supports only a direct-socket-denial evidence path; external VPN default/no fallback and historical network Known Gaps remain. |
| PD-REQ-019, 020, 082, 085, 094 | Supports scoped source/evidence and publication hygiene; no support matrix, complete coverage or third-party integration approval follows. |

## 17. Remaining Unknowns

* **Unknown:** physical ARM64/release-equivalent API 31–37 and Samsung/other-OEM
  enforcement, feature flags, ART module updates, full compiled policy and kernel
  behavior. This record verifies source, not those devices.
* **Unknown:** complete Binder transaction/deputy/handle graph, including package,
  permission, display, callbacks, SDKs and OEM services; all inherited capabilities.
* **Unknown:** complete native/direct-syscall, linker, executable-memory and
  descendant authority/revocation; safe broker-death and restart ordering.
* **Unknown:** a permitted authoritative executable-content admission mechanism
  and any usable technically enforced smaller execution class. Other candidates
  have not been performed; their possible outcomes are not pre-decided here.
* **Unknown:** full app/split/resource/component/background compatibility, safe
  WebView/SDK integrations, and attributable external-VPN routing for every
  broker/helper with loss/replacement/reboot behavior.

**I:** none of these unknowns makes the existing AG-1C or S1 falsifications
uncertain. Useful kernel denials are credited without claiming Persona mediation.
No separate bounded prototype review is proposed: a repeat isolated-service
experiment cannot supply the missing mandatory content/Persona authority.
Accordingly no future experiment plan, test code or runner is added. Reopening
this candidate would require evidence of a materially different permitted
authority, not another incidental load failure or successful app launch.

## 18. Candidate disposition

**REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY**

**Inference from the cited evidence:** ordinary isolated services provide an
OS-owned compartment for selected authority, but the reviewed configuration
cannot deny all mandatory genuine authority and require exclusive PD brokerage.
Public genuine Build/property access, additional permitted Binder/system
authority, and the already-demonstrated unadmitted DEX execution remain. App
zygotes and shared instances do not repair these defects. API 37 native services
change entry/runtime semantics without establishing arbitrary executable-content
admission or full Android applications. Complete native/revocation/routing and
OEM evidence remain Unknown as additional limitations, not substitutes for the
positive contradictions.

No prototype is warranted by this record. AG-1 remains FAILED; S1 remains
FALSIFIED; network Known Gaps and unresolved native/direct-syscall containment
remain. No third-party engine is selected wholesale. PD-REQ-001..095 remain
unchanged. Production Roadmap PR 6 remains unauthorized. Candidates 2–5 are
untouched; no overall redesign exit outcome is selected.

## 19. Source ledger

Every entry below was accessed **2026-09-30**. Documentation is the current
official page on that date (no immutable documentation revision is published in
these URLs). Its API scope is stated separately. AOSP URLs pin release tags;
the tag in each URL is the exact revision inspected, not mutable `main`.
Some Gitiles HTML requests failed in the web reader; the corresponding exact-tag
`?format=TEXT` endpoints were successfully retrieved and decoded as source text.
No claim relies on an unsuccessful retrieval or a secondary commentary source.

### Official documentation and public contracts

| ID / source name and exact URL | Source type / revision | Relevant API scope | Claim supported / category |
|---|---|---|---|
| D1 — [Service manifest][D1] | Official developer guide; current page | Isolation availability cross-checked in D3; background limits 26+ | V: isolated service permission model, service declaration, process/lifecycle attributes. |
| D2 — [Context][D2] | Official API reference; current page | `bindIsolatedService` 29; sharing 34/35 | V: instance binding, co-location flags, bindings/reconnection. |
| D3 — [ServiceInfo][D3] | Official API reference; current page | Isolated 16, external 24, app zygote 29, shared 34, native 37 | V: public manifest-backed service variants and their documented meanings. |
| D4 — [Application Sandbox][D4] | Official platform security documentation; current page | Historical strengthening through API 29; seccomp from 26 | V: kernel sandbox includes native code; DAC/MAC/seccomp baseline. |
| D5 — [InMemoryDexClassLoader][D5] | Official API reference; current page | Constructors 26/27/29, present at 37 | V: memory-backed DEX loading and multiple-buffer/native-library-path overloads. |
| D6 — [ZygotePreload][D6] | Official API reference; current page | 29+ | V: preloading and app-zygote lifetime/callback contract. |
| D7 — [Android 14 target behavior changes][D7] | Official release documentation; current page | Target 34+ | V: dynamic code files must be read-only; not a PD admission rule (I). |
| D8 — [IBinder][D8] | Official API reference; current page | Core IPC/death-recipient APIs predate 31 | V: Binder references, IPC and death notification; no universal resource-revocation promise (I). |
| D9 — [ParcelFileDescriptor][D9] | Official API reference; current page | Core descriptor parceling predates 31 | V: descriptors can cross IPC; duplication/ownership distinct from path access (I). |
| D10 — [Manage network usage][D10] | Official developer guide; current page | Per-process access documented for target 30+ | V: manifest INTERNET per-process allow/deny; documentation does not guarantee no accidental upload. |
| D11 — [Android 17 target behavior changes][D11] | Official release documentation; current page | Target 37+ | V: read-only native files for `System.load`, local-network permission change. |
| D12 — [Application fundamentals][D12] | Official developer guide; current page | General component/package model; no isolated-guest guarantee | V: installed manifest/component/process model; guest-class loading implications are I. |
| D12a — [VPN guide][D12a] | Official developer guide; current page | Always-on 24+; per-app VPN platform model | V: external VPN routing, lockdown/user settings and per-app selection; no evidence for PD route verification. |
| D13 — [SELinux in Android][D13] | Official platform security documentation; current page | Enforcing platform MAC; no OEM-specific validation | V: platform mandatory access control, distinct from ordinary application policy. |
| D14 — [SdkSandboxManager][D14] | Official API reference; current page | 33 / Ad Services extension 3; deprecated/unsupported 37 | V: declared SDK model and current support limitation. |
| D15 — [NativeService NDK reference][D15] | Official NDK reference; current page | 37 | V: native-service callbacks and limited supported NDK APIs without ART. |

### Exact AOSP implementation evidence

All entries in this table are **Repository/source observation (R)**. The scope
is the named source revision, not every shipped device. Class/method/rule names
make the claims reviewable without relying on line numbers that differ between
tags. The linked path after the tag identifies the exact file within the named
repository.

| ID / repository and exact file link | Revision / API | Relevant symbol/rule and supported claim |
|---|---|---|
| A1 — `platform/system/sepolicy`, [private/isolated_app.te][A1] | `android-12.0.0_r1` / 31 | Full baseline isolated policy: three service exceptions, data open denial, non-Unix creation denial, passed FD/socket use. |
| A2 — `platform/system/sepolicy`, [private/isolated_app_all.te][A2] | `android-17.0.0_r1` / 37 | Common isolated rules; fourth service type, app data/external open restrictions, sysfs exceptions, socket creation neverallow. |
| A3 — `platform/system/sepolicy`, [private/isolated_app.te][A3] | `android-17.0.0_r1` / 37 | `app_domain`, `isolated_app_domain`, WebView-update discovery, app-origin TCP/UDP descriptor use. |
| A4 — `platform/system/sepolicy`, [private/seapp_contexts][A4] | `android-17.0.0_r1` / 37 | `_isolated`/`isolated_app`, `levelFrom=user`, compute exception, app/native zygote selectors. |
| A5 — `platform/system/sepolicy`, [private/app.te][A5] | `android-17.0.0_r1` / 37 | `appdomain` execmem, APK/system access, Binder calls and restricted proc/device surfaces. |
| A6 — `platform/system/sepolicy`, [public/te_macros][A6] | `android-17.0.0_r1` / 37 | `app_domain`, `isolated_app_domain`, `binder_call`: inherited attributes, executable tmpfs, call/transfer/FD permissions. |
| A7 — `platform/system/sepolicy`, [private/domain.te][A7] | `android-17.0.0_r1` / 37 | Self fork/files, Binder device, CPU proc/sysfs, `get_prop(domain, build_prop)` and exported property reads; policy modification restrictions. |
| A8 — `platform/frameworks/base`, [ProcessList.java][A8] | `android-17.0.0_r1` / 37 | `IsolatedUidRange`, `newProcessRecordLocked`, `startProcessLocked`, `startProcess`: UID allocation/freeing, GIDs, mounts, app-zygote/SafeSetID branches. |
| A9 — `platform/frameworks/base`, [ActiveServices.java][A9] | `android-17.0.0_r1` / 37 | `bindServiceLocked`, service retrieval and bring-up: external/shared-service validation and app/native zygote hosting selection. |
| A10 — `platform/system/sepolicy`, [private/app_zygote.te][A10] | `android-17.0.0_r1` / 37 | Restricted UID/domain transitions to isolated children, executable preload, data/socket restrictions. |
| A11 — `platform/frameworks/base`, [ServiceInfo.java][A11a]; `platform/system/sepolicy`, [private/native_app_zygote.te][A11b] | `android-17.0.0_r1` / 37 | `FLAG_NATIVE_SERVICE` with feature annotation; native zygote transitions restricted to isolated_app, not a PD-specific privacy domain. |
| A12 — `platform/frameworks/base`, [Process.java][A12] | `android-17.0.0_r1` / 37 | Isolated/app-zygote ID constants and `isIsolatedUid`; internal/test constants are not PD-selectable SDK identities. |
| A13 — `platform/frameworks/base`, [com_android_internal_os_Zygote.cpp][A13] | `android-17.0.0_r1` / 37 | `SpecializeCommon`, `SetUpSeccompFilter`, storage mounting and `selinux_android_setcontext`: pre-entry OS specialization. |
| A14 — `platform/bionic`, [libc/seccomp/seccomp_policy.cpp][A14] | `android-17.0.0_r1` / 37 | `set_app_seccomp_filter`, app-zygote variant, architecture selection and `install_filter`; platform-owned baseline. |
| A15 — `platform/frameworks/base`, [ActivityManagerService.java][A15] | `android-17.0.0_r1` / 37 | `setSystemProcess`, `getCommonServicesLocked`, `enforceNotIsolatedCaller`, `openContentUri`, `getIntentSenderWithFeature`: registration, cached package/permission handles, operation checks. |
| A16 — `platform/frameworks/native`, [cmds/servicemanager/ServiceManager.cpp][A16] | `android-17.0.0_r1` / 37 | `tryGetService`/`tryGetBinder`, `is_multiuser_uid_isolated`, `listServices`: allowIsolated, SELinux find/list, UID-range distinction. |
| A17 — `platform/frameworks/base`, [DisplayManagerService.java][A17] | `android-17.0.0_r1` / 37 | `onStart`, `BinderService.getDisplayInfo`, `getDisplayInfoInternal`: isolated registration and genuine display data subject to access checks. |
| A18 — `platform/frameworks/base`, [ContentProviderHelper.java][A18] | `android-17.0.0_r1` / 37 | `getContentProvider`, `publishContentProviders`: isolated-caller rejection. |
| A19 — `platform/frameworks/base`, [Build.java][A19] | `android-17.0.0_r1` / 37 | `MODEL`, `MANUFACTURER`, related fields and `getString`: property-derived genuine descriptors. |
| A20 — `platform/libcore`, [InMemoryDexClassLoader.java][A20] | `android-17.0.0_r1` / 37 | Public constructors delegate DEX buffers to base loader. |
| A21 — `platform/libcore`, [BaseDexClassLoader.java][A21] | `android-17.0.0_r1` / 37 | ByteBuffer constructor and `initByteBufferDexPath` delegation; no PD authorization. |
| A22 — `platform/libcore`, [DexPathList.java][A22a] and [DexFile.java][A22b] | `android-17.0.0_r1` / 37 | `initByteBufferDexPath`, `openInMemoryDexFiles`: buffer-to-native opening. |
| A23 — `platform/art`, [runtime/native/dalvik_system_DexFile.cc][A23] | `android-17.0.0_r1` / 37 | `DexFile_openInMemoryDexFilesNative`, `AllocateDexMemoryMap`: memory-backed DEX opening; separate read-only Java DCL handling. |
| A24 — `platform/bionic`, [libc/SYSCALLS.TXT][A24a] and [libc/SECCOMP_ALLOWLIST_APP.TXT][A24b] | `android-17.0.0_r1` / 37 | Policy generation inputs include execution/memory operations; not a validated compiled OEM filter. |
| A25 — `platform/frameworks/base`, [ActivityThread.java][A25] | `android-17.0.0_r1` / 37 | `handleBindApplication`, `handleCreateService`, isolated startup branches: hosting Application/service, not imported guest registration. |
| A26 — `platform/system/sepolicy`, [private/isolated_app_all.te][A26a] and [private/isolated_app.te][A26b] | `android-16.0.0_r1` / 36 | Intermediate common/regular domain policy comparison, three discovery exceptions and sysfs exception set. |

**Unknown retrieval limit:** a guessed generated Bionic path
`libc/seccomp/arm64_app_policy.cpp` returned 404 at the API 37 tag. It is not
evidence of missing seccomp; A14 and A24 establish source selection/inputs,
while exact generated filter/runtime behavior remains Unknown. No implementation
claim is based solely on generic Linux documentation.

[D1]: https://developer.android.com/guide/topics/manifest/service-element
[D2]: https://developer.android.com/reference/android/content/Context
[D3]: https://developer.android.com/reference/android/content/pm/ServiceInfo
[D4]: https://source.android.com/docs/security/app-sandbox
[D5]: https://developer.android.com/reference/dalvik/system/InMemoryDexClassLoader
[D6]: https://developer.android.com/reference/android/app/ZygotePreload.html
[D7]: https://developer.android.com/about/versions/14/behavior-changes-14#safer-dynamic-code-loading
[D8]: https://developer.android.com/reference/android/os/IBinder
[D9]: https://developer.android.com/reference/android/os/ParcelFileDescriptor
[D10]: https://developer.android.com/develop/connectivity/network-ops/managing
[D11]: https://developer.android.com/about/versions/17/behavior-changes-17
[D12]: https://developer.android.com/guide/components/fundamentals
[D12a]: https://developer.android.com/develop/connectivity/vpn
[D13]: https://source.android.com/docs/security/features/selinux
[D14]: https://developer.android.com/reference/android/app/sdksandbox/SdkSandboxManager
[D15]: https://developer.android.com/ndk/reference/group/native-service
[A1]: https://android.googlesource.com/platform/system/sepolicy/+/android-12.0.0_r1/private/isolated_app.te
[A2]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/isolated_app_all.te
[A3]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/isolated_app.te
[A4]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/seapp_contexts
[A5]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/app.te
[A6]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/public/te_macros
[A7]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/domain.te
[A8]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/services/core/java/com/android/server/am/ProcessList.java
[A9]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/services/core/java/com/android/server/am/ActiveServices.java
[A10]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/app_zygote.te
[A11a]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/java/android/content/pm/ServiceInfo.java
[A11b]: https://android.googlesource.com/platform/system/sepolicy/+/android-17.0.0_r1/private/native_app_zygote.te
[A12]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/java/android/os/Process.java
[A13]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/jni/com_android_internal_os_Zygote.cpp
[A14]: https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/seccomp/seccomp_policy.cpp
[A15]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/services/core/java/com/android/server/am/ActivityManagerService.java
[A16]: https://android.googlesource.com/platform/frameworks/native/+/android-17.0.0_r1/cmds/servicemanager/ServiceManager.cpp
[A17]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/services/core/java/com/android/server/display/DisplayManagerService.java
[A18]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/services/core/java/com/android/server/am/ContentProviderHelper.java
[A19]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/java/android/os/Build.java
[A20]: https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/InMemoryDexClassLoader.java
[A21]: https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
[A22a]: https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/DexPathList.java
[A22b]: https://android.googlesource.com/platform/libcore/+/android-17.0.0_r1/dalvik/src/main/java/dalvik/system/DexFile.java
[A23]: https://android.googlesource.com/platform/art/+/android-17.0.0_r1/runtime/native/dalvik_system_DexFile.cc
[A24a]: https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SYSCALLS.TXT
[A24b]: https://android.googlesource.com/platform/bionic/+/android-17.0.0_r1/libc/SECCOMP_ALLOWLIST_APP.TXT
[A25]: https://android.googlesource.com/platform/frameworks/base/+/android-17.0.0_r1/core/java/android/app/ActivityThread.java
[A26a]: https://android.googlesource.com/platform/system/sepolicy/+/android-16.0.0_r1/private/isolated_app_all.te
[A26b]: https://android.googlesource.com/platform/system/sepolicy/+/android-16.0.0_r1/private/isolated_app.te
