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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.withoutEventHandling
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.math.abs
import kotlin.math.roundToInt

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

class DrawerSnapPoints<T : Any> internal constructor(
  val entries: List<Entry<T>>,
) {
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

    return entries.map { it.value to it.snapPoint } ==
      other.entries.map { it.value to it.snapPoint }
  }

  override fun hashCode(): Int {
    return entries.map { it.value to it.snapPoint }.hashCode()
  }
}

class DrawerSnapPointsBuilder<T : Any> internal constructor() {
  internal val entries = mutableListOf<DrawerSnapPoints.Entry<T>>()

  infix fun T.at(snapPoint: DrawerSnapPoint) {
    entries += DrawerSnapPoints.Entry(this, snapPoint)
  }
}

fun <T : Any> DrawerSnapPoints(
  block: DrawerSnapPointsBuilder<T>.() -> Unit,
): DrawerSnapPoints<T> {
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

  return DrawerSnapPoints(entries)
}

@JvmInline
value class DrawerPlacement internal constructor(private val value: Int) {
  companion object {
    val Start = DrawerPlacement(0)
    val End = DrawerPlacement(1)
    val Top = DrawerPlacement(2)
    val Bottom = DrawerPlacement(3)
  }
}

@JvmInline
value class DrawerPresentation internal constructor(private val value: Int) {
  companion object {
    val Modal = DrawerPresentation(0)
    val Inline = DrawerPresentation(1)
  }
}

@JvmInline
value class DrawerPanelAlignment internal constructor(private val value: Int) {
  companion object {
    val Start = DrawerPanelAlignment(0)
    val Center = DrawerPanelAlignment(1)
    val End = DrawerPanelAlignment(2)
  }
}

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

data class SystemUi(
  val statusBar: SystemUiAppearance = SystemUiAppearance.Unspecified,
  val navigationBar: SystemUiAppearance = SystemUiAppearance.Unspecified,
)

