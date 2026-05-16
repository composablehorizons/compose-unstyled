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

import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.flow.first

@Stable
class ModalState(initiallyVisible: Boolean = false) {
  val transitionState = MutableTransitionState(initiallyVisible)
  internal var mountedFragments by mutableIntStateOf(0)
  internal var attachedToWindow by mutableStateOf(false)

  suspend fun awaitAttachedToWindow() {
    snapshotFlow { attachedToWindow }.first { it }
    withFrameNanos { }
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

interface ModalScope

internal object ModalScopeInstance : ModalScope

@Composable
expect fun Modal(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  content: @Composable ModalScope.() -> Unit,
)

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
