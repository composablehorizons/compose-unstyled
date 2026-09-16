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
package com.composeunstyled.demo.focusring

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.FocusRingVisibility
import com.composeunstyled.FocusVisibilityProvider
import com.composeunstyled.demo.SimpleButton
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.demoColors
import com.composeunstyled.demo.demoFocus
import com.composeunstyled.focusRing
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("focus-ring-focus-visible", name = "Focus Ring (FocusVisible)")
@Composable
fun FocusRingFocusVisibleDemo() {
  FocusVisibilityProvider {
    FocusRingVariant(visibility = FocusRingVisibility.FocusVisible)
  }
}

@Preview
@UnstyledDemo("focus-ring-focused", name = "Focus Ring (Focused)")
@Composable
fun FocusRingFocusedDemo() {
  FocusVisibilityProvider {
    FocusRingVariant(visibility = FocusRingVisibility.Focused)
  }
}

@Composable
private fun FocusRingVariant(visibility: FocusRingVisibility) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource,
        width = 2.dp,
        color = Theme[demoColors][demoFocus],
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
        visibility = visibility,
      ),
      interactionSource = interactionSource,
    )
  }
}
