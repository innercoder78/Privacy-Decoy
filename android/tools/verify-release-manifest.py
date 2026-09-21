"""Fail closed on unexpected release capabilities; never print manifest contents."""
import pathlib
import sys
import xml.etree.ElementTree as ET

def verify(path):
    root = ET.parse(path).getroot()
    name = '{http://schemas.android.com/apk/res/android}name'
    forbidden = {'android.permission.INTERNET', 'android.permission.ACCESS_NETWORK_STATE'}
    violations = []
    for node in root.iter():
        value = node.get(name, '')
        if node.tag.startswith('uses-permission') and value in forbidden:
            violations.append(value)
        if node.tag in {'service', 'activity', 'receiver', 'provider', 'activity-alias', 'action'}:
            if (value == 'android.net.VpnService' or any(v in value.lower() for v in
                    ('vpn', 'networkresearch', 'researchcontroller', 'fixture', 'pr5'))):
                violations.append(value)
        # Release intentionally has exactly one launcher and no research services.
        if node.tag in {'service', 'receiver', 'provider', 'activity-alias'}:
            violations.append(value)
    if violations:
        raise SystemExit('Forbidden release names: ' + ', '.join(sorted(set(violations))))
    print('Release XML verified: no network permissions or research components')

if __name__ == '__main__':
    path = pathlib.Path(sys.argv[1])
    if not path.is_file():
        raise SystemExit('Required generated release manifest missing')
    verify(path)
