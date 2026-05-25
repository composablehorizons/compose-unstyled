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

plugins {
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.library)
  alias(libs.plugins.maven.publish)
}

val publishGroupId = "com.composables"
val publishVersion = rootProject.extra["publishVersion"] as String
val githubUrl = "github.com/composablehorizons/compose-unstyled"
val projectUrl = "https://composeunstyled.com"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

kotlin {
  compilerOptions {
    optIn.add("androidx.compose.ui.test.ExperimentalTestApi")
    freeCompilerArgs.add("-Xcontext-parameters")
  }
  androidTarget {
    publishLibraryVariants("release", "debug")
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
    instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
  }

  jvm()

  wasmJs {
    browser()
  }

  js {
    browser()
  }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeUnstyledInternalAnchored"
      isStatic = true
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(projects.composeunstyledAnchoredApi)
        implementation(libs.compose.foundation)
        implementation(projects.composeunstyledWindowContainerSize)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
        implementation(libs.assertk)
      }
    }

    applyDefaultHierarchyTemplate {
      common {
        group("nonAndroid") {
          withJvm()
          withIos()
          withWasmJs()
          withJs()
        }

        group("web") {
          withWasmJs()
          withJs()
        }
      }
    }
  }
}

android {
  namespace = "com.composeunstyled.internal.anchored"
  compileSdk = libs.versions.android.compileSDK.get().toInt()
  defaultConfig {
    minSdk = libs.versions.android.minSDK.get().toInt()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

group = publishGroupId
version = publishVersion

mavenPublishing {
  publishToMavenCentral(automaticRelease = true, validateDeployment = false)
  if (project.hasProperty("signingInMemoryKeyId")) {
    signAllPublications()
  }

  coordinates(
    groupId = publishGroupId,
    artifactId = "composeunstyled-internal-anchored",
    version = publishVersion
  )

  pom {
    name.set("Compose Unstyled Internal Anchored")
    description.set("Internal anchored positioning implementation for Compose Unstyled.")
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
        email.set("alex@composables.com")
      }
    }

    scm {
      connection.set("scm:git:${githubUrl}.git")
      developerConnection.set("scm:git:ssh://${githubUrl}.git")
      url.set("https://${githubUrl}/tree/main")
    }
  }
}
