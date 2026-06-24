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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics

data class DialogProperties(
  val dismissOnBackPress: Boolean = true,
  val dismissOnClickOutside: Boolean = true,
)

interface DialogScope

private object DialogScopeInstance : DialogScope

interface DialogOverlayScope

private object DialogOverlayScopeInstance : DialogOverlayScope

@Composable
fun DialogHost(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  ModalHost(modifier = modifier, content = content)
}

@Composable
fun DialogOverlayScope.Scrim(
  modifier: Modifier = Modifier,
  scrimColor: Color = Color.Black.copy(alpha = 0.6f),
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
) {
  val state = LocalModalState.current
  AnimatedVisibility(
    visible = state.transitionState.targetState,
    enter = enter,
    exit = exit,
  ) {
    Box(modifier.modalFragment().fillMaxSize().background(scrimColor))
  }
}

@Composable
fun UnstyledDialog(
  visible: Boolean,
  onDismissRequest: () -> Unit,
  properties: DialogProperties = DialogProperties(),
  overlay: (@Composable DialogOverlayScope.() -> Unit)? = null,
  content: @Composable DialogScope.() -> Unit,
) {
  val modalState = rememberModalState(initiallyVisible = visible)
  val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

  SideEffect {
    modalState.transitionState.targetState = visible
  }

  fun dismiss() {
    currentOnDismissRequest()
  }

  val onKeyEvent = if (properties.dismissOnBackPress) {
    { event: KeyEvent ->
      if (
        event.type == KeyEventType.KeyDown &&
        (event.key == Key.Back || event.key == Key.Escape)
      ) {
        dismiss()
        true
      } else {
        false
      }
    }
  } else {
    { false }
  }

  Modal(
    state = modalState,
    onKeyEvent = onKeyEvent,
  ) {
    Box(
      modifier = Modifier.fillMaxSize() then buildModifier {
        if (properties.dismissOnClickOutside) {
          add(
            Modifier.pointerInput(Unit) {
              detectTapGestures {
                dismiss()
              }
            },
          )
        }
      },
    ) {
      overlay?.invoke(DialogOverlayScopeInstance)
      DialogScopeInstance.content()
    }
  }
}

@Composable
fun DialogScope.DialogPanel(
  modifier: Modifier = Modifier,
  paneTitle: String? = null,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable () -> Unit,
) {
  val modalState = LocalModalState.current
  val panelFocusRequester = remember { FocusRequester() }

  AnimatedVisibility(
    visible = modalState.transitionState.targetState,
    enter = enter,
    exit = exit,
  ) {
    LaunchedEffect(Unit) {
      panelFocusRequester.requestFocus()
    }
    Box(
      modifier = modifier.modalFragment() then buildModifier {
        if (paneTitle != null) {
          add(
            Modifier.semantics {
              this.paneTitle = paneTitle
            },
          )
        }
        add(
          Modifier
            .focusRequester(panelFocusRequester)
            .pointerInput(Unit) { detectTapGestures { } },
        )
      },
    ) {
      content()
    }
  }
}
