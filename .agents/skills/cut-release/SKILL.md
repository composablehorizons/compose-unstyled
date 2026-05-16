---
name: cut-release
description: Prepare and cut a Compose Unstyled release from this repository by finalizing CHANGELOG.md, bumping gradle/libs.versions.toml, committing the release, tagging it, pushing the tag, and confirming the GitHub Release workflow/draft release. Use when asked to prepare, cut, ship, publish, or tag a release for Compose Unstyled.
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

## Prepare Release Commit

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
4. Run required release checks before committing:
   ```bash
   ./gradlew jvmTest spotlessCheck
   ```
   If Kotlin or UI behavior changed as part of release prep, also run the touched behavior tests required by `AGENTS.md`.
5. Commit:
   ```bash
   git add CHANGELOG.md gradle/libs.versions.toml
   git commit -m "Release x.y.z"
   ```

## Tag And Push

1. Create the tag on the release commit:
   ```bash
   git tag x.y.z
   ```
2. Push the branch first, then the tag:
   ```bash
   git push origin main
   git push origin x.y.z
   ```
3. If the workflow must be rerun manually, use the `Release` workflow with input `tag = x.y.z`.

## Do Not

- Do not invent release dates for future releases; use the current local date when cutting.
- Do not use `v`-prefixed tags unless the repository changes its tag convention.
- Do not push a release tag before `CHANGELOG.md` has a matching version heading.
- Do not skip `jvmTest` and `spotlessCheck` for a release commit.
