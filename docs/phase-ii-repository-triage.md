# Phase II repository triage plan

This plan changes no implementation and deletes nothing. Git history is the
archive; no permanent archive directory will be created. Every deletion requires
migration and dependency checks in a later reviewed change.

## KEEP

* `.editorconfig`, `.gitattributes`, `.gitignore`, and the Gradle wrapper and
  configuration.
* ADR-0001 through ADR-0010 and all `docs/evidence/**`. Failed experiments remain
  evidence and must not be rewritten or deleted.
* Historical architecture and redesign studies, including the admission-gated
  runtime and post-AG-1 work.
* Historical requirements, threat model, acceptance criteria, platform study,
  canonical roadmap, and reconciliations, clearly labeled by epoch.

## MODIFY / MIGRATE

* `README.md`, `.github/CONTRIBUTING.md`, platform support, roadmap reconciliation,
  the reference catalog, and Phase II requirements/governance documents.
* The current app foundation, `probe-app`, and `research-native`, without treating
  their Phase I observations as a Phase II boundary.
* Reusable AG-1 Java, dynamic/pre-code, and secondary-DEX fixtures.
* The APK analyzer's useful manifest, DEX, native, signing, and hash inventory,
  while removing old Protected-eligibility assumptions.
* Network evidence tools where useful as generic research tools, never as a PD
  local VPN architecture.
* Release-manifest verification and CI/workflow classification in a later cleanup
  PR. This PR does not modify the workflow or classifier.

## DELETE LATER AFTER MIGRATION AND DEPENDENCY CHECKS

* Old containment implementation and Protected-specific runtime gates/tests.
* Managed-profile controller/probe implementation and obsolete managed-profile
  runners/tooling.
* Obsolete AG-1 runners and parsers after useful fixtures are migrated.
* Other code whose sole purpose is enforcement of the retired Protected contract.

No historical evidence document is a deletion candidate merely because the
experiment failed. Later deletion proposals must identify retained fixtures,
references, dependency consumers, replacement validation, and the exact commit
where history remains accessible.

## Stage 2 active-tree disposition (implemented)

Stage 2 removed the active containment, network-gate, AG-1 runtime/admission, and managed-profile feasibility implementations and their emulator runners. Their evidence documents remain unchanged, and the deleted implementations remain available through Git history.

The AG-1 fixture modules were migrated without their failed policy semantics: `ag1-java-fixture` became `java-surface-fixture`, `ag1-dynamic-fixture` became `dynamic-code-surface-fixture`, `ag1-precode-fixture` became `early-init-loader-fixture` (including the direct `InMemoryDexClassLoader` adversarial behavior), and `ag1-secondary-dex-fixture` became `secondary-dex-fixture`. `ag1-admission-analyzer.py` became the neutral `artifact-analyzer.py`; it reports inventory, hashes, structural consistency, and risk-presence indicators, never eligibility or safety from absence.

`probe-app`, `research-native`, the standalone `external-vpn-fixture`, and the independent packet evidence utility were retained. The external fixture is not a Privacy Decoy module dependency and does not change the permanent prohibition on PD-owned `VpnService`. The app is now a minimal buildable foundation with no Phase II acquisition, transformation, signing, Persona, mediation, or native-hook implementation.
