package com.privacydecoy.research;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;

public final class SessionPolicyTest {
    static Map<String, SessionPolicy.Coverage> coverage() {
        Map<String, SessionPolicy.Coverage> result = new HashMap<>();
        for (String key : SessionPolicy.MANDATORY) result.put(key, SessionPolicy.Coverage.VerifiedForPrototype);
        return result;
    }
    @Test public void unknownUnsupportedAndMissingCoverageBlockBeforeDispatch() {
        for (SessionPolicy.Coverage value : new SessionPolicy.Coverage[]{SessionPolicy.Coverage.Unknown, SessionPolicy.Coverage.Unsupported, null}) {
            Map<String, SessionPolicy.Coverage> values = coverage(); values.put("controlledFixture", value);
            SessionPolicy policy = new SessionPolicy("a", 1);
            assertFalse(policy.prepare(values, true)); assertFalse(policy.attempt());
            assertFalse(policy.register(90001, 200, 10001));
        }
    }
    @Test public void malformedSessionAndFailedInitializationBlock() {
        assertFalse(new SessionPolicy("", 1).prepare(coverage(), true));
        assertFalse(new SessionPolicy("bad-instance", 1).prepare(coverage(), true));
        assertTrue(new SessionPolicy("valid_instance.1", 1).prepare(coverage(), true));
        assertFalse(new SessionPolicy("a", 0).prepare(coverage(), true));
        assertFalse(new SessionPolicy("a", 1).prepare(coverage(), false));
        assertFalse(new SessionPolicy("a", 1).prepare(null, true));
    }
    @Test public void crossSessionStaleCallerAndUnknownOperationAreRejected() {
        SessionPolicy policy = active();
        assertTrue(policy.authorize(90001, 200, "a", 1, "ping"));
        assertFalse(policy.authorize(90002, 201, "a", 1, "ping"));
        assertFalse(policy.authorize(90001, 201, "a", 1, "ping"));
        assertFalse(policy.authorize(90001, 200, "b", 1, "ping"));
        assertFalse(policy.authorize(90001, 200, "a", 2, "ping"));
        assertFalse(policy.authorize(90001, 200, "a", 1, "read-secret"));
        assertFalse(policy.authorize(90001, 200, null, 1, null));
    }
    @Test public void revocationAndDeathAreTerminalEvenWhenUidIsReused() {
        for (boolean death : new boolean[]{false, true}) {
            SessionPolicy policy = active(); if (death) policy.died(); else policy.revoke();
            assertFalse(policy.authorize(90001, 200, "a", 1, "ping"));
            assertFalse(policy.register(90001, 201, 10001));
            assertFalse(policy.prepare(coverage(), true)); assertFalse(policy.attempt());
        }
    }
    @Test public void retriesAreBoundedAndManagerUidCannotRegister() {
        SessionPolicy policy = new SessionPolicy("a", 1); assertTrue(policy.prepare(coverage(), true));
        assertTrue(policy.attempt()); assertTrue(policy.attempt()); assertFalse(policy.attempt());
        assertFalse(policy.register(10001, 200, 10001));
    }
    private static SessionPolicy active() {
        SessionPolicy policy = new SessionPolicy("a", 1); assertTrue(policy.prepare(coverage(), true));
        assertTrue(policy.attempt()); assertTrue(policy.register(90001, 200, 10001)); return policy;
    }
}
