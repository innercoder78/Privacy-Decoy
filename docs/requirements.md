# Requirements register — foundation scaffold

This is a small normative starting point, not the comprehensive register or a
threat model. Stable identifiers use `PD-REQ-NNN`, with monotonically allocated
numbers. Never renumber or reuse an ID; retain superseded entries and link their
replacement. “MUST” states a requirement, not evidence of implementation.

PR 2 will populate the full register, traceability, evidence criteria,
Android/OEM/ABI support investigation, and engine-related requirements.
The canonical roadmap and its PR 5 feasibility gate remain authoritative.

| ID | Normative requirement | PR 1 evidence / limitation |
| --- | --- | --- |
| PD-REQ-001 | The product MUST target Android only. | One Android app module; no other platform product. Supported Android/OEM/ABI scope remains open in PR 2. |
| PD-REQ-002 | Ordinary production operation MUST be root-free and MUST NOT require Magisk, Xposed, LSPosed, or a custom ROM. | No such integration is present. Feasibility of the future runtime is unproven. |
| PD-REQ-003 | Privacy Decoy itself MUST NOT use Android VpnService. | No service declaration, VPN code, or VPN dependency. |
| PD-REQ-004 | Production secrets and signing material MUST NOT be committed. | Ignore rules and contributor guidance; no production signing configuration. Review remains necessary. |
| PD-REQ-005 | User-facing and project documentation MUST NOT claim privacy protection before supporting evidence exists. | Launch screen and README explicitly state protection is not implemented or verified. |
| PD-REQ-006 | Ordinary production use MUST NOT depend on ADB. | The shell has no ADB dependency. Developer build/debug tooling is distinct from production use. |

This scaffold neither assesses containment engines nor proves a security boundary.
