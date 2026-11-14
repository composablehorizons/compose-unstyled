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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.composables.compose.ripple.rememberRippleIndication
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.buildThemeV2

internal data class DemoColors(
  val background: Color,
  val surface: Color,
  val content: Color,
  val mutedContent: Color,
  val border: Color,
  val inputBackground: Color,
  val scrim: Color,
  val focusRing: Color,
  val error: Color,
  val success: Color,
  val highlight: Color,
)

private val LightDemoColors = DemoColors(
  background = Color(0xFFFFFFFF),
  surface = Color(0xFFFFFFFF),
  content = Color(0xFF1D1D1F),
  mutedContent = Color(0xFF52525B),
  border = Color.Black,
  inputBackground = Color(0xFFEDEDED),
  scrim = Color(0x52000000),
  focusRing = Color.Black,
  error = Color(0xFFEF4444),
  success = Color(0xFF10B981),
  highlight = Color(0xFF8B5CF6),
)

private val DarkDemoColors = LightDemoColors.copy(
  background = Color(0xFF121212),
  surface = Color(0xFF242424),
  content = Color(0xFFE5E5E5),
  mutedContent = Color(0xFFA1A1AA),
  border = Color.White,
  inputBackground = Color(0xFF303030),
)

internal val DemoTheme = buildThemeV2 {
  name = "DemoTheme"
  defaultTextStyle = TextStyle(fontFamily = FontFamily.Monospace)
  defaultContentColor = LightDemoColors.content
  defaultIndication = rememberRippleIndication(color = Color.Black)
  provide(LightDemoColors)
  colorScheme(ColorScheme.Dark) {
    defaultContentColor = DarkDemoColors.content
    defaultIndication = rememberRippleIndication(color = Color.White)
    provide(DarkDemoColors)
  }
}
