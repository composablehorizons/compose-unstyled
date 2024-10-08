package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.*
import kotlin.js.JsName
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


@Composable
fun rememberScrollAreaState(scrollState: ScrollState): ScrollAreaState = remember(scrollState) {
    ScrollStateScrollAreaState(scrollState)
}

@Composable
fun rememberScrollAreaState(lazyListState: LazyListState): ScrollAreaState = remember(lazyListState) {
    LazyListScrollAreaState(lazyListState)
}

@Composable
fun rememberScrollAreaState(lazyGridState: LazyGridState): ScrollAreaState = remember(lazyGridState) {
    LazyGridScrollAreaScrollAreaState(lazyGridState)
}

@JvmInline
@Immutable
value class OverscrollSides private constructor(private val id: Int) {
    companion object {
        val Top = OverscrollSides(0)
        val Bottom = OverscrollSides(1)
        val Left = OverscrollSides(2)
        val Right = OverscrollSides(3)
        val Vertical = OverscrollSides(3)
        val Horizontal = OverscrollSides(3)
    }
}

@Composable
fun ScrollArea(
    state: ScrollAreaState,
    modifier: Modifier = Modifier,
    overscrollEffect: OverscrollEffect? = ScrollableDefaults.overscrollEffect(),
    overscrollEffectSides: List<OverscrollSides> = listOf(
        OverscrollSides.Vertical, OverscrollSides.Horizontal
    ),
    content: @Composable ScrollAreaScope.() -> Unit
) {
    val scope = rememberCoroutineScope()

    val scrollEvents = remember { MutableSharedFlow<Unit>() }
    NoOverscroll {
        Box(modifier.nestedScroll(remember {
            object : NestedScrollConnection {
                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                    if (source == NestedScrollSource.Drag && overscrollEffect != null) {
                        // they are scrolling past a dead-end
                        // forward to overscrollEffect's direction they are trying to go

                        val isOverscrollTop =
                            isMovingBackwards(available.y) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Top || it == OverscrollSides.Vertical }

                        val isOverscrollBottom =
                            isMovingForward(available.y) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Bottom || it == OverscrollSides.Vertical }

                        val isOverscrollLeft =
                            isMovingBackwards(available.x) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Left || it == OverscrollSides.Horizontal }

                        val isOverscrollRight =
                            isMovingForward(available.x) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Right || it == OverscrollSides.Horizontal }

                        if (isOverscrollTop || isOverscrollBottom || isOverscrollLeft || isOverscrollRight) {
                            return overscrollEffect.applyToScroll(available, source, performScroll)
                        }
                    }
                    return super.onPostScroll(consumed, available, source)
                }


                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    scope.launch {
                        scrollEvents.emit(Unit)
                    }
                    if (source == NestedScrollSource.Drag && overscrollEffect != null) {
                        // they have already started scrolling
                        // forward to overscrollEffect's opposite direction they are trying to go

                        val isOverscrollTop =
                            isMovingForward(available.y) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Top || it == OverscrollSides.Vertical }

                        val isOverscrollBottom =
                            isMovingBackwards(available.y) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Bottom || it == OverscrollSides.Vertical }

                        val isOverscrollLeft =
                            isMovingForward(available.x) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Left || it == OverscrollSides.Horizontal }

                        val isOverscrollRight =
                            isMovingBackwards(available.x) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Right || it == OverscrollSides.Horizontal }

                        if (isOverscrollTop || isOverscrollBottom || isOverscrollLeft || isOverscrollRight) {
                            return overscrollEffect.applyToScroll(available, source, performScroll)
                        }
                    }

                    return super.onPreScroll(available, source)
                }

                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                    if (overscrollEffect != null) {
                        val isOverscrollTop =
                            isMovingBackwards(available.y) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Top || it == OverscrollSides.Vertical }

                        val isOverscrollBottom =
                            isMovingForward(available.y) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Bottom || it == OverscrollSides.Vertical }

                        val isOverscrollLeft =
                            isMovingBackwards(available.x) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Left || it == OverscrollSides.Horizontal }

                        val isOverscrollRight =
                            isMovingForward(available.x) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Right || it == OverscrollSides.Horizontal }

                        if (isOverscrollTop || isOverscrollBottom || isOverscrollLeft || isOverscrollRight) {
                            overscrollEffect.applyToFling(available, performFling)
                            return available
                        }
                    }
                    return super.onPostFling(consumed, available)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (overscrollEffect != null) {
                        val isOverscrollTop =
                            isMovingForward(available.y) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Top || it == OverscrollSides.Vertical }

                        val isOverscrollBottom =
                            isMovingBackwards(available.y) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Bottom || it == OverscrollSides.Vertical }

                        val isOverscrollLeft =
                            isMovingForward(available.x) && canScrollBackwards.not() && overscrollEffectSides.any { it == OverscrollSides.Left || it == OverscrollSides.Horizontal }

                        val isOverscrollRight =
                            isMovingBackwards(available.x) && canScrollForward.not() && overscrollEffectSides.any { it == OverscrollSides.Right || it == OverscrollSides.Horizontal }

                        if (isOverscrollTop || isOverscrollBottom || isOverscrollLeft || isOverscrollRight) {
                            overscrollEffect.applyToFling(available, performFling)
                            return available
                        }
                    }
                    return super.onPreFling(available)
                }

                val performFling: (Velocity) -> Velocity = {
                    // we are only not really managing scrolling
                    // so consume no velocity
                    Velocity.Zero
                }

                val performScroll: (Offset) -> Offset = {
                    // we are only not really managing scrolling
                    // so consume no offset
                    Offset.Zero
                }

                val canScrollBackwards: Boolean
                    get() = state.scrollOffset > 0

                val canScrollForward: Boolean
                    get() = state.scrollOffset < state.maxScrollOffset

                fun isMovingForward(delta: Float): Boolean = delta < 0

                fun isMovingBackwards(delta: Float): Boolean = delta > 0
            }
        }).let { if (overscrollEffect != null) it.overscroll(overscrollEffect) else it }) {

            val boxScope = this
            val scrollAreaScope = remember {
                ScrollAreaScope(boxScope, state, scrollEvents)
            }

            scrollAreaScope.content()
        }
    }
}

