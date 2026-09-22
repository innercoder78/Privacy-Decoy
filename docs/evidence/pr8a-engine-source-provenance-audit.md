# Roadmap PR 8A: S2 engine source and provenance audit

Date: 2026-09-22. **Non-executing static research only.**

**Documentation/governance reconciliation:** “Roadmap PR 8A” and this filename
are historical redesign-research labels. This is supplemental feasibility
evidence after the original architecture failed, not canonical production
Roadmap PR 8, an inserted canonical PR 8A, or canonical PR 9. The restored
[canonical 46-PR roadmap](../canonical-roadmap-1.0.md),
[amendment](../canonical-audit-integration.md) and
[roadmap reconciliation](../roadmap-reconciliation.md) now govern together with
PD-REQ-001 through PD-REQ-085. Source restoration does not change the original
S2 observations, pins, hashes, methods, limitations or independent outcomes.

The **original audit execution baseline** was
`bc73884edb1af2fbd7dcbed18cc5af3ef1517b7a`. The **current documentation/governance
reconciliation baseline** is `4ed61bae6327fa62e37d154f8f45f675f36fa86e`.
This revision reconciles the existing record without rerunning the third-party
static inspection. Canonical production PR 6 has not started; canonical PR 20
has not occurred. No production implementation or runtime prototype is authorized.

| Candidate | Gate 1 | Deeper architecture audit | Final S2 disposition |
| --- | --- | --- | --- |
| chiyuan5/VirtualSpace | STOPPED_UNRESOLVED | Stopped at license/ancestry gate | STOPPED_UNRESOLVED |
| Black00Z/Blacks-BlackBox | STOPPED_UNRESOLVED | Stopped at license/ancestry/provenance gate | STOPPED_UNRESOLVED |

**PRIVACY DECOY ANALYSIS:** S2 supplied no audit-cleared engine. Neither result
proves an unresolvable licensing defect or a fatal architecture, so neither is
DISQUALIFIED. Neither is AUDIT_PASS. Another explicit architectural decision is
required under [ADR-0003](../decisions/ADR-0003-redesign-prototype-direction.md):
REDESIGN AGAIN, NARROW SCOPE, or STOP / the applicable explicit feasibility gate.
There is no runtime prototype proposal and no broader candidate search in this PR.

## 1. Scope and authorization

Roadmap PR 8A performs S2 on exactly the two pinned candidates below. Gate 1
requires an attributable grant chain for the complete tree, inherited source,
native material and committed binaries, including source/build provenance and
identifiable notices. Missing evidence stops that candidate before architecture
qualification. A root license, public repository or acknowledgment alone cannot
meet this project's complete-tree gate. These are project admission decisions,
not determinations of infringement or legal impossibility.

Only public Git retrieval, tree/history/text inspection, build-declaration reading,
archive metadata/listings and SHA-256 hashing were performed. Neither engine,
its build scripts, tests, wrappers, annotation processors nor native code ran.
Dependencies were not resolved or downloaded. No engine was installed, built,
executed, integrated or vendored. No third-party source or binary was copied into
Privacy Decoy. No Android implementation, workflow, dependency or requirement changed.

## 2. Original audit project state and current reconciliation

The clean local checkout at `C:\GitHub Projects\Privacy-Decoy` was fast-forwarded
through its existing `origin` to verified main
`bc73884edb1af2fbd7dcbed18cc5af3ef1517b7a`. Existing stashes were left untouched;
no remotes were modified. Research branch: `codex/roadmap-pr-8a-s2-source-audit`.

[ADR-0002](../decisions/ADR-0002-feasibility-stop-gate.md) selected **A — REDESIGN**.
[Roadmap PR 8](pr8-managed-profile-boundary.md), GitHub PR #9, validly **FALSIFIED
S1**: both tenants exposed all seven mandatory parent Build identity fields.
Historical redesign/research Roadmap PR 9 remains the **BLOCKED S1 networking/
revocation follow-up**; this audit does not occupy or renumber that historical
slot. This does not imply that canonical PR 9 — Protected Storage and Key
Boundaries — is blocked or has occurred. **NO PRODUCTION ARCHITECTURE SELECTED**.
Product implementation remains paused. Ordinary protected applications, real
accounts and private data remain prohibited. PR 4/5 results are bounded prior
research, not inherited engine containment evidence.

## 3. Method and evidence vocabulary

The [merged study](../architecture-redesign-study.md) defines these exact labels:

- **UPSTREAM CLAIM:** an upstream statement, not verified security behavior.
- **REPOSITORY OBSERVATION:** content/history/metadata at an identified ref.
- **ANDROID PLATFORM FACT:** an official Android documented property.
- **PRIVACY DECOY ANALYSIS:** consequences under this project's requirements.
- **UNKNOWN — REQUIRES EXPERIMENT:** adequate evidence is absent. Here this also
  marks deferred runtime questions; grant/provenance unknowns first require
  documentary evidence, and this label never authorizes an experiment.

The clones used `--no-checkout` in the external OS temporary workspace
`C:\Users\anton\AppData\Local\Temp\privacy-decoy-pr8a-audit-20260922`, in separate
`VirtualSpace` and `Blacks-BlackBox` directories. Evidence reads used the exact
commit argument, not checked-out files or a substituted head. Git's temporary
per-command `safe.directory` setting accommodated the Desktop sandbox identity;
global configuration and remotes were not changed.

