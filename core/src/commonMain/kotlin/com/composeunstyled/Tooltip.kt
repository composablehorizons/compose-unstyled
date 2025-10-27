package com.composeunstyled

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.popup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import core.com.composeunstyled.interceptingLongClickable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class TooltipState {
    var show by mutableStateOf(false)
    var arrowDirection by mutableStateOf(TooltipArrowDirection.Down)
    var arrowOffset by mutableStateOf(IntOffset.Zero)
    var placement by mutableStateOf<RelativeAlignment>(RelativeAlignment.TopCenter)
}

enum class TooltipArrowDirection {
    Up, Down, Left, Right
}

internal val LocalTooltipState = staticCompositionLocalOf<TooltipState?> { null }

@Composable
fun Tooltip(
    enabled: Boolean = true,
    panel: @Composable () -> Unit,
    placement: RelativeAlignment = RelativeAlignment.TopCenter,
    longPressShowDurationMillis: Long = 1500L,
    hoverDelayMillis: Long = 0L,
    anchor: @Composable () -> Unit
) {
    val state = remember { TooltipState() }
    var focused by remember { mutableStateOf(false) }
    var entered by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var timerJob: Job? by remember { mutableStateOf(null) }
    var hoverDelayJob: Job? by remember { mutableStateOf(null) }

    fun showTooltip(duration: Long) {
        timerJob = scope.launch {
            state.show = true
            delay(duration)
            state.show = false
        }
    }

    KeyEventObserver { event ->
        if (event.key == Key.Escape && event.isKeyDown) {
            entered = false
            focused = false
        }
        false // never consume
    }

    SideEffect {
        state.arrowDirection = arrowDirection(placement)
        state.placement = placement
    }

    // focus handling - show instantly when focused
    LaunchedEffect(focused, enabled) {
        if (enabled && focused) {
            state.show = true

            hoverDelayJob?.cancel()
            timerJob?.cancel()

            hoverDelayJob = null
            timerJob = null
        } else if (!focused && !entered) {
            state.show = false
        }
    }

    // hover handling - show after hoverDelayMillis
    LaunchedEffect(entered, enabled, hoverDelayMillis) {
        if (enabled && entered) {
            hoverDelayJob?.cancel()

            hoverDelayJob = scope.launch {
                delay(hoverDelayMillis)
                state.show = true
            }
        } else {
            // Mouse left - cancel hover delay and hide tooltip
            hoverDelayJob?.cancel()
            hoverDelayJob = null
            if (!focused) {
                state.show = false
            }
        }
    }

    FloatingContent(
        modifier = Modifier
            .interceptingLongClickable(
                onLongPress = {
                    showTooltip(longPressShowDurationMillis)
                }
            )
            .onFocusChanged {
                focused = it.hasFocus
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> {
                                entered = true
                            }

                            PointerEventType.Exit -> {
                                entered = false
                            }
                        }
                    }
                }
            },
        placement = placement,
        floatingContent = {
            CompositionLocalProvider(LocalTooltipState provides state) {
                panel()
            }
        },
        anchor = anchor,
    )
}

@Composable
fun TooltipPanel(
    modifier: Modifier = Modifier,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    content: @Composable () -> Unit
) {
    val state = LocalTooltipState.current

    val showTooltip = state?.show ?: false

    AnimatedVisibility(
        visible = showTooltip,
        enter = enter,
        exit = exit,
        modifier = Modifier.tooltipPanelSemantics(),
    ) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(backgroundColor)
                .padding(contentPadding)
        ) {
            ProvideContentColor(contentColor) {
                content()
            }
        }
    }
}

