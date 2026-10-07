# Open-source architecture and implementation reference catalog

> **Active development suspended:** [ADR-0012](decisions/ADR-0012-suspend-active-development-and-establish-pdva-successor.md)
> suspends Privacy Decoy implementation. The Phase II direction and reference
> snapshots below are preserved for historical research, not authorization for
> continued implementation. See [project status](project-status.md).

> **Phase II forward status:**
> [ADR-0010](decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md)
> governs the forward architecture and selects transformation-first hybrid
> mediation. Transformation, rebuild, re-signing, DEX/resource tooling, native
> hooks, Binder work, and virtualization/runtime semantics are distinct reference
> categories. Virtualization/runtime projects remain references rather than a
> selected wholesale engine. References are not security evidence, and this PR
> adopts no dependency. Opaque security-critical binaries cannot enter the
> Privacy Decoy TCB. The catalog is not exhaustive proof that no other approach
> exists.

**Historical Phase I context:** On 2026-09-29,
[ADR-0008](decisions/ADR-0008-post-ag1-enforcement-boundary-redesign.md) kept this
catalog active for the
[post-AG-1 enforcement-boundary redesign](post-ag1-enforcement-boundary-redesign.md).
That redesign and status are historical. Their findings, pins, and dispositions
remain evidence, but they do not govern Phase II forward architecture.

These entries are research reference snapshots, not production dependencies.
Repository HEADs and exact-current upstream state can change; the commit pins
below are the evidence snapshots reviewed on 2026-09-22. Upstream README claims
are claims, not Privacy Decoy evidence. A license observed at a repository root
does not prove the provenance or license of every inherited or bundled component.
Direct reuse requires separate exact-source, license, provenance, dependency,
native-binary, supply-chain, and security review. No opaque binary may enter
PD's trusted computing base merely because a repository has a permissive
top-level license.

## Reference synthesis

| Phase II reference category | Research references |
|---|---|
| Artifact, split, signing, and package analysis | Mirro + NEXTVM + bundletool + AppManager |
| APK transformation, rebuild, and re-signing | Prison + APKEditor + LSPatch + ReVanced Patcher |
| DEX and resource transformation | smali + Redex + ARSCLib + APKEditor |
| Persona/profile and privacy-surface inventory | SpoofMyDevice + XPrivacyLua |
| Native-hook research | ByteHook + ShadowHook + LSPlant + Pine |
| Binder research | Binderceptor + NewBlackbox + NEXTVM + libgbinder |
| Virtualization/runtime semantic references | NewBlackbox + NEXTVM + Renjana + MultiApp |
| Negative and historical lessons | VirtualSpace + Mirro |

**Reference influence does not establish security evidence.** No project is
selected wholesale, and every future use requires a separate integration
decision.

## NewBlackbox

