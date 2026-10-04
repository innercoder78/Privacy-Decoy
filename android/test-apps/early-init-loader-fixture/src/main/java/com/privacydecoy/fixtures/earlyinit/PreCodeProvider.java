package com.privacydecoy.fixtures.earlyinit;

import android.os.SystemClock;

public final class PreCodeProvider {
    public static final long INITIALIZED = SystemClock.elapsedRealtimeNanos();
    public static long callback() { return SystemClock.elapsedRealtimeNanos(); }
    public static String value() { return "controlled-constant"; }
    private PreCodeProvider() {}
}
