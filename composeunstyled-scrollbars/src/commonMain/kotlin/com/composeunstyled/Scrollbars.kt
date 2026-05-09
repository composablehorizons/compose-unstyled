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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalDragOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalDragOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Duration

@Composable
fun rememberScrollbarState(scrollState: ScrollState): ScrollbarState = remember(scrollState) {
  ScrollStateScrollbarState(scrollState)
}

@Composable
fun rememberScrollbarState(lazyListState: LazyListState): ScrollbarState =
  remember(lazyListState) {
    LazyListScrollbarState(lazyListState)
  }

@Composable
fun rememberScrollbarState(lazyGridState: LazyGridState): ScrollbarState =
  remember(lazyGridState) {
    LazyGridScrollbarState(lazyGridState)
  }

interface ScrollbarState {

  val scrollOffset: Double

  val contentSize: Double

  val viewportSize: Double

  suspend fun scrollTo(scrollOffset: Double)

  val interactionSource: InteractionSource

  val isScrollInProgress: Boolean
}

val ScrollbarState.maxScrollOffset: Double
  get() = (contentSize - viewportSize).coerceAtLeast(0.0)

internal class ScrollStateScrollbarState(
  private val scrollState: ScrollState,
) : ScrollbarState {

  override val isScrollInProgress: Boolean
    get() = scrollState.isScrollInProgress

  override val interactionSource: InteractionSource
    get() = scrollState.interactionSource

  override val scrollOffset: Double get() = scrollState.value.toDouble()

  override suspend fun scrollTo(scrollOffset: Double) {
    scrollState.scrollTo(scrollOffset.roundToInt())
  }

  override val contentSize: Double
    get() = scrollState.maxValue + viewportSize

  override val viewportSize: Double
    get() = scrollState.viewportSize.toDouble()
}

internal abstract class LazyLineContentScrollbarState : ScrollbarState {

  class VisibleLine(
    val index: Int,
    val offset: Int,
  )

  protected abstract fun firstVisibleLine(): VisibleLine?

  protected abstract fun totalLineCount(): Int

  protected abstract fun contentPadding(): Int

  protected abstract suspend fun snapToLine(lineIndex: Int, scrollOffset: Int)

  protected abstract suspend fun scrollBy(value: Float)

  protected abstract fun averageVisibleLineSize(): Double

  protected abstract val lineSpacing: Int

  @JsName("averageVisibleLineSizeProperty")
  private val averageVisibleLineSize by derivedStateOf {
    if (totalLineCount() == 0) {
      0.0
    } else {
      averageVisibleLineSize()
    }
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
      return averageVisibleLineSize * totalLineCount + lineSpacing * (totalLineCount - 1).coerceAtLeast(
        0,
      ) + contentPadding()
    }

  override suspend fun scrollTo(scrollOffset: Double) {
    val distance = scrollOffset - this@LazyLineContentScrollbarState.scrollOffset
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

    val offset =
      (scrollOffsetCoerced - index * averageVisibleLineSizeWithSpacing).toInt().coerceAtLeast(0)

    snapToLine(lineIndex = index, scrollOffset = offset)
  }
}