* **Repository:** [ALEX5402/NewBlackbox](https://github.com/ALEX5402/NewBlackbox)
* **Reviewed snapshot:** [`89b59836c66f173756a4ae258cf379a957649820`](https://github.com/ALEX5402/NewBlackbox/commit/89b59836c66f173756a4ae258cf379a957649820)
* **Observed license situation:** top-level Apache-2.0; the repository credits
  VirtualApp/VirtualAPK and bundles/prebuilds third-party artifacts including
  Dobby archives and AAR/JAR files, so inherited provenance is not established.
* **Useful ideas:** mature virtual package/component/runtime organization;
  process slots and stub components; Activity, service, provider, and job
  machinery; Binder/service proxies; path redirection; property/identity hooks;
  JNI compatibility; WebView and background-work compatibility.
* **PD limitations:** no demonstrated seccomp, raw-syscall, or kernel confinement
  boundary was found. Slots remain host-app process identities rather than
  independently installed tenant UIDs. Selected native hooks use function
  interposition. Its PD-prohibited `VpnService` concept must not be copied.
* **Disposition:** **REFERENCE ONLY pending full provenance**.

## Binderceptor

* **Repository:** [iofomo/binderceptor](https://github.com/iofomo/binderceptor)
* **Reviewed snapshot:** [`7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61`](https://github.com/iofomo/binderceptor/commit/7e09a8379cc5d6f71f6cfa0f9e358cecf4ceab61)
* **Observed license situation:** MIT notice; the public tree includes prebuilt
  `libifmabinderceptor-core.so`.
* **Useful ideas:** low-level Binder interception architecture and design
  inspiration below Java service proxies.
* **PD limitations:** visible JNI resolves/calls an apparently source-incomplete
  core, leaving the most security-critical portion opaque. Binder interception
  alone does not contain direct syscalls, filesystem, or network paths.
* **Disposition:** **REFERENCE ONLY unless source completeness/provenance is
  independently resolved**.

## XPrivacyLua

* **Repository:** [M66B/XPrivacyLua](https://github.com/M66B/XPrivacyLua)
* **Reviewed snapshot:** [`85a1e498d5a9dbb902ca3d83e987ae6eec377d7a`](https://github.com/M66B/XPrivacyLua/commit/85a1e498d5a9dbb902ca3d83e987ae6eec377d7a)
* **Observed license situation:** GPLv3; direct reuse is a deliberate licensing
  decision.
* **Useful ideas:** privacy/API surface inventory and hook/category catalog for
  framework, SDK, and library coverage planning.
* **PD limitations:** depends on Xposed-style hooks; its documentation notes
  native code is outside that model. Root/Xposed/LSPosed are prohibited PD
  production mechanisms.
* **Disposition:** **REFERENCE/COVERAGE CATALOG ONLY unless licensing strategy
  explicitly changes**.

## SpoofMyDevice

* **Repository:** [BuSung-dev/SpoofMyDevice](https://github.com/BuSung-dev/SpoofMyDevice)
* **Reviewed snapshot:** [`ca78ffa6f18d44dc3d1e0fff2a48d74ad4884b19`](https://github.com/BuSung-dev/SpoofMyDevice/commit/ca78ffa6f18d44dc3d1e0fff2a48d74ad4884b19)
* **Observed license situation:** root MIT license.
* **Useful ideas:** Persona/profile organization, deterministic stable values,
  device/build/locale/timezone/display/identifier modeling, per-app assignment,
  and persistence.
* **PD limitations:** its root/LSPosed mechanisms remain prohibited for PD
  production. Its APK patching/re-signing approach conceptually overlaps with
  Phase II transformation, but similarity does not establish PD security evidence
  and API hooking does not contain hostile native execution.
* **Disposition:** **REFERENCE / SELECTIVE SOURCE REUSE CANDIDATE AFTER LICENSE,
  PROVENANCE, SECURITY, DEPENDENCY, AND TCB REVIEW**, especially for Persona
  modeling. PD has not adopted SpoofMyDevice's enforcement implementation.

## VirtualSpace

* **Repository:** [chiyuan5/VirtualSpace](https://github.com/chiyuan5/VirtualSpace)
* **Reviewed snapshot:** [`b1ff7988ac598b00b45c22003390ff43396c1c01`](https://github.com/chiyuan5/VirtualSpace/commit/b1ff7988ac598b00b45c22003390ff43396c1c01)
* **Observed license situation:** unresolved provenance history remains part of
  the repository's existing evidence.
* **Useful ideas:** negative evidence, organizational reference, and examples of
  failure modes only.
* **PD limitations:** placeholder/no-op native hooking, unsafe host fallback,
  incomplete service/Binder/application machinery, and unresolved provenance.
* **Disposition:** **DISQUALIFIED EXACT PIN**. Do not resurrect this pin as a
  runtime candidate.

## NEXTVM

* **Repository:** [TanvirHossain2/NEXTVM](https://github.com/TanvirHossain2/NEXTVM)
* **Reviewed snapshot:** [`f581a6642596db396a6fe606addd735979403b6b`](https://github.com/TanvirHossain2/NEXTVM/commit/f581a6642596db396a6fe606addd735979403b6b)
* **Observed license situation:** root Apache-2.0 license.
* **Useful ideas:** process slots, stub registry/component mapping, APK parsing,
  virtual context/storage, Binder proxy organization, component routing, GMS
  research, identity/profile modeling, and real GOT/PLT hooks for `open`,
  `openat`, `access`, `stat`, `lstat`, `readlink`, `fopen`, and
  `__system_property_get`.
* **PD limitations:** no seccomp/direct-syscall containment was established.
  Unhandled ActivityManager calls can reach the genuine system; reviewed proxy
  code sometimes swallows `SecurityException` for compatibility. Host GMS
  bridging must not expose host accounts/state. “Complete isolation” is an
  upstream claim, not PD evidence; the project describes itself as alpha.
* **Disposition:** **SELECTIVE SOURCE REUSE CANDIDATE AFTER PROVENANCE/SECURITY
  AUDIT**. Do not adopt wholesale.

## Renjana

* **Repository:** [josskixg/renjana](https://github.com/josskixg/renjana)
* **Reviewed snapshot:** [`14302a57cd66114c6979acf7ca56c97f845a6841`](https://github.com/josskixg/renjana/commit/14302a57cd66114c6979acf7ca56c97f845a6841)
* **Observed license situation:** root Apache-2.0; Pine and transitive provenance
  and licensing require independent review.
* **Useful ideas:** container-instance management, split/resource handling,
  per-instance/package storage, lifecycle diagnostics, account/container
  mapping, stub Activities, and failure reporting.
* **PD limitations:** non-root enforcement uses Pine/ART instrumentation, which
  does not solve direct native/syscall containment. Reviewed code includes
  `FallbackNoIsolation`. An unsafe/no-isolation or silent genuine-host fallback
  is incompatible with Phase II. Where mediation is claimed, a relevant path
  must return its configured Real/Decoy/Empty/Deny result before genuine
  disclosure. Otherwise it receives an honest Partially mediated, Unsupported,
  or Unknown coverage result as appropriate.
* **Disposition:** **REFERENCE / SELECTIVE NON-ENFORCEMENT SOURCE REUSE CANDIDATE
  AFTER AUDIT**. Pine is not automatically the PD boundary.

## Mirro Android Virtualization

* **Repository:** [obadadallo95/mirro-android-virtualization](https://github.com/obadadallo95/mirro-android-virtualization)
* **Reviewed snapshot:** [`74e6a1e3ea1898b2e2a705d7c8b3e730059023b1`](https://github.com/obadadallo95/mirro-android-virtualization/commit/74e6a1e3ea1898b2e2a705d7c8b3e730059023b1)
* **Observed license situation:** root Apache-2.0 for Mirro-owned material;
  third-party components remain separately licensed.
* **Useful ideas:** `ApkInspector`, split/executable-split and native-library
  inventories, loader graph, dynamic-code diagnostics, capability/failure
  classification, honest separation of guest metadata from real UID/Binder/
  signing identity, compatibility taxonomy, and architecture post-mortem. It is
  particularly useful for PD's admission/analyzer layer.
* **PD limitations:** Mirro concluded its in-process runtime does not own the
  decisive Android authority boundary. It is not PD's runtime; active product
  development ended and upstream says it is not production-ready.
* **Disposition:** **SELECTIVE SOURCE REUSE CANDIDATE AFTER AUDIT**, especially
  analyzer/diagnostic abstractions.

## ByteHook

* **Repository:** [bytedance/bhook](https://github.com/bytedance/bhook)
* **Reviewed snapshot:** [`a8bd254f6e53022b65136f40d10ae3763b6ef8ad`](https://github.com/bytedance/bhook/commit/a8bd254f6e53022b65136f40d10ae3763b6ef8ad)
* **Observed license situation:** MIT with separately attributed third-party source.
* **Useful ideas:** production-oriented Android PLT hooking for single, partial,
  or all callers; newly loaded libraries; broad Android/ABI support. It may be
  preferable to ad hoc GOT patching after review.
* **PD limitations:** PLT interception is function interception, not a kernel
  sandbox; raw syscalls can bypass libc/PLT paths.
* **Disposition:** **DEPENDENCY CANDIDATE AFTER SUPPLY-CHAIN/SECURITY REVIEW**.
  This PR adds no dependency.

## Additional Phase II transformation and instrumentation references

The following pins are research leads. Abbreviated revisions are recorded exactly
as supplied and require full-SHA verification before source review or reuse. No
abbreviation is expanded by guesswork.

| Project | Revision | Phase II relevance and limits |
|---|---|---|
| Prison3/Prison | `7cac8fcd...` | Transformation reference. Committed or prebuilt artifacts require source and provenance review. |
| REAndroid/APKEditor | `70d2c4ba...` | APK/resource editing reference. It includes prebuilt JAR dependencies that require provenance review. |
| REAndroid/ARSCLib | `8748668...` | Separately available source may be preferable for resource-table study. Full provenance and license review still apply. |
| ReVanced Patcher | `c6cc64dd...` | Deterministic patching and transformation reference, not an adopted runtime. |
| google/smali | `1a36aa...` | DEX assembly/disassembly reference. |
| facebook/redex | `99220023...` | DEX transformation and optimization reference. |
| google/bundletool | `586a43a...` | Split and app-bundle artifact reference; it does not grant access to unavailable private assets. |
| JingMatrix/LSPatch | `0dc50f...` | Rootless APK patch and injected-runtime feasibility reference. License, TCB, and Xposed-style design prevent automatic adoption. |
| LSPosed/LSPlant | `1dc6ef...` | ART instrumentation reference only; production root/Xposed dependence remains prohibited. |
| canyie/pine | `216d910...` | ART instrumentation reference only, not a containment boundary. |
| MuntashirAkon/AppManager | `cd9e9a...` | APK inspection, signing, install, and package-management reference. |
| WaxMoon/MultiApp | `90bd646...` | Clone/package-management reference, not privacy evidence. |
| mer-hybris/libgbinder | `fd67150...` | Binder protocol and native client reference; not a selected broker or boundary. |

The full pins already reviewed elsewhere in this catalog remain authoritative for NewBlackbox
`89b598...`, Binderceptor `7e09a...`, XPrivacyLua `85a1e498...`, SpoofMyDevice
`ca78ffa...`, NEXTVM `f581a664...`, renjana `14302a...`, Mirro
`74e6a...`, ByteHook `a8bd254...`, and ShadowHook `593f491...`.
ByteHook and ShadowHook are selective native-hook candidates, never kernel
sandboxes. Direct syscalls and other paths require separate evidence.

App Cloner is a commercial public-information product reference only. No
proprietary code, private implementation, or undocumented knowledge may be used.

## ShadowHook

* **Repository:** [bytedance/android-inline-hook](https://github.com/bytedance/android-inline-hook)
* **Reviewed snapshot:** [`593f491be68799f03ee3bab71ce5908846437147`](https://github.com/bytedance/android-inline-hook/commit/593f491be68799f03ee3bab71ce5908846437147)
* **Observed license situation:** MIT with separately attributed third-party source.
* **Useful ideas:** inline native-function and instruction-address interception,
  loaded-ELF symbol lookup, and callbacks around `.init`/`.init_array` for newly
  loaded ELF files; useful for early observation and trusted PD mediation.
* **PD limitations:** inline hooking is not kernel-enforced containment of
  arbitrary hostile machine code; compatibility varies by ABI, Android, OEM,
  symbol, and implementation.
* **Disposition:** **DEPENDENCY CANDIDATE AFTER SUPPLY-CHAIN/SECURITY REVIEW**.
  This PR adds no dependency.
