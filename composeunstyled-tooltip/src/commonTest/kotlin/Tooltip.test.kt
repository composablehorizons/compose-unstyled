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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class TooltipCommonTest {

  fun ComposeUiTest.setPaddedContent(content: @Composable () -> Unit) {
    setContent {
      PortalHost {
        Box(Modifier.padding(100.dp)) {
          content()
        }
      }
    }
  }

  @Test
  fun panelWithoutTooltipStateIsHidden() = runComposeUiTest {
    setContent {
      with(FakeTooltipScope) {
        TooltipPanel {
          BasicText("Tooltip content")
        }
      }
    }

    onNodeWithText("Tooltip content").assertDoesNotExist()
  }

  @Test
  fun tooltipIsHiddenByDefault() = runComposeUiTest {
    setPaddedContent {
      UnstyledTooltip(
        panel = {
          TooltipPanel {
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

  @Test
  fun tooltipAppearsOnMouseHover() = runComposeUiTest {
    setPaddedContent {
      UnstyledTooltip(
        panel = {
          TooltipPanel {
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

    onNodeWithTag("hover_target").performMouseInput {
      enter(center)
    }
    waitForIdle()

    onNodeWithText("Tooltip content").assertIsDisplayed()
  }

  @Test
  fun exposesTooltipPlacementToPanelContent() = runComposeUiTest {
    var placement: TooltipPlacement? = null

    setPaddedContent {
      UnstyledTooltip(
        side = AnchorSide.Bottom,
        alignment = AnchorAlignment.End,
        panel = {
          TooltipPanel {
            placement = it
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("hover_target")) {
          BasicText("Hover me")
        }
      }
    }

    onNodeWithTag("hover_target").performHover()
    waitForIdle()

    onNodeWithText("Tooltip content").assertIsDisplayed()
    assertEquals(AnchorSide.Bottom, placement?.side)
    assertEquals(AnchorAlignment.End, placement?.alignment)
  }

  @Test
  fun tooltipDisappearsOnMouseOut() = runComposeUiTest {
    setPaddedContent {
      Row {
        UnstyledTooltip(
          panel = {
            TooltipPanel {
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
  fun tooltipWorksWithDisabledElements() = runComposeUiTest {
    setPaddedContent {
      UnstyledTooltip(
        panel = {
          TooltipPanel {
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

    onNodeWithText("Tooltip for disabled element").assertDoesNotExist()

    onNodeWithTag("disabled_button").performHover()
    waitForIdle()

    onNodeWithText("Tooltip for disabled element").assertIsDisplayed()
  }

  @Test
  fun tooltipAppearsAfterHoverDelay() = runComposeUiTest {
    mainClock.autoAdvance = false

    setPaddedContent {
      Row {
        UnstyledTooltip(
          hoverDelayMillis = 500,
          panel = {
            TooltipPanel {
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

    onNodeWithTag("hover_target").performMouseInput {
      enter(center)
    }
    mainClock.advanceTimeByFrame()
    waitForIdle()

    onNodeWithText("Tooltip content").assertDoesNotExist()

    mainClock.advanceTimeBy(400)
    waitForIdle()

    onNodeWithText("Tooltip content").assertDoesNotExist()

    mainClock.advanceTimeBy(200)
    waitForIdle()

    onNodeWithText("Tooltip content").assertIsDisplayed()
  }

  @Test
  fun tooltipDoesNotAppearIfHoverEndsBeforeDelay() = runComposeUiTest {
    mainClock.autoAdvance = false

    setPaddedContent {
      Row {
        UnstyledTooltip(
          hoverDelayMillis = 500,
          panel = {
            TooltipPanel {
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

    mainClock.advanceTimeBy(300)
    waitForIdle()

    onNodeWithTag("hover_target").performMouseInput {
      exit()
    }
    mainClock.advanceTimeByFrame()
    waitForIdle()

    mainClock.advanceTimeBy(300)
    waitForIdle()

    onNodeWithText("Tooltip content").assertDoesNotExist()
  }

  private fun SemanticsNodeInteraction.performHover() = performMouseInput {
    enter(center)
  }

  @Composable
  private fun UnstyledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
  ) {
    Box(
      modifier = modifier then buildModifier {
        if (enabled) {
          add(Modifier.clickable(onClick = onClick))
        }
        add(Modifier.padding(8.dp))
      },
    ) {
      content()
    }
  }
}

private object FakeTooltipScope : TooltipScope
