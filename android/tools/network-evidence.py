"""Bounded fixed-target experiment. No raw packet/log output or capture uploads."""
import collections
import ipaddress
import json
import os
from pathlib import Path
import re
import socketserver
import struct
import subprocess
import threading
import time

OUT=Path('app/build/reports/network')
CAPTURES=('physical.pcap','wifi.pcap')
A='com.privacydecoy.externalvpnfixture'
B=A+'.replacement'
CASES=[
 ('testDirectProcessRestrictions',None),
 ('testBrokerSessionBoundary',None),
 ('testFullTunnel','FULL_TUNNEL'),
 ('testPerAppInclude','PER_APP_INCLUDE'),
 ('testKnownGapPerAppExclude','PER_APP_EXCLUDE'),
 ('testKnownGapSplitRoute','SPLIT_ROUTE'),
 ('testPhysicalSelection','FULL_TUNNEL'),
 ('testKnownGapPhysicalSelectionAllowed','FULL_TUNNEL_BYPASS'),
 ('testExistingSocketAndRealRevocation',None),
 ('testRealSessionSocketRevocation',None),
 ('testVpnLossAndReconnect','FULL_TUNNEL'),
 ('testProviderReplacement','FULL_TUNNEL'),
]
def adb(*args,timeout=30,check=True):
    p=subprocess.run(['adb',*args],capture_output=True,text=True,timeout=timeout)
    if check and p.returncode: raise AssertionError('ADB operation failed: '+args[0])
    return p.stdout
def control(pkg,mode):
    if mode!='STOP':adb('shell','appops','set',pkg,'ACTIVATE_VPN','allow')
    adb('shell','am','start','-W','-n',pkg+'/com.privacydecoy.externalvpnfixture.FixtureController','--es','mode',mode)
def logs():return adb('logcat','-d','-v','raw','PD_PR5:I','PD_PR5_VPN:I','*:S')
def start(mode,pkg=A):
    control(pkg,mode)
    for _ in range(50):
        if 'STATE established mode='+mode in logs(): return
        time.sleep(.1)
    raise AssertionError('Fixture TUN not established: '+mode)
def reset():
    control(A,'STOP');control(B,'STOP')
    adb('shell','am','force-stop',A);adb('shell','am','force-stop',B)
    adb('shell','am','force-stop','com.privacydecoy.app')
    time.sleep(.4);adb('logcat','-c')
def capture_offsets():
    # QEMU filter-dump writes each record directly with writev (no stdio buffer).
    # File offsets avoid comparing its virtual-clock timestamps with host time.
    offsets={name:(OUT/name).stat().st_size for name in CAPTURES}
    assert all(size>=24 for size in offsets.values()), 'Missing capture header'
    return offsets

def case_packets(captures,case):
    rows=[]
    for name,packets_ in captures.items():
        start=case['captureStart'][name];end=case['captureEnd'][name]
        assert 24<=start<=end, 'Invalid capture interval'
        # Include a boundary-overlapping record conservatively; never hide leakage.
        rows.extend(p for p in packets_ if p['recordEnd']>start and p['recordStart']<end)
    return rows

