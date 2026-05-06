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
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun UnstyledCheckbox(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  accessibilityLabel: String? = null,
  content: @Composable UnstyledCheckboxScope.() -> Unit,
) {
  val checkboxInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
  val scope = UnstyledCheckboxScope(
    checked = checked,
    enabled = enabled,
    interactionSource = checkboxInteractionSource,
  )

  Box(
    modifier = modifier.toggleable(
      enabled = enabled,
      value = checked,
      interactionSource = checkboxInteractionSource,
      role = Role.Checkbox,
      indication = indication,
      onValueChange = onCheckedChange,
    ) then buildModifier {
      if (accessibilityLabel != null) {
        add(
          Modifier.semantics {
            this.contentDescription = accessibilityLabel
          },
        )
      }
    },
    contentAlignment = Alignment.Center,
  ) {
    scope.content()
  }
}

class UnstyledCheckboxScope internal constructor(
  internal val checked: Boolean,
  internal val enabled: Boolean,
  internal val interactionSource: MutableInteractionSource,
)

@Composable
fun UnstyledCheckboxScope.CheckedIndicator(
  modifier: Modifier = Modifier,
  indication: Indication? = null,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
  Box(
    modifier = modifier then buildModifier {
      if (indication != null) {
        add(Modifier.indication(interactionSource, indication))
      }
    },
    contentAlignment = Alignment.Center,
  ) {
    AnimatedVisibility(
      visible = checked,
      enter = enter,
      exit = exit,
      content = content,
    )
  }
}
