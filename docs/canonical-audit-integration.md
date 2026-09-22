# Canonical audit integration

**Status:** canonical repository integration; implementation paused

**Effective date:** 2026-09-22

This document is the normalized repository integration of the currently
authoritative Privacy Decoy Project Instructions and Canonical Audit Integration
Amendment obligations. It is **not** represented as a verbatim archival copy of
an unavailable external document. Where older working assumptions conflict or
are incomplete, these obligations control. Existing evidence retains its scope;
this integration does not select a production architecture or turn prototype CI
into protection.

The [requirements register](requirements.md) preserves PD-REQ-001 through
PD-REQ-070 and adds PD-REQ-071 through PD-REQ-084 for materially new obligations.
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
Developer Mode is separate diagnostic/support tooling. It must not weaken policy,
expose unnecessary raw host/persona/protected values, or upgrade missing evidence;
its output is bounded, redacted, and explicit about scope and
Unknown/Unsupported/Partial status.

Rotation is transactional and coherent, never silent regeneration. UX must state
the affected scopes. Rotation, reset, clearing local state, or creating a fresh
instance does not erase remote history, server correlation, sessions, or account
links. Reusable templates, if supported, are versioned configuration/identity
policy—not shared app storage or sessions—and preserve assignment and isolation.

## H. Privacy Decoy 1.0 media scope

Synthetic camera/media input is deferred until after 1.0 unless a later canonical
decision explicitly reprioritizes it. For 1.0, real physical camera/media paths
must still be explicitly mediated, denied, or reliably Unsupported. Mandatory
Unknown paths cannot expose physical input, and blocking a path is not a claim of
synthetic-media support. Existing camera and microphone policy obligations remain.

## I. Competitive-drift restrictions

Competitor features do not justify architectural drift. Privacy Decoy must not
become dependent on root or guest root, Magisk/Xposed/LSPosed/custom ROMs, routine
APK rewriting/re-signing, cosmetic cloning as a privacy substitute, a convenience
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

## K. Canonical roadmap and source gap

The canonical Privacy Decoy 1.0 roadmap contains 46 roadmap PRs. Roadmap numbers
are separate from GitHub PR numbers. Roadmap PR 5 and Roadmap PR 20 are mandatory
STOP/decision gates; Roadmap PR 20 has not occurred.

Repository history records the executed REDESIGN decision as **Roadmap PR 6**.
That historical label and decision remain valid and are not retroactively renamed.
It supplies relevant substantive feasibility/governance evidence, but the restored
canonical specification independently requires the earlier Roadmap PR 5 gate.

The exact authoritative 46-PR sequence and exact amendment refinements for
Roadmap PRs 6, 12, 13, 14, 18, 21, 22, 23, 29, 33, 38, 40, 41, 43, and 46 are not
present in repository evidence and cannot safely be reconstructed. They remain
canonical obligations, not permission to invent content. As detailed in the
[roadmap reconciliation](roadmap-reconciliation.md), later roadmap implementation
is blocked until the exact source is restored into the repository and reviewed.
