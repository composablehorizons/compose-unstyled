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
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import kotlin.test.Test

class TextTest {

  @Test
  fun defaultColorIsUnspecified() = runComposeUiTest {
    setContent {
      Text("Hello World")
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Unspecified }
  }

  @Test
  fun setsColorViaProperty() = runComposeUiTest {
    setContent {
      Text("Hello World", color = Color.Red)
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun setsColorViaTextStyle() = runComposeUiTest {
    setContent {
      Text("Hello World", style = TextStyle(color = Color.Red))
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun setsColorViaTextStyleLocal() = runComposeUiTest {
    setContent {
      ProvideTextStyle(TextStyle(color = Color.Red)) {
        Text("Hello World")
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun setsColorViaColorLocal() = runComposeUiTest {
    setContent {
      ProvideContentColor(Color.Red) {
        Text("Hello World")
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun colorParameterOverridesTextStyleColor() = runComposeUiTest {
    setContent {
      Text("Hello World", color = Color.Red, style = TextStyle(color = Color.Blue))
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun colorParameterOverridesLocalTextStyleColor() = runComposeUiTest {
    setContent {
      ProvideTextStyle(TextStyle(color = Color.Blue)) {
        Text("Hello World", color = Color.Red)
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun colorParameterOverridesLocalContentColor() = runComposeUiTest {
    setContent {
      ProvideContentColor(Color.Blue) {
        Text("Hello World", color = Color.Red)
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun textStyleColorOverridesLocalTextStyleColor() = runComposeUiTest {
    setContent {
      ProvideTextStyle(TextStyle(color = Color.Blue)) {
        Text("Hello World", style = TextStyle(color = Color.Red))
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun textStyleColorOverridesLocalContentColor() = runComposeUiTest {
    setContent {
      ProvideContentColor(Color.Blue) {
        Text("Hello World", style = TextStyle(color = Color.Red))
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  @Test
  fun localTextStyleColorOverridesLocalContentColor() = runComposeUiTest {
    setContent {
      ProvideTextStyle(TextStyle(color = Color.Red)) {
        ProvideContentColor(Color.Blue) {
          Text("Hello World")
        }
      }
    }

    onNodeWithText("Hello World")
      .assertTextStyle { it.color == Color.Red }
  }

  private fun SemanticsNodeInteraction.assertTextStyle(matcher: (TextStyle) -> Boolean): SemanticsNodeInteraction {
    return this.assert(
      SemanticsMatcher("TextStyle") { node ->
        val textLayoutResults = mutableListOf<TextLayoutResult>()
        node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(textLayoutResults)

        if (textLayoutResults.isNotEmpty()) {
          val textStyle = textLayoutResults[0].layoutInput.style
          matcher(textStyle)
        } else {
          false
        }
      },
    )
  }
}
