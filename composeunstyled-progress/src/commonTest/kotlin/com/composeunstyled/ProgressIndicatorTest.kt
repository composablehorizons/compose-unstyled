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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ProgressIndicatorTest {

  @Test
  fun determinateProgressSemanticsAreAppliedToRoot() = runComposeUiTest {
    setContent {
      UnstyledProgress(
        progress = 0.42f,
        modifier = Modifier.testTag("progress"),
      ) {
        BasicText("Progress visual")
      }
    }

    onNodeWithTag("progress")
      .assert(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.42f, 0f..1f, 0)))
  }

  @Test
  fun indeterminateProgressSemanticsAreAppliedToRoot() = runComposeUiTest {
    setContent {
      UnstyledProgress(
        modifier = Modifier.testTag("progress"),
      ) {
        BasicText("Progress visual")
      }
    }

    onNodeWithTag("progress")
      .assert(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
  }

  @Test
  fun progressScopeExposesProgressToContent() = runComposeUiTest {
    setContent {
      UnstyledProgress(progress = 0.42f) {
        BasicText("Progress is $progress")
      }
    }

    onNodeWithText("Progress is 0.42").assertIsDisplayed()
  }

  @Test
  fun indicatorUsesCallerModifierForSize() = runComposeUiTest {
    var indicatorSize = IntSize.Zero

    setContent {
      UnstyledProgress(
        progress = 0.5f,
        modifier = Modifier.size(width = 100.dp, height = 20.dp),
      ) {
        Indicator(
          Modifier
            .size(width = 24.dp, height = 8.dp)
            .onSizeChanged { indicatorSize = it },
        )
      }
    }

    waitForIdle()

    assertThat(indicatorSize).isEqualTo(IntSize(width = 24, height = 8))
  }

  private fun hasProgressBarRangeInfo(rangeInfo: ProgressBarRangeInfo): SemanticsMatcher {
    return SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, rangeInfo)
  }
}
