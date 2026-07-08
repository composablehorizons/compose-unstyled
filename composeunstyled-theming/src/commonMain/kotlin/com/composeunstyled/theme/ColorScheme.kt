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
package com.composeunstyled.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.jvm.JvmInline

/**
 * Used by elements to choose colors that maintain a consistent look and feel across the application.
 */
@JvmInline
value class ColorScheme internal constructor(@Suppress("unused") private val value: Int) {
  companion object {
    val Light = ColorScheme(0)
    val Dark = ColorScheme(1)
  }
}

/**
 * The current [ColorScheme] to use in this composition.
 */
val LocalColorScheme = staticCompositionLocalOf<ColorScheme> {
  error(
    "Tried to access LocalColorScheme without it being set up." +
      " This value is provided by default in themes built using buildTheme {}." +
      " If you are trying to get the system's ColorScheme, use currentSystemColorScheme() instead",
  )
}

/**
 * Returns the currently set [ColorScheme] provided by the system.
 */
@Composable
fun currentSystemColorScheme(): ColorScheme {
  return if (isSystemInDarkTheme()) ColorScheme.Dark else ColorScheme.Light
}
