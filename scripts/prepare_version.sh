#!/bin/bash

# Define the root directory as the current directory
root_dir=$(pwd)

# Read the version from the keyboard
read -p "Enter docs version (e.g 1.0.0): " version

# Find and update the version in .md files
find "$root_dir" -type f -name "*.md" -exec sed -i '' "s/implementation(\"com\.composables:core:[^\"]*\")/implementation(\"com.composables:core:$version\")/g" {} +

# Read the version from the keyboard
read -p "Enter WIP version (e.g., 1.8.0): " version

# Update the publishVersion value in build.gradle.kts
sed -i '' "s/^val publishVersion = \".*\"/val publishVersion = \"$version\"/" "$root_dir/core/build.gradle.kts"

# Add all changes to git
git add .

# Commit with the provided version in the message
git commit -m "Prepare version $version"

echo "👍 Updated all files and created a git commit"