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
package com.composeunstyled.visualregressions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownMenuPanel
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.UnstyledDropdownMenuItem

val DropdownMenuRegressionScreenshots = listOf(
  VisualRegressionScreenshot(
    name = "dropdown-menu-anchored-rtl",
    width = 420,
    height = 340,
    content = { DropdownMenuAnchoredRtlRegression() },
  ),
)

@Composable
fun DropdownMenuAnchoredRtlRegression() {
  RtlLayout {
    Box(
      modifier = Modifier.width(260.dp),
      contentAlignment = Alignment.TopCenter,
    ) {
      UnstyledDropdownMenu(
        expanded = true,
        onExpandedChange = {},
        sideOffset = 8.dp,
        panel = {
          DropdownMenuPanel(
            modifier = Modifier
              .width(176.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFF8FAFC))
              .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(8.dp)),
          ) {
            Column(
              modifier = Modifier.padding(4.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              DropdownMenuItem("First")
              DropdownMenuItem("Second")
              DropdownMenuItem("Third")
            }
          }
        },
        anchor = {
          Box(
            modifier = Modifier
              .width(104.dp)
              .sizeIn(minHeight = 40.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(Color.White)
              .border(1.dp, Color.Black, RoundedCornerShape(6.dp))
              .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
          ) {
            BasicText("Anchor")
          }
        },
      )
    }
  }
}

@Composable
private fun DropdownMenuItem(text: String) {
  UnstyledDropdownMenuItem(
    onClick = {},
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(Color.White)
      .fillMaxWidth()
      .sizeIn(minHeight = 36.dp)
      .padding(horizontal = 10.dp, vertical = 8.dp),
  ) {
    BasicText(text)
  }
}
