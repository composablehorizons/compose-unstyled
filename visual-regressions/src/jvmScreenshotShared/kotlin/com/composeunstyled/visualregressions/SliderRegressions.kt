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
package com.composeunstyled.visualregressions

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.SliderState
import com.composeunstyled.UnstyledSlider

val SliderRegressionScreenshots = listOf(
  VisualRegressionScreenshot(
    name = "slider-vertical-values",
    width = 420,
    height = 320,
    content = { VerticalSliderValuesRegression() },
  ),
  VisualRegressionScreenshot(
    name = "slider-horizontal-rtl",
    width = 420,
    height = 260,
    content = { HorizontalSliderRtlRegression() },
  ),
)

@Composable
fun VerticalSliderValuesRegression() {
  Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
    VerticalSlider(value = 0f)
    VerticalSlider(value = 0.5f)
    VerticalSlider(value = 1f)
    VerticalSlider(value = 0.75f, reverseDirection = true)
  }
}

@Composable
fun HorizontalSliderRtlRegression() {
  RtlLayout {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
      HorizontalSlider(value = 0.25f)
      HorizontalSlider(value = 0.5f)
      HorizontalSlider(value = 0.75f)
    }
  }
}

@Composable
private fun HorizontalSlider(value: Float) {
  UnstyledSlider(
    value = value,
    onValueChange = {},
    modifier = Modifier.width(280.dp).height(36.dp),
    track = { state ->
      HorizontalSliderTrack(state)
    },
    thumb = {
      Box(
        modifier = Modifier.size(36.dp),
        contentAlignment = Alignment.Center,
      ) {
        Box(
          Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.Black),
        )
      }
    },
  )
}

@Composable
private fun HorizontalSliderTrack(state: SliderState) {
  Box(Modifier.width(280.dp).height(36.dp), contentAlignment = Alignment.Center) {
    Box(
      Modifier
        .fillMaxWidth()
        .height(8.dp)
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(100.dp))
        .background(Color(0xFFCACACA)),
      contentAlignment = Alignment.CenterStart,
    ) {
      Box(
        Modifier
          .fillMaxHeight()
          .fillMaxWidth(state.fraction)
          .background(Color.Black),
      )
    }
  }
}

@Composable
private fun VerticalSlider(
  value: Float,
  reverseDirection: Boolean = false,
) {
  UnstyledSlider(
    value = value,
    onValueChange = {},
    orientation = Orientation.Vertical,
    reverseDirection = reverseDirection,
    modifier = Modifier.width(36.dp).height(200.dp),
    track = { state ->
      VerticalSliderTrack(state)
    },
    thumb = {
      Box(
        modifier = Modifier.size(36.dp),
        contentAlignment = Alignment.Center,
      ) {
        Box(
          Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.Black),
        )
      }
    },
  )
}

@Composable
private fun VerticalSliderTrack(state: SliderState) {
  Box(Modifier.width(36.dp).height(200.dp), contentAlignment = Alignment.Center) {
    Box(
      Modifier
        .width(8.dp)
        .fillMaxHeight()
        .padding(vertical = 16.dp)
        .clip(RoundedCornerShape(100.dp))
        .background(Color(0xFFCACACA)),
      contentAlignment = Alignment.BottomCenter,
    ) {
      Box(
        Modifier
          .fillMaxWidth()
          .fillMaxHeight(state.fraction)
          .background(Color.Black),
      )
    }
  }
}
