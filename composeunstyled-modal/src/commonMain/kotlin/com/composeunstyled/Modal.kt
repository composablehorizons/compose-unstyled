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
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.flow.first

@Stable
class ModalState(initiallyVisible: Boolean = false) {
  val transitionState = MutableTransitionState(initiallyVisible)
  internal var mountedFragments by mutableIntStateOf(0)
  val hasMountedFragments: Boolean
    get() = mountedFragments > 0
  internal var attachedToWindow by mutableStateOf(false)
  val isAttachedToWindow: Boolean
    get() = attachedToWindow

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

private val ModalPortalTarget = PortalTarget()

private class HostedModalEntry {
  val focusRequester = FocusRequester()
  val boundaryFocusRequester = FocusRequester()
  var wrappingFocus = false
  var hasFocus by mutableStateOf(false)
  var fallbackFocused by mutableStateOf(false)
}

private class ModalHostState {
  val entries = mutableStateListOf<HostedModalEntry>()
  val backgroundFocusRequester = FocusRequester()

  fun register(entry: HostedModalEntry) {
    (entries.lastOrNull()?.focusRequester ?: backgroundFocusRequester).saveFocusedChild()
    entries.add(entry)
  }
}

private val LocalModalHostState = staticCompositionLocalOf<ModalHostState?> { null }

@Composable
fun ModalHost(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val hostState = remember { ModalHostState() }
  LaunchedEffect(hostState.entries.isEmpty()) {
    if (hostState.entries.isEmpty()) hostState.backgroundFocusRequester.restoreFocusedChild()
  }
  CompositionLocalProvider(LocalModalHostState provides hostState) {
    PortalHost(
      modifier = modifier.focusRequester(hostState.backgroundFocusRequester).focusGroup(),
      key = ModalPortalTarget,
      content = content,
    )
  }
}

interface ModalScope

internal object ModalScopeInstance : ModalScope

@Composable
fun Modal(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean = { false },
  content: @Composable ModalScope.() -> Unit,
) {
  if (
    state.transitionState.targetState.not() &&
    state.mountedFragments == 0
  ) {
    return
  }

  if (LocalModalHostState.current != null) {
    PortalModal(
      state = state,
      onKeyEvent = onKeyEvent,
      content = content,
    )
  } else {
    PlatformModal(
      state = state,
      onKeyEvent = onKeyEvent,
      content = content,
    )
  }
}

@Composable
internal expect fun PlatformModal(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean,
  content: @Composable ModalScope.() -> Unit,
)

@Composable
internal fun PortalModal(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean,
  content: @Composable ModalScope.() -> Unit,
) {
  Portal(target = ModalPortalTarget) {
    ModalContent(state = state, onKeyEvent = onKeyEvent, content = content)
  }
}

@Composable
internal fun ModalContent(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean,
  content: @Composable ModalScope.() -> Unit,
) {
  val hostState = requireNotNull(LocalModalHostState.current)
  val entry = remember { HostedModalEntry() }
  val focusManager = LocalFocusManager.current
  val isTopmost = hostState.entries.lastOrNull() === entry

  fun focusContent(direction: FocusDirection = FocusDirection.Next) {
    entry.wrappingFocus = true
    try {
      if (entry.boundaryFocusRequester.requestFocus()) focusManager.moveFocus(direction)
    } finally {
      entry.wrappingFocus = false
    }
  }

  DisposableEffect(hostState, entry) {
    hostState.register(entry)
    onDispose { hostState.entries.remove(entry) }
  }
  LaunchedEffect(isTopmost, entry.hasFocus) {
    if (isTopmost && entry.hasFocus.not()) {
      state.awaitAttachedToWindow()
      if (
        (entry.hasFocus.not() || entry.fallbackFocused) &&
        entry.focusRequester.restoreFocusedChild().not()
      ) {
        focusContent()
      }
    }
  }

  Box(
    Modifier
      .fillMaxSize()
      .focusRequester(entry.boundaryFocusRequester)
      .focusProperties { canFocus = entry.wrappingFocus }
      .focusTarget()
      .focusRequester(entry.focusRequester)
      .onFocusChanged { entry.hasFocus = it.hasFocus }
      .focusProperties {
        onEnter = {
          if (hostState.entries.lastOrNull() !== entry) cancelFocusChange()
        }
        onExit = {
          if (hostState.entries.lastOrNull() === entry && entry.wrappingFocus.not()) {
            if (
              requestedFocusDirection == FocusDirection.Next ||
              requestedFocusDirection == FocusDirection.Previous
            ) {
              focusContent(requestedFocusDirection)
            }
            cancelFocusChange()
          }
        }
      }
      .focusGroup()
      .onKeyEvent(onKeyEvent)
      .semantics {
        dialog()
        if (isTopmost.not()) hideFromAccessibility()
      },
  ) {
    Box(
      Modifier.matchParentSize().pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            awaitPointerEvent().changes.forEach { it.consume() }
          }
        }
      },
    )
    CompositionLocalProvider(LocalModalState provides state) {
      ModalScopeContent(state = state, content = content)
    }
    Box(
      Modifier
        .onFocusChanged { entry.fallbackFocused = it.isFocused }
        .focusable(enabled = isTopmost && (entry.hasFocus.not() || entry.fallbackFocused)),
    )
  }
}

@Composable
internal fun ModalScopeContent(
  state: ModalState,
  content: @Composable ModalScope.() -> Unit,
) {
  DisposableEffect(state) {
    state.attachedToWindow = true
    onDispose {
      state.attachedToWindow = false
    }
  }
  if (state.attachedToWindow || LocalInspectionMode.current) {
    ModalScopeInstance.content()
  }
}

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
