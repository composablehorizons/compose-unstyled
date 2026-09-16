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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.composables.compose.ripple.rememberRippleIndication
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme

internal val DemoTheme = buildTheme {
  val isDarkTheme = isSystemInDarkTheme()
  val backgroundColor = animateColorAsState(
    targetValue = if (isDarkTheme) Color(0xFF121212) else Color(0xFFFFFFFF),
  ).value
  val contentColor = animateColorAsState(
    targetValue = if (isDarkTheme) Color(0xFFE5E5E5) else Color(0xFF1D1D1F),
  ).value

  name = "DemoTheme"
  defaultTextStyle = TextStyle(fontFamily = FontFamily.Monospace)
  defaultContentColor = contentColor
  defaultIndication = rememberRippleIndication(
    color = if (isDarkTheme) Color.White else Color.Black,
  )
  properties[demoColors] = mapOf(
    demoBackground to backgroundColor,
    demoSurface to if (isDarkTheme) Color(0xFF242424) else backgroundColor,
    demoContent to contentColor,
    demoMutedContent to if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF52525B),
    demoOutline to if (isDarkTheme) Color(0x3DFFFFFF) else Color(0x29000000),
    demoShadow to Color.Black,
    demoInputBackground to if (isDarkTheme) Color(0xFF303030) else Color(0xFFEDEDED),
    demoScrim to Color(0x52000000),
    demoFocus to Color(0xFF3B82F6),
    demoError to Color(0xFFEF4444),
    demoSuccess to Color(0xFF10B981),
    demoHighlight to Color(0xFF8B5CF6),
    demoTransparent to Color.Transparent,
  )
}

internal val demoColors = ThemeProperty<Color>("colors")

internal val demoBackground = ThemeToken<Color>("background")
internal val demoSurface = ThemeToken<Color>("surface")
internal val demoContent = ThemeToken<Color>("content")
internal val demoMutedContent = ThemeToken<Color>("mutedContent")
internal val demoOutline = ThemeToken<Color>("outline")
internal val demoShadow = ThemeToken<Color>("shadow")
internal val demoInputBackground = ThemeToken<Color>("inputBackground")
internal val demoScrim = ThemeToken<Color>("scrim")
internal val demoFocus = ThemeToken<Color>("focus")
internal val demoError = ThemeToken<Color>("error")
internal val demoSuccess = ThemeToken<Color>("success")
internal val demoHighlight = ThemeToken<Color>("highlight")
internal val demoTransparent = ThemeToken<Color>("transparent")
