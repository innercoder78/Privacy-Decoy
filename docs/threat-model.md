# Threat model

**Status:** design baseline, not an implemented or verified boundary

**Assessment date:** 2026-09-19

## Security objective and protected assets

Privacy Decoy intends to run an explicitly selected application without silently
exposing the host's real state. Protected assets are the host device identity and
identifiers, including advertising/ad-tech identifiers; location; telephony,
carrier, network, public-IP/geography, and local-network metadata; package
inventory; accounts and personal datasets; host files/storage; sensor readings;
battery, charging, and power state; host services; other applications' state;
and Privacy Decoy's own
management databases, persona secrets/state, policy state, broker credentials and
capabilities, and coverage/evidence state. Availability matters only where its
loss could trigger unsafe fallback; privacy takes precedence over compatibility.

The security invariant is: an intended guarantee is backed by genuine protection,
redesigned without weakening it, explicitly unsupported, or the project stops.
Running an app, covering common hooks, presenting a coherent persona, or observing
a VPN is not by itself evidence of containment.

## Adversary and capabilities

Every protected app and every byte it loads is untrusted and potentially
adversarial. It may use documented Java/framework APIs, reflection, hidden APIs,
JNI/native code, direct libc calls or syscalls, Binder, ContentProviders,
dynamically loaded code, WebView, subprocesses, multiple processes, background
services/jobs/alarms, cached handles and stale references. It may make malformed
or forged broker requests, exploit races and lifecycle/restart windows, enumerate
packages, observe `/proc`, `/sys`, filesystems and processes, and use network side
channels. It may interact with Play Services or other available host services.
It can compare surfaces, persist observations, and deliberately trigger failure,
death, restart, upgrade, or revocation paths.

Material Play Services, embedded SDK/library and wrapper paths, browser-mediated
WebView behavior, JNI/native libraries, and dynamically loaded code are distinct
attack paths rather than evidence inherited from a direct framework API. An app
may compare descriptive Android/build persona values with actual API/ABI,
kernel/framework, OEM, hardware, and engine capabilities; compare sensor readings
over time and across APIs; correlate local network-persona state with public IP or
geography; treat granted permissions as an opportunity to request host personal
data; and use Developer Mode or other diagnostics as an attempted disclosure
path.

Protected code is not trusted merely because its package was imported, its
signature was checked, or it runs successfully. Collusion among protected apps
and remote servers is assumed possible. Accidental leaks and confused-deputy
behavior are in scope alongside deliberate attack.

## Trust boundaries and data flows

1. **Management UI/processes ↔ protected-app processes:** hostile input and no
   shared authority are assumed. UI convenience must not expose management state.
2. **Protected processes ↔ brokers/services:** every request needs caller,
   instance, persona, policy, capability, freshness, and parameter validation;
   reply handles can become capabilities.
3. **Processes ↔ persistent management databases/persona-policy stores:** only
   minimum TCB components may access these stores. Protected code must never read
   them directly or via a deputy.
4. **Importer ↔ imported package artifacts:** artifacts, splits, signatures,
   manifests, and native payloads are untrusted until validated and remain
   adversarial afterward.
5. **Runtime ↔ host Android framework/services:** Binder, providers, Settings,
   package manager, Play Services, OEM additions, and callbacks can leak host state.
6. **Runtime ↔ Linux kernel:** UID/process/filesystem isolation and direct syscalls
   meet here. Framework interception cannot claim coverage below this boundary.
7. **Device ↔ external VPN:** an external user's VPN is outside Privacy Decoy;
   presence, a UI indicator, or a default route is not proof all relevant traffic
   takes a trusted full tunnel.
8. **Device ↔ network:** packets, DNS, timing, endpoints, and server-held history
   cross the local boundary and may correlate a persona.
9. **Privacy Decoy ↔ optional engine code:** third-party code enters the TCB only
   after provenance/license review and must remain behind a replaceable adapter.

10. **Diagnostics ↔ protected/host/persona state:** Developer Mode is separate
    from the Privacy Access Ledger and MUST NOT emit, record, or retain raw
    protected/persona/host values. It uses only bounded, redacted, allowlisted
    metadata/categories/status evidence, MUST NOT weaken policy or mediation,
    and MUST NOT convert missing evidence into success.

Browser, authentication, sharing, keyboard, autofill, accessibility, screen
capture, notification, backup, device-transfer, and update handoffs are explicit
boundaries—not implied extensions of protection.

## Trusted computing base

The prospective TCB comprises the minimum launcher/supervisor and coverage gate;
policy and persona decision components; authenticated brokers; protected storage
and key handling; the containment adapter and indispensable engine/native pieces;
relevant Android framework/kernel enforcement; and release/update verification.
The ledger is not enforcement. An external VPN is not automatically trusted.

A smaller TCB reduces review surface, privileged confused deputies, dependency
risk, and correlated failure. Protected code must not access management databases,
policy stores, persona secrets, other protected apps' state, broker authentication
secrets, or internal capability/coverage decisions. Broker messages are hostile;
authentication alone does not replace authorization or validation.

## Required fail-closed behavior

Mandatory protection and scoped coverage must be verified and initialized before
*any* protected code, early ContentProvider, native initializer, subprocess, or
background entry runs. `Unknown` mandatory coverage blocks execution. A failed,
dead, stale, unauthenticated, revoked, or overloaded broker blocks the operation;
it never returns a real privacy-sensitive value. Required but unverifiable routing
blocks networking rather than falling back to the physical route. Corrupt persona
state does not silently regenerate. Ledger failure does not open access, and an
empty ledger is not evidence that no access occurred.