Reproduction uses `git cat-file -t <pin>`, `git show -s --format=fuller <pin>`,
`git ls-tree -r <pin>`, `git log <pin>`, `git show <pin>:<path>` and case-insensitive
`git grep` searches for license/copyright/SPDX/grant/ancestry strings. Full tracked
path lists were screened for LICENSE, NOTICE, COPYING, COPYRIGHT, dependency
metadata, gitlinks, native sources and binary/archive extensions. Header samples
supplemented tree-wide notice searches; this is not a line-by-line originality
determination. Absence claims are limited to these tracked trees and searches.

The two AARs and their nested `classes.jar` files were inspected as ZIP containers
in memory using .NET archive readers, without loading classes or native code.
Hashes cover raw Git blob bytes. No dependency resolver, decompiler or SBOM
generator was executed. Inventory is a declaration/provenance screen within
Gate 1, not the deeper supply-chain or architecture qualification gated on PASS.

## 4. Immutable candidates and citation convention

All candidate observations below were retrieved on **2026-09-22**. Each citation
key binds the candidate, exact ref, and exact path listed in section 15. A row's
paths and evidence keys inherit that immutable ref, including archive-member
paths. An upstream component's own revision is Unknown unless explicitly given.

| Candidate | Verified commit / commit date | Retrieval and context |
| --- | --- | --- |
| [VirtualSpace tree][VS-tree] | `b1ff7988ac598b00b45c22003390ff43396c1c01`; author and committer 2026-05-14 06:24:45 UTC | Public and retrievable; `cat-file` returned commit; observed origin/main at this pin; no containing tag observed; 80 tracked files, 25 reachable commits; tree `0989da8e877d201a54d2cbe8fc0c34edbb93583f`. |
| [Blacks-BlackBox tree][BB-tree] | `40282a7bf4500948cfd598fc67e6e63114b26dd9`; author 2026-05-12 17:48:00 +02:00; committer 17:52:03 +02:00 | Public and retrievable; `cat-file` returned commit; observed origin/main and containing tag `4.7.1`; 846 tracked files, 8 reachable commits; tree `b172ffc01d6912b436d14302c54b8d1db981843f`. |

**REPOSITORY OBSERVATION:** full clones supplied the reachable history just
described. **PRIVACY DECOY ANALYSIS:** a Git hash identifies retrieved content;
it does not authenticate original authorship, complete inherited history or legal
rights. Branch/tag context is a dated observation, never the audit identity.
No newer engine head was substituted or qualified.

## 5. VirtualSpace Gate 1

| Evidence class | Finding and exact evidence | Limitation / gate consequence |
| --- | --- | --- |
| UPSTREAM CLAIM | [VS-readme] License section says MIT; features describe a self-built engine without third-party virtualization libraries. Credits name Android SDK, Material Components and native Linux hooking techniques. | Neither the license name nor the authorship claim supplies a complete attributable grant chain. |
| REPOSITORY OBSERVATION | [VS-tree] has no root or nested LICENSE/NOTICE/COPYING/COPYRIGHT-named artifact. Tree-wide text searches outside the wrapper scripts found the README license wording, but no copyright/SPDX/permission-grant header establishing engine-wide coverage. [VS-native] headers provide no such grant. | Search scope is the pinned tree, not every possible external grant. No claim that a standalone LICENSE filename is intrinsically mandatory. |
| REPOSITORY OBSERVATION | History begins at `82b2f630941e1c3edae9568e4827b0c6e23f579e` on 2026-05-14 with the framework import. The 25 reachable commits are local project history; no inherited engine ref or module grant map was found. License-path history supplied no additional artifact. | A short initial import is not proof of original authorship or of copying. |
| REPOSITORY OBSERVATION | [VS-app], [VS-hook], [VS-build], [VS-settings] declare dependencies/plugins but no complete-tree license or inherited-source ledger. No gitlinks or `.gitmodules` were observed. | Maven coordinates do not establish resolved artifact provenance. |
| PRIVACY DECOY ANALYSIS | No attributable VirtualApp inheritance was established. Local identifiers such as `com.virtual.core.entity.VirtualApp` are not evidence of asLody/VirtualApp ancestry. No exact inherited engine/native revision was identified. | Do not invent a VirtualApp relationship; copied-code ancestry and modifications remain Unknown. |

The native-source inventory is `hook/src/main/cpp/{native_hook.cpp,hook_impl.c,
hook_utils.h,CMakeLists.txt}` [VS-native]. The committed executable archive
identified by the tree is `gradle/wrapper/gradle-wrapper.jar`; no `.so`, `.a`,
`.aar`, APK, APKS, AAB or ZIP payload was observed in the tracked path list.
No source-built reproducibility or wrapper-to-official-artifact identity was
established. The wrapper was not run.

**PRIVACY DECOY ANALYSIS — Gate 1: STOPPED_UNRESOLVED.** An explicit attributable
grant covering this pinned engine/native tree and any inherited material remains
unestablished. Deeper architecture, hidden-interface implementation, security
boundary and native behavior qualification stop here. README package/activity/
service/PLT-GOT claims remain claims; locating native files does not validate them.

## 6. Blacks-BlackBox Gate 1

