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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.composeunstyled.theme.ComponentInteractiveSize
import com.composeunstyled.theme.buildTheme
import kotlin.test.Test

class ThemeTest {
  @Test
  fun defaultComponentInteractiveSizeIsPropagatedToModifier() = runComposeUiTest {
    val testTheme = buildTheme {
      defaultComponentInteractiveSize = ComponentInteractiveSize(
        touchInteractionSize = 48.dp,
        nonTouchInteractionSize = 32.dp,
      )
    }

    setContent {
      testTheme {
        Box(
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("interactive-box"),
        )
      }
    }

    onNodeWithTag("interactive-box")
      .assertWidthIsEqualTo(48.dp)
      .assertHeightIsEqualTo(48.dp)
  }
}
