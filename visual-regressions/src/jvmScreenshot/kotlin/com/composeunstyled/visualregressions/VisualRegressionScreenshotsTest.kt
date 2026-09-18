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
package com.composeunstyled.visualregressions

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.awt.image.BufferedImage

class VisualRegressionScreenshotsTest {

  @Test
  fun diffReportsChangedPixelAndChannelMetrics() {
    val expected = BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB).apply {
      setRGB(0, 0, 0xFF010203.toInt())
      setRGB(1, 0, 0xFF0A0B0C.toInt())
    }
    val actual = BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB).apply {
      setRGB(0, 0, 0xFF010203.toInt())
      setRGB(1, 0, 0xFF0C080C.toInt())
    }

    val result = diff(expected, actual)

    assertThat(result.changedPixels).isEqualTo(1)
    assertThat(result.maxChannelDelta).isEqualTo(3)
    assertThat(result.meanChannelDelta).isEqualTo(1.25)
  }
}
