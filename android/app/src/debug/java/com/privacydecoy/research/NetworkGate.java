package com.privacydecoy.research;

import java.util.HashSet;
import java.util.Set;

/** Pure fail-closed state machine for the disposable PR 5 broker. */
public final class NetworkGate {
    public enum Route { UNKNOWN, VERIFIED_VPN, EXPLICIT_OFF }
    public enum Operation { JAVA_TCP4, JAVA_UDP4, NATIVE_TCP4, NATIVE_UDP4,
        JAVA_UDP6, DNS_LOOKUP_TEST, OPEN_CONTROLLED_TCP_CONNECTION,
        SEND_ON_CONTROLLED_CONNECTION, CLOSE_CONTROLLED_CONNECTION }
    private boolean requireVpn = true;
    private boolean warningAccepted;
    private Route route = Route.UNKNOWN;
    private long generation = 1;
    private final Set<Long> connections = new HashSet<>();

    public synchronized boolean requireVpn() { return requireVpn; }
    public synchronized long generation() { return generation; }
    public synchronized void setRoute(Route next) { route = next; generation++; connections.clear(); }
    public synchronized void vpnLost() { setRoute(Route.UNKNOWN); }
    public synchronized void reconnectObserved() { setRoute(Route.UNKNOWN); }
    public synchronized void validateVpnRoute() { setRoute(Route.VERIFIED_VPN); }
    public synchronized boolean authorize(long requestGeneration, Operation operation) {
        if (operation == null || requestGeneration != generation) return false;
        return requireVpn ? route == Route.VERIFIED_VPN : route == Route.EXPLICIT_OFF && warningAccepted;
    }
    public synchronized void setExplicitOff(boolean acceptedWarning) {
        requireVpn = false; warningAccepted = acceptedWarning; setRoute(Route.EXPLICIT_OFF);
    }
    public synchronized boolean own(long id, long requestGeneration) {
        return authorize(requestGeneration, Operation.OPEN_CONTROLLED_TCP_CONNECTION) && connections.add(id);
    }
    public synchronized boolean owns(long id) { return connections.contains(id); }
}
