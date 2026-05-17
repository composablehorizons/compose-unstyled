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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.HeadlessException
import java.awt.Toolkit

@Composable
internal actual fun isTouchDevice(): Boolean {
  return remember { currentDeviceHasTouchCapabilities() }
}

internal fun currentDeviceHasTouchCapabilities(): Boolean {
  val toolkit = try {
    Toolkit.getDefaultToolkit()
  } catch (_: HeadlessException) {
    return false
  }

  return listOf(
    "awt.touch.support",
    "sun.awt.touch.support",
  ).any { propertyName ->
    toolkit.getDesktopProperty(propertyName).isTouchSupportEnabled()
  }
}

internal fun Any?.isTouchSupportEnabled(): Boolean {
  return when (this) {
    is Boolean -> this
    is Number -> toInt() > 0
    is String -> equals("true", ignoreCase = true) || toIntOrNull()?.let { it > 0 } == true
    else -> false
  }
}
