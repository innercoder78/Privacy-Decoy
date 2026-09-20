package com.privacydecoy.externalvpnfixture;
import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;

/** Disposable separate test APK. Finite modes only; no arbitrary command/route input. */
public final class FixtureController extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        String mode=getIntent().getStringExtra("mode");
        if("STOP".equals(mode)) {
            // Android itself binds VpnService. stopService alone does not close a bound TUN.
            startService(new Intent(this,FixtureVpnService.class).putExtra("mode","STOP"));
            finish();return;
        }
        try {FixtureVpnService.Mode.valueOf(mode);}catch(RuntimeException e){finish();return;}
        if(VpnService.prepare(this)!=null) {
            Log.i("PD_PR5_VPN","STATE consent-required");finish();return;
        }
        // The host explicitly stops between modes; a new foreground service owns the TUN.
        startForegroundService(new Intent(this,FixtureVpnService.class).putExtra("mode",mode));
        finish();
    }
}
