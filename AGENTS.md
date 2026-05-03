## PR instructions

Before pushing changes that touch Kotlin (`.kt`) files, you must run `jvmTest` and `spotlessCheck`, and fix all reported issues before pushing.

## Development workflow

- When touching code for any playground or demo module, do not run the app. Instead, use `./gradlew :<module>:hotReloadDesktopMain` to reload the app.
- When asked to run the demo app, use `./gradlew :<module>:hotRunDesktop` instead.
