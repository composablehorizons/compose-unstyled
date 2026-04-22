package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalDragOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.composeunstyled.buildModifier
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Scope for the scrollbars of a [ScrollArea].
 */
class ScrollbarScope internal constructor(
    internal val dragInteraction: MutableState<DragInteraction.Start?>,
    internal val sliderAdapter: SliderAdapter,
    internal val mutableInteractionSource: MutableInteractionSource,
    internal val scrollAreaState: ScrollAreaState,
)

sealed class ThumbVisibility {
    data object AlwaysVisible : ThumbVisibility()
    data class HideWhileIdle(
        val enter: EnterTransition, val exit: ExitTransition, val hideDelay: Duration
    ) : ThumbVisibility()
}

@Composable
fun ScrollAreaScope.VerticalScrollbar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverseLayout: Boolean = false,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) = ScrollBar(modifier, enabled, interactionSource, reverseLayout, true, thumb)

@Composable
fun ScrollAreaScope.HorizontalScrollbar(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverseLayout2: Boolean = false,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) = ScrollBar(modifier, enabled, interactionSource, reverseLayout2, false, thumb)

@Composable
private fun ScrollAreaScope.ScrollBar(
    modifier: Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    reverse: Boolean = false,
    isVertical: Boolean,
    thumb: @Composable (ScrollbarScope.() -> Unit),
) = with(LocalDensity.current) {
    val reverseLayout = if (LocalLayoutDirection.current == LayoutDirection.Rtl) !reverse else reverse
    val dragInteraction = remember { mutableStateOf<DragInteraction.Start?>(null) }
    androidx.compose.runtime.DisposableEffect(interactionSource) {
        onDispose {
            dragInteraction.value?.let { interaction ->
                interactionSource.tryEmit(DragInteraction.Cancel(interaction))
                dragInteraction.value = null
            }
        }
    }

    var containerSize by remember { mutableStateOf(0) }

    val minimalHeight = 16.dp.toPx()

    val coroutineScope = rememberCoroutineScope()
    val sliderAdapter = remember(
        scrollAreaState, containerSize, minimalHeight, reverseLayout, isVertical, coroutineScope
    ) {
        SliderAdapter(
            scrollAreaState, containerSize, minimalHeight, reverseLayout, isVertical, coroutineScope
        )
    }

    val scrollbarScope = remember(sliderAdapter, containerSize) {
        ScrollbarScope(
            dragInteraction, sliderAdapter, interactionSource, scrollAreaState
        )
    }
    val scrollThickness = 8.dp.roundToPx()

    val measurePolicy = if (isVertical) {
        remember(sliderAdapter, scrollThickness) {
            verticalMeasurePolicy(sliderAdapter, { containerSize = it }, scrollThickness)
        }
    } else {
        remember(sliderAdapter, scrollThickness) {
            horizontalMeasurePolicy(sliderAdapter, { containerSize = it }, scrollThickness)
        }
    }

    Layout(
        content = { scrollbarScope.thumb() },
        modifier = modifier.hoverable(interactionSource = interactionSource).let {
            if (enabled) {
                it.scrollOnPressTrack(isVertical, reverseLayout, sliderAdapter)
            } else {
                it
            }
        },
        measurePolicy = measurePolicy
    )
}

