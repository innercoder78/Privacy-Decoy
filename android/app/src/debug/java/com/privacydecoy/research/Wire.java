package com.privacydecoy.research;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/** Fixed research-only protocol, no arbitrary broker operations or file grants. */
final class Wire {
    static final String TOKEN = "com.privacydecoy.research.v1";
    static final int INIT = 1, RUN = 2, REQUEST = 3, KILL = 4, COUNT = 5, NETWORK = 6, DIRECT_NETWORK = 7;
    static Bundle call(IBinder binder, int code, Bundle input) throws RemoteException {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(TOKEN);
            data.writeBundle(input);
            if (!binder.transact(code, data, reply, 0)) throw new RemoteException("Unknown research operation");
            reply.readException();
            return reply.readBundle(Wire.class.getClassLoader());
        } finally { data.recycle(); reply.recycle(); }
    }
    static void simulateUnexpectedDeath(IBinder binder) throws RemoteException {
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(TOKEN); data.writeBundle(new Bundle());
            if (!binder.transact(KILL, data, null, IBinder.FLAG_ONEWAY)) throw new RemoteException("Death injection failed");
        } finally { data.recycle(); }
    }
    private Wire() {}
}