A granted Android permission never by itself authorizes host personal-data
disclosure. Unsupported sensor, battery/power, advertising-identifier, SDK, or
library paths block rather than returning host values. Locally reported network
metadata never supports a claim that public IP changed; optional public-IP or
geography lookup is user-authorized, minimized, approximate, nonessential, and
never guesses an Unknown result. Persona-reported Android/build descriptions do
not alter or overstate actual runtime APIs, ABI, or capabilities.

## Assumptions, exclusions, and unknowns

### Production assumptions

The supported host is a non-rooted Android device whose boot chain, OS, kernel,
SELinux policy, and hardware security boundary are not fully compromised. The
exact API/OEM/ABI set is not yet supported; it is a provisional investigation
matrix. Users accurately select policies and control any external VPN.

### Development and test tooling

ADB, debuggers, emulators, instrumentation, packet capture, test certificates,
and controlled probe apps may be used to produce evidence. They are not production
requirements or evidence that release builds on non-rooted physical devices work.
Before the early feasibility boundary is validated, testing uses only safe fixtures
and purpose-built probes—never ordinary protected apps or real private accounts/data.

### Excluded threats

A fully compromised host OS/kernel, malicious device owner with unrestricted
physical/forensic control, hardware implants, and compromise of remote services
are outside the ordinary model. Privacy Decoy cannot delete server-held history
or promise forensic erasure. These exclusions do not excuse weak containment
below the OS/kernel boundary, real-value fallback, or misleading claims.

### Unsupported versus unknown

`Unsupported` is an intentional, reliably blocked capability. `Unknown` means
coverage has not been established and is unsafe for mandatory paths. Hardware-backed
attestation spoofing and Play Integrity bypass are not product promises. Camera,
microphone, radios, credentials/passkeys, biometrics, Keystore/attestation, many
host-service integrations, and background entry may initially be unsupported.

Core unknowns are whether any root-free runtime can contain modern native and
multiprocess apps on API 31–37 without rewriting; what OEM/hidden-API behavior
breaks mediation; whether pre-code validation is achievable; and whether every
traffic path can be attributed and fail closed with an external VPN. In the
**historical repository research sequence**, PR 4 owned containment investigation,
PR 5 owned networking investigation, and Roadmap PR 6 was the mandatory user
STOP decision. The merged historical Roadmap PR 6 **A — REDESIGN** decision
remains valid evidence and retains its label. The restored canonical roadmap
independently requires mandatory STOP gates at Roadmap PR 5 and Roadmap PR 20.
The canonical roadmap and amendment are restored. Canonical Roadmap PR 5 and
Roadmap PR 20 remain mandatory STOP gates. ADR-0007 authorizes only supplemental
AG-1 feasibility work; canonical production PR 6 remains unstarted. AG-1 success
is necessary but not sufficient: its evidence must be reviewed through the
canonical Roadmap PR 5 STOP/owner gate, and explicit project-owner approval is
required before PR 6 may begin. AG-1 failure favors STOP rather than weakening
Protected Mode; see [roadmap reconciliation](roadmap-reconciliation.md).

## Abuse cases and required evidence

| Abuse case | Required disposition/evidence |
|---|---|
| App reads a real value via a second framework/Binder/provider path | Inventory plus positive and bypass probes; block unknown mandatory surface. |
| Native code reads files, properties, `/proc` or `/sys` | ABI-specific native probes and independent host observations. |
| Early provider runs before policy | Cold-start timeline proving coverage gate precedes app code, or no launch. |
| Forged/replayed request or stale handle survives rotation/death | Authentication, authorization, epoch/revocation and race tests. |
| Helper or subprocess escapes attribution | Process/UID and packet correlation across lifecycle. |
| Broker/engine crashes | Fault injection proving denial, bounded recovery, and no real fallback. |
| OEM/API update changes behavior | Release-build matrix revalidation; scope marked Unknown meanwhile. |
| App infers host package/account through Play Services | Virtual-universe and host-service probes, or explicit blocking. |
| App reads advertising identifiers through an SDK/library alternative | Framework, Play Services, SDK/library and reset/scope probes; no persona-implied cross-app sharing. |
| App correlates sensor or battery observations across time/APIs | Temporal, unit/rate/range, lifecycle, framework/native/SDK coherence tests, or explicit denial. |
| App treats a granted permission as access to host personal data | Permission-by-policy tests proving controlled Real or deliberate Decoy/Empty/Deny behavior. |
| Reported Android/build persona contradicts actual runtime | Descriptor-to-API/ABI/capability matrix and truthful UI/claim review. |
| Network persona conflicts with public IP/geography | Cross-surface checks and independently observed, opt-in lookup behavior; Unknown stays Unknown. |
| Developer diagnostics disclose values or overstate coverage | Separation, authorization, bounded-redaction and missing-evidence tests. |

The normative obligations and evidence ownership are in the
[requirements register](requirements.md); candidate feasibility is assessed in
[the engine assessment](engine-assessment.md).

## Admission execution classes (ADR-0007)

The adversarial-app model is unchanged. Protected Mode treats every protected
code path as adversarial and requires the normal mandatory, scoped evidence;
PD-REQ-021 still makes mandatory Unknown coverage launch-blocking. An app is not
“trusted” merely because static analysis found no obvious malicious behavior.
Static analysis can find risk but cannot prove absent dynamic behavior.

Experimental & Unproven Compatibility Mode knowingly carries unresolved
coverage and therefore carries no complete protection guarantee. Experimental
operation does not transform Unknown into Partially or Fully mediated, and it
cannot support a Protected claim. It is available only for Unknown/unproven
coverage when no mandatory bypass is positively known and the app is not
incompatible. Known-unsafe paths and incompatible applications remain blocked.
A later known-unsafe discovery terminates the affected session.
