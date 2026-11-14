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
package com.composeunstyled

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.buildThemeV2
import kotlin.test.Test

open class AppColors(val primary: Color, val background: Color) {
  override fun equals(other: Any?): Boolean = other is AppColors &&
    primary == other.primary &&
    background == other.background

  override fun hashCode(): Int = 31 * primary.hashCode() + background.hashCode()

  override fun toString(): String = "AppColors($primary, $background)"
}

class DarkAppColors : AppColors(Color.Black, Color.White)

data class AppStrings(val greeting: String)

class ThemeProvidedValuesTest {

  @Test
  fun readsProvidedValueByType() = runComposeUiTest {
    val theme = buildTheme {
      provide(AppColors(Color.Red, Color.Black))
      provide(AppStrings("Hello"))
    }
    var colors: AppColors? = null

    setContent {
      theme {
        colors = Theme.get<AppColors>()
        BasicText(Theme.get<AppStrings>().greeting)
      }
    }

    onNodeWithText("Hello").assertExists()
    assertThat(colors).isEqualTo(AppColors(Color.Red, Color.Black))
  }

  @Test
  fun colorSchemeOverridesProvidedValue() = runComposeUiTest {
    val theme = buildThemeV2 {
      provide(AppColors(Color.White, Color.Black))
      colorScheme(ColorScheme.Dark) {
        provide(AppColors(Color.Black, Color.White))
      }
    }
    var colors: AppColors? = null

    setContent {
      theme(ColorScheme.Dark) {
        colors = Theme.get<AppColors>()
      }
    }

    waitForIdle()
    assertThat(colors).isEqualTo(AppColors(Color.Black, Color.White))
  }

  @Test
  fun getOrNullReturnsNullWhenTheThemeDoesNotProvideTheType() = runComposeUiTest {
    val theme = buildTheme {}
    var colors: AppColors? = AppColors(Color.Red, Color.Red)

    setContent {
      theme {
        colors = Theme.getOrNull<AppColors>()
      }
    }

    waitForIdle()
    assertThat(colors).isNull()
  }

  @Test
  fun missingValueErrorExplainsHowToProvideIt() = runComposeUiTest {
    val theme = buildTheme { name = "Test" }

    assertFailure {
      setContent {
        theme {
          Theme.get<AppColors>()
        }
      }
    }.hasMessage(
      "There is no AppColors value in the Test theme. To fix this, " +
        "call provide(AppColors(...)) in your theme definition.",
    )
  }

  @Test
  fun subtypeIsReadByItsOwnClass() = runComposeUiTest {
    val theme = buildTheme {
      val colors: AppColors = DarkAppColors()
      provide(colors)
    }
    var darkColors: DarkAppColors? = null
    var appColors: AppColors? = AppColors(Color.Red, Color.Red)

    setContent {
      theme {
        darkColors = Theme.get<DarkAppColors>()
        appColors = Theme.getOrNull<AppColors>()
      }
    }

    waitForIdle()
    assertThat(darkColors).isEqualTo(DarkAppColors())
    assertThat(appColors).isNull()
  }
}
