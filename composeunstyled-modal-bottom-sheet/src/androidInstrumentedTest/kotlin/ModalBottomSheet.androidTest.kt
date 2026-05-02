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
package com.composeunstyled

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlin.test.Test

class ModalBottomSheet {

  @Test
  fun backPress() = runTestSuite {
    testCase("sheet is dismissed, when pressing Back with dismissOnBackPress true") {
      var dismissCalled = false
      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnBackPress = true),
          onDismiss = { dismissCalled = true },
          overlay = { UnstyledScrim() },
        ) {
          Sheet(
            Modifier
              .fillMaxWidth()
              .background(Color.White),
          ) {
            Box(
              Modifier
                .testTag("sheet")
                .size(40.dp),
            )
          }
        }
      }

      onNodeWithTag("sheet").assertExists()
      Espresso.pressBack()
      onNodeWithTag("sheet").assertDoesNotExist()
      assertTrue(dismissCalled)
    }

    testCase("sheet is not dismissed, when pressing escape with dismissOnBackPress false") {
      var dismissCalled = false
      setContent {
        UnstyledModalBottomSheet(
          rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnBackPress = false),
          onDismiss = { dismissCalled = true },
          overlay = { UnstyledScrim() },
        ) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet")
                .size(40.dp),
            )
          }
        }
      }
      onNodeWithTag("sheet").assertExists()
      Espresso.pressBack()
      onNodeWithTag("sheet").assertExists()
      assertFalse(dismissCalled)
    }
  }
}
