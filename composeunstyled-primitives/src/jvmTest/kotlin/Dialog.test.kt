package com.composeunstyled

import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DialogTest {

    @Test
    fun isModal() = runComposeUiTest {
        val state = DialogState(initiallyVisible = false)
        setContent {
            Dialog(state) {
                DialogPanel {
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
            Dialog(state = dialogState) {
                DialogPanel(Modifier.testTag("dialog_content")) {
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
            Dialog(state = dialogState) {
                Scrim(Modifier.testTag("scrim"))
            }
        }

        onNodeWithTag("scrim").assertDoesNotExist()

        dialogState.visible = true

        onNodeWithTag("scrim").assertExists()
    }


    @Test
    fun autoFocusesOnDialog() = runComposeUiTest {
        setContent {
            Dialog(rememberDialogState(initiallyVisible = true)) {
                DialogPanel(Modifier.testTag("dialog_content").focusable()) {
                }
            }
        }

        onNodeWithTag("dialog_content").assertIsFocused()
    }
}
