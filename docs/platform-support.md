# Platform support

> **Active development suspended:** The Phase II baseline below is preserved
> under [ADR-0012](decisions/ADR-0012-suspend-active-development-and-establish-pdva-successor.md).
> It establishes no production support claim; resumption requires an explicit
> owner decision/ADR and fresh Android/platform verification. See [project status](project-status.md).

## Retained Phase II baseline

The initial host baseline is **Android 17, API level 37, on stock non-rooted
physical devices**. Privacy Decoy itself may target API 37. This is a selected
investigation and validation baseline, not a claim that mediation is implemented
or proven on every API 37 device.

Production coverage records must identify Android/API, build, OEM/device, ABI,
source application version and artifact hashes, source `targetSdkVersion`, PD
build, transformation/runtime version, and relevant WebView and Play Services
versions. Physical release-equivalent evidence is required for production claims.
Emulators and debug builds remain useful research tools but cannot establish a
production privacy claim alone.

A transformed app preserves its original `targetSdkVersion` by default. Changing
it can activate platform behavior changes, so every change requires a specific
compatibility reason, scoped evidence, and coverage reassessment. Source app
updates and new split sets also invalidate previous coverage.

Ordinary operation remains root-free and must not depend on ADB, guest root,
Magisk, Xposed, LSPosed, a custom ROM, patched kernel, or privileged/system
installation. Android sandboxing and permissions are defense in depth, not proof
of complete mediation. OEM, ABI, framework, native, SDK, Binder, WebView, and
dynamic-code differences stay Unknown until evidenced.

Privacy Decoy never uses Android `VpnService` and never occupies the system VPN
slot. An independent external VPN may run concurrently. Network geography is
External and is not part of the supported mediation matrix.

## Historical context

The prior provisional matrix and its Phase I enforcement-boundary questions are
preserved in Git history and the [Phase I threat model](threat-model.md),
[acceptance criteria](acceptance-criteria-1.0.md), and evidence. Those records did
not establish a supported production boundary. Phase II uses the
[forward threat model](phase-ii-threat-model.md) and [acceptance
criteria](phase-ii-acceptance-criteria.md).
