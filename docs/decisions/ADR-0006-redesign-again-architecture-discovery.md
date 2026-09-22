# ADR-0006: REDESIGN AGAIN architecture-discovery decision

- **Status:** Accepted — REDESIGN AGAIN selected / bounded architecture discovery authorized / product implementation still gated
- **Date:** 2026-09-22

## Context and evidence

[ADR-0002](ADR-0002-feasibility-stop-gate.md) rejected proceeding with the
original prototype architecture unchanged. [ADR-0003](ADR-0003-redesign-prototype-direction.md)
then selected bounded S1 and S2 research directions, not a production architecture.

S1 managed-profile isolation plus least-authority mediation was **FALSIFIED**:
mandatory genuine parent Build identity remained observable in both
managed-profile tenants. Its survival-gated networking/revocation follow-up is
therefore **BLOCKED**. S2 performed only static source/provenance screening.
VirtualSpace and Blacks-BlackBox each stopped unresolved at Gate 1, and neither
is AUDIT_PASS or DISQUALIFIED merely because evidence remains unresolved. S2 is
complete and produced no audit-cleared engine. These repository findings required
another explicit architecture decision.

On 2026-09-22, the project owner made that decision: **REDESIGN AGAIN**. The
choice is a project-owner decision informed by the existing evidence above; it
is not evidence that a viable architecture exists. The questions below remain
unresolved assumptions and are authorized for future research only within this
ADR's bounds.

## Decision

The project owner selected **REDESIGN AGAIN**, preserving the existing Privacy
Decoy goals and PD-REQ-001 through PD-REQ-085 unchanged. No requirement is
deleted, renumbered, weakened, or marked satisfied.

One bounded architecture-discovery phase is authorized to determine whether any
compliant root-free execution/mediation architecture exists at all. This is the
final open-ended architecture search under the present product goals. It must
terminate in a specifically justified bounded prototype, an explicit
scope-narrowing decision, or STOP under current goals; it must not become
indefinite competitor or engine exploration. “Final” imposes a governance exit,
not a deadline and not proof that an architecture exists.

The original prototype remains REDESIGN and must not proceed unchanged. No
production architecture is selected. Canonical production PR 6 has not started,
and canonical PR 20 has not occurred.

## Mandatory architecture questions

Every future architecture hypothesis must begin with a concrete explanation of
how it could satisfy all of the following boundaries.

1. **Pre-code execution boundary.** Mandatory protection must exist before
   providers, `Application`, native initializers, SDK initialization, secondary
   processes, dynamic code, and every other early entry path.
2. **Native and direct-syscall containment.** Hostile JNI/native code, libc,
   direct syscalls, native threads, `dlopen`, filesystem, `/proc`, `/sys`,
   properties, Binder, sockets, file descriptors, and other paths cannot silently
   obtain mandatory genuine host state.
3. **Normal Android application semantics.** The hypothesis must credibly handle
   ordinary Android lifecycle, components, resources, splits, multidex, signed
   package artifacts, native loading, jobs, alarms, services, providers,
   background execution, subprocesses, and multiprocess behavior. A simple DEX
   dispatcher is not equivalent to Android application execution.
4. **Management and tenant isolation.** Management authority and secrets must
   remain outside protected identities; protected apps and peers must not obtain
   management or peer state. Logical directory separation alone is insufficient.
5. **External-VPN fail-closed networking.** Privacy Decoy must not implement
   Android `VpnService`. Every traffic producer must be attributable; required
   external VPN, route, and lockdown state must be verifiable; unverifiable
   routing must fail closed; and no physical-network fallback is permitted.
   Eventual evidence must cover IPv4, IPv6, DNS, TCP, UDP, Java, native,
   QUIC/Cronet, subprocess, background and helper traffic, VPN loss/replacement/
   reboot, and stale connections.
6. **Deployability.** The design must work on ordinary supported non-rooted
   physical devices without privileged/system installation, production ADB,
   root or guest root, Magisk, Xposed, LSPosed, custom ROMs, or patched kernels.
   Its API/OEM/ABI strategy must be credible; emulator or debug success cannot
   establish production viability.