@Composable
fun TooltipPanel(
    modifier: Modifier = Modifier,
    arrow: @Composable (TooltipArrowDirection) -> Unit,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    content: @Composable () -> Unit
) {
    val state = LocalTooltipState.current

    val showTooltip = state?.show ?: false
    val arrowDirection = state?.arrowDirection ?: TooltipArrowDirection.Down
    val arrowOffset = state?.arrowOffset ?: IntOffset.Zero
    val placement = state?.placement ?: RelativeAlignment.TopCenter

    AnimatedVisibility(
        visible = showTooltip,
        enter = enter,
        exit = exit,
        modifier = modifier.tooltipPanelSemantics() then buildModifier {
            if (showTooltip) {
                add(
                    Modifier.onGloballyPositioned { panelCoordinates ->
                        panelCoordinates.parentLayoutCoordinates?.let { parentCoordinates ->
                            val panelPosition = parentCoordinates.localPositionOf(panelCoordinates, Offset.Zero)
                            val anchorSize = parentCoordinates.size
                            val panelSize = panelCoordinates.size

                            val idealX = when (placement) {
                                RelativeAlignment.TopCenter,
                                RelativeAlignment.BottomCenter -> (anchorSize.width - panelSize.width) / 2f

                                RelativeAlignment.TopStart, RelativeAlignment.BottomStart -> 0f
                                RelativeAlignment.TopEnd,
                                RelativeAlignment.BottomEnd -> (anchorSize.width - panelSize.width).toFloat()

                                RelativeAlignment.CenterStart -> -panelSize.width.toFloat()
                                RelativeAlignment.CenterEnd -> anchorSize.width.toFloat()
                            }

                            val idealY = when (placement) {
                                RelativeAlignment.TopStart,
                                RelativeAlignment.TopCenter,
                                RelativeAlignment.TopEnd -> -panelSize.height.toFloat()

                                RelativeAlignment.CenterStart,
                                RelativeAlignment.CenterEnd -> (anchorSize.height - panelSize.height) / 2f

                                RelativeAlignment.BottomStart,
                                RelativeAlignment.BottomCenter,
                                RelativeAlignment.BottomEnd -> anchorSize.height.toFloat()
                            }

                            state.arrowOffset = IntOffset(
                                x = (panelPosition.x - idealX).toInt(),
                                y = (panelPosition.y - idealY).toInt()
                            )
                        }
                    })
            }
        },
    ) {
        when (arrowDirection) {
            TooltipArrowDirection.Up -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.offset { IntOffset(-arrowOffset.x, 0) }) {
                    arrow(arrowDirection)
                }
                content()
            }

            TooltipArrowDirection.Down -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                content()
                Box(modifier = Modifier.offset { IntOffset(-arrowOffset.x, 0) }) {
                    arrow(arrowDirection)
                }
            }

            TooltipArrowDirection.Left -> Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.offset { IntOffset(0, -arrowOffset.y) }) {
                    arrow(arrowDirection)
                }
                content()
            }

            TooltipArrowDirection.Right -> Row(verticalAlignment = Alignment.CenterVertically) {
                content()
                Box(modifier = Modifier.offset { IntOffset(0, -arrowOffset.y) }) {
                    arrow(arrowDirection)
                }
            }
        }
    }
}

private fun arrowDirection(alignment: RelativeAlignment): TooltipArrowDirection {
    return when (alignment) {
        RelativeAlignment.TopStart,
        RelativeAlignment.TopCenter,
        RelativeAlignment.TopEnd -> TooltipArrowDirection.Down

        RelativeAlignment.BottomStart,
        RelativeAlignment.BottomCenter,
        RelativeAlignment.BottomEnd -> TooltipArrowDirection.Up

        RelativeAlignment.CenterStart -> TooltipArrowDirection.Right
        RelativeAlignment.CenterEnd -> TooltipArrowDirection.Left
    }
}

@Composable
private fun Modifier.tooltipPanelSemantics(): Modifier {
    val showTooltip = LocalTooltipState.current?.show ?: false

    return this then Modifier.semantics {
        popup()
        if (showTooltip) {
            liveRegion = LiveRegionMode.Assertive
        }
    }
}
