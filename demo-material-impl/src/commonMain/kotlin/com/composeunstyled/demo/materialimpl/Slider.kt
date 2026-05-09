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

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.composeunstyled.UnstyledSlider
import com.composeunstyled.buildModifier

private val SliderHeight = 44.dp
private val SliderTrackHeight = 16.dp
private val SliderTrackCornerSize = 8.dp
private val SliderTrackInsideCornerSize = 2.dp
private val SliderThumbWidth = 4.dp
private val SliderThumbHeight = 44.dp
private val SliderThumbTrackGap = 6.dp
private val SliderStopIndicatorSize = 4.dp
private fun SliderColors.thumbColorFor(enabled: Boolean): Color =
  if (enabled) thumbColor else disabledThumbColor

private fun SliderColors.trackColorFor(enabled: Boolean, active: Boolean): Color =
  when {
    enabled && active -> activeTrackColor
    enabled && !active -> inactiveTrackColor
    !enabled && active -> disabledActiveTrackColor
    else -> disabledInactiveTrackColor
  }

private fun SliderColors.tickColorFor(enabled: Boolean, active: Boolean): Color =
  when {
    enabled && active -> activeTickColor
    enabled && !active -> inactiveTickColor
    !enabled && active -> disabledActiveTickColor
    else -> disabledInactiveTickColor
  }
private fun Modifier.minimumInteractiveComponentSize(enabled: Boolean): Modifier =
  if (enabled) {
    minimumInteractiveComponentSize()
  } else {
    this
  }

@Composable
private fun PlainSliderTrack(
  progress: Float,
  steps: Int,
  enabled: Boolean,
  thumbActive: Boolean,
  colors: SliderColors,
) {
  val activeColor = colors.trackColorFor(enabled, active = true)
  val inactiveColor = colors.trackColorFor(enabled, active = false)
  val activeTickColor = colors.tickColorFor(enabled, active = true)
  val inactiveTickColor = colors.tickColorFor(enabled, active = false)
  Canvas(modifier = Modifier.fillMaxWidth().height(SliderTrackHeight)) {
    val fraction = progress.coerceIn(0f, 1f)
    val trackWidth = size.width
    val trackHeight = size.height
    val cornerRadius = SliderTrackCornerSize.toPx()
    val insideCornerRadius = SliderTrackInsideCornerSize.toPx()
    val thumbWidth = if (thumbActive) {
      SliderThumbWidth.toPx() / 2f
    } else {
      SliderThumbWidth.toPx()
    }
    val thumbGap = thumbWidth / 2f + SliderThumbTrackGap.toPx()
    val thumbCenter = thumbWidth / 2f + (trackWidth - thumbWidth) * fraction
    val activeEnd = (thumbCenter - thumbGap).coerceAtLeast(0f)
    val inactiveStart = (thumbCenter + thumbGap).coerceAtMost(trackWidth)

    fun drawTrackSegment(
      color: Color,
      start: Float,
      end: Float,
      startRadius: Float,
      endRadius: Float,
    ) {
      if (end <= start) return

      val width = end - start
      val radiusScale = (width / (startRadius + endRadius)).coerceAtMost(1f)
      val resolvedStartRadius = startRadius * radiusScale
      val resolvedEndRadius = endRadius * radiusScale
      val path = Path().apply {
        moveTo(start + resolvedStartRadius, 0f)
        lineTo(end - resolvedEndRadius, 0f)
        quadraticTo(end, 0f, end, resolvedEndRadius)
        lineTo(end, trackHeight - resolvedEndRadius)
        quadraticTo(end, trackHeight, end - resolvedEndRadius, trackHeight)
        lineTo(start + resolvedStartRadius, trackHeight)
        quadraticTo(start, trackHeight, start, trackHeight - resolvedStartRadius)
        lineTo(start, resolvedStartRadius)
        quadraticTo(start, 0f, start + resolvedStartRadius, 0f)
        close()
      }
      drawPath(path = path, color = color)
    }

    if (activeEnd > 0f) {
      drawTrackSegment(
        color = activeColor,
        start = 0f,
        end = activeEnd,
        startRadius = cornerRadius,
        endRadius = insideCornerRadius,
      )
    }

    if (inactiveStart < trackWidth) {
      drawTrackSegment(
        color = inactiveColor,
        start = inactiveStart,
        end = trackWidth,
        startRadius = insideCornerRadius,
        endRadius = cornerRadius,
      )

      val stopRadius = SliderStopIndicatorSize.toPx() / 2f
      drawCircle(
        color = activeColor,
        radius = stopRadius,
        center = Offset(trackWidth - cornerRadius, center.y),
      )
    }

    if (steps > 0) {
      val stopRadius = SliderStopIndicatorSize.toPx() / 2f
      for (step in 1..steps) {
        val tickFraction = step.toFloat() / (steps + 1)
        val tickX = lerp(cornerRadius, trackWidth - cornerRadius, tickFraction)
        if (tickX !in (thumbCenter - thumbGap)..(thumbCenter + thumbGap)) {
          drawCircle(
            color = if (tickFraction <= fraction) activeTickColor else inactiveTickColor,
            radius = stopRadius,
            center = Offset(tickX, center.y),
          )
        }
      }
    }
  }
}

