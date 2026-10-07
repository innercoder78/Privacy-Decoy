# Privacy Decoy project status

## Status

**Active development suspended.**

The owner explicitly suspended active implementation of the original Privacy
Decoy product on 2026-10-06 in
[ADR-0012](decisions/ADR-0012-suspend-active-development-and-establish-pdva-successor.md).
The repository remains available as a historical research and evidence record.
The owner may resume it through an explicit future decision/ADR and fresh
verification of the then-current Android/platform assumptions.

## What the project attempted

Privacy Decoy explored protecting privacy when running arbitrary, potentially
adversarial Android applications on ordinary stock, non-rooted devices. Its
product contract excluded privileged/system installation, production ADB,
Magisk/Xposed/LSPosed, custom ROMs, and patched kernels. The research addressed
how application code could receive coherent Persona values or safe policy
results without silently disclosing genuine host data.

## Why this became difficult

An arbitrary hostile application running in an environment that retains routes
to genuine host Android authority creates a very large mediation/containment
obligation. Those routes include framework APIs, Binder/providers/services,
JNI/native code, direct syscalls, files, `/proc`, `/sys`, properties,
dynamic/generated/downloaded code, SDKs, WebView, processes, networking, and
lifecycle paths. The [Phase I comparative synthesis](evidence/post-ag1-comparative-synthesis.md)
and [Phase II threat model](phase-ii-threat-model.md) preserve the evidence and
analysis of these obligations.

The project repeatedly found that individual hooks, proxies, transformations,
or successful execution did not establish the required system-wide boundary.
Compatibility results and bounded positive observations cannot compensate for
unmediated authority paths or missing security/privacy evidence. Establishing
the original project's security/mediation claims convincingly proved unusually
broad, difficult, and expensive. The owner chose to move active effort to another
architectural level before completing Phase II; that choice does not establish
that Phase II was disproven or that the concept is impossible in all forms.

## What was learned

The repository preserves substantial architecture research, open-source research,
implementation experiments, negative findings, and reusable infrastructure:

* Threat-model development and adversarial-path inventory across Android authority
  surfaces.
* Containment failures, bounded positive findings, and architecture comparison
  without reclassifying failed gates as success.
* Persona/privacy semantics and the distinction between explicit Real disclosure
  and Decoy, Empty, or Deny policy results.
* Network/privacy separation, including network geography as External and the
  independent networking evidence.
* Evidence discipline: exact tested scope, known limitations, and the distinction
  between compatibility and security/privacy evidence.
* Source/provenance discipline for research references and possible dependencies.
* Android adversarial probes, controlled fixtures, testing, and CI infrastructure.
* The importance of treating Unknown as Unknown rather than inferring success
  from absent indicators or untested paths.

## Final implementation state

Phase I concluded **C. NO CREDIBLE BOUNDARY** under its product contract and failed
the required credible-boundary gate. [AG-1 remains failed](evidence/ag1-feasibility-closeout.md).
Those results remain unchanged.

Phase II selected transformation-first hybrid privacy mediation in
[ADR-0010](decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md).
[Stage 2 maintenance was completed](evidence/phase-ii-stage-2-active-tree-cleanup.md),
but Stage 3 was not completed on canonical `main`, and Stages 4–18 remain
unfinished. Phase II did not reach production privacy mediation or prove a
production privacy boundary. The retained tree is a research/foundation tree,
not a finished privacy product.

[ADR-0011](decisions/ADR-0011-adopt-emulator-first-phase-ii-development.md)
allowed emulator-first research with deferred mandatory physical qualification.
That amendment remains part of the history. The owner suspended implementation
before the remaining [Phase II roadmap](phase-ii-roadmap.md) was executed.
Phase II was not fully executed and is not declared technically disproven or
universally impossible.

## Successor project

Active forward development moves to
[Privacy Decoy Virtual Android (PDVA)](https://github.com/innercoder78/privacy-decoy-virtual-android).
PDVA is a separate repository and a separate product lineage, rather than Phase
III or the next old requirement/ADR sequence. It takes lessons from Privacy
Decoy and keeps the privacy goal while changing direction toward a complete
interactive virtual Android guest with a deliberate guest/host boundary.

PDVA begins its own feasibility analysis and inherits no claim of success from
this repository. No particular virtualization engine or security boundary is
yet proven here, and full virtual Android is not claimed to be easier, secure,
or feasible. The successor changes the level at which isolation will be
investigated.

## Historical integrity

Old ADRs, PD-REQ-001..113, threat models, evidence, failed experiments, Unknowns,
and roadmaps remain intentionally preserved. No unfinished stage is reclassified
as success. Existing Privacy Decoy requirements remain authoritative only for
this historical product unless PDVA deliberately re-adopts a concept in its
own governance. Suspension retains the repository and research for possible
future resumption; it does not erase prior work or permanently abandon it.
