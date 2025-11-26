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
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.focus.onFocusChanged
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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

    @Test
    fun givenTextColor_thenColorIsProvidedAsLocalContentColor() = runComposeUiTest {
        val expectedColor = Color.Blue
        var actualColor: Color? = null

        setContent {
            TextField(
                state = rememberTextFieldState(),
                textColor = expectedColor
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
            TextField(
                state = rememberTextFieldState(),
                modifier = Modifier
                    .testTag("textfield")
                    .onFocusChanged { focusState ->
                        focused = focusState.isFocused
                    }
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
            TextField(
                state = rememberTextFieldState(),
                modifier = Modifier.testTag("textfield"),
                interactionSource = interactionSource
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
}
