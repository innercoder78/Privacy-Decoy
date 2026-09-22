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
Privacy Decoy `VpnService`. Routine APK rewriting/re-signing remains disfavored.
No exception is authorized by this synthesis; any narrow future exception
requires a dedicated ADR and explicit project-owner approval under PD-REQ-009.
There is no blanket exception. An opaque/unreviewable engine or native TCB
cannot be trusted.

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

The original synthesis's official-document retrieval attempt failed with HTTP
401; its platform facts used independently reviewer-verified sources and
repository-canonical evidence. During this correction on 2026-09-22, the AOSP
AVF overview, AVF Security, and Microdroid pages were successfully retrieved
and checked, corroborating the independent PR review. Other platform facts
below retain their reviewer-verified provenance.

* **Official Android platform fact:** the [application sandbox][app-sandbox]
  assigns application UIDs and uses kernel-enforced process sandboxing to
  isolate applications. **PRIVACY DECOY ANALYSIS:** the documented ordinary
  third-party-app model supplies no authority to create arbitrary protected-package
  kernel identities or interpose all another app's syscalls and Binder traffic.
* **Official Android platform fact:** [work profiles][work-profile] provide
  platform-managed user separation, app data/instances, policy, and lifecycle.
  **Prior evidence:** completed S1 observed genuine device fields in both
  tenants. **PRIVACY DECOY ANALYSIS:** profiles do not supply the required Decoy
  Persona; this conclusion rests on S1, not a platform-documentation claim.
* **Official Android platform fact:** [application fundamentals][app-fundamentals]
  describe Android-managed application processes and components, including
  Activities, services, broadcast receivers, and content providers.
  **PRIVACY DECOY ANALYSIS:** no reviewed supported ordinary-app mechanism in
  that public API/platform model lets Privacy Decoy become Package Manager or
  Activity Manager for arbitrary protected APKs.
* **Official Android platform fact:** a declared `isolatedProcess` service runs
  in a restricted isolated process/identity. **PRIVACY DECOY ANALYSIS:** this
  primitive does not itself provide arbitrary-package installation or a complete
  Activity/provider/job/alarm/Application lifecycle for ordinary protected apps.
* **Official Android platform fact:** [non-SDK restrictions][non-sdk] restrict
  access to private interfaces. **PRIVACY DECOY ANALYSIS:** hidden/private-interface
  hooks cannot be accepted as stable, supported enforcement without separate evidence.
* **Official platform fact (reviewer-verified):** [SDK Sandbox][sdk-sandbox]
  loads host-declared SDKs in a separate UID range/process.
  `SdkSandboxManager` is deprecated in API 37 and the sandbox is unsupported there.
  **PRIVACY DECOY ANALYSIS:** SDK loading does not provide arbitrary-app execution.
* **OFFICIAL AOSP FACT:** [AVF][avf] provides stronger isolation than the app
  sandbox, supports ARM64 devices only, and exposes optional VirtualizationService
  Java APIs only on AVF-capable devices. Microdroid is Google's mini-Android pVM OS.
* **OFFICIAL AOSP FACT:** [AVF Security][avf-security] requires pVM permissions
  for creation or inspection; requesting permission to create, own, or interact
  with pVMs is restricted to platform-signed apps. Host VirtualizationService
  alone establishes pVM communication channels and can pass them to others.
  **PRIVACY DECOY ANALYSIS:** this authority is unavailable to the current
  ordinary third-party-app product, which prohibits privileged/system deployment.
* **OFFICIAL AOSP FACT:** [Microdroid][microdroid] primarily isolates part of an
  app. It supports APK-embedded binaries/shared libraries, a subset of NDK APIs,
  Binder RPC over vsock, Verified Boot, and SELinux. It lacks Android `android.*`
  Java APIs, SystemServer/Zygote, graphics/UI, and HALs.
  **PRIVACY DECOY ANALYSIS:** these documented limitations prevent Microdroid
  itself from supplying the full ordinary Android runtime needed for Activities,
  providers, framework APIs, UI, services, and package lifecycle.

**PRIVACY DECOY ANALYSIS:** UID, profile, and isolated-process primitives offer real isolation,
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

**PRIVACY DECOY ANALYSIS:** AVF remains technically interesting because a pVM
can contain hostile native code below syscalls. Under the current AOSP permission
model in section 4, permission to create, own, or interact with pVMs is restricted
to platform-signed apps. For the current ordinary third-party-app product, this
is **unavailable required platform authority**, independently of runtime and
networking questions. Privileged/system deployment is prohibited.

Microdroid's documented omissions are a separate **positive architectural
contradiction** to treating it as the full ordinary Android app runtime. A
different general-app guest, its distribution/maintenance, UI/lifecycle
integration, and external-VPN routing for every host/pVM producer remain
**Unknown**. Optional ARM64 availability also limits deployment scope. These
findings concern current AOSP and current product constraints, not every future
Android or OEM design. A prototype cannot supply missing authority; a new
permitted platform capability would need positive evidence first.

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
| AVF/Microdroid | Unavailable required platform authority; positive architectural contradiction | Platform signing gates pVM permission; Microdroid omits the full app runtime. Other guest integration/VPN routing remain Unknown; ARM64/optional APIs limit scope. |
| Full guest | Unsupported deployment scope; unavailable authority | No permitted ordinary-user route is evidenced. |

