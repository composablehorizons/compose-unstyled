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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.test.Test

class ModalTest {

  @Test
  fun semantics() = runTestSuite {
    testCase("add isDialog semantic") {
      var showModal by mutableStateOf(false)

      setContent {
        if (showModal) {
          Modal(onKeyEvent = { false }) {
            Box(Modifier.testTag("modal_contents").size(40.dp))
          }
        }
      }
      onNode(isDialog()).assertDoesNotExist()
      showModal = true
      onNode(isDialog()).assertExists()
    }

    testCase("content respects LocalLayoutDirection") {
      setContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          Modal {
            val layoutDirection =
              if (LocalLayoutDirection.current == LayoutDirection.Rtl) "rtl" else "ltr"
            BasicText(layoutDirection, Modifier.testTag("layout_direction"))
          }
        }
      }

      onNodeWithTag("layout_direction").assertTextEquals("rtl")
    }
  }
}
