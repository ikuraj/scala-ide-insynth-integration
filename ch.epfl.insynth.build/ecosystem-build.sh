#!/bin/bash -e

if [ -n "${DEBUG}" ]
then
  set -x
fi

if ( ! getopts "urls" opt); then
  echo "Usage: `basename $0` options: websites(-s "url [...]") publish(-p)";
  exit $E_OPTERROR;
fi

while getopts ":s:p" opt; do
  case "$opt" in
    s) SITES=$OPTARG ;;
    p) PUBLISH=true ;;
  esac
done

echo sites is $SITES

# takes has parameters eclipse update site URLs, like:
# http://download.scala-ide.org/sdk/e37/scala29/stable/site/
# http://download.scala-ide.org/sdk/next/e38/scala210/dev/site/
#
# It extract the profile information from the URL, build the plugins,
# and merge the update sites.

# root dir (containing this script)
ROOT_DIR=$(dirname $0)
cd ${ROOT_DIR}
ROOT_DIR=${PWD}

TARGET_DIR=${ROOT_DIR}/ecosystem
mkdir -p ${TARGET_DIR}

############################
## Installing merge tool
############################
MERGE_TOOL_REPO_DIR=${TARGET_DIR}/merge-tool
MERGE_TOOL_DIR=${MERGE_TOOL_REPO_DIR}/maven-tool/merge-site
if [ ! -d "${MERGE_TOOL_REPO_DIR}" ]
then
  git clone -o origin git://github.com/scala-ide/build-tools "${MERGE_TOOL_REPO_DIR}"
fi

cd "${MERGE_TOOL_REPO_DIR}"
git fetch origin
git checkout master
git reset --hard
git merge --ff-only origin/master


############################
## Real script
############################

COMBINED_SITE_DIR=${TARGET_DIR}/site
mkdir -p ${COMBINED_SITE_DIR}

for ECOSYSTEM_SITE in $SITES
do

  cd ${ROOT_DIR}

  echo ${ECOSYSTEM_SITE}
  ECLIPSE_VERSION=${ECOSYSTEM_SITE#*/sdk*/e}
  ECLIPSE_VERSION=${ECLIPSE_VERSION%%/*}

  SCALA_VERSION=${ECOSYSTEM_SITE#*/sdk*/e*/scala}
  SCALA_VERSION=${SCALA_VERSION%%/*}

  case ${ECLIPSE_VERSION} in
    37 )
      ECLIPSE_PROFILE="-Peclipse-indigo"
      ;;
    38 )
      ECLIPSE_PROFILE="-Peclipse-juno"
      ;;
  esac

  case ${SCALA_VERSION} in
    29 )
      SCALA_PROFILE="-Pscala-2.9.x"
      ;;
    210 )
      SCALA_PROFILE="-Pscala-2.10.x"
      ;;
    211 )
      SCALA_PROFILE="-Pscala-2.11.x"
      ;;
  esac

  echo "Building InSynth with '${ECLIPSE_PROFILE} ${SCALA_PROFILE}'"

  mvn -Pset-versions "${ECLIPSE_PROFILE}" "${SCALA_PROFILE}" -Drepo.scala-ide=${ECOSYSTEM_SITE} -Dtycho.style=maven --non-recursive exec:java

  mvn "${ECLIPSE_PROFILE}" "${SCALA_PROFILE}" -Drepo.scala-ide=${ECOSYSTEM_SITE} -Dversion.tag=v clean package

  echo "Copying into combined site"

  cd "${MERGE_TOOL_DIR}"
  mvn "-Drepo.source=file://${ROOT_DIR}/ch.epfl.insynth.update-site/target/site" "-Drepo.dest=${COMBINED_SITE_DIR}" package
  
done

# if needed publishing to LARA update site
if [ ! $PUBLISH ];
then
  echo "Not copying files to insynth@laraserver.epfl.ch"
else  
  echo "Copying files to insynth@laraserver.epfl.ch"
  ssh insynth@laraserver.epfl.ch "rm -rf ~/public_html/combined"
  scp -r ${COMBINED_SITE_DIR} insynth@laraserver.epfl.ch:~/public_html/combined
fi

echo "All done"