@Composable
fun ScrollbarScope.Thumb(
    modifier: Modifier = Modifier,
    thumbVisibility: ThumbVisibility = ThumbVisibility.AlwaysVisible,
    enabled: Boolean = true,
) {
    if (thumbVisibility == ThumbVisibility.AlwaysVisible) {
        ThumbContent(modifier = modifier, enabled = enabled)
    } else {
        if (thumbVisibility is ThumbVisibility.HideWhileIdle) {
            var showThumb by remember { mutableStateOf(false) }
            var isScrollDragging by remember { mutableStateOf(false) }
            var isThumbDragging by remember { mutableStateOf(false) }
            var isTrackHovered by remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()
            var thumbVisibilityJob: Job? by remember { mutableStateOf(null) }

            fun shouldKeepThumbVisible(): Boolean {
                return scrollAreaState.isScrollInProgress || isScrollDragging || isThumbDragging || isTrackHovered
            }

            fun syncThumbVisibility() {
                thumbVisibilityJob?.cancel()
                if (shouldKeepThumbVisible()) {
                    thumbVisibilityJob = null
                    showThumb = true
                } else {
                    thumbVisibilityJob = scope.launch {
                        delay(thumbVisibility.hideDelay)
                        if (!shouldKeepThumbVisible()) {
                            showThumb = false
                        }
                    }
                }
            }

            LaunchedEffect(
                scrollAreaState.isScrollInProgress,
                isScrollDragging,
                isThumbDragging,
                isTrackHovered
            ) {
                syncThumbVisibility()
            }

            LaunchedEffect(Unit) {
                scrollAreaState.interactionSource.interactions
                    .collect { interaction ->
                        if (interaction is DragInteraction.Start) {
                            isScrollDragging = true
                        } else if (interaction is DragInteraction.Stop || interaction is DragInteraction.Cancel) {
                            isScrollDragging = false
                        }
                    }
            }

            LaunchedEffect(Unit) {
                mutableInteractionSource.interactions
                    .collect { interaction ->
                        if (interaction is DragInteraction.Start) {
                            isThumbDragging = true
                        } else if (interaction is DragInteraction.Stop || interaction is DragInteraction.Cancel) {
                            isThumbDragging = false
                        } else if (interaction is HoverInteraction.Enter) {
                            if (showThumb) {
                                isTrackHovered = true
                            }
                        } else if (interaction is HoverInteraction.Exit) {
                            isTrackHovered = false
                        }
                    }
            }

            AnimatedVisibility(
                visible = showThumb,
                enter = thumbVisibility.enter,
                exit = thumbVisibility.exit
            ) {
                ThumbContent(modifier = modifier, enabled = enabled)
            }
        }
    }
}

@Composable
private fun ScrollbarScope.ThumbContent(
    modifier: Modifier,
    enabled: Boolean
) {
    Box(modifier then buildModifier {
        if (enabled) {
            add(
                Modifier.scrollbarDrag(
                    interactionSource = mutableInteractionSource,
                    draggedInteraction = dragInteraction,
                    sliderAdapter = sliderAdapter,
                )
            )
        }
    }
    )
}

private val SliderAdapter.thumbPixelRange: IntRange
    get() {
        val start = position.roundToInt()
        val endExclusive = start + thumbSize.roundToInt()

        return (start until endExclusive)
    }

private val IntRange.size get() = last + 1 - first

private fun verticalMeasurePolicy(
    sliderAdapter: SliderAdapter, setContainerSize: (Int) -> Unit, scrollThickness: Int
) = MeasurePolicy { measurables, constraints ->
    setContainerSize(constraints.maxHeight)
    val pixelRange = sliderAdapter.thumbPixelRange
    val placeable = measurables.firstOrNull()?.measure(
        Constraints.fixed(
            constraints.constrainWidth(scrollThickness), pixelRange.size
        )
    )
    if (placeable == null) {
        layout(0, constraints.maxHeight) {}
    } else {
        layout(placeable.width, constraints.maxHeight) {
            placeable.place(0, pixelRange.first)
        }
    }
}

private fun horizontalMeasurePolicy(
    sliderAdapter: SliderAdapter, setContainerSize: (Int) -> Unit, scrollThickness: Int
) = MeasurePolicy { measurables, constraints ->
    setContainerSize(constraints.maxWidth)
    val pixelRange = sliderAdapter.thumbPixelRange
    if (measurables.isEmpty()) {
        layout(constraints.maxWidth, constraints.maxHeight) {
            // nothing to do
        }
    } else {
        val placeable = measurables.first().measure(
            Constraints.fixed(
                pixelRange.size, constraints.constrainHeight(scrollThickness)
            )
        )
        layout(constraints.maxWidth, placeable.height) {
            placeable.place(pixelRange.first, 0)
        }
    }
}

