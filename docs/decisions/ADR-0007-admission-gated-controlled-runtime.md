# ADR-0007: Admission-gated controlled runtime and Experimental & Unproven Compatibility Mode

**Status:** Accepted

**Date:** 2026-09-22

## Context

ADR-0006 authorized **REDESIGN AGAIN**. Its final synthesis recommended **STOP
UNDER CURRENT GOALS** for unrestricted arbitrary hostile-application execution
under the architecture families then evaluated. That recommendation was
historically correct and remains unchanged. Afterward, the project owner
reviewed additional concrete architecture and source evidence from ten projects
cataloged in [the reference catalog](../open-source-reference-catalog.md). The
review identified a narrower hypothesis not fully evaluated by that synthesis.

The owner does not accept STOP as the current project disposition. The owner
chooses **CONTINUE**, but only through a bounded admission-gated architecture
prototype and checkpoint. This is a new decision, not a correction of the old
evidence.

## Decision

Privacy Decoy will investigate a **PD-owned admission engine + PD-owned
controlled runtime + fail-closed Protected Mode + explicitly separate
Experimental & Unproven Compatibility Mode**.

* Mandatory import-time analysis determines a version-and-artifact-set admission
  result before guest code executes.
* Runtime executable-code observation and gating must preserve that admission
  boundary as far as technically enforceable.
* Protected Mode remains evidence-based and fail-closed. Unknown mandatory
  coverage never becomes Protected success.
* Experimental & Unproven Compatibility Mode may be offered only with explicit,
  informed, per-version/artifact-set consent when the blocker is Unknown or
  unproven coverage, no mandatory bypass is positively known, and the app is not
  fundamentally incompatible.
* Known-unsafe and incompatible results remain non-runnable and receive no
  Experimental option.

Privacy Decoy owns this architecture. No third-party engine is selected
wholesale. External work supplies inspiration, patterns, negative lessons,
coverage catalogs, or potential narrowly audited dependencies.

## Honesty boundaries

This decision does **not** claim that arbitrary hostile native code is contained.
ByteHook and ShadowHook do not create a kernel sandbox, and function
interposition does not necessarily intercept direct raw syscalls. Static scanning
can identify risk; it cannot prove an application safe or prove the absence of
dynamically introduced behavior. Compatibility is not evidence of mediation.

This decision is not authorization for NewBlackbox, NEXTVM, Renjana, Mirro,
XPrivacyLua/Xposed, LSPosed, root, routine APK rewriting/re-signing, a full guest
Android, or a Privacy Decoy `VpnService`.

## Authorization and consequences

The owner rejects the current STOP disposition and authorizes only the bounded
AG-1 admission-gated feasibility checkpoint described in the canonical roadmap.
Broad production implementation is not silently authorized.

* Canonical production Roadmap PR 6 remains unstarted. AG-1 success is a
  prerequisite, not authorization: its evidence must be reviewed through the
  still-mandatory canonical Roadmap PR 5 STOP/owner gate, and explicit
  project-owner approval is required before PR 6 may begin.
* Only controlled fixtures and test applications are authorized during AG-1.
  Ordinary private user data and real accounts remain prohibited.
* Historical evidence, including S1 **FALSIFIED**, its follow-up **BLOCKED**,
  Blacks-BlackBox **STOPPED_UNRESOLVED**, VirtualSpace **DISQUALIFIED — exact
  pinned candidate**, and the ADR-0006 STOP recommendation, remains unchanged.
* If the admission-gated hypothesis fails, STOP is preferred to weakening
  Protected Mode.

The forward technical handoff is
[Admission-gated controlled-runtime architecture](../architecture-admission-gated-runtime.md).
