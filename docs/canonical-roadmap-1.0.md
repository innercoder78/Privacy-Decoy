# Privacy Decoy 1.0 Canonical 46-PR Roadmap

This document restores the exact roadmap source supplied by the project owner on
2026-09-22. It supersedes the “roadmap source unavailable” assumption recorded
in ADR-0004 and the earlier roadmap reconciliation. It does **not** supersede
historical evidence, rename completed historical work, or establish that a
production architecture is viable. Roadmap PR numbers are distinct from GitHub
pull-request numbers.

## PR 1 — Minimal Android Foundation and Clean Repository Layout

Establish the deliberately clean repository root and create the Android Gradle
project beneath `android/`. Create a minimal Kotlin/Android application, normal
Gradle wrapper, namespace, versioning, test/lint foundation, basic CI, README,
`.github/CONTRIBUTING.md`, initial development conventions, initial
secret-handling defaults, and initial backup exclusions. GitHub Actions validates
Android from `android/`, and Android Studio opens `android/` correctly.

Do not implement the privacy runtime or imply protection is available. A fresh
wrapper binary requires the Desktop binary workflow.

**Existing-old-layout transition:** **PR 1A — Repository Layout Correction** may
be inserted without renumbering the remaining roadmap.

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

Test external VPN routing, per-app exclusion, destination split routing, IPv4,
IPv6, DNS, native sockets, Java sockets, explicit network selection, open
connections, supervisor death, and VPN-loss races. Use controlled independent
packet observation. Identify required external enforcement dependencies.

## PR 5 — Early Feasibility Decision

Audit PRs 2 through 4 against mandatory constraints. Produce an evidence-backed
proceed/redesign/stop recommendation, proposed supported configuration,
unresolved-risk register, and architecture decision.

**STOP. Do not proceed automatically. Obtain explicit user approval.**

Acceptance requires credible evidence addressing at minimum protected-code
containment, management isolation, native bypass risk, broker authorization,
network-route feasibility, external VPN interaction, and ordinary non-rooted
Android operation.

## PR 6 — Core Domain, Persistence, Capability, and Diagnostic Models

Implement models for protected-app identity, persona identity, persona
generation, assignments, policy inheritance, overrides, artifact references,
runtime authorization, coverage/evidence records, diagnostic event interfaces,
diagnostic session identity, and diagnostic redaction classifications. Define
migrations, corruption handling, and safe defaults. Do not enable ordinary apps
prematurely. Developer Mode need not yet have full UI, but later components use
safe structured diagnostic interfaces rather than ad hoc logging.

**Canonical Audit Amendment refinement:** The capability model must represent
advertising-related identifiers, power/battery surfaces, and other environmental
categories without introducing one-off policy systems. Coverage records must
support those categories and evidence states.

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
management-only isolation for future Ledger and Developer Mode stores. Validate
direct bypass attempts.

## PR 10 — Launch Gateway, Runtime Supervisor, and Crash-Safe Diagnostic Foundation

Build the production execution gateway on the validated prototype. Add
generation-scoped authorization, mandatory initialization, early-code control,
process supervision, revocation, trusted-component death handling, and bounded
recovery. Introduce a minimal bounded persistent diagnostic journal. Do not
record prohibited sensitive contents. Unsupported configurations remain blocked.

## PR 11 — Binder, Service, and Provider Mediation

Expand validated brokerage into required Android services/providers. Enforce
caller identity, request validation, least privilege, handle lifecycle,
revocation, and host-service isolation. Block unsupported paths. Emit structured
safe diagnostic events where useful.

## PR 12 — Policy Engine and Package Visibility

Implement Real/Decoy/Empty/Deny capability checks, policy precedence,
inheritance, overrides, safe transitions, and a virtual package universe. Cover
broad enumeration, targeted probing, intent resolution, and UID relationships.

**Canonical Amendment refinement:** Preserve explicit per-execution
package-universe control. Persona sharing **MUST NOT** automatically grant mutual
visibility. Targeted package probing remains mandatory package-visibility
analysis.

## PR 13 — Synthetic Identifiers and Initial Device Profiles

Implement scoped synthetic identifiers, an initial validated device catalog,
constrained Custom Device, device/build/display coherence, capability validation,
and unsupported-combination handling.

**Canonical Amendment refinement:** Explicitly investigate and, where feasible,
implement advertising-related identifiers, descriptive Android/build identity,
battery/charging/power-state surfaces, and cross-property device coherence. Do
**NOT** advertise Android platform versions or capabilities inconsistent with the
actual protected runtime.

## PR 14 — Telephony, Regional, and Local Network Persona

