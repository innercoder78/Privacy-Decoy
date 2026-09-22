# ADR-0006 final architecture-discovery synthesis

**Research date:** 2026-09-22
**Recommended exit outcome:** **STOP UNDER CURRENT GOALS**

## 1. Exact repository starting state

The mandatory preflight ran before editing:

```text
$ git status --short --branch
## work
$ git rev-parse HEAD
019a9bf6db6e91bd1fd755aa56dd1187bc160c8a
$ git log -1 --oneline
019a9bf Merge pull request #17 from innercoder78/codex/perform-static-architecture-falsification-review
```

**Repository fact:** the worktree was clean and `HEAD` exactly matched the
required commit. This is the final bounded synthesis authorized by
[ADR-0006](../decisions/ADR-0006-redesign-again-architecture-discovery.md), not
a prototype, implementation, or project-owner decision.

## 2. Governing constraints

**Repository fact:** [PD-REQ-001 through PD-REQ-085](../requirements.md) remain
unchanged. Privacy precedes compatibility; mandatory Unknown coverage blocks
execution; genuine-host fallback and fail-open behavior are forbidden.
Production cannot depend on root, guest root, Magisk, Xposed, LSPosed, custom
ROM, patched kernel, privileged/system installation, production ADB, or a
Privacy Decoy `VpnService`. Routine rewriting/re-signing has no exception, and
an opaque/unreviewable engine or native TCB cannot be trusted.

The original PR4/5 architecture remains **REDESIGN**. No production architecture
is selected; canonical production Roadmap PR 6 has not started; canonical PR 20
has not occurred; implementation remains paused; and ordinary protected apps,
accounts, and private data remain prohibited. Nothing here satisfies a
requirement. A viable boundary must jointly provide pre-code protection,
native/syscall/Binder/filesystem mediation, management and tenant isolation,
normal package semantics, safe revocation, and attributable external-VPN
fail-closed networking.

## 3. Completed discovery evidence

These preserved findings are not rewritten:

* **Prior evidence:** [PR4](pr4-containment-prototype.md) showed selected DEX in
  an isolated service is not an ordinary Android app runtime and did not prove
  package semantics or complete containment.
* **Prior evidence:** [PR5](pr5-network-feasibility.md) established useful
  controlled research and a fail-closed gate model, not production networking.
  VPN presence alone is not route enforcement.
* **Prior evidence:** [S1](pr8-managed-profile-boundary.md) is **FALSIFIED**.
  Both tenants exposed all seven mandatory parent Build fields. Its
  networking/revocation follow-up remains **BLOCKED**.
* **Prior evidence:** [S2](pr8a-engine-source-provenance-audit.md) audit-cleared
  no engine. Public VirtualApp, DroidPlugin, original FBlackBox, SpaceCore, and
  the studied full-guest direction retain the dispositions in the
  [redesign study](../architecture-redesign-study.md) and
  [engine assessment](../engine-assessment.md).
* **Prior evidence:** Blacks-BlackBox remains **STOPPED_UNRESOLVED** after its
  [provenance attempt](architecture-discovery-blacks-blackbox-provenance.md).
* **Prior evidence:** VirtualSpace pin
  `b1ff7988ac598b00b45c22003390ff43396c1c01` is **DISQUALIFIED — exact pinned
  candidate**. Its [static review](architecture-discovery-virtualspace-static-falsification.md)
  found native/service placeholders, restored genuine Binder objects, and
  genuine-host-context fallback. Repair is an architectural rewrite.

This does not prove every conceivable implementation impossible. It establishes
that no reviewed engine supplies the boundary and makes platform authority—not
candidate count—the decisive question.

## 4. Current Android-platform primitives

Official-document retrieval was attempted but failed with HTTP 401. This
synthesis does **not** claim independent retrieval on 2026-09-22. It uses the
independently reviewer-verified Android/AOSP facts supplied for this review,
official references below, and repository-canonical evidence.

* **Official Android platform fact:** the [application sandbox][app-sandbox]
  assigns each installed app an OS UID/process boundary. It does not let one
  ordinary app create arbitrary installed identities or interpose on another's
  syscalls/Binder calls.
* **Official Android platform fact:** [work profiles][work-profile] provide
  platform-managed user separation, app data/instances, policy, and lifecycle.
  They do not replace device surfaces with a Persona; S1 falsified that claim.