private fun Modifier.scrollbarDrag(
    interactionSource: MutableInteractionSource,
    draggedInteraction: MutableState<DragInteraction.Start?>,
    sliderAdapter: SliderAdapter,
): Modifier = composed {
    val currentInteractionSource by rememberUpdatedState(interactionSource)
    val currentDraggedInteraction by rememberUpdatedState(draggedInteraction)
    val currentSliderAdapter by rememberUpdatedState(sliderAdapter)

    pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val interaction = DragInteraction.Start()
            currentInteractionSource.tryEmit(interaction)
            currentDraggedInteraction.value = interaction
            currentSliderAdapter.onDragStarted()
            val isSuccess = drag(down.id) { change ->
                currentSliderAdapter.onDragDelta(change.positionChange())
                change.consume()
            }
            val finishInteraction = if (isSuccess) {
                DragInteraction.Stop(interaction)
            } else {
                DragInteraction.Cancel(interaction)
            }
            currentInteractionSource.tryEmit(finishInteraction)
            currentDraggedInteraction.value = null
        }
    }
}

private fun Modifier.scrollOnPressTrack(
    isVertical: Boolean,
    reverseLayout: Boolean,
    sliderAdapter: SliderAdapter,
) = composed {
    val coroutineScope = rememberCoroutineScope()
    val scroller = remember(sliderAdapter, coroutineScope, reverseLayout) {
        TrackPressScroller(coroutineScope, sliderAdapter, reverseLayout)
    }
    Modifier.pointerInput(scroller) {
        detectScrollViaTrackGestures(
            isVertical = isVertical, scroller = scroller
        )
    }
}

/**
 * Responsible for scrolling when the scrollbar track is pressed (outside the thumb).
 */
private class TrackPressScroller(
    private val coroutineScope: CoroutineScope,
    private val sliderAdapter: SliderAdapter,
    private val reverseLayout: Boolean,
) {
    private var direction = 0
    private var offset: Float? = null
    private var job: Job? = null

    private fun directionOfScrollTowards(offset: Float): Int {
        val pixelRange = sliderAdapter.thumbPixelRange
        return when {
            offset < pixelRange.first -> if (reverseLayout) 1 else -1
            offset > pixelRange.last -> if (reverseLayout) -1 else 1
            else -> 0
        }
    }

    private suspend fun scrollTowardsCurrentOffset() {
        offset?.let {
            val currentDirection = directionOfScrollTowards(it)
            if (currentDirection != direction) return
            with(sliderAdapter.adapter) {
                scrollTo(scrollOffset + currentDirection * viewportSize)
            }
        }
    }

    private fun startScrolling() {
        job?.cancel()
        job = coroutineScope.launch {
            scrollTowardsCurrentOffset()
            delay(DelayBeforeSecondScrollOnTrackPress)
            while (true) {
                scrollTowardsCurrentOffset()
                delay(DelayBetweenScrollsOnTrackPress)
            }
        }
    }

    fun onPress(offset: Float) {
        this.offset = offset
        this.direction = directionOfScrollTowards(offset)

        if (direction != 0) startScrolling()
    }

    fun onMovePressed(offset: Float) {
        this.offset = offset
    }

    private fun cleanupAfterGesture() {
        job?.cancel()
        direction = 0
        offset = null
    }

    fun onRelease() {
        cleanupAfterGesture()
    }

    fun onGestureCancelled() {
        cleanupAfterGesture()
    }
}

private suspend fun PointerInputScope.detectScrollViaTrackGestures(
    isVertical: Boolean, scroller: TrackPressScroller
) {
    fun Offset.onScrollAxis() = if (isVertical) y else x

    awaitEachGesture {
        val down = awaitFirstDown()
        scroller.onPress(down.position.onScrollAxis())

        while (true) {
            val drag = if (isVertical) awaitVerticalDragOrCancellation(down.id)
            else awaitHorizontalDragOrCancellation(down.id)

            if (drag == null) {
                scroller.onGestureCancelled()
                break
            } else if (!drag.pressed) {
                scroller.onRelease()
                break
            } else scroller.onMovePressed(drag.position.onScrollAxis())
        }
    }
}

/**
 * The delay between the 1st and 2nd scroll while the scrollbar track is pressed outside the thumb.
 */
internal const val DelayBeforeSecondScrollOnTrackPress: Long = 300L

/**
 * The delay between each subsequent (after the 2nd) scroll while the scrollbar track is pressed
 * outside the thumb.
 */
internal const val DelayBetweenScrollsOnTrackPress: Long = 100L