def run_case(name,mode):
    reset()
    if mode:start(mode)
    capture_start=capture_offsets()
    begin=time.time()
    output=adb('shell','am','instrument','-w','-r','-e','suite','network','-e','case',name,
        'com.privacydecoy.app.test/com.privacydecoy.research.PrototypeTestRunner',timeout=150)
    time.sleep(.4)  # Include trailing fixed packets before closing the case interval.
    capture_end=capture_offsets()
    end=time.time();observations=logs()
    # Both the runner summary and a fixed PASS record are mandatory (no skip/zero-test escape).
    passed='Tests run: 1, Failures: 0' in output and 'PASS '+name in observations
    if not passed:
        for line in observations.splitlines():
            if line.startswith(('FAIL '+name+':', 'DIRECT ', 'BEGIN ', 'END ', 'STATE ')) and len(line)<400:
                print(line,flush=True)
        print('DEVICE_CASE_FAILED '+name,flush=True)
    assert 'Tests run: 1, Failures:' in output, 'Missing device execution: '+name
    assert 'INSTRUMENTATION_FAILED' not in output, 'Instrumentation failed: '+name
    safe=[]
    allowed=re.compile(r'^(?:PASS test[A-Za-z0-9]+|(?:DIRECT|BOUNDARY|ROUTE|BEGIN|END|PHYSICAL|OLD_SOCKET_SEND_BEGIN|OLD_SOCKET_SEND|REGISTRY|LOSS|LOSS_OS_FALLBACK|RECONNECT|REPLACEMENT|LOCKDOWN_ACTIVE|LOCKDOWN_LOSS|LOCKDOWN_DOWN|STATE|PACKET) [A-Za-z0-9_= .-]+)$')
    for line in observations.splitlines():
        if allowed.fullmatch(line):safe.append(line)
    for line in safe: print(name+' '+line,flush=True)
    return dict(name=name,start=begin,end=end,captureStart=capture_start,captureEnd=capture_end,observations=safe,passed=passed)

class TCP(socketserver.BaseRequestHandler):
    def handle(self):
        self.request.settimeout(30)
        try:
            while self.request.recv(1024): pass
        except OSError:pass
class UDP(socketserver.BaseRequestHandler):
    def handle(self):pass
class TCPServer(socketserver.ThreadingTCPServer):
    allow_reuse_address=True
    daemon_threads=True
def exercise():
    OUT.mkdir(parents=True,exist_ok=True)
    servers=[TCPServer(('127.0.0.1',46151),TCP),TCPServer(('127.0.0.1',46153),TCP),socketserver.ThreadingUDPServer(('127.0.0.1',46152),UDP)]
    try:
        for server in servers:threading.Thread(target=server.serve_forever,daemon=True).start()
        for pkg in (A,B):adb('shell','appops','set',pkg,'ACTIVATE_VPN','allow')
        records=[run_case(name,mode) for name,mode in CASES]
        # Public platform setting setup on a disposable emulator; reboot makes Android load it.
        # A setting write alone is never counted as verified lockdown.
        reset()
        adb('shell','settings','put','secure','always_on_vpn_app',A)
        adb('shell','settings','put','secure','always_on_vpn_lockdown','1')
        adb('reboot')
        boot=False
        for _ in range(120):
            time.sleep(2)
            if adb('shell','getprop','sys.boot_completed',timeout=5,check=False).strip()=='1':boot=True;break
        assert boot,'Lockdown setup reboot timed out'
        adb('logcat','-c');control(A,'FULL_TUNNEL_BYPASS');time.sleep(2)
        setup=logs()
        for line in setup.splitlines():
            if line.startswith('STATE ') and re.fullmatch(r'[A-Za-z0-9_= .-]+',line):print('LOCKDOWN_SETUP '+line,flush=True)
        if 'alwaysOn=true lockdown=true' in setup:
            records.append(run_case('testLockdownLoss','FULL_TUNNEL_BYPASS'))
            lockdown='verified-platform-state'
        else:
            lockdown='Unknown: secure always-on/lockdown settings plus reboot did not yield both VpnService booleans'
            print('LOCKDOWN '+lockdown,flush=True)
        OUT.joinpath('observations.json').write_text(json.dumps(dict(cases=records,lockdown=lockdown),indent=2))
    finally:
        for server in servers:server.shutdown();server.server_close()

TARGETS={ipaddress.ip_address(s).packed:c for s,c in [
    ('10.0.2.2','host-control'),('198.51.100.7','documentation-v4'),
    ('198.51.100.53','synthetic-dns'),('198.51.100.54','platform-dns'),('2001:db8::7','documentation-v6')]}
