package com.privacydecoy.externalvpnfixture;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;

/**
 * External test VPN: observes bounded IP/transport header metadata and drops every
 * packet. It never forwards to the public Internet and never stores payload bytes.
 */
public final class FixtureVpnService extends VpnService {
    public enum Mode { FULL_TUNNEL, PER_APP_INCLUDE, PER_APP_EXCLUDE, SPLIT_ROUTE, IPV6 }
    private static final int MAX_OBSERVATIONS = 128;
    private final ArrayDeque<Observation> observations = new ArrayDeque<>();
    private volatile boolean running;
    private ParcelFileDescriptor tun;

    public record Observation(int family, int protocol, String category, int port, int count, Mode mode) {}

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (prepare(this) != null) { stopSelf(); return START_NOT_STICKY; }
        Mode mode;
        try { mode = Mode.valueOf(intent == null ? "" : intent.getStringExtra("mode")); }
        catch (RuntimeException invalid) { stopSelf(); return START_NOT_STICKY; }
        startForeground(51, notification());
        try { establish(mode); } catch (Exception failure) { stopSelf(); }
        return START_NOT_STICKY;
    }
    private void establish(Mode mode) throws Exception {
        Builder b = new Builder().setSession("PR5 external fixture").setMtu(1500)
            .addAddress("192.0.2.1", 32).addAddress("2001:db8::1", 128);
        switch (mode) {
            case FULL_TUNNEL -> { b.addRoute("0.0.0.0", 0); b.addRoute("::", 0); }
            case PER_APP_INCLUDE -> { b.addRoute("0.0.0.0", 0); b.addAllowedApplication("com.privacydecoy.app"); }
            case PER_APP_EXCLUDE -> { b.addRoute("0.0.0.0", 0); b.addDisallowedApplication("com.privacydecoy.app"); }
            case SPLIT_ROUTE -> b.addRoute("198.51.100.0", 24);
            case IPV6 -> b.addRoute("2001:db8::", 32);
        }
        tun = b.establish();
        if (tun == null) throw new IOException("TUN establishment rejected");
        running = true;
        new Thread(() -> observe(tun, mode), "fixture-header-observer").start();
    }
    private void observe(ParcelFileDescriptor descriptor, Mode mode) {
        byte[] packet = new byte[2048];
        try (FileInputStream input = new FileInputStream(descriptor.getFileDescriptor())) {
            while (running) {
                int size = input.read(packet);
                if (size < 1) continue;
                int family = (packet[0] >>> 4) == 6 ? 6 : 4;
                int protocol = family == 6 && size > 6 ? packet[6] & 255 : size > 9 ? packet[9] & 255 : 0;
                int offset = family == 6 ? 40 : (packet[0] & 15) * 4;
                int port = size >= offset + 4 ? ((packet[offset + 2] & 255) << 8) | (packet[offset + 3] & 255) : 0;
                String category = family == 6 ? "documentation-v6" : "fixed-test-v4";
                synchronized (observations) {
                    if (observations.size() == MAX_OBSERVATIONS) observations.removeFirst();
                    observations.addLast(new Observation(family, protocol, category, port, 1, mode));
                }
                java.util.Arrays.fill(packet, (byte) 0);
            }
        } catch (IOException ignored) { /* Stop/close is expected; no addresses or payloads are logged. */ }
    }
    @Override public void onDestroy() {
        running = false;
        try { if (tun != null) tun.close(); } catch (IOException ignored) {}
        synchronized (observations) { observations.clear(); }
        super.onDestroy();
    }
    private Notification notification() {
        String channel = "external-vpn-fixture";
        getSystemService(NotificationManager.class).createNotificationChannel(
            new NotificationChannel(channel, "External VPN fixture", NotificationManager.IMPORTANCE_LOW));
        return new Notification.Builder(this, channel).setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("External VPN test fixture active").setContentText("Observing fixed test headers; forwarding disabled").build();
    }
}
