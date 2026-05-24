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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.uripainter.rememberUriPainter
import com.composeunstyled.Breakpoint
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.ProvideBreakpoints
import com.composeunstyled.ScreenBreakpoints
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.buildModifier
import com.composeunstyled.currentWidthBreakpoint

@Composable
fun BreakpointsDemo() {
  val sm = Breakpoint("sm")
  val md = Breakpoint("md")
  val lg = Breakpoint("lg")
  val xl = Breakpoint("xl")

  val widthBreakpoints = ScreenBreakpoints {
    sm at 640.dp
    md at 768.dp
    lg at 1024.dp
    xl at 1280.dp
  }

  ProvideBreakpoints(widthBreakpoints = widthBreakpoints) {
    val widthBreakpoint = currentWidthBreakpoint()
    val expanded = widthBreakpoint isAtLeast lg
    val orientation = if (expanded) StackOrientation.Horizontal else StackOrientation.Vertical
    val cardShape = RoundedCornerShape(24.dp)
    val imageShape = RoundedCornerShape(18.dp)
    val imagePainter = rememberUriPainter(
      "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee" +
        "?auto=format&fit=crop&w=1200&q=80",
    )

    Stack(
      modifier = Modifier
        .widthIn(max = if (expanded) 860.dp else 360.dp)
        .dropShadow(
          shape = cardShape,
          shadow = Shadow(
            radius = 28.dp,
            spread = 0.dp,
            offset = DpOffset(x = 0.dp, y = 14.dp),
            color = Color(0xFF18181B),
            alpha = 0.16f,
          ),
        )
        .clip(cardShape)
        .background(Color.White)
        .padding(14.dp),
      orientation = orientation,
      crossAxisAlignment = CrossAxisAlignment.Start,
      spacing = 18.dp,
    ) {
      Image(
        painter = imagePainter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .clip(imageShape)
          .background(Color(0xFFE4E4E7)) then buildModifier {
          if (expanded) {
            add(Modifier.size(width = 320.dp, height = 280.dp))
          } else {
            add(Modifier.fillMaxWidth().height(280.dp))
          }
        },
      )

      Stack(
        modifier = Modifier then buildModifier {
          if (expanded) {
            add(Modifier.weight(1f))
          } else {
            add(Modifier.fillMaxWidth())
          }
        },
        orientation = StackOrientation.Vertical,
        spacing = 12.dp,
      ) {
        BasicText(
          text = "Adaptive layouts",
          style = TextStyle(
            color = Color(0xFF18181B),
            fontSize = 24.sp,
            lineHeight = 30.sp,
          ),
        )
        BasicText(
          text = "This card switches from vertical to horizontal at ${lg.name}",
          style = TextStyle(
            color = Color(0xFF52525B),
            fontSize = 15.sp,
            lineHeight = 22.sp,
          ),
        )
      }
    }
  }
}
