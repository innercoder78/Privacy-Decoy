package com.privacydecoy.research.managedprobe;
import android.content.Context; import android.os.*; import android.provider.Settings; import java.io.*; import java.nio.charset.StandardCharsets; import java.security.MessageDigest; import java.util.*;
final class Evidence {
 static { System.loadLibrary("managed_probe"); }
 private static native String nativeCategories(String managementPath, String peerPath);
 static synchronized void record(Context c,String event){
  try { String nonce=java.nio.file.Files.readString(java.nio.file.Path.of("/proc/sys/kernel/random/boot_id")); String[] raw={Build.FINGERPRINT,Build.MODEL,Build.MANUFACTURER,Build.BRAND,Build.DEVICE,Build.PRODUCT,Build.HARDWARE,Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID),Locale.getDefault().toLanguageTag(),TimeZone.getDefault().getID()};
   StringBuilder b=new StringBuilder("schema=1\nevent=").append(event).append("\nuid=").append(Process.myUid()).append("\nuser=").append(UserHandle.myUserId()).append("\npid=").append(Process.myPid()).append('\n');
   for(int i=0;i<raw.length;i++) b.append("surface_").append(i).append("_hash=").append(hash(nonce+String.valueOf(raw[i]))).append('\n');
   File management=new File("/data/user/0/com.privacydecoy.app/no_backup/pr8-sentinel");
   b.append("java_management_read=").append(management.canRead()).append("\njava_management_write=").append(management.canWrite()).append('\n');
   File own=new File(c.getFilesDir(),"sentinel"); if(!own.exists()) java.nio.file.Files.writeString(own.toPath(),"synthetic");
   String peer=c.getPackageName().endsWith(".a")?"com.privacydecoy.research.managedprobe.b":"com.privacydecoy.research.managedprobe.a";
   b.append(nativeCategories("/data/user/0/com.privacydecoy.app/no_backup/pr8-sentinel","/data/user/"+UserHandle.myUserId()+"/"+peer+"/files/sentinel"));
   try(FileOutputStream o=c.openFileOutput("evidence-"+event,Context.MODE_PRIVATE)){o.write(b.toString().getBytes(StandardCharsets.UTF_8));}
  }catch(Exception e){ throw new IllegalStateException("evidence recording failed",e); }
 }
 private static String hash(String s)throws Exception{byte[]d=MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));return Base64.getUrlEncoder().withoutPadding().encodeToString(d);}
}