Implementable support pieces cannot repair structural authority. Unknown is not
failure, but PD-REQ-021 makes a mandatory Unknown launch-blocking rather than a
credible route.

## 7. Management and tenant isolation analysis

Installed UIDs, profiles, and pVMs are genuine separation; synthetic identities
and directories are not. Isolated processes protect a narrow manager only if a
worker has no other authority; they do not create an application tenant.
Profiles fail Persona mediation and ordinary runtimes fail kernel identity.
Current AOSP denies the required AVF authority to ordinary third-party apps;
Microdroid separately lacks the full app runtime. No family combines all requirements.

## 8. Native/syscall/Binder/filesystem analysis

Protection after `Application` begins is too late for providers, initializers,
SDKs, loading, and secondary processes. Proxies cannot be assumed to cover JNI,
direct syscalls, `/proc`, `/sys`, properties, sockets, descriptors, or raw
Binder. A VM can contain these only with a mediated coherent service universe.
Profiles expose real state; hooks lack authority; Microdroid's documented runtime
omissions preclude a complete arbitrary-app service universe. Current AOSP pVM
permission also requires platform signing. Host-value fallback remains forbidden.

## 9. Application-semantics analysis

The unit is an ordinary signed package with splits, multidex, resources, native
libraries, components, jobs, background work, and multiprocess lifecycle. DEX
dispatch, isolated code, SDK loading, and mini-Android workloads differ. Hooks
emulate pieces without an enforceable boundary; a full guest lacks permitted
deployment. Microdroid's documented runtime omissions are a positive limitation,
separate from the current AOSP pVM permission barrier. Routine rewriting/re-signing
does not supply that authority or runtime; no exception is authorized here, and
any narrow future exception remains subject to PD-REQ-009's ADR and explicit
project-owner approval process.

## 10. External-VPN/networking analysis

A survivor must enumerate protected UIDs, subprocess/native traffic, brokers,
helpers, profiles, and VM endpoints, then verify required external-VPN route and
lockdown for each across IPv4/6, DNS, TCP, UDP, QUIC/Cronet, background, stale
sockets, loss, replacement, and reboot. Uncertainty blocks; Privacy Decoy cannot
implement `VpnService`.

PR5 offers methods, not universal enforcement. Hooks cannot attribute hostile
native traffic sharing a host UID. Profiles do not solve Persona mediation.
AVF/full guests add VM networking/helpers whose host-VPN interaction is
**Unknown**. This uncertainty is separate from the documented platform-signing
restriction: resolving routing alone would not supply current AOSP pVM authority
to the ordinary-app product. VPN presence rescues no family.

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
* **ARM64 AVF devices/APIs/OEMs:** detecting AVF capability does not remove the
  current AOSP platform-signing permission restriction. Microdroid still omits
  the full app runtime; other guest lifecycle/UI and VPN integration remain
  Unknown. A different OEM authority model would need separate positive evidence.
* **One device/full guest:** no supported nonprivileged ordinary-user deployment
  is evidenced; developer-only operation is not the product.

No credible remainder retains every guarantee, so **NARROW SCOPE DECISION
REQUIRED** is not supported. A future ordinary-app general guest on a detectable
AVF subset could justify a new decision; it is not current evidence or approval.

## 13. Surviving hypotheses, if any

None survives ADR-0006 admission. AVF is an area of platform interest, not a
surviving Privacy Decoy architecture: current AOSP platform-signing restrictions
independently block ordinary-app authority, Microdroid omits the required full
runtime, and other guest integration/networking remain Unknown. A hypothetical engine/rewrite/guest
is not credible without a concrete permitted primitive.

## 14. Remaining Unknowns

Unknowns remain: whether future Android exposes a general-app guest or stronger
mediation API; whether a future narrowed AVF matrix supplies full UI/lifecycle
and verifiable VPN routing; future materially different engines after provenance
admission; untested OEM paths; and cost/performance after a new capability.
These Unknowns do not make the documented current AOSP signing restriction or
Microdroid omissions Unknown, nor establish that future OEM designs must match.
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
[avf-security]: https://source.android.com/docs/core/virtualization/security

## 16. Final recommended ADR-0006 exit outcome

## **STOP UNDER CURRENT GOALS**

Under current Android mechanisms/evidence, current goals/constraints, and the
ordinary non-rooted-device model, no permitted architecture has a credible route
through every boundary. Ordinary profile/hook designs lack kernel/framework
authority; SDK Sandbox and isolated processes are not arbitrary app runtimes; no
reviewed engine is trustworthy; current AOSP restricts required pVM authority to
platform-signed apps; and Microdroid lacks full Android application semantics.
Other full-guest deployment/integration and external-VPN routing remain
unestablished or Unknown as classified above. This is a research recommendation,
not project-owner acceptance or a universal/mathematical impossibility claim;
materially new Android platform capability could justify another decision.

The decisive conjunction is positive S1 and pinned-VirtualSpace failures,
unresolved/disqualifying engine evidence, and no concrete permitted primitive
joining kernel containment, normal Android semantics, and fail-closed networking.
This follows mechanisms, not preference or candidate count. Evidence supports
stopping under current goals rather than weakening requirements. Only an owner
response or separately authorized future step based on new evidence can change
the pause.
