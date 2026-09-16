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
package com.composeunstyled

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.roundToInt

class UnstyledDrawerState<T : Any>(
  initialValue: T,
  snapPoints: DrawerSnapPoints<T>,
  animationSpec: AnimationSpec<Float> = tween(),
  dismissAnimationSpec: AnimationSpec<Float> = animationSpec,
  confirmValueChange: (DrawerValueChange<T>) -> Boolean = { true },
) {
  init {
    check(snapPoints.contains(initialValue)) {
      "initialValue must be present in snapPoints."
    }
  }

  private var requestVersion by mutableIntStateOf(0)
  internal var pendingTarget: PendingTarget<T>? by mutableStateOf(null)
    private set
  private var innerTargetValue: T by mutableStateOf(initialValue)
  private var confirmingValueChange = false
  private var dismissedReported by mutableStateOf(initialValue == snapPoints.zeroValue)
  private var lastAcceptedValueChange by mutableStateOf<DrawerValueChange<T>?>(null)

  internal var viewportSizePx by mutableStateOf(Float.NaN)
  internal var contentSizePx by mutableStateOf(Float.NaN)
  internal var anchoredToMinEdge by mutableStateOf(false)
  private var hiddenEdgeInsetPx by mutableStateOf(0f)
  internal var isDragging by mutableStateOf(false)
  internal var isEdgeSwipeInProgress by mutableStateOf(false)
  private var predictiveBackActive by mutableStateOf(false)
  private var animationScope: CoroutineScope? = null
  internal var density: Density by mutableStateOf(Density(1f))

  private var innerSnapPoints by mutableStateOf(snapPoints)

  internal val anchoredDraggableState = AnchoredDraggableState(initialValue)

  val snapPoints: DrawerSnapPoints<T>
    get() = innerSnapPoints

  val currentValue: T
    get() = anchoredDraggableState.settledValue

  var targetValue: T
    get() = if (isDragging) {
      anchoredDraggableState.targetValue
    } else {
      innerTargetValue
    }
    set(value) {
      requestTarget(
        value = value,
        reason = DrawerValueChange.Reason.Programmatic,
        shouldAnimate = true,
      )
    }

  var animationSpec: AnimationSpec<Float> by mutableStateOf(animationSpec)

  var dismissAnimationSpec: AnimationSpec<Float> by mutableStateOf(dismissAnimationSpec)

  var confirmValueChange: (DrawerValueChange<T>) -> Boolean by mutableStateOf(confirmValueChange)

  val isIdle: Boolean by derivedStateOf {
    val currentPosition = anchoredDraggableState.anchors.positionOf(
      anchoredDraggableState.settledValue,
    )
    val currentOffset = anchoredDraggableState.offset
    val settled =
      currentOffset.isNaN() ||
        currentPosition.isNaN() ||
        abs(currentOffset - currentPosition) < 0.5f

    pendingTarget == null &&
      isDragging.not() &&
      isEdgeSwipeInProgress.not() &&
      predictiveBackActive.not() &&
      anchoredDraggableState.isAnimationRunning.not() &&
      settled
  }

  val offset: Float by derivedStateOf {
    visiblePanelSizePx()
  }

  fun progress(from: T, to: T): Float {
    validateValue(from)
    validateValue(to)
    if (anchoredDraggableState.offset.isNaN()) return if (from == to) 1f else 0f
    val fromAnchor = anchorFor(from)
    val toAnchor = anchorFor(to)
    if (abs(toAnchor - fromAnchor) < 0.5f) return 1f
    val progress = ((anchoredDraggableState.offset - fromAnchor) / (toAnchor - fromAnchor))
      .coerceIn(0f, 1f)
    return if (progress == 0f) 0f else progress
  }

  suspend fun animateTo(
    value: T,
    animationSpec: AnimationSpec<Float>? = null,
  ) {
    if (
      requestTarget(
        value = value,
        reason = DrawerValueChange.Reason.Programmatic,
        animationSpec = animationSpec,
      ).not()
    ) {
      return
    }
    val version = requestVersion
    try {
      snapshotFlow {
        requestVersion != version ||
          (isIdle && currentValue == value && targetValue == value)
      }.first { it }
    } catch (cancellationException: CancellationException) {
      if (requestVersion == version) {
        val nearest = anchoredDraggableState.offset
          .takeIf { offset -> offset.isNaN().not() }
          ?.let(::closestValueTo)
          ?: currentValue
        requestTarget(
          value = nearest,
          reason = DrawerValueChange.Reason.Programmatic,
          shouldAnimate = false,
          alreadyConfirmed = true,
          force = true,
        )
      }
      throw cancellationException
    }
  }

  fun jumpTo(value: T) {
    requestTarget(
      value = value,
      reason = DrawerValueChange.Reason.Programmatic,
      shouldAnimate = false,
    )
  }

  fun updateSnapPoints(snapPoints: DrawerSnapPoints<T>) {
    if (snapPoints == innerSnapPoints) return

    val previousOffset = anchoredDraggableState.offset
    innerSnapPoints = snapPoints
    val currentSupported = snapPoints.contains(currentValue)
    val targetSupported = snapPoints.contains(targetValue)
    val resolvedTarget = when {
      targetSupported -> targetValue
      previousOffset.isNaN().not() -> {
        closestValueTo(previousOffset, anchors = createAnchors())
          ?: snapPoints.entries.first().value
      }
      currentSupported -> currentValue
      else -> snapPoints.entries.first().value
    }

    if (currentSupported.not() || targetSupported.not()) {
      innerTargetValue = resolvedTarget
      enqueueTarget(resolvedTarget, shouldAnimate = true, animationSpecFor(resolvedTarget))
      if (previousOffset.isNaN().not()) {
        animationScope?.launch(start = CoroutineStart.UNDISPATCHED) {
          anchoredDraggableState.anchoredDrag(resolvedTarget) { _, _ ->
            awaitCancellation()
          }
        }
      }
    }
    updateAnchors(newTarget = resolvedTarget)
  }

  fun invalidateSnapPoints() {
    val preservedProgress = captureGeometryProgress()
    updateAnchors(preservedProgress = preservedProgress)
  }

  internal suspend fun settlePendingTarget(target: PendingTarget<T>) {
    if (requestVersion != target.version) return
    awaitAnchors()
    if (target.shouldAnimate) {
      anchoredDraggableState.animateTo(target.value, target.animationSpec)
    } else {
      anchoredDraggableState.snapTo(target.value)
    }
    clearPendingTarget(target.version)
  }

  internal fun requestTarget(
    value: T,
    reason: DrawerValueChange.Reason,
    shouldAnimate: Boolean = true,
    alreadyConfirmed: Boolean = false,
    force: Boolean = false,
    animationSpec: AnimationSpec<Float>? = null,
  ): Boolean {
    validateValue(value)
    val isLogicalNoOp = value == targetValue
    if (force.not() && isLogicalNoOp) return false
    val requiresConfirmation = when (reason) {
      DrawerValueChange.Reason.Gesture -> value != innerTargetValue
      else -> isLogicalNoOp.not()
    }
    if (
      alreadyConfirmed.not() &&
      requiresConfirmation &&
      confirmTargetValue(value, reason).not()
    ) {
      return false
    }
    innerTargetValue = value
    enqueueTarget(value, shouldAnimate, animationSpec ?: animationSpecFor(value))
    return true
  }

  internal fun settleToClosestValue(
    reason: DrawerValueChange.Reason,
    direction: Float = 0f,
  ) {
    val offset = anchoredDraggableState.offset
    if (offset.isNaN()) return

    val target = closestValueTo(offset, direction) ?: return
    settleTo(target, reason)
  }

  internal fun settleFromFling(velocity: Float) {
    if (abs(velocity) < with(density) { 125.dp.toPx() }) {
      settleToClosestValue(
        reason = DrawerValueChange.Reason.Gesture,
        direction = velocity,
      )
      return
    }
    val currentOffset = anchoredDraggableState.offset
    if (currentOffset.isNaN()) return
    val anchors = innerSnapPoints.entries
      .map { entry -> entry.value to anchorFor(entry.value) }
      .distinctBy { (_, anchor) -> anchor.roundToInt() }
      .sortedBy { (_, anchor) -> anchor }
    val target = if (velocity > 0f) {
      anchors.firstOrNull { (_, anchor) -> anchor > currentOffset + 0.5f }
    } else {
      anchors.lastOrNull { (_, anchor) -> anchor < currentOffset - 0.5f }
    }?.first
    if (target == null) {
      settleToClosestValue(
        reason = DrawerValueChange.Reason.Gesture,
        direction = velocity,
      )
    } else {
      settleTo(target, DrawerValueChange.Reason.Gesture)
    }
  }

  private fun settleTo(
    value: T,
    reason: DrawerValueChange.Reason,
  ) {
    val valueBeforeGesture = innerTargetValue
    if (
      requestTarget(
        value = value,
        reason = reason,
        shouldAnimate = true,
        force = true,
      ).not()
    ) {
      requestTarget(
        value = valueBeforeGesture,
        reason = reason,
        alreadyConfirmed = true,
        force = true,
      )
    }
  }

  internal fun startPredictiveBack(): Boolean {
    val zeroValue = zeroValue ?: return false
    if (predictiveBackActive) return true
    if (
      anchoredDraggableState.offset.isNaN() ||
      currentValue == zeroValue ||
      targetValue == zeroValue
    ) {
      return true
    }
    if (confirmTargetValue(zeroValue, DrawerValueChange.Reason.NavigateBack).not()) return true

    predictiveBackActive = true
    return true
  }

  internal fun progressPredictiveBack(progress: Float) {
    if (predictiveBackActive.not()) return
    val zeroValue = zeroValue ?: return
    val currentOffset = anchoredDraggableState.offset
    val zeroOffset = anchorFor(zeroValue)
    val startOffset = anchorFor(currentValue)
    val desiredOffset = startOffset + (zeroOffset - startOffset) * progress.coerceIn(0f, 1f)
    anchoredDraggableState.dispatchRawDelta(desiredOffset - currentOffset)
  }

  internal fun cancelPredictiveBack() {
    if (predictiveBackActive.not()) return
    predictiveBackActive = false
    requestTarget(
      value = currentValue,
      reason = DrawerValueChange.Reason.NavigateBack,
      alreadyConfirmed = true,
      force = true,
    )
  }

  internal fun invokePredictiveBack() {
    if (predictiveBackActive.not()) return
    predictiveBackActive = false
    val zeroValue = zeroValue ?: return
    requestTarget(
      value = zeroValue,
      reason = DrawerValueChange.Reason.NavigateBack,
      alreadyConfirmed = true,
    )
  }

  internal fun updateViewportSize(
    measuredSizePx: Float,
    anchoredToMinEdge: Boolean,
    hiddenEdgeInsetPx: Float,
    density: Density,
  ) {
    if (
      viewportSizePx == measuredSizePx &&
      this.anchoredToMinEdge == anchoredToMinEdge &&
      this.hiddenEdgeInsetPx == hiddenEdgeInsetPx &&
      this.density.density == density.density &&
      this.density.fontScale == density.fontScale
    ) {
      return
    }

    val preservedProgress = captureGeometryProgress()
    viewportSizePx = measuredSizePx
    this.anchoredToMinEdge = anchoredToMinEdge
    this.hiddenEdgeInsetPx = hiddenEdgeInsetPx
    this.density = Density(density.density, density.fontScale)
    updateAnchors(preservedProgress = preservedProgress)
  }

  internal fun attachAnimationScope(scope: CoroutineScope?) {
    animationScope = scope
  }

  internal fun updateContentSize(measuredSizePx: Float) {
    if (contentSizePx == measuredSizePx) return

    val preservedProgress = captureGeometryProgress()
    contentSizePx = measuredSizePx
    updateAnchors(preservedProgress = preservedProgress)
  }

  internal fun visiblePanelSizePx(): Float {
    if (viewportSizePx.isNaN()) return 0f
    if (anchoredDraggableState.offset.isNaN()) return 0f

    val visiblePanelSizePx = if (anchoredToMinEdge) {
      anchoredDraggableState.offset
    } else {
      viewportSizePx - anchoredDraggableState.offset
    }
    return visiblePanelSizePx.coerceIn(0f, viewportSizePx)
  }

  internal fun panelMainAxisOffsetPx(
    resolvedPlacement: ResolvedDrawerPlacement,
    viewportMainAxisSizePx: Float,
  ): Float {
    val anchoredOffset = anchoredDraggableState.offset
    if (anchoredOffset.isNaN()) {
      return if (resolvedPlacement.isMinEdge) {
        -contentSizePx
      } else {
        viewportMainAxisSizePx
      }
    }

    return if (resolvedPlacement.isMinEdge) {
      anchoredOffset - contentSizePx
    } else {
      anchoredOffset
    }
  }

  internal val zeroValue: T?
    get() = innerSnapPoints.zeroValue

  internal val hasZeroValue: Boolean
    get() = zeroValue != null

  internal val isAtZeroValue: Boolean
    get() {
      val zeroValue = zeroValue ?: return false
      return currentValue == zeroValue && targetValue == zeroValue
    }

  internal val isPanelHidden: Boolean
    get() = hasZeroValue && isDragging.not() && isAtZeroAnchor()

  internal val hasVisiblePanel: Boolean
    get() = offset.isNaN().not() && offset > 0.5f

  internal fun hasMultipleValues(): Boolean {
    return innerSnapPoints.entries.size > 1
  }

  internal fun nextMoreVisibleValue(): T? {
    return nextValueByVisibility(moreVisible = true)
  }

  internal fun nextLessVisibleValue(): T? {
    return nextValueByVisibility(moreVisible = false)
  }

  internal fun markDismissed(): Boolean {
    if (isAtZeroValue.not()) {
      dismissedReported = false
      return false
    }
    if (dismissedReported) return false

    dismissedReported = true
    return true
  }

  internal fun lastDismissedChange(): DrawerValueChange<T> {
    val dismissedValue = zeroValue ?: currentValue
    return lastAcceptedValueChange
      ?.takeIf { change -> change.targetValue == dismissedValue }
      ?: DrawerValueChange(
        initialValue = currentValue,
        targetValue = dismissedValue,
        reason = DrawerValueChange.Reason.Programmatic,
      )
  }

  private fun clearPendingTarget(version: Int) {
    if (requestVersion == version) {
      pendingTarget = null
    }
  }

  private fun enqueueTarget(value: T, shouldAnimate: Boolean, animationSpec: AnimationSpec<Float>) {
    requestVersion += 1
    pendingTarget = PendingTarget(value, requestVersion, shouldAnimate, animationSpec)
  }

  private fun confirmTargetValue(
    target: T,
    reason: DrawerValueChange.Reason,
  ): Boolean {
    validateValue(target)
    check(confirmingValueChange.not()) {
      "confirmValueChange must not request movement on the same UnstyledDrawerState."
    }
    val change = DrawerValueChange(
      initialValue = currentValue,
      targetValue = target,
      reason = reason,
    )
    confirmingValueChange = true
    val accepted = try {
      confirmValueChange(change)
    } finally {
      confirmingValueChange = false
    }
    if (accepted) {
      lastAcceptedValueChange = change
    }
    return accepted
  }

  private fun validateValue(value: T) {
    check(innerSnapPoints.contains(value)) {
      "Value $value is not present in this drawer's snapPoints."
    }
  }

  private fun visibleSizeFor(value: T): Float {
    if (viewportSizePx.isNaN()) return Float.NaN

    return with(density) {
      val viewportSize = viewportSizePx.toDp()
      val contentSize = contentSizePx
        .takeIf { it.isNaN().not() }
        ?.toDp()
        ?: viewportSize
      val requestedSize = innerSnapPoints.snapPointFor(value)
        .calculate(viewportSize, contentSize)
      when {
        requestedSize.isSpecified.not() || requestedSize.value.isNaN() -> 0f
        requestedSize.value == Float.NEGATIVE_INFINITY -> 0f
        requestedSize.value == Float.POSITIVE_INFINITY -> viewportSizePx
        else -> requestedSize.coerceIn(0.dp, minOf(viewportSize, contentSize)).toPx()
      }
    }
  }

  private fun animationSpecFor(value: T): AnimationSpec<Float> {
    return if (value == zeroValue) dismissAnimationSpec else animationSpec
  }

  private fun nextValueByVisibility(moreVisible: Boolean): T? {
    val currentVisibleSize = visibleSizeFor(currentValue)
    if (currentVisibleSize.isNaN()) return null

    return innerSnapPoints.entries
      .map { entry -> entry.value to visibleSizeFor(entry.value) }
      .filter { (_, visibleSize) ->
        visibleSize.isNaN().not() &&
          if (moreVisible) visibleSize > currentVisibleSize else visibleSize < currentVisibleSize
      }
      .let { candidates ->
        if (moreVisible) {
          candidates.minByOrNull { (_, visibleSize) -> visibleSize }
        } else {
          candidates.maxByOrNull { (_, visibleSize) -> visibleSize }
        }
      }
      ?.first
  }

  private fun captureGeometryProgress(): GeometryProgress<T>? {
    if (isIdle || viewportSizePx.isNaN() || contentSizePx.isNaN()) return null

    val visibleSize = visiblePanelSizePx()
    val positions = innerSnapPoints.entries
      .map { entry -> entry.value to visibleSizeFor(entry.value) }
      .distinctBy { (_, size) -> size.roundToInt() }
      .sortedBy { (_, size) -> size }
    val lower = positions.lastOrNull { (_, size) -> size <= visibleSize + 0.5f } ?: return null
    val upper = positions.firstOrNull { (_, size) -> size >= visibleSize - 0.5f } ?: return null
    val distance = upper.second - lower.second
    if (abs(distance) < 0.5f) return null

    return GeometryProgress(
      lowerValue = lower.first,
      upperValue = upper.first,
      fraction = ((visibleSize - lower.second) / distance).coerceIn(0f, 1f),
    )
  }

  private fun updateAnchors(
    newTarget: T? = null,
    preservedProgress: GeometryProgress<T>? = null,
  ) {
    if (viewportSizePx.isNaN() || contentSizePx.isNaN()) return

    val anchors = createAnchors()
    val requestedTarget = targetValue
    val resolvedTarget = newTarget
      ?: requestedTarget.takeIf {
        anchoredDraggableState.isAnimationRunning && innerSnapPoints.contains(it)
      }
      ?: currentValue.takeIf { innerSnapPoints.contains(it) }
      ?: innerSnapPoints.entries.first().value
    anchoredDraggableState.updateAnchors(anchors, newTarget = resolvedTarget)
    preservedProgress?.let { progress ->
      val currentOffset = anchoredDraggableState.offset
      if (currentOffset.isNaN().not()) {
        val lowerVisibleSize = visibleSizeFor(progress.lowerValue)
        val upperVisibleSize = visibleSizeFor(progress.upperValue)
        val desiredVisibleSize = lowerVisibleSize +
          (upperVisibleSize - lowerVisibleSize) * progress.fraction
        val desiredOffset = offsetForVisibleSize(desiredVisibleSize)
        anchoredDraggableState.dispatchRawDelta(desiredOffset - currentOffset)
      }
    }
  }

  private fun createAnchors(): DraggableAnchors<T> {
    return DraggableAnchors {
      innerSnapPoints.entries.forEach { entry ->
        entry.value at calculateAnchorFor(entry.value)
      }
    }
  }

  private class GeometryProgress<T : Any>(
    val lowerValue: T,
    val upperValue: T,
    val fraction: Float,
  )

  private fun closestValueTo(
    offset: Float,
    direction: Float = 0f,
    anchors: DraggableAnchors<T> = anchoredDraggableState.anchors,
  ): T? {
    if (viewportSizePx.isNaN() || contentSizePx.isNaN()) return null

    val candidates = innerSnapPoints.entries
      .map { entry -> entry.value to abs(anchors.positionOf(entry.value) - offset) }
    val distance = candidates.minOfOrNull { (_, distance) -> distance } ?: return null
    val nearest = candidates
      .filter { (_, candidateDistance) -> abs(candidateDistance - distance) < 0.5f }
      .map { (value, _) -> value }
    if (nearest.size > 1 && direction != 0f) {
      return nearest
        .map { value -> value to anchors.positionOf(value) }
        .let { tiedAnchors ->
          if (direction > 0f) {
            tiedAnchors.maxByOrNull { (_, anchor) -> anchor }
          } else {
            tiedAnchors.minByOrNull { (_, anchor) -> anchor }
          }
        }
        ?.first
    }
    return targetValue.takeIf { it in nearest } ?: nearest.first()
  }

  private fun anchorFor(value: T): Float {
    val measuredAnchor = anchoredDraggableState.anchors.positionOf(value)
    return if (measuredAnchor.isNaN()) calculateAnchorFor(value) else measuredAnchor
  }

  private fun calculateAnchorFor(value: T): Float {
    if (value == zeroValue) {
      return if (anchoredToMinEdge) {
        -hiddenEdgeInsetPx
      } else {
        viewportSizePx + hiddenEdgeInsetPx
      }
    }

    val visibleSize = visibleSizeFor(value)
    return offsetForVisibleSize(visibleSize)
  }

  private fun offsetForVisibleSize(visibleSize: Float): Float {
    return if (anchoredToMinEdge) {
      visibleSize
    } else {
      viewportSizePx - visibleSize
    }
  }

  private fun isAtZeroAnchor(): Boolean {
    val zeroValue = zeroValue ?: return false
    val offset = anchoredDraggableState.offset
    if (offset.isNaN()) return false

    return abs(offset - anchorFor(zeroValue)) <= 0.5f
  }

  private suspend fun awaitAnchors() {
    if (anchoredDraggableState.offset.isNaN().not()) return

    snapshotFlow { anchoredDraggableState.offset.isNaN().not() }.first { it }
  }
}

