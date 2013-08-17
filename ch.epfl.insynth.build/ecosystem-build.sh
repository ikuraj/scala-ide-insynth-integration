#!/bin/bash -e

# url names for each flavor defined below
URL_NAMES=(
"dev-indigo-2_9"
"dev-indigo-2_10"
"dev-juno-2_9"
"dev-juno-2_10"
"stable-indigo-2_9"
"stable-indigo-2_10"
"stable-juno-2_9"
"stable-juno-2_10"
)

# combinations of flavors to build
FLAVORS=(
"-Pindigo -Pdev-scala-ide-indigo-scala-2.9 -P2.9.x"
"-Pindigo -Pdev-scala-ide-indigo-scala-2.10 -P2.10.x"
"-Pjuno -Pdev-scala-ide-juno-scala-2.9 -P2.9.x"
"-Pjuno -Pdev-scala-ide-juno-scala-2.10 -P2.10.x"
"-Pindigo -Pstable-scala-ide-indigo-scala-2.9 -P2.9.x"
"-Pindigo -Pstable-scala-ide-indigo-scala-2.10 -P2.10.x"
"-Pjuno -Pstable-scala-ide-juno-scala-2.9 -P2.9.x"
"-Pjuno -Pstable-scala-ide-juno-scala-2.10 -P2.10.x"
)

# root dir (containing this script)
#ROOT_DIR=$(dirname $0)
ROOT_DIR=${PWD}
TARGET_DIR=/localhome/kuraj/temp/insynth-maven-build

mkdir -p ${TARGET_DIR}

for ((i=0; i < ${#FLAVORS[@]}; i++))
do

FLAVOR=${FLAVORS[$i]}

COMB=${URL_NAMES[$i]}
echo "Building InSynth with $FLAVOR into ${TARGET_DIR}/${COMB}"

mvn -Pset-versions $FLAVOR -Dtycho.style=maven --non-recursive exec:java

mvn -Pset-versions $FLAVOR -Dversion.tag=v clean package

RETVAL=$?
[ $RETVAL -ne 0 ] && echo "Maven build for $FLAVOR failed! Press enter to continue..." && read line

rm -rf ${TARGET_DIR}/$COMB

cp -r ${ROOT_DIR}/ch.epfl.insynth.update-site/target/site/ ${TARGET_DIR}/$COMB

# if needed publishing to LARA update site
if [ $# -lt 1 ];
then
	echo "Not copying files to insynth@laraserver.epfl.ch"
else  
	echo "Copying files to insynth@laraserver.epfl.ch"
	ssh insynth@laraserver.epfl.ch "rm -rf ~/public_html/$COMB"
	scp -r ${TARGET_DIR}/$COMB insynth@laraserver.epfl.ch:~/public_html/
fi

done

exit
