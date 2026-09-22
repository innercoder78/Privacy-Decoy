# Architecture discovery: VirtualSpace static architecture falsification

**Review date:** 2026-09-22

**Candidate:** [`chiyuan5/VirtualSpace` at `b1ff7988ac598b00b45c22003390ff43396c1c01`][vs-tree]

**Review type:** bounded, non-executing static architecture falsification

**Disposition:** **DISQUALIFIED — exact pinned candidate**

## 1. Scope and relationship to the prior provenance result

This record asks only whether positive source evidence at the exact immutable
candidate pin above falsifies a mandatory Privacy Decoy architecture boundary.
It is not a general review of VirtualSpace, a claim about every past or future
revision, or a complete audit of every file in the pinned tree.

The earlier [S2 source/provenance audit](pr8a-engine-source-provenance-audit.md)
remains historical evidence. It stopped this same pin at provenance Gate 1 as
**STOPPED_UNRESOLVED**; this review neither repairs nor waives that gate and does
not relabel the earlier evidence as wrong. ADR-0006 does not require continued
provenance work after positive immutable source evidence independently falsifies
a mandatory architecture property. Even a later provenance PASS would not turn
placeholder, no-op, or host-fallback enforcement into a credible boundary.

## 2. Method, evidence basis, and limits

The analysis compares only the source observations listed below with the
mandatory boundaries already established by [ADR-0006](../decisions/ADR-0006-redesign-again-architecture-discovery.md),
the [requirements register](../requirements.md), and the
[canonical audit integration](../canonical-audit-integration.md). The source
observations and blob identities were independently reviewer-verified through
read-only GitHub retrieval on 2026-09-22 at the exact candidate ref and supplied
for this record. This review does not claim a separate Codex retrieval of those
sources.

No candidate source or binary was built, executed, installed, integrated,
vendored, imported, or added to this repository. No runtime behavior was tested
or inferred. Short upstream phrases are quoted only where needed to distinguish
claims from implementation; all other source details are paraphrased. The prior
S2 full-tree inventory bounded the native module considered here, but behavior
outside the reviewed observations remains **Unknown**, not success or failure.

## 3. Immutable observations: claims versus implementation

| Pinned source and reviewer-verified blob | Repository observation | Evidence classification |
|---|---|---|
| [`README.md`][vs-readme], `c7d60b7cf726c4ed94fc81db29fb8550ec03b8a4` | Describes a “complete, self-built app virtualization framework” and claims device spoofing, PackageManager/ActivityManager hooks, service proxying, and native PLT/GOT interception. | **Upstream claims only**, not implementation evidence. |
| [`hook/src/main/cpp/native_hook.cpp`][vs-native-hook], `e177ef789b48bd13556da86194d083eca37be34b` | Initialization explicitly logs `Native hook init (placeholder)`; exposed hook and unhook paths identify themselves as placeholders and return failure instead of performing interception. | Positive placeholder/failure implementation evidence in the bounded native module. |
| [`hook/src/main/cpp/hook_impl.c`][vs-hook-impl], `b22c3333fe11bba0d27adc232f08a30032e07d36` | Describes a simplified placeholder/empty implementation. `hook_plt_got(...)` logs `Hook not implemented` and returns `-1`; unhook likewise fails. | Directly contradicts treating the README PLT/GOT claim as implemented enforcement. |
| [`hook/src/main/java/com/virtual/hook/nativehook/NativeHook.java`][vs-native-java], `a4878751d1f604fe8895d3f44c9116d7a8c93040` | Loads `virtualhook`, declares native methods including `hookMethod`, `hookNativeMethod`, `getModuleBase`, `getSymbol`, and `unhook`, reports API 28+ support, and returns `0` from `getTransactHook()`. | The prior native-module inventory and independent pinned-source search did not establish matching JNI implementations for those declarations. This is a bounded absence-of-evidence statement, not a claim about unreviewed or future code. |
| [`app/src/main/java/com/virtual/core/loader/VirtualAppLoader.java`][vs-loader], `c749b11200376c7c34374accd994f6874dae358c` | Uses `hostContext.createPackageContext(...)`; on failure it returns the genuine `hostContext`. `loadApplicationInfo(...)` returns `null`, `isPackageInstalled(...)` returns `false`, and classloader creation falls back to the supplied parent on failure. | Positive host-fallback and stub behavior in mandatory loading/context paths. |
| [`app/src/main/java/com/virtual/core/service/ServiceBroker.java`][vs-broker], `6ef34f954be891de6f849e120f2e08e06e1e3e12` | `init()` logs initialization; `getService(String)` returns `null`. | No broad service mediation is implemented by this broker path. |
| [`app/src/main/java/com/virtual/core/am/VirtualActivityManager.java`][vs-am], `0bcbe728ddc45b31dd4b1864f83fdbbe2dab6186` | `startActivity(...)` and `finishActivity(...)` return `true`; `getUidForPackage(...)` computes `userId * 100000`. | Success-shaped return values and a calculated identifier are not evidence of lifecycle enforcement, a kernel UID, or OS process/package isolation. |
| [`app/src/main/java/com/virtual/hook/BinderHook.java`][vs-binder], `b877410a283f1eebfcb7a3d3ecae7ac681db0db4` | Obtains genuine system-service Binder objects, stores them in `originalServices`, then writes the same original Binder object into `ServiceManager.sCache`. | That path installs no mediation proxy. A success log does not establish interception. |

