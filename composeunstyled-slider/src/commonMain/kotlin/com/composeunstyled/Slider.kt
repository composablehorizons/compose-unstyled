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

import androidx.annotation.IntRange
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.GestureCancellationException
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction.Press
import androidx.compose.foundation.interaction.PressInteraction.Release
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private val KeyEvent.isKeyDown: Boolean
  get() = type == KeyEventType.KeyDown

@Stable
class SliderState(
  value: Float,
  val valueRange: ClosedFloatingPointRange<Float>,
  val steps: Int,
  val enabled: Boolean = true,
  val orientation: Orientation = Orientation.Horizontal,
  val isRtl: Boolean = false,
  val isDragging: Boolean = false,
  val isPressed: Boolean = false,
  val isFocused: Boolean = false,
) {
  init {
    require(steps >= 0) { "steps must be >= 0" }
  }

  val tickFractions: FloatArray = stepsToTickFractions(steps)

  val value: Float = snapValueToTick(
    value.coerceIn(valueRange.start, valueRange.endInclusive),
    tickFractions,
    valueRange.start,
    valueRange.endInclusive,
  )

  val fraction: Float
    get() = calcFraction(valueRange.start, valueRange.endInclusive, value)
}

@Composable
private fun CorrectValueSideEffect(
  scaleToOffset: (Float) -> Float,
  scaleToValue: (Float) -> Float,
  correctValue: (Float) -> Unit,
  trackRange: ClosedFloatingPointRange<Float>,
  value: Float,
) {
  SideEffect {
    val offset = scaleToOffset(value)
    val clampedOffset = offset.coerceIn(trackRange.start, trackRange.endInclusive)
    val error = (trackRange.endInclusive - trackRange.start) / 1000
    if (abs(clampedOffset - offset) > error) {
      correctValue(scaleToValue(clampedOffset))
    }
  }
}

internal suspend fun AwaitPointerEventScope.waitRelease(
  pass: PointerEventPass = PointerEventPass.Main,
): PointerInputChange {
  while (true) {
    val event = awaitPointerEvent(pass)
    if (event.changes.fastAll { it.changedToUp() }) {
      // All pointers are up
      return event.changes[0]
    }
  }
}

