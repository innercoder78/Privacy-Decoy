package com.privacydecoy.research.profilecontroller;
import android.app.admin.DevicePolicyManager; import android.content.*; import android.os.Process;
public final class SetupReceiver extends BroadcastReceiver {
 public void onReceive(Context c, Intent i){ DevicePolicyManager d=c.getSystemService(DevicePolicyManager.class); if(!d.isProfileOwnerApp(c.getPackageName())) throw new IllegalStateException("not profile owner"); d.setProfileEnabled(ResearchAdminReceiver.getComponentName(c)); c.getSharedPreferences("research_state",0).edit().putBoolean("setup_complete",true).putInt("controller_uid",Process.myUid()).apply(); }
}
