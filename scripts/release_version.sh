#!/bin/bash
set -e

echo "Pulling changes..."
git fetch origin

# Get current branch name
current_branch=$(git rev-parse --abbrev-ref HEAD)

if [ "$current_branch" = "main" ]; then
    echo "On main branch, pulling latest changes..."
    git pull
else
    echo "On branch $current_branch, rebasing from latest tag..."
    # Get the latest tag
    latest_tag=$(git describe --tags --abbrev=0)
    echo "Latest tag: $latest_tag"
    git rebase "$latest_tag"
fi

echo "Compiling Kotlin..."
./gradlew core:assemble


echo "Starting Android emulator..."
emulator_name="Tests"

# Set Android SDK paths
ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
EMULATOR="$ANDROID_HOME/emulator/emulator"
ADB="$ANDROID_HOME/platform-tools/adb"

# Reduce emulator shutdown wait time from 20s to 2s
export ANDROID_EMULATOR_WAIT_TIME_BEFORE_KILL=2

echo "Launching emulator: $emulator_name"
"$EMULATOR" -avd "$emulator_name" -no-audio -no-boot-anim &
emulator_pid=$!

# Wait for emulator to boot
echo "Waiting for emulator to boot..."
"$ADB" wait-for-device
"$ADB" shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'
echo "Emulator booted successfully"

echo "Running tests..."
./gradlew core:jvmTest core:connectedAndroidTest

# Kill the emulator after tests
echo "Shutting down emulator..."
"$ADB" emu kill 2>/dev/null &
sleep 1
kill -9 $emulator_pid 2>/dev/null || true

echo "All tests passed..."

# Define the root directory as the current directory
root_dir=$(pwd)

# Read the docs version from libs.versions.toml
previous_version=$(grep '^unstyled =' "$root_dir/gradle/libs.versions.toml" | sed 's/^unstyled = "\([^\"]*\)"/\1/')

read -p "🚢 Current version is $previous_version – read from libs.versions.toml. Ship it? (y/n):" confirm
if [[ "$confirm" != "y" ]]; then
    echo "Aborted."
    exit 1
fi

# Read the WIP version from the keyboard
read -p "Enter next version (e.g., 1.8.0): " wip_version

echo "Publishing to Sonar..."

./gradlew publishAllPublicationsToSonatypeRepository closeAndReleaseSonatypeStagingRepository

echo "👍 Published $previous_version"

# Retrieve the last commit hash
last_commit=$(git rev-parse HEAD)

# Tag the last commit with the last version
git tag "$previous_version" $last_commit

# Find and update the version in .md files
find "$root_dir" -type f -name "*.md" -exec sed -i '' "s/implementation(\"com\.composables:core:[^\"]*\")/implementation(\"com.composables:core:$previous_version\")/g" {} +

# Update the version in libs.versions.toml
sed -i '' "s/^unstyled = \".*\"/unstyled = \"$wip_version\"/" "$root_dir/gradle/libs.versions.toml"

# Add all changes to git
git add .

# Commit with the provided version in the message
git commit -m "Prepare version $wip_version"

# If we're not on main, merge changes to main first
if [ "$current_branch" != "main" ]; then
    echo "Merging $current_branch to main..."
    git checkout main
    git merge "$current_branch"
fi

git push origin
git push origin $previous_version

echo "💯 All done. Working version is $previous_version"

# Get the previous tag to determine commit range
previous_tag=$(git tag --sort=-version:refname | grep -v "$previous_version" | head -1)

if [ -z "$previous_tag" ]; then
    echo "No previous tag found. Using all commits."
    commit_range=""
else
    echo "Getting commits between $previous_tag and $previous_version..."
    commit_range="$previous_tag..$previous_version"
fi

# Extract commit messages for the release
if [ -z "$commit_range" ]; then
    # If no previous tag, get all commits
    commits=$(git log --oneline --no-merges)
else
    # Get commits between previous tag and current version
    commits=$(git log --oneline --no-merges "$commit_range")
fi

# Format commits for GitHub release
if [ -n "$commits" ]; then
    # Convert to markdown list format and extract issue URLs
    formatted_commits=$(echo "$commits" | while IFS= read -r commit; do
        # Extract commit hash and message
        hash=$(echo "$commit" | cut -d' ' -f1)
        message=$(echo "$commit" | cut -d' ' -f2-)
        
        # Extract GitHub issue URLs from the message
        issue_urls=$(echo "$message" | grep -o 'https://github\.com/composablehorizons/compose-unstyled/issues/[0-9]*' || true)
        
        if [ -n "$issue_urls" ]; then
            # Format with issue links
            echo "- $hash $message $issue_urls"
        else
            # Format without issue links
            echo "- $hash $message"
        fi
    done)
    
    # Create release text
    release_text="## What's Changed

$formatted_commits

## Installation

\`\`\`kotlin
implementation(\"com.composables:core:$previous_version\")
\`\`\`"
    
    # URL encode the release text for GitHub
    encoded_text=$(echo "$release_text" | python3 -c "
import sys
import urllib.parse
text = sys.stdin.read()
encoded = urllib.parse.quote(text, safe='')
print(encoded)
")
    
    echo "TODO: Open a Github version for $previous_version"
    open "https://github.com/composablehorizons/compose-unstyled/releases/new?tag=$previous_version&title=$previous_version&body=$encoded_text"
else
    echo "No commits found for this release."
    echo "TODO: Open a Github version for $previous_version"
    open "https://github.com/composablehorizons/compose-unstyled/releases/new?tag=$previous_version&title=$previous_version"
fi