@Composable
fun UnstyledSlider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
  @IntRange(from = 0)
  steps: Int = 0,
  onValueChangeFinished: (() -> Unit)? = null,
  orientation: Orientation = Orientation.Horizontal,
  reverseDirection: Boolean = false,
  track: @Composable (SliderState) -> Unit,
  thumb: @Composable (SliderState) -> Unit,
) {
  require(steps >= 0) { "steps must be >= 0" }

  var thumbSize by remember { mutableStateOf(IntSize.Zero) }
  val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

  var rawOffset by remember { mutableFloatStateOf(0f) }
  var pressOffset by remember { mutableFloatStateOf(0f) }
  var dragging by remember { mutableStateOf(false) }
  var sliderSize by remember { mutableStateOf(IntSize.Zero) }

  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val isPressed by resolvedInteractionSource.collectIsPressedAsState()
  val isDragged by resolvedInteractionSource.collectIsDraggedAsState()
  val isFocused by resolvedInteractionSource.collectIsFocusedAsState()

  val coerced = snapValueToTick(
    value.coerceIn(valueRange.start, valueRange.endInclusive),
    stepsToTickFractions(steps),
    valueRange.start,
    valueRange.endInclusive,
  )

  val fraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)
  val effectiveReverseDirection =
    reverseDirection != (orientation == Orientation.Horizontal && isRtl)
  val displayFraction = if (effectiveReverseDirection) 1f - fraction else fraction

  val focusRequester = remember { FocusRequester() }

  val scope = rememberCoroutineScope()
  val sliderMainAxisSize = if (orientation == Orientation.Horizontal) {
    sliderSize.width.toFloat()
  } else {
    sliderSize.height.toFloat()
  }
  val thumbMainAxisSize = if (orientation == Orientation.Horizontal) {
    thumbSize.width.toFloat()
  } else {
    thumbSize.height.toFloat()
  }

  val maxPx: Float = max(sliderMainAxisSize - (thumbMainAxisSize / 2), 0f)
  val minPx: Float = min(thumbMainAxisSize / 2, maxPx)

  val offset = (sliderMainAxisSize - thumbMainAxisSize) * displayFraction

  val state = SliderState(
    value = coerced,
    valueRange = valueRange,
    steps = steps,
    enabled = enabled,
    orientation = orientation,
    isRtl = isRtl,
    isDragging = dragging || isDragged,
    isPressed = isPressed,
    isFocused = isFocused,
  )

  fun scaleToUserValue(offset: Float) =
    scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

  fun scaleToOffset(userValue: Float) = scale(
    valueRange.start,
    valueRange.endInclusive,
    userValue,
    minPx,
    maxPx,
  )

  fun updateValueFromOffset(offset: Float) {
    val offsetInTrack = offset.coerceIn(minPx, maxPx)
    val snappedOffset = snapValueToTick(offsetInTrack, state.tickFractions, minPx, maxPx)
    val nextValue = scaleToUserValue(snappedOffset)
    if (nextValue != coerced) {
      onValueChange(nextValue)
    }
  }

  val draggableState = rememberDraggableState { dragAmount ->
    val resolvedDragAmount = if (effectiveReverseDirection) -dragAmount else dragAmount
    rawOffset = (rawOffset + resolvedDragAmount + pressOffset)
    pressOffset = 0f
    updateValueFromOffset(rawOffset)
  }

  CorrectValueSideEffect(
    scaleToOffset = ::scaleToOffset,
    scaleToValue = ::scaleToUserValue,
    correctValue = { onValueChange(it) },
    trackRange = minPx..maxPx,
    value = coerced,
  )

  SideEffect {
    if (!dragging) {
      rawOffset = scaleToOffset(coerced)
    }
  }

  val dragOnTap = Modifier.pointerInput(
    draggableState,
    resolvedInteractionSource,
    sliderMainAxisSize,
    effectiveReverseDirection,
    orientation,
  ) {
    if (enabled) {
      detectTapGestures(
        onPress = { pos ->
          val position = if (orientation == Orientation.Horizontal) pos.x else pos.y
          val to = if (effectiveReverseDirection) sliderMainAxisSize - position else position
          pressOffset = to - rawOffset

          // dispatch a drag so that offset calculations kick in
          scope.launch {
            draggableState.drag(MutatePriority.UserInput) {
              focusRequester.requestFocus()
              dragBy(0f)
            }
          }
          try {
            awaitRelease()
          } catch (_: GestureCancellationException) {
            pressOffset = 0f
          }
        },
        onTap = {
          scope.launch {
            draggableState.drag(MutatePriority.UserInput) {
              // just trigger animation, press offset will be applied
              dragBy(0f)
            }
            onValueChangeFinished?.invoke()
          }
        },
      )
    }
  }
    .pointerInput(draggableState, resolvedInteractionSource, enabled) {
      val currentContext = currentCoroutineContext()
      if (enabled) {
        awaitPointerEventScope {
          while (currentContext.isActive) {
            val down = awaitFirstDown()

            val press = Press(down.position)
            scope.launch { resolvedInteractionSource.emit(press) }
            try {
              waitRelease()
            } catch (_: Exception) {
            }
            scope.launch { resolvedInteractionSource.emit(Release(press)) }
          }
        }
      }
    }

  Box(
    modifier = modifier
      .then(dragOnTap)
      .focusRequester(focusRequester)
      .focusable(enabled, interactionSource = resolvedInteractionSource)
      .sliderKeyboardInteractions(
        enabled = enabled,
        value = coerced,
        valueRange = valueRange,
        steps = steps,
        reverseDirection = effectiveReverseDirection,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
      )
      .draggable(
        state = draggableState,
        orientation = orientation,
        enabled = enabled,
        reverseDirection = effectiveReverseDirection,
        onDragStarted = {
          dragging = true
          focusRequester.requestFocus()
        },
        onDragStopped = {
          dragging = false
          onValueChangeFinished?.invoke()
        },
        interactionSource = resolvedInteractionSource,
      ).onSizeChanged { sliderSize = it }
      .sliderSemantics(
        enabled = enabled,
        value = coerced,
        valueRange = valueRange,
        steps = steps,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
      ),
    contentAlignment = Alignment.CenterStart,
  ) {
    track(state)

    Box(
      Modifier
        .onSizeChanged { thumbSize = it }
        .offset {
          if (orientation == Orientation.Horizontal) {
            IntOffset(x = offset.roundToInt(), y = 0)
          } else {
            IntOffset(x = 0, y = offset.roundToInt())
          }
        },
    ) {
      thumb(state)
    }
  }
}

