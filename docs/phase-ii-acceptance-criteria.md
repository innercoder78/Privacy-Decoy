# Phase II acceptance criteria

These criteria govern forward claims. The [Phase I 1.0 criteria](acceptance-criteria-1.0.md)
remain historical and did not pass under the old contract.

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
