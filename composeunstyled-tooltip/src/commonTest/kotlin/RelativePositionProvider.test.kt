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
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class RelativePositionTest {
  private val density = Density(1f, 1f)
  private val anchorBounds = IntRect(left = 100, top = 100, right = 200, bottom = 150)
  private val windowSize = IntSize(1000, 1000)
  private val popupSize = IntSize(150, 80)

  @Test
  fun topStartPositionsPopoverAboveAndAlignedToStart() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopStart)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.left)
    assertThat(position.y).isEqualTo(anchorBounds.top - popupSize.height)
  }

  @Test
  fun topEndPositionsPopoverAboveAndAlignedToEnd() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopEnd)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.right - popupSize.width)
    assertThat(position.y).isEqualTo(anchorBounds.top - popupSize.height)
  }

  @Test
  fun bottomStartPositionsPopoverBelowAndAlignedToStart() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomStart)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.left)
    assertThat(position.y).isEqualTo(anchorBounds.bottom)
  }

  @Test
  fun bottomEndPositionsPopoverBelowAndAlignedToEnd() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomEnd)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.right - popupSize.width)
    assertThat(position.y).isEqualTo(anchorBounds.bottom)
  }

  @Test
  fun centerStartPositionsPopoverCenteredVerticallyAndLeftOfAnchor() {
    val provider = RelativePositionProvider(density, RelativeAlignment.CenterStart)

    // Use anchor bounds with enough space on the left for the popup
    val anchorWithSpace = IntRect(left = 300, top = 100, right = 400, bottom = 150)

    val position = provider.calculatePosition(
      anchorBounds = anchorWithSpace,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    // CenterStart: popup positioned to the LEFT of anchor (right edge touches anchor's left edge)
    assertThat(position.x).isEqualTo(anchorWithSpace.left - popupSize.width)
    assertThat(position.y).isEqualTo(
      anchorWithSpace.top + (anchorWithSpace.height / 2) - (popupSize.height / 2),
    )
  }

  @Test
  fun centerEndPositionsPopoverCenteredVerticallyAndRightOfAnchor() {
    val provider = RelativePositionProvider(density, RelativeAlignment.CenterEnd)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    // CenterEnd: popup positioned to the RIGHT of anchor (left edge touches anchor's right edge)
    assertThat(position.x).isEqualTo(anchorBounds.right)
    assertThat(
      position.y,
    ).isEqualTo(anchorBounds.top + (anchorBounds.height / 2) - (popupSize.height / 2))
  }

  // RTL tests
  @Test
  fun topStartPositionsPopoverAboveAndAlignedToEndInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopStart)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.right - popupSize.width)
    assertThat(position.y).isEqualTo(anchorBounds.top - popupSize.height)
  }

  @Test
  fun topEndPositionsPopoverAboveAndAlignedToStartInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopEnd)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.left)
    assertThat(position.y).isEqualTo(anchorBounds.top - popupSize.height)
  }

  @Test
  fun bottomStartPositionsPopoverBelowAndAlignedToEndInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomStart)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.right - popupSize.width)
    assertThat(position.y).isEqualTo(anchorBounds.bottom)
  }

  @Test
  fun bottomEndPositionsPopoverBelowAndAlignedToStartInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomEnd)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    assertThat(position.x).isEqualTo(anchorBounds.left)
    assertThat(position.y).isEqualTo(anchorBounds.bottom)
  }

  @Test
  fun centerStartPositionsPopoverCenteredVerticallyAndRightOfAnchorInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.CenterStart)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    // CenterStart in RTL: popup positioned to the RIGHT of anchor (mirrored)
    assertThat(position.x).isEqualTo(anchorBounds.right)
    assertThat(
      position.y,
    ).isEqualTo(anchorBounds.top + (anchorBounds.height / 2) - (popupSize.height / 2))
  }

  @Test
  fun centerEndPositionsPopoverCenteredVerticallyAndLeftOfAnchorInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.CenterEnd)

    // Use anchor bounds with enough space on the left for the popup
    val anchorWithSpace = IntRect(left = 300, top = 100, right = 400, bottom = 150)

    val position = provider.calculatePosition(
      anchorBounds = anchorWithSpace,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    // CenterEnd in RTL: popup positioned to the LEFT of anchor (mirrored)
    assertThat(position.x).isEqualTo(anchorWithSpace.left - popupSize.width)
    assertThat(position.y).isEqualTo(
      anchorWithSpace.top + (anchorWithSpace.height / 2) - (popupSize.height / 2),
    )
  }

  @Test
  fun topCenterPositionsPopoverAboveAndCenteredHorizontally() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopCenter)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    assertThat(
      position.x,
    ).isEqualTo(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2))
    assertThat(position.y).isEqualTo(anchorBounds.top - popupSize.height)
  }

  @Test
  fun bottomCenterPositionsPopoverBelowAndCenteredHorizontally() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomCenter)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = popupSize,
    )

    assertThat(
      position.x,
    ).isEqualTo(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2))
    assertThat(position.y).isEqualTo(anchorBounds.bottom)
  }

  @Test
  fun topCenterPositionsPopoverAboveAndCenteredHorizontallyInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopCenter)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    assertThat(
      position.x,
    ).isEqualTo(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2))
    assertThat(position.y).isEqualTo(anchorBounds.top - popupSize.height)
  }

  @Test
  fun bottomCenterPositionsPopoverBelowAndCenteredHorizontallyInRtl() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomCenter)

    val position = provider.calculatePosition(
      anchorBounds = anchorBounds,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Rtl,
      popupContentSize = popupSize,
    )

    assertThat(
      position.x,
    ).isEqualTo(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2))
    assertThat(position.y).isEqualTo(anchorBounds.bottom)
  }

  // Edge clamping tests
  @Test
  fun topStartClampsToWindowWhenOverflowingTop() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopStart)

    val anchorNearTop = IntRect(left = 100, top = 30, right = 200, bottom = 80)
    val largePopup = IntSize(150, 100)

    val position = provider.calculatePosition(
      anchorBounds = anchorNearTop,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = largePopup,
    )

    assertThat(position.x).isEqualTo(anchorNearTop.left)
    assertThat(position.y).isEqualTo(0)
  }

  @Test
  fun topEndClampsToWindowWhenOverflowingLeft() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopEnd)

    val anchorNearLeft = IntRect(left = 10, top = 200, right = 60, bottom = 250)
    val largePopup = IntSize(150, 100)

    val position = provider.calculatePosition(
      anchorBounds = anchorNearLeft,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = largePopup,
    )

    // TopEnd would position at anchorNearLeft.right - largePopup.width = 60 - 150 = -90
    // Should clamp to 0
    assertThat(position.x).isEqualTo(0)
    assertThat(position.y).isEqualTo(anchorNearLeft.top - largePopup.height)
  }

  @Test
  fun bottomEndClampsToWindowWhenOverflowingBottom() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomEnd)

    val anchorNearBottom = IntRect(left = 100, top = 950, right = 200, bottom = 990)
    val largePopup = IntSize(150, 100)

    val position = provider.calculatePosition(
      anchorBounds = anchorNearBottom,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = largePopup,
    )

    assertThat(position.x).isEqualTo(anchorNearBottom.right - largePopup.width)
    assertThat(position.y).isEqualTo(windowSize.height - largePopup.height)
  }

  @Test
  fun bottomStartClampsToWindowWhenOverflowingRight() {
    val provider = RelativePositionProvider(density, RelativeAlignment.BottomStart)

    val anchorNearRight = IntRect(left = 900, top = 200, right = 950, bottom = 250)
    val largePopup = IntSize(150, 100)

    val position = provider.calculatePosition(
      anchorBounds = anchorNearRight,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = largePopup,
    )

    // BottomStart would position at anchorNearRight.left = 900
    // Popup extends to 900 + 150 = 1050, which exceeds window width (1000)
    // Should clamp to windowSize.width - largePopup.width = 1000 - 150 = 850
    assertThat(position.x).isEqualTo(windowSize.width - largePopup.width)
    assertThat(position.y).isEqualTo(anchorNearRight.bottom)
  }

  @Test
  fun topEndClampsToWindowWhenOverflowingMultipleEdges() {
    val provider = RelativePositionProvider(density, RelativeAlignment.TopEnd)

    val anchorAtTopLeft = IntRect(left = 10, top = 20, right = 60, bottom = 70)
    val largePopup = IntSize(150, 100)

    val position = provider.calculatePosition(
      anchorBounds = anchorAtTopLeft,
      windowSize = windowSize,
      layoutDirection = LayoutDirection.Ltr,
      popupContentSize = largePopup,
    )

    // TopEnd: x = anchorAtTopLeft.right - largePopup.width = 60 - 150 = -90 → clamped to 0
    // TopEnd: y = anchorAtTopLeft.top - largePopup.height = 20 - 100 = -80 → clamped to 0
    assertThat(position.x).isEqualTo(0)
    assertThat(position.y).isEqualTo(0)
  }
}
