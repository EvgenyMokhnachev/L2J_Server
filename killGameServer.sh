#! /bin/sh

kill $(ps aux | grep 'l2jserver' | awk '{print $2}')