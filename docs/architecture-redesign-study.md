# Architecture redesign study and prototype plan

- **Roadmap stage:** PR 7 (architecture-redesign research)
- **Research date:** 2026-09-21
- **Starting repository commit:** `63be1521521e42ba75a81a800eaff9843f1b1ad5`
- **Outcome:** S1 merits a controlled feasibility/falsification prototype; S2
  merits only a bounded non-executing source/provenance audit of two candidates.
  No production architecture or containment engine is selected.

This study follows [ADR-0002](decisions/ADR-0002-feasibility-stop-gate.md), which
selected **A — REDESIGN**. It is a research plan, not a protection claim or an
authorization to run ordinary applications. The old Core Domain/Persistence
implementation sequence remains paused. Only controlled probes and synthetic
data may be used in the proposed experiments.

## Method, evidence vocabulary, and source limits

The review inspected the requirements, threat model, platform matrix, earlier
engine assessment, ADRs, PR 4/5 evidence, all research modules and their tests,
and CI/emulator tooling at the starting commit. It uses these labels:

- **UPSTREAM CLAIM**: a project README or maintainer statement; not accepted as a
  security property.
- **REPOSITORY OBSERVATION**: files, history, metadata, or code at the named
  immutable ref; not runtime evidence.
- **ANDROID PLATFORM FACT**: behavior documented by an official Android source.
- **PRIVACY DECOY ANALYSIS**: consequence for this threat model.
- **UNKNOWN — REQUIRES EXPERIMENT**: no adequate reproducible evidence exists.

The original Codex Cloud research re-reviewed the 2026-09-19 engine assessment
on 2026-09-21, but its network proxy blocked fresh external retrieval. Subsequent
independent PR review by ChatGPT on 2026-09-21 revalidated the following examined
heads and specific observations. These are **reviewer-verified REPOSITORY
OBSERVATIONS**, not retrievals performed by that original Codex environment:

| Repository | Independently revalidated immutable head | Specifically revalidated observation |
|---|---|---|
| chiyuan5/VirtualSpace | `b1ff7988ac598b00b45c22003390ff43396c1c01` | README says MIT; no root LICENSE file. |
| PeterCxy/Shelter | `672560f551772b5cd829b2947bae830d78f20edf` | GPL-3.0 LICENSE. |
| FBlackBox/BlackBox | `a13734339f85a85b4400926f7142557cb6f97dd9` | Examined head revalidated. |
| asLody/VirtualApp | `85768db8e29b5c840f1ba795d09a9d510fb5d068` | Examined head revalidated. |
| DroidPluginTeam/DroidPlugin | `c6ebf652e0f73aa0e5746766e117e51efaf41dbd` | LGPL-3.0 LICENSE. |
| FSpaceCore/SpaceCore | `3826a2fa1ac492fbbe7435ccb52074c1e2b702de` | README says the SDK is not open-source. |

That review also identified Black00Z/Blacks-BlackBox at
`40282a7bf4500948cfd598fc67e6e63114b26dd9` (commit date 2026-05-12), screened
below. During this Desktop Local revision, read-only GitHub retrieval separately
cross-checked that fork's commit date, README, root LICENSE, Gradle declarations,
native-source tree and named AAR paths at the same ref. No engine was built or
executed. Desktop retrieval also confirmed the six immutable refs exist and the
listed license/README observations; it did not establish their latest moving
heads or complete source/dependency provenance.

Head revalidation does not revalidate every historical claim or runtime property.
Other inherited observations, including Island/Insular currency and security
maintenance claims, are not newly verified here; unresolved properties remain
`Unknown`. Recheck moving heads and policies before any later audit/prototype;
never turn source access or README compatibility into positive security evidence.

