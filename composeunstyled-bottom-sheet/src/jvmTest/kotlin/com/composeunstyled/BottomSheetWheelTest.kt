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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.PlatformRootForTest
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class BottomSheetWheelTest {
  @Test
  fun wheelOverScrollableContentLeavesSheetAtADetent() = assertWheelSettles(delta = 1f)

  @Test
  fun wheelOverScrollableContentCanExpandSheet() =
    assertWheelSettles(delta = 40f, expectedExpanded = true)

  @Test
  fun wheelReachingTheStartOfContentSettlesSheet() =
    assertWheelSettles(initialExpanded = true, initialScroll = 100, delta = -40f)

  @Test
  fun trackpadPanWaitsForPanEndBeforeSettlingSheet() =
    assertWheelSettles(delta = 400f, expectedExpanded = true, trackpad = true)

  @Test
  fun wheelDoesNotSettleSheetWhilePointerIsPressed() =
    assertWheelSettles(delta = 1f, holdPointer = true)

  @OptIn(InternalComposeUiApi::class)
  private fun assertWheelSettles(
    initialExpanded: Boolean = false,
    initialScroll: Int = 0,
    delta: Float,
    expectedExpanded: Boolean = false,
    trackpad: Boolean = false,
    holdPointer: Boolean = false,
  ) = runComposeUiTest {
    lateinit var state: BottomSheetState
    var moved = false
    val partial = SheetDetent("partial") { _, _ -> 200.dp }
    setContent {
      state = rememberBottomSheetState(
        initialDetent = if (initialExpanded) SheetDetent.FullyExpanded else partial,
        detents = listOf(partial, SheetDetent.FullyExpanded),
      )
      val scrollState = rememberScrollState(initial = initialScroll)
      LaunchedEffect(state) {
        snapshotFlow { state.offset }.collect { offset ->
          if (offset != if (initialExpanded) 700f else 200f) moved = true
        }
      }
      UnstyledBottomSheet(state, Modifier.requiredSize(400.dp, 700.dp)) {
        Sheet(Modifier.requiredSize(400.dp, 700.dp)) {
          Column(Modifier.verticalScroll(scrollState).testTag("wheel-content")) {
            repeat(50) { Box(Modifier.fillMaxWidth().height(50.dp)) }
          }
        }
      }
    }
    waitForIdle()
    assertThat(state.isIdle).isTrue()
    moved = false

    val content = onNodeWithTag("wheel-content")
    if (trackpad) {
      val node = content.fetchSemanticsNode()
      val root = node.root as PlatformRootForTest
      val position = node.boundsInRoot.topLeft + Offset(20f, 20f)
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
      waitUntil("pan moved the sheet") { moved }
      val heldOffset = state.offset
      mainClock.advanceTimeBy(300)
      assertThat(state.offset).isEqualTo(heldOffset)
      runOnUiThread {
        root.sendPointerEvent(PointerEventType.PanEnd, position, timeMillis = mainClock.currentTime)
      }
    } else {
      content.performMouseInput {
        moveTo(Offset(20f, 20f))
        if (holdPointer) press()
        scroll(delta)
      }
      if (holdPointer) {
        waitUntil("wheel moved the sheet") { moved }
        waitForIdle()
        val heldOffset = state.offset
        mainClock.advanceTimeBy(300)
        assertThat(state.offset).isEqualTo(heldOffset)
        content.performMouseInput { release() }
      }
    }
    waitForIdle()

    waitUntil("wheel moved the sheet") { moved }
    mainClock.advanceTimeUntil(timeoutMillis = 5_000) { state.isIdle }
    val expectedDetent = if (expectedExpanded) SheetDetent.FullyExpanded else partial
    assertThat(state.currentDetent).isEqualTo(expectedDetent)
    assertThat(state.offset).isEqualTo(if (expectedExpanded) 700f else 200f)
  }
}
