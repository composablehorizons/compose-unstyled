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
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.composeunstyled.test.RecompositionTestScope
import com.composeunstyled.test.runComposeRecompositionTest
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.NoIndication
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.buildThemeV2
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
  fun themeAppliesTheSelectedColorSchemeOverrides() = runComposeUiTest {
    val sepia = ColorScheme("sepia")
    var colorScheme by mutableStateOf<ColorScheme>(ColorScheme.Dark)
    val testTheme = buildThemeV2 {
      properties[strings] = mapOf(
        text to "Default",
        alternateText to "Default alternate",
      )
      colorScheme(ColorScheme.Dark) {
        properties[strings] = mapOf(text to "Dark")
      }
      colorScheme(sepia) {
        properties[strings] = mapOf(text to "Sepia")
      }
    }

    setContent {
      testTheme(colorScheme = colorScheme) {
        BasicText(Theme[strings][text])
        BasicText(Theme[strings][alternateText])
      }
    }

    onNodeWithText("Dark").assertExists()
    onNodeWithText("Default alternate").assertExists()

    colorScheme = sepia

    onNodeWithText("Sepia").assertExists()
    onNodeWithText("Dark").assertDoesNotExist()
    onNodeWithText("Default alternate").assertExists()
  }

  @Test
  fun themeUsesDefaultPropertiesWhenTheSelectedSchemeHasNoOverrides() = runComposeUiTest {
    val sepia = ColorScheme("sepia")
    val testTheme = buildThemeV2 {
      properties[strings] = mapOf(text to "Default")
    }

    setContent {
      testTheme(colorScheme = sepia) {
        BasicText(Theme[strings][text])
      }
    }

    onNodeWithText("Default").assertExists()
  }

  @Test
  fun themeAnimatesColorValuesWhenColorSchemeTransitionSpecIsSet() = runComposeUiTest {
    val colors = ThemeProperty<Color>("colors")
    val background = ThemeToken<Color>("background")
    var colorScheme by mutableStateOf<ColorScheme>(ColorScheme.Light)
    var backgroundColor = Color.Unspecified
    var contentColor = Color.Unspecified
    val testTheme = buildThemeV2 {
      colorSchemeTransitionSpec = tween(durationMillis = 1_000)
      colorScheme(ColorScheme.Light) {
        defaultContentColor = Color.White
        properties[colors] = mapOf(background to Color.White)
      }
      colorScheme(ColorScheme.Dark) {
        defaultContentColor = Color.Black
        properties[colors] = mapOf(background to Color.Black)
      }
    }

    mainClock.autoAdvance = false
    try {
      setContent {
        testTheme(colorScheme = colorScheme) {
          backgroundColor = Theme[colors][background]
          contentColor = LocalContentColor.current
        }
      }
      mainClock.advanceTimeByFrame()
      assertThat(backgroundColor).isEqualTo(Color.White)
      assertThat(contentColor).isEqualTo(Color.White)

      colorScheme = ColorScheme.Dark
      mainClock.advanceTimeBy(500)

      assertThat(backgroundColor).isNotEqualTo(Color.White)
      assertThat(backgroundColor).isNotEqualTo(Color.Black)
      assertThat(contentColor).isNotEqualTo(Color.White)
      assertThat(contentColor).isNotEqualTo(Color.Black)

      mainClock.advanceTimeBy(500)

      assertThat(backgroundColor).isEqualTo(Color.Black)
      assertThat(contentColor).isEqualTo(Color.Black)
    } finally {
      mainClock.autoAdvance = true
    }
  }

  @Test
  fun parentRecompositionDoesNotRecomposeThemedContent() = runComposeRecompositionTest {
    var parentValue by mutableStateOf(0)
    var observedParentValue = -1
    val testTheme = buildTheme()

    setContent {
      val currentParentValue = parentValue
      SideEffect { observedParentValue = currentParentValue }

      testTheme {
        ThemedStaticContent()
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    parentValue++
    waitForIdle()

    assertThat(observedParentValue).isEqualTo(1)
    assertThat(recompositionCount("themed-content")).isEqualTo(0)
  }

  @Test
  fun parentRecompositionDoesNotRecomposeContentThatReadsThemeValues() =
    runComposeRecompositionTest {
      var parentValue by mutableStateOf(0)
      var observedParentValue = -1
      val testTheme = buildTheme {
        properties[strings] = mapOf(text to "Themed content")
      }

      setContent {
        val currentParentValue = parentValue
        SideEffect { observedParentValue = currentParentValue }

        testTheme {
          ThemePropertyContent()
        }
      }

      waitForIdle()
      resetRecompositionCounts()

      parentValue++
      waitForIdle()

      assertThat(observedParentValue).isEqualTo(1)
      assertThat(recompositionCount("property-reader")).isEqualTo(0)
    }

  @Test
  fun changingThemePropertyOnlyRecomposesContentThatReadsIt() = runComposeRecompositionTest {
    var themeValue by mutableStateOf("First")
    val testTheme = buildTheme {
      properties[strings] = mapOf(text to themeValue)
    }

    setContent {
      testTheme {
        ThemePropertyContent()
        StaticContent()
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    themeValue = "Second"
    waitForIdle()

    assertThat(recompositionCount("property-reader")).isEqualTo(1)
    assertThat(recompositionCount("static-content")).isEqualTo(0)
  }

  @Test
  fun changingDefaultContentColorOnlyRecomposesContentThatReadsIt() = runComposeRecompositionTest {
    var contentColor by mutableStateOf(Color.Black)
    val testTheme = buildTheme {
      defaultContentColor = contentColor
    }

    setContent {
      testTheme {
        ContentColorContent()
        StaticContent()
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    contentColor = Color.White
    waitForIdle()

    assertThat(recompositionCount("content-color-reader")).isEqualTo(1)
    assertThat(recompositionCount("static-content")).isEqualTo(0)
  }

  @Test
  fun changingColorSchemeOnlyRecomposesContentThatReadsThemeColors() = runComposeRecompositionTest {
    val colors = ThemeProperty<Color>("colors")
    val background = ThemeToken<Color>("background")
    var colorScheme by mutableStateOf<ColorScheme>(ColorScheme.Light)
    val testTheme = buildThemeV2 {
      colorScheme(ColorScheme.Light) {
        properties[colors] = mapOf(background to Color.White)
      }
      colorScheme(ColorScheme.Dark) {
        properties[colors] = mapOf(background to Color.Black)
      }
    }

    setContent {
      testTheme(colorScheme = colorScheme) {
        ThemeColorContent(colors, background)
        StaticContent()
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    colorScheme = ColorScheme.Dark
    waitForIdle()

    assertThat(recompositionCount("color-reader")).isEqualTo(1)
    assertThat(recompositionCount("static-content")).isEqualTo(0)
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
  val alternateText = ThemeToken<String>("alternate_label")

  val LocalExtendedLabel = compositionLocalOf { "Default" }

  @Composable
  private fun RecompositionTestScope.ThemedStaticContent() {
    RecompositionCount("themed-content")
    BasicText("Themed content")
  }

  @Composable
  private fun RecompositionTestScope.ThemePropertyContent() {
    RecompositionCount("property-reader")
    BasicText(Theme[strings][text])
  }

  @Composable
  private fun RecompositionTestScope.ContentColorContent() {
    RecompositionCount("content-color-reader")
    BasicText("Themed content", style = TextStyle(color = LocalContentColor.current))
  }

  @Composable
  private fun RecompositionTestScope.ThemeColorContent(
    colors: ThemeProperty<Color>,
    background: ThemeToken<Color>,
  ) {
    RecompositionCount("color-reader")
    BasicText("Themed content", style = TextStyle(color = Theme[colors][background]))
  }

  @Composable
  private fun RecompositionTestScope.StaticContent() {
    RecompositionCount("static-content")
    BasicText("Static content")
  }
}
