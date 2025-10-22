package com.composeunstyled.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.launch

@Composable
fun rememberColoredIndication(
    hoveredColor: Color = Color.Unspecified,
    pressedColor: Color = Color.Unspecified,
    focusedColor: Color = Color.Unspecified,
    draggedColor: Color = Color.Unspecified,
): IndicationNodeFactory {
    return remember {
        ColoredIndication(
            hoveredColor = hoveredColor,
            pressedColor = pressedColor,
            focusedColor = focusedColor,
            draggedColor = draggedColor
        )
    }
}

@Composable
fun rememberColoredIndication(
    color: Color,
    draggedAlpha: Float = 0.16f,
    focusedAlpha: Float = 0.1f,
    hoveredAlpha: Float = 0.08f,
    pressedAlpha: Float = 0.1f,
): IndicationNodeFactory {
    return remember {
        ColoredIndication(
            hoveredColor = color.copy(alpha = hoveredAlpha),
            pressedColor = color.copy(alpha = pressedAlpha),
            focusedColor = color.copy(alpha = focusedAlpha),
            draggedColor = color.copy(alpha = draggedAlpha)
        )
    }
}

class ColoredIndication(
    private val hoveredColor: Color,
    private val pressedColor: Color,
    private val focusedColor: Color,
    private val draggedColor: Color,
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ColoredIndicationInstance(interactionSource, hoveredColor, pressedColor, focusedColor, draggedColor)

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this

    private class ColoredIndicationInstance(
        private val interactionSource: InteractionSource,
        private val hoveredColor: Color,
        private val pressedColor: Color,
        private val focusedColor: Color,
        private val draggedColor: Color,
    ) :
        Modifier.Node(), DrawModifierNode {
        private var isPressed = false
        private var isHovered = false
        private var isFocused = false
        private var isDragged = false

        override fun onAttach() {
            coroutineScope.launch {
                var pressCount = 0
                var hoverCount = 0
                var focusCount = 0
                var dragCount = 0
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> pressCount++
                        is PressInteraction.Release -> pressCount--
                        is PressInteraction.Cancel -> pressCount--
                        is HoverInteraction.Enter -> hoverCount++
                        is HoverInteraction.Exit -> hoverCount--
                        is FocusInteraction.Focus -> focusCount++
                        is FocusInteraction.Unfocus -> focusCount--
                        is DragInteraction.Start -> dragCount++
                        is DragInteraction.Stop -> dragCount--
                    }
                    val pressed = pressCount > 0
                    val hovered = hoverCount > 0
                    val focused = focusCount > 0
                    val dragged = focusCount > 0

                    var invalidateNeeded = false
                    if (isPressed != pressed) {
                        isPressed = pressed
                        invalidateNeeded = true
                    }
                    if (isHovered != hovered) {
                        isHovered = hovered
                        invalidateNeeded = true
                    }
                    if (isFocused != focused) {
                        isFocused = focused
                        invalidateNeeded = true
                    }
                    if (isDragged != dragged) {
                        isDragged = dragged
                        invalidateNeeded = true
                    }
                    if (invalidateNeeded) invalidateDraw()
                }
            }
        }


        override fun ContentDrawScope.draw() {
            drawContent()
            if (isPressed) {
                drawRect(color = pressedColor, size = size)
            }
            if (isDragged) {
                drawRect(color = draggedColor, size = size)
            }
            if (isHovered) {
                drawRect(color = hoveredColor, size = size)
            }
            if (isFocused) {
                drawRect(color = focusedColor, size = size)
            }
        }
    }
}