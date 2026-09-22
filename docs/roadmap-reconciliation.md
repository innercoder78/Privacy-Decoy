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

## Merged GitHub PR #10

GitHub PR #10, **Roadmap PR 8A: Audit S2 engine source and provenance**, is
supplemental redesign/feasibility research created after the early architecture
failed. It is not canonical production Roadmap PR 8 or an inserted production
PR 8A. Its historical title and findings are neither renumbered nor invalidated.

PR #10 was reconciled and merged after source restoration. Its final reviewed
head was `ad2f0d4d9083ee49866ce6998ac3db0d0225d667`; the resulting current-main
merge commit at the time of this decision was
`09b45dba63523a66efac297ac22ea4be99922af1`. Its S2 findings remain unchanged:
VirtualSpace and Blacks-BlackBox are **STOPPED_UNRESOLVED**, neither is
AUDIT_PASS, and no engine is audit-cleared.

The merge did not authorize production implementation of canonical Roadmap PR 6
or later.

## Subsequent architecture decision

On 2026-09-22, the project owner selected **REDESIGN AGAIN**.
[ADR-0006](decisions/ADR-0006-redesign-again-architecture-discovery.md) records
that choice and authorizes one bounded, final open-ended architecture-discovery
phase under the current product goals. It selects no production architecture;
canonical production PR 6 remains unstarted and product implementation remains
paused.

## Post-synthesis owner decision (2026-09-22)

GitHub PR #18's architecture synthesis merged at
`d1450cf98f9ad7cd87185d9b6711cd25ef2a885c` and recommended **STOP UNDER CURRENT
GOALS**. Afterward, the owner reviewed ten-source reference research and chose to
continue through the admission-gated hypothesis recorded by
[ADR-0007](decisions/ADR-0007-admission-gated-controlled-runtime.md). The old
synthesis remains historical evidence and is not retroactively rewritten.

AG-1 is supplemental feasibility work before canonical production PR 6, which
remains unstarted until AG-1 succeeds. No third-party engine is selected
wholesale. This documentation decision has no hardcoded future GitHub PR number.