**REPOSITORY OBSERVATION:** [BB-license] contains Apache-2.0 text. The tracked
tree has no other LICENSE/NOTICE/COPYING/COPYRIGHT-named artifact. Some AOSP AIDL
files retain explicit Apache-2.0 copyright/grant headers, for example
`Bcore/src/main/aidl/android/accounts/IAccountAuthenticator.aidl` [BB-aidl].
This is positive per-file evidence, not complete-tree coverage.

**UPSTREAM CLAIM:** [BB-readme] describes a BlackBox/NewBlackbox fork and credits
ALEX5402/NewBlackbox, asLody/VirtualApp, didi/VirtualAPK, jmpews/Dobby,
hexhacking/xDL, CodingGay/BlackReflection and tiann/FreeReflection. Its Android,
ARM64, split, profile and spoofing statements are compatibility claims. It also
says VPN mode is disabled on Android 14+ until forwarding is complete; this is
not evidence of compliant external-VPN enforcement.

**REPOSITORY OBSERVATION:** the reachable graph has two roots:
`3e809f3e6e2290d05b2b1b316e376125db7bf160` (2026-03-24, initial commit including
the root license) and `fbe897bcb0d8c5a0ac094a1d495de6a0d0a27f5c` (2026-03-27,
initial public fork including both AARs, Dobby archives and xDL source). Merge
`d4dc1e35192faa243fdadd2c5428d0e27356f60f` joins those lines. This graph does not
preserve an exact parent revision from the named original engines. The root name
in [BB-settings] is still NewBlackbox. Later local commits are traceable, but an
upstream-to-import modification diff cannot be reconstructed without the source ref.

| Source/component | Ref / original grant evidence | Retained notices and modification trace | Unresolved gap |
| --- | --- | --- | --- |
| BlackBox / NewBlackbox | Acknowledged in [BB-readme]; import root above; original inherited ref and grant Unknown | `Bcore` tree and NewBlackbox root name; local changes have Git history | No complete original-source-to-fork grant map. Root Apache text cannot settle it. |
| VirtualApp / VirtualAPK | Named credits [BB-readme], links in [BB-credits]; inherited files/ref/original grants Unknown | No attributable per-file mapping found in screening | Acknowledgment is not proof of exact copying, rights, or modification scope. |
| AOSP interfaces | Per-file Apache-2.0 and AOSP notices in [BB-aidl] and other `Bcore/src/main/aidl/android/` files | Positive retained headers; exact AOSP revisions/modifications Unknown | Does not license unrelated engine/native components. |
| Dobby | [BB-dobby] supplies header and two `.a` files; [BB-mk] links them; exact source/version/build ref Unknown | No separate Dobby notice/source implementation/build provenance found in candidate; imported at public-fork root | Current upstream Apache-2.0 text [Dobby-context] is contextual only, not a mapping to these archives. |
| xDL, including LZMA material | `Bcore/src/main/cpp/xdl/` [BB-xdl], credited upstream; inherited ref Unknown | Header samples `xdl.c` and `xdl_lzma.c` begin with blank lines then includes; notice searches did not locate original grant headers there | Current upstream MIT text [xDL-context] does not identify fork revision, local modifications or complete nested-component notices. |
| BlackReflection / compiler | [BB-reflection], [BB-compiler], acknowledged CodingGay source; inherited ref/grant Unknown | Local Java source present; sampled BlackReflection header has no license grant | Original notice, exact revision and modification mapping missing. |
| FreeReflection | `com.github.tiann:FreeReflection:3.2.2` in [BB-core] | Declaration only; no dependency downloaded | Exact resolved bytes, inherited notices, transitive source/build/license provenance Unknown. |
| CatLoading / FloatingView | Exact archives [BB-cat], [BB-float]; package identities below | Public-fork import is attributable; no embedded notice found in AAR or nested JAR listings | Upstream source candidates are leads, not exact build/source matches. |

### Committed AAR and native-archive evidence

**REPOSITORY OBSERVATION:** `app/build.gradle` includes local `*.jar`/`*.aar`
files. Each AAR includes a manifest, resources, `classes.jar`, `R.txt`, ProGuard
metadata and AAR metadata. Neither listing contains a license/NOTICE file or
native library, including the nested JAR entry listings. This says nothing about
the behavior of its bytecode; no class was loaded or decompiled.

| Pinned archive | Bytes | SHA-256 | Metadata / provenance limit |
| --- | ---: | --- | --- |
| `app/libs/catloading-release.aar` [BB-cat] | 27568 | `f4815fdf39538dc916884de19a236a0ed51b0ecf70a2c07d14c4dac15506c46f` | Manifest package `com.roger.catloadinglibrary`, minSdk 19; classes include CatLoadingView; no exact library version/ref or build recipe. |
| `app/libs/floatingview-release.aar` [BB-float] | 59981 | `42b358f6f90edb4c4621df40da2acbf406f7c52e4662819113b9563105ff75a6` | Manifest package `com.imuxuan.floatingview`, minSdk 16; classes include EnFloatingView and FloatingMagnetView; no exact library version/ref or build recipe. |
| `Bcore/src/main/cpp/Dobby/arm64-v8a/libdobby.a` [BB-dobby] | 366194 | `b0128fe5e30b5401859c389703e0515c499e0af5db9d1e4b5e8face672d61cdc` | Prebuilt archive statically linked by [BB-mk]; exact source/build provenance missing. |
| `Bcore/src/main/cpp/Dobby/armeabi-v7a/libdobby.a` [BB-dobby] | 283322 | `d019b4e0015743d7a2d345ef4458537efde678ee36c7b897209870b65ed9dba5` | Same limitation; ARM32 archive is separate from ARM64. |