@Composable
internal expect fun NoOverscroll(content: @Composable () -> Unit)

class ScrollAreaScope internal constructor(
    private val boxScope: BoxScope,
    internal val scrollAreaState: ScrollAreaState,
    internal val onScrolledEvents: Flow<Unit>
) {
    fun Modifier.align(alignment: Alignment): Modifier {
        return with(boxScope) {
            align(alignment)
        }
    }
}

class ScrollbarScope internal constructor(
    internal val dragInteraction: MutableState<DragInteraction.Start?>,
    internal val sliderAdapter: SliderAdapter,
    internal val mutableInteractionSource: MutableInteractionSource,
    internal val scrollAreaState: ScrollAreaState,
    internal val onScrolledEvents: Flow<Unit>
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
    DisposableEffect(interactionSource) {
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
            dragInteraction, sliderAdapter, interactionSource, scrollAreaState, onScrolledEvents
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

    Layout(content = { scrollbarScope.thumb() },
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
    val content = @Composable {
        Box(modifier.let {
            if (enabled) {
                it.scrollbarDrag(
                    interactionSource = mutableInteractionSource,
                    draggedInteraction = dragInteraction,
                    sliderAdapter = sliderAdapter,
                )
            } else it
        })

    }
    if (thumbVisibility == ThumbVisibility.AlwaysVisible) {
        content()
    } else if (thumbVisibility is ThumbVisibility.HideWhileIdle) {
        var show by remember { mutableStateOf(false) }

        val isHovered by mutableInteractionSource.collectIsHoveredAsState()
        val isDraggingList by scrollAreaState.interactionSource.collectIsDraggedAsState()

        LaunchedEffect(show, isDraggingList) {
            if (show) {
                delay(thumbVisibility.hideDelay)
                show = false
            }
        }
        LaunchedEffect(isDraggingList, isHovered) {
            if (isDraggingList || isHovered) {
                show = true
            }
        }
        LaunchedEffect(Unit) {
            onScrolledEvents.collect {
                show = true
            }
        }

        AnimatedVisibility(show, enter = thumbVisibility.enter, exit = thumbVisibility.exit) {
            content()
        }
    }
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

    /**
     * The current direction of scroll (1: down/right, -1: up/left, 0: not scrolling)
     */
    private var direction = 0

    /**
     * The currently pressed location (in pixels) on the scrollable axis.
     */
    private var offset: Float? = null

    /**
     * The job that keeps scrolling while the track is pressed.
     */
    private var job: Job? = null

    /**
     * Calculates the direction of scrolling towards the given offset (in pixels).
     */
    private fun directionOfScrollTowards(offset: Float): Int {
        val pixelRange = sliderAdapter.thumbPixelRange
        return when {
            offset < pixelRange.first -> if (reverseLayout) 1 else -1
            offset > pixelRange.last -> if (reverseLayout) -1 else 1
            else -> 0
        }
    }

    /**
     * Scrolls once towards the current offset, if it matches the direction of the current gesture.
     */
    private suspend fun scrollTowardsCurrentOffset() {
        offset?.let {
            val currentDirection = directionOfScrollTowards(it)
            if (currentDirection != direction) return
            with(sliderAdapter.adapter) {
                scrollTo(scrollOffset + currentDirection * viewportSize)
            }
        }
    }

    /**
     * Starts the job that scrolls continuously towards the current offset.
     */
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

    /**
     * Invoked on the first press for a gesture.
     */
    fun onPress(offset: Float) {
        this.offset = offset
        this.direction = directionOfScrollTowards(offset)

        if (direction != 0) startScrolling()
    }

    /**
     * Invoked when the pointer moves while pressed during the gesture.
     */
    fun onMovePressed(offset: Float) {
        this.offset = offset
    }

    /**
     * Cleans up when the gesture finishes.
     */
    private fun cleanupAfterGesture() {
        job?.cancel()
        direction = 0
        offset = null
    }

    /**
     * Invoked when the button is released.
     */
    fun onRelease() {
        cleanupAfterGesture()
    }

    /**
     * Invoked when the gesture is cancelled.
     */
    fun onGestureCancelled() {
        cleanupAfterGesture()
        // Maybe revert to the initial position?
    }

}

/**
 * Detects the pointer events relevant for the "scroll by pressing on the track outside the thumb"
 * gesture and calls the corresponding methods in the [scroller].
 */
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

/**
 * Defines how to scroll the scrollable component and how to display a scrollbar for it.
 *
 * The values of this interface are typically in pixels, but do not have to be.
 * It's possible to create an adapter with any scroll range of `Double` values.
 */
interface ScrollAreaState {

    // We use `Double` values here in order to allow scrolling both very large (think LazyList with
    // millions of items) and very small (think something whose natural coordinates are less than 1)
    // content.

    /**
     * Scroll offset of the content inside the scrollable component.
     *
     * For example, a value of `100` could mean the content is scrolled by 100 pixels from the
     * start.
     */
    val scrollOffset: Double

    /**
     * The size of the scrollable content, on the scrollable axis.
     */
    val contentSize: Double

    /**
     * The size of the viewport, on the scrollable axis.
     */
    val viewportSize: Double

    /**
     * Instantly jump to [scrollOffset].
     *
     * @param scrollOffset target offset to jump to, value will be coerced to the valid
     * scroll range.
     */
    suspend fun scrollTo(scrollOffset: Double)

    val interactionSource: InteractionSource
}

/**
 * The maximum scroll offset of the scrollable content.
 */
val ScrollAreaState.maxScrollOffset: Double
    get() = (contentSize - viewportSize).coerceAtLeast(0.0)

internal class ScrollStateScrollAreaState(
    private val scrollState: ScrollState
) : ScrollAreaState {

    override val interactionSource: InteractionSource
        get() = scrollState.interactionSource

    override val scrollOffset: Double get() = scrollState.value.toDouble()

    override suspend fun scrollTo(scrollOffset: Double) {
        scrollState.scrollTo(scrollOffset.roundToInt())
    }

    override val contentSize: Double
        // This isn't strictly correct, as the actual content can be smaller
        // than the viewport when scrollState.maxValue is 0, but the scrollbar
        // doesn't really care as long as contentSize <= viewportSize; it's
        // just not showing itself
        get() = scrollState.maxValue + viewportSize

    override val viewportSize: Double
        get() = scrollState.viewportSize.toDouble()

}

/**
 * Base class for [LazyListScrollAreaState] and [LazyGridScrollAreaScrollAreaState],
 * and in the future maybe other lazy widgets that lay out their content in lines.
 */
internal abstract class LazyLineContentScrollAreaState : ScrollAreaState {

    // Implement the adapter in terms of "lines", which means either rows,
    // (for a vertically scrollable widget) or columns (for a horizontally
    // scrollable one).
    // For LazyList this translates directly to items; for LazyGrid, it
    // translates to rows/columns of items.

    class VisibleLine(
        val index: Int, val offset: Int
    )

    /**
     * Return the first visible line, if any.
     */
    protected abstract fun firstVisibleLine(): VisibleLine?

    /**
     * Return the total number of lines.
     */
    protected abstract fun totalLineCount(): Int

    /**
     * The sum of content padding (before+after) on the scrollable axis.
     */
    protected abstract fun contentPadding(): Int

    /**
     * Scroll immediately to the given line, and offset it by [scrollOffset] pixels.
     */
    protected abstract suspend fun snapToLine(lineIndex: Int, scrollOffset: Int)

    /**
     * Scroll from the current position by the given amount of pixels.
     */
    protected abstract suspend fun scrollBy(value: Float)

    /**
     * Return the average size (on the scrollable axis) of the visible lines.
     */
    protected abstract fun averageVisibleLineSize(): Double

    /**
     * The spacing between lines.
     */
    protected abstract val lineSpacing: Int

    @JsName("averageVisibleLineSizeProperty")
    private val averageVisibleLineSize by derivedStateOf {
        if (totalLineCount() == 0) 0.0
        else averageVisibleLineSize()
    }

    private val averageVisibleLineSizeWithSpacing get() = averageVisibleLineSize + lineSpacing

    override val scrollOffset: Double
        get() {
            val firstVisibleLine = firstVisibleLine()
            return if (firstVisibleLine == null) {
                0.0
            } else {
                val index = firstVisibleLine.index
                val offset = firstVisibleLine.offset

                index * averageVisibleLineSizeWithSpacing - offset
            }
        }

    override val contentSize: Double
        get() {
            val totalLineCount = totalLineCount()
            return averageVisibleLineSize * totalLineCount + lineSpacing * (totalLineCount - 1).coerceAtLeast(0) + contentPadding()
        }

    override suspend fun scrollTo(scrollOffset: Double) {
        val distance = scrollOffset - this@LazyLineContentScrollAreaState.scrollOffset

        // if we scroll less than viewport we need to use scrollBy function to avoid
        // undesirable scroll jumps (when an item size is different)
        //
        // if we scroll more than viewport we should immediately jump to this position
        // without recreating all items between the current and the new position
        if (abs(distance) <= viewportSize) {
            scrollBy(distance.toFloat())
        } else {
            snapTo(scrollOffset)
        }
    }

    private suspend fun snapTo(scrollOffset: Double) {
        val scrollOffsetCoerced = scrollOffset.coerceIn(0.0, maxScrollOffset)

        val index = (scrollOffsetCoerced / averageVisibleLineSizeWithSpacing).toInt().coerceAtLeast(0)
            .coerceAtMost(totalLineCount() - 1)

        val offset = (scrollOffsetCoerced - index * averageVisibleLineSizeWithSpacing).toInt().coerceAtLeast(0)

        snapToLine(lineIndex = index, scrollOffset = offset)
    }

}

internal class LazyListScrollAreaState(
    private val scrollState: LazyListState
) : LazyLineContentScrollAreaState() {

    override val interactionSource: InteractionSource
        get() = scrollState.interactionSource

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Orientation.Vertical) viewportSize.height
            else viewportSize.width
        }.toDouble()

    /**
     * A heuristic that tries to ignore the "currently stickied" header because it breaks the other
     * computations in this adapter:
     * - The currently stickied header always appears in the list of visible items, with its
     *   regular index. This makes [firstVisibleLine] always return its index, even if the list has
     *   been scrolled far beyond it.
     * - [averageVisibleLineSize] calculates the average size in O(1) by assuming that items don't
     *   overlap, and the stickied item breaks this assumption.
     *
     * Attempts to return the index into `visibleItemsInfo` of the first non-currently-stickied (it
     * could be sticky, but not stickied to the top of the list right now) item, if there is one.
     *
     * Note that this heuristic breaks down if the sticky header covers the entire list, so that
     * it's the only visible item for some portion of the scroll range. But there's currently no
     * known better way to solve it, and it's a relatively unusual case.
     */
    private fun firstFloatingVisibleItemIndex() = with(scrollState.layoutInfo.visibleItemsInfo) {
        when (size) {
            0 -> null
            1 -> 0
            else -> {
                val first = this[0]
                val second = this[1]
                // If either the indices or the offsets aren't continuous, then the first item is
                // sticky, so we return 1
                if ((first.index < second.index - 1) || (first.offset + first.size + lineSpacing > second.offset)) 1
                else 0
            }
        }
    }

    override fun firstVisibleLine(): VisibleLine? {
        val firstFloatingVisibleIndex = firstFloatingVisibleItemIndex() ?: return null
        val firstFloatingItem = scrollState.layoutInfo.visibleItemsInfo[firstFloatingVisibleIndex]
        return VisibleLine(
            index = firstFloatingItem.index, offset = firstFloatingItem.offset
        )
    }

    override fun totalLineCount() = scrollState.layoutInfo.totalItemsCount

    override fun contentPadding() = with(scrollState.layoutInfo) {
        beforeContentPadding + afterContentPadding
    }

    override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(lineIndex, scrollOffset)
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    override fun averageVisibleLineSize() = with(scrollState.layoutInfo.visibleItemsInfo) {
        val firstFloatingIndex = firstFloatingVisibleItemIndex() ?: return@with 0.0
        val first = this[firstFloatingIndex]
        val last = last()
        val count = size - firstFloatingIndex
        (last.offset + last.size - first.offset - (count - 1) * lineSpacing).toDouble() / count
    }

    override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing

}

