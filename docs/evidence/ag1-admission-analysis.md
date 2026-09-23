# AG-1A static artifact admission analysis

**Status: AG-1 IN PROGRESS — AG-1A STATIC ADMISSION SLICE**

## Purpose and scope

AG-1A asks only whether controlled APK artifact sets can be inspected before guest
code executes, assigned a stable content-derived generation identity, and given an
honest conservative admission result. It supplies early evidence for PD-REQ-019,
PD-REQ-020, PD-REQ-021, PD-REQ-081, PD-REQ-085, PD-REQ-086, PD-REQ-087,
PD-REQ-088, PD-REQ-090, PD-REQ-093, PD-REQ-094, and PD-REQ-095. It does not claim
that PD-REQ-086 through PD-REQ-095, or any other requirement, is satisfied.

This is supplemental AG-1 feasibility research, not a canonical roadmap PR and
not canonical production PR 6. It adds no guest runtime, Persona, component or
Binder virtualization, runtime interception, VPN enforcement, or production UI.

## Architecture and admission model

`android/tools/ag1-admission-analyzer.py` uses only Python's standard library to
read ZIP entries and DEX bytes. It invokes the pinned Android SDK's `apkanalyzer`
and `apksigner` in read-only modes for manifest and signing identity. Missing SDK
inspection tools are an explicit operational failure. A positively established
structural defect or base/split package, version, or signer mismatch can make a
set `INCOMPATIBLE`. Metadata that the tools cannot establish instead remains
Unknown/unproven, blocks Protected eligibility, and does not by itself prevent
`EXPERIMENTAL_ELIGIBLE` classification.

Each artifact record contains its base/split role, basename, SHA-256, byte size,
statically obtained package/version/split/signer identity, DEX entries, native
libraries, and ABIs. The generation ID hashes normalized schema/analyzer version,
content digest, byte size, and base/split role. Split input ordering is normalized.
Local paths, basenames, clocks, users, hosts, and secrets are excluded, so merely
renaming identical artifact bytes does not create a new admission generation.

The research-domain outcomes are `PROTECTED_ELIGIBLE`,
`EXPERIMENTAL_ELIGIBLE`, `KNOWN_UNSAFE`, and `INCOMPATIBLE`. They are separate
from PD-REQ-020's capability coverage vocabulary. In particular, `Unsupported`
does not mean `KNOWN_UNSAFE`. The pure classifier can represent all four outcomes,
but the real AG-1A analysis path never supplies runtime proof or a defined
mandatory-bypass assertion. Every structurally valid analyzed fixture therefore
retains `RUNTIME_MEDIATION_UNPROVEN` and is at most `EXPERIMENTAL_ELIGIBLE`.

## Positive inventory and limits

The analyzer inventories ordinary multidex, unusually placed DEX, packaged native
libraries and ABIs, and executable-looking DEX/JAR/APK/ELF-style payloads in
unexpected locations. It conservatively searches DEX bytes for class-loader,
native-load, subprocess, WebView, Cronet, GMS, and Firebase references. A positive
match proves only that bytes containing a mechanism/reference are present. Native
code and environment SDK references are Unknown inventory factors, not evidence
that an application is malicious or automatically `KNOWN_UNSAFE`. There is no
numerical or entropy-based risk score.

Static string scanning cannot prove that a mechanism is reachable, cannot prove
its absence, and cannot prove runtime mediation, early enforcement, Binder
coverage, dynamically introduced code blocking, or hostile native containment.
Actual Android split completeness is also Unknown: this slice compares obtainable
package, version, split-name, and signer metadata but does not implement
bundle-aware dependency/configuration completeness. A missing property is never
inferred equal or treated as a mismatch: it receives an explicit Unknown/unproven
finding. Missing metadata cannot qualify for Protected eligibility. App-controlled
native containment and all runtime mediation remain Unknown.

## Controlled fixtures and expected observations

| Fixture | Construction | Expected AG-1A result |
|---|---|---|
| `ag1-java-fixture` | Inert Java-only, no network permission, telemetry, native library, or intentional dynamic loading | `EXPERIMENTAL_ELIGIBLE`; runtime mediation unproven |
| `ag1-dynamic-fixture` | Non-executed `DexClassLoader` and native-load references | `EXPERIMENTAL_ELIGIBLE`; positive dynamic/native-load reference findings |
| Existing `probe-app` | Existing controlled research APK with generated native libraries | `EXPERIMENTAL_ELIGIBLE`; native presence plus any other positive references |
| Temporary malformed bytes | Synthetic non-ZIP unit/integration input | `INCOMPATIBLE` / analysis failure; never Protected |

The integration runner builds these controlled APKs and analyzes their generated
files only. It contains no install or execution step. No APK is committed or
retained as evidence. No guest code was executed, no APK was installed, no real
account or private user data was used, and no third-party hook/runtime dependency
(including ByteHook, ShadowHook, Pine, Binderceptor, or a virtualization engine)
was added.

## Checkpoint conclusion

AG-1A can demonstrate static pre-execution identity and conservative inventory,
subject to the validation recorded with its change. It cannot establish Protected
eligibility. **AG-1 overall is not passed.** Canonical Roadmap PR 5's mandatory
STOP/owner gate has not occurred, canonical production PR 6 remains unauthorized,
and this work does not proceed to AG-1B. Exact-head GitHub CI results are to be
reviewed after publication; this document does not claim advance CI success.
