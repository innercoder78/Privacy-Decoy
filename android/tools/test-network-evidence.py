import importlib.util
from pathlib import Path
import struct
import tempfile
import unittest
from unittest import mock

spec=importlib.util.spec_from_file_location('evidence',Path(__file__).with_name('network-evidence.py'))
evidence=importlib.util.module_from_spec(spec);spec.loader.exec_module(evidence)

class CaptureTests(unittest.TestCase):
    def frame(self,dest=(10,0,2,2),port=46152):
        ip=bytearray(20);ip[0]=0x45;ip[2:4]=struct.pack('!H',28);ip[9]=17;ip[16:20]=bytes(dest)
        return bytes(12)+b'\x08\x00'+ip+struct.pack('!HHHH',40000,port,8,0)

    def capture(self,frames):
        data=struct.pack('<IHHIIII',0xa1b2c3d4,2,4,0,0,65535,1)
        # Deliberately unrelated virtual-clock timestamps.
        for frame in frames:data+=struct.pack('<IIII',100,0,len(frame),len(frame))+frame
        return data

    def parse(self,data):
        with tempfile.TemporaryDirectory() as directory:
            path=Path(directory)/'synthetic.pcap';path.write_bytes(data)
            return list(evidence.packets(path))

    def test_filters_unrelated_destinations_and_ports(self):
        rows=self.parse(self.capture([self.frame(),self.frame((203,0,113,4)),self.frame(port=9000)]))
        self.assertEqual(len(rows),1)
        self.assertEqual(rows[0]['category'],'host-control')

    def test_controlled_dns_is_distinct_from_platform_resolver(self):
        rows=self.parse(self.capture([self.frame((198,51,100,53),53),self.frame((198,51,100,54),53)]))
        self.assertEqual([r['category'] for r in rows],['synthetic-dns','platform-dns'])

    def test_wifi_positive_control_uses_offsets_not_wall_clock(self):
        rows=self.parse(self.capture([self.frame(),self.frame()]))
        case={'start':2000000000,'end':2000000001,
              'captureStart':{'physical.pcap':24,'wifi.pcap':rows[1]['recordStart']},
              'captureEnd':{'physical.pcap':24,'wifi.pcap':rows[1]['recordEnd']}}
        self.assertEqual(evidence.case_packets({'physical.pcap':[],'wifi.pcap':rows},case),[rows[1]])

    def test_boundary_overlap_is_included_conservatively(self):
        rows=self.parse(self.capture([self.frame()]))
        case={'captureStart':{'wifi.pcap':25},'captureEnd':{'wifi.pcap':rows[0]['recordEnd']-1}}
        self.assertEqual(evidence.case_packets({'wifi.pcap':rows},case),rows)

    def test_gated_race_counts_syn_without_payload(self):
        syn=dict(category='host-control',protocol='tcp',port=46151,data=False)
        calibration=dict(syn,port=46153)
        self.assertEqual(evidence.gated_race_packets([syn,calibration]),[syn])
        self.assertEqual(evidence.gated_race_packets([calibration]),[])

    def test_rejects_truncated_record(self):
        with self.assertRaisesRegex(AssertionError,'truncated'):
            self.parse(self.capture([self.frame()])[:-1])

    def test_rejects_missing_capture_header(self):
        with self.assertRaisesRegex(AssertionError,'header'):
            self.parse(b'')

class LockdownReadinessTests(unittest.TestCase):
    def result(self,stdout='',returncode=0):
        return mock.Mock(stdout=stdout,stderr='',returncode=returncode)

    def ready_result(self,args,launches):
        if args[0]=='wait-for-device':return self.result()
        if args[1:3]==('getprop','sys.boot_completed'):return self.result('1\n')
        if args[1:3]==('pm','path'):return self.result('package:/fixture.apk\n')
        if args[1:4]==('cmd','package','resolve-activity'):
            self.assertEqual(args,('shell','cmd','package','resolve-activity','--components',
                                   '-n',evidence.A+'/'+evidence.CONTROLLER))
            return self.result('com.privacydecoy.externalvpnfixture/.FixtureController\n')
        if args[1:4]==('cmd','activity','get-config'):return self.result('config\n')
        if args[1:3]==('appops','set'):return self.result()
        if args[1:3]==('appops','get'):return self.result('ACTIVATE_VPN: allow\n')
        if args[0:2]==('logcat','-c'):return self.result()
        if args[1:3]==('am','start'):
            launches.append(args)
            return self.result(returncode=1 if len(launches)==1 else 0)
        self.fail('unexpected adb call: '+repr(args))

    def test_lockdown_setup_retries_only_controller_setup(self):
        launches=[]
        with mock.patch.object(evidence,'adb_result',side_effect=lambda *a,**k:self.ready_result(a,launches)), \
             mock.patch.object(evidence.time,'sleep'):
            evidence.lockdown_setup_control(deadline_seconds=2,interval=0)
        self.assertEqual(len(launches),2)
        self.assertTrue(all(call[-1]=='FULL_TUNNEL_BYPASS' for call in launches))

    def test_lockdown_setup_rejects_unresolved_or_wrong_component_output(self):
        for output in ('No activity found\n', '',
                       'other.package/.FixtureController\n',
                       'other.package/'+evidence.CONTROLLER+'\n',
                       'priority=0\n'+evidence.A+'/.FixtureController\n'):
            with self.subTest(output=output):
                launches=[]
                def unresolved(*args,**kwargs):
                    result=self.ready_result(args,launches)
                    if args[1:4]==('cmd','package','resolve-activity'):
                        return self.result(output)
                    return result
                ticks=iter((0,0,0,2))
                with mock.patch.object(evidence,'adb_result',side_effect=unresolved), \
                     mock.patch.object(evidence.time,'monotonic',side_effect=lambda:next(ticks)), \
                     mock.patch.object(evidence.time,'sleep'):
                    with self.assertRaisesRegex(AssertionError,r'^Lockdown setup failed: controller-unresolved$'):
                        evidence.lockdown_setup_control(deadline_seconds=1,interval=0)
                self.assertEqual(launches,[])

    def test_lockdown_setup_reports_fixed_package_category(self):
        def unavailable(*args,**kwargs):
            if args[0]=='wait-for-device':return self.result()
            if args[1:3]==('getprop','sys.boot_completed'):return self.result('1\n')
            return self.result(returncode=1)
        ticks=iter((0,0,2,2))
        with mock.patch.object(evidence,'adb_result',side_effect=unavailable), \
             mock.patch.object(evidence.time,'monotonic',side_effect=lambda:next(ticks)), \
             mock.patch.object(evidence.time,'sleep'):
            with self.assertRaisesRegex(AssertionError,r'^Lockdown setup failed: package-unavailable$'):
                evidence.lockdown_setup_control(deadline_seconds=1,interval=0)

if __name__=='__main__':unittest.main()
