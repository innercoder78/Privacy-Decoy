# Containment and virtualization engine assessment

**Assessment date:** 2026-09-19

**Outcome:** no production engine selected; prototype a narrow, replaceable
containment boundary and use third-party projects only as research comparators.

## Method and evidence labels

This assessment prefers canonical repositories, their source/README/LICENSE and
official Android documentation. URLs and the ref available for examination are
recorded below. The assessment environment could not retrieve live GitHub content
(outbound requests returned HTTP 403), so commit hashes and activity after the
previously known public project state are deliberately `Unknown`, not invented.
Reviewers must refresh every default-branch ref, license, release, dependency and
policy fact before PR 4 admits code. This limitation makes a production choice
impossible and is itself supply-chain evidence.

Labels are strict:

- **SOURCE CLAIM** — the linked owner documentation says it; not independently
  established for Privacy Decoy.
- **CODE/REPOSITORY OBSERVATION** — directly observable repository structure or
  metadata; still not proof on a supported device.
- **PRIVACY DECOY ANALYSIS** — implication against this project's threat model.
- **UNKNOWN / REQUIRES PROTOTYPE** — no adequate reproducible evidence yet.

No repository was vendored, built, or executed. “Runs apps” is not containment.
Commercial product claims are not evidence about historical public source.

## Candidate provenance snapshot

