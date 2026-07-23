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
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Stable
class DrawerSnapPoint(
  val identifier: String,
  val calculateVisibleSize: (containerSize: Dp, panelSize: Dp) -> Dp,
) {
  companion object {
    val Open: DrawerSnapPoint =
      DrawerSnapPoint("open") { _, panelSize -> panelSize }

    val Closed: DrawerSnapPoint = DrawerSnapPoint("closed") { _, _ -> 0.dp }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as DrawerSnapPoint

    return identifier == other.identifier
  }

  override fun hashCode(): Int {
    return identifier.hashCode()
  }

  override fun toString(): String {
    return "DrawerSnapPoint(identifier='$identifier')"
  }
}

@JvmInline
value class DrawerPosition internal constructor(private val value: String) {
  companion object {
    val Top = DrawerPosition("top")
    val Bottom = DrawerPosition("bottom")
    val Start = DrawerPosition("start")
    val End = DrawerPosition("end")
  }

  override fun toString(): String {
    return value
  }
}

@Composable
fun rememberDrawerState(
  initialSnapPoint: DrawerSnapPoint = DrawerSnapPoint.Closed,
  snapPoints: List<DrawerSnapPoint> = listOf(DrawerSnapPoint.Closed, DrawerSnapPoint.Open),
  animationSpec: AnimationSpec<Float> = tween(),
  velocityThreshold: () -> Dp = { 125.dp },
  positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
  decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
): DrawerState {
  val density = LocalDensity.current
  val scope = rememberCoroutineScope()
  val state = remember(scope, density) {
    DrawerState(
      initialSnapPoint = initialSnapPoint,
      snapPoints = snapPoints,
      coroutineScope = scope,
      density = density,
      animationSpec = animationSpec,
      velocityThreshold = velocityThreshold,
      positionalThreshold = positionalThreshold,
      decayAnimationSpec = decayAnimationSpec,
    )
  }
  SideEffect {
    state.snapPoints = snapPoints
  }
  return state
}

