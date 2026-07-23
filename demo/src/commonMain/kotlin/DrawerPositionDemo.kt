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

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.composeunstyled.Content
import com.composeunstyled.DrawerPosition
import com.composeunstyled.DrawerSnapPoint.Companion.Closed
import com.composeunstyled.DrawerSnapPoint.Companion.Open
import com.composeunstyled.Panel
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.Viewport
import com.composeunstyled.rememberDrawerState

@Composable
fun DrawerPositionDemo() {
  val drawerState = rememberDrawerState(
    initialSnapPoint = Closed,
    snapPoints = listOf(Closed, Open),
  )

  UnstyledButton(
    onClick = {
      drawerState.targetSnapPoint = Open
    },
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .heightIn(32.dp)
      .background(Color(0xFFF8FAFC))
      .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(10.dp)),
    contentPadding = PaddingValues(horizontal = 10.dp),
    indication = LocalIndication.current,
  ) {
    BasicText("Open drawer")
  }

  UnstyledDrawer(
    state = drawerState,
    position = DrawerPosition.End,
    modifier = Modifier.fillMaxSize(),
  ) {
    Viewport(
      modifier = Modifier
        .fillMaxSize()
        .padding(20.dp),
    ) {
      Panel(
        modifier = Modifier
          .dropShadow(
            shape = RectangleShape,
            shadow = Shadow(
              radius = 0.dp,
              color = Color.Black,
              spread = 0.dp,
              offset = DpOffset(8.dp, 8.dp),
              alpha = 0.33f,
            ),
          )
          .background(Color.White)
          .border(1.dp, Color.Black)
          .fillMaxHeight()
          .widthIn(max = 360.dp),
      ) {
        Content(
          modifier = Modifier.fillMaxHeight(),
        ) {
          Box(Modifier.width(1000.dp).fillMaxHeight())
        }
      }
    }
  }
}