internal class LazyGridScrollAreaScrollAreaState(
    private val scrollState: LazyGridState
) : LazyLineContentScrollAreaState() {

    override val interactionSource: InteractionSource
        get() = scrollState.interactionSource

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Orientation.Vertical) viewportSize.height
            else viewportSize.width
        }.toDouble()

    private val isVertical: Boolean
        get() = scrollState.layoutInfo.orientation == Orientation.Vertical

    private val unknownLine: Int
        get() = with(LazyGridItemInfo) {
            if (isVertical) UnknownRow else UnknownColumn
        }

    private fun LazyGridItemInfo.line() = if (isVertical) row else column

    private fun LazyGridItemInfo.mainAxisSize() = with(size) {
        if (isVertical) height else width
    }

    private fun LazyGridItemInfo.mainAxisOffset() = with(offset) {
        if (isVertical) y else x
    }

    private val slotsPerLine by derivedStateOf {
        val orientation = scrollState.layoutInfo.orientation

        // count all unique columns or rows of the respective orientation
        scrollState.layoutInfo.visibleItemsInfo.distinctBy { if (orientation == Orientation.Vertical) it.column else it.row }
            .count()
    }

    private fun lineOfIndex(index: Int): Int = index / slotsPerLine.coerceAtLeast(1)

    private fun indexOfFirstInLine(line: Int): Int = line * slotsPerLine

    override fun firstVisibleLine(): VisibleLine? {
        return scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.line() != unknownLine } // Skip exiting items
            ?.let { firstVisibleItem ->
                VisibleLine(
                    index = firstVisibleItem.line(), offset = firstVisibleItem.mainAxisOffset()
                )
            }
    }

    override fun totalLineCount(): Int {
        val itemCount = scrollState.layoutInfo.totalItemsCount
        return if (itemCount == 0) 0
        else lineOfIndex(itemCount - 1) + 1
    }

    override fun contentPadding() = with(scrollState.layoutInfo) {
        beforeContentPadding + afterContentPadding
    }

    override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(
            index = indexOfFirstInLine(lineIndex), scrollOffset = scrollOffset
        )
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    override fun averageVisibleLineSize(): Double {
        val visibleItemsInfo = scrollState.layoutInfo.visibleItemsInfo
        val indexOfFirstKnownLineItem = visibleItemsInfo.indexOfFirst { it.line() != unknownLine }
        if (indexOfFirstKnownLineItem == -1) return 0.0
        val reallyVisibleItemsInfo =  // Non-exiting visible items
            visibleItemsInfo.subList(indexOfFirstKnownLineItem, visibleItemsInfo.size)

        // Compute the size of the last line
        val lastLine = reallyVisibleItemsInfo.last().line()
        val lastLineSize = reallyVisibleItemsInfo.asReversed().asSequence().takeWhile { it.line() == lastLine }
            .maxOf { it.mainAxisSize() }

        val first = reallyVisibleItemsInfo.first()
        val last = reallyVisibleItemsInfo.last()
        val lineCount = last.line() - first.line() + 1
        val lineSpacingSum = (lineCount - 1) * lineSpacing
        return (last.mainAxisOffset() + lastLineSize - first.mainAxisOffset() - lineSpacingSum).toDouble() / lineCount
    }

    override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}

