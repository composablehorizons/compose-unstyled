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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val DoNothing: () -> Unit = {}

data class ModalSheetProperties(
  val dismissOnBackPress: Boolean = true,
  val dismissOnClickOutside: Boolean = true,
)

@Composable
fun rememberModalBottomSheetState(
  initialDetent: SheetDetent,
  detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
  animationSpec: AnimationSpec<Float> = tween(),
  dismissAnimationSpec: AnimationSpec<Float>? = null,
  velocityThreshold: () -> Dp = { 125.dp },
  positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
  confirmDetentChange: (SheetDetent) -> Boolean = { true },
  decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
): ModalBottomSheetState {
  val sheetState = rememberBottomSheetState(
    initialDetent = initialDetent,
    detents = detents,
    animationSpec = animationSpec,
    velocityThreshold = velocityThreshold,
    positionalThreshold = positionalThreshold,
    decayAnimationSpec = decayAnimationSpec,
    confirmDetentChange = confirmDetentChange,
  )
  val modalState = rememberModalState(initiallyVisible = initialDetent != SheetDetent.Hidden)
  val scope = rememberCoroutineScope()
  val state = remember(sheetState, modalState, scope) {
    ModalBottomSheetState(
      bottomSheetState = sheetState,
      modalState = modalState,
      scope = scope,
      dismissAnimationSpec = dismissAnimationSpec,
    )
  }
  SideEffect {
    state.dismissAnimationSpec = dismissAnimationSpec
  }
  return state
}

class ModalBottomSheetState internal constructor(
  internal val bottomSheetState: BottomSheetState,
  internal val modalState: ModalState,
  private val scope: CoroutineScope,
  dismissAnimationSpec: AnimationSpec<Float>? = null,
) {
  internal var pendingDetentChange: Job? = null
  internal var modalDetent by mutableStateOf(bottomSheetState.currentDetent)
  internal var dismissAnimationSpec by mutableStateOf(dismissAnimationSpec)
  internal var dismissAnimationInProgress by mutableStateOf(false)
  private var pendingTargetDetent: SheetDetent? by mutableStateOf(null)

  val currentDetent: SheetDetent
    get() {
      return modalDetent
    }
  var targetDetent: SheetDetent
    get() = pendingTargetDetent ?: bottomSheetState.targetDetent
    set(value) {
      scope.launch {
        animateTo(value)
      }
    }

  val isIdle: Boolean
    get() = pendingTargetDetent == null && bottomSheetState.isIdle

  fun progress(from: SheetDetent, to: SheetDetent): Float {
    return bottomSheetState.progress(from, to)
  }

  val offset: Float by derivedStateOf {
    bottomSheetState.offset
  }

  suspend fun animateTo(value: SheetDetent) {
    pendingTargetDetent = value

    val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden ||
      bottomSheetState.targetDetent != SheetDetent.Hidden

    try {
      if (isBottomSheetVisible) {
        bottomSheetState.animateTo(value)
        if (value == SheetDetent.Hidden && bottomSheetState.currentDetent == SheetDetent.Hidden) {
          modalDetent = SheetDetent.Hidden
          modalState.transitionState.targetState = false
        }
      } else {
        modalDetent = value
        if (value == SheetDetent.Hidden) {
          bottomSheetState.animateTo(value)
          modalState.transitionState.targetState = false
          return
        }
        modalState.transitionState.targetState = true
        pendingDetentChange?.cancel()
        pendingDetentChange = scope.launch {
          awaitModalAndSheetAnchors()
          bottomSheetState.animateTo(value)
        }
        pendingDetentChange?.join()
      }
    } finally {
      if (pendingTargetDetent == value) {
        pendingTargetDetent = null
      }
    }
  }

  fun jumpTo(value: SheetDetent) {
    val isBottomSheetVisible =
      bottomSheetState.currentDetent != SheetDetent.Hidden || bottomSheetState.targetDetent != SheetDetent.Hidden

    modalDetent = value

    if (isBottomSheetVisible) {
      bottomSheetState.jumpTo(value)
      if (value == SheetDetent.Hidden) {
        modalState.transitionState.targetState = false
      }
      return
    }

    pendingDetentChange?.cancel()
    pendingDetentChange = scope.launch {
      if (value == SheetDetent.Hidden) {
        modalState.transitionState.targetState = false
      } else {
        modalState.transitionState.targetState = true
      }
      if (value != SheetDetent.Hidden) {
        awaitModalAndSheetAnchors()
      }
      bottomSheetState.jumpTo(value)
    }
  }

  private suspend fun awaitModalAndSheetAnchors() {
    modalState.awaitAttachedToWindow()

    // wait until the anchored state is used and is measured
    snapshotFlow { bottomSheetState.anchoredDraggableState.offset.isNaN().not() }.filter { it }
      .first()
  }

  fun invalidateDetents() {
    bottomSheetState.invalidateDetents()
  }

  internal fun launchPendingDetentChange(block: suspend () -> Unit) {
    pendingDetentChange = scope.launch {
      block()
    }
  }
}

