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
@file:Suppress("ktlint:standard:max-line-length", "ktlint:standard:no-wildcard-imports")

package com.composeunstyled

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.jvm.JvmName
import kotlin.math.abs
import kotlin.math.max
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
      density = localDensity(),
      animationSpec = animationSpec,
      decayAnimationSpec = decayAnimationSpec,
      velocityThreshold = {
        with(localDensity()) {
          velocityThreshold().toDp()
        }
      },
      positionalThreshold = { totalDistance ->
        with(localDensity()) {
          positionalThreshold(totalDistance.toPx()).toDp()
        }
      },
      confirmDetentChange = confirmDetentChange,
    )
  })

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
      density = density,
      animationSpec = animationSpec,
      decayAnimationSpec = decayAnimationSpec,
      velocityThreshold = velocityThreshold,
      positionalThreshold = positionalThreshold,
      confirmDetentChange = confirmDetentChange,
    )
  }
}

@Immutable
class SheetDetent(
  val identifier: String,
  val calculateDetentHeight: (containerHeight: Dp, sheetHeight: Dp) -> Dp,
) {
  companion object {
    val FullyExpanded: SheetDetent =
      SheetDetent("fully-expanded") { containerHeight, sheetHeight -> sheetHeight }

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

class BottomSheetState internal constructor(
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
  constructor(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    coroutineScope: CoroutineScope,
    density: Density,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    animationSpec: AnimationSpec<Float> = tween(),
    velocityThreshold: () -> Dp = { 125.dp },
    positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
    confirmDetentChange: (SheetDetent) -> Boolean = { true },
  ) : this(
    initialDetent = initialDetent,
    detents = detents,
    coroutineScope = coroutineScope,
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
  private var contentDependentDetents by mutableStateOf(false)
  private var sheetHeightDependentDetents: Set<SheetDetent> by mutableStateOf(emptySet())
  private var sheetHeightDependencyContainerHeightPx = Float.NaN
  private var sheetHeightDependencyDetents: List<SheetDetent> = emptyList()
  private var measuredContentHeightPx = Float.NaN
  private var measuredSheetHeightPx: Float by mutableStateOf(Float.NaN)
  internal var maxDetentHeightPx: Float by mutableStateOf(Float.NaN)
  internal var isDragging: Boolean by mutableStateOf(false)

  internal val anchoredDraggableState = AnchoredDraggableState(
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
    anchoredDraggableState.targetValue
  }

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
        animateTo(value)
      }
    }

  val isIdle: Boolean by derivedStateOf {
    val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
    val currentOffset = anchoredDraggableState.offset
    val isSettledAtCurrentDetent =
      currentOffset.isNaN() || currentPosition.isNaN() || abs(currentOffset - currentPosition) < 0.5f

    isDragging.not() && anchoredDraggableState.isAnimationRunning.not() && isSettledAtCurrentDetent
  }

  fun progress(from: SheetDetent, to: SheetDetent): Float {
    return anchoredDraggableState.progress(from, to)
  }

  val offset: Float by derivedStateOf {
    if (anchoredDraggableState.offset.isNaN() || closestDetentToTopPx.isNaN()) {
      0f
    } else {
      val offsetFromTop = anchoredDraggableState.offset
      val diff = containerHeightPx - offsetFromTop
      diff.coerceAtLeast(0f)
    }
  }

  internal val layoutHeightPx: Float by derivedStateOf {
    if (containerHeightPx.isNaN()) {
      Float.NaN
    } else {
      val currentHeight = anchoredHeightFor(currentDetent)
      val targetHeight = if (
        targetDetent != currentDetent &&
        anchoredDraggableState.offset.isNaN().not()
      ) {
        anchoredHeightFor(targetDetent)
      } else {
        Float.NaN
      }
      val offsetHeight = currentOffsetHeight()

      maxOrFallback(
        currentHeight,
        targetHeight,
        offsetHeight,
        fallback = maxDetentHeightPx,
      )
    }
  }

  internal fun layoutHeightPxFor(measuredContentHeightPx: Float): Float {
    if (containerHeightPx.isNaN() || measuredContentHeightPx.isNaN()) return Float.NaN

    val currentHeight = detentHeightPx(currentDetent, measuredContentHeightPx)
    val targetHeight = if (
      targetDetent != currentDetent &&
      anchoredDraggableState.offset.isNaN().not()
    ) {
      detentHeightPx(targetDetent, measuredContentHeightPx)
    } else {
      Float.NaN
    }
    val offsetHeight = currentOffsetHeight()

    return maxOrFallback(
      currentHeight,
      targetHeight,
      offsetHeight,
      fallback = Float.NaN,
    )
  }

  internal fun estimatedContentHeightPx(fallbackHeightPx: Float): Float {
    return when {
      contentHeightPx.isNaN().not() -> contentHeightPx
      else -> fallbackHeightPx
    }
  }

  internal fun hasContentDependentDetents(): Boolean {
    return contentDependentDetents
  }

  internal fun shouldUseContentHeightForLayout(measuredContentHeightPx: Float): Boolean {
    val contentHeightIsChanging = this.measuredContentHeightPx.isNaN().not() &&
      this.measuredContentHeightPx.isSameValueAs(measuredContentHeightPx).not()
    val offsetHeight = currentOffsetHeight()
    if (
      contentHeightIsChanging &&
      offsetHeight.isNaN().not() &&
      (isDragging || isIdle.not()) &&
      offsetHeight > measuredContentHeightPx
    ) {
      return false
    }

    val isMovingToDifferentDetent = targetDetent != currentDetent &&
      anchoredDraggableState.offset.isNaN().not()

    return if (isMovingToDifferentDetent) {
      (
        currentDetent.dependsOnSheetHeight() &&
          (targetDetent.dependsOnSheetHeight() || targetDetent == SheetDetent.Hidden)
        ) ||
        (targetDetent.dependsOnSheetHeight() && currentDetent == SheetDetent.Hidden)
    } else {
      currentDetent.dependsOnSheetHeight()
    }
  }

  private fun anchoredHeightFor(detent: SheetDetent): Float {
    val position = anchoredDraggableState.anchors.positionOf(detent)
    return if (position.isNaN()) {
      Float.NaN
    } else {
      (containerHeightPx - position).coerceAtLeast(0f)
    }
  }

  private fun currentOffsetHeight(): Float {
    val offset = anchoredDraggableState.offset
    return if (containerHeightPx.isNaN() || offset.isNaN()) {
      Float.NaN
    } else {
      (containerHeightPx - offset).coerceAtLeast(0f)
    }
  }

  internal fun bottomAlignedOffsetPx(): Float {
    return if (containerHeightPx.isNaN() || measuredSheetHeightPx.isNaN()) {
      Float.NaN
    } else {
      (containerHeightPx - measuredSheetHeightPx).coerceAtLeast(0f)
    }
  }

  private fun detentHeightPx(
    detent: SheetDetent,
    sheetHeightPx: Float,
    dependsOnSheetHeight: Boolean = detent.dependsOnSheetHeight(),
  ): Float {
    if (containerHeightPx.isNaN()) return Float.NaN

    val rawHeight = rawDetentHeightPx(detent, sheetHeightPx)
    if (dependsOnSheetHeight) {
      return rawHeight.coerceAtMost(sheetHeightPx.coerceAtLeast(0f))
    }
    if (detent != SheetDetent.FullyExpanded) return rawHeight

    val maxDetentHeight = innerDetents
      .asSequence()
      .filter { it != SheetDetent.FullyExpanded }
      .map { rawDetentHeightPx(it, sheetHeightPx) }
      .filter { it.isNaN().not() }
      .maxOrNull()

    return if (maxDetentHeight != null && maxDetentHeight > rawHeight) {
      containerHeightPx
    } else {
      rawHeight
    }
  }

  private fun rawDetentHeightPx(detent: SheetDetent, sheetHeightPx: Float): Float {
    val density = density()
    val containerHeight = with(density) { containerHeightPx.toDp() }
    val sheetHeight = with(density) { sheetHeightPx.toDp() }

    return with(density) {
      detent.calculateDetentHeight(containerHeight, sheetHeight)
        .coerceIn(0.dp, containerHeight)
        .toPx()
    }
  }

  private fun SheetDetent.dependsOnSheetHeight(): Boolean {
    return sheetHeightDependentDetents.contains(this)
  }

  private fun SheetDetent.calculateDependsOnSheetHeight(): Boolean {
    if (containerHeightPx.isNaN()) return false

    val heightAtEmptySheet = rawDetentHeightPx(this, sheetHeightPx = 0f)
    val heightAtFullSheet = rawDetentHeightPx(this, sheetHeightPx = containerHeightPx)
    return heightAtEmptySheet != heightAtFullSheet
  }

  private fun updateSheetHeightDependentDetents() {
    if (containerHeightPx.isNaN()) {
      sheetHeightDependencyContainerHeightPx = Float.NaN
      sheetHeightDependencyDetents = emptyList()
      if (sheetHeightDependentDetents.isNotEmpty()) {
        sheetHeightDependentDetents = emptySet()
      }
      return
    }

    if (
      sheetHeightDependencyContainerHeightPx.isSameValueAs(containerHeightPx) &&
      sheetHeightDependencyDetents == innerDetents
    ) {
      return
    }

    val nextSheetHeightDependentDetents = innerDetents
      .filterTo(mutableSetOf()) { detent -> detent.calculateDependsOnSheetHeight() }

    sheetHeightDependencyContainerHeightPx = containerHeightPx
    sheetHeightDependencyDetents = innerDetents
    if (sheetHeightDependentDetents != nextSheetHeightDependentDetents) {
      sheetHeightDependentDetents = nextSheetHeightDependentDetents
    }
  }

  internal fun updateContainerHeight(measuredHeightPx: Float) {
    if (containerHeightPx.isSameValueAs(measuredHeightPx)) return

    containerHeightPx = measuredHeightPx
    invalidateDetents()
  }

  internal fun updateMeasuredSheetHeight(measuredHeightPx: Float) {
    if (measuredSheetHeightPx.isSameValueAs(measuredHeightPx)) return

    measuredSheetHeightPx = measuredHeightPx
  }

  internal fun updateContentHeight(measuredHeightPx: Float, includeMeasuredSheetHeight: Boolean) {
    val measuredContentHeightShrank = measuredContentHeightPx.isNaN().not() &&
      measuredHeightPx < measuredContentHeightPx
    measuredContentHeightPx = measuredHeightPx
    val shouldIncludeMeasuredSheetHeight = includeMeasuredSheetHeight ||
      (measuredContentHeightShrank.not() && measuredSheetHeightPx < containerHeightPx)
    val measuredSheetHeight = if (shouldIncludeMeasuredSheetHeight) {
      measuredSheetHeightPx.takeUnless { it.isNaN() } ?: 0f
    } else {
      0f
    }
    val resolvedMeasuredHeightPx = maxOf(measuredHeightPx, measuredSheetHeight)
    if (contentHeightPx.isSameValueAs(resolvedMeasuredHeightPx)) return

    contentHeightPx = resolvedMeasuredHeightPx
    invalidateDetents()
  }

  suspend fun animateTo(value: SheetDetent, animationSpec: AnimationSpec<Float>? = null) {
    check(innerDetents.contains(value)) {
      "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
    }
    if (currentDetent == value && targetDetent == value && isIdle) {
      return
    }
    awaitAnchors()
    if (animationSpec == null) {
      anchoredDraggableState.animateTo(value)
    } else {
      anchoredDraggableState.animateTo(value, animationSpec)
    }
  }

  fun jumpTo(value: SheetDetent) {
    check(innerDetents.contains(value)) {
      "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
    }
    if (currentDetent == value && targetDetent == value) {
      return
    }
    coroutineScope.launch {
      awaitAnchors()
      anchoredDraggableState.snapTo(value)
    }
  }

  private suspend fun awaitAnchors() {
    if (anchoredDraggableState.offset.isNaN().not()) return

    snapshotFlow { anchoredDraggableState.offset.isNaN().not() }.first { it }
  }

  fun invalidateDetents() {
    val density = density()
    val containerHeight = with(density) { containerHeightPx.toDp() }
    val hasMeasuredContainer = containerHeightPx.isNaN().not()
    updateSheetHeightDependentDetents()
    var nextClosestDetentToTopPx = Float.NaN
    var nextMaxDetentHeightPx = Float.NaN
    var nextContentDependentDetents = false

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
    val wasMovingUp = if (isIdle.not() && newTarget != currentDetent) {
      val targetPosition = anchoredDraggableState.anchors.positionOf(newTarget)
      val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
      targetPosition < currentPosition
    } else {
      false
    }

    val anchors = DraggableAnchors {
      with(density) {
        innerDetents.forEach { detent ->
          val dependsOnSheetHeight = detent.dependsOnSheetHeight()
          val detentHeightPx = detentHeightPx(
            detent = detent,
            sheetHeightPx = contentHeightPx,
            dependsOnSheetHeight = dependsOnSheetHeight,
          )
          val detentHeight = detentHeightPx.toDp()

          val offsetDp = containerHeight - detentHeight
          val offset = offsetDp.toPx()
          if (nextClosestDetentToTopPx.isNaN() || nextClosestDetentToTopPx > offset) {
            nextClosestDetentToTopPx = offset
          }
          if (nextMaxDetentHeightPx.isNaN() || nextMaxDetentHeightPx < detentHeightPx) {
            nextMaxDetentHeightPx = detentHeightPx
          }
          if (hasMeasuredContainer && nextContentDependentDetents.not()) {
            nextContentDependentDetents = dependsOnSheetHeight
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

    if (closestDetentToTopPx.isSameValueAs(nextClosestDetentToTopPx).not()) {
      closestDetentToTopPx = nextClosestDetentToTopPx
    }
    if (maxDetentHeightPx.isSameValueAs(nextMaxDetentHeightPx).not()) {
      maxDetentHeightPx = nextMaxDetentHeightPx
    }
    if (contentDependentDetents != nextContentDependentDetents) {
      contentDependentDetents = nextContentDependentDetents
    }

    anchoredDraggableState.updateAnchors(anchors, newTarget = overridden)
  }
}

private fun maxOrFallback(
  first: Float,
  second: Float,
  third: Float,
  fallback: Float,
): Float {
  var result = Float.NaN
  if (first.isNaN().not()) result = first
  if (second.isNaN().not() && (result.isNaN() || second > result)) result = second
  if (third.isNaN().not() && (result.isNaN() || third > result)) result = third
  return if (result.isNaN()) fallback else result
}

private fun Float.isSameValueAs(other: Float): Boolean {
  return this == other || (isNaN() && other.isNaN())
}

private fun DraggableAnchors<SheetDetent>.closestDetent(
  offset: Float,
  searchUpwards: Boolean,
): SheetDetent? {
  var closestDetent: SheetDetent? = null
  var closestDistance = Float.POSITIVE_INFINITY

  for (index in 0 until size) {
    val detent = anchorAt(index) ?: continue
    val position = positionAt(index)
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

interface BottomSheetScope

private object BottomSheetScopeInstance : BottomSheetScope

@Composable
fun UnstyledBottomSheet(
  state: BottomSheetState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  offsetForIme: Boolean = false,
  content: @Composable BottomSheetScope.() -> Unit,
) {
  val context = remember(state) { BottomSheetContext(state = state, enabled = enabled) }
  SideEffect { context.enabled = enabled }

  val coroutineScope = rememberCoroutineScope()
  val dragInteractionSource = remember { MutableInteractionSource() }
  LaunchedEffect(dragInteractionSource) {
    dragInteractionSource.interactions.collect { interaction ->
      when (interaction) {
        is DragInteraction.Start -> state.isDragging = true
        is DragInteraction.Stop,
        is DragInteraction.Cancel,
        -> state.isDragging = false
      }
    }
  }
  Box(
    modifier = modifier.onSizeChanged { measuredSize ->
      state.updateContainerHeight(measuredSize.height.toFloat())
    },
  ) {
    CompositionLocalProvider(LocalBottomSheetContext provides context) {
      Box(
        modifier = Modifier
          .onSizeChanged { measuredSize ->
            if (measuredSize.height > 0) {
              state.updateMeasuredSheetHeight(measuredSize.height.toFloat())
            }
          }
          .sheetOffset(state = state, offsetForIme = offsetForIme)
          .then(
            buildModifier {
              if (context.enabled && state.detents.size > 1) {
                add(
                  Modifier
                    .anchoredDraggable(
                      state = state.anchoredDraggableState,
                      orientation = Orientation.Vertical,
                      enabled = context.enabled,
                      interactionSource = dragInteractionSource,
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
            },
          )
          .pointerInput(Unit) { detectTapGestures { } },
      ) {
        BottomSheetScopeInstance.content()
      }
    }
  }
}

@Composable
fun BottomSheetScope.Sheet(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val context = LocalBottomSheetContext.current
  val state = context.state

  Layout(
    modifier = modifier.clipToBounds(),
    content = content,
  ) { measurables, constraints ->
    val fallbackContentHeight = when {
      state == null -> constraints.maxHeight
      state.hasContentDependentDetents() -> state.containerHeightPx.roundToInt()
      state.layoutHeightPx.isNaN() -> constraints.maxHeight
      else -> state.layoutHeightPx.roundToInt()
    }
    val constrainedFallbackContentHeight = fallbackContentHeight.coerceIn(
      constraints.minHeight,
      constraints.maxHeight,
    )
    val contentMeasurementHeight = when {
      state != null && state.containerHeightPx.isNaN().not() -> state.containerHeightPx.roundToInt()
      else -> constraints.maxHeight
    }.coerceIn(
      constraints.minHeight,
      constraints.maxHeight,
    )
    val estimatedContentHeight = state
      ?.estimatedContentHeightPx(constrainedFallbackContentHeight.toFloat())
      ?.roundToInt()
      ?.coerceIn(constraints.minHeight, constraints.maxHeight)
      ?: constrainedFallbackContentHeight

    val resolvedLayoutHeight = state?.layoutHeightPxFor(estimatedContentHeight.toFloat())
      ?: estimatedContentHeight.toFloat()
    val waitingForHiddenAnchors = state != null &&
      state.anchoredDraggableState.offset.isNaN() &&
      state.currentDetent == SheetDetent.Hidden &&
      state.targetDetent == SheetDetent.Hidden
    val layoutMaxHeight = when {
      waitingForHiddenAnchors -> constraints.minHeight
      resolvedLayoutHeight.isNaN() -> constraints.maxHeight
      else -> resolvedLayoutHeight.roundToInt()
    }.coerceIn(constraints.minHeight, constraints.maxHeight)
    val contentMaxHeight = when {
      state == null -> contentMeasurementHeight
      state.contentHeightPx.isNaN() -> contentMeasurementHeight
      state.hasContentDependentDetents() -> contentMeasurementHeight
      waitingForHiddenAnchors -> contentMeasurementHeight
      else -> layoutMaxHeight
    }
    val contentConstraints = constraints.copy(maxHeight = contentMaxHeight)

    val placeables = measurables.map { measurable ->
      measurable.measure(contentConstraints)
    }

    val width = max(
      constraints.minWidth,
      placeables.maxOfOrNull { it.width } ?: 0,
    )
    val contentHeight = placeables.maxOfOrNull { it.height } ?: 0
    val height = when {
      state == null -> contentHeight
      state.shouldUseContentHeightForLayout(
        contentHeight.toFloat(),
      ) -> minOf(contentHeight, layoutMaxHeight)
      constraints.hasBoundedHeight.not() && resolvedLayoutHeight.isNaN() -> contentHeight
      else -> layoutMaxHeight
    }.coerceIn(constraints.minHeight, constraints.maxHeight)

    val measuredContentHeight = state?.contentHeightPx
      ?.takeIf { previousHeight ->
        contentHeight == contentMaxHeight &&
          contentMaxHeight < contentMeasurementHeight &&
          previousHeight > contentHeight
      }
      ?: contentHeight.toFloat()
    state?.updateContentHeight(
      measuredHeightPx = max(measuredContentHeight, height.toFloat()),
      includeMeasuredSheetHeight = contentHeight == contentMaxHeight,
    )

    layout(width, height) {
      placeables.forEach { placeable ->
        placeable.placeRelative(0, 0)
      }
    }
  }
}

@Composable
private fun Modifier.sheetOffset(state: BottomSheetState, offsetForIme: Boolean): Modifier {
  val density = LocalDensity.current
  val ime = WindowInsets.ime
  val imeHeight by remember {
    derivedStateOf {
      if (offsetForIme) ime.getBottom(density) else 0
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
            val visualOffset = state.anchoredDraggableState.visualOffset()
            val calculatedOffset = maxOrFallback(
              visualOffset,
              state.bottomAlignedOffsetPx(),
              Float.NaN,
              fallback = visualOffset,
            ) - imeHeight
            // do not let the sheet's top go out of screen bounds
            val y = calculatedOffset.coerceAtLeast(0f).toInt()
            IntOffset(x = 0, y = y)
          }
        }
      },
    )
    if (offsetForIme) {
      add(Modifier.consumeWindowInsets(ime))
    }
  }
}

private fun AnchoredDraggableState<SheetDetent>.visualOffset(): Float {
  val offset = requireOffset()
  val minPosition = anchors.minPosition()
  val maxPosition = anchors.maxPosition()
  if (minPosition.isNaN() || maxPosition.isNaN()) return offset
  return offset.coerceIn(minPosition, maxPosition)
}

private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
  sheetState: AnchoredDraggableState<SheetDetent>,
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
    val minAnchor = sheetState.anchors.minPosition()
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

@Composable
fun BottomSheetScope.DragIndication(
  modifier: Modifier = Modifier,
  indication: Indication? = null,
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
