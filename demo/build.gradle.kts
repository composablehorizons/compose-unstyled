/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
}

val demoVersionName = providers
  .gradleProperty("publishVersion")
  .orElse(libs.versions.unstyled)
  .get()

fun androidVersionCodeFrom(versionName: String): Int {
  val parts = versionName
    .substringBefore("-")
    .removePrefix("v")
    .split(".")
    .map { it.toInt() }

  check(parts.size == 3) {
    "Expected a semantic version with major, minor, and patch parts, got '$versionName'."
  }

  val (major, minor, patch) = parts
  check(minor in 0..99 && patch in 0..99) {
    "Android versionCode only supports minor and patch values from 0 to 99, got '$versionName'."
  }

  return major * 10_000 + minor * 100 + patch
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

kotlin {
  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
  js {
    browser {
      val rootDirPath = project.rootDir.path
      val projectDirPath = project.projectDir.path
      commonWebpackConfig {
        outputFileName = "composeApp.js"

        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
          static = (static ?: mutableListOf()).apply {
            // Serve sources to debug inside browser
            add(rootDirPath)
            add(projectDirPath)
          }
        }
      }
    }
    binaries.executable()
  }

  wasmJs {
    browser {
      val rootDirPath = project.rootDir.path
      val projectDirPath = project.projectDir.path
      commonWebpackConfig {
        outputFileName = "composeApp.js"

        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
          static = (static ?: mutableListOf()).apply {
            // Serve sources to debug inside browser
            add(rootDirPath)
            add(projectDirPath)
          }
        }
      }
    }
    binaries.executable()
  }
  jvm()

  androidTarget {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
  }

  listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    all {
      languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
      languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    }
    commonMain.dependencies {
      implementation(libs.compose.components.resources)
      implementation(libs.compose.foundation)
      implementation(libs.composables.ripple)
      implementation(project(":composeunstyled-theming"))
      implementation(project(":composeunstyled-avatar"))
      implementation(project(":composeunstyled-breakpoints"))
      implementation(project(":composeunstyled-bottom-sheet"))
      implementation(project(":composeunstyled-build-modifier"))
      implementation(project(":composeunstyled-button"))
      implementation(project(":composeunstyled-checkbox"))
      implementation(project(":composeunstyled-dialog"))
      implementation(project(":composeunstyled-disclosure"))
      implementation(project(":composeunstyled-dropdown-menu"))
      implementation(project(":composeunstyled-focus-ring"))
      implementation(project(":composeunstyled-icon"))
      implementation(project(":composeunstyled-modal"))
      implementation(project(":composeunstyled-modal-bottom-sheet"))
      implementation(project(":composeunstyled-outline"))
      implementation(project(":composeunstyled-portal"))
      implementation(project(":composeunstyled-progress"))
      implementation(project(":composeunstyled-radio-group"))
      implementation(project(":composeunstyled-scrollbars"))
      implementation(project(":composeunstyled-separators"))
      implementation(project(":composeunstyled-slider"))
      implementation(project(":composeunstyled-stack"))
      implementation(project(":composeunstyled-tab-group"))
      implementation(project(":composeunstyled-text-field"))
      implementation(project(":composeunstyled-toggle-switch"))
      implementation(project(":composeunstyled-tooltip"))
      implementation(project(":composeunstyled-tri-state-checkbox"))
      implementation(project(":composeunstyled-platformtheme"))
      implementation(project(":composeunstyled-escape-handler"))
      implementation(project(":composeunstyled-window-container-size"))
      implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")
      implementation(libs.composables.icons.lucide)
      implementation(libs.compose.uri.painter)
    }

    jvmMain.dependencies {
      implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
        exclude("org.jetbrains.compose.material3")
      }
    }

    val androidMain by getting {
      dependencies {
        implementation(libs.androidx.activitycompose)
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "com.composeunstyled.demo.MainKt"
  }
}

android {
  namespace = "com.composeunstyled.demo"
  compileSdk = libs.versions.android.compileSDK.get().toInt()
  signingConfigs {
    getByName("debug") {
      storeFile = layout.projectDirectory.file("demo-debug.keystore").asFile
      storePassword = "android"
      keyAlias = "demo-debug"
      keyPassword = "android"
    }
  }
  defaultConfig {
    minSdk = 23
    targetSdk = libs.versions.android.compileSDK.get().toInt()
    applicationId = "com.composeunstyled.demo"
    versionCode = androidVersionCodeFrom(demoVersionName)
    versionName = demoVersionName
  }
}

androidComponents {
  beforeVariants(selector().withBuildType("release")) { variantBuilder ->
    variantBuilder.enable = false
  }
}
