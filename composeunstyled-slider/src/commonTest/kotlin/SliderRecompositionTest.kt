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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.composeunstyled.test.runComposeRecompositionTest
import kotlin.test.Test

class SliderRecompositionTest {
  @Test
  fun updatingValueRecomposesTrackAndThumbContentOnce() = runComposeRecompositionTest {
    var value by mutableFloatStateOf(0.2f)

    setContent {
      TestSlider(
        value = value,
        track = {
          RecompositionCount("track-content")
          Box(Modifier.size(1.dp))
        },
        thumb = {
          RecompositionCount("thumb-content")
          Box(Modifier.size(1.dp))
        },
      )
    }

    waitForIdle()
    resetRecompositionCounts()

    value = 0.8f
    waitForIdle()

    assertThat(recompositionCount("track-content")).isEqualTo(1)
    assertThat(recompositionCount("thumb-content")).isEqualTo(1)
  }

  @Test
  fun resizingSliderDoesNotRecomposeTrackOrThumbContent() = runComposeRecompositionTest {
    var sliderWidth by mutableIntStateOf(200)

    setContent {
      Layout(
        content = {
          TestSlider(
            value = 0.5f,
            track = {
              RecompositionCount("track-content")
              Box(Modifier.size(1.dp))
            },
            thumb = {
              RecompositionCount("thumb-content")
              Box(Modifier.size(1.dp))
            },
          )
        },
      ) { measurables, _ ->
        val width = sliderWidth
        val height = 40.dp.roundToPx()
        val placeable = measurables.single().measure(Constraints.fixed(width, height))

        layout(width, height) {
          placeable.place(0, 0)
        }
      }
    }

    waitForIdle()
    resetRecompositionCounts()

    sliderWidth = 260
    waitForIdle()

    assertThat(recompositionCount("track-content")).isEqualTo(0)
    assertThat(recompositionCount("thumb-content")).isEqualTo(0)
  }

  @Test
  fun parentRecompositionDoesNotRecomposeTrackOrThumbContent() = runComposeRecompositionTest {
    var parentRecompositions by mutableIntStateOf(0)

    setContent {
      parentRecompositions

      val track: @Composable (SliderState) -> Unit = remember {
        {
          RecompositionCount("track-content")
          Box(Modifier.size(1.dp))
        }
      }
      val thumb: @Composable (SliderState) -> Unit = remember {
        {
          RecompositionCount("thumb-content")
          Box(Modifier.size(1.dp))
        }
      }

      TestSlider(
        value = 0.5f,
        track = track,
        thumb = thumb,
      )
    }

    waitForIdle()
    resetRecompositionCounts()

    parentRecompositions++
    waitForIdle()

    assertThat(recompositionCount("track-content")).isEqualTo(0)
    assertThat(recompositionCount("thumb-content")).isEqualTo(0)
  }

  @Composable
  private fun TestSlider(
    value: Float,
    track: @Composable (SliderState) -> Unit,
    thumb: @Composable (SliderState) -> Unit,
  ) {
    UnstyledSlider(
      value = value,
      onValueChange = {},
      modifier = Modifier.width(200.dp).height(40.dp),
      track = track,
      thumb = thumb,
    )
  }
}
