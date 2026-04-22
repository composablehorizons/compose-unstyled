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

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

private val LocalInnerRadioGroupState = staticCompositionLocalOf { InnerRadioGroupState() }

@Composable
@Deprecated(
  "This will go to 2.0. Use the version with the Unstyled- prefix instead",
  ReplaceWith("UnstyledRadioGroup(value, onValueChange, contentDescription, modifier, content)"),
)
fun RadioGroup(
  value: String?,
  onValueChange: (String) -> Unit,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  UnstyledRadioGroup(value, onValueChange, contentDescription, modifier, content)
}

@Composable
fun UnstyledRadioGroup(
  value: String?,
  onValueChange: (String) -> Unit,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  val focusManager = LocalFocusManager.current
  val state = remember { InnerRadioGroupState() }

  SideEffect {
    state.value = value
    state.onValueChange = onValueChange
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
  var value by mutableStateOf<String?>(null)
  var onValueChange by mutableStateOf<(String) -> Unit>({})
}

@Composable
@Deprecated(
  "This will go to 2.0. Use the version with the Unstyled- prefix instead",
  ReplaceWith(
    "UnstyledRadioButton(value, modifier, shape, backgroundColor, contentColor, selectedColor, enabled, contentPadding, interactionSource, indication, horizontalArrangement, verticalAlignment, content)",
  ),
)
fun RadioButton(
  value: String,
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentColor: Color = Color.Unspecified,
  selectedColor: Color = Color.Unspecified,
  enabled: Boolean = true,
  contentPadding: PaddingValues = NoPadding,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  content: @Composable (RowScope.() -> Unit),
) {
  UnstyledRadioButton(
    value = value,
    modifier = modifier,
    shape = shape,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    selectedColor = selectedColor,
    enabled = enabled,
    contentPadding = contentPadding,
    interactionSource = interactionSource,
    indication = indication,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment,
    content = content,
  )
}

@Composable
fun UnstyledRadioButton(
  value: String,
  modifier: Modifier = Modifier,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentColor: Color = Color.Unspecified,
  selectedColor: Color = Color.Unspecified,
  enabled: Boolean = true,
  contentPadding: PaddingValues = NoPadding,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  verticalAlignment: Alignment.Vertical = Alignment.Top,
  content: @Composable (RowScope.() -> Unit),
) {
  val state = LocalInnerRadioGroupState.current
  val selected = state.value == value

  Row(
    modifier = modifier
      .semantics(mergeDescendants = true) { }
      .clip(shape)
      .background(backgroundColor)
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
        interactionSource = interactionSource,
      )
      .padding(contentPadding),
    verticalAlignment = verticalAlignment,
    horizontalArrangement = horizontalArrangement,
  ) {
    CompositionLocalProvider(
      LocalContentColor provides if (selected) selectedColor else contentColor,
    ) {
      this@Row.content()
    }
  }
}
