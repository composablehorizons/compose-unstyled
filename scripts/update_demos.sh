# Script that builds the WASM version of the demo and moves it to the docs resources

project_root=$(pwd)

menu_demo_destination="${project_root}/docs/menu-demo"
dialog_demo_destination="${project_root}/docs/dialog-demo"

mkdir -p "$menu_demo_destination"
mkdir -p "$dialog_demo_destination"

./gradlew jsBrowserDistribution

# shellcheck disable=SC2115
rm -rf "$menu_demo_destination"/*
# shellcheck disable=SC2115
rm -rf "$dialog_demo_destination"/*

mv "${project_root}"/demo-menu/build/dist/js/productionExecutable/* "$menu_demo_destination"
mv "${project_root}"/demo-dialog/build/dist/js/productionExecutable/* "$dialog_demo_destination"
