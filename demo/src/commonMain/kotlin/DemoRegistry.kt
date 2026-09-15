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
package com.composeunstyled.demo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

enum class DemoSection(val title: String) {
  Components("Components"),
  Theme("Theme"),
  Modifiers("Modifiers"),
  Utilities("Utilities"),
}

enum class DemoContentAlignment {
  Center,
  TopCenter,
}

enum class DemoPadding {
  Default,
  None,
}

internal data class DemoPresentation(
  val contentAlignment: DemoContentAlignment = DemoContentAlignment.Center,
  val padding: DemoPadding = DemoPadding.Default,
) {
  val alignment: Alignment
    get() = when (contentAlignment) {
      DemoContentAlignment.Center -> Alignment.Center
      DemoContentAlignment.TopCenter -> Alignment.TopCenter
    }

  val paddingValues: PaddingValues
    get() = when (padding) {
      DemoPadding.Default -> PaddingValues(16.dp)
      DemoPadding.None -> PaddingValues(0.dp)
    }
}

internal data class DemoItem(
  val name: String,
  val id: String,
  val section: DemoSection,
  val demo: @Composable () -> Unit,
  val presentation: DemoPresentation = DemoPresentation(),
)
