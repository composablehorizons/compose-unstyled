#!/bin/bash
set -e

echo "Fetching latest tags..."
git fetch --tags

# Get the latest release tag
latest_tag=$(git tag --sort=-version:refname | head -1)

if [ -z "$latest_tag" ]; then
    echo "No tags found. Cannot create hotfix."
    exit 1
fi

echo "Latest release: $latest_tag"

# Parse version and increment patch number
IFS='.' read -r major minor patch <<< "$latest_tag"
hotfix_version="$major.$minor.$((patch + 1))"

echo "Hotfix version will be: $hotfix_version"

# Confirm with user
read -p "Create hotfix branch 'hotfix_$hotfix_version' from tag '$latest_tag'? (y/n): " confirm
if [[ "$confirm" != "y" ]]; then
    echo "Aborted."
    exit 1
fi

# Create hotfix branch from the latest tag
branch_name="hotfix_$hotfix_version"
git checkout -b "$branch_name" "$latest_tag"

echo "Created and switched to branch: $branch_name"

# Define the root directory as the current directory
root_dir=$(pwd)

# Update the version in libs.versions.toml
sed -i '' "s/^unstyled = \".*\"/unstyled = \"$hotfix_version\"/" "$root_dir/gradle/libs.versions.toml"

echo "Updated libs.versions.toml to version $hotfix_version"

# Add and commit the version change
git add "$root_dir/gradle/libs.versions.toml"
git commit -m "Prepare version $hotfix_version"

echo "✅ Hotfix branch '$branch_name' created successfully!"
echo "📝 Version updated to: $hotfix_version"
echo ""
echo "Next steps:"
echo "  1. Make your hotfix changes"
echo "  2. Test thoroughly"
echo "  3. Commit your fixes"
echo "  4. Use the release script to publish the hotfix"