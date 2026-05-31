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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
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
  js(IR) {
    browser {
      commonWebpackConfig {
        outputFileName = "composeApp.js"
        val rootDirPath = project.rootDir.path
        val projectDirPath = project.projectDir.path
        outputPath = File("$rootDirPath/docs/demo")
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
      implementation(project(":composeunstyled-theming"))
      implementation(project(":composeunstyled-primitives"))
      implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")
      implementation(libs.composables.icons.lucide)

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
        // Material 3 XML themes
        implementation("com.google.android.material:material:1.12.0")
        // Material 3 compose used for the ripple effect
        implementation(libs.compose.material3)
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
  defaultConfig {
    minSdk = 23
    targetSdk = libs.versions.android.compileSDK.get().toInt()
    applicationId = "com.composeunstyled.demo"
    versionCode = 1
    versionName = "1.0.0"
  }
}

androidComponents {
  beforeVariants(selector().withBuildType("release")) { variantBuilder ->
    variantBuilder.enable = false
  }
}
