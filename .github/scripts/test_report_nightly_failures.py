import os
import unittest
from unittest.mock import patch
from types import SimpleNamespace

import report_nightly_failures as reporter


class NightlyFailureTests(unittest.TestCase):
    def setUp(self):
        self.job = {"id": 42, "name": "drawer / drawer", "conclusion": "failure",
                    "html_url": "https://example.com/job/42",
                    "steps": [{"name": "Android Tests", "conclusion": "failure"}]}

    def test_android_failure_uses_test_name_and_keeps_exception(self):
        result = reporter.failures(self.job,
            "2026-09-27T23:48:06.621Z com.composeunstyled.DrawerTest > shrinking[emulator-5554 - 6.0] \x1b[31mFAILED \x1b[0m\n"
            "2026-09-27T23:48:06.622Z java.lang.IllegalArgumentException: performMeasureAndLayout called during measure layout")
        self.assertEqual(result[0][1], "test:com.composeunstyled.DrawerTest.shrinking")
        self.assertIn("performMeasureAndLayout", result[0][2])

    def test_multiple_tests_get_distinct_identities(self):
        result = reporter.failures(self.job,
            "example.DrawerTest > growing FAILED\nexample.DrawerTest > shrinking FAILED")
        self.assertEqual(len(result), 2)
        self.assertNotEqual(result[0][1], result[1][1])

    def test_infrastructure_cause_wins_over_shell_exit(self):
        result = reporter.failures(self.job,
            "Warning: An error occurred while preparing SDK package Android Emulator: Error on ZipFile unknown archive.\n"
            "##[error]The process '/usr/bin/sh' failed with exit code 1")
        self.assertIn("Error on ZipFile", result[0][0])

    def test_download_version_does_not_change_identity(self):
        def identity(version):
            return reporter.failures(self.job,
                f"Could not GET 'https://repo.maven.apache.org/ui-test/{version}/test.aar'. Received status code 403 from server: Forbidden")[0][1]
        self.assertEqual(identity("1.0"), identity("2.0"))

    def test_missing_log_identifies_job_and_step(self):
        result = reporter.failures(self.job, "")
        self.assertIn("drawer / drawer:Android Tests", result[0][1])

    def run_report(self, issues):
        with patch.dict(os.environ, {"GITHUB_REPOSITORY": "owner/repo", "GITHUB_RUN_ID": "100"}), \
             patch.object(reporter, "pages", side_effect=[
                 [{"jobs": [self.job]}], [issues], [[{"name": reporter.LABEL}]]]), \
             patch.object(reporter.subprocess, "run", return_value=SimpleNamespace(
                 returncode=0, stdout="example.Test > fails FAILED\nAssertionError: wrong size")), \
             patch.object(reporter, "api") as api:
            reporter.main()
            return api.call_args_list

    def test_new_failure_creates_specific_issue(self):
        calls = self.run_report([])
        self.assertEqual(len(calls), 1)
        self.assertEqual(calls[0].args[0], "repos/owner/repo/issues")
        self.assertEqual(calls[0].kwargs["title"], "Test failure: example.Test.fails")

    def test_repeat_updates_body_without_comment(self):
        calls = self.run_report([{"number": 10, "body": reporter.report_body(
            "test:example.Test.fails", "old error", 2, "https://example.com/old", "job")}])
        self.assertEqual(len(calls), 1)
        self.assertEqual(calls[0].args[0], "repos/owner/repo/issues/10")
        self.assertIn("Occurrences: 3", calls[0].kwargs["body"])

    def test_same_run_does_not_increment_count(self):
        calls = self.run_report([{"number": 10, "body": reporter.report_body(
            "test:example.Test.fails", "old error", 2,
            "https://github.com/owner/repo/actions/runs/100", "job")}])
        self.assertIn("Occurrences: 2", calls[0].kwargs["body"])


if __name__ == "__main__":
    unittest.main()
