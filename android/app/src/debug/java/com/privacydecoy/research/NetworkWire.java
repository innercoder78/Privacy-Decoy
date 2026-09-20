package com.privacydecoy.research;
import android.os.*;

/** Separate trusted control transactions from hostile operations. */
public final class NetworkWire {
    public static final String TOKEN="com.privacydecoy.networkresearch.v2";
    public static final int REQUEST=1, REGISTER=2, ROUTE=3, REVOKE=4, STATE=5,
        CALIBRATE=6, OS_OPEN=7, OS_SEND=8, OS_CLOSE=9;
    private NetworkWire() {}
    public static Bundle call(IBinder endpoint,int code,Bundle input) throws RemoteException {
        Parcel data=Parcel.obtain(),reply=Parcel.obtain();
        try {
            data.writeInterfaceToken(TOKEN); data.writeBundle(input);
            if (!endpoint.transact(code,data,reply,0)) throw new RemoteException("Unknown fixed transaction");
            reply.readException(); return reply.readBundle(NetworkWire.class.getClassLoader());
        } finally { data.recycle(); reply.recycle(); }
    }
}
