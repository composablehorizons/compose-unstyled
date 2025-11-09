package com.composeunstyled

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.Text
import com.composeunstyled.TextField
import com.composeunstyled.TextInput
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TextFieldTest {
    @Test
    fun movesFocusToInputWhenTextFieldIsFocused() = runComposeUiTest {
        setContent {
            TextField(
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
            TextField(
                state = rememberTextFieldState(),
                modifier = Modifier.testTag("textfield"),
            ) {
                TextInput(
                    trailing = {
                        Button(onClick = {}, modifier = Modifier.testTag("trailing")) {
                            Text("trailing")
                        }
                    }
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
            TextField(
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
            TextField(
                state = state,
                modifier = Modifier.testTag("textfield")
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
            TextField(
                state = state,
                modifier = Modifier.testTag("textfield"),
                visualTransformation = visualTransformation
            ) {
                TextInput()
            }
        }

        onNodeWithTag("textfield").assertTextEquals("secret")

        visualTransformation = PasswordVisualTransformation()

        onNodeWithTag("textfield").assertTextEquals("••••••")
    }
}
