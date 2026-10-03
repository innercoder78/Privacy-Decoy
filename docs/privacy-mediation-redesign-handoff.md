# Privacy Decoy Phase II privacy-mediation redesign handoff

**Recorded:** 2026-10-03. **Status:** canonical bridge into architecture and
requirement redesign; no implementation authorized.

**Owner decision:** Tony — **ACCEPT STOP UNDER PRIOR GOALS AND CONSTRAINTS;
AUTHORIZE PRODUCT-CONTRACT REDESIGN**, recorded in
[ADR-0009](decisions/ADR-0009-retire-universal-containment-and-authorize-privacy-mediation-redesign.md).

**PHASE II STARTS WITH ARCHITECTURE AND REQUIREMENT REDESIGN, NOT IMPLEMENTATION.**

The next architecture/design Chat must read this document and ADR-0009 before
starting. This handoff closes the original architecture phase and describes the
owner's intended direction, unresolved decisions and required design outputs.
It selects no final architecture and authorizes no runtime prototype, dependency
integration or production work. Old canonical production Roadmap PR 6 remains
unstarted and unauthorized and must not resume automatically.

## Historical result and controlling records

The exact completed old-phase result is **C. NO CREDIBLE BOUNDARY** under the
original Privacy Decoy goals, constraints, intended useful imported-app scope
and available evidence represented by ADR-0008 and PD-REQ-001..095. The owner
accepts its STOP recommendation for that contract. This is not abandonment of
Privacy Decoy or a mathematical proof that
Android can never support the concept. Tony chooses **an explicit change to
project goals or constraints**, the path allowed by the original
[owner handoff](evidence/post-ag1-owner-handoff.md).

ADR-0009 supersedes ADR-0008 **only for future architecture direction**.
ADR-0008 remains a historical Accepted REDESIGN decision dated 2026-09-29.
ADR-0001 through ADR-0008, prior bounded positives, failed experiments, Unknowns,
source dispositions and dated decisions are preserved. The old
[enforcement-boundary charter](post-ag1-enforcement-boundary-redesign.md) is
CLOSED: five candidates, comparative synthesis and owner handoff complete;
no sixth candidate is authorized under that charter.

| Candidate | Exact preserved disposition |
|---|---|
| [1 — OS/process compartment](evidence/post-ag1-candidate-1-os-process-compartment.md) | REJECTED — NO PERMITTED MANDATORY AUTHORITY BOUNDARY |
| [2 — controlled Android semantics above a lower boundary](evidence/post-ag1-candidate-2-controlled-runtime-lower-boundary.md) | UNKNOWN — LOWER BOUNDARY NOT YET ESTABLISHED |
| [3 — constrained execution classes](evidence/post-ag1-candidate-3-constrained-execution-class.md) | UNKNOWN — CLASS ENFORCEMENT DEPENDS ON UNESTABLISHED LOWER AUTHORITY |
| [4 — syscall/Binder restrictions](evidence/post-ag1-candidate-4-syscall-binder-boundary.md) | UNKNOWN — PARTIAL LOWER-BOUNDARY MECHANISMS EXIST BUT SUFFICIENCY IS UNESTABLISHED |
| [5 — hybrid architecture](evidence/post-ag1-candidate-5-hybrid-architecture.md) | REJECTED — HYBRID DOES NOT CLOSE MANDATORY AUTHORITY |

The [comparative synthesis](evidence/post-ag1-comparative-synthesis.md) controls
the cross-candidate conclusion. [AG-1 closeout](evidence/ag1-feasibility-closeout.md)
remains **FAILED**; [AG-1C](evidence/ag1-runtime-executable-code.md) remains a known
direct executable-content admission failure in its tested scope. The
[canonical PR 5 package](evidence/canonical-pr5-feasibility-decision.md) preserves
the failed ADR-0007 hypothesis recommendation and Tony's subsequent historical
REDESIGN response. Roadmap numbers and GitHub PR numbers remain distinct.

