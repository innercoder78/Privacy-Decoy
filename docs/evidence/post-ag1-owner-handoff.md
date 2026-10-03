# Post-AG-1 owner handoff

## Current owner response — 2026-10-03

**OWNER DECISION: ACCEPT STOP UNDER PRIOR GOALS AND CONSTRAINTS; AUTHORIZE PRODUCT-CONTRACT REDESIGN**

Tony has supplied the response that was pending when this handoff was published
on 2026-10-02. He accepts the completed STOP recommendation for the **old product
contract represented by ADR-0008 and PD-REQ-001..095**. He does **not** abandon
Privacy Decoy. He explicitly invokes this handoff's allowed path of **an explicit
change to project goals or constraints** and authorizes a new architecture and
requirement design phase for the changed product contract.

[ADR-0009](../decisions/ADR-0009-retire-universal-containment-and-authorize-privacy-mediation-redesign.md)
records this later accepted decision and supersedes ADR-0008 only for future
architecture direction. ADR-0008 remains the historical Accepted REDESIGN
decision of 2026-09-29. The canonical next-design bridge is the
[privacy-mediation redesign handoff](../privacy-mediation-redesign-handoff.md).

The completed result remains **C. NO CREDIBLE BOUNDARY** under the old goals,
constraints and available evidence, not universal Android impossibility.
AG-1 remains FAILED and all five candidate dispositions remain unchanged.
The old enforcement-boundary charter is CLOSED; no sixth candidate is authorized.
No runtime prototype or production implementation is authorized by this response;
canonical production Roadmap PR 6 remains unstarted and unauthorized.

## Historical handoff published 2026-10-02

**Historical scope of the remainder:** the original recommendation, constraints,
pending-owner statements, requested decision and unfilled template below are
preserved as published on 2026-10-02. They record **OWNER DECISION: PENDING at
that time**, not current status. The dated response above and ADR-0009 govern
future direction; the old evidence remains controlling within its original scope.

**Prepared:** 2026-10-02. **Owner handoff prepared; owner response pending.**

### Evidence-backed phase result

**C. NO CREDIBLE BOUNDARY**

### Evidence recommendation

**STOP under the current goals and constraints represented by ADR-0008 and PD-REQ-001..095.**

This is a recommendation to Tony. **Tony has not yet accepted it.**
[ADR-0008](../decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md)'s recorded
owner decision remains **REDESIGN** until Tony supplies a new decision. No
implementation, prototype, new research phase, scope reduction, or production
PR is authorized while the owner decision is pending.

## Purpose and authority

This package completes the owner handoff required by ADR-0008 and the
[research charter](../post-ag1-enforcement-boundary-redesign.md). It condenses the
[completed comparative synthesis](post-ag1-comparative-synthesis.md) for Tony's
decision; it does not make that decision or open another architecture search.
The five candidate records and bounded research/synthesis are complete. The
handoff package is complete; the new owner response is pending.

