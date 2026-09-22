# Privacy Decoy 1.0 Canonical 46-PR Roadmap

## Repository provenance note — 2026-09-22

This document is the authoritative repository transcription of the canonical
Privacy Decoy 1.0 roadmap supplied by the project owner on 2026-09-22. Markdown
formatting is normalized for repository use, but normative roadmap obligations
are preserved. The [Canonical Audit Integration Amendment](canonical-audit-integration.md)
separately refines the roadmap and remains authoritative.

This resolves the source gap recorded in ADR-0004 without rewriting historical
evidence or the historical Roadmap PR 6 REDESIGN label. No production architecture
is selected, and canonical production PR 6 has not started. Roadmap PR numbering
is distinct from GitHub pull-request numbering. The approximate PR count does
not permit removal of either mandatory STOP gate.

## Canonical roadmap governance

The target remains approximately 46 cohesive PRs for Privacy Decoy 1.0, subject
to evidence and practical development needs.

The PR count is a planning estimate rather than a quota.

PRs may be combined, divided, inserted, or reordered when technically justified,
provided that:

* Reviewability remains strong.
* Security dependencies remain properly ordered.
* Mandatory checkpoints remain intact.
* Privacy boundaries are not deferred merely to reduce PR count.
* Changes remain cohesive.

The earlier approximately 40-PR roadmap is superseded.

Each PR includes appropriate:

* Tests.
* Documentation.
* Failure behavior.
* Complete-diff review.

Testing is not deferred until later testing PRs.

Feasibility documentation does not substitute for actual experiments.

## PR 1 — Minimal Android Foundation and Clean Repository Layout

Establish the deliberately clean repository root and create the Android Gradle
project beneath `android/`. Create a minimal Kotlin/Android application, normal
Gradle wrapper, namespace, versioning, test/lint foundation, basic CI, README,
`.github/CONTRIBUTING.md`, initial development conventions, initial
secret-handling defaults, and initial backup exclusions. GitHub Actions validates
Android from `android/`, and Android Studio opens `android/` correctly.

Do not implement the privacy runtime or imply protection is available. A fresh
ordinary Gradle wrapper includes `gradle-wrapper.jar`. When the wrapper must be
created, the binary-file preflight must identify PR 1 as requiring Desktop Codex.

## PR 1A — Repository Layout Correction

If development has already begun under the previous root-level Android layout,
insert PR 1A before PR 2.

PR 1A should:

* Move the Android Gradle project beneath `android/`.
* Move `CONTRIBUTING.md` beneath `.github/`.
* Preserve Git history through ordinary Git moves where practical.
* Update affected paths.
* Update GitHub Actions.
* Update documentation.
* Update relative links.
* Update Gradle commands.
* Update scripts.
* Update configuration.
* Update tests.
* Preserve Android behavior.
* Preserve dependencies.
* Preserve SDK versions.
* Preserve Gradle versions.
* Preserve AGP versions.
* Preserve application IDs.
* Preserve package names.
* Preserve permissions.
* Preserve privacy behavior.
* Preserve CI behavior except required path adjustments.

Do not regenerate the Gradle wrapper merely because it has moved.

An unchanged `gradle-wrapper.jar` moved byte-for-byte does not constitute a newly
generated binary.

If the JAR must actually be created, regenerated, or modified, the Desktop binary
workflow applies.

PR 1A is organizational and does not count as evidence of containment or privacy
feasibility.

Existing roadmap numbers do not need to be renumbered merely because PR 1A was
inserted.

## PR 2 — Threat Model, Requirements Register, and Engine Assessment

Define trust boundaries, adversaries, mandatory enforcement, evidence criteria,
supported-platform investigation, and distribution assumptions. Assess candidate
engines for architecture, isolation, native behavior, hidden-interface reliance,
licensing, provenance, maintenance, dependency risk, and management separation.
Select a justified prototype direction or report no credible candidate.

## PR 3 — Adversarial Containment Prototype

Using controlled probes, investigate protected-code isolation, cross-app
isolation, management-state protection, native filesystem access, direct Binder
paths, broker authorization, early initialization, dynamic code, and
trusted-component failure. Record actual enforcement and unresolved bypasses.

## PR 4 — Protected Networking Feasibility Prototype

Test the candidate network boundary with external VPN routing, per-app exclusion,
destination split routing, IPv4,
IPv6, DNS, native sockets, Java sockets, explicit network selection, open
connections, supervisor death, and VPN-loss races. Use controlled independent
packet observation. Identify required external enforcement dependencies.

## PR 5 — Early Feasibility Decision

