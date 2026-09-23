#!/usr/bin/env python3
"""AG-1A deterministic, static APK artifact-set admission analyzer."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import zipfile

ANALYZER_VERSION = "ag1a-2"
OUTCOMES = ("PROTECTED_ELIGIBLE", "EXPERIMENTAL_ELIGIBLE", "KNOWN_UNSAFE", "INCOMPATIBLE")
CAPABILITIES = ("Fully mediated", "Partially mediated", "Unsupported", "External", "N/A", "Unknown")

SIGNATURES = {
    b"DexClassLoader": ("DYNAMIC_DEX_LOADER_REFERENCE", "dynamic-loading"),
    b"InMemoryDexClassLoader": ("IN_MEMORY_DEX_LOADER_REFERENCE", "dynamic-loading"),
    b"PathClassLoader": ("PATH_CLASS_LOADER_REFERENCE", "dynamic-loading"),
    b"System.loadLibrary": ("NATIVE_LOAD_REFERENCE", "native-loading"),
    b"System.load": ("NATIVE_LOAD_REFERENCE", "native-loading"),
    b"loadLibrary": ("NATIVE_LOAD_REFERENCE", "native-loading"),
    b"Runtime.exec": ("SUBPROCESS_REFERENCE", "subprocess"),
    b"ProcessBuilder": ("SUBPROCESS_REFERENCE", "subprocess"),
    b"WebView": ("WEBVIEW_REFERENCE", "environment"),
    b"Cronet": ("CRONET_REFERENCE", "environment"),
    b"com/google/android/gms": ("GMS_REFERENCE", "environment"),
    b"com/google/firebase": ("FIREBASE_REFERENCE", "environment"),
}

def finding(code, description, source, implication="Unknown", blocks=True, experimental=True):
    return {"code": code, "description": description, "evidence_source": source,
            "coverage_implication": implication, "blocks_protected": blocks,
            "permits_experimental": experimental}

def classify(structurally_valid, findings, mandatory_bypass=False, runtime_proven=False):
    """Pure classifier; real CLI calls never set runtime_proven or mandatory_bypass."""
    if not structurally_valid:
        return "INCOMPATIBLE"
    if mandatory_bypass:
        return "KNOWN_UNSAFE"
    if runtime_proven and not any(item["blocks_protected"] for item in findings):
        return "PROTECTED_ELIGIBLE"
    return "EXPERIMENTAL_ELIGIBLE"

def executable_payload(name):
    low = name.lower()
    ordinary = re.fullmatch(r"classes(?:[2-9]|[1-9][0-9]+)?\.dex", name) is not None
    suspicious_suffix = low.endswith((".dex", ".jar", ".apk", ".exe", ".elf", ".bin"))
    return suspicious_suffix and not ordinary and not low.startswith("lib/")

def scan_archive(path, role):
    raw = path.read_bytes()
    record = {"role": role, "basename": path.name, "sha256": hashlib.sha256(raw).hexdigest(),
              "byte_size": len(raw), "package": None, "version_code": None,
              "version_name": None, "split_name": None, "split_identity_state": "unknown",
              "signer_sha256_digests": None,
              "dex_entries": [], "native_libraries": [], "abis": []}
    findings = []
    try:
        with zipfile.ZipFile(path) as archive:
            bad = archive.testzip()
            if bad:
                raise zipfile.BadZipFile("CRC failure")
            names = sorted(info.filename for info in archive.infolist() if not info.is_dir())
            if "AndroidManifest.xml" not in names:
                findings.append(finding("APK_MANIFEST_MISSING", "Readable archive lacks the required Android APK manifest entry.", "zip-structure", "Unsupported", True, False))
                return record, findings, False
            record["dex_entries"] = [n for n in names if n.lower().endswith(".dex")]
            record["native_libraries"] = [n for n in names if n.startswith("lib/") and n.lower().endswith(".so")]
            record["abis"] = sorted({n.split("/", 2)[1] for n in record["native_libraries"] if n.count("/") >= 2})
            if record["native_libraries"]:
                findings.append(finding("APP_CONTROLLED_NATIVE_PRESENT", "Packaged native libraries require runtime containment evidence.", "native-inventory"))
            for name in names:
                if executable_payload(name):
                    findings.append(finding("OPAQUE_EXECUTABLE_PAYLOAD", "Executable-looking payload occurs outside ordinary APK code placement.", "archive-entry:" + name))
            for dex_name in record["dex_entries"]:
                if not re.fullmatch(r"classes(?:[2-9]|[1-9][0-9]+)?\.dex", dex_name):
                    findings.append(finding("UNUSUAL_DEX_PLACEMENT", "DEX occurs outside ordinary root placement.", "archive-entry:" + dex_name))
                data = archive.read(dex_name)
                for needle, (code, category) in SIGNATURES.items():
                    if needle in data and not any(x["code"] == code for x in findings):
                        findings.append(finding(code, "Static DEX content contains a mechanism/reference indicator.", category))
    except (OSError, zipfile.BadZipFile, RuntimeError):
        findings.append(finding("APK_PARSE_FAILED", "Artifact is not a readable, valid ZIP/APK container.", "zip-parser", "Unknown", True, False))
        return record, findings, False
    return record, findings, True

def tool(name):
    found = shutil.which(name)
    if found:
        return found
    home = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    if home:
        candidates = list(Path(home).glob("cmdline-tools/*/bin/" + name)) + list(Path(home).glob("build-tools/*/" + name))
        if candidates:
            return str(sorted(candidates)[-1])
    raise RuntimeError("required Android SDK inspection tool unavailable: " + name)

def command(argv):
    result = subprocess.run(argv, text=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=False)
    if result.returncode:
        raise RuntimeError(result.stderr.strip() or result.stdout.strip() or "inspection command failed")
    return result.stdout.strip()

def signer_digests(apksigner_output):
    """Return every current signer digest reported by apksigner, normalized."""
    matches = re.findall(r"Signer #\d+ certificate SHA-256 digest:\s*([0-9a-fA-F:]+)", apksigner_output, re.IGNORECASE)
    return sorted({value.replace(":", "").lower() for value in matches})

def add_sdk_metadata(record, path, apkanalyzer, apksigner):
    complete = True
    for key, operation in (("package", "application-id"), ("version_code", "version-code"), ("version_name", "version-name")):
        try:
            record[key] = command([apkanalyzer, "manifest", operation, str(path)]) or None
        except RuntimeError:
            complete = False
    try:
        manifest = command([apkanalyzer, "manifest", "print", str(path)])
        match = re.search(r'\bsplit="([^"]+)"', manifest)
        record["split_name"] = match.group(1) if match else None
        record["split_identity_state"] = "present" if match else "absent"
    except RuntimeError:
        complete = False
    try:
        certs = command([apksigner, "verify", "--print-certs", str(path)])
        observed = signer_digests(certs)
        if observed:
            record["signer_sha256_digests"] = observed
        else:
            complete = False
    except RuntimeError:
        complete = False
    return complete

def generation_id(records):
    normalized = [{key: item[key] for key in ("role", "sha256", "byte_size")}
                  for item in records]
    normalized.sort(key=lambda x: (x["role"], x["sha256"], x["byte_size"]))
    encoded = json.dumps({"schema_version": 1, "analyzer_version": ANALYZER_VERSION, "artifacts": normalized}, sort_keys=True, separators=(",", ":")).encode()
    return hashlib.sha256(encoded).hexdigest()

def evaluate_metadata(records, findings):
    """Report unknown metadata separately from positively proven mismatches."""
    compatible = True
    properties = (
        ("package", "ARTIFACT_PACKAGE_UNPROVEN", "package identity"),
        ("signer_sha256_digests", "ARTIFACT_SIGNER_UNPROVEN", "complete observed signer set"),
        ("version_code", "ARTIFACT_VERSION_UNPROVEN", "version code"),
    )
    for record in records:
        for key, code, description in properties:
            if record[key] is None:
                findings.append(finding(code, "Artifact " + description + " could not be established.", "android-sdk-tools"))
        if record["version_name"] is None:
            findings.append(finding("ARTIFACT_VERSION_NAME_UNPROVEN", "Informational version name could not be established.", "android-sdk-tools"))
        split_state = record["split_identity_state"]
        if split_state == "unknown":
            findings.append(finding("ARTIFACT_SPLIT_IDENTITY_UNPROVEN", "Manifest split identity could not be established.", "manifest"))
        elif record["role"] == "base" and split_state == "present":
            findings.append(finding("BASE_ARTIFACT_IS_SPLIT", "Artifact supplied as the base has an established split identity.", "manifest", "Unsupported", True, False))
            compatible = False
        elif record["role"] == "split" and split_state == "absent":
            findings.append(finding("SPLIT_ARTIFACT_HAS_NO_SPLIT_IDENTITY", "Artifact supplied as a split is positively established as base-like.", "manifest", "Unsupported", True, False))
            compatible = False
    base = records[0]
    mismatches = (
        ("package", "ARTIFACT_PACKAGE_MISMATCH", "Base and split package identities differ.", "manifest"),
        ("signer_sha256_digests", "ARTIFACT_SIGNER_MISMATCH", "Base and split observed signer sets differ.", "certificate"),
        ("version_code", "ARTIFACT_VERSION_MISMATCH", "Base and split version codes differ.", "manifest"),
    )
    for record in records[1:]:
        for key, code, description, source in mismatches:
            if base[key] is not None and record[key] is not None and base[key] != record[key]:
                findings.append(finding(code, description, source, "Unsupported", True, False))
                compatible = False
    established_names = [record["split_name"] for record in records[1:] if record["split_identity_state"] == "present"]
    if len(established_names) != len(set(established_names)):
        findings.append(finding("DUPLICATE_SPLIT_IDENTITY", "Two supplied splits have the same established split identity.", "manifest", "Unsupported", True, False))
        compatible = False
    return compatible

def analyze(base, splits, use_sdk=True):
    paths = [("base", Path(base))] + [("split", Path(p)) for p in splits]
    if not Path(base).is_file():
        raise FileNotFoundError("base APK does not exist")
    records, findings, valid = [], [], True
    sdk = (tool("apkanalyzer"), tool("apksigner")) if use_sdk else None
    for role, path in paths:
        if not path.is_file():
            raise FileNotFoundError(role + " APK does not exist")
        record, found, readable = scan_archive(path, role)
        records.append(record); findings.extend(found); valid &= readable
        if readable and sdk:
            if not add_sdk_metadata(record, path, *sdk):
                findings.append(finding("APK_METADATA_UNPROVEN", "Android SDK inspection could not establish complete APK metadata.", "android-sdk-tools"))
    if records:
        valid &= evaluate_metadata(records, findings)
    findings.append(finding("RUNTIME_MEDIATION_UNPROVEN", "Static analysis cannot prove runtime mediation or containment.", "analysis-boundary"))
    findings = sorted({(x["code"], x["evidence_source"]): x for x in findings}.values(), key=lambda x: (x["code"], x["evidence_source"]))
    return {"schema_version": 1, "analyzer_version": ANALYZER_VERSION,
            "generation_id": generation_id(records), "artifacts": records,
            "findings": findings, "capability_coverage": "Unknown",
            "split_completeness": "Unknown" if splits else "N/A",
            "admission": classify(valid, findings)}

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--base", required=True)
    parser.add_argument("--split", action="append", default=[])
    parser.add_argument("--json", action="store_true", help="emit deterministic JSON")
    args = parser.parse_args()
    try:
        report = analyze(args.base, args.split)
    except (FileNotFoundError, RuntimeError) as error:
        print("ag1 admission analysis failed: " + str(error), file=sys.stderr)
        return 2
    if args.json:
        print(json.dumps(report, sort_keys=True, separators=(",", ":")))
    else:
        print(report["admission"] + " " + report["generation_id"])
    return 0 if report["admission"] != "INCOMPATIBLE" else 1

if __name__ == "__main__":
    raise SystemExit(main())
