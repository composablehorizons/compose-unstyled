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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledHorizontalSeparator
import com.composeunstyled.UnstyledVerticalSeparator

@Composable
fun SeparatorsDemo() {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(Color(0xFFF8FAFC))
        .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(6.dp))
        .width(240.dp),
    ) {
      BasicText(
        "New Window",
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      )
      UnstyledHorizontalSeparator(Color.LightGray)
      BasicText("New Tab", Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp))
      UnstyledHorizontalSeparator(Color.LightGray)
      BasicText(
        "New Incognito Tab",
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
      )
      UnstyledHorizontalSeparator(Color.LightGray)
      Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        BasicText(
          "Copy",
          modifier = Modifier.padding(8.dp).weight(1f),
          style = TextStyle(textAlign = TextAlign.Center),
        )
        UnstyledVerticalSeparator(Color.LightGray)
        BasicText(
          "Cut",
          modifier = Modifier.padding(8.dp).weight(1f),
          style = TextStyle(textAlign = TextAlign.Center),
        )
        UnstyledVerticalSeparator(Color.LightGray)
        BasicText(
          "Paste",
          Modifier.padding(8.dp).weight(1f),
          style = TextStyle(textAlign = TextAlign.Center),
        )
      }
    }
  }
}
