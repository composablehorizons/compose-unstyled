# Contributing

## Changesets

Compose Unstyled uses [Changesets](https://github.com/changesets/changesets) to collect release
notes from pull requests.

Add a changeset when your PR changes published Compose Unstyled behavior, APIs, dependencies, or
developer-facing release output:

```bash
npm run changeset
```

PRs that only change tests, demos, documentation, internal tooling, or local agent instructions do
not need a changeset.

See [RELEASING.md](RELEASING.md) for the full release flow.

## Working with Android

The recommended way to build and develop on Unstyled is using the JVM target. However, for your PRs
to be merged, the related Android connected tests must pass.

We use the emulator spec in `android-emulator.properties` for Android tests, and it is recommended
that you create the same setup locally.

This will guarantee that the local tests will behave as close as possible to the environment running
on the CI.

Use the `scripts/createAndroidEmulator` script to create the emulator from that profile.

### Emulator Spec

The canonical emulator spec lives in `android-emulator.properties`.
