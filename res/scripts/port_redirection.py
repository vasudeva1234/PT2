#!/usr/bin/env python
import os
import sys
import subprocess
import signal

orig_port = sys.argv[1]
redir_port = sys.argv[2]

command = "iptables -t nat -A PREROUTING -p tcp --destination-port %s -j REDIRECT --to-ports %s" % (orig_port, redir_port)
print command
p1 = subprocess.Popen(command.split(), shell=False, preexec_fn=os.setsid)

print "Redirecting traffic from port %s to port %s" % (orig_port, redir_port)

sys.stdout.flush()

def signal_handler(signal, frame):
    print 'Killing pid %d' % p1.pid
    #os.kill(p1.pid, 9)
    p1.terminate()
    print 'Exiting now!'
    sys.stdout.flush()
    sys.exit(0)


signal.signal(signal.SIGTERM, signal_handler)

print "Port redirection complete"
