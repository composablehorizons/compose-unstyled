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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.math.roundToInt
import kotlin.test.Test

class DrawerTest {

  @Test
  fun defaultStateStartsClosed() = runComposeUiTest {
    setContent {
      DefaultDrawerLayout()
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(viewportBounds.width.roundToInt()).isEqualTo(100)
    assertThat(viewportBounds.height.roundToInt()).isEqualTo(100)
    assertThat(panelBounds.height.roundToInt()).isEqualTo(0)
  }

  @Test
  fun openSnapPointUsesPanelSize() = runComposeUiTest {
    setContent {
      DrawerLayout(
        initialSnapPoint = DrawerSnapPoint.Open,
        contentHeight = 60,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(60)
  }

  @Test
  fun openSnapPointIsCappedToTheViewportSize() = runComposeUiTest {
    setContent {
      DrawerLayout(
        initialSnapPoint = DrawerSnapPoint.Open,
        contentHeight = 160,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(viewportBounds.top.roundToInt())
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(100)
  }

  @Test
  fun peekPanelIsBottomAlignedToTheViewport() = runComposeUiTest {
    setContent {
      DrawerLayout(
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(60)
  }

  @Test
  fun oversizedContentIsBottomAlignedInsideTheVisiblePanel() = runComposeUiTest {
    setContent {
      DrawerLayout(
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val contentBottomBounds = onNodeWithTag("contentBottom", useUnmergedTree = true).boundsInRoot()

    assertThat(contentBottomBounds.bottom.roundToInt())
      .isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(contentBottomBounds.top.roundToInt()).isEqualTo(90)
  }

  private fun SemanticsNodeInteraction.boundsInRoot(): Rect {
    return fetchSemanticsNode().boundsInRoot
  }
}

private val Peek = DrawerSnapPoint("peek") { containerSize, _ ->
  containerSize * 0.6f
}

@Composable
private fun DefaultDrawerLayout() {
  DrawerLayoutContent(state = rememberDrawerState())
}

@Composable
private fun DrawerLayout(
  initialSnapPoint: DrawerSnapPoint = DrawerSnapPoint.Closed,
  snapPoints: List<DrawerSnapPoint> = listOf(DrawerSnapPoint.Closed, DrawerSnapPoint.Open),
  contentHeight: Int = 100,
) {
  val state = rememberDrawerState(
    initialSnapPoint = initialSnapPoint,
    snapPoints = snapPoints,
  )

  DrawerLayoutContent(
    state = state,
    contentHeight = contentHeight,
  )
}

@Composable
private fun DrawerLayoutContent(
  state: DrawerState,
  contentHeight: Int = 100,
) {
  UnstyledDrawer(
    state = state,
    modifier = Modifier.size(100.dp),
  ) {
    Viewport(
      modifier = Modifier
        .size(100.dp)
        .testTag("viewport"),
    ) {
      Panel(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("panel"),
      ) {
        Content(
          modifier = Modifier
            .fillMaxWidth()
            .height(contentHeight.dp),
        ) {
          Box(
            modifier = Modifier
              .width(100.dp)
              .height(contentHeight.dp),
            contentAlignment = Alignment.BottomStart,
          ) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .testTag("contentBottom"),
            )
          }
        }
      }
    }
  }
}
