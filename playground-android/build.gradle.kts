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
plugins {
  alias(libs.plugins.android.application)
  id("org.jetbrains.kotlin.android")
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = "com.composeunstyled.demo.android"
  compileSdk = libs.versions.android.compileSDK.get().toInt()

  defaultConfig {
    applicationId = "com.composeunstyled.demo.android"
    minSdk = 23
    targetSdk = libs.versions.android.compileSDK.get().toInt()
    versionCode = 1
    versionName = "1.0.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }
  }

  buildFeatures {
    compose = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

kotlin {
  jvmToolchain(17)
}

dependencies {
  implementation(project(":composeunstyled-theming"))
  implementation(project(":composeunstyled-primitives"))
  implementation(project(":composeunstyled-platformtheme"))
  implementation(libs.androidx.activitycompose)

  implementation("androidx.compose.ui:ui:1.11.0-alpha05")
  implementation("androidx.compose.foundation:foundation:1.11.0-alpha05")
  implementation("androidx.compose.animation:animation:1.11.0-alpha05")
  implementation("androidx.compose.ui:ui-graphics:1.11.0-alpha05")
  implementation("androidx.compose.ui:ui-unit:1.11.0-alpha05")
  debugImplementation("androidx.compose.ui:ui-tooling:1.11.0-alpha05")
}
