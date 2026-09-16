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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import com.composeunstyled.test.MeasurementCounter
import com.composeunstyled.test.MeasurementCounts
import com.composeunstyled.test.runComposeRecompositionTest
import kotlin.test.Test

class DrawerLifecycleBaselineTest {
  @Test
  fun inlineBottomLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.Bottom, LayoutDirection.Ltr)

  @Test
  fun inlineBottomRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.Bottom, LayoutDirection.Rtl)

  @Test
  fun inlineTopLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.Top, LayoutDirection.Ltr)

  @Test
  fun inlineTopRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.Top, LayoutDirection.Rtl)

  @Test
  fun inlineStartLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.Start, LayoutDirection.Ltr)

  @Test
  fun inlineStartRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.Start, LayoutDirection.Rtl)

  @Test
  fun inlineEndLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.End, LayoutDirection.Ltr)

  @Test
  fun inlineEndRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.InPlace, DrawerPlacement.End, LayoutDirection.Rtl)

  @Test
  fun overlayBottomLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.Bottom, LayoutDirection.Ltr)

  @Test
  fun overlayBottomRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.Bottom, LayoutDirection.Rtl)

  @Test
  fun overlayTopLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.Top, LayoutDirection.Ltr)

  @Test
  fun overlayTopRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.Top, LayoutDirection.Rtl)

  @Test
  fun overlayStartLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.Start, LayoutDirection.Ltr)

  @Test
  fun overlayStartRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.Start, LayoutDirection.Rtl)

  @Test
  fun overlayEndLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.End, LayoutDirection.Ltr)

  @Test
  fun overlayEndRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Overlay, DrawerPlacement.End, LayoutDirection.Rtl)

  @Test
  fun modalBottomLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.Bottom, LayoutDirection.Ltr)

  @Test
  fun modalBottomRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.Bottom, LayoutDirection.Rtl)

  @Test
  fun modalTopLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.Top, LayoutDirection.Ltr)

  @Test
  fun modalTopRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.Top, LayoutDirection.Rtl)

  @Test
  fun modalStartLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.Start, LayoutDirection.Ltr)

  @Test
  fun modalStartRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.Start, LayoutDirection.Rtl)

  @Test
  fun modalEndLtrBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.End, LayoutDirection.Ltr)

  @Test
  fun modalEndRtlBaseline() =
    lifecycleBaseline(DrawerPresentation.Modal, DrawerPlacement.End, LayoutDirection.Rtl)

  private fun lifecycleBaseline(
    presentation: DrawerPresentation,
    placement: DrawerPlacement,
    direction: LayoutDirection,
  ) = runComposeRecompositionTest {
    val panel = MeasurementCounter()
    val overlay = MeasurementCounter()
    var contentSize by mutableStateOf(120.dp)
    var viewportSize by mutableStateOf(240.dp)
    var inset by mutableStateOf(0)
    var layoutDirection by mutableStateOf(direction)
    var mounted by mutableStateOf(true)
    var gesturesEnabled by mutableStateOf(true)
    var dismissOnNavigateBack by mutableStateOf(true)
    var dismissOnClickOutside by mutableStateOf(true)
    var allowChanges = true
    var dismissals = 0
    var overlayMounts = 0
    val snapPoints = DrawerSnapPoints<LifecycleValue> {
      LifecycleValue.Closed at DrawerSnapPoint.Zero
      LifecycleValue.Open at DrawerSnapPoint { _, size -> panel.recordSizeCalculation { size } }
    }
    val state = UnstyledDrawerState(
      initialValue = LifecycleValue.Closed,
      snapPoints = snapPoints,
      animationSpec = tween(160),
      confirmValueChange = { allowChanges },
    )
    val panelPolicy = panel.measurePolicy {
      val size = contentSize.roundToPx()
      IntSize(size, size)
    }
    val overlayPolicy = overlay.measurePolicy { IntSize(1, 1) }

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        DrawerHost(Modifier.requiredSize(viewportSize)) {
          if (mounted) {
            UnstyledDrawer(
              state,
              placement = placement,
              presentation = presentation,
              gesturesEnabled = gesturesEnabled,
              dismissOnNavigateBack = dismissOnNavigateBack,
              dismissOnClickOutside = dismissOnClickOutside,
              onDismissed = { dismissals++ },
              overlay = {
                Overlay(enter = fadeIn(tween(160)), exit = fadeOut(tween(160))) {
                  DisposableEffect(Unit) {
                    overlayMounts++
                    onDispose { overlayMounts-- }
                  }
                  RecompositionCount("overlay")
                  Layout(content = {}, measurePolicy = overlayPolicy)
                }
              },
            ) {
              Viewport(
                Modifier.requiredSize(viewportSize),
                windowInsets = WindowInsets(inset, inset, inset, inset),
              ) {
                Panel {
                  RecompositionCount("panel")
                  Layout(content = {}, measurePolicy = panelPolicy)
                }
              }
            }
          }
        }
      }
    }
    val observations = mutableListOf<Pair<String, Baseline>>()
    fun record(name: String) {
      waitForIdle()
      observations += name to Baseline(
        panel.snapshot(),
        overlay.snapshot(),
        recompositionCount("panel"),
        recompositionCount("overlay"),
      )
      panel.reset()
      overlay.reset()
      resetRecompositionCounts()
    }
    fun settle(value: LifecycleValue) {
      waitUntil("drawer and overlay settled at $value") {
        state.currentValue == value && state.isIdle &&
          if (value == LifecycleValue.Closed) overlayMounts == 0 else overlayMounts == 1
      }
      waitForIdle()
    }

    record("closed")
    state.targetValue = LifecycleValue.Open
    settle(LifecycleValue.Open)
    record("open")
    state.targetValue = LifecycleValue.Open
    state.jumpTo(LifecycleValue.Open)
    record("no-op")

    contentSize = 160.dp
    record("grow")
    contentSize = 100.dp
    record("shrink")
    viewportSize = 220.dp
    record("viewport")
    inset = 12
    record("insets")
    layoutDirection =
      if (direction == LayoutDirection.Ltr) LayoutDirection.Rtl else LayoutDirection.Ltr
    record("direction")
    state.invalidateSnapPoints()
    record("invalidate")
    state.updateSnapPoints(
      DrawerSnapPoints {
        LifecycleValue.Closed at DrawerSnapPoint.Zero
        LifecycleValue.Open at DrawerSnapPoint { _, size ->
          panel.recordSizeCalculation { size / 2 }
        }
      },
    )
    record("replace snap points")
    state.updateSnapPoints(snapPoints)
    record("restore snap points")
    gesturesEnabled = false
    dismissOnNavigateBack = false
    dismissOnClickOutside = false
    record("disable input")
    gesturesEnabled = true
    dismissOnNavigateBack = true
    dismissOnClickOutside = true
    record("enable input")
    state.animationSpec = tween(80)
    state.dismissAnimationSpec = tween(80)
    record("animation specs")

    allowChanges = false
    state.targetValue = LifecycleValue.Closed
    record("rejected close")
    assertThat(state.currentValue).isEqualTo(LifecycleValue.Open)
    assertThat(dismissals).isEqualTo(0)
    allowChanges = true

    state.isDragging = true
    state.anchoredDraggableState.dispatchRawDelta(if (state.anchoredToMinEdge) -20f else 20f)
    record("drag preview")
    viewportSize = 210.dp
    record("resize during drag")
    state.isDragging = false
    state.settleToClosestValue(DrawerValueChange.Reason.Gesture)
    settle(LifecycleValue.Open)
    record("drag settle")

    state.startPredictiveBack()
    state.progressPredictiveBack(0.5f)
    record("back preview")
    state.cancelPredictiveBack()
    settle(LifecycleValue.Open)
    record("back cancel")
    state.startPredictiveBack()
    state.progressPredictiveBack(1f)
    state.invokePredictiveBack()
    settle(LifecycleValue.Closed)
    record("back commit")
    assertThat(dismissals).isEqualTo(1)

    state.jumpTo(LifecycleValue.Open)
    settle(LifecycleValue.Open)
    record("jump open")
    state.targetValue = LifecycleValue.Closed
    settle(LifecycleValue.Closed)
    record("close")
    assertThat(dismissals).isEqualTo(2)
    state.targetValue = LifecycleValue.Open
    settle(LifecycleValue.Open)
    record("reopen")
    mounted = false
    record("unmount")
    mounted = true
    settle(LifecycleValue.Open)
    record("remount")

    observations.forEach { (phase, baseline) ->
      baseline.assertMatches(phase, presentation, placement)
    }
  }
}