**PRIVACY DECOY ANALYSIS:** public [CatLoadingView][Cat-context] and
[EnFloatingView][Float-context] are plausible source leads based on names and
package metadata, not established producers of these exact AAR bytes. CatLoadingView's
current README includes a full MIT grant, but no mapping to the candidate's
archive was established. FloatingView's exact applicable grant remains Unknown.
Source-built UI replacements may be plausible, but equivalence, required behavior
and removability have not been reviewed. UI naming does not make packaged
executable code non-security-relevant. The AARs enter the host application build;
exact runtime privileges and indispensable engine-TCB status are deferred.

Dobby is a material native provenance blocker: [BB-mk] explicitly includes it
as a prebuilt static library in `blackbox`. The source recipe for producing these
archive bytes is absent from the screened tree. Public upstream availability is
not proof of matching bytes, license continuity or reproducibility. Replacement
may be possible, but irreplacability is not established. Thus this is an
unresolved opaque native input, not a finding of an irreplaceable opaque TCB.

**PRIVACY DECOY ANALYSIS — Gate 1: STOPPED_UNRESOLVED.** Root Apache-2.0 and
selected retained AOSP notices do not close the inherited engine, xDL/reflection,
Dobby and AAR grant/build gaps. No deeper architecture review or runtime proposal
follows. The evidence does not justify DISQUALIFIED as an unresolvable condition.

## 7. Gate 1 supply-chain inventories

These compact SBOM-style tables are **REPOSITORY OBSERVATION** for declarations
and paths, with **PRIVACY DECOY ANALYSIS** for TCB relevance/status. Unknown means
not established, not absent. Reproducibility was never tested. Licenses below
are Unknown for exact resolved artifacts unless an explicit pinned grant was
observed. No full transitive SBOM or vulnerability clearance is claimed.

### VirtualSpace only

| Component | Type | Source/origin | Version/ref | License | Source available? | Reproducible? | TCB relevance | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| app and hook native/Java tree | Source | Candidate tree | VS pin | Complete grant Unknown | Pinned source yes; ancestry Unknown | Unknown | Prospective engine/host | Gate blocked | [VS-tree], [VS-native] |
| Android application/library plugins | Gradle plugins | Google/plugin repositories | 8.1.4 | Exact artifact Unknown | Not retrieved | Unknown | Build | Declaration only | [VS-build], [VS-settings] |
| AndroidX, Material, Glide | Maven | Google/Maven Central | Coordinates below | Exact artifact Unknown | Not retrieved | Unknown | App/runtime and processor | Unresolved transitives | [VS-app], [VS-hook] |
| Gradle wrapper | JAR + download declaration | Committed JAR; services.gradle.org | Distribution 8.2 | Exact JAR grant/identity not verified | Matching JAR source Unknown | Unknown | Build bootstrap | Not executed | [VS-wrapper] |
| Native module virtualhook | Native build declaration | Local `hook/src/main/cpp` | CMake 3.22.1 requested in hook build | Unknown | C/C++ source present | Unknown | Prospective native engine | No qualified enforcement finding | [VS-hook], [VS-native] |
| CI actions | Workflow declarations | actions/checkout, actions/setup-java, gradle/actions/setup-gradle, actions/upload-artifact | All `@v4`; JDK 17; Gradle 8.2 | Exact revisions/grants Unknown | Not retrieved | Unknown | Build tooling | Moving tags, no execution | [VS-ci] |
| Submodules/local payloads | Tree inventory | Candidate | VS pin | N/A for absent entries | N/A | N/A | Unknown | No gitlinks; no local AAR/SO/A/APK payload observed; wrapper JAR separately listed | [VS-tree] |

Direct coordinates from [VS-app]/[VS-hook]:

```text
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0
androidx.constraintlayout:constraintlayout:2.1.4
androidx.recyclerview:recyclerview:1.3.2
androidx.cardview:cardview:1.0.0
androidx.swiperefreshlayout:swiperefreshlayout:1.1.0
androidx.core:core:1.12.0
com.github.bumptech.glide:glide:4.16.0
com.github.bumptech.glide:compiler:4.16.0 (annotationProcessor)
androidx.annotation:annotation:1.7.0
```

[VS-settings] declares Google, Maven Central and Gradle Plugin Portal for plugins;
Google and Maven Central for dependencies. [VS-wrapper] declares the Gradle 8.2
distribution ZIP without a distribution SHA-256 property. No dependency lockfile
or verification metadata was found in the tracked path screen. No resolved
transitives, native toolchain identity or generated output was audited.

### Blacks-BlackBox only

