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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
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
        TextInput()
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
        TextInput()
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
        TextInput()
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
        TextInput()
      }
    }

    onNodeWithText("••••••", useUnmergedTree = true).assertTextEquals("••••••")
  }

  @Test
  fun outputTransformationTransformsReadOnlyText() = runComposeUiTest {
    val state = TextFieldState("secret")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        readOnly = true,
        outputTransformation = PasswordOutputTransformation,
      ) {
        TextInput()
      }
    }

    onNodeWithText("••••••", useUnmergedTree = true).assertTextEquals("••••••")
  }

  @Test
  fun readOnlyTextFieldRendersTextAsSelectableContent() = runComposeUiTest {
    val state = TextFieldState("secret")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        readOnly = true,
      ) {
        TextInput()
      }
    }

    onNodeWithText("secret", useUnmergedTree = true).assertTextEquals("secret")
  }

  @Test
  fun providesSelectionColorsToTextInputContent() = runComposeUiTest {
    val selectionColors = TextSelectionColors(
      handleColor = Color.Red,
      backgroundColor = Color.Blue,
    )
    var providedSelectionColors: TextSelectionColors? = null

    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        selectionColors = selectionColors,
      ) {
        providedSelectionColors = LocalTextSelectionColors.current
        TextInput()
      }
    }

    waitForIdle()

    assertThat(providedSelectionColors).isEqualTo(selectionColors)
  }

  @Test
  fun usesUnspecifiedSelectionColorsByDefault() = runComposeUiTest {
    val parentSelectionColors = TextSelectionColors(
      handleColor = Color.Red,
      backgroundColor = Color.Blue,
    )
    var providedSelectionColors: TextSelectionColors? = null

    setContent {
      CompositionLocalProvider(
        LocalTextSelectionColors provides parentSelectionColors,
      ) {
        UnstyledTextField(
          state = rememberTextFieldState(),
        ) {
          providedSelectionColors = LocalTextSelectionColors.current
          TextInput()
        }
      }
    }

    waitForIdle()

    assertThat(providedSelectionColors).isEqualTo(
      TextSelectionColors(
        handleColor = Color.Unspecified,
        backgroundColor = Color.Unspecified,
      ),
    )
  }

  @Test
  fun disabledTextFieldExposesDisabledSemantics() = runComposeUiTest {
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier
          .testTag("textfield")
          .size(50.dp),
        enabled = false,
      ) {
        TextInput()
      }
    }

    onNodeWithTag("textfield").assertIsNotEnabled()
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
        TextInput()
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
        TextInput()
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
        TextInput(
          placeholder = {
            BasicText("Search", modifier = Modifier.testTag("placeholder"))
          },
        )
      }
    }

    onNodeWithTag("placeholder", useUnmergedTree = true).assertExists()
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
        TextInput(
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
        TextInput(
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
        TextInput()
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
        TextInput()
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
        TextInput()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("12")

    onNodeWithTag("textfield").assertTextEquals("(12)")
    assertThat(state.text.toString()).isEqualTo("12")
  }

  @Test
  fun lineLimitsAreForwardedToTextInput() = runComposeUiTest {
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
        TextInput()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("A\nB")
    waitForIdle()

    assertThat(lineCount).isEqualTo(1)
  }

  @Test
  fun onTextLayoutIsForwardedToTextInput() = runComposeUiTest {
    var layoutText = ""
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState("layout"),
        modifier = Modifier.testTag("textfield"),
        onTextLayout = { getResult ->
          layoutText = getResult()?.layoutInput?.text?.text.orEmpty()
        },
      ) {
        TextInput()
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
