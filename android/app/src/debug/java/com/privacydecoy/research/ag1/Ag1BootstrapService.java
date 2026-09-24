package com.privacydecoy.research.ag1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.SharedMemory;
import android.os.SystemClock;
import android.system.OsConstants;
import dalvik.system.InMemoryDexClassLoader;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.ArrayList;

/** Trusted pre-code bootstrap only. Post-entry observations are controlled-fixture evidence. */
public final class Ag1BootstrapService extends Service {
    private IBinder authority;
    private Bundle metadata;
    private int managerUid = -1, managerPid = -1;
    private boolean terminal, ready, used;
    private long readyAt, bytesAt, loaderAt, resolutionAt;
    private int loaders, resolutions, invocations;
    private final Binder endpoint = new Binder() {
        @Override protected synchronized boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            if (code < Ag1BootstrapWire.INIT || code > Ag1BootstrapWire.KILL) return false;
            Bundle result = new Bundle();
            try {
                data.enforceInterface(Ag1BootstrapWire.TOKEN);
                if (data.dataSize() > 8192) throw new SecurityException();
                Bundle input = data.readBundle(getClass().getClassLoader());
                if (input == null || data.dataAvail() != 0) throw new SecurityException();
                if (code == Ag1BootstrapWire.INIT) {
                    if (authority != null || terminal || input.containsKey("dex")) throw new SecurityException();
                    managerUid = Binder.getCallingUid(); managerPid = Binder.getCallingPid();
                    if (managerUid == Process.myUid() || managerPid == Process.myPid()) throw new SecurityException();
                    authority = input.getBinder("authority");
                    metadata = new Bundle(input); metadata.remove("authority");
                    if (!Ag1BootstrapWire.same(metadata, metadata)
                            || !Ag1AdmissionSnapshot.digest(metadata.getString("sha"))
                            || !Ag1AdmissionSnapshot.digest(metadata.getString("generation"))) throw new SecurityException();
                    if (!ask(Ag1BootstrapWire.REGISTER, metadata)) throw new SecurityException();
                    authority.linkToDeath(() -> Process.killProcess(Process.myPid()), 0);
                    result.putBoolean("accepted", true);
                } else {
                    if (authority == null || Binder.getCallingUid() != managerUid
                            || Binder.getCallingPid() != managerPid) throw new SecurityException();
                    if (code == Ag1BootstrapWire.COUNT) result = observations();
                    else if (code == Ag1BootstrapWire.KILL) {
                        terminal = true;
                        final int pid = Process.myPid();
                        Process.killProcess(pid);
                        // Controlled research fault injection: terminate even if killProcess returns.
                        Runtime.getRuntime().halt(0);
                        result.putBoolean("accepted", false);
                    }
                    else if (code == Ag1BootstrapWire.ARM) {
                        if (terminal || ready || input.containsKey("dex") || !Ag1BootstrapWire.same(metadata, input)) throw new SecurityException();
                        ArrayList<String> prerequisites = input.getStringArrayList("prerequisites");
                        if (prerequisites == null || !new HashSet<>(prerequisites).equals(Ag1LaunchPolicy.REQUIRED)) throw new SecurityException();
                        // Manager verifies all bindings again; guest bytes are still absent.
                        if (!ask(Ag1BootstrapWire.READY, input)) throw new SecurityException();
                        readyAt = SystemClock.elapsedRealtimeNanos(); ready = true;
                        result.putBoolean("accepted", true); result.putLong("readyAt", readyAt);
                    } else if (code == Ag1BootstrapWire.RUN) {
                        // Do not access the descriptor until READY and live one-use authority agree.
                        if (terminal || !ready || used || !Ag1BootstrapWire.same(metadata, input)
                                || !ask(Ag1BootstrapWire.CONSUME, metadata)) throw new SecurityException();
                        used = true;
                        result = run(input);
                    }
                }
            } catch (Exception | LinkageError failure) {
                terminal = true; result = observations(); result.putBoolean("accepted", false);
            }
            if (reply != null) { reply.writeNoException(); reply.writeBundle(result); }
            return true;
        }
    };
    private boolean ask(int code, Bundle input) throws android.os.RemoteException {
        // Capture manager identity above; outgoing calls must carry this isolated process identity.
        long token = Binder.clearCallingIdentity();
        try { return Ag1BootstrapWire.call(authority, code, input).getBoolean("accepted"); }
        finally { Binder.restoreCallingIdentity(token); }
    }
    @Override public IBinder onBind(Intent intent) { return endpoint; }
    private Bundle observations() {
        Bundle b = new Bundle(); b.putLong("readyAt", readyAt); b.putLong("bytesAt", bytesAt);
        b.putLong("loaderAt", loaderAt); b.putLong("resolutionAt", resolutionAt);
        b.putInt("loaders", loaders); b.putInt("resolutions", resolutions); b.putInt("invocations", invocations);
        return b;
    }
    private Bundle run(Bundle input) throws Exception {
        SharedMemory memory = input.getParcelable("dex");
        if (memory == null) throw new SecurityException();
        try (memory) {
            int size = input.getInt("size");
            if (size <= 0 || size > Ag1BootstrapWire.MAX_DEX || size != memory.getSize()
                    || !memory.setProtect(OsConstants.PROT_READ)) throw new SecurityException();
            bytesAt = SystemClock.elapsedRealtimeNanos();
            if (bytesAt <= readyAt) throw new SecurityException();
            ByteBuffer dex = memory.mapReadOnly();
            try {
                // Verify the manager-extracted bounded bytes before constructing any guest loader.
                if (!Ag1BootstrapSession.hash(dex.duplicate()).equals(input.getString("dexSha"))) throw new SecurityException();
                loaderAt = SystemClock.elapsedRealtimeNanos(); loaders++;
                ClassLoader loader = new InMemoryDexClassLoader(dex, ClassLoader.getSystemClassLoader().getParent());
                resolutionAt = SystemClock.elapsedRealtimeNanos();
                long[] events = new long[6];
                String[] classes = {"PreCodeProvider", "PreCodeApplication", "PreCodeEntry"};
                for (int i = 0; i < classes.length; i++) {
                    resolutions++;
                    Class<?> type = Class.forName("com.privacydecoy.ag1.precode." + classes[i], true, loader);
                    events[i * 2] = type.getField("INITIALIZED").getLong(null);
                    events[i * 2 + 1] = (Long) type.getMethod(i == 2 ? "enter" : "callback").invoke(null);
                    if (i == 0 && !"controlled-constant".equals(type.getMethod("value").invoke(null))) throw new SecurityException();
                }
                invocations++;
                Bundle result = observations(); result.putLongArray("events", events); result.putBoolean("accepted", true);
                return result;
            } finally { SharedMemory.unmap(dex); }
        }
    }
}
