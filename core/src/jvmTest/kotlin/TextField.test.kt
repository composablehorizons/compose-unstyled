package com.composables.core

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
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
                value = "",
                onValueChange = {},
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
                value = "",
                onValueChange = {},
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
        var text by mutableStateOf("")
        setContent {

            TextField(
                value = text,
                onValueChange = { text = it },
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
}
