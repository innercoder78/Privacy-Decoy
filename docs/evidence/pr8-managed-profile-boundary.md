# Roadmap PR 8: managed-profile boundary experiment

## Question and outcome semantics

This research-only S1 falsification tests the managed-profile identity/storage
boundary and mandatory real host-state exposure before hostile code. Evidence is
validated first, and architecture is evaluated only when that evidence is valid.

- `PD_S1_HARNESS=PASS / PD_S1_OUTCOME=FALSIFIED` is a successful experiment with
  adverse architectural evidence: any tenant management/peer read or write succeeds,
  a fixture changes, unsafe UID sharing is observed, or any mandatory Build field
  matches the parent real host value. This does not raise a harness error.
- `PD_S1_HARNESS=FAIL / PD_S1_OUTCOME=INCONCLUSIVE` means evidence is untrustworthy:
  absent/malformed nonce, uncreated fixture, setup uncertainty, missing/duplicate
  lifecycle evidence, identity mismatch, unsafe output, or unclassified storage error.
- `SURVIVED_CURRENT_SLICE` credits only this bounded slice; it neither selects a
  production architecture nor satisfies any requirement.

## Verified predecessor result and reason preservation

Tested source head: `919a64e4624d7926f311ab27aabf00ad022ae8e5`.
The independently verified **Android foundation #98 (push)** and **#99
(pull_request)** suites both completed successfully. In each suite, `validate`,
`containment-prototype`, `managed-profile-feasibility`, and `network-feasibility`
all returned SUCCESS. Both managed-profile logs recorded:

```text
PD_S1_PROFILE_STOP=PROCESS_DEATH_OBSERVED
PD_S1_HARNESS=PASS
PD_S1_OUTCOME=FALSIFIED
```

This is a valid adverse S1 architectural result, not a failed CI job or an
inconclusive harness. The predecessor logs did **not** retain the specific
falsification reason: it existed only in the generated, unretained `report.json`.
The cause is therefore not asserted here; in particular, Build equality must not
be assumed to have caused the observed result.

This evidence-output-only revision preserves that result and adds permanent,
bounded log tokens for new exact-head CI. Reasons are internally constructed from
a fixed vocabulary, sorted and deduplicated, and capped at 87 lines: one shared-UID
reason, two mandatory-Build summaries, 80 tenant/event/access reasons and four
fixture-mutation reasons. Examples of the format (not predecessor observations):

```text
PD_S1_REASON=tenant_a_mandatory_build_same
PD_S1_REASON=tenant_a_activity_java_management_read_accessible
PD_S1_REASON=tenant_b_provider_native_peer_write_accessible
PD_S1_REASON=management_fixture_changed
PD_S1_SAME_AS_PARENT=tenant_a:build_fingerprint
```

Only a valid FALSIFIED report emits these lines. SURVIVED_CURRENT_SLICE and
FAIL/INCONCLUSIVE emit no architectural reasons. Mandatory Build equality lines
use only the two fixed tenant names and seven fixed Build names (at most 14 lines).
The safe report supplements existing indexes with the exact semantic mapping:
`build_fingerprint`, `build_model`, `build_manufacturer`, `build_brand`,
`build_device`, `build_product`, `build_hardware`, `android_id`, `locale`, `timezone`.
No raw value, hash, UID/PID, user serial, path, nonce, fixture content or property
content is printed. No Android ID/locale/timezone difference is called synthetic.
The decision rule remains unchanged: equality of any mandatory Build index 0-6
falsifies S1.

New exact-head reason evidence remains pending. Synthetic unit tests are not
emulator observations. S1 remains FALSIFIED; its survival-gated networking follow-up
is currently **BLOCKED**. Architecture consequences await review of the new safe
reason output. ADR-0003 is not rewritten around an inferred cause; S2 remains the
independently authorized, non-executing source/provenance audit direction. No
production architecture or ordinary protected-app support is selected.

## Bounded emulator startup and cleanup

The harness follows the established containment/network startup pattern with its
own `android-user-managed-profile` and `avd-managed-profile` build directories,
explicit AVD path and Pixel 2 device definition. It verifies the AVD ini and system
image before launch, fixes the emulator port to 5554 and all device commands to
`emulator-5554`, and rejects an already-connected emulator on that port.

The hosted-runner flags use software GPU, 2048 MB memory, two cores, a 2048 MB data
partition, no window/audio/boot animation/snapshots, and wiped disposable data.
There is no unbounded `adb wait-for-device`. A five-minute boot deadline checks
the emulator PID and uses three-second property-query timeouts, with a bounded
kill grace period. Progress output is categorical. API 35 and x86_64 are verified
before profile provisioning; raw property values are not printed.

