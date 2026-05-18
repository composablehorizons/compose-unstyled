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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeWithVelocity
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThanOrEqualTo
import kotlin.test.Test

class BottomSheetTest {

  private val BoundsTolerance = 1.dp

  @Test
  fun isidle_is_false_when_dragging_sheet_with_scrollable_content() = runComposeUiTest {
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

    assertThat(sheetState.isIdle).isFalse()
  }

  @Test
  fun sheet_stays_within_bounds_when_flinging_to_fullyexpanded() = runComposeUiTest {
    lateinit var state: BottomSheetState
    val peek = SheetDetent("peek") { _, _ -> 240.dp }
    val contentHeight = 560.dp

    setContent {
      Box(Modifier.fillMaxSize()) {
        state = rememberBottomSheetState(
          initialDetent = peek,
          detents = listOf(SheetDetent.Hidden, peek, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .testTag("sheet"),
        ) {
          Box(
            Modifier
              .fillMaxWidth()
              .height(contentHeight),
          )
        }
      }
    }

    val expandedTop = onNodeWithTag("sheet").fetchSemanticsNode().boundsInRoot.bottom -
      with(density) { contentHeight.toPx() }
    mainClock.autoAdvance = false

    try {
      onNodeWithTag("sheet").performTouchInput {
        swipeWithVelocity(
          start = Offset(centerX, centerY),
          end = Offset(centerX, top),
          endVelocity = 4_000f,
          durationMillis = 50,
        )
      }

      repeat(10) {
        mainClock.advanceTimeByFrame()

        val sheetBounds = onNodeWithTag("sheet").fetchSemanticsNode().boundsInRoot
        assertThat(sheetBounds.top).isGreaterThanOrEqualTo(
          expandedTop - with(density) { BoundsTolerance.toPx() },
        )
      }
    } finally {
      mainClock.autoAdvance = true
    }
  }
}