* **Official Android platform fact:** package components are installed and
  instantiated by Android. [Application fundamentals][app-fundamentals] expose
  no ordinary-app API to become Package/Activity Manager for arbitrary APKs.
* **Official Android platform fact:** a declared `isolatedProcess` service gets
  a restricted identity, not arbitrary-package installation or a complete
  Activity/provider/job/alarm/application runtime.
* **Official Android platform fact:** [non-SDK restrictions][non-sdk] constrain
  private interfaces; hidden-API hooks are not stable supported enforcement.
* **Official platform fact (reviewer-verified):** [SDK Sandbox][sdk-sandbox]
  loads host-declared SDKs in a separate UID range/process, not arbitrary apps.
  `SdkSandboxManager` is deprecated in API 37 and the sandbox is unsupported there.
* **Official AOSP fact (reviewer-verified; documentation updated 2026-06-25):**
  [AVF][avf] provides pVMs stronger than the app sandbox, is ARM64-only, and has
  optional VirtualizationService APIs only on AVF devices.
  [Microdroid][microdroid] is a Google mini-Android OS in a pVM.

**Analysis:** UID, profile, and isolated-process primitives offer real isolation,
not general hostile-app interposition. AVF adds a stronger hypervisor boundary;
that alone does not establish a general Android app runtime or deployable product.

## 5. Architecture-family reconciliation

### Managed-profile / Android Enterprise

Profiles offer real user, installed-identity, storage, lifecycle, and background
isolation. However, S1 establishes a **positive architectural contradiction**:
mandatory genuine state was visible before Decoy mediation. No documented
ordinary-app addition intercepts every Java, native/direct-syscall, Binder,
property, and early-init path. Device policy manages apps but is not syscall or
framework-service virtualization. “Add hooks” preserves the bypass. This family
is rejected under current goals without denying its genuine OS isolation.

### User-space virtualization / hook engines

VirtualApp is stale/incomplete for assurance; DroidPlugin lacks the relevant
native layer; original FBlackBox lacks auditable current source/license;
SpaceCore is opaque; Blacks-BlackBox failed admission; and pinned VirtualSpace
has positive core failures. Those candidate findings are not universal proof.

The structural problem is nevertheless concrete. An ordinary host can run code,
proxy selected objects, and emulate package metadata. It cannot request normal
independent installed-package UIDs, become the kernel syscall/filesystem/property
mediator, or replace Package Manager, Activity Manager, and Binder authority.
Java hooks cannot control hostile JNI, direct syscalls, inherited descriptors,
early native code, or Binder obtained elsewhere. Synthetic IDs are not kernel
credentials. No specified design simultaneously supplies normal semantics,
kernel tenants, management separation, pre-code/native/Binder mediation,
revocation, and attributable networking without prohibited privilege/mutation.

Existing engines have **evidence/provenance blockers** and/or **positive
architectural contradictions**. A hypothetical rewrite faces **unavailable
required platform authority**, not merely missing implementation.

### Revised bespoke Privacy Decoy runtime

Fresh code can improve provenance, policy, brokers, and quality, but gains no
privilege. Ordinary primitives do not supply arbitrary-package tenant UIDs,
comprehensive syscall/Binder interposition, component scheduling, or revocation
of OS capabilities held by hostile code. Rebuilding Android services in one app
still runs above the same kernel identity. Supporting pieces are
**implementation missing but theoretically available**; the decisive gap is
**unavailable required platform authority**.

### Isolated-process / sandbox primitives

An isolated service is useful for a narrow helper. It does not install an
arbitrary package, preserve normal package/signing identity, or provide its
Activities, providers, jobs, alarms, services, native/multiprocess behavior, and
Persona services. Treating it as a full runtime is a **positive architectural
contradiction**. As one component, the general boundary stays **Unknown** absent
another permitted mechanism.

### SDK Sandbox

It accepts host-declared SDKs, not arbitrary app artifacts, and lacks app
components. API-37 no-support also conflicts with the provisional API-37
investigation baseline. This is a **positive architectural contradiction** plus
**unsupported deployment scope**.

### AVF / pKVM / Microdroid

