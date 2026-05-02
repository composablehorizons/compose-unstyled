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
@file:OptIn(ExperimentalFoundationApi::class)
@file:Suppress("ktlint:standard:max-line-length", "ktlint:standard:no-wildcard-imports")

package com.composeunstyled

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.composeunstyled.androidx.compose.foundation.gestures.UnstyledAnchoredDraggableState
import com.composeunstyled.androidx.compose.foundation.gestures.UnstyledDraggableAnchors
import com.composeunstyled.androidx.compose.foundation.gestures.animateTo
import com.composeunstyled.androidx.compose.foundation.gestures.snapTo
import com.composeunstyled.androidx.compose.foundation.gestures.unstyledAnchoredDraggable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.JvmName
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private fun Saver(
  animationSpec: AnimationSpec<Float>,
  coroutineScope: CoroutineScope,
  sheetDetents: List<SheetDetent>,
  velocityThreshold: () -> Float,
  positionalThreshold: (totalDistance: Float) -> Float,
  decayAnimationSpec: DecayAnimationSpec<Float>,
  confirmDetentChange: (SheetDetent) -> Boolean,
  localDensity: () -> Density,
): Saver<BottomSheetState, *> =
  mapSaver(save = { mapOf("detent" to it.currentDetent.identifier) }, restore = { map ->
    val selectedDetentName = map["detent"]
    BottomSheetState(
      initialDetent = sheetDetents.first { it.identifier == selectedDetentName },
      detents = sheetDetents,
      coroutineScope = coroutineScope,
      animationSpec = animationSpec,
      velocityThreshold = velocityThreshold,
      positionalThreshold = positionalThreshold,
      decayAnimationSpec = decayAnimationSpec,
      confirmDetentChange = confirmDetentChange,
      density = localDensity,
    )
  })

/**
 * Creates a [BottomSheetState]
 *
 * @param initialDetent The initial detent of the sheet.
 * @param detents The list of detents that the sheet can snap to.
 * @param animationSpec The animation spec to use for animating between detents.
 * @param confirmDetentChange Callback to confirm whether a detent change should be allowed.
 * @param decayAnimationSpec The animation spec to use for decay animations.
 * @param velocityThreshold The velocity threshold for determining whether to snap to the next detent.
 * @param positionalThreshold The positional threshold for determining whether to snap to the next detent.
 * @return A remembered [BottomSheetState] instance.
 */
@Composable
fun rememberBottomSheetState(
  initialDetent: SheetDetent,
  detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
  animationSpec: AnimationSpec<Float> = tween(),
  confirmDetentChange: (SheetDetent) -> Boolean = { true },
  decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
  velocityThreshold: () -> Dp = { 125.dp },
  positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
): BottomSheetState {
  val density = LocalDensity.current
  val scope = rememberCoroutineScope()
  return rememberSaveable(
    saver = Saver(
      animationSpec = animationSpec,
      coroutineScope = scope,
      sheetDetents = detents,
      velocityThreshold = {
        with(density) {
          velocityThreshold().toPx()
        }
      },
      positionalThreshold = { totalDistance ->
        with(density) {
          positionalThreshold(totalDistance.toDp()).toPx()
        }
      },
      decayAnimationSpec = decayAnimationSpec,
      confirmDetentChange = confirmDetentChange,
      localDensity = { density },
    ),
  ) {
    BottomSheetState(
      initialDetent = initialDetent,
      detents = detents,
      coroutineScope = scope,
      animationSpec = animationSpec,
      velocityThreshold = {
        with(density) {
          velocityThreshold().toPx()
        }
      },
      positionalThreshold = { totalDistance ->
        with(density) {
          positionalThreshold(totalDistance.toDp()).toPx()
        }
      },
      decayAnimationSpec = decayAnimationSpec,
      confirmDetentChange = confirmDetentChange,
      density = { density },
    )
  }
}

/**
 * A detent represents a stopping point for a bottom sheet.
 *
 * @property identifier A unique identifier for this detent.
 * @property calculateDetentHeight A function that calculates the height of this detent based on the container and sheet heights.
 */
