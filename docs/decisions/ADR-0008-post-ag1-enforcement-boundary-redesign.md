# ADR-0008: Post-AG-1 enforcement-boundary redesign

**Status:** Accepted — REDESIGN selected / bounded enforcement-boundary research authorized / production implementation remains gated

**Date:** 2026-09-29

**Owner decision:** Tony — REDESIGN

**Supersedes:** [ADR-0007](ADR-0007-admission-gated-controlled-runtime.md) for forward architecture work only.

## Context and evidence sequence

The decision baseline is main `b289873284250881ac7379ede3356c1dc216f309`, tree
`2883d025a1db10fe3c97bfad197761ba8d6104ef`, after merged GitHub
[PR #23](https://github.com/innercoder78/Privacy-Decoy/pull/23),
**docs: close AG-1 and prepare canonical PR 5 decision**. Roadmap numbers are
distinct from GitHub PR numbers.

1. [ADR-0006](ADR-0006-redesign-again-architecture-discovery.md) authorized a
   broad bounded architecture-discovery phase following the original prototype
   REDESIGN and S1/S2 results.
2. That phase's [synthesis](../evidence/architecture-discovery-synthesis.md)
   recommended **STOP UNDER CURRENT GOALS** under the architecture families
   then reviewed. It remains valid historical evidence.
3. Subsequent review of [ten open-source projects](../open-source-reference-catalog.md)
   supplied materially new architecture/source evidence.
4. ADR-0007 used that evidence to authorize the narrower admission-gated
   controlled-runtime hypothesis and supplemental AG-1 checkpoint.
5. [AG-1A](../evidence/ag1-admission-analysis.md) established useful bounded
   artifact/admission analysis; [AG-1B](../evidence/ag1-precode-bootstrap.md)
   established useful bounded pre-code authorization and bootstrap ordering.
6. [AG-1C](../evidence/ag1-runtime-executable-code.md) preserved correct trusted-helper
   behavior, then positively demonstrated guest execution of previously
   unadmitted DEX through the tested direct `InMemoryDexClassLoader` path without
   asking that helper. [AG-1 overall remains FAILED](../evidence/ag1-feasibility-closeout.md).
7. The [canonical Roadmap PR 5 package](../evidence/canonical-pr5-feasibility-decision.md)
   therefore recommended **STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED
   RUNTIME HYPOTHESIS**. That is the correct evidence recommendation for the
   failed ADR-0007 hypothesis; this ADR does not claim it was wrong.
8. Tony has considered that evidence and explicitly selects **REDESIGN**.
   He accepts that ADR-0007 failed AG-1, does not authorize production
   continuation under it, and does not accept final project STOP at this point.

## Decision and authority boundary

> Do not proceed with ADR-0007 unchanged, but do not abandon Privacy Decoy.
> Search for a materially different enforcement boundary using the accumulated
> evidence and source research.

The next authorized activity is the supplemental
[post-AG-1 enforcement-boundary redesign](../post-ag1-enforcement-boundary-redesign.md)
research phase. It begins with architecture, authority, and bypass analysis;
it does not begin with another runtime prototype or a patch to the failed helper.
No replacement architecture has been found or selected by this decision.

The central question is:

> What permitted boundary actually owns execution authority strongly enough
> that adversarial guest code cannot bypass Privacy Decoy's decision merely by
> invoking another Java, Binder, native, filesystem, network, loader, or syscall
> path?

The decisive ADR-0007 failure was authority ownership. The trusted helper could
make correct authorization decisions, yet guest code did not have to ask it
before constructing an Android loader and executing new DEX. The next design
may not assume that **guest code will voluntarily traverse PD's authorization
helper**, or treat cooperative API mediation as the final execution authority.
Mandatory protection must sit below, outside, or otherwise authoritatively
around the guest-controlled mechanism. A new helper API, more hooks, static
scanning alone, or compatibility success cannot supply that boundary.

Useful supporting concepts remain available for selective reuse: immutable
artifact/admission identity, import-time analysis, split/package inventory,
pre-code authorization, isolated research-process patterns, fail-closed policy
models, persona-policy concepts, broker authorization, the external-VPN
requirement, coverage/evidence classification, and Experimental versus Protected
separation. Reuse inherits neither a feasibility pass nor broader security evidence.

## Requirements and historical integrity

[PD-REQ-001 through PD-REQ-095](../requirements.md) remain unchanged. No
requirement is deleted, renumbered, rewritten, weakened, or marked satisfied.
In particular:

| Requirement | Continuing obligation |
|---|---|
| PD-REQ-021 | Mandatory Unknown blocks Protected execution; mandatory unsupported paths fail closed. |
| PD-REQ-090 | A positively known mandatory bypass or incompatibility hard-stops execution; Experimental mode cannot override it. |
| PD-REQ-091 | Runtime executable-code control before use and safe termination/revocation on positive known-unsafe discovery remain mandatory. |
| PD-REQ-093 | Evidence and classification remain honest; static absence and compatibility are not mediation proof, and no numerical safety score substitutes for coverage. |
| PD-REQ-095 | Experimental and Protected claims remain visibly separate; Experimental compatibility supplies no Protected evidence. |

Privacy takes precedence over compatibility. The
[threat model](../threat-model.md), [canonical amendment](../canonical-audit-integration.md),
and [1.0 acceptance criteria](../acceptance-criteria-1.0.md) remain governing.
Android-only, root-free, non-privileged operation, artifact integrity, management
isolation, and all existing evidence obligations remain intact. No production
ADB, root/guest root, Magisk/Xposed/LSPosed, custom ROM/patched kernel,
privileged/system installation, routine APK rewriting/re-signing, convenience
full guest Android, or opaque security-critical binary TCB is authorized.

Privacy Decoy has no `VpnService`; **Require VPN for protected apps = ON** remains
the default. Required external VPN or route state that cannot be verified blocks
execution/traffic, with no physical-network fallback. No duplicate tracker/VPN
product is authorized. Real means mediated genuine information, never uncontrolled
host passthrough; location never falls back to host GPS; permissions never
automatically reveal real personal data. Experimental mode cannot waive these
invariants. Ordinary protected apps, private user data, and real accounts remain
prohibited; no current protection claim is made.

The original prototype REDESIGN, S1 **FALSIFIED**, S1 follow-up **BLOCKED**, S2
provenance results, VirtualSpace exact-pin **DISQUALIFIED**, Blacks-BlackBox
**STOPPED_UNRESOLVED**, ADR-0006 recommendation, ten-project findings, ADR-0007
owner authorization, AG-1A/B bounded positives, AG-1C helper positive and
direct-loader bypass, AG-1 **FAILED**, canonical PR 5 evidence recommendation,
network Known Gaps, and unresolved native/direct-syscall containment all remain
intact. REDESIGN changes forward governance, not those facts.

## Active references and source reuse

The [reference catalog](../open-source-reference-catalog.md) remains active with
its existing mapping: Mirro + NEXTVM for admission/APK/runtime inventory;
NewBlackbox + NEXTVM for Android runtime/component semantics; Renjana + NEXTVM
for container/split/lifecycle management; XPrivacyLua for privacy/coverage
inventory; SpoofMyDevice for persona/profile modeling; Binderceptor, NewBlackbox,
and NEXTVM for Binder mediation concepts; ByteHook for PLT/native imported-function
interception; ShadowHook for inline/linker/native observation; and VirtualSpace
and Mirro for negative architecture lessons.

Reference influence is not security evidence. No project is selected wholesale,
no catalog disposition changes, and no previously disqualified exact pin becomes
valid because REDESIGN was selected. ByteHook and ShadowHook are not kernel
sandboxes. This phase is not another random competitor survey.

Any later source incorporation separately requires exact upstream source and
commit, license, provenance, transitive dependencies, bundled and native binaries,
security relevance, TCB membership, and PD's ability to maintain and audit it,
followed by the required explicit integration decision. Opaque security-critical
binary components remain prohibited from PD's TCB.

## Governance consequences and exit

| Item | Forward status |
|---|---|
| Architecture | **REDESIGN**; ADR-0008 supersedes ADR-0007 for forward work. |
| Production implementation | **Paused**. |
| Canonical production PR 6 | **Unstarted and unauthorized**. |
| Requirements | **Unchanged**, PD-REQ-001..095. |
| Next authorized activity | **Bounded architecture/enforcement-boundary research only** under the charter. |
| Current canonical PR 5 owner response | **Resolved as REDESIGN**, without converting failed feasibility evidence into PASS. |

ADR-0007 remains an Accepted historical decision whose hypothesis was subsequently
falsified at AG-1; it is not erased. ADR-0006 and prior pending-owner statements
in dated evidence records remain historical. Current forward status is recorded
here and in the canonical PR 5 owner-decision section. The canonical 46-PR roadmap
is neither edited nor renumbered, and its mandatory decision gates remain intact.

The charter must close with **CREDIBLE BOUNDARY FOUND**, **NARROWER ENFORCEABLE
CLASS FOUND**, or **NO CREDIBLE BOUNDARY**. The first permits only a separately
reviewed bounded prototype; the second additionally requires Tony's explicit
approval before any actual product-scope, requirements, or UX reduction; the
third returns a STOP recommendation to Tony without weakening requirements.
None authorizes production PR 6. Any future production proposal must return
through canonical feasibility governance with adequate evidence and Tony's
explicit authorization; this decision neither waives the failed prerequisite
nor declares a replacement gate passed.
