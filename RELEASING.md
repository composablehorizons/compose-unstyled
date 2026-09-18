# Releasing

Compose Unstyled uses Changesets to prepare release pull requests and the existing `Release`
workflow to publish artifacts.

## Changesets

Every change that should appear in release notes or publish a new version needs a changeset:

```bash
npm run changeset
```

Each changeset should describe the user-visible behavior change. Use `patch`, `minor`, or `major`
according to SemVer. Pull requests without changesets do not contribute release notes or version
bumps.

## Version PR

When changesets land on `main`, the `Version Packages` workflow opens or updates the release PR.
That PR runs:

```bash
npm run changeset:version
```

This consumes the pending `.changeset/*.md` files, writes their descriptions to `CHANGELOG.md`,
bumps `package.json`, and synchronizes `package-lock.json` and `gradle/libs.versions.toml`.

The version PR validates only its release metadata and scope. It intentionally does not run the
library, Android, or screenshot test matrix; it exists solely to prepare the release.

## Publishing

After the version PR is merged, the `Version Packages` workflow sees that no pending changesets
remain. It creates the current version tag, pushes it, and dispatches `.github/workflows/release.yml`
with that tag.

The `Release` workflow verifies that the tag and release metadata agree, runs the full release
checks—including screenshot tests—publishes artifacts to Maven Central, and creates a draft
GitHub Release from the matching `CHANGELOG.md` section.