@Immutable
class SheetDetent(
  val identifier: String,
  val calculateDetentHeight: (containerHeight: Dp, sheetHeight: Dp) -> Dp,
) {
  companion object {
    /**
     * A detent that expands the sheet to its full height.
     */
    val FullyExpanded: SheetDetent =
      SheetDetent("fully-expanded") { containerHeight, sheetHeight -> sheetHeight }

    /**
     * A detent that hides the sheet.
     */
    val Hidden: SheetDetent = SheetDetent("hidden") { containerHeight, sheetHeight -> 0.dp }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as SheetDetent

    return identifier == other.identifier
  }

  override fun hashCode(): Int {
    return identifier.hashCode()
  }

  override fun toString(): String {
    return "SheetDetent(identifier='$identifier')"
  }
}

class BottomSheetState(
  initialDetent: SheetDetent,
  detents: List<SheetDetent>,
  private val coroutineScope: CoroutineScope,
  animationSpec: AnimationSpec<Float>,
  velocityThreshold: () -> Float,
  positionalThreshold: (totalDistance: Float) -> Float,
  decayAnimationSpec: DecayAnimationSpec<Float>,
  val confirmDetentChange: (SheetDetent) -> Boolean,
  private val density: () -> Density,
) {
  init {
    checkValidDetents(detents)
    check(detents.contains(initialDetent)) {
      "The initialDetent ${initialDetent.identifier} was not part of the included detents while creating the sheet's state."
    }
  }

  private fun checkValidDetents(detents: List<SheetDetent>) {
    check(detents.isNotEmpty()) {
      "Tried to create a bottom sheet without any detents. Make sure to pass at least one detent when creating your sheet's state."
    }

    val duplicates = detents.groupBy { it.identifier }.filter { it.value.size > 1 }.map { it.key }

    check(duplicates.isEmpty()) {
      "Detent identifiers need to be unique, but you passed the following detents multiple times: ${duplicates.joinToString { it }}."
    }
  }

  private var innerDetents: List<SheetDetent> by mutableStateOf(detents)

  var detents: List<SheetDetent>
    get() {
      return innerDetents
    }
    set(value) {
      checkValidDetents(value)
      innerDetents = value
      invalidateDetents()
    }

  internal var closestDetentToTopPx: Float by mutableStateOf(Float.NaN)
  internal var contentHeightPx: Float by mutableStateOf(Float.NaN)
  internal var containerHeightPx: Float by mutableStateOf(Float.NaN)

  val anchoredDraggableState = UnstyledAnchoredDraggableState(
    initialValue = initialDetent,
    positionalThreshold = positionalThreshold,
    velocityThreshold = velocityThreshold,
    snapAnimationSpec = animationSpec,
    decayAnimationSpec = decayAnimationSpec,
    confirmValueChange = confirmDetentChange,
  )

  val currentDetent: SheetDetent
    get() {
      return anchoredDraggableState.settledValue
    }

  private val derivedTargetDetent: SheetDetent by derivedStateOf {
    // If we have a drag target, use that
    if (anchoredDraggableState.dragTarget != null) {
      return@derivedStateOf anchoredDraggableState.dragTarget as SheetDetent
    }
    // Otherwise determine target based on current offset and direction
    val currentOffset = anchoredDraggableState.offset
    if (currentOffset.isNaN()) return@derivedStateOf currentDetent

    val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
    val isMovingUp = currentOffset < currentPosition

    return@derivedStateOf anchoredDraggableState.anchors.closestAnchor(currentOffset, !isMovingUp)
      ?: currentDetent
  }

  /**
   * The [SheetDetent] that the sheet is heading towards, in case of an animation or drag events.
   */
  var targetDetent: SheetDetent
    get() {
      return derivedTargetDetent
    }
    set(value) {
      check(innerDetents.contains(value)) {
        "Cannot set targetDetent to a detent (${value.identifier}) that is not part of the detents of the sheet's state. Current detents are: ${innerDetents.joinToString()}"
      }
      // do not start another animation if we are already heading to the requested detent
      // starting another animation, causes the sheet to pause for a split second, which is annoying for UX
      if (targetDetent == value) {
        return
      }
      coroutineScope.launch {
        anchoredDraggableState.animateTo(value)
      }
    }

  /**
   * Whether the sheet is currently rested at a detent.
   */
  val isIdle: Boolean by derivedStateOf {
    anchoredDraggableState.isDragging.not() && anchoredDraggableState.isAnimationRunning.not()
  }

  /**
   * A 0 to 1 value representing the progress of a sheet between its [from] and the [to] detents.
   */
  fun progress(from: SheetDetent, to: SheetDetent): Float {
    return anchoredDraggableState.progress(from, to)
  }

  /**
   * The amount the sheet has travelled from the bottom of its container in pixels.
   *
   */
  val offset: Float by derivedStateOf {
    if (anchoredDraggableState.offset.isNaN() || closestDetentToTopPx.isNaN()) {
      0f
    } else {
      val offsetFromTop = anchoredDraggableState.offset
      val diff = containerHeightPx - offsetFromTop
      diff.coerceAtLeast(0f)
    }
  }

  internal val visibleHeightPx: Float by derivedStateOf {
    when {
      containerHeightPx.isNaN() -> Float.NaN
      offset > 0f -> offset
      else -> containerHeightPx
    }
  }

  internal fun updateContentHeight(measuredHeightPx: Float) {
    if (contentHeightPx != measuredHeightPx) {
      contentHeightPx = measuredHeightPx
      invalidateDetents()
    }
  }

  /**
   * Animates the sheet to the given [SheetDetent]
   */
  suspend fun animateTo(value: SheetDetent) {
    check(innerDetents.contains(value)) {
      "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
    }
    anchoredDraggableState.animateTo(value)
  }

  /**
   * Instantly moves the sheet to the given [SheetDetent] without any animations.
   */
  fun jumpTo(value: SheetDetent) {
    check(innerDetents.contains(value)) {
      "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
    }
    coroutineScope.launch { anchoredDraggableState.snapTo(value) }
  }

  fun invalidateDetents() {
    val density = density()
    val containerHeight = with(density) { containerHeightPx.toDp() }
    val sheetHeight = with(density) { contentHeightPx.toDp() }

    // We are about to update detents, and we need to figure out the new target for the sheet
    // If anchors haven't been initialized yet (offset is NaN), always use currentDetent to establish the starting position
    // Otherwise, if we're idle, use currentDetent to maintain position
    // If we're animating/dragging, use targetDetent
    val newTarget = if (anchoredDraggableState.offset.isNaN()) {
      currentDetent
    } else if (isIdle) {
      currentDetent
    } else {
      targetDetent
    }

    // Capture the direction we were moving BEFORE updating anchors
    // We determine this by comparing the target position to the current detent position
    val wasMovingUp = if (!isIdle && newTarget != currentDetent) {
      val targetPosition = anchoredDraggableState.anchors.positionOf(newTarget)
      val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
      targetPosition < currentPosition
    } else {
      false
    }

    val anchors = UnstyledDraggableAnchors {
      with(density) {
        closestDetentToTopPx = Float.NaN

        innerDetents.forEach { detent ->
          val maxDetentHeight = minOf(containerHeight, sheetHeight)
          val contentHeight =
            detent.calculateDetentHeight(containerHeight, sheetHeight)
              .coerceIn(0.dp, maxDetentHeight)

          val offsetDp = containerHeight - contentHeight
          val offset = offsetDp.toPx()
          if (closestDetentToTopPx.isNaN() || closestDetentToTopPx > offset) {
            closestDetentToTopPx = offset
          }
          detent at offset
        }
      }
    }

    val overridden = if (innerDetents.contains(newTarget)) {
      newTarget
    } else {
      // Find the closest detent in the direction of movement
      val currentOffset = anchoredDraggableState.offset
      val closestDetent = anchors.closestDetent(currentOffset, searchUpwards = wasMovingUp)

      (closestDetent ?: innerDetents.first()).also {
        // the anchored draggable state does not update its target value
        // so we have to force it in order to update
        targetDetent = it
      }
    }

    anchoredDraggableState.updateAnchors(anchors, newTarget = overridden)
  }
}

