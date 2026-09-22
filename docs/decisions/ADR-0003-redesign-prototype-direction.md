# ADR-0003: Redesign prototype direction

- **Status:** Proposed — PROTOTYPE DIRECTIONS IDENTIFIED / NO PRODUCTION ARCHITECTURE SELECTED
- **Date:** 2026-09-21
- **Roadmap:** PR 7; S1 result recorded by Roadmap PR 8 on 2026-09-22

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

The two bounded research directions have the following current dispositions;
neither is selected for production:

1. **S1: managed-profile isolation plus least-authority mediation — FALSIFIED by
   Roadmap PR 8.** The completed boundary probe exposed mandatory parent Build
   identity in both managed-profile tenants. Its survival-gated networking/
   revocation follow-up is blocked; useful OS isolation/lifecycle properties do
   not establish Decoy Persona mediation.
2. **S2: current open-source engine source/provenance audit — next authorized
   redesign research direction, not an established viable architecture.** One bounded,
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

## Completed S1 boundary result

Roadmap PR 8 tested exact source head
`69f0352510a55d92dcf4a408aa524cc0532788f9`. Android foundation **#100 (push)** and
**#101 (pull_request)** were both fully successful: validate, containment-prototype,
managed-profile-feasibility and network-feasibility all passed. Both independent
workflow invocations reported a valid harness (`PD_S1_HARNESS=PASS`) and adverse
architecture outcome (`PD_S1_OUTCOME=FALSIFIED`), with reasons
`tenant_a_mandatory_build_same` and `tenant_b_mandatory_build_same`.

Both tenants matched the parent on all seven mandatory surfaces:
`build_fingerprint`, `build_model`, `build_manufacturer`, `build_brand`,
`build_device`, `build_product`, and `build_hardware`. The bounded tokens exposed
no raw values. This satisfies this ADR's falsification condition: the managed
profile did not replace or block mandatory real Build identity before hostile
code. **Managed-profile OS isolation alone cannot satisfy Decoy Persona mediation.**
Green CI establishes correct experiment execution, not S1 feasibility.

The [Roadmap PR 8 evidence record](../evidence/pr8-managed-profile-boundary.md)
contains the exact tokens and limits: API 35 Google APIs x86_64 debug only, bounded
storage/lifecycle observations and coarse profile-stop process death. Physical
ARM64, OEM, release-equivalent and full API-matrix coverage remain unproved, as do
persistent resources and full recovery. This finding authorizes no requirement
weakening, External relabeling, hooks, privilege, root or APK rewriting workaround.
The experiment accomplished its purpose; S1 must not proceed to its planned
networking/revocation follow-up. **NO PRODUCTION ARCHITECTURE SELECTED** remains
the overall decision status.

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

S1 used the falsification rule that obtaining a mandatory real host value which
allowed root-free ordinary-app mechanisms cannot replace or block invalidates the
direction. Roadmap PR 8 met that condition through mandatory Build identity
equality. Profile isolation is not Decoy Persona mediation; PD-REQ-001 through
PD-REQ-070 remain unchanged and must not be weakened to reverse this result.

## Prototype-only authorization and next decisions

The sequence now records:

1. **S1 boundary probe: completed and FALSIFIED** by Roadmap PR 8 at the exact
   head recorded above.
2. **S1 networking/revocation follow-up: BLOCKED by the survival gate.** Its
   prerequisite did not hold, so this follow-up must not proceed.
3. **S2: next authorized redesign research direction.** A bounded, non-executing
   source/provenance audit of VirtualSpace and Blacks-BlackBox, starting with
   separate license/ancestry gates and stopping each unresolved candidate before
   deeper architecture review. No runtime prototype proposal precedes resolution
   of its provenance, binary, dependency and hidden-API issues. No third-party
   engine execution or integration is authorized, and neither candidate is
   established viable or selected for production.

S2 requires its own reviewed PR. Existing hostile probe, native library, sentinel,
lifecycle, Binder/session, revocation, external-VPN, provider-replacement and pcap
assets are baselines, not inherited proof. Physical ARM64, API/OEM and
release-equivalent evidence remains required before another explicit feasibility
gate. Roadmap PR 8 records a completed research falsification, not a change to this
ADR's overall proposed status or a production architecture selection.

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
