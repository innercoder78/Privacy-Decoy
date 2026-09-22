# Canonical audit integration

**Status:** canonical repository integration; implementation paused

**Effective date:** 2026-09-22

This document integrates the authoritative Privacy Decoy Project Instructions
and the restored Canonical Audit Integration Amendment. The project owner supplied
the original canonical source on 2026-09-22. Existing evidence retains its scope;
this integration does not select a production architecture or turn prototype CI
into protection. The PR-specific amendment refinements are faithfully transcribed
below.

The [requirements register](requirements.md) preserves PD-REQ-001 through
PD-REQ-070 and adds PD-REQ-071 through PD-REQ-085 for materially new obligations.
Ordinary protected applications, real accounts, and private user data remain
prohibited.

## A. Architecture invariants

Privacy Decoy is Android-only and must operate on supported non-rooted devices
without production ADB, root, Magisk, Xposed, LSPosed, a custom ROM or patched
kernel, privileged/system installation, or guest root. Privacy Decoy itself must
not implement `VpnService`. The preferred architecture is a controlled runtime
for imported package artifacts; routine rewriting/re-signing is disfavored and
requires the existing exception process. A full guest Android environment is not
a convenience substitute for the intended boundary. Privacy takes precedence
over compatibility, and no production architecture has been selected.

## B. Adversarial coverage

Protected code and everything it loads are hostile. Coverage must include
Java/framework calls; Binder, services, and providers; JNI, native code, libc and
direct syscalls; filesystem, `/proc`, `/sys`, and properties; Play Services and
other SDK/library paths; WebView/browser-mediated paths; dynamic code;
multiprocess, subprocess, helper, and background execution; early initialization;
stale handles; and lifecycle, death, restart, and revocation. A result on one path
is not evidence for another.

## C. Coverage vocabulary

Every relevant capability uses exactly these bounded states:

- **Fully mediated:** every claimed path is verified within the stated scope.
- **Partially mediated:** only explicitly listed paths are verified.
- **Unsupported:** the capability is intentionally and reliably blocked.
- **External:** outside Privacy Decoy's control, with the boundary stated.
- **N/A:** not present in the scoped configuration.
- **Unknown:** coverage has not been established.

Mandatory `Unknown` blocks execution before protected code. Unsupported mandatory
paths fail closed. Neither absence nor compatibility is evidence of mediation.

## D. Persona model

A persona is a coherent, persistent synthetic environment with controlled
**Real**, **Decoy**, **Empty**, and **Deny** policy modes. It covers, where
supported, device/build descriptors; advertising and ad-tech identifiers; locale
and location; SIM, carrier, and region; network and local-network metadata;
declared capabilities; sensors; battery/charging/power; package visibility; and
personal data. Values expected to be stable remain stable; dynamic values remain
plausible and mutually coherent. Identifier and sharing scopes are explicit.

Persona sharing does not imply shared protected storage, login/session state,
package visibility, or every identifier namespace. Real is controlled mediation,
not bypass. Permissions alone do not authorize host personal data. Device and
Custom Device remain the only selection modes; there is no inferred automatic
selection.

## E. Reported versus real Android semantics

Persona-reported Android version, build, device, manufacturer, model, product,
and similar descriptors describe a persona; they are not another Android runtime.
Actual API behavior and availability, API level, ABI, kernel/framework behavior,
hardware and platform features, OEM behavior, and engine/containment capabilities
remain facts of the real protected runtime. Descriptive values cannot support a
claim that an unavailable API, ABI, or capability exists. Coverage, evidence, and
UI must keep these concepts separate.

## F. Networking

“Require VPN for protected apps” defaults ON. A required external VPN or route
that cannot be verified causes networking to fail closed; physical-route fallback
is prohibited. VPN presence alone is insufficient. Independent evidence must
cover IPv4, IPv6, DNS, TCP, UDP, QUIC/Cronet, Java, native, background, subprocess,
and helper traffic, including VPN loss, replacement, reboot, and lockdown states.
Every traffic producer must be attributable.

Local persona spoofing does not change the external/public IP. An optional
exit-IP or geolocation lookup must be explicitly user-authorized, minimized to
required data, approximate when geography is displayed, nonessential to protected
execution, and must not silently contact a third party. Unavailable or
unverifiable geography remains Unknown rather than guessed. The VPN remains
external unless separately assessed; Privacy Decoy does not add a duplicate VPN
or tracker product.

