# AG-1 Admission-Gated Controlled Runtime Feasibility Closeout

**Closeout date:** 2026-09-29

**AG-1 CHECKPOINT: FAILED UNDER CURRENT HYPOTHESIS**

AG-1 is the supplemental feasibility checkpoint authorized by
[ADR-0007](../decisions/ADR-0007-admission-gated-controlled-runtime.md), not
canonical production Roadmap PR 6. This documentation-only closeout evaluates
all nine [canonical AG-1 questions](../canonical-roadmap-1.0.md#ag-1--admission-gated-controlled-runtime-feasibility-checkpoint)
against repository evidence at main
`b1754bc02c2b9cbf54f29a2d3afa16bd2967628e`, tree
`0adb246bcb888a10f4f3ed7b51213ff3f85956ee`, the merge of GitHub PR #22.
It closes the checkpoint as failed without running another experiment or
rewriting the historical slice reports. Their IN PROGRESS statements describe
their publication stages, not the overall disposition after this closeout.

## Evidence progression and decisive result

[AG-1A](ag1-admission-analysis.md) established bounded pre-execution artifact
identity, inventory, and conservative classification. Its real analyzer supplied
no runtime proof; valid analyzed fixtures remained at most Experimental eligible.
Actual Android split completeness, signing rotation lineage, hostile archive
hardening, and opaque/dynamic behavior were not established.

[AG-1B](ag1-precode-bootstrap.md) bound exact analyzed bytes and admission
generation to session authority, execution class, and synthetic consent. Its
READY barrier preceded guest-byte transfer, class loading, and the controlled
provider/Application/entry sequence. Run #147 at
`180213a970d7382ca8c69908d18a5309d6a8ee2d` passed all eight AG-1B device cases;
that workflow separately failed on missing network observation evidence. Run
#150 later succeeded for both runtime and network jobs as recorded by AG-1C.
These are bounded bootstrap results, not normal Android lifecycle virtualization
or complete mandatory mediation.

[AG-1C](ag1-runtime-executable-code.md) preserved successful trusted-helper
authorization: exact expected secondary DEX completed all stages; changed,
unknown, stale, revoked, and replay attempts were denied without additional
secondary stages. The helper governed callers that explicitly used it; it was
not an Android loader interceptor.

The decisive [Actions run #150](https://github.com/innercoder78/Privacy-Decoy/actions/runs/36057346901)
used exact head `55ed5b7803608101f57f7c7a87eb9bd0a2a56634`, tree
`5fc2b17be1c23cedb2b58af8e500875426af3762`, on API 35 Google APIs x86_64 debug.
The admitted synthetic guest directly loaded separately built, previously
unadmitted secondary DEX without passing through `Ag1ExecutableAuthorization`:

```text
AG1C_STAGE DIRECT LOADER_CONSTRUCTED=1
AG1C_STAGE DIRECT CLASS_RESOLVED=1
AG1C_STAGE DIRECT STATIC_INITIALIZED=1
AG1C_STAGE DIRECT ENTRY_INVOKED=1
AG1C_CATEGORY DIRECT COMPLETE
AG1C_INTERPRETATION TESTED_DIRECT_PATH_NOT_MEDIATED
```

**AG-1C falsified the current runtime-mediation hypothesis for the tested direct
InMemoryDexClassLoader path.** Previously unadmitted DEX reached execution
before Privacy Decoy could enforce the required runtime executable-code decision.
This conflicts with AG-1 questions 4 and 5 and PD-REQ-091. A successful observation
workflow is not a successful security result. Correct helper behavior, READY
ordering, and artifact integrity do not mediate a route that bypasses the helper.
Normal teardown does not prove discovery-triggered safe termination or revocation.

## All nine canonical AG-1 questions

These are checkpoint evidence states, not new PD-REQ-020 capability classifications
or numerical safety scores. Each conclusion is limited to its cited scope.

| Question | Evidence state | Evidence and boundary of the conclusion |
|---|---|---|
| 1. Can import analysis classify complete base/split artifact sets before guest code runs? | **Demonstrated in bounded controlled scope** | [AG-1A](ag1-admission-analysis.md) inspected the supplied known base/artifact sets without executing guest code and derived immutable content identity. It checked bounded base/split structural agreement. Actual required split completeness and arbitrary opaque/dynamic package handling remain Unknown; the full general question is not established. |
| 2. Can it detect native libraries, executable splits, dynamic-code mechanisms, and opaque/packed cases sufficiently to report honest Protected, Experimental, Known unsafe, and Incompatible outcomes? | **Partially demonstrated** | AG-1A inventoried controlled native/executable/reference features and exercised the four-way policy model. Real analysis never established Protected eligibility or runtime proof. Static scanning cannot prove absence of dynamically introduced, packed, encrypted, generated, or opaque behavior. Missing detections are not Protected evidence. |
| 3. Can all mandatory Protected mediation be established before providers, Application, native initializers, or target code? | **Partially demonstrated** | [AG-1B](ag1-precode-bootstrap.md) demonstrated the READY barrier and ordered synthetic callbacks for its fixture. It did not establish complete framework/Binder/provider/native mediation, real Android component lifecycle virtualization, or protection before every arbitrary-app entry route. Full production mediation remains Unknown. |
| 4. Can unexpected dynamic executable content be blocked or demoted from Protected before execution? | **FALSIFIED — tested direct InMemoryDexClassLoader path** | [AG-1C](ag1-runtime-executable-code.md) observed previously unadmitted DEX through construction, resolution, static initialization, and entry invocation without a trusted executable authorization decision. This falsifies the proposed control for that path; no actual Protected session or Protected-eligible device fixture was established. Other loader paths remain Unknown. |
| 5. Can a controlled hostile Java/Kotlin fixture attempt unexpected DEX/native loading and be stopped? | **FALSIFIED — controlled Java fixture/direct-loader probe** | The unexpected secondary DEX was not stopped before entry invocation. The bounded trusted-helper denials do not cover direct loading. Native loading was not tested by AG-1C and remains Unknown; another native probe is unnecessary to establish the checkpoint failure. |
| 6. Can ByteHook/ShadowHook-style mechanisms be evaluated only as trusted-runtime interception without claiming syscall confinement? | **Unknown / Not reached after decisive checkpoint failure** | The [reference catalog](../open-source-reference-catalog.md) already distinguishes PLT/inline function interception from kernel or direct-syscall confinement. These remain reference/dependency candidates only, with no integration or native experiment. Native and direct-syscall containment remain Unknown. Questions 4/5 already determine failure; this closeout requires no further native experiment. |
| 7. Can Protected and Experimental execution remain technically and visibly distinct? | **Partially demonstrated** | AG-1A/B policy tests modeled separate execution classes, exact synthetic consent, and hard stops; hypothetical Protected cases existed only in the pure policy model. Real fixtures were not Protected eligible. Complete visible product/UI/runtime/coverage/Ledger distinction was not implemented or validated. |
| 8. Can every Protected failure remain fail-closed? | **Partially demonstrated; complete claim not established** | AG-1B rejected bounded stale, mismatched, revoked, missing-prerequisite, replay, and dead-session authority; AG-1C's helper denied bounded invalid requests. No real Protected-eligible fixture established complete mandatory coverage. The direct executable bypass prevents a claim that every Protected failure is fail-closed. General hostile-code termination/revocation remains Unknown. |
| 9. Can the design remain non-root, non-privileged, non-ADB in production, without a PD VpnService or routine rewriting/re-signing? | **Demonstrated in bounded controlled scope** | AG-1 preserved these constraints: no root/privileged mechanism, production ADB dependency, PD VpnService, or routine APK rewriting/re-signing was introduced. Development emulator/ADB tooling is separate. This is constraint compliance by the experiments, not physical-device/release-equivalent production deployability evidence, and it does not rescue the failed enforcement hypothesis. |

## Classification consequence

The exact tested direct loader path is positively demonstrated as unmediated.
The tested artifact/path cannot remain Experimental merely because its earlier
static result was Unknown/unproven. A positively known mandatory bypass requires
the PD-REQ-090 hard stop; PD-REQ-091 requires safe termination or revocation on
positive runtime discovery. This closeout records the obligation and failure,
not a newly implemented stop mechanism.

This does **not** classify every Android application as universally Known unsafe.
Other loaders, native paths, and opaque behavior remain Unknown until evidenced.
Conversely, absence of an explicit loader reference cannot prove an arbitrary
application cannot introduce executable code through reflection, generated code,
downloaded content, SDK behavior, or another path. The current architecture
lacks evidence for a defensible real Protected execution class. An Unknown
mandatory path still blocks Protected execution; a known bypass is a stronger,
positive finding and cannot receive an Experimental override.

## Requirements consequences

The [requirements register](../requirements.md) remains unchanged: no entry in
PD-REQ-001..095 is deleted, renumbered, weakened, or globally completed here.

| Requirement | Closeout consequence |
|---|---|
| PD-REQ-021 | Unknown mandatory coverage blocks before Protected code; bounded READY checks cannot satisfy unestablished mandatory coverage. Unsupported mandatory paths must fail closed. |
| PD-REQ-081 | Dynamic, SDK/library, WebView, and native paths need their own evidence; trusted-helper results cannot be inherited by the direct loader or other paths. |
| PD-REQ-083 | The 1.0 acceptance gate remains unsatisfied. No complete mandatory coverage, physical non-rooted release-equivalent matrix, or completed independent Android/native security review is established by AG-1. Experimental or green emulator results do not satisfy it. |
| PD-REQ-087 | No real Protected-eligible device fixture was established. An unmediated dynamic path and mandatory Unknowns cannot be presumed safe. |
| PD-REQ-088 | Experimental is available only for Unknown/unproven-only blockers, absent a positive mandatory bypass or incompatibility, with informed opt-in. Earlier static eligibility does not authorize continued execution after this discovery. |
| PD-REQ-090 | The positively demonstrated bypass requires a hard stop for the affected artifact/path, with no Experimental override; this is not a universal classification of all apps. |
| PD-REQ-091 | The tested direct path executed newly introduced, unadmitted DEX before the required decision. The requirement is not satisfied for that path; required discovery-triggered termination/revocation was not demonstrated by teardown. |
| PD-REQ-093 | Static references, their absence, compatibility, and helper success cannot prove full mediation. Opaque behavior remains Unknown and no numerical score substitutes for evidence. |
| PD-REQ-095 | Technical policy separation is bounded evidence only. Complete visible end-to-end distinction remains unestablished; Experimental compatibility cannot support Protected claims. |

Relaxing PD-REQ-091, treating the positively demonstrated direct path as merely
Experimental, or permitting execution after a positively known mandatory bypass
would contradict current requirements. The [threat model](../threat-model.md),
[canonical audit integration](../canonical-audit-integration.md), and
[1.0 acceptance criteria](../acceptance-criteria-1.0.md) retain their force.

## Closeout and handoff

No remediation, native containment experiment, hook library, third-party engine,
privileged mechanism, APK rewriting/re-signing, full guest Android, or PD
VpnService is selected or added. ByteHook/ShadowHook interception is not sufficient
containment evidence. Untested questions stay Unknown or not reached rather than
being filled in by speculation.

The [canonical Roadmap PR 5 decision package](canonical-pr5-feasibility-decision.md)
is the active governance gate and recommends **STOP UNDER CURRENT GOALS /
CURRENT ADMISSION-GATED RUNTIME HYPOTHESIS**. This is a repository recommendation,
not Tony's owner decision. His decision remains pending. AG-1's prerequisite
for canonical production PR 6 was not met; PR 6 remains unstarted and unauthorized.
