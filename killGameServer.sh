#! /bin/sh

kill $(ps aux | grep 'l2jserver' | awk '{print $2}') &&
kill $(ps aux | grep 'GameServer_loop' | awk '{print $2}')