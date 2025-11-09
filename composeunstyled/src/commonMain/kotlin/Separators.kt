package com.composables.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.composeunstyled.LocalContentColor

/**
 * Creates a horizontal separator line.
 *
 * For interactive preview & code examples, visit [Separators Documentation](https://composeunstyled.com/separators).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Column {
 *     Text("Item 1")
 *     Separator() // Horizontal separator in Column
 *     Text("Item 2")
 * }
 *
 * Row {
 *     Text("Item 1")
 *     Separator() // Vertical separator in Row
 *     Text("Item 2")
 * }
 * ```
 *
 * @param color The color of the separator line.
 * @param modifier Modifier to be applied to the separator.
 * @param thickness The thickness of the separator line.
 */
@Composable
fun HorizontalSeparator(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = Dp.Hairline
) {
    Canvas(modifier.fillMaxWidth().height(thickness)) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
        )
    }
}

/**
 * Creates a vertical separator line.
 *
 * For interactive preview & code examples, visit [Separators Documentation](https://composeunstyled.com/separators).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Column {
 *     Text("Item 1")
 *     Separator() // Horizontal separator in Column
 *     Text("Item 2")
 * }
 *
 * Row {
 *     Text("Item 1")
 *     Separator() // Vertical separator in Row
 *     Text("Item 2")
 * }
 * ```
 *
 * @param color The color of the separator line.
 * @param modifier Modifier to be applied to the separator.
 * @param thickness The thickness of the separator line.
 */
@Composable
fun VerticalSeparator(
    color: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = Dp.Hairline
) {
    Canvas(modifier.width(thickness).fillMaxHeight()) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(thickness.toPx() / 2, 0f),
            end = Offset(thickness.toPx() / 2, size.height),
        )
    }
}

/**
 * Creates a horizontal separator line within a [ColumnScope].
 *
 * For interactive preview & code examples, visit [Separators Documentation](https://composeunstyled.com/separators).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Column {
 *     Text("Item 1")
 *     Separator() // Horizontal separator in Column
 *     Text("Item 2")
 * }
 *
 * Row {
 *     Text("Item 1")
 *     Separator() // Vertical separator in Row
 *     Text("Item 2")
 * }
 * ```
 *
 * @param modifier Modifier to be applied to the separator.
 * @param color The color of the separator line. Defaults to the current content color.
 * @param thickness The thickness of the separator line.
 */
@Composable
fun ColumnScope.Separator(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = Dp.Hairline
) {
    HorizontalSeparator(color = color, modifier = modifier, thickness = thickness)
}

/**
 * Creates a vertical separator line within a [RowScope].
 *
 * For interactive preview & code examples, visit [Separators Documentation](https://composeunstyled.com/separators).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Column {
 *     Text("Item 1")
 *     Separator() // Horizontal separator in Column
 *     Text("Item 2")
 * }
 *
 * Row {
 *     Text("Item 1")
 *     Separator() // Vertical separator in Row
 *     Text("Item 2")
 * }
 * ```
 *
 * @param modifier Modifier to be applied to the separator.
 * @param color The color of the separator line. Defaults to the current content color.
 * @param thickness The thickness of the separator line.
 */
@Composable
fun RowScope.Separator(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = Dp.Hairline
) {
    VerticalSeparator(color = color, modifier = modifier, thickness = thickness)
}