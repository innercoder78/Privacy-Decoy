# Phase II threat model

**Status:** Forward model. The historical [Phase I threat model](threat-model.md)
is preserved and must not be read as proof of Phase II coverage.

## Assets and adversaries

Prepared apps, every bundled SDK, native library, dynamically obtained component,
and remote peer are potentially adversarial. Assets include genuine host data,
Persona coherence, Manager policy and credentials, per-clone keys and state,
other apps' state, source artifacts, transformation provenance, and honest
coverage records. Stock non-rooted Android 17/API 37 and its sandbox are trusted
defense in depth, but not a proof that transformed code is fully mediated.

## Attack surfaces and required analysis

| Surface | Threat and required treatment |
|---|---|
| Framework and Java APIs | Direct and wrapper calls can expose genuine values. Inventory and transform known call sites, then test positive synthetic results and bypasses. |
| Reflection | Names and invocation can evade static matching. Track reflective paths independently; absence of a match is Unknown. |
| Binder, services, providers | Direct transactions, cached interfaces, confused deputies, and OEM services can bypass Java rewriting. Broker or deny only narrowly evidenced interfaces. |
| JNI and native libraries | JNI, PLT/libc calls, inline calls, and library initialization need ABI- and version-specific evidence. A hook is not containment. |
| Direct syscalls | Native code can bypass libc hooks. Unestablished syscall paths remain Unknown or make the capability Unsupported. |
| `/proc`, `/sys`, files, properties | Reads can disclose device, process, mount, network, and build facts. Inventory Java and native paths and deny or mediate claimed surfaces. |
| Package visibility | Queries, intents, providers, shared storage, and side channels can reveal installed packages. Enforce the declared clone-visible universe where claimed. |
| Permissions and capabilities | A transformed clone may retain alternate genuine-data authority. Apply least authority and the permission firewall; permission denial alone is not complete mediation proof. |
| Dynamic executable code | Loaded, downloaded, generated, encrypted, or private code was not covered merely because bundled DEX was transformed. Report it separately without reviving universal admission. |
| Bundled SDKs | SDK bytecode and native components are adversarial and require their own path inventory and evidence. |
| WebView and JavaScript | User agent, geolocation, bridges, JS, network, GPU/WebGL, audio, browser storage, and fingerprinting differ. Stock WebView receives separate coverage; partial controls never imply full browser coverage. |
| Multiple processes and services | Secondary processes, isolated services, providers, and helpers may miss runtime injection or policy updates. Evidence must cover each declared and observed process. |
| Lifecycle and caching | Startup ordering, cached values/handles, process death, restart, rotation, revocation, and policy generations may expose stale Real state. Safe results must precede disclosure in claimed paths. |
| Background work | Jobs, alarms, broadcasts, push, services, and notification actions can run without foreground UX. They require the same scoped policy behavior. |
| Source updates | New base/splits, libraries, SDKs, manifests, signatures, and code invalidate prior assumptions. Reanalysis and retransformation are mandatory. |
| Public-IP geography | Remote services can infer approximate network location. It is External; PD performs no automatic lookup, does not use genuine GPS for comparison, and never uses `VpnService`. |
| Remote correlation | Accounts, cookies, server history, behavior, and network identifiers can correlate a fresh clone. Fresh local state does not erase or control remote history. |

## Security properties and non-properties

Every positively claimed path must return Real, Decoy, Empty, or Deny according
to policy before genuine disclosure. Real is explicit and revocable. No silent
Real fallback is permitted. A warning may say `blocked` only when pre-disclosure
prevention is positively established.

Allowing execution means only that the recorded compatibility decision permits
it. It does not mean all paths are mediated. Partial and Unknown paths stay
visible; Unknown is never success. PD does not claim universal compatibility,
anonymity, undetectability, remote erasure, hardware attestation spoofing, or a
kernel sandbox. It does not bypass app security controls for compatibility.
