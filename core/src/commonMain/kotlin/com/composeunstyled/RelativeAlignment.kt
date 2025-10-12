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
    val anchor: RelativeAlignment
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
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
