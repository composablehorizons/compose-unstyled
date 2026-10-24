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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.PlatformRootForTest
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ScrollWheel
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class DrawerWheelTest {
  @Test
  fun wheelOverScrollableContentLeavesDrawerAtASnapPoint() = assertScrollSettles(delta = 1f)

  @Test
  fun wheelOverScrollableContentCanOpenDrawer() =
    assertScrollSettles(delta = 40f, expected = "open")

  @Test
  fun wheelReachingTheStartOfContentSettlesDrawer() =
    assertScrollSettles(initial = "open", initialScroll = 100, delta = -40f)

  @Test
  fun trackpadPanWaitsForPanEndBeforeSettlingDrawer() =
    assertScrollSettles(delta = 400f, expected = "open", trackpad = true)

  @Test
  fun wheelDoesNotSettleDrawerWhilePointerIsPressed() =
    assertScrollSettles(delta = 1f, holdPointer = true)

  @Test
  fun wheelOverScrollableContentSettlesEndDrawer() =
    assertScrollSettles(delta = 40f, expected = "open", placement = DrawerPlacement.End)

  @Test
  fun wheelOverScrollableContentSettlesStartDrawer() =
    assertScrollSettles(
      initialScroll = 100,
      delta = -40f,
      expected = "open",
      placement = DrawerPlacement.Start,
    )

  @Test
  fun wheelOverScrollableContentSettlesTopDrawer() =
    assertScrollSettles(
      initialScroll = 100,
      delta = -40f,
      expected = "open",
      placement = DrawerPlacement.Top,
    )

  @OptIn(InternalComposeUiApi::class)
  private fun assertScrollSettles(
    initial: String = "partial",
    initialScroll: Int = 0,
    delta: Float,
    expected: String = "partial",
    trackpad: Boolean = false,
    placement: DrawerPlacement = DrawerPlacement.Bottom,
    holdPointer: Boolean = false,
  ) = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<String>
    var moved = false
    val horizontal = placement == DrawerPlacement.Start || placement == DrawerPlacement.End
    val width = if (horizontal) 700.dp else 400.dp
    val height = if (horizontal) 400.dp else 700.dp
    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          "partial" at DrawerSnapPoint { _, _ -> 200.dp }
          "open" at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(initial, snapPoints) }
      LaunchedEffect(state) {
        snapshotFlow { state.offset }.collect { offset ->
          if (offset != if (initial == "open") 700f else 200f) moved = true
        }
      }
      val scrollState = rememberScrollState(initial = initialScroll)
      UnstyledDrawer(state, placement = placement, presentation = DrawerPresentation.InPlace) {
        Viewport(Modifier.requiredSize(width, height).testTag("wheel-viewport")) {
          Panel(Modifier.requiredSize(width, height)) {
            if (horizontal) {
              Row(Modifier.horizontalScroll(scrollState).testTag("wheel-content")) {
                repeat(50) { Box(Modifier.width(50.dp).fillMaxHeight()) }
              }
            } else {
              Column(Modifier.verticalScroll(scrollState).testTag("wheel-content")) {
                repeat(50) { Box(Modifier.fillMaxWidth().height(50.dp)) }
              }
            }
          }
        }
      }
    }
    waitForIdle()
    assertThat(state.isIdle).isTrue()
    moved = false

    val content = onNodeWithTag("wheel-content")
    val contentNode = content.fetchSemanticsNode()
    val viewportBounds = onNodeWithTag("wheel-viewport").fetchSemanticsNode().boundsInRoot
    val position = contentNode.boundsInRoot.intersect(viewportBounds).center
    val localPosition = position - contentNode.boundsInRoot.topLeft
    if (trackpad) {
      val root = contentNode.root as PlatformRootForTest
      runOnUiThread {
        root.sendPointerEvent(PointerEventType.Move, position, timeMillis = mainClock.currentTime)
        root.sendPointerEvent(
          PointerEventType.PanStart,
          position,
          timeMillis = mainClock.currentTime,
        )
        root.sendPointerEvent(
          PointerEventType.PanMove,
          position,
          timeMillis = mainClock.currentTime,
          panGestureOffset = Offset(0f, delta),
        )
      }
      waitUntil("pan moved the drawer") { moved }
      val heldOffset = state.offset
      mainClock.advanceTimeBy(300)
      assertThat(state.offset).isEqualTo(heldOffset)
      runOnUiThread {
        root.sendPointerEvent(PointerEventType.PanEnd, position, timeMillis = mainClock.currentTime)
      }
    } else {
      content.performMouseInput {
        moveTo(localPosition)
        if (holdPointer) press()
        scroll(delta, if (horizontal) ScrollWheel.Horizontal else ScrollWheel.Vertical)
      }
      if (holdPointer) {
        waitUntil("wheel moved the drawer") { moved }
        waitForIdle()
        val heldOffset = state.offset
        mainClock.advanceTimeBy(300)
        assertThat(state.offset).isEqualTo(heldOffset)
        content.performMouseInput { release() }
      }
    }
    waitForIdle()

    waitUntil("wheel moved the drawer") { moved }
    mainClock.advanceTimeUntil(timeoutMillis = 5_000) { state.isIdle }
    assertThat(state.currentValue).isEqualTo(expected)
    assertThat(state.offset).isEqualTo(if (expected == "open") 700f else 200f)
  }
}
