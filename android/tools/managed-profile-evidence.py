#!/usr/bin/env python3
"""Validate bounded PR8 evidence and classify S1 independently of execution."""
import argparse, json, pathlib, re, sys

ALLOWED = re.compile(r"^[A-Za-z0-9_=-]{1,160}$")
MANDATORY_EVENTS = {"provider", "application", "activity", "service", "receiver"}

def read(path):
    values = {}
    for line in pathlib.Path(path).read_text().splitlines():
        if not ALLOWED.fullmatch(line) or "=" not in line: raise ValueError(f"unsafe evidence line in {path}")
        key, value = line.split("=", 1)
        if key in values: raise ValueError(f"duplicate key {key}")
        values[key] = value
    required = {"schema", "event", "uid", "user", "native_uid", "management_open", "java_management_read", "java_management_write"}
    if values.keys() < required or values["schema"] != "1": raise ValueError(f"incomplete evidence {path}")
    if values["uid"] != values["native_uid"]: raise ValueError("Java/native UID ambiguity")
    return values

def main():
    ap=argparse.ArgumentParser(); ap.add_argument("--parent",required=True);ap.add_argument("--tenant-a",required=True);ap.add_argument("--tenant-b",required=True);ap.add_argument("--output",required=True);a=ap.parse_args()
    groups={name:[read(p) for p in pathlib.Path(root).glob("evidence-*")] for name,root in (("parent",a.parent),("tenant_a",a.tenant_a),("tenant_b",a.tenant_b))}
    for name, records in groups.items():
        events={r["event"] for r in records}
        if events != MANDATORY_EVENTS: raise ValueError(f"{name}: lifecycle mismatch {events}")
        if len({r["uid"] for r in records}) != 1 or len({r["user"] for r in records}) != 1: raise ValueError(f"{name}: identity ambiguous")
    if groups["tenant_a"][0]["user"] != groups["tenant_b"][0]["user"] or len({groups[x][0]["uid"] for x in groups}) != 3: raise ValueError("tenant/user separation invalid")
    for name in ("tenant_a","tenant_b"):
        r=groups[name][0]
        if r["management_open"] != "BLOCKED" or r.get("peer_open") != "BLOCKED" or r["java_management_read"] != "false" or r["java_management_write"] != "false": raise ValueError("management/peer storage isolation failed")
    parent=groups["parent"][0]; tenant=groups["tenant_a"][0]
    same=[i for i in range(10) if parent.get(f"surface_{i}_hash") == tenant.get(f"surface_{i}_hash")]
    # Build identity (0..6) is mandatory and a same-as-parent value falsifies S1.
    outcome="FALSIFIED" if any(i < 7 for i in same) else "SURVIVED_CURRENT_SLICE"
    report={"schema":1,"harness":"PASS","outcome":outcome,"same_as_parent_surfaces":same,"raw_values_recorded":False}
    out=pathlib.Path(a.output);out.parent.mkdir(parents=True,exist_ok=True);out.write_text(json.dumps(report,sort_keys=True)+"\n")
    print("PD_S1_HARNESS=PASS");print("PD_S1_OUTCOME="+outcome)
if __name__ == "__main__":
    try: main()
    except Exception as e: print("PD_S1_HARNESS=FAIL\nPD_S1_OUTCOME=INCONCLUSIVE\nERROR="+type(e).__name__,file=sys.stderr);sys.exit(1)
