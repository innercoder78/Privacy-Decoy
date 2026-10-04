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

Local validation covered Python analyzer and packet tests, script/Python syntax,
release-manifest properties, wrapper and executable modes, classifier scenarios,
dangling-reference searches, and repository diff/binary hygiene. The exact-head
GitHub Actions Android Phase II foundation run linted, tested, and built every
retained Gradle module with the pinned SDK/NDK/CMake toolchain. This CI result is
build validation only, not privacy evidence.
