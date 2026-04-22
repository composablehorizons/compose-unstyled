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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@OptIn(InternalComposeUiApi::class)
@Composable
internal actual fun KeyEventObserver(onEvent: (KeyEvent) -> Boolean) {
  DisposableEffect(Unit) {
    val listener: (Event) -> Unit = { event ->
      event as KeyboardEvent
      val consumed = onEvent(
        KeyEvent(
          key = Key(event.keyCode.toLong()),
          type = when (event.type) {
            "keydown" -> KeyEventType.KeyDown
            "keyup" -> KeyEventType.KeyUp
            else -> KeyEventType.Unknown
          },
        ),
      )
      if (consumed) {
        event.preventDefault()
        event.stopPropagation()
      }
    }

    document.addEventListener("keydown", listener)
    document.addEventListener("keyup", listener)

    onDispose {
      document.removeEventListener("keydown", listener)
      document.removeEventListener("keyup", listener)
    }
  }
}
