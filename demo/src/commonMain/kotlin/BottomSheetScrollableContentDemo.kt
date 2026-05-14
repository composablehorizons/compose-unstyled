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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.DragIndication
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.rememberBottomSheetState

@Composable
fun BottomSheetScrollableContentDemo() {
  val Peek = SheetDetent("peek") { containerHeight, _ ->
    containerHeight * 0.45f
  }
  val sheetState = rememberBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Peek, FullyExpanded),
  )
  val items = List(24) { index -> "Item ${index + 1}" }

  UnstyledBottomSheet(
    state = sheetState,
    modifier = Modifier.fillMaxSize().padding(top = 12.dp),
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
      Sheet(
        modifier = Modifier
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(Color(0xFFF8FAFC))
          .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .widthIn(max = 640.dp)
          .fillMaxWidth(),
      ) {
        Box(Modifier.fillMaxWidth()) {
          LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(top = 48.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            items(items) { item ->
              BasicText(
                text = item,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 24.dp)
                  .background(Color.White, RoundedCornerShape(8.dp))
                  .border(1.dp, Color(0xFFE4E4E7), RoundedCornerShape(8.dp))
                  .padding(16.dp),
                style = TextStyle(
                  color = Color(0xFF18181B),
                  fontSize = 14.sp,
                ),
              )
            }
          }
          Box(
            Modifier.fillMaxWidth().padding(top = 22.dp),
            contentAlignment = Alignment.Center,
          ) {
            DragIndication(
              modifier = Modifier
                .background(Color(0xFFCACACA), RoundedCornerShape(100))
                .size(32.dp, 4.dp),
            )
          }
        }
      }
    }
  }
}
