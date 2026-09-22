package com.privacydecoy.research.managedprobe;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.system.ErrnoException;
import android.system.OsConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Locale;
import java.util.TimeZone;

final class Evidence {
    static { System.loadLibrary("managed_probe"); }
    private static native String nativeCategories(String management, String peer, String fingerprint);

    static synchronized void record(Context c, String event) {
        try {
            // Engineering fixtures only: never generate a fallback from host state.
            String nonce = fixture(c, "test-nonce", 64);
            String peerUser = fixture(c, "peer-shell-user", 10);
            if (!nonce.matches("[0-9a-f]{64}") || !peerUser.matches("[0-9]{1,10}")) {
                throw new IOException("invalid research fixture");
            }
            long serial = c.getSystemService(UserManager.class)
                    .getSerialNumberForUser(android.os.Process.myUserHandle());
            if (serial < 0) throw new IOException("unknown user serial");
            String[] raw = {Build.FINGERPRINT, Build.MODEL, Build.MANUFACTURER, Build.BRAND,
                    Build.DEVICE, Build.PRODUCT, Build.HARDWARE,
                    Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID),
                    Locale.getDefault().toLanguageTag(), TimeZone.getDefault().getID()};
            StringBuilder b = new StringBuilder("schema=2\nevent=").append(event)
                    .append("\nuid=").append(android.os.Process.myUid())
                    .append("\nuser_serial=").append(serial)
                    .append("\npid=").append(android.os.Process.myPid())
                    .append("\nnonce_source=SEEDED_TEST\nnonce_hash=").append(hash(nonce)).append('\n');
            for (int i = 0; i < raw.length; i++) {
                if (raw[i] == null || raw[i].isEmpty()) throw new IOException("missing surface");
                b.append("surface_").append(i).append("_hash=")
                        .append(hash(nonce + ":" + raw[i])).append('\n');
            }
            File management = new File("/data/user/0/com.privacydecoy.app/no_backup/pr8-sentinel");
            String peerPackage = c.getPackageName().endsWith(".a")
                    ? "com.privacydecoy.research.managedprobe.b" : "com.privacydecoy.research.managedprobe.a";
            File peer = new File("/data/user/" + peerUser + "/" + peerPackage + "/files/sentinel");
            b.append("java_management_read=").append(access(management, false))
                    .append("\njava_management_write=").append(access(management, true))
                    .append("\njava_peer_read=").append(access(peer, false))
                    .append("\njava_peer_write=").append(access(peer, true)).append('\n');
            b.append(nativeCategories(management.getPath(), peer.getPath(), Build.FINGERPRINT));
            // Append makes accidental repeated lifecycle execution detectable as duplicate keys.
            try (FileOutputStream out = c.openFileOutput("evidence-" + event, Context.MODE_APPEND)) {
                out.write(b.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception ignored) {
            // Do not leak identifiers, nonce, or raw platform exception text into logcat.
            throw new IllegalStateException("research evidence unavailable");
        }
    }

    private static String fixture(Context c, String name, int maximum) throws IOException {
        File file = new File(c.getFilesDir(), name);
        if (file.length() < 1 || file.length() > maximum) throw new IOException("invalid fixture size");
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.US_ASCII);
    }

    private static String access(File path, boolean write) {
        try {
            if (write) {
                try (FileOutputStream out = new FileOutputStream(path, true)) { out.write(0x53); }
            } else {
                try (FileInputStream in = new FileInputStream(path)) { in.read(); }
            }
            return "ACCESSIBLE";
        } catch (SecurityException denied) {
            return "PERMISSION_DENIED";
        } catch (IOException failure) {
            // Android wraps public ErrnoException in stream IOExceptions. Never parse raw messages.
            for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                if (cause instanceof ErrnoException) {
                    int errno = ((ErrnoException) cause).errno;
                    if (errno == OsConstants.EACCES || errno == OsConstants.EPERM) return "PERMISSION_DENIED";
                    if (errno == OsConstants.ENOENT) return "ABSENT";
                    break;
                }
            }
            return "OTHER_ERROR";
        }
    }

    private static String hash(String value) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
