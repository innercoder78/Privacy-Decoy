# ADR-0002: Mandatory feasibility / STOP gate

- **Status:** Proposed — USER DECISION REQUIRED / ROADMAP STOP
- **Date:** 2026-09-21
- **Decision:** PENDING USER SELECTION

## Purpose and evidence basis

This is Roadmap PR 6, the mandatory user decision gate required by
[PD-REQ-063](../requirements.md). It converts the controlled evidence from
[PR 4](../evidence/pr4-containment-prototype.md) and
[PR 5](../evidence/pr5-network-feasibility.md) into a decision package; it does
not authorize implementation. GitHub PR #6 was a stacked correction merged into
PR #5, so the GitHub number of this roadmap stage differs from its roadmap number.

The containment evidence was produced by a debug API 35 x86_64 emulator run at
PR 4 tested commit `8386a4e86d588e5f50abf724798f9aca87f0cec3`
([Actions run 35487976187](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35487976187)).
The final PR #5 head after the stacked correction was
`29723d89052267f64b49af91613d88e0263c8610`; the merged
[PR #5](https://github.com/innercoder78/Privacy-Decoy/pull/5) records its
exact-head result. The evidence document's completed implementation run is
[Actions run 35564605873](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35564605873)
on `b87b2682ba6085bef2edab4e99aea74568fdb6d7`, which passed validation,
9/9 containment cases, and 13/13 network cases with independent packet analysis.
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

No row declares a requirement globally satisfied. “Preliminary evidence” and
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
| PD-REQ-063 | Roadmap STOP | This ADR presents the required user decision. Implementation cannot continue until the user selects and records a compliant path. |
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
- later product implementation remains blocked until the user selects and
  records a compliant path below.

## User decision — no option selected

There is no default or preselected option. **“GO unchanged into arbitrary-app
implementation” is NOT presently a requirements-compliant gate outcome**, because
mandatory guarantees still lack credible evidence.

### A. REDESIGN

Redesign the containment/runtime architecture before ordinary-app implementation,
then repeat the relevant feasibility gates.

### B. ADDITIONAL RESEARCH ONLY

Continue controlled research solely to close specifically identified mandatory
evidence gaps. Do not advance into product implementation until another explicit
gate is passed.

### C. NARROW SCOPE / MARK UNSUPPORTED

Explicitly remove unsupported classes of apps or capabilities from planned scope
and redesign requirements and UX around a smaller defensible boundary. This path
requires explicit user acceptance of the reduced scope.

### D. STOP

Stop Privacy Decoy development under the current product goals.

### Decision record

- **Decision:** PENDING USER SELECTION
- **Selected path:** _Not selected_
- **Decision date:** _Pending_
- **Decision rationale:** _Pending_
- **Roadmap consequences:** _Pending_

## Evidence required by a continuation path

REDESIGN or ADDITIONAL RESEARCH does not itself clear the gate. Before a later
gate can authorize relevant scope, controlled evidence must resolve or explicitly
exclude at least the following items. This list records gates; it does not
schedule or authorize experiments.

### Containment

- real Android component, `Application`, and provider lifecycle;
- native imported-code execution and mediation;
- early initialization and multiprocess behavior;
- broad Binder, service, and provider mediation;
- host `Build`, `Context`, Settings, and package-state leakage;
- native/syscall, `/proc`, `/sys`, property, and filesystem mediation or explicit denial;
- cached-capability and native-thread revocation;
- cross-app and storage isolation; and
- physical ARM64, OEM, non-rooted-device, and release-equivalent evidence.

### Networking

- reliable external-lockdown verification before protected networking;
- fail-closed behavior when lockdown cannot be verified;
- QUIC/Cronet, subprocess networking, and general resolver paths;
- background/helper traffic inventory and attribution;
- physical-device independent packet evidence; and
- release-equivalent build evidence.

NARROW SCOPE / MARK UNSUPPORTED must identify every excluded application class,
capability, path, platform, and lifecycle entry; block it before protected code;
and update requirements, coverage, and UX only through a separately approved
scope decision. It cannot relabel an Unknown mandatory path as safe.

## Consequences while pending

This ADR freezes product progression without changing runtime behavior,
dependencies, permissions, APIs, workflows, or the requirements register. The
research artifacts remain unsafe for ordinary apps, accounts, or private data.
No production containment engine is selected. A later edit may mark this ADR
accepted only after the user explicitly selects a path and completes the decision
record; implementation authority is limited by the selected path and all
remaining requirements.
