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

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.withoutEventHandling
import androidx.compose.foundation.withoutVisualEffect
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
import androidx.compose.ui.layout.Placeable
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
import kotlin.jvm.JvmInline
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
value class DrawerSide internal constructor(private val value: String) {
  companion object {
    val Top = DrawerSide("top")
    val Bottom = DrawerSide("bottom")
    val Start = DrawerSide("start")
    val End = DrawerSide("end")
  }

  override fun toString(): String {
    return value
  }
}

@Composable
fun rememberDrawerState(
  initialSnapPoint: DrawerSnapPoint = DrawerSnapPoint.Closed,
  snapPoints: () -> List<DrawerSnapPoint> = {
    listOf(DrawerSnapPoint.Closed, DrawerSnapPoint.Open)
  },
): DrawerState {
  val density = LocalDensity.current
  val scope = rememberCoroutineScope()
  val currentSnapPoints = snapPoints()
  val state = remember(scope, density) {
    DrawerState(
      initialSnapPoint = initialSnapPoint,
      snapPoints = currentSnapPoints,
      coroutineScope = scope,
      density = density,
    )
  }
  SideEffect {
    state.updateSnapPoints(currentSnapPoints)
  }
  return state
}

class DrawerState internal constructor(
  initialSnapPoint: DrawerSnapPoint,
  snapPoints: List<DrawerSnapPoint>,
  private val coroutineScope: CoroutineScope,
  private val density: Density,
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
  internal var isAnchoredToMinEdge by mutableStateOf(false)
  internal var isDragging by mutableStateOf(false)

  internal val anchoredDraggableState = AnchoredDraggableState(initialValue = initialSnapPoint)

  private var innerSnapPoints: List<DrawerSnapPoint> by mutableStateOf(snapPoints)

  val snapPoints: List<DrawerSnapPoint>
    get() {
      return innerSnapPoints
    }

  internal fun updateSnapPoints(value: List<DrawerSnapPoint>) {
    checkValidSnapPoints(value)
    val currentOffset = anchoredDraggableState.offset
    val requestedTarget = if (currentOffset.isNaN()) {
      anchoredDraggableState.settledValue
    } else {
      targetSnapPoint
    }

    innerSnapPoints = value
    val closestTarget = if (currentOffset.isNaN().not() && value.contains(requestedTarget).not()) {
      createAnchors().closestAnchor(currentOffset) ?: value.first()
    } else {
      null
    }

    if (closestTarget == null) {
      updateAnchors()
      return
    }

    pendingTargetSnapPoint = closestTarget
    pendingSnapPointChange?.cancel()
    pendingSnapPointChange = coroutineScope.launch {
      try {
        anchoredDraggableState.anchoredDrag {
          updateAnchors(newTarget = closestTarget)
        }
        anchoredDraggableState.animateTo(closestTarget)
      } finally {
        if (pendingTargetSnapPoint == closestTarget) {
          pendingTargetSnapPoint = null
        }
      }
    }
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
    visiblePanelSizePx()
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

  internal fun updateContainerSize(
    measuredSizePx: Float,
    isAnchoredToMinEdge: Boolean,
  ) {
    if (
      containerSizePx == measuredSizePx &&
      this.isAnchoredToMinEdge == isAnchoredToMinEdge
    ) {
      return
    }

    containerSizePx = measuredSizePx
    this.isAnchoredToMinEdge = isAnchoredToMinEdge
    updateAnchors()
  }

  internal fun updatePanelSize(measuredSizePx: Float) {
    if (panelSizePx == measuredSizePx) return

    panelSizePx = measuredSizePx
    updateAnchors()
  }

  internal fun visiblePanelSizePx(): Float {
    if (containerSizePx.isNaN()) return 0f
    if (anchoredDraggableState.offset.isNaN()) return 0f

    val visiblePanelSizePx = if (isAnchoredToMinEdge) {
      anchoredDraggableState.offset
    } else {
      containerSizePx - anchoredDraggableState.offset
    }
    return visiblePanelSizePx.coerceIn(0f, containerSizePx)
  }

  internal fun panelMainAxisOffsetPx(
    side: DrawerSide,
    viewportMainAxisSizePx: Float,
  ): Float {
    if (anchoredDraggableState.offset.isNaN()) {
      return if (side.isLeadingEdge) {
        0f
      } else {
        viewportMainAxisSizePx
      }
    }

    return if (side.isLeadingEdge) {
      visiblePanelSizePx() - panelSizePx
    } else {
      (viewportMainAxisSizePx - visiblePanelSizePx()).coerceIn(0f, viewportMainAxisSizePx)
    }
  }

  private fun updateAnchors(newTarget: DrawerSnapPoint? = null) {
    if (containerSizePx.isNaN() || panelSizePx.isNaN()) return

    val anchors = createAnchors()
    val requestedTarget = if (anchoredDraggableState.offset.isNaN()) {
      anchoredDraggableState.settledValue
    } else {
      targetSnapPoint
    }
    val resolvedTarget = newTarget
      ?: requestedTarget.takeIf { innerSnapPoints.contains(it) }
      ?: DrawerSnapPoint.Closed.takeIf { innerSnapPoints.contains(it) }
      ?: innerSnapPoints.first()
    anchoredDraggableState.updateAnchors(anchors, newTarget = resolvedTarget)
  }

  private fun createAnchors(): DraggableAnchors<DrawerSnapPoint> {
    return DraggableAnchors {
      with(density) {
        innerSnapPoints.forEach { snapPoint ->
          val containerSize = containerSizePx.toDp()
          val panelSize = panelSizePx.toDp()
          val snapPointSize = snapPoint.calculateVisibleSize(containerSize, panelSize)
            .takeIf { it.isSpecified }
            ?.coerceIn(0.dp, minOf(containerSize, panelSize))
            ?: 0.dp
          val offset = if (isAnchoredToMinEdge) {
            snapPointSize.toPx()
          } else {
            (containerSize - snapPointSize).toPx()
          }
          snapPoint at offset
        }
      }
    }
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
  internal val side: DrawerSide,
  internal val enabled: Boolean,
)

class DrawerViewportScope internal constructor()

class DrawerPanelScope internal constructor()

private class DrawerContext(
  internal val state: DrawerState? = null,
  internal val side: DrawerSide = DrawerSide.Bottom,
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
  side: DrawerSide = DrawerSide.Start,
  enabled: Boolean = true,
  content: @Composable DrawerScope.() -> Unit,
) {
  if (
    enabled &&
    state.snapPoints.contains(DrawerSnapPoint.Closed) &&
    (
      state.currentSnapPoint != DrawerSnapPoint.Closed ||
        state.targetSnapPoint != DrawerSnapPoint.Closed
      )
  ) {
    EscapeHandler {
      state.targetSnapPoint = DrawerSnapPoint.Closed
    }
  }

  Box(modifier) {
    val drawerScope = remember(state, side, enabled) {
      DrawerScope(
        drawerState = state,
        side = side,
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
  val context = remember(drawerState, side, interactionSource) {
    DrawerContext(
      state = drawerState,
      side = side,
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
    val side = context.side
    val containerMainAxisSizePx = if (side.isHorizontal) {
      width.toFloat()
    } else {
      height.toFloat()
    }
    drawerState.updateContainerSize(
      measuredSizePx = containerMainAxisSizePx,
      isAnchoredToMinEdge = side.isLeadingEdge,
    )

    layout(width, height) {
      val panelMainAxisOffset =
        drawerState.panelMainAxisOffsetPx(side, containerMainAxisSizePx).roundToInt()
      placeables.forEach { placeable ->
        if (side.isHorizontal) {
          val coercionOffset = placeable.mainAxisCoercionOffset(side)
          val x = if (side.isLeadingEdge) {
            panelMainAxisOffset
          } else {
            panelMainAxisOffset - coercionOffset
          }
          placeable.placeRelative(x, 0)
        } else {
          val coercionOffset = placeable.mainAxisCoercionOffset(side)
          val y = if (side.isLeadingEdge) {
            panelMainAxisOffset
          } else {
            panelMainAxisOffset - coercionOffset
          }
          placeable.placeRelative(0, y)
        }
      }
    }
  }
}

@Composable
fun DrawerViewportScope.Panel(
  modifier: Modifier = Modifier,
  overscrollEffect: OverscrollEffect? = null,
  content: @Composable DrawerPanelScope.() -> Unit,
) {
  val context = LocalDrawerContext.current
  val state = context.state
  val side = context.side
  val orientation = if (side.isHorizontal) {
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
  Layout(
    modifier = buildModifier {
      if (panelOverscrollVisualEffect != null) {
        add(Modifier.overscroll(panelOverscrollVisualEffect))
      }
    }.then(
      buildModifier {
        if (state != null && context.enabled && state.snapPoints.size > 1) {
          add(
            Modifier.anchoredDraggable(
              state = state.anchoredDraggableState,
              orientation = orientation,
              enabled = context.enabled,
              interactionSource = context.interactionSource,
              overscrollEffect = panelOverscrollEffect,
            ),
          )
        }
      },
    ),
    content = {
      Box(modifier) {
        DrawerPanelScope().content()
      }
    },
  ) { measurables, constraints ->
    val containerMainAxisSize = state?.containerSizePx
      ?.takeIf { it.isNaN().not() }
      ?.roundToInt()
    val canUseContainerMax = containerMainAxisSize != null &&
      state.panelSizePx.isNaN().not() &&
      state.panelSizePx <= containerMainAxisSize
    val contentConstraints = if (side.isHorizontal) {
      val maxWidth = constraints.maxWidth.takeIf { maxWidth ->
        maxWidth != Constraints.Infinity &&
          containerMainAxisSize != null &&
          maxWidth < containerMainAxisSize
      } ?: containerMainAxisSize.takeIf { canUseContainerMax }
        ?: Constraints.Infinity
      constraints.copy(minWidth = 0, maxWidth = maxWidth)
    } else {
      val maxHeight = constraints.maxHeight.takeIf { maxHeight ->
        maxHeight != Constraints.Infinity &&
          containerMainAxisSize != null &&
          maxHeight < containerMainAxisSize
      } ?: containerMainAxisSize.takeIf { canUseContainerMax }
        ?: Constraints.Infinity
      constraints.copy(minHeight = 0, maxHeight = maxHeight)
    }
    val placeables = measurables.map { measurable ->
      measurable.measure(contentConstraints)
    }
    val panelMainAxisSize = placeables.maxOfOrNull { placeable ->
      placeable.mainAxisSize(side)
    } ?: 0
    state?.updatePanelSize(panelMainAxisSize.toFloat())

    val width = if (side.isHorizontal) {
      panelMainAxisSize
    } else {
      maxOf(
        constraints.minWidth,
        placeables.maxOfOrNull { it.width } ?: 0,
      )
    }
    val height = if (side.isHorizontal) {
      maxOf(
        constraints.minHeight,
        placeables.maxOfOrNull { it.height } ?: 0,
      )
    } else {
      panelMainAxisSize
    }

    layout(width, height) {
      placeables.forEach { placeable ->
        val x = if (side.isHorizontal && side.isLeadingEdge.not()) {
          width - placeable.measuredWidth
        } else {
          0
        }
        val y = if (side.isHorizontal.not() && side.isLeadingEdge.not()) {
          height - placeable.height
        } else {
          0
        }
        placeable.placeRelative(x, y)
      }
    }
  }
}

private fun Placeable.mainAxisSize(side: DrawerSide): Int {
  return if (side.isHorizontal) {
    measuredWidth
  } else {
    measuredHeight
  }
}

private fun Placeable.mainAxisCoercionOffset(side: DrawerSide): Int {
  // Compose centers placeables that exceed parent constraints.
  // Trailing-edge drawers need to cancel that center offset to stay edge-aligned.
  return if (side.isHorizontal) {
    measuredWidth - width
  } else {
    measuredHeight - height
  }.coerceAtLeast(0) / 2
}

private val DrawerSide.isHorizontal: Boolean
  get() {
    return this == DrawerSide.Start || this == DrawerSide.End
  }

private val DrawerSide.isLeadingEdge: Boolean
  get() {
    return this == DrawerSide.Top || this == DrawerSide.Start
  }
