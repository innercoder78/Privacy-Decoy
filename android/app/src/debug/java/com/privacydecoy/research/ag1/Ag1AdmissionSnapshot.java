package com.privacydecoy.research.ag1;

/** Immutable manager-owned AG-1A result; no guest-supplied admission authority. */
public final class Ag1AdmissionSnapshot {
    public enum Outcome { PROTECTED_ELIGIBLE, EXPERIMENTAL_ELIGIBLE, KNOWN_UNSAFE, INCOMPATIBLE }
    public final String generation, sha;
    public final Outcome outcome;
    public Ag1AdmissionSnapshot(String generation, String sha, Outcome outcome) {
        this.generation = generation; this.sha = sha; this.outcome = outcome;
    }
    public static boolean digest(String value) { return value != null && value.matches("[0-9a-f]{64}"); }
    public boolean valid() { return digest(generation) && digest(sha) && outcome != null; }
}
