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
import kotlin.test.Test
import kotlin.test.assertEquals

class AnchoredPositionTest {
  private val anchorBounds = IntRect(left = 100, top = 50, right = 180, bottom = 90)
  private val windowSize = IntSize(width = 500, height = 500)
  private val contentSize = IntSize(width = 60, height = 40)

  @Test
  fun appliesOffsetsBeforeClamping() {
    val position = calculatePosition(
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    )

    assertEquals(IntOffset(x = 112, y = 98), position)
  }

  @Test
  fun mirrorsAlignmentOffsetInRtl() {
    val position = calculatePosition(
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
      layoutDirection = LayoutDirection.Rtl,
    )

    assertEquals(IntOffset(x = 108, y = 98), position)
  }

  @Test
  fun appliesSideOffsetAwayFromAnchorSide() {
    val position = calculatePosition(
      side = AnchorSide.Top,
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    )

    assertEquals(IntOffset(x = 112, y = 2), position)
  }

  @Test
  fun appliesSideOffsetAwayFromHorizontalAnchorSide() {
    val startPosition = calculatePosition(
      side = AnchorSide.Start,
      alignment = AnchorAlignment.Center,
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    )
    val endPosition = calculatePosition(
      side = AnchorSide.End,
      alignment = AnchorAlignment.Center,
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    )

    assertEquals(IntOffset(x = 32, y = 62), startPosition)
    assertEquals(IntOffset(x = 188, y = 62), endPosition)
  }

  @Test
  fun alignsToAnchorCenter() {
    val position = calculatePosition(
      alignment = AnchorAlignment.Center,
    )

    assertEquals(IntOffset(x = 110, y = 90), position)
  }

  @Test
  fun clampsOffsetPositionInsideWindow() {
    val position = calculatePosition(
      side = AnchorSide.Top,
      sideOffset = 200.dp,
      alignmentOffset = (-200).dp,
      windowSize = IntSize(width = 120, height = 80),
    )

    assertEquals(IntOffset(x = 0, y = 0), position)
  }

  @Test
  fun reportsPositionAdjustmentWhenClamped() {
    val position = calculateFloatingPlacement(
      density = Density(1f),
      anchorBounds = anchorBounds,
      windowSize = IntSize(width = 120, height = 80),
      layoutDirection = LayoutDirection.Ltr,
      contentSize = contentSize,
      side = AnchorSide.Top,
      alignment = AnchorAlignment.Start,
      sideOffset = 200.dp,
      alignmentOffset = (-200).dp,
    )

    assertEquals(IntOffset(x = 0, y = 0), position.position)
    assertEquals(IntOffset(x = 100, y = 190), position.positionAdjustment)
  }

  @Test
  fun clampsOversizedContentToWindowOrigin() {
    val position = calculatePosition(
      windowSize = IntSize(width = 80, height = 40),
      contentSize = IntSize(width = 160, height = 120),
    )

    assertEquals(IntOffset(x = 0, y = 0), position)
  }

  private fun calculatePosition(
    side: AnchorSide = AnchorSide.Bottom,
    alignment: AnchorAlignment = AnchorAlignment.Start,
    sideOffset: Dp = 0.dp,
    alignmentOffset: Dp = 0.dp,
    windowSize: IntSize = this.windowSize,
    contentSize: IntSize = this.contentSize,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
  ) = calculateFloatingPlacement(
    density = Density(1f),
    anchorBounds = anchorBounds,
    windowSize = windowSize,
    layoutDirection = layoutDirection,
    contentSize = contentSize,
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
  ).position
}