7. **Artifact identity and supply chain.** Routine APK rewriting/re-signing
   remains disfavored and receives no new exception. Any third-party trusted
   computing base must pass complete source, license, provenance, dependency,
   and native-binary review before execution is proposed. Opaque or unresolved
   TCB components cannot be treated as trusted.

Privacy takes precedence over compatibility, and mandatory Unknown coverage
remains fail-closed. Repository PRs, logs, evidence, and artifacts must remain
safe for public disclosure under PD-REQ-085.

## Candidate and direction admission rule

This phase is not another random competitor survey. Every architecture or
candidate must enter with a concrete hypothesis explaining how it could satisfy
the mandatory boundary above.

For a third-party engine or runtime, provenance, license, and source-completeness
admission precedes build or execution. An unresolved admission gate remains
Unknown and blocks runtime progression. README compatibility claims and
successful app launches are not security evidence. No third-party engine has
been audit-cleared.

VirtualSpace or Blacks-BlackBox may be reconsidered only with a concrete,
evidence-backed plan to resolve their existing Gate 1 provenance defects first.
This ADR does not reopen, build, execute, integrate, or vendor either candidate.
Managed-profile isolation must not be revived as sufficient by itself. Its useful
OS isolation properties may be reused only within a materially different
architecture that independently solves the falsified persona boundary.

## Exit outcomes

The phase must end with one of these evidence-backed outcomes:

1. **PROCEED TO BOUNDED PROTOTYPE.** At least one architecture has a concrete,
   credible route through the mandatory boundary and survives required static
   and admission checks. This authorizes only a separately reviewed controlled
   prototype/research step—not canonical production PR 6 or ordinary protected
   apps.
2. **NARROW SCOPE DECISION REQUIRED.** A credible architecture exists only with
   precisely enumerated exclusions, such as particular app classes,
   capabilities, API levels, OEMs, ABIs, or execution configurations. Every
   exclusion must be observable and reliably blocked before protected code. No
   requirement may be silently weakened. Any scope reduction requires another
   explicit project-owner decision before requirements, UX, or supported scope
   changes.
3. **STOP UNDER CURRENT GOALS.** No credible architecture remains that satisfies
   the mandatory boundary without violating core constraints, or every remaining
   architecture requires a prohibited mechanism or leaves mandatory host
   exposure unmediated. This must be reported honestly rather than weakening
   privacy requirements to continue development.

## Explicit non-authorization

This ADR does **not** authorize:

- canonical production PR 6 or product implementation;
- ordinary protected applications, private user data, or real personal accounts;
- continuation of the S1 networking/revocation follow-up;
- automatic integration or execution of S2 candidates, random engine searching,
  or additions of third-party binaries;
- requirement weakening;
- root, privileged/system-install, or guest-root paths, including production ADB,
  Magisk, Xposed, LSPosed, custom ROMs, or patched kernels;
- a Privacy Decoy `VpnService`;
- routine APK rewriting/re-signing; or
- protection, security, privacy, or release claims.

Product implementation remains paused. Ordinary protected apps, real accounts,
and private user data remain prohibited.

## Subsequent discovery status (2026-09-22)

This status note records later evidence without rewriting the facts known when
this ADR was accepted. The discovery phase examined both prior S2 candidates
further. Blacks-BlackBox remains **STOPPED_UNRESOLVED** after its
[provenance-closure attempt](../evidence/architecture-discovery-blacks-blackbox-provenance.md).
VirtualSpace's exact S2 pin,
`b1ff7988ac598b00b45c22003390ff43396c1c01`, received a bounded,
non-executing [static architecture falsification review](../evidence/architecture-discovery-virtualspace-static-falsification.md)
and is **DISQUALIFIED — exact pinned candidate**. Positive immutable source
evidence shows placeholder/no-op implementations in mandatory native and service
enforcement areas, a Binder path that restores original service objects, and an
unsafe genuine-host-context fallback. Satisfying the mandatory boundary would
require replacing or implementing core containment and mediation mechanisms,
not merely configuring or validating that pin.

VirtualSpace's earlier Gate 1 **STOPPED_UNRESOLVED** provenance result remains
valid historical evidence; this later architecture finding neither repairs nor
waives it. No production architecture is selected, no third-party engine is
audit-cleared, and no product implementation or canonical production PR 6 is
authorized. S1 remains **FALSIFIED** and its follow-up remains **BLOCKED**.
