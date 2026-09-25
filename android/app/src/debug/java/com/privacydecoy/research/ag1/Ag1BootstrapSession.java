package com.privacydecoy.research.ag1;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.SharedMemory;
import android.system.OsConstants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Manager authority for one immutable analyzed APK and one fresh isolated session. */
public final class Ag1BootstrapSession implements AutoCloseable {
    public final Ag1LaunchPolicy policy;
    private final Context context;
    private final byte[] apk;
    private final Ag1LaunchPolicy.Consent consent;
    private final String requestedGeneration;
    private final ExecutorService ipc = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "ag1-bounded-ipc"); t.setDaemon(true); return t;
    });
    private final CountDownLatch connected = new CountDownLatch(1), death = new CountDownLatch(1);
    private volatile IBinder remote;
    private boolean bound, acknowledged;
    private int bindings, transfers, extractions;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            remote = binder;
            try { binder.linkToDeath(() -> { policy.died(); death.countDown(); }, 0); }
            catch (android.os.RemoteException failure) { policy.died(); death.countDown(); }
            connected.countDown();
        }
        @Override public void onServiceDisconnected(ComponentName name) { policy.died(); death.countDown(); }
        @Override public void onBindingDied(ComponentName name) { policy.died(); death.countDown(); connected.countDown(); }
        @Override public void onNullBinding(ComponentName name) { policy.died(); connected.countDown(); }
    };
    private final Binder authority = new Binder() {
        @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            boolean accepted = false;
            try {
                data.enforceInterface(Ag1BootstrapWire.TOKEN);
                if (data.dataSize() > 4096) throw new SecurityException();
                Bundle input = data.readBundle(getClass().getClassLoader());
                if (input == null || data.dataAvail() != 0 || !Ag1BootstrapWire.same(Ag1BootstrapWire.metadata(policy), input)) throw new SecurityException();
                int uid = Binder.getCallingUid(), pid = Binder.getCallingPid();
                if (code == Ag1BootstrapWire.REGISTER) accepted = policy.register(uid, pid, Process.myUid(), Process.myPid());
                else if (policy.identity(uid, pid, input.getString("id"), input.getLong("sessionEpoch"))) {
                    if (code == Ag1BootstrapWire.READY) {
                        ArrayList<String> prerequisites = input.getStringArrayList("prerequisites");
                        accepted = policy.ready(prerequisites == null ? null : new HashSet<>(prerequisites));
                    } else if (code == Ag1BootstrapWire.CONSUME) {
                        accepted = policy.consume(uid, pid, input.getString("id"), input.getLong("sessionEpoch"));
                    }
                }
            } catch (RuntimeException failure) { /* Fixed denial; no raw exception output. */ }
            Bundle result = new Bundle(); result.putBoolean("accepted", accepted);
            reply.writeNoException(); reply.writeBundle(result); return true;
        }
    };
    public Ag1BootstrapSession(Context context, byte[] artifact, Ag1AdmissionSnapshot admission,
            String requestedGeneration, Ag1ExecutionClass execution, Ag1LaunchPolicy.Consent consent,
            long consentEpoch, String id, long sessionEpoch) {
        if (artifact == null || artifact.length == 0 || artifact.length > 16 * 1024 * 1024)
            throw new IllegalArgumentException("AG-1 artifact bound");
        this.context = context; this.apk = artifact.clone(); this.consent = consent;
        this.requestedGeneration = requestedGeneration;
        policy = new Ag1LaunchPolicy(admission, execution, id, sessionEpoch, consentEpoch);
    }
    /** Metadata-only bind, also exposed to negative bootstrap tests. */
    public synchronized void bind() throws Exception {
        String sha = hash(ByteBuffer.wrap(apk));
        if (!policy.validate(requestedGeneration, sha, consent)
                || !generation(sha, apk.length).equals(policy.admission.generation)) {
            policy.revoke(); throw new IllegalStateException("AG-1 launch denied");
        }
        try {
            bindings++;
            bound = context.bindIsolatedService(new Intent(context, Ag1BootstrapService.class),
                Context.BIND_AUTO_CREATE, policy.id, context.getMainExecutor(), connection);
            if (!bound || !connected.await(10, TimeUnit.SECONDS) || remote == null) throw new IllegalStateException("AG-1 bind failed");
            Bundle input = Ag1BootstrapWire.metadata(policy); input.putBinder("authority", authority);
            if (!call(Ag1BootstrapWire.INIT, input).getBoolean("accepted")) throw new IllegalStateException("AG-1 registration denied");
        } catch (Exception failure) { close(); throw failure; }
    }
    public synchronized boolean arm(Set<String> prerequisites) throws Exception {
        if (policy.state() != Ag1LaunchPolicy.State.REGISTERED) return false;
        Bundle input = Ag1BootstrapWire.metadata(policy);
        if (prerequisites != null) input.putStringArrayList("prerequisites", new ArrayList<>(prerequisites));
        Bundle result = call(Ag1BootstrapWire.ARM, input);
        acknowledged = result.getBoolean("accepted") && result.getLong("readyAt") > 0
            && policy.state() == Ag1LaunchPolicy.State.READY;
        if (!acknowledged) policy.revoke();
        return acknowledged;
    }
    public synchronized Bundle execute() throws Exception {
        if (!acknowledged || !policy.beginTransfer()) throw new IllegalStateException("AG-1 transfer denied");
        try {
            // No ZIP/DEX extraction until the isolated service has acknowledged READY.
            byte[] dex = extract(); extractions++;
            try (SharedMemory memory = SharedMemory.create("ag1-controlled-dex", dex.length)) {
                ByteBuffer buffer = memory.mapReadWrite();
                try { buffer.put(dex); } finally { SharedMemory.unmap(buffer); }
                if (!memory.setProtect(OsConstants.PROT_READ)) throw new IllegalStateException("AG-1 read-only transfer failed");
                Bundle input = Ag1BootstrapWire.metadata(policy); input.putParcelable("dex", memory);
                input.putInt("size", dex.length); input.putString("dexSha", hash(ByteBuffer.wrap(dex)));
                transfers++;
                Bundle result = call(Ag1BootstrapWire.RUN, input);
                if (!result.getBoolean("accepted")) policy.revoke();
                return result;
            }
        } catch (Exception failure) { policy.revoke(); throw failure; }
    }
    /** Metadata-only adversarial RUN: intentionally bypass manager dispatch, never sends bytes. */
    public synchronized Bundle probeRun(String id, long epoch) throws Exception {
        Bundle input = Ag1BootstrapWire.metadata(policy); input.putString("id", id); input.putLong("sessionEpoch", epoch);
        Bundle result = call(Ag1BootstrapWire.RUN, input);
        if (!result.getBoolean("accepted")) revoke();
        return result;
    }
    public synchronized Bundle observations() throws Exception { return call(Ag1BootstrapWire.COUNT, new Bundle()); }
    /** Controlled helper denies before descriptor transfer or any secondary loader construction. */
    public synchronized Bundle controlledSecondary(byte[] bytes, Ag1ExecutableAuthorization authorization,
            String generation, String id, long epoch) throws Exception {
        byte[] snapshot = bytes.clone();
        if (authorization == null || !authorization.belongsTo(policy)
                || !authorization.consume(generation, id, epoch, hash(ByteBuffer.wrap(snapshot)),
                Ag1ExecutableAuthorization.Kind.DEX, policy.execution)) {
            Bundle denied = observations(); denied.putBoolean("authorized", false); return denied;
        }
        Bundle result = transferSecondary(snapshot, false); result.putBoolean("authorized", true); return result;
    }
    /** Explicit synthetic experiment. No call to Ag1ExecutableAuthorization. */
    public synchronized Bundle directSecondary(byte[] bytes) throws Exception {
        return transferSecondary(bytes.clone(), true);
    }
    private Bundle transferSecondary(byte[] bytes, boolean direct) throws Exception {
        if (!policy.executableSessionActive() || bytes.length == 0 || bytes.length > Ag1BootstrapWire.MAX_DEX)
            throw new IllegalStateException("AG-1 secondary transfer denied");
        try (SharedMemory memory = SharedMemory.create("ag1-secondary", bytes.length)) {
            ByteBuffer buffer = memory.mapReadWrite();
            try { buffer.put(bytes); } finally { SharedMemory.unmap(buffer); }
            if (!memory.setProtect(OsConstants.PROT_READ)) throw new IllegalStateException("AG-1 secondary read-only failed");
            Bundle input = Ag1BootstrapWire.metadata(policy); input.putParcelable("dex", memory);
            input.putString("dexSha", hash(ByteBuffer.wrap(bytes))); input.putBoolean("direct", direct);
            return call(Ag1BootstrapWire.SECONDARY, input);
        }
    }
    public synchronized void revoke() { policy.revoke(); acknowledged = false; }
    public synchronized void killAndAwaitDeath() throws Exception {
        // Reconnection may replace remote; only this authorized target is being killed.
        final IBinder target = remote;
        if (target == null) throw new IllegalStateException("AG-1 death not observed");
        final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        try {
            Ag1BootstrapWire.kill(target);
        } catch (android.os.RemoteException failure) {
            if (target.isBinderAlive() && target.pingBinder()) throw failure;
            policy.died(); death.countDown(); return;
        }
        while (death.getCount() != 0) {
            if (!target.isBinderAlive() || !target.pingBinder()) {
                policy.died(); death.countDown(); return;
            }
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) throw new IllegalStateException("AG-1 death not observed");
            if (death.await(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(50)), TimeUnit.NANOSECONDS)) return;
        }
    }
    public synchronized int bindings() { return bindings; }
    public synchronized int transfers() { return transfers; }
    public synchronized int extractions() { return extractions; }
    @Override public synchronized void close() {
        revoke();
        if (bound) { context.unbindService(connection); bound = false; }
        ipc.shutdownNow();
    }
    private Bundle call(int code, Bundle input) throws Exception {
        Future<Bundle> pending = ipc.submit(() -> Ag1BootstrapWire.call(remote, code, input));
        try { return pending.get(10, TimeUnit.SECONDS); }
        catch (Exception failure) { pending.cancel(true); close(); throw new IllegalStateException("AG-1 IPC failed"); }
    }
    private byte[] extract() throws Exception {
        byte[] dex = null; int count = 0;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(apk))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".so")) throw new IllegalStateException("AG-1 native artifact denied");
                if (name.endsWith(".dex")) {
                    count++;
                    if (!"classes.dex".equals(name)) throw new IllegalStateException("AG-1 single DEX required");
                    dex = readBounded(zip, Ag1BootstrapWire.MAX_DEX);
                }
            }
        }
        if (count != 1 || dex == null || dex.length == 0) throw new IllegalStateException("AG-1 sole DEX missing");
        return dex;
    }
    public static byte[] readBounded(InputStream input, int limit) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream(); byte[] buffer = new byte[8192]; int n;
        while ((n = input.read(buffer)) != -1) {
            if (output.size() + n > limit) throw new IllegalStateException("AG-1 input exceeds bound");
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
    public static String hash(ByteBuffer bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); digest.update(bytes);
        StringBuilder hex = new StringBuilder();
        for (byte b : digest.digest()) hex.append(String.format(java.util.Locale.ROOT, "%02x", b & 255));
        return hex.toString();
    }
    /** Exact AG-1A ag1a-2 single-base canonical JSON, independently bound on device. */
    public static String generation(String sha, int size) throws Exception {
        String normalized = "{\"analyzer_version\":\"ag1a-2\",\"artifacts\":[{\"byte_size\":" + size
            + ",\"role\":\"base\",\"sha256\":\"" + sha + "\"}],\"schema_version\":1}";
        return hash(ByteBuffer.wrap(normalized.getBytes(StandardCharsets.UTF_8)));
    }
}