internal class LazyListScrollbarState(
  private val scrollState: LazyListState,
) : LazyLineContentScrollbarState() {

  override val interactionSource: InteractionSource
    get() = scrollState.interactionSource

  override val isScrollInProgress: Boolean
    get() = scrollState.isScrollInProgress

  override val viewportSize: Double
    get() = with(scrollState.layoutInfo) {
      if (orientation == Orientation.Vertical) {
        viewportSize.height
      } else {
        viewportSize.width
      }
    }.toDouble()

  private fun firstFloatingVisibleItemIndex() = with(scrollState.layoutInfo.visibleItemsInfo) {
    when (size) {
      0 -> null
      1 -> 0
      else -> {
        val first = this[0]
        val second = this[1]
        if ((first.index < second.index - 1) || (first.offset + first.size + lineSpacing > second.offset)) {
          1
        } else {
          0
        }
      }
    }
  }

  override fun firstVisibleLine(): VisibleLine? {
    val firstFloatingVisibleIndex = firstFloatingVisibleItemIndex() ?: return null
    val firstFloatingItem = scrollState.layoutInfo.visibleItemsInfo[firstFloatingVisibleIndex]
    return VisibleLine(
      index = firstFloatingItem.index,
      offset = firstFloatingItem.offset,
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

internal class LazyGridScrollbarState(
  private val scrollState: LazyGridState,
) : LazyLineContentScrollbarState() {

  override val isScrollInProgress: Boolean
    get() = scrollState.isScrollInProgress

  override val interactionSource: InteractionSource
    get() = scrollState.interactionSource

  override val viewportSize: Double
    get() = with(scrollState.layoutInfo) {
      if (orientation == Orientation.Vertical) {
        viewportSize.height
      } else {
        viewportSize.width
      }
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
    scrollState.layoutInfo.visibleItemsInfo.distinctBy {
      if (orientation == Orientation.Vertical) it.column else it.row
    }
      .count()
  }

  private fun lineOfIndex(index: Int): Int = index / slotsPerLine.coerceAtLeast(1)

  private fun indexOfFirstInLine(line: Int): Int = line * slotsPerLine

  override fun firstVisibleLine(): VisibleLine? {
    return scrollState.layoutInfo.visibleItemsInfo.firstOrNull { it.line() != unknownLine }
      ?.let { firstVisibleItem ->
        VisibleLine(
          index = firstVisibleItem.line(),
          offset = firstVisibleItem.mainAxisOffset(),
        )
      }
  }

  override fun totalLineCount(): Int {
    val itemCount = scrollState.layoutInfo.totalItemsCount
    return if (itemCount == 0) {
      0
    } else {
      lineOfIndex(itemCount - 1) + 1
    }
  }

  override fun contentPadding() = with(scrollState.layoutInfo) {
    beforeContentPadding + afterContentPadding
  }

  override suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
    scrollState.scrollToItem(
      index = indexOfFirstInLine(lineIndex),
      scrollOffset = scrollOffset,
    )
  }

  override suspend fun scrollBy(value: Float) {
    scrollState.scrollBy(value)
  }

  override fun averageVisibleLineSize(): Double {
    val visibleItemsInfo = scrollState.layoutInfo.visibleItemsInfo
    val indexOfFirstKnownLineItem = visibleItemsInfo.indexOfFirst { it.line() != unknownLine }
    if (indexOfFirstKnownLineItem == -1) return 0.0
    val reallyVisibleItemsInfo =
      visibleItemsInfo.subList(indexOfFirstKnownLineItem, visibleItemsInfo.size)
    val lastLine = reallyVisibleItemsInfo.last().line()
    val lastLineSize =
      reallyVisibleItemsInfo.asReversed().asSequence().takeWhile { it.line() == lastLine }
        .maxOf { it.mainAxisSize() }

    val first = reallyVisibleItemsInfo.first()
    val last = reallyVisibleItemsInfo.last()
    val lineCount = last.line() - first.line() + 1
    val lineSpacingSum = (lineCount - 1) * lineSpacing
    return (last.mainAxisOffset() + lastLineSize - first.mainAxisOffset() - lineSpacingSum).toDouble() / lineCount
  }

  override val lineSpacing get() = scrollState.layoutInfo.mainAxisItemSpacing
}

class ScrollbarScope internal constructor(
  internal val dragInteraction: MutableState<DragInteraction.Start?>,
  internal val sliderAdapter: SliderAdapter,
  internal val mutableInteractionSource: MutableInteractionSource,
  internal val scrollbarState: ScrollbarState,
)

sealed class ThumbVisibility {
  data object AlwaysVisible : ThumbVisibility()
  data class HideWhileIdle(
    val enter: EnterTransition,
    val exit: ExitTransition,
    val hideDelay: Duration,
  ) : ThumbVisibility()
}

@Composable
fun UnstyledVerticalScrollbar(
  scrollbarState: ScrollbarState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  reverseLayout: Boolean = false,
  thumb: @Composable (ScrollbarScope.() -> Unit),
) = ScrollBar(scrollbarState, modifier, enabled, interactionSource, reverseLayout, true, thumb)

@Composable
fun UnstyledHorizontalScrollbar(
  scrollbarState: ScrollbarState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  reverseLayout: Boolean = false,
  thumb: @Composable (ScrollbarScope.() -> Unit),
) = ScrollBar(scrollbarState, modifier, enabled, interactionSource, reverseLayout, false, thumb)

@Composable
private fun ScrollBar(
  scrollbarState: ScrollbarState,
  modifier: Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  reverse: Boolean = false,
  isVertical: Boolean,
  thumb: @Composable (ScrollbarScope.() -> Unit),
) {
  val reverseLayout = if (LocalLayoutDirection.current == LayoutDirection.Rtl) reverse.not() else reverse
  val dragInteraction = remember { mutableStateOf<DragInteraction.Start?>(null) }
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  androidx.compose.runtime.DisposableEffect(resolvedInteractionSource) {
    onDispose {
      dragInteraction.value?.let { interaction ->
        resolvedInteractionSource.tryEmit(DragInteraction.Cancel(interaction))
        dragInteraction.value = null
      }
    }
  }

  var containerSize by remember { mutableStateOf(0) }

  var preferredMinThumbSize by remember { mutableStateOf(0f) }

  val coroutineScope = rememberCoroutineScope()
  val sliderAdapter = remember(
    scrollbarState,
    containerSize,
    preferredMinThumbSize,
    reverseLayout,
    isVertical,
    coroutineScope,
  ) {
    SliderAdapter(
      scrollbarState,
      containerSize,
      preferredMinThumbSize,
      reverseLayout,
      isVertical,
      coroutineScope,
    )
  }

  val scrollbarScope = remember(sliderAdapter, resolvedInteractionSource, scrollbarState) {
    ScrollbarScope(
      dragInteraction,
      sliderAdapter,
      resolvedInteractionSource,
      scrollbarState,
    )
  }
  val measurePolicy = if (isVertical) {
    remember(sliderAdapter) {
      verticalMeasurePolicy(
        sliderAdapter = sliderAdapter,
        setContainerSize = { containerSize = it },
        setPreferredMinThumbSize = { preferredMinThumbSize = it },
      )
    }
  } else {
    remember(sliderAdapter) {
      horizontalMeasurePolicy(
        sliderAdapter = sliderAdapter,
        setContainerSize = { containerSize = it },
        setPreferredMinThumbSize = { preferredMinThumbSize = it },
      )
    }
  }

  Layout(
    content = { scrollbarScope.thumb() },
    modifier = modifier.hoverable(interactionSource = resolvedInteractionSource).let {
      if (enabled) {
        it.scrollOnPressTrack(isVertical, reverseLayout, sliderAdapter)
      } else {
        it
      }
    },
    measurePolicy = measurePolicy,
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
        return scrollbarState.isScrollInProgress || isScrollDragging || isThumbDragging || isTrackHovered
      }

      fun syncThumbVisibility() {
        thumbVisibilityJob?.cancel()
        if (shouldKeepThumbVisible()) {
          thumbVisibilityJob = null
          showThumb = true
        } else {
          thumbVisibilityJob = scope.launch {
            delay(thumbVisibility.hideDelay)
            if (shouldKeepThumbVisible().not()) {
              showThumb = false
            }
          }
        }
      }

      LaunchedEffect(
        scrollbarState.isScrollInProgress,
        isScrollDragging,
        isThumbDragging,
        isTrackHovered,
      ) {
        syncThumbVisibility()
      }

      LaunchedEffect(Unit) {
        scrollbarState.interactionSource.interactions
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
        exit = thumbVisibility.exit,
      ) {
        ThumbContent(modifier = modifier, enabled = enabled)
      }
    }
  }
}