## 4. Mandatory-boundary consequences

| Existing obligation | Consequence of the positive pinned-source evidence |
|---|---|
| **Pre-code mediation** | Placeholder native initialization, stub application metadata, parent-classloader fallback, and success-shaped activity operations do not establish protection before providers, `Application`, native initializers, dynamic code, or secondary processes. Those unreviewed entry paths remain **Unknown**; under PD-REQ-021 and PD-REQ-044, mandatory Unknown coverage must block before protected code rather than be treated as working mediation. |
| **Native/direct-syscall containment** | The bounded native hook and PLT/GOT implementations explicitly identify themselves as placeholders and return failure. Java declarations without matching implementations established in the reviewed pinned native module do not supply native enforcement. Nothing reviewed establishes containment of JNI, libc, direct syscalls, native threads, `dlopen`, filesystems, `/proc`, `/sys`, properties, sockets, or file descriptors as required by PD-REQ-014 and PD-REQ-081. Unreviewed paths remain Unknown. |
| **Binder and service mediation** | The broker returns no services, while the reviewed Binder hook restores the genuine Binder rather than a proxy. These positive implementations cannot establish broad Binder/service/provider mediation under PD-REQ-013 and PD-REQ-031; log wording cannot substitute for an interposed enforcement object. |
| **Host-context leakage and fail-closed behavior** | Returning the genuine host context when virtual context creation fails is an explicit fail-open host-semantics fallback. A parent-classloader fallback also crosses toward host execution semantics on failure. This conflicts directly with PD-REQ-021 and ADR-0006: mandatory mediation failure must block, not expose genuine host context or continue through an uncontrolled parent. |
| **Normal Android application semantics** | Null application metadata, a permanently false installed-package check, and unconditional boolean activity success do not establish real application/component lifecycle, resources, splits, multidex, signed artifact handling, native loading, jobs, alarms, services, providers, background execution, subprocesses, or multiprocess behavior. The review does not claim how those unreviewed paths behave; it finds no credible boundary in the positively observed core implementations. |
| **Actual OS UID/process isolation** | `userId * 100000` is a synthetic arithmetic identifier returned by application code. It is not evidence that Android assigned a distinct kernel UID, created an isolated process, enforced filesystem ownership, or separated Binder credentials. Descriptive or synthetic identity must remain distinct from actual runtime/kernel capability under PD-REQ-011 and PD-REQ-073. |
| **Revocation and lifecycle failure** | Returning success without observed enforcement and retaining genuine Binder objects do not establish capability invalidation, stale-handle control, death handling, or safe recovery under PD-REQ-015, PD-REQ-027, and PD-REQ-045. Those behaviors remain Unknown rather than inferred from method names or return values. |
| **Storage and networking** | This bounded review did not examine complete storage or networking behavior. They remain Unknown. Nothing here changes the requirements for native filesystem isolation, traffic-producer attribution, external-VPN verification, independent path evidence, or no physical fallback; Privacy Decoy still must not implement `VpnService`. |

