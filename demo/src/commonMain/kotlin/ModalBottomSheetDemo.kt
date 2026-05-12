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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.SheetDetent.Companion.Hidden
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.focusRing
import com.composeunstyled.rememberModalBottomSheetState
import kotlinx.coroutines.delay

private val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
  containerHeight * 0.6f
}

@Composable
fun ModalBottomSheetDemo() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF800080), Color(0xFFDA70D6)))),
  ) {
    val modalSheetState = rememberModalBottomSheetState(
      initialDetent = Hidden,
      detents = listOf(Hidden, Peek, FullyExpanded),
    )

    LaunchedEffect(Unit) {
      delay(500)
      modalSheetState.targetDetent = Peek
    }

    UnstyledButton(
      onClick = { modalSheetState.targetDetent = Peek },
      modifier = Modifier
        .align(Alignment.Center)
        .clip(RoundedCornerShape(6.dp))
        .background(Color.White),
    ) {
      Text("Show Sheet", modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
    }

    UnstyledModalBottomSheet(
      state = modalSheetState,
      overlay = {
        Scrim(scrimColor = Color.Black.copy(0.3f), enter = fadeIn(), exit = fadeOut())
      },
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
      ) {
        Sheet(
          modifier = Modifier
            .padding(top = 12.dp)
            .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(Color.White)
            .widthIn(max = 640.dp)
            .fillMaxWidth(),
        ) {
          Box(Modifier.fillMaxWidth().height(600.dp), contentAlignment = Alignment.TopCenter) {
            val interactionSource = remember { MutableInteractionSource() }

            DragIndication(
              interactionSource = interactionSource,
              modifier = Modifier
                .padding(top = 22.dp)
                .focusRing(
                  interactionSource,
                  width = 2.dp,
                  Color(0XFF2563EB),
                  RoundedCornerShape(100),
                  offset = 4.dp,
                )
                .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                .size(32.dp, 4.dp),
            )
          }
        }
      }
    }
  }
}