Audit PRs 2 through 4 against mandatory constraints. Produce an evidence-backed
proceed/redesign/stop recommendation, proposed supported configuration,
unresolved-risk register, and architecture decision.

**STOP. Do not proceed automatically. Obtain explicit user approval.**

## PR 6 — Core Domain, Persistence, Capability, and Diagnostic Models

Implement models for protected-app identity, persona identity, persona
generation, assignments, policy inheritance, overrides, artifact references,
runtime authorization, coverage/evidence records, diagnostic event interfaces,
diagnostic session identity, and diagnostic redaction classifications. Define
migrations, corruption handling, and safe defaults. Do not enable ordinary apps
prematurely. Developer Mode need not yet have full UI, but later components use
safe structured diagnostic interfaces rather than ad hoc logging.

## PR 7 — Virtual Package Registry

Implement installed-package discovery, metadata, protected package records,
versions, signing identity, artifact-set identity, and registry persistence.
Apply host-information controls.

## PR 8 — Complete Package Import and Integrity

Implement complete APK/split staging, validation, immutable committed artifacts,
interruption recovery, and package-only import. Reject incomplete or inconsistent
artifact sets. Never import outside runtime data.

## PR 9 — Protected Storage and Key Boundaries

Implement protected app-private namespaces, management-state isolation, required
native restrictions, key and alias isolation where supported, temporary-data
handling, initial quotas, backup exclusions, and removal primitives. Include
management-only isolation requirements for the future Privacy Access Ledger and
Developer Mode diagnostics. Validate
direct bypass attempts.

## PR 10 — Launch Gateway, Runtime Supervisor, and Crash-Safe Diagnostic Foundation

Build the production execution gateway on the validated prototype. Add
generation-scoped authorization, mandatory initialization, early-code control,
process supervision, revocation, trusted-component death handling, and bounded
recovery. Introduce a minimal bounded persistent diagnostic journal for trusted
runtime events and crash breadcrumbs. This journal forms the technical foundation
for later Developer Mode. Do not record prohibited sensitive contents.
Unsupported configurations remain blocked.

## PR 11 — Binder, Service, and Provider Mediation

Expand validated brokerage into required Android services/providers. Enforce
caller identity, request validation, least privilege, handle lifecycle,
revocation, and host-service isolation. Block unsupported paths. Emit structured
safe diagnostic events for internal failures where useful.

## PR 12 — Policy Engine and Package Visibility

Implement Real/Decoy/Empty/Deny capability checks, policy precedence,
inheritance, overrides, safe transitions, and a virtual package universe. Cover
broad enumeration, targeted probing, intent resolution, and UID relationships.

## PR 13 — Synthetic Identifiers and Initial Device Profiles

Implement scoped synthetic identifiers, an initial validated device catalog,
constrained Custom Device, device/build/display coherence, capability validation,
and unsupported-combination handling.

## PR 14 — Telephony, Regional, and Local Network Persona

Implement supported SIM country, network country, carrier, MCC/MNC, locale,
language, timezone, Wi-Fi metadata, and local connectivity metadata. Validate
coherence. Do not claim public-IP spoofing.

## PR 15 — Synthetic Location

Implement city, postal, address, coordinates, stable persona points, geocoding
consent, precise/coarse semantics, supported location APIs, host-GPS
independence, cached-value handling, and failure probes.

## PR 16 — Privacy Access Ledger

Implement a minimized ledger schema, bounded storage, aggregation, retention,
redaction, clear-history behavior, overload handling, and management/user views.
Keep the Ledger conceptually and physically separate from Developer Mode
diagnostics. Permit safe correlation identifiers where useful.

## PR 17 — Persona Management and Initial Onboarding

Implement Default Persona, additional personas, assignment, duplication
semantics, overrides, Reset All to Persona, deliberate rotation, coordinated
transitions, user warnings, and first-run setup.

## PR 18 — Production VPN-Required Networking

Convert the validated network design into production policy. Implement a
default-on VPN requirement, effective-route validation, safe revocation,
reconnection handling, explicit OFF warnings, split-routing handling, and
supported protocol/client coverage. Privacy Decoy `VpnService` remains
prohibited.

## PR 19 — Initial Multiprocess and Lifecycle Integration

Validate identity, storage, policy, location, services, networking, and
diagnostic journaling across multiprocess execution, process recreation, reboot,
supervisor failure, and stale work. Add initial resource measurements.

## PR 20 — Integrated Architecture and Privacy Checkpoint

Audit the integrated supported configuration, including containment, native
boundaries, management isolation, package import,
personas, policy, storage, identity, services, location, networking, lifecycle,
Ledger, diagnostic foundation, and baseline resource behavior. Demonstrate
meaningful supported behavior with controlled probes and selected compatible apps
where safe. Document unsupported paths and evidence behind claims.

