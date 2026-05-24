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
@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
val pomArtifactId = "composeunstyled-breakpoints"
val pomName = "Compose Unstyled Breakpoints"
val pomDescription = "Screen breakpoint utility for Compose Unstyled."
val frameworkBaseName = "ComposeUnstyledBreakpoints"
val androidNamespace = "com.composeunstyled.breakpoints"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

kotlin {
  androidTarget {
    publishLibraryVariants("release", "debug")
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
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
      baseName = frameworkBaseName
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.foundation)
      implementation(projects.composeunstyledWindowContainerSize)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.assertk)
    }

    applyDefaultHierarchyTemplate {
      common {
        group("nonAndroid") {
          withJvm()
          withIos()
          withWasmJs()
          withJs()
        }
      }
    }
  }
}

android {
  namespace = androidNamespace
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

  coordinates(
    groupId = publishGroupId,
    artifactId = pomArtifactId,
    version = publishVersion
  )

  pom {
    name.set(pomName)
    description.set(pomDescription)
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