The new decision baseline is main `6716b8f2ebcb722888e5afc2674601cdad05578f`,
tree `9b28e603c190b7cf0da99b71feba932242b32166`, following merged
[GitHub PR #31](https://github.com/innercoder78/Privacy-Decoy/pull/31).
The 2026-10-02 handoff's pending-response statements describe its publication
stage. They do not override the accepted response dated 2026-10-03.

## What changes, and the required contract migration

The original fail-closed Protected contract is retired as the future design
objective. It required complete mandatory privacy-sensitive path mediation or
safe blocking/fail-closed handling within the claimed scope of each imported
app/version/artifact set represented as Protected. Phase I did not promise
universal APK compatibility; PD-REQ-017 explicitly prohibited that claim.
Protected eligible, Experimental eligible, Known unsafe and Incompatible were
distinct possible outcomes. Mandatory Unknown blocked Protected execution;
known mandatory bypasses or incompatibility required a hard stop without an
Experimental override. Excluding apps did not relax the bar for Protected claims.
The completed research found no credible permitted boundary satisfying that
contract for the intended useful imported-app product scope. Phase II must
design an honest privacy-mediation contract instead of trying to make Candidate
5, more hooks or the old admission-gated architecture satisfy the original claim.
This does not rename incomplete old protection as successful new protection.

Routine artifact transformation/re-signing is now an authorized architectural
option to evaluate, explicitly changing the old architectural preference.
It is a leading direction, not a selected implementation. No blanket exception
is made for privilege, opaque security-critical binaries or unreviewed dependencies.
Android-only ordinary-device operation remains the design context; this decision
does not authorize production root, guest root, Magisk/Xposed/LSPosed, custom
ROM/patched kernel, privileged/system installation or production ADB dependence.

The following files remain unchanged as the canonical/historical Phase I contract
until an explicit Phase II migration:

* [Requirements PD-REQ-001..095](requirements.md).
* [Threat model](threat-model.md).
* [1.0 acceptance criteria](acceptance-criteria-1.0.md).
* [Platform support investigation](platform-support.md).
* [Canonical roadmap](canonical-roadmap-1.0.md).
* [Roadmap reconciliation](roadmap-reconciliation.md).

Future design must explicitly disposition **every** old requirement using
concepts such as **Retained unchanged**, **Retained but revised**, **Superseded
by explicit owner-approved redesign**, and **Retired by explicit owner decision**.
Each entry needs its old ID, rationale, replacement/retained obligation,
owner-decision trace and intended evidence/gate consequences. Preserve old IDs
and history. Reconcile the threat model, acceptance gates, supported scope and
roadmap explicitly; do not silently interpret old wording as the new contract.
The full 95-requirement migration has **not** been performed in this closeout.

Old Protected / Experimental / Known unsafe / Incompatible meanings remain
historical evidence. Changed future goals neither satisfy the old acceptance
gate nor waive AG-1's failure. Future production proposals require explicit
governance reconciliation and Tony's separate authorization.

## Existing-app and fresh-instance product target

Privacy Decoy remains an Android application virtualization/privacy-mediation
product. The user selects an application already on the device and chooses
something equivalent to **Add to Privacy Decoy**. PD prepares a separate instance
with fresh application state, as though newly installed, without silently
copying the original's private data, login state, databases, SharedPreferences,
cache or personal data. Fresh state and Persona rotation are distinct operations;
neither promises to erase remote history or account correlation.

Controlled APK/split modification, injected runtime mediation, package adaptation,
rebuilding and re-signing may participate in preparation. A possible progress
message is **Adding and modifying [APP NAME] to Privacy Decoy**. Possible stages
are analyzing package/base/splits, checking compatibility, preparing isolated/fresh
state, installing mediation machinery, applying structural transformations,
rebuilding, signing, installing/registering the instance and assigning a Persona.
These are target workflow concepts, not implemented stages or finalized wording.

Phase II must determine package/UID/component identity, split completeness,
signature and update behavior, permissions, backup/restore exclusion, storage
and management/peer isolation, lifecycle and recovery semantics. A separate
instance must be established by the selected design; a new display name or
package facade alone is insufficient evidence of fresh isolated state.

App Cloner is permitted as a **commercial product reference** for publicly
documented clone preparation, modification/re-signing, runtime setting updates,
compatibility limits and UX. Do not copy proprietary code, assert undocumented
internals, or claim PD is adopting its exact architecture. Its current behavior
must be researched from public documentation when Phase II uses it; this handoff
does not conduct that product research or make feature guarantees.

## Persona and runtime-versus-rebuild settings

Preserve Persona coherence and stability until deliberate user change/rotation,
with explicit scope and assignment. Where feasible and supported, the intended
coverage includes region/country, geographic location, language/locale, timezone,
Android ID, device/model/build descriptors, carrier/operator, SIM-country-style
metadata, MCC/MNC-like metadata, advertising identifiers, package visibility,
storage exposure, clipboard, sensors, network metadata and other surfaces from
the old requirements and coverage research. Descriptive Android/build values do
not change actual API behavior, ABI, hardware or runtime capabilities.

**Real / Decoy / Empty / Deny** remains a useful policy concept for redesign unless
a later ADR deliberately changes it. Real is explicit controlled disclosure;
permissions do not automatically grant real personal data. Persona sharing must
not silently imply shared application state, login sessions or all identifiers.

The preferred hypothesis is **generic PD mediation machinery in the prepared
app**, with Persona values owned by PD policy/configuration and supplied
dynamically where technically feasible. Do not assume values are permanently
baked into every modified APK.

| Setting class | Design objective and unresolved behavior |
|---|---|
| Runtime Persona/policy | Canada to United States, Toronto to Vancouver, carrier, locale, timezone, Persona Android ID, model/build and Real/Decoy/Empty/Deny changes should not automatically rebuild every app. Determine authenticated configuration access, scope and consistency. |
| Cached/session state | Restart, new session or generation may be required to avoid mixed values. Do not promise live mutation of values cached by Android, native libraries or app code. Define invalidation and stale-capability handling. |
| Structural preparation | Package/manifest structure, injected implementation, signing identity, transformation strategy, upstream app updates, newly required native/runtime instrumentation and some compatibility changes may require re-analysis/rebuild/reinstallation. Define data-preserving versus fresh-state outcomes explicitly. |

The runtime-versus-structural split is a required design question, not a completed
API or implementation. Management policy and secrets must not become writable
guest authority merely because prepared apps need permitted configuration values.

## Public-network Persona consistency

Where feasible, compare the Persona country with the application's apparent
**public-IP exit country**. For example, a Canada Persona and observed Japan exit
should support **Network location does not match Persona**, recommending a
VPN/proxy exit in Canada. A future flow may let the user configure/connect a VPN
or continue after warning; exact policy and wording require design.

Do **not** acquire the phone's real GPS merely for this comparison. Persona GPS
spoofing does not change public-IP geography. IP geolocation is approximate,
can be wrong or unavailable, and is not proof of physical location. Unknown
results remain Unknown. A lookup made by PD must not be assumed to represent
the app's route, all processes or every destination, especially with per-app
VPN exclusions and split routing. Design lookup consent, minimization, endpoint
disclosure, freshness and failure behavior explicitly.

The owner currently expects an external VPN matching the Persona country. This
ADR/handoff does **not** authorize a PD-owned Android `VpnService`. The prior
no-PD-VpnService rule is no longer eternally immutable, but changing it requires
an explicit future architecture/owner decision. Exact network architecture,
verification limits and revised route-failure policy remain unresolved. A
mismatch warning is not evidence of fail-closed routing or a silent amendment
to the historical external-VPN requirements.

## Observed real-data access and honest coverage

When PD has a known mediated path and can positively observe an application
attempting to obtain a genuine host value through another covered path, prefer
blocking the real value and notifying the user. A conceptual message is:

> Privacy Decoy blocked an attempt by [APP] to access real [DATA TYPE] through
> a non-Persona path.

Conceptual actions are **Close** and **Continue**. Continue must be an explicit
user decision about the affected Real capability/session/policy; it must never
silently disable all mediation. Phase II must define what Close closes, what
Continue grants, its lifetime, revocation and persistence. Do not claim an access
was blocked if evidence only establishes observation or a post-disclosure event.
Neither warning wording nor behavioral policy is finalized here.

PD must distinguish monitored/covered access, known blocked access, partially
mediated access, unsupported access and Unknown/unobserved paths. **Absence of
a detected event is not proof that no alternate path exists.** Never claim
**We detect every bypass**. A working app or empty event ledger proves neither
full coverage nor absence of real-data access.

Phase II needs a mediation-appropriate coverage and compatibility model. Possible
concepts include **mediated**, **mediated with known gaps**, **explicit Real/
compatibility exception**, **unsupported/incompatible**, and **not yet analyzed**.
These are design concepts, not approved UI labels or implemented states. Scope
claims to app version/artifacts, mediation runtime, API/OEM/ABI, policy and paths.
Preserve the old classification history; Unknown cannot become PASS through
renaming or a numerical safety score.

## Evidence lessons that must survive redesign

| Preserved finding | Required Phase II implication |
|---|---|
| [AG-1C](evidence/ag1-runtime-executable-code.md): previously unadmitted DEX executed through direct `InMemoryDexClassLoader` without mandatory PD helper admission; run #150, API 35 Google APIs x86_64 debug | Instrumentation/rewriting can provide useful mediation but cannot be described as complete control of later executable content without separate proof. The known failure is not demoted to Unknown; other paths retain their own scope. |
| [S1](evidence/pr8-managed-profile-boundary.md) / [PR4](evidence/pr4-containment-prototype.md): genuine Build/framework/process state remained accessible | Distinguish dynamically mediated values from genuine already-resident/cached state outside mediation. OS isolation alone does not synthesize Persona values. |
| [Candidate 4](evidence/post-ag1-candidate-4-syscall-binder-boundary.md): additional seccomp has meaningful lower hard-denial mechanisms in inspected Android configurations | Consider defense in depth with configuration-specific evidence; do not equate syscall restriction with DEX identity/admission or complete application virtualization. Source support is not a new PD device result. |
| [Candidate 5](evidence/post-ag1-candidate-5-hybrid-architecture.md): isolated worker + broker + syscall restriction did not close the old mandatory authority graph | Changed goals do not make that rejected Protected-contract design solved. Reuse limited mechanisms only with explicit new claims and limits. |
| Binder/FD/capability closure remained incomplete | A Binder object, FD, socket, mapped object, provider object, callback or token can carry genuine host authority. Prefer copied/synthetic values and scoped logical capabilities where feasible; document genuine durable capabilities as Real/coverage-relevant authority, including revocation limits. |
| [PR5](evidence/pr5-network-feasibility.md): VPN-loss physical egress and exclusion/split-route/allowBypass Known Gaps | Network identity is a separate coherence concern. Spoofed GPS does not alter public IP; external VPN presence is not all-producer routing proof. |
| Bounded static admission analysis | Static presence can establish risk; absence of a pattern does not prove behavioral absence, including generated/downloaded/opaque code. |
| Successful application behavior | Compatibility is not evidence that every privacy-sensitive path is mediated. Keep compatibility tests and privacy evidence distinct. |
| Artifact/version/split changes | App updates may invalidate coverage assumptions and require re-analysis/rebuild. Do not transfer confidence automatically to new bytes. |
| Diagnostics and publication discipline | Never log raw genuine host values, raw Persona secrets, private app data, credentials or sensitive content for debugging. Use bounded, redacted metadata; diagnostic silence is not enforcement proof. |

The bounded positives remain useful too: artifact inventories and identities,
generation/session models, controlled pre-code ordering, narrow broker checks,
independent packet/persistent-state observation, Persona modeling and explicit
evidence classification. Their original limits continue to control reuse.

## Independent 2026-10-03 defensive review

The [independent-review synthesis](evidence/post-ag1-independent-defensive-review-synthesis.md)
preserves Tony's supplied account of the GPT-6.1 Sol High review. It is research
and architecture synthesis, subordinate to runtime evidence and independently
verified source evidence; it is not direct PD security evidence or a completed
production security assessment. Its result **A. NO MATERIALLY NEW BOUNDARY** uses
different labels from ADR-0008 and coexists with **C. NO CREDIBLE BOUNDARY**.

Reported leads included Chromium isolated-process patterns, `isolated_app`,
modern seccomp, TAWC/tawcroot, Landlock and already-open FDs, `USER_NOTIF` /
`NEW_LISTENER` access contexts, libgbinder, Prison, and WAMR/Wasmtime. They did
not establish mandatory later executable-content admission inside retained ART
or complete authority over genuine resident/cached framework/process state.
Binder/FD closure and external-VPN verification also remained incomplete.
The strongest unchanged-product hypothesis was materially similar to Candidate
5, supporting retention of STOP. No independent source audit of those additional
leads is claimed by this handoff.

## Reinterpreting prior source research for Phase II

The [open-source reference catalog](open-source-reference-catalog.md) remains
the controlling record of the **exact 2026-09-22 reviewed pins, observed license/
provenance facts, limitations and dispositions**. It is unchanged. The table below
maps future research relevance only; every link points to that controlling entry.
It does not update upstream pins, resolve provenance, or reclassify a project as
an approved dependency.

| Reference and controlling snapshot | Phase II relevance; unchanged reuse limit |
|---|---|
| [XPrivacyLua](open-source-reference-catalog.md#xprivacylua), `85a1e498d5a9dbb902ca3d83e987ae6eec377d7a` | Privacy-sensitive API/coverage inventory. GPLv3; reference/coverage catalog only unless licensing strategy explicitly changes. Its Xposed/native limitations remain. |
| [SpoofMyDevice](open-source-reference-catalog.md#spoofmydevice), `ca78ffa6f18d44dc3d1e0fff2a48d74ad4884b19` | Persona/profile organization and deterministic stable identity. Especially relevant because the catalog already records APK patching/re-signing as one enforcement mechanism. Root MIT; selective source reuse candidate after audit, especially Persona modeling. Changed PD goals do not validate its enforcement. |
| [NewBlackbox](open-source-reference-catalog.md#newblackbox), `89b59836c66f173756a4ae258cf379a957649820` | Package/component/runtime semantics, storage redirection, service proxies and identity hooks. Top-level Apache-2.0, inherited VirtualApp/VirtualAPK and bundled Dobby/AAR/JAR provenance unresolved; reference only pending full provenance. |
| [NEXTVM](open-source-reference-catalog.md#nextvm), `f581a6642596db396a6fe606addd735979403b6b` | Package/runtime semantics, storage, service proxy organization, GMS/compatibility and identity research. Root Apache-2.0; selective reuse candidate after provenance/security audit, never wholesale adoption. Genuine fallbacks and host-account risks remain. |
| [Renjana](open-source-reference-catalog.md#renjana), `14302a57cd66114c6979acf7ca56c97f845a6841` | Container/split/lifecycle/instance management and diagnostics. Root Apache-2.0 with Pine/transitive licensing and provenance review outstanding; reference/selective non-enforcement reuse candidate after audit. |
| [Mirro](open-source-reference-catalog.md#mirro-android-virtualization), `74e6a1e3ea1898b2e2a705d7c8b3e730059023b1` | APK analyzer, split/native/executable inventory, compatibility taxonomy, loader diagnostics and negative architecture lessons. Apache-2.0 for Mirro-owned material, third parties separately licensed; selective reuse candidate after audit. |
| [Binderceptor](open-source-reference-catalog.md#binderceptor), `7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61` | Binder architecture reference only unless source completeness/provenance is independently resolved. MIT notice does not resolve the apparently source-incomplete prebuilt core. |
| [ByteHook](open-source-reference-catalog.md#bytehook), `a8bd254f6e53022b65136f40d10ae3763b6ef8ad` | Possible PLT/native mediation dependency candidate after full supply-chain/security review. MIT plus separate third-party attribution; not containment or raw-syscall coverage. |
| [ShadowHook](open-source-reference-catalog.md#shadowhook), `593f491be68799f03ee3bab71ce5908846437147` | Possible native/inline/linker observation or mediation dependency candidate after full review. MIT plus separate third-party attribution; not a kernel sandbox or universal native mediation. |
| [VirtualSpace](open-source-reference-catalog.md#virtualspace), `b1ff7988ac598b00b45c22003390ff43396c1c01` | Negative/historical evidence only. Exact pin remains DISQUALIFIED, with unresolved provenance history and observed no-op/fallback defects. Never resurrect it as a trusted runtime. |

Additional leads remain distinct from the pinned catalog:

| Lead | Permitted future research role and gate |
|---|---|
| Chromium / seccomp / Landlock / TAWC-style mechanisms | Possible hardening and defense-in-depth references, not proof of complete mediation. Verify exact Android app access, source, configuration and retained-capability limits. |
| Prison and newly discovered hook/virtualization engines | Possible Android-semantics/coverage references only after exact source/license/provenance review; discovery is not approval. |
| libgbinder and similar Binder implementations | Possible trusted-side parsing/broker references if needed; no mandatory authority by themselves. |
| WAMR / Wasmtime | Possible optional high-assurance non-APK module execution research, separate from the main existing-app goal and requiring its own owner approval; no high-assurance claim is established here. |
| App Cloner | Commercial product reference for public clone-preparation, rewriting/re-signing, runtime-settings, compatibility and UX research; no proprietary source access or copying. |

Before incorporation, identify exact source/commit, license and inherited
provenance, transitive dependencies, bundled/native binaries, security role,
TCB membership and maintenance/audit capability, then obtain the explicit
integration decision. A permissive root license is insufficient. No opaque
security-critical binary becomes trusted through this changed product goal.

## Required entire-repository triage before future cleanup

The Phase II architecture Chat **must inspect the ENTIRE current repository**
and classify relevant active files/subtrees as **KEEP / MODIFY / ARCHIVE / DELETE**.
Produce a reviewable inventory with path/subtree, purpose, classification,
rationale, retained consumers, evidence/reproduction implications and proposed
follow-up. Classification is required before removal, not an instruction to
delete first and assess later.

Explicitly cover Android research harnesses, AG-1 runtime fixtures, containment
prototypes, managed-profile fixtures, network feasibility fixtures, research-native
modules, old test apps, research scripts, architecture-specific implementation,
CI tasks tied only to dead Phase I research and obsolete product scaffolding.
Also inspect the remaining documentation, build/configuration and active product
tree so cross-subtree dependencies are accounted for.

| Classification | Decision expected |
|---|---|
| KEEP | Still useful as-is, including valuable regression fixtures for redesign risks; identify that use. |
| MODIFY | Useful with explicit adaptation or status/cross-link correction; identify affected dependencies. |
| ARCHIVE | Concrete reason for continued accessible retention, with location and purpose; not a default permanent dead-code directory. |
| DELETE | Obsolete implementation no longer useful after dependency/reproduction review; preserve it in Git history. |

Rules for the later cleanup:

1. Preserve Git history. Historical evidence documents and ADRs should generally
   remain in the repository, even when their implementation is removed.
2. Documentation may receive superseded/current-status notices and cross-links.
   Do not delete evidence merely because its research code becomes obsolete.
3. Remove no-longer-useful research code from the **active tree** eventually;
   do not carry every experiment indefinitely. Retain/adapt fixtures that still
   provide valuable regression evidence with an explicit purpose.
4. Before deletion, verify that retained documentation, CI, build configuration
   and future evidence reproduction do not depend on the active path. Resolve
   those dependencies first; historical reproduction may need an explicit
   preserved revision/path reference.
5. Do not move dead code into permanent `archive/` merely to avoid deciding.
   Require a concrete retention reason. Git history itself archives deleted
   implementation material.
6. Distinguish implementation from evidence: obsolete code can be deleted while
   the document explaining its result remains. The future active repository
   should represent the new architecture.

**No deletions, moves, CI changes or implementation triage dispositions are made
by this closeout.** The future design phase must first determine what is useful.

## Unresolved design decisions and next-phase deliverables

Phase II must produce a reviewable architecture/requirements proposal addressing:

* Every old requirement's disposition and explicit migration of governing
  documents, threat/coverage claims, acceptance gates and roadmap sequencing.
* The actual existing-app preparation/execution architecture and alternatives:
  transformation versus other mediation approaches, fresh-state isolation,
  package/UID/signing identity, complete splits, permissions, updates and recovery.
* Runtime Persona delivery versus structural rebuild settings, authentication,
  caching, session/generation consistency, rotation and management/peer isolation.
* Java/framework, Binder/provider, native/JNI/direct-syscall, SDK/WebView and
  later executable-content coverage, including honest limitations and lifecycle/
  early-initialization paths. Rewriting and injected code are not complete coverage
  by assumption.
* Network-exit observation for the actual app, external-VPN verification limits,
  mismatch warnings, consent and failures; any proposal for PD VpnService needs
  a separate explicit owner decision.
* Real-access warning evidence, block timing, Close/Continue scope, consent,
  revocation, coverage/compatibility reporting and bounded private diagnostics.
* Platform/API/OEM/ABI feasibility, Play Integrity and signature-bound apps,
  Google login/Play Services, clone/modification detection, signing custody and
  distribution constraints. None is promised compatible or undetectable.
* Exact-source/provenance and security review plans, reproducible positive and
  adversarial evidence, and separate compatibility evidence before any claims.
* The entire-repository KEEP/MODIFY/ARCHIVE/DELETE inventory and a deliberate
  dependency-safe cleanup proposal, preserving history and evidence.

These are design deliverables, not permission to begin a prototype while deciding
the architecture. End design with explicit unresolved risks, proposed bounded
validation and the owner decisions needed for later execution. This handoff
alone authorizes no production implementation, no ordinary-app/private-account
testing, no new Protected claim and no automatic continuation of Roadmap PR 6.
