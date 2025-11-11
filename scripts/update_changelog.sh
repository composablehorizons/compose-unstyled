#!/bin/bash
set -e  # Exit immediately if a command exits with a non-zero status

# Usage: ./update_changelog.sh <version>
# Example: ./update_changelog.sh 1.48.4

if [ -z "$1" ]; then
  echo "Error: Version number required"
  echo "Usage: $0 <version>"
  echo "Example: $0 1.48.4"
  exit 1
fi

VERSION="$1"
DATE=$(date +%Y-%m-%d)
CHANGELOG_FILE="CHANGELOG.md"

# Check if CHANGELOG.md exists
if [ ! -f "$CHANGELOG_FILE" ]; then
  echo "Error: $CHANGELOG_FILE not found"
  exit 1
fi

# Check if the version already exists in the changelog
if grep -q "## \[$VERSION\]" "$CHANGELOG_FILE"; then
  echo "Version $VERSION already exists in $CHANGELOG_FILE"
  exit 0
fi

# Create a temporary file
TEMP_FILE=$(mktemp)

# Read the file line by line and insert the new version after Unreleased
found_unreleased=false
while IFS= read -r line; do
  echo "$line" >> "$TEMP_FILE"
  if [[ "$line" == "## [Unreleased]" ]] && [ "$found_unreleased" = false ]; then
    echo "" >> "$TEMP_FILE"
    echo "## [$VERSION] - $DATE" >> "$TEMP_FILE"
    found_unreleased=true
  fi
done < "$CHANGELOG_FILE"

# Replace the original file
mv "$TEMP_FILE" "$CHANGELOG_FILE"

echo "✓ Updated $CHANGELOG_FILE with version $VERSION dated $DATE"