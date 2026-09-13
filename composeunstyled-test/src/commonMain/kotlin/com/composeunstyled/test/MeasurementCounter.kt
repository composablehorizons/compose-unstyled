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
package com.composeunstyled.test

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize

class MeasurementCounter {
  var measureCalls: Int = 0
    private set
  var intrinsicCalls: Int = 0
    private set
  var sizeCalculationCalls: Int = 0
    private set

  fun <T> recordSizeCalculation(block: () -> T): T {
    sizeCalculationCalls++
    return block()
  }

  fun reset() {
    measureCalls = 0
    intrinsicCalls = 0
    sizeCalculationCalls = 0
  }

  fun snapshot(): MeasurementCounts =
    MeasurementCounts(measureCalls, intrinsicCalls, sizeCalculationCalls)

  fun measurePolicy(size: Density.() -> IntSize): MeasurePolicy = object : MeasurePolicy {
    override fun MeasureScope.measure(
      measurables: List<Measurable>,
      constraints: Constraints,
    ): MeasureResult {
      measureCalls++
      val requestedSize = size()
      return layout(
        requestedSize.width.coerceIn(constraints.minWidth, constraints.maxWidth),
        requestedSize.height.coerceIn(constraints.minHeight, constraints.maxHeight),
      ) {}
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
      measurables: List<IntrinsicMeasurable>,
      height: Int,
    ): Int {
      intrinsicCalls++
      return size().width
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
      measurables: List<IntrinsicMeasurable>,
      height: Int,
    ): Int {
      intrinsicCalls++
      return size().width
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
      measurables: List<IntrinsicMeasurable>,
      width: Int,
    ): Int {
      intrinsicCalls++
      return size().height
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
      measurables: List<IntrinsicMeasurable>,
      width: Int,
    ): Int {
      intrinsicCalls++
      return size().height
    }
  }
}

data class MeasurementCounts(
  val measures: Int,
  val intrinsics: Int,
  val sizeCalculations: Int,
)
