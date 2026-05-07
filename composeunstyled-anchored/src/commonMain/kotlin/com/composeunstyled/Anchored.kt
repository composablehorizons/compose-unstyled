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

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

enum class AnchorSide {
  Top,
  Bottom,
  Start,
  End,
}

enum class AnchorAlignment {
  Start,
  Center,
  End,
}

fun calculateAnchoredPosition(
  density: Density,
  anchorBounds: IntRect,
  windowSize: IntSize,
  layoutDirection: LayoutDirection,
  contentSize: IntSize,
  side: AnchorSide,
  alignment: AnchorAlignment,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
): IntOffset {
  val x = when (side) {
    AnchorSide.Top, AnchorSide.Bottom -> when (alignment) {
      AnchorAlignment.Start -> if (layoutDirection == LayoutDirection.Ltr) {
        anchorBounds.left
      } else {
        anchorBounds.right - contentSize.width
      }

      AnchorAlignment.Center -> anchorBounds.left + anchorBounds.width / 2 - contentSize.width / 2
      AnchorAlignment.End -> if (layoutDirection == LayoutDirection.Ltr) {
        anchorBounds.right - contentSize.width
      } else {
        anchorBounds.left
      }
    }

    AnchorSide.Start -> if (layoutDirection == LayoutDirection.Ltr) {
      anchorBounds.left - contentSize.width
    } else {
      anchorBounds.right
    }

    AnchorSide.End -> if (layoutDirection == LayoutDirection.Ltr) {
      anchorBounds.right
    } else {
      anchorBounds.left - contentSize.width
    }
  }

  val y = when (side) {
    AnchorSide.Top -> anchorBounds.top - contentSize.height
    AnchorSide.Bottom -> anchorBounds.bottom
    AnchorSide.Start, AnchorSide.End -> when (alignment) {
      AnchorAlignment.Start -> anchorBounds.top
      AnchorAlignment.Center -> anchorBounds.top + anchorBounds.height / 2 - contentSize.height / 2
      AnchorAlignment.End -> anchorBounds.bottom - contentSize.height
    }
  }

  val isLtr = layoutDirection == LayoutDirection.Ltr
  val sideOffsetPx = with(density) { sideOffset.roundToPx() }
  val alignmentOffsetPx = with(density) { alignmentOffset.roundToPx() }

  val offsetX = when (side) {
    AnchorSide.Top, AnchorSide.Bottom -> alignmentOffsetPx * if (isLtr) 1 else -1
    AnchorSide.Start -> if (isLtr) -sideOffsetPx else sideOffsetPx
    AnchorSide.End -> if (isLtr) sideOffsetPx else -sideOffsetPx
  }
  val offsetY = when (side) {
    AnchorSide.Top -> -sideOffsetPx
    AnchorSide.Bottom -> sideOffsetPx
    AnchorSide.Start, AnchorSide.End -> alignmentOffsetPx
  }

  return IntOffset(
    x = (x + offsetX).coerceIn(0, windowSize.width - contentSize.width),
    y = (y + offsetY).coerceIn(0, windowSize.height - contentSize.height),
  )
}
