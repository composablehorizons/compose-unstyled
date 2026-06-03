---
name: android-test-emulator
description: Launch or create the local Android emulator required before running Android connected/instrumented tests in this repository. Use this whenever running Android tests is required and no suitable device is already connected.
---

# Android Test Emulator

Use this skill before running Android connected tests such as `connectedAndroidTest` or `connectedDebugAndroidTest`.

The required AVD name is `Unstyled_Tests`. If it does not exist, create it with:

- API level: `23`
- System image: `system-images;android-23;default;x86_64`, except Apple Silicon Macs use `system-images;android-23;default;arm64-v8a`
- Display: `320x640`
- Density: `160 dpi`
- CPU cores: `2`
- Disk size: `2048M`

## Workflow

1. Check connected devices:

   ```bash
   adb devices
   ```

2. If a device is already connected and booted, use it.

3. Create the project emulator if it does not exist:

   ```bash
   bash scripts/createAndroidEmulator
   ```

4. Launch the project emulator:

   ```bash
   bash scripts/launchAndroidEmulator
   ```

5. Wait until boot is complete before running Android tests. The script waits for:

   ```bash
   adb shell getprop sys.boot_completed
   ```

5. After the emulator is ready, run the requested Android test task.

## Notes

- Do not run Android tests until the emulator appears in `adb devices` as `device` and `sys.boot_completed` is `1`.
- If `emulator` is not on `PATH`, use `$ANDROID_HOME/emulator/emulator`, `$ANDROID_SDK_ROOT/emulator/emulator`, or `$HOME/Library/Android/sdk/emulator/emulator`.
- The script uses the same runtime options expected for test execution: `-no-snapshot-save -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -camera-back none`.
