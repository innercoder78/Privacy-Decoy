# PR 5 protected-network feasibility evidence

## Scope and evidence identity

This is a **network feasibility research prototype**, not VPN enforcement and not
evidence that ordinary applications can be networked safely. The tested source is
the commit containing this document, based on PR 4 commit
`6dfbe9698d366e03a1665be7e95bebccb1945ed2`. PR 6 remains the mandatory early
feasibility/STOP decision.

The intended device target is debug, API 35, x86_64, Google APIs emulator. At
authoring time the device route matrix is **Not exercised** because no Android SDK
or emulator is available locally. Unit/build success must not promote those rows;
exact-head CI and generated filtered capture are required evidence.

## Architecture under test

Privacy Decoy's main manifest remains permissionless and contains neither a
`VpnService` nor a network broker. Its debug overlay alone requests `INTERNET` and
`ACCESS_NETWORK_STATE`, denies `INTERNET` by default with the per-process manifest
facility, explicitly allows it for `:networkresearch`, and keeps the PR 4 service
isolated. `NetworkResearchBrokerService` is trusted: it executes no untrusted code
and exposes fixed operation enums and synthetic destinations/ports. It accepts no
caller URL, hostname, address, port, payload, file descriptor, or `Network`.

`NetworkGate` defaults Require-VPN on. Unknown routes deny, route transitions
increment a generation, stale requests deny, reconnect requires revalidation, and
policy changes discard broker-owned connection identities. This is pure state
machine evidence only. It does not prove Android routing or close sockets unknown
to the broker.

The separate `com.privacydecoy.externalvpnfixture` application implements
`VpnService` solely for the disposable test environment. It is **not Privacy
Decoy**, is not a dependency/release component, never forwards to the Internet,
retains at most 128 sanitized header observations in memory, and clears packet
bytes. Modes are full tunnel, per-app include/exclude, split route, and a
documentation-only IPv6 route. Android user consent is still required.

## Traffic-producer inventory

| Package/process | Role and identity | INTERNET/socket expectation | Route evidence |
|---|---|---|---|
| `com.privacydecoy.app` default | management; app UID/default PID | denied in debug; absent in release | merged-manifest check pending |
| `com.privacydecoy.app:networkresearch` | trusted broker; same package/app UID, distinct PID; expands TCB | debug INTERNET allow; intended originator | device evidence Not exercised |
| isolated PR 4 instance | hostile fixture; isolated UID/PID | no permissions of its own plus debug deny | direct tests Not exercised |
| fixed subprocess | child of isolated fixture | expected to inherit restriction | Not exercised |
| `com.privacydecoy.externalvpnfixture` | separate external VPN app/UID | owns TUN; no forwarding | fixture build pending locally |
| instrumentation/host helper | control and host loopback server | no claimed protected traffic | Not exercised |

Logical labels are not OS isolation. The broker's same-package/app-UID relationship
with management expands the TCB and is not final management separation. Android
per-app VPN policy cannot be presumed to distinguish future logical protected apps
sharing this broker identity.

## Requirement traceability

| Requirement | Evidence/state |
|---|---|
| PD-REQ-003, PD-REQ-070 | main/release has no `VpnService`; separate fixture only; **Preliminary evidence** pending merged-release inspection |
| PD-REQ-032 | producer inventory and fixed correlation ports defined; **Partial** |
| PD-REQ-033 | default-on fail-closed generation gate unit tested; real route verification **Gap** |
| PD-REQ-034 | Java fixed paths scaffolded; native, IPv6, DNS, QUIC and captures **Unknown/Not exercised** |
| PD-REQ-058 | independent emulator pcap is mandatory but **Not exercised**; self-report is insufficient |
| PD-REQ-011/013/014/015/021/027/044/045/057 | PR 4 boundaries preserved; no full lifecycle/native mediation claim; revocation logic **Partial** |

No requirement is globally satisfied by this API 35 prototype.

## Exact tests and independent method

The independent observer must be `emulator -tcpdump <generated build path>` (or an
equivalent official mechanism). Assertions may parse only `198.51.100.0/24`,
`2001:db8::/32`, `10.0.2.2`, fixed ports 46151/46152, family, protocol, count, and
timing. Captures must not be logged, uploaded, or committed. Fixture observation
plus filtered physical/emulator capture are both required for a green full-tunnel
claim.

Pure JVM tests cover: `requireVpnDefaultsOn`, `unknownRouteBlocks`,
`vpnLossInvalidatesGeneration`, `staleNetworkGenerationRejected`,
`brokerOwnedConnectionsClosedOnRevocation`,
`routeRevalidationRequiredAfterReconnect`, and
`explicitOffModeRequiresWarningState`.

| Question/path | Current result |
|---|---|
| isolated Java Socket/DatagramSocket | **Unknown — Not exercised** |
| isolated native TCP/UDP and subprocess | **Gap — not implemented/exercised** |
| broker Java TCP/IPv4 and UDP/IPv4 | fixed operations implemented; route behavior **Not exercised** |
| DNS resolver (`route-test.invalid`) | fixed synthetic name; observations **Not exercised** |
| native TCP/UDP | **Gap**; Java is not native evidence |
| IPv6 | fixture route exists; socket/packet result **Unknown** |
| full tunnel/no physical egress | **Unknown — Not exercised** |
| per-app include/exclude | configuration implemented; expected unsafe exclusion is not evidence; **Not exercised** |
| split route | configuration implemented; routed/non-routed capture **Not exercised** |
| active network/capabilities/link properties | **Not exercised**; `TRANSPORT_VPN` cannot prove trust, no logging, exit, or coverage |
| explicit physical `Network` selection | **Unknown — Not exercised** with/without lockdown |
| connection opened before VPN | **Unknown — Not exercised** |
| VPN-loss race without lockdown | **Unknown — Not exercised** |
| always-on/lockdown loss behavior | **Unknown — Not exercised**; shell setup must verify state and packets |
| genuine replacement/reconnection | replacement **Unknown**; gate requires new generation/revalidation |
| background broker service | service exists; independent origin evidence **Not exercised** |
| Cronet/QUIC | **Unknown/Not exercised**; raw UDP is not QUIC evidence |

## Preserved PR 4 findings

PR 4 preliminarily showed controlled artifact DEX execution in distinct isolated
UID/PIDs, separate instance identities, denied synthetic management storage via
exercised Java/native paths, and working harness session/epoch/revocation checks.
It also showed gaps: no ordinary Application/ContentProvider lifecycle, no
uninstalled package Context/resources, no imported `System.loadLibrary`, and
continued visibility of host Build data, supplied Context implementation classes,
fixed package/services, and selected proc/sys/property surfaces. The isolated
process is not a complete mediated Android application runtime. PR 5 does not
weaken or erase those results.

## Limitations and PR 6 implications

VPN presence is not route proof. Per-app exclusion, split routes, explicit network
selection, pre-existing sockets, loss races, replacement, IPv6, DNS, native paths,
helpers, and QUIC require independent tests. Public connectivity APIs may be
insufficient to establish destination coverage.

This commit therefore cannot yet answer whether no-physical-fallback is possible
without Android's external **Block connections without VPN** enforcement. The
dependency is **Unknown** until no-lockdown and verified-lockdown packet tests run.
If callback detection permits fallback while lockdown blocks it, external
always-on/lockdown becomes a mandatory user/platform dependency and Privacy Decoy
must block when unable to verify it. Privacy Decoy itself still must not implement
`VpnService`. Failure of lockdown to cover the broker identity/configuration is a
PR 6 blocker.
