#!/usr/bin/env python

import sys
import subprocess

sys.stdout.write("Flushing IP tables...")
sys.stdout.flush()

subprocess.call(["iptables","-F"])
subprocess.call(["iptables","-X"])
subprocess.call(["iptables","-t","nat","-F"])
subprocess.call(["iptables","-t","nat","-X"])
subprocess.call(["iptables","-t","mangle","-F"])
subprocess.call(["iptables","-t","mangle","-X"])
subprocess.call(["iptables","-P","INPUT","ACCEPT"])
subprocess.call(["iptables","-P","FORWARD","ACCEPT"])
subprocess.call(["iptables","-P","OUTPUT","ACCEPT"])

sys.stdout.write("Done\n")
sys.stdout.flush()

