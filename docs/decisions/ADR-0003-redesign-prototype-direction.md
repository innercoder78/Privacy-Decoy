# ADR-0003: Redesign prototype direction

- **Status:** Proposed — PROTOTYPE DIRECTIONS IDENTIFIED / NO PRODUCTION ARCHITECTURE SELECTED
- **Date:** 2026-09-21
- **Roadmap:** PR 7

## Problem and context

[ADR-0002](ADR-0002-feasibility-stop-gate.md) accepted **A — REDESIGN** because
the PR 4/5 architecture lacks normal Android application semantics, imported
native containment, broad host-state mediation, complete revocation, safe
management isolation, and independently enforceable networking without an
external-lockdown dependency. Continuing the old Core Domain/Persistence sequence
would mistake narrow research mechanisms for a product boundary.

The [redesign study](../architecture-redesign-study.md) compares a fundamentally
revised bespoke runtime, open-source user-space engines, an OS-managed-profile
hybrid, a full Android guest/VM, and combinations of those mechanisms. It preserves
PD-REQ-001 through PD-REQ-070 unchanged.

## Decision

Authorize planning—not implementation or production selection—for two bounded
research directions:

1. **S1: managed-profile isolation plus least-authority mediation.** A controlled
   probe may test whether normal OS installation/profile UIDs supply lifecycle,
   split/native and storage isolation while supported ordinary-app mechanisms can
   replace or block every mandatory real host-state path before protected code.
2. **S2: current open-source engine source/provenance audit.** One bounded,
   non-executing audit covers VirtualSpace and Blacks-BlackBox at the study's
   immutable refs, with separate evidence and outcomes. Initial license/ancestry
   screening must establish complete inherited grants before deeper architecture
   review. License, native/binary/dependency provenance and hidden-API questions
   must be resolved before any runtime prototype is proposed. Audit success only
   permits that later proposal, never execution or integration under this PR.

Independent PR review on 2026-09-21 revalidated the study's listed source refs and
identified Black00Z/Blacks-BlackBox at
`40282a7bf4500948cfd598fc67e6e63114b26dd9` (2026-05-12). Its public tree and
root Apache-2.0 text warrant screening, but do not resolve inherited
BlackBox/VirtualApp licensing, native/AAR provenance or containment. Both S2
candidates remain **Major unresolved risk**. A shared audit checklist with
independent outcomes is narrower than two separate research tracks; neither
modern app launches nor profile-support claims select an engine.

This is **not** a production architecture/engine decision. It does not authorize
ordinary applications, accounts, private data, third-party integration, vendoring,
binaries, permissions, dependencies, or runtime changes.

## Disqualified and deferred directions

- Historical public VirtualApp and its opaque commercial successor are
  disqualified by stale/incomplete public source and unresolved licensing or
  reviewability.
- DroidPlugin is disqualified by its published lack of native-layer support and
  stale modern-platform evidence.
- Original FBlackBox and SpaceCore are disqualified by unavailable/incomplete or
  opaque engine source and inadequate license/provenance reviewability.
- A standalone revised bespoke runtime and hook-engine/broker hybrids have major
  structural gaps: ordinary-app APIs do not provide both full package semantics
  and comprehensive native/Binder/syscall interposition.
- A full Android guest is disqualified for the required ordinary-app/API 31–37
  matrix on current evidence; AVF is not evidence of a generally deployable full
  Android guest API for this product.

## Security assumptions and prototype TCB

The Android kernel/OS and verified boot base remain trusted; protected Java,
native, dynamic and subprocess code is hostile. For S1, every protected probe must
have an OS UID distinct from management and peers. Android profile/package policy,
the minimal supervisor/broker, and only the capabilities they actually control are
trusted. Logical directory separation is not isolation. Management secrets and
unrestricted broker authority may never enter a protected identity.

All Binder/service/provider, filesystem, native and network paths must be mediated
or blocked. Broker generations revoke only broker-owned capabilities; profile/
package lifecycle must be tested for stale Binder handles, open files/sockets,
native threads, jobs and early components. Trusted-component death must leave
execution/networking disabled pending complete revalidation with bounded recovery.

Privacy Decoy does not implement `VpnService`. External Android always-on/lockdown
is a required supported-configuration dependency for any no-physical-fallback
claim unless later independent evidence proves an equally strong supported
control. Every traffic-producing UID/process/helper must be attributable.

## Known limitations

Managed profiles isolate users and app storage; they do not create a synthetic
device or automatically mediate device-wide build, kernel, telephony, sensor,
service, `/proc`, `/sys`, property, Binder, provider, or network observations.
Provisioning, DPC authority, OEM differences, cross-profile paths, revocation,
external VPN treatment, API 31–37 behavior and distribution remain unproved.

VirtualSpace's README MIT wording without a root LICENSE leaves grant coverage
unresolved. Blacks-BlackBox's root Apache-2.0 text does not establish inherited
grants; its native source, committed AARs, Maven inputs, target SDK 28 and
hidden-API bypass require provenance and platform review. Both candidates'
source completeness, forkability, security maintenance, UID/process design,
management isolation, Binder/native/filesystem/network mediation and fail-closed
behavior remain unresolved. README Android/ARM64/split/profile/spoofing claims are
not security evidence; work-profile support resolves none of those uncertainties.

S1 remains a falsification experiment: if hostile Java/native code can obtain a
mandatory real host value that allowed root-free ordinary-app mechanisms cannot
replace or block, S1 fails. Profile isolation is not Decoy Persona mediation;
PD-REQ-001 through PD-REQ-070 must not be weakened to make it pass.

## Prototype-only authorization and next decisions

The proposed sequence is:

1. a managed-profile lifecycle/native/storage/host-state boundary probe;
2. only if it survives, a separate networking/revocation probe using the external
   VPN fixture and independent packet observation; and
3. independently, a bounded non-executing source/provenance audit of VirtualSpace
   and Blacks-BlackBox, starting with their separate license/ancestry gates and
   stopping each unresolved candidate before deeper architecture review. No
   runtime prototype proposal precedes resolution of its provenance, binary,
   dependency and hidden-API issues.

Each requires its own reviewed PR. Existing hostile probe, native library,
sentinel, lifecycle, Binder/session, revocation, external-VPN, provider-replacement
and pcap assets are baselines, not inherited proof. Physical ARM64, API/OEM and
release-equivalent evidence remains required before another explicit feasibility
gate.

Roadmap PR 8 implements the first direction solely as the controlled experiment
specified in [its evidence plan](../evidence/pr8-managed-profile-boundary.md).
That work does not change this ADR's proposed status or select S1 for production.

## Invalidation conditions

Return to **REDESIGN AGAIN** if S1's failure is architectural but another compliant
boundary remains credible. Seek an explicit **NARROW SCOPE** decision only for a
precisely enumerated app/capability/API/device exclusion that can be blocked before
code and reflected in requirements/UX. **STOP** under current goals if hostile
native code inherently reaches mandatory real host state, safe profile provisioning
or required route enforcement is unavailable across the approved matrix, or no
source-complete auditable alternative survives.

Any need for root, privilege/system installation, ordinary-use ADB, Magisk/Xposed/
LSPosed, custom ROM/kernel, guest root, Privacy Decoy `VpnService`, routine
re-signing without the PD-REQ-009 decision, opaque native TCB, unsafe shared
management identity, or fail-open behavior immediately invalidates a direction.

Another explicit feasibility decision is mandatory before product implementation.
The prior post-gate implementation roadmap remains superseded and paused.
