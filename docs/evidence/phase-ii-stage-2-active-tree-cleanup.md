# Phase II Stage 2 active-tree cleanup record

**Starting main SHA:** `b594bd829d20cda7d3c8f86a5a5eb25767ba466c`

This maintenance record covers Roadmap Stage 2 only. It makes no privacy-success
or mediation claim. Phase I remains **C. NO CREDIBLE BOUNDARY** and AG-1 remains
failed.

Removed from the active tree: the app containment/session boundary, network
gate/broker, AG-1 authorization/bootstrap runtime, their unit/device tests and
generated asset wiring; managed-profile controller/probe and evidence/emulator
tools; and containment, network, AG-1 pre-code, and AG-1 dynamic emulator
runners. Historical documents and evidence were preserved; Git history retains
deleted implementation.

Migrated: four AG-1-named APK fixtures to neutral Java-surface,
dynamic-code-surface, early-init/direct-loader, and secondary-DEX fixtures; the
admission analyzer/runner/tests to an artifact inventory analyzer. The analyzer
reports hashes, contents, metadata, and positive risk indicators. Structural
`VALID` requires positively established package, signer, version-code, and
base/split identity evidence with no contradiction; proven contradictions are
`INVALID`; readable artifacts with incomplete required evidence are `UNKNOWN`.
Structural status is not safety or privacy coverage. Capability coverage and
split completeness remain Unknown; caller omission or static absence does not
establish N/A, completeness, or safety.

Retained: the minimal app, `probe-app`, `research-native`, independent packet
analyzer, and the unmistakably separate external VPN fixture. The app has no
dependency on those probes or the VPN fixture. No dependency was added and no
acquisition, APK transformation, signing, Persona runtime, mediation channel,
hook, or PD `VpnService` was implemented.

The Desktop replacement applies the complete tree change from superseded,
unmerged PR #36 (`7441da32e5fb3e0e0ee9c807a267795a4fe06d69`) to the starting
main without retaining that Cloud commit in its history. It also replaces
superseded PRs #34 and #35. Review found one further cleanup omission in the
retained probe: its obsolete session-broker handshake was removed, and its
class-visibility target now names the retained `MainActivity` rather than the
deleted `ResearchSession`. The adversarial observations remain; no new execution
harness or mediation claim is introduced.

## Signer diagnosis and repair

Local Windows inspection used the generated
`test-apps/java-surface-fixture/build/outputs/apk/debug/java-surface-fixture-debug.apk`
and official `apksigner verify --print-certs`. Executables were under the ignored
local SDK at `<checkout>/android/build/ag1-toolchain/sdk/build-tools/36.0.0/apksigner.bat`
and the corresponding `37.0.0/apksigner.bat`. Both processes exited `0`, wrote
certificate metadata to stdout, and emitted empty stderr. The relevant observed
lines were, respectively (digest values omitted here):

```text
Signer #1 certificate SHA-256 digest: <64 hexadecimal characters>
V2 Signer: certificate SHA-256 digest: <the same 64 hexadecimal characters>
```

Both tools also printed the standard controlled Android Debug certificate DN,
SHA-1 digest, and MD5 digest. No private key or key material was inspected or
logged. Build-tools 37.0.0 was obtained only for this local diagnosis; it is not
a new repository dependency or CI toolchain pin.

The [failed run #179](https://github.com/innercoder78/Privacy-Decoy/actions/runs/37172492302)
used [runner image ubuntu24/20260927.320](https://github.com/actions/runner-images/blob/ubuntu24/20260927.320/images/ubuntu/Ubuntu2404-Readme.md),
which carried both versions. The old resolver preferred PATH or the lexically
last installed version rather than the workflow's installed 36.0.0 pin. Running
the exact #36 analyzer locally with 37.0.0 on PATH reproduced `UNKNOWN`/exit `2`,
with package, version code, and base identity established but signers missing;
the finding codes matched the failed run. The old log did not record the resolved
executable, so the local reproduction establishes the failure mechanism without
claiming an unavailable historical command trace.

The resolver now requires the official SDK's build-tools 36.0.0 signer even when
37.0.0 is on PATH or installed alongside it. Missing pinned tools fail analysis;
there is no silent version fallback. The same generated APK then produces
`VALID`/exit `0` with all required metadata established and
`RUNTIME_MEDIATION_UNPROVEN` retained. Parsing requires complete 64-digit SHA-256
certificate digests and excludes public-key digests and warning-prefixed text.
Failure, partial digests, or unrecognized output do not establish a signer set.
The command helper still accepts stdout only after exit `0`: the observed bug
did not involve stderr, and arbitrary diagnostics are not promoted to metadata.

## Completed local validation

Validation ran in Codex Desktop on Windows using JDK 17.0.20.1, the committed
Gradle 9.6.0 wrapper, API 37, build-tools 36.0.0, NDK 27.2.12479018, CMake 3.22.1,
Python 3.11.4, and Git Bash. An unchanged temporary copy of the official
`apkanalyzer` toolchain avoided its Windows launcher failing on spaces and the
pre-existing nested SDK layout. Generated tools, APKs, and logs remain untracked.

* `python3 android/tools/test-artifact-analyzer.py`: 14 tests passed, including
  pinned selection over newer PATH/SDK tools, actual certificate-line grammar,
  command exit/stdout/stderr separation, Unknown cases, contradictions, and inventory.
* `python3 android/tools/test-network-evidence.py`: 19 tests passed.
* Python compilation of all five active `android/tools/*.py` files and
  `bash -n .github/scripts/classify-ci-changes.sh android/tools/run-artifact-fixtures.sh`
  passed.
* `bash tools/run-artifact-fixtures.sh`: normal Java, dynamic-code, and native
  probe APKs passed `VALID` assertions; loader/native presence remained detected;
  omitted metadata passed `UNKNOWN`/exit `2`; malformed input passed
  `INVALID`/exit `1`. Coverage and split completeness stayed Unknown throughout.
* The full CI-equivalent `lintDebug`, `testDebugUnitTest`, and `assembleDebug`
  task set passed for `app`, `probe-app`, `research-native`, `external-vpn-fixture`,
  `java-surface-fixture`, `dynamic-code-surface-fixture`, `early-init-loader-fixture`,
  and `secondary-dex-fixture`, together with early-init `lintDynamic` and
  `assembleDynamic`, and `:app:processReleaseMainManifest` (404 actionable tasks).
  Modules without unit-test sources correctly reported NO-SOURCE.
* `python3 tools/verify-release-manifest.py app/build/intermediates/merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml`
  passed on the generated release manifest.
* Classifier checks passed for documentation, app source, analyzer, reusable
  fixture, workflow, classifier, and unknown Android/security-sensitive paths.
  The unknown path selected both current suites.
* Diff whitespace, tracked binary/signing-output hygiene, root layout, retained
  module references, unchanged historical evidence, isolated VPN fixture, wrapper
  blob/SHA-256, and Git executable modes were checked.

## CI and evidence boundary

GitHub Actions remains authoritative for its pinned Linux SDK/NDK/CMake
validation: the baseline job must
lint, unit-test, and build every retained Gradle module and verify the release
manifest, while the artifact-analyzer job must separately build and inspect the
controlled fixtures through the real SDK tools. Merge requires every exact-head
job to succeed. These checks are build and analyzer validation only, not privacy
evidence, and this record does not pre-claim a future workflow result.
