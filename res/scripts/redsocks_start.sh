#!/bin/bash
sudo killall redsocks
sudo redsocks -c ./redsocks.conf
#sudo iptables -t nat -N REDSOCKS
#sudo iptables -t nat -A REDSOCKS -d 0.0.0.0/8 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 10.0.0.0/8 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 127.0.0.0/8 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 169.254.0.0/16 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 172.16.0.0/12 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 192.168.0.0/16 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 224.0.0.0/4 -j RETURN
#sudo iptables -t nat -A REDSOCKS -d 240.0.0.0/4 -j RETURN
#sudo iptables -t nat -A REDSOCKS -p tcp -j REDIRECT --to-ports 12345
sudo iptables -t nat -A PREROUTING -p tcp --destination-port 20000 -j REDIRECT --to-port 12345
