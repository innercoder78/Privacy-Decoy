package com.privacydecoy.research.ag1;

import dalvik.system.InMemoryDexClassLoader;
import java.nio.ByteBuffer;

/** Fixed synthetic stages: loader, resolution, initialization, entry, result category. */
public final class Ag1DexObservation {
    private Ag1DexObservation() {}
    public static int[] observe(ByteBuffer dex) {
        int[] stages = new int[5];
        try {
            ClassLoader loader = new InMemoryDexClassLoader(dex, ClassLoader.getSystemClassLoader().getParent());
            stages[0] = 1;
            Class<?> type = Class.forName("com.privacydecoy.ag1.secondary.SecondaryDexPayload", false, loader);
            stages[1] = 1;
            int initialized = type.getField("INITIALIZED").getInt(null);
            stages[2] = 1;
            if (initialized != 73) { stages[4] = 5; return stages; }
            Object entered = type.getMethod("enter").invoke(null);
            stages[3] = 1;
            if (!Integer.valueOf(91).equals(entered)) stages[4] = 5;
        } catch (SecurityException failure) { stages[4] = 1;
        } catch (ClassNotFoundException failure) { stages[4] = 2;
        } catch (LinkageError failure) { stages[4] = 3;
        } catch (ReflectiveOperationException | RuntimeException failure) { stages[4] = 4; }
        return stages;
    }
}
