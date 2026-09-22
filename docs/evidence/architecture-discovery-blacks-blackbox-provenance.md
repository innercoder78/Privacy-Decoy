# Architecture discovery: Blacks-BlackBox provenance closure

Date: 2026-09-22. **Bounded, non-executing documentary research under
[ADR-0006](../decisions/ADR-0006-redesign-again-architecture-discovery.md).**

## 1. Scope and pinned identity

This investigation tests only whether the complete trusted tree of
`Black00Z/Blacks-BlackBox` at immutable commit
[`40282a7bf4500948cfd598fc67e6e63114b26dd9`][BB-tree] has a reconstructable,
legally usable, source-complete provenance chain. The candidate pin, rather than
its moving default branch or tag, is the audit identity. The repository's prior
static inspection verified that the object is a commit with tree
`b172ffc01d6912b436d14302c54b8d1db981843f`, 846 tracked files and eight
reachable commits; tag `4.7.1` then contained it [S2].

**PRIVACY DECOY ANALYSIS:** this is an admission re-investigation, not a deep
architecture audit. It does not test containment or runtime behavior. S1 remains
**FALSIFIED**; its networking/revocation follow-up remains **BLOCKED**;
VirtualSpace remains **STOPPED_UNRESOLVED**. No production architecture is
selected, canonical production PR 6 has not started, and product implementation
remains paused. PD-REQ-001 through PD-REQ-085 are unchanged.

## 2. Research method and evidence rules

The starting Privacy Decoy revision was
`a30923bb7fa04924304631bfc394fb2f670b2ef9`, with a clean worktree. The governing
documents listed in the task and the complete earlier candidate inventory [S2]
were read before this follow-up.

The method was deliberately static:

1. Reconcile the pinned tree, its reachable roots, file inventory, archive hashes,
   build declarations and notice searches already recorded by the prior audit.
2. Follow the candidate's stated credits to the named public projects and look
   for immutable revision, history, license and build correspondence evidence.
3. Require content/history evidence before asserting inheritance. Repository
   names, matching packages, credits and current upstream licenses are leads only.
4. Require exact source revision, toolchain/configuration and artifact
   correspondence before calling a committed binary reproducible.
5. Keep all architecture/security questions outside this documentary gate
   **Unknown**.

Public Git endpoints were unavailable from this research environment (HTTP 403),
and the public web-research endpoint was unavailable (HTTP 401). No authenticated
GitHub access was used. This environmental limit did not turn absent evidence
into a negative fact: it means no new upstream revision could be authenticated
or compared beyond the immutable material already preserved in [S2]. No source
or binary was downloaded to fill that gap. Consequently, this document makes no
new positive source-relationship claim that depends on a moving upstream page.

Evidence labels have these meanings:

- **UPSTREAM CLAIM** — a maintainer or repository statement, not verified
  inheritance, authorship, license coverage or behavior.
- **REPOSITORY OBSERVATION** — content or Git metadata observed at an identified
  candidate revision in the prior pinned-tree inspection.
- **ANDROID/TOOLCHAIN FACT** — a build declaration or official platform/tool
  constraint; it is not proof that a build is reproducible.
- **PRIVACY DECOY ANALYSIS** — the conservative admission consequence.
- **UNKNOWN** — documentary evidence is insufficient. Unknown blocks unsafe
  progression and is not evidence of absence or wrongdoing.

## 3. Source and ancestry map

### 3.1 Reachable candidate history

**REPOSITORY OBSERVATION:** the pinned candidate graph has two roots. Commit
`3e809f3e6e2290d05b2b1b316e376125db7bf160` (2026-03-24) is an initial commit
that includes the root Apache-2.0 text. Commit
`fbe897bcb0d8c5a0ac094a1d495de6a0d0a27f5c` (2026-03-27) is an “initial public
fork” containing the two AARs, two Dobby archives and xDL source. Merge
`d4dc1e35192faa243fdadd2c5428d0e27356f60f` joins them [S2]. The settings root
name remains `NewBlackbox` [BB-settings].

