package com.composeunstyled.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

@Composable
fun rememberColoredIndication(
    hoveredColor: Color = Color.Unspecified,
    pressedColor: Color = Color.Unspecified,
    focusedColor: Color = Color.Unspecified,
    draggedColor: Color = Color.Unspecified,
    showAnimationSpec: AnimationSpec<Float> = snap(),
    hideAnimationSpec: AnimationSpec<Float> = snap()
): IndicationNodeFactory {
    return remember {
        ColoredIndication(
            hoveredColor = hoveredColor,
            pressedColor = pressedColor,
            focusedColor = focusedColor,
            draggedColor = draggedColor,
            animationSpecEnter = showAnimationSpec,
            animationSpecExit = hideAnimationSpec
        )
    }
}

@Composable
fun rememberColoredIndication(
    color: Color,
    draggedAlpha: Float = 0.16f,
    focusedAlpha: Float = 0.1f,
    hoveredAlpha: Float = 0.08f,
    pressedAlpha: Float = 0.1f,
    showAnimationSpec: AnimationSpec<Float> = snap(),
    hideAnimationSpec: AnimationSpec<Float> = snap()
): IndicationNodeFactory {
    return remember {
        ColoredIndication(
            hoveredColor = color.copy(alpha = hoveredAlpha),
            pressedColor = color.copy(alpha = pressedAlpha),
            focusedColor = color.copy(alpha = focusedAlpha),
            draggedColor = color.copy(alpha = draggedAlpha),
            animationSpecEnter = showAnimationSpec,
            animationSpecExit = hideAnimationSpec
        )
    }
}

class ColoredIndication(
    private val hoveredColor: Color,
    private val pressedColor: Color,
    private val focusedColor: Color,
    private val draggedColor: Color,
    private val animationSpecEnter: AnimationSpec<Float>,
    private val animationSpecExit: AnimationSpec<Float>
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode = ColoredIndicationInstance(
        interactionSource = interactionSource,
        hoveredColor = hoveredColor,
        pressedColor = pressedColor,
        focusedColor = focusedColor,
        draggedColor = draggedColor,
        animationSpecEnter = animationSpecEnter,
        animationSpecExit = animationSpecExit
    )

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this

    private class ColoredIndicationInstance(
        private val interactionSource: InteractionSource,
        private val hoveredColor: Color,
        private val pressedColor: Color,
        private val focusedColor: Color,
        private val draggedColor: Color,
        private val animationSpecEnter: AnimationSpec<Float>,
        private val animationSpecExit: AnimationSpec<Float>
    ) : Modifier.Node(), DrawModifierNode {

        private val pressedAlpha = Animatable(0f)
        private val hoveredAlpha = Animatable(0f)
        private val focusedAlpha = Animatable(0f)
        private val draggedAlpha = Animatable(0f)

        private val timeSource = TimeSource.Monotonic
        private var pressStartTime: TimeSource.Monotonic.ValueTimeMark? = null
        private var hoverStartTime: TimeSource.Monotonic.ValueTimeMark? = null
        private var focusStartTime: TimeSource.Monotonic.ValueTimeMark? = null
        private var dragStartTime: TimeSource.Monotonic.ValueTimeMark? = null
        private val minimumVisualDuration = 25L

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        // press
                        is PressInteraction.Press -> {
                            launch {
                                pressStartTime = timeSource.markNow()
                                pressedAlpha.animateTo(1f, animationSpecEnter)
                            }
                        }

                        is PressInteraction.Release -> {
                            launch {
                                val elapsed = pressStartTime?.elapsedNow()?.inWholeMilliseconds ?: 0L
                                val remaining = minimumVisualDuration - elapsed

                                if (remaining > 0) {
                                    delay(remaining)
                                }

                                pressedAlpha.animateTo(0f, animationSpecExit)
                                pressStartTime = null
                            }
                        }

                        is PressInteraction.Cancel -> {
                            launch {
                                val elapsed = pressStartTime?.elapsedNow()?.inWholeMilliseconds ?: 0L
                                val remaining = minimumVisualDuration - elapsed

                                if (remaining > 0) {
                                    delay(remaining)
                                }

                                pressedAlpha.animateTo(0f, animationSpecExit)
                                pressStartTime = null
                            }
                        }

                        // hover
                        is HoverInteraction.Enter -> {
                            launch {
                                hoverStartTime = timeSource.markNow()
                                hoveredAlpha.animateTo(1f, animationSpecEnter)
                            }
                        }

                        is HoverInteraction.Exit -> {
                            launch {
                                val elapsed = hoverStartTime?.elapsedNow()?.inWholeMilliseconds ?: 0L
                                val remaining = minimumVisualDuration - elapsed

                                if (remaining > 0) {
                                    delay(remaining)
                                }

                                hoveredAlpha.animateTo(0f, animationSpecExit)
                                hoverStartTime = null
                            }
                        }

                        // focus
                        is FocusInteraction.Focus -> {
                            launch {
                                focusStartTime = timeSource.markNow()
                                focusedAlpha.animateTo(1f, animationSpecEnter)
                            }
                        }

                        is FocusInteraction.Unfocus -> {
                            launch {
                                val elapsed = focusStartTime?.elapsedNow()?.inWholeMilliseconds ?: 0L
                                val remaining = minimumVisualDuration - elapsed

                                if (remaining > 0) {
                                    delay(remaining)
                                }

                                focusedAlpha.animateTo(0f, animationSpecExit)
                                focusStartTime = null
                            }
                        }

                        // drag
                        is DragInteraction.Start -> {
                            launch {
                                dragStartTime = timeSource.markNow()
                                draggedAlpha.animateTo(1f, animationSpecEnter)
                            }
                        }

                        is DragInteraction.Stop -> {
                            launch {
                                val elapsed = dragStartTime?.elapsedNow()?.inWholeMilliseconds ?: 0L
                                val remaining = minimumVisualDuration - elapsed

                                if (remaining > 0) {
                                    delay(remaining)
                                }

                                draggedAlpha.animateTo(0f, animationSpecExit)
                                dragStartTime = null
                            }
                        }

                        is DragInteraction.Cancel -> {
                            launch {
                                val elapsed = dragStartTime?.elapsedNow()?.inWholeMilliseconds ?: 0L
                                val remaining = minimumVisualDuration - elapsed

                                if (remaining > 0) {
                                    delay(remaining)
                                }

                                draggedAlpha.animateTo(0f, animationSpecExit)
                                dragStartTime = null
                            }
                        }
                    }
                }
            }
        }

        override fun ContentDrawScope.draw() {
            drawContent()
            if (pressedAlpha.value > 0f) {
                drawRect(color = pressedColor, alpha = pressedAlpha.value, size = size)
            }
            if (draggedAlpha.value > 0f) {
                drawRect(color = draggedColor, alpha = draggedAlpha.value, size = size)
            }
            if (hoveredAlpha.value > 0f) {
                drawRect(color = hoveredColor, alpha = hoveredAlpha.value, size = size)
            }
            if (focusedAlpha.value > 0f) {
                drawRect(color = focusedColor, alpha = focusedAlpha.value, size = size)
            }
        }
    }
}
