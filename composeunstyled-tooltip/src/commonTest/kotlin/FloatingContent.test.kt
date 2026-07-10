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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotEqualTo
import kotlin.math.roundToInt
import kotlin.test.Test

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
  fun rendersNoFloatingContentWithoutTooltipHost() = runComposeUiTest {
    setContent {
      FloatingContent(
        floatingContent = {
          BasicText("Floating content")
        },
        anchor = {
          BasicText("Anchor")
        },
      )
    }

    waitForIdle()
    onNodeWithText("Anchor").assertExists()
    onNodeWithText("Floating content").assertDoesNotExist()
  }

  @Test
  fun placementChangesFloatingContentPosition() = runComposeUiTest {
    // Test that different placements result in different positions
    var side by mutableStateOf(AnchorSide.Top)
    var topStartY = 0f
    var bottomStartY = 0f

    setContent {
      TooltipHost {
        FloatingContent(
          side = side,
          alignment = AnchorAlignment.Start,
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
    }

    waitForIdle()
    topStartY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

    side = AnchorSide.Bottom
    waitForIdle()
    bottomStartY = onNodeWithText("Floating").fetchSemanticsNode().positionInRoot.y

    // The Y positions should be different when placement changes
    assertThat(bottomStartY).isNotEqualTo(topStartY)
  }

  @Test
  fun floatingContentClampsToWindowBounds() = runComposeUiTest {
    // FloatingContent clamps to window bounds like a Popup
    setContent {
      TooltipHost {
        FloatingContent(
          side = AnchorSide.Top,
          alignment = AnchorAlignment.Start,
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
    }

    waitForIdle()

    val floatingY = onNodeWithText("Floating").fetchSemanticsNode().positionInWindow.y

    // With TopStart placement at the top of the window,
    // floating content should be clamped to not go negative
    assertThat(floatingY).isGreaterThanOrEqualTo(0f)
  }

  @Test
  fun floatingContentLargerThanWindow() = runComposeUiTest {
    // When floating content is larger than the window,
    // it should be clamped to start at 0 to maximize visible area
    setContent {
      TooltipHost {
        FloatingContent(
          side = AnchorSide.Bottom,
          alignment = AnchorAlignment.Start,
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
    }

    waitForIdle()

    val floatingNode = onNodeWithText("Large floating").fetchSemanticsNode()
    val floatingX = floatingNode.positionInWindow.x
    val floatingY = floatingNode.positionInWindow.y

    // When content is larger than window, it should be clamped to 0
    // to maximize the visible portion at the top-left
    assertThat(floatingX).isGreaterThanOrEqualTo(0f)
    assertThat(floatingY).isGreaterThanOrEqualTo(0f)
  }

  @Test
  fun positionsFloatingContentInsideOffsetHostWhenWindowInfoIsOverridden() = runComposeUiTest {
    setContent {
      Box(Modifier.requiredSize(700.dp)) {
        FakeWindowInfoSurface(
          width = 300.dp,
          height = 300.dp,
          modifier = Modifier
            .offset(x = 200.dp, y = 100.dp)
            .testTag("fake_host"),
        ) {
          TooltipHost(Modifier.requiredSize(300.dp, 300.dp)) {
            FloatingContent(
              modifier = Modifier.offset(x = 260.dp, y = 160.dp),
              side = AnchorSide.Top,
              alignment = AnchorAlignment.Center,
              sideOffset = 8.dp,
              floatingContent = {
                Box(Modifier.size(width = 80.dp, height = 20.dp)) {
                  BasicText("Floating")
                }
              },
              anchor = {
                Box(Modifier.size(40.dp)) {
                  BasicText("Anchor")
                }
              },
            )
          }
        }
      }
    }

    waitForIdle()

    val hostX = onNodeWithTag("fake_host").fetchSemanticsNode().positionInWindow.x
    val floatingX = onNodeWithText("Floating").fetchSemanticsNode().positionInWindow.x

    assertThat((floatingX - hostX).roundToInt()).isEqualTo(220)
  }

  @Composable
  private fun FakeWindowInfoSurface(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
  ) {
    val density = LocalDensity.current
    val parentWindowInfo = LocalWindowInfo.current
    val containerSize = with(density) {
      IntSize(width.roundToPx(), height.roundToPx())
    }
    val fakeWindowInfo = remember(parentWindowInfo, containerSize, width, height) {
      object : WindowInfo {
        override val containerSize: IntSize = containerSize
        override val containerDpSize: DpSize = DpSize(width, height)
        override val isWindowFocused: Boolean
          get() = parentWindowInfo.isWindowFocused
        override val keyboardModifiers: androidx.compose.ui.input.pointer.PointerKeyboardModifiers
          get() = parentWindowInfo.keyboardModifiers
      }
    }

    Box(modifier.requiredSize(width, height)) {
      CompositionLocalProvider(LocalWindowInfo provides fakeWindowInfo) {
        content()
      }
    }
  }
}