**PRIVACY DECOY ANALYSIS:** these commits establish continuity only inside the
Black00Z repository after its imports. Neither root has a Git parent in
ALEX5402/NewBlackbox, ALEX5402/NewBlackbox-old, FBlackBox/BlackBox,
asLody/VirtualApp or didi/VirtualAPK. An import commit is not an upstream
revision, and later Black00Z diffs cannot reconstruct changes made before import.

### 3.2 Credited lineage

| Area | Evidence actually present | Exact predecessor | Result |
| --- | --- | --- | --- |
| Blacks-BlackBox → NewBlackbox | **UPSTREAM CLAIM:** the README calls the project a fork and credits ALEX5402/NewBlackbox; the build retains the `NewBlackbox` root name [BB-readme] [BB-settings]. | **UNKNOWN:** no upstream commit is recorded in the candidate graph or pinned documentation. | Credited/copy relationship plausible; exact ancestry and pre-import modifications unproved. |
| NewBlackbox → NewBlackbox-old | Repository names are a research lead only. | **UNKNOWN:** no immutable handoff, common Git parent or content-hash map was established. | No continuity claim. |
| NewBlackbox → FBlackBox/BlackBox | **UPSTREAM CLAIM:** candidate credits identify BlackBox/NewBlackbox lineage [BB-readme]. The separately preserved FBlackBox snapshot is `a13734339f85a85b4400926f7142557cb6f97dd9` [FBB-tree]. | **UNKNOWN:** that incomplete snapshot is not shown to be the candidate's import parent. | Context only, not a source chain. |
| Engine → VirtualApp | **UPSTREAM CLAIM:** asLody/VirtualApp is credited [BB-readme]. | **UNKNOWN:** no immutable revision or file-level derivation map. | Similar virtualization concepts/package structures cannot prove copying or grant coverage. |
| Engine → VirtualAPK | **UPSTREAM CLAIM:** didi/VirtualAPK is credited [BB-readme]. | **UNKNOWN:** no immutable revision or inherited-file map. | Credit does not prove materially inherited code. |

**Historical continuity finding:** repository fork metadata was not preserved in
the candidate's reachable Git graph; copied-code history before the two candidate
roots is absent; stated credits are present; structural/name similarity exists.
These are four different evidence classes. Only the third and fourth are
available here, so a Blacks-BlackBox → NewBlackbox → earlier BlackBox/VirtualApp
chain cannot be manufactured.

## 4. License and grant map

| Material | License/notice at relevant candidate revision | Coverage analysis | Classification |
| --- | --- | --- | --- |
| Candidate root | Complete Apache License 2.0 text [BB-license]. | **UNKNOWN:** no copyright statement or provenance ledger shows that the root project's licensor owns or may relicense every earlier import. A root license was present at one root before the separate public-fork root was merged; placement alone does not establish rights to inherited code. | Grant chain unresolved. |
| AOSP-derived AIDL | Individual Android/AOSP files retain Apache-2.0 copyright and grant headers, e.g. `IAccountAuthenticator.aidl` [BB-aidl]. | Positive per-file evidence only; exact AOSP revisions/modifications remain unknown and these headers do not cover unrelated engine code. | Attributable license family; revision correspondence unresolved. |
| NewBlackbox / BlackBox engine | Root license and README credits only. | No relevant immutable predecessor license artifact or file-level copyright/notice preservation map. | Unresolved. |
| VirtualApp / VirtualAPK material | Credit lines only. | No identified inherited files, revisions or relevant-revision license artifacts. | Unresolved. |
| Dobby | Candidate carries a header and archives but no Dobby source or Dobby notice beside them [BB-dobby]. Current upstream's Apache-2.0 file is moving context [Dobby-context]. | A current upstream license cannot be assigned retroactively to unidentified archive bytes. | Unresolved. |
| xDL / bundled LZMA-related source | Source is present; candidate credits xDL. Sampled files contain no applicable grant header [BB-xdl] [S2]. Current upstream's MIT file is moving context [xDL-context]. | Exact imported revision, local diff and nested LZMA notice/grant chain are absent. | Unresolved. |
| BlackReflection / compiler | Local source and compiler module are present, and CodingGay/BlackReflection is credited [BB-reflection] [BB-compiler]. Sampled source has no grant header [S2]. | No exact predecessor revision, relevant-revision license or modification map. | Unresolved. |
| FreeReflection | Maven coordinate `com.github.tiann:FreeReflection:3.2.2` [BB-core]. | A coordinate is attribution, not verification of resolved bytes, transitive contents, relevant tag/commit, license artifact or notice handling. | Attributable lead; exact correspondence unresolved. |
| CatLoading/FloatingView AARs | No LICENSE/NOTICE member in either AAR or nested JAR [S2]. | Candidate root licensing cannot establish third-party archive rights; likely source projects are unpinned leads. | Unresolved. |

