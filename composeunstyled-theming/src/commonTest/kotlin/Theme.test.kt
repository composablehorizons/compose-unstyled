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

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.LocalColorScheme
import com.composeunstyled.theme.NoIndication
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.currentSystemColorScheme
import kotlin.test.Test

class ThemeCommonTest {

  @Test
  fun canSetAndReadStringValueFromTheme() = runComposeUiTest {
    val testTheme = buildTheme {
      properties[strings] = mapOf(
        text to "Hello Theme",
      )
    }

    setContent {
      testTheme {
        BasicText(Theme[strings][text])
      }
    }

    onNodeWithText("Hello Theme").assertExists()
  }

  @Test
  fun themeTokenValuesUpdateDisplayedText() = runComposeUiTest {
    var themeValue by mutableStateOf("Hello")

    setContent {
      val testTheme = buildTheme {
        properties[strings] = mapOf(
          text to themeValue,
        )
      }

      testTheme {
        BasicText(Theme[strings][text])
      }
    }

    onNodeWithText("Hello").assertExists()

    themeValue = "World"

    onNodeWithText("World").assertExists()
    onNodeWithText("Hello").assertDoesNotExist()
  }

  @Test
  fun assigningSameThemeValuesDoesNotCauseExtraRecompositions() = runComposeUiTest {
    // use neverEqualPolicy to force a recomposition
    var themeValue by mutableStateOf("Hello", neverEqualPolicy())
    var themedContentRecompositions = 0

    val TestTheme = buildTheme {
      println("RECOMPO")
      properties[strings] = mapOf(
        text to themeValue,
      )
    }
    setContent {
      TestTheme {
        SideEffect {
          themedContentRecompositions++
        }
        BasicText(Theme[strings][text])
      }
    }

    themeValue = "Hello"

    onNodeWithText("Hello").assertExists()
    waitForIdle()
    assertThat(themedContentRecompositions).isEqualTo(1)
  }

  @Test
  fun themeDefaultsToNoIndicationWhenParentProvidesIndication() = runComposeUiTest {
    val parentIndication = object : Indication {}
    var currentIndication: Indication = parentIndication
    val testTheme = buildTheme()

    setContent {
      CompositionLocalProvider(LocalIndication provides parentIndication) {
        testTheme {
          currentIndication = LocalIndication.current
        }
      }
    }

    waitForIdle()
    assertThat(currentIndication).isEqualTo(NoIndication)
  }

  @Test
  fun themeDefaultsToUnspecifiedTextSelectionColorsWhenParentProvidesColors() = runComposeUiTest {
    val parentSelectionColors = TextSelectionColors(
      handleColor = Color.Red,
      backgroundColor = Color.Blue,
    )
    var currentSelectionColors = parentSelectionColors
    val testTheme = buildTheme()

    setContent {
      CompositionLocalProvider(LocalTextSelectionColors provides parentSelectionColors) {
        testTheme {
          currentSelectionColors = LocalTextSelectionColors.current
        }
      }
    }

    waitForIdle()
    assertThat(currentSelectionColors.handleColor).isEqualTo(Color.Unspecified)
    assertThat(currentSelectionColors.backgroundColor).isEqualTo(Color.Unspecified)
  }

  @Test
  fun themeProvidesSystemColorSchemeByDefault() = runComposeUiTest {
    var expectedColorScheme = ColorScheme.Light
    var currentColorScheme = ColorScheme.Dark
    val testTheme = buildTheme()

    setContent {
      expectedColorScheme = currentSystemColorScheme()

      testTheme {
        currentColorScheme = LocalColorScheme.current
      }
    }

    waitForIdle()
    assertThat(currentColorScheme).isEqualTo(expectedColorScheme)
  }

  @Test
  fun extendedThemeCanOverrideColorScheme() = runComposeUiTest {
    var expectedColorScheme = ColorScheme.Light
    var currentColorScheme = ColorScheme.Light
    val testTheme = buildTheme {
      val colorScheme = if (currentSystemColorScheme() == ColorScheme.Dark) {
        ColorScheme.Light
      } else {
        ColorScheme.Dark
      }
      expectedColorScheme = colorScheme

      extend { content ->
        CompositionLocalProvider(LocalColorScheme provides colorScheme) {
          content()
        }
      }
    }

    setContent {
      testTheme {
        currentColorScheme = LocalColorScheme.current
      }
    }

    waitForIdle()
    assertThat(currentColorScheme).isEqualTo(expectedColorScheme)
  }

  @Test
  fun extendWrapsThemeContent() = runComposeUiTest {
    val testTheme = buildTheme {
      extend { content ->
        CompositionLocalProvider(LocalExtendedLabel provides "Extended") {
          content()
        }
      }
    }

    setContent {
      testTheme {
        BasicText(LocalExtendedLabel.current)
      }
    }

    onNodeWithText("Extended").assertExists()
    onNodeWithText("Default").assertDoesNotExist()
  }

  @Test
  fun extendCanOnlyBeCalledOnce() {
    assertFailure {
      runComposeUiTest {
        val testTheme = buildTheme {
          extend { content ->
            content()
          }
          extend { content ->
            content()
          }
        }

        setContent {
          testTheme {
            BasicText("Theme")
          }
        }
      }
    }.hasMessage(
      "Themes can only be extended exactly once. " +
        "Make sure you use the `extend {}` block within your buildTheme {} only once.",
    )
  }

  @Test
  fun extendedContentCanOnlyBeEmittedOnce() {
    assertFailure {
      runComposeUiTest {
        val testTheme = buildTheme {
          extend { content ->
            content()
            content()
          }
        }

        setContent {
          testTheme {
            BasicText("Theme")
          }
        }
      }
    }.hasMessage("You may call the content lambda of extend {} exactly once.")
  }

  @Test
  fun extendedContentCanRecompose() = runComposeUiTest {
    var label by mutableStateOf("First")
    val testTheme = buildTheme {
      extend { content ->
        CompositionLocalProvider(LocalExtendedLabel provides label) {
          content()
        }
      }
    }

    setContent {
      testTheme {
        BasicText(LocalExtendedLabel.current)
      }
    }

    onNodeWithText("First").assertExists()

    label = "Second"

    onNodeWithText("Second").assertExists()
    onNodeWithText("First").assertDoesNotExist()
  }

  @Test
  fun themeBuilderActionCanRecomposeWithExtend() = runComposeUiTest {
    var label by mutableStateOf("First")
    val testTheme = buildTheme {
      val currentLabel = label

      extend { content ->
        CompositionLocalProvider(LocalExtendedLabel provides currentLabel) {
          content()
        }
      }
    }

    setContent {
      testTheme {
        BasicText(LocalExtendedLabel.current)
      }
    }

    onNodeWithText("First").assertExists()

    label = "Second"

    onNodeWithText("Second").assertExists()
    onNodeWithText("First").assertDoesNotExist()
  }

  val strings = ThemeProperty<String>("strings")
  val text = ThemeToken<String>("label")

  val LocalExtendedLabel = compositionLocalOf { "Default" }
}
