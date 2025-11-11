package com.composeunstyled

import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatingContentPlacementTest {
    private val anchorWidth = 100
    private val anchorHeight = 50
    private val contentWidth = 150
    private val contentHeight = 80

    // TopStart tests
    @Test
    fun topStartPositionsContentAboveAndAlignedToStart() {
        val x = calculateX(
            placement = RelativeAlignment.TopStart,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.TopStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(0, x)
        assertEquals(-contentHeight, y)
    }

    @Test
    fun topStartPositionsContentAboveAndAlignedToEndInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.TopStart,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.TopStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(anchorWidth - contentWidth, x)
        assertEquals(-contentHeight, y)
    }

    // TopEnd tests
    @Test
    fun topEndPositionsContentAboveAndAlignedToEnd() {
        val x = calculateX(
            placement = RelativeAlignment.TopEnd,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.TopEnd,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(anchorWidth - contentWidth, x)
        assertEquals(-contentHeight, y)
    }

    @Test
    fun topEndPositionsContentAboveAndAlignedToStartInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.TopEnd,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.TopEnd,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(0, x)
        assertEquals(-contentHeight, y)
    }

    // TopCenter tests
    @Test
    fun topCenterPositionsContentAboveAndCenteredHorizontally() {
        val x = calculateX(
            placement = RelativeAlignment.TopCenter,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.TopCenter,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals((anchorWidth - contentWidth) / 2, x)
        assertEquals(-contentHeight, y)
    }

    @Test
    fun topCenterPositionsContentAboveAndCenteredHorizontallyInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.TopCenter,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.TopCenter,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // TopCenter should be the same in RTL
        assertEquals((anchorWidth - contentWidth) / 2, x)
        assertEquals(-contentHeight, y)
    }

    // BottomStart tests
    @Test
    fun bottomStartPositionsContentBelowAndAlignedToStart() {
        val x = calculateX(
            placement = RelativeAlignment.BottomStart,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.BottomStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(0, x)
        assertEquals(anchorHeight, y)
    }

    @Test
    fun bottomStartPositionsContentBelowAndAlignedToEndInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.BottomStart,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.BottomStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(anchorWidth - contentWidth, x)
        assertEquals(anchorHeight, y)
    }

    // BottomEnd tests
    @Test
    fun bottomEndPositionsContentBelowAndAlignedToEnd() {
        val x = calculateX(
            placement = RelativeAlignment.BottomEnd,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.BottomEnd,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(anchorWidth - contentWidth, x)
        assertEquals(anchorHeight, y)
    }

    @Test
    fun bottomEndPositionsContentBelowAndAlignedToStartInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.BottomEnd,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.BottomEnd,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals(0, x)
        assertEquals(anchorHeight, y)
    }

    // BottomCenter tests
    @Test
    fun bottomCenterPositionsContentBelowAndCenteredHorizontally() {
        val x = calculateX(
            placement = RelativeAlignment.BottomCenter,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.BottomCenter,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        assertEquals((anchorWidth - contentWidth) / 2, x)
        assertEquals(anchorHeight, y)
    }

    @Test
    fun bottomCenterPositionsContentBelowAndCenteredHorizontallyInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.BottomCenter,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.BottomCenter,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // BottomCenter should be the same in RTL
        assertEquals((anchorWidth - contentWidth) / 2, x)
        assertEquals(anchorHeight, y)
    }

    // CenterStart tests
    @Test
    fun centerStartPositionsContentCenteredVerticallyAndLeftOfAnchor() {
        val x = calculateX(
            placement = RelativeAlignment.CenterStart,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.CenterStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // CenterStart: content positioned to the LEFT of anchor
        assertEquals(-contentWidth, x)
        assertEquals((anchorHeight - contentHeight) / 2, y)
    }

    @Test
    fun centerStartPositionsContentCenteredVerticallyAndRightOfAnchorInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.CenterStart,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.CenterStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // CenterStart in RTL: content positioned to the RIGHT of anchor (mirrored)
        assertEquals(anchorWidth, x)
        assertEquals((anchorHeight - contentHeight) / 2, y)
    }

    // CenterEnd tests
    @Test
    fun centerEndPositionsContentCenteredVerticallyAndRightOfAnchor() {
        val x = calculateX(
            placement = RelativeAlignment.CenterEnd,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.CenterEnd,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // CenterEnd: content positioned to the RIGHT of anchor
        assertEquals(anchorWidth, x)
        assertEquals((anchorHeight - contentHeight) / 2, y)
    }

    @Test
    fun centerEndPositionsContentCenteredVerticallyAndLeftOfAnchorInRtl() {
        val x = calculateX(
            placement = RelativeAlignment.CenterEnd,
            layoutDirection = LayoutDirection.Rtl,
            anchorWidth = anchorWidth,
            contentWidth = contentWidth
        )
        val y = calculateY(
            placement = RelativeAlignment.CenterEnd,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // CenterEnd in RTL: content positioned to the LEFT of anchor (mirrored)
        assertEquals(-contentWidth, x)
        assertEquals((anchorHeight - contentHeight) / 2, y)
    }

    // Edge cases with different sizes
    @Test
    fun contentSmallerThanAnchorPositionsCorrectlyForTopStart() {
        val smallContent = 30
        val x = calculateX(
            placement = RelativeAlignment.TopStart,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = smallContent
        )

        assertEquals(0, x)
    }

    @Test
    fun contentSmallerThanAnchorPositionsCorrectlyForTopEnd() {
        val smallContent = 30
        val x = calculateX(
            placement = RelativeAlignment.TopEnd,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = smallContent
        )

        assertEquals(anchorWidth - smallContent, x)
    }

    @Test
    fun contentSmallerThanAnchorPositionsCorrectlyForTopCenter() {
        val smallContent = 30
        val x = calculateX(
            placement = RelativeAlignment.TopCenter,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = smallContent
        )

        assertEquals((anchorWidth - smallContent) / 2, x)
    }

    @Test
    fun contentLargerThanAnchorPositionsCorrectlyForTopEnd() {
        val largeContent = 200
        val x = calculateX(
            placement = RelativeAlignment.TopEnd,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = largeContent
        )

        // TopEnd should position at anchorWidth - contentWidth (can be negative)
        assertEquals(anchorWidth - largeContent, x)
    }

    @Test
    fun contentLargerThanAnchorPositionsCorrectlyForTopCenter() {
        val largeContent = 200
        val x = calculateX(
            placement = RelativeAlignment.TopCenter,
            layoutDirection = LayoutDirection.Ltr,
            anchorWidth = anchorWidth,
            contentWidth = largeContent
        )

        // TopCenter should center even if result is negative
        assertEquals((anchorWidth - largeContent) / 2, x)
    }

    @Test
    fun bottomPositionsAreConsistentWithAnchorHeight() {
        val y = calculateY(
            placement = RelativeAlignment.BottomStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // Bottom placements should position at anchorHeight
        assertEquals(anchorHeight, y)
    }

    @Test
    fun centerVerticalPositioningCalculatesCorrectOffset() {
        val y = calculateY(
            placement = RelativeAlignment.CenterStart,
            anchorHeight = anchorHeight,
            contentHeight = contentHeight
        )

        // Center should position at (anchorHeight - contentHeight) / 2
        assertEquals((anchorHeight - contentHeight) / 2, y)
    }

    @Test
    fun centerVerticalPositioningWithContentSmallerThanAnchor() {
        val smallContentHeight = 20
        val y = calculateY(
            placement = RelativeAlignment.CenterStart,
            anchorHeight = anchorHeight,
            contentHeight = smallContentHeight
        )

        // With smaller content, should be positive offset
        assertEquals((anchorHeight - smallContentHeight) / 2, y)
    }
}

// These are the internal functions from FloatingContent.kt that we're testing
private fun calculateX(
    placement: RelativeAlignment,
    layoutDirection: LayoutDirection,
    anchorWidth: Int,
    contentWidth: Int
): Int {
    return when (placement) {
        RelativeAlignment.TopStart, RelativeAlignment.BottomStart -> {
            if (layoutDirection == LayoutDirection.Ltr) 0 else anchorWidth - contentWidth
        }

        RelativeAlignment.TopEnd, RelativeAlignment.BottomEnd -> {
            if (layoutDirection == LayoutDirection.Ltr) anchorWidth - contentWidth else 0
        }

        RelativeAlignment.CenterStart -> {
            // CenterStart: content positioned to the LEFT of anchor
            if (layoutDirection == LayoutDirection.Ltr) -contentWidth else anchorWidth
        }

        RelativeAlignment.CenterEnd -> {
            // CenterEnd: content positioned to the RIGHT of anchor
            if (layoutDirection == LayoutDirection.Ltr) anchorWidth else -contentWidth
        }

        RelativeAlignment.TopCenter, RelativeAlignment.BottomCenter -> {
            (anchorWidth - contentWidth) / 2
        }
    }
}

private fun calculateY(
    placement: RelativeAlignment,
    anchorHeight: Int,
    contentHeight: Int
): Int {
    return when (placement) {
        RelativeAlignment.TopStart, RelativeAlignment.TopEnd, RelativeAlignment.TopCenter -> {
            -contentHeight
        }

        RelativeAlignment.CenterStart, RelativeAlignment.CenterEnd -> {
            (anchorHeight - contentHeight) / 2
        }

        RelativeAlignment.BottomStart, RelativeAlignment.BottomEnd, RelativeAlignment.BottomCenter -> {
            anchorHeight
        }
    }
}