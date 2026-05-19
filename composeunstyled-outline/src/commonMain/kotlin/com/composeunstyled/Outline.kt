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

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.outline(
  width: Dp,
  color: Color,
  shape: Shape = RectangleShape,
  offset: Dp = 0.dp,
): Modifier {
  return drawBehind {
    val strokeWidth = width.toPx()
    when (val outline = shape.createOutline(size, layoutDirection, this)) {
      is Outline.Generic -> {
        // not supported
      }

      is Outline.Rectangle -> {
        val geometry = calculateOutlineRectGeometry(
          rect = Rect(0f, 0f, size.width, size.height),
          strokeWidth = strokeWidth,
          offset = offset.toPx(),
        )

        val path = Path().apply {
          addRect(geometry.outer)
          fillType = PathFillType.EvenOdd
          addRect(geometry.inner)
        }

        drawPath(path, color)
      }

      is Outline.Rounded -> {
        val geometry = calculateOutlineRoundRectGeometry(
          roundRect = outline.roundRect,
          strokeWidth = strokeWidth,
          offset = offset.toPx(),
        )

        val path = Path().apply {
          addRoundRect(geometry.outer)
          fillType = PathFillType.EvenOdd
          addRoundRect(geometry.inner)
        }

        drawPath(path, color)
      }
    }
  }
}

internal data class OutlineRectGeometry(
  val inner: Rect,
  val outer: Rect,
)

internal data class OutlineRoundRectGeometry(
  val inner: RoundRect,
  val outer: RoundRect,
)

internal fun calculateOutlineRectGeometry(
  rect: Rect,
  strokeWidth: Float,
  offset: Float,
): OutlineRectGeometry {
  return OutlineRectGeometry(
    inner = rect.inflate(offset),
    outer = rect.inflate(offset + strokeWidth),
  )
}

internal fun calculateOutlineRoundRectGeometry(
  roundRect: RoundRect,
  strokeWidth: Float,
  offset: Float,
): OutlineRoundRectGeometry {
  return OutlineRoundRectGeometry(
    inner = roundRect.inflate(offset),
    outer = roundRect.inflate(offset + strokeWidth),
  )
}

private fun Rect.inflate(amount: Float): Rect {
  return Rect(
    left = left - amount,
    top = top - amount,
    right = right + amount,
    bottom = bottom + amount,
  )
}

private fun RoundRect.inflate(amount: Float): RoundRect {
  return RoundRect(
    left = left - amount,
    top = top - amount,
    right = right + amount,
    bottom = bottom + amount,
    topLeftCornerRadius = topLeftCornerRadius.inflate(amount),
    topRightCornerRadius = topRightCornerRadius.inflate(amount),
    bottomRightCornerRadius = bottomRightCornerRadius.inflate(amount),
    bottomLeftCornerRadius = bottomLeftCornerRadius.inflate(amount),
  )
}

private fun CornerRadius.inflate(amount: Float): CornerRadius {
  return CornerRadius(
    x = (x + amount).coerceAtLeast(0f),
    y = (y + amount).coerceAtLeast(0f),
  )
}
