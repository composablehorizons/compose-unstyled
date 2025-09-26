package com.composeunstyled

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.rememberDialogState
import kotlin.test.Test

class DialogTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun focusesContentWhenDisplayed() = runComposeUiTest {
        setContent {
            Dialog(rememberDialogState(initiallyVisible = true)) {
                DialogPanel {
                    Box(Modifier.testTag("dialog_content").focusable())
                }
            }
        }

        onNodeWithTag("dialog_content").assertIsFocused()
    }
}