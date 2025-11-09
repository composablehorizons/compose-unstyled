
echo "Running detekt..."
./gradlew detekt

echo "Compiling Kotlin..."
./gradlew :composeunstyled:assemble


echo "Starting Android emulator..."
emulator_name="Tests"

# Set Android SDK paths
ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
EMULATOR="$ANDROID_HOME/emulator/emulator"
ADB="$ANDROID_HOME/platform-tools/adb"

# Reduce emulator shutdown wait time from 20s to 2s
export ANDROID_EMULATOR_WAIT_TIME_BEFORE_KILL=2

echo "Launching emulator: $emulator_name"
"$EMULATOR" -avd "$emulator_name" -no-audio -no-boot-anim &
emulator_pid=$!

# Wait for emulator to boot
echo "Waiting for emulator to boot..."
"$ADB" wait-for-device
"$ADB" shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'
echo "Emulator booted successfully"

echo "Running tests..."
./gradlew jvmTest connectedAndroidTest

# Kill the emulator after tests
echo "Shutting down emulator..."
"$ADB" emu kill 2>/dev/null &
sleep 1
kill -9 $emulator_pid 2>/dev/null || true

echo "All tests passed..."