@Composable
private fun ScrollbarScope.ThumbContent(
  modifier: Modifier,
  enabled: Boolean,
) {
  Box(
    modifier then buildModifier {
      if (enabled) {
        add(
          Modifier.scrollbarDrag(
            interactionSource = mutableInteractionSource,
            draggedInteraction = dragInteraction,
            sliderAdapter = sliderAdapter,
          ),
        )
      }
    },
  )
}

private val SliderAdapter.thumbPixelRange: IntRange
  get() {
    val start = position.roundToInt()
    val endExclusive = start + thumbSize.roundToInt()

    return (start until endExclusive)
  }

private val IntRange.size get() = last + 1 - first

internal class SliderAdapter internal constructor(
  val adapter: ScrollbarState,
  private val trackSize: Int,
  private val minHeight: Float,
  private val reverseLayout: Boolean,
  private val isVertical: Boolean,
  private val coroutineScope: CoroutineScope,
) {
  private val contentSize get() = adapter.contentSize
  private val visiblePart: Double
    get() {
      val contentSize = contentSize
      return if (contentSize == 0.0) {
        1.0
      } else {
        (adapter.viewportSize / contentSize).coerceAtMost(1.0)
      }
    }

  val thumbSize
    get() = (trackSize * visiblePart).coerceAtLeast(minHeight.toDouble())

  private val scrollScale: Double
    get() {
      val extraScrollbarSpace = trackSize - thumbSize
      val extraContentSpace = adapter.maxScrollOffset
      return if (extraContentSpace == 0.0) 1.0 else extraScrollbarSpace / extraContentSpace
    }

  private val rawPosition: Double
    get() = scrollScale * adapter.scrollOffset

  val position: Double
    get() = if (reverseLayout) trackSize - thumbSize - rawPosition else rawPosition

  private var unscrolledDragDistance = 0.0

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

  fun onDragDelta(offset: Offset) {
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
      dragMutex.withLock {
        val dragDelta = if (isVertical) offset.y else offset.x
        val maxScrollPosition = adapter.maxScrollOffset * scrollScale
        val currentPosition = position
        val targetPosition = (currentPosition + dragDelta + unscrolledDragDistance).coerceIn(
          0.0,
          maxScrollPosition,
        )
        val sliderDelta = targetPosition - currentPosition
        val newPos = position + sliderDelta
        setPosition(newPos)
        unscrolledDragDistance += dragDelta - sliderDelta
      }
    }
  }
}

