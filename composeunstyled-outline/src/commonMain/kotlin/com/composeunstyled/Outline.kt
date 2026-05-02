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

import androidx.compose.foundation.border
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

/**
 * Draws an outline outside the composable's bounds.
 *
 * Unlike [border], this modifier does not affect layout or size.
 *
 * The final outline shape is based on the given [shape]'s corner radius and the [offset]
 *
 * Only [Outline.Rectangle] and [Outline.Rounded] are supported. [Outline.Generic] is ignored.
 *
 *
 * @param width the thickness of the outline
 * @param color the color of the outline
 * @param shape the shape of the composable. This is *not* the shape of the outline
 * @param offset the distance between the composable and the outline
 */
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
        val inset = -offset.toPx()

        val path = Path().apply {
          addRect(
            Rect(
              left = inset - strokeWidth,
              top = inset - strokeWidth,
              right = size.width - inset + strokeWidth,
              bottom = size.height - inset + strokeWidth,
            ),
          )
          fillType = PathFillType.EvenOdd
          addRect(
            Rect(
              left = inset,
              top = inset,
              right = size.width - inset,
              bottom = size.height - inset,
            ),
          )
        }

        drawPath(path, color)
      }

      is Outline.Rounded -> {
        val inset = -offset.toPx()
        val roundRect = outline.roundRect

        val topLeftRadius = roundRect.topLeftCornerRadius.x
        val topRightRadius = roundRect.topRightCornerRadius.x
        val bottomRightRadius = roundRect.bottomRightCornerRadius.x
        val bottomLeftRadius = roundRect.bottomLeftCornerRadius.y

        val topLeftOutlineRadius = topLeftRadius + strokeWidth
        val topRightOutlineRadius = topRightRadius + strokeWidth
        val bottomRightOutlineRadius = bottomRightRadius + strokeWidth
        val bottomLeftOutlineRadius = bottomLeftRadius + strokeWidth

        val path = Path().apply {
          addRoundRect(
            RoundRect(
              left = inset - strokeWidth,
              top = inset - strokeWidth,
              right = size.width - inset + strokeWidth,
              bottom = size.height - inset + strokeWidth,
              topLeftCornerRadius = CornerRadius(topLeftOutlineRadius, topLeftOutlineRadius),
              topRightCornerRadius = CornerRadius(topRightOutlineRadius, topRightOutlineRadius),
              bottomRightCornerRadius = CornerRadius(
                bottomRightOutlineRadius,
                bottomRightOutlineRadius,
              ),
              bottomLeftCornerRadius = CornerRadius(
                bottomLeftOutlineRadius,
                bottomLeftOutlineRadius,
              ),
            ),
          )
          fillType = PathFillType.EvenOdd
          addRoundRect(
            RoundRect(
              left = inset,
              top = inset,
              right = size.width - inset,
              bottom = size.height - inset,
              topLeftCornerRadius = CornerRadius(topLeftRadius, topLeftRadius),
              topRightCornerRadius = CornerRadius(topRightRadius, topRightRadius),
              bottomRightCornerRadius = CornerRadius(bottomRightRadius, bottomRightRadius),
              bottomLeftCornerRadius = CornerRadius(bottomLeftRadius, bottomLeftRadius),
            ),
          )
        }

        drawPath(path, color)
      }
    }
  }
}
