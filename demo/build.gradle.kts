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
  alias(libs.plugins.android.kotlin.multiplatform.library)
}

val generatedDemoRegistry = layout.buildDirectory.file("generated/demo-registry/GeneratedDemoRegistry.kt")
val generatedDemoSourceMap = layout.buildDirectory.file("generated/demo-registry/DemoSourceMap.properties")

val generateDemoRegistry by tasks.registering(Exec::class) {
  group = "build"
  description = "Generates the demo registry from @UnstyledDemo declarations."

  inputs.dir(layout.projectDirectory.dir("src/commonMain/kotlin"))
  inputs.file(rootProject.layout.projectDirectory.file("scripts/generate-demo-registry.js"))
  outputs.file(generatedDemoRegistry)
  outputs.file(generatedDemoSourceMap)

  commandLine(
    "bun",
    rootProject.layout.projectDirectory.file("scripts/generate-demo-registry.js").asFile.absolutePath,
    generatedDemoRegistry.get().asFile.absolutePath,
    generatedDemoSourceMap.get().asFile.absolutePath,
  )
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
      commonWebpackConfig {
        outputFileName = "composeApp.js"
      }
    }
    binaries.executable()
  }

  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "composeApp.js"
      }
    }
    binaries.executable()
  }
  jvm()

  android {
    namespace = "com.composeunstyled.demo.shared"
    compileSdk = libs.versions.android.compileSDK.get().toInt()
    minSdk = 23
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
    commonMain {
      kotlin.srcDir(generatedDemoRegistry.map { it.asFile.parentFile })

      dependencies {
        implementation(libs.compose.foundation)
        implementation(libs.compose.ui.tooling.preview)
        implementation(libs.composables.ripple)
        implementation(project(":composeunstyled"))
        implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")
        implementation(libs.composables.icons.lucide)
        implementation(libs.compose.uri.painter)
      }
    }

    jvmMain.dependencies {
      implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
        exclude("org.jetbrains.compose.material3")
      }
    }

  }
}

tasks.configureEach {
  if (name.startsWith("compile")) {
    dependsOn(generateDemoRegistry)
  }
}

compose.desktop {
  application {
    mainClass = "com.composeunstyled.demo.MainKt"
  }
}
