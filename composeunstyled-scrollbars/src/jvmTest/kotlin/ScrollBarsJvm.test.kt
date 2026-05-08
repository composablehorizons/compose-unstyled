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

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class ScrollBarsJvmTest {

  @Test
  fun hide_while_idle_does_not_hide_thumb_while_track_is_hovered() = runComposeUiTest {
    setContent {
      val scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("track"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb"),
            thumbVisibility = ThumbVisibility.HideWhileIdle(
              enter = EnterTransition.None,
              exit = ExitTransition.None,
              hideDelay = 5.seconds,
            ),
          )
        }
      }
    }

    onNodeWithTag("thumb").assertDoesNotExist()

    onNodeWithTag("track").performMouseInput { enter() }
    onNodeWithTag("track").performMouseInput { exit() }
    onNodeWithTag("thumb").assertDoesNotExist()

    onNodeWithTag("list").performTouchInput {
      swipeUp(durationMillis = 1_000)
    }
    onNodeWithTag("track").performMouseInput { enter() }
    onNodeWithTag("thumb").assertIsDisplayed()

    advanceTimeBy(6.seconds)
    onNodeWithTag("thumb").assertIsDisplayed()

    onNodeWithTag("track").performMouseInput { exit() }

    advanceTimeBy(6.seconds)
    waitForIdle()
    onNodeWithTag("thumb").assertDoesNotExist()
  }
}
