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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.focusRing
import com.composeunstyled.rememberBottomSheetState
import kotlinx.coroutines.delay

@Composable
fun BottomSheetDemo() {
  val peekHeight = 280.dp
  val sheetTopPadding = 12.dp
  val mini = SheetDetent("mini") { _, _ ->
    peekHeight + sheetTopPadding
  }
  val sheetState = rememberBottomSheetState(
    initialDetent = mini,
    detents = listOf(mini, FullyExpanded),
  )
  LaunchedEffect(Unit) {
    delay(500)
    sheetState.targetDetent = FullyExpanded
  }

  UnstyledBottomSheet(
    state = sheetState,
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White),
  ) {
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Sheet(
        modifier = Modifier
          .padding(top = sheetTopPadding)
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(Color.White)
          .demoOutline(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .widthIn(max = 640.dp)
          .fillMaxWidth(),
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .clickable {
              if (sheetState.currentDetent != FullyExpanded) {
                sheetState.targetDetent = FullyExpanded
              }
            },
          contentAlignment = Alignment.TopCenter,
        ) {
          val interactionSource = remember { MutableInteractionSource() }

          DragIndication(
            interactionSource = interactionSource,
            modifier = Modifier
              .padding(top = 22.dp)
              .focusRing(
                interactionSource,
                width = 2.dp,
                Color.Black,
                RoundedCornerShape(100),
                offset = 4.dp,
              )
              .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(100))
              .size(32.dp, 4.dp),
          )
        }
      }
    }
  }
}
