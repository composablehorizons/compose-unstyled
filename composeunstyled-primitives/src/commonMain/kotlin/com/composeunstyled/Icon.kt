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

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A foundational component used to display icons.
 *
 * For interactive preview & code examples, visit [Icon Documentation](https://composeunstyled.com/icon).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Icon(
 *     painter = painterResource("icon.xml"),
 *     contentDescription = "Settings",
 *     tint = Color.Black
 * )
 * ```
 *
 * @param painter The painter to draw the icon.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the icon.
 * @param tint The tint color to be applied to the icon.
 */
@Composable
fun UnstyledIcon(
  painter: Painter,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = Color.Unspecified,
) {
  val colorFilter = remember(tint) {
    if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
  }
  Image(painter, contentDescription, modifier, colorFilter = colorFilter)
}

/**
 * A foundational component used to display icons.
 *
 * For interactive preview & code examples, visit [Icon Documentation](https://composeunstyled.com/icon).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Icon(
 *     painter = painterResource("icon.xml"),
 *     contentDescription = "Settings",
 *     tint = Color.Black
 * )
 * ```
 *
 * @param imageBitmap The image bitmap to draw the icon.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the icon.
 * @param tint The tint color to be applied to the icon.
 */
@Composable
fun UnstyledIcon(
  imageBitmap: ImageBitmap,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = Color.Unspecified,
) {
  val colorFilter = remember(tint) {
    if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
  }
  Image(imageBitmap, contentDescription, modifier, colorFilter = colorFilter)
}

/**
 * A foundational component used to display icons.
 *
 * For interactive preview & code examples, visit [Icon Documentation](https://composeunstyled.com/icon).
 *
 * ## Basic Example
 *
 * ```kotlin
 * Icon(
 *     painter = painterResource("icon.xml"),
 *     contentDescription = "Settings",
 *     tint = Color.Black
 * )
 * ```
 *
 * @param imageVector The image vector to draw the icon.
 * @param contentDescription The content description for accessibility.
 * @param modifier Modifier to be applied to the icon.
 * @param tint The tint color to be applied to the icon.
 */
@Composable
fun UnstyledIcon(
  imageVector: ImageVector,
  contentDescription: String?,
  modifier: Modifier = Modifier,
  tint: Color = Color.Unspecified,
) {
  val colorFilter = remember(tint) {
    if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
  }
  Image(imageVector, contentDescription, modifier, colorFilter = colorFilter)
}
