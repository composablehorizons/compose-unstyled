# Compose Unstyled Docs

This directory is the source of truth for the current Compose Unstyled documentation.

- `docs.yml` defines the docs navigation.
- `pages/` contains the markdown docs pages.
- `assets/` contains images, videos, and other static files referenced by the docs.
- `<UnstyledDemo id="...">` embeds a destination from the bundled Compose Unstyled demo app and links it to its Kotlin source.
- `<ApiReference id="...">` expands into generated API tables during bundling.

Run the bundle task from the repository root:

```sh
./gradlew bundleComposeUnstyledDocs
```

The bundle is written to `build/docs-bundle/compose-unstyled`. It contains the generated markdown pages, demo source snippets, static docs assets, a manifest, and the built demo web app.
