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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun FloatingContent(
  floatingContent: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  side: AnchorSide = AnchorSide.Top,
  alignment: AnchorAlignment = AnchorAlignment.Center,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
  onPlaced: (FloatingPlacement) -> Unit = {},
  anchor: @Composable () -> Unit,
) {
  AnchoredFloatingContent(
    modifier = modifier,
    layer = { content ->
      Portal {
        content()
      }
    },
    content = floatingContent,
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
    onPlaced = onPlaced,
    anchor = anchor,
  )
}
