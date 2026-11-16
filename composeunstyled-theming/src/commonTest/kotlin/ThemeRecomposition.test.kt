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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.composeunstyled.test.RecompositionTestScope
import com.composeunstyled.test.runComposeRecompositionTest
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.buildThemeV2
import kotlin.test.Test

class ThemeRecompositionTest {
  private val strings = ThemeProperty<String>("strings")
  private val label = ThemeToken<String>("label")
  private val colors = ThemeProperty<Color>("colors")
  private val background = ThemeToken<Color>("background")

  private data class Labels(val title: String)
  private data class Palette(val primary: Color)

  @Test
  fun recomposingThemeWithSameValuesDoesNotRecomposePropertyReaders() =
    runComposeRecompositionTest {
      var trigger by mutableStateOf(Unit, neverEqualPolicy())
      val testTheme = buildTheme {
        trigger
        properties[strings] = mapOf(label to "Label")
      }

      setContent {
        testTheme {
          StringReader()
        }
      }

      waitForIdle()
      resetRecompositionCounts()

      trigger = Unit
      waitForIdle()

      assertThat(recompositionCount("string-reader")).isEqualTo(0)
    }

  @Test
  fun recomposingThemeWithSameValuesDoesNotRecomposeProvidedValueReaders() =
    runComposeRecompositionTest {
      var trigger by mutableStateOf(Unit, neverEqualPolicy())
      val testTheme = buildTheme {
        trigger
        provide(Labels("Title"))
      }

      setContent {
        testTheme {
          LabelsReader()
        }
      }

      waitForIdle()
      resetRecompositionCounts()

      trigger = Unit
      waitForIdle()

      assertThat(recompositionCount("labels-reader")).isEqualTo(0)
    }

  @Test
  fun changingThemePropertyDoesNotRecomposeReadersOfOtherProperties() =
    runComposeRecompositionTest {
      var color by mutableStateOf(Color.White)
      val testTheme = buildTheme {
        properties[strings] = mapOf(label to "Label")
        properties[colors] = mapOf(background to color)
      }

      setContent {
        testTheme {
          StringReader()
          ColorReader()
        }
      }

      waitForIdle()
      resetRecompositionCounts()

      color = Color.Black
      waitForIdle()

      assertThat(recompositionCount("color-reader")).isEqualTo(1)
      assertThat(recompositionCount("string-reader")).isEqualTo(0)
    }

  @Test
  fun changingProvidedValueDoesNotRecomposeReadersOfOtherProvidedValues() =
    runComposeRecompositionTest {
      var primary by mutableStateOf(Color.White)
      val testTheme = buildTheme {
        provide(Labels("Title"))
        provide(Palette(primary))
      }

      setContent {
        testTheme {
          LabelsReader()
          PaletteReader()
        }
      }

      waitForIdle()
      resetRecompositionCounts()

      primary = Color.Black
      waitForIdle()

      assertThat(recompositionCount("palette-reader")).isEqualTo(1)
      assertThat(recompositionCount("labels-reader")).isEqualTo(0)
    }

  @Test
  fun changingThemePropertyDoesNotRecomposeProvidedValueReaders() = runComposeRecompositionTest {
    var color by mutableStateOf(Color.White)
    val testTheme = buildTheme {
      properties[colors] = mapOf(background to color)
      provide(Labels("Title"))
    }

    setContent {
      testTheme {
        ColorReader()
        LabelsReader()
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    color = Color.Black
    waitForIdle()

    assertThat(recompositionCount("color-reader")).isEqualTo(1)
    assertThat(recompositionCount("labels-reader")).isEqualTo(0)
  }

  @Test
  fun animatingColorSchemeDoesNotRecomposeReadersOfNonColorProperties() =
    runComposeRecompositionTest {
      var colorScheme by mutableStateOf<ColorScheme>(ColorScheme.Light)
      val testTheme = buildThemeV2 {
        colorSchemeTransitionSpec = tween(durationMillis = 300)
        properties[strings] = mapOf(label to "Label")
        provide(Labels("Title"))
        colorScheme(ColorScheme.Light) {
          properties[colors] = mapOf(background to Color.White)
        }
        colorScheme(ColorScheme.Dark) {
          properties[colors] = mapOf(background to Color.Black)
        }
      }

      setContent {
        testTheme(colorScheme = colorScheme) {
          StringReader()
          LabelsReader()
          ColorReader()
        }
      }

      waitForIdle()
      resetRecompositionCounts()

      colorScheme = ColorScheme.Dark
      waitForIdle()

      assertThat(recompositionCount("color-reader")).isGreaterThan(1)
      assertThat(recompositionCount("string-reader")).isEqualTo(0)
      assertThat(recompositionCount("labels-reader")).isEqualTo(0)
    }

  @Composable
  private fun RecompositionTestScope.StringReader() {
    RecompositionCount("string-reader")
    BasicText(Theme[strings][label])
  }

  @Composable
  private fun RecompositionTestScope.ColorReader() {
    RecompositionCount("color-reader")
    BasicText("Color", style = TextStyle(color = Theme[colors][background]))
  }

  @Composable
  private fun RecompositionTestScope.LabelsReader() {
    RecompositionCount("labels-reader")
    BasicText(Theme.get<Labels>().title)
  }

  @Composable
  private fun RecompositionTestScope.PaletteReader() {
    RecompositionCount("palette-reader")
    BasicText("Palette", style = TextStyle(color = Theme.get<Palette>().primary))
  }
}