**STOP. Do not proceed automatically. Obtain explicit user approval.**

## PR 21 — Sensor Mediation

Implement supported sensor modes, coherent synthetic streams, Java paths, native
paths, availability metadata, lifecycle, background behavior, multiprocess
consistency, and bounded sampling.

## PR 22 — Personal-Data Mediation

Implement mediation for accounts, contacts, calendars, clipboard, call metadata,
SMS metadata, and related providers. Use safe defaults. Add synthetic content
only where useful. Real only when safely mediated and deliberately authorized.

## PR 23 — Camera, Microphone, Credentials, and Other Hardware Policies

Implement explicit supported/denied behavior for camera, microphone,
credentials, biometrics, Bluetooth, NFC, USB, and nearby devices. Validate
permission separation/lifecycle. Unsupported features remain blocked.
Hardware-attestation spoofing remains excluded.

## PR 24 — Protected Background Execution

Implement protected handling for jobs, alarms, deferred work, services, and
broadcasts. Test stale
generations, revocation, process death, scheduling limits, and background network
safety.

## PR 25 — Protected Notifications and Push

Implement Notification Broker behavior including protected indication,
lock-screen content policy, notification taps, actions, inline replies, push routing, stale-action
invalidation, and safe deferral/drop.

## PR 26 — Entry Points, Sharing, and Authentication Handoffs

Implement protected shortcuts, links, widgets, quick actions, incoming/outgoing
shares, and supported authentication handoffs. Define explicit boundary
crossings. Validate safe return routing.

## PR 27 — WebView and Browser-Surface Isolation

Implement per-app WebView isolation. Evaluate cookies, storage, cache,
rendering/browser surfaces, and host-browser interaction. Record limitations
honestly. Do not claim universal browser-fingerprint protection.

## PR 28 — Native Storage and Boundary Expansion

Expand adversarial native coverage for direct paths, file descriptors,
filesystem races, `/proc`, `/sys`, shared storage, and indirect escapes. Earlier
required native protection is not postponed until this PR.

## PR 29 — Media and Shared-Storage Bridges

Implement supported pickers, selected-content import/proxy, explicit export,
sharing grants, metadata controls, temporary cleanup, and interruption handling.

## PR 30 — Protected-App Update Pipeline

Implement update discovery, version comparison, complete staging,
signature/lineage validation, execution coordination, atomic package commit,
preserved persona, and preserved protected state. Ask before updating remains
default.

## PR 31 — Update Compatibility, Snapshots, and Safe Rollback

Implement compatibility reassessment, pre-execution recovery, post-migration
recovery, justified consistent snapshots, bounded snapshot storage, safe
downgrade blocking, safe rollback blocking, and data-loss warnings.

## PR 32 — Privacy Decoy Self-Updates

Implement published GitHub Release discovery, version rules, release notes,
dismissal, artifact trust checks, user-approved installation, privacy-preserving
metadata, and safe failure.

## PR 33 — Coverage, Compatibility, and Developer Mode Diagnostics

Expand coverage reporting into complete user/developer diagnostics.

Implement full Developer Mode behavior including:

- User-controlled enable/disable.
- Explicit warning.
- Persistent enabled state.
- Visible enabled indicator.
- Structured detailed diagnostics.
- Bounded persistent sessions.
- Crash-session detection.
- Recovery after Privacy Decoy crash.
- Viewing diagnostic sessions.
- Redaction.
- Export.
- Clear-log behavior.
- Diagnostic format versioning.
- Safe environment metadata.
- Protected-app pseudonymous references where useful.
- Separation from Privacy Access Ledger.

Scope diagnostic reports to appropriate:

- App version.
- Runtime version.
- Android version.
- OEM environment.
- Configuration.
- Coverage state.

Unknown and Partial remain explicit.

## PR 34 — Health Infrastructure and Resource Budgets

Implement health metrics, Health UI, reproducible benchmark workloads,
reference-device budgets, runtime status, network status, storage accounting,
health checks, safe temporary cleanup, Developer Mode storage status, and
Developer Mode overhead measurement.

## PR 35 — Memory, CPU, and Battery Hardening

Investigate retained objects, lifecycle leaks, sampling resources, wake locks, supervisor
efficiency, long standby, restart loops, idle overhead, and Developer Mode overhead.
Meet measured budgets without weakening protection.

## PR 36 — Low Storage, Backup/Transfer, and Deletion Hardening

