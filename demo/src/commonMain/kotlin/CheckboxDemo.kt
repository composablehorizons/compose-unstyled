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
package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composeunstyled.Icon
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.theme.Theme

@Composable
fun CheckboxDemo() {
  Box(
    modifier = Modifier.fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))),
    contentAlignment = Alignment.Center,
  ) {
    var checked by remember { mutableStateOf(false) }
    UnstyledCheckbox(
      checked = checked,
      onCheckedChange = { checked = it },
      shape = RoundedCornerShape(4.dp),
      backgroundColor = Color.White,
      borderWidth = 1.dp,
      borderColor = Color.Black.copy(0.33f),
      modifier = Modifier.size(24.dp),
      contentDescription = "Add olives",
      indication = Theme[indications][dimmed],
    ) {
      Icon(Lucide.Check, contentDescription = null)
    }
  }
}
