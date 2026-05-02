/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.composeunstyled

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

class TooltipJvmTest {

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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Button")
        }
      }
    }

    onNodeWithText("Tooltip content").assertDoesNotExist()
  }

  // Keyboard Interaction Tests
  @Test
  fun tooltipAppearsOnKeyboardFocus() = runComposeUiTest {
    setPaddedContent {
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Focus me")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Focus me")
        }
      }

      UnstyledButton(onClick = {}) {
        BasicText("Another button")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("hover_target")) {
          BasicText("Hover me")
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
      UnstyledTooltip(
        enabled = false,
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger")) {
          BasicText("Hover me")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Focus me")
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
        UnstyledTooltip(
          panel = {
            UnstyledTooltipPanel {
              BasicText("Tooltip content")
            }
          },
        ) {
          UnstyledButton(onClick = {}) {
            BasicText("Hover me")
          }
        }

        UnstyledButton(onClick = {}) {
          BasicText("Other")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger")) {
          BasicText("Trigger")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            Box(Modifier.testTag("tooltip_content")) {
              BasicText("Tooltip content")
            }
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger_button")) {
          BasicText("Trigger")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Trigger")
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
    // Test that UnstyledTooltipPanel accepts placement parameter
    setPaddedContent {
      UnstyledTooltip(
        placement = RelativeAlignment.BottomStart,
        panel = {
          UnstyledTooltipPanel {
            BasicText("Bottom tooltip")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Trigger")
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
      UnstyledButton(onClick = {}, modifier = Modifier.testTag("outside_button")) {
        BasicText("Outside button")
      }

      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}) {
          BasicText("Trigger")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip for disabled element")
          }
        },
      ) {
        UnstyledButton(
          onClick = {},
          enabled = false,
          modifier = Modifier.testTag("disabled_button"),
        ) {
          BasicText("Disabled button")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger")) {
          BasicText("Trigger")
        }
      }

      UnstyledButton(onClick = {}) {
        BasicText("Outside button")
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

    onNodeWithTag("trigger").assertIsNotFocused()
  }

  @Test
  fun tooltipAppearsAfterHoverDelay() = runComposeUiTest {
    // Tooltip should appear after hover delay duration
    mainClock.autoAdvance = false

    setPaddedContent {
      Row {
        UnstyledTooltip(
          hoverDelayMillis = 500,
          panel = {
            UnstyledTooltipPanel {
              BasicText("Tooltip content")
            }
          },
        ) {
          UnstyledButton(onClick = {}, modifier = Modifier.testTag("hover_target")) {
            BasicText("Hover me")
          }
        }

        UnstyledButton(onClick = {}, modifier = Modifier.testTag("outside_target")) {
          BasicText("Outside")
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
      Row {
        UnstyledTooltip(
          hoverDelayMillis = 500,
          panel = {
            UnstyledTooltipPanel {
              BasicText("Tooltip content")
            }
          },
        ) {
          UnstyledButton(onClick = {}, modifier = Modifier.testTag("hover_target")) {
            BasicText("Hover me")
          }
        }

        UnstyledButton(onClick = {}, modifier = Modifier.testTag("outside_target")) {
          BasicText("Outside")
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
  fun escapeDoesNotCancelPendingTooltipWhenTooltipIsStillHidden() = runComposeUiTest {
    mainClock.autoAdvance = false

    setPaddedContent {
      Row {
        UnstyledTooltip(
          hoverDelayMillis = 500,
          panel = {
            UnstyledTooltipPanel {
              BasicText("Tooltip content")
            }
          },
        ) {
          UnstyledButton(onClick = {}, modifier = Modifier.testTag("hover_target")) {
            BasicText("Hover me")
          }
        }

        UnstyledButton(onClick = {}, modifier = Modifier.testTag("outside_target")) {
          BasicText("Outside")
        }
      }
    }

    onNodeWithTag("hover_target").performMouseInput {
      enter(center)
    }
    mainClock.advanceTimeByFrame()
    waitForIdle()

    onNodeWithText("Tooltip content").assertDoesNotExist()

    onNodeWithTag("outside_target").requestFocus()
    waitForIdle()
    onNodeWithTag("outside_target").assertIsFocused()

    // ESC should not be consumed by hidden tooltips.
    onNodeWithTag("outside_target").performKeyInput {
      pressKey(Key.Escape)
    }
    waitForIdle()

    mainClock.advanceTimeBy(600)
    waitForIdle()

    onNodeWithText("Tooltip content").assertIsDisplayed()
  }

  @Test
  fun tooltipAppearsImmediatelyOnFocusRegardlessOfDelay() = runComposeUiTest {
    // Focus should show tooltip immediately, even with hover delay configured
    setPaddedContent {
      UnstyledTooltip(
        hoverDelayMillis = 500,
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger")) {
          BasicText("Focus me")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger")) {
          BasicText("Trigger")
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
      UnstyledTooltip(
        panel = {
          UnstyledTooltipPanel(arrow = { BasicText("Arrow") }) {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("trigger")) {
          BasicText("Trigger")
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

  @Composable
  private fun UnstyledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
  ) {
    Box(
      modifier = modifier
        .then(if (enabled) Modifier.clickable(onClick = onClick).focusable() else Modifier)
        .padding(8.dp),
    ) {
      content()
    }
  }
}
