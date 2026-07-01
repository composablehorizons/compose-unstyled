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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

private val LocalPortalState = staticCompositionLocalOf<PortalState?> { null }

private class PortalState {
  val entries = mutableStateListOf<PortalEntry>()

  fun addOrUpdate(id: Any, content: @Composable () -> Unit) {
    val index = entries.indexOfFirst { it.id == id }
    if (index == -1) {
      entries.add(PortalEntry(id = id, content = content))
    } else {
      entries[index] = entries[index].copy(content = content)
    }
  }

  fun remove(id: Any) {
    entries.removeAll { it.id == id }
  }
}

private data class PortalEntry(
  val id: Any,
  val content: @Composable () -> Unit,
)

@Composable
fun PortalHost(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val state = remember { PortalState() }

  CompositionLocalProvider(LocalPortalState provides state) {
    Box(modifier) {
      content()
      state.entries.forEach { entry ->
        key(entry.id) {
          Box(Modifier.matchParentSize()) {
            entry.content()
          }
        }
      }
    }
  }
}

@Composable
fun Portal(
  content: @Composable () -> Unit,
) {
  val state = LocalPortalState.current
  val id = remember { Any() }

  if (state == null) {
    return
  }

  SideEffect {
    state.addOrUpdate(id = id, content = content)
  }

  DisposableEffect(state, id) {
    onDispose {
      state.remove(id)
    }
  }
}
