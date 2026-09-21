#!/usr/bin/env python3
import pathlib, subprocess, tempfile
SCRIPT=pathlib.Path(__file__).with_name("managed-profile-evidence.py")
def evidence(root,event,uid,user,offset=0):
 d={"schema":"1","event":event,"uid":str(uid),"user":str(user),"pid":"1","native_uid":str(uid),"management_open":"BLOCKED","peer_open":"BLOCKED","java_management_read":"false","java_management_write":"false"}
 d.update({f"surface_{i}_hash":("same" if i==0 else f"h{i+offset}") for i in range(10)})
 pathlib.Path(root,f"evidence-{event}").write_text("".join(f"{k}={v}\n" for k,v in d.items()))
with tempfile.TemporaryDirectory() as t:
 roots=[pathlib.Path(t,x) for x in ("p","a","b")]
 for r in roots:r.mkdir()
 for event in ("provider","application","activity","service","receiver"):
  evidence(roots[0],event,10001,0);evidence(roots[1],event,110001,10,20);evidence(roots[2],event,110002,10,40)
 cmd=[str(SCRIPT),"--parent",str(roots[0]),"--tenant-a",str(roots[1]),"--tenant-b",str(roots[2]),"--output",t+"/report.json"]
 assert subprocess.run(cmd,capture_output=True,text=True).returncode==0
 pathlib.Path(roots[2],"evidence-service").unlink()
 assert subprocess.run(cmd,capture_output=True,text=True).returncode!=0
print("managed profile evidence tests passed")
