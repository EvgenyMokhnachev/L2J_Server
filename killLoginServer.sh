#! /bin/sh

(kill $(ps aux | grep 'l2jlogin.jar' | awk '{print $2}')) || (echo Killed l2jlogin.jar) &&
(kill $(ps aux | grep 'LoginServer_loop' | awk '{print $2}')) || (echo Killed LoginServer_loop)