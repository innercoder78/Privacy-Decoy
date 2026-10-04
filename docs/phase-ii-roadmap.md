# Phase II roadmap

This is the active forward sequence. It does not renumber, amend, or resume the
historical [46-PR roadmap](canonical-roadmap-1.0.md), and its item numbers are
planning stages rather than GitHub pull-request numbers.

| Stage | Deliverable | Gate |
|---:|---|---|
| 1 | Architecture and governance documents | This documentation PR only; no runtime authorization. |
| 2 | Active-tree cleanup and CI reset (**complete**) | Preserve evidence; migrate useful fixtures before deleting obsolete implementation. Completion records maintenance only, not privacy mediation. |
| 3 | Installed base/split acquisition on physical API 37 devices | **STOP:** if exact legitimate artifact inventory and unavailable-asset reporting cannot be established, stop or narrow supported acquisition. |
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
| 16 | Physical API 37 ARM64 multi-device/OEM release-equivalent validation | **STOP:** emulator/debug evidence is insufficient for production claims. |
| 17 | Independent security and supply-chain review | Resolve or scope findings; no opaque security-critical binary enters the TCB. |
| 18 | Owner production/release decision | Explicit GO, narrower scope, REDESIGN, or STOP. No automatic release. |

At every gate, failure means STOP or narrower evidence-backed claims. It never
means weaker privacy guarantees, silent Real fallback, or converting Unknown to
success.
