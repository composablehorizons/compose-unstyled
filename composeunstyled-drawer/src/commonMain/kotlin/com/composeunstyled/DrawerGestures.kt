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
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.JvmName

internal fun <T : Any> Modifier.closedEdgeSwipe(
  drawerState: UnstyledDrawerState<T>,
  resolvedPlacement: ResolvedDrawerPlacement,
  gesturesEnabled: Boolean,
): Modifier {
  return pointerInput(drawerState, resolvedPlacement, gesturesEnabled) {
    awaitEachGesture {
      val down = awaitFirstDown(requireUnconsumed = false)
      if (down.type == PointerType.Mouse) {
        return@awaitEachGesture
      }
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
      try {
        drag(down.id) { change ->
          val delta = change.positionChange().toFloat(resolvedPlacement.orientation)
          if (accepted || resolvedPlacement.isOpeningDelta(delta)) {
            if (accepted.not()) {
              drawerState.isEdgeSwipeInProgress = true
              accepted = true
            }
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            drawerState.anchoredDraggableState.dispatchRawDelta(delta)
            change.consume()
          }
        }
      } finally {
        if (accepted) {
          try {
            drawerState.settleFromFling(
              velocityTracker.calculateVelocity().toFloat(resolvedPlacement.orientation),
            )
          } finally {
            drawerState.isEdgeSwipeInProgress = false
          }
        }
      }
    }
  }
}

@JvmName("velocityToFloat")
private fun Velocity.toFloat(orientation: Orientation): Float {
  return if (orientation == Orientation.Horizontal) x else y
}

@JvmName("offsetToFloat")
private fun Offset.toFloat(orientation: Orientation): Float {
  return if (orientation == Orientation.Horizontal) x else y
}

internal class DrawerPanelBounds {
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

internal fun Modifier.consumeOverlayOutsideTap(
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

internal fun <T : Any> Modifier.panelSwipe(
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

private fun Float.toOffset(orientation: Orientation): Offset {
  return Offset(
    x = if (orientation == Orientation.Horizontal) this else 0f,
    y = if (orientation == Orientation.Vertical) this else 0f,
  )
}

internal class DrawerFlingBehavior<T : Any>(
  private val drawerState: UnstyledDrawerState<T>,
) : FlingBehavior {
  override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
    drawerState.settleFromFling(initialVelocity)
    return initialVelocity
  }
}

internal class DrawerNestedScrollConnection<T : Any>(
  private val drawerState: UnstyledDrawerState<T>,
  private val resolvedPlacement: ResolvedDrawerPlacement,
) : NestedScrollConnection {
  private val orientation: Orientation
    get() = resolvedPlacement.orientation

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
