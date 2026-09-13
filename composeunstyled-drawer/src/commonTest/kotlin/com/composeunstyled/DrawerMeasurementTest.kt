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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.composeunstyled.test.MeasurementCounter
import com.composeunstyled.test.MeasurementCounts
import kotlin.test.Test

class DrawerMeasurementTest {
  @Test
  fun progressUsesMeasuredAnchorsUntilTheyAreInvalidated() {
    val counters = MeasurementCounter()
    var requestedSize = 200.dp
    val state = UnstyledDrawerState(
      initialValue = Value.Open,
      snapPoints = DrawerSnapPoints {
        Value.Closed at DrawerSnapPoint.Zero
        Value.Open at DrawerSnapPoint { _, _ ->
          counters.recordSizeCalculation { requestedSize }
        }
      },
    )
    state.updateViewportSize(400f, false, 0f, Density(1f))
    state.updateContentSize(200f)
    counters.reset()

    assertThat(state.progress(Value.Closed, Value.Open)).isEqualTo(1f)
    state.startPredictiveBack()
    state.progressPredictiveBack(0.5f)
    assertThat(state.offset).isEqualTo(100f)
    assertThat(state.progress(Value.Closed, Value.Open)).isEqualTo(0.5f)
    assertThat(counters.sizeCalculationCalls).isEqualTo(0)

    requestedSize = 300.dp
    state.invalidateSnapPoints()
    counters.reset()
    state.progressPredictiveBack(0.5f)
    assertThat(state.offset).isEqualTo(150f)
    assertThat(counters.sizeCalculationCalls).isEqualTo(0)
  }

  @Test
  fun equivalentLayoutDensitiesDoNotRecalculateSnapPoints() {
    val counters = MeasurementCounter()
    val state = UnstyledDrawerState(
      initialValue = Value.Open,
      snapPoints = DrawerSnapPoints {
        Value.Open at DrawerSnapPoint { _, size -> counters.recordSizeCalculation { size } }
      },
    )
    fun layoutDensity(scale: Float = 1f, textScale: Float = 1f) = object : Density {
      override val density = scale
      override val fontScale = textScale
    }
    state.updateViewportSize(400f, false, 0f, layoutDensity())
    state.updateContentSize(200f)
    counters.reset()

    state.updateViewportSize(400f, false, 0f, layoutDensity())
    assertThat(counters.sizeCalculationCalls).isEqualTo(0)

    state.updateViewportSize(400f, false, 0f, layoutDensity(2f))
    assertThat(counters.sizeCalculationCalls).isEqualTo(1)

    state.updateViewportSize(400f, false, 0f, layoutDensity(2f, 1.5f))
    assertThat(counters.sizeCalculationCalls).isEqualTo(2)
  }

  @Test
  fun bottomDrawerMeasurementBaseline() = measurementBaseline(DrawerPlacement.Bottom)

  @Test
  fun startDrawerMeasurementBaseline() = measurementBaseline(DrawerPlacement.Start)

  private fun measurementBaseline(placement: DrawerPlacement) = runComposeUiTest {
    val counters = MeasurementCounter()
    var contentSize by mutableStateOf(200.dp)
    var viewportSize by mutableStateOf(400.dp)
    var inset by mutableStateOf(0)
    val contentSnapPoint = DrawerSnapPoint { _, size ->
      counters.recordSizeCalculation { size }
    }
    val state = UnstyledDrawerState(
      initialValue = Value.Open,
      snapPoints = DrawerSnapPoints {
        Value.Closed at DrawerSnapPoint.Zero
        Value.Open at contentSnapPoint
      },
      animationSpec = tween(durationMillis = 160),
    )
    val policy = counters.measurePolicy {
      val size = contentSize.roundToPx()
      IntSize(size, size)
    }
    setContent {
      UnstyledDrawer(state, placement = placement, presentation = DrawerPresentation.Inline) {
        Viewport(
          Modifier.requiredSize(viewportSize),
          windowInsets = WindowInsets(inset, inset, inset, inset),
        ) {
          Panel {
            Layout(content = {}, measurePolicy = policy)
          }
        }
      }
    }
    val observed = mutableListOf<Pair<String, MeasurementCounts>>()
    fun record(phase: String) {
      observed += phase to counters.snapshot()
      counters.reset()
    }

    waitForIdle()
    record("initial")

    contentSize = 250.dp
    waitForIdle()
    record("content resize")

    viewportSize = 350.dp
    waitForIdle()
    record("viewport resize")

    inset = 20
    waitForIdle()
    record("insets")

    state.invalidateSnapPoints()
    waitForIdle()
    record("snap point invalidation")

    mainClock.autoAdvance = false
    state.targetValue = Value.Closed
    mainClock.advanceTimeUntil { state.currentValue == Value.Closed && state.isIdle }
    waitForIdle()
    assertThat(state.currentValue).isEqualTo(Value.Closed)
    record("close")

    state.targetValue = Value.Open
    mainClock.advanceTimeUntil { state.currentValue == Value.Open && state.isIdle }
    waitForIdle()
    assertThat(state.currentValue).isEqualTo(Value.Open)
    record("open")

    assertThat(observed).isEqualTo(
      listOf(
        "initial" to MeasurementCounts(1, 0, 1),
        "content resize" to MeasurementCounts(1, 0, 1),
        "viewport resize" to MeasurementCounts(1, 0, 1),
        "insets" to MeasurementCounts(1, 0, 1),
        "snap point invalidation" to MeasurementCounts(0, 0, 1),
        "close" to MeasurementCounts(0, 0, 0),
        "open" to MeasurementCounts(0, 0, 0),
      ),
    )
  }
}

private enum class Value {
  Closed,
  Open,
}
