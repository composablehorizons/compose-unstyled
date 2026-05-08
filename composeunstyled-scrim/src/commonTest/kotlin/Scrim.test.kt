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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import kotlin.test.Test

class ScrimTest {

  @Test
  fun scrim() = runTestSuite {
    testCase("scrim is removed from composition, when modal is hidden") {
      setContent {
        val modalState = rememberModalState(initiallyVisible = true)

        Modal(state = modalState) {
          Scrim(Modifier.testTag("scrim"))
        }

        modalState.transitionState.targetState = false
      }

      waitForIdle()
      onNodeWithTag("scrim").assertDoesNotExist()
    }

    testCase("scrim exists in composition, when modal is visible") {
      setContent {
        val modalState = rememberModalState(initiallyVisible = true)
        Modal(state = modalState) {
          Scrim(Modifier.testTag("scrim"))
        }
      }

      onNodeWithTag("scrim").assertExists()
    }

    testCase("scrim appears when state changes to visible") {
      var visible by mutableStateOf(false)

      setContent {
        val modalState = rememberModalState(initiallyVisible = visible)
        modalState.transitionState.targetState = visible

        Modal(state = modalState) {
          Scrim(Modifier.testTag("scrim"))
        }
      }

      visible = true
      waitForIdle()
      onNodeWithTag("scrim").assertExists()
    }
  }
}
