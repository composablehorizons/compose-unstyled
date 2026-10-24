"""Prepare Android CI without retrying instrumentation tests."""

import os
from pathlib import Path
import re
import shlex
import shutil
import subprocess
import sys
import time


BUILD_TOOLS = "37.0.0"
NETWORK_ERROR = re.compile(
    r"Read timed out|Connect timed out|Connection reset|Temporary failure in name resolution|"
    r"UnknownHostException|Received status code (?:429|5\d\d)|Server returned HTTP response code: (?:429|5\d\d)",
    re.IGNORECASE)


def run(command, **kwargs):
    result = subprocess.run(command, text=True, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, **kwargs)
    print(result.stdout, end="", flush=True)
    result.check_returncode()
    return result


def install_sdk(sdk, download_cache, packages, command=run):
    manager = sdk / "cmdline-tools/latest/bin/sdkmanager"
    for attempt in range(3):
        try:
            command([str(manager), f"--sdk_root={sdk}", "--install", *packages], input="y\n" * 100, timeout=240)
            return
        except (subprocess.CalledProcessError, subprocess.TimeoutExpired):
            # sdkmanager otherwise reuses truncated archives and incomplete package directories.
            shutil.rmtree(sdk / ".temp", ignore_errors=True)
            shutil.rmtree(download_cache, ignore_errors=True)
            for package in packages:
                path = sdk.joinpath(*package.split(";"))
                if not (path / "package.xml").exists():
                    shutil.rmtree(path, ignore_errors=True)
            if attempt == 2:
                raise
            print(f"Retrying Android SDK installation ({attempt + 2}/3).", flush=True)
            time.sleep(10)


def prepare_tests(test_command, command=run):
    tasks = [arg for arg in test_command if arg.endswith(":connectedDebugAndroidTest")]
    if not tasks:
        raise ValueError("Expected a module's connectedDebugAndroidTest task")
    preparation = [arg.replace(":connectedDebugAndroidTest", ":assembleDebugAndroidTest")
                   if arg in tasks else arg for arg in test_command]
    preparation += [task.replace(":connectedDebugAndroidTest", ":assembleDebug") for task in tasks]
    for attempt in range(3):
        try:
            command(preparation)
            return
        except subprocess.CalledProcessError as error:
            if attempt == 2 or not NETWORK_ERROR.search(error.output or ""):
                raise
            print(f"Retrying Android test dependency preparation ({attempt + 2}/3).", flush=True)
            time.sleep(10)


def start_emulator(sdk, profile, log_directory):
    adb = str(sdk / "platform-tools/adb")
    avdmanager = str(sdk / "cmdline-tools/latest/bin/avdmanager")
    avd = profile["avdName"]
    image = f"system-images;android-{profile['apiLevel']};{profile['target']};{profile['arch.default']}"
    avd_directory = Path.home() / ".android/avd" / f"{avd}.avd"
    avd_directory.parent.mkdir(parents=True, exist_ok=True)
    environment = {**os.environ, "ANDROID_AVD_HOME": str(avd_directory.parent)}
    log_directory.mkdir(parents=True, exist_ok=True)
    run([adb, "start-server"])
    for attempt in range(3):
        command = [avdmanager, "create", "avd", "--force", "--name", avd,
                   "--package", image, "--path", str(avd_directory)]
        if profile["hardwareProfile"] != "none":
            command += ["--device", profile["hardwareProfile"]]
        run(command, input="no\n", timeout=60, env=environment)
        config_path = avd_directory / "config.ini"
        config = dict(line.split("=", 1) for line in config_path.read_text().splitlines() if "=" in line)
        config.update({"hw.lcd.width": profile["displayWidth"], "hw.lcd.height": profile["displayHeight"],
                       "hw.lcd.density": profile["displayDensity"], "disk.dataPartition.size": profile["diskSize"],
                       "hw.keyboard": "no"})
        config_path.write_text("".join(f"{key}={value}\n" for key, value in config.items()))
        log_path = log_directory / f"emulator-{attempt + 1}.log"
        with log_path.open("w") as log:
            process = subprocess.Popen(
                [str(sdk / "emulator/emulator"), "-avd", avd, "-port", "5554",
                 "-cores", profile["cpuCores"], "-no-snapshot",
                 *shlex.split(profile["launchFlags"])],
                stdout=log, stderr=subprocess.STDOUT, start_new_session=True, env=environment)
        try:
            deadline = time.monotonic() + 180
            while process.poll() is None and time.monotonic() < deadline:
                try:
                    boot = subprocess.run([adb, "-s", "emulator-5554", "shell", "getprop", "sys.boot_completed"],
                                          capture_output=True, text=True, timeout=10)
                    if boot.returncode == 0 and boot.stdout.strip() == "1":
                        for setting in ("window_animation_scale", "transition_animation_scale", "animator_duration_scale"):
                            run([adb, "-s", "emulator-5554", "shell", "settings", "put", "global", setting, "0"], timeout=15)
                        run([adb, "-s", "emulator-5554", "shell", "input", "keyevent", "82"], timeout=15)
                        return
                except subprocess.TimeoutExpired:
                    pass
                time.sleep(2)
        except BaseException:
            process.kill()
            process.wait()
            raise
        process.kill()
        process.wait()
        print(log_path.read_text(), flush=True)
        print(f"Android emulator boot attempt {attempt + 1}/3 failed.", flush=True)
    raise RuntimeError("Android emulator setup failed after 3 attempts; see emulator logs")


def main():
    action, profile_path, *arguments = sys.argv[1:]
    profile = dict(line.split("=", 1) for line in Path(profile_path).read_text().splitlines()
                   if line and not line.startswith("#"))
    sdk = Path(os.environ["ANDROID_HOME"])
    if action == "install":
        packages = [f"build-tools;{BUILD_TOOLS}", "platform-tools", "emulator",
                    f"platforms;android-{profile['apiLevel']}",
                    f"system-images;android-{profile['apiLevel']};{profile['target']};{profile['arch.default']}"]
        missing = [package for package in packages if not sdk.joinpath(*package.split(";"), "package.xml").exists()]
        if missing:
            install_sdk(sdk, Path.home() / ".android/cache", missing)
    elif action == "prepare-tests":
        prepare_tests(shlex.split(arguments[0]))
    elif action == "start":
        start_emulator(sdk, profile, Path("build/reports/android-emulator"))
    else:
        raise ValueError(f"Unknown action: {action}")


if __name__ == "__main__":
    main()
