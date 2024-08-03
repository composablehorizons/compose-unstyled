#!/bin/bash

# Define the root directory as the current directory
root_dir=$(pwd)

# Read the docs version from the keyboard
previous_version=$(grep '^val publishVersion =' "$root_dir/core/build.gradle.kts" | sed 's/^val publishVersion = "\([^\"]*\)"/\1/')

read -p "🚢 Shipping version $previous_version (y/n):" confirm
if [[ "$confirm" != "y" ]]; then
    echo "Aborted."
    exit 1
fi

echo "Publishing to Sonar..."

./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository

echo "👍 Published"

# Retrieve the last commit hash
last_commit=$(git rev-parse HEAD)

# Tag the last commit with the last version
git tag "$previous_version" $last_commit

# Read the WIP version from the keyboard
read -p "Enter next version (e.g., 1.8.0): " wip_version

# Find and update the version in .md files
find "$root_dir" -type f -name "*.md" -exec sed -i '' "s/implementation(\"com\.composables:core:[^\"]*\")/implementation(\"com.composables:core:$previous_version\")/g" {} +

# Update the publishVersion value in build.gradle.kts
sed -i '' "s/^val publishVersion = \".*\"/val publishVersion = \"$wip_version\"/" "$root_dir/core/build.gradle.kts"

# Add all changes to git
git add .

# Commit with the provided version in the message
git commit -m "Prepare version $wip_version"

git push origin --follow-tags

echo "💯 All done. Working version is $previous_version"