The verified starting main is `4de49f1c16f65634ce0551abc1031af8b8ba3da8`, tree
`e9e0e9bf603f35ea6689d9b6a52b399a9633fab1`, the merge of
[GitHub PR #30](https://github.com/innercoder78/Privacy-Decoy/pull/30). The supplied
baseline reports Android foundation run #168, push, success, no open PRs, and
operational GitHub Status. Those supplied publication facts are not new security
evidence. This handoff adds no external architecture research or runtime result.

Evidence categories below remain separate: **historical observation** preserves
a recorded experiment and its scope; **source-supported finding** preserves the
candidate's exact source/configuration evidence; **synthesis inference** is the
reasoned phase conclusion; **Unknown** means evidence is missing or insufficient.
Proposed designs remain hypotheses, and upstream claims remain claims. The
**evidence recommendation** is STOP; the **owner decision** belongs only to Tony.

## Executive summary and decisive blockers

The phase reached STOP because none of the five candidates supplies both the
missing executable-content authority and complete control over genuine state
accessible inside the runtime. These are missing mandatory owners, beyond the
remaining implementation measurements. Useful isolation, correct broker decisions,
and selected syscall denials cannot compensate for a mandatory path that never
requests PD's decision. The synthesis establishes neither charter exit A's
credible complete boundary nor exit B's useful enforceable narrower class.

| Decisive issue | Evidence and implication for Tony's decision |
|---|---|
| Executable-content authority | **Historical observation:** [AG-1C](ag1-runtime-executable-code.md#first-exact-head-ag-1c-device-result) positively demonstrated previously unadmitted DEX through direct `InMemoryDexClassLoader`, loader construction, class resolution, static initialization, and entry execution without a mandatory PD before-use authorization decision. **Synthesis inference:** none of Candidates 1–5 supplies the missing unavoidable executable-content owner. PD-REQ-091 remains unresolved for the affected path; AG-1 remains FAILED. |
| Genuine framework/cache authority | **Historical observation:** [S1](pr8-managed-profile-boundary.md) exposed all seven mandatory parent Build fields in both tenants; [PR4](pr4-containment-prototype.md) separately exposed genuine Build/host Context and selected framework/proc/sys/property state. **Source-supported finding and inference:** Candidate 4 filters future syscalls; it cannot retroactively control values already cached or mapped in process memory. No completed candidate supplies complete mandatory Persona/state authority over those reads. |
| Binder/capability authority | **Source-supported finding:** [Candidate 4](post-ag1-candidate-4-syscall-binder-boundary.md) supplies bounded hard-denial possibilities at new Binder ioctl entry, not semantic Parcel/object mediation. **Unknown:** complete inherited/pending Binder, FD, reply, callback and deputy authority closure. A permitted Binder driver call does not mean only PD's object is reachable; closing a broker's FD copy does not revoke every holder. |
| Native/direct syscall authority | **Source-supported positive:** additional seccomp can enforce selected hard denials below raw syscall instructions in the inspected Android configuration. **Limit:** this is not executable-content admission, arbitrary-native containment, semantic Binder mediation, provenance enforcement, or a complete Android sandbox. Existing memory/code, allowed effects, pending operations and all-thread setup remain separate obligations. |
| Networking | **Historical observation:** [PR5](pr5-network-feasibility.md) retains physical egress under VPN loss without the required lockdown condition, plus per-app exclusion, split-route and allowBypass Known Gaps. **Unknown:** all-producer attribution and no-fallback verification, including reliable product verification of external enforcement. Moving sockets to a broker does not close that race. Privacy Decoy still has no `VpnService`; external VPN remains required by default. |
| Useful Android execution class | **Synthesis inference:** no candidate establishes a technically enforceable narrower class that preserves meaningful imported Android application semantics and closes mandatory content/state/authority paths. A fixed-artifact foreground utility remains conditional on missing owners and safe UI/storage adapters. A toy computation worker does not silently become Privacy Decoy's product scope. |

AG-1C's decisive run #150 used head
`55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`, API 35 Google APIs x86_64 debug.
The four milestones were `1111`. It bypassed PD's helper, not Android's UID
sandbox. The fixture was not a real Protected-eligible application. The old
fixture was not run under Candidate 4/5's unimplemented filters; that untested
behavior supplies no missing content owner and does not downgrade the observed
bypass to Unknown. Denying new executable native pages does not itself deny
DEX interpreted by existing ART code; later denial or normal teardown cannot
retroactively authorize code that already executed.

## Historical evidence sequence and governance — 2026-10-02

| Historical stage | Preserved result and present meaning |
|---|---|
| Original containment/network research | PR4 retained bounded isolation/broker positives with genuine-state and app-semantics gaps; PR5 retained bounded packet/lockdown results with Known Gaps. The historical Roadmap PR 6 **A — REDESIGN** event retains its label. |
| S1 and engine review | S1 **FALSIFIED**; S1 follow-up **BLOCKED**. S2 audit-cleared no engine. VirtualSpace pin `b1ff7988ac598b00b45c22003390ff43396c1c01` remains **DISQUALIFIED — exact pinned candidate**, without erasing its earlier provenance STOP. Blacks-BlackBox remains **STOPPED_UNRESOLVED**. |
| Earlier discovery and AG-1 authorization | The [ADR-0006 synthesis](architecture-discovery-synthesis.md) recommended **STOP UNDER CURRENT GOALS** within its scope. Subsequent source evidence led to Tony's historical ADR-0007 **CONTINUE** authorization for bounded AG-1, not production. |
| AG-1 and canonical Roadmap PR 5 | [AG-1 closeout](ag1-feasibility-closeout.md): **FAILED**. The [canonical PR 5 historical recommendation](canonical-pr5-feasibility-decision.md) remains **STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**. |
| Tony's later owner response | ADR-0008 records **REDESIGN**, dated 2026-09-29, authorizing the bounded enforcement-boundary phase. It did not convert AG-1 into PASS or authorize production continuation. |
| Completed phase and this handoff | Five records and synthesis complete; result **C. NO CREDIBLE BOUNDARY**, **STOP recommendation to Tony**. Owner handoff prepared; owner response pending. No new phase authorized. |

Dated pending/in-progress statements in earlier records describe their publication
stage. They remain historical; this handoff does not rewrite them. Roadmap numbers
remain distinct from GitHub PR numbers. The already-resolved canonical PR 5 owner
response of REDESIGN is distinct from the new response now requested from Tony.

## Candidate dispositions

These dispositions are preserved exactly; the phase result does not reclassify
Candidates 2–4's Unknowns as failures or passes.

| Candidate | Fixed disposition |
|---|---|
| [1 — OS/process compartment](post-ag1-candidate-1-os-process-compartment.md) | REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY |
| [2 — controlled Android semantics](post-ag1-candidate-2-controlled-runtime-lower-boundary.md) | UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED |
| [3 — constrained execution classes](post-ag1-candidate-3-constrained-execution-class.md) | UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY |
| [4 — syscall/Binder restrictions](post-ag1-candidate-4-syscall-binder-boundary.md) | UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED |
| [5 — hybrid architecture](post-ag1-candidate-5-hybrid-architecture.md) | REJECTED — HYBRID DOES NOT CLOSE MANDATORY AUTHORITY |

## Positive evidence retained

STOP does not mean the research produced nothing useful. The
[synthesis's positive-evidence table](post-ag1-comparative-synthesis.md#13-positive-evidence-retained)
retains the detailed scope of these contributions:

* **Historical observations:** artifact identity and positive inventory; immutable
  generation/session concepts; pre-code READY ordering; manager-owned authorization
  state; exact helper grants and denials; isolated UID/process separation; narrow
  broker authorization and selected persistent sentinel/socket observations.
* **Source-supported mechanisms and architecture ideas:** controlled semantics
  organization, Persona modeling, ordinary-app additional seccomp possibilities,
  all-thread synchronization/inheritance, and selected hard direct-syscall denials.
  Candidate 4 established an Android-specific source path, not a new PD device test
  or an all-OEM guarantee. Selective open-source ideas retain their catalog scope;
  no engine is selected or source integrated.
* **Evidence methods and governance:** independent external-VPN packet techniques,
  positive controls, explicit evidence limits, and fail-closed governance separating
  mandatory Unknown, known bypass, Experimental compatibility and Protected claims.

These pieces own limited decisions. They do not compose into complete mandatory
authority: correct initial admission cannot force later direct loaders to ask,
and an outside Persona value source cannot control every cached genuine read.
Composition also leaves capability-transfer, broker, startup and revocation duties.

## Known negatives, remaining Unknowns, and prototype readiness

**Known negatives** retain their original scope: AG-1C's direct executable bypass,
S1/PR4's genuine-state exposure, the reference adapters' source-observed genuine
fallbacks, and PR5's physical-egress and routing Known Gaps. These are stronger
than missing measurements. They do not classify every Android app as Known unsafe.

**Remaining Unknowns** include an unavoidable before-use content owner; complete
genuine-cache exclusion; safe useful Android adapters; complete Binder/FD/mapping
and deputy closure; arbitrary-native containment/exclusion and provenance; trusted
all-thread setup and pending-work handling; every lifecycle/restart/descendant and
safe revocation path; all-producer external-VPN verification; complete artifact,
split/signing/dependency analysis; and future TCB provenance/security review.
Actual API 31–37, ART/kernel/ABI, Google/AOSP, Samsung and another-OEM behavior
needs separately scoped physical non-rooted, release-equivalent evidence under
the unchanged [platform matrix](../platform-support.md). Debug emulator evidence
does not establish that support.

**Synthesis inference:** no candidate is currently prototype-ready. An individual
syscall denial is falsifiable, but testing that primitive cannot supply unnamed
content/state owners. A prototype that must invent those owners while implementing
is not a complete enough enforcement hypothesis under this charter. Missing
measurements do not justify another prototype, repeat candidate, more hooks,
another ten-project survey, or a sixth candidate.

The bounded phase result is **C. NO CREDIBLE BOUNDARY** under current goals,
constraints and evidence. It is not a mathematical impossibility proof, a claim
that Android can never support Privacy Decoy, or an exhaustive review of every
future Android mechanism. Static absence does not prove behavioral absence;
Unknown remains Unknown and mandatory Unknown remains disqualifying.

## Requirements, constraints, and production status

**PD-REQ-001..095 remain unchanged.** No requirement is marked satisfied, deleted,
renumbered, weakened, or silently narrowed. The [requirements](../requirements.md),
[threat model](../threat-model.md), and [1.0 acceptance gate](../acceptance-criteria-1.0.md)
remain governing. In particular:

* Protected Mode still fails closed; mandatory Unknown blocks before execution.
  A Known mandatory bypass remains a hard stop. Experimental cannot override
  known unsafe/incompatible conditions or count toward Protected acceptance.
* No root/guest root, Magisk/Xposed/LSPosed, custom ROM or patched kernel,
  privileged/system installation, production ADB dependency, routine APK
  rewriting/re-signing, convenience full guest Android, or opaque security-critical
  binaries in the PD TCB is authorized. No exception is granted here.
* No Privacy Decoy `VpnService`; **Require VPN for protected apps = ON** remains
  the default. External-VPN requirements, all-producer attribution and independent
  evidence remain unchanged. Unverifiable required VPN/route state blocks
  execution/traffic; no physical-network fallback is permitted.
* Real remains controlled mediation. No-host-GPS rules remain unchanged; location
  never falls back to host GPS. Permissions never automatically disclose host
  personal data. Ordinary protected apps, private user data and real accounts
  remain prohibited; no protection or release claim is established.

Production remains paused. AG-1 remains **FAILED**; canonical production Roadmap
PR 6 remains **unstarted and unauthorized**. The [canonical roadmap](../canonical-roadmap-1.0.md)
and [reconciliation](../roadmap-reconciliation.md) are unchanged; canonical PR 20
has not occurred. This package changes only documentation and the charter's
progress state: no Android/runtime/test/workflow/dependency/binary changes,
prototype, source integration, engine selection, or scope change. Merging this
handoff neither accepts STOP for Tony nor authorizes further work.

## Historical owner decision request — 2026-10-02

### STOP

**Evidence recommendation: STOP under the current goals and constraints
represented by ADR-0008 and PD-REQ-001..095.** If Tony accepts it, that means:

* Accept the completed phase's recommendation under the current project goals
  and constraints.
* Do not proceed to production Roadmap PR 6 or start another runtime prototype.
* Retain all requirements and evidence, including useful positives and Unknowns.
* Leave the project paused unless materially new evidence, Android capability,
  or an explicit future owner decision justifies reopening it. New information
  alone is not work authorization.

Tony may instead explicitly reject the recommendation. **Rejection does not
itself authorize work.** Any non-STOP continuation requires a separate explicit
owner decision defining at least one of:

* A materially new, bounded enforcement hypothesis not already covered by the
  completed phase.
* Materially new external/platform evidence that changes the authority analysis.
* An explicit change to project goals or constraints.

These are conditions on a future owner decision, not proposed new directions.
If Tony chooses any of them, a new ADR or equivalent owner-decision record must
be prepared **after Tony explicitly states his decision after this handoff is
merged**. This PR does not prepare or pre-authorize such an ADR. No new research,
prototype, integration or production work follows automatically from that record;
the stated authorization and applicable canonical gates must govern the next step.

Until Tony supplies the new response, ADR-0008 continues to record **REDESIGN**,
owner response remains **PENDING**, and no new phase is authorized. No answer is
filled in on Tony's behalf.

## Evidence references

The linked five candidate records retain their detailed authority matrices and
exact source ledgers. The [comparative synthesis](post-ag1-comparative-synthesis.md)
contains the cross-candidate analysis, A/B/C exit matrix, narrower-class and
prototype-readiness assessment. The executive table and history above link the
original AG-1, S1, PR4/PR5, earlier synthesis and canonical PR 5 records. Those
records' dates, exact tested heads and source-versus-runtime limits remain
controlling; this handoff adds no new experiment or source finding.

## Historical owner-response template — unfilled at publication on 2026-10-02

```text
OWNER DECISION: PENDING

Evidence recommendation:
STOP under the current goals and constraints represented by ADR-0008
and PD-REQ-001..095.

Tony's decision:
[To be supplied by Tony after this handoff is merged.]

If Tony does not accept STOP:
A separate explicit authorization must define the materially new hypothesis,
new evidence, or changed constraint before any further work begins.
```