## G. Storage, diagnostics, and rotation

Import accepts package artifacts only, not external runtime state. Protected
storage is isolated from host and peer protected apps. All mandatory protections
must be validated before any protected code, provider, or native initializer.

The Privacy Access Ledger is local, bounded, clearable, metadata-only, and
redacted; it is not proof of non-access and its failure never opens access.
Developer Mode is diagnostic/support tooling separate from the Privacy Access
Ledger. It MUST NOT emit, record, or retain raw protected/persona/host values.
It MUST use only bounded, redacted, allowlisted metadata/categories/status
evidence, explicit about scope and Unknown/Unsupported/Partial status. It MUST
NOT weaken policy or mediation or convert missing evidence into success.

Rotation is transactional and coherent, never silent regeneration. UX must state
the affected scopes. Rotation, reset, clearing local state, or creating a fresh
instance does not erase remote history, server correlation, sessions, or account
links. Reusable templates, if supported, are versioned configuration/identity
policy—not shared app storage or sessions—and preserve assignment and isolation.

Repository content, pull requests, Actions logs, retained/uploaded CI artifacts,
diagnostic evidence intended for project review, and other project publication
surfaces MUST be treated as public for secrecy purposes regardless of actual
GitHub visibility. They MUST NOT contain secrets, credentials, private user data,
raw protected/persona/host values, or production signing material (PD-REQ-085).
Controlled synthetic fixtures are allowed; private user data is never required
for research or CI evidence.

## H. Privacy Decoy 1.0 media scope

Synthetic camera/media input is deferred until after 1.0 unless a later canonical
decision explicitly reprioritizes it. For 1.0, real physical camera/media paths
must still be explicitly mediated, denied, or reliably Unsupported. Mandatory
Unknown paths cannot expose physical input, and blocking a path is not a claim of
synthetic-media support. Existing camera and microphone policy obligations remain.

## I. Competitive-drift restrictions

Competitor features do not justify architectural drift. Privacy Decoy must not
become dependent on root or guest root, Magisk/Xposed/LSPosed/custom ROMs, patched
kernels, routine APK rewriting/re-signing, cosmetic cloning as a privacy substitute, a convenience
full guest Android, or a duplicate built-in tracker/VPN. Any future exception
must pass the already-applicable ADR and explicit user-decision rules.

## J. Evidence and 1.0 acceptance

Evidence must establish both enforcement/privacy behavior and compatibility
within a precisely stated scope. Supported synthetic capabilities require, as
applicable, stability, scope, persistence, cross-API consistency, temporal
plausibility, coherent rotation, failure behavior, and adversarial bypass tests.
`Unknown` is never success. Debug/emulator success is not physical-device or
release evidence.

Privacy Decoy 1.0 claims require the [acceptance gate](acceptance-criteria-1.0.md),
including physical non-rooted devices, release-equivalent builds, independent
network and persistent-state observation, the claimed API/OEM/ABI matrix, and an
independent Android/native security review. Compatibility cannot hide an
unresolved mandatory protection.

## K. Restored canonical roadmap governance

The [authoritative restored roadmap](canonical-roadmap-1.0.md) contains canonical
Roadmap PR 1 through PR 46. The existing 46-PR roadmap remains intact and is
refined by this separate amendment. Roadmap numbers remain separate from GitHub PR
numbers. Roadmap PR 5 and Roadmap PR 20 are mandatory STOP/decision gates;
Roadmap PR 20 has not occurred.

Repository history records the executed REDESIGN decision as **Roadmap PR 6**.
That label is historical evidence and is not retroactively renamed. Canonical
production-domain PR 6 has not started. The source gap is resolved, but product
implementation remains paused for architecture/feasibility reasons. The
[roadmap reconciliation](roadmap-reconciliation.md) records the variance and the
separate status of open GitHub PR #10.

## L. Restored PR-specific amendment refinements

### PR 6

The capability model must be able to represent advertising-related identifiers,
power/battery surfaces, and other environmental categories without introducing
one-off policy systems.

Coverage records must support these categories and their evidence state.

### PR 12

Preserve explicit per-execution package-universe control.

Persona sharing must not automatically grant mutual visibility.

Targeted package probing remains part of mandatory package-visibility analysis.

### PR 13

Add explicit investigation and implementation, where feasible, for:

