# ADR-0009: Retire universal containment goal and authorize privacy-mediation redesign

**Status:** Accepted — owner accepts STOP under the prior product contract;
product-contract architecture/design work authorized; production remains paused

**Date:** 2026-10-03

**Owner decision:** Tony — ACCEPT STOP UNDER PRIOR GOALS AND CONSTRAINTS;
AUTHORIZE PRODUCT-CONTRACT REDESIGN

**Supersedes:** [ADR-0008](ADR-0008-post-ag1-enforcement-boundary-redesign.md)
**only for future architecture direction**. ADR-0008 remains a valid historical
Accepted decision recording REDESIGN on 2026-09-29.

## Context and preserved evidence

The documentation baseline is main `6716b8f2ebcb722888e5afc2674601cdad05578f`,
tree `9b28e603c190b7cf0da99b71feba932242b32166`, after merged
[GitHub PR #31](https://github.com/innercoder78/Privacy-Decoy/pull/31),
**docs: prepare post-AG-1 owner handoff**. The
[owner handoff](../evidence/post-ag1-owner-handoff.md) was published on
2026-10-02 with **OWNER DECISION: PENDING**. Tony has now supplied that response.

The completed [comparative synthesis](../evidence/post-ag1-comparative-synthesis.md)
remains **C. NO CREDIBLE BOUNDARY** under the original goals, constraints and
available evidence represented by ADR-0008 and PD-REQ-001..095. It found no
credible permitted boundary for the original universal fail-closed containment
goal for arbitrary imported Android applications. This is a scoped research
conclusion, not a mathematical proof that this concept can never exist on Android.

The previous architecture and research phases remain valid historical evidence:

* [AG-1](../evidence/ag1-feasibility-closeout.md) remains **FAILED**.
  [AG-1C](../evidence/ag1-runtime-executable-code.md) remains a positively known
  executable-content admission failure: previously unadmitted DEX executed
  through the tested direct `InMemoryDexClassLoader` path without the PD helper
  being mandatory. Untested paths remain Unknown.
* Candidates 1–5 retain their [exact recorded dispositions](../privacy-mediation-redesign-handoff.md#historical-result-and-controlling-records),
  including Candidate 5's rejection and Candidates 2–4's Unknowns.
* S1 remains FALSIFIED and its follow-up BLOCKED; PR4/PR5 gaps, earlier STOP
  recommendations, provenance findings, and bounded positive results remain.
* The [independent defensive review synthesis](../evidence/post-ag1-independent-defensive-review-synthesis.md)
  records **A. NO MATERIALLY NEW BOUNDARY** in a different classification system.
  It supports retaining STOP under the old contract, adds no runtime evidence,
  and does not replace the repository's result C.

## Decision and architectural epoch boundary

Tony **accepts the completed STOP recommendation for the old product contract**.
He does **not** abandon Privacy Decoy. He explicitly chooses the handoff's path
of **an explicit change to project goals or constraints**. The old
[enforcement-boundary charter](../post-ag1-enforcement-boundary-redesign.md) is
CLOSED; no sixth candidate is authorized under it.

The universal-containment goal and the attempt to satisfy the old Protected
contract through ADR-0007/ADR-0008 are retired as the direction for future design.
This decision does not authorize repairing Candidate 5, adding more hooks, or
reviving the admission-gated runtime while retaining the original universal claim.
It authorizes redesign of the **product contract itself**. No failed prerequisite
is declared satisfied and no old guarantee is redefined as a success.

**PHASE II STARTS WITH ARCHITECTURE AND REQUIREMENT REDESIGN, NOT IMPLEMENTATION.**
The canonical bridge is the [privacy-mediation redesign handoff](../privacy-mediation-redesign-handoff.md).
No final technical architecture is selected or proven. No runtime prototype,
source integration, production implementation, or old canonical production
Roadmap PR 6 is authorized. PR 6 must not resume automatically. Later execution
requires separately stated authorization and explicit reconciliation of the
applicable feasibility and owner gates; this ADR supplies no replacement pass.

## Owner's intended product direction

Privacy Decoy remains an Android application virtualization/privacy-mediation
product. A user should select an application already installed on the device and
choose something equivalent to **Add to Privacy Decoy**. Privacy Decoy should
prepare a separate instance with fresh application state, as though newly
installed. It must not silently inherit the original application's private data,
login state, databases, SharedPreferences, cache, or personal data. Fresh local
state does not erase remote account/history linkage.

Controlled artifact transformation, APK/split modification, injected runtime
mediation, package adaptation, rebuilding, and re-signing are now **authorized
architectural options and a leading design direction to evaluate**. This is an
explicit change from the old preference against routine rewriting/re-signing;
it is not selection or implementation of that mechanism. The old requirements
remain unchanged records until their explicit migration.

Preparation may show progress such as **Adding and modifying [APP NAME] to
Privacy Decoy**. Possible stages are package/base/split analysis, compatibility
checks, isolated/fresh-state preparation, installation of mediation machinery,
structural transformations, rebuilding, signing, installation/registration, and
Persona assignment. These are workflow targets; neither stage order nor UI
wording is finalized.

App Cloner may be studied as a **commercial product reference** for the general
clone/modify/re-sign/runtime-mediation model, preparation UX, runtime setting
updates and compatibility limitations. Only publicly documented behavior may
inform that research. No proprietary code may be copied, and no knowledge of
undocumented internals or adoption of its exact architecture is asserted.

## Persona, configuration and coverage objectives

The Persona remains central: coherent, stable synthetic/controlled values until
deliberate user change or rotation. Coverage goals, where feasible and supported,
include country/region, geographic location, language/locale, timezone, Android
ID, device/model/build descriptors, carrier/operator, SIM-country and MCC/MNC-like
metadata, advertising identifiers, package visibility, storage exposure,
clipboard, sensors, network metadata and the other old privacy-sensitive surfaces.
**Real / Decoy / Empty / Deny** remains the policy concept for redesign analysis
unless a later ADR deliberately changes it. Real remains an explicit controlled
disclosure decision, not blanket host passthrough.

The preferred hypothesis is generic mediation machinery in the prepared app,
with actual Persona values owned by Privacy Decoy policy/configuration and
supplied dynamically where feasible. Country changes (Canada to United States),
city changes (Toronto to Vancouver), carrier, locale, timezone, Android ID,
model/build and capability-mode changes should not automatically require every
app to be rebuilt. Restart, a new session or a new generation may be required;
live mutation of cached Android/app values is not promised. Package/manifest,
injected implementation, signing identity, transformation strategy, upstream
app updates, new native/runtime instrumentation and some compatibility changes
may legitimately require re-analysis, rebuilding or reinstallation. Phase II
must resolve this runtime-versus-structural distinction.

Compare Persona country to the application's apparent **public network exit
country**, where feasible, without acquiring the phone's real GPS for the check.
A Canada Persona with an observed Japan exit should support a mismatch warning
and a recommendation to use a VPN/proxy exit matching Canada. IP geography is
approximate, not physical-location proof; unverifiable results remain Unknown.
The user currently expects an external matching-country VPN. No PD-owned Android
`VpnService` is authorized. The prior prohibition is not eternally immutable,
but changing it requires an explicit future architecture/owner decision. Exact
networking architecture and warning/continue semantics remain unresolved.

Where PD can positively observe a real-value request through another covered
path, it should prefer blocking that real value and notifying the user. A
conceptual warning is **Privacy Decoy blocked an attempt by [APP] to access real
[DATA TYPE] through a non-Persona path**, with **Close** and **Continue** actions.
Continue must be an explicit decision about the affected Real capability,
session or policy, never a silent global disabling of mediation. Final wording,
scope and enforcement semantics require design.

PD must never claim to detect every bypass. Covered/monitored, known blocked,
partially mediated, unsupported and Unknown/unobserved paths must remain distinct.
No detection is not proof of no alternate path. Phase II must design honest
coverage/compatibility categories appropriate to mediation; possible concepts
include mediated, mediated with known gaps, explicit Real/compatibility exception,
unsupported/incompatible and not yet analyzed. These are not implemented labels.
Old Protected / Experimental / Known unsafe / Incompatible semantics remain
historical, with no new Protected or safety claim.

## Migration, cleanup and consequences

[Requirements](../requirements.md), [threat model](../threat-model.md),
[acceptance criteria](../acceptance-criteria-1.0.md), [platform support](../platform-support.md),
[canonical roadmap](../canonical-roadmap-1.0.md) and
[roadmap reconciliation](../roadmap-reconciliation.md) remain unchanged as the
canonical/historical Phase I contract pending explicit Phase II migration.
ADR-0001 through ADR-0008 are not rewritten. Their dated future/pending language
describes the earlier epoch, not authorization to restart it.

Every PD-REQ-001..095 entry must later receive an explicit disposition: **Retained
unchanged**, **Retained but revised**, **Superseded by explicit owner-approved
redesign**, or **Retired by explicit owner decision**, with rationale and
traceability. This ADR does not perform the 95-entry migration or silently
reinterpret those files. Changed goals do not authorize unrelated privilege,
opaque TCB components, unreviewed dependencies, private-data testing or claims.

Phase II must also inspect the entire repository and classify relevant active
files/subtrees as **KEEP / MODIFY / ARCHIVE / DELETE**, using the handoff's
dependency and evidence-preservation rules before proposing cleanup. Historical
evidence and ADRs should generally remain; obsolete implementation should
eventually leave the active tree where no longer useful. Git history is its
archive. No research code is deleted or moved by this decision.

The immediate result is an accepted owner response, closure of the old charter,
and a durable design handoff. Implementation, compatibility, native coverage,
signing/identity behavior, Play Integrity, Google login/Play Services, external
VPN verification and clone detectability remain unresolved. Rewriting, runtime
injection, re-signing, ByteHook/ShadowHook or seccomp do not by themselves prove
privacy or universal containment. Production remains paused.
