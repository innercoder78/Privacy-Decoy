# Admission-gated controlled-runtime architecture

## 1. Goal

Investigate a PD-owned runtime that preserves strong, evidence-based Protected
Mode by admitting only precisely scoped application versions whose mandatory
execution surface is established as contained. A distinct Experimental &
Unproven Compatibility Mode may serve Unknown-but-not-positively-known-unsafe
applications without misrepresenting them as protected. This is a hypothesis,
not an implemented or proven boundary.

## 2. Protected Mode versus Experimental & Unproven Compatibility Mode

Protected Mode retains PD-REQ-021: mandatory Unknown coverage blocks launch,
there is no silent host fallback, and no known mandatory bypass is tolerated.
App-controlled native code is not Protected merely because functions can be
hooked; each supported native execution class needs explicit evidence.

Experimental mode carries unresolved coverage and no complete privacy or identity
guarantee. It is available only when the app is not Protected-eligible, the
blocker is Unknown/unproven coverage, PD has not positively identified a
mandatory privacy-breaking bypass, and the app is not incompatible. It never
turns Unknown into mediated coverage or counts as Protected evidence.

## 3. Import-time admission pipeline

```text
Imported base/splits
        |
        v
Artifact integrity/signing/split validation
        |
        v
Manifest/component/process/permission inventory
        |
        v
DEX + dynamic-loader analysis
        |
        v
ELF/native-library/ABI/import analysis
        |
        v
SDK/WebView/GMS/background/path inventory
        |
        v
Capability/coverage resolution
        |
        +--> Protected eligible
        |
        +--> Experimental eligible
        |
        +--> Unsupported / known unsafe
        |
        +--> Incompatible
```

Before code runs, the analyzer should investigate, where technically feasible:
package/signing lineage; base/split completeness; components and processes;
early providers/initializers; DEX and executable splits; native `.so` inventory
and ABI; `System.load`/`System.loadLibrary`; custom/dynamic class loaders,
`DexClassLoader`, and `InMemoryDexClassLoader`; subprocess/exec mechanisms;
WebView, Cronet, and SDK paths; GMS/Firebase; integrity/attestation dependencies;
filesystem, network, Binder, property, and native imports; and packer,
encryption, or opaque-executable indicators.

Static analysis can establish presence of risk but cannot prove absence of
dynamically introduced behavior. There is no numerical safety score; admission
is capability-level evidence.

## 4. Runtime executable-code gate

An admitted artifact set is immutable within one admission generation. Updates
or changed splits invalidate admission. Previously unseen DEX, JAR, APK, or
native executable content must be detected where technically possible. Protected
Mode must not use it before admission; opaque or unobservable introduction is
Unknown and disqualifies Protected Mode. Experimental behavior may be broader
but never silently becomes Protected. Newly identified known-unsafe behavior
blocks or terminates the session. This gate is intended, not implemented or proven.

## 5. Controlled-runtime layers

The PD-owned runtime composes immutable artifact/admission identity, package and
component semantics, supervised process slots, pre-code launch gating, virtual
services, policy/persona brokers, storage/path mediation, native observation,
network gating, and coverage diagnostics. Compatibility logic is subordinate to
the execution class and may never restore genuine host authority in Protected Mode.

## 6. Persona and policy

Persona values remain coherent across identity, locale, region, time zone,
display, telephony, network metadata, and supported capabilities. Capability
policy remains Real, Decoy, Empty, or Deny; Real is controlled mediation, never
bypass. Persona sharing does not imply storage, account, package visibility, or
identifier-scope sharing. Experimental mode favors Decoy, Empty, or Deny for
sensitive host data and does not enable Real personal data by default.

## 7. Binder/service mediation

PD must establish all mandatory Binder, service, provider, package, Activity,
job, and background mediation before guest initialization. A source-complete,
PD-owned design inspired by Binderceptor may investigate lower-level Binder,
while NewBlackbox/NEXTVM inform semantics. Protected Mode has no unhandled
pass-through or genuine-host-object fallback.

## 8. Native mediation

ByteHook is a candidate for PLT/imported-function mediation. ShadowHook is a
candidate for inline/native/linker observation, including newly loaded ELF
initialization. These are trusted-runtime interception candidates, not kernel
sandboxes, and are not assumed to intercept direct raw syscalls. Initial
Protected support may exclude app-controlled native machine code until a bounded
execution class has explicit evidence. Direct-syscall containment remains an
unresolved, high-risk research area.

## 9. Storage/package/component model

Imports preserve validated base/split/signing identity without routine rewriting
or re-signing. Each instance receives isolated storage, package visibility, and
component routing. Stub/process-slot mappings cannot be mistaken for independent
kernel UIDs. Providers, `Application`, native initializers, secondary processes,
jobs, alarms, services, broadcasts, and update lifecycle must remain behind the
same admission and policy boundary.

## 10. Networking

Privacy Decoy provides no `VpnService`. The external-VPN default remains
required. Every traffic producer must be attributable, route state independently
evidenced, and unverifiable required routing blocked rather than sent directly.
Experimental mode does not disable this policy; any uncertainty stays disclosed
and unproven.

