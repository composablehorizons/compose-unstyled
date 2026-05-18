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

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.Scrim
import com.composeunstyled.Sheet
import com.composeunstyled.SheetDetent
import com.composeunstyled.UnstyledModalBottomSheet
import com.composeunstyled.rememberModalBottomSheetState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ModalBottomSheetInsetRegression() {
  val sheetState = rememberModalBottomSheetState(
    initialDetent = SheetDetent.FullyExpanded,
    detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
  )
  LaunchedEffect(sheetState.isIdle, sheetState.currentDetent) {
    if (
      sheetState.isIdle &&
      sheetState.currentDetent == SheetDetent.Hidden
    ) {
      delay(1.seconds)
      sheetState.targetDetent = SheetDetent.FullyExpanded
    }
  }

  UnstyledModalBottomSheet(
    state = sheetState,
    overlay = {
      Scrim(
        scrimColor = Color.Black.copy(0.28f),
        enter = EnterTransition.None,
        exit = ExitTransition.None,
      )
    },
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.TopCenter,
    ) {
      Sheet(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 64.dp)
          .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
          .background(Color(0xFFF8FAFC))
          .border(
            width = 1.dp,
            color = Color(0xFFCBD5E1),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
          ),
      ) {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(24) { index ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                  color = if (index % 2 == 0) Color.White else Color(0xFFF1F5F9),
                  shape = RoundedCornerShape(8.dp),
                )
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            )
          }
        }
      }
    }
  }
}
