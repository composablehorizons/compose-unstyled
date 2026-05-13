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

  jvm {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
  }

  androidTarget {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.androidx.annotation)
      implementation(libs.compose.material3)
      implementation("com.composables:icons-material-symbols-rounded-cmp:2.2.1")
      implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")
      implementation(projects.composeunstyledPrimitives)
      implementation(projects.composeunstyledWindowContainerSize)
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
    mainClass = "com.composeunstyled.demo.materialimpl.MainKt"
  }
}

android {
  namespace = "com.composeunstyled.demo.materialimpl"
  compileSdk = libs.versions.android.compileSDK.get().toInt()

  defaultConfig {
    applicationId = "com.composeunstyled.demo.materialimpl"
    minSdk = 23
    targetSdk = libs.versions.android.compileSDK.get().toInt()
    versionCode = 1
    versionName = "1.0.0"
  }
}

androidComponents {
  beforeVariants(selector().withBuildType("release")) { variantBuilder ->
    variantBuilder.enable = false
  }
}
