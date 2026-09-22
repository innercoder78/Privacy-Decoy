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

## Verified exact-head result: S1 FALSIFIED

Tested exact source head: `69f0352510a55d92dcf4a408aa524cc0532788f9`.
The independently verified **Android foundation #100 (push)** and **#101
(pull_request)** workflow invocations both completed successfully:

| Job | #100 push | #101 pull_request |
| --- | --- | --- |
| validate | SUCCESS | SUCCESS |
| containment-prototype | SUCCESS | SUCCESS |
| managed-profile-feasibility | SUCCESS | SUCCESS |
| network-feasibility | SUCCESS | SUCCESS |

Both managed-profile runs independently recorded the following bounded output:

```text
PD_S1_PROFILE_STOP=PROCESS_DEATH_OBSERVED
PD_S1_HARNESS=PASS
PD_S1_OUTCOME=FALSIFIED
PD_S1_REASON=tenant_a_mandatory_build_same
PD_S1_REASON=tenant_b_mandatory_build_same
PD_S1_SAME_AS_PARENT=tenant_a:build_fingerprint
PD_S1_SAME_AS_PARENT=tenant_a:build_model
PD_S1_SAME_AS_PARENT=tenant_a:build_manufacturer
PD_S1_SAME_AS_PARENT=tenant_a:build_brand
PD_S1_SAME_AS_PARENT=tenant_a:build_device
PD_S1_SAME_AS_PARENT=tenant_a:build_product
PD_S1_SAME_AS_PARENT=tenant_a:build_hardware
PD_S1_SAME_AS_PARENT=tenant_b:build_fingerprint
PD_S1_SAME_AS_PARENT=tenant_b:build_model
PD_S1_SAME_AS_PARENT=tenant_b:build_manufacturer
PD_S1_SAME_AS_PARENT=tenant_b:build_brand
PD_S1_SAME_AS_PARENT=tenant_b:build_device
PD_S1_SAME_AS_PARENT=tenant_b:build_product
PD_S1_SAME_AS_PARENT=tenant_b:build_hardware
```

**S1 managed-profile isolation is FALSIFIED for the Privacy Decoy requirements.**
All seven mandatory Build identity surfaces matched the parent environment for
both tenants in both exact-head workflow invocations. A managed profile supplies
useful OS user/UID/storage separation and normal Android application lifecycle
semantics, but the tested boundary did not replace or block mandatory real device
Build identity before hostile app code executed. Under the existing requirements
and ADR-0003 falsification rule, this is decisive architectural failure:
managed-profile OS isolation alone cannot satisfy Decoy Persona mediation.

Green CI means the experiment executed correctly, **not that S1 passed**. The
harness was valid in both runs; this is neither INCONCLUSIVE nor a failure inferred
from a red CI job. The experiment accomplished its purpose by falsifying S1.
The profile-stop observation establishes death of both observed tenant processes.
Storage and lifecycle observations remain bounded to this controlled experiment;
they do not establish production containment or complete resource revocation.

No raw host values were logged. Reasons use a fixed vocabulary, are sorted and
deduplicated, and are capped at 87 lines. Mandatory Build equality uses only two
fixed tenant names and seven fixed surface names, at most 14 lines. The safe report
also names `android_id`, `locale`, and `timezone`; differences in those platform
values are not evidence of synthetic identity. No raw value, hash, UID/PID, user
serial, path, nonce, fixture content or property content is printed. Synthetic
unit tests are not the source of this runtime conclusion.

The earlier tested head `919a64e4624d7926f311ab27aabf00ad022ae8e5` also returned
valid FALSIFIED outcomes in runs #98/#99, but those logs did not retain specific
reasons. The exact-head #100/#101 evidence above now records mandatory Build
identity equality explicitly; it is not a guessed explanation of earlier logs.

The S1 networking/revocation follow-up is **BLOCKED by the survival gate**.
No requirement is weakened and no surface is relabeled External or profile-scoped
synthetic identity. No hooks, privileged APIs, root or APK rewriting are authorized
to patch around the result. S2 is the next separately authorized research direction:
a non-executing source/provenance audit of VirtualSpace and Blacks-BlackBox. No
third-party engine execution/integration, ordinary protected-app support, or
production architecture selection is authorized.

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
45-minute CI job limit is unchanged. The verified exact-head result and reasons
are recorded above; this final documentation revision does not alter the harness.

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

This final documentation revision records the completed experiment at exact head
`69f0352510a55d92dcf4a408aa524cc0532788f9`, with the two successful workflow
invocations and safe falsification reasons recorded above. It changes no experiment,
classifier, test, workflow or runtime code. The workflow uses explicit controller
and tenant-flavor tasks. CI for the subsequent documentation-only commit is
separate from the already-observed source-head evidence.

The tested scope is API 35 Google APIs x86_64 debug signing only.
Physical non-rooted ARM64, Samsung/other OEM, release-equivalent builds, API 31-37
coverage, distribution/provisioning and production recovery remain Unknown. No
API 35 outcome establishes API 37 behavior. API 37 image availability is Unknown.

Long-lived FDs, persistent native workers, jobs, alarms, sockets, full restart and
re-execution validation, networking attribution/revocation, split APKs, multidex,
dynamic code and secondary processes remain **Unknown / not reached**. Roadmap PR 9
network work is BLOCKED because S1 was FALSIFIED in Stage A. The unrelated
CMake SDK-download failure does not justify changing the network prototype or its analyzer/harness.

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
