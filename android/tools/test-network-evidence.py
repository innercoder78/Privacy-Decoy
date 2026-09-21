import importlib.util
from pathlib import Path
import struct
import tempfile
import unittest

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

if __name__=='__main__':unittest.main()
