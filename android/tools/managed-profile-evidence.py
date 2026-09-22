#!/usr/bin/env python3
"""Validate trusted engineering checkpoints before evaluating the bounded S1 slice."""
import argparse
import json
import pathlib
import re
import sys

EVENTS = {"provider", "application", "activity", "service", "receiver"}
GROUPS = ("parent", "tenant_a", "tenant_b")
FIXTURES = ("management",) + GROUPS
ACCESS = tuple(f"{language}_{target}_{mode}" for language in ("java", "native")
               for target in ("management", "peer") for mode in ("read", "write"))
CATEGORIES = {"ACCESSIBLE", "PERMISSION_DENIED", "ABSENT", "OTHER_ERROR"}
SURFACES = tuple(f"surface_{i}_hash" for i in range(10))
OBSERVATIONS = ("proc_self", "proc_host", "sys")
BOOLS = ("native_pid_present", "native_thread", "property_available", "property_matches_java_build")
HASH = re.compile(r"[A-Za-z0-9_-]{43}")
HEX = re.compile(r"[0-9a-f]{64}")
NUMBER = re.compile(r"0|[1-9][0-9]{0,10}")


def require(condition):
    if not condition:
        raise ValueError("invalid bounded evidence")


def read(path):
    values = {}
    data = pathlib.Path(path).read_bytes()
    require(0 < len(data) <= 16384)
    text = data.decode("ascii")
    require(re.fullmatch(r"[A-Za-z0-9_=\n-]+", text) is not None)
    for line in text.splitlines():
        require(re.fullmatch(r"[a-z][a-z0-9_]*=[A-Za-z0-9_-]{1,128}", line) is not None)
        key, value = line.split("=", 1)
        require(key not in values)
        values[key] = value
    return values


def validate_setup(root, checkpoint_only=False):
    root = pathlib.Path(root)
    setup = read(root / "setup-state")
    require(set(setup) == {"schema", "setup_complete", "profile_owner", "managed_profile",
                           "receiver_disabled", "controller_uid", "user_serial"})
    require(setup["schema"] == "2")
    for key in ("setup_complete", "profile_owner", "managed_profile", "receiver_disabled"):
        require(setup[key] == "true")
    for key in ("controller_uid", "user_serial"):
        require(NUMBER.fullmatch(setup[key]) is not None)
    checkpoint = read(root / "checkpoint")
    require(set(checkpoint) == {"schema", "dpm_success", "probes_absent", "target_shell_user",
                                "controller_package_uid"})
    require(checkpoint["schema"] == "2" and checkpoint["dpm_success"] == "true"
            and checkpoint["probes_absent"] == "true")
    require(NUMBER.fullmatch(checkpoint["target_shell_user"]) is not None)
    require(int(checkpoint["target_shell_user"]) > 0)
    require(setup["controller_uid"] == checkpoint["controller_package_uid"])
    if checkpoint_only:
        return setup, checkpoint
    require((root / "setup-state").read_bytes() == (root / "setup-state-after").read_bytes())
    fixtures = {}
    for name in FIXTURES:
        f = read(root / ("fixture-" + name))
        required = {"schema", "shell_user", "package_uid", "existed_before", "owner_readable_before",
                    "before_hash", "after_hash", "owner_readable_after"}
        if name != "management":
            required |= {"nonce_seeded", "nonce_hash"}
        require(set(f) == required and f["schema"] == "2")
        for key in ("shell_user", "package_uid"):
            require(NUMBER.fullmatch(f[key]) is not None)
        for key in ("existed_before", "owner_readable_before", "owner_readable_after"):
            require(f[key] == "true")
        for key in ("before_hash", "after_hash"):
            require(HEX.fullmatch(f[key]) is not None)
        require(f["shell_user"] == ("0" if name in ("management", "parent")
                                   else checkpoint["target_shell_user"]))
        if name != "management":
            require(f["nonce_seeded"] == "true" and HASH.fullmatch(f["nonce_hash"]) is not None)
        fixtures[name] = f
    require(len({fixtures[n]["nonce_hash"] for n in GROUPS}) == 1)
    return setup, fixtures


