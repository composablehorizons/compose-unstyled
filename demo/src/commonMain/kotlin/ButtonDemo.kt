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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composeunstyled.Icon
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.theme.Theme

@Composable
fun ButtonDemo() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFFFF6F61), Color(0xFFFF8A65)))),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledButton(
      onClick = { },
      backgroundColor = Color.White,
      contentColor = Color(0xFF020817),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.shadow(elevation = 4.dp, RoundedCornerShape(12.dp)),
      indication = Theme[indications][dimmed],
    ) {
      Icon(Lucide.Pencil, contentDescription = null)
      Spacer(Modifier.width(12.dp))
      Text("Compose")
    }
  }
}
