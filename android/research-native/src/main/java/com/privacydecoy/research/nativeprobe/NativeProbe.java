package com.privacydecoy.research.nativeprobe;

/** Debug harness native code, not imported APK native-code support. */
public final class NativeProbe {
    static { System.loadLibrary("containment_probe"); }
    private NativeProbe() {}
    public static native int[] observe(String sentinel, int managerPid);
}
