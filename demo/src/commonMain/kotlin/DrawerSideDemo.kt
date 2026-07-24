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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Inbox
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.SendHorizontal
import com.composables.icons.lucide.Trash2
import com.composeunstyled.DrawerSide
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.Panel
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.Viewport
import com.composeunstyled.rememberDrawerState

@Composable
fun DrawerSideDemo() {
  val drawerState = rememberDrawerState(
    initialSnapPoint = DrawerSnapPoint.Open,
  )

  UnstyledButton(
    onClick = {
      drawerState.targetSnapPoint = DrawerSnapPoint.Open
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
    modifier = Modifier.fillMaxSize(),
    side = DrawerSide.End,
  ) {
    Viewport(Modifier.fillMaxSize()) {
      Panel(
        modifier = Modifier
          .background(Color.White)
          .border(1.dp, Color.Black)
          .width(360.dp)
          .fillMaxHeight()
          .padding(12.dp),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          BasicText(
            "Mail",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(10.dp),
          )
          UnstyledButton(
            onClick = {},
            indication = LocalIndication.current,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              UnstyledIcon(Lucide.Inbox)
              BasicText("Inbox")
            }
          }
          UnstyledButton(
            onClick = {},
            indication = LocalIndication.current,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              UnstyledIcon(Lucide.SendHorizontal)
              BasicText("Outbox")
            }
          }
          UnstyledButton(
            onClick = {},
            indication = LocalIndication.current,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              UnstyledIcon(Lucide.Heart)
              BasicText("Favorites")
            }
          }
          UnstyledButton(
            onClick = {},
            indication = LocalIndication.current,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              UnstyledIcon(Lucide.Trash2)
              BasicText("Trash")
            }
          }
        }
      }
    }
  }
}
