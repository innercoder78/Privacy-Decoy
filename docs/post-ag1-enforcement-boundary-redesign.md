# Post-AG-1 enforcement-boundary redesign research charter

**Authorized:** 2026-09-29 by Tony's **REDESIGN** decision in
[ADR-0008](decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md).

**Scope:** bounded supplemental architecture research only. Production
implementation is paused; canonical Roadmap PR 6 remains unstarted and
unauthorized. The canonical 46-PR roadmap is unchanged. This charter defines
future research; it does not perform it, select an engine, or implement a prototype.

## Objective and first gate

Identify whether a materially different root-free Android architecture can
establish an actual mandatory enforcement boundary while retaining enough
Android semantics to support some defensible Privacy Decoy execution class.
Begin with architecture, authority, and bypass analysis before implementation.
Do not immediately write another runtime prototype.

> What permitted boundary actually owns execution authority strongly enough
> that adversarial guest code cannot bypass Privacy Decoy's decision merely by
> invoking another Java, Binder, native, filesystem, network, loader, or syscall
> path?

Every candidate must identify the authority it owns, the hostile code and handles
outside that authority, and where enforcement precedes guest execution. Mandatory
protection must be below, outside, or otherwise authoritatively around the
guest-controlled mechanism. A helper that guests may bypass, a larger hook list,
static scanning alone, or successful app launches is insufficient.

## Evidence baseline to carry forward

Use all prior Privacy Decoy evidence, not just the newest loader observation:

* [Requirements](requirements.md), [threat model](threat-model.md),
  [canonical amendment](canonical-audit-integration.md), and
  [acceptance criteria](acceptance-criteria-1.0.md) define unchanged obligations.
* [ADR-0002](decisions/ADR-0002-feasibility-stop-gate.md),
  [ADR-0003](decisions/ADR-0003-redesign-prototype-direction.md), the
  [redesign study](architecture-redesign-study.md), and
  [engine assessment](engine-assessment.md) preserve original failures and limits.
* [PR 4 containment](evidence/pr4-containment-prototype.md) preserves isolated
  process positives, missing Android semantics, and host-state gaps.
  [PR 5 networking](evidence/pr5-network-feasibility.md) preserves Known Gaps,
  independent packet methods, external-lockdown dependence, and route uncertainty.
* [S1](evidence/pr8-managed-profile-boundary.md) remains **FALSIFIED** and its
  follow-up **BLOCKED**. [S2](evidence/pr8a-engine-source-provenance-audit.md)
  audit-cleared no engine; [Blacks-BlackBox](evidence/architecture-discovery-blacks-blackbox-provenance.md)
  remains **STOPPED_UNRESOLVED**, and the later
  [VirtualSpace exact-pin review](evidence/architecture-discovery-virtualspace-static-falsification.md)
  remains **DISQUALIFIED** without erasing its earlier provenance result.
* [ADR-0006](decisions/ADR-0006-redesign-again-architecture-discovery.md) and its
  [STOP synthesis](evidence/architecture-discovery-synthesis.md) remain historical
  evidence against the families then reviewed.
* The [ten-project catalog](open-source-reference-catalog.md),
  [ADR-0007](decisions/ADR-0007-admission-gated-controlled-runtime.md), and its
  [technical handoff](architecture-admission-gated-runtime.md) retain their
  source findings and historical authorization. [AG-1A](evidence/ag1-admission-analysis.md)
  and [AG-1B](evidence/ag1-precode-bootstrap.md) retain bounded positives.
  [AG-1C](evidence/ag1-runtime-executable-code.md) retains both correct helper
  decisions and the positively demonstrated direct-loader bypass.
* [AG-1 closeout](evidence/ag1-feasibility-closeout.md) remains **FAILED**.
  The [canonical PR 5 evidence recommendation](evidence/canonical-pr5-feasibility-decision.md)
  remains **STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**;
  Tony's subsequent REDESIGN is a forward decision, not a feasibility pass.

