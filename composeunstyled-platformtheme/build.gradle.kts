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
@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.maven.publish)
}

val publishGroupId = "com.composables"
val publishVersion = libs.versions.unstyled.get()
val githubUrl = "github.com/composablehorizons/compose-unstyled"
val projectUrl = "https://composeunstyled.com"

java {
  toolchain {
    vendor = JvmVendorSpec.JETBRAINS
    languageVersion = JavaLanguageVersion.of(17)
  }
}

kotlin {
  androidTarget {
    publishLibraryVariants("release", "debug")
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
    instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
  }

  jvm()

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

  js {
    browser()
  }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeUnstyledPlatformTheme"
      isStatic = true
    }
  }

  applyDefaultHierarchyTemplate {
    common {
      group("nonAndroid") {
        withIos()
        withJvm()
        withJs()
        withWasmJs()
      }

      group("nonWeb") {
        withIos()
        withAndroidTarget()
        withJvm()
      }
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(compose.foundation)
      api(projects.composeunstyledTheming)
      implementation(compose.components.resources)
    }
    androidMain.dependencies {
      implementation(libs.composables.ripple)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
    }

    val jvmTest by getting

    jvmTest.dependencies {
      implementation(compose.desktop.uiTestJUnit4)
      implementation(compose.desktop.currentOs) {
        exclude(compose.material)
      }
    }

    all {
      if (name.endsWith("Test") || name.contains("InstrumentedTest")) {
        languageSettings.optIn("androidx.compose.ui.test.ExperimentalTestApi")
      }
    }
  }
}

android {
  namespace = "com.composeunstyled.platformtheme"
  compileSdk = libs.versions.android.compileSDK.get().toInt()
  defaultConfig {
    minSdk = libs.versions.android.minSDK.get().toInt()
  }
}

group = publishGroupId
version = publishVersion

mavenPublishing {
  publishToMavenCentral(automaticRelease = true, validateDeployment = false)
  if (project.hasProperty("signingInMemoryKeyId")) {
    signAllPublications()
  }

  coordinates(publishGroupId, "composeunstyled-platformtheme", version = publishVersion)

  pom {
    name.set("Compose Unstyled Platform Theme")
    description.set("Theme with native look and feel for Compose Multiplatform.")
    url.set(projectUrl)

    licenses {
      license {
        name.set("MIT License")
        url.set("https://${githubUrl}/blob/main/LICENSE")
      }
    }

    issueManagement {
      system.set("GitHub Issues")
      url.set("https://${githubUrl}/issues")
    }

    developers {
      developer {
        id.set("composablehorizons")
        name.set("Composable Horizons")
        email.set("alex@composablesui.com")
      }
    }

    scm {
      connection.set("scm:git:${githubUrl}.git")
      developerConnection.set("scm:git:ssh://${githubUrl}.git")
      url.set("https://${githubUrl}/tree/main")
    }
  }
}