class DrawerState internal constructor(
  initialSnapPoint: DrawerSnapPoint,
  snapPoints: List<DrawerSnapPoint>,
  private val coroutineScope: CoroutineScope,
  private val density: Density,
  animationSpec: AnimationSpec<Float>,
  velocityThreshold: () -> Dp,
  positionalThreshold: (totalDistance: Dp) -> Dp,
  decayAnimationSpec: DecayAnimationSpec<Float>,
) {
  init {
    checkValidSnapPoints(snapPoints)
    check(snapPoints.contains(initialSnapPoint)) {
      "The initialSnapPoint ${initialSnapPoint.identifier} was not part of the included " +
        "snapPoints while creating the drawer's state."
    }
  }

  internal var pendingSnapPointChange: Job? = null
  private var pendingTargetSnapPoint: DrawerSnapPoint? by mutableStateOf(null)
  internal var containerSizePx by mutableStateOf(Float.NaN)
  internal var panelSizePx by mutableStateOf(Float.NaN)
  internal var isDragging by mutableStateOf(false)

  internal val anchoredDraggableState = AnchoredDraggableState(
    initialValue = initialSnapPoint,
    positionalThreshold = { totalDistance ->
      with(density) {
        positionalThreshold(totalDistance.toDp()).toPx()
      }
    },
    velocityThreshold = {
      with(density) {
        velocityThreshold().toPx()
      }
    },
    snapAnimationSpec = animationSpec,
    decayAnimationSpec = decayAnimationSpec,
  )

  private var innerSnapPoints: List<DrawerSnapPoint> by mutableStateOf(snapPoints)

  var snapPoints: List<DrawerSnapPoint>
    get() {
      return innerSnapPoints
    }
    set(value) {
      checkValidSnapPoints(value)
      innerSnapPoints = value
      updateAnchors()
    }

  val currentSnapPoint: DrawerSnapPoint
    get() {
      return anchoredDraggableState.settledValue
    }

  private val derivedTargetSnapPoint: DrawerSnapPoint by derivedStateOf {
    anchoredDraggableState.targetValue
  }

  var targetSnapPoint: DrawerSnapPoint
    get() = pendingTargetSnapPoint ?: derivedTargetSnapPoint
    set(value) {
      coroutineScope.launch {
        animateTo(value)
      }
    }

  val isIdle: Boolean by derivedStateOf {
    val currentPosition =
      anchoredDraggableState.anchors.positionOf(anchoredDraggableState.settledValue)
    val currentOffset = anchoredDraggableState.offset
    val isSettledAtCurrentSnapPoint =
      currentOffset.isNaN() ||
        currentPosition.isNaN() ||
        abs(currentOffset - currentPosition) < 0.5f

    pendingTargetSnapPoint == null &&
      isDragging.not() &&
      anchoredDraggableState.isAnimationRunning.not() &&
      isSettledAtCurrentSnapPoint
  }

  val offset: Float by derivedStateOf {
    if (anchoredDraggableState.offset.isNaN() || containerSizePx.isNaN()) {
      0f
    } else {
      (containerSizePx - anchoredDraggableState.offset).coerceAtLeast(0f)
    }
  }

  fun progress(from: DrawerSnapPoint, to: DrawerSnapPoint): Float {
    return anchoredDraggableState.progress(from, to)
  }

  suspend fun animateTo(value: DrawerSnapPoint) {
    check(innerSnapPoints.contains(value)) {
      "Tried to set currentSnapPoint to an unknown snapPoint with identifier " +
        "${value.identifier}. Make sure that the snapPoint is passed to the list of " +
        "snapPoints when instantiating the drawer's state."
    }

    pendingTargetSnapPoint = value
    try {
      pendingSnapPointChange?.cancel()
      awaitAnchors()
      anchoredDraggableState.animateTo(value)
    } finally {
      if (pendingTargetSnapPoint == value) {
        pendingTargetSnapPoint = null
      }
    }
  }

  fun jumpTo(value: DrawerSnapPoint) {
    check(innerSnapPoints.contains(value)) {
      "Tried to set currentSnapPoint to an unknown snapPoint with identifier " +
        "${value.identifier}. Make sure that the snapPoint is passed to the list of " +
        "snapPoints when instantiating the drawer's state."
    }
    if (
      anchoredDraggableState.settledValue == value &&
      anchoredDraggableState.targetValue == value
    ) {
      return
    }

    pendingTargetSnapPoint = value
    pendingSnapPointChange?.cancel()
    pendingSnapPointChange = coroutineScope.launch {
      try {
        awaitAnchors()
        anchoredDraggableState.snapTo(value)
      } finally {
        if (pendingTargetSnapPoint == value) {
          pendingTargetSnapPoint = null
        }
      }
    }
  }

  fun invalidateSnapPoints() {
    updateAnchors()
  }

  internal fun updateContainerSize(measuredSizePx: Float) {
    if (containerSizePx == measuredSizePx) return

    containerSizePx = measuredSizePx
    updateAnchors()
  }

  internal fun updatePanelSize(measuredSizePx: Float) {
    if (panelSizePx == measuredSizePx) return

    panelSizePx = measuredSizePx
    updateAnchors()
  }

  internal fun visiblePanelHeightPx(): Float {
    if (containerSizePx.isNaN()) return 0f
    if (anchoredDraggableState.offset.isNaN()) return 0f

    return (containerSizePx - anchoredDraggableState.offset)
      .coerceIn(0f, containerSizePx)
  }

  internal fun panelTopPx(viewportHeightPx: Float): Float {
    if (anchoredDraggableState.offset.isNaN()) return viewportHeightPx

    return anchoredDraggableState.offset.coerceIn(0f, viewportHeightPx)
  }

  private fun updateAnchors() {
    if (containerSizePx.isNaN() || panelSizePx.isNaN()) return

    val anchors = DraggableAnchors {
      with(density) {
        innerSnapPoints.forEach { snapPoint ->
          val containerSize = containerSizePx.toDp()
          val panelSize = panelSizePx.toDp()
          val snapPointSize = snapPoint.calculateVisibleSize(containerSize, panelSize)
            .takeIf { it.isSpecified }
            ?.coerceIn(0.dp, containerSize)
            ?: 0.dp
          val offset = (containerSize - snapPointSize).toPx()
          snapPoint at offset
        }
      }
    }
    val newTarget = if (anchoredDraggableState.offset.isNaN()) {
      anchoredDraggableState.settledValue
    } else {
      targetSnapPoint
    }
    anchoredDraggableState.updateAnchors(anchors, newTarget = newTarget)
  }

  private suspend fun awaitAnchors() {
    if (anchoredDraggableState.offset.isNaN().not()) return

    snapshotFlow { anchoredDraggableState.offset.isNaN().not() }.first { it }
  }

  private fun checkValidSnapPoints(snapPoints: List<DrawerSnapPoint>) {
    check(snapPoints.isNotEmpty()) {
      "Tried to create a drawer without any snapPoints. Make sure to pass at least one " +
        "snapPoint when creating the drawer's state."
    }

    val duplicates =
      snapPoints.groupBy { it.identifier }.filter { it.value.size > 1 }.map { it.key }
    check(duplicates.isEmpty()) {
      "SnapPoint identifiers need to be unique, but you passed the following snapPoints " +
        "multiple times: ${duplicates.joinToString { it }}."
    }
  }
}

