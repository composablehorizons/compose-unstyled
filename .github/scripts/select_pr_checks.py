import os
import subprocess

RELEASE_BRANCH_PREFIX = "changeset-release/"
RELEASE_PATHS = {
    "CHANGELOG.md",
    "gradle/libs.versions.toml",
    "package.json",
    "package-lock.json",
}
SCREENSHOT_PATHS = {
    "gradle/libs.versions.toml",
    "gradle.properties",
    "settings.gradle.kts",
    "build.gradle.kts",
    ".github/workflows/ci.yml",
    ".github/workflows/nightly-checks.yml",
    ".github/workflows/update-screenshot-baselines.yml",
    ".github/workflows/visual-regressions.yml",
}


def is_release_pr(branch):
    return branch.startswith(RELEASE_BRANCH_PREFIX)


def has_valid_release_scope(paths):
    return all(path in RELEASE_PATHS or path.startswith(".changeset/") for path in paths)


def needs_library_checks(paths, branch=""):
    if is_release_pr(branch):
        return False

    for path in paths:
        if path.startswith(("website/", "docs/")) or path in {
            "scripts/generate-compose-unstyled-api.js",
            "scripts/generate-demo-registry.js",
            ".github/workflows/deploy-website.yml",
            ".github/workflows/redeploy-docs.yml",
            ".github/workflows/docs.yml",
            ".github/workflows/version-packages.yml",
        }:
            continue
        if "/" not in path and path.endswith(".md"):
            continue

        # Keep existing library checks for all paths not explicitly exempted.
        return True
    return False


def needs_screenshot_checks(paths, branch=""):
    if is_release_pr(branch):
        return False

    for path in paths:
        if path in SCREENSHOT_PATHS or path.startswith(("gradle/wrapper/", "demo/", "visual-regressions/")):
            return True
        if "/src/commonMain/" in path or "/src/jvmMain/" in path or "/src/androidMain/" in path:
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
    changed = list(filter(None, changed))
    branch = os.environ.get("HEAD_REF", "")
    release = is_release_pr(branch)
    library = needs_library_checks(changed, branch)
    screenshots = needs_screenshot_checks(changed, branch)
    print(f"library={str(library).lower()}")
    print(f"screenshots={str(screenshots).lower()}")
    print(f"release={str(release).lower()}")
    print(f"release_valid={str(not release or has_valid_release_scope(changed)).lower()}")
