package com.privacydecoy.research.profilecontroller;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

/** Minimal test-only profile owner: it claims no optional device-admin policies. */
public final class ResearchAdminReceiver extends DeviceAdminReceiver {
  static android.content.ComponentName getComponentName(Context c) { return new android.content.ComponentName(c, ResearchAdminReceiver.class); }
  @Override public void onProfileProvisioningComplete(Context context, Intent intent) {
    DevicePolicyManager dpm = context.getSystemService(DevicePolicyManager.class);
    dpm.setProfileEnabled(getWho(context));
    context.getSharedPreferences("research_state", Context.MODE_PRIVATE).edit()
        .putBoolean("setup_complete", true).putInt("controller_uid", Process.myUid()).apply();
  }
}
