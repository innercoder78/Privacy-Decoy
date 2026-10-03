# Post-AG-1 independent defensive review synthesis

**Review date:** 2026-10-03

**Review attribution:** independent GPT-6.1 Sol High defensive architecture review
conducted after the five-candidate phase, as reported by the owner.

**Evidence class:** independent research/review artifact; architecture leads and
synthesis, subordinate to repository runtime evidence and independently verified
source evidence. This is not direct Privacy Decoy runtime security evidence or
the independent implementation security assessment required for production claims.

## Provenance and scope

This document preserves the conclusions supplied by Tony in the documentation
closeout request. The complete external review transcript, exact external source
pins, and reproductions were not supplied as repository evidence for this PR.
Accordingly, the statements below are **reported review conclusions and leads**,
not newly verified platform facts, fresh source audits, or experiments performed
by this closeout. Future use of any lead requires independently checking its
exact sources, scope, access context, provenance and applicability.

The controlling repository baseline is main
`6716b8f2ebcb722888e5afc2674601cdad05578f`, tree
`9b28e603c190b7cf0da99b71feba932242b32166`. The
[comparative synthesis](post-ag1-comparative-synthesis.md), its five candidate
records and the [owner handoff](post-ag1-owner-handoff.md) retain their original
evidence scopes. This synthesis adds no runtime result or approved dependency.

## Two different classifications

| Classification system | Recorded result | Question answered |
|---|---|---|
| Independent defensive review | **A. NO MATERIALLY NEW BOUNDARY** | Did the additional mechanisms justify reopening the old imported-app Protected-containment architecture? The review reported no. |
| ADR-0008 completed repository phase | **C. NO CREDIBLE BOUNDARY** | Did the five-candidate phase establish a credible permitted boundary or enforceable useful narrower class under the old mandatory contract? The synthesis found neither. |

The review's A/B/C labels are different from ADR-0008's A/B/C exit labels.
Review result A does not mean charter exit A, **CREDIBLE BOUNDARY FOUND**. Both
recorded results coexist; the review does not replace or rename the repository's
result C, change a candidate disposition, or turn Unknown into PASS.

The old contract required complete mandatory-path mediation or safe blocking/
fail-closed handling within the claimed scope of each app/version/artifact set
represented as Protected. It did not promise compatibility with every APK.
Experimental/unproven, Known unsafe, Incompatible and otherwise non-Protected
outcomes remained possible; mandatory Unknown blocked Protected execution and
known mandatory bypasses or incompatibility received no Experimental override.
The completed repository result concerns that contract for the intended useful
imported-app scope under the old goals, constraints and evidence.

## Reported leads and their limits

| Lead considered by the review | Reported significance and limits to preserve |
|---|---|
| Chromium Android isolated-process/sandbox patterns | Useful sandbox organization and defense-in-depth reference; not evidence of complete mandatory PD Persona/content mediation within an imported app's claimed Protected scope. |
| Current `isolated_app` restrictions | Relevant process/service/authority limits; access to genuine state and later executable content still needs separate analysis. No all-device support claim follows. |
| Modern Android/Linux seccomp evidence | Strengthened knowledge of additional lower-level hard-denial mechanisms; syscall restrictions are not DEX content identity/admission or complete Android virtualization. |
| TAWC / tawcroot | Lead for additional ordinary-Android seccomp-style deployment, not hostile-code containment evidence. Exact deployment context must be verified before reuse. |
| Current Landlock developments | Possible hardening lead; already-open FDs and retained capabilities matter. No ordinary-app Android availability or sufficient mediation is established here. |
| seccomp `USER_NOTIF` / `NEW_LISTENER` | Access, listener transfer, supervision and race questions remain. ADB-shell observations cannot be promoted to real production app-context evidence. |
| libgbinder / low-level Binder implementations | Possible trusted-side parsing/broker design references; a library supplies no mandatory authority over all Binder/capability paths. |
| Prison | Android-semantics/interposition reference, not containment. Exact source, license and provenance need review before any use. |
| WAMR / Wasmtime | A materially different controlled-execution class, not transparent arbitrary-APK compatibility. At most a separately approved optional non-APK module mode, not a replacement for the main existing-app product goal. |

These are preserved research leads, not an assertion that this PR independently
inspected the current upstream implementations. Existing independently reviewed
Android/source facts remain traceable through
[Candidate 1](post-ag1-candidate-1-os-process-compartment.md),
[Candidate 4](post-ag1-candidate-4-syscall-binder-boundary.md) and their exact source
ledgers; those records must not inherit stronger scope from this review summary.

## Why the old STOP recommendation survives

The review strengthened mechanism knowledge but supplied neither decisive missing
owner under the old contract:

1. Mandatory later executable-content admission inside retained ART.
2. Complete authority over genuine already-resident/cached Android framework
   and process state.

[AG-1C](ag1-runtime-executable-code.md)'s direct-loader failure remains a positive
observation in its tested scope, not Unknown. [S1](pr8-managed-profile-boundary.md)
and [PR4](pr4-containment-prototype.md) retain genuine-state exposure findings.
The review also reinforced incomplete Binder/FD/capability closure and the
external-VPN verification limitations preserved by
[PR5](pr5-network-feasibility.md). Already-held objects and copied/mapped values
cannot be assumed revoked by denying future acquisition.

The strongest unchanged-product hypothesis remained materially similar to
[Candidate 5](post-ag1-candidate-5-hybrid-architecture.md): isolated worker,
outside brokers and syscall restrictions, without complete mandatory authority.
It was not a materially new complete boundary. Therefore the review supported
retaining STOP for ADR-0008 / PD-REQ-001..095's old product contract. It did not
prove universal impossibility on Android and did not authorize another prototype.

## Phase II use and authority

[ADR-0009](../decisions/ADR-0009-retire-universal-containment-and-authorize-privacy-mediation-redesign.md)
records Tony's later acceptance of STOP under prior goals and his authorization
to redesign the product contract. The [Phase II handoff](../privacy-mediation-redesign-handoff.md)
maps these leads to possible mediation, compatibility and hardening research.
The review itself is not the authorization, an architecture selection, a
feasibility pass, or production Roadmap PR 6 approval. AG-1 remains FAILED;
all five candidate dispositions remain unchanged; production remains paused.
