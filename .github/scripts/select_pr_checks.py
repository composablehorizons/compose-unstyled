import os
import subprocess


def needs_library_checks(paths, branch=""):
    if branch.startswith("changeset-release/"):
        return True

    for path in paths:
        if path.startswith(("website/", "docs/")) or path in {
            "scripts/generate-compose-unstyled-api.js",
            "scripts/generate-demo-registry.js",
            ".github/workflows/deploy-website.yml",
            ".github/workflows/redeploy-docs.yml",
            ".github/workflows/docs.yml",
        }:
            continue
        if "/" not in path and path.endswith(".md"):
            continue

        # Keep existing library checks for all paths not explicitly exempted.
        return True
    return False


if __name__ == "__main__":
    base = os.environ["BASE_SHA"]
    head = os.environ["HEAD_SHA"]
    merge_base = subprocess.check_output(
        ["git", "merge-base", base, head], text=True
    ).strip()
    # Disable rename detection so moving a file out of library code checks both paths.
    changed = subprocess.check_output(
        ["git", "diff", "--no-renames", "--name-only", "-z", merge_base, head]
    ).decode().split("\0")
    library = needs_library_checks(filter(None, changed), os.environ.get("HEAD_REF", ""))
    print(f"library={str(library).lower()}")
