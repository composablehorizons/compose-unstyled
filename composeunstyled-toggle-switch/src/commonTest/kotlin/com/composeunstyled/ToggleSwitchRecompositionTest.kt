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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.composeunstyled.test.runComposeRecompositionTest
import kotlin.test.Test

class ToggleSwitchRecompositionTest {
  @Test
  fun togglingCheckedRecomposesThumbContentOnce() = runComposeRecompositionTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledSwitch(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier
          .width(58.dp)
          .height(32.dp),
      ) {
        SwitchThumb(
          animationSpec = tween(durationMillis = 1_000),
          modifier = Modifier.size(24.dp),
        ) {
          RecompositionCount("thumb-content")
          Box(Modifier.size(1.dp))
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts("thumb-content")

    checked = true
    waitForIdle()

    assertThat(recompositionCount("thumb-content")).isEqualTo(1)
  }

  @Test
  fun resizingCheckedSwitchContainerDoesNotRecomposeThumbContent() = runComposeRecompositionTest {
    var switchWidth by mutableIntStateOf(58)

    setContent {
      Layout(
        content = {
          UnstyledSwitch(
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.height(32.dp),
          ) {
            SwitchThumb(
              animationSpec = tween(durationMillis = 1_000),
              modifier = Modifier.size(24.dp),
            ) {
              RecompositionCount("thumb-content")
              Box(Modifier.size(1.dp))
            }
          }
        },
      ) { measurables, _ ->
        val width = switchWidth
        val height = 32.dp.roundToPx()
        val placeable = measurables.single().measure(Constraints.fixed(width, height))

        layout(width, height) {
          placeable.place(0, 0)
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts("thumb-content")

    switchWidth = 68
    waitForIdle()

    assertThat(recompositionCount("thumb-content")).isEqualTo(0)
  }

  @Test
  fun togglingCheckedRecomposesTrackAndThumbContentOnce() = runComposeRecompositionTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledSwitch(
        checked = checked,
        onCheckedChange = { checked = it },
      ) {
        Track(
          modifier = Modifier
            .width(58.dp)
            .height(32.dp),
        ) {
          RecompositionCount("track-content")
          Thumb(
            animationSpec = tween(durationMillis = 1_000),
            modifier = Modifier.size(24.dp),
          ) {
            RecompositionCount("thumb-content")
            Box(Modifier.size(1.dp))
          }
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts("track-content", "thumb-content")

    checked = true
    waitForIdle()

    assertThat(recompositionCount("track-content")).isEqualTo(1)
    assertThat(recompositionCount("thumb-content")).isEqualTo(1)
  }

  @Test
  fun resizingLabeledSwitchDoesNotRecomposeTrackOrThumbContent() = runComposeRecompositionTest {
    var switchWidth by mutableIntStateOf(120)

    setContent {
      Layout(
        content = {
          UnstyledSwitch(
            checked = true,
            onCheckedChange = {},
            modifier = Modifier.height(32.dp),
          ) {
            Row {
              Track(
                modifier = Modifier
                  .width(58.dp)
                  .height(32.dp),
              ) {
                RecompositionCount("track-content")
                Thumb(
                  animationSpec = tween(durationMillis = 1_000),
                  modifier = Modifier.size(24.dp),
                ) {
                  RecompositionCount("thumb-content")
                  Box(Modifier.size(1.dp))
                }
              }

              Box(Modifier.size(1.dp))
            }
          }
        },
      ) { measurables, _ ->
        val width = switchWidth
        val height = 32.dp.roundToPx()
        val placeable = measurables.single().measure(Constraints.fixed(width, height))

        layout(width, height) {
          placeable.place(0, 0)
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts("track-content", "thumb-content")

    switchWidth = 140
    waitForIdle()

    assertThat(recompositionCount("track-content")).isEqualTo(0)
    assertThat(recompositionCount("thumb-content")).isEqualTo(0)
  }
}
