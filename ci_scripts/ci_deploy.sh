#!/bin/bash

# if this is a pull request or branch (non-master) build, then just exit
echo "TRAVIS_PULL_REQUEST=$TRAVIS_PULL_REQUEST, TRAVIS_BRANCH=$TRAVIS_BRANCH"
if [[ "$TRAVIS_PULL_REQUEST" != "false"  || "$TRAVIS_BRANCH" != "master" ]]; then
   echo "Not deploying pull request or branch build"
   exit
fi

check_variable_is_set(){
    if [[ -z ${!1} ]]; then
        echo "$1 must be set and non empty"
        exit 1
    fi
}

check_variable_is_set BIN_DIR
check_variable_is_set DEPLOY_SCRIPTS_URL
check_variable_is_set DEPLOY_SCRIPT_VERSION
check_variable_is_set APP_NAME

echo "Testing for existence of ${BIN_DIR}/deploy_scripts_${DEPLOY_SCRIPT_VERSION}"
if [[ ! -e ${BIN_DIR}/deploy_scripts_${DEPLOY_SCRIPT_VERSION} ]]; then
    echo "Installing deploy scripts"
    mkdir -p ${BIN_DIR}
    cd ${BIN_DIR}
    wget "${DEPLOY_SCRIPTS_URL}/${DEPLOY_SCRIPT_VERSION}.zip" -q -O deploy_scripts.zip && unzip -j -o deploy_scripts.zip && rm deploy_scripts.zip
    touch deploy_scripts_${DEPLOY_SCRIPT_VERSION}
    cd ..
fi

# determine APP_PATH
APP_VERSION=`cat version.properties | grep "version" | cut -d'=' -f2`
APP_PATH="api/build/libs/$APP_NAME-$APP_VERSION.jar"
export APP_PATH

# deploy
/bin/bash ${BIN_DIR}/deploy.sh && ${BIN_DIR}/trigger_java_cd_build.sh