private fun Modifier.sliderSemantics(
  enabled: Boolean,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  steps: Int,
  onValueChange: (Float) -> Unit,
  onValueChangeFinished: (() -> Unit)?,
): Modifier {
  return semantics {
    if (enabled.not()) disabled()
    if (enabled) {
      setProgress(
        action = { targetValue ->
          val resolvedValue = snapValueToTick(
            targetValue.coerceIn(valueRange.start, valueRange.endInclusive),
            stepsToTickFractions(steps),
            valueRange.start,
            valueRange.endInclusive,
          )
          // This is to keep it consistent with AbsSeekbar.java: return false if no
          // change from current.
          if (resolvedValue == value) {
            false
          } else {
            onValueChange(resolvedValue)
            onValueChangeFinished?.invoke()
            true
          }
        },
      )
    }
  }.progressSemantics(
    value,
    valueRange.start..valueRange.endInclusive,
    steps,
  )
}

private fun Modifier.sliderKeyboardInteractions(
  enabled: Boolean,
  value: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  steps: Int,
  reverseDirection: Boolean,
  onValueChange: (Float) -> Unit,
  onValueChangeFinished: (() -> Unit)?,
): Modifier {
  if (enabled.not()) return this
  return onKeyEvent { event: KeyEvent ->
    val stepSize = if (steps > 0) {
      (valueRange.endInclusive - valueRange.start) / (steps + 1)
    } else {
      (valueRange.endInclusive - valueRange.start) * 0.01f
    }
    val pageStepSize = stepSize * 10
    val sign = if (reverseDirection) -1 else 1
    fun setValue(newValue: Float) {
      val coerced = newValue.coerceIn(valueRange.start, valueRange.endInclusive)
      val snapped = snapValueToTick(
        coerced,
        stepsToTickFractions(steps),
        valueRange.start,
        valueRange.endInclusive,
      )
      onValueChange(snapped)
    }

    when (event.key) {
      Key.DirectionRight, Key.DirectionUp -> {
        if (event.isKeyDown) {
          setValue(value + stepSize * sign)
        } else {
          onValueChangeFinished?.invoke()
        }
        true
      }

      Key.DirectionLeft, Key.DirectionDown -> {
        if (event.isKeyDown) {
          setValue(value - stepSize * sign)
        } else {
          onValueChangeFinished?.invoke()
        }
        true
      }

      Key.Home -> {
        if (event.isKeyDown) {
          setValue(valueRange.start)
        } else {
          onValueChangeFinished?.invoke()
        }
        true
      }

      Key.MoveEnd -> {
        if (event.isKeyDown) {
          setValue(valueRange.endInclusive)
        } else {
          onValueChangeFinished?.invoke()
        }
        true
      }

      Key.PageUp -> {
        if (event.isKeyDown) {
          setValue(value + pageStepSize * sign)
        } else {
          onValueChangeFinished?.invoke()
        }
        true
      }

      Key.PageDown -> {
        if (event.isKeyDown) {
          setValue(value - pageStepSize * sign)
        } else {
          onValueChangeFinished?.invoke()
        }
        true
      }

      else -> false
    }
  }
}

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
  (if (b - a == 0f) 0f else (pos - a) / (b - a)).fastCoerceIn(0f, 1f)

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
  lerp(a2, b2, calcFraction(a1, b1, x1))

internal fun stepsToTickFractions(steps: Int): FloatArray {
  return if (steps == 0) {
    floatArrayOf()
  } else {
    FloatArray(steps + 2) { it.toFloat() / (steps + 1) }
  }
}

internal fun snapValueToTick(
  current: Float,
  tickFractions: FloatArray,
  min: Float,
  max: Float,
): Float {
  return tickFractions
    .minByOrNull { abs(lerp(min, max, it) - current) }
    ?.run { lerp(min, max, this) }
    ?: current
}
