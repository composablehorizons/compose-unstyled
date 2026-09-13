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
package com.composeunstyled.demo

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

internal data object PrototypeIndication : IndicationNodeFactory {
  override fun create(interactionSource: InteractionSource): DelegatableNode =
    PrototypeFocusNode(interactionSource)
}

private class PrototypeFocusNode(
  private val interactionSource: InteractionSource,
) : Modifier.Node(), DrawModifierNode {
  private var focused by mutableStateOf(false)

  override fun onAttach() {
    coroutineScope.launch {
      val focuses = mutableSetOf<FocusInteraction.Focus>()
      interactionSource.interactions.collect { interaction ->
        when (interaction) {
          is FocusInteraction.Focus -> focuses.add(interaction)
          is FocusInteraction.Unfocus -> focuses.remove(interaction.focus)
        }
        focused = focuses.isNotEmpty()
      }
    }
  }

  override fun onDetach() {
    focused = false
  }

  override fun ContentDrawScope.draw() {
    drawContent()
    if (focused) {
      val width = 2.dp.toPx()
      drawRect(
        color = Color.Black,
        topLeft = Offset(width / 2, width / 2),
        size = Size(
          (size.width - width).coerceAtLeast(0f),
          (size.height - width).coerceAtLeast(0f),
        ),
        style = Stroke(width),
      )
    }
  }
}