def packets(path):
    # Decode only link/IP/transport headers. Never inspect or retain payload bytes.
    with path.open('rb') as f:
        file_size=path.stat().st_size
        header=f.read(24);assert len(header)==24,'Capture missing header'
        assert header[:4] in (b'\xd4\xc3\xb2\xa1',b'\xa1\xb2\xc3\xd4'),'Unsupported capture format'
        endian='<' if header[:4]==b'\xd4\xc3\xb2\xa1' else '>'
        link=struct.unpack(endian+'I',header[20:24])[0]
        assert link in (1,101),'Unsupported capture link type'
        while True:
            record_start=f.tell()
            h=f.read(16)
            if not h:break
            assert len(h)==16,'Truncated capture record'
            sec,usec,size,_=struct.unpack(endian+'IIII',h)
            assert size<=262144 and f.tell()+size<=file_size,'Invalid or truncated capture record'
            # Read at most 128 header bytes; skip all remaining captured bytes.
            p=f.read(min(size,128));f.seek(max(0,size-128),1)
            offset=14 if link==1 else 0
            if len(p)<=offset:continue
            family=p[offset]>>4
            if family==4:
                if len(p)<offset+20:continue
                ihl=(p[offset]&15)*4
                if ihl<20 or struct.unpack('!H',p[offset+6:offset+8])[0]&0x1fff:continue
                protocol=p[offset+9];dest=p[offset+16:offset+20];transport=offset+ihl
                iplen=struct.unpack('!H',p[offset+2:offset+4])[0]
            elif family==6:
                if len(p)<offset+40:continue
                protocol=p[offset+6];dest=p[offset+24:offset+40];transport=offset+40;ihl=40
                iplen=40+struct.unpack('!H',p[offset+4:offset+6])[0]
            else:continue
            category=TARGETS.get(dest)
            if category is None or protocol not in (6,17) or len(p)<transport+4:continue
            port=struct.unpack('!H',p[transport+2:transport+4])[0]
            if port not in (46151,46152,46153,46154,53):continue
            data=False
            if protocol==6 and len(p)>=transport+20:
                data=iplen-ihl-((p[transport+12]>>4)*4)>0
            yield dict(recordStart=record_start,recordEnd=f.tell(),time=sec+usec/1e6,family=family,protocol='tcp' if protocol==6 else 'udp',category=category,port=port,data=data)

def gated_race_packets(rows):
    # A SYN is already physical egress, even if connect fails before payload.
    # Port 46153 belongs to the independent ungated OS fallback calibration.
    return [r for r in rows if r['category']=='host-control'
            and r['protocol']=='tcp' and r['port']==46151]

