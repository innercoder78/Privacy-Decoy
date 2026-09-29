# Canonical Roadmap PR 5 Feasibility Decision Package

**Prepared:** 2026-09-29

**Gate status:** evidence package complete; owner decision pending; production paused

This is the **canonical Roadmap PR 5** early feasibility evidence package, not
GitHub PR #5. It audits the mandatory boundaries in the
[canonical PR 5 acceptance criteria](../canonical-roadmap-1.0.md#pr-5-acceptance)
and [early decision stage](../canonical-roadmap-1.0.md#pr-5--early-feasibility-decision).
The required outputs are an evidence-backed recommendation, proposed supported
configuration, unresolved-risk register, and architecture decision. This package
supplies the first three and presents the architecture disposition for Tony's
decision; it does not fabricate the final owner decision or satisfy the gate by
its existence.

The evidence baseline is main `b1754bc02c2b9cbf54f29a2d3afa16bd2967628e`, tree
`0adb246bcb888a10f4f3ed7b51213ff3f85956ee`, after GitHub PR #22. This is a
documentation/governance closeout, not a runtime change or new experiment.
The [AG-1 closeout](ag1-feasibility-closeout.md) records
**AG-1 CHECKPOINT: FAILED UNDER CURRENT HYPOTHESIS**.

## Evidence and historical decisions retained

The [roadmap reconciliation](../roadmap-reconciliation.md) preserves numbering:
historical containment PR 4 and networking PR 5 substantially served canonical
research PRs 3 and 4. [ADR-0002](../decisions/ADR-0002-feasibility-stop-gate.md)
records the historical **Roadmap PR 6 A — REDESIGN** owner decision, not a
completed canonical PR 5 decision. Canonical production PR 6 has never begun.

The [engine assessment](../engine-assessment.md),
[redesign study](../architecture-redesign-study.md), and
[ADR-0003](../decisions/ADR-0003-redesign-prototype-direction.md) retain their
historical candidate and research dispositions. Subsequent evidence established:

* [S1 managed-profile boundary](pr8-managed-profile-boundary.md): **FALSIFIED**;
  both tenants exposed all seven mandatory parent Build fields. Useful OS
  UID/storage/lifecycle isolation did not establish Persona mediation. The S1
  networking/revocation follow-up remains **BLOCKED**.
* [S2 source/provenance audit](pr8a-engine-source-provenance-audit.md): both exact
  candidates **STOPPED_UNRESOLVED**, neither AUDIT_PASS. The later
  [Blacks-BlackBox provenance attempt](architecture-discovery-blacks-blackbox-provenance.md)
  remains **STOPPED_UNRESOLVED**, without asserting an uncorrectable defect.
* [VirtualSpace static falsification](architecture-discovery-virtualspace-static-falsification.md):
  exact pin `b1ff7988ac598b00b45c22003390ff43396c1c01` **DISQUALIFIED — exact
  pinned candidate** due to positive placeholder/no-op, genuine-Binder, and
  host-context fallback evidence. This later finding does not repair or erase
  its earlier provenance STOP.
* [ADR-0006](../decisions/ADR-0006-redesign-again-architecture-discovery.md)
  authorized REDESIGN AGAIN; its [architecture synthesis](architecture-discovery-synthesis.md)
  recommended **STOP UNDER CURRENT GOALS** across the reviewed families. That
  was a research recommendation, not an owner acceptance or universal impossibility
  proof. No reviewed family supplied all required containment, Android semantics,
  management isolation, and routing boundaries under the mandatory constraints.
* [ADR-0007](../decisions/ADR-0007-admission-gated-controlled-runtime.md)
  subsequently records Tony's **CONTINUE** authorization for bounded AG-1 only.
  [AG-1A](ag1-admission-analysis.md), [AG-1B](ag1-precode-bootstrap.md), and
  [AG-1C](ag1-runtime-executable-code.md) supplied new bounded evidence; AG-1C
  decisively falsified the tested direct-loader control. The earlier authorization
  is preserved exactly and is not rewritten as a prior STOP choice.

## Canonical PR 5 acceptance matrix

The acceptance rule requires a credible enforceable architecture boundary under
mandatory constraints. These dispositions describe evidence, not new capability
coverage states or globally satisfied requirements. Positive bounded results
remain positive; they cannot cancel a mandatory failure.

| Criterion | Evidence | Limitations | Current disposition |
|---|---|---|---|
| Protected-code containment | [Containment prototype](pr4-containment-prototype.md) executed an unchanged uninstalled single-DEX fixture in an isolated UID/PID, with narrow pre-dispatch gates. AG-1B established READY ordering. [AG-1C run #150](ag1-runtime-executable-code.md#first-exact-head-ag-1c-device-result) then observed direct unadmitted DEX construction, resolution, initialization, and entry invocation without helper authorization. | Earlier prototype exposed Build/host-Context and selected platform state and lacked full application semantics. Synthetic ordering is not complete Binder/provider/native mediation. No real Protected-eligible fixture was established. | **Not satisfied; current executable-code hypothesis falsified for the tested direct path.** Protected execution cannot be claimed. |
| Management isolation | Containment tests observed distinct Binder identities, rejected cross-session capability use, and corroborated Java/native sentinel non-access with unchanged persistent manager bytes. AG-1B bound fresh isolated sessions to exact artifact/consent authority. | Synthetic sentinel and narrow broker checks do not establish protection of every management database, credential, peer state, resource, or hostile native path. Guest self-reports are not independent broad hostile-code proof. | **Partially demonstrated in bounded research; complete mandatory isolation Unknown.** |
| Native bypass risk | Historical JNI/direct-open probes found selected proc/sys/property access and denied-or-unavailable sentinel access. The [reference catalog](../open-source-reference-catalog.md) explicitly limits ByteHook/ShadowHook to function interception. | Imported arbitrary native containment, direct syscalls, early native initialization, cached descriptors, and hostile threads remain unresolved. AG-1C ran no native containment experiment. Hooks are not kernel sandboxes. | **Unknown / not reached in AG-1 after decisive failure.** Existing adverse observations remain; another native experiment is not required to justify STOP. |
| Broker authorization | Containment/network tests enforced Binder-observed UID/PID, session, epoch, allowlisted operation, generation, revocation, and death. AG-1B tested exact admission/consent binding. AG-1C's helper authorized exact secondary content once and rejected bounded changed/unknown/stale/revoked/replay requests. | These checks cover cooperating calls to narrow trusted endpoints, not every platform handle or executable route. Revocation checks do not prove termination of arbitrary running code. The direct loader never requested the helper's decision. | **Demonstrated in bounded controlled scope; insufficient as the complete boundary.** |
| Network-route feasibility | [Network evidence](pr5-network-feasibility.md) and ADR-0002 preserve independent packet observations for Java/native IPv4, controlled DNS, preliminary IPv6, broker-owned socket closure, reconnect, and provider replacement. Final historical PR #5 run `35637885698` at `29723d89052267f64b49af91613d88e0263c8610` passed 13/13 network cases. | Non-lockdown VPN-loss physical egress, per-app exclusion, split routing, and explicit allowBypass remain Known Gaps. QUIC/Cronet, subprocess/background/helper attribution, and general resolver behavior remain Unknown. Callback/snapshot detection is not atomic route enforcement. | **Partially demonstrated; production no-fallback feasibility not established.** |
| External VPN interaction | Separate non-forwarding external VPN fixture observed controlled full-tunnel/include traffic and verified always-on/lockdown state; confirmed loss with lockdown produced no fixed physical egress in the scoped experiment. Privacy Decoy itself adds no VpnService. | No-fallback behavior depends on external Android lockdown in tested scope. Reliable product verification, provider trust, destination coverage, and physical-device/release-equivalent behavior remain unresolved. VPN presence alone is not proof. | **External dependency demonstrated in bounded scope; production gate unresolved.** Experimental mode cannot waive it. |
| Ordinary non-rooted Android operation | Project-owned controlled experiments preserved non-root/non-privileged operation, artifact identity, and absence of production ADB dependence, PD VpnService, or routine rewriting/re-signing. | Debug API 35 Google APIs x86_64 emulator evidence with development tooling is not ordinary non-rooted physical-device release-equivalent evidence or a supported API/OEM/ABI matrix. Earlier S1 development provisioning is not a product deployment result. | **Constraint compliance in research; production deployability Unknown.** |

## Admission-gated runtime consequence

AG-1 narrowed the research hypothesis; it did not relax the
[requirements](../requirements.md), [threat model](../threat-model.md),
[canonical audit integration](../canonical-audit-integration.md), or
[1.0 acceptance gate](../acceptance-criteria-1.0.md). The
[nine-question closeout and requirement trace](ag1-feasibility-closeout.md#requirements-consequences)
explicitly address PD-REQ-021, PD-REQ-081, PD-REQ-083, PD-REQ-087, PD-REQ-088,
PD-REQ-090, PD-REQ-091, PD-REQ-093, and PD-REQ-095.

The direct-loader observation establishes a mandatory bypass for the exact
tested artifact/path. PD-REQ-090 requires a hard stop; its earlier static
Experimental classification cannot authorize continued execution. PD-REQ-091
was not satisfied on that path, and normal test teardown is not the required
discovery-triggered safe termination/revocation evidence. All Android apps are
not thereby universally Known unsafe. Untested loader/native/opaque paths remain
Unknown, and mandatory Unknown still blocks Protected execution. Absence of a
loader reference cannot establish safety against reflection, generated or
downloaded code, SDK behavior, or another executable path.

The current architecture lacks evidence for a defensible real Protected class.
Relaxing executable control, relabeling the known bypass as merely Experimental,
or counting compatibility/green observation CI as protection would contradict
the current requirements. No PD-REQ-001..095 entry is changed or marked complete.

## Proposed supported configuration and unresolved-risk register

**No production Protected configuration is proposed as supported.** The existing
evidence scope is controlled synthetic research on the recorded debug
emulator configurations. It establishes neither real-app eligibility nor a
shippable Experimental product. Ordinary protected applications, private user
data, and real accounts remain prohibited. Retaining the research assets does
not authorize rerunning or extending them in this closeout.

| Unresolved risk or established failure | Evidence state | Gate consequence |
|---|---|---|
| Secondary executable bypasses trusted decision | **Falsified** for tested direct InMemoryDexClassLoader path | Current AG-1 hypothesis fails; affected artifact/path requires hard stop, not Experimental override. |
| Other loaders, opaque/generated/downloaded content, static false negatives, complete splits/signing lineage | **Unknown** beyond bounded analysis | Static absence cannot establish Protected eligibility; no arbitrary-app admission claim. |
| Complete early framework/Binder/provider/native and application/component semantics | **Unknown / historical Known Gaps** | READY for a synthetic fixture is insufficient to admit ordinary apps. |
| Native/direct-syscall and cross-tenant/management containment; hostile live-code revocation | **Unknown / historical partial and adverse evidence** | No hook-library inference or native continuation required after the decisive failure. |
| Routing races, exclusion/split/bypass, complete traffic attribution and external-lockdown verification | **Known Gaps / Unknown production enforcement** | Bounded network success cannot override containment failure or authorize physical fallback. |
| Exact-source/provenance and security of any external TCB | **No engine audit-cleared; reference candidates only** | No third-party enforcement mechanism selected; prior STOP/disqualification findings remain. |
| Visible Protected/Experimental distinction, physical/release matrix, independent Android/native review | **Not established** | Policy model results cannot satisfy PD-REQ-095 or PD-REQ-083 product acceptance. |

## Architecture disposition for owner review

Repository evidence identifies no concrete, already-supported enforcement
boundary that resolves the demonstrated executable-code bypass while preserving
all mandatory constraints. AG-1A/B and the helper show useful supporting
mechanisms, not that missing boundary. Prior architecture findings and network
Known Gaps independently remain relevant.

Speculative hooks, stronger scanning, compatibility success, a possible future
engine, or further investigation do not support PROCEED or REDESIGN on this
record. ByteHook/ShadowHook are function-interception candidates, not syscall
confinement. No ByteHook, ShadowHook, Pine, Xposed, LSPosed, root/Magisk,
privileged/system installation, custom ROM/patched kernel, guest root,
APK rewriting/re-signing, full guest Android, PD VpnService, or new virtualization
engine is added, selected, or authorized here. No remediation or native
experiment is attempted. STOP follows the current evidence and constraints;
it is not a universal impossibility claim about every future architecture.

**Repository recommendation: STOP UNDER CURRENT GOALS / CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**

## OWNER DECISION REQUIRED

Tony has not made the closeout owner decision. The recommendation above is not
owner acceptance, an Accepted STOP ADR, or authorization to implement anything.
The canonical Roadmap PR 5 gate remains pending his explicit disposition; its
architecture decision must record what he actually chooses.

No production Roadmap PR 6 work may begin unless the canonical governance
conditions are satisfied and Tony explicitly authorizes it. AG-1 did not succeed,
so its necessary prerequisite for PR 6 is unmet; publication or approval of this
evidence package cannot imply otherwise. Production remains paused. Stop here:
no follow-on architecture experiment, native containment work, or production PR 6
is authorized by this package.
