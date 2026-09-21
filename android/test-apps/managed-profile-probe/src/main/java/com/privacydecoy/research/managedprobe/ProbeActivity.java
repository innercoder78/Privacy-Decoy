package com.privacydecoy.research.managedprobe; import android.app.Activity; import android.os.Bundle;
public final class ProbeActivity extends Activity { protected void onCreate(Bundle b){super.onCreate(b);Evidence.record(this,"activity");startService(new android.content.Intent(this,ProbeService.class));finish();} }
