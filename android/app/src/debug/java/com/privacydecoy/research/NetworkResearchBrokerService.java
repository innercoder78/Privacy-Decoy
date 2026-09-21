package com.privacydecoy.research;

import android.app.Service;
import android.content.Intent;
import android.net.*;
import android.os.*;
import java.net.*;
import java.util.*;

/** Trusted debug broker. Registered isolated callers receive no OS socket capabilities. */
public final class NetworkResearchBrokerService extends Service {
    private final NetworkGate gate=new NetworkGate();
    private final Map<String,Registration> sessions=new HashMap<>();
    private final Map<Long,Owned> sockets=new HashMap<>();
    private long nextId=1;
    private int physicallyClosed;
    private ConnectivityManager connectivity;
    private Network observedNetwork;
    private NetworkCapabilities observedCapabilities;
    private LinkProperties observedLinks;
    private boolean observedRoute;
    // Binder dispatch must not wait for asynchronous callback delivery to invalidate
    // sockets after an already observable route change. This is still not atomic
    // with socket I/O; the VPN-loss race remains a measured platform dependency.
    private void refreshRoute() {
        Network network=connectivity.getActiveNetwork();
        NetworkCapabilities capabilities=network==null?null:connectivity.getNetworkCapabilities(network);
        LinkProperties links=network==null?null:connectivity.getLinkProperties(network);
        if(!observedRoute || !Objects.equals(network,observedNetwork) ||
                !Objects.equals(capabilities,observedCapabilities) || !Objects.equals(links,observedLinks)) {
            observedRoute=true;observedNetwork=network;observedCapabilities=capabilities;observedLinks=links;
            invalidate();
        }
    }
    // OS calibration is manager-only, deliberately outside the hostile authority registry.
    // It exists solely to distinguish platform old-socket behavior from broker revocation.
    private Socket osCalibration;
    private static final class Registration {
        final SessionPolicy policy; final IBinder lifetime; final IBinder.DeathRecipient death;
        Registration(SessionPolicy p,IBinder b,IBinder.DeathRecipient d) {policy=p;lifetime=b;death=d;}
    }
    private static final class Owned {
        final String session; final Socket socket;
        Owned(String s,Socket value) {session=s;socket=value;}
    }
    private final ConnectivityManager.NetworkCallback callback=new ConnectivityManager.NetworkCallback() {
        @Override public void onAvailable(Network n) { invalidate(); }
        @Override public void onLost(Network n) { invalidate(); }
        @Override public void onCapabilitiesChanged(Network n,NetworkCapabilities c) { invalidate(); }
        @Override public void onLinkPropertiesChanged(Network n,LinkProperties p) { invalidate(); }
    };
    @Override public void onCreate() {
        super.onCreate(); connectivity=getSystemService(ConnectivityManager.class);
        connectivity.registerDefaultNetworkCallback(callback);
    }
    private synchronized void invalidate() { gate.vpnLost(); closeAll(); }
    private void closeAll() {
        for (Owned owned:sockets.values()) {
            try { owned.socket.close(); } catch (java.io.IOException ignored) {}
            if (owned.socket.isClosed()) physicallyClosed++;
        }
        sockets.clear();
    }
    private void revoke(String id,boolean dead) {
        Registration r=sessions.remove(id);
        if (r!=null) {
            if (dead) r.policy.died(); else r.policy.revoke();
            r.lifetime.unlinkToDeath(r.death,0);
        }
        closeAll();
    }
    private final Binder endpoint=new Binder() {
        @Override protected boolean onTransact(int code,Parcel data,Parcel reply,int flags) {
            if (code<NetworkWire.REQUEST || code>NetworkWire.OS_CLOSE) return false;
            Bundle result=new Bundle(); result.putString("result","denied");
            synchronized(NetworkResearchBrokerService.this) {
                try {
                    data.enforceInterface(NetworkWire.TOKEN);
                    if (data.dataSize()>4096) throw new SecurityException();
                    Bundle input=data.readBundle(getClass().getClassLoader());
                    if (input==null || data.dataAvail()!=0) throw new SecurityException();
                    int uid=Binder.getCallingUid(),pid=Binder.getCallingPid();
                    if (code!=NetworkWire.REQUEST || !"BOUNDARY_NOOP".equals(input.getString("op")))
                        refreshTrustedRoute();
                    if (code==NetworkWire.REQUEST) {
                        result=request(uid,pid,input);
                    } else {
                        // Only the trusted application identity can register or validate routes.
                        // An isolated UID cannot acquire control by binding or transferring this Binder.
                        if (uid!=android.os.Process.myUid() || pid==android.os.Process.myPid()) throw new SecurityException();
                        result=control(code,input);
                    }
                } catch (Exception | LinkageError denied) { result.putString("result","denied"); }
            }
            if (reply!=null) {reply.writeNoException();reply.writeBundle(result);} return true;
        }
    };
    private void refreshTrustedRoute() {
        long identity=Binder.clearCallingIdentity();
        try { refreshRoute(); } finally { Binder.restoreCallingIdentity(identity); }
    }
    private Bundle request(int uid,int pid,Bundle b) throws Exception {
        if (!Set.of("session","epoch","generation","op","connection").containsAll(b.keySet())) throw new SecurityException();
        String id=b.getString("session"),op=b.getString("op");
        Registration r=sessions.get(id);
        if (r==null || !r.lifetime.isBinderAlive() ||
                !r.policy.authorize(uid,pid,id,b.getLong("epoch"),"ping")) throw new SecurityException();
        // Debug-only identity evidence: exact schema, no route observation, socket,
        // network operation, capability or manager state in either direction.
        if ("BOUNDARY_NOOP".equals(op)) {
            if (!b.keySet().equals(Set.of("session","epoch","op"))) throw new SecurityException();
            Bundle out=new Bundle();out.putString("result","success");return out;
        }
        NetworkGate.Operation operation;
        try { operation=NetworkGate.Operation.valueOf(op); } catch (RuntimeException e) {throw new SecurityException();}
        if (!gate.authorize(b.getLong("generation"),operation)) throw new SecurityException();
        Bundle out=new Bundle(); String status="success";
        long identity=Binder.clearCallingIdentity();
        try {
            switch(operation) {
                case OPEN_CONTROLLED_TCP_CONNECTION:
                    if(sockets.size()>=8) throw new SecurityException();
                    Socket socket=FixedNetworkProbe.open(); long number=nextId++;
                    sockets.put(number,new Owned(id,socket)); out.putLong("connection",number); break;
                case SEND_ON_CONTROLLED_CONNECTION:
                case CLOSE_CONTROLLED_CONNECTION:
                    Owned owned=sockets.get(b.getLong("connection"));
                    if(owned==null || !owned.session.equals(id)) {status="closed";break;}
                    if(operation==NetworkGate.Operation.CLOSE_CONTROLLED_CONNECTION) {
                        owned.socket.close(); sockets.remove(b.getLong("connection"));
                        if(owned.socket.isClosed()) physicallyClosed++;
                    } else {owned.socket.getOutputStream().write(53);owned.socket.getOutputStream().flush();}
                    break;
                default: status=FixedNetworkProbe.run(op);
            }
        } catch(java.io.IOException e) {status=FixedNetworkProbe.category(e);}
          finally {Binder.restoreCallingIdentity(identity);}
        out.putString("result",status); return out;
    }
    private Bundle control(int code,Bundle b) throws Exception {
        Bundle out=new Bundle();out.putString("result","success");
        switch(code) {
            case NetworkWire.REGISTER:
                String id=b.getString("session");
                if(sessions.containsKey(id) || sessions.size()>=8) throw new SecurityException();
                SessionPolicy p=new SessionPolicy(id,b.getLong("epoch"));
                Map<String,SessionPolicy.Coverage> coverage=new HashMap<>();
                for(String key:SessionPolicy.MANDATORY) coverage.put(key,SessionPolicy.Coverage.VerifiedForPrototype);
                if(!p.prepare(coverage,true) || !p.register(b.getInt("uid"),b.getInt("pid"),android.os.Process.myUid()))
                    throw new SecurityException();
                IBinder lifetime=b.getBinder("lifetime");
                if(lifetime==null || !lifetime.isBinderAlive()) throw new SecurityException();
                IBinder.DeathRecipient death=()-> {synchronized(this){revoke(id,true);}};
                lifetime.linkToDeath(death,0); sessions.put(id,new Registration(p,lifetime,death));break;
            case NetworkWire.ROUTE:
                closeAll();
                String route=b.getString("route","");
                if("verified".equals(route)) gate.validateVpnRoute();
                else if("calibration-off".equals(route)) gate.setExplicitOff(true);
                else gate.vpnLost();
                break;
            case NetworkWire.REVOKE: revoke(b.getString("session"),false);break;
            case NetworkWire.STATE:
                Network active=connectivity.getActiveNetwork();
                NetworkCapabilities c=active==null?null:connectivity.getNetworkCapabilities(active);
                LinkProperties links=active==null?null:connectivity.getLinkProperties(active);
                out.putBoolean("vpn",c!=null&&c.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
                // Manager-only identity comparison; never passed to hostile callers or logged.
                out.putLong("networkHandle",active==null?0:active.getNetworkHandle());
                boolean v4=false,v6=false;
                if(links!=null) for(RouteInfo r:links.getRoutes()) if(r.isDefaultRoute()) {
                    if(r.getDestination().getAddress() instanceof Inet4Address)v4=true;else v6=true;
                }
                out.putBoolean("default4",v4);out.putBoolean("default6",v6);break;
            case NetworkWire.CALIBRATE:
                // Fixed physical Network test is trusted-harness-only, never a hostile operation.
                if(!"PHYSICAL_TCP4".equals(b.getString("op"))) throw new SecurityException();
                out.putString("result",physical());break;
            case NetworkWire.OS_OPEN:
                closeOs();osCalibration=FixedNetworkProbe.open();break;
            case NetworkWire.OS_SEND:
                if(osCalibration==null || osCalibration.isClosed()) out.putString("result","closed");
                else try {osCalibration.getOutputStream().write(53);osCalibration.getOutputStream().flush();}
                    catch(Exception e){out.putString("result",FixedNetworkProbe.category(e));}
                break;
            case NetworkWire.OS_CLOSE:closeOs();break;
            default:throw new SecurityException();
        }
        out.putLong("generation",gate.generation());out.putInt("owned",sockets.size());
        out.putInt("physicallyClosed",physicallyClosed);return out;
    }
    // Separate fixed port from the gated Java TCP path, so loss-race packets
    // cannot be confused with this deliberately ungated OS fallback calibration.
    private String physical() {
        for(Network n:connectivity.getAllNetworks()) {
            NetworkCapabilities c=connectivity.getNetworkCapabilities(n);
            if(c==null||c.hasTransport(NetworkCapabilities.TRANSPORT_VPN))continue;
            try(Socket socket=n.getSocketFactory().createSocket()) {
                socket.connect(new InetSocketAddress(FixedNetworkProbe.HOST,46153),1200);
                socket.getOutputStream().write(53);return "success";
            } catch(Exception e){return FixedNetworkProbe.category(e);}
        }
        return "unavailable";
    }
    private void closeOs() {try{if(osCalibration!=null)osCalibration.close();}catch(Exception ignored){}osCalibration=null;}
    @Override public IBinder onBind(Intent intent){return endpoint;}
    @Override public synchronized void onDestroy(){
        connectivity.unregisterNetworkCallback(callback);invalidate();closeOs();
        for(String id:new ArrayList<>(sessions.keySet()))revoke(id,false);
        super.onDestroy();
    }
}
