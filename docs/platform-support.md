# Provisional platform and distribution investigation matrix

This matrix scopes prototypes; it is **not** a support promise. The build remains
`minSdk 31`, `targetSdk 37`, and `compileSdk 37`; PR 3 changes none of them.

| Dimension | Prototype target | Evidence rule / open decision |
|---|---|---|
| Device/ABI | ARM64 physical, non-rooted device is primary | Required for release evidence; native dependencies must expose reviewed arm64-v8a provenance and behavior. |
| Engineering convenience | x86_64 Android emulator | Useful for deterministic probes, never sufficient release/privacy evidence. |
| API range | Investigate API 31 through 37, prioritizing endpoints and current API 37 | The eventual range may narrow in a later implementation PR if fail-closed evidence is unavailable. |
| OEMs | AOSP/Google reference plus at least materially different Samsung and another OEM | Hidden APIs, services, background rules, backup/transfer, and kernel builds vary; each claimed OEM/API combination needs evidence. |
| Build type | Release-equivalent non-debuggable build, plus debug diagnostics | Debug-only success is not support. Production signing custody remains separately gated. |
| Packaging | Base APK and representative split APK/APKS, including native splits | Complete-set, signing-lineage, ABI and integrity validation required. |
| Distribution | Direct artifacts / published GitHub Releases are the initial investigation path | Authentic release metadata and safe updater are required. GitHub branches, commits, tags alone, drafts, and prereleases are not releases. |
| Google Play | Policy and technical feasibility remain open | Dynamic-code/plugin behavior, installed-app visibility, sensitive permissions and device/network behavior may constrain review/distribution; consult current official policy before any submission. |
| Networking | User-controlled external VPN; no Privacy Decoy `VpnService` | PR 5 must prove routing or block traffic; channel does not change this constraint. |

Candidate engines can change feasibility through native ABI payloads, hidden/private
API use, target-SDK behavior, bundled dependencies, licenses, dynamic-code design,
and store policy. None is production-selected. Work-profile tools are broadly
distributable but do not provide synthetic persona mediation; full VMs add large
artifacts/TCB and store/device constraints. See the [engine assessment](engine-assessment.md).

Primary policy references for later re-checking: [Google Play Developer Program
Policies](https://play.google.com/about/developer-content-policy/) and Android's
[non-SDK interface restrictions](https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces).
