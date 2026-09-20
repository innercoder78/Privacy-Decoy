package com.privacydecoy.research;

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
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/** All authorization state stays in the management process. Never trust payload UID fields. */
public final class ResearchSession implements ResearchBoundary {
    public final SessionPolicy policy;
    public final String id;
    public final long epoch;
    public final AtomicInteger entered = new AtomicInteger();
    private final Context context;
    private final ExecutorService ipc = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "pr4-bounded-ipc"); thread.setDaemon(true); return thread;
    });
    private volatile IBinder remote;
    private boolean bound;
    private final CountDownLatch connected = new CountDownLatch(1);
    private final CountDownLatch death = new CountDownLatch(1);
    private final ServiceConnection connection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder binder) {
            remote = binder;
            try { binder.linkToDeath(() -> { policy.died(); death.countDown(); }, 0); }
            catch (android.os.RemoteException e) { policy.died(); }
            connected.countDown();
        }
        @Override public void onServiceDisconnected(ComponentName name) { policy.died(); death.countDown(); }
        @Override public void onNullBinding(ComponentName name) { policy.died(); connected.countDown(); }
        @Override public void onBindingDied(ComponentName name) { policy.died(); death.countDown(); }
    };
    private final Binder broker = new Binder() {
        @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            if (code != Wire.INIT && code != Wire.REQUEST) return false;
            boolean accepted = false;
            try {
                data.enforceInterface(Wire.TOKEN);
                Bundle b = data.readBundle(getClass().getClassLoader());
                if (b == null || data.dataAvail() != 0 || data.dataSize() > 4096) throw new IllegalArgumentException();
                int callerUid = Binder.getCallingUid(), callerPid = Binder.getCallingPid();
                if (code == Wire.INIT) {
                    accepted = id.equals(b.getString("session")) && epoch == b.getLong("epoch")
                        && policy.register(callerUid, callerPid, Process.myUid());
                } else {
                    accepted = policy.authorize(callerUid, callerPid, b.getString("session"), b.getLong("epoch"), b.getString("op"));
                    if (accepted && "entered".equals(b.getString("op"))) entered.incrementAndGet();
                }
            } catch (RuntimeException ignored) { /* Invalid messages deny without raw exception logging. */ }
            Bundle result = new Bundle(); result.putBoolean("accepted", accepted);
            reply.writeNoException(); reply.writeBundle(result);
            return true;
        }
    };

    public ResearchSession(Context context, String id, long epoch, Map<String, SessionPolicy.Coverage> coverage,
            boolean initialized) {
        this.context = context; this.id = id; this.epoch = epoch;
        policy = new SessionPolicy(id, epoch);
        policy.prepare(coverage, initialized);
    }
    public void connect() throws Exception {
        if (!policy.attempt()) throw new IllegalStateException("Prototype coverage blocked");
        try {
            bound = context.bindIsolatedService(new Intent(context, IsolatedProbeService.class),
                Context.BIND_AUTO_CREATE, id, context.getMainExecutor(), connection);
            if (!bound || !connected.await(10, TimeUnit.SECONDS) || remote == null) throw new IllegalStateException("Bind failed");
            Bundle input = claim(id, epoch, ""); input.putBinder("broker", broker);
            if (!call(Wire.INIT, input).getBoolean("accepted")) throw new IllegalStateException("Registration failed");
        } catch (Exception e) { close(); throw e; }
    }
    public static Bundle claim(String id, long epoch, String operation) {
        Bundle b = new Bundle(); b.putString("session", id); b.putLong("epoch", epoch); b.putString("op", operation); return b;
    }
    @Override public synchronized Bundle execute(byte[] dex, Bundle input) throws Exception {
        if (policy.state() != SessionPolicy.State.ACTIVE || dex.length == 0 || dex.length > 4 * 1024 * 1024)
            throw new IllegalStateException("Execution blocked");
        try (SharedMemory memory = SharedMemory.create("controlled-probe-dex", dex.length)) {
            ByteBuffer buffer = memory.mapReadWrite();
            try { buffer.put(dex); } finally { SharedMemory.unmap(buffer); }
            if (!memory.setProtect(OsConstants.PROT_READ)) throw new IllegalStateException("Read-only transfer failed");
            Bundle request = new Bundle(input); request.putParcelable("dex", memory);
            Bundle result = call(Wire.RUN, request);
            if (result == null || result.containsKey("error")) policy.revoke();
            return result;
        } catch (Exception e) {
            policy.revoke();
            throw e;
        }
    }
    public boolean request(String claim, long generation, String op) throws Exception {
        return call(Wire.REQUEST, claim(claim, generation, op)).getBoolean("accepted");
    }
    /** Deliberately transfer a test broker handle to falsify UID binding, not just name matching. */
    public boolean requestThroughBrokerOf(ResearchSession owner, String claim, long generation) throws Exception {
        Bundle input = claim(claim, generation, "ping"); input.putBinder("targetBroker", owner.broker);
        return call(Wire.REQUEST, input).getBoolean("accepted");
    }

    public IBinder researchLifetime() { return remote; }
    private Runnable networkRevocation = () -> {};
    public synchronized void onNetworkRevocation(Runnable callback) { networkRevocation = callback; }
    public Bundle directNetwork(String operation) throws Exception {
        Bundle input=new Bundle(); input.putString("op",operation); return call(Wire.DIRECT_NETWORK,input);
    }
    public Bundle network(IBinder endpoint, Bundle request, int transaction) throws Exception {
        Bundle input=new Bundle(request); input.putBinder("networkBroker",endpoint);
        input.putInt("transaction",transaction); return call(Wire.NETWORK,input);
    }
    public int invocationCount() throws Exception { return call(Wire.COUNT, new Bundle()).getInt("count"); }
    public void killAndAwaitDeath() throws Exception {
        // Fault injection: no prior revoke and no synchronous call failure masking death handling.
        Wire.simulateUnexpectedDeath(remote);
        if (!death.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Death not observed");
    }
    @Override public synchronized void revoke() { policy.revoke(); networkRevocation.run(); }
    @Override public synchronized void close() {
        policy.revoke(); networkRevocation.run();
        if (bound) { context.unbindService(connection); bound = false; }
        ipc.shutdownNow();
    }
    private synchronized Bundle call(int operation, Bundle input) throws Exception {
        Future<Bundle> pending = ipc.submit(() -> Wire.call(remote, operation, input));
        try { return pending.get(10, TimeUnit.SECONDS); }
        catch (ExecutionException e) {
            policy.revoke();
            if (e.getCause() instanceof Exception) throw (Exception) e.getCause();
            throw new IllegalStateException("Research IPC failed");
        } catch (Exception e) {
            pending.cancel(true); close(); throw e;
        }
    }
}
