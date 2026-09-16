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
package com.composeunstyled.demo.modalbottomsheet

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.SheetDetent.Companion.Hidden
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.demoColors
import com.composeunstyled.demo.demoContent
import com.composeunstyled.demo.demoOutline
import com.composeunstyled.demo.demoShadow
import com.composeunstyled.demo.demoSurface
import com.composeunstyled.rememberModalBottomSheetState
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("modal-bottom-sheet")
@Composable
fun ModalBottomSheetDemo() {
  val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  val Peek = SheetDetent("peek") { containerHeight, _ ->
    containerHeight * 0.6f
  }
  val modalSheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Hidden, Peek, FullyExpanded),
  )

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledButton(
      onClick = { modalSheetState.targetDetent = Peek },
      modifier = Modifier
        .clip(RoundedCornerShape(10.dp))
        .heightIn(32.dp)
        .background(Theme[demoColors][demoSurface])
        .border(1.dp, Theme[demoColors][demoOutline], RoundedCornerShape(10.dp)),
      contentPadding = PaddingValues(horizontal = 10.dp),
      indication = LocalIndication.current,
    ) {
      Text("Show bottom sheet")
    }

    UnstyledModalBottomSheet(
      state = modalSheetState,
      overlay = {
        Scrim(
          scrimColor = Theme[demoColors][demoContent].copy(0.3f),
          enter = fadeIn(),
          exit = fadeOut(),
        )
      },
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
      ) {
        Sheet(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .dropShadow(
              shape = sheetShape,
              shadow = Shadow(
                radius = 16.dp,
                offset = DpOffset(0.dp, (-4).dp),
                color = Theme[demoColors][demoShadow],
                alpha = 0.24f,
              ),
            )
            .clip(sheetShape)
            .background(Theme[demoColors][demoSurface], sheetShape),
        ) {
          Box(
            modifier = Modifier.fillMaxWidth().height(1000.dp),
            contentAlignment = Alignment.TopCenter,
          ) {
            DragIndication(
              modifier = Modifier
                .padding(top = 22.dp)
                .background(Theme[demoColors][demoOutline], RoundedCornerShape(100))
                .size(32.dp, 4.dp),
              indication = LocalIndication.current,
            )
          }
        }
      }
    }
  }
}