@JvmInline
value class SystemUiAppearance internal constructor(private val value: Int) {
  companion object {
    val Unspecified = SystemUiAppearance(0)
    val Light = SystemUiAppearance(1)
    val Dark = SystemUiAppearance(2)
  }
}

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

  private var requestedTargetVersion by mutableIntStateOf(0)
  private var requestedTargetReason by mutableStateOf(DrawerValueChange.Reason.Programmatic)
  private var requestedTargetShouldAnimate by mutableStateOf(true)
  private var requestedTargetAlreadyConfirmed by mutableStateOf(false)
  private var requestedTargetAnimationSpec: AnimationSpec<Float> = animationSpec
  private var pendingTargetValue: T? by mutableStateOf(null)
  private var pendingTargetVersion: Int by mutableIntStateOf(0)
  private var innerTargetValue: T by mutableStateOf(initialValue)
  private var confirmingValueChange = false
  private var movementReason by mutableStateOf(DrawerValueChange.Reason.Gesture)
  private var dismissedReported by mutableStateOf(initialValue == snapPoints.zeroValue)
  private var lastAcceptedValueChange by mutableStateOf<DrawerValueChange<T>?>(null)

  internal var viewportSizePx by mutableStateOf(Float.NaN)
  internal var contentSizePx by mutableStateOf(Float.NaN)
  internal var anchoredToMinEdge by mutableStateOf(false)
  private var hiddenEdgeInsetPx by mutableStateOf(0f)
  internal var isDragging by mutableStateOf(false)
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

    pendingTargetValue == null &&
      isDragging.not() &&
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
    val requestVersion = pendingTargetVersion
    try {
      snapshotFlow {
        pendingTargetVersion != requestVersion ||
          (isIdle && currentValue == value && targetValue == value)
      }.first { it }
    } catch (cancellationException: CancellationException) {
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
        closestValueTo(previousOffset) ?: snapPoints.entries.first().value
      }
      currentSupported -> currentValue
      else -> snapPoints.entries.first().value
    }

    if (currentSupported.not() || targetSupported.not()) {
      innerTargetValue = resolvedTarget
      pendingTargetValue = resolvedTarget
      requestedTargetReason = DrawerValueChange.Reason.Programmatic
      requestedTargetShouldAnimate = true
      requestedTargetAlreadyConfirmed = true
      requestedTargetAnimationSpec = animationSpecFor(resolvedTarget)
      requestedTargetVersion += 1
      pendingTargetVersion = requestedTargetVersion
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

  internal val pendingTargetKey: Int
    get() = pendingTargetValue?.let { pendingTargetVersion } ?: 0

  internal fun consumePendingTarget(): PendingTarget<T>? {
    val value = pendingTargetValue ?: return null
    return PendingTarget(
      value = value,
      version = pendingTargetVersion,
      reason = requestedTargetReason,
      shouldAnimate = requestedTargetShouldAnimate,
      alreadyConfirmed = requestedTargetAlreadyConfirmed,
      animationSpec = requestedTargetAnimationSpec,
    )
  }

  internal suspend fun animatePendingTarget(target: PendingTarget<T>) {
    if (pendingTargetVersion != target.version) return
    movementReason = target.reason
    awaitAnchors()
    anchoredDraggableState.animateTo(
      targetValue = target.value,
      animationSpec = target.animationSpec,
    )
    clearPendingTarget(target.version)
  }

  internal suspend fun snapPendingTarget(target: PendingTarget<T>) {
    if (pendingTargetVersion != target.version) return
    movementReason = target.reason
    awaitAnchors()
    anchoredDraggableState.snapTo(target.value)
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
    if (
      alreadyConfirmed.not() &&
      isLogicalNoOp.not() &&
      confirmTargetValue(value, reason).not()
    ) {
      return false
    }
    pendingTargetValue = value
    innerTargetValue = value
    requestedTargetReason = reason
    requestedTargetShouldAnimate = shouldAnimate
    requestedTargetAlreadyConfirmed = alreadyConfirmed
    requestedTargetAnimationSpec = animationSpec ?: animationSpecFor(value)
    requestedTargetVersion += 1
    pendingTargetVersion = requestedTargetVersion
    return true
  }

  internal fun settleToClosestValue(
    reason: DrawerValueChange.Reason,
    direction: Float = 0f,
  ) {
    val offset = anchoredDraggableState.offset
    if (offset.isNaN()) return

    val target = closestValueTo(offset, direction) ?: return
    if (
      requestTarget(
        value = target,
        reason = reason,
        shouldAnimate = true,
        force = true,
      ).not()
    ) {
      requestTarget(
        value = currentValue,
        reason = reason,
        alreadyConfirmed = true,
        force = true,
      )
    }
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
      requestTarget(
        value = target,
        reason = DrawerValueChange.Reason.Gesture,
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
      this.density == density
    ) {
      return
    }

    val preservedProgress = captureGeometryProgress()
    viewportSizePx = measuredSizePx
    this.anchoredToMinEdge = anchoredToMinEdge
    this.hiddenEdgeInsetPx = hiddenEdgeInsetPx
    this.density = density
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
    if (pendingTargetVersion == version) {
      pendingTargetValue = null
    }
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
        else -> requestedSize.coerceIn(0.dp, viewportSize).toPx()
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
        entry.value at anchorFor(entry.value)
      }
    }
  }

  private class GeometryProgress<T : Any>(
    val lowerValue: T,
    val upperValue: T,
    val fraction: Float,
  )

  private fun closestValueTo(offset: Float, direction: Float = 0f): T? {
    if (viewportSizePx.isNaN() || contentSizePx.isNaN()) return null

    val candidates = innerSnapPoints.entries
      .map { entry -> entry.value to abs(anchorFor(entry.value) - offset) }
    val distance = candidates.minOfOrNull { (_, distance) -> distance } ?: return null
    val nearest = candidates
      .filter { (_, candidateDistance) -> abs(candidateDistance - distance) < 0.5f }
      .map { (value, _) -> value }
    if (nearest.size > 1 && direction != 0f) {
      return nearest
        .map { value -> value to anchorFor(value) }
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

internal class PendingTarget<T : Any>(
  val value: T,
  val version: Int,
  val reason: DrawerValueChange.Reason,
  val shouldAnimate: Boolean,
  val alreadyConfirmed: Boolean,
  val animationSpec: AnimationSpec<Float>,
)

class DrawerScope internal constructor(
  internal val drawerState: UnstyledDrawerState<*>,
  internal val placement: DrawerPlacement,
  internal val presentation: DrawerPresentation,
  internal val gesturesEnabled: Boolean,
  internal val dismissOnClickOutside: Boolean,
  internal val overlay: (@Composable DrawerOverlayScope<*>.() -> Unit)?,
) {
  private var declaredViewportCount = 0

  internal fun declareViewport() {
    declaredViewportCount += 1
  }

  internal fun forgetViewport() {
    declaredViewportCount -= 1
  }

  internal fun validateSlots() {
    check(declaredViewportCount == 1) {
      "UnstyledDrawer must contain exactly one Viewport; found $declaredViewportCount."
    }
  }
}

class DrawerViewportScope<T : Any> internal constructor()

class DrawerPanelScope<T : Any> internal constructor(
  internal val drawerState: UnstyledDrawerState<T>,
)

class DrawerOverlayScope<T : Any> internal constructor(
  internal val drawerState: UnstyledDrawerState<T>,
)

private class DrawerContext<T : Any>(
  internal val state: UnstyledDrawerState<T>,
  internal val placement: DrawerPlacement,
  internal val presentation: DrawerPresentation,
  internal val gesturesEnabled: Boolean,
  dismissOnClickOutside: Boolean,
  internal val overlay: (@Composable DrawerOverlayScope<T>.() -> Unit)?,
  internal val interactionSource: MutableInteractionSource,
) {
  internal var dismissOnClickOutside by mutableStateOf(dismissOnClickOutside)
  internal var panelOverscrollEffect: OverscrollEffect? by mutableStateOf(null)
}

private val LocalDrawerContext: ProvidableCompositionLocal<DrawerContext<*>?> =
  compositionLocalOf { null }

@Composable
fun <T : Any> UnstyledDrawer(
  state: UnstyledDrawerState<T>,
  modifier: Modifier = Modifier,
  placement: DrawerPlacement = DrawerPlacement.Bottom,
  presentation: DrawerPresentation = DrawerPresentation.Modal,
  gesturesEnabled: Boolean = true,
  dismissOnNavigateBack: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  onDismissed: (change: DrawerValueChange<T>) -> Unit = {},
  overlay: (@Composable DrawerOverlayScope<T>.() -> Unit)? = null,
  systemUi: SystemUi = SystemUi(),
  content: @Composable DrawerScope.() -> Unit,
) {
  val animationScope = androidx.compose.runtime.rememberCoroutineScope()
  DisposableEffect(state, animationScope) {
    state.attachAnimationScope(animationScope)
    onDispose {
      state.attachAnimationScope(null)
    }
  }
  val zeroValue = state.zeroValue
  val visible = zeroValue == null ||
    state.hasVisiblePanel ||
    state.currentValue != zeroValue ||
    state.targetValue != zeroValue

  LaunchedEffect(state.currentValue, state.targetValue, state.isIdle) {
    val dismissedValue = zeroValue ?: return@LaunchedEffect
    if (
      state.isIdle &&
      state.currentValue == dismissedValue &&
      state.targetValue == dismissedValue &&
      state.markDismissed()
    ) {
      onDismissed(state.lastDismissedChange())
    }
  }

  val drawerScope = remember(
    state,
    placement,
    presentation,
    gesturesEnabled,
    dismissOnClickOutside,
    overlay,
    systemUi,
  ) {
    @Suppress("UNCHECKED_CAST")
    val typedOverlay = overlay as? @Composable DrawerOverlayScope<*>.() -> Unit
    DrawerScope(
      drawerState = state,
      placement = placement,
      presentation = presentation,
      gesturesEnabled = gesturesEnabled,
      dismissOnClickOutside = dismissOnClickOutside,
      overlay = typedOverlay,
    )
  }

  when (presentation) {
    DrawerPresentation.Modal -> {
      val modalState = rememberModalState(initiallyVisible = visible)
      SideEffect {
        modalState.transitionState.targetState = visible
      }
      LaunchedEffect(state.pendingTargetKey) {
        val target = state.consumePendingTarget() ?: return@LaunchedEffect
        if (target.shouldAnimate) {
          val opensFromZero = zeroValue != null &&
            target.value != zeroValue &&
            state.hasVisiblePanel.not()
          if (opensFromZero) {
            modalState.awaitAttachedToWindow()
          }
          state.animatePendingTarget(target)
        } else {
          state.snapPendingTarget(target)
        }
      }
      val predictiveBackHandler = remember(state, dismissOnNavigateBack) {
        object : ModalPredictiveBackHandler {
          override fun onBackStarted(): Boolean {
            if (dismissOnNavigateBack.not()) return false
            return state.startPredictiveBack()
          }

          override fun onBackProgressed(progress: Float) {
            state.progressPredictiveBack(progress)
          }

          override fun onBackCancelled() {
            state.cancelPredictiveBack()
          }

          override fun onBackInvoked() {
            state.invokePredictiveBack()
          }
        }
      }
      DisposableEffect(modalState, predictiveBackHandler) {
        modalState.setPredictiveBackHandler(predictiveBackHandler)
        onDispose {
          modalState.setPredictiveBackHandler(null)
        }
      }

      DrawerModalSourceHost<T>(
        modifier = modifier,
        drawerScope = drawerScope,
        content = content,
        systemUi = systemUi,
        showContent = visible.not() && modalState.hasMountedFragments.not(),
      )
      if (visible || modalState.hasMountedFragments) {
        Modal(
          state = modalState,
          onKeyEvent = drawerDismissKeyEvent(
            drawerState = state,
            dismissOnNavigateBack = dismissOnNavigateBack,
          ),
        ) {
          DrawerContent<T>(
            modifier = modifier,
            drawerScope = drawerScope,
            content = content,
            systemUi = systemUi,
            isModal = true,
            dismissOnNavigateBack = dismissOnNavigateBack,
          )
        }
      }
    }

    DrawerPresentation.Inline -> {
      LaunchedEffect(state.pendingTargetKey) {
        val target = state.consumePendingTarget() ?: return@LaunchedEffect
        if (target.shouldAnimate) {
          state.animatePendingTarget(target)
        } else {
          state.snapPendingTarget(target)
        }
      }
      DrawerContent<T>(
        modifier = modifier,
        drawerScope = drawerScope,
        content = content,
        systemUi = systemUi,
        isModal = false,
        dismissOnNavigateBack = dismissOnNavigateBack,
      )
    }
  }
}

@Composable
private fun <T : Any> DrawerModalSourceHost(
  modifier: Modifier,
  drawerScope: DrawerScope,
  content: @Composable DrawerScope.() -> Unit,
  systemUi: SystemUi,
  showContent: Boolean,
) {
  var sourceSize by remember { mutableStateOf(IntSize.Zero) }
  val density = androidx.compose.ui.platform.LocalDensity.current

  Box(
    modifier.onGloballyPositioned { coordinates ->
      sourceSize = coordinates.size
    },
  ) {
    if (showContent) {
      DrawerContent<T>(
        modifier = Modifier,
        drawerScope = drawerScope,
        content = content,
        systemUi = systemUi,
        isModal = false,
        dismissOnNavigateBack = false,
      )
    } else if (sourceSize != IntSize.Zero) {
      Spacer(
        Modifier.requiredSize(
          width = with(density) { sourceSize.width.toDp() },
          height = with(density) { sourceSize.height.toDp() },
        ),
      )
    }
  }
}

@Composable
private fun <T : Any> DrawerContent(
  modifier: Modifier,
  drawerScope: DrawerScope,
  content: @Composable DrawerScope.() -> Unit,
  systemUi: SystemUi,
  isModal: Boolean,
  dismissOnNavigateBack: Boolean,
) {
  ApplyAndroidSystemUi(
    systemUi = systemUi,
    enabled = isModal,
  )

  @Suppress("UNCHECKED_CAST")
  val typedDrawerState = drawerScope.drawerState as UnstyledDrawerState<T>

  val zeroValue = typedDrawerState.zeroValue
  if (dismissOnNavigateBack && zeroValue != null) {
    EscapeHandler {
      typedDrawerState.requestTarget(
        value = zeroValue,
        reason = DrawerValueChange.Reason.NavigateBack,
      )
    }
  }

  Box(modifier) {
    drawerScope.content()
    SideEffect {
      drawerScope.validateSlots()
    }
  }
}

private fun <T : Any> drawerDismissKeyEvent(
  drawerState: UnstyledDrawerState<T>,
  dismissOnNavigateBack: Boolean,
): (KeyEvent) -> Boolean {
  return { event ->
    val zeroValue = drawerState.zeroValue
    val shouldDismiss = dismissOnNavigateBack &&
      zeroValue != null &&
      event.type == KeyEventType.KeyDown &&
      (event.key == Key.Escape || event.key == Key.Back)
    if (shouldDismiss) {
      drawerState.requestTarget(
        value = checkNotNull(zeroValue),
        reason = DrawerValueChange.Reason.NavigateBack,
      )
    }
    shouldDismiss
  }
}

@Composable
fun DrawerScope.SwipeArea(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .then(
        buildModifier {
          if (gesturesEnabled) {
            add(Modifier.excludeDrawerEdgeFromSystemGesture())
          }
        },
      )
      .closedEdgeSwipe(
        drawerState = drawerState,
        resolvedPlacement = placement.resolve(LocalLayoutDirection.current),
        gesturesEnabled = gesturesEnabled,
      ),
  )
}

@Composable
fun <T : Any> DrawerScope.Viewport(
  modifier: Modifier = Modifier,
  panelAlignment: DrawerPanelAlignment = DrawerPanelAlignment.Start,
  windowInsets: WindowInsets = WindowInsets(),
  content: @Composable DrawerViewportScope<T>.() -> Unit,
) {
  @Suppress("UNCHECKED_CAST")
  val typedDrawerState = drawerState as UnstyledDrawerState<T>
  val typedOverlay = overlay as? @Composable DrawerOverlayScope<T>.() -> Unit
  DisposableEffect(this@Viewport) {
    declareViewport()
    onDispose {
      forgetViewport()
    }
  }
  val interactionSource = remember { MutableInteractionSource() }
  val interactionScope = androidx.compose.runtime.rememberCoroutineScope()
  val currentLayoutDirection = LocalLayoutDirection.current
  val context = remember(
    typedDrawerState,
    placement,
    presentation,
    gesturesEnabled,
    typedOverlay,
    interactionSource,
  ) {
    DrawerContext(
      state = typedDrawerState,
      placement = placement,
      presentation = presentation,
      gesturesEnabled = gesturesEnabled,
      dismissOnClickOutside = dismissOnClickOutside,
      overlay = typedOverlay,
      interactionSource = interactionSource,
    )
  }
  SideEffect {
    context.dismissOnClickOutside = dismissOnClickOutside
  }
  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect { interaction ->
      when (interaction) {
        is DragInteraction.Start -> drawerState.isDragging = true
        is DragInteraction.Stop,
        is DragInteraction.Cancel,
        -> drawerState.isDragging = false
      }
    }
  }
  val resolvedPlacementForModifier = placement.resolve(currentLayoutDirection)
  val zeroValue = drawerState.zeroValue
  val isInModalContent = LocalModalState.current.isAttachedToWindow
  val visible = zeroValue == null ||
    drawerState.hasVisiblePanel ||
    drawerState.currentValue != zeroValue ||
    drawerState.targetValue != zeroValue
  val panelBounds = remember { DrawerPanelBounds() }

  Layout(
    modifier = modifier
      .then(
        buildModifier {
          if (
            presentation == DrawerPresentation.Modal &&
            isInModalContent
          ) {
            add(
              Modifier.consumeModalOutsideTap(
                panelBounds = panelBounds,
                onOutsideTap = if (context.dismissOnClickOutside && zeroValue != null) {
                  {
                    drawerState.requestTarget(
                      zeroValue,
                      DrawerValueChange.Reason.ClickOutside,
                    )
                  }
                } else {
                  null
                },
              ),
            )
          }
        },
      )
      .then(
        buildModifier {
          if (
            presentation == DrawerPresentation.Modal &&
            visible &&
            gesturesEnabled &&
            drawerState.hasMultipleValues()
          ) {
            add(
              Modifier.panelSwipe(
                drawerState = drawerState,
                resolvedPlacement = resolvedPlacementForModifier,
                panelBounds = panelBounds,
                interactionSource = interactionSource,
                overscrollEffect = { context.panelOverscrollEffect },
                coroutineScope = interactionScope,
              ),
            )
          }
        },
      ),
    content = {
      CompositionLocalProvider(LocalDrawerContext provides context) {
        if (presentation == DrawerPresentation.Modal && visible) {
          Box(
            Modifier
              .drawerModalBarrierParentData()
              .semantics {
                if (context.dismissOnClickOutside && zeroValue != null) {
                  dismiss {
                    drawerState.requestTarget(
                      zeroValue,
                      DrawerValueChange.Reason.ClickOutside,
                    )
                    true
                  }
                }
              },
          )
        }
        context.overlay?.invoke(DrawerOverlayScope(typedDrawerState))
        DrawerViewportScope<T>().content()
      }
    },
  ) { measurables, constraints ->
    check(
      constraints.maxWidth != Constraints.Infinity && constraints.maxHeight != Constraints.Infinity,
    ) {
      "Drawer Viewport requires finite maximum width and height constraints. " +
        "Constrain Viewport before using Drawer."
    }
    val panelCount = measurables.count { it.parentData is DrawerPanelParentData }
    check(panelCount == 1) {
      "Drawer Viewport must contain exactly one Panel; found $panelCount."
    }
    val overlayCount = measurables.count { it.parentData == DrawerOverlayParentData }
    check(overlayCount <= 1) {
      "Drawer may contain at most one Overlay; found $overlayCount."
    }
    val resolvedPlacement = placement.resolve(layoutDirection)
    val leftInset = windowInsets.getLeft(this, layoutDirection)
    val topInset = windowInsets.getTop(this)
    val rightInset = windowInsets.getRight(this, layoutDirection)
    val bottomInset = windowInsets.getBottom(this)
    val layoutWidth = constraints.maxWidth
    val layoutHeight = constraints.maxHeight
    val usableWidth = (layoutWidth - leftInset - rightInset).coerceAtLeast(0)
    val usableHeight = (layoutHeight - topInset - bottomInset).coerceAtLeast(0)
    val childConstraints = Constraints(
      minWidth = 0,
      maxWidth = usableWidth,
      minHeight = 0,
      maxHeight = usableHeight,
    )
    val placeables = measurables.map { measurable ->
      when (measurable.parentData) {
        DrawerModalBarrierParentData,
        DrawerOverlayParentData,
        -> measurable.measure(Constraints.fixed(layoutWidth, layoutHeight))
        else -> measurable.measure(childConstraints)
      }
    }
    val panelMainAxisSizePx = placeables
      .filter { placeable -> placeable.parentData is DrawerPanelParentData }
      .maxOfOrNull { placeable -> placeable.mainAxisSize(resolvedPlacement) }
      ?: 0
    val panelPlaceable = placeables.firstOrNull { placeable ->
      placeable.parentData is DrawerPanelParentData
    }
    val panelWidth = panelPlaceable?.drawerPanelWidth ?: 0
    val panelHeight = panelPlaceable?.drawerPanelHeight ?: 0
    val panelCrossAxisOffset = panelPlaceable?.crossAxisOffset(
      resolvedPlacement = resolvedPlacement,
      panelAlignment = panelAlignment,
      usableWidth = usableWidth,
      usableHeight = usableHeight,
      layoutDirection = layoutDirection,
    ) ?: 0
    val viewportMainAxisSizePx = if (resolvedPlacement.isHorizontal) {
      usableWidth
    } else {
      usableHeight
    }.toFloat()
    val hiddenEdgeInsetPx = when (resolvedPlacement) {
      ResolvedDrawerPlacement.Start -> leftInset
      ResolvedDrawerPlacement.End -> rightInset
      ResolvedDrawerPlacement.Top -> topInset
      ResolvedDrawerPlacement.Bottom -> bottomInset
    }.toFloat()
    drawerState.updateViewportSize(
      measuredSizePx = viewportMainAxisSizePx,
      anchoredToMinEdge = resolvedPlacement.isMinEdge,
      hiddenEdgeInsetPx = hiddenEdgeInsetPx,
      density = this,
    )
    drawerState.updateContentSize(panelMainAxisSizePx.toFloat())

    layout(layoutWidth, layoutHeight) {
      val panelMainAxisOffset =
        drawerState.panelMainAxisOffsetPx(resolvedPlacement, viewportMainAxisSizePx)
          .roundToInt()
      panelBounds.update(
        left = if (resolvedPlacement.isHorizontal) {
          leftInset + panelMainAxisOffset
        } else {
          leftInset + panelCrossAxisOffset
        },
        top = if (resolvedPlacement.isHorizontal) {
          topInset + panelCrossAxisOffset
        } else {
          topInset + panelMainAxisOffset
        },
        width = panelWidth,
        height = panelHeight,
      )
      placeables
        .filter { placeable ->
          placeable.parentData == DrawerModalBarrierParentData ||
            placeable.parentData == DrawerOverlayParentData
        }
        .forEach { placeable ->
          placeable.placeRelative(0, 0)
        }
      placeables
        .filter { placeable ->
          placeable.parentData != DrawerModalBarrierParentData &&
            placeable.parentData != DrawerOverlayParentData
        }
        .forEach { placeable ->
          when (placeable.parentData) {
            is DrawerPanelParentData -> {
              val crossAxisOffset = placeable.crossAxisOffset(
                resolvedPlacement = resolvedPlacement,
                panelAlignment = panelAlignment,
                usableWidth = usableWidth,
                usableHeight = usableHeight,
                layoutDirection = layoutDirection,
              )
              if (resolvedPlacement.isHorizontal) {
                placePanel(
                  placeable = placeable,
                  x = leftInset + panelMainAxisOffset,
                  y = topInset + crossAxisOffset,
                  hidden = drawerState.isPanelHidden,
                )
              } else {
                placePanel(
                  placeable = placeable,
                  x = leftInset + crossAxisOffset,
                  y = topInset + panelMainAxisOffset,
                  hidden = drawerState.isPanelHidden,
                )
              }
            }

            else -> placeable.placeRelative(leftInset, topInset)
          }
        }
    }
  }
}

