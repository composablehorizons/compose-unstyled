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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragIndication
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.SheetDetent.Companion.FullyExpanded
import com.composeunstyled.SheetDetent.Companion.Hidden
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.rememberModalBottomSheetState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ModalBottomSheetDemo() {
  val Peek = SheetDetent("peek") { containerHeight, _ ->
    containerHeight * 0.6f
  }
  val modalSheetState = rememberModalBottomSheetState(
    initialDetent = Peek,
    detents = listOf(Hidden, Peek, FullyExpanded),
  )

  LaunchedEffect(
    modalSheetState.isIdle,
    modalSheetState.currentDetent,
  ) {
    if (
      modalSheetState.isIdle &&
      modalSheetState.currentDetent == Hidden
    ) {
      delay(1.seconds)
      modalSheetState.targetDetent = Peek
    }
  }

  UnstyledModalBottomSheet(
    state = modalSheetState,
    overlay = {
      Scrim(
        scrimColor = Color.Black.copy(0.3f),
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
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(Color(0xFFF8FAFC))
          .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
      ) {
        Box(
          modifier = Modifier.fillMaxWidth().height(600.dp),
          contentAlignment = Alignment.TopCenter,
        ) {
          DragIndication(
            modifier = Modifier
              .padding(top = 22.dp)
              .background(Color(0xFFCACACA), RoundedCornerShape(100))
              .size(32.dp, 4.dp),
            indication = LocalIndication.current,
          )
        }
      }
    }
  }
}