private data class Baseline(
  val panel: MeasurementCounts,
  val overlay: MeasurementCounts,
  val panelCompositions: Int,
  val overlayCompositions: Int,
)

private fun Baseline.assertMatches(
  phase: String,
  presentation: DrawerPresentation,
  placement: DrawerPlacement,
) {
  val inline = presentation == DrawerPresentation.InPlace
  val portal = presentation == DrawerPresentation.Overlay
  val horizontal = placement == DrawerPlacement.Start || placement == DrawerPlacement.End
  // Bounds were observed on JVM and Android. Native-window layout can coalesce a direction
  // change, and geometry callbacks can cause one additional panel composition at settlement.
  val expected = when (phase) {
    "closed" -> Budget(1..1, 1..1, 0..0, 2..2, 0..0)
    "open" -> Budget(
      panelMeasures = if (inline) 0..0 else 1..1,
      panelCalculations = 0..0,
      overlayMeasures = 1..1,
      panelCompositions = 11..11,
      overlayCompositions = if (portal) 10..10 else 11..11,
    )
    "no-op", "animation specs", "rejected close", "unmount" -> Budget()
    "grow", "shrink" -> Budget(1..1, 1..1, 0..0, 1..1, 1..1)
    "viewport" -> Budget(1..1, 1..1, 1..1, 1..2, 1..1)
    "insets" -> Budget(1..1, 1..1, 0..0, 2..2, 1..1)
    "direction" -> {
      val measures = if (presentation == DrawerPresentation.Modal) 0..1 else 1..1
      val compositions = 1 + (if (horizontal) 1 else 0) + (if (portal) 1 else 0)
      val nativeModal = presentation == DrawerPresentation.Modal
      Budget(
        panelMeasures = measures,
        panelCalculations = if (horizontal) (if (nativeModal) 0..1 else 1..1) else 0..0,
        overlayMeasures = measures,
        panelCompositions = (if (nativeModal) 1 else compositions)..compositions,
        overlayCompositions = if (portal) 2..2 else 1..1,
      )
    }
    "invalidate" -> Budget(panelCalculations = 1..1)
    "replace snap points", "restore snap points" ->
      Budget(0..0, 1..1, 0..0, 1..1, 1..1)
    "disable input", "enable input", "drag preview", "back preview" ->
      Budget(0..0, 0..0, 0..0, 1..1, 1..1)
    "resize during drag" -> Budget(1..1, 3..3, 1..1, 1..1, 1..1)
    "drag settle" -> Budget(0..0, 0..0, 0..0, 6..6, 5..5)
    "back cancel" -> Budget(0..0, 0..0, 0..0, 5..5, 5..5)
    "back commit" -> when (presentation) {
      DrawerPresentation.InPlace -> Budget(0..0, 0..0, 0..0, 2..2, 2..2)
      DrawerPresentation.Overlay -> Budget(1..1, 0..0, 0..0, 3..3, 2..2)
      else -> Budget(1..1, if (horizontal) 0..1 else 0..0, 0..0, 3..4, 2..2)
    }
    "jump open" -> Budget(
      panelMeasures = if (inline) 0..0 else 1..1,
      panelCalculations = 0..0,
      overlayMeasures = 1..1,
      panelCompositions = if (inline) 2..2 else 1..1,
      overlayCompositions = 1..1,
    )
    "close" -> when (presentation) {
      DrawerPresentation.InPlace -> Budget(0..0, 0..0, 0..0, 5..6, 5..5)
      else -> Budget(1..1, 0..0, 0..0, 6..7, 5..5)
    }
    "reopen" -> Budget(
      panelMeasures = if (inline) 0..0 else 1..1,
      panelCalculations = 0..0,
      overlayMeasures = 1..1,
      panelCompositions = 6..6,
      overlayCompositions = if (portal) 5..5 else 6..6,
    )
    "remount" -> Budget(1..1, 0..0, 1..1, 1..1, 1..1)
    else -> error("Missing baseline for $phase")
  }
  fun check(name: String, actual: Int, range: IntRange) {
    assertThat(actual, name = "$phase: $name").isBetween(range.first, range.last)
  }
  check("panel measurements", panel.measures, expected.panelMeasures)
  check("panel size calculations", panel.sizeCalculations, expected.panelCalculations)
  check("overlay measurements", overlay.measures, expected.overlayMeasures)
  check("panel compositions", panelCompositions, expected.panelCompositions)
  check("overlay compositions", overlayCompositions, expected.overlayCompositions)
  assertThat(panel.intrinsics, name = "$phase: panel intrinsics").isEqualTo(0)
  assertThat(overlay.intrinsics, name = "$phase: overlay intrinsics").isEqualTo(0)
  assertThat(overlay.sizeCalculations, name = "$phase: overlay size calculations").isEqualTo(0)
}

private data class Budget(
  val panelMeasures: IntRange = 0..0,
  val panelCalculations: IntRange = 0..0,
  val overlayMeasures: IntRange = 0..0,
  val panelCompositions: IntRange = 0..0,
  val overlayCompositions: IntRange = 0..0,
)

private enum class LifecycleValue {
  Closed,
  Open,
}
