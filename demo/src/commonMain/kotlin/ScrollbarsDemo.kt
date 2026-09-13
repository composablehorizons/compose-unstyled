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
package com.composeunstyled.demo

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import com.composeunstyled.Thumb
import com.composeunstyled.ThumbVisibility
import com.composeunstyled.UnstyledHorizontalScrollbar
import com.composeunstyled.UnstyledVerticalScrollbar
import com.composeunstyled.rememberScrollbarState
import kotlin.time.Duration.Companion.seconds

@Composable
fun ScrollbarsDemo() {
  VerticalScrollbarsDemo()
}

@Composable
fun VerticalScrollbarsDemo() {
  Box(
    modifier = Modifier.fillMaxSize()
      .padding(vertical = 40.dp)
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.TopCenter,
  ) {
    val desserts = listOf(
      "CUPCAKE",
      "DONUT",
      "ECLAIR",
      "FROYO",
      "GINGERBREAD",
      "HONEYCOMB",
      "ICE CREAM SANDWICH",
      "JELLY BEAN",
      "KITKAT",
      "LOLLIPOP",
      "MARSHMALLOW",
      "NOUGAT",
      "OREO",
      "PIE",
      "QUINCE",
      "RED VELVET CAKE",
      "SNOW CONE",
      "TIRAMISU",
      "UPSIDE-DOWN CAKE",
      "VANILLA CUSTARD",
      "WAFFLE",
      "XMAS PUDDING",
      "YOGURT PARFAIT",
      "ZABAGLIONE",
    )

    val state = rememberScrollState()
    val scrollbarState = rememberScrollbarState(state)

    Box(
      modifier = Modifier
        .widthIn(max = 400.dp)
        .background(Color.White, RectangleShape)
        .border(1.dp, Color.Black, RectangleShape)
        .fillMaxSize(),
    ) {
      Column(
        Modifier.verticalScroll(state)
          .padding(start = 4.dp, end = 16.dp)
          .fillMaxWidth()
          .padding(8.dp),
      ) {
        Text(
          "DESERTS",
          Modifier.padding(4.dp),
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(12.dp))
        desserts.forEach { i ->
          Text(i, Modifier.padding(4.dp).fillMaxWidth())
          Spacer(Modifier.height(12.dp))
        }
      }
      UnstyledVerticalScrollbar(
        scrollbarState = scrollbarState,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .width(12.dp)
          .fillMaxHeight(),
      ) {
        Thumb(
          modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .height(12.dp)
            .background(Color.Black, RectangleShape),
          thumbVisibility = ThumbVisibility.AlwaysVisible,
        )
      }
    }
  }
}

@Composable
fun HorizontalScrollbarsDemo() {
  Box(
    modifier = Modifier.fillMaxSize()
      .padding(vertical = 40.dp),
    contentAlignment = Alignment.TopCenter,
  ) {
    val state = rememberScrollState()
    val scrollbarState = rememberScrollbarState(state)

    Box(
      modifier = Modifier
        .widthIn(max = 400.dp)
        .background(Color.White, RectangleShape)
        .border(1.dp, Color.Black, RectangleShape)
        .wrapContentHeight(),
    ) {
      Row(
        Modifier.horizontalScroll(state)
          .systemBarsPadding()
          .navigationBarsPadding()
          .padding(start = 4.dp, end = 16.dp)
          .fillMaxWidth()
          .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        (1..100).forEach { i ->
          Box(Modifier.size(90.dp).clip(RectangleShape).background(Color.Black))
        }
      }
      UnstyledHorizontalScrollbar(
        scrollbarState = scrollbarState,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .height(12.dp)
          .fillMaxWidth(),
      ) {
        Thumb(
          modifier = Modifier
            .fillMaxHeight()
            .padding(2.dp)
            .width(12.dp)
            .background(Color.Black, RectangleShape),
          thumbVisibility = ThumbVisibility.HideWhileIdle(
            enter = fadeIn(),
            exit = fadeOut(),
            hideDelay = 1.seconds,
          ),
        )
      }
    }
  }
}