Implement supported SIM country, network country, carrier, MCC/MNC, locale,
language, timezone, Wi-Fi metadata, and local connectivity metadata. Validate
coherence. Do not claim public-IP spoofing.

**Canonical Amendment refinement:** Include network transport/type information,
local-network metadata, and cross-checking network persona against regional
persona where technically meaningful. Do not infer public-IP protection from
local values.

## PR 15 — Synthetic Location

Implement city, postal, address, coordinates, stable persona points, geocoding
consent, precise/coarse semantics, supported location APIs, host-GPS
independence, cached-value handling, and failure probes.

## PR 16 — Privacy Access Ledger

Implement a minimized ledger schema, bounded storage, aggregation, retention,
redaction, clear-history behavior, overload handling, and management/user views.
Keep Ledger separate from Developer Mode diagnostics. Allow safe correlation
identifiers where useful.

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

**Canonical Amendment refinement:** Add conservative network-persona consistency
diagnostics where reliable evidence exists. Any external exit-IP/geography lookup
must be deliberate, user-authorized, privacy-conscious/minimized, approximate
evidence, and nonessential to fail-closed enforcement. Unknown is preferable to
guessed geography.

## PR 19 — Initial Multiprocess and Lifecycle Integration

Validate identity, storage, policy, location, services, networking, and
diagnostic journaling across multiprocess execution, process recreation, reboot,
supervisor failure, and stale work. Add initial resource measurements.

## PR 20 — Integrated Architecture and Privacy Checkpoint

Audit containment, native boundaries, management isolation, package import,
personas, policy, storage, identity, services, location, networking, lifecycle,
Ledger, diagnostic foundation, and baseline resource behavior. Demonstrate
meaningful supported behavior with controlled probes and selected compatible apps
where safe. Document unsupported paths and evidence.

**STOP. Do not proceed automatically. Obtain explicit user approval.**

Acceptance requires an integrated demonstration including containment, native
boundaries, management isolation, package import, personas, policy, storage,
identity, services, location, networking, lifecycle, Ledger, and initial
diagnostic infrastructure. There must be no hidden dependence on root,
privileged installation, production-use ADB, routine APK re-signing, or Privacy
Decoy `VpnService`.

## PR 21 — Sensor Mediation

Implement supported sensor modes, coherent synthetic streams, Java paths, native
paths, availability metadata, lifecycle, background behavior, multiprocess
consistency, and bounded sampling.

**Canonical Amendment refinement:** Add explicit cross-API and
temporal-coherence testing requirements.

## PR 22 — Personal-Data Mediation

Implement mediation for accounts, contacts, calendars, clipboard, call metadata,
SMS metadata, and related providers. Use safe defaults. Add synthetic content
only where useful. Real only when safely mediated and deliberately authorized.

**Canonical Amendment refinement:** Maintain preference for Empty where
fictional data adds little benefit.

## PR 23 — Camera, Microphone, Credentials, and Other Hardware Policies

Implement explicit supported/denied behavior for camera, microphone,
credentials, biometrics, Bluetooth, NFC, USB, and nearby devices. Validate
permission separation/lifecycle. Unsupported features remain blocked.
Hardware-attestation spoofing remains excluded.

**Canonical Amendment refinement:** Synthetic camera/media injection is deferred
advanced post-1.0 work. For 1.0 establish physical paths safely as supported,
denied, unavailable, or Unsupported without requiring synthetic camera input.

## PR 24 — Protected Background Execution

Implement jobs, alarms, deferred work, services, and broadcasts. Test stale
generations, revocation, process death, scheduling limits, and background network
safety.

## PR 25 — Protected Notifications and Push

Implement Notification Broker behavior including protected indication,
lock-screen policy, taps, actions, inline replies, push routing, stale-action
invalidation, and safe deferral/drop.

## PR 26 — Entry Points, Sharing, and Authentication Handoffs

Implement protected shortcuts, links, widgets, quick actions, incoming/outgoing
shares, and supported authentication handoffs. Define explicit boundary
crossings and safe return routing.

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

**Canonical Amendment refinement:** Design bridges so they do not foreclose
future synthetic-media input. Do **NOT** implement unrestricted synthetic camera
injection merely because media bridging exists.

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

Expand full coverage/user/developer diagnostics. Implement user-controlled
Developer Mode, warning, persistent enabled state, visible indicator, structured
detailed diagnostics, bounded persistent sessions, crash-session detection,
crash recovery, viewing, redaction, export, clear-log behavior, format
versioning, safe environment metadata, pseudonymous app refs, and separation from
Ledger. Scope reports appropriately. Unknown/Partial remain explicit.

**Canonical Amendment refinement:** Expand reporting for advertising-related
identity, battery/power, network-persona consistency, Java versus native sensor
coverage, known SDK/library paths, and external protection dependencies. Maintain
separation among coverage evidence, Privacy Access Ledger, and Developer Mode. No
raw protected/persona/host values.

