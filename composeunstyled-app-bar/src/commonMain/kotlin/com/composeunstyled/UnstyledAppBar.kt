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

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A layout for a title, navigation content, and trailing actions. The caller provides all visuals,
 * sizing, and accessible labels for interactive slot content.
 *
 * @param itemSpacing Space between navigation, title, and actions, including individual actions.
 */
@Composable
fun UnstyledAppBar(
  title: @Composable () -> Unit,
  modifier: Modifier = Modifier,
  windowInsets: WindowInsets = WindowInsets(0),
  contentPadding: PaddingValues = PaddingValues.Zero,
  itemSpacing: Dp = 0.dp,
  navigation: @Composable () -> Unit = {},
  actions: @Composable RowScope.() -> Unit = {},
) {
  Row(
    modifier =
    modifier
      .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
      .windowInsetsPadding(windowInsets)
      .padding(contentPadding),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(itemSpacing),
  ) {
    navigation()
    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) { title() }
    Row(
      horizontalArrangement = Arrangement.spacedBy(itemSpacing),
      verticalAlignment = Alignment.CenterVertically,
      content = actions,
    )
  }
}
