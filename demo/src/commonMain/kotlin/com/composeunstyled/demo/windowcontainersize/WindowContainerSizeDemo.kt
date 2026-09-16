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
package com.composeunstyled.demo.windowcontainersize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import com.composeunstyled.currentWindowContainerSize
import com.composeunstyled.demo.UnstyledDemo

@Preview
@UnstyledDemo("window-container-size")
@Composable
fun WindowContainerSizeDemo() {
  val windowContainerSize = currentWindowContainerSize()

  Column(
    modifier = Modifier.fillMaxSize()
      .background(Color.White)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "Window container size",
      color = Color.Black,
      fontSize = 22.sp,
      lineHeight = 28.sp,
      fontWeight = FontWeight.Medium,
    )
    Text(
      text = "${windowContainerSize.width} x ${windowContainerSize.height}",
      color = Color.Black,
      fontSize = 36.sp,
      lineHeight = 44.sp,
      modifier = Modifier.padding(top = 12.dp),
    )
    Text(
      text = "Resize the window to watch this value update.",
      color = Color(0xFF525252),
      fontSize = 14.sp,
      lineHeight = 20.sp,
      modifier = Modifier.padding(top = 8.dp),
    )
  }
}
