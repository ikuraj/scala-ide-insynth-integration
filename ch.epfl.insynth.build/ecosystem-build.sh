#!/bin/bash

# combinations of flavors to build
ECLIPSE_FLAVORS=( "indigo" )
SCALA_IDE_FLAVORS=( "dev-scala-ide-indigo-scala-2.9" )
SCALA_FLAVORS=( "2.9.x" )

# root dir (containing this script)
ROOT_DIR=$(dirname $0)
cd ${ROOT_DIR}
ROOT_DIR=${PWD}
#TARGET_DIR=/localhome/kuraj/temp/insynth-maven-build
TARGET_DIR=/localhome/kuraj/Dropbox/Public/insynth

for eclipse_flavor in "${ECLIPSE_FLAVORS[@]}"
do
for scala_ide_flavor in "${SCALA_IDE_FLAVORS[@]}"
do
for scala_flavor in "${SCALA_FLAVORS[@]}"
do

COMB="${eclipse_flavor}_${scala_ide_flavor}_${scala_flavor}"
echo "Bulding InSynth for flavors ${eclipse_flavor} + ${scala_ide_flavor} + ${scala_flavor} into ${TARGET_DIR}/${COMB}"

mvn -Pset-versions -P$eclipse_flavor -P$scala_ide_flavor -P$scala_flavor -Dtycho.style=maven --non-recursive exec:java

mvn -Pset-versions -P$eclipse_flavor -P$scala_ide_flavor -P$scala_flavor clean package

rm -rf ${TARGET_DIR}/$COMB
mkdir -p ${TARGET_DIR}

cp -r ${ROOT_DIR}/ch.epfl.insynth.update-site/target/site/ ${TARGET_DIR}/$COMB

done
done
done

exit