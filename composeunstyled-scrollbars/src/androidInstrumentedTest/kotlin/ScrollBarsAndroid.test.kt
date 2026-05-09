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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ScrollBarsAndroidTest {

  @Test
  fun touch_does_not_drag_thumb_before_long_press() = runComposeUiTest {
    lateinit var scrollState: ScrollState

    setContent {
      scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb"),
            thumbVisibility = ThumbVisibility.AlwaysVisible,
          )
        }
      }
    }

    onNodeWithTag("scrollbar").performTouchInput {
      down(Offset(4f, 8f))
      moveBy(Offset(0f, 100f))
      up()
    }

    waitForIdle()
    assertEquals(0, scrollState.value)
  }

  @Test
  fun touch_can_long_press_visible_thumb_and_drag_to_scroll() = runComposeUiTest {
    lateinit var scrollState: ScrollState

    setContent {
      scrollState = rememberScrollState()
      val scrollAreaState = rememberScrollAreaState(scrollState)
      ScrollArea(state = scrollAreaState) {
        Column(modifier = Modifier.height(200.dp).verticalScroll(scrollState).testTag("list")) {
          repeat(100) { index ->
            BasicText("Item $index")
          }
        }
        UnstyledVerticalScrollbar(
          scrollAreaState = scrollAreaState,
          modifier = Modifier.testTag("scrollbar"),
        ) {
          Thumb(
            modifier = Modifier.testTag("thumb"),
            thumbVisibility = ThumbVisibility.AlwaysVisible,
          )
        }
      }
    }

    onNodeWithTag("scrollbar").performTouchInput {
      down(Offset(4f, 8f))
      advanceEventTime(600)
      moveBy(Offset(0f, 100f))
      up()
    }

    waitForIdle()
    assertTrue(scrollState.value > 0)
  }
}
