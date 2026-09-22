# Roadmap reconciliation

**Status:** canonical source gap resolved; architecture/feasibility pause remains

**Recorded:** 2026-09-22

## Restored governing source

The project owner restored the complete original roadmap source on 2026-09-22.
The authoritative [canonical roadmap](canonical-roadmap-1.0.md) now provides
Roadmap PR 1 through PR 46. Roadmap PR 5 and PR 20 are mandatory STOP gates, and
the exact amendment refinements for PRs 6, 12, 13, 14, 18, 21, 22, 23, 29, 33,
38, 40, 41, 43, and 46 are available. No roadmap content needed to be
reconstructed or guessed. Roadmap numbers remain distinct from GitHub PR numbers.

The source-restoration blocker described by ADR-0004 is resolved by ADR-0005.
This does not satisfy a technical security requirement, select a production
architecture, or authorize canonical PR 6 implementation.

## Historical variance preserved

- The repository foundation corresponds to canonical PR 1.
- The clean-root correction corresponds to the inserted PR 1A transition and
  does not renumber PRs 2–46.
- Historical threat/engine work substantially served canonical PR 2 research.
- Historical containment research substantially served canonical PR 3 research.
- Historical networking research substantially served canonical PR 4 research.
- The repository's REDESIGN decision was historically labeled **Roadmap PR 6**,
  although canonical governance calls for the early decision at PR 5. That
  historical event is not renamed or represented as having occurred under PR 5.
- Later redesign, S1, and S2 research is additional feasibility work performed
  because the early architecture did not survive.

Canonical production-domain PR 6 has **not** started and is not complete.
Historical REDESIGN, S1 falsification, the blocked S1 networking follow-up, and
all scoped containment/network evidence remain unchanged. Roadmap PR 20 has not
occurred.

## Open GitHub PR #10

GitHub PR #10, **Roadmap PR 8A: Audit S2 engine source and provenance**, is
supplemental redesign/feasibility research created after the early architecture
failed. It is not canonical production Roadmap PR 8 or an inserted production
PR 8A. Its historical title and findings are neither renumbered nor invalidated.

After this restoration PR merges, PR #10 may be refreshed onto current `main`,
reconciled against PD-REQ-001 through PD-REQ-085 and the restored canonical
documents, reviewed as research/evidence, and potentially merged under normal
hard-merge rules. This restoration does not modify, rebase, merge, or copy
evidence from PR #10.

Merging PR #10 would not authorize production implementation of canonical
Roadmap PR 6 or later. A separate project decision must determine the next
architecture direction from the REDESIGN/S1/S2 evidence.