internal class SliderAdapter internal constructor(
    val adapter: ScrollAreaState,
    private val trackSize: Int,
    private val minHeight: Float,
    private val reverseLayout: Boolean,
    private val isVertical: Boolean,
    private val coroutineScope: CoroutineScope
) {
    private val contentSize get() = adapter.contentSize
    private val visiblePart: Double
        get() {
            val contentSize = contentSize
            return if (contentSize == 0.0) 1.0
            else (adapter.viewportSize / contentSize).coerceAtMost(1.0)
        }

    val thumbSize
        get() = (trackSize * visiblePart).coerceAtLeast(minHeight.toDouble())

    private val scrollScale: Double
        get() {
            val extraScrollbarSpace = trackSize - thumbSize
            val extraContentSpace = adapter.maxScrollOffset  // == contentSize - viewportSize
            return if (extraContentSpace == 0.0) 1.0 else extraScrollbarSpace / extraContentSpace
        }

    private val rawPosition: Double
        get() = scrollScale * adapter.scrollOffset

    val position: Double
        get() = if (reverseLayout) trackSize - thumbSize - rawPosition else rawPosition

    val bounds get() = position..position + thumbSize

    // How much of the current drag was ignored because we've reached the end of the scrollbar area
    private var unscrolledDragDistance = 0.0

    /** Called when the thumb dragging starts */
    fun onDragStarted() {
        unscrolledDragDistance = 0.0
    }

    private suspend fun setPosition(value: Double) {
        val rawPosition = if (reverseLayout) {
            trackSize - thumbSize - value
        } else {
            value
        }
        adapter.scrollTo(rawPosition / scrollScale)
    }

    private val dragMutex = Mutex()

    /** Called on every movement while dragging the thumb */
    fun onDragDelta(offset: Offset) {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            // Mutex is used to ensure that all earlier drag deltas were applied
            // before calculating a new raw position
            dragMutex.withLock {
                val dragDelta = if (isVertical) offset.y else offset.x
                val maxScrollPosition = adapter.maxScrollOffset * scrollScale
                val currentPosition = position
                val targetPosition = (currentPosition + dragDelta + unscrolledDragDistance).coerceIn(
                    0.0, maxScrollPosition
                )
                val sliderDelta = targetPosition - currentPosition

                // Have to add to position for smooth content scroll if the items are of different size
                val newPos = position + sliderDelta
                setPosition(newPos)
                unscrolledDragDistance += dragDelta - sliderDelta
            }
        }
    }
}
