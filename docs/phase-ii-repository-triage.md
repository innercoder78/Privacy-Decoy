# Phase II repository triage record

This document records the current active-tree disposition after Phase II Roadmap
Stage 2. The original triage plan authorized no implementation change itself;
Stage 2 subsequently performed the reviewed migration and deletion work. Git
history is the archive. No permanent archive directory is created, and historical
ADRs and `docs/evidence/**` remain preserved.

## KEEP — retained active material

* `.editorconfig`, `.gitattributes`, `.gitignore`, the Gradle wrapper, and active
  build configuration.
* ADR-0001 through ADR-0010, all historical evidence, historical requirements,
  threat models, acceptance criteria, architecture studies, platform studies,
  roadmaps, and reconciliations. Failed experiments remain evidence and are not
  rewritten as success.
* The minimal `:app` Phase II foundation, without a mediation runtime or claims.
* `probe-app` and `research-native` as reusable adversarial probes, not proof of
  containment or Phase II privacy coverage.
* The independent packet evidence utility as a generic analysis tool, never as a
  PD local-VPN architecture.
* The standalone `external-vpn-fixture` as an unmistakably separate test app. It
  is not an `:app` dependency and does not alter the prohibition on PD-owned
  `VpnService`.
* Release-manifest verification and Phase II path-aware CI classification.

## RETAINED / MIGRATED in Stage 2

* `ag1-java-fixture` became `java-surface-fixture`.
* `ag1-dynamic-fixture` became `dynamic-code-surface-fixture`.
* `ag1-precode-fixture` became `early-init-loader-fixture`, retaining early
  Application/ContentProvider behavior and the direct `InMemoryDexClassLoader`
  adversarial surface.
* `ag1-secondary-dex-fixture` became `secondary-dex-fixture`.
* `ag1-admission-analyzer.py` became `artifact-analyzer.py`. It inventories
  hashes, APK/DEX/native/manifest/signing metadata, and positive risk indicators.
  It reports evidence-backed structural `VALID`, `INVALID`, or `UNKNOWN` without
  treating structural validity or absent indicators as safety or privacy coverage.
  Capability coverage remains Unknown, and split completeness remains Unknown
  unless a future positively evidenced mechanism establishes completeness.

Historical documents retain old names where they describe historical work.
Renaming active fixtures does not alter those records or make AG-1 successful.

## DELETED from the active tree in Stage 2

After dependency and reuse review, Stage 2 removed:

* the old containment/session boundary, Protected-specific runtime gates, and
  their app unit/instrumentation tests;
* the PD-app network gate/broker and its tests;
* AG-1 authorization/bootstrap/runtime policy code and runtime evidence parsers;
* managed-profile controller/probe modules and their evidence/emulator tooling;
* containment, managed-profile, AG-1 pre-code/dynamic-code, and old network
  feasibility emulator runners; and
* obsolete app fixture-asset generation and the debug-only `:research-native`
  dependency from `:app`.

The deleted implementation remains accessible through Git history. Its evidence
remains in the repository and is not converted from failure or Unknown to success.

## Deferred beyond Stage 2

No identified obsolete Stage 2 runtime component remains queued for deletion.
Future roadmap stages—not this triage cleanup—own artifact acquisition,
transformation, signing, Persona/runtime mediation, permission policy, and any
evidence-scoped native work. Stage 2 implements none of those capabilities.
