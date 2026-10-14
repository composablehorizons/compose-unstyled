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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

/**
 * A layout for a title, navigation content, and trailing actions. The caller provides all visuals,
 * sizing, and accessible labels for interactive slot content.
 */
@Composable
fun UnstyledCenteredAppBar(
  title: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  windowInsets: WindowInsets = WindowInsets(0),
  contentPadding: PaddingValues = PaddingValues.Zero,
  navigation: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
) {
  Layout(
    content = {
      Box(contentAlignment = Alignment.CenterStart) { navigation() }
      Box(contentAlignment = Alignment.Center) { title() }
      Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    },
    modifier =
    modifier
      .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
      .windowInsetsPadding(windowInsets)
      .padding(contentPadding),
  ) { measurables, constraints ->
    val loose = constraints.copy(minWidth = 0, minHeight = 0)
    val leading = measurables[0].measure(loose)
    val trailing = measurables[2].measure(loose)
    val sideWidth = maxOf(leading.width, trailing.width)
    val titleWidth =
      if (constraints.hasBoundedWidth) {
        (constraints.maxWidth - sideWidth * 2).coerceAtLeast(0)
      } else {
        Constraints.Infinity
      }
    val center = measurables[1].measure(loose.copy(maxWidth = titleWidth))
    val width =
      maxOf(leading.width + trailing.width, center.width + sideWidth * 2)
        .coerceIn(constraints.minWidth, constraints.maxWidth)
    val height =
      maxOf(leading.height, center.height, trailing.height)
        .coerceIn(constraints.minHeight, constraints.maxHeight)

    layout(width, height) {
      leading.placeRelative(0, (height - leading.height) / 2)
      center.placeRelative((width - center.width) / 2, (height - center.height) / 2)
      trailing.placeRelative(width - trailing.width, (height - trailing.height) / 2)
    }
  }
}