private fun androidx.compose.ui.layout.Placeable.PlacementScope.placePanel(
  placeable: Placeable,
  x: Int,
  y: Int,
  hidden: Boolean,
) {
  val placementX = x - (placeable.width - placeable.drawerPanelWidth) / 2
  val placementY = y - (placeable.height - placeable.drawerPanelHeight) / 2
  if (hidden) {
    placeable.placeWithLayer(placementX, placementY) {
      alpha = 0f
    }
  } else {
    placeable.place(placementX, placementY)
  }
}

@Composable
@Suppress("UNCHECKED_CAST")
fun <T : Any> DrawerViewportScope<T>.Panel(
  modifier: Modifier = Modifier,
  overscrollEffect: OverscrollEffect? = null,
  content: @Composable DrawerPanelScope<T>.() -> Unit,
) {
  val context = LocalDrawerContext.current as? DrawerContext<T>
    ?: error("Drawer Panel must be placed inside Drawer Viewport.")
  val state = context.state
  val layoutDirection = LocalLayoutDirection.current
  val resolvedPlacement = context.placement.resolve(layoutDirection)
  val orientation = if (resolvedPlacement.isHorizontal) {
    Orientation.Horizontal
  } else {
    Orientation.Vertical
  }
  val panelOverscrollEffect = remember(overscrollEffect) {
    overscrollEffect?.withoutVisualEffect()
  }
  val panelOverscrollVisualEffect = remember(overscrollEffect) {
    overscrollEffect?.withoutEventHandling()
  }
  val panelParentData = remember { DrawerPanelParentData() }
  SideEffect {
    context.panelOverscrollEffect = panelOverscrollEffect
  }
  DisposableEffect(context) {
    onDispose {
      context.panelOverscrollEffect = null
    }
  }

  Layout(
    modifier = modifier
      .zIndex(1f)
      .then(
        buildModifier {
          if (state.isPanelHidden) {
            add(
              Modifier
                .semantics { hideFromAccessibility() }
                .focusProperties { canFocus = false },
            )
          }
        },
      )
      .then(
        buildModifier {
          if (panelOverscrollVisualEffect != null) {
            add(Modifier.overscroll(panelOverscrollVisualEffect))
          }
        },
      )
      .then(
        buildModifier {
          if (
            context.gesturesEnabled &&
            state.hasMultipleValues()
          ) {
            add(
              Modifier
                .anchoredDraggable(
                  state = state.anchoredDraggableState,
                  orientation = orientation,
                  enabled = true,
                  interactionSource = context.interactionSource,
                  overscrollEffect = panelOverscrollEffect,
                  flingBehavior = remember(state) {
                    DrawerFlingBehavior(state)
                  },
                )
                .nestedScroll(
                  remember(state, orientation, resolvedPlacement) {
                    ConsumeSwipeWithinDrawerBoundsNestedScrollConnection(
                      drawerState = state,
                      orientation = orientation,
                      resolvedPlacement = resolvedPlacement,
                    )
                  },
                ),
            )
          }
        },
      )
      .drawerPanelParentData(panelParentData),
    content = {
      DrawerPanelScope(state).content()
    },
  ) { measurables, constraints ->
    val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { measurable ->
      measurable.measure(childConstraints)
    }
    val width = maxOf(
      constraints.minWidth,
      placeables.maxOfOrNull { it.measuredWidth } ?: 0,
    )
    val height = maxOf(
      constraints.minHeight,
      placeables.maxOfOrNull { it.measuredHeight } ?: 0,
    )
    panelParentData.update(width = width, height = height)

    layout(width, height) {
      placeables.forEach { placeable ->
        placeable.placeRelative(0, 0)
      }
    }
  }
}

