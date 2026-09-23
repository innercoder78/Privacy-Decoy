package com.privacydecoy.research.ag1;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;
import static com.privacydecoy.research.ag1.Ag1AdmissionSnapshot.Outcome.*;
import static com.privacydecoy.research.ag1.Ag1ExecutionClass.*;

public final class Ag1LaunchPolicyTest {
    private static final String G = "a".repeat(64), H = "b".repeat(64), OTHER = "c".repeat(64);
    private Ag1LaunchPolicy policy(Ag1AdmissionSnapshot.Outcome outcome, Ag1ExecutionClass execution) {
        return new Ag1LaunchPolicy(new Ag1AdmissionSnapshot(G, H, outcome), execution, "synthetic", 1, 2);
    }
    private Ag1LaunchPolicy.Consent consent(String generation, String sha, long epoch) {
        return new Ag1LaunchPolicy.Consent(generation, sha, epoch, EXPERIMENTAL);
    }
    private Ag1LaunchPolicy valid() {
        Ag1LaunchPolicy p = policy(EXPERIMENTAL_ELIGIBLE, EXPERIMENTAL);
        assertTrue("exact consent denied", p.validate(G, H, consent(G, H, 2))); return p;
    }
    @Test public void experimentalCannotLaunchProtected() {
        assertFalse("experimental promoted", policy(EXPERIMENTAL_ELIGIBLE, PROTECTED).validate(G, H, consent(G,H,2)));
    }
    @Test public void missingWrongGenerationAndStaleConsentDeny() {
        for (Ag1LaunchPolicy.Consent c : new Ag1LaunchPolicy.Consent[]{null, consent(OTHER,H,2), consent(G,OTHER,2), consent(G,H,1),
                new Ag1LaunchPolicy.Consent(G,H,2,PROTECTED)}) {
            assertFalse("invalid consent accepted", policy(EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL).validate(G,H,c));
        }
    }
    @Test public void artifactAndGenerationMismatchDeny() {
        assertFalse("artifact mismatch accepted", policy(EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL).validate(G,OTHER,consent(G,H,2)));
        assertFalse("generation mismatch accepted", policy(EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL).validate(OTHER,H,consent(G,H,2)));
    }
    @Test public void knownUnsafeAndIncompatibleHaveNoOverride() {
        for (Ag1AdmissionSnapshot.Outcome outcome : new Ag1AdmissionSnapshot.Outcome[]{KNOWN_UNSAFE,INCOMPATIBLE})
            for (Ag1ExecutionClass execution : Ag1ExecutionClass.values())
                assertFalse("hard stop overridden", policy(outcome,execution).validate(G,H,consent(G,H,2)));
    }
    @Test public void everyPrerequisiteRequiredAndNoEarlyTransfer() {
        for (String missing : Ag1LaunchPolicy.REQUIRED) {
            Ag1LaunchPolicy p = valid(); assertFalse("early transfer", p.beginTransfer());
            assertTrue("identity denied", p.register(100,200,10,20));
            Set<String> prerequisites = new HashSet<>(Ag1LaunchPolicy.REQUIRED); prerequisites.remove(missing);
            assertFalse("missing prerequisite accepted", p.ready(prerequisites));
            assertFalse("blocked transfer", p.beginTransfer());
        }
        Ag1LaunchPolicy p=valid(); p.register(100,200,10,20); assertFalse("unknown prerequisites accepted",p.ready(null));
    }
    @Test public void identityAndReplayAreBound() {
        Ag1LaunchPolicy p=valid(); assertFalse("manager registered",p.register(10,20,10,20));
        assertTrue(p.register(100,200,10,20)); assertTrue(p.ready(Ag1LaunchPolicy.REQUIRED)); assertTrue(p.beginTransfer());
        assertFalse("foreign UID",p.consume(101,200,"synthetic",1)); assertFalse("foreign PID",p.consume(100,201,"synthetic",1));
        assertFalse("foreign session",p.consume(100,200,"other",1)); assertFalse("stale epoch",p.consume(100,200,"synthetic",2));
        assertTrue(p.consume(100,200,"synthetic",1)); assertFalse("replay",p.consume(100,200,"synthetic",1));
        assertFalse("second transfer",p.beginTransfer());
    }
    @Test public void revocationAndDeathAreTerminal() {
        for (boolean dead : new boolean[]{false,true}) {
            Ag1LaunchPolicy p=valid(); p.register(100,200,10,20); p.ready(Ag1LaunchPolicy.REQUIRED);
            if(dead) p.died(); else p.revoke();
            assertFalse("terminal transfer",p.beginTransfer()); assertFalse("terminal replay",p.consume(100,200,"synthetic",1));
            assertFalse("terminal revalidation",p.validate(G,H,consent(G,H,2))); assertFalse("terminal rebind",p.register(100,201,10,20));
        }
    }
    @Test public void updatedGenerationCannotInheritConsent() {
        Ag1LaunchPolicy p=new Ag1LaunchPolicy(new Ag1AdmissionSnapshot(OTHER,H,EXPERIMENTAL_ELIGIBLE),EXPERIMENTAL,"updated",2,2);
        assertFalse("update inherited consent",p.validate(OTHER,H,consent(G,H,2)));
        Ag1LaunchPolicy.Consent revoked=consent(G,H,2); revoked.revoke();
        assertFalse("revoked consent reused",policy(EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL).validate(G,H,revoked));
        Ag1LaunchPolicy.Consent live=consent(G,H,2);
        Ag1LaunchPolicy active=policy(EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL);
        assertTrue(active.validate(G,H,live)); active.register(100,200,10,20); active.ready(Ag1LaunchPolicy.REQUIRED);
        live.revoke(); assertFalse("revoked consent transferred",active.beginTransfer());
        Ag1LaunchPolicy.Consent pending=consent(G,H,2);
        Ag1LaunchPolicy transfer=policy(EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL);
        assertTrue(transfer.validate(G,H,pending)); transfer.register(100,200,10,20); transfer.ready(Ag1LaunchPolicy.REQUIRED);
        assertTrue(transfer.beginTransfer()); pending.revoke();
        assertFalse("revoked reservation consumed",transfer.consume(100,200,"synthetic",1));
    }
    @Test public void hypotheticalProtectedStateIsOnlyPolicyModel() {
        assertTrue("synthetic future state denied",policy(PROTECTED_ELIGIBLE,PROTECTED).validate(G,H,null));
        assertFalse("classes conflated",policy(PROTECTED_ELIGIBLE,EXPERIMENTAL).validate(G,H,consent(G,H,2)));
    }
    @Test public void unknownAndMalformedAuthorityDeny() {
        assertFalse("unknown admission accepted",policy(null,EXPERIMENTAL).validate(G,H,consent(G,H,2)));
        assertFalse("unknown execution accepted",policy(EXPERIMENTAL_ELIGIBLE,null).validate(G,H,consent(G,H,2)));
        Ag1LaunchPolicy p=new Ag1LaunchPolicy(new Ag1AdmissionSnapshot("bad",H,EXPERIMENTAL_ELIGIBLE),EXPERIMENTAL,"bad/id",0,0);
        assertFalse("malformed state accepted",p.validate(G,H,null));
    }
}