class DrawerSnapPoints<T : Any>(
  block: DrawerSnapPointsBuilder<T>.() -> Unit,
) {
  val entries: List<Entry<T>> = buildDrawerSnapPointEntries(block)

  operator fun contains(value: T): Boolean {
    return entries.any { entry -> entry.value == value }
  }

  internal fun snapPointFor(value: T): DrawerSnapPoint {
    return entries.first { entry -> entry.value == value }.snapPoint
  }

  internal val zeroValue: T?
    get() {
      return entries.firstOrNull { entry -> entry.snapPoint == DrawerSnapPoint.Zero }?.value
    }

  class Entry<T> internal constructor(
    val value: T,
    val snapPoint: DrawerSnapPoint,
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if ((other is DrawerSnapPoints<*>).not()) return false

    return entries.size == other.entries.size && entries.indices.all { index ->
      val entry = entries[index]
      val otherEntry = other.entries[index]
      entry.value == otherEntry.value && entry.snapPoint == otherEntry.snapPoint
    }
  }

  override fun hashCode(): Int {
    return entries.fold(1) { hash, entry ->
      31 * hash + (31 * entry.value.hashCode() + entry.snapPoint.hashCode())
    }
  }
}

internal class PendingTarget<T : Any>(
  val value: T,
  val version: Int,
  val shouldAnimate: Boolean,
  val animationSpec: AnimationSpec<Float>,
)

