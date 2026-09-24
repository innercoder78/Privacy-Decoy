# PR 5 protected-network feasibility evidence

## Scope and current validation status

Research only: API 35 / Google APIs / x86_64 / debug. The next roadmap feasibility/STOP decision stage remains mandatory; its eventual GitHub PR number is no longer #6.

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

## PR #5 session-boundary revision after the PR #6 merge

GitHub PR #6 was the stacked lockdown-harness correction and was merged into
PR #5. It did not perform the roadmap's mandatory feasibility/STOP decision;
that decision remains the next roadmap stage under a later GitHub PR number.

[Exact-head run 35633747836](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35633747836)
on PR #5 head `34c1eb33a3f742d904a871bcb3a07844a607e35d` passed `validate`
and `containment-prototype` (9/9). Twelve of thirteen network cases passed.
Only `testBrokerSessionBoundary` failed, with
`route changed during boundary assertions`. ConnectivityManager callbacks can
invalidate NetworkGate generations asynchronously even after the quiet setup
window. The generation-equality assertion correctly exposed that identity/epoch
checks still depended on volatile route state. The earlier quiet-window approach
and its failure history remain recorded below.

Verified lockdown still passed with Android reporting `alwaysOn=true` and
`lockdown=true`; tested physical fallback operations were denied. Independent
capture again recorded non-lockdown VPN-loss physical egress as **Known Gap**.
Neither observation establishes production protection.

This revision separates Binder/session authorization from network-generation
evidence. Debug-only `BOUNDARY_NOOP` traverses the real isolated-process Binder
request path and checks Binder-observed UID/PID, registered session ID and epoch,
live lifetime Binder, and active/non-revoked/non-dead SessionPolicy. It accepts
exactly `session`, `epoch`, and `op`; no generation, connection, destination or
payload is accepted. It returns only `result=success` or `result=denied`, with no
socket, FD, Network or manager state. Its branch returns before route observation
or network dispatch and is absent from the network-operation enum.

The device case independently checks unregistered isolated and ordinary callers,
active A/B, B claiming A, stale epoch, extra/missing fields, unknown operation,
hostile ROUTE, revocation and dead-session claims by an active registered
replacement. It requires exact bounded reply keys and no file descriptors, checks
no owned socket was created, and repeats a positive boundary check after deliberate
route invalidation. Identity assertions use no network generation or retry.
A separate phase uses actual fixed `HOST_UDP4`: an unvalidated current generation
is denied, an intentionally authorized current generation succeeds, and a stale
generation is denied. Existing network experiments and their measured operations
are unchanged. Focused JVM coverage checks every network operation across current,
stale, invalidated and reauthorized generations and keeps BOUNDARY_NOOP outside
the networking enum.

Local validation for this revision: all 10 Python capture/readiness regression
tests and `py_compile` passed. Gradle version/build/lint/unit/instrumentation/fixture
tasks were attempted through the Windows wrapper but could not start because no
Java runtime was available on PATH or through JAVA_HOME. No configured Android
SDK/emulator was found; no local device validation is claimed. New-head GitHub
Actions results are reported in the updated PR #5 body after publication, without
inferring success from prior runs. Cronet/QUIC, subprocess networking and general
Android resolver behavior remain **Unknown / Not exercised**.

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
epoch, live lifetime and finite operation. Actual network operations additionally require
the current authorized NetworkGate generation. SessionPolicy is
reused. Cross-session, stale, malformed, unknown, revoked and dead-session calls
are tested, including attempted hostile route validation and direct bypass.

The actual broker registry maps opaque IDs to owned Socket objects and checks
the owner on send/close. Route callbacks, generation changes, revocation, lifetime
death and destruction close resources. Each networking/control Binder dispatch also samples active
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
dependency reliably in a product remains a next-roadmap-stage blocker. Callback/snapshot checks
alone are not enforcement.

## Follow-up harness stabilization

[Exact-head run 35566250067](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35566250067)
on `5e4089a5c065989eb111ea5deedc171237d2cad8` passed validation and the
nine-test containment suite. The network job completed all mandatory VPN-loss,
reconnect, and provider-replacement operations, then failed during optional
lockdown setup: immediately after `sys.boot_completed` became `1`, Activity
Manager did not successfully start the external fixture controller. This was a
post-reboot harness readiness failure, not a measured network result. The revised
setup now waits, with a fixed deadline, for ADB, boot completion, both fixture
packages, controller resolution, Activity Manager, and the restored development
VPN app-op. Only the pre-measurement controller start may be retried. It then
requires fixture establishment and both platform lockdown booleans exactly as
before; measured network operations remain single observations.

