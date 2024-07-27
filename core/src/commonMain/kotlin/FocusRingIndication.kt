package com.composables.core

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
public fun rememberFocusRingIndication(
    ringColor: Color = Color.Unspecified,
    ringWidth: Dp = Dp.Unspecified,
    paddingValues: PaddingValues = PaddingValues(),
    cornerRadius: Dp
): Indication {
    return remember { FocusRingIndication(ringColor, ringWidth, paddingValues, cornerRadius) }
}

internal class FocusRingIndication internal constructor(
    private val ringColor: Color,
    private val strokeWidth: Dp,
    private val paddingValues: PaddingValues,
    private val cornerRadius: Dp,
) : Indication {

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isFocused = interactionSource.collectIsFocusedAsState()
        val layoutDirection = LocalLayoutDirection.current
        return remember(interactionSource, layoutDirection) {
            object : IndicationInstance {
                override fun ContentDrawScope.drawIndication() {
                    drawContent()
                    if (isFocused.value) {
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
        }
    }
}