## 11. Admission outcomes

* **Protected eligible:** every mandatory capability in the evidenced execution
  scope is mediated or fail-closed; no mandatory Unknown remains.
* **Experimental eligible:** only unresolved/Unknown coverage prevents Protected,
  and there is neither a positively known mandatory bypass nor incompatibility.
* **Unsupported / known unsafe:** a mandatory bypass or genuine-state exposure
  outside enforceable mediation is positively identified; hard stop, no checkbox.
* **Incompatible:** the app fundamentally requires Android semantics PD cannot
  provide; hard stop, distinct from privacy coverage.

## 12. Update/re-analysis

Admission is bound to package version, signing lineage, exact base/split set,
content identity, analyzer/runtime versions, and evidence generation. Any
relevant artifact change invalidates admission and Experimental consent. The old
generation may remain stored atomically, but the changed generation cannot
inherit execution authorization.

## 13. Experimental-mode safeguards

Baseline disclosure:

> **Full protection cannot be verified**
>
> Privacy Decoy found capabilities in this app that it cannot completely
> evaluate or mediate. The app may still run, but some real device or personal
> information could potentially be exposed.

Checkbox: **Enable Experimental & Unproven Compatibility Mode for this app**

Supporting explanation: **Privacy Decoy will apply every available protection,
but complete identity and privacy protection is not guaranteed.**

Actions: **Close** and **Continue experimentally**. Never say “Run anyway
protected” or call this execution Protected.

Opt-in is explicit, per app version and exact artifact/split set, never global,
and invalidated by update/re-analysis. A persistent visible Experimental/
Unproven indicator appears while running. Coverage UI, Ledger, and diagnostics
label the class. Experimental runs cannot prove Protected support. Sensitive
host data favors Decoy/Empty/Deny; Real personal-data mediation is off by default.
External-VPN policy continues to the degree evidenced, with uncertainty
disclosed. A later known-unsafe discovery stops the session.

## 14. Known-unsafe/incompatible hard stops

Known unsafe is separate from Unknown. A positively identified mandatory bypass
or path capable of exposing genuine mandatory state outside enforceable mediation
gets no Experimental checkbox and no Protected launch. Fundamentally incompatible
apps likewise get no checkbox; UI explains that required Android capabilities
are unavailable in PD's virtual environment.

## 15. Coverage and diagnostics

Reports identify artifact generation, execution class, capability, path,
classification, evidence scope, and unresolved conditions. Diagnostics are
bounded and redacted, never contain raw host/persona/protected values, and cannot
turn compatibility, absence of detections, or Experimental execution into
evidence of protection.

## 16. Evidence requirements

Evidence must prove mediation precedes providers, `Application`, native
initializers, and target code; test unexpected dynamic code; exercise lifecycle,
process, Binder, storage, network, native, and failure paths; and scope Android,
OEM/device, ABI, app artifacts, runtime, persona, and policy. Production claims
still require release builds, non-rooted physical devices, independent
observation, and independent security review.

## 17. Open questions

Can complete splits be known before launch? Which loader and executable-memory
paths can be reliably gated? Can all entry routes receive pre-code policy? Which
Java-only class is defensible? Can any bounded native class survive raw-syscall,
Binder, filesystem, `/proc`, `/sys`, property, linker, and inherited-descriptor
probes? Can all traffic producers be attributed behind an external VPN? How do
OEM/Android changes invalidate evidence? Until answered, these remain Unknown.

## 18. Reference-project mapping

Mirro and NEXTVM inform analysis; NewBlackbox and NEXTVM controlled-runtime
semantics; Renjana/NEXTVM split/container management; XPrivacyLua coverage;
SpoofMyDevice Persona design; Binderceptor/NewBlackbox/NEXTVM Binder research;
ByteHook PLT mediation; ShadowHook inline/linker observation; VirtualSpace and
Mirro negative lessons. See the [catalog](open-source-reference-catalog.md).
Reference influence does not establish security evidence.

## 19. New feasibility checkpoint

AG-1 is the supplemental, controlled-fixture checkpoint before canonical
production Roadmap PR 6. It must demonstrate honest four-way admission,
pre-code mandatory mediation, executable-code blocking/demotion, distinct
Protected/Experimental behavior, and fail-closed failure without prohibited
privilege or product mechanisms. Passing AG-1 is not production privacy evidence.

## 20. Non-goals and retained invariants

No production root, Magisk/Xposed/LSPosed, custom ROM/patched kernel,
privileged/system install, production ADB, guest root, PD `VpnService`, routine
rewriting/re-signing, host GPS fallback, automatic host-personal-data disclosure,
full guest Android, Play Integrity bypass, or arbitrary hardware-attestation
fabrication. External VPN remains default-required; Persona coherence, package
universe isolation, and evidence-based claims remain mandatory. No third-party
engine is selected wholesale, and arbitrary hostile native containment is not
claimed solved.