### PR #6 explicit controller Intent correction

[Exact-head run 35620936585](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35620936585)
on `35c41c79b21ba4f119e9a3d9bfa750b243a8b5fd` passed validation and containment,
but failed at `Lockdown setup failed: controller-unresolved` after the mandatory
VPN-loss/reconnect and provider-replacement operations. This is a readiness
harness/setup failure, not a measured network-security failure or evidence that
Android lockdown is Unknown. The new probe used a bare positional component
with `--brief`, rather than the intended component-only Intent.

The [Android 15/API 35 Intent parser](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-15.0.0_r1/core/java/android/content/Intent.java)
accepts a positional component but adds `ACTION_MAIN` and `CATEGORY_LAUNCHER`;
`-n` sets the component without those defaults, matching the existing controller
launch. Thus the old form is not an unsupported syntax; its Intent semantics
are wrong for the intended probe. The corrected command is
`adb shell cmd package resolve-activity --components -n com.privacydecoy.externalvpnfixture/com.privacydecoy.externalvpnfixture.FixtureController`.
The [API 35 PackageManager shell implementation](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-15.0.0_r1/services/core/java/com/android/server/pm/PackageManagerShellCommand.java)
supports `--components` and prints `flattenToShortString()`: the probe now requires
exactly `com.privacydecoy.externalvpnfixture/.FixtureController` after stripping
surrounding whitespace. A zero exit status with `No activity found` is rejected.
Regression tests require the exact `-n` command and reject unresolved, wrong-package,
and unexpected multiline output before any controller launch.

This correction is source-verified; no local Android SDK/emulator was available
for direct system-image verification. The exact causal explanation and corrected
probe on the CI image remain subject to the new exact-head Actions result,
recorded in PR #6 after publication. No new device/analyzer/lockdown success is
claimed here. All other bounded setup checks and measured operations are unchanged;
lockdown still requires both platform booleans before `testLockdownLoss`.

