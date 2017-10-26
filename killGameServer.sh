#! /bin/sh

(kill $(ps aux | grep 'l2jserver' | awk '{print $2}')) || (echo Killed l2jserver.jar) &&
(kill $(ps aux | grep 'GameServer_loop' | awk '{print $2}'))  || (echo Killed GameServer_loop.sh)