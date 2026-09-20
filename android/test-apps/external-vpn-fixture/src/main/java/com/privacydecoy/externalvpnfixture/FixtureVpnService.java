package com.privacydecoy.externalvpnfixture;

import android.app.*;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.*;
import java.util.Arrays;

/** Separate dropping VPN. Reads headers only, emits bounded allowlisted categories. */
public final class FixtureVpnService extends VpnService {
    public enum Mode { FULL_TUNNEL, FULL_TUNNEL_BYPASS, PER_APP_INCLUDE, PER_APP_EXCLUDE, SPLIT_ROUTE, IPV6 }
    private ParcelFileDescriptor tun;
    private volatile int generation;
    @Override public int onStartCommand(Intent intent,int flags,int startId) {
        if(intent!=null && "STOP".equals(intent.getStringExtra("mode"))) {
            closeTun();stopForeground(STOP_FOREGROUND_REMOVE);stopSelf();
            Log.i("PD_PR5_VPN","STATE stopped");return START_NOT_STICKY;
        }
        if(prepare(this)!=null){Log.i("PD_PR5_VPN","STATE consent-required");stopSelf();return START_NOT_STICKY;}
        Mode mode;
        try{mode=Mode.valueOf(intent==null?"":intent.getStringExtra("mode"));}
        catch(RuntimeException e){stopSelf();return START_NOT_STICKY;}
        startForeground(51,notification());
        try{establish(mode);}catch(Exception e){Log.i("PD_PR5_VPN","STATE establishment-failed");stopSelf();}
        return START_NOT_STICKY;
    }
    private synchronized void establish(Mode mode) throws Exception {
        closeTun();
        Builder b=new Builder().setSession("PR5 external fixture").setMtu(1500).setBlocking(false)
            .addAddress("192.0.2.1",32).addAddress("2001:db8::1",128).addDnsServer("198.51.100.53");
        switch(mode) {
            case FULL_TUNNEL:case FULL_TUNNEL_BYPASS:
                b.addRoute("0.0.0.0",0).addRoute("::",0);break;
            case PER_APP_INCLUDE:
                b.addRoute("0.0.0.0",0).addRoute("::",0).addAllowedApplication("com.privacydecoy.app");break;
            case PER_APP_EXCLUDE:
                b.addRoute("0.0.0.0",0).addRoute("::",0).addDisallowedApplication("com.privacydecoy.app");break;
            case SPLIT_ROUTE:b.addRoute("198.51.100.0",24).addRoute("2001:db8::",32);break;
            case IPV6:b.addRoute("2001:db8::",32);break;
        }
        if(mode==Mode.FULL_TUNNEL_BYPASS)b.allowBypass();
        tun=b.establish();if(tun==null)throw new IOException();
        int token=generation;ParcelFileDescriptor descriptor=tun;
        Log.i("PD_PR5_VPN","STATE established mode="+mode+" alwaysOn="+isAlwaysOn()+" lockdown="+isLockdownEnabled());
        new Thread(()->observe(descriptor,mode,token),"fixed-header-observer").start();
    }
    private void observe(ParcelFileDescriptor descriptor,Mode mode,int token) {
        byte[] packet=new byte[2048];int count=0;
        try {
            while(token==generation) {
                int length;
                try { length=android.system.Os.read(descriptor.getFileDescriptor(),packet,0,packet.length); }
                catch(android.system.ErrnoException e) {
                    if(e.errno==android.system.OsConstants.EAGAIN) { Thread.sleep(20);continue; }
                    break;
                }
                if(length<1)break;
                int family=(packet[0]&255)>>>4;
                int offset=family==4?(packet[0]&15)*4:40;
                if((family!=4&&family!=6)||length<offset+4||offset<20){Arrays.fill(packet,(byte)0);continue;}
                int protocol=packet[family==4?9:6]&255;
                // No fragmented IPv4 or extension-header interpretation; report no invented ports.
                boolean first=family!=4||((packet[6]&31)==0&&packet[7]==0);
                int port=first&&(protocol==6||protocol==17)?((packet[offset+2]&255)<<8)|(packet[offset+3]&255):0;
                String category="other";
                if(family==4) {
                    if(eq(packet,16,new int[]{10,0,2,2}))category="host-control";
                    else if(eq(packet,16,new int[]{198,51,100,53}))category="synthetic-dns";
                    else if(eq(packet,16,new int[]{198,51,100,7}))category="documentation-v4";
                } else if(eq(packet,24,new int[]{32,1,13,184,0,0,0,0,0,0,0,0,0,0,0,7}))category="documentation-v6";
                // Unknown traffic never reveals an address or arbitrary port/protocol.
                if("other".equals(category)||!(port==46151||port==46152||port==46153||port==46154||port==53)) {category="other";port=0;}
                if(count<128) {
                    count++;
                    Log.i("PD_PR5_VPN","PACKET mode="+mode+" family="+family+" protocol="+
                        (protocol==6?"tcp":protocol==17?"udp":"other")+" category="+category+" port="+port+" count="+count);
                }
                Arrays.fill(packet,(byte)0);
            }
        } catch(InterruptedException | InterruptedIOException ignored){Thread.currentThread().interrupt();}finally{Arrays.fill(packet,(byte)0);}
    }
    private static boolean eq(byte[] p,int offset,int[] value) {
        for(int i=0;i<value.length;i++)if((p[offset+i]&255)!=value[i])return false;return true;
    }
    private synchronized void closeTun(){generation++;try{if(tun!=null)tun.close();}catch(IOException ignored){}tun=null;}
    @Override public void onRevoke(){Log.i("PD_PR5_VPN","STATE revoked");closeTun();stopSelf();}
    @Override public void onDestroy(){closeTun();Log.i("PD_PR5_VPN","STATE destroyed");super.onDestroy();}
    private Notification notification(){
        String channel="external-vpn-fixture";
        getSystemService(NotificationManager.class).createNotificationChannel(new NotificationChannel(channel,"External VPN fixture",NotificationManager.IMPORTANCE_LOW));
        return new Notification.Builder(this,channel).setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("External VPN test fixture").setContentText("Dropping fixed research traffic").build();
    }
}