Reuse supporting admission identity, analysis, split/package inventory, pre-code
authorization, isolated-process patterns, fail-closed policy, persona concepts,
broker authorization, external-VPN obligations, coverage classification, and
Experimental/Protected separation only within their evidenced scope. The
ADR-0007 runtime itself is not the forward architecture.

## Bounded hypothesis set and work sequence

Examine the five categories below, with one candidate record per category.
A record may reject the category or specify one concrete variant; the hybrid
record may combine mechanisms from the other four. This bounds the phase to
five records, one comparative synthesis, and one owner handoff. Record missing
evidence as Unknown rather than starting an indefinite chain of variants or
another competitor survey. Expansion requires a separate bounded authorization.

For each record: first map authority and prior falsifications, then inspect
relevant official Android/platform documentation and exact source, complete the
authority matrix and bypass path, and identify a falsifiable evidence plan.
Reject an unavailable/prohibited boundary at that point. A plan needing execution
does not authorize execution: runtime experiments, builds, dependency integration,
or another prototype require the separate review described by the exit criteria.
Do not patch the failed ADR-0007 helper as a substitute for this analysis.

### Research progress

Candidate 1 — OS/process-enforced compartment: **REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY**.
Evidence: [candidate record](evidence/post-ag1-candidate-1-os-process-compartment.md).
Candidate 2 — controlled Android semantics above a stronger lower boundary:
**UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED**.
Evidence: [candidate record](evidence/post-ag1-candidate-2-controlled-runtime-lower-boundary.md).
Candidate 3 — technically constrained execution classes:
**UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY**.
Evidence: [candidate record](evidence/post-ag1-candidate-3-constrained-execution-class.md).
Candidate 4 — ordinary-app-accessible syscall/Binder restrictions:
**UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED**.
Evidence: [candidate record](evidence/post-ag1-candidate-4-syscall-binder-boundary.md).
Candidate 5 — hybrid architecture:
**REJECTED — HYBRID DOES NOT CLOSE MANDATORY AUTHORITY**.
Evidence: [candidate record](evidence/post-ag1-candidate-5-hybrid-architecture.md).
All five candidate records are complete. The
[comparative synthesis](evidence/post-ag1-comparative-synthesis.md) is complete
and selects **C. NO CREDIBLE BOUNDARY** as the evidence-backed synthesis result:
a **STOP recommendation to Tony** under this bounded phase's current constraints
and evidence, not a universal impossibility proof or a new owner decision.
**Owner handoff prepared; owner response pending.** The bounded research phase
and synthesis are complete; the [owner handoff package](evidence/post-ag1-owner-handoff.md)
is complete. Tony has not yet supplied the new owner response. ADR-0008 continues
to record **REDESIGN** until a subsequent explicit owner decision is recorded.
No new phase, prototype, or implementation is authorized by this PR.
Production Roadmap PR 6 remains unstarted and unauthorized.

### 1. OS/process-enforced guest compartment plus brokered authority

Investigate whether ordinary non-rooted Android exposes a usable sandbox/process
configuration that can hold guest-controlled code, deny direct mandatory genuine
authority, and expose only explicitly mediated capabilities. Examine actual
documented/enforced isolated-process behavior, UID/process separation, SELinux
effects visible to ordinary apps, Binder reachability, filesystem namespace and
visibility controls available without privilege, executable-memory/code-loading
behavior, network authority, inherited descriptors, process creation, and platform
service access. Identify when each restriction takes effect and who can change it.

An isolated UID does not establish Persona or framework mediation. Compare with
the existing isolated-process and managed-profile observations explicitly; useful
OS separation cannot erase their failed or incomplete mandatory boundaries.

### 2. Controlled Android semantics above a stronger lower execution boundary

Investigate whether NewBlackbox/NEXTVM-style component/runtime semantics can sit
above a separate enforceable boundary. App-launch compatibility is not the
question. Identify what prevents direct class loaders, reflection, Binder, files,
`/proc`, `/sys`, properties, native code, direct syscalls, network operations,
and inherited handles from escaping mediation. If no lower boundary exists,
reject this hypothesis rather than patching it indefinitely with more hooks.

