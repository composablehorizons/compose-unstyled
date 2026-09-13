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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.ProvideWindowWidthBreakpoints
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.Text
import com.composeunstyled.WidthBreakpoint
import com.composeunstyled.WindowWidthBreakpoints
import com.composeunstyled.buildModifier
import com.composeunstyled.currentWindowWidthBreakpoint

private val Compact = WidthBreakpoint("compact")
private val Medium = WidthBreakpoint("medium")
private val Expanded = WidthBreakpoint("expanded")

private val DemoWidthBreakpoints = WindowWidthBreakpoints {
  Compact startsAt 0.dp
  Medium startsAt 600.dp
  Expanded startsAt 840.dp
}

@Composable
fun BreakpointsDemo() {
  ProvideWindowWidthBreakpoints(DemoWidthBreakpoints) {
    val widthBreakpoint = currentWindowWidthBreakpoint()
    val cardShape = RectangleShape
    val imageShape = RectangleShape

    Stack(
      modifier = Modifier
        .widthIn(max = if (widthBreakpoint isAtLeast Expanded) 860.dp else 360.dp)
        .border(1.dp, Color.Black)
        .clip(cardShape)
        .background(Color.White)
        .padding(14.dp),
      orientation = if (widthBreakpoint isAtLeast Expanded) {
        StackOrientation.Horizontal
      } else {
        StackOrientation.Vertical
      },
      crossAxisAlignment = CrossAxisAlignment.Start,
      spacing = 18.dp,
    ) {
      Image(
        painter = PrototypeImagePainter,
        contentDescription = "IMAGE",
        modifier = Modifier
          .clip(imageShape) then buildModifier {
          if (widthBreakpoint isAtLeast Expanded) {
            add(Modifier.size(width = 320.dp, height = 280.dp))
          } else {
            add(Modifier.fillMaxWidth().height(280.dp))
          }
        },
      )

      Stack(
        modifier = Modifier then buildModifier {
          if (widthBreakpoint isAtLeast Expanded) {
            add(Modifier.weight(1f))
          } else {
            add(Modifier.fillMaxWidth())
          }
        },
        orientation = StackOrientation.Vertical,
        spacing = 12.dp,
      ) {
        Text(
          text = "ADAPTIVE LAYOUTS",
          color = Color.Black,
          fontSize = 24.sp,
          lineHeight = 30.sp,
        )
        Text(
          text = "THIS CARD SWITCHES FROM VERTICAL TO HORIZONTAL AT ${Expanded.name}",
          color = Color.Black,
          fontSize = 15.sp,
          lineHeight = 22.sp,
        )
      }
    }
  }
}
