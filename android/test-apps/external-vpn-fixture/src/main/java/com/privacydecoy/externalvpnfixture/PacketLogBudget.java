package com.privacydecoy.externalvpnfixture;

/** Per-establishment budget for sanitized header categories only; at most 120 records. */
final class PacketLogBudget {
    private static final int CONTROLLED_LIMIT = 8;
    private static final int BACKGROUND_LIMIT = 64;
    private final int[] controlled = new int[7];
    private int background;

    boolean shouldEmit(int family, String protocol, String category, int port) {
        int signature = signature(family, protocol, category, port);
        if (signature >= 0) {
            if (controlled[signature] >= CONTROLLED_LIMIT) return false;
            controlled[signature]++;
            return true;
        }
        if (background >= BACKGROUND_LIMIT) return false;
        background++;
        return true;
    }

    private static int signature(int family, String protocol, String category, int port) {
        if (family == 4) {
            if ("tcp".equals(protocol) && "host-control".equals(category)) {
                if (port == 46151) return 0;
                if (port == 46153) return 1;
            }
            if ("udp".equals(protocol)) {
                if ("documentation-v4".equals(category)) {
                    if (port == 46152) return 2;
                    if (port == 46154) return 3;
                }
                if ("synthetic-dns".equals(category) && port == 53) return 4;
            }
        }
        if (family == 6 && "udp".equals(protocol) && "documentation-v6".equals(category)) {
            if (port == 46152) return 5;
            if (port == 46154) return 6;
        }
        return -1;
    }
}