This is documentary risk reporting, not legal advice. No incompatible license was
positively established. Equally, the candidate-wide grant chain was not closed.

## 5. Committed-binary inventory

The prior audit hashed raw Git blob bytes without loading classes or native code.
Those pin-bound values are preserved, not recomputed from a moving branch.

| Pinned path | Bytes | SHA-256 | Trusted-path purpose | Provenance state |
| --- | ---: | --- | --- | --- |
| `app/libs/catloading-release.aar` | 27,568 | `f4815fdf39538dc916884de19a236a0ed51b0ecf70a2c07d14c4dac15506c46f` | Host-app loading UI bytecode/resources | **Unresolved** |
| `app/libs/floatingview-release.aar` | 59,981 | `42b358f6f90edb4c4621df40da2acbf406f7c52e4662819113b9563105ff75a6` | Host-app floating UI bytecode/resources | **Unresolved** |
| `Bcore/src/main/cpp/Dobby/arm64-v8a/libdobby.a` | 366,194 | `b0128fe5e30b5401859c389703e0515c499e0af5db9d1e4b5e8face672d61cdc` | Prebuilt ARM64 hook archive linked into `blackbox` | **Unresolved** |
| `Bcore/src/main/cpp/Dobby/armeabi-v7a/libdobby.a` | 283,322 | `d019b4e0015743d7a2d345ef4458537efde678ee36c7b897209870b65ed9dba5` | Prebuilt ARM32 hook archive linked into `blackbox` | **Unresolved** |

The Gradle wrapper JAR is also committed build bootstrap code. No additional
local JAR, `.so`, APK, AAB, APKS or ZIP payload was found in the earlier complete
tracked-path screen [S2]. Absence here means “not found by that screen,” not a
universal claim about downloaded dependencies.

## 6. AAR provenance findings

### `catloading-release.aar`

**REPOSITORY OBSERVATION:** manifest package `com.roger.catloadinglibrary`,
minimum SDK 19, and classes including `CatLoadingView` identify
Rogero0o/CatLoadingView as a plausible source lead [S2] [Cat-context]. The AAR
has no embedded license/notice.

**UNKNOWN:** no exact upstream commit/tag, Gradle/AGP/JDK revision, build variant,
resource inputs, dependency graph, published checksum, signature, reproducible
build statement or byte-for-byte correspondence was found. The moving source
project's MIT statement is not tied to these bytes. The archive therefore cannot
credibly be claimed rebuildable from attributable source.

### `floatingview-release.aar`

**REPOSITORY OBSERVATION:** manifest package `com.imuxuan.floatingview`, minimum
SDK 16, and classes including `EnFloatingView` and `FloatingMagnetView` identify
leotyndale/EnFloatingView as a plausible lead [S2] [Float-context]. The AAR has
no embedded license/notice.

**UNKNOWN:** no exact upstream revision, applicable grant, build configuration,
dependency lock, published checksum, reproducible-build evidence or exact binary
match exists in the reviewed record. It cannot credibly be claimed rebuildable
from attributable source.

**PRIVACY DECOY ANALYSIS:** both archives are unresolved. Their UI role does not
prove that they are outside a future trusted boundary, and this provenance-only
task was not authorized to redesign or remove them.

## 7. Dobby archive provenance findings

