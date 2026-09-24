package com.privacydecoy.research.ag1;

import org.junit.Test;
import static org.junit.Assert.*;

public final class Ag1ExecutableAuthorizationTest {
    private static final String GENERATION = "a".repeat(64), SHA = "b".repeat(64), DEX = "c".repeat(64);
    private static String hash(byte[] bytes) throws Exception {
        return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(bytes));
    }
    private Ag1LaunchPolicy session() {
        Ag1LaunchPolicy policy = new Ag1LaunchPolicy(new Ag1AdmissionSnapshot(GENERATION, SHA,
            Ag1AdmissionSnapshot.Outcome.EXPERIMENTAL_ELIGIBLE), Ag1ExecutionClass.EXPERIMENTAL, "synthetic", 1, 2);
        assertTrue(policy.validate(GENERATION, SHA,
            new Ag1LaunchPolicy.Consent(GENERATION, SHA, 2, Ag1ExecutionClass.EXPERIMENTAL)));
        assertTrue(policy.register(100, 200, 300, 400));
        assertTrue(policy.ready(Ag1LaunchPolicy.REQUIRED));
        assertTrue(policy.beginTransfer()); assertTrue(policy.consume(100, 200, "synthetic", 1));
        return policy;
    }
    private boolean consume(Ag1ExecutableAuthorization authorization, String generation, String digest, long epoch) {
        return authorization.consume(generation, "synthetic", epoch, digest,
            Ag1ExecutableAuthorization.Kind.DEX, Ag1ExecutionClass.EXPERIMENTAL);
    }
    @Test public void exactThenReplay() {
        Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(session(), DEX);
        assertTrue(consume(a, GENERATION, DEX, 1)); assertFalse(consume(a, GENERATION, DEX, 1));
    }
    @Test public void changedBytesAndUnknownIdentity() throws Exception {
        String original = hash(new byte[]{1,2,3});
        String changed = hash(new byte[]{1,2,4});
        assertNotEquals(original, changed);
        Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(session(), original);
        assertFalse(consume(a, GENERATION, changed, 1)); assertFalse(consume(a, GENERATION, DEX, 1));
        assertTrue(consume(a, GENERATION, original, 1));
    }
    @Test public void staleGenerationsAndWrongSession() {
        Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(session(), DEX);
        assertFalse(consume(a, SHA, DEX, 1)); assertFalse(consume(a, GENERATION, DEX, 2));
        assertFalse(a.consume(GENERATION, "other", 1, DEX, Ag1ExecutableAuthorization.Kind.DEX, Ag1ExecutionClass.EXPERIMENTAL));
    }
    @Test public void revokedOrDeadSession() {
        Ag1LaunchPolicy p = session(); Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(p, DEX);
        p.revoke(); assertFalse(consume(a, GENERATION, DEX, 1));
        p = session(); a = new Ag1ExecutableAuthorization(p, DEX);
        p.died(); assertFalse(consume(a, GENERATION, DEX, 1));
    }
    @Test public void wrongKindOrExecutionClass() {
        Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(session(), DEX);
        assertFalse(a.consume(GENERATION, "synthetic", 1, DEX, null, Ag1ExecutionClass.EXPERIMENTAL));
        assertFalse(a.consume(GENERATION, "synthetic", 1, DEX, Ag1ExecutableAuthorization.Kind.DEX, Ag1ExecutionClass.PROTECTED));
    }
    @Test public void cannotBorrowAnotherSessionAuthority() {
        Ag1LaunchPolicy p = session(); Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(p, DEX);
        assertTrue(a.belongsTo(p)); assertFalse(a.belongsTo(session()));
    }
    @Test public void concurrentConsumptionIsOneUse() throws Exception {
        Ag1ExecutableAuthorization a = new Ag1ExecutableAuthorization(session(), DEX);
        java.util.concurrent.atomic.AtomicInteger accepted = new java.util.concurrent.atomic.AtomicInteger();
        Runnable attempt = () -> { if (consume(a, GENERATION, DEX, 1)) accepted.incrementAndGet(); };
        Thread first = new Thread(attempt), second = new Thread(attempt);
        first.start(); second.start(); first.join(); second.join(); assertEquals(1, accepted.get());
    }
}
