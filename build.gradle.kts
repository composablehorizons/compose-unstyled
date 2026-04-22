plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.compose.hotreload) apply false
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.detekt).apply(false)
    alias(libs.plugins.spotless)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target(
                fileTree(project.projectDir) {
                    include("src/**/*.kt")
                    exclude("src/**/kotlin/androidx/**/*.kt")
                }
            )
            ktlint().editorConfigOverride(
                mapOf(
                    "indent_size" to 2,
                    "continuation_indent_size" to 2,
                    "ktlint_standard_filename" to "disabled",
                )
            )
            licenseHeaderFile(rootProject.file("$rootDir/spotless/copyright.kt"))
        }
        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
            licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml")
            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
        }
    }
}
