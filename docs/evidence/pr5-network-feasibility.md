# PR 5 protected-network feasibility evidence

## Scope and current validation status

Research only: API 35 / Google APIs / x86_64 / debug. PR 6 remains mandatory.

**Completed revised implementation evidence:**
[Actions run 35564605873](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35564605873)
on `b87b2682ba6085bef2edab4e99aea74568fdb6d7` passed all three jobs:
validation, **9/9 containment tests**, and **13/13 network tests with independent
packet analysis** (12 mandatory plus verified lockdown). Local lint, all 12 JVM
tests, debug/instrumentation/fixture builds, generated release-manifest validation,
seven parser regression tests and five unsafe-manifest rejection checks passed.
No pinned toolchain version changed. The only local lint warning suggests a newer
Gradle; the requested 9.6.0 pin is retained.

The implementation run reproduced physical egress during VPN loss: two packets
on gated TCP port 46151 while the operation returned `timeout`. The independent
OS fallback calibration produced four packets on port 46153 and returned success.
Verified lockdown produced no fixed physical packets after confirmed VPN loss.
This is evidence of an external enforcement dependency, not production protection.
The documentation-only follow-up records this completed implementation run;
its own head's CI status is reported in PR #5 rather than inferred here.

### Earlier runs and corrections

Starting reviewed commit: `a4437ba4f4b92aa7a25a4fe98e54a268cc07f62e`.
The resumed published head was `e9e646056b4757d1319e93d2f1b627c10157c952`.
[Actions run 35533011105](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35533011105)
passed validation and containment; all 13 network device cases emitted PASS, but
the independent analyzer failed its physical positive control. Those device
observations alone do not establish no-egress evidence. Corrected capture results
are recorded in [run 35547581742](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35547581742)
on `f3c09f1`: Wi-Fi capture saw 49 fixed packets and physical positive controls.
Full tunnel/include had no fixed physical egress in that run and both Java/native
IPv6 probes were seen in TUN. One socket-closure check failed, and lockdown's
all-target assertion failed. The parallel PR run failed an earlier full-tunnel
all-target assertion. Because platform resolver and controlled DNS used the same
address, those failures are not attributed to the broker without further evidence.
The immediate gated VPN-loss attempt returned success in the push run, with
physical TCP captured in the combined race/calibration interval: a Known Gap,
not production protection. The follow-up separates physical calibration onto
port 46153 and keeps gated Java TCP on 46151 for independent race attribution.
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
| Host helper | loopback-only TCP 46151/46153 and UDP 46152 servers on the disposable runner |
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

