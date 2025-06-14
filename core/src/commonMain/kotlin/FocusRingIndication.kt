package com.composables.core

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch


@Composable
public fun rememberFocusRingIndication(
    ringColor: Color = Color.Unspecified,
    ringWidth: Dp = Dp.Unspecified,
    paddingValues: PaddingValues = PaddingValues(),
    cornerRadius: Dp
): Indication {
    return remember { FocusRingIndicationNodeFactory(ringColor, ringWidth, paddingValues, cornerRadius) }
}

internal class FocusRingIndicationNodeFactory internal constructor(
    private val ringColor: Color,
    private val strokeWidth: Dp,
    private val paddingValues: PaddingValues,
    private val cornerRadius: Dp,
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return FocusRingIndicationInstance(interactionSource, ringColor, strokeWidth, paddingValues, cornerRadius)
    }

    private class FocusRingIndicationInstance(
        private val interactionSource: InteractionSource,
        private val ringColor: Color,
        private val strokeWidth: Dp,
        private val paddingValues: PaddingValues,
        private val cornerRadius: Dp,
    ) : Modifier.Node(), DrawModifierNode {

        private var isFocused = false

        override fun onAttach() {
            super.onAttach()
            coroutineScope.launch {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is FocusInteraction.Focus -> {
                            if (isFocused.not()) {
                                isFocused = true
                                invalidateDraw()
                            }
                        }

                        is FocusInteraction.Unfocus -> {
                            if (isFocused) {
                                isFocused = false
                                invalidateDraw()
                            }
                        }
                    }
                }
            }
        }

        override fun ContentDrawScope.draw() {
            drawContent()
            if (isFocused) {
                val cornerRadiusObj = CornerRadius(cornerRadius.toPx())
                val ringWidthFloat = strokeWidth.toPx()

                val paddingFloatTop = paddingValues.calculateTopPadding().toPx()
                val paddingFloatBottom = paddingValues.calculateBottomPadding().toPx()
                val paddingFloatStart = paddingValues.calculateStartPadding(layoutDirection).toPx()
                val paddingFloatEnd = paddingValues.calculateEndPadding(layoutDirection).toPx()

                val ringSize = Size(
                    width = size.width + paddingFloatStart + paddingFloatEnd,
                    height = size.height + paddingFloatTop + paddingFloatBottom
                )

                val topLeft = Offset(-paddingFloatStart, -paddingFloatTop)

                val ringPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(offset = topLeft, size = ringSize),
                            cornerRadius = cornerRadiusObj
                        )
                    )
                }
                drawPath(ringPath, color = ringColor, style = Stroke(width = ringWidthFloat))
            }
        }

    }

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this
}