**REPOSITORY OBSERVATION:** `Android.mk` declares each `libdobby.a` as a prebuilt
static library and links it into the native `blackbox` library [BB-mk]. Directory
names and the two ABIs distinguish ARM64-v8a and armeabi-v7a archives. Dobby's
hook API header is present, but Dobby implementation source, a submodule pointer,
commit ID and archive build recipe are not [BB-dobby]. Both archives entered at
the public-fork root.

**UNKNOWN:** the candidate does not identify the Dobby commit/tag, submodules,
NDK/Clang version, CMake options, ABI API level, compiler/linker flags, archive
member ordering, stripping/debug settings or deterministic-build procedure.
There is no upstream checksum or reproducibility attestation matching either
hash. The archives were not extracted, linked or compared, because doing so
would not supply the missing documentary chain and execution/build was forbidden.

**PRIVACY DECOY ANALYSIS:** each archive is attributable in name and purpose but
has unresolved exact source/build correspondence. Current upstream availability
and an Apache-2.0 file do not prove the identity or grant of the committed bytes.
These are opaque trusted native inputs for this admission decision. They are not
called irreplaceable; inability to establish evidence is not `DISQUALIFIED`.

## 8. xDL and other native provenance

**REPOSITORY OBSERVATION:** native source exists under
`Bcore/src/main/cpp`, including xDL/LZMA, Hook, JniHook, Utils, hidden-API, IO and
BoxCore areas [BB-native]. xDL is compiled from local source, unlike Dobby's
prebuilt archives [BB-mk].

**UNKNOWN:** no immutable xDL predecessor, import record or modification diff is
recorded. No applicable license header was found in sampled `xdl.c` and
`xdl_lzma.c`; the current upstream MIT page is contextual only. The relevant
LZMA-origin notices and modifications are not mapped. Other native areas lack a
complete original-author/revision/license ledger. Source presence therefore does
not prove grant continuity or reproducibility, and it cannot explain the object
code inside either Dobby archive.

## 9. Reflection provenance

**REPOSITORY OBSERVATION:** the candidate vendors Java source in
`black-reflection`, includes a local compiler/annotation-processor module, and
declares FreeReflection 3.2.2 [BB-reflection] [BB-compiler] [BB-core]. This means
some generated reflection source can be regenerated only if the processor and
its complete build inputs are attributable.

**UPSTREAM CLAIM:** the README credits CodingGay/BlackReflection and
tiann/FreeReflection [BB-readme].

**UNKNOWN:** the vendored BlackReflection source has no identified immutable
upstream revision, license artifact or fork diff. The audit did not establish
which generated files came from which processor revision. FreeReflection's Maven
coordinate does not pin a source commit or verify the resolved artifact and
transitives. The local processor is source-present, but its dependencies
(`auto-service` and JavaPoet) remain repository downloads without dependency
verification. No separately committed opaque generator/plugin binary was found;
however, Gradle/AGP/Kotlin and Maven artifacts are still unverified downloaded
build inputs. Reproduction of trusted reflection source is therefore unresolved.

## 10. Dependency and build provenance

These classifications are admission classifications, not an SBOM or
vulnerability assessment.

