# Canonical audit integration

**Status:** canonical repository integration; implementation paused

**Effective date:** 2026-09-22

This document integrates the authoritative Privacy Decoy Project Instructions
and the restored Canonical Audit Integration Amendment. The project owner supplied
the original canonical source on 2026-09-22. Existing evidence retains its scope;
this integration does not select a production architecture or turn prototype CI
into protection. The exact PR-specific amendment refinements are restored below.

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
Roadmap PR 1 through PR 46. Roadmap numbers remain separate from GitHub PR
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

The capability model must represent advertising-related identifiers,
power/battery surfaces, and other environmental categories without introducing
one-off policy systems. Coverage records must support those categories and
evidence states.

### PR 12

Preserve explicit per-execution package-universe control. Persona sharing **MUST
NOT** automatically grant mutual visibility. Targeted package probing remains
mandatory package-visibility analysis.

### PR 13

Explicitly investigate and, where feasible, implement advertising-related
identifiers, descriptive Android/build identity, battery/charging/power-state
surfaces, and cross-property device coherence. Do **NOT** advertise Android
platform versions/capabilities inconsistent with the actual protected runtime.

### PR 14

Include network transport/type information, local-network metadata, and
cross-checking network persona against regional persona where technically
meaningful. Do not infer public-IP protection from local values.

### PR 18

Add conservative network-persona consistency diagnostics where reliable evidence
exists. Any external exit-IP/geography lookup must be deliberate,
user-authorized, privacy-conscious/minimized, approximate evidence, and
nonessential to fail-closed enforcement. Unknown is preferable to guessed
geography.

### PR 21

Add explicit cross-API and temporal-coherence testing requirements.

### PR 22

Maintain preference for Empty where fictional data adds little benefit.

### PR 23

Synthetic camera/media injection is deferred advanced post-1.0 work. For 1.0,
establish physical paths safely as supported, denied, unavailable, or Unsupported
without requiring synthetic camera input.

### PR 29

Design bridges so they do not foreclose future synthetic-media input. Do **NOT**
implement unrestricted synthetic camera injection merely because media bridging
exists.

### PR 33

Expand reporting for advertising-related identity, battery/power,
network-persona consistency, Java versus native sensor coverage, known
SDK/library paths, and external protection dependencies. Maintain separation
among coverage evidence, Privacy Access Ledger, and Developer Mode. No raw
protected/persona/host values.

### PR 38

Look explicitly for alternative paths, including SDK/library-mediated behavior
not exercised by earlier probes. Compatibility fixes must never weaken privacy
for a specific app.

### PR 40

Include regressions for advertising identifiers, battery/power where supported,
cross-surface persona coherence, targeted package-probe isolation,
network-persona consistency, diagnostic redaction, and coverage-state
correctness.

### PR 41

Where implemented, test long-running dynamic synthetic battery, sensors,
network, and diagnostic models for bounded/coherent behavior.

### PR 43

Document advertising-ID scope, power/battery coverage, descriptive Android
identity versus actual runtime capability, network-persona consistency limits,
external VPN responsibilities, and synthetic camera/media as future advanced
work rather than a 1.0 guarantee.

### PR 46

Reject claims of coherent persona behavior where supported surfaces materially
contradict one another. Release readiness remains evidence-based rather than
feature-presence-based.

## M. Restored 1.0 acceptance refinements

Where supported:

- advertising-related ID behavior has defined scope and tested mediation;
- battery/power behavior is coherent and policy-safe;
- descriptive Android/device values do not imply unavailable runtime capabilities;
- network-persona warnings do not cause unnecessary third-party requests;
- package visibility stays isolated among apps sharing a persona;
- cross-surface persona coherence survives restart, process death, reboot, update,
  and rotation; and
- coverage distinguishes framework/native/external/unsupported/partial/unknown.

Synthetic camera/media input is **not** a 1.0 requirement.
