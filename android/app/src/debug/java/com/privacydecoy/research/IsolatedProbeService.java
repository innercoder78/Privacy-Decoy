package com.privacydecoy.research;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.SharedMemory;
import dalvik.system.InMemoryDexClassLoader;
import java.nio.ByteBuffer;
import com.privacydecoy.research.nativeprobe.NativeProbe;

/** Trusted only until fixture execution begins. Contains no manager secrets/state. */
public final class IsolatedProbeService extends Service {
    private IBinder broker;
    private String session;
    private long epoch;
    private int managerUid = -1;
    private int invocations;
    private String phase = "protocol";
    private final Binder endpoint = new Binder() {
        @Override protected synchronized boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            if (code < Wire.INIT || code > Wire.DIRECT_NETWORK) return false;
            Bundle result = new Bundle();
            phase = "protocol";
            try {
                data.enforceInterface(Wire.TOKEN);
                Bundle input = data.readBundle(getClass().getClassLoader());
                if (input == null || data.dataAvail() != 0 || data.dataSize() > 16384) throw new IllegalArgumentException();
                if (code == Wire.INIT && broker == null) {
                    managerUid = Binder.getCallingUid();
                    if (managerUid == Process.myUid()) throw new SecurityException();
                    broker = input.getBinder("broker"); session = input.getString("session"); epoch = input.getLong("epoch");
                    result = Wire.call(broker, Wire.INIT, ResearchSession.claim(session, epoch, ""));
                } else {
                    if (broker == null || Binder.getCallingUid() != managerUid) throw new SecurityException();
                    switch (code) {
                        case Wire.RUN:
                            if (invocations != 0 || !Wire.call(broker, Wire.REQUEST,
                                    ResearchSession.claim(session, epoch, "ping")).getBoolean("accepted")) throw new SecurityException();
                            result = runFixture(input);
                            break;
                        case Wire.REQUEST:
                            IBinder target = input.getBinder("targetBroker"); input.remove("targetBroker");
                            result = Wire.call(target == null ? broker : target, Wire.REQUEST, input);
                            break;

                        case Wire.DIRECT_NETWORK:
                            result.putString("result",FixedNetworkProbe.run(input.getString("op", "")));
                            break;
                        case Wire.NETWORK:
                            IBinder network=input.getBinder("networkBroker"); input.remove("networkBroker");
                            int transaction=input.getInt("transaction",NetworkWire.REQUEST); input.remove("transaction");
                            result=NetworkWire.call(network,transaction,input);
                            break;
                        case Wire.COUNT: result.putInt("count", invocations); break;
                        case Wire.KILL: Process.killProcess(Process.myPid()); break;
                        default: throw new IllegalArgumentException();
                    }
                }
            } catch (Exception | LinkageError failure) {
                result = new Bundle(); result.putString("error", "initialization-or-probe-failed");
                result.putString("errorPhase", phase);
                Throwable cause = failure instanceof java.lang.reflect.InvocationTargetException
                    ? failure.getCause() : failure;
                // Fixed phase and exception category only; no exception messages or stack traces.
                String category = cause instanceof SecurityException ? "security"
                    : cause instanceof LinkageError ? "linkage"
                    : cause instanceof ClassNotFoundException ? "class-not-found"
                    : cause instanceof android.system.ErrnoException ? "memory-or-os"
                    : cause instanceof NullPointerException ? "null"
                    : cause instanceof IllegalArgumentException ? "invalid-argument" : "other";
                android.util.Log.i("PD_PR4", "EXECUTION phase=" + phase + " failure=" + category);
            }
            reply.writeNoException(); reply.writeBundle(result); return true;
        }
    };
    @Override public IBinder onBind(Intent intent) { return endpoint; }
    private Bundle runFixture(Bundle input) throws Exception {
        // Native library initialization is also behind the manager coverage gate.
        phase = "harness-native";
        int[] nativeResult = NativeProbe.observe(input.getString("sentinel"), input.getInt("managerPid"));
        phase = "shared-memory";
        SharedMemory memory = input.getParcelable("dex");
        if (memory == null || memory.getSize() > 4 * 1024 * 1024) throw new IllegalArgumentException();
        try (memory) {
            ByteBuffer dex = memory.mapReadOnly();
            try {
                // Bootstrap parent exposes platform types, not app implementation classes.
                phase = "dex-loader";
                ClassLoader loader = new InMemoryDexClassLoader(dex, ClassLoader.getSystemClassLoader().getParent());
                Class<?> entry = loader.loadClass("com.privacydecoy.probe.ProbeEntry");
                Bundle fixtureInput = new Bundle(input); fixtureInput.remove("dex");
                fixtureInput.putBinder("broker", broker); fixtureInput.putString("session", session); fixtureInput.putLong("epoch", epoch);
                invocations++;
                phase = "fixture-entry";
                Bundle result = (Bundle) entry.getMethod("run", android.content.Context.class, Bundle.class)
                    .invoke(null, IsolatedProbeService.this, fixtureInput);
                result.putIntArray("native", nativeResult);
                return result;
            } finally { SharedMemory.unmap(dex); }
        }
    }
}
