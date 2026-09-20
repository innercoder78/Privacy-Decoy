# PR 5 protected-network feasibility evidence

## Scope and current validation status

Research only: API 35 / Google APIs / x86_64 / debug. PR 6 remains mandatory.
Starting reviewed commit: `a4437ba4f4b92aa7a25a4fe98e54a268cc07f62e`.
Device observations for this revision are pending its exact-head Actions execution.
Local JDK 17 validation passed lintDebug, testDebugUnitTest, assembleDebug,
assembleDebugAndroidTest and the separate VPN fixture build. These are build/unit
results, not routing evidence. No production protection is claimed.

The original Actions run 35528662036 failed both validation and containment.
The release check used `merged_manifests` instead of AGP's generated
`merged_manifest/release/processReleaseMainManifest/AndroidManifest.xml`.
The locally regenerated release XML contains no networking permissions or research
components. The replacement ElementTree check rejects networking permissions,
services and research controller components. The debug APK also omitted the
default application process from `<processes>`, preventing installation and
causing zero containment tests. The default process is now explicitly declared
after the default INTERNET denial; the broker alone explicitly allows INTERNET.

## Producer and authority inventory

| Producer | Identity and scope |
|---|---|
| Default/management + instrumentation | `com.privacydecoy.app`, application UID, default process; debug INTERNET denied |
| Hostile research service | separate isolated UID/PID per PR 4 session; fixed Java and project-owned native attempts |
| Trusted background broker | `com.privacydecoy.app:networkresearch`, same application UID/package as management; debug INTERNET allowed |
| VPN A | separate `com.privacydecoy.externalvpnfixture` package/UID; dropping TUN, no forwarding |
| VPN B | generated `.replacement` application ID from the same source; real Android provider replacement |
| Host helper | loopback-only TCP 46151 and UDP 46152 servers on the disposable runner |
| Fixed subprocess | PR 4 UID observation only; networking Not exercised |

External per-app VPN applies to the broker's Android application identity, not
future logical protected applications sharing that identity. No hostile caller
receives a Socket, FD, Network, arbitrary destination, port, hostname or payload.

## Device experiment and independent observer

`network-feasibility` is separate from `containment-prototype`; explicit suites
keep the original nine tests independent. The network job requires twelve named
device cases and their PASS records; verified lockdown adds a thirteenth case.
Missing tests, failures, skips and missing required independent evidence fail.
Optional lockdown is Unknown unless both `VpnService.isAlwaysOn()` and
`isLockdownEnabled()` report true after disposable secure-settings setup/reboot.

The official emulator `-tcpdump` facility writes only to ignored build output.
The emulator is terminated before parsing so the capture writer flushes. Cellular
networking is used to avoid a newer emulator's separately implemented Wi-Fi path.
The parser reads at most 128 header bytes per record, skips payload, and retains
only fixed destination/port/protocol/family/count/timing information. It discards
unrelated traffic. Raw captures are never printed/uploaded and are deleted by the
script's cleanup trap. Physical positive controls are required for exclusion,
split routing, allowed explicit selection and VPN-loss OS fallback calibration.
The separate VPN emits bounded fixed categories using PD_PR5_VPN. Full-tunnel
evidence requires each IPv4/native/DNS operation's TUN observation and absence of
fixed-target physical egress. IPv6 has a separate observation status.

DNS uses one fixed wire query for `route-test.invalid` sent directly to controlled
synthetic `198.51.100.53:53`. This tests DNS packet routing, not Android resolver
selection/cache behavior, DNS-over-TLS or DNS-over-HTTPS. No public DNS is queried.
Java, native, and DNS results are recorded separately. Raw UDP is not QUIC evidence.

## Session and socket boundary

Manager-only registration supplies the UID/PID already observed by the PR 4
ResearchSession Binder handshake and links to the isolated lifetime Binder.
Every hostile operation verifies the actual Binder UID/PID, registered session,
epoch, live lifetime, network generation and finite operation. SessionPolicy is
reused. Cross-session, stale, malformed, unknown, revoked and dead-session calls
are tested, including attempted hostile route validation and direct bypass.

The actual broker registry maps opaque IDs to owned Socket objects and checks
the owner on send/close. Route callbacks, generation changes, revocation, lifetime
death and destruction close resources. Device tests check Socket.isClosed-derived
closure counts and failed subsequent sends, rather than only numeric ID removal.
The pure NetworkGate unit model remains a model, not socket-resource evidence.

A separate manager-only retained Socket calibrates Android's old-connection
behavior. It is explicitly outside hostile authority and closed by calibration
cleanup/service destruction. Its physical data is compared with deliberate
closure of policy-owned sockets. This does not revoke arbitrary native sockets.

## Results pending exact-head device evidence

| Question | Current status |
|---|---|
| Management TCP, isolated Java/native TCP/UDP | Implemented; device result pending |
| Broker caller/session/epoch/generation enforcement | Implemented; device result pending |
| Full tunnel, include, exclude and split route | Implemented; independent evidence pending |
| Controlled DNS wire packet | Implemented; independent evidence pending |
| Java/native IPv6 UDP | Implemented; Unknown until observed |
| Explicit physical Network | Tests provider default and explicit allowBypass mode separately; pending |
| Existing connection and policy Socket closure | Implemented; independent evidence pending |
| VPN-loss race | Immediate gated attempt and separate OS fallback calibration; pending; these are not interchangeable evidence |
| Always-on/lockdown | Setup attempted in CI; Unknown until platform state and capture verified |
| Reconnect and genuine provider replacement | Implemented; device result pending |
| Cronet / QUIC | Unknown / Not exercised |
| External-lockdown dependency | Unknown until loss/lockdown evidence; no favorable conclusion assumed |

TRANSPORT_VPN and default-route booleans are observations only: they do not prove
provider trust, destination coverage, logging practices, full tunneling or exit
location. Route authorization comes from trusted test setup, not hostile code or
broker self-attestation; production route validation remains a PR 6 question.
PR 4 containment limitations remain unchanged. No requirement is globally
satisfied by this debug prototype; PD-REQ-033/034/058 require actual observations.
