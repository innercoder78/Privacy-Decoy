# ADR-0001: Engine prototype direction

- **Status:** Accepted for prototype only; no production engine selected
- **Date:** 2026-09-19

## Problem and context

Privacy Decoy needs evidence that an ordinary, root-free Android application can
contain adversarial protected code and mediate privacy-sensitive state without
its own `VpnService`, privileged installation, or routine ADB. Existing
virtualization projects emphasize compatibility; compatibility and hook coverage
do not establish the required boundary. Current candidate provenance and modern
API evidence also contain material unknowns documented in the
[engine assessment](../engine-assessment.md).

## Constraints

The prototype must not imply protection or ship as production. It must preserve
artifact identity if possible, fail before protected code on unknown mandatory
coverage, address native/multiprocess/early-init paths, use controlled probes only,
and isolate any engine behind a replacement boundary. No root framework, custom
ROM/kernel, guest root, privileged install, full VM merely for convenience, or
Privacy Decoy `VpnService` is acceptable. License/provenance must be known before
third-party code enters the repository.

## Options considered and evidence

1. Adopt VirtualApp or a commercial descendant: historically capable and useful
   for research, but public/commercial lineage, license, maintenance, native and
   API 31–37 evidence are unresolved.
2. Adopt DroidPlugin: inspectable historical design, but its documented native
   limitation and modernity gap conflict with the adversarial model.
3. Adopt original BlackBox, closed SpaceCore, or chiyuan5/VirtualSpace: corrected
   current evidence respectively shows dissolved/degraded source, an opaque SDK,
   and a small/new project with an unresolved README/license-file discrepancy.
4. Use Shelter/Island/Insular-style managed profiles: strong platform isolation
   comparator, but not the synthetic-persona/service mediation required.
5. Run a full Android VM: potentially stronger isolation, but device availability,
   large TCB, distribution, ABI, resource and integration costs are unresolved and
   convenience is not sufficient justification.
6. Build a narrow bespoke research boundary: no inherited compatibility claim,
   maximum control of experiments, but substantial engineering burden and no
   assurance the architecture is feasible.

Evidence is the primary-source inventory and explicitly unknown matrix in the
assessment, Android's documented UID sandbox/profile/non-SDK constraints, and the
[threat model](../threat-model.md). No candidate has complete accepted evidence.
The 2026-09-19 source/provenance corrections were re-evaluated and strengthen,
rather than invalidate, the prototype-only direction below.

## Provisional decision

PR 4 should prototype a **narrow bespoke, engine-replaceable containment test
boundary**, not a general app runtime. It may build probes/adapters needed to
falsify the core assumptions. It must not import candidate source until an
immutable-ref provenance, license, dependency and native-binary audit is reviewed.
Candidate projects remain comparators; work-profile behavior is a baseline.

This is prototype-only because native/syscall containment, framework/Binder
coverage, early initialization, process/UID semantics, OEM/API behavior, artifact
loading and fail-closed launch are unproven. It authorizes neither protected-app
use nor a production engine. PR 6, after PR 4 and PR 5 evidence, is the mandatory
user decision gate.

## Rejected or deferred

- Production selection of every named engine is deferred.
- DroidPlugin is rejected as the lead prototype while the native limitation stands.
- Original BlackBox is rejected in its dissolved/source-degraded state; fork use
  remains rejected until ancestry, provenance and licensing are resolved.
- SpaceCore is rejected for production while its engine SDK is closed and cannot
  support complete provenance, native audit, SBOM and independent review.
- chiyuan5/VirtualSpace remains a research comparator pending license resolution
  and adversarial evidence; current activity alone does not select it.
- Managed profile redesign is deferred but retained if persona goals can be
  reconciled with its documented boundary.
- Full VM is deferred unless evidence shows it is necessary, deployable, and
  proportionate rather than convenient.
- APK rewriting/re-signing is not selected; a future narrow exception requires a
  separate ADR and explicit user approval.

## Assumptions, limitations, and risks

The host OS/kernel is trusted as defined by the threat model. API 31–37, OEM and
ABI support remain provisional. A bespoke probe can under-model real applications;
framework internals can change; hidden APIs can fail; native code may bypass every
user-space technique; the effort may grow into an unauditable TCB; and a clean
adapter can still leak authority through brokers. Networking remains independently
gated by PR 5.

The decision is invalidated if a candidate produces reproducible, license-clean
evidence covering the mandatory matrix more safely; if controlled execution
necessarily requires disallowed privilege/re-signing; if protected code can reach
management state or bypass mandatory mediation; if coverage cannot block before
early code; or if the boundary cannot remain smaller and reviewable. Invalidation
causes reassessment, redesign, unsupported status, or STOP—not real-data fallback.

## Replacement boundary expectations

Management UI, persona/policy storage and coverage decisions must depend on a
small internal interface, never candidate-specific classes. The adapter owns
lifecycle and translates opaque instance IDs and least-authority capabilities;
it exposes no database paths or secrets. Calls are authenticated, authorized,
versioned, revocable and fail closed. Candidate dependencies/native artifacts stay
within the engine module boundary (if a later PR creates one), with SBOM, license
and immutable provenance. Contract tests must run against test doubles and every
candidate so removal does not migrate trust into management code.
