package com.privacydecoy.probe;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Process;
import android.provider.Settings;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/** Safe adversarial fixture. Returns only booleans, categories and test PID/UID. */
public final class ProbeEntry {
    public static boolean applicationCreated;
    public static boolean providerCreated;
    private static native int nativeMarker();

    public static Bundle run(Context hostContext, Bundle input) throws Exception {
        Bundle result = new Bundle();
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        try {
            data.writeInterfaceToken("com.privacydecoy.research.v1");
            Bundle claim = new Bundle(); claim.putString("session", input.getString("session"));
            claim.putLong("epoch", input.getLong("epoch")); claim.putString("op", "entered");
            data.writeBundle(claim);
            if (!input.getBinder("broker").transact(3, data, reply, 0)) throw new SecurityException();
            reply.readException();
            if (!reply.readBundle(ProbeEntry.class.getClassLoader()).getBoolean("accepted")) throw new SecurityException();
        } finally { data.recycle(); reply.recycle(); }
        result.putInt("pid", Process.myPid()); result.putInt("uid", Process.myUid());
        result.putBoolean("applicationCreated", applicationCreated);
        result.putBoolean("providerCreated", providerCreated);
        result.putBoolean("parentExposesManagement", resolves(ProbeEntry.class.getClassLoader()));
        // A supplied host Context is itself a capability; parent separation does not sanitize it.
        result.putBoolean("contextExposesManagement", resolves(hostContext.getClass().getClassLoader()));
        result.putBoolean("hostContextIdentity", "com.privacydecoy.app".equals(hostContext.getPackageName()));
        result.putBoolean("hostProcessName", android.app.Application.getProcessName().startsWith("com.privacydecoy.app"));
        result.putString("javaRead", read(input.getString("sentinel")));
        result.putString("javaWrite", write(input.getString("sentinel")));
        result.putBoolean("buildEqualsHost", Arrays.equals(input.getByteArray("buildHash"),
            hash(input.getByteArray("nonce"), Build.FINGERPRINT)));
        try {
            String value = Settings.Secure.getString(hostContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            result.putString("settings", value == null ? "absent" : "visible");
            result.putBoolean("settingsEqualsHost", value != null && Arrays.equals(input.getByteArray("settingsHash"),
                hash(input.getByteArray("nonce"), value)));
        } catch (RuntimeException e) { result.putString("settings", "denied-or-unavailable"); }
        result.putString("platformPackage", attempt(() -> hostContext.getPackageManager().getApplicationInfo("android", 0)));
        result.putString("probePackage", attempt(() -> hostContext.getPackageManager().getApplicationInfo("com.privacydecoy.probe", 0)));
        result.putString("probeContext", attempt(() -> hostContext.createPackageContext("com.privacydecoy.probe", 0)));
        result.putString("probeResources", attempt(() -> hostContext.getPackageManager().getResourcesForApplication("com.privacydecoy.probe")));
        result.putString("providerResolution", attempt(() -> {
            if (hostContext.getPackageManager().resolveContentProvider("com.privacydecoy.probe.marker", 0) == null)
                throw new android.content.pm.PackageManager.NameNotFoundException();
        }));
        result.putString("activityService", attempt(() -> {
            if (hostContext.getSystemService(Context.ACTIVITY_SERVICE) == null) throw new IllegalStateException();
        }));
        try { System.loadLibrary("fixture_native"); result.putBoolean("importedNativeLoaded", nativeMarker() == 4); }
        catch (LinkageError | SecurityException e) { result.putBoolean("importedNativeLoaded", false); }
        java.lang.Process child = null;
        try {
            child = new ProcessBuilder("/system/bin/id", "-u").redirectErrorStream(true).start();
            if (!child.waitFor(2, TimeUnit.SECONDS)) {
                result.putString("subprocess", "timeout");
            } else if (child.exitValue() != 0) {
                result.putString("subprocess", "denied-or-failed");
            } else {
                byte[] bytes = new byte[32];
                int length = child.getInputStream().read(bytes);
                boolean same = length > 0 && Integer.parseInt(new String(bytes, 0, length, StandardCharsets.US_ASCII).trim()) == Process.myUid();
                result.putString("subprocess", same ? "same-uid" : "different-uid-GAP");
            }
        } catch (Exception e) { result.putString("subprocess", "denied-or-failed"); }
        finally { if (child != null) child.destroyForcibly(); }
        return result;
    }
    private static boolean resolves(ClassLoader loader) {
        try { Class.forName("com.privacydecoy.research.ResearchSession", false, loader); return true; }
        catch (ClassNotFoundException e) { return false; }
    }
    private static String read(String path) {
        try (FileInputStream stream = new FileInputStream(path)) { stream.read(); return "accessible"; }
        catch (Exception e) { return "denied-or-unavailable"; }
    }
    private static String write(String path) {
        // Append a synthetic byte if access unexpectedly succeeds; manager verifies persistent state.
        try (FileOutputStream stream = new FileOutputStream(path, true)) { stream.write(0); return "accessible"; }
        catch (Exception e) { return "denied-or-unavailable"; }
    }
    private interface Observation { void run() throws Exception; }
    private static String attempt(Observation observation) {
        try { observation.run(); return "visible"; }
        catch (android.content.pm.PackageManager.NameNotFoundException e) { return "absent"; }
        catch (Exception e) { return "denied-or-unavailable"; }
    }
    private static byte[] hash(byte[] nonce, String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); digest.update(nonce);
        return digest.digest(value.getBytes(StandardCharsets.UTF_8));
    }
}
