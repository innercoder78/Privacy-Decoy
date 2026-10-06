# Phase II roadmap

This is the active forward sequence. It does not renumber, amend, or resume the
historical [46-PR roadmap](canonical-roadmap-1.0.md), and its item numbers are
planning stages rather than GitHub pull-request numbers.

Under the explicit owner decision in
[ADR-0011](decisions/ADR-0011-adopt-emulator-first-phase-ii-development.md),
Stages 3–15 are primarily developed and evaluated using Android Studio's Android
17 / API 37 emulator. Successful emulator/debug evidence may satisfy an
applicable research-stage gate and authorize forward research/implementation;
results remain research/development evidence pending Stage 16 physical
qualification. Physical/OEM-specific behavior remains unqualified and production
coverage is not established. Stage-specific STOP conditions still apply, known
failures require STOP or narrowing, and Unknown never becomes success.

Earlier physical validation is encouraged when useful or readily available, but
is not required merely to continue development. If a stage specifically requires
behavior unavailable or not meaningfully evaluable in an emulator, its evidence
record must explicitly identify the limitation and the earlier physical evidence
needed for that research gate. Do not invent emulator success for such surfaces.

| Stage | Deliverable | Gate |
|---:|---|---|
| 1 | Architecture and governance documents | This documentation PR only; no runtime authorization. |
| 2 | Active-tree cleanup and CI reset (**complete**) | Preserve evidence; migrate useful fixtures before deleting obsolete implementation. Completion records maintenance only, not privacy mediation. |
| 3 | Installed base/split acquisition on Android 17 / API 37; primary development evidence may come from the API 37 emulator | **STOP or narrow:** if exact legitimate artifact inventory and unavailable-asset reporting cannot be established in the controlled research environment, or if the mechanism depends on emulator-only privilege/behavior incompatible with the stock-device product contract. Successful emulator evidence may authorize Stage 4 research; physical qualification remains pending until Stage 16. |
| 4 | Deterministic package transformation, rebuild, stable re-signing, and fresh multi-split installation | **STOP:** require valid structural rewrites, separate UID/state, stable clone update identity, and no source-private-state copy. |
| 5 | Authenticated read-only Persona channel and multiprocess policy generations | Stop if prepared code can mutate Manager policy or receive unsafe stale/unauthenticated policy. |
| 6 | Permission firewall and initial Java Persona mediation | Produce exact transformation-site inventory and bypass evidence. |
| 7 | Location, locale, timezone, carrier, SIM, Android ID, advertising ID, and device descriptor mediation | **STOP:** require credible Persona mediation across core tested paths or narrow/stop claims. |
| 8 | Personal data, storage, package visibility, clipboard, and sensor surfaces | Each capability receives independent coverage and compatibility state. |
| 9 | Native property/file mediation experiments | Scope claims by library/API/ABI/version; hooks are not containment. |
| 10 | Narrow Binder/provider/service brokers where justified | Require explicit interface and confused-deputy evidence. |
| 11 | SDK and stock WebView characterization | Keep SDK, browser, and Java coverage distinct. |
| 12 | Source update, rebuild, reset, backup, and clone-state lifecycle | Updates invalidate coverage; compatible signing may retain clone state. |
| 13 | External VPN reminder and network-consistency UX | No automatic IP lookup and no PD `VpnService`. Network geography remains External. |
| 14 | Real-data warning and bounded local ledger | `blocked` requires proven pre-disclosure prevention; do not block calls on modal UX. |
| 15 | Compatibility, signing, GMS, integrity, and PAD matrix | Do not bypass app security; unavailable assets remain explicit. |
| 16 | Mandatory physical stock non-rooted Android 17 / API 37 ARM64 multi-device/OEM release-equivalent qualification | Revalidate all applicable emulator-developed claims on the supported physical matrix. Missing or failed required evidence blocks Stage 17/18 production progression for the affected scope; fix, narrow supported scope, classify Unknown/Unsupported as applicable, or REDESIGN/STOP. Emulator/debug evidence is insufficient for production claims. |
| 17 | Independent security and supply-chain review | Resolve or scope findings; no opaque security-critical binary enters the TCB. |
| 18 | Owner production/release decision | Explicit GO, narrower scope, REDESIGN, or STOP. No automatic release. |

Stage 16 requires multiple supported physical devices/OEMs, release-equivalent
Manager and transformed builds, exact Android/API/device/OEM/build/ABI/source
artifact and `targetSdkVersion` scope, and relevant native, lifecycle, background,
and process behavior. No prohibited root or privileged mechanism may supply the
result. Material physical failures or differences invalidate affected
evidence/coverage; a prior emulator success cannot override them. Stage 17
independent security and supply-chain review remains mandatory after physical
qualification, followed by the Stage 18 owner decision.

Changing these gates does not claim Stage 3 complete or establish a successful
Stage 3 emulator run.

At every gate, failure means STOP or narrower evidence-backed claims. It never
means weaker privacy guarantees, silent Real fallback, or converting Unknown to
success.
