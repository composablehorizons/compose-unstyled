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

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isGreaterThan
import kotlin.test.Test

class DialogJvmTest {

  @Test
  fun dialogPanelRunsEnterTransitionWhenShown() = runComposeUiTest {
    var visible by mutableStateOf(false)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          DialogPanel(
            modifier = Modifier
              .size(100.dp)
              .testTag("dialog_content"),
            enter = slideInVertically(
              animationSpec = tween(durationMillis = 10_000),
              initialOffsetY = { it },
            ),
          ) {
          }
        }
      }
    }

    waitForIdle()
    mainClock.autoAdvance = false
    try {
      visible = true
      mainClock.advanceTimeByFrame()
      waitUntilExactlyOneExists(hasTestTag("dialog_content"))

      val enteringTop = onNodeWithTag("dialog_content").fetchSemanticsNode().boundsInRoot.top

      mainClock.advanceTimeBy(10_000)
      val restingTop = onNodeWithTag("dialog_content").fetchSemanticsNode().boundsInRoot.top

      assertThat(enteringTop).isGreaterThan(restingTop)
    } finally {
      mainClock.autoAdvance = true
    }
  }
}