Primary platform sources used are Android's [application
sandbox](https://source.android.com/docs/security/app-sandbox), [managed
profiles](https://developer.android.com/work/managed-profiles), [work-profile
data](https://developer.android.com/work/managed-profiles#data), [cross-profile
interaction](https://developer.android.com/work/cross-profile-apps), [package
visibility](https://developer.android.com/training/package-visibility),
[non-SDK restrictions](https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces),
[APK signing](https://source.android.com/docs/security/features/apksigning),
[app bundles](https://developer.android.com/guide/app-bundle), [background-work
limits](https://developer.android.com/about/versions/oreo/background),
[VPN lockdown](https://developer.android.com/reference/android/net/VpnService),
and the [Android Virtualization Framework (AVF)](https://source.android.com/docs/core/virtualization).
The distribution review uses current-policy entry points rather than claiming
eligibility: [Google Play Developer Program
Policies](https://play.google.com/about/developer-content-policy/), [target API
requirements](https://developer.android.com/google/play/requirements/target-sdk),
and the [package-visibility policy](https://support.google.com/googleplay/android-developer/answer/10158779).

## Structural failures inherited from PR 4 and PR 5

These are properties of the tested architecture, not missing polish.

1. **DEX dispatch is not Android application execution.** The PR 4 boundary has
   no package installation model, `Application`/provider startup, component
   manager, resources, package `Context`, manifest semantics, multidex or split
   set, jobs/alarms/notifications/deep links/shares, or complete dynamic-loading
   model. Adding more entry methods cannot reproduce the platform.
2. **A Java boundary is not a native boundary.** Imported native libraries are
   unsupported. JNI, libc, direct syscalls, native threads/loading, file
   descriptors and sockets, properties, `/proc`, `/sys`, and filesystem views can
   execute beneath Java hooks. The prototype's own JNI observations demonstrate
   a probe path, not mediation.
3. **Supplying a host `Context` leaks host semantics.** Fixed observations of
   `Build`, packages and Settings do not create a coherent virtual package or
   service universe. Binder, providers, telephony, location, sensors, personal
   data and OEM/Play services remain broad alternate paths.
4. **Logical sessions cannot revoke arbitrary kernel/framework capabilities.**
   Epoch checks revoke narrow broker operations, but not cached Binder handles,
   open files, native sockets, native threads, scheduled/background work, or
   references acquired before a generation transition.
5. **One app UID is not multi-tenant isolation.** A broker in the management app's
   UID and logical directories do not prevent hostile native code from attacking
   management or peer state. The isolated research service improves a narrow
   boundary but cannot host normal app component semantics.
6. **Route observation is not route enforcement.** PR 5 independently reproduced
   physical egress during non-lockdown VPN loss. All traffic identities—including
   native, subprocess, Cronet/QUIC, resolver and background helpers—must be known;
   required external-VPN/lockdown state must be verified; unverifiable state must
   block traffic. Privacy Decoy must not implement `VpnService`.

Consequently the replacement needs: platform-complete application semantics;
kernel-backed tenant/management isolation; an enforceable native boundary;
complete and fail-closed host-state mediation; capability ownership and teardown;
and traffic attribution at actual OS identities. Compatibility hooks alone do not
supply these properties.

## Mandatory constraints and classification framework

Every direction is Android-only, root-free, and usable on ordinary non-rooted
devices. It may not require Magisk, Xposed, LSPosed, a custom ROM, patched kernel,
privileged/system installation, ordinary-use ADB, guest root, or a Privacy Decoy
`VpnService`. Routine APK rewriting/re-signing remains disfavored and requires the
separate exception process in PD-REQ-009. A full VM may be considered only for a
demonstrated security need. Closed components that prevent source, provenance,
native, supply-chain, or security review are not acceptable.

Classification is deliberately non-numeric:

- **Disqualifying:** a mandatory constraint is inherently violated or there is no
  credible correction without changing the architecture/product promise.
- **Major unresolved risk:** plausible in principle, but adoption is blocked by a
  material source, platform, maintenance, or security issue.
- **Requires prototype:** a falsifiable experiment can resolve a decisive unknown.
- **Potentially viable:** no known fatal issue at study scope; proof is still
  required and the label is not approval.

Fatal or near-fatal checks include root/privilege/ADB/framework requirements;
Privacy Decoy VPN ownership; mandatory routine re-signing; missing ARM64/imported
native execution; direct native/filesystem bypass; unsafe shared management
identity; unmediable mandatory host state; inadequate source/license/provenance or
opaque native code; indefensible hidden-API dependence; inability to fail closed;
and inherent disclosure of a real value the product promises to mediate. Unknown
is never treated as success.

## Candidate families

### 1. Revised bespoke user-space runtime

This cannot be the PR 4 loader with more hooks. A qualifying redesign would need
an artifact/split verifier; package/resource/classloader model; complete component
and lifecycle scheduler; per-tenant OS identities; a narrow authenticated broker;
Binder/provider/service façades; native loader and syscall/filesystem/socket
containment; capability ownership; generation-wide teardown; and an external-VPN
network gate. It would effectively reimplement a security-sensitive portion of
Android while remaining an ordinary app.

**Analysis:** Android exposes no ordinary-app primitive that both creates normal
third-party package semantics under a synthetic identity and interposes every
native syscall/Binder path. Isolated services provide useful UIDs but lack normal
application semantics. Hidden API/hooks are compatibility mechanisms, not a
kernel boundary. **Classification: Major gap; not shortlisted alone.** A future
platform primitive could change this, but incremental PR 4 work cannot.

### 2. Open-source user-space virtualization/container engines

The following is source/provenance review only. No code, dependency, or binary is
copied or approved.

| Project | Canonical source and immutable examined ref | License/provenance and maintenance observation | Security-relevant result |
|---|---|---|---|
| VirtualApp public lineage | asLody/[VirtualApp](https://github.com/asLody/VirtualApp/tree/85768db8e29b5c840f1ba795d09a9d510fb5d068), `85768db8e29b5c840f1ba795d09a9d510fb5d068` (`master`) | No root license observed; README says public engine updates stopped in 2017 and current line is commercial. Public-tree ancestry is visible but the grant, current source, binary provenance, dependencies, and security reporting are inadequate. | Public line is stale and hidden-API/native/ARM64/split/security coverage is unproved; commercial line is opaque. **Disqualifying.** |
| DroidPlugin | DroidPluginTeam/[DroidPlugin](https://github.com/DroidPluginTeam/DroidPlugin/tree/c6ebf652e0f73aa0e5746766e117e51efaf41dbd), `c6ebf652e0f73aa0e5746766e117e51efaf41dbd` (`master`, 2019-12-14) | Root LGPL-3.0 file observed; public ancestry/source is forkable. Canonical activity is stale; no current security process or API 31–37 maintenance established. README explicitly says native-layer hook is absent and native APKs may fail. | Imported native/direct-syscall requirement cannot be met by this architecture as published. **Disqualifying.** |
| FBlackBox original | FBlackBox/[BlackBox](https://github.com/FBlackBox/BlackBox/tree/a13734339f85a85b4400926f7142557cb6f97dd9), `a13734339f85a85b4400926f7142557cb6f97dd9` (`master`, 2024-04-12) | Canonical repository shows dissolution/deletion, no complete current engine tree or root license. An unknown fork cannot cure missing upstream license/provenance, native binary ancestry, dependencies, or security history. | Not auditable or safely forkable from canonical source. **Disqualifying.** |
| SpaceCore | FSpaceCore/[SpaceCore](https://github.com/FSpaceCore/SpaceCore/tree/3826a2fa1ac492fbbe7435ccb52074c1e2b702de), `3826a2fa1ac492fbbe7435ccb52074c1e2b702de` (`main`, 2024-02-01) | Public repository is a demo/integration shell with no root license; upstream describes the SDK as non-open-source. Claimed Android 6–14 and ARM32/ARM64 support does not reveal engine/native source, bundled binaries, transitive dependencies, vulnerabilities, or security reporting. | Opaque engine is inside the TCB and cannot be independently reviewed or replaced. **Disqualifying.** |
| chiyuan5 VirtualSpace | chiyuan5/[VirtualSpace](https://github.com/chiyuan5/VirtualSpace/tree/b1ff7988ac598b00b45c22003390ff43396c1c01), `b1ff7988ac598b00b45c22003390ff43396c1c01` (`main`, observed 2026-05-14) | Source visible; README says MIT but no root license file was observed, so grant/coverage is unresolved. Recent activity is not security maintenance. Small/new ancestry, native provenance, Maven graph, imported binaries, SBOM, and reporting process require audit. | README claims package/activity/service interception and C/C++ PLT/GOT hooks, but inconsistently maps Android 16 to API 35. ARM64, splits, API 31–37/OEMs, Binder, syscalls, multiprocess, signing, isolation, and fail-closed behavior are **Unknown**. **Major unresolved risk; conditional audit only.** |
| Blacks-BlackBox | Black00Z/[Blacks-BlackBox](https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9), `40282a7bf4500948cfd598fc67e6e63114b26dd9` (2026-05-12) | Public source and root Apache-2.0 text; BlackBox/NewBlackbox-derived lineage with unresolved inherited grants, native provenance and committed AARs. Recent compatibility work is not established security maintenance. | Modern Android/ARM64/profile/split/spoofing claims do not prove containment. **Major unresolved risk; non-executing source/provenance screening only.** |

Independent review therefore identified at least one additional current,
actively developed BlackBox-derived fork worth **source/provenance screening**.
This corrects the original network-limited conclusion; it does not establish a
source-complete, adequately licensed or secure engine. README feature lists are
not proof of lifecycle completeness, containment, native mediation or revocation.
User-space hook engines commonly execute tenants within host-defined identities;
the exact UID/process/resource and bypass behavior remains to be established.

#### Blacks-BlackBox: bounded evidence and unresolved risks

At the immutable ref above, the reviewer-verified [README](https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/README.md)
claims Android 14+/16 compatibility work, physical ARM64 as the primary validation
path, main/work-profile smoke validation, `.apks`/split import, device spoofing
and additional service hooks. It also says VPN mode is disabled on Android 14+
because forwarding is incomplete. These remain **UPSTREAM CLAIMS**, including
any statement that a modern application launches; none is security evidence.

The root [build.gradle](https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/build.gradle)
declares `compileSdkVersion = 35`, `targetSdkVersion = 28`, `minSdk = 21` and
`hiddenApiBypass = '4.3'`. The tree contains substantial C/C++ hook source and
committed `app/libs/catloading-release.aar` and
`app/libs/floatingview-release.aar`. These are **REPOSITORY OBSERVATIONS**.

| Screening question | Evidence and Privacy Decoy analysis |
|---|---|
| Ancestry and inherited grants | README credits/identifies NewBlackbox, BlackBox, VirtualApp, Dobby, xDL, BlackReflection and FreeReflection (also VirtualAPK). Acknowledgments do not establish file-level ancestry or rights. The root [Apache-2.0 text](https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/LICENSE) does not resolve inherited BlackBox/VirtualApp grants. Exact upstream revisions, notices, modifications and coverage are **Unknown / Major unresolved risk**. |
| Native source and libraries | Visible C/C++ permits inspection but does not prove all native inputs are present or reproducible. Map each hook/loader component to licensed upstream source and all generated/packaged native libraries to that source. Source completeness, library provenance and ABI behavior are **Unknown**. |
| AARs and binary-only TCB | The two named AARs need exact source/version, license/notice, build recipe, integrity and privilege/TCB mapping. Their names do not establish harmless UI-only behavior. Whether they or other inputs contain binary-only trusted code is **Unknown**, not proven absent; an irreplaceable opaque TCB component is disqualifying. |
| Maven/repositories | Desktop read-only inspection of [settings.gradle](https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/settings.gradle) shows JitPack, Aliyun mirrors, Google and Maven Central, plus the Gradle Plugin Portal for plugins. [app/build.gradle](https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/app/build.gradle) loads local JAR/AAR files and Maven artifacts. Complete transitive coordinates, source/license mapping, immutable verification and native contents remain **Unknown**; declarations are not an audited dependency graph. |
| Forkability/security maintenance | A public tree and recent compatibility changes make static questions answerable. Legally defensible forkability, reproducible source completeness, vulnerability response and maintained security coverage are **Unknown** until the inherited grant and supply-chain audit passes. |
| SDK/hidden-API strategy | Target 28 can retain target-dependent compatibility behavior on newer Android; compiling against 35 does not establish a modern target strategy. Android's [target-SDK semantics](https://developer.android.com/guide/topics/manifest/uses-sdk-element#target) and non-SDK restrictions require per-API analysis. The bypass declaration and hook claims leave private-interface dependence, API 31–37/OEM stability, target migration, install/distribution eligibility and fail-closed handling **Major unresolved risk**. Android 14/16 launch claims resolve none of these. |
| ARM64 and splits | Physical ARM64 and `.apks` import are README claims. Imported hostile JNI, native threads/loading, direct syscalls, complete split/signature/resource handling and release/OEM coverage are **Unknown**. |
| UID/process and management | OS UID/process mapping, tenant separation, management authority and shared resources are **Unknown**. A virtual package/user ID is not proof of an independent kernel UID; no safe management boundary is inferred. |
| Mediation and revocation | Complete Binder/service/provider, filesystem/direct-syscall, host-state and direct-network coverage is **Unknown**. Device spoofing and added hooks do not prove alternate paths are blocked. Pre-code policy, cached capabilities, engine death and fail-closed teardown require source mapping and later independent evidence. |
| Network/profile interaction | Disabled/incomplete VPN forwarding proves no external-route enforcement. Privacy Decoy still must not implement `VpnService`. Claimed work-profile support does not resolve grants, dependencies, shared in-engine identities, native bypass, persona mediation or fail-closed networking. Profile separation alone cannot supply a Decoy Persona. |

**Classification: Major unresolved risk.** The evidence supports only a
non-executing static audit; neither modern app compatibility nor the root license
justifies promotion to a runtime prototype or production selection. Do not
import/vendor, build or execute this engine under this PR.

VirtualSpace and Blacks-BlackBox are conditional static-audit comparators only.
Initial screening may inspect public metadata/source and license artifacts without
execution. Before deeper architecture auditing, establish unambiguous grants for
each complete immutable tree and inherited material. Before proposing any runtime
prototype, inventory every submodule, Maven artifact, native library/archive and
source origin, resolve binary/dependency provenance and hidden-API strategy, and
exclude opaque engine TCB content. Neither candidate is a shortlisted runtime.

### 3. Work-profile / managed-profile hybrid

**Platform facts:** Android managed profiles are separate users with separate app
data and OS-assigned UIDs; packages are installed into a profile and receive
normal Android package/component/native semantics. A device-policy controller
(DPC) administers the profile subject to provisioning and policy APIs. Cross-
profile interaction is deliberately controlled, but some device-wide hardware,
kernel, build, network and system-service state is shared. A work profile is not
a synthetic Android device and does not automatically virtualize `Build`, `/proc`,
`/sys`, properties, telephony, sensors, Play services, network identity, or every
Binder/provider result.

Reference implementations—not engines selected for reuse—are:

- PeterCxy/[Shelter](https://github.com/PeterCxy/Shelter/tree/672560f551772b5cd829b2947bae830d78f20edf),
  mirror ref `672560f551772b5cd829b2947bae830d78f20edf` (2026-06-02),
  GPL-3.0; canonical upstream is `https://gitea.angry.im/PeterCxy/Shelter`.
  Upstream describes maintenance-mode adaptation. Source is forkable subject to
  its license, but no use is proposed.
- Oasis Feng/[Island](https://github.com/oasisfeng/island/tree/d63538212a9f417c180bdb9258c0f5461de53c27),
  `d63538212a9f417c180bdb9258c0f5461de53c27` (2021-09-02), Apache-2.0.
  The stale public tree cannot establish current distributed-product behavior.
- secure-system/[Insular](https://gitlab.com/secure-system/Insular/-/tree/d46911c9),
  canonical GitLab `dev-ci` ref `d46911c9` (observed 2025-07-31), Apache-2.0;
  upstream identifies Island ancestry. Current dependency/native/provenance and
  security-process review remains required before reuse.

**Analysis:** profile isolation gives the strongest available ordinary-device
base for lifecycle, imported native execution, per-app UID/storage, and tenant
separation. It does **not** solve persona mediation. A hybrid must put management
authority outside protected app UIDs, expose only authenticated least-authority
brokers, deny uncontrolled cross-profile grants, and prove each mandatory host
surface can be replaced or blocked before code runs. If native code can still
read a forbidden real value, the hybrid fails rather than calling profile
isolation sufficient.

**Classification: Requires prototype; shortlisted as a platform-boundary
feasibility direction.** It survives initial disqualifiers because managed-profile
provisioning can be root-free, retains normal signed package/native semantics and
kernel UIDs, and does not require Privacy Decoy VPN ownership. Provisioning/device
policy availability, OEM behavior, user consent, external-VPN treatment and
mandatory persona mediation are decisive unknowns—not assumed advantages.

### 4. Full Android guest / VM

AVF/pKVM provides strong workload isolation on supported devices, but official
AVF documentation describes platform-managed virtual machines and Microdroid
workloads, not a generally available promise that an arbitrary ordinary app can
ship and host a complete Android UI guest across API 31–37 consumer devices.
Availability is hardware/OS dependent and older devices in the matrix predate
AVF. A full guest adds a guest image and update chain, boot/memory/storage cost,
ABI/device integration, UI/notification/background lifecycle mismatch, virtual
device/service design, external-VPN routing questions, and a much larger TCB and
supply-chain burden. Distribution of guest images and executable code also needs
separate current policy review.

**Classification: Disqualifying for the mandatory ordinary-app/API 31–37 matrix
on current evidence.** It may be revisited only if Android exposes a stable,
ordinary-app API across the approved matrix and a demonstrated security need
justifies the cost. It is not selected for convenience.

### 5. Hybrid architectures

- **Managed profile + hardened mediation/broker:** shortlisted for a controlled
  platform-boundary prototype. The profile supplies OS identities/lifecycle; it
  does not inherit a passing grade for persona mediation.
- **User-space virtualization + hardened broker:** a broker protects only its own
  capabilities. It does not stop direct Binder/syscall/filesystem/native paths or
  repair unsafe shared UIDs. **Major gap**, except as a conditional source-audit
  comparator.
- **Virtualization + external network broker:** preserves the external VPN model,
  but the broker becomes the traffic origin only for traffic it exclusively owns.
  Direct/native/helper traffic remains fatal. **Major gap.**
- **Managed profile + user-space engine:** the total TCB includes profile/DPC,
  engine, hooks/native code and brokers. Profile isolation does not validate a
  weak engine. Consider only after the profile experiment and a separately
  licensed source audit; not presently shortlisted.

## Requirements-to-architecture matrix

States mean exactly **Potentially viable**, **Requires prototype**, **Major gap**,
**Disqualifying**, or **Unknown**. `Profile hybrid` means OS-managed profile plus
a yet-unproven least-authority mediation layer. The existing `VirtualSpace`
column retains its prior scores, subject to the S2 license/provenance gate.
`Blacks-BlackBox` is separately recorded as `Unknown` for each unproved mandatory
property and `Major gap` for PD-REQ-060's unresolved provenance/SBOM. Its overall
candidate classification is **Major unresolved risk**. No cell authorizes runtime
work; the historical engines remain individually disqualified above.

| Requirement | Revised bespoke | VirtualSpace | Blacks-BlackBox | Profile hybrid | Full VM |
|---|---|---|---|---|---|
| PD-REQ-008 imported unchanged artifacts | Major gap | Requires prototype | Unknown | Potentially viable | Requires prototype |
| PD-REQ-011 adversarial process/UID/resources | Major gap | Major gap | Unknown | Requires prototype | Potentially viable |
| PD-REQ-013 Binder/service/provider mediation | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-014 native/syscall/proc/sys/filesystem | Disqualifying | Requires prototype | Unknown | Requires prototype | Potentially viable |
| PD-REQ-015 lifecycle/early init/revocation | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-019 reproducible claim evidence | Requires prototype | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-020 complete coverage classification | Unknown | Unknown | Unknown | Unknown | Unknown |
| PD-REQ-021 pre-code fail closed | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-027 capability revocation | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-030 virtual package universe | Major gap | Requires prototype | Unknown | Major gap | Potentially viable |
| PD-REQ-031 host services mediated/blocked | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-032 traffic producer attribution | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-033 required route fails closed | Requires prototype | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-034 all network stacks/paths evidenced | Unknown | Unknown | Unknown | Unknown | Unknown |
| PD-REQ-041 storage/native isolation | Major gap | Requires prototype | Unknown | Potentially viable | Potentially viable |
| PD-REQ-044 validation before any app code | Major gap | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-045 supervisor safety | Requires prototype | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-057 adversarial test program | Potentially viable | Potentially viable | Unknown | Potentially viable | Potentially viable |
| PD-REQ-058 independent/release/device evidence | Requires prototype | Requires prototype | Unknown | Requires prototype | Requires prototype |
| PD-REQ-060 reviewable engine boundary/SBOM | Potentially viable | Major gap | Major gap | Potentially viable | Major gap |
| PD-REQ-070 no built-in VPN/blocklist | Potentially viable | Unknown | Unknown | Potentially viable | Unknown |

The matrix does not declare any requirement satisfied. The bespoke native row is
disqualifying under the presently available ordinary-app primitives; this does
not mean all bespoke code is forbidden inside a profile-based hybrid.

## Shortlist and trusted computing bases

### Direction S1: managed-profile isolation plus least-authority mediation

**Why it survived:** it uses Android installation rather than DEX emulation, so
normal lifecycle, resources, splits, ABI-native loading, and per-package kernel
UID/storage semantics have a credible route without root or re-signing. It offers
a testable separation improvement rather than trusting hooks as containment.

**Provisional TCB and authority map:**

| Question | Prototype design answer |
|---|---|
| Protected execution identity | Each controlled probe is installed by the OS in the managed profile and executes in its own profile-scoped application UID/processes. |
| Management identity/state | Privacy Decoy management remains outside every protected UID; secrets/policy are not stored in shared/profile-readable locations. Exact personal/profile placement is an experiment variable. |
| Trusted components | Android kernel/user sandbox, package/component managers, managed-profile/DPC enforcement, minimal supervisor and authenticated broker, and configured external VPN/lockdown for network claims. |
| Hostile components | All protected Java/native/dynamic code, its subprocesses, SDKs and data; untrusted intents/providers; stale sessions. |
| Native loader | Android package/runtime linker under the protected app UID, never management. |
| Traffic origin | Protected app UIDs and any OS/helper/broker identities actually observed. A broker is not assumed to capture direct traffic. |
| Binder/service/provider mediation | OS profile/policy first; an explicit broker or deny rule only where an ordinary-app supported API can enforce it. Uninterposed paths fail the experiment. |
| Tenant isolation | Distinct OS app UIDs and data directories inside the profile; cross-app grants/providers and shared-user/signing cases are denied or separately proven. Logical directories do not count. |
| Revocation | Stop/disable/suspend or remove the profile/package as the coarse kernel-backed boundary; broker generations close owned FDs/Binder sessions. Tests must show stale Binder, files, sockets, threads and jobs cannot continue. |
| Trusted-component death | No restart grants authority; protected execution/networking remains disabled until policy, route and generation are revalidated. Bounded recovery only. |
| Shared identity/resources | No shared UID with management or peers; shared storage and cross-profile URI/intent channels are denied by default and inventoried. Device hardware/kernel remains shared. |
| OS/kernel | Trusted, as in the existing threat model. Compromised OS/kernel is excluded. |
| External lockdown | Required for any claim of no physical fallback unless independent evidence establishes an equally strong supported platform control. Privacy Decoy does not own `VpnService`. |

**Exact unknowns:** whether an ordinary DPC/profile deployment works consistently
on API 31–37 and the OEM matrix; which device-wide values remain observable; which
mandatory values can be replaced or blocked through supported APIs before
provider/native initialization; app/profile network attribution and lockdown
verification; cross-profile attack surface; revocation completeness; split/native/
multiprocess behavior; and direct/GitHub versus possible Play distribution.

**PASS:** on each tested scope, all required lifecycle/native entrypoints execute
under the recorded tenant UID; management/peer sentinels remain inaccessible;
every mandatory host surface is either demonstrably synthetic or blocked before
app code; all producer identities are attributable; stale handles/threads/jobs/
sockets cease after revocation; and required external routing fails closed with
independent packet evidence. Passing an early emulator slice only permits the next
physical/OEM slice; it does not pass the architecture.

**FAIL:** any pre-policy provider/native execution; real mandatory host value or
direct native/Binder/filesystem bypass; unsafe shared identity; surviving stale
capability; unattributed traffic; physical fallback; unsupported mandatory API/OEM
configuration; required privilege/ADB/root; or routine re-signing.

**Decision consequences:** a failure inherent to profiles triggers **REDESIGN
AGAIN**. If only a precisely enumerated application/API/device class fails,
**NARROW SCOPE** requires explicit user approval and pre-launch blocking. If the
core synthetic persona cannot be mediated against hostile native code on ordinary
devices, or the required matrix cannot fail closed, **STOP** under current goals.

### Direction S2: current open-source engine source/provenance audit

Choose one bounded **non-executing static audit** covering only VirtualSpace and
Blacks-BlackBox at the pinned refs above, with separate evidence and pass/fail
records. This is the smallest justified scope: both visible current trees pose
the same decisive grant/provenance/containment questions, so one checklist avoids
duplicating research tracks without combining their evidence or selecting an
engine based on compatibility. No broader candidate search is authorized.

**Why they survived this far:** public source and recent observed compatibility
activity permit screening, not a passing grade. Both remain **Major unresolved
risk**. Phase 1 examines license artifacts, ancestry and supply-chain provenance;
unresolved grants block deeper architecture audit for that candidate. VirtualSpace
needs explicit coverage beyond README MIT wording; Blacks-BlackBox needs inherited
grant coverage beyond its root Apache-2.0 text. No build, engine execution,
vendoring, dependency integration or protected-mode test is authorized. Binary,
dependency and hidden-API questions must be resolved before a runtime prototype
may even be proposed; each candidate stands or fails independently.

**TCB if it ever passed audit:** protected code would likely run in engine-defined
host processes/UIDs; management must be in a distinct OS UID; engine Java/native
loader, virtual package/component managers and brokers would be trusted; protected
code hostile; all direct Binder/syscall/filesystem/socket paths would need engine
or kernel control; tenants could not share a UID/resources without proven native
containment; all capabilities would need ownership/generation teardown; engine
death must terminate tenants; OS/kernel remains trusted; external lockdown would
be required unless independently disproved. These are questions, not observations.

**PASS for the audit only:** an explicit license covers the complete immutable
tree; ancestry and every binary/dependency are attributable and reproducible from
source; no opaque engine component exists; supported API 31–37 strategy is
defensible; code-path mapping identifies credible enforcement points for normal
lifecycle, Binder/provider/package, imported ARM64 native/syscall/filesystem,
tenant/management isolation, revocation and all network origins. Audit PASS merely
authorizes a later controlled engine prototype ADR/update.

**FAIL:** absent/ambiguous license; unexplained `.so`/archive; binary-only or
commercial dependency in the TCB; routine rewriting/re-signing; shared hostile
and management UID without credible native containment; hook-only direct-syscall
story; unavoidable prohibited framework; or no fail-closed version strategy.
Unresolved license/provenance **STOPs progression for that candidate** at static
screening; no build or execution follows. A confirmed unresolvable grant, opaque
TCB or architectural failure disqualifies that candidate, not the other S2
candidate or S1. Record unresolved evidence as Unknown/Major unresolved risk,
never audit PASS. If every auditable engine shares the same structural bypass,
**REDESIGN AGAIN** or **STOP**.

## Proposed experimental PR sequence (design only)

No experiment below is authorized by this document; each is a proposed, cohesive
research PR requiring its own review. No ordinary apps, accounts, or private data.

### Proposed Roadmap PR 8: managed-profile boundary probe

- **Objective/candidate:** test S1's OS identity/lifecycle/native/storage boundary
  and inventory unavoidable host-state leaks; do not build persona features.
- **Threat questions:** Does policy exist before `Application`, provider and native
  initialization? Are management and two tenants different UIDs/resources? Can
  JNI/direct syscalls/Binder/providers/shared storage cross them? What survives
  disable/suspend/profile stop/removal and broker death?
- **Likely repository areas:** adapt `probe-app`, `research-native`, sentinel and
  lifecycle markers, `PrototypeTests`, and a new *future* isolated experimental
  harness only after approval. Existing production `app` remains unexposed.
- **Third party/binaries:** no third-party engine source. Official SDK/emulator
  images and generated test APKs remain uncommitted. No committed binary. Any DPC
  reference code requires prior source/license/provenance review; prefer platform
  APIs and project-owned probe code.
- **Adversarial evidence:** early provider/receiver/service/job/alarm/notification/
  deep-link/share; split/multidex/dynamic code; imported ARM64 JNI, native threads,
  `dlopen`, direct syscalls, proc/sys/properties/FDs/sockets; Binder/provider/service
  enumeration; host/peer/management sentinel traversal; malformed/cross-tenant
  grants; cached Binder/file/socket and background work across policy generation,
  process/DPC death, reboot and profile transitions.
- **Matrix:** API 31 and 37 endpoints first, API 35 x86_64 engineering emulator,
  then non-rooted ARM64 Google/AOSP reference, Samsung and another materially
  different OEM; release-equivalent non-debuggable evidence is mandatory before
  any architecture gate. Record split set, signing lineage, ABI and package IDs.
- **Pass/fail/stop:** use S1 criteria above. A mandatory native leak or inability
  to establish policy before app code stops the PR as adverse evidence; do not add
  hooks until it turns green.

### Proposed Roadmap PR 9: managed-profile network and revocation probe

- **Objective:** only if PR 8 survives, determine every traffic identity and
  whether external routing/lockdown and coarse profile revocation fail closed.
- **Threat questions:** direct Java/native TCP/UDP IPv4/IPv6/DNS, Cronet/QUIC,
  subprocess, job/alarm/service/provider/helper/resolver traffic; cached sockets
  and FDs; VPN loss/replacement/reboot; profile quiet/disabled/removed states;
  broker/DPC death and stale persona/policy generations.
- **Reuse:** external VPN fixture, fixed endpoints, independent dual-interface
  pcap analyzer, provider replacement, generation/session attacks and socket
  closure checks. Adapt expected producer UIDs; never treat old results as proof.
- **Dependencies/binaries:** no Privacy Decoy `VpnService`, no third-party engine,
  no committed capture/native/APK. Any Cronet test dependency needs an immutable
  provenance/license/SBOM review first; a project-owned UDP QUIC-shaped probe is
  not equivalent to Cronet evidence.
- **PASS:** every observed producer is attributable; required platform
  always-on/lockdown state is independently verified; all required traffic enters
  the external VPN; loss/unverifiable state yields no physical packet including
  cached/native/subprocess traffic; revocation closes owned and direct resources.
  **FAIL/STOP:** any unattributed origin or physical fallback under the claimed
  supported configuration, or inability to verify/enforce required lockdown.

### Proposed Roadmap PR 8A (parallel in roadmap, not implementation): engine source audit

- **Objective:** screen only the two pinned S2 candidates, VirtualSpace and
  Blacks-BlackBox, without compiling or executing either; keep separate findings.
- **Phases/prerequisites:** first resolve full-tree and inherited grants from
  immutable license artifacts/upstream clarification. Stop a candidate with an
  unresolved grant before deeper architecture review. Inventory immutable source,
  ancestry, submodule/dependency locks, native/AAR provenance and reproducibility,
  binary TCB and security process. Resolve these and hidden-API/version strategy
  before proposing any later runtime prototype.
- **Review evidence:** source/module graph; lifecycle/component virtualization;
  package/signature/split behavior; process/UID map; native loader/hooks and direct
  syscall bypass; Binder/filesystem/network paths; hidden APIs by API level;
  ARM64/x86_64; multiprocess; revocation; signing mutation; Maven/native SBOM;
  replacement/forkability.
- **PASS/FAIL:** exactly S2 above. No code is copied, dependency added, or binary
  built. A PASS proposes a later separate controlled prototype; a FAIL records
  disqualification and ends the branch of research.

After the cohesive containment and network probes, another explicit feasibility
gate—not automatic implementation—must choose **PROCEED TO MORE PROTOTYPING,
REDESIGN AGAIN, NARROW SCOPE, or STOP**.

## Reuse of existing research assets

| Asset | Reuse rule |
|---|---|
| Hostile probe app and component/lifecycle markers | Adapt to normal profile installation and expand entrypoints; old DEX result is only a baseline. |
| Isolated-process probes | Reuse as a negative/control comparison, not as the profile tenant model. |
| Project-owned native research library | Adapt with ARM64 and direct-syscall/native-thread/dynamic-loading probes; generated `.so` remains uncommitted. |
| Binder/session adversarial tests | Reuse malformed, cross-session, stale epoch, death and confused-deputy patterns against new OS identities. |
| Sentinel storage tests | Reuse persistent independent observation; add peer/profile/shared-storage/provider/grant cases. |
| External VPN fixture and independent pcap | Reuse unchanged where possible; it remains separate and non-forwarding. Record all new profile producer identities. |
| VPN-loss/lockdown/provider-replacement tests | Adapt only after containment survives; preserve adverse non-lockdown baseline. |
| Generation/revocation tests | Expand from broker-owned resources to cached Binder, files, sockets, threads, jobs and component restarts. |

These assets are adversarial baselines. Passing them under PR 4/5 proves nothing
about a new architecture, device, API, ABI, build type, or package.

## Platform and distribution gates

The investigation remains API 31–37, ARM64 physical devices plus x86_64 engineering
emulator, Google/AOSP reference hardware, Samsung and another materially different
OEM, release-equivalent non-debuggable builds, and base plus representative signed
split APK/APKS. Direct distribution/GitHub Releases is only an investigation path;
release authenticity/updating remains future work. Possible Google Play
distribution is **Unknown**: DPC/provisioning, package visibility, dynamic code,
non-SDK usage, downloaded executable code, permissions and SDK behavior must be
checked against policy current at submission time. No eligibility is claimed.

For every result record API/patch level, OEM/device/kernel, ABI, build/signing,
profile/provisioning mode, complete package artifacts, process/UID map, policy and
persona generation, external VPN/lockdown state, and independent observer. An
emulator or debug pass cannot establish physical/release support.

## Final decision

**Outcome A — prototype directions identified / no production architecture
selected.** S1 is the sole executable architecture direction: managed-profile
kernel isolation plus a least-authority mediation boundary, tested first for
fatal native/persona leaks. S2 is only a conditional, non-executing source audit
of VirtualSpace and Blacks-BlackBox with independent outcomes. Both are **Major
unresolved risk**; initial public-source/license screening is allowed, but
unresolved grants block deeper audit and unresolved provenance, binary/dependency
or hidden-API questions block any runtime prototype proposal.

The historical engines outside S2 and the full VM are disqualified on current
evidence;
standalone bespoke/user-space virtualization hybrids retain structural major
gaps. Ordinary protected apps remain blocked. If S1 cannot prevent or block
mandatory real host state for hostile native code, and S2 supplies no fully
auditable enforceable alternative, the evidence requires **STOP** or an explicit
user-approved **NARROW SCOPE** decision—not another optimistic implementation
sequence.
