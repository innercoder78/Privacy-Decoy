# ADR-0011: Adopt emulator-first Phase II development with deferred physical qualification

**Status:** Accepted

**Date:** 2026-10-05

**Owner decision:** This is an explicit project-owner governance decision to
amend Phase II development gating and evidence timing. It does not establish
implementation success or production privacy coverage.

## Context and preserved decisions

OEM/device rollout schedules and hardware availability should not unnecessarily
block architectural development when bounded research can proceed in a controlled
environment. The stronger final physical evidence standard remains mandatory.

[ADR-0010](ADR-0010-select-transformation-first-hybrid-privacy-mediation.md)
remains the Phase II transformation-first hybrid architecture decision. This ADR
changes development/evidence sequencing only. Phase I remains **C. NO CREDIBLE
BOUNDARY**, [AG-1 remains FAILED](../evidence/ag1-feasibility-closeout.md), and
all historical evidence, failures, and Unknowns remain as recorded.

## Decision

1. Android Studio's Android 17 / API 37 emulator is the primary Phase II
   development and research environment for Stages 3 through 15.
2. Successful emulator/debug evidence MAY authorize continued research and
   implementation into subsequent roadmap stages when the applicable
   research-stage gate is otherwise satisfied. Stage-specific STOP conditions
   and known failures still require STOP or evidence-backed narrowing.
3. Emulator/debug success MUST NOT be represented as physical-device evidence,
   release-equivalent evidence, production privacy evidence, multi-OEM/device
   evidence, or proof that OEM/hardware-specific behavior is covered.
4. Unknown remains Unknown. An emulator result cannot convert untested physical
   behavior into success or establish production coverage for that scope.
5. Physical validation before Stage 16 is encouraged when useful or readily
   available, but is not required merely to continue forward Phase II development.
   If a stage's subject cannot be meaningfully evaluated in an emulator, its
   evidence record MUST explicitly identify that limitation and any earlier
   physical evidence needed to satisfy the affected research gate. Emulator
   success must not be invented for unavailable surfaces.
6. Stage 16 is the mandatory physical qualification gate before independent
   production security review and release consideration. It MUST revalidate all
   applicable emulator-developed claims on stock non-rooted Android 17 / API 37,
   ARM64, across multiple supported physical devices/OEMs, using release-equivalent
   Manager and transformed builds. Evidence MUST identify the exact Android/API,
   device, OEM, build, ABI, source base/split artifacts, source `targetSdkVersion`,
   and transformation/runtime versions, and cover relevant native, lifecycle,
   background, and process behavior without prohibited root/privileged mechanisms.
7. Any behavior that succeeds in emulator research but fails or differs materially
   on physical devices MUST invalidate the affected evidence/coverage. It MUST be
   fixed, narrowed, classified Unknown/Unsupported as appropriate, or result in
   REDESIGN/STOP when necessary. Prior emulator results cannot paper over the
   physical result. Missing or failed required physical evidence MUST block
   Stage 17/18 production progression for the affected scope.
8. Stage 17 independent security and supply-chain review remains mandatory after
   physical qualification. Findings must be resolved or the supported scope
   narrowed before production consideration.
9. Stage 18 remains the explicit owner **GO / narrower scope / REDESIGN / STOP**
   decision. No production/release claim may be based only on emulator/debug
   evidence, and no roadmap gate automatically authorizes release.

## Explicit non-changes

This decision does NOT:

* weaken, renumber, or delete PD-REQ-001..113, including PD-REQ-096..113 and the
  exact production-scope obligation in PD-REQ-112;
* weaken Unknown semantics, change **Real / Decoy / Empty / Deny**, or authorize
  genuine-data fallback;
* change the coverage vocabulary: **Fully mediated, Partially mediated,
  Unsupported, External, N/A, Unknown**, or conflate compatibility with privacy
  coverage;
* authorize root, Magisk/Xposed/LSPosed, privileged/system installation,
  production ADB, custom ROM/patched kernel requirements, or guest root;
* authorize PD `VpnService` or full guest Android;
* alter the transformation-first architecture or revive the failed Phase I
  admission-gated runtime;
* make emulator evidence production evidence, remove physical testing before
  release, or remove independent security review.

## Consequences

The [roadmap](../phase-ii-roadmap.md), [acceptance criteria](../phase-ii-acceptance-criteria.md),
and [development guidance](../../.github/CONTRIBUTING.md) distinguish forward
research gates from production qualification. Emulator-developed results remain
bounded research/development evidence pending Stage 16 physical qualification.
Missing physical qualification cannot be labeled Fully mediated for a production
scope. No new requirement or coverage status is introduced.

This governance amendment does not claim Stage 3 complete, record a successful
Stage 3 emulator run, implement any roadmap stage, or begin Stage 4.