| Component | Type | Source/origin | Version/ref | License | Source available? | Reproducible? | TCB relevance | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Bcore/host source | Source | Named BlackBox/NewBlackbox ancestry | BB pin; inherited ref Unknown | Root Apache-2.0, complete inheritance Unknown | Pinned tree yes | Unknown | Prospective engine/host | Gate blocked | [BB-tree], [BB-readme], [BB-license] |
| Dobby ARM64 / ARM32 | Two static native archives | Credited jmpews/Dobby | Exact upstream ref Unknown | Archive grant Unknown | Matching archive source Unknown | Unknown | Linked into native engine | Blocking provenance | [BB-dobby], [BB-mk] |
| xDL / LZMA, Hook, JniHook, Utils, hidden_api, IO, BoxCore | Native sources | Local Bcore; xDL credited | Inherited refs Unknown | Complete grants Unknown | Candidate sources yes | Unknown | Native build inputs | Attribution incomplete | [BB-native], [BB-xdl], [BB-mk] |
| CatLoading AAR | Local Java/resource archive | Package metadata; possible CatLoadingView | Exact source/version Unknown | Exact archive grant Unknown | Possible source lead only | Unknown | Packaged host bytecode; irreplaceability Unknown | Blocking provenance | [BB-cat], [BB-app] |
| FloatingView AAR | Local Java/resource archive | Package metadata; possible EnFloatingView | Exact source/version Unknown | Unknown | Possible source lead only | Unknown | Packaged host bytecode; irreplaceability Unknown | Blocking provenance | [BB-float], [BB-app] |
| BlackReflection and compiler | Vendored Java / annotation processor | Credited CodingGay/BlackReflection | Inherited ref Unknown | Unknown | Local source yes | Unknown | Runtime reflection / build generation | Grant mapping unresolved | [BB-reflection], [BB-compiler] |
| FreeReflection | Maven dependency | com.github.tiann | 3.2.2 declared | Exact artifact Unknown | Not retrieved | Unknown | Runtime dependency | No resolved provenance | [BB-core] |
| Android plugins / Kotlin / java-library | Build plugins | Configured plugin repositories | AGP 8.13.2, Kotlin 1.9.23, Gradle built-in java-library | Exact artifacts Unknown | Not retrieved | Unknown | Build | Declarations only | [BB-catalog], [BB-build], [BB-compiler] |
| Other Maven dependencies | Runtime/test/build | Repository declarations | Coordinates below | Exact artifacts Unknown | Not retrieved | Unknown | Runtime/build/tests vary | Transitively unresolved | [BB-app], [BB-core], [BB-catalog], [BB-compiler] |
| Gallery stub | Source and generated embedded APK declaration | Local `blackbox-gallery-stub` | versionCode 1 / versionName 1.0 | Root text, full grant coverage Unknown | Local source yes | Unknown | Additional executable asset if built | No APK generated or run | [BB-gallery], [BB-app] |
| Gradle wrapper / native toolchain | Committed JAR + tool declarations | Gradle distribution; NDK | Gradle 8.13; NDK 29.0.13846066 | Exact artifact grants/identity Unknown | Matching binary source unverified | Unknown | Build bootstrap/toolchain | Not executed | [BB-wrapper], [BB-core] |
| Gitlinks and other local binaries | Tree inventory | Candidate | BB pin | N/A for absent entries | N/A | N/A | Unknown | No gitlinks, `.gitmodules`, committed SO/APK/APKS/AAB/ZIP observed; only wrapper JAR, two AARs and two `.a` files in binary-extension screen | [BB-tree] |

