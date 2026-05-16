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
fun AnchoredLayout(
  modifier: Modifier = Modifier,
  content: @Composable (anchorBounds: IntRect, windowSize: IntSize) -> Unit,
  anchor: @Composable () -> Unit,
) {
  val density = LocalDensity.current
  val windowSize = currentWindowContainerSize().toIntSize(density)
  var anchorBounds by remember { mutableStateOf<IntRect?>(null) }

  Layout(
    content = anchor,
    modifier = modifier.onGloballyPositioned { coordinates ->
      anchorBounds = if (coordinates.size == IntSize.Zero) {
        null
      } else {
        val positionInWindow = coordinates.positionInWindow().round()
        IntRect(
          left = positionInWindow.x,
          top = positionInWindow.y,
          right = positionInWindow.x + coordinates.size.width,
          bottom = positionInWindow.y + coordinates.size.height,
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
    content(currentAnchorBounds, windowSize)
  }
}

@Composable
fun AnchoredFloatingContent(
  layer: @Composable (content: @Composable () -> Unit) -> Unit,
  content: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  contentModifier: Modifier = Modifier,
  side: AnchorSide = AnchorSide.Top,
  alignment: AnchorAlignment = AnchorAlignment.Center,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
  onPlaced: (FloatingPlacement) -> Unit = {},
  anchor: @Composable () -> Unit,
) {
  val density = LocalDensity.current
  val layoutDirection = LocalLayoutDirection.current

  AnchoredLayout(
    modifier = modifier,
    content = { anchorBounds, windowSize ->
      layer {
        AnchoredContent(
          anchorBounds = anchorBounds,
          windowSize = windowSize,
          modifier = contentModifier,
          side = side,
          alignment = alignment,
          sideOffset = sideOffset,
          alignmentOffset = alignmentOffset,
          density = density,
          layoutDirection = layoutDirection,
          onPlaced = onPlaced,
          content = content,
        )
      }
    },
    anchor = anchor,
  )
}

@Composable
fun AnchoredContent(
  anchorBounds: IntRect,
  windowSize: IntSize,
  modifier: Modifier = Modifier,
  side: AnchorSide,
  alignment: AnchorAlignment,
  sideOffset: Dp,
  alignmentOffset: Dp,
  density: Density,
  layoutDirection: LayoutDirection,
  onPlaced: (FloatingPlacement) -> Unit,
  content: @Composable () -> Unit,
) {
  var containerPositionInWindow by remember { mutableStateOf<IntOffset?>(null) }

  Layout(
    content = content,
    modifier = modifier.onGloballyPositioned {
      containerPositionInWindow = it.positionInWindow().round()
    },
  ) { measurables, constraints ->
    val contentPlaceable = measurables.firstOrNull()?.measure(Constraints())

    if (contentPlaceable == null) {
      return@Layout layout(0, 0) {}
    }

    val containerPosition = containerPositionInWindow

    if (containerPosition == null) {
      return@Layout layout(constraints.maxWidth, constraints.maxHeight) {}
    }

    val placement = calculateFloatingPlacement(
      density = density,
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = layoutDirection,
      contentSize = IntSize(contentPlaceable.width, contentPlaceable.height),
      side = side,
      alignment = alignment,
      sideOffset = sideOffset,
      alignmentOffset = alignmentOffset,
    )
    val contentPosition = placement.position

    layout(constraints.maxWidth, constraints.maxHeight) {
      onPlaced(placement)
      contentPlaceable.place(
        x = contentPosition.x - containerPosition.x,
        y = contentPosition.y - containerPosition.y,
      )
    }
  }
}

private fun DpSize.toIntSize(density: Density): IntSize {
  return with(density) {
    IntSize(width.roundToPx(), height.roundToPx())
  }
}
