# Script that builds the JS version of the demo and moves it to the docs resources

## Build the project
./gradlew jsBrowserDevelopmentExecutableDistribution

project_root=$(pwd)

# Get all directories starting with demo- and exclude demo-ios
all_demo_dirs=($(find "$project_root" -maxdepth 1 -type d -name 'demo-*' ! -name 'demo-ios'))

demo_names=()
for dir in "${all_demo_dirs[@]}"; do
    demo_name=$(basename "$dir")
    demo_names+=("${demo_name#demo-}")
    # Remove the 'demo-' prefix
    demo_names+=("${demo_name#demo-}")
done

# Create directories for each demo
for demo in "${demo_names[@]}"; do
    demo_destination="${project_root}/docs/${demo}-demo"
    mkdir -p "$demo_destination"
    rm -rf "$demo_destination"/*
done

## Move built files to respective directories
for demo in "${demo_names[@]}"; do
    demo_source="${project_root}/demo-${demo}/build/dist/js/developmentExecutable/*"
    demo_destination="${project_root}/docs/${demo}-demo"
    mv $demo_source "$demo_destination"
done
