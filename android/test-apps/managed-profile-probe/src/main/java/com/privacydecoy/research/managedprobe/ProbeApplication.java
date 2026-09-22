package com.privacydecoy.research.managedprobe;
import android.app.Application;
public final class ProbeApplication extends Application { public void onCreate(){super.onCreate(); Evidence.record(this,"application");} }
