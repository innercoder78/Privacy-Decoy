# ADR-0004: Canonical audit reconciliation

- **Status:** Accepted — specification correction / implementation remains paused
- **Date:** 2026-09-22
- **Scope:** documentation and governance only; no runtime architecture selection

## Context

The restored Privacy Decoy Project Instructions and Canonical Audit Integration
Amendment are normative. Repository requirements PD-REQ-001 through PD-REQ-070
substantially covered many obligations but only partially represented the full
amendment. Review also found roadmap drift: repository history records the
feasibility REDESIGN decision as Roadmap PR 6 while the restored canonical
specification requires an early Roadmap PR 5 STOP gate and a future Roadmap PR 20
STOP gate.

The authoritative roadmap contains 46 roadmap PRs, but its exact per-PR sequence
is unavailable in repository evidence. Exact amendment-specific refinement text
for Roadmap PRs 6, 12, 13, 14, 18, 21, 22, 23, 29, 33, 38, 40, 41, 43, and 46 is
also unavailable. Reconstructing either from GitHub PR numbering or current
historical labels would fabricate canonical content.

## Decision

1. The new Project Instructions and Canonical Audit Integration Amendment govern
   where prior working assumptions conflict or are incomplete.
2. PD-REQ-001 through PD-REQ-070 retain their identifiers and historical meaning.
   PD-REQ-071 through PD-REQ-085 are deliberate additive obligations covering
   advertising identifiers, battery/power, descriptive-versus-runtime Android
   identity, network-persona/public-IP rules, sensor coherence, permission and
   personal-data separation, persona templates, diagnostic separation, 1.0 media
   scope, competitive drift, SDK/library paths, expanded testing, the 1.0 gate,
   roadmap governance, and repository/publication privacy.
3. The normalized [canonical integration](../canonical-audit-integration.md),
   [1.0 acceptance checklist](../acceptance-criteria-1.0.md), and
   [roadmap reconciliation](../roadmap-reconciliation.md) are accepted as the
   repository representation of currently available authoritative obligations.
   The currently recoverable obligations have been integrated; the full amendment
   remains authoritative. Exact roadmap/refinement source gaps remain explicitly
   recorded and block later implementation until restoration and review. These
   documents are not verbatim copies of unavailable source text.
4. Historical evidence and ADRs remain scoped and unchanged. ADR-0002's
   **Roadmap PR 6 — A REDESIGN** event is not renamed or reversed. ADR-0003's
   **NO PRODUCTION ARCHITECTURE SELECTED** decision and S1 falsification are not
   reversed. PR 4 containment and PR 5 networking evidence are not invalidated or
   upgraded.
5. S2 findings in open GitHub PR #10 are not invalidated, copied, merged, or
   modified here. They remain unmerged and separate from canonical main state.
6. The canonical 46-PR roadmap, Roadmap PR 5 and PR 20 STOP gates, and listed
   refinement obligations remain authoritative. Roadmap PR 20 has not occurred.
   The exact missing sequence/refinement text must not be invented.
7. Later roadmap implementation remains paused until the exact canonical roadmap
   source and refinements are restored into the repository and reviewed.

## Consequences

This decision corrects specification and governance only. It selects no engine,
containment model, runtime, distribution path, supported platform, or production
architecture. It authorizes no ordinary protected apps, real accounts, private
user data, production claims, Android/runtime change, or continuation of S2.

Existing Known Gap, Partial, Unsupported, Unknown, preliminary, and not-yet-
evidenced states remain bounded as recorded. Green prototype CI is not production
protection. The Roadmap PR 6 REDESIGN decision provides relevant substantive
feasibility/governance evidence but does not retroactively become Roadmap PR 5,
and it does not satisfy the future Roadmap PR 20 gate.

Implementation may resume only after the missing canonical roadmap material is
restored and reviewed and the resulting authoritative sequence permits it. Later
production claims remain additionally subject to PD-REQ-083 and independent
security review.
