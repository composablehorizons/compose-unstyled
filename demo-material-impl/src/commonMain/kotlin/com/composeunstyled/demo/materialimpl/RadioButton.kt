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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.composeunstyled.demo.material3api

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.composeunstyled.RadioButton
import com.composeunstyled.UnstyledRadioGroup

private val RadioButtonPadding = 2.dp
private val RadioButtonSize = 20.dp
private val RadioButtonDotSize = 12.dp
private val RadioButtonStrokeWidth = 2.dp
private fun Modifier.minimumInteractiveComponentSize(enabled: Boolean): Modifier =
  if (enabled) {
    minimumInteractiveComponentSize()
  } else {
    this
  }

@Composable
fun RadioButton(
  selected: Boolean,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: RadioButtonColors = RadioButtonDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  UnstyledRadioGroup(
    value = if (selected) "selected" else null,
    onValueChange = { onClick?.invoke() },
  ) {
    val radioColorTarget = when {
      enabled && selected -> colors.selectedColor
      enabled && !selected -> colors.unselectedColor
      !enabled && selected -> colors.disabledSelectedColor
      else -> colors.disabledUnselectedColor
    }
    val radioColor by animateColorAsState(
      targetValue = radioColorTarget,
      animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
    )
    val dotRadius by animateDpAsState(
      targetValue = if (selected) RadioButtonDotSize / 2 else 0.dp,
      animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
    )
    RadioButton(
      value = "selected",
      modifier = modifier.minimumInteractiveComponentSize(onClick != null),
      enabled = enabled,
      interactionSource = interactionSource,
      indication = ripple(bounded = false, radius = 20.dp),
    ) {
      Canvas(
        Modifier
          .wrapContentSize(Alignment.Center)
          .padding(RadioButtonPadding)
          .requiredSize(RadioButtonSize),
      ) {
        val strokeWidth = RadioButtonStrokeWidth.toPx()
        drawCircle(
          color = radioColor,
          radius = (RadioButtonSize / 2).toPx() - strokeWidth / 2,
          style = Stroke(width = strokeWidth),
        )
        if (dotRadius > 0.dp) {
          drawCircle(
            color = radioColor,
            radius = dotRadius.toPx() - strokeWidth / 2,
            style = Fill,
          )
        }
      }
    }
  }
}
