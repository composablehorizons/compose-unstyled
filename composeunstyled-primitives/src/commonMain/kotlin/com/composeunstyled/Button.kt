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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * An accessible clickable component used to create buttons with the styling of your choice.
 *
 * For interactive preview & code examples, visit [Button Documentation](https://composeunstyled.com/button).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Button(
 *     onClick = { /* TODO */ },
 *     backgroundColor = Color(0xFFFFFFFF),
 *     contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
 *     shape = RoundedCornerShape(12.dp),
 * ) {
 *     Text("Submit")
 * }
 * ```
 *
 * @param onClick    The callback to be invoked when the button is clicked.
 * @param modifier    Modifier to be applied to the button.
 * @param enabled    Whether the button is enabled.
 * @param shape    The shape of the button.
 * @param backgroundColor    The background color of the button.
 * @param contentPadding    Padding values for the content.
 * @param borderColor    The color of the border. Applied only if both borderColor is specified and borderWidth is > `0.dp`
 * @param borderWidth    The width of the border. Applied only if both borderColor is specified and borderWidth is > `0.dp`
 * @param role    The role of the button for accessibility purposes.
 * @param indication    The indication to be shown when the button is interacted with.
 * @param interactionSource    The interaction source for the button.
 * @param verticalAlignment The vertical alignment of the button's children.
 * @param horizontalArrangement The horizontal arrangement of the button's children.
 * @param content    A composable function that defines the content of the button.
 *
 */
@Composable
fun UnstyledButton(
  onClick: () -> Unit,
  enabled: Boolean = true,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentPadding: PaddingValues = NoPadding,
  borderColor: Color = Color.Unspecified,
  borderWidth: Dp = 0.dp,
  modifier: Modifier = Modifier,
  role: Role = Role.Button,
  indication: Indication? = LocalIndication.current,
  interactionSource: MutableInteractionSource? = null,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
  verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
  content: @Composable (RowScope.() -> Unit),
) {
  Row(
    modifier = modifier then buildModifier {
      add(
        Modifier.clip(shape)
          .background(backgroundColor)
          .clickable(
            onClick = onClick,
            role = role,
            enabled = enabled,
            indication = indication,
            interactionSource = interactionSource,
          ),
      )
      if (borderWidth > 0.dp && borderColor.isSpecified) {
        add(Modifier.border(borderWidth, borderColor, shape))
      }
      if (enabled) {
        add(Modifier.pointerHoverIcon(PointerIcon.Default))
      }
      add(Modifier.padding(contentPadding))
    },
    verticalAlignment = verticalAlignment,
    horizontalArrangement = horizontalArrangement,
  ) {
    content()
  }
}
