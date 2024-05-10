# Script that builds the WASM version of the demo and moves it to the docs resources

project_root=$(pwd)

destination_dir="${project_root}/docs/preview"

mkdir -p "$destination_dir"

./gradlew jsBrowserDistribution

# shellcheck disable=SC2115
rm -rf "$destination_dir"/*

mv "${project_root}"/demo/build/dist/js/productionExecutable/* "$destination_dir"
