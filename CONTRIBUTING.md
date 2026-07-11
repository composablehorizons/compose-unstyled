# Contributing

Thanks for helping improve Compose Unstyled. We welcome focused contributions that keep the
library's unstyled promise intact: components should expose behavior, state, semantics, and slots
without taking visual or design-system decisions for users.

## Before opening a PR

For small bug fixes, focused tests, and documentation fixes, feel free to open a PR directly.

For new primitives, public APIs, or behavior changes, open an issue before starting implementation
so we can agree on the scope and API first. Include:

- the user problem the change solves
- the proposed public API shape
- the relevant accessibility pattern or semantics
- the expected tests
- the non-goals, especially any visuals or design-system choices that should stay out of scope

Good contribution candidates are marked with `help wanted`.

Compose Unstyled contributions should:

- avoid visual and design-system opinions
- expose behavior through state and slots
- follow relevant accessibility patterns
- include focused tests for shared behavior when behavior changes

## Working with Android

The recommended way to build and develop on Unstyled is using the JVM target. However, for your PRs
to be merged, the related Android connected tests must pass.

We use the following emulator spec on our CI, and it is recommended that you create the same setup
locally.

This will guarantee that the local tests will behave as close as possible to the environment running
on the CI.

Use the `scripts/createAndroidEmulator` script to create the emulator.

### Emulator Spec

- AVD name: `Unstyled_Tests`
- API level: `23`
- Target: `default`
- System image:
  - CI and non-Apple-Silicon machines: `system-images;android-23;default;x86_64`
  - Apple Silicon Macs: `system-images;android-23;default;arm64-v8a`
- CPU architecture:
  - CI and non-Apple-Silicon machines: `x86_64`
  - Apple Silicon Macs: `arm64-v8a`
- Hardware profile: none. Do not pass `--device` to `avdmanager`.
- Display: `320x640`
- Density: `160 dpi`
- CPU cores: `2`
- Disk size: `2048M`
- Runtime options:
  ```bash
  -no-snapshot-save -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none
  ```
- Test execution disables system animations before running connected tests.
