package com.composables.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.composeunstyled.Button
import com.composeunstyled.Text
import com.composeunstyled.Tooltip
import com.composeunstyled.TooltipPanel
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TooltipTest {

    /**
     * We are specifying padding to prevent cases where the Tooltip ends up above the trigger
     */
    private fun ComposeUiTest.setPaddedContent(content: @Composable () -> Unit) {
        setContent {
            Box(Modifier.padding(100.dp)) {
                content()
            }
        }
    }

    @Test
    fun showTooltipOnLongPress() = runComposeUiTest {
        // Android/Touch device requirement: Tooltips should appear on long press
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("long_press_target")) {
                    Text("Long press me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Perform long press
        onNodeWithTag("long_press_target").performTouchInput {
            longClick()
        }
        onNodeWithTag("long_press_target").assertIsNotFocused()
        waitForIdle()

        // Tooltip should appear after long press
        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    @Test
    fun tooltipDismissesAfter1500ms() = runComposeUiTest {
        // Tooltip should automatically dismiss after 1500ms
        mainClock.autoAdvance = false

        setPaddedContent {
            Tooltip(
                longPressShowDurationMillis = 1500,
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("long_press_target")) {
                    Text("Long press me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Perform long press to show tooltip
        onNodeWithTag("long_press_target").performTouchInput {
            longClick()
        }
        mainClock.advanceTimeByFrame()
        waitForIdle()

        // Tooltip should be displayed
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // Wait for just before 1500ms - tooltip should still be visible
        mainClock.advanceTimeBy(1400)
        waitForIdle()
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // Wait past 1500ms to ensure dismissal
        mainClock.advanceTimeBy(200)
        waitForIdle()

        // Tooltip should be dismissed
        onNodeWithText("Tooltip content").assertDoesNotExist()
    }

}