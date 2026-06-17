#!/usr/bin/env python3
"""Resolve a conflict block in a CRLF file.
Usage: resolve.py <file> <hunk_index 0-based> <mode> [payload-file]
modes:
  both     -> ours-body + theirs-body (keep both, in order)
  bothrev  -> theirs-body + ours-body
  ours     -> keep ours only
  theirs   -> keep theirs only
  repl     -> replace whole block with contents of payload-file (LF, will be CRLF-ized)
File paths relative to leaf-server/src/minecraft/java.
"""
import sys,os,re
ROOT="leaf-server/src/minecraft/java"
f=os.path.join(ROOT,sys.argv[1]); idx=int(sys.argv[2]); mode=sys.argv[3]
raw=open(f,encoding='utf-8',errors='replace').read()
crlf="\r\n" in raw
data=raw.replace("\r\n","\n")
pat=re.compile(r'<<<<<<<[^\n]*\n(.*?)\n\|\|\|\|\|\|\|[^\n]*\n(.*?)\n=======\n(.*?)\n>>>>>>>[^\n]*',re.S)
blocks=list(pat.finditer(data))
if idx>=len(blocks): sys.exit(f"only {len(blocks)} blocks")
m=blocks[idx]; ours,base,theirs=m.groups()
if mode=="both": rep=ours+"\n"+theirs
elif mode=="bothrev": rep=theirs+"\n"+ours
elif mode=="ours": rep=ours
elif mode=="theirs": rep=theirs
elif mode=="repl": rep=open(sys.argv[4],encoding='utf-8').read().replace("\r\n","\n").rstrip("\n")
else: sys.exit("bad mode")
data=data[:m.start()]+rep+data[m.end():]
if crlf: data=data.replace("\n","\r\n")
open(f,"w",encoding='utf-8',newline='').write(data)
left=len(pat.findall(data.replace("\r\n","\n")))
print(f"{sys.argv[1]}: resolved hunk {idx} ({mode}); {left} conflict(s) left")
