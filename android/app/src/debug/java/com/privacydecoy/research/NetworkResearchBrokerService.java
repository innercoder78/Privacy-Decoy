package com.privacydecoy.research;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Trusted debug-only broker. It never executes probe code and accepts a fixed enum,
 * fixed synthetic destinations and empty/synthetic payloads only. It is not a product API.
 */
public final class NetworkResearchBrokerService extends Service {
    static final String TOKEN = "com.privacydecoy.networkresearch.v1";
    static final String TCP4 = "10.0.2.2";
    static final String RESERVED4 = "198.51.100.7";
    static final int TCP_PORT = 46151, UDP_PORT = 46152;
    private final NetworkGate gate = new NetworkGate();
    private final Binder endpoint = new Binder() {
        @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            String result = "denied";
            try {
                data.enforceInterface(TOKEN);
                long generation = data.readLong();
                NetworkGate.Operation operation = NetworkGate.Operation.values()[data.readInt()];
                if (data.dataAvail() != 0 || !gate.authorize(generation, operation)) throw new SecurityException();
                result = executeFixed(operation);
            } catch (SecurityException | IllegalArgumentException e) { result = "denied"; }
              catch (Exception e) { result = "io-failure"; }
            reply.writeNoException(); reply.writeString(result); return true;
        }
    };
    @Override public IBinder onBind(Intent intent) { return endpoint; }
    private static String executeFixed(NetworkGate.Operation operation) throws Exception {
        switch (operation) {
            case JAVA_TCP4:
                try (Socket socket = new Socket()) { socket.connect(new java.net.InetSocketAddress(TCP4, TCP_PORT), 1000); }
                return "success";
            case JAVA_UDP4:
                try (DatagramSocket socket = new DatagramSocket()) {
                    byte[] value = {0x50, 0x44, 0x35};
                    socket.send(new DatagramPacket(value, value.length, InetAddress.getByName(RESERVED4), UDP_PORT));
                }
                return "success";
            case DNS_LOOKUP_TEST:
                InetAddress.getAllByName("route-test.invalid"); return "success";
            default: return "not-exercised";
        }
    }
}