def classify(root):
    root = pathlib.Path(root)
    setup, fixtures = validate_setup(root)
    groups = {}
    expected = {"schema", "event", "uid", "user_serial", "pid", "nonce_source", "nonce_hash",
                "native_uid", "native_gid"} | set(ACCESS + SURFACES + OBSERVATIONS + BOOLS)
    stable = ("uid", "user_serial", "nonce_hash", "native_gid") + SURFACES + OBSERVATIONS + BOOLS
    for name in GROUPS:
        records = []
        paths = list((root / name).glob("evidence-*"))
        require(len(paths) == len(EVENTS))
        events = set()
        for path in paths:
            r = read(path)
            require(set(r) == expected and r["schema"] == "2")
            require(r["event"] in EVENTS and r["event"] not in events)
            require(path.name == "evidence-" + r["event"])
            events.add(r["event"])
            for key in ("uid", "user_serial", "native_uid", "native_gid", "pid"):
                require(NUMBER.fullmatch(r[key]) is not None)
            require(int(r["pid"]) > 0 and r["uid"] == r["native_uid"] == fixtures[name]["package_uid"])
            require(r["nonce_source"] == "SEEDED_TEST" and r["nonce_hash"] == fixtures[name]["nonce_hash"])
            for key in SURFACES:
                require(HASH.fullmatch(r[key]) is not None)
            for key in ACCESS + OBSERVATIONS:
                require(r[key] in CATEGORIES)
            # Unknown errors in storage cannot establish a trustworthy attack result.
            require(all(r[key] != "OTHER_ERROR" for key in ACCESS))
            for key in BOOLS:
                require(r[key] in {"true", "false"})
            require(r["native_pid_present"] == r["native_thread"] == "true")
            require(r["property_available"] == "true" or r["property_matches_java_build"] == "false")
            records.append(r)
        require(events == EVENTS)
        for key in stable:
            require(len({r[key] for r in records}) == 1)
        groups[name] = records
    representative = {n: groups[n][0] for n in GROUPS}  # Only after validating every record.
    require(representative["tenant_a"]["user_serial"] == representative["tenant_b"]["user_serial"]
            == setup["user_serial"])
    require(representative["parent"]["user_serial"] != setup["user_serial"])
    reasons = []
    identities = [fixtures[n]["package_uid"] for n in FIXTURES] + [setup["controller_uid"]]
    if len(set(identities)) != len(identities):
        reasons.append("unsafe_shared_uid")
    comparisons = {}
    for name in ("tenant_a", "tenant_b"):
        comparisons[name] = [i for i, key in enumerate(SURFACES)
                             if representative[name][key] == representative["parent"][key]]
        if any(i < 7 for i in comparisons[name]):
            reasons.append(name + "_mandatory_build_same")
        for r in groups[name]:
            for key in ACCESS:
                if r[key] == "ACCESSIBLE":
                    reasons.append(name + "_" + r["event"] + "_" + key)
    for name, fixture in fixtures.items():
        if fixture["before_hash"] != fixture["after_hash"]:
            reasons.append(name + "_fixture_changed")
    return {"schema": 2, "harness": "PASS", "outcome": "FALSIFIED" if reasons else "SURVIVED_CURRENT_SLICE",
            "reasons": reasons, "same_as_parent_surfaces": comparisons,
            "observations": {n: {k: ("Unknown" if r[k] == "OTHER_ERROR" else r[k])
                                  for k in OBSERVATIONS + BOOLS[2:]} for n, r in representative.items()},
            "raw_values_recorded": False,
            "remaining_coverage": "Unknown_not_reached"}


def evaluate(root):
    try:
        return classify(root)
    except (OSError, ValueError, KeyError, UnicodeError):
        return {"schema": 2, "harness": "FAIL", "outcome": "INCONCLUSIVE"}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", required=True)
    parser.add_argument("--output")
    parser.add_argument("--check-setup", action="store_true")
    args = parser.parse_args()
    if args.check_setup:
        try:
            validate_setup(args.root, checkpoint_only=True)
            return 0
        except (OSError, ValueError, KeyError, UnicodeError):
            print("PD_S1_HARNESS=FAIL\nPD_S1_OUTCOME=INCONCLUSIVE")
            return 1
    report = evaluate(args.root)
    if args.output:
        pathlib.Path(args.output).write_text(json.dumps(report, sort_keys=True) + "\n", encoding="ascii")
    print("PD_S1_HARNESS=" + report["harness"])
    print("PD_S1_OUTCOME=" + report["outcome"])
    return 0 if report["harness"] == "PASS" else 1


if __name__ == "__main__":
    sys.exit(main())