[Run 35565382044](https://github.com/innercoder78/Privacy-Decoy/actions/runs/35565382044)
on documentation head `d3bb06d` passed validation and all nine containment cases,
but the network boundary case accepted B and then denied A using the same route
generation. The other twelve network cases and packet assertions passed, including
both previously corrected cases. The broker intentionally invalidates generations
on asynchronous network changes. The harness now requires a one-second quiet
generation before authorizing an experiment, within a ten-second deadline.
No measured operation is retried and no route is revalidated during the measured
VPN-loss or replacement interval. An additional generation-equality assertion
prevents incidental stale-generation denials from masking identity/epoch checks.
This follow-up changes test preparation only; broker fail-closed behavior is
unchanged. Its exact-head CI result is maintained in PR #5.

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
broker self-attestation; production route validation remains a next-roadmap-stage question.
PR 4 containment limitations remain unchanged. No requirement is globally
satisfied by this debug prototype; PD-REQ-033/034/058 require actual observations.


## PR #20 exact-head observation-harness stabilization

PR #20 run #136 passed network feasibility. Run #137 failed after device
operations with `Missing per-operation TUN evidence: NATIVE_UDP4`; run #138
failed after device operations with `Missing independent physical positive control`.
AG-1A admission feasibility was green on both latter heads. These failures
occurred at different asynchronous observation boundaries; neither missing
observation is accepted as successful network evidence.

This revision replaces the fixed 0.4-second post-case wait with capture-size
observation: at least one second after instrumentation, both physical and Wi-Fi
offsets stable for 0.5 seconds, and a three-second overall deadline, sampled every
0.1 seconds. Either file changing restarts the quiet interval. At the deadline,
the final offsets bound the case even if sizes have not settled; all existing
evidence assertions still apply. No packet contents are inspected while waiting.

Mandatory IPv4/native/controlled-DNS TUN attribution now requires both BEGIN and
END markers plus the operation's exact family/protocol/category/port tuple within
the same reset-bounded case observations. A delayed fixture PACKET log after END
can count; another case, wrong tuple, `other`, or platform DNS cannot substitute.
This removes dependence on cross-process textual placement. IPv6 remains
preliminary with its existing observation handling unchanged.

Measured operations remain single-shot. All physical positive controls, required
TUN evidence, no-fixed-egress assertions, distinct VPN-loss calibration and verified
lockdown checks remain mandatory; missing evidence still fails. Known Gap results
and production privacy claims are unchanged. Deterministic host regression tests
cover settling, deadlines, both offsets, single instrumentation dispatch and exact
case-local attribution. Successful stabilization is not established until the new
exact-head GitHub Actions run supplies device and independent capture evidence.

### PR #20 run #139 UDP tuple correction

Exact-head run #139 on `8dad707701d77811db443bd9389a01eb20094542`
passed all jobs except `network-feasibility`, which failed with
`Missing per-operation TUN evidence: JAVA_UDP4`. The device operation emitted
the expected family-4 UDP packet with category `documentation-v4` and port 46152;
the newly introduced analyzer tuple incorrectly expected `host-control`.
Source inspection confirms that `FixedNetworkProbe.JAVA_UDP4` and native UDP4
in `android/research-native/src/main/cpp/probe.c` target `198.51.100.7`, which
`FixtureVpnService` classifies as `documentation-v4`. Native UDP4 uses port 46154.

This revision corrects only the matcher and independent test expectations for
those two UDP tuples, with explicit rejection of `host-control`. TCP remains
`host-control` and controlled DNS remains `synthetic-dns`. Capture quiescence,
case-local exact matching, BEGIN/END requirements and delayed-after-END support
are preserved. No measured operation, network policy, positive-control requirement,
physical-capture requirement, lockdown assertion, Known Gap result or privacy
claim is weakened. The next exact-head CI result remains pending; no success is
claimed in advance.

### PR #21 run #147 bounded PACKET-log starvation

At PR #21 head `180213a970d7382ca8c69908d18a5309d6a8ee2d`, Actions run
#147's network job failed with `Missing per-operation TUN evidence: NATIVE_UDP4`.
In `testFullTunnel`, observer output reached `count=128` before NATIVE_UDP4.
The fixture had one shared 128-record budget; platform/background traffic
consumed capacity needed by later controlled observations. No NATIVE_UDP4
PACKET record was emitted afterward. This does not establish that the required
packet actually traversed the TUN: the analyzer correctly failed on missing
evidence.

The revision reserves eight records for each exact controlled signature:

| Family | Protocol | Category | Port |
|---|---|---|---|
| 4 | tcp | host-control | 46151 |
| 4 | udp | documentation-v4 | 46152 |
| 4 | tcp | host-control | 46153 |
| 4 | udp | documentation-v4 | 46154 |
| 4 | udp | synthetic-dns | 53 |
| 6 | udp | documentation-v6 | 46152 |
| 6 | udp | documentation-v6 | 46154 |

All other sanitized tuples, including platform DNS, share 64 background slots.
The per-establishment maximum is **7 x 8 + 64 = 120 PACKET records**, stricter
than the previous 128. The public count remains an emitted-record ordinal, never
a raw packet total. Unknown traffic is still sanitized to `other` and port zero
before budgeting; the pure helper receives only family, protocol category,
destination category, and port. It receives no addresses or payloads and cannot
create evidence for an unobserved packet. IPv6 remains preliminary.

No measured operation is retried. Exact per-operation TUN evidence, BEGIN/END
markers, case boundaries, packet signatures, physical-capture assertions,
positive controls, VPN-loss calibration, split-route and lockdown requirements
are unchanged. Known Gap findings and production privacy claims remain unchanged.
Prior run history is preserved. A fixture-only JUnit 4.13.2 test dependency
matches the app's existing test version and adds no runtime dependency.

Local validation passed fixture lint, nine fixture JVM tests, fixture debug
assembly, app lint, all 24 app JVM tests, and instrumentation assembly. Tests
cover 1,000-record other/platform-DNS floods, exact independent eight-record
signature limits, the shared 64-record background limit, non-controlled tuples,
the 120-record total bound, fresh establishment budgets, and the sanitized-only
API. All 57 existing Python tests and both network/AG-1B runner syntax checks
passed. Fixture-only CI classification remains baseline/network only; no
classifier change was needed. Linux/KVM remains unavailable locally, so no
network device experiment or measured-operation retry was run. CI pending after
publication.