/**
 * Finds the closest detent to the given offset in the specified direction.
 *
 * For bottom sheets:
 * - Moving up means we want smaller offset values (closer to top of screen)
 * - Moving down means we want larger offset values (closer to bottom of screen)
 *
 * @param offset The current offset position
 * @param searchUpwards Whether to search upwards (true) or downwards (false)
 * @return The closest detent in the specified direction, or null if none found
 */
private fun UnstyledDraggableAnchors<SheetDetent>.closestDetent(
  offset: Float,
  searchUpwards: Boolean,
): SheetDetent? {
  var closestDetent: SheetDetent? = null
  var closestDistance = Float.POSITIVE_INFINITY

  forEach { detent, position ->
    if (searchUpwards) {
      // Moving up: we want positions smaller than current offset
      if (position < offset) {
        val distance = offset - position
        if (distance < closestDistance) {
          closestDetent = detent
          closestDistance = distance
        }
      }
    } else {
      // Moving down: we want positions larger than current offset
      if (position > offset) {
        val distance = position - offset
        if (distance < closestDistance) {
          closestDetent = detent
          closestDistance = distance
        }
      }
    }
  }

  return closestDetent
}

private class BottomSheetContext(
  internal val state: BottomSheetState? = null,
  enabled: Boolean = true,
) {
  internal var enabled by mutableStateOf(enabled)
}

