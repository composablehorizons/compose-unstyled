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
  alias(libs.plugins.compose.compiler)
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

dependencies {
  implementation(projects.demo)
  implementation(libs.androidx.activitycompose)
  debugImplementation(libs.compose.ui.tooling)
}
