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
package com.composeunstyled.demo.toggleswitch

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import com.composeunstyled.Thumb
import com.composeunstyled.Track
import com.composeunstyled.UnstyledSwitch
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.contentToken
import com.composeunstyled.demo.inputBackgroundToken
import com.composeunstyled.demo.outlineToken
import com.composeunstyled.demo.surfaceToken
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("toggleswitch")
@Composable
fun ToggleSwitchDemo() {
  var toggled by remember { mutableStateOf(true) }
  val backgroundColor by animateColorAsState(
    if (toggled) Theme[colors][contentToken] else Theme[colors][inputBackgroundToken],
  )

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledSwitch(
      checked = toggled,
      onCheckedChange = { toggled = it },
      modifier = Modifier
        .width(300.dp)
        .clip(RoundedCornerShape(10.dp)),
      indication = LocalIndication.current,
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Airplane Mode", fontSize = 18.sp)
        Track(
          modifier = Modifier
            .width(58.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(100))
            .background(backgroundColor, RoundedCornerShape(100))
            .border(1.dp, Theme[colors][outlineToken], RoundedCornerShape(100)),
        ) {
          Thumb(
            animationSpec = tween(),
            modifier = Modifier
              .padding(4.dp)
              .clip(CircleShape)
              .background(Theme[colors][surfaceToken])
              .border(1.dp, Theme[colors][outlineToken], CircleShape)
              .size(24.dp),
          )
        }
      }
    }
  }
}
