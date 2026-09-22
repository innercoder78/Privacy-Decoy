# ADR-0005: Canonical roadmap source restoration

- **Status:** Accepted — canonical source restored / implementation governance updated
- **Date:** 2026-09-22
- **Scope:** documentation and governance only; no runtime architecture selection

## Context

ADR-0004 correctly recorded that the exact roadmap and amendment refinement
source was unavailable at that time. The project owner has now supplied the
original canonical specification and full audit amendment. The complete 46-PR
roadmap and all listed amendment refinements are now available.

## Decision

1. [`docs/canonical-roadmap-1.0.md`](../canonical-roadmap-1.0.md) is the
   authoritative restored roadmap.
2. Canonical PR 5 and PR 20 STOP gates remain mandatory.
3. The historical Roadmap PR 6 REDESIGN event is preserved under its actual
   historical label; it is not retroactively renamed PR 5.
4. PD-REQ-001 through PD-REQ-085 remain in force and unchanged.
5. The amendment refinements are authoritative.
6. The “source unavailable” implementation blocker is resolved.

## Consequences

This restoration does **not** declare the old architecture viable. It does not
reverse REDESIGN, S1 falsification, containment or network evidence, ADR-0002, or
ADR-0003. It does not select a production engine or authorize production
implementation. Product implementation remains paused for architecture and
feasibility reasons, and all technical acceptance and evidence gates remain.
