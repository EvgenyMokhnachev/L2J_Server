#! /bin/sh

kill $(ps aux | grep 'l2jlogin.jar' | awk '{print $2}') &&
kill $(ps aux | grep 'LoginServer_loop' | awk '{print $2}')