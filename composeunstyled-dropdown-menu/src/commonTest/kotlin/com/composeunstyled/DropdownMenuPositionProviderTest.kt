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

class DropdownMenuPositionProviderTest {
  private val anchorBounds = IntRect(left = 100, top = 50, right = 180, bottom = 90)
  private val windowSize = IntSize(width = 500, height = 500)
  private val popupContentSize = IntSize(width = 60, height = 40)

  @Test
  fun appliesOffsetsBeforeClamping() {
    val position = positionProvider(
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupContentSize,
    )

    assertEquals(IntOffset(x = 112, y = 98), position)
  }

  @Test
  fun mirrorsAlignOffsetInRtl() {
    val position = positionProvider(
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupContentSize,
    )

    assertEquals(IntOffset(x = 108, y = 98), position)
  }

  @Test
  fun appliesSideOffsetAwayFromAnchorSide() {
    val position = positionProvider(
      side = AnchorSide.Top,
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupContentSize,
    )

    assertEquals(IntOffset(x = 112, y = 2), position)
  }

  @Test
  fun appliesSideOffsetAwayFromCenterAnchorSide() {
    val startPosition = positionProvider(
      side = AnchorSide.Start,
      alignment = AnchorAlignment.Center,
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupContentSize,
    )
    val endPosition = positionProvider(
      side = AnchorSide.End,
      alignment = AnchorAlignment.Center,
      sideOffset = 8.dp,
      alignmentOffset = 12.dp,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupContentSize,
    )

    assertEquals(IntOffset(x = 32, y = 62), startPosition)
    assertEquals(IntOffset(x = 188, y = 62), endPosition)
  }

  @Test
  fun alignsHorizontallyToAnchorCenter() {
    val position = positionProvider(
      alignment = AnchorAlignment.Center,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupContentSize,
    )

    assertEquals(IntOffset(x = 110, y = 90), position)
  }

  @Test
  fun clampsOffsetPositionInsideWindow() {
    val position = positionProvider(
      side = AnchorSide.Top,
      sideOffset = 200.dp,
      alignmentOffset = (-200).dp,
    ).calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = IntSize(width = 120, height = 80),
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupContentSize,
    )

    assertEquals(IntOffset(x = 0, y = 0), position)
  }

  private fun positionProvider(
    side: AnchorSide = AnchorSide.Bottom,
    alignment: AnchorAlignment = AnchorAlignment.Start,
    sideOffset: Dp = 0.dp,
    alignmentOffset: Dp = 0.dp,
  ) = MenuContentPositionProvider(
    density = Density(1f),
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
  )
}
