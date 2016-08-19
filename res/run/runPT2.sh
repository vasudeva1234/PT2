#!/bin/sh

chmod +x ./scripts/*.sh
chmod +x ./scripts/*.py

export LD_LIBRARY_PATH=/usr/lib:lib:/opt/framework/lib

java -jar pt2-${version}.jar
