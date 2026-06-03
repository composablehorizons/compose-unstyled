---
name: cut-release
description: Prepare and cut a Compose Unstyled release from this repository by finalizing CHANGELOG.md, bumping gradle/libs.versions.toml, opening a release PR, tagging the merged commit, pushing the tag, and confirming the GitHub Release workflow/draft release. Use when asked to prepare, cut, ship, publish, or tag a release for Compose Unstyled.
---

# Cut Release

Use this skill only in `/Users/alexstyl/projects/composeunstyled.com`.

## Source of truth

- Release automation: `.github/workflows/release.yml`
- Version: `gradle/libs.versions.toml`, key `unstyled`
- Release notes: `CHANGELOG.md`
- Tags use plain SemVer, for example `2.0.1`, not `v2.0.1`.
- Pushing a tag triggers the `Release` workflow, which runs Spotless, JVM tests, Android connected tests, publishes to Maven Central, and creates a draft GitHub Release from the matching changelog section.

## Before Cutting

1. Sync `main`:
   ```bash
   git switch main
   git pull --ff-only origin main
   git fetch --tags origin
   ```
2. Confirm all release PRs are merged.
3. Inspect the current version and latest tag:
   ```bash
   rg '^unstyled = ' gradle/libs.versions.toml
   git tag --sort=-creatordate | head
   ```
4. Decide the next SemVer version from the user request and changelog contents. Ask if ambiguous.

## Prepare Release PR

1. Move `CHANGELOG.md` Unreleased entries into a dated heading:
   ```markdown
   ## [x.y.z] - YYYY-MM-DD
   ```
   Keep `## [Unreleased]` first. If no future entries exist, leave it empty.
2. Update `gradle/libs.versions.toml`:
   ```toml
   unstyled = "x.y.z"
   ```
3. Validate the changelog section exactly matches the release workflow parser:
   - The heading must be `## [x.y.z] - YYYY-MM-DD`.
   - The tag must be `x.y.z`.
4. Before opening the release PR, run the local checks that correspond to the release changes:
   - Always run:
     ```bash
     ./gradlew --console=plain jvmTest
     ./gradlew --console=plain spotlessCheck
     ```
   - If the release includes Kotlin, Compose, Gradle, test infrastructure, platform, or dependency changes, also run the relevant Android connected tests locally. Launch the project test emulator first:
     ```bash
     bash scripts/createAndroidEmulator
     bash scripts/launchAndroidEmulator
     adb devices
     adb shell getprop sys.boot_completed
     ./gradlew --console=plain :composeunstyled-<module>:connectedDebugAndroidTest
     ```
   - For broad or uncertain release contents, run connected tests for every affected `composeunstyled-*` module. Do not rely on the tag workflow as the first Android signal.
   - Fix all failures before pushing the release branch.
5. Commit on a release branch:
   ```bash
   git switch -c release/x.y.z
   git add CHANGELOG.md gradle/libs.versions.toml
   git commit -m "Release x.y.z"
   ```
6. Push the branch and open a PR into `main`:
   ```bash
   git push -u origin release/x.y.z
   gh pr create --base main --head release/x.y.z --title "Release x.y.z"
   ```
   Before opening the PR body, read `.github/pull_request_template.md` and follow its structure.
7. If the user asks to launch the PR, open the PR URL in the browser.

## Tag And Push

1. After the release PR is merged, sync `main`:
   ```bash
   git switch main
   git pull --ff-only origin main
   git fetch --tags origin
   ```
2. Re-run local pre-tag verification on the exact merge commit before creating the tag:
   - Always run:
     ```bash
     ./gradlew --console=plain jvmTest
     ./gradlew --console=plain spotlessCheck
     ```
   - Launch the Android test emulator and run all Android connected tests that are relevant to the release contents:
     ```bash
     bash scripts/createAndroidEmulator
     bash scripts/launchAndroidEmulator
     adb devices
     adb shell getprop sys.boot_completed
     ./gradlew --console=plain :composeunstyled-<module>:connectedDebugAndroidTest
     ```
   - If the relevant module set is unclear, inspect the changed modules since the previous tag and err on the side of running more connected tests locally.
   - Do not tag until these checks pass.
3. Create the tag on the merge commit:
   ```bash
   git tag x.y.z
   ```
4. Push the tag:
   ```bash
   git push origin x.y.z
   ```
5. If the workflow must be rerun manually, use the `Release` workflow with input `tag = x.y.z`.

## Do Not

- Do not invent release dates for future releases; use the current local date when cutting.
- Do not use `v`-prefixed tags unless the repository changes its tag convention.
- Do not push a release tag before `CHANGELOG.md` has a matching version heading.
- Do not push directly to `main`; use a release PR.
