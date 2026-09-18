import unittest

from select_pr_checks import has_valid_release_scope, needs_library_checks, needs_screenshot_checks


class SelectChecksTest(unittest.TestCase):
    def test_website_only_skips_library_checks(self):
        self.assertEqual(needs_library_checks([
            "website/src/layouts/SiteLayout.astro",
            "website/public/favicon.ico",
            ".github/workflows/deploy-website.yml",
            ".github/workflows/redeploy-docs.yml",
            ".github/workflows/docs.yml",
            ".github/workflows/version-packages.yml",
        ]), False)

    def test_library_workflow_runs_library_checks(self):
        self.assertEqual(needs_library_checks([".github/workflows/ci.yml"]), True)

    def test_selection_changes_run_library_checks(self):
        for path in [
            ".github/scripts/select_pr_checks.py",
            ".github/scripts/test_select_pr_checks.py",
        ]:
            with self.subTest(path=path):
                self.assertEqual(needs_library_checks([path]), True)

    def test_docs_and_generator_skip_library_checks(self):
        for path in [
            "docs/pages/button.md",
            "docs/docs.yml",
            "scripts/generate-compose-unstyled-api.js",
            "scripts/generate-demo-registry.js",
        ]:
            with self.subTest(path=path):
                self.assertEqual(needs_library_checks([path]), False)

    def test_library_and_demo_inputs_run_library_checks(self):
        for path in [
            "composeunstyled-button/src/commonMain/kotlin/Button.kt",
            "demo/src/wasmJsMain/resources/index.html",
            "gradle/libs.versions.toml", "settings.gradle.kts", "gradle.properties",
        ]:
            with self.subTest(path=path):
                self.assertEqual(needs_library_checks([path]), True)

    def test_platform_tests_keep_library_checks(self):
        self.assertEqual(needs_library_checks([
            "composeunstyled-button/src/androidInstrumentedTest/ButtonTest.kt",
        ]), True)

    def test_mixed_pr_runs_library_checks(self):
        self.assertEqual(needs_library_checks([
            "website/package.json", "composeunstyled-button/src/commonTest/ButtonTest.kt",
        ]), True)

    def test_releases_skip_library_checks(self):
        self.assertEqual(needs_library_checks(["CHANGELOG.md"], "changeset-release/main"), False)

    def test_ui_and_screenshot_inputs_run_screenshot_checks(self):
        for path in [
            "composeunstyled-button/src/commonMain/kotlin/Button.kt",
            "composeunstyled-button/src/jvmMain/kotlin/Button.jvm.kt",
            "composeunstyled-button/src/androidMain/kotlin/Button.android.kt",
            "demo/src/commonMain/kotlin/Demo.kt",
            "visual-regressions/src/jvmScreenshot/kotlin/Regression.kt",
            "gradle/libs.versions.toml",
        ]:
            with self.subTest(path=path):
                self.assertEqual(needs_screenshot_checks([path]), True)

    def test_non_visual_inputs_skip_screenshot_checks(self):
        self.assertEqual(needs_screenshot_checks(["CHANGELOG.md"]), False)

    def test_releases_skip_screenshot_checks(self):
        self.assertEqual(needs_screenshot_checks(["gradle/libs.versions.toml"], "changeset-release/main"), False)

    def test_release_scope_only_allows_release_files(self):
        self.assertEqual(has_valid_release_scope(["CHANGELOG.md", ".changeset/example.md"]), True)
        self.assertEqual(has_valid_release_scope(["composeunstyled-button/src/commonMain/kotlin/Button.kt"]), False)

    def test_unknown_paths_keep_library_checks(self):
        self.assertEqual(needs_library_checks(["new-tool/config.json"]), True)

    def test_root_markdown_needs_no_build(self):
        self.assertEqual(needs_library_checks(["README.md", "CONTRIBUTING.md"]), False)


if __name__ == "__main__":
    unittest.main()
