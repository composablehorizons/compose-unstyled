import unittest

from select_pr_checks import select_checks


class SelectChecksTest(unittest.TestCase):
    def test_website_only_skips_library_checks(self):
        self.assertEqual(select_checks([
            "website/src/layouts/SiteLayout.astro",
            "website/public/favicon.ico",
            ".github/workflows/deploy-website.yml",
            ".github/workflows/redeploy-docs.yml",
            ".github/workflows/docs.yml",
        ]), (False, True))

    def test_library_workflow_runs_library_checks(self):
        self.assertEqual(select_checks([".github/workflows/ci.yml"]), (True, False))

    def test_shared_selection_changes_run_both(self):
        for path in [
            ".github/scripts/select_pr_checks.py",
            ".github/scripts/test_select_pr_checks.py",
        ]:
            with self.subTest(path=path):
                self.assertEqual(select_checks([path]), (True, True))

    def test_docs_and_generator_build_website(self):
        for path in ["docs/pages/button.md", "docs/docs.yml", "scripts/generate-compose-unstyled-api.mjs"]:
            with self.subTest(path=path):
                self.assertEqual(select_checks([path]), (False, True))

    def test_library_and_demo_inputs_run_both(self):
        for path in [
            "composeunstyled-button/src/commonMain/kotlin/Button.kt",
            "demo/src/wasmJsMain/resources/index.html",
            "gradle/libs.versions.toml", "settings.gradle.kts", "gradle.properties",
        ]:
            with self.subTest(path=path):
                self.assertEqual(select_checks([path]), (True, True))

    def test_platform_tests_keep_library_checks(self):
        self.assertEqual(select_checks([
            "composeunstyled-button/src/androidInstrumentedTest/ButtonTest.kt",
        ]), (True, False))

    def test_mixed_pr_runs_both(self):
        self.assertEqual(select_checks([
            "website/package.json", "composeunstyled-button/src/commonTest/ButtonTest.kt",
        ]), (True, True))

    def test_releases_always_keep_all_checks(self):
        self.assertEqual(select_checks(["CHANGELOG.md"], "changeset-release/main"), (True, True))

    def test_unknown_paths_keep_library_checks(self):
        self.assertEqual(select_checks(["new-tool/config.json"]), (True, False))

    def test_root_markdown_needs_no_build(self):
        self.assertEqual(select_checks(["README.md", "CONTRIBUTING.md"]), (False, False))


if __name__ == "__main__":
    unittest.main()
