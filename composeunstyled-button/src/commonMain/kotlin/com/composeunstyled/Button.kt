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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

private val NoPadding = PaddingValues(0.dp)

@Composable
fun UnstyledButton(
  onClick: () -> Unit,
  enabled: Boolean = true,
  contentPadding: PaddingValues = NoPadding,
  modifier: Modifier = Modifier,
  role: Role = Role.Button,
  indication: Indication? = LocalIndication.current,
  interactionSource: MutableInteractionSource? = null,
  contentAlignment: Alignment = Alignment.Center,
  content: @Composable () -> Unit,
) {
  Box(
    modifier = modifier then buildModifier {
      add(
        Modifier.clickable(
          onClick = onClick,
          role = role,
          enabled = enabled,
          indication = indication,
          interactionSource = interactionSource,
        ),
      )
      if (enabled) {
        add(Modifier.pointerHoverIcon(PointerIcon.Default))
      }
      add(Modifier.padding(contentPadding))
    },
    contentAlignment = contentAlignment,
  ) {
    content()
  }
}
