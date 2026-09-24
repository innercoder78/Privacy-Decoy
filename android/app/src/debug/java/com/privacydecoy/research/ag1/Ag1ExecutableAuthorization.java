package com.privacydecoy.research.ag1;

/** Manager-owned, one-use research authorization. It does not intercept Android loaders. */
public final class Ag1ExecutableAuthorization {
    public enum Kind { DEX }
    private final Ag1LaunchPolicy session;
    private final String digest;
    private boolean consumed;

    public Ag1ExecutableAuthorization(Ag1LaunchPolicy session, String digest) {
        if (session == null || !Ag1AdmissionSnapshot.digest(digest))
            throw new IllegalArgumentException("Invalid executable authority");
        this.session = session;
        this.digest = digest;
    }

    public synchronized boolean consume(String generation, String id, long epoch,
            String actualDigest, Kind kind, Ag1ExecutionClass execution) {
        // Lock against concurrent session revocation while making the decision.
        synchronized (session) {
            if (consumed || !session.executableSessionActive()
                    || session.admission.outcome != Ag1AdmissionSnapshot.Outcome.EXPERIMENTAL_ELIGIBLE
                    || session.execution != Ag1ExecutionClass.EXPERIMENTAL
                    || execution != session.execution || kind != Kind.DEX
                    || !session.admission.generation.equals(generation)
                    || !session.id.equals(id) || session.sessionEpoch != epoch
                    || !digest.equals(actualDigest)) return false;
            consumed = true;
            return true;
        }
    }
    boolean belongsTo(Ag1LaunchPolicy candidate) { return session == candidate; }
}
