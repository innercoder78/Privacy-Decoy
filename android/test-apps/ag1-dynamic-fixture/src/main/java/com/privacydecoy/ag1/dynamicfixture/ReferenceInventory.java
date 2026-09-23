package com.privacydecoy.ag1.dynamicfixture;

import dalvik.system.DexClassLoader;

/** Non-executed references used only to validate conservative static inventory. */
public final class ReferenceInventory {
    private ReferenceInventory() {}
    public static Class<?> dynamicLoaderType() { return DexClassLoader.class; }
    public static String nativeLoadReference() { return "System.loadLibrary"; }
}
