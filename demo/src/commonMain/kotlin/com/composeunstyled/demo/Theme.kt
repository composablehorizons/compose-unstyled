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

import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.composables.compose.ripple.rememberRippleIndication
import com.composeunstyled.theme.ColorScheme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildThemeV2

internal val DemoTheme = buildThemeV2 {
  name = "DemoTheme"
  defaultTextStyle = TextStyle(fontFamily = FontFamily.Monospace)
  colorSchemeTransitionSpec = tween(durationMillis = 200)
  defaultContentColor = Color(0xFF1D1D1F)
  defaultIndication = rememberRippleIndication(color = Color.Black)
  properties[colors] = mapOf(
    backgroundToken to Color(0xFFFFFFFF),
    surfaceToken to Color(0xFFFFFFFF),
    contentToken to Color(0xFF1D1D1F),
    mutedContentToken to Color(0xFF52525B),
    outlineToken to Color(0x29000000),
    shadowToken to Color.Black,
    inputBackgroundToken to Color(0xFFEDEDED),
    scrimToken to Color(0x52000000),
    focusToken to Color(0xFF3B82F6),
    errorToken to Color(0xFFEF4444),
    successToken to Color(0xFF10B981),
    highlightToken to Color(0xFF8B5CF6),
  )
  colorScheme(ColorScheme.Dark) {
    defaultContentColor = Color(0xFFE5E5E5)
    defaultIndication = rememberRippleIndication(color = Color.White)
    properties[colors] = mapOf(
      backgroundToken to Color(0xFF121212),
      surfaceToken to Color(0xFF242424),
      contentToken to Color(0xFFE5E5E5),
      mutedContentToken to Color(0xFFA1A1AA),
      outlineToken to Color(0x3DFFFFFF),
      inputBackgroundToken to Color(0xFF303030),
    )
  }
}

internal val colors = ThemeProperty<Color>("colors")

internal val backgroundToken = ThemeToken<Color>("background")
internal val surfaceToken = ThemeToken<Color>("surface")
internal val contentToken = ThemeToken<Color>("content")
internal val mutedContentToken = ThemeToken<Color>("mutedContent")
internal val outlineToken = ThemeToken<Color>("outline")
internal val shadowToken = ThemeToken<Color>("shadow")
internal val inputBackgroundToken = ThemeToken<Color>("inputBackground")
internal val scrimToken = ThemeToken<Color>("scrim")
internal val focusToken = ThemeToken<Color>("focus")
internal val errorToken = ThemeToken<Color>("error")
internal val successToken = ThemeToken<Color>("success")
internal val highlightToken = ThemeToken<Color>("highlight")
