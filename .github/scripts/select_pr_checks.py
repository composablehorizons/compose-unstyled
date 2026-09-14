import os
import subprocess


def select_checks(paths, branch=""):
    if branch.startswith("changeset-release/"):
        return True, True

    library = False
    website = False
    for path in paths:
        if path.startswith(("website/", "docs/")) or path in {
            "scripts/generate-compose-unstyled-api.mjs",
            ".github/workflows/deploy-website.yml",
            ".github/workflows/ci.yml",
            ".github/scripts/select_pr_checks.py",
            ".github/scripts/test_select_pr_checks.py",
        }:
            website = True
            continue
        if "/" not in path and path.endswith(".md"):
            continue

        # Keep existing library checks for all paths not explicitly exempted.
        library = True
        if (
            path.startswith(("demo/", "gradle/", "buildSrc/", "build-logic/"))
            or path.endswith((".gradle.kts", ".gradle"))
            or path in {"gradlew", "gradlew.bat", "gradle.properties"}
            or "/src/commonMain/" in path
            or "/src/wasmJsMain/" in path
        ):
            website = True
    return library, website


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
    library, website = select_checks(filter(None, changed), os.environ.get("HEAD_REF", ""))
    print(f"library={str(library).lower()}")
    print(f"website={str(website).lower()}")
