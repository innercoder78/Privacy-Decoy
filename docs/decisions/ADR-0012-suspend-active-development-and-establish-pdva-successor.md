# ADR-0012: Suspend active Privacy Decoy development and establish the PDVA successor lineage

**Status:** Accepted

**Date:** 2026-10-06

**Owner decision:** This is an explicit project-owner decision to suspend active
implementation of the original Privacy Decoy product, preserve its historical
research record, and move active forward effort to a separate product lineage.

## Context and preserved findings

Privacy Decoy investigated protecting arbitrary, potentially adversarial Android
applications on ordinary stock, non-rooted devices without privileged operation.
It accumulated substantial architecture and open-source research, threat
modeling, adversarial probes, CI/test infrastructure, Persona/privacy concepts,
networking evidence, negative findings, and implementation experiments.

Phase I concluded **C. NO CREDIBLE BOUNDARY** under its product contract, as
recorded in the [comparative synthesis](../evidence/post-ag1-comparative-synthesis.md).
[AG-1 remains FAILED](../evidence/ag1-feasibility-closeout.md). No failure,
Unknown, or bounded positive result is softened or rewritten by this decision.

The mediation/containment obligation repeatedly extended across framework APIs,
Binder/providers/services, JNI/native code, direct syscalls, files, `/proc`,
`/sys`, properties, dynamic/generated/downloaded code, SDKs, WebView, processes,
networking, and lifecycle paths. Adversarial code retaining routes to genuine
host Android authority requires a defensible boundary across the claimed scope.
More hooks, proxies, transformations, or successful compatibility tests do not
automatically establish that boundary.

Phase II selected the transformation-first hybrid architecture in
[ADR-0010](ADR-0010-select-transformation-first-hybrid-privacy-mediation.md).
[Stage 2](../evidence/phase-ii-stage-2-active-tree-cleanup.md) completed
implementation maintenance only. Stage 3 was not completed on canonical `main`,
and Stages 4–18 remain unfinished. Phase II did not reach production privacy
mediation or prove a production privacy boundary.

[ADR-0011](ADR-0011-adopt-emulator-first-phase-ii-development.md) allowed
emulator-first research with deferred mandatory physical qualification. Its
historical meaning remains intact. The owner now chooses suspension before
executing the remaining roadmap. Phase II was not fully executed and is not
declared disproven. The original security/mediation problem proved unusually
broad, difficult, and expensive to establish convincingly; the owner is moving
active effort to another architectural level.

## Decision

1. Active implementation of the original Privacy Decoy product is suspended.
   This repository is retained intact as a historical research and evidence
   record, with its existing code and useful infrastructure preserved.
2. The owner may explicitly reactivate Privacy Decoy later. Resumption requires
   an explicit owner decision/ADR and fresh verification of the then-current
   Android/platform assumptions, scope, evidence, and applicable gates. The
   preserved roadmap is not currently an active execution queue.
3. No prior failure, Unknown, evidence record, ADR, requirement, or roadmap
   history is erased. No unfinished stage is reclassified as success. Phase I's
   result and AG-1's failure remain unchanged. The Phase II transformation-first
   approach is not declared universally impossible or technically disproven.
4. Current forward development moves to the separate
   [Privacy Decoy Virtual Android (PDVA) repository](https://github.com/innercoder78/privacy-decoy-virtual-android).
   PDVA is a new product lineage, rather than Phase III or the next step in the
   old Privacy Decoy requirement/ADR sequence.
5. PDVA takes lessons from Privacy Decoy and selects a complete interactive
   virtual Android guest, with a deliberate guest/host boundary, as its product
   direction. It begins its own feasibility analysis. No particular
   virtualization engine or security boundary is yet proven, and no claim is
   made that full virtual Android is already easier, secure, or feasible.
6. PD-REQ-001..113 remain untouched and authoritative only for this historical
   Privacy Decoy product unless PDVA deliberately re-adopts a concept through
   its own governance. No PDVA requirements or ADR numbering are created here,
   and this decision does not modify the successor repository.

## Consequences

The canonical [project status](../project-status.md), README,
[roadmap](../phase-ii-roadmap.md), and [development guidance](../../.github/CONTRIBUTING.md)
communicate suspension and preserve the final canonical implementation state.
Unsolicited forward architectural work must not infer authorization from the
old roadmap or ADR-0011's research-stage permissions during suspension.
Historical/security documentation fixes may still be appropriate when they
preserve the meaning and scope of the evidence.

The retained tree remains a research/foundation tree, not a finished privacy
product. Compatibility, privacy mediation, and security evidence remain distinct;
Unknown remains Unknown. No implementation, dependency, CI behavior, fixture,
binary, signing configuration, or historical finding changes through this
documentation/governance decision. The repository and its research remain
available for reference and possible future resumption rather than being erased
or permanently abandoned.