@Composable
private fun rememberSliderThumbActive(interactionSource: MutableInteractionSource): Boolean {
  val interactions = remember { mutableStateListOf<Interaction>() }
  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect { interaction ->
      when (interaction) {
        is PressInteraction.Press -> interactions.add(interaction)
        is PressInteraction.Release -> interactions.remove(interaction.press)
        is PressInteraction.Cancel -> interactions.remove(interaction.press)
        is DragInteraction.Start -> interactions.add(interaction)
        is DragInteraction.Stop -> interactions.remove(interaction.start)
        is DragInteraction.Cancel -> interactions.remove(interaction.start)
      }
    }
  }
  return interactions.isNotEmpty()
}

@Composable
private fun PlainSliderThumb(color: Color, enabled: Boolean, thumbActive: Boolean) {
  Box(
    modifier = (
      Modifier
        .width(if (thumbActive) SliderThumbWidth / 2 else SliderThumbWidth)
        .height(SliderThumbHeight)
      ) then buildModifier {
      if (enabled) {
        add(Modifier.pointerHoverIcon(PointerIcon.Hand))
      }
      add(Modifier.background(color, MaterialTheme.shapes.extraLarge))
    },
  )
}

@Composable
fun Slider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  @IntRange(from = 0) steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  colors: SliderColors = SliderDefaults.colors(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  val thumbActive = rememberSliderThumbActive(interactionSource)
  UnstyledSlider(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier.minimumInteractiveComponentSize().height(SliderHeight),
    enabled = enabled,
    interactionSource = interactionSource,
    valueRange = valueRange,
    steps = steps,
    onValueChangeFinished = onValueChangeFinished,
    orientation = Orientation.Horizontal,
    track = { state ->
      PlainSliderTrack(
        progress = state.fraction,
        steps = state.steps,
        enabled = state.enabled,
        thumbActive = thumbActive,
        colors = colors,
      )
    },
    thumb = { state ->
      PlainSliderThumb(colors.thumbColorFor(state.enabled), state.enabled, thumbActive)
    },
  )
}

@Composable
fun Slider(
  state: SliderState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: SliderColors = SliderDefaults.colors(),
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  thumb: @Composable (SliderState) -> Unit = {},
  track: @Composable (SliderState) -> Unit = {},
) {
  val thumbActive = rememberSliderThumbActive(interactionSource)
  UnstyledSlider(
    value = state.value,
    onValueChange = { state.value = it },
    modifier = modifier.minimumInteractiveComponentSize().height(SliderHeight),
    enabled = enabled,
    interactionSource = interactionSource,
    valueRange = state.valueRange,
    steps = state.steps,
    onValueChangeFinished = state.onValueChangeFinished,
    orientation = Orientation.Horizontal,
    track = { unstyledState ->
      PlainSliderTrack(
        progress = unstyledState.fraction,
        steps = unstyledState.steps,
        enabled = unstyledState.enabled,
        thumbActive = thumbActive,
        colors = colors,
      )
      track(state)
    },
    thumb = { unstyledState ->
      PlainSliderThumb(
        colors.thumbColorFor(unstyledState.enabled),
        unstyledState.enabled,
        thumbActive,
      )
      thumb(state)
    },
  )
}

@Composable
fun RangeSlider(
  value: ClosedFloatingPointRange<Float>,
  onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  @IntRange(from = 0) steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  colors: SliderColors = SliderDefaults.colors(),
) {
}

@Composable
fun RangeSlider(
  value: ClosedFloatingPointRange<Float>,
  onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  onValueChangeFinished: (() -> Unit)? = null,
  colors: SliderColors = SliderDefaults.colors(),
  startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  startThumb: @Composable (RangeSliderState) -> Unit = {},
  endThumb: @Composable (RangeSliderState) -> Unit = {},
  track: @Composable (RangeSliderState) -> Unit = {},
  @IntRange(from = 0) steps: Int = 0,
) {
}

@Composable
fun RangeSlider(
  state: RangeSliderState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  colors: SliderColors = SliderDefaults.colors(),
  startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  startThumb: @Composable (RangeSliderState) -> Unit = {},
  endThumb: @Composable (RangeSliderState) -> Unit = {},
  track: @Composable (RangeSliderState) -> Unit = {},
) {
}
