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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import com.composeunstyled.test.runComposeRecompositionTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test

class DrawerRecompositionTest {
  @Test
  fun settlingNestedScrollWithoutAFlingDoesNotRecomposePanelContent() =
    assertNestedScrollSettlement()

  @Test
  fun pendingScrollSettlementDoesNotOverrideProgrammaticDrawerAnimation() =
    assertNestedScrollSettlement(animate = true)

  private fun assertNestedScrollSettlement(animate: Boolean = false) = runComposeRecompositionTest {
    lateinit var state: UnstyledDrawerState<String>
    lateinit var scope: CoroutineScope
    val dispatcher = NestedScrollDispatcher()
    val connection = object : NestedScrollConnection {}
    setContent {
      scope = rememberCoroutineScope()
      state = remember {
        UnstyledDrawerState(
          "partial",
          DrawerSnapPoints {
            "partial" at DrawerSnapPoint { _, _ -> 30.dp }
            "open" at DrawerSnapPoint.ContentSize
          },
        )
      }
      UnstyledDrawer(state, presentation = DrawerPresentation.InPlace) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(60.dp)) {
            RecompositionCount("drawer-panel-content")
            Box(Modifier.requiredSize(60.dp).nestedScroll(connection, dispatcher))
          }
        }
      }
    }
    waitForIdle()
    resetRecompositionCounts("drawer-panel-content")

    val consumed = dispatcher.dispatchPreScroll(Offset(0f, -10f), NestedScrollSource.UserInput)
    assertThat(consumed.y).isEqualTo(-10f)
    if (animate) {
      scope.launch { state.animateTo("open", tween(1_000)) }
    }
    waitUntil("drawer settled without a fling", timeoutMillis = 5_000) { state.isIdle }

    assertThat(state.currentValue).isEqualTo(if (animate) "open" else "partial")
    assertThat(recompositionCount("drawer-panel-content")).isEqualTo(0)
  }

  @Test
  fun updatingAnimationSpecsDoesNotRecomposePanelContent() =
    runComposeRecompositionTest {
      val snapPoints = DrawerSnapPoints<DrawerRecompositionValue> {
        DrawerRecompositionValue.Closed at DrawerSnapPoint.Zero
        DrawerRecompositionValue.Open at DrawerSnapPoint.ContentSize
      }
      lateinit var state: UnstyledDrawerState<DrawerRecompositionValue>

      setContent {
        state = remember {
          UnstyledDrawerState(
            initialValue = DrawerRecompositionValue.Closed,
            snapPoints = snapPoints,
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.InPlace) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(60.dp)) {
              RecompositionCount("drawer-panel-content")
              Box(Modifier)
            }
          }
        }
      }

      waitUntil { state.isIdle }
      resetRecompositionCounts()

      state.animationSpec = tween(durationMillis = 100)
      state.dismissAnimationSpec = tween(durationMillis = 200)
      waitForIdle()

      assertThat(recompositionCount("drawer-panel-content")).isEqualTo(0)
    }

  @Test
  fun projectingTargetValueDuringDragDoesNotRecomposePanelContent() =
    runComposeRecompositionTest {
      val snapPoints = DrawerSnapPoints<DrawerRecompositionValue> {
        DrawerRecompositionValue.Closed at DrawerSnapPoint.Zero
        DrawerRecompositionValue.Open at DrawerSnapPoint.ContentSize
      }
      lateinit var state: UnstyledDrawerState<DrawerRecompositionValue>

      setContent {
        state = remember {
          UnstyledDrawerState(
            initialValue = DrawerRecompositionValue.Closed,
            snapPoints = snapPoints,
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.InPlace) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(60.dp)) {
              RecompositionCount("drawer-panel-content")
              Box(Modifier)
            }
          }
        }
      }

      waitUntil { state.isIdle }
      resetRecompositionCounts()

      state.isDragging = true
      state.anchoredDraggableState.dispatchRawDelta(-120f)
      waitUntil { state.targetValue == DrawerRecompositionValue.Open }

      assertThat(recompositionCount("drawer-panel-content")).isEqualTo(0)
      state.isDragging = false
    }

  @Test
  fun invalidatingContentSizedSnapPointsDoesNotRecomposePanelContent() =
    runComposeRecompositionTest {
      val snapPoints = DrawerSnapPoints<DrawerRecompositionValue> {
        DrawerRecompositionValue.Open at DrawerSnapPoint.ContentSize
      }
      lateinit var state: UnstyledDrawerState<DrawerRecompositionValue>

      setContent {
        state = remember {
          UnstyledDrawerState(
            initialValue = DrawerRecompositionValue.Open,
            snapPoints = snapPoints,
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.InPlace) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(60.dp)) {
              RecompositionCount("drawer-panel-content")
              Box(Modifier)
            }
          }
        }
      }

      waitUntil { state.isIdle }
      resetRecompositionCounts()

      state.invalidateSnapPoints()
      waitUntil { state.isIdle }

      assertThat(recompositionCount("drawer-panel-content")).isEqualTo(0)
    }

  @Test
  fun closingDrawerKeepsOverlayContentRecompositionsBoundedDuringExit() =
    runComposeRecompositionTest {
      val snapPoints = DrawerSnapPoints<DrawerRecompositionValue> {
        DrawerRecompositionValue.Closed at DrawerSnapPoint.Zero
        DrawerRecompositionValue.Open at DrawerSnapPoint.ContentSize
      }
      lateinit var state: UnstyledDrawerState<DrawerRecompositionValue>

      setContent {
        state = remember {
          UnstyledDrawerState(
            initialValue = DrawerRecompositionValue.Open,
            snapPoints = snapPoints,
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.InPlace, overlay = {
          Overlay(exit = fadeOut(tween(durationMillis = 300))) {
            RecompositionCount("drawer-overlay-content")
          }
        }) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(60.dp)) {
              Box(Modifier)
            }
          }
        }
      }

      waitUntil { state.isIdle }
      resetRecompositionCounts()

      state.targetValue = DrawerRecompositionValue.Closed
      waitUntil { state.currentValue == DrawerRecompositionValue.Closed && state.isIdle }
      waitForIdle()

      assertThat(recompositionCount("drawer-overlay-content")).isLessThanOrEqualTo(19)
    }
}

private enum class DrawerRecompositionValue {
  Closed,
  Open,
}
