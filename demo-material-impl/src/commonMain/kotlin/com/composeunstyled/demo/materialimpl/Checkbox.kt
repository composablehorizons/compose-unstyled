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

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.composeunstyled.CheckedIndicator
import com.composeunstyled.StateIndicator
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.UnstyledTriStateCheckbox
import kotlin.math.max

private val CheckboxSize = 18.dp
private val CheckboxCornerRadius = 2.dp
private val CheckboxStrokeWidth = 2.dp
private val MaterialDefaultSpatialAnimationSpec = spring<Float>(
  dampingRatio = 0.9f,
  stiffness = 700f,
)
private const val CheckboxSnapAnimationDelay = 100
private fun Modifier.minimumInteractiveComponentSize(enabled: Boolean): Modifier =
  if (enabled) {
    minimumInteractiveComponentSize()
  } else {
    this
  }

@Composable
fun Checkbox(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: CheckboxColors = CheckboxDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  val boxColor by animateColorAsState(
    targetValue = checkboxBoxColor(colors, enabled, ToggleableState(checked)),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val borderColor by animateColorAsState(
    targetValue = checkboxBorderColor(colors, enabled, ToggleableState(checked)),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val checkColor by animateColorAsState(
    targetValue = checkboxCheckmarkColor(colors, enabled, ToggleableState(checked)),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  UnstyledCheckbox(
    checked = checked,
    modifier = modifier.minimumInteractiveComponentSize(onCheckedChange != null),
    enabled = enabled && onCheckedChange != null,
    onCheckedChange = onCheckedChange ?: {},
    interactionSource = interactionSource,
    indication = ripple(bounded = false, radius = 20.dp),
  ) {
    CheckedIndicator(
      modifier = Modifier
        .size(CheckboxSize)
        .drawBehind {
          drawMaterialCheckboxBox(
            boxColor = boxColor,
            borderColor = borderColor,
            radius = CheckboxCornerRadius.toPx(),
            strokeWidth = CheckboxStrokeWidth.toPx(),
          )
        },
    ) {
      val checkDrawFraction = transition.animateFloat(
        transitionSpec = {
          if (targetState == EnterExitState.PostExit) {
            snap(delayMillis = CheckboxSnapAnimationDelay)
          } else {
            MaterialDefaultSpatialAnimationSpec
          }
        },
      ) { state ->
        if (state == EnterExitState.Visible) 1f else 0f
      }
      val checkPath = remember { Path() }
      val pathToDraw = remember { Path() }
      val pathMeasure = remember { PathMeasure() }
      Canvas(Modifier.fillMaxSize()) {
        drawMaterialCheck(
          checkColor = checkColor,
          checkFraction = checkDrawFraction.value,
          crossCenterGravitation = 0f,
          checkPath = checkPath,
          pathToDraw = pathToDraw,
          pathMeasure = pathMeasure,
        )
      }
    }
  }
}

private fun DrawScope.drawMaterialCheckboxBox(
  boxColor: Color,
  borderColor: Color,
  radius: Float,
  strokeWidth: Float,
) {
  val halfStrokeWidth = strokeWidth / 2f
  val checkboxSize = size.width
  if (boxColor == borderColor) {
    drawRoundRect(
      color = boxColor,
      size = Size(checkboxSize, checkboxSize),
      cornerRadius = CornerRadius(radius),
      style = Fill,
    )
  } else {
    drawRoundRect(
      color = boxColor,
      topLeft = Offset(strokeWidth, strokeWidth),
      size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
      cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
      style = Fill,
    )
    drawRoundRect(
      color = borderColor,
      topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
      size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
      cornerRadius = CornerRadius(radius - halfStrokeWidth),
      style = Stroke(width = strokeWidth),
    )
  }
}

private fun DrawScope.drawMaterialCheck(
  checkColor: Color,
  checkFraction: Float,
  crossCenterGravitation: Float,
  checkPath: Path,
  pathToDraw: Path,
  pathMeasure: PathMeasure,
) {
  val width = size.width
  val checkCrossX = 0.4f
  val checkCrossY = 0.7f
  val leftX = 0.2f
  val leftY = 0.5f
  val rightX = 0.8f
  val rightY = 0.3f
  val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
  val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
  val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
  val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

  checkPath.rewind()
  checkPath.moveTo(width * leftX, width * gravitatedLeftY)
  checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
  checkPath.lineTo(width * rightX, width * gravitatedRightY)

  pathMeasure.setPath(checkPath, false)
  pathToDraw.rewind()
  pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
  drawPath(
    path = pathToDraw,
    color = checkColor,
    style = Stroke(
      width = CheckboxStrokeWidth.toPx(),
      cap = StrokeCap.Square,
    ),
  )
}

@Composable
fun TriStateCheckbox(
  state: ToggleableState,
  onClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: CheckboxColors = CheckboxDefaults.colors(),
  interactionSource: MutableInteractionSource? = null,
) {
  val boxColor by animateColorAsState(
    targetValue = checkboxBoxColor(colors, enabled, state),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val borderColor by animateColorAsState(
    targetValue = checkboxBorderColor(colors, enabled, state),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  val checkColor by animateColorAsState(
    targetValue = checkboxCheckmarkColor(colors, enabled, state),
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
  )
  UnstyledTriStateCheckbox(
    value = state,
    modifier = modifier.minimumInteractiveComponentSize(onClick != null),
    onClick = { onClick?.invoke() },
    enabled = enabled && onClick != null,
    interactionSource = interactionSource,
    indication = ripple(bounded = false, radius = 20.dp),
  ) {
    StateIndicator(
      modifier = Modifier
        .size(CheckboxSize)
        .drawBehind {
          drawMaterialCheckboxBox(
            boxColor = boxColor,
            borderColor = borderColor,
            radius = CheckboxCornerRadius.toPx(),
            strokeWidth = CheckboxStrokeWidth.toPx(),
          )
        },
    ) { state ->
      val transition = updateTransition(state)
      val checkDrawFraction by transition.animateFloat(
        transitionSpec = {
          when {
            initialState == ToggleableState.Off -> MaterialDefaultSpatialAnimationSpec
            targetState == ToggleableState.Off -> snap(delayMillis = CheckboxSnapAnimationDelay)
            else -> MaterialDefaultSpatialAnimationSpec
          }
        },
      ) {
        when (it) {
          ToggleableState.On -> 1f
          ToggleableState.Off -> 0f
          ToggleableState.Indeterminate -> 1f
        }
      }
      val checkCenterGravitationShiftFraction by transition.animateFloat(
        transitionSpec = {
          when {
            initialState == ToggleableState.Off -> snap()
            targetState == ToggleableState.Off -> snap(delayMillis = CheckboxSnapAnimationDelay)
            else -> MaterialDefaultSpatialAnimationSpec
          }
        },
      ) {
        when (it) {
          ToggleableState.On -> 0f
          ToggleableState.Off -> 0f
          ToggleableState.Indeterminate -> 1f
        }
      }
      val checkPath = remember { Path() }
      val pathToDraw = remember { Path() }
      val pathMeasure = remember { PathMeasure() }
      Canvas(Modifier.fillMaxSize()) {
        drawMaterialCheck(
          checkColor = checkColor,
          checkFraction = checkDrawFraction,
          crossCenterGravitation =
          checkCenterGravitationShiftFraction,
          checkPath = checkPath,
          pathToDraw = pathToDraw,
          pathMeasure = pathMeasure,
        )
      }
    }
  }
}

private fun checkboxBoxColor(
  colors: CheckboxColors,
  enabled: Boolean,
  state: ToggleableState,
): Color {
  return if (enabled) {
    when (state) {
      ToggleableState.On,
      ToggleableState.Indeterminate,
      -> colors.checkedBoxColor
      ToggleableState.Off -> colors.uncheckedBoxColor
    }
  } else {
    when (state) {
      ToggleableState.On -> colors.disabledCheckedBoxColor
      ToggleableState.Indeterminate -> colors.disabledIndeterminateBoxColor
      ToggleableState.Off -> colors.disabledUncheckedBoxColor
    }
  }
}

private fun checkboxBorderColor(
  colors: CheckboxColors,
  enabled: Boolean,
  state: ToggleableState,
): Color {
  return if (enabled) {
    when (state) {
      ToggleableState.On,
      ToggleableState.Indeterminate,
      -> colors.checkedBorderColor
      ToggleableState.Off -> colors.uncheckedBorderColor
    }
  } else {
    when (state) {
      ToggleableState.Indeterminate -> colors.disabledIndeterminateBorderColor
      ToggleableState.On -> colors.disabledBorderColor
      ToggleableState.Off -> colors.disabledUncheckedBorderColor
    }
  }
}

private fun checkboxCheckmarkColor(
  colors: CheckboxColors,
  enabled: Boolean,
  state: ToggleableState,
): Color {
  return if (enabled) {
    if (state == ToggleableState.Off) {
      colors.uncheckedCheckmarkColor
    } else {
      colors.checkedCheckmarkColor
    }
  } else {
    colors.disabledCheckmarkColor
  }
}
