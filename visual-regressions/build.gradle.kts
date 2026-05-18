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
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.multiplatform)
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

kotlin {
  jvm {
    val mainCompilation = compilations.getByName("main")
    val screenshot by compilations.creating {
      associateWith(mainCompilation)
    }
  }

  sourceSets {
    all {
      languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    }

    commonMain.dependencies {
      implementation(libs.compose.foundation)
      implementation(projects.demo)
      implementation(projects.composeunstyledBottomSheet)
      implementation(projects.composeunstyledCheckbox)
      implementation(projects.composeunstyledIcon)
      implementation(projects.composeunstyledModal)
      implementation(projects.composeunstyledModalBottomSheet)
    }

    jvmMain.dependencies {
      implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material")
        exclude("org.jetbrains.compose.material3")
      }
    }

    val jvmScreenshotSharedDir = "src/jvmScreenshotShared/kotlin"

    val jvmScreenshot by getting {
      kotlin.srcDir(jvmScreenshotSharedDir)
      resources.srcDir("src/jvmTest/resources")
      dependencies {
        implementation(kotlin("test-junit"))
        implementation(libs.assertk)
        implementation(libs.compose.ui.test.junit4)
      }
    }
  }
}

val jvmScreenshotCompilation = kotlin.targets
  .getByName("jvm")
  .compilations
  .getByName("screenshot")

tasks.register<JavaExec>("takeScreenshots") {
  group = "verification"
  description = "Updates visual regression screenshot baselines."
  dependsOn(jvmScreenshotCompilation.compileTaskProvider)
  mainClass.set("com.composeunstyled.visualregressions.VisualRegressionBaselinesKt")
  classpath = files(
    jvmScreenshotCompilation.output.allOutputs,
    jvmScreenshotCompilation.runtimeDependencyFiles,
  )
  workingDir = projectDir
}

tasks.register<Test>("jvmScreenshotTest") {
  group = "verification"
  description = "Runs visual regression screenshot tests against checked-in baselines."
  dependsOn(jvmScreenshotCompilation.compileTaskProvider)
  testClassesDirs = jvmScreenshotCompilation.output.classesDirs
  classpath = files(
    jvmScreenshotCompilation.output.allOutputs,
    jvmScreenshotCompilation.runtimeDependencyFiles,
  )
  workingDir = projectDir
}
