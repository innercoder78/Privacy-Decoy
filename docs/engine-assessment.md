# Containment and virtualization engine assessment

**Assessment date:** 2026-09-19

**Outcome:** no production engine is selected. PR 4 should prototype a narrow,
replaceable containment boundary and use third-party projects as research
comparators only.

## Method and evidence labels

This assessment uses canonical repositories, their current README and LICENSE
files, immutable commit identifiers, and official Android documentation. A recent
commit or compatibility claim is not containment evidence. The following labels
are used throughout:

- **SOURCE CLAIM** — an upstream README or project page says it; Privacy Decoy has
  not independently established it.
- **CODE/REPOSITORY OBSERVATION** — repository metadata or contents observable at
  the recorded ref; this is still not device evidence.
- **PRIVACY DECOY ANALYSIS** — implication under this project's threat model.
- **UNKNOWN / REQUIRES PROTOTYPE** — adequate reproducible evidence does not yet
  exist.

No candidate was vendored, built, or executed for this PR. “Runs apps,” hook
coverage, and a coherent spoofed surface do not prove containment.

## Candidate provenance and current source state

### VirtualApp: historical public tree versus commercial claims

- **Owner/source:** asLody, [public repository](https://github.com/asLody/VirtualApp).
- **Ref/date examined:** `master` at
  [`85768db8e29b5c840f1ba795d09a9d510fb5d068`](https://github.com/asLody/VirtualApp/tree/85768db8e29b5c840f1ba795d09a9d510fb5d068),
  2026-09-15.
- **License/source availability:** **CODE/REPOSITORY OBSERVATION:** the current
  repository has no root LICENSE file. The README says public GitHub code stopped
  updating in December 2017, separately maintained current source requires a
  business license, and users should purchase commercial access. Without an
  immutable historical canonical license artifact, the public tree's complete
  license posture is unresolved; a fork's license cannot establish it.
- **Maintenance:** repository HEAD is current, but recent changes chiefly maintain
  documentation/commercial-version information, not the historical public engine.
- **Capabilities:** **SOURCE CLAIM (commercial version only):** Android 17,
  Binder interception, Seccomp-BPF, 64-bit apps, ARM32/ARM64, Java/native hooks,
  Google Services, built-in Xposed Hook, and device/location modification. These
  claims are not evidence about the 2017 public code or Privacy Decoy.
- **Analysis:** closed commercial source limits provenance, SBOM and independent
  native review. Built-in Xposed is in tension with the no-Xposed requirement;
  evidence must show it is optional and absent in any qualifying configuration.
  Split support, early initialization, syscall leakage, OEM behavior, artifact
  identity and broker security remain **UNKNOWN / REQUIRES PROTOTYPE**.

### DroidPlugin

- **Owner/source:** DroidPluginTeam/Qihoo 360,
  [canonical repository](https://github.com/DroidPluginTeam/DroidPlugin).
- **Ref/date examined:** `master` at
  [`c6ebf652e0f73aa0e5746766e117e51efaf41dbd`](https://github.com/DroidPluginTeam/DroidPlugin/tree/c6ebf652e0f73aa0e5746766e117e51efaf41dbd),
  2019-12-14.
- **License/source availability:** root LICENSE is GNU LGPL-3.0; public source is
  available at the recorded ref.
- **Maintenance:** the last recorded canonical commit is stale relative to modern
  Android releases.
- **Capabilities:** **SOURCE CLAIM:** the README describes Android 2.3+ support,
  explicitly lacks Native-layer Hook, and warns APKs containing native code may
  fail to load as plugins.
- **Analysis:** lack of native-layer mediation is a major gap for an adversary that
  can use JNI, libc and direct syscalls. API 31–37, 64-bit, modern split packages,
  OEM behavior, multiprocess and early-init coverage remain **UNKNOWN / REQUIRES
  PROTOTYPE**; it is not the lead prototype candidate.

### Original FBlackBox/BlackBox

- **Owner/source:** FBlackBox,
  [original repository](https://github.com/FBlackBox/BlackBox).
- **Ref/date examined:** `master` at
  [`a13734339f85a85b4400926f7142557cb6f97dd9`](https://github.com/FBlackBox/BlackBox/tree/a13734339f85a85b4400926f7142557cb6f97dd9),
  2024-04-12.
- **License/source availability:** **CODE/REPOSITORY OBSERVATION:** the repository
  remains reachable but now presents a project dissolution/deletion notice and no
  longer exposes the prior complete engine tree. It has no root LICENSE file.
- **Maintenance/provenance:** the dissolution state prevents ordinary maintenance
  assessment and materially degrades source availability. License continuity and
  the provenance of removed history/native artifacts are unresolved.
- **Analysis:** this original repository is disqualifying as an adoptable engine in
  its current state. Any surviving fork must independently prove resolvable source,
  ancestry, license continuity, imported binaries and security-fix history. A
  fork-provided license does not cleanse the original history. No unverified fork
  is promoted here.

### SpaceCore

- **Owner/source:** FSpaceCore,
  [SpaceCore demo/integration repository](https://github.com/FSpaceCore/SpaceCore).
- **Ref/date examined:** `main` at
  [`3826a2fa1ac492fbbe7435ccb52074c1e2b702de`](https://github.com/FSpaceCore/SpaceCore/tree/3826a2fa1ac492fbbe7435ccb52074c1e2b702de),
  2024-02-01.
- **License/source availability:** **CODE/REPOSITORY OBSERVATION:** no root LICENSE
  exists. **SOURCE CLAIM:** the SDK is free but not open-source; the public
  repository is a demo/integration project, not engine source.
- **Capabilities/dependencies:** **SOURCE CLAIM:** Android 6.0–14.0,
  `armeabi-v7a` and `arm64-v8a`; listed dependencies include MMKV, Gson and Kotlin
  stdlib.
- **Maintenance:** the recorded demo ref has no later commit established here; it
  is not evidence of SDK internals or ongoing security maintenance.
- **Analysis:** closed engine/native binaries prevent complete provenance review,
  native audit, independent security review and code-derived SBOM/dependency
  verification. A wrapper replacement boundary cannot remove trust in opaque code
  while it executes protected apps. API 35–37 and all containment properties are
  unknown. This is disqualifying for production absent substantially stronger
  review access and evidence.

### chiyuan5/VirtualSpace (Android project)

- **Owner/source:** chiyuan5,
  [Android VirtualSpace repository](https://github.com/chiyuan5/VirtualSpace)
  (not an unrelated project with the same name).
- **Ref/date examined:** `main` at
  [`b1ff7988ac598b00b45c22003390ff43396c1c01`](https://github.com/chiyuan5/VirtualSpace/tree/b1ff7988ac598b00b45c22003390ff43396c1c01),
  2026-05-14.
- **License/source availability:** public source is visible. **SOURCE CLAIM:** the
  README says MIT; **CODE/REPOSITORY OBSERVATION:** there is no root LICENSE file,
  so the grant and coverage must be resolved before reuse.
- **Capabilities:** **SOURCE CLAIM:** Android 9–16, root not required with limited
  functionality, PackageManager/ActivityManager/service interception, native C/C++
  PLT/GOT hooking, device spoofing, and a `QUERY_ALL_PACKAGES` requirement.
- **Maintenance/maturity:** the recent ref establishes activity, not maturity. Its
  small/new public footprint increases provenance, review-depth, bus-factor and
  test-evidence risk, but does not alone reject it.
- **Analysis:** Android 17/API 37, split packages, UID/process isolation, syscall
  bypass, early/multiprocess behavior, broker security, tests and OEM coverage are
  **UNKNOWN / REQUIRES PROTOTYPE**. `QUERY_ALL_PACKAGES` has privacy and Google
  Play distribution implications. README claims are not accepted capability proof.

### Work-profile projects

**Shelter.** Owner PeterCxy; canonical source is the author's
[Gitea repository](https://gitea.angry.im/PeterCxy/Shelter), with a qualified
[GitHub mirror](https://github.com/PeterCxy/Shelter). The mirror ref examined is
[`672560f551772b5cd829b2947bae830d78f20edf`](https://github.com/PeterCxy/Shelter/tree/672560f551772b5cd829b2947bae830d78f20edf),
2026-06-02; its LICENSE is GPL-3.0. **SOURCE CLAIM:** Shelter uses Android Work
Profile APIs and is in effective maintenance mode while continuing adaptation to
new Android versions. Public source remains available.

**Island.** Owner Oasis Feng; [public repository](https://github.com/oasisfeng/island),
`master` at
[`d63538212a9f417c180bdb9258c0f5461de53c27`](https://github.com/oasisfeng/island/tree/d63538212a9f417c180bdb9258c0f5461de53c27),
2021-09-02; root LICENSE is Apache-2.0. The stale public-source ref must be assessed
separately from any current Play-distributed product behavior or closed changes.

**Insular.** Owner secure-system; the canonical project is
[GitLab secure-system/Insular](https://gitlab.com/secure-system/Insular), not a
GitHub mirror. **SOURCE CLAIM:** it is based on/forked from Island.
**CODE/REPOSITORY OBSERVATION:** canonical history was visible through 2025,
including a 2025-07-31 commit and v6.4.2-era tags. Exact current head and license
at the assessment cutoff require recording before code reuse. GitHub mirrors such
as `proletarius101/Insular` are noncanonical and stale relative to GitLab.

**Family analysis:** Android documents managed-profile isolation and policy APIs.
These projects offer valuable OS-enforced storage/account/package separation and
are stronger comparators than pretending hooks form a kernel boundary. They do
not inherently provide arbitrary coherent personas, synthesize every service,
hide all device-wide kernel observations, or prove external-VPN routing. Profile
provisioning, device/OEM policy and distribution constraints also apply.

### Full Android VM and bespoke runtime

**Full VM.** Android's [Virtualization Framework overview](https://source.android.com/docs/core/virtualization)
is the primary platform source. **SOURCE CLAIM:** supported Android devices can
isolate workloads using virtualization. **ANALYSIS:** this does not prove an
ordinary app can distribute and host a general Android guest. A full guest may
strengthen isolation but expands images, TCB, boot/runtime cost, ABI/device
availability, lifecycle/UI and distribution constraints; it must not be selected
merely for convenience.

**Bespoke narrow prototype.** Owner/source: Privacy Decoy; no implementation and
no project license. It offers direct control and a clean replacement boundary but
the greatest engineering/audit burden. Framework emulation, Binder, native/syscall
and OEM coverage are all **UNKNOWN / REQUIRES PROTOTYPE**.

Relevant platform sources are Android's [application sandbox](https://source.android.com/docs/security/app-sandbox),
[package visibility](https://developer.android.com/training/package-visibility),
[non-SDK restrictions](https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces),
[managed profiles](https://developer.android.com/work/managed-profiles), and
[APK signing](https://source.android.com/docs/security/features/apksigning).

## Qualitative criteria matrix

States are only **Meets**, **Potentially meets**, **Gap**, **Unknown**, or
**Disqualifying**. “Meets” reflects a documented architectural fact, not a complete
Privacy Decoy endorsement. VA-public is the historical public VirtualApp tree;
VA-commercial is the separately maintained line described by its README; VS is
chiyuan5/VirtualSpace; and Work profiles covers Shelter/Island/Insular where their
shared platform boundary is decisive. Project-specific maintenance/license
differences remain recorded above.

| Criterion | VA-public | VA-commercial | DroidPlugin | FBlackBox original | SpaceCore | VS | Work profiles | Full VM | Bespoke prototype |
|---|---|---|---|---|---|---|---|---|---|
| Root-free; no Magisk/Xposed/LSPosed | Potentially meets | Unknown | Potentially meets | Disqualifying | Potentially meets | Potentially meets | Meets | Unknown | Potentially meets |
| No Privacy Decoy `VpnService` | Potentially meets | Unknown | Potentially meets | Disqualifying | Potentially meets | Potentially meets | Potentially meets | Potentially meets | Potentially meets |
| No APK rewriting/re-signing | Unknown | Unknown | Potentially meets | Disqualifying | Unknown | Unknown | Meets | Potentially meets | Unknown |
| Split APK/APKS | Unknown | Unknown | Gap | Disqualifying | Unknown | Unknown | Meets | Potentially meets | Unknown |
| ARM64 / 64-bit protected apps | Unknown | Potentially meets | Unknown | Disqualifying | Potentially meets | Potentially meets | Meets | Unknown | Unknown |
| API 31–37 / OEM robustness | Gap | Unknown | Gap | Disqualifying | Gap | Unknown | Potentially meets | Unknown | Unknown |
| Java/framework mediation | Potentially meets | Potentially meets | Potentially meets | Disqualifying | Unknown | Potentially meets | Gap | Potentially meets | Unknown |
| Binder/service/provider mediation | Unknown | Potentially meets | Unknown | Disqualifying | Unknown | Potentially meets | Gap | Potentially meets | Unknown |
| JNI/native mediation | Unknown | Potentially meets | Gap | Disqualifying | Unknown | Potentially meets | Gap | Potentially meets | Unknown |
| Direct syscall, `/proc`, `/sys` containment | Unknown | Potentially meets | Gap | Disqualifying | Unknown | Unknown | Gap | Potentially meets | Unknown |
| Process/UID and management isolation | Unknown | Unknown | Unknown | Disqualifying | Unknown | Unknown | Potentially meets | Potentially meets | Unknown |
| Broker authentication / stale capability safety | Unknown | Unknown | Unknown | Disqualifying | Unknown | Unknown | Gap | Unknown | Unknown |
| Multiprocess / early initialization | Unknown | Unknown | Unknown | Disqualifying | Unknown | Unknown | Potentially meets | Potentially meets | Unknown |
| Dynamic loading | Unknown | Unknown | Unknown | Disqualifying | Unknown | Unknown | Meets | Potentially meets | Unknown |
| Package visibility / host-service leakage | Unknown | Unknown | Unknown | Disqualifying | Unknown | Potentially meets | Gap | Potentially meets | Unknown |
| Google Play Services behavior | Unknown | Potentially meets | Unknown | Disqualifying | Unknown | Unknown | Potentially meets | Unknown | Unknown |
| No hidden/private API dependency | Gap | Unknown | Gap | Disqualifying | Unknown | Unknown | Potentially meets | Unknown | Potentially meets |
| Maintenance/test evidence | Gap | Unknown | Gap | Disqualifying | Gap | Unknown | Potentially meets | Unknown | Gap |
| Source/license suitability | Gap | Disqualifying | Potentially meets | Disqualifying | Disqualifying | Gap | Potentially meets | Unknown | Potentially meets |
| Provenance / dependency / supply-chain auditability | Gap | Disqualifying | Potentially meets | Disqualifying | Disqualifying | Unknown | Potentially meets | Unknown | Potentially meets |
| Reviewable TCB / replacement boundary | Unknown | Gap | Unknown | Disqualifying | Gap | Unknown | Potentially meets | Gap | Potentially meets |

The matrix contains no production winner. Commercial VirtualApp and SpaceCore may
claim broad compatibility, but opaque source cannot presently satisfy independent
review and supply-chain requirements. VirtualSpace is current enough to study but
its license discrepancy, maturity and containment evidence remain unresolved.
Work profiles have the best documented OS isolation primitive but a functional gap
against synthetic-persona mediation.

## Prototype handoff and STOP gate

PR 4 must determine whether app code can execute without installation/re-signing;
the actual process/UID and management-state boundaries; Binder, service, provider,
package and host-service leakage; JNI/syscall, `/proc`, `/sys`, filesystem and
property bypasses; early initialization and multiprocess behavior; stale capability
survival; and whether Unknown mandatory coverage blocks all protected code.

PR 5 must enumerate every traffic-producing process/helper; establish attribution
and control; test external-VPN verification and route changes; prove physical
fallback is blocked; and independently observe IPv4/IPv6, DNS, TCP/UDP, QUIC,
Java/native, background and helper traffic. No `VpnService` is authorized.

PR 6 is the mandatory user STOP decision. Evidence may justify another prototype,
a managed-profile redesign, explicit unsupported status, or ending the project.
Until the boundary is sufficiently validated, use only controlled probe apps and
safe fixtures—never ordinary protected apps or real private accounts/data.
