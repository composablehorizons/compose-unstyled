# Compose Unstyled Docs

This directory is the source of truth for the current Compose Unstyled documentation.

- `docs.yml` defines the docs navigation.
- `pages/` contains the markdown docs pages.
- `assets/` contains images, videos, and other static files referenced by the docs.
- `<UnstyledDemo id="...">` embeds a destination from the Compose Unstyled demo app and links it to its Kotlin source.
- `<ApiReference declaration="...">` expands into generated API tables during website preparation.

Demo source attachments are generated from `@UnstyledDemo` declarations in the demo source.

## API references

API reference tables are generated from public Kotlin declarations. Add one marker for each
fully-qualified declaration in the order it should appear:

```md
## API Reference

<ApiReference declaration="com.composeunstyled.UnstyledExample" />
<ApiReference declaration="com.composeunstyled.ExampleScope.Item" />
```

The generator finds declarations in Compose Unstyled's public `commonMain` Kotlin sources. It
validates every marker, derives the signatures and parameters from Kotlin, and preserves the order
in the page.

Run the documentation site locally:

```sh
cd website
bun run dev
```

The website preparation scripts generate markdown pages, demo source snippets, static docs assets, and the demo web app.

## Search discovery

The website build generates `sitemap-index.xml` and `sitemap-0.xml` for the landing page and all
documentation routes. `robots.txt` and every page link to the sitemap index so crawlers can find it.
