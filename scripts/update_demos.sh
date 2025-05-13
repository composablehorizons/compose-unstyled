#!/bin/bash
## Build the project
./gradlew jsBrowserDevelopmentExecutableDistribution

project_root=$(pwd)

# Set demo directory and destination
demo_destination="${project_root}/docs/demo"
mkdir -p "$demo_destination"
rm -rf "$demo_destination"/*

# Move built files to destination
demo_source="${project_root}/demo/build/dist/js/developmentExecutable/"
cp -r $demo_source/* "$demo_destination"
