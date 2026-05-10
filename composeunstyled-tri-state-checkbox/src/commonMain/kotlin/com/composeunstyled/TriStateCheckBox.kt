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

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState

@Composable
fun UnstyledTriStateCheckbox(
  value: ToggleableState,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  onClick: () -> Unit,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  accessibilityLabel: String? = null,
  content: @Composable TriStateCheckboxScope.() -> Unit,
) {
  val checkboxInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

  Box(
    modifier = modifier.triStateToggleable(
      enabled = enabled,
      state = value,
      interactionSource = checkboxInteractionSource,
      role = Role.Checkbox,
      indication = indication,
      onClick = onClick,
    ) then buildModifier {
      if (accessibilityLabel != null) {
        add(
          Modifier.semantics {
            this.contentDescription = accessibilityLabel
          },
        )
      }
    },
  ) {
    TriStateCheckboxScope(
      value = value,
      enabled = enabled,
      interactionSource = checkboxInteractionSource,
    ).content()
  }
}

class TriStateCheckboxScope internal constructor(
  internal val value: ToggleableState,
  internal val enabled: Boolean,
  internal val interactionSource: MutableInteractionSource,
)

@Composable
fun TriStateCheckboxScope.StateIndicator(
  modifier: Modifier = Modifier,
  indication: Indication? = null,
  content: @Composable (ToggleableState) -> Unit,
) {
  Box(
    modifier = modifier then buildModifier {
      if (indication != null) {
        add(Modifier.indication(interactionSource, indication))
      }
    },
  ) {
    content(value)
  }
}
