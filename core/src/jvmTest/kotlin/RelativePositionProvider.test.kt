package com.composeunstyled

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.composeunstyled.RelativeAlignment
import com.composeunstyled.RelativePositionProvider
import kotlin.test.Test
import kotlin.test.assertEquals

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
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left, position.x)
        assertEquals(anchorBounds.top - popupSize.height, position.y)
    }

    @Test
    fun topEndPositionsPopoverAboveAndAlignedToEnd() {
        val provider = RelativePositionProvider(density, RelativeAlignment.TopEnd)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.right - popupSize.width, position.x)
        assertEquals(anchorBounds.top - popupSize.height, position.y)
    }

    @Test
    fun bottomStartPositionsPopoverBelowAndAlignedToStart() {
        val provider = RelativePositionProvider(density, RelativeAlignment.BottomStart)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left, position.x)
        assertEquals(anchorBounds.bottom, position.y)
    }

    @Test
    fun bottomEndPositionsPopoverBelowAndAlignedToEnd() {
        val provider = RelativePositionProvider(density, RelativeAlignment.BottomEnd)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.right - popupSize.width, position.x)
        assertEquals(anchorBounds.bottom, position.y)
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
            popupContentSize = popupSize
        )

        // CenterStart: popup positioned to the LEFT of anchor (right edge touches anchor's left edge)
        assertEquals(anchorWithSpace.left - popupSize.width, position.x)
        assertEquals(anchorWithSpace.top + (anchorWithSpace.height / 2) - (popupSize.height / 2), position.y)
    }

    @Test
    fun centerEndPositionsPopoverCenteredVerticallyAndRightOfAnchor() {
        val provider = RelativePositionProvider(density, RelativeAlignment.CenterEnd)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = popupSize
        )

        // CenterEnd: popup positioned to the RIGHT of anchor (left edge touches anchor's right edge)
        assertEquals(anchorBounds.right, position.x)
        assertEquals(anchorBounds.top + (anchorBounds.height / 2) - (popupSize.height / 2), position.y)
    }

    // RTL tests
    @Test
    fun topStartPositionsPopoverAboveAndAlignedToEndInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.TopStart)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.right - popupSize.width, position.x)
        assertEquals(anchorBounds.top - popupSize.height, position.y)
    }

    @Test
    fun topEndPositionsPopoverAboveAndAlignedToStartInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.TopEnd)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left, position.x)
        assertEquals(anchorBounds.top - popupSize.height, position.y)
    }

    @Test
    fun bottomStartPositionsPopoverBelowAndAlignedToEndInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.BottomStart)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.right - popupSize.width, position.x)
        assertEquals(anchorBounds.bottom, position.y)
    }

    @Test
    fun bottomEndPositionsPopoverBelowAndAlignedToStartInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.BottomEnd)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left, position.x)
        assertEquals(anchorBounds.bottom, position.y)
    }

    @Test
    fun centerStartPositionsPopoverCenteredVerticallyAndRightOfAnchorInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.CenterStart)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        // CenterStart in RTL: popup positioned to the RIGHT of anchor (mirrored)
        assertEquals(anchorBounds.right, position.x)
        assertEquals(anchorBounds.top + (anchorBounds.height / 2) - (popupSize.height / 2), position.y)
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
            popupContentSize = popupSize
        )

        // CenterEnd in RTL: popup positioned to the LEFT of anchor (mirrored)
        assertEquals(anchorWithSpace.left - popupSize.width, position.x)
        assertEquals(anchorWithSpace.top + (anchorWithSpace.height / 2) - (popupSize.height / 2), position.y)
    }

    @Test
    fun topCenterPositionsPopoverAboveAndCenteredHorizontally() {
        val provider = RelativePositionProvider(density, RelativeAlignment.TopCenter)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2), position.x)
        assertEquals(anchorBounds.top - popupSize.height, position.y)
    }

    @Test
    fun bottomCenterPositionsPopoverBelowAndCenteredHorizontally() {
        val provider = RelativePositionProvider(density, RelativeAlignment.BottomCenter)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Ltr,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2), position.x)
        assertEquals(anchorBounds.bottom, position.y)
    }

    @Test
    fun topCenterPositionsPopoverAboveAndCenteredHorizontallyInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.TopCenter)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2), position.x)
        assertEquals(anchorBounds.top - popupSize.height, position.y)
    }

    @Test
    fun bottomCenterPositionsPopoverBelowAndCenteredHorizontallyInRtl() {
        val provider = RelativePositionProvider(density, RelativeAlignment.BottomCenter)

        val position = provider.calculatePosition(
            anchorBounds = anchorBounds,
            windowSize = windowSize,
            layoutDirection = LayoutDirection.Rtl,
            popupContentSize = popupSize
        )

        assertEquals(anchorBounds.left + (anchorBounds.width / 2) - (popupSize.width / 2), position.x)
        assertEquals(anchorBounds.bottom, position.y)
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
            popupContentSize = largePopup
        )

        assertEquals(anchorNearTop.left, position.x)
        assertEquals(0, position.y)
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
            popupContentSize = largePopup
        )

        // TopEnd would position at anchorNearLeft.right - largePopup.width = 60 - 150 = -90
        // Should clamp to 0
        assertEquals(0, position.x)
        assertEquals(anchorNearLeft.top - largePopup.height, position.y)
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
            popupContentSize = largePopup
        )

        assertEquals(anchorNearBottom.right - largePopup.width, position.x)
        assertEquals(windowSize.height - largePopup.height, position.y)
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
            popupContentSize = largePopup
        )

        // BottomStart would position at anchorNearRight.left = 900
        // Popup extends to 900 + 150 = 1050, which exceeds window width (1000)
        // Should clamp to windowSize.width - largePopup.width = 1000 - 150 = 850
        assertEquals(windowSize.width - largePopup.width, position.x)
        assertEquals(anchorNearRight.bottom, position.y)
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
            popupContentSize = largePopup
        )

        // TopEnd: x = anchorAtTopLeft.right - largePopup.width = 60 - 150 = -90 → clamped to 0
        // TopEnd: y = anchorAtTopLeft.top - largePopup.height = 20 - 100 = -80 → clamped to 0
        assertEquals(0, position.x)
        assertEquals(0, position.y)
    }
}