AVF is strongest because a pVM can contain hostile native code below syscalls.
The facts establish optional ARM64 AVF and mini-Android Microdroid. They do not
establish a root-free ordinary-app route for arbitrary APKs with full framework,
UI, resources, providers, jobs, alarms, services, background/Play/OEM services,
package lifecycle, and multiprocess semantics. Guest distribution/maintenance,
UI integration, and external-VPN verification for all host/pVM producers are
also unestablished.

pVM isolation exists on a subset, but application semantics and VPN integration
remain **Unknown**; availability is **unsupported deployment scope**; and the
required general-app facility is **unavailable required platform authority**
under current evidence. A prototype cannot test an unspecified
facility into existence; positive platform evidence must identify it first.

### Full Android guest / VM

A maintained full guest could conceptually supply kernel identities, framework
semantics, and containment. Evidence identifies no ordinary-user root-free route
across a defensible matrix without custom ROM, privilege, guest root, production
ADB, or prohibited setup. Microdroid is not automatically a full guest. Images,
patching, UI, storage, lifecycle, resource burden, VPN routing, and helpers
expand the TCB. A developer emulator is insufficient. This remains
**unsupported deployment scope** and **unavailable required platform authority**;
integration/networking are **Unknown**. Prior disposition is not reversed.

### Any genuinely new primitive

No current documented ordinary-app primitive materially different from profiles,
app/isolated sandboxes, SDK Sandbox, or AVF was identified. OEM-private APIs,
rooted tools, papers, and desktop virtualization do not qualify. This does not
predict future Android capability.

## 6. Structural versus implementation gaps

| Direction | Decisive classification | Reason |
|---|---|---|
| Profile alone/plus hooks | Positive architectural contradiction | Genuine state observed; hooks miss native/Binder/early paths. |
| Reviewed hook engines | Evidence/provenance blocker and/or positive contradiction | Admission failures coexist with unsafe/missing enforcement. |
| Hypothetical complete hooks | Unavailable required platform authority | No kernel identity, interposition, and framework authority primitive. |
| Bespoke ordinary runtime | Unavailable required platform authority | Reimplementation grants no kernel/framework control. |
| Isolated service | Positive architectural contradiction | Selected code is not arbitrary app execution. |
| SDK Sandbox | Positive contradiction; unsupported deployment scope | SDK model is not app model; unsupported at API 37. |
| AVF/Microdroid | Unsupported deployment scope; Unknown | Isolation exists; general-app semantics/integration do not. |
| Full guest | Unsupported deployment scope; unavailable authority | No permitted ordinary-user route is evidenced. |

Implementable support pieces cannot repair structural authority. Unknown is not
failure, but PD-REQ-021 makes a mandatory Unknown launch-blocking rather than a
credible route.

## 7. Management and tenant isolation analysis

Installed UIDs, profiles, and pVMs are genuine separation; synthetic identities
and directories are not. Isolated processes protect a narrow manager only if a
worker has no other authority; they do not create an application tenant.
Profiles fail Persona mediation, ordinary runtimes fail kernel identity, and AVF
lacks evidenced application semantics. No family combines all requirements.

## 8. Native/syscall/Binder/filesystem analysis

Protection after `Application` begins is too late for providers, initializers,
SDKs, loading, and secondary processes. Proxies cannot be assumed to cover JNI,
direct syscalls, `/proc`, `/sys`, properties, sockets, descriptors, or raw
Binder. A VM can contain these only with a mediated coherent service universe.
Profiles expose real state; hooks lack authority; Microdroid has no evidenced
arbitrary-app universe. Host-value fallback remains forbidden.

## 9. Application-semantics analysis

The unit is an ordinary signed package with splits, multidex, resources, native
libraries, components, jobs, background work, and multiprocess lifecycle. DEX
dispatch, isolated code, SDK loading, and mini-Android workloads differ. Hooks
emulate pieces without an enforceable boundary; a full guest lacks permitted
deployment. Routine rewriting/re-signing cannot close the gap.

## 10. External-VPN/networking analysis

A survivor must enumerate protected UIDs, subprocess/native traffic, brokers,
helpers, profiles, and VM endpoints, then verify required external-VPN route and
lockdown for each across IPv4/6, DNS, TCP, UDP, QUIC/Cronet, background, stale
sockets, loss, replacement, and reboot. Uncertainty blocks; Privacy Decoy cannot
implement `VpnService`.

