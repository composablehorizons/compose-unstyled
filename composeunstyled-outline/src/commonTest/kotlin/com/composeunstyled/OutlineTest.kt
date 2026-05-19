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

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class OutlineTest {
  @Test
  fun rectangleOffsetExpandsInnerAndOuterBounds() {
    val geometry = calculateOutlineRectGeometry(
      rect = Rect(left = 0f, top = 0f, right = 100f, bottom = 40f),
      strokeWidth = 2f,
      offset = 4f,
    )

    assertThat(geometry.inner).isEqualTo(Rect(left = -4f, top = -4f, right = 104f, bottom = 44f))
    assertThat(geometry.outer).isEqualTo(Rect(left = -6f, top = -6f, right = 106f, bottom = 46f))
  }

  @Test
  fun roundedOffsetExpandsCornerRadii() {
    val geometry = calculateOutlineRoundRectGeometry(
      roundRect = RoundRect(
        left = 0f,
        top = 0f,
        right = 100f,
        bottom = 40f,
        cornerRadius = CornerRadius(8f, 8f),
      ),
      strokeWidth = 2f,
      offset = 4f,
    )

    assertThat(geometry.inner.topLeftCornerRadius).isEqualTo(CornerRadius(12f, 12f))
    assertThat(geometry.outer.topLeftCornerRadius).isEqualTo(CornerRadius(14f, 14f))
  }

  @Test
  fun roundedOffsetPreservesAsymmetricCornerRadii() {
    val geometry = calculateOutlineRoundRectGeometry(
      roundRect = RoundRect(
        left = 0f,
        top = 0f,
        right = 100f,
        bottom = 40f,
        topLeftCornerRadius = CornerRadius(8f, 4f),
        topRightCornerRadius = CornerRadius(10f, 6f),
        bottomRightCornerRadius = CornerRadius(12f, 8f),
        bottomLeftCornerRadius = CornerRadius(14f, 10f),
      ),
      strokeWidth = 2f,
      offset = 4f,
    )

    assertThat(geometry.inner.topLeftCornerRadius).isEqualTo(CornerRadius(12f, 8f))
    assertThat(geometry.outer.topLeftCornerRadius).isEqualTo(CornerRadius(14f, 10f))
    assertThat(geometry.inner.bottomRightCornerRadius).isEqualTo(CornerRadius(16f, 12f))
    assertThat(geometry.outer.bottomRightCornerRadius).isEqualTo(CornerRadius(18f, 14f))
  }
}
