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

/**
 * Properties that control the behavior of a [UnstyledModalBottomSheet].
 *
 * @param dismissOnBackPress Whether the sheet should be dismissed when the back button is pressed.
 * @param dismissOnClickOutside Whether the sheet should be dismissed when clicking outside of it.
 */
data class ModalSheetProperties(
  val dismissOnBackPress: Boolean = true,
  val dismissOnClickOutside: Boolean = true,
)

/**
 * Creates a [ModalBottomSheetState] that is remembered across compositions.
 *
 * @param initialDetent The initial detent of the sheet.
 * @param detents The list of detents that the sheet can snap to.
 * @param animationSpec The animation spec to use for animating between detents.
 * @param velocityThreshold The velocity threshold for determining whether to snap to the next detent.
 * @param positionalThreshold The positional threshold for determining whether to snap to the next detent.
 * @param confirmDetentChange Callback to confirm whether a detent change should be allowed.
 * @param decayAnimationSpec The animation spec to use for decay animations.
 * @return A remembered [ModalBottomSheetState] instance.
 */
@Composable
fun rememberModalBottomSheetState(
  initialDetent: SheetDetent,
  detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
  animationSpec: AnimationSpec<Float> = tween(),
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
  return remember(sheetState, modalState, scope) {
    ModalBottomSheetState(
      bottomSheetState = sheetState,
      modalState = modalState,
      scope = scope,
    )
  }
}

class ModalBottomSheetState(
  internal val bottomSheetState: BottomSheetState,
  internal val modalState: ModalState,
  val scope: CoroutineScope,
) {
  internal var pendingDetentChange: Job? = null
  internal var modalDetent by mutableStateOf(bottomSheetState.currentDetent)
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

  /**
   * A 0 to 1 value representing the progress of a sheet between its [from] and the [to] detents.
   */
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
          modalState.visible = false
        }
      } else {
        modalDetent = value
        if (value == SheetDetent.Hidden) {
          bottomSheetState.animateTo(value)
          modalState.visible = false
          return
        }
        modalState.visible = true
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
        modalState.visible = false
      }
      return
    }

    pendingDetentChange?.cancel()
    pendingDetentChange = scope.launch {
      modalState.visible = value != SheetDetent.Hidden
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
}

/**
 * A foundational component used to build modal bottom sheets.
 *
 * For interactive preview & code examples, visit [Modal Bottom Sheet Documentation](https://composeunstyled.com/modalbottomsheet).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val sheetState = rememberModalBottomSheetState(
 *     initialDetent = Hidden,
 * )
 *
 * Button(onClick = { sheetState.currentDetent = FullyExpanded }) {
 *     Text("Show Sheet")
 * }
 *
 * UnstyledModalBottomSheet(state = sheetState) {
 *     Sheet(modifier = Modifier.fillMaxWidth().background(Color.White)) {
 *         Box(
 *             modifier = Modifier.fillMaxWidth().height(1200.dp),
 *             contentAlignment = Alignment.TopCenter
 *         ) {
 *             UnstyledDragIndication()
 *         }
 *     }
 * }
 * ```
 *
 * @param state The [ModalBottomSheetState] that controls the sheet.
 * @param properties The [ModalSheetProperties] that control the behavior of the sheet.
 * @param onDismiss Callback that is called right before the sheet is about to be dismissed.
 * @param overlay Optional content placed behind the modal sheet content.
 * @param content The content of the modal
 */
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

  fun onDismissRequest() {
    if (dismissRequested.not()) {
      dismissRequested = true
      currentDismissCallback()
    }
    state.modalState.visible = false
    state.modalDetent = SheetDetent.Hidden
    state.bottomSheetState.targetDetent = SheetDetent.Hidden
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
      ) {
        if (state.bottomSheetState.isIdle && state.bottomSheetState.currentDetent == SheetDetent.Hidden) {
          onDismissRequest()
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
        onDismissRequest()
      }
    }

    Box(
      modifier = Modifier.fillMaxSize() then buildModifier {
        if (properties.dismissOnClickOutside) {
          add(
            Modifier.pointerInput(Unit) {
              detectTapGestures {
                if (state.bottomSheetState.confirmDetentChange(SheetDetent.Hidden)) {
                  onDismissRequest()
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