Startup failures inspect a bounded portion of the local emulator log and emit only
fixed diagnostic categories: insufficient disk space, missing library, GPU failure,
unavailable acceleration, missing AVD/image, AVD path conflict, or unclassified
startup failure. No raw log lines, paths, IP addresses, identifiers, nonce or fixture
values are emitted. Failure is `FAIL / INCONCLUSIVE`, never S1 falsification.

Device operations have a 60-second bound (120 seconds for APK installation), plus
a two-second kill grace period. Evidence-file readiness checks retain bounded
polling with three-second command limits; measured component/access execution is
not retried. Cleanup skips device operations before successful boot, bounds profile
removal and emulator shutdown, retains local PID termination with a bounded grace
period, and bounds local AVD deletion. No unbounded child wait remains. The existing
45-minute CI job limit is unchanged. The verified predecessor result is recorded
above; new exact-head reason evidence remains pending.

## Engineering setup and pre-code ordering

The disposable emulator uses development ADB provisioning only. The harness knows
the exact target **shell user ID**, requires successful `dpm set-profile-owner
--user <target>`, and reads the controller checkpoint using
`run-as <controller-package> --user <target>`
from that same package. The controller verifies `isProfileOwnerApp()` and
`UserManager.isManagedProfile()`, records its package UID and public **user serial**,
and enables the profile. The serial is not an Android internal user ID. The harness
correlates controller UID with the target user's PackageManager UID independently.

The exported custom setup receiver accepts only the fixed setup action, refuses to
rewrite an existing checkpoint, and disables itself using public PackageManager
APIs before writing the checkpoint. The harness validates the disabled checkpoint
and verifies probes absent before installing any probe. A final owner read must
match the checkpoint byte for byte. No optional broad admin policies are requested.
The normal provisioning callback does not write this experiment checkpoint.

The harness then installs tenant A in the parent and managed profile and tenant B
in the managed profile. Before **any** hostile component executes, it prepares:

1. A management sentinel in the production app's private `no_backup` directory,
   without launching that app or modifying its manifest/code.
2. Parent-baseline and tenant A/B sentinels, each through `run-as` under that
   installation's own package UID. Contents are independently generated synthetic
   random test values. Both tenant targets exist before either tenant executes.
3. One fresh cryptographically random 256-bit **synthetic test nonce**, generated
   by Python `secrets` for this run and seeded into all three probe installations'
   private storage. It is verified by an owner read before launch, never logged,
   never generated by a probe, and never derived from host state. Only its SHA-256
   commitment is retained. Missing or malformed preparation fails closed.
4. An engineering peer-path fixture containing the target shell user ID. This avoids
   hidden UserHandle internals; public user serials are never used as filesystem IDs.

Independent owner `sha256sum` reads prove each sentinel existed and was readable
before attacks, and retain safe before/after hashes. No sentinel contents are logged.
Mutation is architectural failure, not a hash-equality assertion that fails the
harness. A missing required owner read is inconclusive. All engineering preparation
is development-only and must never become an ordinary-use ADB/run-as dependency.

## Observations and access semantics

Normal ContentProvider (before Application), Application, deep-link Activity,
Service, and BroadcastReceiver executions each emit a bounded schema-2 record.
Repeated writes append and are rejected as duplicate keys instead of overwriting
evidence. JNI uses public NDK APIs and direct `openat`, read and write syscalls;
it records UID/GID, PID presence and a joined native thread.

Both Java and native code attempt **management read, management write, peer read,
and peer write**, including A -> B and B -> A. Java uses actual FileInputStream and
FileOutputStream operations; it does not infer denial from `canRead`/`canWrite`.
Writes append only a synthetic byte. Native writes do not create missing targets.
Java append streams may create a missing file only if their open succeeds; fixture
existence was already independently proved and any success is adverse evidence.
No accessed contents are emitted.

| Category | Meaning and treatment |
| --- | --- |
| `ACCESSIBLE` | Actual storage operation succeeded; tenant private-target success falsifies S1. |
| `PERMISSION_DENIED` | EACCES/EPERM, or Java SecurityException. |
| `ABSENT` | ENOENT; kept distinct from denial. With owner-proven existence it may reflect namespace hiding. |
| `OTHER_ERROR` | Unclassified error, including Java IOExceptions without a supported errno cause. Storage evidence is inconclusive. |

