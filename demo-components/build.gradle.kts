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

  jvm("desktop") {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_17
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.foundation)
      implementation(libs.composables.icons.lucide)
      implementation("com.composables:compose-uri-painter:1.0.4")
      implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")

      implementation(projects.composeunstyledBottomSheet)
      implementation(projects.composeunstyledButton)
      implementation(projects.composeunstyledCheckbox)
      implementation(projects.composeunstyledDialog)
      implementation(projects.composeunstyledDisclosure)
      implementation(projects.composeunstyledDropdownMenu)
      implementation(projects.composeunstyledEscapeHandler)
      implementation(projects.composeunstyledFocusRing)
      implementation(projects.composeunstyledIcon)
      implementation(projects.composeunstyledModal)
      implementation(projects.composeunstyledModalBottomSheet)
      implementation(projects.composeunstyledOutline)
      implementation(projects.composeunstyledPortal)
      implementation(projects.composeunstyledProgress)
      implementation(projects.composeunstyledRadioGroup)
      implementation(projects.composeunstyledScrollbars)
      implementation(projects.composeunstyledScrim)
      implementation(projects.composeunstyledSeparators)
      implementation(projects.composeunstyledSlider)
      implementation(projects.composeunstyledTabGroup)
      implementation(projects.composeunstyledTextField)
      implementation(projects.composeunstyledTheming)
      implementation(projects.composeunstyledToggleSwitch)
      implementation(projects.composeunstyledTooltip)
      implementation(projects.composeunstyledTriStateCheckbox)
    }

    val desktopMain by getting {
      dependencies {
        implementation(compose.desktop.currentOs)
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "com.composeunstyled.demo.components.MainKt"
  }
}
