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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.roundToInt
import kotlin.time.Duration

class ScrollbarScope internal constructor(
  internal val dragInteraction: MutableState<DragInteraction.Start?>,
  internal val sliderAdapter: SliderAdapter,
  internal val mutableInteractionSource: MutableInteractionSource,
  internal val scrollAreaState: ScrollAreaState,
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
  scrollAreaState: ScrollAreaState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  reverseLayout: Boolean = false,
  thumb: @Composable (ScrollbarScope.() -> Unit),
) = ScrollBar(scrollAreaState, modifier, enabled, interactionSource, reverseLayout, true, thumb)

@Composable
fun UnstyledHorizontalScrollbar(
  scrollAreaState: ScrollAreaState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  reverseLayout: Boolean = false,
  thumb: @Composable (ScrollbarScope.() -> Unit),
) = ScrollBar(scrollAreaState, modifier, enabled, interactionSource, reverseLayout, false, thumb)

@Composable
private fun ScrollBar(
  scrollAreaState: ScrollAreaState,
  modifier: Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  reverse: Boolean = false,
  isVertical: Boolean,
  thumb: @Composable (ScrollbarScope.() -> Unit),
) = with(LocalDensity.current) {
  val reverseLayout = if (LocalLayoutDirection.current == LayoutDirection.Rtl) !reverse else reverse
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

  val defaultMinThumbSize = 16.dp.toPx()
  var preferredMinThumbSize by remember { mutableStateOf(defaultMinThumbSize) }

  val coroutineScope = rememberCoroutineScope()
  val sliderAdapter = remember(
    scrollAreaState,
    containerSize,
    preferredMinThumbSize,
    reverseLayout,
    isVertical,
    coroutineScope,
  ) {
    SliderAdapter(
      scrollAreaState,
      containerSize,
      preferredMinThumbSize,
      reverseLayout,
      isVertical,
      coroutineScope,
    )
  }

  val scrollbarScope = remember(sliderAdapter, resolvedInteractionSource, scrollAreaState) {
    ScrollbarScope(
      dragInteraction,
      sliderAdapter,
      resolvedInteractionSource,
      scrollAreaState,
    )
  }
  val scrollThickness = 8.dp.roundToPx()

  val measurePolicy = if (isVertical) {
    remember(sliderAdapter, scrollThickness, defaultMinThumbSize) {
      verticalMeasurePolicy(
        sliderAdapter = sliderAdapter,
        setContainerSize = { containerSize = it },
        scrollThickness = scrollThickness,
        defaultMinThumbSize = defaultMinThumbSize,
        setPreferredMinThumbSize = { preferredMinThumbSize = it },
      )
    }
  } else {
    remember(sliderAdapter, scrollThickness, defaultMinThumbSize) {
      horizontalMeasurePolicy(
        sliderAdapter = sliderAdapter,
        setContainerSize = { containerSize = it },
        scrollThickness = scrollThickness,
        defaultMinThumbSize = defaultMinThumbSize,
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
        isTrackHovered,
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
  val adapter: ScrollAreaState,
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
  scrollThickness: Int,
  defaultMinThumbSize: Float,
  setPreferredMinThumbSize: (Float) -> Unit,
) = MeasurePolicy { measurables, constraints ->
  updatePreferredMinThumbSize(
    measurable = measurables.firstOrNull(),
    isVertical = true,
    crossAxisSize = scrollThickness,
    defaultMinThumbSize = defaultMinThumbSize,
    setPreferredMinThumbSize = setPreferredMinThumbSize,
  )
  setContainerSize(constraints.maxHeight)
  val pixelRange = sliderAdapter.thumbPixelRange
  val placeable = measurables.firstOrNull()?.measure(
    Constraints.fixed(
      constraints.constrainWidth(scrollThickness),
      pixelRange.size,
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
  scrollThickness: Int,
  defaultMinThumbSize: Float,
  setPreferredMinThumbSize: (Float) -> Unit,
) = MeasurePolicy { measurables, constraints ->
  updatePreferredMinThumbSize(
    measurable = measurables.firstOrNull(),
    isVertical = false,
    crossAxisSize = scrollThickness,
    defaultMinThumbSize = defaultMinThumbSize,
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
      Constraints.fixed(
        pixelRange.size,
        constraints.constrainHeight(scrollThickness),
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
  defaultMinThumbSize: Float,
  setPreferredMinThumbSize: (Float) -> Unit,
) {
  if (measurable == null) return
  val intrinsicMainAxisSize = if (isVertical) {
    measurable.minIntrinsicHeight(crossAxisSize)
  } else {
    measurable.minIntrinsicWidth(crossAxisSize)
  }.coerceAtLeast(0)
  val preferredMinThumbSize = intrinsicMainAxisSize.toFloat().coerceAtLeast(defaultMinThumbSize)
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
      } else if (!drag.pressed) {
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
