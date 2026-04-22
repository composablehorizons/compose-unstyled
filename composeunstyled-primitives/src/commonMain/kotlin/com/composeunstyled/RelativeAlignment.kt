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
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

sealed interface RelativeAlignment {
  object TopStart : RelativeAlignment
  object TopCenter : RelativeAlignment
  object TopEnd : RelativeAlignment

  object CenterStart : RelativeAlignment
  object CenterEnd : RelativeAlignment

  object BottomStart : RelativeAlignment
  object BottomCenter : RelativeAlignment
  object BottomEnd : RelativeAlignment
}

@Immutable
internal data class RelativePositionProvider(
  val density: Density,
  val anchor: RelativeAlignment,
) : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize,
  ): IntOffset {
    val x = when (anchor) {
      RelativeAlignment.TopStart, RelativeAlignment.BottomStart -> {
        if (layoutDirection == LayoutDirection.Ltr) {
          anchorBounds.left
        } else {
          anchorBounds.right - popupContentSize.width
        }
      }

      RelativeAlignment.CenterStart -> {
        // CenterStart: popup positioned to the LEFT of anchor
        if (layoutDirection == LayoutDirection.Ltr) {
          anchorBounds.left - popupContentSize.width
        } else {
          anchorBounds.right
        }
      }

      RelativeAlignment.TopEnd, RelativeAlignment.BottomEnd -> {
        if (layoutDirection == LayoutDirection.Ltr) {
          anchorBounds.right - popupContentSize.width
        } else {
          anchorBounds.left
        }
      }

      RelativeAlignment.CenterEnd -> {
        // CenterEnd: popup positioned to the RIGHT of anchor
        if (layoutDirection == LayoutDirection.Ltr) {
          anchorBounds.right
        } else {
          anchorBounds.left - popupContentSize.width
        }
      }

      RelativeAlignment.TopCenter, RelativeAlignment.BottomCenter -> {
        anchorBounds.left + (anchorBounds.width / 2) - (popupContentSize.width / 2)
      }
    }

    val y = when (anchor) {
      RelativeAlignment.TopStart, RelativeAlignment.TopEnd, RelativeAlignment.TopCenter -> {
        anchorBounds.top - popupContentSize.height
      }

      RelativeAlignment.CenterStart, RelativeAlignment.CenterEnd -> {
        anchorBounds.top + (anchorBounds.height / 2) - (popupContentSize.height / 2)
      }

      RelativeAlignment.BottomStart, RelativeAlignment.BottomEnd, RelativeAlignment.BottomCenter -> {
        anchorBounds.bottom
      }
    }

    // Clamp to window bounds to prevent overflow
    val clampedX = x.coerceAtLeast(0).coerceAtMost(windowSize.width - popupContentSize.width)
    val clampedY = y.coerceAtLeast(0).coerceAtMost(windowSize.height - popupContentSize.height)

    return IntOffset(clampedX, clampedY)
  }
}
