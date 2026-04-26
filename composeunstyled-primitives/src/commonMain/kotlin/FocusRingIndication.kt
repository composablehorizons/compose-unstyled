/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

internal class FocusRingIndicationNodeFactory internal constructor(
  private val ringColor: Color,
  private val strokeWidth: Dp,
  private val paddingValues: PaddingValues,
  private val cornerRadius: Dp,
) : IndicationNodeFactory {

  override fun create(interactionSource: InteractionSource): DelegatableNode {
    return FocusRingIndicationInstance(
      interactionSource,
      ringColor,
      strokeWidth,
      paddingValues,
      cornerRadius,
    )
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
          height = size.height + paddingFloatTop + paddingFloatBottom,
        )

        val topLeft = Offset(-paddingFloatStart, -paddingFloatTop)

        val ringPath = Path().apply {
          addRoundRect(
            RoundRect(
              rect = Rect(offset = topLeft, size = ringSize),
              cornerRadius = cornerRadiusObj,
            ),
          )
        }
        drawPath(ringPath, color = ringColor, style = Stroke(width = ringWidthFloat))
      }
    }
  }

  override fun hashCode(): Int = -1

  override fun equals(other: Any?) = other === this
}
