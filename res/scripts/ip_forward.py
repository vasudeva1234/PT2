#!/usr/bin/env python
import os
import sys
import subprocess


print "Enabling ip forwarding"

c1 = "sysctl -w net.ipv4.ip_forward=1"
p1 = subprocess.Popen(c1.split(), shell=False, preexec_fn=os.setsid)
print c1

sys.stdout.flush()

