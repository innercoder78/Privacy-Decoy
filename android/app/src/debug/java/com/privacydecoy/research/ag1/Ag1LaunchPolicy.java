package com.privacydecoy.research.ag1;

import java.util.Set;

/** Pure research policy. READY describes bootstrap prerequisites, never privacy mediation. */
public final class Ag1LaunchPolicy {
    public enum State { NEW, VALIDATED, REGISTERED, READY, TRANSFERRING, USED, BLOCKED, REVOKED, DEAD }
    public static final Set<String> REQUIRED = Set.of("artifact", "admission", "execution",
        "identity", "session", "transferGate");
    public static final class Consent {
        public final String generation, sha;
        public final long epoch;
        public final Ag1ExecutionClass execution;
        private boolean revoked;
        public Consent(String generation, String sha, long epoch, Ag1ExecutionClass execution) {
            this.generation = generation; this.sha = sha; this.epoch = epoch; this.execution = execution;
        }
        public synchronized void revoke() { revoked = true; }
        public synchronized boolean active() { return !revoked; }
    }
    public final Ag1AdmissionSnapshot admission;
    public final Ag1ExecutionClass execution;
    public final String id;
    public final long sessionEpoch, consentEpoch;
    private State state = State.NEW;
    private int uid = -1, pid = -1;
    private Consent boundConsent;
    public Ag1LaunchPolicy(Ag1AdmissionSnapshot admission, Ag1ExecutionClass execution,
            String id, long sessionEpoch, long consentEpoch) {
        this.admission = admission; this.execution = execution; this.id = id;
        this.sessionEpoch = sessionEpoch; this.consentEpoch = consentEpoch;
    }
    public synchronized boolean validate(String generation, String sha, Consent consent) {
        if (state != State.NEW) return false;
        boolean valid = admission != null && admission.valid() && execution != null
            && id != null && id.matches("[a-zA-Z0-9_.]{1,64}") && sessionEpoch > 0 && consentEpoch > 0;
        if (valid) {
            valid = admission.generation.equals(generation) && admission.sha.equals(sha);
            if (execution == Ag1ExecutionClass.PROTECTED) {
                valid &= admission.outcome == Ag1AdmissionSnapshot.Outcome.PROTECTED_ELIGIBLE;
            } else {
                valid &= admission.outcome == Ag1AdmissionSnapshot.Outcome.EXPERIMENTAL_ELIGIBLE
                    && consent != null && consent.active() && consent.epoch == consentEpoch
                    && consent.execution == Ag1ExecutionClass.EXPERIMENTAL
                    && admission.generation.equals(consent.generation) && admission.sha.equals(consent.sha);
            }
        }
        state = valid ? State.VALIDATED : State.BLOCKED;
        if (valid) boundConsent = consent;
        return valid;
    }
    public synchronized boolean register(int observedUid, int observedPid, int managerUid, int managerPid) {
        if (state != State.VALIDATED || !consentActive() || observedUid <= 0 || observedPid <= 0
                || observedUid == managerUid || observedPid == managerPid) return false;
        uid = observedUid; pid = observedPid; state = State.REGISTERED; return true;
    }
    public synchronized boolean ready(Set<String> positivePrerequisites) {
        if (state != State.REGISTERED || !consentActive()) return false;
        if (positivePrerequisites == null || !positivePrerequisites.containsAll(REQUIRED)) {
            state = State.BLOCKED; return false;
        }
        state = State.READY; return true;
    }
    public synchronized boolean beginTransfer() {
        if (state != State.READY || !consentActive()) return false;
        state = State.TRANSFERRING; return true;
    }
    public synchronized boolean consume(int callerUid, int callerPid, String claim, long epoch) {
        if (state != State.TRANSFERRING || !consentActive() || !identity(callerUid, callerPid, claim, epoch)) return false;
        state = State.USED; return true;
    }
    public synchronized boolean identity(int callerUid, int callerPid, String claim, long epoch) {
        return uid == callerUid && pid == callerPid && id.equals(claim) && sessionEpoch == epoch;
    }
    private boolean consentActive() { return execution == Ag1ExecutionClass.PROTECTED || boundConsent != null && boundConsent.active(); }
    public synchronized void revoke() {
        if (boundConsent != null) boundConsent.revoke();
        if (state != State.DEAD) state = State.REVOKED;
    }
    public synchronized void died() { if (boundConsent != null) boundConsent.revoke(); state = State.DEAD; }
    public synchronized State state() { return state; }
    public synchronized int uid() { return uid; }
    public synchronized int pid() { return pid; }
}
