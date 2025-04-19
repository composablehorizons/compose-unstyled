# Script that builds the JS version of the demo and moves it to the docs resources

project_root=$(pwd)
demo_names=("dialog" "icon" "menu" "modalsheet" "sheet" "separators" "scrollarea")

# Create directories for each demo
for demo in "${demo_names[@]}"; do
    demo_destination="${project_root}/docs/${demo}"
    mkdir -p "$demo_destination-demo"
    rm -rf "$demo_destination-demo"/*
done

## Build the project
./gradlew jsBrowserDevelopmentExecutableDistribution

## Move built files to respective directories
for demo in "${demo_names[@]}"; do
    demo_source="${project_root}/demo-${demo%-demo}/build/dist/js/developmentExecutable/*"
    demo_destination="${project_root}/docs/${demo}-demo"
    mv $demo_source "$demo_destination"
done
