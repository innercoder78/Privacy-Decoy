package com.privacydecoy.research.ag1;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/** Fixed protocol; INIT/ARM carry metadata only. No guest-selected operations or class names. */
final class Ag1BootstrapWire {
    static final String TOKEN = "com.privacydecoy.research.ag1.v1";
    static final int INIT = 1, ARM = 2, RUN = 3, COUNT = 4, KILL = 5;
    static final int REGISTER = 10, READY = 11, CONSUME = 12;
    static final int MAX_DEX = 4 * 1024 * 1024;
    static Bundle call(IBinder binder, int code, Bundle input) throws RemoteException {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(TOKEN); data.writeBundle(input);
            if (binder == null || !binder.transact(code, data, reply, 0)) throw new RemoteException("AG-1 IPC denied");
            reply.readException();
            Bundle result = reply.readBundle(Ag1BootstrapWire.class.getClassLoader());
            if (result == null) throw new RemoteException("AG-1 empty reply");
            return result;
        } finally { data.recycle(); reply.recycle(); }
    }
    static void kill(IBinder binder) throws RemoteException {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(TOKEN); data.writeBundle(new Bundle());
            // KILL must be synchronous: one-way Binder transactions do not expose calling PID,
            // and this research service intentionally checks the exact manager UID/PID.
            if (!binder.transact(KILL, data, reply, 0)) throw new RemoteException("AG-1 death injection failed");
            reply.readException();
            throw new RemoteException("AG-1 death injection returned");
        } finally { data.recycle(); reply.recycle(); }
    }
    static Bundle metadata(Ag1LaunchPolicy policy) {
        Bundle b = new Bundle();
        b.putString("id", policy.id); b.putLong("sessionEpoch", policy.sessionEpoch);
        b.putString("generation", policy.admission.generation); b.putString("sha", policy.admission.sha);
        b.putString("outcome", policy.admission.outcome.name()); b.putString("execution", policy.execution.name());
        b.putLong("consentEpoch", policy.consentEpoch); return b;
    }
    static boolean same(Bundle expected, Bundle actual) {
        if (expected == null || actual == null) return false;
        for (String key : new String[]{"id", "generation", "sha", "outcome", "execution"}) {
            String value = expected.getString(key);
            if (value == null || !value.equals(actual.getString(key))) return false;
        }
        return expected.getLong("sessionEpoch") > 0
            && expected.getLong("sessionEpoch") == actual.getLong("sessionEpoch")
            && expected.getLong("consentEpoch") > 0
            && expected.getLong("consentEpoch") == actual.getLong("consentEpoch");
    }
    private Ag1BootstrapWire() {}
}
