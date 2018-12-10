#!/bin/bash

BASE_URL=https://$1

echo "Running smoke tests against ${BASE_URL}"
cd smoke_tests
./gradlew clean test -Dbase_url=${BASE_URL}