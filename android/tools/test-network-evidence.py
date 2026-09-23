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


class CaptureQuiescenceTests(unittest.TestCase):
    def settle(self,offsets):
        elapsed=[0.0]
        def sleep(seconds):
            elapsed[0]+=seconds
        with mock.patch.object(evidence.time,'monotonic',side_effect=lambda:elapsed[0]), \
             mock.patch.object(evidence.time,'sleep',side_effect=sleep), \
             mock.patch.object(evidence,'capture_offsets',side_effect=lambda:offsets(elapsed[0])), \
             mock.patch.object(evidence,'adb') as adb:
            result=evidence.capture_end_after_quiescence(interval=.125)
        adb.assert_not_called()
        return result,elapsed[0]

    def test_stable_sizes_wait_for_minimum_observation(self):
        offsets={'physical.pcap':24,'wifi.pcap':80}
        result,elapsed=self.settle(lambda _:dict(offsets))
        self.assertEqual(result,offsets)
        self.assertEqual(elapsed,1.0)

    def test_changes_in_either_capture_restart_quiet_interval(self):
        def offsets(t):
            return {'physical.pcap':24 if t<.875 else 100,
                    'wifi.pcap':24 if t<1.25 else 200}
        result,elapsed=self.settle(offsets)
        self.assertEqual(result,{'physical.pcap':100,'wifi.pcap':200})
        self.assertEqual(elapsed,1.75)

    def test_continuous_changes_return_final_offsets_at_deadline(self):
        result,elapsed=self.settle(lambda t:{'physical.pcap':24+int(t*1000),
                                            'wifi.pcap':48+int(t*2000)})
        self.assertEqual(elapsed,3.0)
        self.assertEqual(result,{'physical.pcap':3024,'wifi.pcap':6048})

    def test_run_case_instruments_once_and_uses_settled_offsets(self):
        start={'physical.pcap':24,'wifi.pcap':24}
        end={'physical.pcap':100,'wifi.pcap':200}
        with mock.patch.object(evidence,'reset'), \
             mock.patch.object(evidence,'capture_offsets',return_value=start), \
             mock.patch.object(evidence,'capture_end_after_quiescence',return_value=end) as settle, \
             mock.patch.object(evidence,'adb',return_value='Tests run: 1, Failures: 0') as adb, \
             mock.patch.object(evidence,'logs',return_value='PASS testFullTunnel'), \
             mock.patch('builtins.print'):
            case=evidence.run_case('testFullTunnel',None)
        adb.assert_called_once()
        self.assertEqual(adb.call_args.args[:3],('shell','am','instrument'))
        settle.assert_called_once_with()
        self.assertEqual(case['captureStart'],start)
        self.assertEqual(case['captureEnd'],end)
        self.assertTrue(case['passed'])

class TunAttributionTests(unittest.TestCase):
    # Independent expected tuples prevent the tests from merely echoing the map.
    signatures=(('JAVA_TCP4',4,'tcp','host-control',46151),
                ('JAVA_UDP4',4,'udp','documentation-v4',46152),
                ('NATIVE_TCP4',4,'tcp','host-control',46153),
                ('NATIVE_UDP4',4,'udp','documentation-v4',46154),
                ('DNS_LOOKUP_TEST',4,'udp','synthetic-dns',53))

    def packet(self,family,protocol,category,port):
        return (f'PACKET mode=FULL_TUNNEL family={family} protocol={protocol} '
                f'category={category} port={port} count=1')

    def bounds(self,op):
        return ['BEGIN op='+op,'END op='+op+' result=success']

    def test_exact_signatures_before_and_after_end(self):
        for op,*signature in self.signatures:
            for delayed in (False,True):
                with self.subTest(op=op,delayed=delayed):
                    obs=self.bounds(op)
                    obs.insert(2 if delayed else 1,self.packet(*signature))
                    evidence.require_tun_operation(obs,op)

    def test_wrong_tuple_or_missing_packet_fails(self):
        for op,family,protocol,category,port in self.signatures:
            for changes in ({'port':port+1},{'category':'other'},
                            {'category':'platform-dns'},{'family':6},
                            {'protocol':'udp' if protocol=='tcp' else 'tcp'}):
                with self.subTest(op=op,changes=changes):
                    fields=dict(family=family,protocol=protocol,category=category,port=port)
                    fields.update(changes)
                    with self.assertRaisesRegex(AssertionError,'Missing per-operation TUN evidence: '+op):
                        evidence.require_tun_operation(self.bounds(op)+[self.packet(**fields)],op)
            with self.assertRaisesRegex(AssertionError,'Missing per-operation TUN evidence: '+op):
                evidence.require_tun_operation(self.bounds(op),op)

    def test_udp4_requires_documentation_destination_not_host_control(self):
        for op,port in (('JAVA_UDP4',46152),('NATIVE_UDP4',46154)):
            with self.subTest(op=op):
                evidence.require_tun_operation(
                    self.bounds(op)+[self.packet(4,'udp','documentation-v4',port)],op)
                with self.assertRaisesRegex(AssertionError,'Missing per-operation TUN evidence: '+op):
                    evidence.require_tun_operation(
                        self.bounds(op)+[self.packet(4,'udp','host-control',port)],op)

    def test_native_udp4_packet_in_other_case_cannot_satisfy_current_case(self):
        op='NATIVE_UDP4'
        previous=self.bounds(op)+[self.packet(4,'udp','documentation-v4',46154)]
        current=self.bounds(op)
        evidence.require_tun_operation(previous,op)
        with self.assertRaisesRegex(AssertionError,'Missing per-operation TUN evidence: NATIVE_UDP4'):
            evidence.require_tun_operation(current,op)

    def test_both_exact_operation_markers_are_required(self):
        op='NATIVE_UDP4';packet=self.packet(4,'udp','documentation-v4',46154)
        for bounds in ([],self.bounds(op)[:1],self.bounds(op)[1:],
                       self.bounds(op+'_OTHER')):
            with self.subTest(bounds=bounds):
                with self.assertRaisesRegex(AssertionError,'Missing operation bounds: '+op):
                    evidence.require_tun_operation(bounds+[packet],op)

if __name__=='__main__':unittest.main()
