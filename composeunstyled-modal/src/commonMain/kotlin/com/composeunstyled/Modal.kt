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
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.first

private val AppearInstantly: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 0))
private val DisappearInstantly: ExitTransition = fadeOut(animationSpec = tween(durationMillis = 0))

@Stable
class ModalState(initiallyVisible: Boolean = false) {
  val transitionState = MutableTransitionState(initiallyVisible)
  internal var mountedFragments by mutableIntStateOf(0)
  internal var attachedToWindow by mutableStateOf(false)

  suspend fun awaitAttachedToWindow() {
    snapshotFlow { attachedToWindow }.first { it }
  }
}

private val ModalStateSaver = run {
  mapSaver(
    save = { mapOf("visible" to it.transitionState.targetState) },
    restore = { ModalState(initiallyVisible = it["visible"] as Boolean) },
  )
}

@Composable
fun rememberModalState(initiallyVisible: Boolean = false): ModalState {
  return rememberSaveable(saver = ModalStateSaver) {
    ModalState(initiallyVisible = initiallyVisible)
  }
}

val LocalModalState = staticCompositionLocalOf<ModalState> {
  ModalState()
}

/**
 * Modals are the building blocks for components such as dialogs, alerts and modal bottom sheets.
 *
 * They create their own window and block interaction with the rest of the interface until removed from the composition.
 *
 * @param state Controls modal visibility and lifecycle.
 * @param onKeyEvent Allows the caller to intercept [KeyEvent]s happening within the contents of the modal. Return `true` if you handled the event.
 * @param content The content of the modal
 */
@Composable
expect fun Modal(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  content: @Composable () -> Unit,
)

@Composable
fun UnstyledScrim(
  modifier: Modifier = Modifier,
  scrimColor: Color = Color.Black.copy(alpha = 0.6f),
  enter: EnterTransition = AppearInstantly,
  exit: ExitTransition = DisappearInstantly,
) {
  val state = LocalModalState.current
  AnimatedVisibility(
    visibleState = state.transitionState,
    enter = enter,
    exit = exit,
  ) {
    Box(modifier.modalFragment().fillMaxSize().background(scrimColor))
  }
}

/**
 * Marks the composable as a participant in the parent [Modal] lifecycle.
 *
 * As soon as all fragments are removed from the composition, the [Modal] will automatically remove itself from composition.
 */
@Composable
fun Modifier.modalFragment(): Modifier {
  val state = LocalModalState.current
  DisposableEffect(state) {
    state.mountedFragments += 1
    onDispose {
      state.mountedFragments -= 1
    }
  }
  return this
}
