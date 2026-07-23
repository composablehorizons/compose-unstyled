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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.LayoutDirection
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
  fun startOpenSnapPointUsesPanelSize() = runComposeUiTest {
    setContent {
      StartDrawerLayout(
        initialSnapPoint = DrawerSnapPoint.Open,
        contentWidth = 60,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(viewportBounds.left.roundToInt())
    assertThat(panelBounds.right.roundToInt()).isEqualTo(60)
    assertThat(panelBounds.width.roundToInt()).isEqualTo(60)
    assertThat(panelBounds.height.roundToInt()).isEqualTo(viewportBounds.height.roundToInt())
  }

  @Test
  fun startStateStartsClosed() = runComposeUiTest {
    setContent {
      StartDrawerLayout()
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(viewportBounds.left.roundToInt())
    assertThat(panelBounds.width.roundToInt()).isEqualTo(0)
  }

  @Test
  fun startClosedContentIsClippedToPanelBounds() = runComposeUiTest {
    setContent {
      StartDrawerLayout()
    }

    waitForIdle()

    onNodeWithTag("content").assertIsNotDisplayed()
  }

  @Test
  fun startPeekSnapPointUsesViewportWidth() = runComposeUiTest {
    setContent {
      StartDrawerLayout(
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
        contentWidth = 160,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(viewportBounds.left.roundToInt())
    assertThat(panelBounds.right.roundToInt()).isEqualTo(120)
    assertThat(panelBounds.width.roundToInt()).isEqualTo(120)
    assertThat(panelBounds.height.roundToInt()).isEqualTo(viewportBounds.height.roundToInt())
  }

  @Test
  fun endOpenSnapPointUsesPanelSize() = runComposeUiTest {
    setContent {
      EdgeDrawerLayout(
        position = DrawerPosition.End,
        initialSnapPoint = DrawerSnapPoint.Open,
        contentWidth = 60,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(
      viewportBounds.right.roundToInt() - 60,
    )
    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.right.roundToInt())
    assertThat(panelBounds.width.roundToInt()).isEqualTo(60)
    assertThat(panelBounds.height.roundToInt()).isEqualTo(viewportBounds.height.roundToInt())
  }

  @Test
  fun topOpenSnapPointUsesPanelSize() = runComposeUiTest {
    setContent {
      EdgeDrawerLayout(
        position = DrawerPosition.Top,
        initialSnapPoint = DrawerSnapPoint.Open,
        contentHeight = 60,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(viewportBounds.top.roundToInt())
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(60)
    assertThat(panelBounds.width.roundToInt()).isEqualTo(viewportBounds.width.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(60)
  }

  @Test
  fun startPositionUsesLeftEdgeInLtr() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        EdgeDrawerLayout(
          position = DrawerPosition.Start,
          initialSnapPoint = DrawerSnapPoint.Open,
          contentWidth = 60,
        )
      }
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(0)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(60)
  }

  @Test
  fun startPositionUsesRightEdgeInRtl() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        EdgeDrawerLayout(
          position = DrawerPosition.Start,
          initialSnapPoint = DrawerSnapPoint.Open,
          contentWidth = 60,
        )
      }
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(140)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(200)
  }

  @Test
  fun endPositionUsesRightEdgeInLtr() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        EdgeDrawerLayout(
          position = DrawerPosition.End,
          initialSnapPoint = DrawerSnapPoint.Open,
          contentWidth = 60,
        )
      }
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(140)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(200)
  }

  @Test
  fun endPositionUsesLeftEdgeInRtl() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        EdgeDrawerLayout(
          position = DrawerPosition.End,
          initialSnapPoint = DrawerSnapPoint.Open,
          contentWidth = 60,
        )
      }
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(0)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(60)
  }

  @Test
  fun startDrawerClosesTowardLeftInLtr() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        StartDrawerLayout(
          initialSnapPoint = DrawerSnapPoint.Open,
          onState = { state = it },
        )
      }
    }

    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(start = centerRight, end = centerLeft)
    }
    waitForIdle()

    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Closed)
  }

  @Test
  fun startDrawerMovesImmediatelyWhenContentIsWiderThanPanelConstraints() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      ConstrainedStartDrawerLayout(
        initialSnapPoint = DrawerSnapPoint.Open,
        onState = { state = it },
      )
    }

    waitForIdle()

    assertThat(onNodeWithTag("panel").boundsInRoot().width.roundToInt()).isEqualTo(100)

    runOnIdle {
      state.anchoredDraggableState.dispatchRawDelta(-50f)
    }
    waitForIdle()

    assertThat(onNodeWithTag("panel").boundsInRoot().width.roundToInt()).isEqualTo(50)
  }

  @Test
  fun startDrawerClosesTowardRightInRtl() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        StartDrawerLayout(
          initialSnapPoint = DrawerSnapPoint.Open,
          onState = { state = it },
        )
      }
    }

    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(start = centerLeft, end = centerRight)
    }
    waitForIdle()

    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Closed)
  }

  @Test
  fun endDrawerClosesTowardRightInLtr() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        EdgeDrawerLayout(
          position = DrawerPosition.End,
          initialSnapPoint = DrawerSnapPoint.Open,
          onState = { state = it },
        )
      }
    }

    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(start = centerLeft, end = centerRight)
    }
    waitForIdle()

    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Closed)
  }

  @Test
  fun endDrawerIsPlacedAtRightEdgeWithConstrainedPanelAndOversizedContent() = runComposeUiTest {
    setContent {
      ConstrainedEndDrawerLayout()
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(
      viewportBounds.right.roundToInt() - 100,
    )
    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.right.roundToInt())
    assertThat(panelBounds.width.roundToInt()).isEqualTo(100)
  }

  @Test
  fun endDrawerClosesTowardLeftInRtl() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        EdgeDrawerLayout(
          position = DrawerPosition.End,
          initialSnapPoint = DrawerSnapPoint.Open,
          onState = { state = it },
        )
      }
    }

    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(start = centerRight, end = centerLeft)
    }
    waitForIdle()

    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Closed)
  }

  @Test
  fun topDrawerClosesTowardTop() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      EdgeDrawerLayout(
        position = DrawerPosition.Top,
        initialSnapPoint = DrawerSnapPoint.Open,
        onState = { state = it },
      )
    }

    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(start = bottomCenter, end = topCenter)
    }
    waitForIdle()

    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Closed)
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

  @Test
  fun bottomCustomSnapPointIsCappedToPanelSize() = runComposeUiTest {
    setContent {
      DrawerLayout(
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
        contentHeight = 40,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(60)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(40)
  }

  @Test
  fun topCustomSnapPointIsCappedToPanelSize() = runComposeUiTest {
    setContent {
      EdgeDrawerLayout(
        position = DrawerPosition.Top,
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
        contentHeight = 40,
      )
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(0)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.height.roundToInt()).isEqualTo(40)
  }

  @Test
  fun startCustomSnapPointIsCappedToPanelSize() = runComposeUiTest {
    setContent {
      EdgeDrawerLayout(
        position = DrawerPosition.Start,
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
        contentWidth = 40,
      )
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(0)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.width.roundToInt()).isEqualTo(40)
  }

  @Test
  fun startCustomSnapPointIsCappedToPanelSizeInRtl() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        EdgeDrawerLayout(
          position = DrawerPosition.Start,
          initialSnapPoint = Peek,
          snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
          contentWidth = 40,
        )
      }
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(
      viewportBounds.right.roundToInt() - 40,
    )
    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.right.roundToInt())
    assertThat(panelBounds.width.roundToInt()).isEqualTo(40)
  }

  @Test
  fun endCustomSnapPointIsCappedToPanelSize() = runComposeUiTest {
    setContent {
      EdgeDrawerLayout(
        position = DrawerPosition.End,
        initialSnapPoint = Peek,
        snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
        contentWidth = 40,
      )
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag("viewport").boundsInRoot()
    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(
      viewportBounds.right.roundToInt() - 40,
    )
    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.right.roundToInt())
    assertThat(panelBounds.width.roundToInt()).isEqualTo(40)
  }

  @Test
  fun endCustomSnapPointIsCappedToPanelSizeInRtl() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        EdgeDrawerLayout(
          position = DrawerPosition.End,
          initialSnapPoint = Peek,
          snapPoints = listOf(DrawerSnapPoint.Closed, Peek),
          contentWidth = 40,
        )
      }
    }

    waitForIdle()

    val panelBounds = onNodeWithTag("panel").boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(0)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.width.roundToInt()).isEqualTo(40)
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
private fun StartDrawerLayout(
  initialSnapPoint: DrawerSnapPoint = DrawerSnapPoint.Closed,
  snapPoints: List<DrawerSnapPoint> = listOf(DrawerSnapPoint.Closed, DrawerSnapPoint.Open),
  contentWidth: Int = 100,
  onState: (DrawerState) -> Unit = {},
) {
  EdgeDrawerLayout(
    position = DrawerPosition.Start,
    initialSnapPoint = initialSnapPoint,
    snapPoints = snapPoints,
    contentWidth = contentWidth,
    onState = onState,
  )
}

