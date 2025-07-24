package com.composeunstyled

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws an outline outside the composable's bounds.
 *
 * Unlike [border], this modifier does not affect layout or size.
 *
 * The final outline shape is based on the given [shape]'s corner radius and the [offset]
 *
 * Only [Outline.Rectangle] and [Outline.Rounded] are supported. [Outline.Generic] is ignored.
 *
 *
 * @param width the thickness of the outline
 * @param color the color of the outline
 * @param shape the shape of the composable. This is *not* the shape of the outline
 * @param offset the distance between the composable and the outline
 */
fun Modifier.outline(width: Dp, color: Color, shape: Shape = RectangleShape, offset: Dp = 0.dp): Modifier {
    return drawBehind {
        val strokeWidth = width.toPx()
        val outline = shape.createOutline(size, layoutDirection, this)

        when (outline) {
            is Outline.Generic -> {
                // not supported
            }

            is Outline.Rectangle -> {
                val inset = offset.toPx()

                val path = Path().apply {
                    addRect(
                        Rect(
                            left = inset - strokeWidth,
                            top = inset - strokeWidth,
                            right = size.width - inset + strokeWidth,
                            bottom = size.height - inset + strokeWidth
                        )
                    )
                    fillType = PathFillType.EvenOdd
                    addRect(
                        Rect(
                            left = inset,
                            top = inset,
                            right = size.width - inset,
                            bottom = size.height - inset
                        )
                    )
                }

                drawPath(path, color)
            }

            is Outline.Rounded -> {
                val inset = offset.toPx()
                val roundRect = outline.roundRect

                val topLeftRadius = roundRect.topLeftCornerRadius.x
                val topRightRadius = roundRect.topRightCornerRadius.x
                val bottomRightRadius = roundRect.bottomRightCornerRadius.x
                val bottomLeftRadius = roundRect.bottomLeftCornerRadius.y

                val topLeftOutlineRadius = topLeftRadius + strokeWidth
                val topRightOutlineRadius = topRightRadius + strokeWidth
                val bottomRightOutlineRadius = bottomRightRadius + strokeWidth
                val bottomLeftOutlineRadius = bottomLeftRadius + strokeWidth

                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = inset - strokeWidth,
                            top = inset - strokeWidth,
                            right = size.width - inset + strokeWidth,
                            bottom = size.height - inset + strokeWidth,
                            topLeftCornerRadius = CornerRadius(topLeftOutlineRadius, topLeftOutlineRadius),
                            topRightCornerRadius = CornerRadius(topRightOutlineRadius, topRightOutlineRadius),
                            bottomRightCornerRadius = CornerRadius(bottomRightOutlineRadius, bottomRightOutlineRadius),
                            bottomLeftCornerRadius = CornerRadius(bottomLeftOutlineRadius, bottomLeftOutlineRadius)
                        )
                    )
                    fillType = PathFillType.EvenOdd
                    addRoundRect(
                        RoundRect(
                            left = inset,
                            top = inset,
                            right = size.width - inset,
                            bottom = size.height - inset,
                            topLeftCornerRadius = CornerRadius(topLeftRadius, topLeftRadius),
                            topRightCornerRadius = CornerRadius(topRightRadius, topRightRadius),
                            bottomRightCornerRadius = CornerRadius(bottomRightRadius, bottomRightRadius),
                            bottomLeftCornerRadius = CornerRadius(bottomLeftRadius, bottomLeftRadius)
                        )
                    )
                }

                drawPath(path, color)
            }
        }
    }
}
