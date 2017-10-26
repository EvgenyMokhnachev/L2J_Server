#! /bin/sh

rm -rf doc game images server.zip datapack.zip languages/ libs/ login/ sql/ tools/ &&
cd L2J_Server/ &&
git pull &&
cd .. &&
cd L2J_DataPack/ &&
git pull &&
gradle build &&
cd .. &&
cp L2J_DataPack/build/distributions/L2J_DataPack.zip datapack.zip &&
cp L2J_Server/build/distributions/L2J_Server.zip server.zip &&
unzip datapack.zip &&
unzip server.zip &&

exit