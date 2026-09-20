package com.privacydecoy.research;

import android.system.ErrnoException;
import android.system.OsConstants;
import com.privacydecoy.research.nativeprobe.NativeProbe;
import java.net.*;

/** Finite research operations: no caller-controlled address, port, or payload. */
public final class FixedNetworkProbe {
    public static final String HOST = "10.0.2.2", DOC4 = "198.51.100.7", DOC6 = "2001:db8::7";
    public static final int TCP = 46151, UDP = 46152;
    private FixedNetworkProbe() {}
    public static String run(String operation) {
        try {
            switch (operation) {
                case "JAVA_TCP4":
                    try (Socket s = open()) { s.getOutputStream().write(53); }
                    return "success";
                case "JAVA_UDP4": return udp(DOC4);
                case "HOST_UDP4": return udp(HOST);
                case "JAVA_UDP6": return udp(DOC6);
                case "NATIVE_TCP4": return nativeResult(NativeProbe.network(0));
                case "NATIVE_UDP4": return nativeResult(NativeProbe.network(1));
                case "NATIVE_UDP6": return nativeResult(NativeProbe.network(2));
                case "DNS_LOOKUP_TEST":
                    // Explicitly controlled DNS wire query, never the default/public resolver.
                    try (DatagramSocket s = new DatagramSocket()) {
                        byte[] query = {0x50,0x35,1,0,0,1,0,0,0,0,0,0,10,
                            'r','o','u','t','e','-','t','e','s','t',7,'i','n','v','a','l','i','d',0,0,1,0,1};
                        s.send(new DatagramPacket(query, query.length, InetAddress.getByName("198.51.100.53"),53));
                    }
                    return "success";
                default: return "denied";
            }
        } catch (Exception e) { return category(e); }
          catch (LinkageError e) { return "unavailable"; }
    }
    static Socket open() throws java.io.IOException {
        Socket s = new Socket();
        try { s.connect(new InetSocketAddress(HOST,TCP),1200); s.setSoTimeout(1200); return s; }
        catch (java.io.IOException | RuntimeException e) { s.close(); throw e; }
    }
    private static String udp(String target) throws Exception {
        try (DatagramSocket s = new DatagramSocket()) {
            s.send(new DatagramPacket(new byte[]{53},1,InetAddress.getByName(target),UDP));
        }
        return "success";
    }
    public static String category(Throwable failure) {
        for (Throwable e=failure; e!=null; e=e.getCause()) {
            if (e instanceof SecurityException) return "denied";
            if (e instanceof ErrnoException) {
                int n=((ErrnoException)e).errno;
                if (n==OsConstants.EACCES || n==OsConstants.EPERM) return "denied";
                if (n==OsConstants.ENETUNREACH || n==OsConstants.EAFNOSUPPORT) return "unavailable";
            }
            if (e instanceof SocketTimeoutException) return "timeout";
        }
        return "io-failure";
    }
    private static String nativeResult(int result) {
        return result==0 ? "success" : result==1 ? "denied" : result==2 ? "unavailable"
            : result==3 ? "timeout" : "io-failure";
    }
}
