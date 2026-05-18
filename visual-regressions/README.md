# Visual Regressions

This module contains screenshot coverage for Compose Unstyled behavior that is easy to break visually.

Use it for focused regression cases where the screenshot should make one layout or rendering contract obvious. These tests should be small, stable, and diagnostic. Avoid making them look like product demos; prefer simple shapes, clear bounds, and minimal styling that helps explain the behavior under test.

## Demos And Regressions

The demo app and this module serve different purposes:

- Demos are for trying the APIs by hand and for documentation. They should show realistic usage and help contributors understand how a primitive is meant to be used.
- Visual regressions are for screenshot testing. They should isolate bugs, edge cases, and layout states that can silently regress.

When contributing a new component, behavior, or bug fix, consider both sides. Add or update a demo when the change is useful for API exploration or docs, and add a visual regression when the behavior needs screenshot coverage.

## Running Tests

Compare the checked-in baselines:

```sh
./gradlew :visual-regressions:jvmScreenshotTest
```

Update the checked-in baselines after intentionally changing screenshots:

```sh
./gradlew :visual-regressions:takeScreenshots
```

Do not update screenshot baselines from normal test tasks. Screenshot tests should only write failure artifacts under `build/`.

## Adding A Case

Add focused fixtures in the relevant `commonMain` regression file, then register the screenshot in that component's screenshot list. Keep the screenshot name stable because it maps to the baseline PNG filename.

Prefer one component test file that parameterizes over that component's screenshot list. This keeps failures readable without creating one test function per screenshot.