Public Android `ErrnoException` causes preserve Java errno categories without parsing
raw error strings. Every observation must use the fixed allowlist. Native availability
observations separately open `/proc/self/status`, `/proc/sys/kernel/random/boot_id`,
and `/sys/devices`; they do not dump or read contents. The boot ID is **never a nonce**.
Uninterpretable availability is reported as **Unknown**, not blocked or mediated.

Native `__system_property_get` observes `ro.build.fingerprint`. It returns only
`property_available` and `property_matches_java_build`, comparing in native memory
against Java Build.FINGERPRINT. Neither raw value is emitted. Availability is not
mediation. A match corroborates the same fingerprint surface; parent equality is
established using the Java hashes, not inferred merely from property availability.

Parent and both managed tenants hash these values using the same seeded nonce:
Build.FINGERPRINT, MODEL, MANUFACTURER, BRAND, DEVICE, PRODUCT, HARDWARE, Android ID,
locale, and timezone. Any of the seven mandatory Build values equal to the parent
is decisive adverse host-identity evidence. Different values remain uncontrolled
platform values, not evidence of a supported synthetic replacement. Android ID is
never called synthetic merely because user/package scoping makes it different.

## Evidence validation and trust boundary

The classifier verifies the exact required fields and event set, filename/event
agreement, no duplicate keys/events, safe bounded text, stable UID and public user
serial, Java/native UID agreement **on every record**, package UID correlation with
independent harness state, shared nonce commitments, and stable static hashes,
property results and availability categories across lifecycle records. Required
storage attempts are checked on every event; a later accessible result cannot be
hidden by an earlier denial. Both tenant comparisons must be evaluated.

The TCB is the emulator OS/kernel, profile/package policy, engineering harness and
classifier. Probe evidence is instrumentation for this controlled experiment, not a
production tamper-resistant attestation scheme. Owner fixture hashes and setup
checkpoints are collected externally. Generated records/reports remain under
`app/build/reports/managed-profile/` and are never committed. Errors must not print
raw identifiers, properties, fixture contents or nonce values.

After collection, the coarse profile-stop check requires both tenant UIDs present
before stopping and absent afterward. The profile is started again and then removed.
Classification is published only after harness completion and cleanup. Closed
descriptors and a joined native worker do not prove persistent-resource revocation.

## Validation provenance and limits

This evidence-preservation revision starts from verified PR #9 head
`919a64e4624d7926f311ab27aabf00ad022ae8e5`. Its predecessor runtime results are
recorded above. Local classifier/regression tests and shell syntax checks are
recorded in the PR body; new exact-head reason output remains pending. The workflow
uses explicit controller and tenant-flavor tasks.

The observed predecessor scope is API 35 Google APIs x86_64 debug signing.
Physical non-rooted ARM64, Samsung/other OEM, release-equivalent builds, API 31-37
coverage, distribution/provisioning and production recovery remain Unknown. No
API 35 outcome establishes API 37 behavior. API 37 image availability is Unknown.

Long-lived FDs, persistent native workers, jobs, alarms, sockets, full restart and
re-execution validation, networking attribution/revocation, split APKs, multidex,
dynamic code and secondary processes remain **Unknown / not reached**. Roadmap PR 9
network work is currently BLOCKED because S1 was FALSIFIED in Stage A. The unrelated CMake SDK-download
failure does not justify changing the network prototype or its analyzer/harness.

## Production safety and requirements

Production AndroidManifest.xml and PD-REQ-001 through PD-REQ-070 remain unchanged.
There is no production DPC, DeviceAdminReceiver, VPN, networking research permission,
telemetry, native research component or ordinary protected-app support. The controller
and probe remain exclusively in their respective `android/test-apps/` directories.
ADR-0003 remains **NO PRODUCTION ARCHITECTURE SELECTED** and README stays conservative.

This supplies bounded evidence, never requirement satisfaction, for PD-REQ-006,
007, 008, 011-016, 019-021, 027, 028, 030, 031, 041, 044, 045, 057, 058, 060, 063,
065 and 070. No requirement is weakened. Unknown mandatory coverage stays Unknown.

Public API references: [UserManager user serial](https://developer.android.com/reference/android/os/UserManager#getSerialNumberForUser(android.os.UserHandle)),
[Android system errno APIs](https://developer.android.com/reference/android/system/package-summary),
and [NDK system-property header](https://android.googlesource.com/platform/bionic/+/refs/heads/main/libc/include/sys/system_properties.h).