### 3. Technically constrained execution classes

Investigate narrower Protected classes, potentially Java-only or another bounded
class, with both a reliable pre-execution admission rule and a runtime boundary
that prevents excluded behavior. Static absence of native libraries or loader
references cannot establish membership. Explain whether hostile code can
manufacture the prohibited behavior after launch, including through reflection,
SDKs, generated/downloaded bytes, or platform libraries. If it can, the class is
not enforceable. Enumerate observable exclusions and block them before Protected
code. Discovery of a possible class is not approval to reduce product scope.

### 4. Ordinary-app-accessible syscall/Binder restrictions

Investigate whether an Android/Linux mechanism genuinely available to an ordinary
non-rooted application can establish useful deny-by-default syscall or Binder
boundaries for child/isolated processes. Seccomp-related or other process-sandbox
mechanisms are questions, not selected solutions. Verify application access,
kernel support, SELinux constraints, API/OEM portability, setup ordering,
supervision, thread/child inheritance, and bypass behavior. Upstream Linux
documentation alone cannot establish Android feasibility. If Android prevents
ordinary-app use of a needed feature, record that fact and its exact scope.

### 5. Hybrid architecture

A candidate may combine admission analysis, controlled Android semantics, OS
process isolation, capability brokers, virtual services, Persona mediation,
native observation, external-VPN verification, and fail-closed admission. For
every security-relevant layer identify its owned authority, dependencies, and
what remains reachable outside it. Analyze cross-layer handles and confused
deputies. A collection of layers catching different API subsets is not a security
boundary without an enforceable account of the remaining paths.

## Required AG-1C authority path for every candidate

Every candidate record must include a diagram or a step-by-step authority path
answering: **What happens when guest code directly constructs
`InMemoryDexClassLoader` with previously unadmitted bytes?** At minimum trace:

1. Admitted guest obtains or manufactures the previously unadmitted bytes.
2. Guest directly invokes the platform loader without the PD helper.
3. Loader construction, class resolution, static initialization, and entry
   invocation encounter the candidate's named enforcement mechanism(s).
4. Identify the earliest authoritative decision, its owner, and why guest code
   cannot bypass, disable, replace, or race it through another path.
5. Trace resulting capabilities, denial/failure, termination/revocation, and
   supervisor death; specify what independent evidence could falsify the claim.

To be credible, establish a concrete evidence path for at least one of:
the operation is technically impossible; the bytes cannot become executable;
the loader operation is authoritatively denied before execution; the guest lacks
authority to obtain/use the execution mechanism; or another enforceable lower
boundary prevents resulting code from escaping the admitted capability set.
The last case still requires an explicit PD-REQ-091 account of admission and
classification before use, opaque/unobservable introductions, and safe
termination/revocation; it is not a waiver for unadmitted execution.

After-the-fact detection is insufficient for Protected Mode if unadmitted code
already executed. Providing a helper or finding no static reference is
insufficient. Hooking the constructor is insufficient unless the hook cannot be
bypassed within the threat model and that claim has a credible evidence path.
No candidate may relabel AG-1C's positive bypass as merely Unknown.

## Native and Android-semantics obligations

The Java loader question must not hide the native problem. Any candidate allowing
app-controlled native code must explain containment of **JNI, direct syscalls,
native threads, `dlopen`, executable memory, filesystem, `/proc`, `/sys`,
properties, Binder, sockets, and inherited file descriptors**. Account for early
initializers, process creation, cached handles, and revocation. ByteHook and
ShadowHook may inform trusted-runtime interception; they are not kernel sandboxes.
If arbitrary app-controlled native code is excluded initially, reliably enforce
that exclusion before execution and prevent later manufacture of native behavior.

