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

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CurrentBreakpointEmissionTest {
  private val compact = WidthBreakpoint("compact")
  private val medium = WidthBreakpoint("medium")
  private val short = HeightBreakpoint("short")
  private val tall = HeightBreakpoint("tall")

  @Test
  fun current_window_width_breakpoint_ignores_pixel_changes() = runComposeUiTest {
    val windowInfo = TestWindowInfo(DpSize(width = 500.dp, height = 800.dp))
    val widthBreakpoints = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
    }
    val emissions = mutableListOf<WidthBreakpoint>()

    setContent {
      CompositionLocalProvider(
        LocalWindowInfo provides windowInfo,
      ) {
        ProvideWindowWidthBreakpoints(widthBreakpoints) {
          val breakpoint = currentWindowWidthBreakpoint()

          LaunchedEffect(breakpoint) {
            emissions += breakpoint.value
          }
        }
      }
    }

    waitForIdle()
    assertThat(emissions).isEqualTo(listOf(compact))

    runOnIdle {
      windowInfo.containerDpSize = DpSize(width = 501.dp, height = 800.dp)
    }
    waitForIdle()
    assertThat(emissions).isEqualTo(listOf(compact))

    runOnIdle {
      windowInfo.containerDpSize = DpSize(width = 600.dp, height = 800.dp)
    }
    waitForIdle()
    assertThat(emissions).isEqualTo(listOf(compact, medium))
  }

  @Test
  fun current_window_height_breakpoint_ignores_pixel_changes() = runComposeUiTest {
    val windowInfo = TestWindowInfo(DpSize(width = 500.dp, height = 500.dp))
    val heightBreakpoints = WindowHeightBreakpoints {
      short startsAt 0.dp
      tall startsAt 720.dp
    }
    val emissions = mutableListOf<HeightBreakpoint>()

    setContent {
      CompositionLocalProvider(
        LocalWindowInfo provides windowInfo,
      ) {
        ProvideWindowHeightBreakpoints(heightBreakpoints) {
          val breakpoint = currentWindowHeightBreakpoint()

          LaunchedEffect(breakpoint) {
            emissions += breakpoint.value
          }
        }
      }
    }

    waitForIdle()
    assertThat(emissions).isEqualTo(listOf(short))

    runOnIdle {
      windowInfo.containerDpSize = DpSize(width = 500.dp, height = 501.dp)
    }
    waitForIdle()
    assertThat(emissions).isEqualTo(listOf(short))

    runOnIdle {
      windowInfo.containerDpSize = DpSize(width = 500.dp, height = 720.dp)
    }
    waitForIdle()
    assertThat(emissions).isEqualTo(listOf(short, tall))
  }

  @Test
  fun current_window_width_breakpoint_emits_when_breakpoint_scale_changes() = runComposeUiTest {
    val windowInfo = TestWindowInfo(DpSize(width = 500.dp, height = 800.dp))
    val firstScale = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 600.dp
    }
    val secondScale = WindowWidthBreakpoints {
      compact startsAt 0.dp
      medium startsAt 900.dp
    }
    var widthBreakpoints by mutableStateOf(firstScale)
    val emissions = mutableListOf<ResolvedWidthBreakpoint>()

    setContent {
      CompositionLocalProvider(
        LocalWindowInfo provides windowInfo,
      ) {
        ProvideWindowWidthBreakpoints(widthBreakpoints) {
          val breakpoint = currentWindowWidthBreakpoint()

          LaunchedEffect(breakpoint) {
            emissions += breakpoint
          }
        }
      }
    }

    waitForIdle()
    assertThat(emissions.map { it.value }).isEqualTo(listOf(compact))

    runOnIdle {
      widthBreakpoints = secondScale
    }
    waitForIdle()
    assertThat(emissions.map { it.value }).isEqualTo(listOf(compact, compact))
  }

  private class TestWindowInfo(
    containerDpSize: DpSize,
  ) : WindowInfo {
    override val isWindowFocused: Boolean = true

    override var containerDpSize: DpSize by mutableStateOf(containerDpSize)
  }
}