Two independent QEMU packet filters write only to ignored build output:
`-tcpdump` attaches to `mynet`, and an explicit `filter-dump` attaches to
`virtio-wifi`. `-feature -WiFiPacketStream` selects the legacy Wi-Fi backend.
Capturing only `mynet` missed the working Wi-Fi positive control. The official
[emulator netdev wiring](https://android.googlesource.com/platform/external/qemu/+/refs/heads/emu-master-dev/android-qemu2-glue/main.cpp)
and [QEMU capture writer](https://android.googlesource.com/platform/external/qemu/+/refs/heads/emu-master-dev/net/dump.c)
show separate interfaces and virtual-clock packet timestamps. Each test records
both files' byte offsets before instrumentation and after a bounded trailing
wait; matching records are selected by overlap with these intervals, not by
comparing virtual timestamps with host wall time. The writer uses unbuffered
`writev`, and the emulator terminates before final parsing. Missing capture
headers, malformed/truncated records, missing named cases and missing physical
positive controls fail. Synthetic regression checks cover Wi-Fi-only evidence,
clock independence, conservative interval overlap and parser rejection.
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
selection/cache behavior, DNS-over-TLS or DNS-over-HTTPS. The VPN advertises
`198.51.100.54` to the platform resolver instead; its packets are reported separately
as `PCAP_PLATFORM_DNS`, never counted as broker DNS evidence. This avoids conflating
background resolver traffic with the fixed `.53` operation. No public DNS is queried.
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
death and destruction close resources. Each Binder dispatch also samples active
Network/capabilities/link properties under the broker identity and invalidates
changed state synchronously, so queued callback delivery cannot postpone closure
of an already-observable route change. Observation and socket I/O are still not
atomic; this does not prove absence of a VPN-loss race. Device tests check Socket.isClosed-derived
closure counts and failed subsequent sends, rather than only numeric ID removal.
The pure NetworkGate unit model remains a model, not socket-resource evidence.

A separate manager-only retained Socket calibrates Android's old-connection
behavior. It is explicitly outside hostile authority and closed by calibration
cleanup/service destruction. Its physical data is compared with deliberate
closure of policy-owned sockets. This does not revoke arbitrary native sockets.

## Resumption and observed baseline

The 2026-09-21 resumption found local and remote head
`ab7db51340603aadcc7230d7e33bd8a11b1afe84`, with six prior revision commits intact.
Eleven working files contained nested conflicts against the old scaffold; every
added non-marker line was checked against that scaffold or the preserved HEAD.
Copies were retained outside the repository and only the stale conflict additions
were resolved. The original stash `3448e814e87cdb0b647329e89fc5f9674d1e6887`
remains preserved; it was not applied, popped or dropped during this resumption.

[Push run 35548440242](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35548440242)
passed validation, nine containment cases and thirteen network cases with the
independent analyzer. However,
[parallel PR run 35548443052](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35548443052)
failed two network assertions. The socket test had already verified actual
closure, then required the missing-ID response `closed` even when a concurrent
route invalidation could return `denied`. Both responses prevent use; the revised
test logs the distinction while still requiring actual closure and stale-generation
denial. Provider replacement now waits up to ten seconds for a different active
VPN Network instead of assuming completion after 1.2 seconds. It still requires
old-generation and unvalidated-generation denial plus independent fixture revoke
and establishment records.

The baseline push run observed one gated TCP packet on physical port 46151 during
VPN loss even though the operation returned `io-failure`. The previous analyzer
classified only TCP payload as a race; the revised analyzer counts SYNs too.
This is **Known Gap: physical egress**, not successful TCP data delivery, and the
distinct calibration port 46153 cannot account for it. External platform lockdown
is therefore required for the tested no-physical-fallback goal; verifying that
dependency reliably in a product remains a PR 6 blocker. Callback/snapshot checks
alone are not enforcement.

## Observed results on b87b268 (run 35564605873)

| Question | Current status |
|---|---|
| Management TCP, isolated Java/native TCP/UDP | Denied; no fixed physical packets |
| Broker caller/session/epoch/generation enforcement | Passed device boundary assertions |
| Full tunnel and per-app include | Java/native IPv4 and controlled DNS observed in TUN; no fixed physical egress |
| Per-app exclude and split route | Known Gap: host TCP physically escaped; split documentation target stayed in TUN |
| Controlled DNS wire packet | Fixed .53 query seen in TUN; platform .54 resolver traffic separately classified |
| Java/native IPv6 UDP | Both observed in TUN for full tunnel/include; preliminary API 35 evidence only |
| Explicit physical Network | Default full tunnel denied; allowBypass connected with physical capture |
| Existing connection and policy Socket closure | Actual closure verified; post-closure send denied during route invalidation. Explicit close, revocation and death also passed. Separate OS retained-socket send returned io-failure |
| VPN-loss race | Known Gap: two gated physical TCP packets despite timeout; separate OS fallback calibration succeeded |
| Always-on/lockdown | Both platform booleans true; physical selection denied after confirmed VPN loss; no fixed physical packets |
| Reconnect and genuine provider replacement | Both passed: old and unvalidated generations denied; reconnect required explicit revalidation; replacement observed a different active VPN Network and old-provider revocation |
| Cronet / QUIC | Unknown / Not exercised |
| Subprocess networking and general Android resolver behavior | Unknown / Not exercised |
| External-lockdown dependency | Required for tested no-physical-fallback goal; reliable product verification remains unresolved |

TRANSPORT_VPN and default-route booleans are observations only: they do not prove
provider trust, destination coverage, logging practices, full tunneling or exit
location. Route authorization comes from trusted test setup, not hostile code or
broker self-attestation; production route validation remains a PR 6 question.
PR 4 containment limitations remain unchanged. No requirement is globally
satisfied by this debug prototype; PD-REQ-033/034/058 require actual observations.
