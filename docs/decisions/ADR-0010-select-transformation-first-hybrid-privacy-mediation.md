# ADR-0010: Select transformation-first hybrid privacy mediation for Phase II

**Status:** Accepted

**Date:** 2026-10-04

**Owner decision:** Select the Phase II product contract and architecture direction
described below. This is an architecture and governance decision, not evidence
that an implementation is secure or complete.

## Preserved historical result

Phase I ended **C. NO CREDIBLE BOUNDARY** under its old product contract.
[AG-1 remains FAILED](../evidence/ag1-feasibility-closeout.md), every candidate
outcome and Unknown remains as recorded, and no old result is reclassified as
success. [ADR-0009](ADR-0009-retire-universal-containment-and-authorize-privacy-mediation-redesign.md)
remains the epoch boundary. This ADR supersedes only its unresolved forward
options. It does not revive the old admission-gated runtime or canonical
Roadmap PR 6.

## Decision

Phase II selects a transformation-first hybrid architecture:

```text
installed source app
  -> collect available base APK and installed splits
  -> analyze manifest, DEX, native, SDK, WebView, and dynamic-code surfaces
  -> build a deterministic transformation plan
  -> rewrite package identity and structural references
  -> apply privacy mediation transformations
  -> inject the generic Privacy Decoy Persona runtime
  -> optionally apply narrowly scoped, audited native mediation
  -> rebuild required artifacts
  -> sign with a stable clone identity
  -> install as a separate package, UID, and fresh local state
```

Virtualization engines remain research references, not the primary architecture.
Initial hosts are stock, non-rooted Android 17, API 37 devices. Privacy Decoy may
target API 37. A source application's `targetSdkVersion` is preserved by default;
retargeting requires a specific compatibility rationale and evidence.

The prepared app is a genuinely separate package with its own UID and private
storage. Source private data, including sessions, databases, preferences, cache,
cookies, WebView state, tokens, files, and account state, is never copied.
Backup and restore must be controlled to prevent silent reintroduction. Fresh
local state does not erase remote history or server correlation.

The Persona and **Real / Decoy / Empty / Deny** modes remain central. Real is
explicit, scoped, visible, revocable disclosure, never silent host passthrough.
Values should be coherent and stable until deliberate change or rotation.
Coverage should address region, location, locale, timezone, identifiers,
carrier/SIM metadata, device/build descriptors, package visibility, storage,
clipboard, sensors, and other sensitive surfaces where feasible.

## Mediation boundaries

The permission firewall removes or withholds unnecessary direct genuine-data
authority when PD can safely mediate or broker it. Android permissions and the
sandbox are defense in depth, not proof of complete mediation.

Known DEX call sites should be deterministically transformed to a generic PD
runtime, including bundled SDK bytecode where feasible. A transformation-site
inventory is evidence, not proof about reflection, dynamic code, native code,
Binder, WebView, or other paths. Native mediation is selective and evidence
scoped. ByteHook and ShadowHook remain candidates subject to full review, never
kernel sandboxes. Direct syscalls remain potential bypasses unless separately
established.

Phase II initially uses stock Android WebView and will not maintain a Chromium
fork. WebView, SDK, native, and dynamic-code coverage are established separately.
Dynamically obtained code inherits no claim from transformed bundled DEX. This
does not reinstate universal executable admission.

## Hard VPN invariant and network geography

**Privacy Decoy MUST NEVER operate as an Android VPN.** It must not declare,
implement, start, bind to, depend on, or otherwise use Android `VpnService`; it
must not occupy or interfere with the active VPN slot, appear as the active
system VPN, or use local-VPN interception for any feature. The user remains free
to run an independent VPN. This permanently supersedes ADR-0009's unresolved
suggestion that PD-owned `VpnService` might be reconsidered.

Network geography is **External**. There is no automatic public-IP lookup and no
use of genuine GPS to compare Persona and network. Saving geographic Persona
settings must provide substantially this reminder:

> **Network location reminder**
>
> If you want your internet location to match this Persona, use an external VPN
> or proxy with an exit location that matches the Persona region. Otherwise,
> apps may determine an approximate location from your public IP address that
> does not match the Persona.

PD does not claim to verify VPN country or that IP reveals exact physical
location. A manual check is outside the initial architecture.

## Identity, updates, and configuration

Each clone slot or prepared-app family uses a stable signing identity across
compatible transformed updates. Persona rotation does not rotate it. One global
key should be avoided; secrets never enter Git, logs, CI, evidence, or production
fixtures. Keystore-backed or Keystore-protected custody should be investigated.

Persona and policy values should be runtime configurable where feasible, with
restart or policy-generation changes when caches require them. Package identity,
authorities, manifest permissions/components, transformation passes, injected
runtime, native instrumentation, signing identity, source updates/splits, and
any future WebView implementation are structural and can require rebuild or
reinstall. Source updates invalidate coverage and require reacquisition,
analysis, and transformation. Compatible updates may preserve clone signing and
clone state, but never import source private data.

Only legitimately available installed base and split APKs are acquired. Private
or on-demand assets, including some Play Asset Delivery content, are not copied
from source private storage. An app is limited or Unsupported unless such assets
can be legitimately reacquired.

## Coverage, warnings, and integrity

Coverage states are **Fully mediated, Partially mediated, Unsupported, External,
N/A, and Unknown**. Compatibility is separately **Prepared, Prepared with
compatibility limitations, Rebuild required, Import incomplete, Unsupported,
or Not analyzed**. Execution is not proof of mediation. Unknown is never success;
there is no aggregate privacy or safety score and no silent Real fallback.

The word `blocked` is allowed only with positive evidence that disclosure was
prevented. Post-disclosure detection is an observed exposure or known gap. A
blocked call receives its existing safe policy result rather than waiting on a
modal decision. `Keep blocked` and `Allow real data...` may update subsequent
calls; Real authorization is scoped, visible, and revocable.

PD will not defeat certificate validation, Play Integrity, anti-tamper,
licensing, copy protection, or other app security controls for compatibility.
Changed signing may make an app Unsupported. Initial distribution may be direct
or through GitHub; Google Play policy is a separate investigation.

## Consequences

The [migration register](../phase-ii-requirements-migration.md), [forward
requirements](../phase-ii-requirements.md), [threat model](../phase-ii-threat-model.md),
[acceptance criteria](../phase-ii-acceptance-criteria.md), [roadmap](../phase-ii-roadmap.md),
and [triage plan](../phase-ii-repository-triage.md) govern forward work. No
runtime implementation or dependency is authorized by this documentation PR.
