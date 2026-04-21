package com.composeunstyled

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DialogTest {

    @Test
    fun isModal() = runComposeUiTest {
        val state = DialogState(initiallyVisible = false)
        setContent {
            UnstyledDialog(state) {
                UnstyledDialogPanel {
                }
            }
        }
        onNode(isDialog()).assertDoesNotExist()
        state.visible = true
        onNode(isDialog()).assertExists()
    }

    @Test
    fun visibleDialogShowsThePanel() = runComposeUiTest {
        val dialogState = DialogState(initiallyVisible = false)

        setContent {
            UnstyledDialog(state = dialogState) {
                UnstyledDialogPanel(Modifier.testTag("dialog_content")) {
                }
            }
        }

        onNodeWithTag("dialog_content").assertDoesNotExist()

        dialogState.visible = true

        onNodeWithTag("dialog_content").assertExists()
    }

    @Test
    fun visibleDialogShowsTheScrim() = runComposeUiTest {
        val dialogState = DialogState(initiallyVisible = false)

        setContent {
            UnstyledDialog(state = dialogState) {
                UnstyledScrim(Modifier.testTag("scrim"))
            }
        }

        onNodeWithTag("scrim").assertDoesNotExist()

        dialogState.visible = true

        onNodeWithTag("scrim").assertExists()
    }


    @Test
    fun autoFocusesOnDialog() = runComposeUiTest {
        setContent {
            UnstyledDialog(rememberDialogState(initiallyVisible = true)) {
                UnstyledDialogPanel(Modifier.testTag("dialog_content")) {
                    BasicTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.testTag("dialog_focus_target")
                    )
                }
            }
        }

        onNodeWithTag("dialog_focus_target").assertIsFocused()
    }

    @Test
    fun pressingEscapeDismissesDialogWhenDismissOnBackPressIsTrue() = runComposeUiTest {
        val dialogState = DialogState(initiallyVisible = true)

        setContent {
            var value by remember { mutableStateOf("") }
            UnstyledDialog(
                state = dialogState,
                properties = DialogProperties(dismissOnBackPress = true)
            ) {
                UnstyledDialogPanel(Modifier.testTag("dialog_content")) {
                    BasicTextField(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.testTag("dialog_input")
                    )
                }
            }
        }

        onNodeWithTag("dialog_content").assertExists()
        onNodeWithTag("dialog_input").performClick()
        onNodeWithTag("dialog_input").assertIsFocused()

        onNodeWithTag("dialog_input").performKeyInput {
            pressKey(Key.Escape)
        }
        waitForIdle()

        onNodeWithTag("dialog_content").assertDoesNotExist()
    }

    @Test
    fun pressingEscapeDoesNotDismissDialogWhenDismissOnBackPressIsFalse() = runComposeUiTest {
        val dialogState = DialogState(initiallyVisible = true)

        setContent {
            var value by remember { mutableStateOf("") }
            UnstyledDialog(
                state = dialogState,
                properties = DialogProperties(dismissOnBackPress = false)
            ) {
                UnstyledDialogPanel(Modifier.testTag("dialog_content")) {
                    BasicTextField(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier.testTag("dialog_input")
                    )
                }
            }
        }

        onNodeWithTag("dialog_content").assertExists()
        onNodeWithTag("dialog_input").performClick()
        onNodeWithTag("dialog_input").assertIsFocused()

        onNodeWithTag("dialog_input").performKeyInput {
            pressKey(Key.Escape)
        }
        waitForIdle()

        onNodeWithTag("dialog_content").assertExists()
    }
}
