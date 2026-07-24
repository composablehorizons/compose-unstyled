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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DrawerAndroidTest {

  @Test
  fun pressingBackClosesOpenDrawer() = runComposeUiTest {
    lateinit var state: DrawerState

    setContent {
      state = rememberDrawerState(
        initialSnapPoint = DrawerSnapPoint.Open,
      )
      UnstyledDrawer(
        state = state,
        modifier = Modifier.size(100.dp),
      ) {
        Viewport(
          modifier = Modifier.size(100.dp),
        ) {
          Panel(
            modifier = Modifier.fillMaxWidth(),
          ) {
            Content(
              modifier = Modifier.fillMaxWidth().height(100.dp),
            ) {
              Box(Modifier.size(100.dp))
            }
          }
        }
      }
    }

    Espresso.pressBack()

    waitUntil {
      state.currentSnapPoint == DrawerSnapPoint.Closed
    }
    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Closed)
  }

  @Test
  fun pressingBackDoesNotCloseDisabledDrawer() = runComposeUiTest {
    lateinit var state: DrawerState
    var fallbackBackHandlerCalled = false

    setContent {
      EscapeHandler {
        fallbackBackHandlerCalled = true
      }
      state = rememberDrawerState(
        initialSnapPoint = DrawerSnapPoint.Open,
      )
      UnstyledDrawer(
        state = state,
        enabled = false,
        modifier = Modifier.size(100.dp),
      ) {
        Viewport(
          modifier = Modifier.size(100.dp),
        ) {
          Panel(
            modifier = Modifier.fillMaxWidth(),
          ) {
            Content(
              modifier = Modifier.fillMaxWidth().height(100.dp),
            ) {
              Box(Modifier.size(100.dp))
            }
          }
        }
      }
    }

    Espresso.pressBack()
    waitForIdle()

    assertThat(fallbackBackHandlerCalled).isEqualTo(true)
    assertThat(state.currentSnapPoint).isEqualTo(DrawerSnapPoint.Open)
  }
}