@Composable
fun <T : Any> DrawerPanelScope<T>.DragHandle(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit = {},
) {
  val expandTarget = drawerState.nextMoreVisibleValue()
  val collapseTarget = drawerState.nextLessVisibleValue()
  val dismissTarget = drawerState.zeroValue
    ?.takeIf { it != drawerState.currentValue }
  val hasAction = expandTarget != null || collapseTarget != null || dismissTarget != null

  Box(
    modifier
      .then(
        buildModifier {
          if (hasAction) {
            add(Modifier.focusable())
          }
        },
      )
      .semantics(mergeDescendants = false) {
        if (expandTarget != null) {
          expand {
            drawerState.requestTarget(
              value = expandTarget,
              reason = DrawerValueChange.Reason.AccessibilityAction,
            )
            true
          }
        }
        if (collapseTarget != null) {
          collapse {
            drawerState.requestTarget(
              value = collapseTarget,
              reason = DrawerValueChange.Reason.AccessibilityAction,
            )
            true
          }
        }
        if (dismissTarget != null) {
          dismiss {
            drawerState.requestTarget(
              value = dismissTarget,
              reason = DrawerValueChange.Reason.AccessibilityAction,
            )
            true
          }
        }
      },
  ) {
    content()
  }
}

@Composable
fun <T : Any> DrawerOverlayScope<T>.Overlay(
  modifier: Modifier = Modifier,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable () -> Unit = {},
) {
  val context = LocalDrawerContext.current
    ?: error("Drawer Overlay must be placed inside Drawer Viewport.")
  val isModalPresentation = context.presentation == DrawerPresentation.Modal
  val zeroValue = drawerState.zeroValue
  val targetVisible = zeroValue == null ||
    drawerState.targetValue != zeroValue ||
    (drawerState.currentValue == zeroValue && drawerState.hasVisiblePanel)
  val initialVisible = if (isModalPresentation) {
    LocalModalState.current.transitionState.currentState
  } else {
    targetVisible
  }
  val visibilityState = remember(drawerState, isModalPresentation) {
    MutableTransitionState(initialVisible)
  }
  SideEffect {
    visibilityState.targetState = targetVisible
  }
  Box(Modifier.drawerOverlayParentData()) {
    AnimatedVisibility(
      visibleState = visibilityState,
      enter = enter,
      exit = exit,
    ) {
      Box(
        modifier.then(
          buildModifier {
            if (isModalPresentation) {
              add(Modifier.modalFragment())
            }
          },
        ),
      ) {
        content()
      }
    }
  }
}

