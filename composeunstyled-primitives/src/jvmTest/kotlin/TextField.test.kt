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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
  fun keepsFocusWhenTextFieldIsFocusedAndTrailingIsSpecified() = runComposeUiTest {
    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        modifier = Modifier.testTag("textfield"),
      ) {
        TextInput(
          trailing = {
            UnstyledButton(onClick = {}, modifier = Modifier.testTag("trailing")) {
              Text("trailing")
            }
          },
        )
      }
    }

    onNodeWithTag("textfield").requestFocus()
    onNodeWithTag("textfield", useUnmergedTree = true).assertIsFocused()
    onNodeWithTag("trailing", useUnmergedTree = true).assertIsNotFocused()
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
  fun visualTransformationTransformText() = runComposeUiTest {
    val state = TextFieldState("secret")
    var visualTransformation by mutableStateOf(VisualTransformation.None)
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        visualTransformation = visualTransformation,
      ) {
        TextInput()
      }
    }

    onNodeWithTag("textfield").assertTextEquals("secret")

    visualTransformation = PasswordVisualTransformation()

    onNodeWithText("••••••", useUnmergedTree = true).assertTextEquals("••••••")
  }

  @Test
  fun visualTransformationTransformText_whenNotEditable() = runComposeUiTest {
    val state = TextFieldState("secret")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield"),
        editable = false,
        visualTransformation = PasswordVisualTransformation(),
      ) {
        TextInput()
      }
    }

    onNodeWithText("••••••", useUnmergedTree = true).assertTextEquals("••••••")
  }

  @Test
  fun givenTextColor_thenColorIsProvidedAsLocalContentColor() = runComposeUiTest {
    val expectedColor = Color.Blue
    var actualColor: Color? = null

    setContent {
      UnstyledTextField(
        state = rememberTextFieldState(),
        textColor = expectedColor,
      ) {
        actualColor = LocalContentColor.current
      }
    }

    waitForIdle()
    assertEquals(expectedColor, actualColor)
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
    assertTrue(focused, "Focus should be detected via Modifier.onFocusChanged")
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
          Text("focused", modifier = Modifier.testTag("focus-indicator"))
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
            Text("Search", modifier = Modifier.testTag("placeholder"))
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
            Text("Search", modifier = Modifier.testTag("placeholder"))
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
            Text("Search", modifier = Modifier.testTag("placeholder"))
          },
        )
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput(" ")
    onNodeWithTag("placeholder", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun visualTransformationWithLongerOutputWorksWithStateBasedTextField() = runComposeUiTest {
    val state = TextFieldState("")
    setContent {
      UnstyledTextField(
        state = state,
        modifier = Modifier.testTag("textfield").size(120.dp),
        visualTransformation = CreditCardVisualTransformation(),
      ) {
        TextInput()
      }
    }

    onNodeWithTag("textfield").performClick()
    onNodeWithTag("textfield").performTextInput("1234")
    onNodeWithTag("textfield").assertTextEquals("1234-")
  }
}

private class CreditCardVisualTransformation : VisualTransformation {
  override fun filter(text: AnnotatedString): TransformedText {
    var out = ""
    for (i in text.text.indices) {
      out += text.text[i]
      if (i % 4 == 3 && i != 15) {
        out += "-"
      }
    }
    val creditCardOffsetTranslator = object : OffsetMapping {
      override fun originalToTransformed(offset: Int): Int {
        if (offset <= 3) return offset
        if (offset <= 7) return offset + 1
        if (offset <= 11) return offset + 2
        if (offset <= 16) return offset + 3
        return 19
      }

      override fun transformedToOriginal(offset: Int): Int {
        if (offset <= 4) return offset
        if (offset <= 9) return offset - 1
        if (offset <= 14) return offset - 2
        if (offset <= 19) return offset - 3
        return 16
      }
    }

    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
  }
}
