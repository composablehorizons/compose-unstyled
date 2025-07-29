package com.composeunstyled.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
): IndicationNodeFactory {
    return remember {
        ColoredIndication(
            hoveredColor = hoveredColor,
            pressedColor = pressedColor,
            focusedColor = focusedColor,
        )
    }
}

class ColoredIndication(
    private val hoveredColor: Color,
    private val pressedColor: Color,
    private val focusedColor: Color,
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ColoredIndicationInstance(interactionSource, hoveredColor, pressedColor, focusedColor)

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this

    private class ColoredIndicationInstance(
        private val interactionSource: InteractionSource,
        private val hoveredColor: Color,
        private val pressedColor: Color,
        private val focusedColor: Color,
    ) :
        Modifier.Node(), DrawModifierNode {
        private var isPressed = false
        private var isHovered = false
        private var isFocused = false

        override fun onAttach() {
            coroutineScope.launch {
                var pressCount = 0
                var hoverCount = 0
                var focusCount = 0
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> pressCount++
                        is PressInteraction.Release -> pressCount--
                        is PressInteraction.Cancel -> pressCount--
                        is HoverInteraction.Enter -> hoverCount++
                        is HoverInteraction.Exit -> hoverCount--
                        is FocusInteraction.Focus -> focusCount++
                        is FocusInteraction.Unfocus -> focusCount--
                    }
                    val pressed = pressCount > 0
                    val hovered = hoverCount > 0
                    val focused = focusCount > 0

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
                    if (invalidateNeeded) invalidateDraw()
                }
            }
        }


        override fun ContentDrawScope.draw() {
            drawContent()
            if (isPressed) {
                drawRect(color = pressedColor, size = size)
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