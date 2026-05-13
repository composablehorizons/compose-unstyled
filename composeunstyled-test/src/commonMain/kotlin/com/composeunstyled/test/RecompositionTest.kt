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
package com.composeunstyled.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.runComposeUiTest

fun runComposeRecompositionTest(
  block: RecompositionTestScope.() -> Unit,
) = runComposeUiTest {
  RecompositionTestScope(this).block()
}

class RecompositionTestScope(
  private val composeUiTest: ComposeUiTest,
) {
  private val recompositionCounts = mutableMapOf<String, Int>()

  fun setContent(content: @Composable () -> Unit) {
    composeUiTest.setContent(content)
  }

  fun waitForIdle() {
    composeUiTest.waitForIdle()
  }

  fun waitUntil(
    conditionDescription: String? = null,
    timeoutMillis: Long = 1_000,
    condition: () -> Boolean,
  ) {
    composeUiTest.waitUntil(
      conditionDescription = conditionDescription,
      timeoutMillis = timeoutMillis,
      condition = condition,
    )
  }

  @Composable
  @NonSkippableComposable
  fun RecompositionCount(name: String) {
    SideEffect {
      recompositionCounts[name] = recompositionCount(name) + 1
    }
  }

  fun resetRecompositionCounts(vararg names: String) {
    if (names.isEmpty()) {
      recompositionCounts.keys.forEach { name ->
        recompositionCounts[name] = 0
      }
      return
    }

    names.forEach { name ->
      recompositionCounts[name] = 0
    }
  }

  fun recompositionCount(name: String): Int {
    return recompositionCounts[name] ?: 0
  }
}
