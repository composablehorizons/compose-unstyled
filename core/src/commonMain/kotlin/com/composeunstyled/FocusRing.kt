package com.composeunstyled

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws an outline around the composable's bound when the component is focused.
 *
 * This modifier does not affect the components bounds.
 *
 * @param interactionSource The [InteractionSource] to subscribe to for focus events.
 * @param width the thickness of the outline
 * @param color the color of the outline
 * @param shape the shape of the composable. This is *not* the shape of the outline
 * @param offset the distance between the composable and the outline
 */
@Composable
fun Modifier.focusRing(
    interactionSource: InteractionSource,
    width: Dp,
    color: Color,
    shape: Shape = RectangleShape,
    offset: Dp = 0.dp
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()

    return if (focused) {
        this then Modifier.outline(width = width, color = color, shape = shape, offset = offset)
    } else {
        this
    }
}
