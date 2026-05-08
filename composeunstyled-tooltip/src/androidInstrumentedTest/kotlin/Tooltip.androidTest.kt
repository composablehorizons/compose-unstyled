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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

class TooltipTest {

  private fun ComposeUiTest.setPaddedContent(content: @Composable () -> Unit) {
    setContent {
      OverlayHost {
        Box(Modifier.padding(100.dp)) {
          content()
        }
      }
    }
  }

  @Test
  fun showTooltipOnLongPress() = runComposeUiTest {
    // Android/Touch device requirement: Tooltips should appear on long press
    setPaddedContent {
      UnstyledTooltip(
        panel = {
          TooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("long_press_target")) {
          BasicText("Long press me")
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
      UnstyledTooltip(
        longPressShowDurationMillis = 1500,
        panel = {
          TooltipPanel {
            BasicText("Tooltip content")
          }
        },
      ) {
        UnstyledButton(onClick = {}, modifier = Modifier.testTag("long_press_target")) {
          BasicText("Long press me")
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
