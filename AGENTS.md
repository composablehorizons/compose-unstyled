## PR instructions

Before pushing changes that touch Kotlin (`.kt`) files, you must run `jvmTest` and `spotlessCheck`, and fix all reported issues before pushing.

Before opening or updating a pull request body, read `.github/pull_request_template.md` and follow its structure.

## Testing

- Add tests for shared Kotlin/Compose behavior in `commonTest` so they run on both JVM and Android targets. Use platform-specific test source sets only when the behavior is platform-specific.
- Put touch-specific tests in the Android test source set. Put mouse-specific tests in the JVM test source set.
- Use AssertK for test assertions instead of `kotlin.test` assertion helpers.
- When creating a library module with an Android target, add the Android instrumented test dependencies required for Compose UI tests: `libs.androidx.compose.test`, `libs.androidx.compose.test.manifest`, and `libs.androidx.espresso`.
- Screenshot tests must only compare screenshots and write failure artifacts under `build/`; they must not update checked-in baselines while running tests. When screenshot baselines need to be updated, use the dedicated screenshot task, such as `./gradlew :visual-regressions:takeScreenshots`.

## Recomposition testing

- When changing Compose behavior that can affect composition stability, add focused recomposition tests in `commonTest` using `composeunstyled-test`.
- Use `runComposeRecompositionTest {}` with `RecompositionCount(name)` placed at the specific subtree whose recomposition behavior matters. Assert with the project's normal assertion framework through `recompositionCount(name)` after calling `resetRecompositionCounts(...)`.
- Prefer testing user-relevant state transitions, such as opening or closing components, changing detents, invalidating measured state, and resizing dynamic containers.
- Record the current observed recomposition count unless the change is explicitly intended to improve it.
- Do not use broad parent-level counters when the behavior under test is scoped to a specific slot or content subtree.

## Development workflow

- When touching code for any playground or demo module, do not run the app. Instead, use `./gradlew :<module>:hotReloadJvmMain` to reload the app.
- When asked to run the demo app, use `./gradlew :<module>:hotRunJvm` instead.
- When running any desktop JVM app, start it in a background terminal/process so the main terminal remains available.

## Documentation

- Keep the `Primitives` and `Utilities` entries in `docs/docs.yml` in alphabetical order.

## Working with Compose Foundation

- Do not use experimental Compose Foundation APIs. They can be removed or changed in future Compose versions and cause consumers to crash.
- When an unstable Foundation API is required, vendor the source code needed by this project until the final API becomes stable.

## API design choices

- Compose Unstyled promises that it will never take or force design choices on the user. Keep APIs focused on behavior, state, semantics, and slots so design systems can provide their own visuals, icons, animation, layout, and styling.
- Every non-side-effect composable must expose a `modifier: Modifier = Modifier` parameter as the first optional parameter. That `modifier` must be applied to the top-level composable emitted by the function.
- Modifier chains in public composables must always start with the `modifier` parameter before adding internal modifiers.
- Component primitives must never affect parent layout by forcing their own size. Do not use `fillMaxSize()` or other fill modifiers internally; primitives must wrap their content unless the caller sizes them with a modifier.
- Use `buildModifier` for conditional modifiers instead of branching inside `Modifier.then(...)`.
- Scoped composables must be extension functions on the scope, not members of the scope interface or class.
- State object constructors should stay available to users so they can wrap primitive state in their own state objects. Keep implementation details hidden, but do not hide a state constructor only because `remember...State` exists.
- Public `interactionSource` parameters should be nullable and default to `null`. Resolve a non-null interaction source internally only when the primitive needs one for behavior or slot state.
- Public `indication` parameters should be nullable and default to `null`: `indication: Indication? = null`.

## Working in Unstyled

- Treat the rules in this file as product constraints, not preferences. If a change needs to break one of them, call that out explicitly before making the change.
- When adding or changing APIs, keep the unstyled promise intact: expose behavior and extension points, not visual opinions.
- Primitives must follow the WAI-ARIA Authoring Practices Guide patterns for semantics and accessibility: https://www.w3.org/WAI/ARIA/apg/patterns/. Verify this with semantic checks in unit tests.

## Design with Compose

- Never use the old `shadow()` API as it uses Material Design's specs. Always prefer using `dropShadow()` (`androidx.compose.ui.draw.dropShadow`) instead.

## Kotlin style

- Use `.not()` instead of the unary `!` operator for Boolean negation.

## Git commit etiquette

- Commit messages should describe the behavior that changed, not the implementation details that changed.
- Fix commits must start with `Fix` and be written in normal English, for example `Fix short bottom sheets entering percentage detents too slowly`.
- When the reason for the change is not obvious from the title, include a commit body that explains why the change was required.
