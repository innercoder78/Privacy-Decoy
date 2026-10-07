# Privacy Decoy

> **Status: Active development suspended.**
>
> This repository is preserved as the historical research record of the original
> Privacy Decoy project. Active development has moved to the separate
> [Privacy Decoy Virtual Android (PDVA) project](https://github.com/innercoder78/privacy-decoy-virtual-android).

Privacy Decoy explored privacy protection for arbitrary, potentially adversarial Android applications on ordinary stock, non-rooted devices. The difficult part was establishing a defensible boundary across the many routes by which application code can reach genuine host Android authority. Individual hooks, proxies, transformations, or successful compatibility tests did not establish system-wide privacy mediation.

Phase I concluded **C. NO CREDIBLE BOUNDARY** under its product contract, and [AG-1 remains failed](docs/evidence/ag1-feasibility-closeout.md). Phase II selected a transformation-first hybrid redesign in [ADR-0010](docs/decisions/ADR-0010-select-transformation-first-hybrid-privacy-mediation.md), but remained incomplete: Stage 2 maintenance was completed, while Stage 3 and later stages were unfinished on canonical `main`. No production privacy mediation or boundary was proven. The current tree is a research/foundation tree, not a finished privacy product; suspension does not establish that the Phase II design was disproven.

The repository remains useful for research and reference, including its architecture studies, threat models, Persona/privacy concepts, adversarial probes, networking evidence, negative findings, and Android testing/CI infrastructure. Historical failures and Unknowns remain intact. The owner may resume Privacy Decoy through an explicit future decision/ADR and fresh verification of Android/platform assumptions.

PDVA is a separate product lineage, rather than Phase III or the next step in this repository's requirement/ADR sequence. It keeps the privacy goal while investigating a complete interactive virtual Android guest and a deliberate guest/host boundary. It begins its own feasibility analysis. No particular virtualization engine or security boundary is yet proven, and full virtual Android is not claimed to be easier, secure, or feasible.

Read the canonical [project status](docs/project-status.md) and the explicit owner decision in [ADR-0012](docs/decisions/ADR-0012-suspend-active-development-and-establish-pdva-successor.md). Historical context includes the [Phase I comparative synthesis](docs/evidence/post-ag1-comparative-synthesis.md), [Phase II threat model](docs/phase-ii-threat-model.md), and [Stage 2 maintenance record](docs/evidence/phase-ii-stage-2-active-tree-cleanup.md).

The historical Privacy Decoy product contract targeted supported stock Android without root, Magisk, Xposed/LSPosed, a custom ROM, privileged installation, production ADB, a patched kernel, or a guest Android system. Privacy Decoy never implements or uses `VpnService`; the separate `external-vpn-fixture` is test-only and is not a dependency of the app. Network geography is External.

## Retained Android research tree

* `app` — minimal Privacy Decoy application foundation: one launcher activity, no permissions, services, receivers, providers, networking, or runtime mediation.
* `probe-app` and `research-native` — reusable adversarial Java/framework/native probes; they are not privacy evidence.
* `test-apps/*-fixture` — controlled Java, dynamic-code, early-initialization/direct-loader, secondary-DEX, and isolated external-VPN fixtures.
* `tools/artifact-analyzer.py` — static inventory/risk-presence analysis for supplied controlled APKs. Absence of an indicator is not a safety or coverage result; unavailable executable behavior remains Unknown.
* `tools/network-evidence.py` — independent packet-evidence utility retained for generic analysis, not a PD-owned VPN architecture.

## Historical research build and validation

Use JDK 17 and the committed wrapper:

```sh
cd android
./gradlew --no-daemon :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
python3 tools/test-artifact-analyzer.py
bash tools/run-artifact-fixtures.sh
```

CI builds/lints/tests every active app, probe, and fixture module, verifies the release manifest and wrapper, and runs artifact-analyzer tests when relevant. Documentation-only changes do not trigger Android builds. Generated APKs, AABs, shared libraries, and build output must not be committed. No production signing configuration or mediation runtime exists.

See [development guidance](.github/CONTRIBUTING.md), the [Phase II roadmap](docs/phase-ii-roadmap.md), [requirements](docs/phase-ii-requirements.md), [threat model](docs/phase-ii-threat-model.md), and [acceptance criteria](docs/phase-ii-acceptance-criteria.md).
