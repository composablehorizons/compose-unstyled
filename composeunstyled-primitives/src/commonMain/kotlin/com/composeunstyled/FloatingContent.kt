package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection

/**
 * A layout that renders [floatingContent] above the [anchor].
 *
 * This is handy when building floating non-interactive interactions such as Tooltips.
 *
 * As opposed to a [androidx.compose.ui.window.Popup], the FloatingContent renders its contents in the same window as the composition it is part of.
 *
 * This has the benefit of not intercepting pointer events for fully non-intrusive floating elements.
 */
@Composable
internal fun FloatingContent(
    floatingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    placement: RelativeAlignment = RelativeAlignment.TopStart,
    anchor: @Composable () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val density = LocalDensity.current
    val windowSize = currentWindowContainerSize().toIntSize(density)

    SubcomposeLayout(
        modifier = modifier
    ) { constraints ->
        // Measure anchor
        val anchorPlaceable = subcompose("anchor", anchor)
            .firstOrNull()
            ?.measure(constraints)

        // If there's no anchor, don't render anything
        if (anchorPlaceable == null) {
            return@SubcomposeLayout layout(0, 0) {}
        }

        // Measure floating content if visible
        val floatingPlaceable = subcompose("anchored", floatingContent)
            .firstOrNull()
            ?.measure(Constraints())

        // Layout size = anchor only
        layout(anchorPlaceable.width, anchorPlaceable.height) {
            // Place anchor
            anchorPlaceable.place(0, 0)

            // Calculate floating content position relative to anchor
            if (floatingPlaceable != null) {
                val x = calculateX(
                    placement = placement,
                    layoutDirection = layoutDirection,
                    anchorWidth = anchorPlaceable.width,
                    contentWidth = floatingPlaceable.width
                )

                val y = calculateY(
                    placement = placement,
                    anchorHeight = anchorPlaceable.height,
                    contentHeight = floatingPlaceable.height
                )

                // Clamp to window bounds using this layout's position in window
                val layoutCoordinates = coordinates
                val clampedX: Int
                val clampedY: Int

                if (layoutCoordinates != null) {
                    val layoutPositionX = layoutCoordinates.positionInWindow().x.toInt()
                    val layoutPositionY = layoutCoordinates.positionInWindow().y.toInt()

                    // Calculate clamping bounds
                    val minX = -layoutPositionX
                    val maxX = windowSize.width - floatingPlaceable.width - layoutPositionX
                    val minY = -layoutPositionY
                    val maxY = windowSize.height - floatingPlaceable.height - layoutPositionY

                    // When content is larger than window, prefer showing top-left (0, 0)
                    clampedX = if (maxX < minX) {
                        // Content larger than window - clamp to 0
                        -layoutPositionX
                    } else {
                        x.coerceIn(minX, maxX)
                    }

                    clampedY = if (maxY < minY) {
                        // Content larger than window - clamp to 0
                        -layoutPositionY
                    } else {
                        y.coerceIn(minY, maxY)
                    }
                } else {
                    // If coordinates not available yet, don't clamp
                    clampedX = x
                    clampedY = y
                }

                floatingPlaceable.place(clampedX, clampedY)
            }
        }
    }
}

private fun DpSize.toIntSize(density: Density): IntSize {
    return with(density) {
        IntSize(width.roundToPx(), height.roundToPx())
    }
}

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