A toy DEX executor cannot silently replace Android applications. Every candidate
must account for **Activities, Services, Providers, BroadcastReceivers,
`Application`, resources, splits, multidex, lifecycle, jobs, alarms, secondary
processes, WebView, SDK initialization, storage, package visibility,
Binder/services, and background work**. For each, identify retained semantics,
owned authority, or a reliably detected and blocked exclusion before Protected
execution. Describe the useful execution class that remains. Compatibility and
enforcement require separate evidence; exclusions cannot hide mandatory exposure.

## Active ten-project reference mapping and reuse gate

Use the catalog's reviewed snapshots and dispositions, not a new random survey:

| Design/source role | Active references |
|---|---|
| Admission/APK/runtime inventory | Mirro + NEXTVM |
| Android runtime/component semantics | NewBlackbox + NEXTVM |
| Container/split/lifecycle management | Renjana + NEXTVM |
| Privacy/coverage inventory | XPrivacyLua |
| Persona/profile modeling | SpoofMyDevice |
| Binder mediation concepts | Binderceptor + NewBlackbox + NEXTVM |
| PLT/native imported-function interception | ByteHook |
| Inline/linker/native observation | ShadowHook |
| Negative architecture lessons | VirtualSpace + Mirro |

Reference influence is not security evidence. No project is selected wholesale,
and REDESIGN validates no previously disqualified exact pin. Catalog findings and
dispositions are unchanged. Explain exactly which idea informs each candidate
and which enforcement claim it cannot support.

Any later source incorporation must separately establish exact upstream source,
exact commit, license, provenance, transitive dependencies, bundled binaries,
native binaries, security relevance, TCB membership, and whether PD can maintain
and audit the component. A root license alone is insufficient. Apply the required
explicit integration decision; opaque security-critical binaries remain prohibited
from PD's TCB. This charter authorizes no source integration or dependency.

## Invariants applied to every candidate

PD-REQ-001..095 remain unchanged, with particular attention to PD-REQ-021
fail-closed Protected behavior, PD-REQ-090 known-unsafe hard stops, PD-REQ-091
runtime executable-code control, PD-REQ-093 evidence/classification honesty,
and PD-REQ-095 claim separation. No requirement is marked satisfied. Privacy
takes precedence over compatibility. Mandatory Unknown blocks Protected Mode;
a known mandatory bypass hard-stops execution with no Experimental override.

Remain Android-only, root-free, non-privileged and independent of production
ADB, Magisk/Xposed/LSPosed, custom ROM/patched kernel, guest root, or system
installation. Preserve artifact identity; no routine APK rewriting/re-signing
or convenience full guest Android is authorized. Management and peer state,
persona secrets, broker authority, and coverage decisions stay outside hostile
authority. Existing exception processes receive no new exception here.

No Privacy Decoy `VpnService`; **Require VPN for protected apps = ON** remains
the default. Preserve external-VPN fail-closed operation: unverifiable required
VPN or route state blocks execution/traffic, never physical-network fallback.
Inventory every traffic producer and its route authority, including native,
helper, background, and secondary processes; retain independent packet evidence
obligations and network Known Gaps. No duplicate tracker-blocking/VPN product.

Real means mediated genuine information, never uncontrolled host passthrough.
Location never falls back to host GPS. Permissions never automatically reveal
real personal data. Experimental status cannot silently waive these invariants.
Ordinary apps, private user data, and real accounts remain prohibited. Repository,
PR, log, diagnostic, and artifact surfaces remain public for secrecy purposes:
no credentials, signing material, private paths/data, or raw host/Persona values.

## Evidence standard and candidate deliverables

Classify each assertion separately as **verified Android/platform fact**,
**repository/source observation**, **upstream claim**, **inference**,
**design hypothesis**, or **Unknown**. Cite exact source/revision and scope;
an upstream claim does not become a verified fact by repetition. Preserve
contradictory observations. No numerical safety score; Unknown is never success.
Platform availability must be established on Android, not inferred from Linux
or a different device/API. A future evidence plan is not a completed observation.

