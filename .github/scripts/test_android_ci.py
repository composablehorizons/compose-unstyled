import subprocess
import tempfile
import unittest
from pathlib import Path
from unittest.mock import Mock, patch
from types import SimpleNamespace

import android_ci


class AndroidCiTests(unittest.TestCase):
    @patch.object(android_ci.time, "sleep")
    def test_sdk_retry_clears_partial_downloads_but_keeps_installed_packages(self, sleep):
        with tempfile.TemporaryDirectory() as directory:
            sdk = Path(directory) / "sdk"
            cache = Path(directory) / "cache"
            (sdk / ".temp").mkdir(parents=True)
            cache.mkdir()
            (sdk / "platforms/android-23").mkdir(parents=True)
            (sdk / "platforms/android-23/package.xml").touch()
            (sdk / "system-images/android-23/default/x86_64").mkdir(parents=True)
            command = Mock(side_effect=[subprocess.CalledProcessError(1, "sdkmanager"), None])
            android_ci.install_sdk(sdk, cache, ["platforms;android-23", "system-images;android-23;default;x86_64"], command)
            self.assertEqual(command.call_count, 2)
            self.assertFalse((sdk / ".temp").exists())
            self.assertFalse(cache.exists())
            self.assertTrue((sdk / "platforms/android-23/package.xml").exists())
            self.assertFalse((sdk / "system-images/android-23/default/x86_64").exists())

    @patch.object(android_ci.time, "sleep")
    def test_sdk_install_stops_after_three_failures(self, sleep):
        with tempfile.TemporaryDirectory() as directory:
            command = Mock(side_effect=subprocess.CalledProcessError(1, "sdkmanager"))
            with self.assertRaises(subprocess.CalledProcessError):
                android_ci.install_sdk(Path(directory), Path(directory) / "cache", [], command)
            self.assertEqual(command.call_count, 3)

    @patch.object(android_ci.time, "sleep")
    def test_dependency_download_timeout_is_retried(self, sleep):
        command = Mock(side_effect=[
            subprocess.CalledProcessError(1, "gradle", output="Could not download ui-test.aar\nRead timed out"), None])
        android_ci.prepare_tests(["./gradlew", ":example:connectedDebugAndroidTest"], command)
        self.assertEqual(command.call_count, 2)
        self.assertIn(":example:assembleDebugAndroidTest", command.call_args.args[0])
        self.assertNotIn(":example:connectedDebugAndroidTest", command.call_args.args[0])

    def test_compile_failure_is_not_retried(self):
        command = Mock(side_effect=subprocess.CalledProcessError(1, "gradle", output="Unresolved reference: broken"))
        with self.assertRaises(subprocess.CalledProcessError):
            android_ci.prepare_tests(["./gradlew", ":example:connectedDebugAndroidTest"], command)
        self.assertEqual(command.call_count, 1)

    def test_preparation_rejects_unrecognized_test_commands(self):
        command = Mock()
        with self.assertRaises(ValueError):
            android_ci.prepare_tests(["./gradlew", "jvmTest"], command)
        command.assert_not_called()

    def emulator_fixture(self, directory):
        root = Path(directory)
        avd = root / ".android/avd/test.avd"
        avd.mkdir(parents=True)
        (avd / "config.ini").write_text("hw.lcd.width=1080\nhw.lcd.height=1920\n")
        profile = {"avdName": "test", "apiLevel": "23", "target": "default", "arch.default": "x86_64",
                   "hardwareProfile": "none", "displayWidth": "320", "displayHeight": "640",
                   "displayDensity": "160", "diskSize": "2048M", "cpuCores": "2", "launchFlags": "-no-window"}
        return root, profile

    def test_emulator_boot_failure_recreates_device_and_stops_after_three_attempts(self):
        with tempfile.TemporaryDirectory() as directory:
            root, profile = self.emulator_fixture(directory)
            process = Mock()
            process.poll.return_value = 1
            with patch.object(android_ci.Path, "home", return_value=root), \
                 patch.object(android_ci, "run") as command, \
                 patch.object(android_ci.subprocess, "Popen", return_value=process) as launch:
                with self.assertRaisesRegex(RuntimeError, "setup failed after 3 attempts"):
                    android_ci.start_emulator(root / "sdk", profile, root / "logs")
            self.assertEqual(launch.call_count, 3)
            self.assertEqual(process.wait.call_count, 3)
            self.assertEqual(sum("avdmanager" in call.args[0][0] for call in command.call_args_list), 3)

    def test_successful_boot_disables_animations_and_leaves_emulator_running(self):
        with tempfile.TemporaryDirectory() as directory:
            root, profile = self.emulator_fixture(directory)
            process = Mock()
            process.poll.return_value = None
            with patch.object(android_ci.Path, "home", return_value=root), \
                 patch.object(android_ci, "run") as command, \
                 patch.object(android_ci.subprocess, "Popen", return_value=process) as launch, \
                 patch.object(android_ci.subprocess, "run", return_value=SimpleNamespace(returncode=0, stdout="1\n")):
                android_ci.start_emulator(root / "sdk", profile, root / "logs")
            process.kill.assert_not_called()
            self.assertEqual(sum("settings" in call.args[0] for call in command.call_args_list), 3)
            create = next(call.args[0] for call in command.call_args_list if "avdmanager" in call.args[0][0])
            self.assertIn("--path", create)
            self.assertEqual(create[create.index("--path") + 1], str(root / ".android/avd/test.avd"))
            create_call = next(call for call in command.call_args_list if "avdmanager" in call.args[0][0])
            self.assertEqual(create_call.kwargs["env"]["ANDROID_AVD_HOME"], str(root / ".android/avd"))
            self.assertEqual(launch.call_args.kwargs["env"]["ANDROID_AVD_HOME"], str(root / ".android/avd"))
            config = (root / ".android/avd/test.avd/config.ini").read_text()
            self.assertEqual(config.count("hw.lcd.width="), 1)
            self.assertIn("hw.lcd.width=320", config)


if __name__ == "__main__":
    unittest.main()