private val LocalBottomSheetContext: ProvidableCompositionLocal<BottomSheetContext> =
  compositionLocalOf { BottomSheetContext() }

/**
 * A foundational component used to build bottom sheets.
 *
 * For interactive preview & code examples, visit [Bottom Sheet Documentation](https://composeunstyled.com/bottomsheet).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val sheetState = rememberBottomSheetState(
 *     initialDetent = Hidden,
 * )
 *
 * Button(onClick = { sheetState.currentDetent = FullyExpanded }) {
 *     Text("Show Sheet")
 * }
 *
 * BottomSheet(
 *     state = sheetState,
 *     modifier = Modifier.fillMaxWidth(),
 * ) {
 *     Box(
 *         modifier = Modifier.fillMaxWidth().height(1200.dp),
 *         contentAlignment = Alignment.TopCenter
 *     ) {
 *         UnstyledDragIndication(Modifier.width(32.dp).height(4.dp))
 *     }
 * }
 * ```
 *
 * @param state The [BottomSheetState] that controls the sheet.
 * @param modifier Modifier to be applied to the sheet.
 * @param enabled Whether the sheet is enabled.
 * @param imeAware Automatically move the sheet according to the soft keyboard's height.
 * @param content The content of the sheet.
 */
@Composable
fun UnstyledBottomSheet(
  state: BottomSheetState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  imeAware: Boolean = false,
  content: @Composable () -> Unit,
) {
  val context = remember(state) { BottomSheetContext(state = state, enabled = enabled) }
  SideEffect { context.enabled = enabled }

  val coroutineScope = rememberCoroutineScope()

  BoxWithConstraints(
    modifier = Modifier.fillMaxSize().onSizeChanged {
      state.containerHeightPx = it.height.toFloat()
      state.invalidateDetents()
    },
    contentAlignment = Alignment.TopCenter,
  ) {
    CompositionLocalProvider(LocalBottomSheetContext provides context) {
      Box(
        modifier = buildModifier {
          add(Modifier.sheetOffset(state = state, imeAware = imeAware))
          if (context.enabled && state.detents.size > 1) {
            add(
              Modifier
                .unstyledAnchoredDraggable(
                  state = state.anchoredDraggableState,
                  orientation = Orientation.Vertical,
                  enabled = context.enabled,
                )
                .nestedScroll(
                  remember(state.anchoredDraggableState, Orientation.Vertical) {
                    ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                      orientation = Orientation.Vertical,
                      sheetState = state.anchoredDraggableState,
                      onFling = {
                        coroutineScope.launch { state.anchoredDraggableState.settle(it) }
                      },
                    )
                  },
                ),
            )
          }
          add(modifier.pointerInput(Unit) { detectTapGestures { } })
        },
        contentAlignment = Alignment.TopCenter,
      ) {
        content()
      }
    }
  }
}

