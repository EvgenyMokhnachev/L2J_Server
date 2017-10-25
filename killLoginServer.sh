#! /bin/sh

kill $(ps aux | grep 'l2jlogin.jar' | awk '{print $2}')