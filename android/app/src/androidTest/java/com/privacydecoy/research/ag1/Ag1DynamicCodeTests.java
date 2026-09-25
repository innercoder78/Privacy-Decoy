package com.privacydecoy.research.ag1;

import android.app.Instrumentation;
import android.os.Bundle;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;
import com.privacydecoy.research.PrototypeTestRunner;

/** Synthetic compatibility observations; no desired direct-loader outcome is asserted. */
public final class Ag1DynamicCodeTests {
    private final Instrumentation instrumentation;
    private final byte[] primary, secondary;
    private final String generation, sha, secondarySha;
    private static final String OTHER = "f".repeat(64);
    public Ag1DynamicCodeTests(Instrumentation instrumentation) throws Exception {
        this.instrumentation = instrumentation;
        Bundle args = ((PrototypeTestRunner) instrumentation).researchArguments();
        generation = args.getString("ag1Generation"); sha = args.getString("ag1Sha");
        secondarySha = args.getString("ag1SecondarySha");
        check(Ag1AdmissionSnapshot.digest(generation) && Ag1AdmissionSnapshot.digest(sha)
            && Ag1AdmissionSnapshot.digest(secondarySha), "IDENTITY_ARGUMENTS");
        check("EXPERIMENTAL_ELIGIBLE".equals(args.getString("ag1Admission")), "EXPERIMENTAL_REQUIRED");
        primary = asset("ag1-precode-fixture-dynamic.apk", 16 * 1024 * 1024);
        secondary = asset("ag1-secondary.dex", Ag1BootstrapWire.MAX_DEX);
        check(sha.equals(Ag1BootstrapSession.hash(ByteBuffer.wrap(primary)))
            && generation.equals(Ag1BootstrapSession.generation(sha, primary.length))
            && secondarySha.equals(Ag1BootstrapSession.hash(ByteBuffer.wrap(secondary))), "IDENTITY_MISMATCH");
    }
    private byte[] asset(String name, int bound) throws Exception {
        try (java.io.InputStream input = instrumentation.getContext().getAssets().open(name)) {
            return Ag1BootstrapSession.readBounded(input, bound);
        }
    }
    private Ag1BootstrapSession fresh() throws Exception {
        Ag1BootstrapSession session = new Ag1BootstrapSession(instrumentation.getTargetContext(), primary,
            new Ag1AdmissionSnapshot(generation, sha, Ag1AdmissionSnapshot.Outcome.EXPERIMENTAL_ELIGIBLE),
            generation, Ag1ExecutionClass.EXPERIMENTAL,
            new Ag1LaunchPolicy.Consent(generation, sha, 2, Ag1ExecutionClass.EXPERIMENTAL),
            2, "ag1c_" + UUID.randomUUID().toString().replace("-", ""), 1);
        try {
            session.bind(); check(session.arm(Ag1LaunchPolicy.REQUIRED), "READY_REQUIRED");
            check(session.execute().getBoolean("accepted"), "PRIMARY_EXECUTION_REQUIRED");
            check(Arrays.equals(new int[5], session.observations().getIntArray("secondary")), "INITIAL_STAGES");
            return session;
        } catch (Exception | AssertionError failure) { session.close(); throw failure; }
    }
    private void denied(String scenario) throws Exception {
        try (Ag1BootstrapSession session = fresh()) {
            byte[] bytes = secondary.clone();
            if (scenario.equals("CHANGED")) bytes[bytes.length - 1] ^= 1;
            Ag1ExecutableAuthorization auth = new Ag1ExecutableAuthorization(session.policy,
                scenario.equals("UNKNOWN") ? OTHER : secondarySha);
            if (scenario.equals("REVOKED")) session.revoke();
            if (scenario.equals("REPLAY")) {
                Bundle first = session.controlledSecondary(bytes, auth, generation, session.policy.id, 1);
                check(first.getBoolean("authorized") && first.getBoolean("accepted"), "FIRST_AUTHORIZATION");
                check(Arrays.equals(new int[]{1,1,1,1,0}, first.getIntArray("secondary")), "VALID_PAYLOAD_REQUIRED");
            }
            int[] before = session.observations().getIntArray("secondary");
            int attempts = session.observations().getInt("secondaryAttempts");
            Bundle result = session.controlledSecondary(bytes, auth,
                scenario.equals("STALE_ARTIFACT") ? OTHER : generation, session.policy.id,
                scenario.equals("STALE_SESSION") ? 2 : 1);
            check(!result.getBoolean("authorized"), "DENIAL_REQUIRED");
            check(attempts == result.getInt("secondaryAttempts")
                && attempts == session.observations().getInt("secondaryAttempts"), "DENIED_ATTEMPT_CHANGED");
            check(Arrays.equals(before, result.getIntArray("secondary"))
                && Arrays.equals(before, session.observations().getIntArray("secondary")), "DENIED_STAGES_CHANGED");
            evidence(scenario, "DENIED", new int[5]);
        }
    }
    public void testChangedBytes() throws Exception { denied("CHANGED"); }
    public void testUnknownIdentity() throws Exception { denied("UNKNOWN"); }
    public void testStaleArtifact() throws Exception { denied("STALE_ARTIFACT"); }
    public void testStaleSession() throws Exception { denied("STALE_SESSION"); }
    public void testRevokedSession() throws Exception { denied("REVOKED"); }
    public void testReplay() throws Exception { denied("REPLAY"); }
    public void testExactIdentity() throws Exception {
        try (Ag1BootstrapSession session = fresh()) {
            Bundle result = session.controlledSecondary(secondary,
                new Ag1ExecutableAuthorization(session.policy, secondarySha), generation, session.policy.id, 1);
            check(result.getBoolean("authorized") && result.getBoolean("accepted"), "EXACT_REQUIRED");
            int[] stages = result.getIntArray("secondary");
            check(Arrays.equals(new int[]{1,1,1,1,0}, stages), "VALID_PAYLOAD_REQUIRED");
            evidence("EXACT", "AUTHORIZED", stages);
        }
    }
    public void testDirectAndroidLoader() throws Exception {
        try (Ag1BootstrapSession session = fresh()) {
            Bundle result = session.directSecondary(secondary);
            check(result.getBoolean("accepted"), "DIRECT_OBSERVATION_MISSING");
            int[] stages = result.getIntArray("secondary");
            check(stages != null && stages.length == 5, "STAGES_MISSING");
            for (int i = 0; i < 4; i++) {
                check(stages[i] == 0 || stages[i] == 1, "STAGE_RANGE");
                if (i > 0) check(stages[i] <= stages[i-1], "STAGE_ORDER");
            }
            check(stages[4] >= 0 && stages[4] <= 5 && (stages[4] != 0 || stages[3] == 1), "RESULT_RANGE");
            evidence("DIRECT", "OBSERVED", stages);
        }
    }
    private void evidence(String scenario, String decision, int[] stages) {
        Bundle result = new Bundle();
        result.putString("ag1c", scenario + ":" + decision + ":" + stages[0] + stages[1] + stages[2] + stages[3] + ":" + stages[4]);
        instrumentation.sendStatus(2, result);
    }
    private static void check(boolean condition, String fixed) { if (!condition) throw new AssertionError(fixed); }
}