| Input | Declared identity | Classification | Reason |
| --- | --- | --- | --- |
| Candidate Java/Kotlin/native source | Candidate pin | **Attributable but exact inherited correspondence unresolved** | Bytes are pinned; predecessor revisions and complete grants are not. |
| AOSP AIDL subset | Per-file Apache notices | **Attributable but exact correspondence unresolved** | Notices survive; exact AOSP revisions/diffs do not. |
| xDL source | Credited hexhacking/xDL | **Attributable but exact correspondence unresolved** | Source present; immutable import, diff and nested notices absent. |
| Dobby archives | Credited jmpews/Dobby; ARM64/ARM32 | **Unresolved** | Exact source/toolchain/flags and artifact match absent. |
| Two local AARs | Package-name source leads | **Unresolved** | Exact revision/build/license/artifact match absent. |
| BlackReflection/compiler | Credited project plus local source | **Attributable but exact correspondence unresolved** | Revision, license and generated-output map absent. |
| FreeReflection | `com.github.tiann:FreeReflection:3.2.2` | **Attributable but exact build correspondence unresolved** | Coordinate only; resolved bytes/source/tag/transitives not verified. |
| AndroidX/Material and app libraries | Exact declared coordinates listed in [S2] | **Attributable but exact build correspondence unresolved** | No lockfile or dependency verification metadata; resolved graph was not downloaded. |
| `auto-service:1.1.1`, `javapoet:1.13.0` | Compiler dependencies | **Attributable but exact build correspondence unresolved** | Required for generation/build; transitives and resolved bytes unverified. |
| Test dependencies | JUnit/AndroidX test coordinates | **Not part of trusted runtime** as declared | Still build/test supply-chain inputs; resolution unverified. |
| Gradle/AGP/Kotlin/JDK | Gradle 8.13; AGP 8.13.2; Kotlin 1.9.23; JDK not pinned in candidate evidence | **Unresolved** | Wrapper lacks distribution SHA-256; plugin/JDK bytes and environment are not locked. |
| Android native toolchain | NDK `29.0.13846066`; make/CMake inputs | **Attributable but exact build correspondence unresolved** | Declared NDK does not explain prebuilt Dobby archives or pin host/tool binaries and flags. |
| Repositories/downloads | Google, Maven Central, Gradle Plugin Portal, JitPack and Aliyun mirrors | **Unresolved** | No complete lock/verification metadata; mirrors increase possible byte sources. |
| Gallery stub | Local source, generated embedded APK if built | **Attributable but exact build correspondence unresolved** | No generated APK is committed; source license inheritance/build reproducibility remains open. |
| Candidate VPN-related code | Candidate source inventory only | **Not assessed for adoption; prohibited from Privacy Decoy** | Presence is not networking evidence and cannot satisfy external-VPN requirements. |

Declared runtime/build coordinates include version conflicts across modules; the
full coordinate list is preserved in [S2]. There are no candidate gitlinks or
`.gitmodules`, so no submodule pin closes any upstream chain. The candidate's
compile SDK 35, target SDK 28, minimum SDK 21, ARM64/ARM32 filters and NDK version
are **ANDROID/TOOLCHAIN FACTS** only; they do not prove API/device compatibility
or reproducibility.

## 11. Historical continuity conclusion

The strongest supportable continuity is:

1. exact Black00Z internal history from the two roots through the candidate pin;
2. candidate statements crediting NewBlackbox, BlackBox, VirtualApp, VirtualAPK,
   BlackReflection, FreeReflection, Dobby and xDL; and
3. selected package/module/source similarities and retained names.

There is no common-parent Git proof, immutable predecessor pin, file-hash map or
import manifest connecting the engine source to ALEX5402/NewBlackbox,
ALEX5402/NewBlackbox-old, FBlackBox/BlackBox, asLody/VirtualApp or
didi/VirtualAPK. Likewise, the native/reflection credits do not identify imported
revisions. Accordingly, this investigation distinguishes internal Git continuity
from copied-code evidence, stated credit and inferred similarity and does not
promote any of them to exact ancestry.

## 12. Blocking unresolved items

All of the following block admission:

1. No exact NewBlackbox/NewBlackbox-old/BlackBox engine predecessor revision or
   pre-import modification map.
2. No file-level VirtualApp/VirtualAPK inheritance map, relevant immutable
   revisions, or grant/notice chain.
3. No evidence that the candidate root Apache-2.0 file establishes licensing
   authority over every inherited engine, native and reflection component.
4. No exact source revision, toolchain, flags, archive recipe or reproducible
   match for either hashed Dobby archive.
5. No exact upstream revision, applicable full notice chain or modification map
   for xDL and its LZMA-related material.
6. No immutable BlackReflection predecessor/license/diff or complete generated
   source provenance; no verified FreeReflection artifact/source correspondence.
7. No exact source revision, build configuration, checksum correspondence,
   reproducible-build evidence or embedded notice for either committed AAR.