@Composable
private fun ConstrainedStartDrawerLayout(
  initialSnapPoint: DrawerSnapPoint = DrawerSnapPoint.Closed,
  onState: (DrawerState) -> Unit = {},
) {
  val state = rememberDrawerState(
    initialSnapPoint = initialSnapPoint,
  )
  onState(state)

  UnstyledDrawer(
    state = state,
    position = DrawerPosition.Start,
    modifier = Modifier.width(500.dp).height(100.dp),
  ) {
    Viewport(
      modifier = Modifier
        .width(500.dp)
        .height(100.dp)
        .testTag("viewport"),
    ) {
      Panel(
        modifier = Modifier
          .height(100.dp)
          .widthIn(max = 100.dp)
          .testTag("panel"),
      ) {
        Content(
          modifier = Modifier
            .testTag("content")
            .width(300.dp)
            .height(100.dp),
        ) {
          Box(Modifier.width(300.dp).height(100.dp))
        }
      }
    }
  }
}

@Composable
private fun ConstrainedEndDrawerLayout() {
  val state = rememberDrawerState(
    initialSnapPoint = DrawerSnapPoint.Open,
  )

  UnstyledDrawer(
    state = state,
    position = DrawerPosition.End,
    modifier = Modifier.width(500.dp).height(100.dp),
  ) {
    Viewport(
      modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)
        .testTag("viewport"),
    ) {
      Panel(
        modifier = Modifier
          .height(100.dp)
          .widthIn(max = 100.dp)
          .testTag("panel"),
      ) {
        Content(
          modifier = Modifier
            .testTag("content")
            .width(300.dp)
            .height(100.dp),
        ) {
          Box(Modifier.width(300.dp).height(100.dp))
        }
      }
    }
  }
}

