plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.compose).apply(false)
  alias(libs.plugins.android.application).apply(false)
  alias(libs.plugins.android.library).apply(false)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.spotless)
}

subprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

  plugins.withId(rootProject.libs.plugins.kotlin.multiplatform.get().pluginId) {
    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
      sourceSets.matching { it.name == "androidInstrumentedTest" }.configureEach {
        dependencies {
          implementation(rootProject.libs.androidx.test.runner)
        }
      }
    }
  }

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target(
        fileTree(project.projectDir) {
          include("src/**/*.kt")
          exclude("src/**/kotlin/androidx/**/*.kt")
          exclude("src/**/kotlin/com/composeunstyled/ContextDpToPx.kt")
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
