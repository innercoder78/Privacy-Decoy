# ADR-0002: Mandatory feasibility / STOP gate

- **Status:** Accepted — REDESIGN SELECTED / PRODUCT IMPLEMENTATION STILL GATED
- **Date:** 2026-09-21
- **Decision:** A — REDESIGN

## Purpose and evidence basis

This is Roadmap PR 6, the mandatory user decision gate required by
[PD-REQ-063](../requirements.md). It converts the controlled evidence from
[PR 4](../evidence/pr4-containment-prototype.md) and
[PR 5](../evidence/pr5-network-feasibility.md) into a decision package; it does
not authorize implementation. GitHub PR #6 was a stacked correction merged into
PR #5, so GitHub PR #7 corresponds to Roadmap PR 6.

The containment evidence was produced by a debug API 35 x86_64 emulator run at
PR 4 tested commit `8386a4e86d588e5f50abf724798f9aca87f0cec3`
([Actions run 35487976187](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35487976187)).
The final [PR #5](https://github.com/innercoder78/Privacy-Decoy/pull/5) head after
the stacked correction was `29723d89052267f64b49af91613d88e0263c8610`.
Its decisive final exact-head PR evidence is
[Actions run 35637885698](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35637885698):
`validate`: SUCCESS; `containment-prototype`: SUCCESS (9/9 cases);
`network-feasibility`: SUCCESS (13/13 cases); independent packet evidence passed;
and lockdown was verified platform state. Non-lockdown VPN-loss physical egress
remained a **Known Gap**.

For historical traceability, the evidence document's earlier implementation
[Actions run 35564605873](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35564605873)
on `b87b2682ba6085bef2edab4e99aea74568fdb6d7` passed validation,
9/9 containment cases, and 13/13 network cases with independent packet analysis.
That earlier commit and run are not the final PR #5 exact-head evidence.
These are scoped prototype observations, not production proof.

## Evidence reviewed

PR 4 established useful preliminary evidence for an unchanged, uninstalled,
single-DEX artifact entrypoint, isolated UID/PID boundaries, narrow authenticated
broker operations, persistent sentinel non-access, and bounded lifecycle tests.
It also established material limits:

- artifact-derived DEX execution is not normal Android application execution;
  `Application`, provider, and full component lifecycle are not implemented;
- native libraries packaged in the imported app cannot be loaded by the prototype;
- host `Build` information and a deliberately supplied host `Context` remain
  visible, while selected `/proc`, `/sys`, and property surfaces remain observable;
- Settings being denied or unavailable is not synthetic mediation, and broad
  Binder, provider, package, and host-service mediation is absent;
- broker revocation does not revoke arbitrary framework handles, cached
  capabilities, native threads, or syscalls;
- general hostile-app, early-initialization, multiprocess, cross-app, and storage
  coverage is not established; and
- physical ARM64, OEM, release-build evidence and a selected production
  containment engine are absent.

PR 5 substantially strengthened the scoped networking evidence: the final
exact-head run passed 9/9 containment and 13/13 networking cases; independent
packet observation covered Java/native IPv4, controlled DNS, and preliminary
IPv6; session/UID/PID/epoch/generation checks passed; real broker-owned sockets
were closed; reconnect and genuine provider replacement were exercised; and
Android always-on/lockdown behavior was verified in the fixture.

That evidence also reproduced physical egress after VPN loss when lockdown was
not enabled. Callback and snapshot detection cannot guarantee absence of physical
fallback. The tested no-physical-fallback result therefore depends on external
Android lockdown, whose reliable production verification remains unresolved.
Cronet/QUIC, subprocess networking, and general Android resolver behavior remain
Unknown / Not exercised. The emulator/debug observations are not physical-device
or release-equivalent evidence.

## Requirements-based gate

No row declares a technical requirement globally satisfied. PD-REQ-063 is
satisfied only in the governance sense: the user explicitly selected REDESIGN;
unchanged product implementation remains blocked. “Preliminary evidence” and
“Partial” describe only tested paths. “Known Gap,” “Unknown,” “Unsupported,” and
“Not yet evidenced for production” retain their ordinary limiting meanings; they
must not be read as **Fully mediated** under PD-REQ-020.

| Requirement | Gate classification | Evidence and unresolved obligation |
|---|---|---|
| PD-REQ-008 | Preliminary evidence | Unchanged single-DEX entry executed without installation; ordinary lifecycle, signatures, multidex, splits, and production import are not established. |
| PD-REQ-011 | Partial | Isolated Binder-observed UID/PID boundaries passed; hostile native/resource containment and arbitrary-app isolation remain unevidenced. |
| PD-REQ-013 | Known Gap | A narrow broker was tested; general Binder, service, and provider mediation is absent. |
| PD-REQ-014 | Known Gap | Harness JNI ran, but imported native loading is unsupported and selected proc/sys/property surfaces remain visible; syscall coverage is incomplete. |
| PD-REQ-015 | Partial | Narrow death, restart, subprocess, and revocation cases passed; full component lifecycle, early init, multiprocess, cached handles, and native threads remain open. |
| PD-REQ-019 | Not yet evidenced for production | Evidence is scoped and reproducible for the research harness; no arbitrary-app privacy claim has production evidence. |
| PD-REQ-020 | Unknown | The prototypes do not provide a complete capability inventory and production coverage classification. |
| PD-REQ-021 | Preliminary evidence | Research dispatch and network gates reject selected unknown/stale states; pre-code blocking for all mandatory production paths is unproved. |
| PD-REQ-027 | Partial | Narrow broker/session revocation and real socket closure passed; arbitrary framework handles, cached capabilities, native threads, and syscalls are not revoked. |
| PD-REQ-030 | Unsupported | The prototype has no explicit virtual package universe; fixed package observations exposed host state. |
| PD-REQ-031 | Known Gap | Settings/Context/service observations demonstrate incomplete mediation; broad host-service mediation is absent. |
| PD-REQ-032 | Partial | Tested management, isolated, broker, native, and fixed network paths were attributed; subprocess, background/helper, and general resolver producers remain Unknown. |
| PD-REQ-033 | Known Gap | Non-lockdown VPN loss produced physical egress. Tested no-fallback behavior depends on external lockdown whose reliable production verification is unresolved. |
| PD-REQ-034 | Partial | Independent evidence covers scoped TCP/UDP, Java/native IPv4, controlled DNS, and preliminary IPv6; QUIC/Cronet, subprocess, background/helper, and general resolver paths remain Unknown. |
| PD-REQ-041 | Preliminary evidence | Synthetic Java/native sentinel non-access and persistent bytes were checked; broad native filesystem, host shared-storage, and cross-app isolation are unproved. |
| PD-REQ-044 | Partial | Research gates run before selected payload dispatch; no full-app provider/native/early-init launch boundary exists. |
| PD-REQ-045 | Partial | Bounded state-machine, death, and selected revocation checks passed; hostile load, process exhaustion, and adversarial timing remain open. |
| PD-REQ-057 | Preliminary evidence | Controlled positive, negative, adversarial, lifecycle, and failure cases exist, but device, production, and hostile-workload coverage is incomplete. |
| PD-REQ-058 | Not yet evidenced for production | Packet and persistent-state observers were used, but evidence is debug/emulator-only; non-rooted physical release evidence is absent. |
| PD-REQ-060 | Partial | A project-owned research replacement interface exists; no production engine is selected and production SBOM, license, provenance, and vulnerability inventories do not exist. |
| PD-REQ-063 | Governance decision recorded — REDESIGN | The mandatory user decision occurred on 2026-09-21. Unchanged product implementation remains blocked; a redesigned architecture requires another explicit feasibility gate. Underlying containment/network requirements are not satisfied by this decision. |
| PD-REQ-070 | Preliminary evidence | Privacy Decoy adds no product VPN or tracker blocklist; the external fixture is test-only. This does not resolve route verification or fail-closed gaps. |

## Technical gate conclusion

**The current prototype architecture does NOT have sufficient evidence to
proceed unchanged into arbitrary-third-party-app production implementation.**
This conclusion follows from unresolved mandatory containment and networking
coverage. It is not a decision to abandon Privacy Decoy.

Accordingly:

- ordinary protected-app execution remains blocked;
- real accounts and private user data remain prohibited;
- no production privacy or security claims are authorized; and
- ordinary third-party-app product implementation remains blocked until a
  redesigned architecture passes another explicit feasibility gate.

## User decision — A. REDESIGN selected

The user explicitly selected **A — REDESIGN** on **2026-09-21**. Options B, C,
and D below are retained as considered alternatives and were not selected.

**“GO unchanged into arbitrary-app
implementation” is NOT presently a requirements-compliant gate outcome**, because
mandatory guarantees still lack credible evidence.

### A. REDESIGN — selected

Redesign the containment/runtime architecture before ordinary-app implementation,
then repeat the relevant feasibility gates.

### B. ADDITIONAL RESEARCH ONLY

Continue controlled research solely to close specifically identified mandatory
evidence gaps. Do not advance into product implementation until another explicit
gate is passed.

### C. NARROW SCOPE / MARK UNSUPPORTED

Explicitly remove unsupported classes of apps or capabilities from planned scope
and redesign requirements and UX around a smaller defensible boundary. This path
requires explicit user acceptance of the reduced scope. Any future scope decision
must identify every excluded application class, capability, path, platform, and
lifecycle entry; block it before protected code; and update requirements, coverage,
and UX only through separate approval. It cannot relabel an Unknown mandatory
path as safe. This path was not selected.

### D. STOP

Stop Privacy Decoy development under the current product goals.

### Decision record

- **Decision:** A — REDESIGN
- **Selected path:** A — REDESIGN
- **Decision date:** 2026-09-21
- **Decision rationale:** Preserve the original product goals and privacy guarantees; structural prototype gaps require architecture redesign, as detailed below.
- **Roadmap consequences:** Architecture redesign/research is next; product implementation remains gated, as detailed below.

## Decision rationale

The intended product remains a root-free Android privacy container/mediation
system for ordinary third-party apps. REDESIGN preserves the original goals,
existing privacy guarantees, and fail-closed principles instead of narrowing or
abandoning them to fit the current prototype. Privacy takes precedence over
compatibility.

PR #4 and PR #5 demonstrated useful mechanisms and valuable test infrastructure,
but exposed structural gaps in ordinary Android lifecycle execution, imported
native code, early initialization and multiprocess coverage, broad Binder/service/
provider mediation, host-state leakage, cached/native capability revocation, and
production-grade containment. Networking demonstrated an external Android
lockdown dependency for the tested no-physical-fallback goal and left QUIC/Cronet,
subprocess, and general resolver paths unevidenced. Continuing directly from the
current prototype to the product is therefore not justified.

Acceptance records the user's choice of direction. It does not approve a specific
redesign, establish that one is implemented or proven, or guarantee feasibility.
PD-REQ-001 through PD-REQ-070 retain their IDs and meanings; the technical Known
Gap, Partial, Unknown, and other evidence classifications above remain unchanged.

## Roadmap consequences

1. The PR #4 / PR #5 prototypes are **not the production architecture** and will
   not proceed unchanged as the product.
2. Useful components remain research/test assets and evidence baselines: hostile
   probes, fail-closed coverage concepts, Binder/session authority testing,
   lifecycle/death/revocation tests, network-generation tests, independent packet
   capture, the external-VPN fixture, and containment/network regression cases.
3. The next roadmap phase is containment/runtime architecture redesign and
   research. It must evaluate replacement architectures against this ADR's
   mandatory gaps; this PR records the direction and performs no implementation.
4. Ordinary protected apps remain blocked. Real accounts and private user data
   remain prohibited. No production privacy/security claims are authorized.
5. No replacement architecture or production containment engine is selected.
6. Any redesigned architecture must pass another explicit feasibility gate before
   ordinary third-party-app implementation is authorized. Selecting REDESIGN does
   not satisfy the underlying containment or networking requirements.
7. Privacy takes precedence over compatibility. Redesign must not silently fall
   back to real host data or uncontrolled physical networking to hide gaps.

## Next stage: architecture redesign and evidence work

The next stage must examine and compare candidate containment/runtime
architectures against the mandatory gaps below, using PR #4 and PR #5 as
adversarial test baselines. This section defines future evaluation obligations;
it does not implement or preselect an architecture or authorize ordinary-app use.

Candidates may include revised bespoke containment, suitable open-source
virtualization/container approaches, OS-managed/profile-based components where
useful, and other architectures consistent with existing project constraints.
Comparison must preserve the limitations in the
[engine assessment](../engine-assessment.md) and
[prototype-direction ADR](ADR-0001-engine-prototype-direction.md). A candidate
previously marked disqualifying must not be revived without new evidence.

### Containment/runtime evaluation

- real Android `Application`, component, and provider lifecycle;
- imported native-library execution and mediation;
- early initialization and multiprocess behavior;
- broad Binder/service/provider mediation and package-universe isolation;
- host `Build`, `Context`, Settings, and package-state leakage;
- native/syscall, `/proc`, `/sys`, property, and filesystem behavior and mediation
  or explicit denial;
- cached-capability and native-thread revocation;
- cross-app and storage isolation;
- artifact identity without routine APK rewriting or re-signing, preserving the
  existing requirements for any separately approved exception;
- root-free operation under existing platform constraints; and
- API/OEM/ARM64 viability, non-rooted physical-device and release-equivalent
  containment evidence.

### Networking evaluation

- retain the user-controlled external-VPN model; Privacy Decoy itself MUST NOT
  implement Android `VpnService`;
- reliable verification of required external lockdown and routing state before
  protected networking;
- fail-closed networking when required routing or lockdown cannot be verified;
- QUIC/Cronet, subprocess networking, and general resolver paths;
- resolver/background/helper traffic inventory and attribution; and
- physical-device independent packet evidence and release-equivalent validation.

### Governance and supply-chain evaluation

- explicit engine replacement boundary;
- source availability and immutable source provenance;
- license compatibility and provenance review;
- native binaries and dependency inventory;
- SBOM and vulnerability review; and
- explicit compatibility versus privacy tradeoffs without weakening requirements
  or silently reclassifying an Unknown mandatory path as safe.

Controlled evidence must support another explicit feasibility decision before
product progression. This accepted governance decision changes no runtime
behavior, dependencies, permissions, APIs, workflows, or requirements. Research
artifacts remain unsafe for ordinary apps, accounts, or private data.