## 5. Why configuration or validation cannot repair this pin

The decisive evidence is implementation content, not merely missing test output:
mandatory native interception functions are explicit placeholders that fail;
the reviewed service broker supplies no service mediation; the Binder path
reinstalls original objects; and loader failure exposes genuine host context.
Configuration, documentation, or runtime validation cannot transform those
implementations into containment.

Making this exact pin satisfy Privacy Decoy would therefore require implementing
or replacing core native containment, Binder/service mediation, fail-closed
loading/context behavior, lifecycle handling, and real isolation mechanisms.
That is a material architectural rewrite, not validation of an existing credible
boundary. A materially redesigned fork would be a new candidate, not a repaired
PASS inherited from this review.

## 6. Requirement implications

No requirement is marked satisfied, removed, renumbered, or weakened. The
observations positively conflict with or fail to establish the mandatory
boundaries in PD-REQ-011, PD-REQ-013 through PD-REQ-015, PD-REQ-021, PD-REQ-027,
PD-REQ-030, PD-REQ-031, PD-REQ-041, PD-REQ-044, PD-REQ-045, PD-REQ-073, and
PD-REQ-081. PD-REQ-019, PD-REQ-020, PD-REQ-057, PD-REQ-058, PD-REQ-060,
PD-REQ-063, PD-REQ-083, PD-REQ-084, and PD-REQ-085 continue to govern evidence,
coverage, supply chain, gates, acceptance, sequencing, and public-safe records.

This document is static evidence only. It does not establish runtime behavior,
production viability, compatibility, network safety, storage safety, or complete
source coverage. All unreviewed behavior remains **Unknown**. Unknown is not a
finding of safety, a finding of failure, or permission to run protected code.

## 7. Explicit non-authorization

This review does not authorize a build, runtime test, import, installation,
integration, vendoring, dependency addition, binary addition, bounded prototype,
ordinary protected app, private data, real account, production claim, canonical
production PR 6, or any product implementation. It does not authorize root,
guest root, privileged/system installation, production ADB, Magisk, Xposed,
LSPosed, a custom ROM, patched kernel, routine APK rewriting/re-signing,
fail-open behavior, or a Privacy Decoy `VpnService`.

S1 remains **FALSIFIED** and its follow-up **BLOCKED**. Blacks-BlackBox remains
**STOPPED_UNRESOLVED**. No third-party engine is audit-cleared, no production
architecture is selected, and product implementation remains paused.

## 8. Candidate disposition

## **DISQUALIFIED — exact pinned candidate**

The exact pinned VirtualSpace candidate contains positive placeholder/no-op
evidence in mandatory core enforcement areas and an unsafe genuine-host-context
fallback. Satisfying Privacy Decoy would require implementing or replacing core
containment and mediation mechanisms rather than configuring or validating an
existing credible boundary. The pin is therefore not a viable Privacy Decoy
architecture candidate.

This disposition is limited to
`b1ff7988ac598b00b45c22003390ff43396c1c01`. It does not establish that
user-space virtualization is universally impossible and makes no claim that a
missing feature is absent from every future VirtualSpace version. A future
materially different revision or independently redesigned fork is a new
architecture candidate requiring fresh provenance, source-completeness,
architecture, and evidence admission; it inherits no PASS from this work.

## 9. Immutable source references

[vs-tree]: https://github.com/chiyuan5/VirtualSpace/tree/b1ff7988ac598b00b45c22003390ff43396c1c01
[vs-readme]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/README.md
[vs-native-hook]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/hook/src/main/cpp/native_hook.cpp
[vs-hook-impl]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/hook/src/main/cpp/hook_impl.c
[vs-native-java]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/hook/src/main/java/com/virtual/hook/nativehook/NativeHook.java
[vs-loader]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/app/src/main/java/com/virtual/core/loader/VirtualAppLoader.java
[vs-broker]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/app/src/main/java/com/virtual/core/service/ServiceBroker.java
[vs-am]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/app/src/main/java/com/virtual/core/am/VirtualActivityManager.java
[vs-binder]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/app/src/main/java/com/virtual/hook/BinderHook.java