def analyze():
    report=json.loads(OUT.joinpath('observations.json').read_text())
    captures={name:list(packets(OUT/name)) for name in CAPTURES}
    for name,rows in captures.items():
        print('CAPTURE '+name+' fixedPackets='+str(len(rows)),flush=True)
    assert [c['name'] for c in report['cases'][:len(CASES)]]==[n for n,_ in CASES], 'Missing or reordered mandatory network device tests'
    for case in report['cases']:
        rows=case_packets(captures,case)
        name=case['name'];obs='\n'.join(case['observations'])
        counts=lambda items:{str(k):v for k,v in collections.Counter((r['family'],r['protocol'],r['category'],r['port']) for r in items).items()}
        # The VPN advertises a DIFFERENT address to Android's system resolver.
        # Report that traffic separately; the broker's direct fixed DNS probe is .53.
        background=[r for r in rows if r['category']=='platform-dns']
        case['platformDnsPhysicalCounts']=counts(background)
        rows=[r for r in rows if r['category']!='platform-dns']
        case['physicalCounts']=counts(rows)
        print('PCAP '+name+' '+json.dumps(case['physicalCounts']),flush=True)
        print('PCAP_PLATFORM_DNS '+name+' '+json.dumps(case['platformDnsPhysicalCounts']),flush=True)
        host=[r for r in rows if r['category']=='host-control' and r['protocol']=='tcp']
        if name=='testDirectProcessRestrictions':assert not rows,'Restricted process emitted fixed traffic'
        if name in ('testFullTunnel','testPerAppInclude'):
            assert not rows,'Fixed target escaped full VPN route'
            for op in ('JAVA_TCP4','JAVA_UDP4','NATIVE_TCP4','NATIVE_UDP4','DNS_LOOKUP_TEST'):
                assert 'BEGIN op='+op+'\n' in obs and 'END op='+op+' ' in obs, 'Missing operation bounds: '+op
                window=obs.split('BEGIN op='+op+'\n',1)[1].split('END op='+op,1)[0]
                port={'JAVA_TCP4':46151,'JAVA_UDP4':46152,'NATIVE_TCP4':46153,'NATIVE_UDP4':46154,'DNS_LOOKUP_TEST':53}[op]
                assert re.search(r'PACKET .* port='+str(port)+r' ',window),'Missing per-operation TUN evidence: '+op
            assert 'category=synthetic-dns port=53' in obs,'Missing controlled DNS TUN evidence'
            case['ipv6']={}
            for op in ('JAVA_UDP6','NATIVE_UDP6'):
                window=obs.split('BEGIN op='+op+'\n')[-1].split('END op='+op)[0]
                case['ipv6'][op]='Preliminary evidence' if 'category=documentation-v6' in window else 'Unknown'
            print('IPV6 '+name+' '+json.dumps(case['ipv6']),flush=True)
        if name in ('testKnownGapPerAppExclude','testKnownGapPhysicalSelectionAllowed'):
            assert host,'Missing independent physical positive control'
            assert 'category=host-control' not in obs,'Excluded/physical host traffic unexpectedly entered TUN'
        if name=='testKnownGapSplitRoute':
            assert host and 'category=documentation-v4' in obs,'Missing split-route evidence'
            assert not any(r['category']=='documentation-v4' for r in rows),'Routed split target physically escaped'
            assert 'category=host-control' not in obs,'Outside split target entered TUN'
        if name=='testExistingSocketAndRealRevocation':
            assert 'STATE established mode=FULL_TUNNEL' in obs,'VPN not established for old socket'
            case['oldSocketPhysicalData']=sum(r['data'] for r in host)
            assert case['oldSocketPhysicalData']>0 or 'OLD_SOCKET_SEND result=success' not in obs,'Old socket success lacks independent data evidence'
        if name=='testVpnLossAndReconnect':
            assert host,'Missing OS physical fallback calibration evidence'
            race=gated_race_packets(rows)
            calibration=[r for r in host if r['port']==46153]
            assert calibration,'Missing distinct OS fallback calibration capture'
            assert race or 'LOSS immediate=success' not in obs,'Gated race success lacks distinct physical packet evidence'
            case['gatedRace']='Known Gap' if race else 'Not reproduced in this run'
            print('RACE '+name+' '+case['gatedRace'],flush=True)
        if name=='testProviderReplacement':
            assert 'STATE revoked' in obs and 'STATE established mode=FULL_TUNNEL' in obs,'Real provider replacement evidence missing'
        if name=='testLockdownLoss':
            assert 'alwaysOn=true lockdown=true' in obs,'Unverified lockdown'
            assert 'LOCKDOWN_DOWN vpn=false' in obs,'VPN loss not confirmed during lockdown'
            assert not rows,'SEVERE: fixed traffic escaped verified lockdown'

    assert all(c['passed'] for c in report['cases']), 'One or more network device cases failed'
    OUT.joinpath('summary.json').write_text(json.dumps(report,indent=2))
    print('NETWORK_EVIDENCE tests='+str(len(report['cases']))+' requiredEvidence=passed lockdown='+report['lockdown'])

if __name__=='__main__':
    import sys
    if sys.argv[1]=='exercise':exercise()
    elif sys.argv[1]=='analyze':analyze()
    else:raise SystemExit('Unknown fixed host operation')