@Composable
fun UnstyledModalBottomSheet(
  state: ModalBottomSheetState,
  properties: ModalSheetProperties = ModalSheetProperties(),
  onDismiss: () -> Unit = DoNothing,
  overlay: (@Composable () -> Unit)? = null,
  content: @Composable () -> Unit,
) {
  val currentDismissCallback by rememberUpdatedState(onDismiss)
  var dismissRequested by remember { mutableStateOf(false) }

  fun dispatchDismiss() {
    if (dismissRequested.not()) {
      dismissRequested = true
      currentDismissCallback()
    }
  }

  fun completeDismiss(notifyDismiss: Boolean = true) {
    if (notifyDismiss) {
      dispatchDismiss()
    }
    state.modalDetent = SheetDetent.Hidden
    state.modalState.transitionState.targetState = false
  }

  fun requestDismiss() {
    dispatchDismiss()
    state.dismissAnimationInProgress = true
    state.pendingDetentChange?.cancel()
    state.launchPendingDetentChange {
      try {
        state.bottomSheetState.animateTo(SheetDetent.Hidden, state.dismissAnimationSpec)
        completeDismiss(notifyDismiss = false)
      } finally {
        state.dismissAnimationInProgress = false
      }
    }
  }

  Modal(state = state.modalState) {
    var hasBeenShown by remember { mutableStateOf(false) }
    if (hasBeenShown.not()) {
      LaunchedEffect(state.offset > 0f) {
        hasBeenShown = true
      }
    } else {
      LaunchedEffect(
        state.bottomSheetState.isIdle,
        state.bottomSheetState.currentDetent,
        state.dismissAnimationInProgress,
      ) {
        if (
          state.dismissAnimationInProgress.not() &&
          state.bottomSheetState.isIdle &&
          state.bottomSheetState.currentDetent == SheetDetent.Hidden
        ) {
          completeDismiss()
        }
      }
    }
    LaunchedEffect(state.bottomSheetState.currentDetent) {
      if (state.bottomSheetState.currentDetent != SheetDetent.Hidden) {
        dismissRequested = false
      }
    }
    if (properties.dismissOnBackPress) {
      EscapeHandler {
        if (state.bottomSheetState.confirmDetentChange(SheetDetent.Hidden)) {
          requestDismiss()
        }
      }
    }

    Box(
      modifier = Modifier.fillMaxSize() then buildModifier {
        if (properties.dismissOnClickOutside) {
          add(
            Modifier.pointerInput(Unit) {
              detectTapGestures {
                if (state.bottomSheetState.confirmDetentChange(SheetDetent.Hidden)) {
                  requestDismiss()
                }
              }
            },
          )
        }
      },
    ) {
      overlay?.invoke()
      UnstyledBottomSheet(
        state = state.bottomSheetState,
        modifier = Modifier.fillMaxSize(),
      ) {
        content()
      }
    }
  }
}
