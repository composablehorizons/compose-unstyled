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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round

@Composable
internal fun FloatingContent(
  floatingContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  side: AnchorSide = AnchorSide.Top,
  alignment: AnchorAlignment = AnchorAlignment.Center,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
  onOffsetFromIdealPositionChanged: (IntOffset) -> Unit = {},
  anchor: @Composable () -> Unit,
) {
  val layoutDirection = LocalLayoutDirection.current
  val density = LocalDensity.current
  val windowSize = currentWindowContainerSize().toIntSize(density)
  var anchorBounds by remember { mutableStateOf<AnchorBounds?>(null) }

  Layout(
    content = anchor,
    modifier = modifier.onGloballyPositioned {
      anchorBounds = if (it.size == IntSize.Zero) {
        null
      } else {
        AnchorBounds(
          positionInWindow = it.positionInWindow().round(),
          size = it.size,
        )
      }
    },
  ) { measurables, constraints ->
    val anchorPlaceable = measurables.firstOrNull()?.measure(constraints)

    if (anchorPlaceable == null) {
      return@Layout layout(0, 0) {}
    }

    layout(anchorPlaceable.width, anchorPlaceable.height) {
      anchorPlaceable.place(0, 0)
    }
  }

  val currentAnchorBounds = anchorBounds
  if (currentAnchorBounds != null) {
    Overlay {
      FloatingOverlayContent(
        anchorBounds = currentAnchorBounds,
        side = side,
        alignment = alignment,
        sideOffset = sideOffset,
        alignmentOffset = alignmentOffset,
        onOffsetFromIdealPositionChanged = onOffsetFromIdealPositionChanged,
        density = density,
        layoutDirection = layoutDirection,
        windowSize = windowSize,
        floatingContent = floatingContent,
      )
    }
  }
}

@Composable
private fun FloatingOverlayContent(
  anchorBounds: AnchorBounds,
  side: AnchorSide,
  alignment: AnchorAlignment,
  sideOffset: Dp,
  alignmentOffset: Dp,
  onOffsetFromIdealPositionChanged: (IntOffset) -> Unit,
  density: Density,
  layoutDirection: LayoutDirection,
  windowSize: IntSize,
  floatingContent: @Composable () -> Unit,
) {
  var overlayPositionInWindow by remember { mutableStateOf<IntOffset?>(null) }

  Layout(
    content = floatingContent,
    modifier = Modifier
      .onGloballyPositioned {
        overlayPositionInWindow = it.positionInWindow().round()
      },
  ) { measurables, constraints ->
    val floatingPlaceable = measurables.firstOrNull()?.measure(Constraints())

    if (floatingPlaceable == null) {
      return@Layout layout(0, 0) {}
    }
    val overlayPosition = overlayPositionInWindow

    if (overlayPosition == null) {
      return@Layout layout(constraints.maxWidth, constraints.maxHeight) {}
    }

    val contentPosition = calculateAnchoredPosition(
      density = density,
      anchorBounds = anchorBounds.toIntRect(),
      windowSize = windowSize,
      layoutDirection = layoutDirection,
      contentSize = IntSize(floatingPlaceable.width, floatingPlaceable.height),
      side = side,
      alignment = alignment,
      sideOffset = sideOffset,
      alignmentOffset = alignmentOffset,
    )

    val overlayX = contentPosition.x - overlayPosition.x
    val overlayY = contentPosition.y - overlayPosition.y

    layout(constraints.maxWidth, constraints.maxHeight) {
      onOffsetFromIdealPositionChanged(IntOffset.Zero)
      floatingPlaceable.place(overlayX, overlayY)
    }
  }
}

private data class AnchorBounds(
  val positionInWindow: IntOffset,
  val size: IntSize,
) {
  val left: Int get() = positionInWindow.x
  val top: Int get() = positionInWindow.y
  val width: Int get() = size.width
  val height: Int get() = size.height
  val right: Int get() = left + width
  val bottom: Int get() = top + height

  fun toIntRect(): IntRect = IntRect(
    left = left,
    top = top,
    right = right,
    bottom = bottom,
  )
}

private fun DpSize.toIntSize(density: Density): IntSize {
  return with(density) {
    IntSize(width.roundToPx(), height.roundToPx())
  }
}
