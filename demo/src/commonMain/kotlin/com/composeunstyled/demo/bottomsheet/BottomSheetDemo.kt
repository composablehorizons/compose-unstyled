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
package com.composeunstyled.demo.bottomsheet

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.SheetDetent.Companion.Hidden
import com.composeunstyled.Text
import com.composeunstyled.UnstyledBottomSheet
import com.composeunstyled.UnstyledButton
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.outlineToken
import com.composeunstyled.demo.shadowToken
import com.composeunstyled.demo.surfaceToken
import com.composeunstyled.rememberBottomSheetState
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("bottom-sheet")
@Composable
fun BottomSheetDemo() {
  val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  val Peek = SheetDetent("peek") { containerHeight, _ ->
    containerHeight * 0.6f
  }
  val sheetState = rememberBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Hidden, Peek, FullyExpanded),
  )
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledButton(
      onClick = { sheetState.targetDetent = Peek },
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .heightIn(32.dp)
        .background(Theme[colors][surfaceToken])
        .border(1.dp, Theme[colors][outlineToken], RoundedCornerShape(10.dp)),
      contentPadding = PaddingValues(horizontal = 10.dp),
      indication = LocalIndication.current,
    ) {
      Text("Show bottom sheet")
    }

    UnstyledBottomSheet(
      state = sheetState,
      modifier = Modifier.fillMaxSize().padding(top = 12.dp),
    ) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Sheet(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .dropShadow(
              shape = sheetShape,
              shadow = Shadow(
                radius = 16.dp,
                offset = DpOffset(0.dp, (-4).dp),
                color = Theme[colors][shadowToken],
                alpha = 0.24f,
              ),
            )
            .background(Theme[colors][surfaceToken], sheetShape),
        ) {
          Box(Modifier.fillMaxWidth().height(1000.dp)) {
            DragIndication(
              modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 22.dp)
                .background(Theme[colors][outlineToken], RoundedCornerShape(100))
                .size(32.dp, 4.dp),
              indication = LocalIndication.current,
            )
          }
        }
      }
    }
  }
}