private class DrawerPanelBounds {
  var left = 0f
    private set
  var top = 0f
    private set
  var right = 0f
    private set
  var bottom = 0f
    private set

  fun update(
    left: Int,
    top: Int,
    width: Int,
    height: Int,
  ) {
    this.left = left.toFloat()
    this.top = top.toFloat()
    right = this.left + width
    bottom = this.top + height
  }
}

private fun Modifier.consumeModalOutsideTap(
  panelBounds: DrawerPanelBounds,
  onOutsideTap: (() -> Unit)?,
): Modifier {
  return pointerInput(panelBounds, onOutsideTap) {
    detectTapGestures { position ->
      if (position.isInside(panelBounds).not()) {
        onOutsideTap?.invoke()
      }
    }
  }
}

private fun <T : Any> Modifier.panelSwipe(
  drawerState: UnstyledDrawerState<T>,
  resolvedPlacement: ResolvedDrawerPlacement,
  panelBounds: DrawerPanelBounds,
  interactionSource: MutableInteractionSource,
  overscrollEffect: () -> OverscrollEffect?,
  coroutineScope: CoroutineScope,
): Modifier {
  return pointerInput(drawerState, resolvedPlacement, panelBounds) {
    awaitEachGesture {
      val down = awaitFirstDown(requireUnconsumed = false)
      var accepted = false
      var dragInteraction: DragInteraction.Start? = null
      val velocityTracker = VelocityTracker()
      fun beginDrag() {
        if (accepted.not()) {
          accepted = true
          dragInteraction = DragInteraction.Start().also(interactionSource::tryEmit)
        }
      }

      fun dispatchDragDelta(delta: Float) {
        fun dispatch(available: Offset): Offset {
          return drawerState.anchoredDraggableState
            .dispatchRawDelta(available.toFloat(resolvedPlacement.orientation))
            .toOffset(resolvedPlacement.orientation)
        }

        val effect = overscrollEffect()
        if (effect == null) {
          dispatch(delta.toOffset(resolvedPlacement.orientation))
        } else {
          effect.applyToScroll(
            delta.toOffset(resolvedPlacement.orientation),
            NestedScrollSource.UserInput,
            ::dispatch,
          )
        }
      }

      fun finishDrag(completed: Boolean) {
        dragInteraction?.let { interaction ->
          val finishInteraction = if (completed) {
            DragInteraction.Stop(interaction)
          } else {
            DragInteraction.Cancel(interaction)
          }
          interactionSource.tryEmit(finishInteraction)
        }
        if (accepted.not()) return

        val releaseVelocity = velocityTracker.calculateVelocity()
        val effect = overscrollEffect()
        if (effect == null) {
          drawerState.settleFromFling(releaseVelocity.toFloat(resolvedPlacement.orientation))
        } else {
          coroutineScope.launch {
            effect.applyToFling(releaseVelocity) { available ->
              drawerState.settleFromFling(available.toFloat(resolvedPlacement.orientation))
              Velocity.Zero
            }
          }
        }
      }
      if (down.position.isInside(panelBounds).not()) {
        var panelEntry: PointerInputChange? = null
        while (panelEntry == null) {
          val change = awaitPointerEvent(PointerEventPass.Initial)
            .changes
            .firstOrNull { it.id == down.id }
            ?: return@awaitEachGesture
          if (change.pressed.not()) return@awaitEachGesture
          if (change.position.isInside(panelBounds)) {
            panelEntry = change
          }
        }

        velocityTracker.addPosition(panelEntry.uptimeMillis, panelEntry.position)
        val completed = drag(panelEntry.id) { change ->
          val delta = change.positionChange().toFloat(resolvedPlacement.orientation)
          if (delta != 0f) {
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            dispatchDragDelta(delta)
            change.consume()
            beginDrag()
          }
        }
        finishDrag(completed)
        return@awaitEachGesture
      }

      velocityTracker.addPosition(down.uptimeMillis, down.position)
      val changeAfterSlop = if (resolvedPlacement.orientation == Orientation.Horizontal) {
        awaitHorizontalTouchSlopOrCancellation(down.id) { change, overSlop ->
          velocityTracker.addPosition(change.uptimeMillis, change.position)
          dispatchDragDelta(overSlop)
          change.consume()
          beginDrag()
        }
      } else {
        awaitVerticalTouchSlopOrCancellation(down.id) { change, overSlop ->
          velocityTracker.addPosition(change.uptimeMillis, change.position)
          dispatchDragDelta(overSlop)
          change.consume()
          beginDrag()
        }
      }
      if (accepted && changeAfterSlop != null) {
        val completed = drag(changeAfterSlop.id) { change ->
          velocityTracker.addPosition(change.uptimeMillis, change.position)
          val delta = change.positionChange().toFloat(resolvedPlacement.orientation)
          dispatchDragDelta(delta)
          change.consume()
        }
        finishDrag(completed)
      }
    }
  }
}

