package com.composables.core

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.composeunstyled.LocalContentColor

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

@Composable
fun VerticalSeparator(
    color: Color ,
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

@Composable
fun ColumnScope.Separator(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = Dp.Hairline
) {
    HorizontalSeparator(color = color, modifier = modifier, thickness = thickness)
}

@Composable
fun RowScope.Separator(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    thickness: Dp = Dp.Hairline
) {
    VerticalSeparator(color = color, modifier = modifier, thickness = thickness)
}