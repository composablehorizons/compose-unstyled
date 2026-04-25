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
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput

data class DialogProperties(
  val dismissOnBackPress: Boolean = true,
  val dismissOnClickOutside: Boolean = true,
)

@Stable
class DialogState(initiallyVisible: Boolean = false) {

  internal val panelVisibilityState = MutableTransitionState(initiallyVisible)
  internal val scrimVisibilityState = MutableTransitionState(initiallyVisible)
  internal var mountedPanels by mutableIntStateOf(0)
  internal var mountedScrims by mutableIntStateOf(0)

  private var innerVisible by mutableStateOf(initiallyVisible)

  var visible: Boolean = innerVisible
    set(value) {
      innerVisible = value
      if (value.not()) {
        panelVisibilityState.targetState = false
        scrimVisibilityState.targetState = false
      }
      field = value
    }
    get() = innerVisible
}

internal val LocalDialogState = staticCompositionLocalOf { DialogState() }

private val DialogStateSaver = run {
  mapSaver(
    save = {
      mapOf("visible" to it.visible)
    },
    restore = {
      DialogState(initiallyVisible = it["visible"] as Boolean)
    },
  )
}

@Composable
fun rememberDialogState(initiallyVisible: Boolean = false): DialogState {
  return rememberSaveable(saver = DialogStateSaver) { DialogState(initiallyVisible) }
}

@Composable
fun UnstyledDialog(
  state: DialogState,
  properties: DialogProperties = DialogProperties(),
  onDismiss: () -> Unit = DoNothing,
  content: @Composable (() -> Unit),
) {
  val currentDismiss by rememberUpdatedState(onDismiss)

  CompositionLocalProvider(LocalDialogState provides state) {
    val isAnimatingPanel = state.mountedPanels > 0 && state.panelVisibilityState.isIdle.not()
    val isAnimatingScrim = state.mountedScrims > 0 && state.scrimVisibilityState.isIdle.not()
    val addModal = state.visible || isAnimatingScrim || isAnimatingPanel

    if (addModal) {
      val onKeyEvent = if (properties.dismissOnBackPress) {
        { event: KeyEvent ->
          if (
            event.type == KeyEventType.KeyDown &&
            (event.key == Key.Back || event.key == Key.Escape)
          ) {
            currentDismiss()
            state.visible = false
            true
          } else {
            false
          }
        }
      } else {
        { false }
      }
      Modal(onKeyEvent = onKeyEvent) {
        LaunchedEffect(Unit) {
          state.panelVisibilityState.targetState = true
          state.scrimVisibilityState.targetState = true
        }
        Box(
          modifier = Modifier
            .fillMaxSize()
            .then(
              if (properties.dismissOnClickOutside) {
                Modifier.pointerInput(Unit) {
                  detectTapGestures {
                    currentDismiss()
                    state.visible = false
                  }
                }
              } else {
                Modifier
              },
            ),
          contentAlignment = Alignment.Center,
        ) {
          content()
        }
      }
    }
  }
}

@Composable
fun UnstyledDialogPanel(
  modifier: Modifier = Modifier,
  enter: EnterTransition = AppearInstantly,
  exit: ExitTransition = DisappearInstantly,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentPadding: PaddingValues = NoPadding,
  content: @Composable () -> Unit,
) {
  val state = LocalDialogState.current
  val panelFocusRequester = remember { FocusRequester() }
  DisposableEffect(state) {
    state.mountedPanels += 1
    onDispose {
      state.mountedPanels -= 1
    }
  }

  AnimatedVisibility(
    visibleState = state.panelVisibilityState,
    enter = enter,
    exit = exit,
  ) {
    LaunchedEffect(Unit) {
      panelFocusRequester.requestFocus()
    }
    Box(
      modifier
        .focusRequester(panelFocusRequester)
        .clip(shape)
        .background(backgroundColor)
        .pointerInput(Unit) { detectTapGestures { } }
        .padding(contentPadding),
    ) {
        content()
    }
  }
}

@Composable
fun UnstyledScrim(
  modifier: Modifier = Modifier,
  scrimColor: Color = Color.Black.copy(0.6f),
  enter: EnterTransition = AppearInstantly,
  exit: ExitTransition = DisappearInstantly,
) {
  val state = LocalDialogState.current
  DisposableEffect(state) {
    state.mountedScrims += 1
    onDispose {
      state.mountedScrims -= 1
    }
  }

  AnimatedVisibility(
    visibleState = state.scrimVisibilityState,
    enter = enter,
    exit = exit,
  ) {
    Box(Modifier.fillMaxSize().focusable(false).background(scrimColor).then(modifier))
  }
}