* Advertising-related identifiers.
* Descriptive Android/build identity.
* Battery/charging/power-state surfaces.
* Cross-property device coherence.

Do not advertise an Android platform version or capability inconsistent with what
the protected runtime actually provides.

### PR 14

Include:

* Network transport/type information.
* Local-network metadata.
* Cross-checking of network persona against regional persona where technically
  meaningful.

Do not infer public-IP protection from these local values.

### PR 18

Add conservative network-persona consistency diagnostics where reliable evidence
is available.

Any external exit-IP/geography lookup must be deliberate, privacy-conscious, and
nonessential to fail-closed enforcement.

Unknown remains preferable to guessed geography.

The stronger general amendment requirements in section F remain in force:
explicit user authorization, minimization, approximate geography, and no silent
third-party lookup.

### PR 21

No fundamental roadmap change is required. Sensor mediation already has the
necessary prominence.

Add explicit cross-API and temporal-coherence testing requirements.

### PR 22

No fundamental roadmap change is required.

Maintain preference for Empty where a fictional dataset provides little benefit.

### PR 23

Document synthetic camera/media injection as deferred advanced work.

For 1.0, establish safe supported, denied, or unsupported behavior without
requiring synthetic camera input.

### PR 29

Design media bridges so they do not foreclose a future synthetic-media input
system.

Do not implement unrestricted synthetic camera injection merely because media
bridging exists.

### PR 33

Expand reporting to include:

* Advertising-related identity.
* Battery/power surfaces.
* Network-persona consistency status.
* Java versus native sensor coverage.
* SDK/library access where known.
* External protection dependencies.

Maintain separation between coverage evidence, Privacy Access Ledger events, and
Developer Mode diagnostics.

The stronger canonical no-raw-value rule remains in force.

### PR 38

Use representative applications to look for alternative access paths, including
SDK/library-mediated behavior that earlier probes may not have exercised.

Compatibility fixes must remain generalizable and must not weaken privacy for a
particular application.

### PR 40

Include regression tests for:

* Advertising-identifier behavior.
* Battery/power-state mediation where supported.
* Cross-surface persona coherence.
* Package targeted-probe isolation.
* Network-persona consistency logic.
* Diagnostic redaction.
* Coverage-state correctness.

### PR 41

Test long-running dynamic synthetic state, where implemented, to ensure battery,
sensor, network, and diagnostic models remain bounded and coherent.

### PR 43

Document:

* Advertising-identifier scope.
* Power/battery coverage.
* Difference between descriptive Android identity and actual runtime capability.
* Network-persona consistency limitations.
* External VPN responsibilities.
* Synthetic camera/media input as a future advanced feature rather than a 1.0
  guarantee.

### PR 46

The final audit must reject claims of coherent persona behavior where supported
surfaces contradict one another materially.

Release readiness must still be based on actual evidence rather than feature
presence.

## M. Restored 1.0 acceptance refinements

In addition to existing 1.0 requirements, verify where those surfaces are
advertised as supported:

* Advertising-related identifier behavior has a defined scope and tested
  mediation path.
* Battery/power-state behavior is coherent and does not leak host state contrary
  to policy.
* Descriptive Android/device values do not falsely imply unsupported runtime
  capabilities.
* Network-persona warnings do not generate unnecessary third-party requests.
* Package visibility remains isolated even among applications sharing a persona.
* Cross-surface persona values remain coherent under restart, process death,
  reboot, update, and rotation.
* Coverage reporting accurately distinguishes framework coverage from native,
  external, unsupported, partial, and unknown paths.

Synthetic camera/media input is NOT a Privacy Decoy 1.0 release requirement under
the current roadmap.

## N. Architecture dependency sequence

The roadmap continues to build in this order:

Foundation and clean repository structure → threat model and engine feasibility →
containment and network feasibility → mandatory feasibility decision → domain and
persistence → package/artifact isolation → protected storage → supervised runtime
→ mediation infrastructure → policy and package visibility → coherent
identity/persona surfaces → location and regional/network persona → Ledger and
persona management → fail-closed networking → lifecycle integration → mandatory
integrated checkpoint → sensors and personal data → hardware policies → protected
background and entry mechanisms → browser/native/media boundaries → updates →
coverage and Developer Mode diagnostics → resource hardening → compatibility and
security regression → independent assessment → documentation and release
engineering → release candidate readiness.

No new competitive feature is permitted to bypass that dependency order.
