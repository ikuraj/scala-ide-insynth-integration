#!/bin/bash

# combinations of flavors to build
ECLIPSE_FLAVORS=( "indigo" )
SCALA_IDE_FLAVORS=( "scala-ide-indigo-scala-2.9" "scala-ide-indigo-scala-2.10" )
SCALA_FLAVORS=( "2.9.x" "2.10.x" )

# root dir (containing this script)
#ROOT_DIR=$(dirname $0)
ROOT_DIR=${PWD}
TARGET_DIR=/localhome/kuraj/temp/insynth-maven-build

mkdir -p ${TARGET_DIR}

for eclipse_flavor in "${ECLIPSE_FLAVORS[@]}"
do
for array_index in `seq 0 1`
do

scala_ide_flavor=${SCALA_IDE_FLAVORS[$array_index]}
scala_flavor=${SCALA_FLAVORS[$array_index]}

COMB="${eclipse_flavor}_${scala_ide_flavor}_${scala_flavor}"
echo "Building InSynth for flavors ${eclipse_flavor} + ${scala_ide_flavor} + Scala ${scala_flavor} into ${TARGET_DIR}/${COMB}"

mvn -Pset-versions -P$eclipse_flavor -P$scala_ide_flavor -P$scala_flavor -Dtycho.style=maven --non-recursive exec:java

mvn -Pset-versions -P$eclipse_flavor -P$scala_ide_flavor -P$scala_flavor clean package

rm -rf ${TARGET_DIR}/$COMB

cp -r ${ROOT_DIR}/ch.epfl.insynth.update-site/target/site/ ${TARGET_DIR}/$COMB

# if needed publishing to LARA update site
echo "Copying files to insynth@laraserver.epfl.ch"
ssh insynth@laraserver.epfl.ch "rm -rf ~/public_html/$COMB"
scp -r ${TARGET_DIR}/$COMB insynth@laraserver.epfl.ch:~/public_html/

done
done

exit