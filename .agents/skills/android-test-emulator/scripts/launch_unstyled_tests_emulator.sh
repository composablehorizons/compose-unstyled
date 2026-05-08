#!/usr/bin/env bash
set -euo pipefail

AVD_NAME="Unstyled_Tests"
API_LEVEL="23"
ARCH="x86_64"
DISK_SIZE="2048M"
SYSTEM_IMAGE="system-images;android-${API_LEVEL};default;${ARCH}"

if [ -n "${ANDROID_HOME:-}" ]; then
  SDK_ROOT="$ANDROID_HOME"
elif [ -n "${ANDROID_SDK_ROOT:-}" ]; then
  SDK_ROOT="$ANDROID_SDK_ROOT"
else
  SDK_ROOT="$HOME/Library/Android/sdk"
fi

export ANDROID_HOME="$SDK_ROOT"
export ANDROID_SDK_ROOT="$SDK_ROOT"

SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
AVDMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/avdmanager"
EMULATOR="$SDK_ROOT/emulator/emulator"
ADB="$SDK_ROOT/platform-tools/adb"

for tool in "$SDKMANAGER" "$AVDMANAGER" "$EMULATOR" "$ADB"; do
  if [ ! -x "$tool" ]; then
    echo "Required Android SDK tool not found or not executable: $tool" >&2
    exit 1
  fi
done

if ! "$EMULATOR" -list-avds | grep -Fxq "$AVD_NAME"; then
  "$SDKMANAGER" "platform-tools" "emulator" "platforms;android-${API_LEVEL}" "$SYSTEM_IMAGE"
  printf "no\n" | "$AVDMANAGER" create avd \
    --force \
    --name "$AVD_NAME" \
    --package "$SYSTEM_IMAGE" \
    --device "pixel"

  CONFIG="$HOME/.android/avd/${AVD_NAME}.avd/config.ini"
  if [ -f "$CONFIG" ]; then
    if grep -q '^disk.dataPartition.size=' "$CONFIG"; then
      sed -i.bak "s/^disk.dataPartition.size=.*/disk.dataPartition.size=${DISK_SIZE}/" "$CONFIG"
    else
      printf "\ndisk.dataPartition.size=%s\n" "$DISK_SIZE" >> "$CONFIG"
    fi
  fi
fi

if "$ADB" devices | awk 'NR > 1 && $2 == "device" { found = 1 } END { exit found ? 0 : 1 }'; then
  boot_completed="$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [ "$boot_completed" = "1" ]; then
    echo "Android device already connected and booted."
    "$ADB" devices
    exit 0
  fi
fi

"$EMULATOR" \
  -avd "$AVD_NAME" \
  -no-snapshot-save \
  -no-window \
  -gpu swiftshader_indirect \
  -noaudio \
  -no-boot-anim \
  -camera-back none >/tmp/unstyled-tests-emulator.log 2>&1 &

"$ADB" wait-for-device

for _ in $(seq 1 180); do
  boot_completed="$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || true)"
  if [ "$boot_completed" = "1" ]; then
    "$ADB" shell settings put global window_animation_scale 0 >/dev/null 2>&1 || true
    "$ADB" shell settings put global transition_animation_scale 0 >/dev/null 2>&1 || true
    "$ADB" shell settings put global animator_duration_scale 0 >/dev/null 2>&1 || true
    echo "Android emulator $AVD_NAME is booted."
    "$ADB" devices
    exit 0
  fi
  sleep 1
done

echo "Timed out waiting for Android emulator $AVD_NAME to boot." >&2
echo "Emulator log: /tmp/unstyled-tests-emulator.log" >&2
exit 1
