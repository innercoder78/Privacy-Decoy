package com.privacydecoy.research;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Real Binder/UID/device tests, run by the dependency-free instrumentation runner. */
public final class PrototypeTests {
    private final Instrumentation instrumentation;
    private final Context context;
    public PrototypeTests(Instrumentation instrumentation) {
        this.instrumentation = instrumentation; context = instrumentation.getTargetContext();
    }
    private static Map<String, SessionPolicy.Coverage> coverage() {
        Map<String, SessionPolicy.Coverage> values = new HashMap<>();
        for (String key : SessionPolicy.MANDATORY) values.put(key, SessionPolicy.Coverage.VerifiedForPrototype);
        return values;
    }
    private ResearchSession session() throws Exception {
        ResearchSession s = new ResearchSession(context, UUID.randomUUID().toString(), 1, coverage(), true);
        s.connect(); return s;
    }
    public void testUnknownCoverageBlocksBeforeProbeExecution() throws Exception {
        for (SessionPolicy.Coverage value : new SessionPolicy.Coverage[]{SessionPolicy.Coverage.Unknown, SessionPolicy.Coverage.Unsupported}) {
            Map<String, SessionPolicy.Coverage> values = coverage(); values.put("controlledFixture", value);
            assertBlocked(new ResearchSession(context, "blocked", 1, values, true));
        }
        assertBlocked(new ResearchSession(context, "bad/session", 1, coverage(), true));
        assertBlocked(new ResearchSession(context, "failed", 1, coverage(), false));
    }
    private static void assertBlocked(ResearchSession s) throws Exception {
        try (s) {
            boolean blocked = false;
            try { s.connect(); } catch (IllegalStateException expected) { blocked = true; }
            check(blocked, "gate permitted binding");
            blocked = false;
            try { s.execute(new byte[]{0}, new Bundle()); } catch (IllegalStateException expected) { blocked = true; }
            check(blocked && s.entered.get() == 0, "blocked fixture executed");
        }
    }
    public void testSeparateIsolatedInstancesRejectCrossSessionStaleAndMalformedRequests() throws Exception {
        try (ResearchSession a = session(); ResearchSession b = session()) {
            check(a.policy.uid() != Process.myUid(), "manager UID reused");
            check(a.policy.pid() != Process.myPid(), "manager process reused");
            check(a.policy.uid() != b.policy.uid() && a.policy.pid() != b.policy.pid(), "instances share identity");
            check(a.request(a.id, a.epoch, "ping"), "active A denied");
            check(b.request(b.id, b.epoch, "ping"), "active B denied");
            check(!b.request(a.id, a.epoch, "ping"), "cross-session accepted");
            check(!b.requestThroughBrokerOf(a, a.id, a.epoch), "transferred A capability accepted B UID");
            check(!a.request(a.id, a.epoch - 1, "ping"), "stale epoch accepted");
            check(!a.request(a.id, a.epoch, "read-secret"), "unknown operation accepted");
            check(!a.request(null, 0, null), "malformed claim accepted");
            a.revoke(); check(!a.request(a.id, a.epoch, "ping"), "revoked request accepted");
            check(!b.requestThroughBrokerOf(a, a.id, a.epoch), "revoked transferred capability accepted");
            check(a.invocationCount() == 0, "request probes invoked payload");
        }
    }
    public void testIsolatedSessionDeathDoesNotRestoreAuthority() throws Exception {
        try (ResearchSession old = session()) {
            int oldPid = old.policy.pid(); old.killAndAwaitDeath();
            check(old.policy.state() == SessionPolicy.State.DEAD, "unexpected death did not invalidate authority");
            try (ResearchSession replacement = session()) {
                check(replacement.policy.pid() != oldPid, "fresh process not observed");
                check(!replacement.request(old.id, old.epoch, "ping"), "old claim regained authority");
                check(!old.policy.authorize(replacement.policy.uid(), replacement.policy.pid(), old.id, old.epoch, "ping"),
                    "dead session authority restored");
                check(replacement.request(replacement.id, replacement.epoch, "ping"), "fresh session denied");
            }
        }
    }
    public void testRevokedSessionBlocksBeforeEntrypoint() throws Exception {
        try (ResearchSession s = session()) {
            s.revoke(); boolean blocked = false;
            try { s.execute(dex(artifact()), new Bundle()); } catch (IllegalStateException expected) { blocked = true; }
            check(blocked && s.invocationCount() == 0 && s.entered.get() == 0, "revoked fixture executed");
        }
    }
    public void testMalformedDexFailsInitializationWithoutEntrypoint() throws Exception {
        withFixture(new byte[]{0, 1, 2}, (s, result) -> {
            check(result.containsKey("error"), "malformed DEX accepted");
            check(s.entered.get() == 0 && s.invocationCount() == 0, "malformed DEX invoked entry");
            check(s.policy.state() == SessionPolicy.State.REVOKED, "initialization failure retained authority");
        });
    }
    public void testArtifactDerivedDexExecutesWhilePackageRemainsUninstalled() throws Exception {
        byte[] apk = artifact(), before = hash(new byte[0], apk);
        try (ZipInputStream zip = new ZipInputStream(new java.io.ByteArrayInputStream(apk))) {
            boolean found = false; ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().equals("lib/" + Build.SUPPORTED_ABIS[0] + "/libfixture_native.so")) found = true;
            }
            check(found, "generated APK lacks native fixture for device ABI");
        }
        checkUninstalled();
        withFixture(dex(apk), (s, result) -> {
            check(!result.containsKey("error"), "fixture execution failed");
            check(s.entered.get() == 1 && s.invocationCount() == 1, "entrypoint not observed exactly once");
            check(result.getInt("uid") == s.policy.uid() && result.getInt("pid") == s.policy.pid(), "payload identity differs from Binder identity");
            check(result.getInt("uid") != Process.myUid(), "payload ran as manager");
            check(!result.getBoolean("parentExposesManagement"), "payload parent exposes management classes");
        });
        checkUninstalled(); check(Arrays.equals(before, hash(new byte[0], artifact())), "source APK changed");
    }
    public void testKnownGapUninstalledArtifactHasNoNormalLifecycleOrNativeLibraryPath() throws Exception {
        checkUninstalled();
        withFixture(dex(artifact()), (s, result) -> {
            check(!result.containsKey("error"), "fixture execution failed");
            check(!result.getBoolean("applicationCreated"), "unexpected Application lifecycle");
            check(!result.getBoolean("providerCreated"), "unexpected provider lifecycle");
            check(!result.getBoolean("importedNativeLoaded"), "native loader behavior changed; reassess gap");
            check(!"visible".equals(result.getString("probeContext")), "package Context unexpectedly available");
            check(!"visible".equals(result.getString("probeResources")), "package resources unexpectedly available");
            check(!"visible".equals(result.getString("providerResolution")), "declared provider unexpectedly resolved");
        });
    }
    public void testJavaAndNativeDirectOpenCannotReadOrMutateManagementSentinel() throws Exception {
        withFixture(dex(artifact()), (s, result) -> {
            check(!result.containsKey("error"), "fixture execution failed");
            check("denied-or-unavailable".equals(result.getString("javaRead")), "Java sentinel read succeeded");
            check("denied-or-unavailable".equals(result.getString("javaWrite")), "Java sentinel write succeeded");
            int[] n = result.getIntArray("native");
            check(n != null && n.length == 9, "native probe missing");
            check(n[0] == s.policy.uid() && n[2] == s.policy.pid(), "native UID/PID mismatch");
            check(n[3] == 1 && n[4] == 1, "direct open not permission-denied");
            check(n[6] == 1 || n[6] == 2, "manager proc maps accessible or ambiguous");
        });
    }
    public void testKnownGapHostBuildContextAndSanitizedPlatformObservations() throws Exception {
        withFixture(dex(artifact()), (s, result) -> {
            check(!result.containsKey("error"), "fixture execution failed");
            check(result.getBoolean("buildEqualsHost"), "Build comparison changed; reassess observation");
            check(result.getBoolean("hostContextIdentity"), "host Context observation changed");
            check(result.getBoolean("contextExposesManagement"), "Context classloader observation changed");
            for (String key : new String[]{"settings", "platformPackage", "probePackage", "probeContext", "probeResources", "providerResolution", "activityService"}) {
                check(Arrays.asList("visible", "absent", "denied-or-unavailable").contains(result.getString(key)), "invalid sanitized category");
            }
            check(Arrays.asList("same-uid", "denied-or-failed", "timeout").contains(result.getString("subprocess")), "subprocess UID escaped");
            // Only explicitly allowlisted, sanitized observations enter test reports.
            String evidence = "OBSERVATION GAP managerPid=" + Process.myPid() + " managerUid=" + Process.myUid()
                + " buildEqualsHost=" + result.getBoolean("buildEqualsHost")
                + " contextExposesManagement=" + result.getBoolean("contextExposesManagement")
                + " settings=" + result.getString("settings") + " settingsEqualsHost=" + result.getBoolean("settingsEqualsHost")
                + " platformPackage=" + result.getString("platformPackage") + " activityService=" + result.getString("activityService")
                + " subprocess=" + result.getString("subprocess") + " nativeStatus=" + Arrays.toString(result.getIntArray("native"));
            android.util.Log.i("PD_PR4", evidence);
        });
    }
    private interface Check { void run(ResearchSession session, Bundle result) throws Exception; }
    private void withFixture(byte[] dex, Check assertion) throws Exception {
        File sentinel = new File(context.getNoBackupFilesDir(), "pr4-synthetic-sentinel");
        byte[] state = new byte[32]; new SecureRandom().nextBytes(state);
        Files.write(sentinel.toPath(), state);
        try (ResearchSession s = session()) {
            Bundle input = new Bundle(); input.putString("sentinel", sentinel.getAbsolutePath()); input.putInt("managerPid", Process.myPid());
            byte[] nonce = new byte[32]; new SecureRandom().nextBytes(nonce); input.putByteArray("nonce", nonce);
            input.putByteArray("buildHash", hash(nonce, Build.FINGERPRINT.getBytes(StandardCharsets.UTF_8)));
            String settings = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (settings != null) input.putByteArray("settingsHash", hash(nonce, settings.getBytes(StandardCharsets.UTF_8)));
            Bundle result = s.execute(dex, input);
            check(Arrays.equals(state, Files.readAllBytes(sentinel.toPath())), "persistent sentinel mutated");
            assertion.run(s, result);
        } finally { Files.deleteIfExists(sentinel.toPath()); }
    }
    private byte[] artifact() throws Exception {
        try (java.io.InputStream input = instrumentation.getContext().getAssets().open("probe-app-debug.apk")) {
            return readBounded(input, 16 * 1024 * 1024);
        }
    }
    private static byte[] dex(byte[] apk) throws Exception {
        try (ZipInputStream zip = new ZipInputStream(new java.io.ByteArrayInputStream(apk))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("classes.dex".equals(entry.getName())) return readBounded(zip, 4 * 1024 * 1024);
            }
        }
        throw new AssertionError("generated fixture has no DEX");
    }
    private void checkUninstalled() throws Exception {
        try { context.getPackageManager().getApplicationInfo("com.privacydecoy.probe", 0); }
        catch (PackageManager.NameNotFoundException expected) { return; }
        throw new AssertionError("fixture package must not be installed");
    }
    private static byte[] hash(byte[] nonce, byte[] value) throws Exception {
        MessageDigest d = MessageDigest.getInstance("SHA-256"); d.update(nonce); return d.digest(value);
    }
    private static byte[] readBounded(java.io.InputStream input, int limit) throws Exception {
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; int count;
        while ((count = input.read(buffer)) != -1) {
            if (output.size() + count > limit) throw new AssertionError("fixture exceeds research bound");
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }
    static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
