package com.composables.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.Modal
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat

@OptIn(ExperimentalTestApi::class)
class ModalTest {

    @Test
    fun modalInComposition_blocksInteractionsWithBelowLayer() = runComposeUiTest {
        var showModal by mutableStateOf(false)
        var clickCount by mutableStateOf(0)

        setContent {
            Button(
                onClick = { clickCount++ },
                modifier = Modifier
                    .testTag("button")
                    .size(40.dp)
            ) {

            }

            if (showModal) {
                Modal(onKeyEvent = { false }) {
                    Box(Modifier.testTag("modal_contents").size(40.dp))
                }
            }
        }

        onNodeWithTag("button").performClick()

        assertThat(clickCount).isEqualTo(1)

        showModal = true

        onNodeWithTag("button").performClick()

        assertThat(clickCount).isEqualTo(1)
    }
}