Direct Maven coordinates (including conflicting declared versions, without
guessing Gradle's resolved graph):

```text
androidx.appcompat:appcompat:1.7.0 (app/gallery); 1.2.0 (Bcore)
com.google.android.material:material:1.12.0 (app/gallery); 1.3.0 (Bcore)
androidx.constraintlayout:constraintlayout:2.2.0
androidx.core:core-ktx:1.15.0
androidx.preference:preference-ktx:1.1.1
androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1
androidx.lifecycle:lifecycle-livedata-ktx:2.3.1
androidx.lifecycle:lifecycle-runtime-ktx:2.3.1
androidx.recyclerview:recyclerview:1.2.1
com.gitee.cbfg5210:RVAdapter:0.3.7
com.github.Othershe:CornerLabelView:1.0.0
com.github.nukc.stateview:kotlin:2.2.0
com.github.Ferfalk:SimpleSearchView:0.2.0
com.tbuonomo:dotsindicator:4.2
org.osmdroid:osmdroid-android:6.1.11
com.afollestad.material-dialogs:core:3.3.0
com.afollestad.material-dialogs:input:3.3.0
androidx.work:work-runtime:2.7.1
com.moandjiezana.toml:toml4j:0.7.2
com.github.tiann:FreeReflection:3.2.2
com.google.auto.service:auto-service:1.1.1 (annotationProcessor + compileOnly)
com.squareup:javapoet:1.13.0 (compiler)
junit:junit:4.13.2 (test)
androidx.test.ext:junit:1.2.1 (androidTest)
androidx.test.espresso:espresso-core:3.6.1 (androidTest)
androidx.test:core:1.6.1 (androidTest)
```

[BB-settings] uses JitPack, Aliyun `releases`, `google`, `central`, `gradle-plugin`
and `public` mirrors, Google and Maven Central; plugin resolution also uses the
Gradle Plugin Portal. [BB-core]/[BB-app] load local JAR/AAR globs and [BB-compiler]
loads local JARs. No extra local JAR beyond the wrapper was observed. No lockfile
or dependency verification metadata was found in the path screen. [BB-wrapper]
declares the Gradle 8.13 ZIP without a distribution SHA-256 property. Downloads
implied by wrappers, plugins and Maven resolution were not performed. README's
`tools/smoke_install_launch.sh` reference does not correspond to a tracked file
in this pin; no such script was fetched or run.

## 8. Deeper audit disposition

**PRIVACY DECOY ANALYSIS:** Gate 1 did not pass for either candidate. Therefore
neither receives a lifecycle, package/component, loader, ABI containment,
filesystem, Binder or supply-chain qualification. Build-file reading and archive
listing above identify provenance obligations only. They do not establish actual
enforcement, native support, complete source, or source reproducibility.

For VirtualSpace, [VS-app]/[VS-hook] declare compile/target SDK 34 and minSdk 28;
the README's broader version and PLT/GOT claims remain **UPSTREAM CLAIM**.
For Blacks-BlackBox, [BB-build] confirms compileSdkVersion 35, targetSdkVersion
28 and minSdk 21. [BB-core]/[BB-app] declare arm64-v8a and armeabi-v7a; no x86_64
qualification follows. These are **REPOSITORY OBSERVATION**, not device support.

## 9. Security-boundary and artifact-identity questions

For **each candidate independently**, the following remain
**UNKNOWN — REQUIRES EXPERIMENT**, with prerequisite static review **not reached**:

| Deferred question | VirtualSpace | Blacks-BlackBox |
| --- | --- | --- |
| Actual OS UID/process identity for tenants, management, engine and peers; virtual UID versus kernel UID | Not reviewed after Gate 1 stop | Not reviewed after Gate 1 stop |
| Hostile native reachability of management/peer/host files, Binder, sockets, proc/sys, properties and unhooked services | Not reviewed | Not reviewed |
| Complete/native hook-only/namespace/kernel enforcement; direct-syscall bypass; dlopen/JNI/native threads/helpers | Not reviewed | Not reviewed |
| Policy before provider, Application, native initialization and secondary processes; package/split parsing and classloaders/resources/multidex/dynamic code | Not reviewed | Not reviewed |
| Activities/services/receivers/jobs/alarms/background routes and cached capabilities | Not reviewed | Not reviewed |
| Revocation/engine death for Binder handles, FDs, sockets, native threads, subprocesses/jobs; fail-closed termination | Not reviewed | Not reviewed |
| All traffic identities, resolver/DNS and external-VPN attribution; need for Privacy Decoy VpnService | Not reviewed | Not reviewed |
| Parsing versus runtime signature virtualization versus persistent APK/manifest/DEX/native rewriting or re-signing | Not reviewed | Not reviewed |

**PRIVACY DECOY ANALYSIS:** no shared-UID or direct-syscall architectural fatality
is asserted from filenames or README language. Any need for Privacy Decoy
VpnService remains disqualifying. Any rewriting exception remains subject to
PD-REQ-009; this audit grants none. The host build's signing declarations do not
answer whether imported artifacts are rewritten. No negative architecture finding
is manufactured to strengthen an already sufficient provenance stop.

## 10. Hidden/non-SDK interfaces and API matrix

**REPOSITORY OBSERVATION:** Blacks-BlackBox declares FreeReflection 3.2.2,
local BlackReflection/compiler modules and native `hidden_api.cpp` source
([BB-core], [BB-reflection], [BB-native]). The merged study's
`hiddenApiBypass = '4.3'` observation is confirmed **only as an ext variable** in
[BB-build]: tree-wide exact-name search found no other use. It does not establish
a resolved hiddenApiBypass 4.3 library dependency or effective bypass strategy.

**ANDROID PLATFORM FACT:** Android restricts non-SDK access including reflection
and JNI. Some interfaces are blocked regardless of target SDK; others depend on
maximum target levels, and unsupported interfaces can change. Android 16 is API
36. See the official [non-SDK restrictions][Android-nonSDK], retrieved 2026-09-22.

**PRIVACY DECOY ANALYSIS:** target 28, a bypass declaration, native filenames or
successful launch claims cannot establish a defensible current platform strategy.
VirtualSpace's README Android 9–16/SDK 28–35 wording is inconsistent with the
official Android 16/API 36 mapping. Its declared target 34 is also not a guarantee.
VMRuntime exemptions, private Binder/framework interfaces, signature/target
assumptions, version conditionals and hook behavior were not mapped after Gate 1.

| API | VirtualSpace credible strategy | Blacks-BlackBox credible strategy |
| --- | --- | --- |
| 31 | Unknown; review gated off | Unknown; review gated off |
| 32 | Unknown; review gated off | Unknown; review gated off |
| 33 | Unknown; review gated off | Unknown; review gated off |
| 34 | Unknown; review gated off | Unknown; review gated off |
| 35 | Unknown; review gated off | Unknown; review gated off |
| 36 | Unknown; review gated off | Unknown; review gated off |
| 37 | Unknown; review gated off | Unknown; review gated off |

These are **UNKNOWN — REQUIRES EXPERIMENT** outcomes with prior documentary and
static-review prerequisites. No assertion about API 37 image availability or
support is inferred from the cited platform page. No experiment is authorized.

## 11. Requirement implications

Exact IDs below come from the unchanged [requirements register](../requirements.md).
This table is **PRIVACY DECOY ANALYSIS**; no requirement is marked satisfied.

| Requirement IDs | S2 implication for both candidates |
| --- | --- |
| PD-REQ-002, 006, 007 | Root-free, no ordinary-use ADB, no privileged/guest-root operation remain unverified. |
| PD-REQ-008, 009, 038 | Artifact execution, signing/splits/integrity and narrow rewriting exception rules remain unchanged; source map not reached. |
| PD-REQ-011, 012, 041, 065 | Hostile native/process/UID and management/peer storage isolation remain Unknown. |
| PD-REQ-013, 014, 030, 031 | Binder/provider/package/host-state/native/syscall mediation remains Unknown. |
| PD-REQ-015, 021, 027, 044, 045, 046 | Early lifecycle, mandatory pre-code gate, revocation/death and alternate entry safety remain Unknown. |
| PD-REQ-003, 032, 033, 034, 070 | No Privacy Decoy VPN; attribution, all protocol/origin paths and fail-closed external routing remain mandatory and unproved. |
| PD-REQ-016 | Hidden/private/OEM interface inventory and evidence per supported platform not reached; full provisional API 31–37 strategy Unknown. |
| PD-REQ-060 | Gate 1 identifies blocking license/provenance/SBOM inputs; no complete dependency/vulnerability inventory or replacement boundary is established. |
| PD-REQ-005, 019, 020, 057, 058, 059 | Static evidence is not runtime protection, complete coverage, independent packet/device evidence or specialist security review. |
| PD-REQ-063 | No candidate passes; another explicit architectural/feasibility decision is required before product work. |

The original PD-REQ-001 through PD-REQ-070 implications above are preserved.
The following additional traceability applies the restored PD-REQ-071 through
PD-REQ-085 to the existing evidence; it does not represent new source inspection
or runtime testing. Each row applies to both candidates independently and is
**PRIVACY DECOY ANALYSIS**. Deferred technical coverage remains
**UNKNOWN — REQUIRES EXPERIMENT**, with Gate 1 prerequisites still unresolved.

| Requirement ID | Conservative implication for both candidates |
| --- | --- |
| PD-REQ-071 | Advertising/ad-tech identifier mediation across framework, Play Services, SDK and library paths was not architecture-qualified; scope/reset/rotation and host-real fallback prevention remain Unknown. |
| PD-REQ-072 | Battery/power framework, broadcast, service and native mediation, including temporal coherence, was not architecture-qualified; Unknown. |
| PD-REQ-073 | Descriptive spoofing and README claims cannot establish actual API/ABI/runtime/capability mediation or support; Unknown. |
| PD-REQ-074 | No public-IP or network-persona consistency guarantee is established. External VPN and network-producer behavior remain Unknown; this audit authorizes no exit-IP/geolocation lookup. |
| PD-REQ-075 | Java/native/SDK temporal and cross-path sensor mediation, capability consistency and lifecycle behavior were not reached; Unknown. |
| PD-REQ-076 | Permission-versus-host-data policy mediation was not architecture-qualified; Unknown. Android permission grants do not establish controlled Real disclosure. |
| PD-REQ-077 | Persistent persona/template versioning, isolation, assignment, migration and transactional failure behavior were not assessed; Unknown. |
| PD-REQ-078 | S2 selects or evaluates no Privacy Decoy Developer Mode implementation. Evidence must remain free of raw protected/persona/host values; bounded diagnostics and Ledger separation remain unevidenced. |
| PD-REQ-079 | No physical camera/media mediation is established. Synthetic camera/media remains post-1.0; Unknown mandatory physical paths cannot be treated as safe. |
| PD-REQ-080 | Candidate popularity/features justify no root, patched kernel, routine rewriting/re-signing, convenience guest Android, duplicate VPN/tracker or other prohibited scope drift. S2 grants no exception. |
| PD-REQ-081 | Embedded SDK/library/wrapper/WebView/native/dynamic path qualification was not reached because both candidates stopped at Gate 1; Unknown. |
| PD-REQ-082 | This static audit produced no runtime enforcement/compatibility, persistence, coherence, lifecycle, physical-device or release-equivalent evidence. |
| PD-REQ-083 | Not satisfied: static source/provenance screening is not Privacy Decoy 1.0 release/privacy acceptance evidence. The [acceptance criteria](../acceptance-criteria-1.0.md) remain applicable. |
| PD-REQ-084 | Historical S2 labels are preserved as supplemental redesign research, distinct from canonical production numbering. Neither canonical PR 5 nor PR 20 is inferred satisfied; canonical production PR 6 has not started and PR 20 has not occurred. |
| PD-REQ-085 | The repository, PR and evidence are treated as public: no secrets, credentials, private user data, raw protected/persona/host values or production signing material may be introduced. Artifact-level review does not satisfy project-wide release acceptance. |

**PD-REQ-001 through PD-REQ-085 remain in force and unchanged. No requirement is
marked satisfied by this static audit.** No existing requirement, including
PD-REQ-009, is weakened or relabeled to admit a candidate.

## 12. Candidate outcomes

- **VirtualSpace — STOPPED_UNRESOLVED:** complete pinned-tree grant and attributable
  source/native ancestry not established beyond README MIT wording. Gate 1 stops
  deeper review. No AUDIT_PASS or runtime proposal.
- **Blacks-BlackBox — STOPPED_UNRESOLVED:** root Apache-2.0 and selected AOSP
  headers leave inherited engine/native/reflection grants and exact AAR/Dobby
  source/build provenance unresolved. Gate 1 stops deeper review. No AUDIT_PASS
  or runtime proposal; unresolvability/irreplaceability has not been proven.

**PRIVACY DECOY ANALYSIS:** S2 produced no auditable candidate under its admission
rules. Source visibility is not audit clearance. S1 remains FALSIFIED and Roadmap
PR 9 in the historical redesign/research sequence remains BLOCKED, distinct from
canonical production PR 9. Choosing REDESIGN AGAIN, NARROW SCOPE or STOP is a subsequent
explicit decision, not a decision silently made by this documentation PR.

## 13. Remaining Unknowns and limits

VirtualSpace needs a complete explicit grant and original/inherited component
ledger tied to the pin, including native source and build bootstrap provenance.
Blacks-BlackBox needs exact original engine/reflection/native revisions,
rights/notice mapping and modification history, plus source/toolchain/build
recipes attributable to the four hashed archives. Possible UI-library source
leads and current Dobby/xDL licenses do not close that chain. For both, resolved
transitives, immutable artifact identities, reproducibility, vulnerability posture
and all deferred security questions remain Unknown. Public maintainers were not
contacted; no private grant or unpublished build material was assumed.

Only two engines were candidates. Looking up credited components and AAR source
leads was provenance research, not a new engine search. Those contextual pages
are moving references and cannot establish an inherited revision. Search/header
inspection cannot prove absence of copying. No claim of complete forensic code
ancestry or universal license noncompliance is made.

## 14. Explicitly not authorized and local validation

No third-party build, execution, tests, install, integration, vendoring, dependency
addition, runtime prototype, product resumption, ordinary protected application,
real account/private data, S1 networking follow-up, merge or release is authorized.

Local validation is documentation diff/whitespace/link/ref/scope inspection, not
Android or upstream build/testing. Relative to the reconciliation baseline,
requirements, all `android/` files (including
production manifest and harnesses), and `.github/workflows/` remain unchanged.
The Privacy Decoy wrapper remains Git blob
`b1b8ef56b44f16b14dc800fa8103a6d89abb526f`, SHA-256
`497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7`;
`android/gradlew` remains mode `100755`. Only this evidence, README and ADR-0003
are changed. No upstream clone, binary, cache or research log is committed.
**Exact-head CI pending after publication**; old PR #10 workflows and merge
verdicts are not evidence for the revised head. Under current main's unchanged
docs-only CI policy, one pull_request workflow is expected with `changes` SUCCESS
and validate/containment/network/managed-profile jobs SKIPPED; this is an
expectation, not an observed result. The Desktop task stops once existing PR #10
and its exact new head are confirmed, without waiting for or polling Actions.

## 15. References and provenance

Candidate citations below are immutable; all were inspected on 2026-09-22.
Directory links identify the exact source/module tree for grouped inventory rows.
Absence observations use the whole pinned tree rather than a nonexistent file URL.
Context links explicitly do not identify an inherited ref or grant for candidate
bytes. No third-party source text is reproduced as an implementation artifact.

[VS-tree]: https://github.com/chiyuan5/VirtualSpace/tree/b1ff7988ac598b00b45c22003390ff43396c1c01
[VS-readme]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/README.md
[VS-app]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/app/build.gradle
[VS-hook]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/hook/build.gradle
[VS-build]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/build.gradle
[VS-settings]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/settings.gradle
[VS-native]: https://github.com/chiyuan5/VirtualSpace/tree/b1ff7988ac598b00b45c22003390ff43396c1c01/hook/src/main/cpp
[VS-wrapper]: https://github.com/chiyuan5/VirtualSpace/tree/b1ff7988ac598b00b45c22003390ff43396c1c01/gradle/wrapper
[VS-ci]: https://github.com/chiyuan5/VirtualSpace/blob/b1ff7988ac598b00b45c22003390ff43396c1c01/.github/workflows/build.yml
[BB-tree]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9
[BB-readme]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/README.md
[BB-license]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/LICENSE
[BB-aidl]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/aidl/android/accounts/IAccountAuthenticator.aidl
[BB-credits]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/app/src/main/java/com/onebitmonochrome/blacksbbox/view/setting/SettingFragment.kt
[BB-build]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/build.gradle
[BB-settings]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/settings.gradle
[BB-app]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/app/build.gradle
[BB-core]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/build.gradle
[BB-catalog]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/gradle/libs.versions.toml
[BB-cat]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/app/libs/catloading-release.aar
[BB-float]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/app/libs/floatingview-release.aar
[BB-native]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp
[BB-dobby]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp/Dobby
[BB-xdl]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp/xdl
[BB-mk]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp/Android.mk
[BB-reflection]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/black-reflection
[BB-compiler]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/compiler/build.gradle.kts
[BB-gallery]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/blackbox-gallery-stub/build.gradle
[BB-wrapper]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/gradle/wrapper
[Dobby-context]: https://github.com/jmpews/Dobby/blob/master/LICENSE
[xDL-context]: https://github.com/hexhacking/xDL/blob/master/LICENSE
[Cat-context]: https://github.com/Rogero0o/CatLoadingView
[Float-context]: https://github.com/leotyndale/EnFloatingView
[Android-nonSDK]: https://developer.android.com/guide/app-compatibility/restrictions-non-sdk-interfaces
