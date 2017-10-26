#! /bin/sh

bash killGameServer.sh &&
bash killLoginServer.sh &&
bash updateFromGit.sh &&
bash startNewBuild.sh &&

exit