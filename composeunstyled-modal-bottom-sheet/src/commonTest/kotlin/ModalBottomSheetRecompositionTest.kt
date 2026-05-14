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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import com.composeunstyled.test.runComposeRecompositionTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test

class ModalBottomSheetRecompositionTest {
  @Test
  fun expanding_from_hidden_composes_content_at_most_twice() = runComposeRecompositionTest {
    lateinit var state: ModalBottomSheetState
    lateinit var scope: CoroutineScope

    setContent {
      scope = rememberCoroutineScope()
      state = rememberModalBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
      )

      UnstyledModalBottomSheet(state) {
        Sheet {
          RecompositionCount("sheet-content")
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitUntil { state.isIdle }
    resetRecompositionCounts()

    scope.launch {
      state.animateTo(SheetDetent.FullyExpanded)
    }
    waitUntil { state.isIdle && state.currentDetent == SheetDetent.FullyExpanded }

    // Android currently composes this once, while JVM composes it twice.
    assertThat(recompositionCount("sheet-content")).isLessThanOrEqualTo(2)
  }

  @Test
  fun expanding_to_content_detent_composes_content_at_most_twice() = runComposeRecompositionTest {
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }
    lateinit var state: ModalBottomSheetState
    lateinit var scope: CoroutineScope

    setContent {
      scope = rememberCoroutineScope()
      state = rememberModalBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = listOf(SheetDetent.Hidden, contentDetent),
      )

      UnstyledModalBottomSheet(state) {
        Sheet {
          RecompositionCount("sheet-content")
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitUntil { state.isIdle }
    resetRecompositionCounts()

    scope.launch {
      state.animateTo(contentDetent)
    }
    waitUntil { state.isIdle && state.currentDetent == contentDetent }

    // Android currently composes this once, while JVM composes it twice.
    assertThat(recompositionCount("sheet-content")).isLessThanOrEqualTo(2)
  }

  @Test
  fun invalidating_content_sized_detent_does_not_recompose_content() = runComposeRecompositionTest {
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }
    lateinit var state: ModalBottomSheetState

    setContent {
      state = rememberModalBottomSheetState(
        initialDetent = contentDetent,
        detents = listOf(contentDetent),
      )

      UnstyledModalBottomSheet(state) {
        Sheet {
          RecompositionCount("sheet-content")
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    state.invalidateDetents()
    waitForIdle()

    assertThat(recompositionCount("sheet-content")).isEqualTo(0)
  }

  @Test
  fun updating_detents_does_not_recompose_content_sized_content() = runComposeRecompositionTest {
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }
    val halfDetent = SheetDetent("half") { containerHeight, _ ->
      containerHeight / 2
    }
    lateinit var state: ModalBottomSheetState

    setContent {
      state = rememberModalBottomSheetState(
        initialDetent = contentDetent,
        detents = listOf(contentDetent),
      )

      UnstyledModalBottomSheet(state) {
        Sheet {
          RecompositionCount("sheet-content")
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    state.bottomSheetState.detents = listOf(halfDetent, contentDetent)
    waitForIdle()

    assertThat(recompositionCount("sheet-content")).isEqualTo(0)
  }

  @Test
  fun resizing_container_based_detent_recomposes_content_once() = runComposeRecompositionTest {
    val containerDetent = SheetDetent("container") { containerHeight, _ ->
      containerHeight / 2
    }
    var containerSize by mutableStateOf(300.dp)
    lateinit var state: ModalBottomSheetState

    setContent {
      Box(Modifier.requiredSize(containerSize)) {
        state = rememberModalBottomSheetState(
          initialDetent = containerDetent,
          detents = listOf(containerDetent),
        )

        UnstyledModalBottomSheet(state) {
          Sheet {
            RecompositionCount("sheet-content")
            Box(Modifier.fillMaxWidth().height(300.dp))
          }
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    containerSize = 400.dp
    waitForIdle()

    assertThat(recompositionCount("sheet-content")).isEqualTo(1)
  }
}
