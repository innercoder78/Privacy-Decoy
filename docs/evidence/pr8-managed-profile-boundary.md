# Roadmap PR 8: managed-profile boundary experiment

## Question and outcome semantics

This controlled S1 falsification asks whether a supported Android managed profile
provides kernel-backed identity/storage isolation **and** whether ordinary profile-
owner/application mechanisms block or replace every mandatory real host-state path
before hostile code. Harness validity and architecture outcome are independent:
complete evidence yields `PD_S1_HARNESS=PASS`; matching mandatory parent/device
Build identity yields `PD_S1_OUTCOME=FALSIFIED`. `INCONCLUSIVE` fails the job.
Exact-head emulator evidence is pending GitHub Actions and no local outcome is
claimed here.

## Design, ordering, and TCB

The disposable API-parameterized harness creates a managed profile with documented
`pm create-user`/`dpm set-profile-owner` shell commands, installs the minimal
project-owned controller, broadcasts provisioning completion, verifies the owner,
and proves the probe absent before installing it. ADB is development setup only;
it does not satisfy production provisioning and must never become a production
dependency. PD-REQ-006 is unchanged.

The TCB is the emulator Android OS/kernel, package/profile policy, harness, and
evidence classifier. The controller has a `DeviceAdminReceiver`, claims no optional
admin policy, calls only `setProfileEnabled`, and stores setup completion in its own
UID. It has no Internet permission, VPN, telemetry, secrets, broker, cross-profile
grant, or production integration. Production Privacy Decoy is neither profile nor
device owner.

Two flavor APKs (`com.privacydecoy.research.managedprobe.a` and `.b`) share source
but are independently installed and must have distinct app UIDs in the same profile
user; controller and parent installations are distinct identities. The harness
rejects ambiguous Java/native UIDs, wrong users, missing owner/probe ordering,
missing lifecycle events, unsafe report characters, duplicate fields, and failed
cleanup. A synthetic parent `no_backup` sentinel never enters shared storage.

## Exercised Stage A surfaces

The installed probe executes normal `ContentProvider` (before `Application`),
`Application`, deep-link `Activity`, explicit `Service`, and `BroadcastReceiver`
components. Lifecycle success establishes package semantics, not privacy. Native
code is built from source for normal APK ABIs and loaded with `System.loadLibrary`;
JNI records categorical UID/GID/PID, creates a native thread, uses direct `openat`
for management/peer paths, and categorizes `/proc/self` and `/sys` availability.
No `.so` is committed.

Parent/profile comparisons use the same fresh emulator-boot nonce and retain only
SHA-256 hashes/equality for fingerprint, model, manufacturer, brand, device,
product, hardware, Android ID, locale, and time zone. Build surfaces are classified
`same-as-parent real host value`, `different but uncontrolled real/platform value`,
or Unknown; Android ID is profile/package-scoped platform-generated, never called
synthetic. PackageManager installation/identity, profile identity, Settings access,
provider initialization, ActivityManager component dispatch, and service
availability are represented by setup and lifecycle evidence. No personal provider
or account data is accessed.

Java and native attempts against the management sentinel must be blocked; tenant
UID/user assertions and private evidence directories test storage separation.
After collection the harness stops the profile from outside the protected UID,
requires tenant process death, restarts the profile, then removes it during teardown.
The native worker is joined and private descriptor is closed before stop; persistent
FD/worker and recovery re-execution therefore remain Unknown.

## Evidence schema and classification

Generated files live only under `app/build/reports/managed-profile/`. Every event
has schema, event, user, Java/native UID, PID-presence, categorical access results,
and nonce-keyed hashes. Raw identifiers, properties, sentinel content, accounts,
location, network identifiers, tokens, and secrets are forbidden. The independent
classifier checks exact lifecycle sets and identities before deciding outcome.
A same parent/profile mandatory Build hash is fatal S1 evidence: it is not relabeled
External and does not fail otherwise-valid CI. Missing or unsafe evidence is
Inconclusive and exits nonzero.

## Platform facts, observations, assumptions, and limits

Managed profiles supply separate Android users and package UIDs, but do not promise
a synthetic device. Repository observation: the profile owner API used here cannot
replace Build constants before provider/native code. Experimental observations,
head SHA, API/ABI/image and exact S1 outcome remain pending exact-head CI. Planned
primary scope is API 35 Google APIs x86_64 debug signing. API 31 is an endpoint to
attempt where its official image is available; API 37 image availability is
**Unknown / environment unavailable** until inspected. No API 35 result is API 37
evidence.

Stage B is not silently credited. Split installation, multidex, dynamic code,
secondary processes, jobs, alarms, long-lived descriptors/workers, and networking
are **not reached pending Stage A**. If Stage A decisively falsifies S1 they remain
not reached rather than attracting compatibility hooks. Roadmap PR 9 network and
socket-revocation work proceeds only if S1 survives. Physical non-rooted ARM64,
Samsung/other OEM, release-equivalent, API 31–37, provisioning/distribution, and
production recovery evidence remain Unknown. No root, `su`, system-image changes,
hidden APIs, hooking, third-party DPC/engine, ordinary application, real data, or
network capability is used.

## Requirements trace

This experiment supplies bounded evidence—not satisfaction—for PD-REQ-006,
PD-REQ-007, PD-REQ-008, PD-REQ-011, PD-REQ-012, PD-REQ-013, PD-REQ-014,
PD-REQ-015, PD-REQ-016, PD-REQ-019, PD-REQ-020, PD-REQ-021, PD-REQ-027,
PD-REQ-028, PD-REQ-030, PD-REQ-031, PD-REQ-041, PD-REQ-044, PD-REQ-045,
PD-REQ-057, PD-REQ-058, PD-REQ-060, PD-REQ-063, PD-REQ-065, and PD-REQ-070.
PD-REQ-001 through PD-REQ-070 remain unchanged.
