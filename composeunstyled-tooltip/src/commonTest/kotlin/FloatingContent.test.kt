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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertNotEquals

class FloatingContentTest {

  @Test
  fun rendersNothingWhenNoAnchor() = runComposeUiTest {
    // FloatingContent should not render floating content when there's no anchor
    setContent {
      FloatingContent(
        floatingContent = {
          BasicText("Floating content")
        },
        anchor = {},
      )
    }

    waitForIdle()
    onNodeWithText("Floating content").assertDoesNotExist()
  }

  @Test
  fun rendersOnlyAnchorWhenNoFloatingContent() = runComposeUiTest {
    // FloatingContent should render only the anchor when there's no floating content
    setContent {
      FloatingContent(
        floatingContent = {},
        anchor = {
          BasicText("Anchor")
        },
      )
    }

    waitForIdle()
    onNodeWithText("Anchor").assertExists()
  }

  @Test
  fun placementChangesFloatingContentPosition() = runComposeUiTest {
    // Test that different placements result in different positions
    var placement by mutableStateOf<RelativeAlignment>(RelativeAlignment.TopStart)
    var topStartY = 0f
    var bottomStartY = 0f

    setContent {
      FloatingContent(
        placement = placement,
        floatingContent = {
          Box(Modifier.size(50.dp)) {
            BasicText("Floating")
          }
        },
        anchor = {
          Box(Modifier.size(100.dp)) {
            BasicText("Anchor")
          }
        },
      )
    }

    waitForIdle()
    topStartY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

    placement = RelativeAlignment.BottomStart
    waitForIdle()
    bottomStartY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

    // The Y positions should be different when placement changes
    assertNotEquals(
      topStartY,
      bottomStartY,
      "Floating content position should change when placement changes",
    )
  }

  @Test
  fun floatingContentClampsToWindowBounds() = runComposeUiTest {
    // FloatingContent clamps to window bounds like a Popup
    setContent {
      FloatingContent(
        placement = RelativeAlignment.TopStart,
        floatingContent = {
          Box(Modifier.size(100.dp)) {
            BasicText("Floating")
          }
        },
        anchor = {
          Box(Modifier.size(50.dp)) {
            BasicText("Anchor")
          }
        },
      )
    }

    waitForIdle()

    val floatingY = onNodeWithText("Floating").fetchSemanticsNode().positionInWindow.y

    // With TopStart placement at the top of the window,
    // floating content should be clamped to not go negative
    assert(floatingY >= 0f) {
      "Floating content should be clamped to window bounds (floatingY=$floatingY should be >= 0)"
    }
  }

  @Test
  fun floatingContentLargerThanWindow() = runComposeUiTest {
    // When floating content is larger than the window,
    // it should be clamped to start at 0 to maximize visible area
    setContent {
      FloatingContent(
        placement = RelativeAlignment.BottomStart,
        floatingContent = {
          // Make floating content very large (larger than typical window)
          Box(Modifier.size(10000.dp)) {
            BasicText("Large floating")
          }
        },
        anchor = {
          Box(Modifier.size(50.dp)) {
            BasicText("Anchor")
          }
        },
      )
    }

    waitForIdle()

    val floatingNode = onNodeWithText("Large floating").fetchSemanticsNode()
    val floatingX = floatingNode.positionInWindow.x
    val floatingY = floatingNode.positionInWindow.y

    // When content is larger than window, it should be clamped to 0
    // to maximize the visible portion at the top-left
    assert(floatingX >= 0f) {
      "Large floating content should be clamped to x >= 0 (got x=$floatingX)"
    }
    assert(floatingY >= 0f) {
      "Large floating content should be clamped to y >= 0 (got y=$floatingY)"
    }
  }
}