private fun Offset.isInside(panelBounds: DrawerPanelBounds): Boolean {
  return x >= panelBounds.left &&
    x <= panelBounds.right &&
    y >= panelBounds.top &&
    y <= panelBounds.bottom
}

private fun <T : Any> Modifier.closedEdgeSwipe(
  drawerState: UnstyledDrawerState<T>,
  resolvedPlacement: ResolvedDrawerPlacement,
  gesturesEnabled: Boolean,
): Modifier {
  return pointerInput(drawerState, resolvedPlacement, gesturesEnabled) {
    awaitEachGesture {
      val down = awaitFirstDown(requireUnconsumed = false)
      val zeroValue = drawerState.zeroValue
      if (
        gesturesEnabled.not() ||
        drawerState.hasZeroValue.not() ||
        drawerState.hasMultipleValues().not() ||
        drawerState.isAtZeroValue.not() ||
        drawerState.targetValue != zeroValue
      ) {
        return@awaitEachGesture
      }

      var accepted = false
      val velocityTracker = VelocityTracker()
      velocityTracker.addPosition(down.uptimeMillis, down.position)
      drag(down.id) { change ->
        val delta = change.positionChange().toFloat(resolvedPlacement.orientation)
        if (accepted || resolvedPlacement.isOpeningDelta(delta)) {
          velocityTracker.addPosition(change.uptimeMillis, change.position)
          drawerState.anchoredDraggableState.dispatchRawDelta(delta)
          change.consume()
          accepted = true
        }
      }
      if (accepted) {
        drawerState.settleFromFling(
          velocityTracker.calculateVelocity().toFloat(resolvedPlacement.orientation),
        )
      }
    }
  }
}