8. No locked and verified dependency graph, Gradle distribution, plugin/JDK
   environment, or complete compiler/generator input chain.
9. No evidence placing the AARs outside every prospective trusted boundary. No
   authorized architecture analysis established that they are safely removable.
10. No complete candidate-wide copyright/attribution/notice ledger.

No blocker above is described as necessarily uncorrectable. Therefore the
evidence does not support `DISQUALIFIED`.

## 13. Requirement implications

**PRIVACY DECOY ANALYSIS:** PD-REQ-060's source, license, dependency, native
binary, SBOM and replacement-boundary obligations remain open. PD-REQ-085 is
preserved: this public document contains no secret, credential, private user
data, raw protected/persona/host value, signing material or local private path.
PD-REQ-001 through PD-REQ-085 remain unchanged and none is satisfied by this
research.

All architecture questions deliberately remain **UNKNOWN**: UID/process and
management isolation; native/direct-syscall containment; Binder, provider and
filesystem mediation; package and lifecycle semantics; early initialization;
revocation; networking and external-VPN behavior; persona, sensor, battery and
personal-data mediation; API 31–37/OEM/ABI behavior; and release/physical-device
compatibility. The result neither implies runtime security nor compatibility.

Privacy Decoy **MUST NOT** implement Android `VpnService`. Candidate VPN-related
source was not run or evaluated as a solution. Its presence does not satisfy any
networking requirement. Root, guest root, privileged/system installation,
production ADB, Magisk, Xposed, LSPosed, custom ROMs and patched kernels remain
prohibited. Routine APK rewriting/re-signing receives no exception.

## 14. Explicit non-authorization

This result authorizes no build, execution, installation, dependency resolution,
integration, vendoring, import, prototype, runtime test, deep architecture audit,
product work, canonical production PR 6, ordinary protected application, private
data/account use, release or security/privacy claim. No candidate APK, AAR,
archive, dependency or source was added to Privacy Decoy. No Android emulator or
candidate build suite was run.

## 15. Final admission result

## `STOPPED_UNRESOLVED`

The hypothesis is not established. Material opaque trusted binaries and missing
immutable ancestry, grant, notice, generator and build-correspondence evidence
remain. Blacks-BlackBox therefore remains **STOPPED_UNRESOLVED** and may not
proceed to a deep static architecture audit. No execution follows.

This is not `DISQUALIFIED`: the investigation found insufficient evidence, not a
proven incompatible grant, irreplaceable opaque component, confirmed mandatory
source absence or other demonstrated uncorrectable defect.

## 16. Immutable and public references

Immutable candidate references were verified in the prior pin-bound inspection
and are reused here. Moving upstream references are explicitly contextual and do
not prove correspondence to candidate bytes.

[S2]: pr8a-engine-source-provenance-audit.md
[BB-tree]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9
[BB-readme]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/README.md
[BB-license]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/LICENSE
[BB-settings]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/settings.gradle
[BB-core]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/build.gradle
[BB-aidl]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/aidl/android/accounts/IAccountAuthenticator.aidl
[BB-native]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp
[BB-dobby]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp/Dobby
[BB-xdl]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp/xdl
[BB-mk]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/Bcore/src/main/cpp/Android.mk
[BB-reflection]: https://github.com/Black00Z/Blacks-BlackBox/tree/40282a7bf4500948cfd598fc67e6e63114b26dd9/black-reflection
[BB-compiler]: https://github.com/Black00Z/Blacks-BlackBox/blob/40282a7bf4500948cfd598fc67e6e63114b26dd9/compiler/build.gradle.kts
[FBB-tree]: https://github.com/FBlackBox/BlackBox/tree/a13734339f85a85b4400926f7142557cb6f97dd9
[Dobby-context]: https://github.com/jmpews/Dobby/blob/master/LICENSE
[xDL-context]: https://github.com/hexhacking/xDL/blob/master/LICENSE
[Cat-context]: https://github.com/Rogero0o/CatLoadingView
[Float-context]: https://github.com/leotyndale/EnFloatingView
