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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified

/**
 * A foundational component used to build checkboxes.
 *
 * For interactive preview & code examples, visit
 * [Checkbox Documentation](https://composeunstyled.com/checkbox).
 *
 * ## Basic Example
 *
 * ```kotlin
 * var checked by remember { mutableStateOf(false) }
 *
 * Checkbox(
 *     checked = checked,
 *     onCheckedChange = { checked = it },
 *     shape = RoundedCornerShape(4.dp),
 *     backgroundColor = Color.White,
 * ) {
 *     // will be shown if checked
 *     Icon(Check, contentDescription = null)
 * }
 * ```
 *
 * @param checked Whether the checkbox is checked.
 * @param modifier Modifier to be applied to the checkbox.
 * @param backgroundColor Background color of the checkbox.
 * @param enabled Whether the checkbox is enabled.
 * @param onCheckedChange Callback when the checked state changes.
 * @param shape Shape of the checkbox.
 * @param borderColor Color of the border.
 * @param borderWidth Width of the border.
 * @param interactionSource The interaction source for the checkbox.
 * @param indication The indication to be shown when the checkbox is interacted with.
 * @param contentDescription Accessibility description of the checkbox.
 * @param checkIcon Composable function to define the check icon.
 */
@Composable
fun UnstyledCheckbox(
  checked: Boolean,
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.Transparent,
  enabled: Boolean = true,
  onCheckedChange: ((Boolean) -> Unit)? = null,
  shape: Shape = RectangleShape,
  borderColor: Color = Color.Unspecified,
  borderWidth: Dp = 1.dp,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  contentDescription: String? = null,
  checkIcon: @Composable () -> Unit,
) {
  Box(
    modifier = modifier then buildModifier {
      if (borderColor.isSpecified && borderWidth.isSpecified) {
        add(Modifier.border(borderWidth, borderColor, shape))
      }
      add(Modifier.clip(shape).background(backgroundColor))

      if (onCheckedChange != null) {
        add(
          Modifier.toggleable(
            enabled = enabled,
            value = checked,
            interactionSource = interactionSource,
            role = Role.Checkbox,
            indication = indication,
            onValueChange = onCheckedChange,
          ),
        )
      }
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
    if (checked) {
      checkIcon()
    }
  }
}
