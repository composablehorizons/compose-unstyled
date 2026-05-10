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

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.assertions.isTrue
import kotlin.test.Test

class TextFieldTest {
  @Test
  fun movesFocusToInputWhenTextFieldIsFocused() = runComposeUiTest {
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("textfield"),
      ) {
        Editable()
      }
    }
    onNodeWithTag("textfield").requestFocus()
    onNodeWithTag("textfield", useUnmergedTree = true).assertIsFocused()
  }

  @Test
  fun exposesAccessibilityLabelSemantics() = runComposeUiTest {
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("input"),
        accessibilityLabel = "Email",
      ) {
        Editable()
      }
    }

    onNodeWithTag("input", useUnmergedTree = true)
      .assert(
        SemanticsMatcher.expectValue(
          SemanticsProperties.ContentDescription,
          listOf("Email"),
        ),
      )
  }

  @Test
  fun typingTextWhileTextFieldIsFocused_entersText() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield")
          // setting size as test can't click otherwise
          .size(50.dp),
      ) {
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("A")
    onNodeWithTag("textfield").assertTextEquals("A")
  }

  @Test
  fun changingValueUpdatesRenderedText() = runComposeUiTest {
    val state = TextFieldState("initial")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").assertTextEquals("initial")

    state.setTextAndPlaceCursorAtEnd("updated")

    onNodeWithTag("textfield").assertTextEquals("updated")
  }

  @Test
  fun outputTransformationTransformsText() = runComposeUiTest {
    val state = TextFieldState("secret")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        outputTransformation = PasswordOutputTransformation,
      ) {
        Editable()
      }
    }

    onNodeWithText("••••••", useUnmergedTree = true).assertTextEquals("••••••")
  }

  @Test
  fun outputTransformationTransformsNonEditableText() = runComposeUiTest {
    val state = TextFieldState("secret")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        editable = false,
        outputTransformation = PasswordOutputTransformation,
      ) {
        Editable()
      }
    }

    onNodeWithText("••••••", useUnmergedTree = true).assertTextEquals("••••••")
  }

  @Test
  fun nonEditableTextFieldRendersTextAsSelectableContent() = runComposeUiTest {
    val state = TextFieldState("secret")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        editable = false,
      ) {
        Editable()
      }
    }

    onNodeWithText("secret", useUnmergedTree = true).assertTextEquals("secret")
  }

  @Test
  fun focusDetected_whenFocusedAndUsingOnFocusChangedModifier() = runComposeUiTest {
    var focused by mutableStateOf(false)

    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier
          .testTag("textfield")
          .onFocusChanged { focusState ->
            focused = focusState.isFocused
          },
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").requestFocus()
    waitForIdle()
    assertThat(focused).isTrue()
  }

  @Test
  fun focusDetected_whenFocusedAndUsingInteractionSource() = runComposeUiTest {
    val interactionSource = MutableInteractionSource()

    setContent {
      val isFocused by interactionSource.collectIsFocusedAsState()
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("textfield"),
        interactionSource = interactionSource,
      ) {
        Editable()
        if (isFocused) {
          BasicText("focused", modifier = Modifier.testTag("focus-indicator"))
        }
      }
    }

    onNodeWithTag("textfield").requestFocus()
    waitForIdle()
    onNodeWithTag("focus-indicator", useUnmergedTree = true).assertExists()
  }

  @Test
  fun placeholderIsVisibleWhenTextIsEmpty() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(state = state, modifier = Modifier.testTag("textfield")) {
        Editable(
          placeholder = {
            BasicText("Search", modifier = Modifier.testTag("placeholder"))
          },
        )
      }
    }

    onNodeWithTag("placeholder", useUnmergedTree = true).assertExists()
  }

  @Test
  fun textFieldDoesNotFillParentByDefault() = runComposeUiTest {
    setContent {
      Box(Modifier.testTag("root").size(400.dp, 100.dp)) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.testTag("textfield"),
        ) {
          Editable(
            placeholder = {
              BasicText("Search", modifier = Modifier.testTag("placeholder"))
            },
          )
        }
      }
    }

    val inputWidth = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.width

    assertThat(inputWidth).isLessThan(200f)
  }

  @Test
  fun editableDoesNotFillParentByDefault() = runComposeUiTest {
    setContent {
      Box(Modifier.testTag("root").size(400.dp, 100.dp)) {
        UnstyledTextField(
          state = rememberTextFieldState("A"),
          modifier = Modifier.testTag("textfield"),
        ) {
          Editable()
        }
      }
    }

    val inputWidth = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.width

    assertThat(inputWidth).isLessThan(200f)
  }

  @Test
  fun placeholderDoesNotAffectTextFieldSize() = runComposeUiTest {
    setContent {
      Row(Modifier.testTag("root").size(400.dp, 100.dp)) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.testTag("without-placeholder"),
        ) {
          Editable()
        }
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.testTag("with-placeholder"),
        ) {
          Editable(
            placeholder = {
              BasicText(
                "This placeholder is intentionally much wider than the editable",
                modifier = Modifier.testTag("placeholder"),
              )
            },
          )
        }
      }
    }

    val widthWithoutPlaceholder =
      onNodeWithTag("without-placeholder").fetchSemanticsNode().boundsInRoot.width
    val widthWithPlaceholder =
      onNodeWithTag("with-placeholder").fetchSemanticsNode().boundsInRoot.width

    assertThat(widthWithPlaceholder).isEqualTo(widthWithoutPlaceholder)
  }

  @Test
  fun placeholderIsClippedToEditableSize() = runComposeUiTest {
    setContent {
      Box(
        Modifier
          .testTag("root")
          .size(400.dp, 100.dp)
          .background(Color.White),
      ) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.testTag("textfield"),
        ) {
          Editable(
            placeholder = {
              Box(
                Modifier
                  .testTag("placeholder")
                  .size(200.dp, 20.dp)
                  .background(Color.Red),
              )
            },
          )
        }
      }
    }

    val inputBounds = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot
    val pixelMap = onNodeWithTag("root").captureToImage().toPixelMap()
    val insideEditableX = (inputBounds.left + 1f).toInt()
    val outsideEditableX = (inputBounds.right + 10f).toInt()
    val insideEditableY = (inputBounds.top + 2f).toInt()

    assertThat(pixelMap[insideEditableX, insideEditableY]).isEqualTo(Color.Red)
    assertThat(pixelMap[outsideEditableX, insideEditableY]).isEqualTo(Color.White)
  }

  @Test
  fun placeholderCanUseCallerProvidedEditableWidth() = runComposeUiTest {
    setContent {
      Box(
        Modifier
          .testTag("root")
          .size(400.dp, 100.dp)
          .background(Color.White),
      ) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.testTag("textfield"),
        ) {
          Editable(
            modifier = Modifier.size(200.dp, 40.dp),
            placeholder = {
              Box(
                Modifier
                  .testTag("placeholder")
                  .size(200.dp, 20.dp)
                  .background(Color.Red),
              )
            },
          )
        }
      }
    }

    val inputBounds = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot
    val pixelMap = onNodeWithTag("root").captureToImage().toPixelMap()
    val insideCallerWidthX = (inputBounds.right - 10f).toInt()
    val insideEditableY = (inputBounds.top + 2f).toInt()

    assertThat(inputBounds.width).isGreaterThan(100f)
    assertThat(pixelMap[insideCallerWidthX, insideEditableY]).isEqualTo(Color.Red)
  }

  @Test
  fun callerProvidedWidthDoesNotLetPlaceholderGrowEditableHeight() = runComposeUiTest {
    setContent {
      Box(
        Modifier
          .testTag("root")
          .size(400.dp, 160.dp)
          .background(Color.White),
      ) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.testTag("textfield"),
        ) {
          Editable(
            modifier = Modifier.width(200.dp),
            placeholder = {
              Box(
                Modifier
                  .testTag("placeholder")
                  .size(200.dp, 120.dp)
                  .background(Color.Red),
              )
            },
          )
        }
      }
    }

    val inputBounds = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot
    val pixelMap = onNodeWithTag("root").captureToImage().toPixelMap()
    val belowEditableX = (inputBounds.left + 10f).toInt()
    val belowEditableY = (inputBounds.bottom + 10f).toInt()

    assertThat(inputBounds.width).isGreaterThan(100f)
    assertThat(inputBounds.height).isLessThan(100f)
    assertThat(pixelMap[belowEditableX, belowEditableY]).isEqualTo(Color.White)
  }

  @Test
  fun placeholderFollowsTextAlignment() = runComposeUiTest {
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("textfield").size(100.dp),
        contentAlignment = Alignment.TopStart,
        textAlign = TextAlign.End,
      ) {
        Editable(
          modifier = Modifier.fillMaxWidth(),
          placeholder = {
            BasicText("Search", modifier = Modifier.testTag("placeholder"))
          },
        )
      }
    }

    val inputLeft = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.left
    val placeholderLeft = onNodeWithTag(
      "placeholder",
      useUnmergedTree = true,
    ).fetchSemanticsNode().boundsInRoot.left

    assertThat(placeholderLeft).isGreaterThan(inputLeft + 50f)
  }

  @Test
  fun editableContentFollowsContentAlignment() = runComposeUiTest {
    setContent {
      Box(Modifier.testTag("root").size(100.dp)) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.fillMaxWidth().size(100.dp),
          contentAlignment = Alignment.CenterStart,
        ) {
          Editable(
            placeholder = {
              BasicText("Search", modifier = Modifier.testTag("placeholder"))
            },
          )
        }
      }
    }

    val inputTop = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot.top
    val placeholderTop = onNodeWithTag(
      "placeholder",
      useUnmergedTree = true,
    ).fetchSemanticsNode().boundsInRoot.top

    assertThat(placeholderTop).isGreaterThan(inputTop + 30f)
  }

  @Test
  fun contentAlignmentPositionsAllContent() = runComposeUiTest {
    setContent {
      Box(Modifier.testTag("root").size(200.dp, 100.dp)) {
        UnstyledTextField(
          state = rememberTextFieldState(),
          modifier = Modifier.size(200.dp, 100.dp),
          contentAlignment = Alignment.CenterEnd,
        ) {
          Row {
            BasicText("Icon", modifier = Modifier.testTag("leading"))
            Editable(
              placeholder = {
                BasicText("Search", modifier = Modifier.testTag("placeholder"))
              },
            )
          }
        }
      }
    }

    val inputLeft = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot.left
    val leadingLeft = onNodeWithTag(
      "leading",
      useUnmergedTree = true,
    ).fetchSemanticsNode().boundsInRoot.left
    val placeholderLeft = onNodeWithTag(
      "placeholder",
      useUnmergedTree = true,
    ).fetchSemanticsNode().boundsInRoot.left

    assertThat(leadingLeft).isGreaterThan(inputLeft + 20f)
    assertThat(placeholderLeft).isGreaterThan(leadingLeft)
  }

  @Test
  fun placeholderHidesWhenNonWhitespaceTextIsEntered() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier
          .testTag("textfield")
          .size(100.dp),
      ) {
        Editable(
          placeholder = {
            BasicText("Search", modifier = Modifier.testTag("placeholder"))
          },
        )
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("a")
    onNodeWithTag("placeholder", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun placeholderHidesWhenWhitespaceOnlyTextIsEntered() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(state = state, modifier = Modifier.testTag("textfield")) {
        Editable(
          placeholder = {
            BasicText("Search", modifier = Modifier.testTag("placeholder"))
          },
        )
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput(" ")
    onNodeWithTag("placeholder", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun outputTransformationWithLongerOutputWorksWithStateBasedTextField() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").size(120.dp),
        outputTransformation = CreditCardOutputTransformation,
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("1234")
    onNodeWithTag("textfield").assertTextEquals("1234-")
  }

  @Test
  fun inputTransformationFiltersUserInput() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").size(120.dp),
        inputTransformation = InputTransformation.maxLength(2),
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("12")
    onNodeWithTag("textfield").performTextInput("3")

    onNodeWithTag("textfield").assertTextEquals("12")
    assertThat(state.text.toString()).isEqualTo("12")
  }

  @Test
  fun outputTransformationChangesRenderedTextWithoutChangingState() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").size(120.dp),
        outputTransformation = object : OutputTransformation {
          override fun TextFieldBuffer.transformOutput() {
            replace(0, 0, "(")
            replace(length, length, ")")
          }
        },
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("12")

    onNodeWithTag("textfield").assertTextEquals("(12)")
    assertThat(state.text.toString()).isEqualTo("12")
  }

  @Test
  fun lineLimitsAreForwardedToEditable() = runComposeUiTest {
    val state = TextFieldState("")
    var lineCount = 0
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").size(120.dp),
        lineLimits = TextFieldLineLimits.SingleLine,
        onTextLayout = { getResult ->
          lineCount = getResult()?.lineCount ?: 0
        },
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("A\nB")
    waitForIdle()

    assertThat(lineCount).isEqualTo(1)
  }

  @Test
  fun multiLineTextFieldAcceptsEnterKey() = runComposeUiTest {
    val state = TextFieldState("")
    var lineCount = 0
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").width(240.dp),
        lineLimits = TextFieldLineLimits.MultiLine(),
        onTextLayout = { getResult ->
          lineCount = getResult()?.lineCount ?: 0
        },
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("A")
    onNodeWithTag("textfield").performKeyInput {
      pressKey(Key.Enter)
    }
    onNodeWithTag("textfield").performTextInput("B")
    waitForIdle()

    assertThat(state.text.toString()).isEqualTo("A\nB")
    assertThat(lineCount).isEqualTo(2)
  }

  @Test
  fun multiLineEnteredTextIsDisplayed() = runComposeUiTest {
    val enteredText = "email is\nso\ncool"
    var layoutText = ""
    var lineCount = 0
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("textfield").width(240.dp),
        lineLimits = TextFieldLineLimits.MultiLine(),
        onTextLayout = { getResult ->
          val result = getResult()
          layoutText = result?.layoutInput?.text?.text.orEmpty()
          lineCount = result?.lineCount ?: 0
        },
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput(enteredText)
    waitForIdle()

    onNodeWithTag("textfield").assertTextEquals(enteredText)
    assertThat(layoutText).isEqualTo(enteredText)
    assertThat(lineCount).isEqualTo(3)
  }

  @Test
  fun multiLineEnteredTextDisplaysEmptyLines() = runComposeUiTest {
    val enteredText = "email is\n\nso cool"
    var layoutText = ""
    var lineCount = 0
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("textfield").width(240.dp),
        lineLimits = TextFieldLineLimits.MultiLine(),
        onTextLayout = { getResult ->
          val result = getResult()
          layoutText = result?.layoutInput?.text?.text.orEmpty()
          lineCount = result?.lineCount ?: 0
        },
      ) {
        Editable()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput(enteredText)
    waitForIdle()

    onNodeWithTag("textfield").assertTextEquals(enteredText)
    assertThat(layoutText).isEqualTo(enteredText)
    assertThat(lineCount).isEqualTo(3)
  }

  @Test
  fun multiLineEnteredTextMeasuresEmptyMiddleLines() = runComposeUiTest {
    val state = TextFieldState("e\nwhat")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").width(240.dp),
        lineLimits = TextFieldLineLimits.MultiLine(),
      ) {
        Editable()
      }
    }

    val twoLineHeight = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.height
    state.setTextAndPlaceCursorAtEnd("e\n\nwhat")
    waitForIdle()

    val emptyMiddleLineHeight = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.height

    onNodeWithTag("textfield").assertTextEquals("e\n\nwhat")
    assertThat(emptyMiddleLineHeight).isGreaterThan(twoLineHeight * 1.4f)
  }

  @Test
  fun multiLineTextFieldKeepsTrailingBlankLinesVisible() = runComposeUiTest {
    val state = TextFieldState("")
    var lineCount = 0
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").width(240.dp),
        lineLimits = TextFieldLineLimits.MultiLine(),
        onTextLayout = { getResult ->
          lineCount = getResult()?.lineCount ?: 0
        },
      ) {
        Editable()
      }
    }

    val initialHeight = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.height
    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("A")
    onNodeWithTag("textfield").performKeyInput {
      pressKey(Key.Enter)
      pressKey(Key.Enter)
      pressKey(Key.Enter)
    }
    waitForIdle()

    val expandedHeight = onNodeWithTag("textfield").fetchSemanticsNode().boundsInRoot.height

    assertThat(state.text.toString()).isEqualTo("A\n\n\n")
    assertThat(lineCount).isEqualTo(4)
    assertThat(expandedHeight).isGreaterThan(initialHeight * 2f)
  }

  @Test
  fun onTextLayoutIsForwardedToEditable() = runComposeUiTest {
    var layoutText = ""
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState("layout"),
        modifier = Modifier.testTag("textfield"),
        onTextLayout = { getResult ->
          layoutText = getResult()?.layoutInput?.text?.text.orEmpty()
        },
      ) {
        Editable()
      }
    }

    waitForIdle()

    assertThat(layoutText).isEqualTo("layout")
  }
}

private val PasswordOutputTransformation = object : OutputTransformation {
  override fun TextFieldBuffer.transformOutput() {
    for (index in 0 until length) {
      replace(index, index + 1, "•")
    }
  }
}

private val CreditCardOutputTransformation = object : OutputTransformation {
  override fun TextFieldBuffer.transformOutput() {
    var index = 4
    while (index <= length) {
      replace(index, index, "-")
      index += 5
    }
  }
}
