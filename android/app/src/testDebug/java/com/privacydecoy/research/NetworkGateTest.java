package com.privacydecoy.research;

import org.junit.Test;
import static org.junit.Assert.*;

public final class NetworkGateTest {
    @Test public void requireVpnDefaultsOn() { assertTrue(new NetworkGate().requireVpn()); }
    @Test public void unknownRouteBlocks() { NetworkGate g = new NetworkGate(); assertFalse(g.authorize(g.generation(), NetworkGate.Operation.JAVA_TCP4)); }
    @Test public void vpnLossInvalidatesGeneration() { NetworkGate g = verified(); long old = g.generation(); g.vpnLost(); assertFalse(g.authorize(old, NetworkGate.Operation.JAVA_UDP4)); }
    @Test public void staleNetworkGenerationRejected() { NetworkGate g = verified(); assertFalse(g.authorize(g.generation() - 1, NetworkGate.Operation.NATIVE_UDP4)); }
    @Test public void brokerOwnedConnectionsClosedOnRevocation() { NetworkGate g = verified(); assertTrue(g.own(7, g.generation())); g.vpnLost(); assertFalse(g.owns(7)); }
    @Test public void routeRevalidationRequiredAfterReconnect() { NetworkGate g = verified(); g.reconnectObserved(); assertFalse(g.authorize(g.generation(), NetworkGate.Operation.JAVA_TCP4)); g.validateVpnRoute(); assertTrue(g.authorize(g.generation(), NetworkGate.Operation.JAVA_TCP4)); }
    @Test public void explicitOffModeRequiresWarningState() { NetworkGate g = new NetworkGate(); g.setExplicitOff(false); assertFalse(g.authorize(g.generation(), NetworkGate.Operation.JAVA_TCP4)); g.setExplicitOff(true); assertTrue(g.authorize(g.generation(), NetworkGate.Operation.JAVA_TCP4)); }
    private static NetworkGate verified() { NetworkGate g = new NetworkGate(); g.validateVpnRoute(); return g; }
}
