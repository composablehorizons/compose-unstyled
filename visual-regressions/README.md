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

Pull requests that can affect rendered output run this suite automatically on macOS 15. The
workflow does not run on release-only pull requests; the tagged release workflow runs the full
suite before publishing.

Do not update checked-in baselines locally. After an intentional visual change, run the
`Update Screenshot Baselines` GitHub Actions workflow for the branch that needs the update. It
regenerates baselines on macOS 15 and opens a dedicated pull request.

Normal screenshot test tasks never update baselines; they only write failure artifacts under
`build/`. On CI failure, download `screenshot-failure-artifacts` for the actual and diff images.
The failure also reports the changed-pixel count, maximum channel delta, and mean channel delta.

The suite permits at most 20 changed pixels for tiny renderer drift. Do not increase that limit to
accept a mismatch: inspect the artifacts and update the baseline only when the rendered result is
intentional.

## Adding A Case

Add focused fixtures in the relevant `commonMain` regression file, then register the screenshot in that component's screenshot list. Keep the screenshot name stable because it maps to the baseline PNG filename.

Prefer one component test file that parameterizes over that component's screenshot list. This keeps failures readable without creating one test function per screenshot.
