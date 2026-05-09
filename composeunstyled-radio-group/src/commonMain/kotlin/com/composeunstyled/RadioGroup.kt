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
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val KeyEvent.isKeyDown: Boolean
  get() = type == KeyEventType.KeyDown

private val LocalInnerRadioGroupState = staticCompositionLocalOf { InnerRadioGroupState() }

@Composable
fun <T> UnstyledRadioGroup(
  value: T?,
  onValueChange: (T) -> Unit,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  val focusManager = LocalFocusManager.current
  val state = remember { InnerRadioGroupState() }

  SideEffect {
    state.value = value
    state.onValueChange = { nextValue ->
      @Suppress("UNCHECKED_CAST")
      onValueChange(nextValue as T)
    }
  }

  Column(
    modifier
      .selectableGroup()
      .semantics {
        if (contentDescription != null) {
          this.contentDescription = contentDescription
        }
      }
      .onKeyEvent { event ->
        when (event.key) {
          Key.DirectionDown, Key.DirectionRight -> {
            if (event.isKeyDown) {
              focusManager.moveFocus(FocusDirection.Next)
            }
            true
          }

          Key.DirectionUp, Key.DirectionLeft -> {
            if (event.isKeyDown) {
              focusManager.moveFocus(FocusDirection.Previous)
            }
            true
          }

          else -> false
        }
      },
  ) {
    CompositionLocalProvider(LocalInnerRadioGroupState provides state) {
      content()
    }
  }
}

private class InnerRadioGroupState {
  var value by mutableStateOf<Any?>(null)
  var onValueChange by mutableStateOf<(Any?) -> Unit>({})
}

@Composable
fun <T> UnstyledRadioButton(
  value: T,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  content: @Composable RadioButtonScope.() -> Unit,
) {
  val state = LocalInnerRadioGroupState.current
  val selected = state.value == value
  val radioInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val scope = RadioButtonScope(
    selected = selected,
    enabled = enabled,
    interactionSource = radioInteractionSource,
  )

  Box(
    modifier = modifier
      .toggleable(
        value = selected,
        onValueChange = { selected ->
          if (selected) {
            state.onValueChange(value)
          }
        },
        role = Role.RadioButton,
        enabled = enabled,
        indication = indication,
        interactionSource = radioInteractionSource,
      )
      .semantics(mergeDescendants = true) { },
    contentAlignment = Alignment.Center,
  ) {
    scope.content()
  }
}

class RadioButtonScope internal constructor(
  internal val selected: Boolean,
  internal val enabled: Boolean,
  internal val interactionSource: MutableInteractionSource,
)

@Composable
fun RadioButtonScope.SelectedIndicator(
  modifier: Modifier = Modifier,
  indication: Indication? = null,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
  Box(
    modifier = if (indication != null) {
      modifier.indication(interactionSource, indication)
    } else {
      modifier
    },
    contentAlignment = Alignment.Center,
  ) {
    AnimatedVisibility(
      visible = selected,
      enter = enter,
      exit = exit,
      content = content,
    )
  }
}