@Composable
fun Sheet(
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentPadding: PaddingValues = PaddingValues(0.dp),
  content: @Composable () -> Unit,
) {
  val context = LocalBottomSheetContext.current
  val state = context.state
  val density = LocalDensity.current
  val visibleHeight = state?.visibleHeightPx?.let { height ->
    if (height.isNaN()) Dp.Unspecified else with(density) { height.toDp() }
  } ?: Dp.Unspecified

  Layout(
    modifier = modifier
      .heightIn(max = visibleHeight)
      .clip(shape)
      .background(backgroundColor)
      .padding(contentPadding),
    content = content,
  ) { measurables, constraints ->
    var contentHeight = 0
    val placeables = measurables.map { measurable ->
      contentHeight = max(contentHeight, measurable.maxIntrinsicHeight(constraints.maxWidth))

      measurable.measure(constraints).also { placeable ->
        contentHeight = max(contentHeight, placeable.height)
      }
    }

    val width = max(
      constraints.minWidth,
      placeables.maxOfOrNull { it.width } ?: 0,
    )
    val height = min(
      constraints.maxHeight,
      max(
        constraints.minHeight,
        placeables.maxOfOrNull { it.height } ?: 0,
      ),
    )

    state?.updateContentHeight(max(contentHeight, height).toFloat())

    layout(width, height) {
      placeables.forEach { placeable ->
        placeable.placeRelative(0, 0)
      }
    }
  }
}

@Composable
private fun Modifier.sheetOffset(state: BottomSheetState, imeAware: Boolean): Modifier {
  val density = LocalDensity.current
  val ime = WindowInsets.ime
  val imeHeight by remember {
    derivedStateOf {
      if (imeAware) ime.getBottom(density) else 0
    }
  }

  return this then buildModifier {
    add(
      Modifier.offset {
        when {
          state.containerHeightPx.isNaN() || state.containerHeightPx.isNaN() -> {
            // hasn't been initialized
            IntOffset(x = 0, y = 0)
          }

          state.anchoredDraggableState.offset.isNaN() -> {
            // draggable state is not ready
            // let the sheet take the height of the container
            val y = state.containerHeightPx.roundToInt()
            IntOffset(x = 0, y = y)
          }

          else -> {
            val calculatedOffset = state.anchoredDraggableState.requireOffset() - imeHeight
            // do not let the sheet's top go out of screen bounds
            val y = calculatedOffset.coerceAtLeast(0f).toInt()
            IntOffset(x = 0, y = y)
          }
        }
      },
    )
    if (imeAware) {
      add(Modifier.consumeWindowInsets(ime))
    }
  }
}

private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
  sheetState: UnstyledAnchoredDraggableState<SheetDetent>,
  orientation: Orientation,
  onFling: (velocity: Float) -> Unit,
): NestedScrollConnection = object : NestedScrollConnection {
  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    val delta = available.toFloat()
    return if (delta < 0 && source == NestedScrollSource.UserInput) {
      sheetState.dispatchRawDelta(delta).toOffset()
    } else {
      Offset.Zero
    }
  }

  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource,
  ): Offset {
    return if (source == NestedScrollSource.UserInput) {
      sheetState.dispatchRawDelta(available.toFloat()).toOffset()
    } else {
      Offset.Zero
    }
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    val toFling = available.toFloat()
    val currentOffset = sheetState.requireOffset()
    val minAnchor = sheetState.anchors.minAnchor()
    return if (toFling < 0 && currentOffset > minAnchor) {
      onFling(toFling)
      // since we go to the anchor with tween settling, consume all for the best UX
      available
    } else {
      Velocity.Zero
    }
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    onFling(available.toFloat())
    return available
  }

  private fun Float.toOffset(): Offset = Offset(
    x = if (orientation == Orientation.Horizontal) this else 0f,
    y = if (orientation == Orientation.Vertical) this else 0f,
  )

  @JvmName("velocityToFloat")
  private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

  @JvmName("offsetToFloat")
  private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

