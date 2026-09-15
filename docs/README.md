# Compose Unstyled Docs

This directory is the source of truth for the current Compose Unstyled documentation.

- `docs.yml` defines the docs navigation.
- `pages/` contains the markdown docs pages.
- `assets/` contains images, videos, and other static files referenced by the docs.
- `<UnstyledDemo id="...">` embeds a destination from the Compose Unstyled demo app and links it to its Kotlin source.
- `<ApiReference id="...">` expands into generated API tables during website preparation.

Demo source attachments are generated from `@UnstyledDemo` declarations in the demo source.

Run the documentation site locally:

```sh
cd website
bun run dev
```

The website preparation scripts generate markdown pages, demo source snippets, static docs assets, and the demo web app.
