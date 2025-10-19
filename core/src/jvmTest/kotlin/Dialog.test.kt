package com.composeunstyled

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogState
import com.composables.core.rememberDialogState
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class DialogTest {

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

    @Test
    fun dialogIsModal() = runComposeUiTest {
        var clickCount by mutableStateOf(0)
        lateinit var dialogState: DialogState

        setContent {
            dialogState = rememberDialogState(initiallyVisible = false)

            Button(
                onClick = { clickCount++ },
                modifier = Modifier
                    .testTag("button")
                    .size(40.dp)
            ) {

            }

            Dialog(dialogState) {
                DialogPanel {
                    Box(Modifier.testTag("dialog_content").size(40.dp))
                }
            }
        }

        onNodeWithTag("button").performClick()

        assertThat(clickCount).isEqualTo(1)

        dialogState.visible = true

        onNodeWithTag("button").performClick()

        assertThat(clickCount).isEqualTo(1)
    }
}