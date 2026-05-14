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

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledCheckbox

@Composable
fun CheckboxCustomCheckedIndicatorDemo() {
  var checked by remember { mutableStateOf(true) }
  val checkboxShape = RoundedCornerShape(4.dp)
  UnstyledCheckbox(
    checked = checked,
    onCheckedChange = { checked = it },
    modifier = Modifier.clip(checkboxShape),
    accessibilityLabel = "Enable notifications",
  ) {
    MaterialCheckboxIndicator(checked = checked)
  }
}

@Composable
private fun MaterialCheckboxIndicator(
  checked: Boolean,
  modifier: Modifier = Modifier,
) {
  val transition = updateTransition(checked)
  val boxProgress by transition.animateFloat(
    transitionSpec = { tween(durationMillis = 450) },
  ) { isChecked ->
    if (isChecked) 1f else 0f
  }
  val checkProgress by transition.animateFloat(
    transitionSpec = { tween(durationMillis = 450) },
  ) { isChecked ->
    if (isChecked) 1f else 0f
  }
  val checkPath = remember { Path() }
  val checkPathMeasure = remember { PathMeasure() }
  val visibleCheckPath = remember { Path() }

  Canvas(modifier.size(24.dp)) {
    val strokeWidth = 2.dp.toPx()
    val radius = 4.dp.toPx()
    val boxColor = lerp(Color.Transparent, Color(0xFF18181B), boxProgress)
    val borderColor = lerp(Color(0xFFCACACA), Color(0xFF18181B), boxProgress)
    val checkColor = Color.White

    drawRoundRect(
      color = boxColor,
      size = size,
      cornerRadius = CornerRadius(radius),
      style = Fill,
    )
    drawRoundRect(
      color = borderColor,
      topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
      size = Size(size.width - strokeWidth, size.height - strokeWidth),
      cornerRadius = CornerRadius(radius - strokeWidth / 2f),
      style = Stroke(width = strokeWidth),
    )

    checkPath.reset()
    checkPath.moveTo(size.width * 0.2f, size.height * 0.52f)
    checkPath.lineTo(size.width * 0.42f, size.height * 0.72f)
    checkPath.lineTo(size.width * 0.8f, size.height * 0.32f)

    visibleCheckPath.reset()
    checkPathMeasure.setPath(checkPath, false)
    checkPathMeasure.getSegment(
      startDistance = 0f,
      stopDistance = checkPathMeasure.length * checkProgress,
      destination = visibleCheckPath,
      startWithMoveTo = true,
    )

    drawPath(
      path = visibleCheckPath,
      color = checkColor,
      style = Stroke(
        width = strokeWidth,
        join = StrokeJoin.Round,
      ),
    )
  }
}
