# Phase II Stage 2 active-tree cleanup record

**Starting main SHA:** `b594bd829d20cda7d3c8f86a5a5eb25767ba466c`

This maintenance record covers Roadmap Stage 2 only. It makes no privacy-success or mediation claim. Phase I remains **C. NO CREDIBLE BOUNDARY** and AG-1 remains failed.

Removed from the active tree: the app containment/session boundary, network gate/broker, AG-1 authorization/bootstrap runtime, their unit/device tests and generated asset wiring; managed-profile controller/probe and evidence/emulator tools; and containment, network, AG-1 pre-code, and AG-1 dynamic emulator runners. Historical documents and evidence were preserved; Git history retains deleted implementation.

Migrated: four AG-1-named APK fixtures to neutral Java-surface, dynamic-code-surface, early-init/direct-loader, and secondary-DEX fixtures; the admission analyzer/runner/tests to an artifact inventory analyzer that reports hashes, contents, metadata, positive risk indicators, structural validity, and Unknown coverage without safety eligibility.

Retained: the minimal app, `probe-app`, `research-native`, independent packet analyzer, and the unmistakably separate external VPN fixture. The app has no dependency on those probes or the VPN fixture. No dependency was added and no acquisition, APK transformation, signing, Persona runtime, mediation channel, hook, or PD `VpnService` was implemented.

Validation comprises Python analyzer and packet tests, controlled fixture analysis, lint/unit/build tasks for every retained module, release-manifest verification, wrapper and executable-mode checks, path-classifier scenarios, dangling-reference searches, and repository diff/binary hygiene checks.
