package com.privacydecoy.research;

import android.app.Instrumentation;
import android.content.*;
import android.os.*;
import android.util.Log;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/** Real API-35 Binder/process/socket tests. Host script supplies independent pcap assertions. */
public final class NetworkTests {
    private final Instrumentation instrumentation;
    private final Context context;
    public NetworkTests(Instrumentation i){instrumentation=i;context=i.getTargetContext();}
    private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
    private static void evidence(String value){Log.i("PD_PR5",value);}
    private ResearchSession session() throws Exception {
        Map<String,SessionPolicy.Coverage> c=new HashMap<>();
        for(String key:SessionPolicy.MANDATORY)c.put(key,SessionPolicy.Coverage.VerifiedForPrototype);
        ResearchSession s=new ResearchSession(context,UUID.randomUUID().toString().replace("-",""),1,c,true);
        s.connect();return s;
    }
    private final class Broker implements AutoCloseable {
        IBinder binder;boolean bound;CountDownLatch ready=new CountDownLatch(1);
        final ServiceConnection connection=new ServiceConnection(){
            public void onServiceConnected(ComponentName n,IBinder b){binder=b;ready.countDown();}
            public void onServiceDisconnected(ComponentName n){binder=null;}
        };
        Broker() throws Exception {
            bound=context.bindService(new Intent(context,NetworkResearchBrokerService.class),connection,Context.BIND_AUTO_CREATE);
            check(bound&&ready.await(10,TimeUnit.SECONDS)&&binder!=null,"network broker bind failed");
            Thread.sleep(700);
        }
        Bundle control(int code,Bundle b)throws Exception{return NetworkWire.call(binder,code,b);}
        Bundle state()throws Exception{return control(NetworkWire.STATE,new Bundle());}
        long route(String value)throws Exception{
            Bundle b=new Bundle();b.putString("route",value);return control(NetworkWire.ROUTE,b).getLong("generation");
        }
        void register(ResearchSession s)throws Exception{
            Bundle b=ResearchSession.claim(s.id,s.epoch,"");b.putInt("uid",s.policy.uid());b.putInt("pid",s.policy.pid());
            b.putBinder("lifetime",s.researchLifetime());
            check("success".equals(control(NetworkWire.REGISTER,b).getString("result")),"session registration rejected");
            s.onNetworkRevocation(()->{try{Bundle r=new Bundle();r.putString("session",s.id);control(NetworkWire.REVOKE,r);}catch(Exception ignored){}});
        }
        Bundle request(ResearchSession s,long generation,String op,long connection)throws Exception{
            Bundle b=ResearchSession.claim(s.id,s.epoch,op);b.putLong("generation",generation);b.putLong("connection",connection);
            return s.network(binder,b,NetworkWire.REQUEST);
        }
        String op(ResearchSession s,long generation,String op)throws Exception{
            evidence("BEGIN op="+op);
            String status=request(s,generation,op,0).getString("result");
            check(Set.of("success","denied","unavailable","timeout","io-failure").contains(status),"invalid operation result");
            Thread.sleep(250);evidence("END op="+op+" result="+status);return status;
        }
        public void close(){if(bound){context.unbindService(connection);bound=false;}}
    }
    public void testDirectProcessRestrictions()throws Exception{
        try(ResearchSession s=session()){
            check(s.policy.uid()!=android.os.Process.myUid(),"probe is not isolated");
            check("denied".equals(FixedNetworkProbe.run("JAVA_TCP4")),"management TCP not permission denied");
            evidence("DIRECT producer=management op=JAVA_TCP4 result=denied");
            for(String op:new String[]{"JAVA_TCP4","JAVA_UDP4","NATIVE_TCP4","NATIVE_UDP4","JAVA_UDP6","NATIVE_UDP6"}){
                String result=s.directNetwork(op).getString("result");
                evidence("DIRECT producer=isolated op="+op+" result="+result);
                check("denied".equals(result),"isolated socket not permission denied");
            }
        }
    }
    public void testBrokerSessionBoundary()throws Exception{
        try(Broker broker=new Broker();ResearchSession a=session();ResearchSession b=session()){
            broker.register(a);broker.register(b);long g=broker.route("calibration-off");
            check("success".equals(broker.op(a,g,"HOST_UDP4")),"active A denied");
            Bundle claim=ResearchSession.claim(a.id,a.epoch,"HOST_UDP4");claim.putLong("generation",g);
            check("denied".equals(b.network(broker.binder,claim,NetworkWire.REQUEST).getString("result")),"B claimed A");
            check("denied".equals(NetworkWire.call(broker.binder,NetworkWire.REQUEST,claim).getString("result")),"unregistered direct caller accepted");
            claim.putLong("epoch",0);
            check("denied".equals(a.network(broker.binder,claim,NetworkWire.REQUEST).getString("result")),"stale epoch accepted");
            check("denied".equals(broker.request(a,g-1,"HOST_UDP4",0).getString("result")),"stale network generation accepted");
            claim=ResearchSession.claim(a.id,a.epoch,"HOST_UDP4");claim.putLong("generation",g);claim.putString("extra","malformed");
            check("denied".equals(a.network(broker.binder,claim,NetworkWire.REQUEST).getString("result")),"malformed operation accepted");
            check("denied".equals(broker.request(a,g,"UNKNOWN",0).getString("result")),"unknown operation accepted");
            Bundle route=new Bundle();route.putString("route","verified");
            check("denied".equals(a.network(broker.binder,route,NetworkWire.ROUTE).getString("result")),"hostile verified route");
            a.revoke();check("denied".equals(broker.request(a,g,"HOST_UDP4",0).getString("result")),"revoked accepted");
            b.killAndAwaitDeath();Thread.sleep(250);
            try(ResearchSession replacement=session()){
                Bundle old=ResearchSession.claim(b.id,b.epoch,"HOST_UDP4");old.putLong("generation",g);
                check("denied".equals(replacement.network(broker.binder,old,NetworkWire.REQUEST).getString("result")),"dead session replaced authority");
            }
            evidence("BOUNDARY active=true crossSessionDenied=true staleEpochDenied=true staleGenerationDenied=true revokedDenied=true deadDenied=true malformedDenied=true controlDenied=true");
        }
    }
    private void routed(String mode)throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);Bundle state=b.state();
            evidence("ROUTE mode="+mode+" vpn="+state.getBoolean("vpn")+" default4="+state.getBoolean("default4")+" default6="+state.getBoolean("default6"));
            long g=b.route("verified");
            for(String op:new String[]{"JAVA_TCP4","JAVA_UDP4","NATIVE_TCP4","NATIVE_UDP4","DNS_LOOKUP_TEST","JAVA_UDP6","NATIVE_UDP6"}) {
                String result=b.op(s,g,op);
                check(!"denied".equals(result),"authorized broker operation denied");
            }
        }
    }
    public void testFullTunnel()throws Exception{routed("FULL_TUNNEL");}
    public void testPerAppInclude()throws Exception{routed("PER_APP_INCLUDE");}
    public void testKnownGapPerAppExclude()throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);Bundle state=b.state();evidence("ROUTE mode=PER_APP_EXCLUDE vpn="+state.getBoolean("vpn"));
            check("success".equals(b.op(s,b.route("verified"),"JAVA_TCP4")),"excluded host TCP did not connect");
        }
    }
    public void testKnownGapSplitRoute()throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);Bundle state=b.state();evidence("ROUTE mode=SPLIT_ROUTE vpn="+state.getBoolean("vpn")+" default4="+state.getBoolean("default4"));
            long g=b.route("verified");check("success".equals(b.op(s,g,"JAVA_UDP4")),"split routed UDP failed");
            check("success".equals(b.op(s,g,"JAVA_TCP4")),"outside route TCP failed");
        }
    }
    public void testPhysicalSelection()throws Exception{
        try(Broker b=new Broker()){
            Bundle input=new Bundle();input.putString("op","PHYSICAL_TCP4");
            evidence("PHYSICAL result="+b.control(NetworkWire.CALIBRATE,input).getString("result"));
        }
    }
    public void testKnownGapPhysicalSelectionAllowed()throws Exception{
        try(Broker b=new Broker()){
            Bundle input=new Bundle();input.putString("op","PHYSICAL_TCP4");
            String result=b.control(NetworkWire.CALIBRATE,input).getString("result");
            evidence("PHYSICAL allowBypass=true result="+result);check("success".equals(result),"explicit physical Network did not connect");
        }
    }
    private void shell(String command)throws Exception{
        try(ParcelFileDescriptor fd=instrumentation.getUiAutomation().executeShellCommand(command);
            FileInputStream input=new FileInputStream(fd.getFileDescriptor())){
            byte[] discard=new byte[1024];int size,total=0;
            while((size=input.read(discard))!=-1){total+=size;check(total<65536,"shell output exceeds bound");}
        }
    }
    private void fixture(String pkg,String mode)throws Exception{
        shell("am start -W -n "+pkg+"/com.privacydecoy.externalvpnfixture.FixtureController --es mode "+mode);
    }
    private static final String A="com.privacydecoy.externalvpnfixture",B=A+".replacement";
    private void awaitVpn(Broker b,boolean expected)throws Exception{
        for(int i=0;i<50;i++){if(b.state().getBoolean("vpn")==expected){Thread.sleep(400);return;}Thread.sleep(100);}
        throw new AssertionError("expected VPN transition absent");
    }
    public void testExistingSocketAndRealRevocation()throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);long g=b.route("calibration-off");
            Bundle opened=b.request(s,g,"OPEN_CONTROLLED_TCP_CONNECTION",0);
            check("success".equals(opened.getString("result")),"owned TCP open failed");
            long id=opened.getLong("connection");
            check("success".equals(b.control(NetworkWire.OS_OPEN,new Bundle()).getString("result")),"OS calibration TCP open failed");
            fixture(A,"FULL_TUNNEL");awaitVpn(b,true);
            evidence("OLD_SOCKET_SEND_BEGIN");
            String result=b.control(NetworkWire.OS_SEND,new Bundle()).getString("result");Thread.sleep(250);
            evidence("OLD_SOCKET_SEND result="+result);
            Bundle state=b.state();check(state.getInt("owned")==0&&state.getInt("physicallyClosed")>0,"route did not close real owned socket");
            check("denied".equals(b.request(s,g,"SEND_ON_CONTROLLED_CONNECTION",id).getString("result")),"old generation usable");
            long fresh=b.route("verified");check("closed".equals(b.request(s,fresh,"SEND_ON_CONTROLLED_CONNECTION",id).getString("result")),"owned socket still usable");
            b.control(NetworkWire.OS_CLOSE,new Bundle());evidence("REGISTRY routeClosed=true sendClosed=true");
        }
    }
    public void testRealSessionSocketRevocation()throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);long g=b.route("calibration-off");
            check("success".equals(b.request(s,g,"OPEN_CONTROLLED_TCP_CONNECTION",0).getString("result")),"owned open failed");
            s.revoke();check(b.state().getInt("owned")==0&&b.state().getInt("physicallyClosed")>0,"session revoke did not close Socket");
            try(ResearchSession dead=session()){
                b.register(dead);check("success".equals(b.request(dead,g,"OPEN_CONTROLLED_TCP_CONNECTION",0).getString("result")),"death socket open failed");
                dead.killAndAwaitDeath();Thread.sleep(300);
                check(b.state().getInt("owned")==0&&b.state().getInt("physicallyClosed")>=2,"session death did not close Socket");
            }
            evidence("REGISTRY sessionClosed=true deathClosed=true");
        }
    }
    public void testVpnLossAndReconnect()throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);long g=b.route("verified");
            fixture(A,"STOP");
            String immediate=b.op(s,g,"JAVA_TCP4");
            evidence("LOSS immediate="+immediate);
            awaitVpn(b,false);
            check("denied".equals(b.request(s,g,"JAVA_TCP4",0).getString("result")),"lost generation survived callback");
            // Calibrate OS fallback independently of whether this callback race reproduced.
            Bundle physical=new Bundle();physical.putString("op","PHYSICAL_TCP4");
            evidence("LOSS_OS_FALLBACK result="+b.control(NetworkWire.CALIBRATE,physical).getString("result"));
            fixture(A,"FULL_TUNNEL");awaitVpn(b,true);
            check("denied".equals(b.request(s,g,"JAVA_TCP4",0).getString("result")),"old generation restored on reconnect");
            check("denied".equals(b.request(s,b.state().getLong("generation"),"JAVA_TCP4",0).getString("result")),"reconnect silently authorized");
            check(!"denied".equals(b.op(s,b.route("verified"),"JAVA_UDP4")),"new route generation denied");
            evidence("RECONNECT oldDenied=true unverifiedDenied=true revalidatedAllowed=true");
        }
    }
    public void testProviderReplacement()throws Exception{
        try(Broker b=new Broker();ResearchSession s=session()){
            b.register(s);long g=b.route("verified");fixture(B,"FULL_TUNNEL");Thread.sleep(1200);
            check(b.state().getBoolean("vpn"),"replacement VPN absent");
            check("denied".equals(b.request(s,g,"JAVA_UDP4",0).getString("result")),"replacement retained old generation");
            check("denied".equals(b.request(s,b.state().getLong("generation"),"JAVA_UDP4",0).getString("result")),"replacement self-validated");
            evidence("REPLACEMENT oldDenied=true unverifiedDenied=true");
        }
    }
    public void testLockdownLoss()throws Exception{
        try(Broker b=new Broker()){
            Bundle input=new Bundle();input.putString("op","PHYSICAL_TCP4");
            evidence("LOCKDOWN_ACTIVE result="+b.control(NetworkWire.CALIBRATE,input).getString("result"));
            fixture(A,"STOP");
            String result=b.control(NetworkWire.CALIBRATE,input).getString("result");
            evidence("LOCKDOWN_LOSS result="+result);check(!"success".equals(result),"SEVERE lockdown physical fallback");
        }
    }
}
