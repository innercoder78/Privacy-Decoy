package com.privacydecoy.research;

import android.os.Bundle;

/** Disposable debug boundary. Only synthetic controlled fixtures are authorized. */
public interface ResearchBoundary extends AutoCloseable {
    Bundle execute(byte[] dex, Bundle syntheticInputs) throws Exception;
    void revoke();
    @Override void close();
}
