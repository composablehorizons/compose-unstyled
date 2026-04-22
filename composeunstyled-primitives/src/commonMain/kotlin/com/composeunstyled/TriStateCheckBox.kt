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
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

@Composable
@Deprecated(
  "This will go to 2.0. Use the version with the Unstyled- prefix instead",
  ReplaceWith(
    "UnstyledTriStateCheckbox(value, modifier, backgroundColor, contentColor, enabled, onClick, shape, borderColor, borderWidth, interactionSource, indication, contentDescription, checkIcon)",
  ),
)
fun TriStateCheckbox(
  value: ToggleableState,
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.Transparent,
  contentColor: Color = LocalContentColor.current,
  enabled: Boolean = true,
  onClick: () -> Unit,
  shape: Shape = RectangleShape,
  borderColor: Color = Color.Unspecified,
  borderWidth: Dp = 1.dp,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  contentDescription: String? = null,
  checkIcon: @Composable (ToggleableState) -> Unit,
) {
  UnstyledTriStateCheckbox(
    value = value,
    modifier = modifier,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    enabled = enabled,
    onClick = onClick,
    shape = shape,
    borderColor = borderColor,
    borderWidth = borderWidth,
    interactionSource = interactionSource,
    indication = indication,
    contentDescription = contentDescription,
    checkIcon = checkIcon,
  )
}

@Composable
fun UnstyledTriStateCheckbox(
  value: ToggleableState,
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.Transparent,
  contentColor: Color = LocalContentColor.current,
  enabled: Boolean = true,
  onClick: () -> Unit,
  shape: Shape = RectangleShape,
  borderColor: Color = Color.Unspecified,
  borderWidth: Dp = 1.dp,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  contentDescription: String? = null,
  checkIcon: @Composable (ToggleableState) -> Unit,
) {
  Box(
    modifier = modifier then buildModifier {
      if (borderColor.isSpecified && borderWidth.isSpecified) {
        add(Modifier.border(borderWidth, borderColor, shape))
      }
      add(Modifier.clip(shape).background(backgroundColor))

      add(
        Modifier.triStateToggleable(
          enabled = enabled,
          state = value,
          interactionSource = interactionSource,
          role = Role.Checkbox,
          indication = indication,
          onClick = onClick,
        ),
      )

      if (contentDescription != null) {
        add(
          Modifier.semantics {
            this.contentDescription = contentDescription
          },
        )
      }
    },
    contentAlignment = Alignment.Center,
  ) {
    CompositionLocalProvider(LocalContentColor provides contentColor) {
      checkIcon(value)
    }
  }
}
