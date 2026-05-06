## PR instructions

Before pushing changes that touch Kotlin (`.kt`) files, you must run `jvmTest` and `spotlessCheck`, and fix all reported issues before pushing.

## Testing

- Add tests for shared Kotlin/Compose behavior in `commonTest` so they run on both JVM and Android targets. Use platform-specific test source sets only when the behavior is platform-specific.
- Put touch-specific tests in the Android test source set. Put mouse-specific tests in the JVM test source set.

## Development workflow

- When touching code for any playground or demo module, do not run the app. Instead, use `./gradlew :<module>:hotReloadDesktopMain` to reload the app.
- When asked to run the demo app, use `./gradlew :<module>:hotRunDesktop` instead.

## Working with Compose Foundation

- Do not use experimental Compose Foundation APIs. They can be removed or changed in future Compose versions and cause consumers to crash.
- When an unstable Foundation API is required, vendor the source code needed by this project until the final API becomes stable.

## API design choices

- Compose Unstyled promises that it will never take or force design choices on the user. Keep APIs focused on behavior, state, semantics, and slots so design systems can provide their own visuals, icons, animation, layout, and styling.
- Modifier chains in public composables must always start with the `modifier` parameter before adding internal modifiers.

## Design with Compose

- Never use the old `shadow()` API as it uses Material Design's specs. Always prefer using `dropShadow()` (`androidx.compose.ui.draw.dropShadow`) instead.

## Git commit etiquette

- Commit messages should describe how the codebase changed, not just the bug that was fixed.
- When the reason for the change is not obvious from the title, include a commit body that explains why the change was required.