private class DrawerPanelParentData {
  var measuredWidth: Int = 0
    private set

  var measuredHeight: Int = 0
    private set

  fun update(width: Int, height: Int) {
    measuredWidth = width
    measuredHeight = height
  }
}

private fun Modifier.drawerPanelParentData(drawerPanelParentData: DrawerPanelParentData): Modifier {
  return then(
    object : ParentDataModifier {
      override fun Density.modifyParentData(parentData: Any?): Any {
        return drawerPanelParentData
      }
    },
  )
}

private object DrawerModalBarrierParentData

private fun Modifier.drawerModalBarrierParentData(): Modifier {
  return then(
    object : ParentDataModifier {
      override fun Density.modifyParentData(parentData: Any?): Any {
        return DrawerModalBarrierParentData
      }
    },
  )
}

private object DrawerOverlayParentData

private fun Modifier.drawerOverlayParentData(): Modifier {
  return then(
    object : ParentDataModifier {
      override fun Density.modifyParentData(parentData: Any?): Any {
        return DrawerOverlayParentData
      }
    },
  )
}

private fun Placeable.mainAxisSize(resolvedPlacement: ResolvedDrawerPlacement): Int {
  return if (resolvedPlacement.isHorizontal) {
    drawerPanelWidth
  } else {
    drawerPanelHeight
  }
}