/**
 * A drag indication that can be used to control the bottom sheet.
 *
 * It is strongly advised to use this component within your [UnstyledBottomSheet]. Sheets are not by default accessible, and the [UnstyledDragIndication] allows toggling of the sheet via the keyboard.
 *
 * @param modifier Modifier to be applied to the drag indication.
 * @param indication The indication to be shown when the drag indication is interacted with.
 * @param interactionSource The interaction source for the drag indication.
 */
@Composable
fun UnstyledDragIndication(
  modifier: Modifier = Modifier,
  indication: Indication = LocalIndication.current,
  interactionSource: MutableInteractionSource? = null,
) {
  val context = LocalBottomSheetContext.current
  val state = context.state
  val enabled = context.enabled

  var detentIndex by rememberSaveable { mutableStateOf(-1) }
  var goUp by rememberSaveable { mutableStateOf(true) }

  val canExpand by remember {
    derivedStateOf {
      if (state == null) return@derivedStateOf false
      if (state.detents.size <= 1) return@derivedStateOf false

      val currentPosition = state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
      if (currentPosition.isNaN()) return@derivedStateOf false

      state.detents.any { detent ->
        val position = state.anchoredDraggableState.anchors.positionOf(detent)
        position.isNaN().not() && position < currentPosition
      }
    }
  }

  val canCollapse by remember {
    derivedStateOf {
      if (state == null) return@derivedStateOf false
      if (state.detents.size <= 1) return@derivedStateOf false

      val currentPosition = state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
      if (currentPosition.isNaN()) return@derivedStateOf false

      state.detents.any { detent ->
        val position = state.anchoredDraggableState.anchors.positionOf(detent)
        position.isNaN().not() && position > currentPosition
      }
    }
  }

  val canDismiss by remember {
    derivedStateOf {
      if (state == null) return@derivedStateOf false
      state.detents.contains(SheetDetent.Hidden) && state.currentDetent != SheetDetent.Hidden
    }
  }

  val coroutineScope = rememberCoroutineScope()

  val onIndicationClicked: () -> Unit = {
    if (state != null) {
      if (detentIndex == -1) {
        detentIndex = state.detents.indexOf(state.currentDetent)
      }
      if (detentIndex == state.detents.size - 1) goUp = false
      if (detentIndex == 0) goUp = true

      if (goUp) detentIndex++ else detentIndex--

      val detent = state.detents[detentIndex]
      state.targetDetent = detent
    }
  }

  Box(
    modifier = modifier
      .semantics(mergeDescendants = false) {
        if (state == null) return@semantics
        if (canExpand) {
          expand {
            // Find next detent with LOWER position value (more expanded = higher on screen)
            val currentPosition =
              state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
            val nextDetent = state.detents
              .filter { detent ->
                val pos = state.anchoredDraggableState.anchors.positionOf(detent)
                pos < currentPosition
              }
              .maxByOrNull { detent ->
                // Get the closest one (highest position that's still < current)
                state.anchoredDraggableState.anchors.positionOf(detent)
              }

            if (nextDetent != null) {
              coroutineScope.launch {
                state.animateTo(nextDetent)
              }
              true
            } else {
              false
            }
          }
        }
        if (canCollapse) {
          collapse {
            // Find next detent with HIGHER position value (less expanded = lower on screen)
            val currentPosition =
              state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
            val nextDetent = state.detents
              .filter { detent ->
                val pos = state.anchoredDraggableState.anchors.positionOf(detent)
                pos > currentPosition
              }
              .minByOrNull { detent ->
                // Get the closest one (lowest position that's still > current)
                state.anchoredDraggableState.anchors.positionOf(detent)
              }

            if (nextDetent != null) {
              coroutineScope.launch {
                state.animateTo(nextDetent)
              }
              true
            } else {
              false
            }
          }
        }
        if (canDismiss) {
          dismiss {
            coroutineScope.launch {
              state.animateTo(SheetDetent.Hidden)
            }
            true
          }
        }
      }
      .clickable(
        role = Role.Button,
        enabled = state != null && enabled && state.detents.size > 1,
        interactionSource = interactionSource,
        indication = indication,
        onClick = onIndicationClicked,
      ),
  )
}
