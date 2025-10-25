package com.composeunstyled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composeunstyled.*
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TooltipTest {

    /**
     * We are specifying padding to prevent cases where the Tooltip ends up above the trigger
     */
    fun ComposeUiTest.setPaddedContent(content: @Composable () -> Unit) {
        setContent {
            Box(Modifier.padding(100.dp)) {
                content()
            }
        }
    }


    // Basic Visibility Tests
    @Test
    fun tooltipIsHiddenByDefault() = runComposeUiTest {
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}) {
                    Text("Button")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()
    }

    // Keyboard Interaction Tests
    @Test
    fun tooltipAppearsOnKeyboardFocus() = runComposeUiTest {
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}) {
                    Text("Focus me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        onNodeWithText("Focus me").requestFocus()
        waitForIdle()

        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    @Test
    fun tooltipDisappearsWhenFocusIsLost() = runComposeUiTest {
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}) {
                    Text("Focus me")
                }
            }

            Button(onClick = {}) {
                Text("Another button")
            }
        }

        onNodeWithText("Focus me").requestFocus()
        waitForIdle()

        onNodeWithText("Tooltip content").assertIsDisplayed()

        onNodeWithText("Another button").requestFocus()
        waitForIdle()

        onNodeWithText("Tooltip content").assertDoesNotExist()
    }

    @Test
    fun tooltipAppearsOnMouseHover() = runComposeUiTest {
        // W3C requirement: Tooltip should appear on mouse hover
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("hover_target")) {
                    Text("Hover me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        println("performMouseInput enter")

        onNodeWithTag("hover_target").performMouseInput {
            enter(center)
        }
        waitForIdle()

        println("Asserting")
        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    @Test
    fun tooltipIsNotDisplayedWhenEnabledIsSetToFalse() = runComposeUiTest {
        setPaddedContent {
            Tooltip(
                enabled = false,
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger")) {
                    Text("Hover me")
                }
            }
        }

        // Try to trigger tooltip with hover
        onNodeWithTag("trigger").performHover()
        waitForIdle()

        // Tooltip should not appear when disabled
        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Try to trigger tooltip with focus
        onNodeWithTag("trigger").requestFocus()
        waitForIdle()

        // Tooltip should still not appear when disabled
        onNodeWithText("Tooltip content").assertDoesNotExist()
    }


    @Test
    fun edgeCaseTooltipOnTopOnTarget() = runComposeUiTest {
        setContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                },
            ) {
                Button(onClick = {}) {
                    Text("Focus me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        onNodeWithText("Focus me").performHover()
        waitForIdle()

        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    @Test
    fun tooltipDisappearsOnMouseOut() = runComposeUiTest {
        // W3C requirement: Tooltip should disappear on mouse out
        setPaddedContent {
            Row {
                Tooltip(
                    panel = {
                        TooltipPanel {
                            Text("Tooltip content")
                        }
                    },
                ) {
                    Button(onClick = {}) {
                        Text("Hover me")
                    }
                }

                Button(onClick = {}) {
                    Text("Other")
                }
            }
        }

        onNodeWithText("Hover me").performMouseInput {
            enter(center)
        }

        onNodeWithText("Tooltip content").assertIsDisplayed()

        onNodeWithText("Hover me").performMouseInput {
            exit()
        }

        onNodeWithText("Other").performMouseInput {
            enter(center)
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()
    }

    @Test
    fun tooltipRemainsVisibleWhenBothHoveredAndFocused() = runComposeUiTest {
        // W3C requirement: Tooltip should remain visible when both hovered and focused
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger")) {
                    Text("Trigger")
                }
            }
        }

        // Focus the trigger
        onNodeWithTag("trigger").requestFocus()
        waitForIdle()
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // Also hover over it
        onNodeWithTag("trigger").performMouseInput {
            enter(center)
        }
        waitForIdle()

        // Tooltip should still be visible
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // Remove hover but keep focus
        onNodeWithTag("trigger").performMouseInput {
            exit()
        }
        waitForIdle()

        // Tooltip should remain visible due to focus
        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    // Focus Behavior Tests
    @Test
    fun tooltipDoesNotReceiveFocus() = runComposeUiTest {
        // W3C requirement: Tooltips themselves do not receive focus
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Box(Modifier.testTag("tooltip_content")) {
                            Text("Tooltip content")
                        }
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger_button")) {
                    Text("Trigger")
                }
            }
        }

        onNodeWithTag("trigger_button").requestFocus()
        waitForIdle()

        onNodeWithText("Tooltip content").assertIsDisplayed()
        onNodeWithTag("trigger_button").assertIsFocused()
        // Tooltip content should not be focused
    }

    @Test
    fun tooltipPositionsRelativeToTrigger() = runComposeUiTest {
        // Test that tooltip is displayed and positioned via PopoverPanel
        // Detailed positioning tests would require layout coordinate assertions
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}) {
                    Text("Trigger")
                }
            }
        }

        onNodeWithText("Trigger").requestFocus()
        waitForIdle()

        // Verify tooltip is displayed (positioning is handled by PopoverPanel)
        onNodeWithText("Tooltip content").assertIsDisplayed()
        onAllNodes(isPopup()).assertCountEquals(1)
    }

    @Test
    fun tooltipSupportsCustomPlacement() = runComposeUiTest {
        // Test that TooltipPanel accepts placement parameter
        setPaddedContent {
            Tooltip(
                placement = RelativeAlignment.BottomStart,
                panel = {
                    TooltipPanel {
                        Text("Bottom tooltip")
                    }
                }
            ) {
                Button(onClick = {}) {
                    Text("Trigger")
                }
            }
        }

        onNodeWithText("Trigger").requestFocus()
        waitForIdle()

        onNodeWithText("Bottom tooltip").assertIsDisplayed()
    }

    @Test
    fun tooltipIsNonModal() = runComposeUiTest {
        // W3C/NN/g guideline: Tooltips are informative and non-blocking
        // Users should be able to interact with other elements while tooltip is visible
        setPaddedContent {
            Button(onClick = {}, modifier = Modifier.testTag("outside_button")) {
                Text("Outside button")
            }

            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}) {
                    Text("Trigger")
                }
            }
        }

        onNodeWithText("Trigger").performHover()
        waitForIdle()
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // Should be able to click outside button while tooltip is visible
        onNodeWithTag("outside_button").requestFocus()
        waitForIdle()

        // Tooltip should remain visible (non-modal behavior)
        onNodeWithText("Tooltip content").assertIsDisplayed()
        onNodeWithTag("outside_button").assertIsFocused()
    }

    private fun SemanticsNodeInteraction.performHover() = performMouseInput {
        enter(center)
    }

    @Test
    fun tooltipWorksWithDisabledElements() = runComposeUiTest {
        // NN/g guideline: Tooltips are especially useful for disabled/unlabeled elements
        // Disabled elements cannot receive focus, so this test requires hover simulation
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip for disabled element")
                    }
                }
            ) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.testTag("disabled_button")
                ) {
                    Text("Disabled button")
                }
            }
        }

        // Disabled button cannot receive focus
        onNodeWithText("Tooltip for disabled element").assertDoesNotExist()

        // But hovering should still show tooltip
        onNodeWithTag("disabled_button").performHover()
        waitForIdle()

        onNodeWithText("Tooltip for disabled element").assertIsDisplayed()
    }

    @Test
    fun tooltipContentCannotBeFocused() = runComposeUiTest {
        // W3C note: For tooltips containing focusable elements, use a dialog instead
        // This test verifies that tooltip content doesn't trap or receive focus
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger")) {
                    Text("Trigger")
                }
            }

            Button(onClick = {}) {
                Text("Outside button")
            }
        }

        onNodeWithTag("trigger").requestFocus()
        waitForIdle()

        onNodeWithText("Tooltip content").assertIsDisplayed()

        // Tab should move focus away from trigger, not into tooltip
        onNodeWithTag("trigger").performKeyInput {
            pressKey(Key.Tab)
        }
        waitForIdle()

        onNodeWithText("Outside button").assertIsFocused()
    }

    @Test
    fun tooltipAppearsAfterHoverDelay() = runComposeUiTest {
        // Tooltip should appear after hover delay duration
        mainClock.autoAdvance = false

        setPaddedContent {
            Tooltip(
                hoverDelayMillis = 500,
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("hover_target")) {
                    Text("Hover me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Start hovering
        onNodeWithTag("hover_target").performMouseInput {
            enter(center)
        }
        mainClock.advanceTimeByFrame()
        waitForIdle()

        // Tooltip should not appear immediately
        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Advance time by less than delay
        mainClock.advanceTimeBy(400)
        waitForIdle()

        // Tooltip should still not appear
        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Advance time past delay
        mainClock.advanceTimeBy(200)
        waitForIdle()

        // Tooltip should now appear
        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    @Test
    fun tooltipDoesNotAppearIfHoverEndsBeforeDelay() = runComposeUiTest {
        // Tooltip should not appear if mouse leaves before delay completes
        mainClock.autoAdvance = false

        setPaddedContent {
            Tooltip(
                hoverDelayMillis = 500,
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("hover_target")) {
                    Text("Hover me")
                }
            }
        }

        // Start hovering
        onNodeWithTag("hover_target").performMouseInput {
            enter(center)
        }
        mainClock.advanceTimeByFrame()
        waitForIdle()

        // Advance time by less than delay
        mainClock.advanceTimeBy(300)
        waitForIdle()

        // Stop hovering before delay completes
        onNodeWithTag("hover_target").performMouseInput {
            exit()
        }
        mainClock.advanceTimeByFrame()
        waitForIdle()

        // Advance time past the original delay
        mainClock.advanceTimeBy(300)
        waitForIdle()

        // Tooltip should not appear because hover ended early
        onNodeWithText("Tooltip content").assertDoesNotExist()
    }

    @Test
    fun tooltipAppearsImmediatelyOnFocusRegardlessOfDelay() = runComposeUiTest {
        // Focus should show tooltip immediately, even with hover delay configured
        setPaddedContent {
            Tooltip(
                hoverDelayMillis = 500,
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger")) {
                    Text("Focus me")
                }
            }
        }

        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Focus should show tooltip immediately
        onNodeWithTag("trigger").requestFocus()
        waitForIdle()

        // Tooltip should appear immediately without delay
        onNodeWithText("Tooltip content").assertIsDisplayed()
    }

    @Test
    fun announcesTooltipContentWhenShown() = runComposeUiTest {
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger")) {
                    Text("Trigger")
                }
            }
        }

        // When hidden, tooltip content should not exist
        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Show tooltip
        onNodeWithTag("trigger").requestFocus()
        waitForIdle()

        // Tooltip should be visible
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // When shown, verify tooltip has live region with assertive politeness
        // The live region is set on the AnimatedVisibility root, which has the popup semantic
        val tooltipNode = onAllNodes(isPopup()).onFirst()
        val tooltipSemanticsNode = tooltipNode.fetchSemanticsNode()
        val hasLiveRegion = SemanticsProperties.LiveRegion in tooltipSemanticsNode.config
        assert(hasLiveRegion) { "Expected LiveRegion to be set" }

        val liveRegion = tooltipSemanticsNode.config[SemanticsProperties.LiveRegion]
        assert(liveRegion == LiveRegionMode.Assertive) {
            "Expected LiveRegionMode.Assertive but got $liveRegion"
        }
    }

    @Test
    fun announcesTooltipWithArrowContentWhenShown() = runComposeUiTest {
        setPaddedContent {
            Tooltip(
                panel = {
                    TooltipPanel(arrow = { Text("Arrow") }) {
                        Text("Tooltip content")
                    }
                }
            ) {
                Button(onClick = {}, modifier = Modifier.testTag("trigger")) {
                    Text("Trigger")
                }
            }
        }

        // When hidden, tooltip content should not exist
        onNodeWithText("Tooltip content").assertDoesNotExist()

        // Show tooltip
        onNodeWithTag("trigger").requestFocus()
        waitForIdle()

        // Tooltip should be visible
        onNodeWithText("Tooltip content").assertIsDisplayed()

        // When shown, verify tooltip has live region with assertive politeness
        // The live region is set on the AnimatedVisibility root, which has the popup semantic
        val tooltipNode = onAllNodes(isPopup()).onFirst()
        val tooltipSemanticsNode = tooltipNode.fetchSemanticsNode()
        val hasLiveRegion = SemanticsProperties.LiveRegion in tooltipSemanticsNode.config
        assert(hasLiveRegion) { "Expected LiveRegion to be set" }

        val liveRegion = tooltipSemanticsNode.config[SemanticsProperties.LiveRegion]
        assert(liveRegion == LiveRegionMode.Assertive) {
            "Expected LiveRegionMode.Assertive but got $liveRegion"
        }
    }
}