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

This consumes the pending `.changeset/*.md` files, bumps `package.json`, and syncs
`gradle/libs.versions.toml`.

## Publishing

After the version PR is merged, the `Version Packages` workflow sees that no pending changesets
remain. It creates the current version tag, pushes it, and dispatches `.github/workflows/release.yml`
with that tag.

The `Release` workflow runs the release checks, publishes artifacts to Maven Central, and creates a
draft GitHub Release with GitHub-generated release notes.
