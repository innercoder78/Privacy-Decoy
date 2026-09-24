package com.privacydecoy.research.ag1;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import com.privacydecoy.research.PrototypeTestRunner;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static com.privacydecoy.research.ag1.Ag1AdmissionSnapshot.Outcome.*;
import static com.privacydecoy.research.ag1.Ag1ExecutionClass.*;

/** Eight device tests; only testExactExperimentalExecutionAfterBarrier executes guest code. */
public final class Ag1RuntimeTests {
    private final Context context;
    private final byte[] apk;
    private final String generation, sha;
    private static final String OTHER = "0".repeat(64);
    public Ag1RuntimeTests(Instrumentation instrumentation) throws Exception {
        context = instrumentation.getTargetContext();
        Bundle args = ((PrototypeTestRunner) instrumentation).researchArguments();
        generation = args.getString("ag1Generation"); sha = args.getString("ag1Sha");
        check(Ag1AdmissionSnapshot.digest(generation) && Ag1AdmissionSnapshot.digest(sha), "bounded analyzer arguments missing");
        check("EXPERIMENTAL_ELIGIBLE".equals(args.getString("ag1Admission")), "real admission must be Experimental only");
        try (java.io.InputStream input = instrumentation.getContext().getAssets().open("ag1-precode-fixture-debug.apk")) {
            apk = Ag1BootstrapSession.readBounded(input, 16 * 1024 * 1024);
        }
        // Independent device hash precedes every extraction, binding and guest transfer.
        check(sha.equals(Ag1BootstrapSession.hash(ByteBuffer.wrap(apk))), "embedded artifact differs from analyzer");
        check(generation.equals(Ag1BootstrapSession.generation(sha, apk.length)), "embedded generation differs from analyzer");
        checkUninstalled();
    }
    private Ag1LaunchPolicy.Consent consent(String gen, String hash, long epoch) {
        return new Ag1LaunchPolicy.Consent(gen, hash, epoch, EXPERIMENTAL);
    }
    private Ag1BootstrapSession create(byte[] bytes, Ag1AdmissionSnapshot.Outcome outcome, Ag1ExecutionClass execution,
            String admittedGeneration, String admittedSha, String requested, Ag1LaunchPolicy.Consent consent) {
        return new Ag1BootstrapSession(context, bytes, new Ag1AdmissionSnapshot(admittedGeneration, admittedSha, outcome),
            requested, execution, consent, 2, "ag1_" + UUID.randomUUID().toString().replace("-", ""), 1);
    }
    private Ag1BootstrapSession fresh() {
        return create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,sha,generation,consent(generation,sha,2));
    }
    private void blocked(Ag1BootstrapSession s) throws Exception {
        try (s) {
            boolean denied = false;
            try { s.bind(); } catch (IllegalStateException expected) { denied = true; }
            check(denied && s.bindings() == 0 && s.transfers() == 0 && s.extractions() == 0, "invalid launch crossed manager gate");
            denyExecute(s);
        }
    }
    private void denyExecute(Ag1BootstrapSession s) throws Exception {
        boolean denied = false;
        try { s.execute(); } catch (IllegalStateException expected) { denied = true; }
        check(denied && s.transfers() == 0 && s.extractions() == 0, "denied state exposed guest bytes");
    }
    private void zero(Ag1BootstrapSession s) throws Exception {
        Bundle evidence = s.observations();
        check(evidence.containsKey("loaders") && evidence.containsKey("resolutions")
            && evidence.containsKey("invocations") && evidence.containsKey("bytesAt"), "negative evidence missing");
        check(evidence.getInt("loaders") == 0 && evidence.getInt("resolutions") == 0
            && evidence.getInt("invocations") == 0 && evidence.getLong("bytesAt") == 0, "negative path reached guest code");
        check(s.transfers() == 0 && s.extractions() == 0, "negative path transferred guest bytes");
    }
    public void testLaunchPolicyHardStopsBeforeBinding() throws Exception {
        blocked(create(apk,EXPERIMENTAL_ELIGIBLE,PROTECTED,generation,sha,generation,consent(generation,sha,2)));
        for (Ag1LaunchPolicy.Consent c : new Ag1LaunchPolicy.Consent[]{null,consent(OTHER,sha,2),consent(generation,sha,1),consent(generation,OTHER,2)})
            blocked(create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,sha,generation,c));
        for (Ag1AdmissionSnapshot.Outcome outcome : new Ag1AdmissionSnapshot.Outcome[]{KNOWN_UNSAFE,INCOMPATIBLE})
            for (Ag1ExecutionClass execution : Ag1ExecutionClass.values())
                blocked(create(apk,outcome,execution,generation,sha,generation,consent(generation,sha,2)));
    }
    public void testArtifactAndGenerationMismatchBeforeBinding() throws Exception {
        blocked(create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,OTHER,generation,consent(generation,OTHER,2)));
        blocked(create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,sha,OTHER,consent(generation,sha,2)));
        byte[] changed = apk.clone(); changed[0] ^= 1;
        blocked(create(changed,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,sha,generation,consent(generation,sha,2)));
        // Even mutually matching claimed generation/consent cannot override content-derived identity.
        blocked(create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,OTHER,sha,OTHER,consent(OTHER,sha,2)));
    }
    public void testUpdatedGenerationCannotInheritConsent() throws Exception {
        blocked(create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,OTHER,sha,OTHER,consent(generation,sha,2)));
        Ag1LaunchPolicy.Consent reused=consent(generation,sha,2);
        try (Ag1BootstrapSession old=create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,sha,generation,reused)) {
            old.bind(); check(old.arm(Ag1LaunchPolicy.REQUIRED), "bootstrap denied"); old.revoke(); zero(old);
            blocked(create(apk,EXPERIMENTAL_ELIGIBLE,EXPERIMENTAL,generation,sha,generation,reused));
        }
    }
    public void testMissingPrerequisiteNeverReady() throws Exception {
        for (String missing : Ag1LaunchPolicy.REQUIRED) {
            try (Ag1BootstrapSession s = fresh()) {
                s.bind(); Set<String> prerequisites = new HashSet<>(Ag1LaunchPolicy.REQUIRED); prerequisites.remove(missing);
                check(!s.arm(prerequisites), "missing prerequisite reached READY");
                check(s.observations().getLong("readyAt") == 0, "missing prerequisite recorded READY");
                denyExecute(s); zero(s);
            }
        }
        try (Ag1BootstrapSession s = fresh()) { s.bind(); check(!s.arm(null), "unknown prerequisites reached READY"); zero(s); }
    }
    public void testRunBeforeReadyHasZeroGuestClassLoads() throws Exception {
        try (Ag1BootstrapSession s = fresh()) {
            s.bind(); denyExecute(s);
            check(!s.probeRun(s.policy.id,s.policy.sessionEpoch).getBoolean("accepted"), "service accepted pre-READY RUN");
            denyExecute(s); zero(s);
        }
    }
    public void testRevocationAndReplayBeforeTransfer() throws Exception {
        try (Ag1BootstrapSession s = fresh()) {
            s.bind(); check(s.arm(Ag1LaunchPolicy.REQUIRED), "bootstrap denied"); s.revoke();
            denyExecute(s);
            check(!s.probeRun(s.policy.id,s.policy.sessionEpoch).getBoolean("accepted"), "revoked service accepted RUN");
            check(!s.probeRun(s.policy.id,s.policy.sessionEpoch).getBoolean("accepted"), "replayed session accepted RUN");
            check(!s.arm(Ag1LaunchPolicy.REQUIRED), "revoked session rearmed"); zero(s);
        }
    }
    private interface CheckedAction {
        void run() throws Exception;
    }
    private static void lifecyclePhase(String fixedMessage, CheckedAction action) throws Exception {
        try {
            action.run();
        } catch (IllegalStateException failure) {
            throw new AssertionError(fixedMessage);
        }
    }
    public void testProcessDeathInvalidatesAuthorization() throws Exception {
        try (Ag1BootstrapSession old = fresh()) {
            lifecyclePhase("death setup bind failed", old::bind);
            lifecyclePhase("death setup arm failed", () -> check(old.arm(Ag1LaunchPolicy.REQUIRED), "bootstrap denied"));
            lifecyclePhase("death setup observation failed", () -> zero(old));
            lifecyclePhase("death observation failed", old::killAndAwaitDeath);
            check(old.policy.state() == Ag1LaunchPolicy.State.DEAD, "death retained authority");
            denyExecute(old);
            try (Ag1BootstrapSession replacement = fresh()) {
                lifecyclePhase("replacement bind failed", replacement::bind);
                lifecyclePhase("replacement arm failed", () -> check(replacement.arm(Ag1LaunchPolicy.REQUIRED), "replacement denied"));
                check(replacement.policy.pid() != old.policy.pid(), "replacement reused process");
                lifecyclePhase("stale claim probe failed", () ->
                    check(!replacement.probeRun(old.policy.id,old.policy.sessionEpoch).getBoolean("accepted"), "dead claim regained authority"));
                denyExecute(replacement);
                lifecyclePhase("replacement observation failed", () -> zero(replacement));
            }
        }
    }
    public void testExactExperimentalExecutionAfterBarrier() throws Exception {
        try (Ag1BootstrapSession s = fresh()) {
            s.bind();
            check(s.policy.uid() != Process.myUid() && s.policy.pid() != Process.myPid(), "isolated identity equals manager");
            check(s.arm(Ag1LaunchPolicy.REQUIRED), "exact Experimental bootstrap denied"); zero(s);
            Bundle result = s.execute(); check(result.getBoolean("accepted"), "controlled execution failed");
            long ready = result.getLong("readyAt"), bytes = result.getLong("bytesAt"), loader = result.getLong("loaderAt"), resolution = result.getLong("resolutionAt");
            check(ready > 0 && ready < bytes && bytes <= loader && loader <= resolution, "pre-code ordering violated");
            long[] events = result.getLongArray("events"); check(events != null && events.length == 6, "fixture events missing");
            long previous = resolution;
            for (long event : events) { check(event > ready && event >= previous, "controlled event preceded barrier"); previous = event; }
            check(result.getInt("loaders") == 1 && result.getInt("resolutions") == 3
                && result.getInt("invocations") == 1 && s.transfers() == 1 && s.extractions() == 1, "execution was not exactly once");
            boolean denied=false; try { s.execute(); } catch (IllegalStateException expected) { denied=true; }
            check(denied, "second invocation authorized");
            check(!s.probeRun(s.policy.id,s.policy.sessionEpoch).getBoolean("accepted"), "service replay authorized");
            check(s.observations().getInt("invocations") == 1, "replay ran guest again");
        }
        checkUninstalled();
    }
    private void checkUninstalled() throws Exception {
        try { context.getPackageManager().getApplicationInfo("com.privacydecoy.ag1.precode",0); }
        catch (PackageManager.NameNotFoundException expected) { return; }
        throw new AssertionError("controlled guest must never be installed");
    }
    private static void check(boolean condition, String fixedMessage) { if (!condition) throw new AssertionError(fixedMessage); }
}
