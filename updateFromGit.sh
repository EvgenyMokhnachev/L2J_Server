#! /bin/sh

rm -rf doc game images server.zip datapack.zip languages/ libs/ login/ sql/ tools/ &&
cd L2J_Server/ &&
git pull &&
cd .. &&
cd L2J_DataPack/ &&
git pull &&
gradle build &&
cd .. &&
cp L2J_DataPack/build/distributions/L2J_DataPack_2017-10-25.zip datapack.zip &&
cp L2J_Server/build/distributions/L2J_Server_2017-10-25.zip server &&
unzip datapack.zip &&
unzip server.zip &&

exit