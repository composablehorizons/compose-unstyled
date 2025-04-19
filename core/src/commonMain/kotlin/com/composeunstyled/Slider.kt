package com.composeunstyled

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.PressInteraction.Press
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.composables.core.androidx.annotation.IntRange
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

class SliderState(
    initialValue: Float,
    internal val valueRange: ClosedFloatingPointRange<Float>,
    internal val steps: Int,
) {
    init {
        require(steps >= 0) { "steps must be >= 0" }
    }

    private var innerValue by mutableStateOf(initialValue)

    var value: Float
        get() = innerValue
        set(value) {
            val stepSize = if (steps > 0) (valueRange.endInclusive - valueRange.start) / steps else 0f
            innerValue = if (steps > 0) {
                val snappedValue = ((value - valueRange.start) / stepSize).roundToInt() * stepSize + valueRange.start
                snappedValue.coerceIn(valueRange)
            } else {
                value.coerceIn(valueRange)
            }
        }
}

@Composable
fun rememberSliderState(
    initialValue: Float = 0.0f,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
): SliderState {
    return remember {
        SliderState(initialValue, valueRange, steps)
    }
}

@Composable
private fun CorrectValueSideEffect(
    scaleToOffset: (Float) -> Float,
    correctValue: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    trackRange: ClosedFloatingPointRange<Float>,
    value: Float,
) {
    SideEffect {
        val error = (valueRange.endInclusive - valueRange.start) / 1000
        val newOffset = scaleToOffset(value)
        if (abs(newOffset - value) > error) {
            if (value in trackRange) {
                correctValue(newOffset)
            }
        }
    }
}


@Composable
fun Slider(
    state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    orientation: Orientation = Orientation.Horizontal,
    track: @Composable () -> Unit,
    thumb: @Composable () -> Unit
) {
    var thumbWidthPx by remember { mutableStateOf(0f) }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    var rawOffset by remember { mutableStateOf(0f) }
    var pressOffset by remember { mutableFloatStateOf(0f) }

    val coerced = state.value.coerceIn(valueRange.start, valueRange.endInclusive)
    val fraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    val focusRequester = remember { FocusRequester() }
    var sliderWidth by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    val maxPx: Float = max(sliderWidth - (thumbWidthPx / 2), 0f)
    val minPx: Float = min(thumbWidthPx / 2, maxPx)

    val offset = (sliderWidth - thumbWidthPx) * fraction

    var currentPress by remember { mutableStateOf<Press?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    fun scaleToUserValue(offset: Float) = scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

    fun scaleToOffset(userValue: Float) = scale(
        valueRange.start, valueRange.endInclusive, userValue, minPx, maxPx
    )

    val draggableState = rememberDraggableState { dragAmount ->
        rawOffset = (rawOffset + dragAmount + pressOffset)
        pressOffset = 0f
        val offsetInTrack = rawOffset.coerceIn(minPx, maxPx)
        state.value = scaleToUserValue(offsetInTrack)
    }

    CorrectValueSideEffect(
        scaleToOffset = ::scaleToOffset,
        correctValue = { state.value = it },
        valueRange = valueRange,
        trackRange = minPx..maxPx,
        value = state.value
    )

    fun onDragEnded() {
        isDragging = false
        if (currentPress != null) {
            scope.launch {
                var interaction = PressInteraction.Release(currentPress!!)
                interactionSource.emit(interaction)
                currentPress = null
            }
        }
    }

    val dragOnTap = Modifier
        .pointerInput(draggableState, interactionSource, maxPx, isRtl) {
            if (enabled) {
                detectTapGestures(
                    onPress = { pos ->
                        val to = if (isRtl) maxPx - pos.x else pos.x
                        pressOffset = to - rawOffset
                        var interaction = Press(pos)

                        currentPress = interaction
                        interactionSource.emit(interaction)

                        isDragging = true
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
                        onDragEnded()
                    }
                )
            }
        }

    Box(
        modifier = modifier
                then dragOnTap
            .focusRequester(focusRequester)
            .focusable(enabled, interactionSource = interactionSource)
            .sliderKeyboardInteractions(enabled = enabled, state = state)
            .draggable(
                state = draggableState,
                orientation = orientation,
                onDragStarted = {
                    focusRequester.requestFocus()
                },
                onDragStopped = {
                    onDragEnded()
                },
                interactionSource = interactionSource
            )
            .onSizeChanged { sliderWidth = it.width.toFloat() }
            .sliderSemantics(enabled = enabled, state = state, coerced = coerced),
        contentAlignment = Alignment.CenterStart,
    ) {
        track()

        Box(
            Modifier
                .onSizeChanged { thumbWidthPx = it.width.toFloat() }
                .offset { IntOffset(x = offset.roundToInt(), 0) }
        ) {
            thumb()
        }
    }
}

private fun Modifier.sliderSemantics(enabled: Boolean, state: SliderState, coerced: Float): Modifier {
    return semantics {
        if (enabled.not()) disabled()
        setProgress(
            action = { targetValue ->
                var newValue = targetValue.coerceIn(state.valueRange.start, state.valueRange.endInclusive)
                val originalVal = newValue
                val resolvedValue = if (state.steps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.steps + 1) {
                        val stepValue = lerp(
                            state.valueRange.start,
                            state.valueRange.endInclusive,
                            i.toFloat() / (state.steps + 1)
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }
                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == coerced) {
                    false
                } else {
                    state.value = resolvedValue
                    true
                }
            }
        )
    }
        .progressSemantics(
            state.value,
            state.valueRange.start..state.valueRange.endInclusive,
            state.steps
        )
}

private fun Modifier.sliderKeyboardInteractions(enabled: Boolean, state: SliderState): Modifier {
    if (enabled.not()) return this
    return onKeyEvent { event: KeyEvent ->
        val stepSize = if (state.steps > 0) {
            (state.valueRange.endInclusive - state.valueRange.start) / state.steps
        } else {
            (state.valueRange.endInclusive - state.valueRange.start) * 0.01f
        }
        val pageStepSize = stepSize * 2
        when (event.key) {
            Key.DirectionRight, Key.DirectionUp -> {
                if (event.isKeyDown) {
                    state.value += stepSize
                }
                true
            }

            Key.DirectionLeft, Key.DirectionDown -> {
                if (event.isKeyDown) {
                    state.value -= stepSize
                }
                true
            }

            Key.Home -> {
                if (event.isKeyDown) {
                    state.value = state.valueRange.start
                }
                true
            }

            Key.MoveEnd -> {
                if (event.isKeyDown) {
                    state.value = state.valueRange.endInclusive
                }
                true
            }

            Key.PageUp -> {
                if (event.isKeyDown) {
                    state.value += pageStepSize
                }
                true
            }

            Key.PageDown -> {
                if (event.isKeyDown) {
                    state.value -= pageStepSize
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

@Composable
fun Thumb(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = Color.Unspecified
) {
    Box(
        modifier
            .clip(shape)
            .background(color)
            .size(24.dp)
    )
}