class DrawerScope internal constructor(
  internal val drawerState: DrawerState,
  internal val enabled: Boolean,
)

class DrawerViewportScope internal constructor()

class DrawerPanelScope internal constructor()

private class DrawerContext(
  internal val state: DrawerState? = null,
  enabled: Boolean = true,
  internal val interactionSource: MutableInteractionSource? = null,
) {
  internal var enabled by mutableStateOf(enabled)
}

private val LocalDrawerContext: ProvidableCompositionLocal<DrawerContext> =
  compositionLocalOf { DrawerContext() }

@Composable
fun UnstyledDrawer(
  state: DrawerState,
  modifier: Modifier = Modifier,
  position: DrawerPosition = DrawerPosition.Bottom,
  enabled: Boolean = true,
  content: @Composable DrawerScope.() -> Unit,
) {
  Box(modifier) {
    val drawerScope = remember(state, position, enabled) {
      DrawerScope(
        drawerState = state,
        enabled = enabled,
      )
    }
    drawerScope.content()
  }
}

@Composable
fun DrawerScope.Viewport(
  modifier: Modifier = Modifier,
  content: @Composable DrawerViewportScope.() -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val context = remember(drawerState, interactionSource) {
    DrawerContext(
      state = drawerState,
      enabled = enabled,
      interactionSource = interactionSource,
    )
  }
  SideEffect {
    context.enabled = enabled
  }
  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect { interaction ->
      when (interaction) {
        is androidx.compose.foundation.interaction.DragInteraction.Start ->
          drawerState.isDragging = true
        is androidx.compose.foundation.interaction.DragInteraction.Stop,
        is androidx.compose.foundation.interaction.DragInteraction.Cancel,
        -> drawerState.isDragging = false
      }
    }
  }

  Layout(
    modifier = modifier,
    content = {
      CompositionLocalProvider(LocalDrawerContext provides context) {
        DrawerViewportScope().content()
      }
    },
  ) { measurables, constraints ->
    val childConstraints = constraints.copy(
      minWidth = 0,
      minHeight = 0,
    )
    val placeables = measurables.map { measurable ->
      measurable.measure(childConstraints)
    }

    val width = maxOf(
      constraints.minWidth,
      placeables.maxOfOrNull { placeable -> placeable.width } ?: 0,
    ).coerceIn(constraints.minWidth, constraints.maxWidth)
    val height = maxOf(
      constraints.minHeight,
      placeables.maxOfOrNull { placeable -> placeable.height } ?: 0,
    ).coerceIn(constraints.minHeight, constraints.maxHeight)
    drawerState.updateContainerSize(height.toFloat())

    layout(width, height) {
      val panelTop = drawerState.panelTopPx(height.toFloat()).roundToInt()
      placeables.forEach { placeable ->
        placeable.placeRelative(0, panelTop)
      }
    }
  }
}

@Composable
fun DrawerViewportScope.Panel(
  modifier: Modifier = Modifier,
  content: @Composable DrawerPanelScope.() -> Unit,
) {
  val context = LocalDrawerContext.current
  val state = context.state
  Layout(
    modifier = modifier
      .then(
        buildModifier {
          if (state != null && context.enabled && state.snapPoints.size > 1) {
            add(
              Modifier.anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Vertical,
                enabled = context.enabled,
                interactionSource = context.interactionSource,
              ),
            )
          }
        },
      ),
    content = {
      DrawerPanelScope().content()
    },
  ) { measurables, constraints ->
    val contentConstraints = constraints.copy(
      minHeight = 0,
      maxHeight = Constraints.Infinity,
    )
    val placeables = measurables.map { measurable ->
      measurable.measure(contentConstraints)
    }
    val contentHeight = placeables.maxOfOrNull { it.height } ?: 0
    state?.updatePanelSize(contentHeight.toFloat())

    val visibleHeight = state?.visiblePanelHeightPx()
      ?.roundToInt()
      ?.coerceIn(constraints.minHeight, constraints.maxHeight)
      ?: contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
    val width = maxOf(
      constraints.minWidth,
      placeables.maxOfOrNull { it.width } ?: 0,
    ).coerceIn(constraints.minWidth, constraints.maxWidth)

    layout(width, visibleHeight) {
      placeables.forEach { placeable ->
        placeable.placeRelative(0, visibleHeight - placeable.height)
      }
    }
  }
}

@Composable
fun DrawerPanelScope.Content(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Box(modifier) {
    content()
  }
}
