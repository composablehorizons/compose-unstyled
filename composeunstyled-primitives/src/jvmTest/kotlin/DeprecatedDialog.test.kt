@file:Suppress("DEPRECATION")

package com.composables.core

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.test.Test

class DeprecatedDialogTest {

    @Test
    fun initiallyVisibleDialogWithoutScrimCanBeDismissed() = runComposeUiTest {
        val dialogState = DialogState(initiallyVisible = true)

        setContent {
            Dialog(state = dialogState) {
                DialogPanel(Modifier.testTag("dialog_content")) {
                }
            }
        }

        onNodeWithTag("dialog_content").assertExists()

        dialogState.visible = false
        waitForIdle()

        onNodeWithTag("dialog_content").assertDoesNotExist()
        onNode(isDialog()).assertDoesNotExist()
    }
}
