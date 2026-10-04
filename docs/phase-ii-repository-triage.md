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
