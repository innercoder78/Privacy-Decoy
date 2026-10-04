# Privacy Decoy Phase II forward requirements

These requirements continue permanent numbering after the immutable historical
[PD-REQ-001..095 register](requirements.md). They are obligations, not evidence
of implementation. The [migration register](phase-ii-requirements-migration.md)
states how every historical requirement enters Phase II.

| ID | Normative Phase II requirement |
|---|---|
| **PD-REQ-096** | A prepared application MUST use a separate package, UID, and private state and MUST NOT import the source application's private data. |
| **PD-REQ-097** | Acquisition MUST inventory the exact available base APK and installed splits; unavailable private or dynamic assets MUST be explicit. |
| **PD-REQ-098** | Each transformation plan and result MUST be traceable to source artifact hashes and exact transformation and runtime versions. |
| **PD-REQ-099** | A stable per-clone signing identity MUST survive compatible transformed source updates; Persona rotation MUST NOT rotate signing identity. |
| **PD-REQ-100** | PD MUST remove or withhold unnecessary direct genuine-data permissions wherever it can safely mediate or broker the capability. |
| **PD-REQ-101** | The PD Manager MUST own canonical Persona and policy state and expose it to prepared clones through an authenticated, read-only channel. |
| **PD-REQ-102** | Every setting MUST be classified as runtime-changeable, restart-required, reset-required, or rebuild-required. |
| **PD-REQ-103** | Package, authority, permission, and component structural rewrites MUST be validated before installation. |
| **PD-REQ-104** | A source update or new artifact set MUST invalidate prior coverage and require reacquisition, reanalysis, and retransformation. |
| **PD-REQ-105** | Network geography MUST be External. Persona/network guidance MUST NOT use genuine GPS, claim VPN-country verification, or require automatic public-IP lookup, and PD MUST NOT use `VpnService`. |
| **PD-REQ-106** | PD MUST use the word `blocked` for genuine-data access only when positive evidence establishes prevention before disclosure; later detection MUST be an exposure or known gap. |
| **PD-REQ-107** | Compatibility state and privacy coverage state MUST remain separate; successful execution MUST NOT establish mediation. |
| **PD-REQ-108** | PD MUST NOT defeat application integrity, certificate, licensing, anti-tamper, Play Integrity, copy-protection, or other security controls merely for compatibility. |
| **PD-REQ-109** | Unavailable private, dynamic, or Play Asset Delivery assets MUST produce explicit limitations or Unsupported status unless legitimately reacquired. |
| **PD-REQ-110** | Native, SDK, WebView, and dynamic-code coverage MUST be established separately and MUST NOT be inferred from Java mediation. |
| **PD-REQ-111** | Fresh and reset semantics MUST cover app private state, backup/restore, WebView/private browser state, and account/session material without claiming remote erasure. |
| **PD-REQ-112** | Production coverage MUST record exact Android/API, device, OEM, ABI, source artifacts, and source target SDK context; transformation MUST preserve source `targetSdkVersion` by default, with any change specifically justified and evidenced. |
| **PD-REQ-113** | Privacy Decoy MUST NOT declare, implement, start, bind to, depend on, or otherwise use Android `VpnService`, and MUST NOT occupy or interfere with the device's active VPN slot. |

Coverage uses Fully mediated, Partially mediated, Unsupported, External, N/A,
and Unknown. Unknown never means success. Compatibility uses Prepared, Prepared
with compatibility limitations, Rebuild required, Import incomplete,
Unsupported, and Not analyzed. No single-number score may replace either model.
