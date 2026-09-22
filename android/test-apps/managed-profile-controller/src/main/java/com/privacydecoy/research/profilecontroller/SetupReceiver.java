package com.privacydecoy.research.profilecontroller;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserManager;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/** One-shot engineering setup; disabled before any hostile package is installed. */
public final class SetupReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent intent) {
        if (!"com.privacydecoy.research.COMPLETE_SETUP".equals(intent.getAction())) return;
        File state = new File(c.getFilesDir(), "setup-state");
        if (state.exists()) return;
        DevicePolicyManager dpm = c.getSystemService(DevicePolicyManager.class);
        UserManager users = c.getSystemService(UserManager.class);
        if (!dpm.isProfileOwnerApp(c.getPackageName()) || !users.isManagedProfile()) {
            throw new IllegalStateException("invalid research owner");
        }
        long serial = users.getSerialNumberForUser(android.os.Process.myUserHandle());
        if (serial < 0) throw new IllegalStateException("unknown user serial");
        dpm.setProfileEnabled(ResearchAdminReceiver.getComponentName(c));
        c.getPackageManager().setComponentEnabledSetting(new ComponentName(c, SetupReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        String value = "schema=2\nsetup_complete=true\nprofile_owner=true\nmanaged_profile=true"
                + "\nreceiver_disabled=true\ncontroller_uid=" + android.os.Process.myUid()
                + "\nuser_serial=" + serial + "\n";
        try (FileOutputStream out = new FileOutputStream(state)) {
            out.write(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            throw new IllegalStateException("research setup unavailable");
        }
    }
}
