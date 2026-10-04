package com.privacydecoy.fixtures.earlyinit;

import android.os.SystemClock;

public final class PreCodeEntry {
    public static final long INITIALIZED = SystemClock.elapsedRealtimeNanos();
    public static long enter() { return SystemClock.elapsedRealtimeNanos(); }
    private PreCodeEntry() {}
}
