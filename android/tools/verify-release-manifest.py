#!/usr/bin/env python3
"""Verify only conservative release-manifest properties; this is not mediation proof."""
import pathlib, sys, xml.etree.ElementTree as ET
ANDROID='{http://schemas.android.com/apk/res/android}'
def verify(path):
    root=ET.parse(path).getroot(); name=ANDROID+'name'; permission=ANDROID+'permission'; violations=[]
    app=root.find('application')
    if app is None: violations.append('missing application')
    else:
        if app.get(ANDROID+'allowBackup') != 'false': violations.append('allowBackup must be false')
        if app.get(ANDROID+'fullBackupContent') != 'false': violations.append('fullBackupContent must be false')
        if not app.get(ANDROID+'dataExtractionRules'): violations.append('dataExtractionRules missing')
    forbidden_permissions={'android.permission.INTERNET','android.permission.ACCESS_NETWORK_STATE','android.permission.BIND_VPN_SERVICE'}
    for node in root.iter():
        value=node.get(name,''); service_permission=node.get(permission,'')
        if node.tag.startswith('uses-permission') and value in forbidden_permissions: violations.append(value)
        if node.tag in {'service','receiver','provider','activity-alias'}: violations.append('unexpected '+node.tag+': '+value)
        if node.tag == 'action' and value == 'android.net.VpnService': violations.append(value)
        if value == 'android.net.VpnService' or service_permission == 'android.permission.BIND_VPN_SERVICE': violations.append(value or service_permission)
        if node.tag in {'service','activity','receiver','provider','activity-alias'} and any(x in value.lower() for x in ('vpn','networkresearch','researchcontroller','fixture','containment')): violations.append(value)
    if violations: raise SystemExit('Forbidden release properties: '+', '.join(sorted(set(violations))))
    print('Release XML verified: conservative backup settings and no VPN, network, or research components')
if __name__=='__main__':
    path=pathlib.Path(sys.argv[1])
    if not path.is_file(): raise SystemExit('Required generated release manifest missing')
    verify(path)