## PR 34 — Health Infrastructure and Resource Budgets

Implement health metrics, Health UI, benchmark workloads, reference-device
budgets, runtime/network/storage status, health checks, safe temporary cleanup,
and Developer Mode storage status/overhead.

## PR 35 — Memory, CPU, and Battery Hardening

Investigate retained objects, lifecycle leaks, sampling, wake locks, supervisor
efficiency, standby, restart loops, idle overhead, and Developer Mode overhead.
Meet measured budgets without weakening protection.

## PR 36 — Low Storage, Backup/Transfer, and Deletion Hardening

Expand quotas, staging cleanup, snapshot cleanup, diagnostic bounds, disk-full
recovery, orphan detection, backup validation, transfer validation, and removal
semantics.

## PR 37 — Multiprocess, Revocation, and Recovery Stress

Stress concurrent policy changes, persona rotation, process death, broker
failure, supervisor failure, queued work, generation changes, and Developer Mode
crash persistence. Confirm no stale authorization, mixed-generation exposure, or
diagnostic privacy downgrade.

## PR 38 — Realistic Supported-App Compatibility Pass

Test representative real apps within validated scope. Classify
supported/unsupported/partial/compatibility failure. Make generalizable fixes
only.

**Canonical Amendment refinement:** Look explicitly for alternative paths,
including SDK/library-mediated behavior not exercised by earlier probes.
Compatibility fixes must never weaken privacy for a specific app.

## PR 39 — Accessibility, OEM, and Error-State Finalization

Harden TalkBack, large text, supported OEM behavior, onboarding, management
screens, notifications, blocked networking, unsupported apps,
storage/update/recovery errors, Developer Mode accessibility, and diagnostic
export UX.

## PR 40 — Integrated Security Regression Suite

Consolidate accumulated tests. Add cross-boundary adversarial cases, mutation
checks, failure checks, diagnostic redaction, Developer Mode isolation, crash
survival, and export-content tests.

**Canonical Amendment refinement:** Include regressions for advertising
identifiers, battery/power where supported, cross-surface persona coherence,
targeted package-probe isolation, network-persona consistency, diagnostic
redaction, and coverage-state correctness.

## PR 41 — Resource and Network Soak Suite

Exercise sustained launches, Ledger, Developer Mode, sensors, notifications,
package probes, VPN transitions, failures, updates, rotations, media, low
memory/storage, and long idle. Verify bounded growth and no privacy downgrade.

**Canonical Amendment refinement:** Where implemented, test long-running dynamic
synthetic battery, sensors, network, and diagnostic models for bounded/coherent
behavior.

## PR 42 — Independent Security Assessment and Remediation

Arrange a real independent security assessment. Record reviewed revision, scope,
evidence, findings, and remediation. Review management isolation, diagnostics,
redaction/export, and crash behavior. Codex must not fabricate external
assessment.

## PR 43 — Final Privacy, Security, Compatibility, and Diagnostic Documentation

Finalize the threat model, ADRs, supported matrix, persona semantics, policy
modes, VPN responsibilities, backup/deletion/update/rollback limitations,
remote-inference limits, Developer Mode, diagnostic privacy/export, and user
guidance.

**Canonical Amendment refinement:** Document advertising-ID scope, power/battery
coverage, descriptive Android identity versus actual runtime capability,
network-persona consistency limits, external VPN responsibilities, and synthetic
camera/media as future advanced work rather than a 1.0 guarantee.

## PR 44 — Release Engineering and Supply-Chain Validation

Finalize signing custody, reproducible-build practices where practical,
dependency inventory, release workflows, artifact naming, version/tag
consistency, secret protection, signature validation, and distribution
procedures. No release without explicit authorization.

## PR 45 — Final Installation, Upgrade, and Compatibility Regression

Verify clean installation, Privacy Decoy upgrades, persona/state migrations,
protected-package updates, rollback limits, self-update, Developer Mode
persistence, diagnostic migration, supported platform combinations, and
realistic protected-app behavior. Use release candidates.

## PR 46 — 1.0 Release Candidate and Final Readiness

Perform final integrated review of privacy, security, functionality, resource
behavior, networking, upgrades, diagnostics, Developer Mode, documentation,
artifacts, repository structure, and supply chain. Resolve release blockers,
record exact revisions/evidence, and produce a readiness recommendation.
Readiness does not itself authorize merge, tag, publication, or release.

**Canonical Amendment refinement:** Reject claims of coherent persona behavior
where supported surfaces materially contradict one another. Release readiness
remains evidence-based rather than feature-presence-based.
