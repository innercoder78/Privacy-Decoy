# Phase II acceptance criteria

These criteria govern forward claims. The [Phase I 1.0 criteria](acceptance-criteria-1.0.md)
remain historical and did not pass under the old contract.

## Research/development evidence

Under the explicit owner decision in
[ADR-0011](decisions/ADR-0011-adopt-emulator-first-phase-ii-development.md),
Android Studio's Android 17 / API 37 emulator is the primary development and
research environment for roadmap Stages 3–15. Emulator/debug evidence may
establish bounded feasibility observations, exercise deterministic
transformations and controlled fixtures, expose known failures, provide
adversarial/regression evidence, and support continued roadmap implementation
when the applicable research-stage gate is otherwise satisfied.

Such records must identify the tested environment, artifacts, build versions,
paths, results, and limitations. They do not establish production physical-device
coverage, release-equivalent evidence, multi-device/OEM evidence, or coverage of
untested hardware/OEM behavior. Unknown remains Unknown; known failures still
require STOP or evidence-backed narrowing. If meaningful evaluation requires
earlier physical evidence, the stage record must explicitly say so. Physical
testing before Stage 16 is encouraged when useful, but is not a general gate on
forward development.

## Production evidence

Production claims retain the capability and product/release gates below and
require applicable [Stage 16 physical qualification](phase-ii-roadmap.md).
Evidence must come from stock non-rooted physical Android 17 / API 37 ARM64
devices across multiple supported devices/OEMs, using release-equivalent
transformed and Manager builds. Record the exact Android/API, device, OEM, build,
ABI, source base/split artifacts, source `targetSdkVersion`, and transformation/
runtime context. Revalidate all applicable emulator-developed claims, including
relevant native, lifecycle, background, and process behavior. Compatibility
remains separate from privacy coverage.

Emulator/debug evidence alone cannot support a production privacy or release
claim. Missing required physical qualification cannot be labeled Fully mediated
for that production scope. Physical failures or material differences invalidate
affected evidence/coverage and require a fix, narrower supported scope,
Unknown/Unsupported as appropriate, or REDESIGN/STOP. Missing or failed required
Stage 16 evidence blocks Stage 17/18 production progression for the affected
scope. Independent security and supply-chain review remains mandatory at Stage
17 after physical qualification; Stage 18 remains the separate owner decision.

## Capability claim gate

A capability may be called **Fully mediated** only when a reviewable evidence
record contains all applicable items:

1. A scoped inventory of known framework, reflection, Binder/provider/service,
   native, file/property, SDK, WebView, dynamic-code, process, and lifecycle paths.
2. Hashes and provenance for the exact source base APK, every available installed
   split, and an explicit list of unavailable private or dynamic assets.
3. Exact transformer, pass-set, injected-runtime, policy-schema, and build versions.
4. Positive evidence that every claimed path returns the expected stable Persona
   or other safe synthetic/policy value.
5. Relevant negative and adversarial bypass probes, including genuine-value
   sentinels where safe and controlled.
6. Cold/warm start, process death, restart, cache, revocation, rotation, background,
   and multiprocess tests as applicable.
7. Evidence of no silent Real fallback throughout the tested scope.
8. Separate ABI- and path-scoped native evidence wherever native paths exist.
9. Separate evidence for bundled SDK, stock WebView, and dynamic-code paths where
   applicable. Java results cannot substitute for these.
10. Physical stock non-rooted Android 17/API 37 device evidence with exact device,
    OEM, build, ABI, and source target SDK recorded for a production claim.
11. Release-equivalent transformed and Manager builds. Emulator or debug evidence
    alone is research evidence and cannot support a production privacy claim.
12. Compatibility results recorded separately from privacy evidence.

Missing applicable evidence yields Partially mediated, Unsupported, or Unknown,
never Fully mediated. N/A requires evidence that the surface is not present.
External identifies a concern outside PD control and is not a mediated claim.
There is no overall privacy or safety score.

The coverage vocabulary remains **Fully mediated, Partially mediated,
Unsupported, External, N/A, and Unknown**. Research progress introduces no
provisional coverage status and cannot turn missing production evidence into
Fully mediated coverage.

## Product and release gate

Before any production claim, all applicable forward requirements must be traced
to evidence; artifact acquisition, rewrite validation, stable per-clone signing,
fresh-state/backup controls, permission removal, authenticated read-only policy,
and update invalidation must pass on the supported matrix. Secret and provenance
review, release-equivalent supply-chain review, and an independent Android/native
security review are mandatory. The manifest and dependency graph must establish
that PD neither uses nor depends on `VpnService`.

Known dangerous paths require the affected capability or app to be Unsupported
when no safe policy result can be enforced. Failure produces STOP or narrower
claims, not genuine-data fallback, weakened tests, or an expanded support claim.
The owner production/release decision is a separate final gate.
