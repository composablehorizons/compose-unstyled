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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledProgressBar
import com.composeunstyled.UnstyledProgressIndicator
import kotlinx.coroutines.delay

@Composable
fun ProgressIndicatorDemo() {
  var hasProgressed by remember { mutableStateOf(false) }

  val progress by animateFloatAsState(
    targetValue = if (hasProgressed) 0.85f else 0.2f,
    animationSpec = tween(durationMillis = 450),
  )
  LaunchedEffect(Unit) {
    delay(500)
    hasProgressed = true
  }

  Box(
    modifier = Modifier.fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF134E5E), Color(0xFF71B280))))
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledProgressIndicator(
      progress = progress,
      modifier = Modifier.width(400.dp).height(24.dp).shadow(4.dp, RoundedCornerShape(100)),
      shape = RoundedCornerShape(100),
      backgroundColor = Color(0xff176153),
    ) {
      UnstyledProgressBar(shape = RoundedCornerShape(100), color = Color(0xffb6eabb))
    }
  }
}
