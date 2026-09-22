# ADR-0003: Redesign prototype direction

- **Status:** Proposed — PROTOTYPE DIRECTIONS IDENTIFIED / NO PRODUCTION ARCHITECTURE SELECTED
- **Date:** 2026-09-21
- **Historical redesign/research sequence:** PR 7; S1 result recorded by Roadmap PR 8 and S2 disposition by Roadmap PR 8A on 2026-09-22

## Current canonical governance

> **Subsequent decision (2026-09-22):** S1 and S2 completed with the
> dispositions recorded below. The project owner then made the explicit
> architecture decision required by this ADR. [ADR-0006](ADR-0006-redesign-again-architecture-discovery.md)
> records **REDESIGN AGAIN** and authorizes one bounded architecture-discovery
> phase. It does not reverse S1's falsification or either S2 outcome, select a
> production architecture, or start canonical production PR 6. Product
> implementation remains paused.

Historical redesign labels Roadmap PR 7, PR 8, PR 8A and the historical PR 9
networking/revocation follow-up are preserved as supplemental feasibility research.
They are not the numbering of the restored [canonical 46-PR production
roadmap](../canonical-roadmap-1.0.md) or inserted production stages. In particular,
the blocked historical S1 follow-up is not canonical PR 9 — Protected Storage
and Key Boundaries. See the [roadmap reconciliation](../roadmap-reconciliation.md)
and [ADR-0005](ADR-0005-canonical-roadmap-source-restoration.md).

Canonical production PR 6 has not begun; merging this evidence does not authorize
it. Canonical PR 20 has not occurred, and this audit does not infer satisfaction
of either canonical PR 5 or PR 20. PD-REQ-001 through PD-REQ-085 remain in force
and unchanged; no requirement is marked satisfied by this static audit.

The original S2 audit baseline remains
`bc73884edb1af2fbd7dcbed18cc5af3ef1517b7a`; this documentation/governance
reconciliation uses `4ed61bae6327fa62e37d154f8f45f675f36fa86e`. The third-party
static inspection was not rerun and its findings are unchanged.

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
2. **S2: completed supplemental source/provenance audit under the historical
   Roadmap PR 8A label; no audit-cleared engine.** VirtualSpace and Blacks-BlackBox independently
   received **STOPPED_UNRESOLVED** at Gate 1. Deeper architecture review stopped for
   both. The [canonical S2 evidence](../evidence/pr8a-engine-source-provenance-audit.md)
   records exact refs, grant/ancestry gaps and binary/dependency provenance limits.
   No runtime prototype is proposed or authorized by S2; the subsequently required
   architectural decision is recorded in ADR-0006.

Independent PR review on 2026-09-21 revalidated the study's listed source refs and
identified Black00Z/Blacks-BlackBox at
`40282a7bf4500948cfd598fc67e6e63114b26dd9` (2026-05-12). Its public tree and
root Apache-2.0 text warrant screening, but do not resolve inherited
BlackBox/VirtualApp licensing, native/AAR provenance or containment. Both S2
candidates remain **Major unresolved risk** after the completed Gate 1 screening.
A shared audit checklist with
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

## Completed S2 source/provenance disposition

Roadmap PR 8A performed only non-executing static research at the study's exact
pins, with independent outcomes:

| Candidate / immutable ref | Gate 1 and final disposition | Reason / deeper-review status |
| --- | --- | --- |
| VirtualSpace — `b1ff7988ac598b00b45c22003390ff43396c1c01` | **STOPPED_UNRESOLVED** | README MIT wording does not establish complete attributable engine/native grant coverage or ancestry. Deeper architecture review stopped. |
| Blacks-BlackBox — `40282a7bf4500948cfd598fc67e6e63114b26dd9` | **STOPPED_UNRESOLVED** | Root Apache-2.0 and selected AOSP notices do not resolve inherited engine/native/reflection grants or exact source/build provenance for two local AARs and two Dobby static archives. Deeper architecture review stopped. |

See the [S2 evidence record](../evidence/pr8a-engine-source-provenance-audit.md)
for declaration inventories, archive hashes, provenance limitations and remaining
Unknowns. Neither candidate is AUDIT_PASS; neither is DISQUALIFIED on an assumed
unresolvable defect. No engine was built, executed, installed, integrated or
vendored, and no dependencies or binaries were added. No runtime prototype may
be proposed on this evidence. S2 supplied no auditable candidate under its gate;
another explicit decision had to consider **REDESIGN AGAIN**, **NARROW SCOPE**, or
**STOP** / the applicable explicit feasibility gate. This historical S2 work
selected none of those decisions and did not initiate another candidate search;
ADR-0006 records the later project-owner decision.

S1 remains **FALSIFIED**; its historical research Roadmap PR 9 networking/revocation follow-up remains
**BLOCKED**. **NO PRODUCTION ARCHITECTURE SELECTED**. Product implementation,
ordinary protected applications, real accounts and private data remain prohibited.
PD-REQ-001 through PD-REQ-085 remain in force and unchanged; no requirement is
marked satisfied by this static audit. A future project-owner decision may
investigate another compliant architecture, deliberately narrow supported scope
while preserving every mandatory boundary, or stop under current goals. None of
those decisions is selected here.

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
3. **S2: completed by Roadmap PR 8A, both candidates STOPPED_UNRESOLVED.** Separate
   license/ancestry/provenance gates stopped both before deeper architecture
   review. No third-party engine execution, integration or runtime prototype
   proposal is authorized. Neither candidate is established viable or selected
   for production; ADR-0006 records the architectural decision subsequently made.

Roadmap PR 8A records S2 for review. Existing hostile probe, native library, sentinel,
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

ADR-0006 is the subsequent governance decision, not a feasibility pass; another
explicit feasibility decision remains mandatory before product implementation.
The prior post-gate implementation roadmap remains superseded and paused.
