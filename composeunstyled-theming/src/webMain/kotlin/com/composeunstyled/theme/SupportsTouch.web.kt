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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import org.w3c.dom.events.Event

@Composable
internal actual fun supportsTouch(): Boolean {
  val mediaQueryList = remember { window.matchMedia("(any-pointer: coarse)") }
  var anyPointerCoarse by remember { mutableStateOf(mediaQueryList.matches) }

  DisposableEffect(mediaQueryList) {
    val listener: (Event) -> Unit = {
      anyPointerCoarse = mediaQueryList.matches
    }

    mediaQueryList.addEventListener("change", listener)

    onDispose {
      mediaQueryList.removeEventListener("change", listener)
    }
  }

  return browserHasTouchCapabilities(
    maxTouchPoints = window.navigator.maxTouchPoints,
    anyPointerCoarse = anyPointerCoarse,
  )
}

internal fun browserHasTouchCapabilities(
  maxTouchPoints: Int?,
  anyPointerCoarse: Boolean,
): Boolean {
  return (maxTouchPoints ?: 0) > 0 || anyPointerCoarse
}
