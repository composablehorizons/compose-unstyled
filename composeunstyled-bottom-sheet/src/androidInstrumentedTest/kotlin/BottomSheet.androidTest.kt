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
@file:JvmName("BottomSheetAndroidTest")

package com.composeunstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertFalse

/**
 * We keep this test in Android source set as we cannot 'expand' the sheet using a mouse scroll
 */
class BottomSheetTest {

  @Test
  fun stateChangesDuringInteractions() = runTestSuite {
    testCase("isIdle is false, when dragging sheet with scrollable content") {
      lateinit var sheetState: BottomSheetState
      setContent {
        val Peek = SheetDetent(identifier = "peek") { containerHeight, _ ->
          containerHeight * 0.6f
        }
        sheetState = rememberBottomSheetState(
          initialDetent = Peek,
          detents = listOf(Peek, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = sheetState,
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("sheet"),
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            LazyColumn {
              repeat(50) {
                item {
                  BasicText(
                    text = "Item #${(it + 1)}",
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                  )
                }
              }
            }
          }
        }
      }

      // Drag the sheet
      onNodeWithTag("sheet").performTouchInput {
        swipeDown(
          startY = centerY,
          endY = centerY + 200f,
        )
      }

      assertFalse(sheetState.isIdle)
    }
  }
}
