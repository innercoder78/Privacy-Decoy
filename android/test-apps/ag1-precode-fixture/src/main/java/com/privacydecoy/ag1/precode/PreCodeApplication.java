package com.privacydecoy.ag1.precode;

import android.os.SystemClock;

/** Synthetic callback only; no Context, host data, or Android lifecycle virtualization. */
public final class PreCodeApplication {
    public static final long INITIALIZED = SystemClock.elapsedRealtimeNanos();
    public static long callback() { return SystemClock.elapsedRealtimeNanos(); }
    private PreCodeApplication() {}
}
