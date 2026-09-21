package com.privacydecoy.research.managedprobe; import android.app.Service; import android.content.Intent; import android.os.IBinder;
public final class ProbeService extends Service { public int onStartCommand(Intent i,int f,int id){Evidence.record(this,"service");stopSelf();return START_NOT_STICKY;} public IBinder onBind(Intent i){return null;} }