private fun verticalMeasurePolicy(
  sliderAdapter: SliderAdapter,
  setContainerSize: (Int) -> Unit,
  setPreferredMinThumbSize: (Float) -> Unit,
) = MeasurePolicy { measurables, constraints ->
  updatePreferredMinThumbSize(
    measurable = measurables.firstOrNull(),
    isVertical = true,
    crossAxisSize = constraints.maxWidth,
    setPreferredMinThumbSize = setPreferredMinThumbSize,
  )
  setContainerSize(constraints.maxHeight)
  val pixelRange = sliderAdapter.thumbPixelRange
  val placeable = measurables.firstOrNull()?.measure(
    Constraints(
      minWidth = 0,
      maxWidth = constraints.maxWidth,
      minHeight = pixelRange.size,
      maxHeight = pixelRange.size,
    ),
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
  sliderAdapter: SliderAdapter,
  setContainerSize: (Int) -> Unit,
  setPreferredMinThumbSize: (Float) -> Unit,
) = MeasurePolicy { measurables, constraints ->
  updatePreferredMinThumbSize(
    measurable = measurables.firstOrNull(),
    isVertical = false,
    crossAxisSize = constraints.maxHeight,
    setPreferredMinThumbSize = setPreferredMinThumbSize,
  )
  setContainerSize(constraints.maxWidth)
  val pixelRange = sliderAdapter.thumbPixelRange
  if (measurables.isEmpty()) {
    layout(constraints.maxWidth, constraints.maxHeight) {
      // nothing to do
    }
  } else {
    val placeable = measurables.first().measure(
      Constraints(
        minWidth = pixelRange.size,
        maxWidth = pixelRange.size,
        minHeight = 0,
        maxHeight = constraints.maxHeight,
      ),
    )
    layout(constraints.maxWidth, placeable.height) {
      placeable.place(pixelRange.first, 0)
    }
  }
}

private fun updatePreferredMinThumbSize(
  measurable: Measurable?,
  isVertical: Boolean,
  crossAxisSize: Int,
  setPreferredMinThumbSize: (Float) -> Unit,
) {
  if (measurable == null) return
  val intrinsicMainAxisSize = if (isVertical) {
    measurable.minIntrinsicHeight(crossAxisSize)
  } else {
    measurable.minIntrinsicWidth(crossAxisSize)
  }.coerceAtLeast(0)
  val preferredMinThumbSize = intrinsicMainAxisSize.toFloat()
  setPreferredMinThumbSize(preferredMinThumbSize)
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
      isVertical = isVertical,
      scroller = scroller,
    )
  }
}

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
  isVertical: Boolean,
  scroller: TrackPressScroller,
) {
  fun Offset.onScrollAxis() = if (isVertical) y else x

  awaitEachGesture {
    val down = awaitFirstDown()
    scroller.onPress(down.position.onScrollAxis())

    while (true) {
      val drag = if (isVertical) {
        awaitVerticalDragOrCancellation(down.id)
      } else {
        awaitHorizontalDragOrCancellation(down.id)
      }

      if (drag == null) {
        scroller.onGestureCancelled()
        break
      } else if (drag.pressed.not()) {
        scroller.onRelease()
        break
      } else {
        scroller.onMovePressed(drag.position.onScrollAxis())
      }
    }
  }
}

internal const val DelayBeforeSecondScrollOnTrackPress: Long = 300L

internal const val DelayBetweenScrollsOnTrackPress: Long = 100L
