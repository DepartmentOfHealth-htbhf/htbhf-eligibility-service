#!/bin/bash

echo "Running smoke tests against $1"
cd integration
./gradlew clean test -Dbase_url=https://$1