For every candidate, produce an authority matrix using this schema. Fill every
row with the named mechanism, owner, guest bypass analysis, evidence available
(with category and scope), and remaining Unknowns. Empty cells are not passes;
use explicit Unknown where no mechanism or evidence exists. N/A requires a
justified, enforced exclusion. The placeholders below are a required template,
not findings from research performed by this charter.

| Boundary | Enforcing mechanism | Owner | Can guest bypass it, and how? | Available evidence/category/scope | Remaining Unknowns |
|---|---|---|---|---|---|
| Executable-code authority | To establish | To establish | To analyze | To cite | To enumerate |
| Framework/Java | To establish | To establish | To analyze | To cite | To enumerate |
| Binder/services/providers | To establish | To establish | To analyze | To cite | To enumerate |
| Native/JNI | To establish | To establish | To analyze | To cite | To enumerate |
| Direct syscalls | To establish | To establish | To analyze | To cite | To enumerate |
| Filesystem | To establish | To establish | To analyze | To cite | To enumerate |
| `/proc` | To establish | To establish | To analyze | To cite | To enumerate |
| `/sys` | To establish | To establish | To analyze | To cite | To enumerate |
| Properties | To establish | To establish | To analyze | To cite | To enumerate |
| Networking | To establish | To establish | To analyze | To cite | To enumerate |
| Storage | To establish | To establish | To analyze | To cite | To enumerate |
| Lifecycle/components | To establish | To establish | To analyze | To cite | To enumerate |
| Management isolation | To establish | To establish | To analyze | To cite | To enumerate |
| Dynamic code | To establish | To establish | To analyze | To cite | To enumerate |
| Third-party TCB | To establish | To establish | To analyze | To cite | To enumerate |

Each candidate record also needs: a trust/authority diagram or stepwise path;
the explicit AG-1C counterexample analysis; native and Android-semantics accounts;
pre-code setup and lifecycle/revocation ordering; a supported-configuration
hypothesis with API/OEM/ABI/access prerequisites and observable exclusions;
source/TCB inventory; requirement traceability; and decisive falsification
conditions. Specify what independent observation a later bounded prototype would
need. Debug/emulator findings do not establish physical non-rooted,
release-equivalent support or replace independent Android/native security review.

## Termination and exit outcomes

After the five candidate records, produce one comparative synthesis and return
one of the following evidence-backed outcomes to Tony. Do not start an unbounded
search when a category fails or an Unknown remains. Reject candidates with no
concrete permitted authority, unavoidable mandatory bypass, or prohibited
dependency; preserve their reasons in the synthesis.

### A. CREDIBLE BOUNDARY FOUND

At least one candidate identifies a concrete enforcement authority and a credible
route through every mandatory boundary, including the AG-1C counterexample,
native/direct-syscall paths, Android semantics, management isolation, and
external-VPN enforcement. State all unresolved proof obligations. This outcome
permits only a **separately reviewed bounded prototype** with fixed scope,
adversarial probes, pass/fail rules, and stop conditions. It does not authorize
production Roadmap PR 6 or claim existing Protected support.

### B. NARROWER ENFORCEABLE CLASS FOUND

A technically enforceable smaller app/execution class appears viable. List every
exclusion, its observable pre-execution admission rule, runtime prevention, and
fail-closed behavior. Static absence alone does not qualify. Any actual
product-scope reduction requires **Tony's explicit approval before requirements
or UX change**. This is neither implicit scope approval nor production PR 6
authorization; any prototype still requires separate bounded review.

### C. NO CREDIBLE BOUNDARY

No permitted root-free ordinary-device mechanism identified by this phase
provides the authority required for the mandatory model. Return a **STOP
recommendation** to Tony with evidence, limits, and remaining Unknowns. Do not
weaken requirements to escape this result or represent it as a universal proof
about every future Android architecture. The recommendation is distinct from
the owner's eventual response.

All three exits terminate this research phase. Production remains paused until
canonical feasibility governance and explicit owner authorization permit a
separate next step. Neither ADR-0008 nor this charter turns AG-1 into PASS or
waives its failed prerequisite for canonical PR 6.
