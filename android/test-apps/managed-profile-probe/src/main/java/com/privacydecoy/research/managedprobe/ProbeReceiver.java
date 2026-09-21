package com.privacydecoy.research.managedprobe; import android.content.*;
public final class ProbeReceiver extends BroadcastReceiver { public void onReceive(Context c,Intent i){Evidence.record(c,"receiver");} }
