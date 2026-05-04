## PR instructions

Before pushing changes that touch Kotlin (`.kt`) files, you must run `jvmTest` and `spotlessCheck`, and fix all reported issues before pushing.

## Development workflow

- When touching code for any playground or demo module, do not run the app. Instead, use `./gradlew :<module>:hotReloadDesktopMain` to reload the app.
- When asked to run the demo app, use `./gradlew :<module>:hotRunDesktop` instead.

## Working with Compose Foundation

- Do not use experimental Compose Foundation APIs. They can be removed or changed in future Compose versions and cause consumers to crash.
- When an unstable Foundation API is required, vendor the source code needed by this project until the final API becomes stable.

## Git commit etiquette

- Commit messages should describe how the codebase changed, not just the bug that was fixed.
- When the reason for the change is not obvious from the title, include a commit body that explains why the change was required.
