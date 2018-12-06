#!/bin/bash

remove_route() {
  MY_HOST=$1
  MY_DOMAIN=$2
  MY_APP=$3
  if cf check-route ${MY_HOST} ${MY_DOMAIN} | grep "does exist"; then
    cf unmap-route $3 ${MY_DOMAIN} --hostname ${MY_HOST}
    cf delete-route -f ${MY_DOMAIN} --hostname ${MY_HOST}
  fi
}

perform_first_time_deployment() {
  echo "$APP_FULL_NAME does not exist, doing regular deployment"

  cf push -p ${APP_PATH} --var suffix=${CF_SPACE}

  ROUTE=$(cat /dev/urandom | tr -dc 'a-z' | fold -w 16 | head -n 1)
  cf map-route ${APP_FULL_NAME} ${CF_PUBLIC_DOMAIN} --hostname ${ROUTE}

  (./ci_scripts/integration_tests.sh ${ROUTE}.${CF_PUBLIC_DOMAIN})
  RESULT=$?

  remove_route ${ROUTE} ${CF_PUBLIC_DOMAIN} ${APP_FULL_NAME}

  if [[ ${RESULT} != 0 ]]; then
    echo "Tests failed, rolling back deployment of $APP_FULL_NAME"
    cf delete -f -r ${APP_FULL_NAME}
    exit 1
  fi
}

perform_blue_green_deployment() {
  echo "$APP_FULL_NAME exists, performing blue-green deployment"
  BLUE_APP="${APP_FULL_NAME}"
  GREEN_APP="${APP_FULL_NAME}-green"

  echo "# pushing new (green) app without a route"
  cf push -p ${APP_PATH} --var suffix=${CF_SPACE}-green --no-route

  echo "# creating a temporary (public) route to the green app"
  ROUTE=$(cat /dev/urandom | tr -dc 'a-z' | fold -w 16 | head -n 1)
  cf map-route ${GREEN_APP} ${CF_PUBLIC_DOMAIN} --hostname ${ROUTE}

  echo "# run smoke tests"
  (./ci_scripts/smoke_tests.sh ${ROUTE}.${CF_PUBLIC_DOMAIN})
  RESULT=$?

  echo "# removing the temporary route"
  remove_route ${ROUTE} ${CF_PUBLIC_DOMAIN} ${GREEN_APP}

  # roll back if tests failed
  if [[ ${RESULT} != 0 ]]; then
    echo "# Tests failed, rolling back deployment of GREEN_APP"
    cf delete -f -r ${GREEN_APP}
    exit 1
  fi

  echo "# start routing traffic to green (in addition to blue)"
  cf map-route ${GREEN_APP} ${CF_DOMAIN} --hostname ${APP_FULL_NAME}
  echo "# stop routing traffic to blue"
  unmap_blue_route
  echo "# delete blue"
  cf delete -f ${BLUE_APP}
  echo "# rename green -> blue"
  cf rename ${GREEN_APP} ${BLUE_APP}
}

unmap_blue_route() {
  if cf check-route ${APP_FULL_NAME} ${CF_DOMAIN}; then
    cf unmap-route ${APP_FULL_NAME} ${CF_DOMAIN} --hostname ${APP_FULL_NAME}
  fi
}

export PATH=$PATH:./bin

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
 # check necessary environment variables are set and not empty
check_variable_is_set APP_NAME
check_variable_is_set APP_LOCATION
check_variable_is_set CF_SPACE
check_variable_is_set CF_API
check_variable_is_set CF_ORG
check_variable_is_set CF_USER
check_variable_is_set CF_PASS
check_variable_is_set CF_DOMAIN
check_variable_is_set CF_PUBLIC_DOMAIN

/bin/bash ci_scripts/install_cf_cli.sh;

APP_FULL_NAME="$APP_NAME-$CF_SPACE"

echo "Logging into cloud foundry with api:$CF_API, org:$CF_ORG, space:$CF_SPACE with user:$CF_USER"
cf login -a ${CF_API} -u ${CF_USER} -p "${CF_PASS}" -s ${CF_SPACE} -o ${CF_ORG}

echo "Deploying $APP_FULL_NAME to $CF_SPACE"

APP_VERSION=`cat version.properties | grep "version" | cut -d'=' -f2`
APP_PATH="${APP_LOCATION}/$APP_NAME-$APP_VERSION.jar"

# if the app already exists, perform a blue green deployment, if not then a regular deployment
if cf app ${APP_FULL_NAME} >/dev/null 2>/dev/null; then
  perform_blue_green_deployment
else
  perform_first_time_deployment
fi