| Candidate | Owner / canonical primary source | Ref examined; source/license | Maintenance, claims, dependencies, distribution observations |
|---|---|---|---|
| VirtualApp | asLody, [canonical historical repository](https://github.com/asLody/VirtualApp) | Public default branch metadata; exact commit **Unknown**. Historically published GPL-3.0 repository; license and present availability require live confirmation. | **SOURCE CLAIM:** historical framework virtualizes Android apps. **OBSERVATION:** old public tree and separately maintained/commercial VirtualApp offerings must be treated as different products. **ANALYSIS:** commercial compatibility statements cannot establish source behavior, licensing, or security. Current activity, API 31–37, ABI/native/split evidence and commercial terms: **UNKNOWN**. |
| DroidPlugin | DroidPluginTeam/Qihoo 360, [canonical repository](https://github.com/DroidPluginTeam/DroidPlugin) | Public default branch; exact commit **Unknown**; historically Apache-2.0 (reverify). | **SOURCE CLAIM:** README limitations include incomplete native-layer support and an old Android compatibility target. **ANALYSIS:** that gap is fundamental against JNI/direct-syscall adversaries and modern APIs. Current maintenance and API 31–37 evidence: **UNKNOWN**. |
| BlackBox | FBlackBox, [original repository URL](https://github.com/FBlackBox/BlackBox) | Current availability/ref/license **Unknown** because the source could not be fetched. | **OBSERVATION/ANALYSIS:** many similarly named mirrors/forks make identity, ancestry, license continuity and security-fix provenance high risk. No fork inherits credibility merely by retaining the name. Original status, native binaries/dependencies, API/ABI tests and maintenance require reconstruction from immutable history. |
| BlackBox forks | Individually audit candidates such as [ToryYang/BlackBox](https://github.com/ToryYang/BlackBox) | Default branch/commit/license/ancestry **Unknown**; not accepted as upstream. | **ANALYSIS:** surviving fork must map every imported commit and binary to provenance/license, show active tests and disclose divergence. Until then supply-chain risk is disqualifying for adoption, though source can inform probes. |
| FSpace / newer “VirtualSpace” family | FSpaceCore, [FSpace repository](https://github.com/FSpaceCore/FSpace) | Default branch/commit/license **Unknown**; source availability requires refresh. | **SOURCE CLAIM:** project presentation may describe application virtualization. **ANALYSIS:** name, recency or stars do not prove lineage, maturity or containment. Native payload provenance, hidden APIs, copied VirtualApp/BlackBox ancestry, splits, API 31–37 and test quality all require audit. |
| Shelter | PeterCxy, [canonical repository](https://github.com/PeterCxy/Shelter) | Default branch/commit **Unknown**; GPL-family license historically published (reverify exact SPDX). Source available historically. | **SOURCE CLAIM:** uses Android work profile/device-policy facilities to isolate apps. Depends on platform managed-profile APIs rather than an app-virtualization engine. Maintenance and current releases require refresh. Distribution is subject to device-admin/profile and store policy. |
| Island | Oasis Feng, [canonical repository](https://github.com/oasisfeng/island) | Default branch/commit and complete license posture **Unknown**. Public source/product availability must be rechecked. | **SOURCE CLAIM:** uses Android managed profiles. **ANALYSIS:** useful isolation comparator; commercial/source boundaries and dependencies require explicit review. |
| Insular | secure-system, [canonical repository](https://github.com/secure-system/Insular) | Default branch/commit/license/ancestry **Unknown**; historically an Island-derived open-source project (verify). | **ANALYSIS:** audit fork ancestry and independent maintenance. Platform profile support gives OS UID/storage separation but not arbitrary synthetic service responses. |
| Full Android VM/emulator | Android platform, e.g. [AVF overview](https://source.android.com/docs/core/virtualization) | Official architecture documentation; product/device availability varies. | **SOURCE CLAIM:** Android virtualization can isolate workloads on supported devices. **ANALYSIS:** a full guest could strengthen boundaries but greatly expands images, boot/runtime cost, TCB, ABI/device availability, lifecycle/UI and distribution constraints. AVF is not evidence ordinary apps can host a general Android guest. |
| Bespoke Privacy Decoy runtime | This project | No implementation; no license selected. | Maximum control and replacement-boundary fit, but highest engineering/audit burden. Framework emulation, Binder, native/syscall and OEM coverage are **UNKNOWN**. A narrow prototype can falsify feasibility without importing an untrusted engine. |

Android primary references that constrain all candidates include
[application sandbox/UID isolation](https://source.android.com/docs/security/app-sandbox),
[package visibility](https://developer.android.com/training/package-visibility),
[non-SDK restrictions](https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces),
[managed profiles](https://developer.android.com/work/managed-profiles), and
[APK signing](https://source.android.com/docs/security/features/apksigning).

## What work profiles do—and do not—establish

**SOURCE CLAIM:** Android assigns profile apps separate profile-scoped data and
applies device-policy controls. **PRIVACY DECOY ANALYSIS:** Shelter, Island, and
Insular can demonstrate valuable OS-provided storage/account/package separation
and are preferable to pretending hooks are a kernel boundary. They do not, by
themselves, supply arbitrary coherent decoy identifiers, intercept every service
response, synthesize sensors/location, hide all device-wide kernel observations,
or prove external-VPN routing. The profile owner also has constrained policy
authority and OEM/user provisioning limitations. Thus this family is a useful
baseline or redesign option, not a drop-in realization of the stated persona and
mediation requirements.

## Qualitative criteria matrix

States mean: **Meets** only with adequate primary evidence; **Potentially meets**
where architecture plausibly permits validation; **Gap** for a known mismatch;
**Unknown** for absent evidence; and **Disqualifying** where the current known
design conflicts with a mandatory prototype premise. Compact cells apply to the
named family and are not production verdicts.

| Criterion | VirtualApp | DroidPlugin | BlackBox / FSpace family | Work profile family | Full VM | Bespoke narrow prototype |
|---|---|---|---|---|---|---|
| Ordinary root-free; no Magisk/Xposed/LSPosed | Potentially meets | Potentially meets | Unknown | Meets | Unknown | Potentially meets |
| No Decoy VpnService | Potentially meets | Potentially meets | Unknown | Potentially meets | Potentially meets | Potentially meets |
| No APK rewrite/re-sign | Unknown | Potentially meets | Unknown | Meets (installed in profile) | Potentially meets | Unknown |
| Split/APKS | Unknown | Gap | Unknown | Meets via platform installer, subject to policy | Potentially meets | Unknown |
| ARM64 and 64-bit protected apps | Unknown | Gap | Unknown | Potentially meets | Unknown | Unknown |
| API 31–37 and OEM robustness | Unknown | Disqualifying absent modernization | Unknown | Potentially meets | Unknown | Unknown |
| Java/framework mediation | Potentially meets | Potentially meets | Potentially meets | Gap for synthetic mediation | Potentially meets | Unknown |
| Binder/service/provider mediation | Unknown | Unknown | Unknown | Gap for synthetic mediation | Potentially meets | Unknown |
| Native/JNI and libc/direct syscall | Unknown | Disqualifying known limitation | Unknown | Gap for synthetic mediation | Potentially meets | Unknown |
| `/proc`, `/sys`, filesystem/process leakage | Unknown | Gap | Unknown | Partially mediated | Potentially meets | Unknown |
| Process/UID isolation | Unknown | Unknown | Unknown | Meets for profile-vs-host UID/data model; intra-profile limits remain | Potentially meets | Unknown |
| Management-state isolation | Unknown | Unknown | Unknown | Potentially meets | Potentially meets | Potentially meets |
| Broker authentication/stale capabilities | Unknown | Unknown | Unknown | N/A to synthetic broker goal | Unknown | Unknown |
| Multiprocess and early initialization | Unknown | Unknown | Unknown | Platform launch works; mediation gap remains | Potentially meets | Unknown |
| Dynamic loading | Unknown | Unknown | Unknown | Meets ordinary platform semantics, not mediation | Potentially meets | Unknown |
| Package visibility / host-service leakage | Unknown | Unknown | Unknown | Partially mediated; profile/device services remain | Potentially meets | Unknown |
| Play Services behavior | Unknown | Unknown | Unknown | External/profile-dependent | Unknown | Unknown |
| Hidden/private API dependence | Gap | Gap | Unknown | Potentially meets (public policy APIs) | Unknown | Potentially meets |
| Maintenance/test quality | Unknown | Gap (historical modernity) | Unknown | Unknown per project | Platform-specific/Unknown | Not yet present |
| Source/license suitability | Gap until current public/commercial boundary resolves | Potentially meets, reverify | Disqualifying until provenance/license resolved | Unknown per project | Gap/Unknown | Potentially meets |
| Dependency/supply-chain risk | Unknown/high | Unknown | Unknown/high | Moderate, platform plus project | High/large TCB | Potentially lower initially; self-authored TCB risk high |
| Replaceable engine boundary | Potentially meets only via adapter | Potentially meets | Potentially meets | Potentially meets | Gap due broad coupling | Meets as design constraint |

### Additional criteria conclusions

- **Process/UID/resource isolation:** user-space “virtualization” must demonstrate
  the real Linux credentials and kernel-visible resources, not internal virtual
  identifiers. Work profiles provide a stronger documented platform primitive,
  but not the required synthetic surface.
- **Authentication and stale capabilities:** no candidate has accepted evidence
  for caller-bound, epoch-bound broker handles, revocation, or restart behavior.
- **Native and early code:** these are decisive unknowns for VirtualApp-like and
  BlackBox-like systems. DroidPlugin's documented native limitation is a gap, not
  a backlog detail.
- **Maintenance/test quality:** a recent commit, stars, demo APK, or compatibility
  list would not substitute for adversarial, native, lifecycle, OEM and release
  tests. Low-star/new projects remain eligible for investigation but enter no TCB
  without stronger provenance and evidence.
- **Distribution:** GPL/copyleft obligations, unavailable commercial source,
  embedded native blobs, dynamic loading, device-admin/profile provisioning, and
  full-VM images each create different feasibility issues. Exact current licenses
  and Google Play rules must be refreshed before code selection.

## Prototype handoff and STOP gate

PR 4 should implement only enough controlled probe infrastructure to answer:

1. Can app code execute without installation or re-signing, and what actual
   process/UID boundaries result?
2. Can it reach management state; which Binder/services/providers, package queries,
   properties, host services, files, `/proc`, and `/sys` leak?
3. Which JNI/libc/direct-syscall, dynamic-load, subprocess, multiprocess, early
   provider and native-initializer paths bypass control?
4. Can authenticated capabilities be revoked across races, death and restart?
5. Can Unsupported or Unknown mandatory coverage prevent *all* protected code
   from starting?

PR 5 must separately enumerate traffic-producing processes/helpers; attribute and
control every route; test external-VPN verification and route changes; prove
physical fallback is blocked; and independently observe IPv4/IPv6, DNS, TCP/UDP,
QUIC, Java/native, background/helper traffic. No `VpnService` is authorized.

PR 6 is a mandatory user STOP decision. Evidence may select further prototypes,
redesign around documented work-profile guarantees, declare capabilities
unsupported, or end the project. Until that boundary is sufficiently validated,
only controlled probe apps and safe fixtures may be used—no ordinary protected
apps or real private accounts/data.