private val Placeable.drawerPanelWidth: Int
  get() {
    val panelParentData = parentData as DrawerPanelParentData
    return maxOf(width, measuredWidth, panelParentData.measuredWidth)
  }

private val Placeable.drawerPanelHeight: Int
  get() {
    val panelParentData = parentData as DrawerPanelParentData
    return maxOf(height, measuredHeight, panelParentData.measuredHeight)
  }

private fun Placeable.crossAxisOffset(
  resolvedPlacement: ResolvedDrawerPlacement,
  panelAlignment: DrawerPanelAlignment,
  usableWidth: Int,
  usableHeight: Int,
  layoutDirection: LayoutDirection,
): Int {
  val available = if (resolvedPlacement.isHorizontal) {
    usableHeight - drawerPanelHeight
  } else {
    usableWidth - drawerPanelWidth
  }.coerceAtLeast(0)
  return when (panelAlignment) {
    DrawerPanelAlignment.Center -> available / 2
    DrawerPanelAlignment.End -> available
    else -> 0
  }.let { offset ->
    if (
      resolvedPlacement.isHorizontal.not() &&
      layoutDirection == LayoutDirection.Rtl &&
      panelAlignment == DrawerPanelAlignment.Start
    ) {
      available
    } else if (
      resolvedPlacement.isHorizontal.not() &&
      layoutDirection == LayoutDirection.Rtl &&
      panelAlignment == DrawerPanelAlignment.End
    ) {
      0
    } else {
      offset
    }
  }
}

private class DrawerFlingBehavior<T : Any>(
  private val drawerState: UnstyledDrawerState<T>,
) : FlingBehavior {
  override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
    drawerState.settleFromFling(initialVelocity)
    return initialVelocity
  }
}

private fun <T : Any> ConsumeSwipeWithinDrawerBoundsNestedScrollConnection(
  drawerState: UnstyledDrawerState<T>,
  orientation: Orientation,
  resolvedPlacement: ResolvedDrawerPlacement,
): NestedScrollConnection = object : NestedScrollConnection {
  private val anchoredDraggableState: AnchoredDraggableState<T>
    get() = drawerState.anchoredDraggableState

  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    val delta = available.toFloat(orientation)
    return if (source == NestedScrollSource.UserInput && resolvedPlacement.isOpeningDelta(delta)) {
      anchoredDraggableState.dispatchRawDelta(delta).toOffset(orientation)
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
      anchoredDraggableState.dispatchRawDelta(available.toFloat(orientation)).toOffset(orientation)
    } else {
      Offset.Zero
    }
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    val velocity = available.toFloat(orientation)
    val currentOffset = anchoredDraggableState.requireOffset()
    val minAnchor = anchoredDraggableState.anchors.minPosition()
    val maxAnchor = anchoredDraggableState.anchors.maxPosition()
    val canMoveTowardOpen = if (resolvedPlacement.isMinEdge) {
      currentOffset < maxAnchor
    } else {
      currentOffset > minAnchor
    }
    return if (resolvedPlacement.isOpeningDelta(velocity) && canMoveTowardOpen) {
      drawerState.settleFromFling(velocity)
      available
    } else {
      Velocity.Zero
    }
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    drawerState.settleFromFling(available.toFloat(orientation))
    return available
  }
}

internal sealed class ResolvedDrawerPlacement(
  val orientation: Orientation,
  val isMinEdge: Boolean,
) {
  data object Start : ResolvedDrawerPlacement(Orientation.Horizontal, true)
  data object End : ResolvedDrawerPlacement(Orientation.Horizontal, false)
  data object Top : ResolvedDrawerPlacement(Orientation.Vertical, true)
  data object Bottom : ResolvedDrawerPlacement(Orientation.Vertical, false)

  val isHorizontal: Boolean
    get() = orientation == Orientation.Horizontal

  fun isOpeningDelta(delta: Float): Boolean {
    return if (isMinEdge) {
      delta > 0f
    } else {
      delta < 0f
    }
  }
}

private fun DrawerPlacement.resolve(layoutDirection: LayoutDirection): ResolvedDrawerPlacement {
  return when (this) {
    DrawerPlacement.Top -> ResolvedDrawerPlacement.Top
    DrawerPlacement.Bottom -> ResolvedDrawerPlacement.Bottom
    DrawerPlacement.Start -> if (layoutDirection == LayoutDirection.Ltr) {
      ResolvedDrawerPlacement.Start
    } else {
      ResolvedDrawerPlacement.End
    }

    DrawerPlacement.End -> if (layoutDirection == LayoutDirection.Ltr) {
      ResolvedDrawerPlacement.End
    } else {
      ResolvedDrawerPlacement.Start
    }

    else -> ResolvedDrawerPlacement.Bottom
  }
}

private fun Float.toOffset(orientation: Orientation): Offset {
  return Offset(
    x = if (orientation == Orientation.Horizontal) this else 0f,
    y = if (orientation == Orientation.Vertical) this else 0f,
  )
}

@JvmName("velocityToFloat")
private fun Velocity.toFloat(orientation: Orientation): Float {
  return if (orientation == Orientation.Horizontal) x else y
}

@JvmName("offsetToFloat")
private fun Offset.toFloat(orientation: Orientation): Float {
  return if (orientation == Orientation.Horizontal) x else y
}