Expand quotas, staging cleanup, snapshot cleanup, diagnostic bounds, disk-full
recovery, orphan detection, platform backup validation, platform transfer
validation, and removal semantics.

Verify:

- Transactional state integrity.
- Exported-file preservation.
- Diagnostic-store exclusion from automatic backup.

## PR 37 — Multiprocess, Revocation, and Recovery Stress

Stress concurrent policy changes, persona rotation, process death, broker
failure, supervisor failure, queued work, generation changes, and Developer Mode
crash persistence. Confirm no stale authorization, mixed-generation exposure, or
diagnostic behavior that weakens containment.

## PR 38 — Realistic Supported-App Compatibility Pass

Test representative real apps within validated scope. Classify
supported/unsupported/partial/compatibility failure. Make generalizable fixes.
Reject application-specific bypasses that weaken privacy.

Use Developer Mode diagnostics where useful, while preserving redaction
requirements.

## PR 39 — Accessibility, OEM, and Error-State Finalization

Harden TalkBack, large text, supported OEM behavior, onboarding, management
screens, notifications, blocked networking, unsupported apps,
storage failures, update failures, recovery explanations, Developer Mode warning
and indicator accessibility, and diagnostic-export UX.

## PR 40 — Integrated Security Regression Suite

Consolidate accumulated tests. Add missing cross-boundary adversarial cases,
mutation checks where useful, failure checks, diagnostic redaction tests,
Developer Mode isolation tests, crash-survival tests, and export-content tests.

Enforce reproducible regression execution.

## PR 41 — Resource and Network Soak Suite

Exercise sustained launches, Ledger traffic, Developer Mode traffic, sensors,
notifications, package probes, VPN transitions, runtime failures, updates, persona
rotations, media operations, low
memory/storage, and long idle. Verify bounded growth, bounded diagnostics,
no restart storms, and no privacy downgrade.

## PR 42 — Independent Security Assessment and Remediation

Arrange an independent security assessment of the actual implementation and claims.
Record reviewed revision, scope, evidence, findings, and remediation. Include
review of management isolation, diagnostic logging, diagnostic redaction,
diagnostic export, and crash-recovery behavior.

Remediate release blockers.

Obtain revalidation where appropriate.

External assessment is a real dependency.

Codex may not fabricate it.

This milestone may require several cohesive remediation PRs.

## PR 43 — Final Privacy, Security, Compatibility, and Diagnostic Documentation

Finalize the threat model, ADRs, supported matrix, persona semantics, policy
modes, VPN responsibilities, backup behavior, deletion behavior, update
limitations, rollback limitations,
remote-inference limits, Developer Mode, diagnostic privacy/export, and user
guidance.

## PR 44 — Release Engineering and Supply-Chain Validation

Finalize signing custody, reproducible-build practices where practical,
dependency inventory, release workflows, artifact naming, version/tag
consistency, secret protection, signature validation, and distribution
procedures. No release without explicit authorization.

## PR 45 — Final Installation, Upgrade, and Compatibility Regression

Verify clean installation, Privacy Decoy upgrades, persona/state migrations,
protected-package updates, rollback limits, self-update, Developer Mode
persistence across Privacy Decoy upgrades, diagnostic migration, supported
platform combinations, and realistic protected-app behavior. Use release candidates.

## PR 46 — 1.0 Release Candidate and Final Readiness

Perform final integrated review of privacy, security, functionality, resource
behavior, networking, upgrades, diagnostics, Developer Mode, documentation,
artifacts, repository structure, and supply chain. Resolve release blockers,
record exact revisions/evidence, and produce a readiness recommendation.
Readiness does not itself authorize merge, tag, publication, or release.

## Canonical checkpoint acceptance

### PR 5 acceptance

Require evidence that the proposed architecture has a credible enforceable
boundary under mandatory constraints.

Evidence must address at minimum:

* Protected-code containment.
* Management isolation.
* Native bypass risk.
* Broker authorization.
* Network-route feasibility.
* External VPN interaction.
* Ordinary non-rooted Android operation.

### PR 20 acceptance

Require an integrated demonstration of the supported core configuration.

The demonstration must include:

* Containment.
* Native boundaries.
* Management isolation.
* Package import.
* Personas.
* Policy.
* Storage.
* Identity.
* Services.
* Location.
* Networking.
* Lifecycle.
* Ledger.
* Initial diagnostic infrastructure.

There must be no hidden dependence on:

* Root.
* Privileged installation.
* Production-use ADB.
* Routine APK re-signing.
* Privacy Decoy `VpnService`.

Outside-versus-protected probes must use intentionally configured test permissions
and environments.

Do not assume the outside copy automatically has access to every genuine value.