class DrawerValueChange<T : Any>(
  val initialValue: T,
  val targetValue: T,
  val reason: Reason,
) {
  @JvmInline
  value class Reason internal constructor(private val value: Int) {
    companion object {
      val Gesture = Reason(0)
      val NavigateBack = Reason(1)
      val ClickOutside = Reason(2)
      val AccessibilityAction = Reason(3)
      val Programmatic = Reason(4)
    }
  }
}

class DrawerSnapPointsBuilder<T : Any> internal constructor() {
  internal val entries = mutableListOf<DrawerSnapPoints.Entry<T>>()

  infix fun T.at(snapPoint: DrawerSnapPoint) {
    entries += DrawerSnapPoints.Entry(this, snapPoint)
  }
}

private fun <T : Any> buildDrawerSnapPointEntries(
  block: DrawerSnapPointsBuilder<T>.() -> Unit,
): List<DrawerSnapPoints.Entry<T>> {
  val builder = DrawerSnapPointsBuilder<T>()
  builder.block()
  val entries = builder.entries.toList()
  check(entries.isNotEmpty()) {
    "DrawerSnapPoints must define at least one snap point."
  }

  val duplicateValues = entries
    .groupBy { it.value }
    .filter { it.value.size > 1 }
    .keys
  check(duplicateValues.isEmpty()) {
    "DrawerSnapPoints values must be unique. Duplicate values: ${duplicateValues.joinToString()}."
  }

  val zeroValues = entries.filter { it.snapPoint == DrawerSnapPoint.Zero }
  check(zeroValues.size <= 1) {
    "DrawerSnapPoints can map at most one value to DrawerSnapPoint.Zero."
  }

  return entries
}

@Stable
class DrawerSnapPoint internal constructor(
  val calculate: (viewportSize: Dp, contentSize: Dp) -> Dp,
) {
  companion object {
    val Zero = DrawerSnapPoint { _, _ -> 0.dp }
    val ContentSize = DrawerSnapPoint { viewportSize, contentSize ->
      contentSize.coerceAtMost(viewportSize)
    }

    operator fun invoke(
      block: (viewportSize: Dp, contentSize: Dp) -> Dp,
    ): DrawerSnapPoint {
      return DrawerSnapPoint(block)
    }
  }
}
