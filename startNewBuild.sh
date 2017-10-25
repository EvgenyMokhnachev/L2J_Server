#! /bin/sh

cd game/ &&
mkdir log &&
chmod +x GameServer_loop.sh &&
bash startGameServer.sh &&
cd .. &&
cd login/ &&
mkdir log &&
chmod +x LoginServer_loop.sh &&
bash startLoginServer.sh &&

exit