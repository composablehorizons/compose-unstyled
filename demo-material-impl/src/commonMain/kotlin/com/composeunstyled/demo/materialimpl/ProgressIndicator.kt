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
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@file:Suppress("unused", "UNUSED_PARAMETER")

package com.composeunstyled.demo.material3api

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledProgress

private val LinearProgressHeight = 4.dp
private val LinearProgressWidth = 240.dp
private val CircularProgressSize = 40.dp

@Composable
fun LinearProgressIndicator(
  progress: () -> Float,
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.linearColor,
  trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
  gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
  drawStopIndicator: DrawScope.() -> Unit = {},
) {
  val coercedProgress = progress().coerceIn(0f, 1f)
  UnstyledProgress(
    progress = coercedProgress,
    modifier = modifier.width(LinearProgressWidth).height(LinearProgressHeight),
  ) {
    Canvas(Modifier.fillMaxSize()) {
      val gapPx = gapSize.toPx()
      val stopRadius = size.height / 2f
      val stopCenter = Offset(size.width - stopRadius, size.height / 2f)
      val activeEnd = (size.width * coercedProgress - gapPx / 2f).coerceAtLeast(0f)
      val inactiveStart = (size.width * coercedProgress + gapPx / 2f).coerceAtMost(size.width)
      val cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)

      if (activeEnd > 0f) {
        drawRoundRect(
          color = color,
          size = Size(activeEnd, size.height),
          cornerRadius = cornerRadius,
        )
      }
      if (inactiveStart < size.width - stopRadius * 3f) {
        drawRoundRect(
          color = trackColor,
          topLeft = Offset(inactiveStart, 0f),
          size = Size(size.width - inactiveStart - stopRadius * 3f, size.height),
          cornerRadius = cornerRadius,
        )
      }
      drawCircle(color = color, radius = stopRadius, center = stopCenter)
    }
  }
}

@Composable
fun LinearProgressIndicator(
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.linearColor,
  trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
) {
  UnstyledProgress(
    modifier = modifier.width(LinearProgressWidth).height(LinearProgressHeight),
  ) {
    Box(Modifier.fillMaxSize().background(trackColor, MaterialTheme.shapes.extraSmall))
    Box(
      Modifier
        .fillMaxWidth(0.35f)
        .fillMaxHeight()
        .background(color, MaterialTheme.shapes.extraSmall),
    )
  }
}

@Composable
fun CircularProgressIndicator(
  progress: () -> Float,
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.circularColor,
  strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
  trackColor: Color = Color.Transparent,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
  gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
) {
  Canvas(modifier.size(CircularProgressSize)) {
    drawArc(
      color = trackColor,
      startAngle = 0f,
      sweepAngle = 360f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
    )
    drawArc(
      color = color,
      startAngle = -90f,
      sweepAngle = progress().coerceIn(0f, 1f) * 360f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
    )
  }
}

@Composable
fun CircularProgressIndicator(
  modifier: Modifier = Modifier,
  color: Color = ProgressIndicatorDefaults.circularColor,
  strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
  trackColor: Color = Color.Transparent,
  strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularIndeterminateStrokeCap,
) {
  Canvas(modifier.size(CircularProgressSize)) {
    if (trackColor != Color.Transparent) {
      drawArc(
        color = trackColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
      )
    }
    drawArc(
      color = color,
      startAngle = -90f,
      sweepAngle = 270f,
      useCenter = false,
      style = Stroke(width = strokeWidth.toPx(), cap = strokeCap),
    )
  }
}
