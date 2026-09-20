package com.privacydecoy.probe;

public final class ProbeApplication extends android.app.Application {
    @Override public void onCreate() {
        super.onCreate();
        ProbeEntry.applicationCreated = true;
    }
}
