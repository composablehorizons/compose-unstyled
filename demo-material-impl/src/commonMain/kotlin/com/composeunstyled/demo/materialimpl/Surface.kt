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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.composeunstyled.demo.material3api

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.buildModifier

@Composable
private fun ContainerSurface(
  modifier: Modifier,
  shape: Shape,
  color: Color,
  contentColor: Color,
  border: BorderStroke?,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalContentColor provides contentColor) {
    Box(
      modifier = (
        modifier
          .clip(shape)
          .background(color, shape)
        ) then buildModifier {
        if (border != null) {
          add(Modifier.border(border, shape))
        }
      },
    ) {
      content()
    }
  }
}

@Composable
fun Surface(
  modifier: Modifier = Modifier,
  shape: Shape = androidx.compose.ui.graphics.RectangleShape,
  color: Color = MaterialTheme.colorScheme.surface,
  contentColor: Color = androidx.compose.material3.contentColorFor(color),
  tonalElevation: Dp = 0.dp,
  shadowElevation: Dp = 0.dp,
  border: BorderStroke? = null,
  content: @Composable () -> Unit,
) {
  ContainerSurface(
    modifier = modifier,
    shape = shape,
    color = color,
    contentColor = contentColor,
    border = border,
    content = content,
  )
}
