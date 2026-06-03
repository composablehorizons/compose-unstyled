# Contributing

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
