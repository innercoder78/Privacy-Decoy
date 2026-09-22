# Privacy Decoy 1.0 acceptance criteria

**Gate status:** not satisfied; no release or protection claim authorized

**Coverage vocabulary:** Fully mediated, Partially mediated, Unsupported,
External, N/A, Unknown

This checklist translates PD-REQ-083 and the canonical audit integration into a
reviewable release gate. Each row requires a scoped evidence reference and a
coverage status. `Not yet evidenced` below is a gate-tracking state, not a new
capability-coverage classification: until evidence assigns one of the canonical
coverage states, the capability remains **Unknown**. No research-harness result,
green debug/emulator suite, or application compatibility result completes a row.
Mandatory Unknown blocks execution; Unsupported must be intentional and fail
closed.

| Area | Acceptance item | Required evidence | Initial status |
|---|---|---|---|
| Architecture/platform | Android-only production operation is demonstrated on the claimed non-rooted matrix without ADB, root/guest-root, Magisk/Xposed/LSPosed, custom ROM/kernel, or privileged/system installation. | Release-equivalent device records and architecture review | Unknown — not yet evidenced |
| Architecture/platform | Privacy Decoy contains no `VpnService`, routine rewrite/re-sign path, convenience guest Android, or duplicate tracker/VPN, and the selected architecture has passed its applicable decisions. | Manifest/dependency/artifact review and accepted ADRs | Unknown — no production architecture selected |
| Containment/startup | Every mandatory coverage decision and protection is initialized before application, provider, native initializer, subprocess, helper, or background code. | Instrumented cold-start/entry-route timelines and negative tests | Unknown — not yet evidenced |
| Containment/startup | Protected code is isolated from management authority, peer apps, policy/persona secrets, and coverage decisions across process/UID/Binder/filesystem boundaries. | Adversarial and confused-deputy suite | Unknown — not yet evidenced |
| Persona coherence | Real/Decoy/Empty/Deny policies produce a persistent coherent persona; stable values remain stable and dynamic values remain plausible across restart, reboot, update, and lifecycle. | Cross-surface persistence and temporal test corpus | Unknown — not yet evidenced |
| Persona coherence | Persona/template assignment and migration are explicit and transactional and never imply shared storage, sessions, package visibility, or every identifier. | Assignment, migration, isolation, and fault tests | Unknown — not yet evidenced |
| Identifiers | Device and advertising/ad-tech identifiers are mediated across relevant framework, Play Services, SDK/library, WebView, and native paths with explicit per-app/per-persona scopes. | Path inventory, value vectors, cross-app isolation probes | Unknown — not yet evidenced |
| Identifiers | Reset, limited-advertising, identifier rotation, and persona rotation preserve declared scope and coherence without silent host fallback or regeneration. | Reset/rotation/fault and persistence evidence | Unknown — not yet evidenced |
| Android identity/runtime distinction | UI, coverage, and claims distinguish descriptive Android/build/device persona values from actual API level/behavior, ABI, kernel/framework, OEM, hardware, and engine capabilities. | Claim-to-runtime matrix and adversarial compatibility tests | Unknown — not yet evidenced |
| Location/network persona | Location, locale/region, carrier/SIM, and local-network metadata are coherent and have no silent host fallback. | Cross-API persona and lifecycle probes | Unknown — not yet evidenced |
| Location/network persona | Local spoofing is not claimed to change public IP; optional public-IP/geography lookup is opt-in, minimized, approximate, nonessential, independently observed, and never guesses Unknown geography. | Consent/UX review and positive/negative packet observation | Unknown — not yet evidenced |
| External VPN/network enforcement | Required external VPN defaults ON; loss, replacement, reboot, lockdown, and unverifiable route states block traffic without physical fallback. | Independent packet captures for state transitions | Unknown — prior research is not production evidence |
| External VPN/network enforcement | All producers are attributable and IPv4/IPv6/DNS/TCP/UDP/QUIC/Cronet/Java/native/background/subprocess/helper paths have scoped independent evidence. | UID/process correlation and independent captures | Unknown — not yet evidenced |
| Package universe | Each protected app sees an explicit virtual package universe; persona sharing does not reveal host or peer package inventory. | Query/intent/provider/SDK and collusion probes | Unknown — not yet evidenced |
| Sensors/battery | Supported sensors demonstrate valid units/rates/ranges, temporal and cross-API coherence, capability consistency, and lifecycle/rotation behavior; unsupported paths deny host readings. | Java/framework/native/SDK physical-device probes | Unknown — not yet evidenced |
| Sensors/battery | Battery percentage, charging, plug, and power observations are mutually plausible and consistent across framework, broadcasts, services, and native paths without host fallback. | Temporal cross-path physical-device probes | Unknown — not yet evidenced |
| Personal data/storage | A granted Android permission never alone reveals host contacts, calendar, messages, calls, media, accounts, clipboard, or similar data; every Real path is controlled and absent policy fails closed. | Permission-by-policy matrix and leakage probes | Unknown — not yet evidenced |
| Personal data/storage | Protected and management storage remain isolated across Java/native paths, peers, host shared storage, backup, and transfer; persistent-state inspection corroborates claims. | Adversarial traversal and independent state snapshots | Unknown — not yet evidenced |
| Ledger/Developer Mode | The Ledger is local, bounded, clearable, redacted metadata only; failure cannot open access and silence is not proof of non-access. | Schema/bounds/clear/failure tests and UX review | Unknown — not yet evidenced |
| Ledger/Developer Mode | Developer Mode is separate, bounded, redacted support tooling that does not weaken mediation, disclose unnecessary raw values, or promote missing evidence to success. | Separation, privilege, redaction, and failure tests | Unknown — not yet evidenced |
| Rotation/reset | Edit, reassignment, policy reset, identifier rotation, persona rotation, state clearing, and fresh-instance actions are distinct, previewed, scoped, transactional, and fail closed. | State-transition and fault-injection suite | Unknown — not yet evidenced |
| Rotation/reset | UX does not imply rotation/reset erased remote history, account/session linkage, cookies, or server correlation. | Copy and usability review | Unknown — not yet evidenced |
| Update/import | Only complete validated package-artifact sets enter atomic staging; signature lineage, integrity, splits, native payloads, ABI, and replacement coverage are verified. | Valid/malformed/adversarial fixture matrix | Unknown — not yet evidenced |
| Update/import | Failed import/update preserves old state and protection; no external runtime state is imported and safe update preserves scoped stable values. | Atomicity, rollback, persistence, and state inspection | Unknown — not yet evidenced |
| Lifecycle/revocation | Death, restart, pause/resume, policy change, rotation, update, stale handles, native threads, jobs, alarms, services, and every supported entry route revoke or revalidate safely. | Concurrent lifecycle/race and fault tests | Unknown — not yet evidenced |
| Native/SDK/library paths | JNI/libc/direct syscall, filesystem/proc/sys/property, Binder/provider/service, Play Services, embedded SDK, wrapper, WebView, and dynamic-code paths are inventoried and mediated or blocked. | ABI/API/OEM-specific adversarial path matrix | Unknown — not yet evidenced |
| Physical/release support matrix | Every claimed API/OEM/device/ABI combination passes on physical non-rooted devices using release-equivalent builds; emulator/debug evidence is labeled separately. | Reproducible signed matrix records and artifacts | Unknown — not yet evidenced |
| Compatibility | Supported applications and capabilities pass compatibility tests without weakening enforcement, hiding mandatory gaps, or using real accounts/private user data before authorization. | Scoped positive/negative compatibility suite paired with enforcement results | Unknown — not yet evidenced |
| Independent security review | Independent Android and native specialists review the TCB, engine/provenance, mediation, lifecycle, storage, and networking; required findings are remediated and retested. | Independent report, disposition, and regression evidence | Unknown — review not complete |
| Claims/release readiness | Every privacy/support claim traces to current scoped evidence; mandatory coverage has no Unknown, Unsupported paths fail closed, and no compatibility success hides an unresolved requirement. | Claim-evidence trace and complete requirement matrix | Unknown — gate not satisfied |
| Claims/release readiness | Synthetic camera/media is not claimed for 1.0; physical capture is mediated, denied, or reliably Unsupported, and all required acceptance evidence is approved at a deliberate gate. | Media policy probes, release review, and signed gate decision | Unknown — gate not satisfied |

## Gate decision rule

Privacy Decoy 1.0 cannot claim protection or release readiness unless every
mandatory row has reviewed evidence, all relevant capability paths use the
canonical coverage vocabulary, no mandatory row remains Unknown, every
Unsupported path demonstrably fails closed, and PD-REQ-083 is accepted as a
whole. Evidence and compatibility are both required; neither substitutes for the
other. The missing canonical roadmap source independently keeps implementation
paused even before this acceptance gate can be completed.
