# Roadmap reconciliation

> **Phase II notice (2026-10-04):** This document reconciles historical Phase I
> sequencing and remains evidence of that epoch. It does not authorize resuming
> old canonical Roadmap PR 6. The active forward plan is the
> [Phase II roadmap](phase-ii-roadmap.md), selected by
> [ADR-0010](decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md).
> Phase I remains **C. NO CREDIBLE BOUNDARY**, AG-1 remains failed, and no old
> Unknown or failed gate is converted to success.

**Status:** AG-1 failed; canonical Roadmap PR 5 owner decision REDESIGN; bounded enforcement-boundary research authorized; production paused

**Recorded:** 2026-09-22

**Forward decision updated:** 2026-09-29

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

AG-1 is supplemental feasibility work before canonical production PR 6.
Canonical PR 6 remains unstarted. AG-1 success is necessary but not sufficient:
after AG-1, its evidence must be reviewed through the still-mandatory canonical
Roadmap PR 5 STOP/owner gate, and explicit project-owner approval is required
before PR 6 may begin. AG-1 failure favors STOP rather than weakening Protected
Mode. No third-party engine is selected wholesale. This documentation decision
has no hardcoded future GitHub PR number.

## AG-1 closeout and canonical PR 5 gate — 2026-09-29

GitHub [PR #22](https://github.com/innercoder78/Privacy-Decoy/pull/22),
**research: prototype AG-1 dynamic-code compatibility**, merged at
`b1754bc02c2b9cbf54f29a2d3afa16bd2967628e` (tree
`0adb246bcb888a10f4f3ed7b51213ff3f85956ee`). Its AG-1C evidence positively
demonstrated previously unadmitted DEX reaching entry invocation through the
tested direct `InMemoryDexClassLoader` path without trusted-helper authorization.
The successful observation workflow did not establish runtime mediation.

**AG-1 CHECKPOINT: FAILED UNDER CURRENT HYPOTHESIS.** The
[question-by-question closeout](evidence/ag1-feasibility-closeout.md) preserves
AG-1A and AG-1B's bounded positive findings, AG-1C's trusted-helper behavior,
and its decisive direct-loader falsification. Native/direct-syscall containment
and other untested paths remain Unknown; completing another experiment is not
required to establish this failure. Historical slice-level IN PROGRESS notes
remain historical and are superseded for overall status by this closeout.

The [canonical Roadmap PR 5 feasibility decision package](evidence/canonical-pr5-feasibility-decision.md)
became the active governance gate at closeout. It is not GitHub PR #5 and does
not relabel the historical Roadmap PR 6 REDESIGN decision. The repository recommendation is
**STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**;
Tony's owner response was pending at that publication. The subsequent decision
below resolves that response without changing the evidence recommendation.

AG-1 success was necessary but not sufficient for production PR 6; that
prerequisite was not met. Canonical production PR 6 remains unstarted and
unauthorized, and PR 20 has not occurred. No production PR 6 work may begin
unless canonical governance conditions are satisfied and Tony explicitly
authorizes it. This closeout authorizes no runtime remediation, native experiment,
new engine, or requirement weakening. The source-restoration blocker remains
resolved; the pause is due to feasibility and governance.

## Post-AG-1 owner decision — 2026-09-29

GitHub [PR #23](https://github.com/innercoder78/Privacy-Decoy/pull/23),
**docs: close AG-1 and prepare canonical PR 5 decision**, merged. Main at this
owner-decision baseline is `b289873284250881ac7379ede3356c1dc216f309`, tree
`2883d025a1db10fe3c97bfad197761ba8d6104ef`.

Tony's canonical Roadmap PR 5 owner decision is now **REDESIGN**.
[ADR-0008](decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md) supersedes
ADR-0007 for forward architecture work. ADR-0007 remains an Accepted historical
decision whose hypothesis failed AG-1; ADR-0006 and all prior evidence remain
historical. **AG-1 remains FAILED**, and the canonical PR 5 evidence recommendation
remains **STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**.
The owner response is resolved as REDESIGN, not PROCEED or final project STOP,
and not a technical feasibility pass.

The supplemental [post-AG-1 enforcement-boundary redesign phase](post-ag1-enforcement-boundary-redesign.md)
comes before any production Roadmap PR 6 work. It authorizes bounded architecture,
authority, and bypass research using all accumulated evidence and the ten-project
catalog. No architecture has already been found, no runtime prototype is
authorized, and no engine is selected wholesale. PD-REQ-001..095 and canonical
roadmap numbering remain unchanged. Canonical production PR 6 remains unstarted
and unauthorized; implementation remains paused and PR 20 has not occurred.
Future production work still requires canonical feasibility governance and Tony's
explicit authorization; this research decision does not waive AG-1's failed
prerequisite or approve a replacement gate.
