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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Thumb
import com.composeunstyled.ThumbVisibility
import com.composeunstyled.UnstyledHorizontalScrollbar
import com.composeunstyled.rememberScrollbarState

val ScrollbarRegressionScreenshots = listOf(
  VisualRegressionScreenshot(
    name = "scrollbar-horizontal-rtl",
    width = 420,
    height = 180,
    content = { HorizontalScrollbarRtlRegression() },
  ),
)

@Composable
fun HorizontalScrollbarRtlRegression() {
  RtlLayout {
    val scrollState = rememberScrollState(initial = 120)
    val scrollbarState = rememberScrollbarState(scrollState)

    Box(
      modifier = Modifier
        .width(320.dp)
        .height(96.dp)
        .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
        .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(8.dp)),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(72.dp)
          .horizontalScroll(scrollState)
          .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        repeat(8) {
          Box(Modifier.size(56.dp).clip(CircleShape).background(Color.Black))
        }
      }
      UnstyledHorizontalScrollbar(
        scrollbarState = scrollbarState,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .height(16.dp)
          .fillMaxWidth(),
      ) {
        Thumb(
          modifier = Modifier
            .fillMaxHeight()
            .padding(3.dp)
            .width(48.dp)
            .background(Color.Black.copy(alpha = 0.36f), RoundedCornerShape(100.dp)),
          thumbVisibility = ThumbVisibility.AlwaysVisible,
        )
      }
    }
  }
}
