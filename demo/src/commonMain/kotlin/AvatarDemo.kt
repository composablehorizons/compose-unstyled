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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.composeunstyled.UnstyledAvatar

@Composable
fun AvatarDemo() {
  Row(
    modifier = Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    UnstyledAvatar(
      painter = null,
      underlay = {
        Text("CC")
      },
      contentDescription = "@coolcat",
      modifier = Modifier
        .size(32.dp)
        .clip(RectangleShape)
        .border(1.dp, Color.Black, RectangleShape)
        .background(Color.White),
      contentScale = ContentScale.Crop,
    )
    val painter = PrototypeImagePainter

    UnstyledAvatar(
      painter = painter,
      underlay = {
        Text("CC")
      },
      contentDescription = "@coolcat",
      modifier = Modifier
        .size(48.dp)
        .clip(RectangleShape)
        .border(1.dp, Color.Black, RectangleShape)
        .background(Color.White),
      contentScale = ContentScale.Crop,
    )
    UnstyledAvatar(
      painter = painter,
      underlay = {
        Text("CC")
      },
      contentDescription = "@coolcat",
      modifier = Modifier
        .size(56.dp)
        .clip(RectangleShape)
        .border(1.dp, Color.Black, RectangleShape)
        .background(Color.White),
      contentScale = ContentScale.Crop,
    )
  }
}
