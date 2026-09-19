package com.privacydecoy.research;

import java.util.Map;
import java.util.Set;

/** Manager-owned, pure state machine. Prototype scope is not production coverage. */
public final class SessionPolicy {
    public enum Coverage { VerifiedForPrototype, Unsupported, Unknown }
    public enum State { NEW, READY, ACTIVE, REVOKED, DEAD, BLOCKED }
    public static final Set<String> MANDATORY = Set.of("controlledFixture", "isolatedTransport", "brokerIdentity");
    private State state = State.NEW;
    private final String id;
    private long epoch;
    private int uid = -1;
    private int pid = -1;
    private int attempts;

    public SessionPolicy(String id, long epoch) { this.id = id; this.epoch = epoch; }
    public synchronized boolean prepare(Map<String, Coverage> coverage, boolean initialized) {
        if (state != State.NEW) return false;
        boolean valid = id != null && id.matches("[a-zA-Z0-9-]{1,64}") && epoch > 0 && initialized;
        valid &= coverage != null && MANDATORY.stream().allMatch(k -> coverage.get(k) == Coverage.VerifiedForPrototype);
        state = valid ? State.READY : State.BLOCKED;
        return valid;
    }
    public synchronized boolean attempt() {
        if (state != State.READY || attempts >= 2) return false;
        attempts++;
        return true;
    }
    public synchronized boolean register(int observedUid, int observedPid, int managerUid) {
        if (state != State.READY || observedUid <= 0 || observedUid == managerUid || observedPid <= 0) return false;
        uid = observedUid;
        pid = observedPid;
        state = State.ACTIVE;
        return true;
    }
    public synchronized boolean authorize(int callerUid, int callerPid, String claim, long generation, String op) {
        return state == State.ACTIVE && uid == callerUid && pid == callerPid && id.equals(claim)
            && epoch == generation && ("ping".equals(op) || "entered".equals(op));
    }
    public synchronized void revoke() { epoch++; state = State.REVOKED; }
    public synchronized void died() { epoch++; state = State.DEAD; }
    public synchronized State state() { return state; }
    public synchronized int uid() { return uid; }
    public synchronized int pid() { return pid; }
}
