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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import kotlin.test.Test
import kotlin.test.assertEquals

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
    assertEquals(1, themedContentRecompositions)
  }

  val strings = ThemeProperty<String>("strings")
  val text = ThemeToken<String>("label")
}