PR5 offers methods, not universal enforcement. Hooks cannot attribute hostile
native traffic sharing a host UID. Profiles do not solve Persona mediation.
AVF/full guests add VM networking/helpers whose host-VPN interaction is
**Unknown**. VPN presence rescues no family.

## 11. Supply-chain/provenance implications

No engine is audit-cleared. Blacks-BlackBox cannot enter the TCB with unresolved
source, grants, ancestry, dependencies, AARs, and native archives. VirtualSpace's
architecture result does not erase prior provenance uncertainty. SpaceCore is
opaque. A VM expands provenance to guest image, framework, updates, native code,
and patching. Bespoke source can solve provenance, not authority.

## 12. Narrowing analysis

No narrowing is endorsed or applied. Options were tested for precise/reliably
pre-code-detectable exclusion, retained privacy, no fallback, meaningful utility,
and required owner approval.

* **Java-only/no-native:** manifest inspection cannot exclude dynamic/transitive
  native code, loading/generation, raw Binder, or Java-visible genuine state.
  Policing future loading requires the missing boundary.
* **Permission-defined apps:** permissions omit Build, property, filesystem,
  Binder, network, and other paths; privacy is not preserved.
* **Selected DEX/plugin or host SDK workloads:** definable, but not the arbitrary
  protected-app product and not meaningfully the current product.
* **Managed-profile-compatible apps:** installation constraints do not repair
  genuine device exposure.
* **ARM64 AVF devices/APIs/OEMs:** capability is plausibly detectable, but no
  general app runtime, lifecycle/UI, or VPN boundary exists in evidence.
* **One device/full guest:** no supported nonprivileged ordinary-user deployment
  is evidenced; developer-only operation is not the product.

No credible remainder retains every guarantee, so **NARROW SCOPE DECISION
REQUIRED** is not supported. A future ordinary-app general guest on a detectable
AVF subset could justify a new decision; it is not current evidence or approval.

## 13. Surviving hypotheses, if any

None survives ADR-0006 admission. AVF is an area of platform interest, not a
surviving Privacy Decoy architecture: isolation is supported, while general-app
runtime, deployment, and networking are not. A hypothetical engine/rewrite/guest
is not credible without a concrete permitted primitive.

## 14. Remaining Unknowns

Unknowns remain: whether future Android exposes a general-app guest or stronger
mediation API; whether a future narrowed AVF matrix supplies full UI/lifecycle
and verifiable VPN routing; future materially different engines after provenance
admission; untested OEM paths; and cost/performance after a new capability.
None supplies a current mechanism or falsifiable experiment meeting **PROCEED TO
BOUNDED PROTOTYPE**.

## 15. Explicit non-authorization

This authorizes no prototype, build, execution, integration, dependency, binary,
Android change, protected app/data/account, claim, canonical PR 6, implementation,
requirement/scope change, privilege, rewriting, `VpnService`, or fallback. The
recommendation is research, not fabricated owner acceptance. A future platform
capability can justify a new decision; this is not universal impossibility.

[app-sandbox]: https://source.android.com/docs/security/app-sandbox
[work-profile]: https://developer.android.com/work/managed-profiles
[app-fundamentals]: https://developer.android.com/guide/components/fundamentals
[non-sdk]: https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces
[sdk-sandbox]: https://developer.android.com/design-for-safety/privacy-sandbox/sdk-runtime
[avf]: https://source.android.com/docs/core/virtualization
[microdroid]: https://source.android.com/docs/core/virtualization/microdroid

## 16. Final recommended ADR-0006 exit outcome

## **STOP UNDER CURRENT GOALS**

Under current Android mechanisms/evidence, current goals/constraints, and the
ordinary non-rooted-device model, no permitted architecture has a credible route
through every boundary. Ordinary profile/hook designs lack kernel/framework
authority; SDK Sandbox and isolated processes are not arbitrary app runtimes; no
reviewed engine is trustworthy; and AVF/full guests lack an evidenced root-free
general-app facility, supported matrix, and VPN integration.

The decisive conjunction is positive S1 and pinned-VirtualSpace failures,
unresolved/disqualifying engine evidence, and no concrete permitted primitive
joining kernel containment, normal Android semantics, and fail-closed networking.
This follows mechanisms, not preference or candidate count. Evidence supports
stopping under current goals rather than weakening requirements. Only an owner
response or separately authorized future step based on new evidence can change
the pause.