@Composable
private fun EdgeDrawerLayout(
  position: DrawerPosition,
  initialSnapPoint: DrawerSnapPoint = DrawerSnapPoint.Closed,
  snapPoints: List<DrawerSnapPoint> = listOf(DrawerSnapPoint.Closed, DrawerSnapPoint.Open),
  contentWidth: Int = 100,
  contentHeight: Int = 100,
  onState: (DrawerState) -> Unit = {},
) {
  val isHorizontal = position == DrawerPosition.Start || position == DrawerPosition.End
  val viewportWidth = if (isHorizontal) {
    200.dp
  } else {
    100.dp
  }
  val viewportHeight = 100.dp
  val panelModifier = if (position == DrawerPosition.Top || position == DrawerPosition.Bottom) {
    Modifier.width(100.dp)
  } else {
    Modifier.height(viewportHeight)
  }
  val state = rememberDrawerState(
    initialSnapPoint = initialSnapPoint,
    snapPoints = snapPoints,
  )
  onState(state)

  UnstyledDrawer(
    state = state,
    position = position,
    modifier = Modifier.width(viewportWidth).height(viewportHeight),
  ) {
    Viewport(
      modifier = Modifier
        .width(viewportWidth)
        .height(viewportHeight)
        .testTag("viewport"),
    ) {
      Panel(
        modifier = panelModifier
          .testTag("panel"),
      ) {
        Content(
          modifier = Modifier
            .testTag("content")
            .width(contentWidth.dp)
            .height(contentHeight.dp),
        ) {
          Box(Modifier.width(contentWidth.dp).height(contentHeight.dp))
        }
      }
    }
  }
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
