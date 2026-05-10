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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.BellDot
import com.composables.icons.lucide.Lucide
import com.composeunstyled.AnchorAlignment
import com.composeunstyled.AnchorSide
import com.composeunstyled.TooltipPanel
import com.composeunstyled.TooltipPlacement
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.UnstyledTooltip
import com.composeunstyled.focusRing

@Composable
fun TooltipDemo() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66)))),
    contentAlignment = Alignment.Center,
  ) {
    Row {
      UnstyledTooltip(
        side = AnchorSide.Top,
        alignment = AnchorAlignment.Center,
        panel = {
          TooltipPanel(
            enter = slideInVertically(tween(150), initialOffsetY = { (it * 0.25).toInt() }) +
              scaleIn(
                animationSpec = tween(150),
                transformOrigin = TransformOrigin(0.5f, 1f),
                initialScale = 0.65f,
              ) + fadeIn(tween(150)),
            exit = fadeOut(tween(250)),
          ) {
            TooltipBubble(it)
          }
        },
      ) {
        val interactionSource = remember { MutableInteractionSource() }

        UnstyledButton(
          onClick = { },
          contentPadding = PaddingValues(8.dp),
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.White)
            .focusRing(interactionSource, 1.dp, Color(0xFF3B82F6), CircleShape),
          interactionSource = interactionSource,
        ) {
          UnstyledIcon(Lucide.BellDot, contentDescription = null)
        }
      }
    }
  }
}

@Composable
private fun TooltipBubble(placement: TooltipPlacement) {
  when (placement.side) {
    AnchorSide.Top -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
      TooltipContainer()
      TooltipArrow(placement)
    }

    AnchorSide.Bottom -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
      TooltipArrow(placement)
      TooltipContainer()
    }

    AnchorSide.Start -> Row(verticalAlignment = Alignment.CenterVertically) {
      TooltipContainer()
      TooltipArrow(placement)
    }

    AnchorSide.End -> Row(verticalAlignment = Alignment.CenterVertically) {
      TooltipArrow(placement)
      TooltipContainer()
    }
  }
}

@Composable
private fun TooltipContainer() {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(100))
      .background(Color.Black.copy(0.8f))
      .padding(vertical = 8.dp, horizontal = 12.dp),
  ) {
    Text("Notifications", color = Color.White)
  }
}

@Composable
private fun TooltipArrow(placement: TooltipPlacement) {
  val arrowOffset = placement.positionAdjustment
  val modifier = when (placement.side) {
    AnchorSide.Top,
    AnchorSide.Bottom,
    -> Modifier.offset { IntOffset(-arrowOffset.x, 0) }

    AnchorSide.Start,
    AnchorSide.End,
    -> Modifier.offset { IntOffset(0, -arrowOffset.y) }
  }
  val degrees = when (placement.side) {
    AnchorSide.Top -> 180f
    AnchorSide.Bottom -> 0f
    AnchorSide.Start -> 270f
    AnchorSide.End -> 90f
  }

  ArrowUp(modifier.rotate(degrees), Color.Black.copy(0.8f))
}

@Composable
private fun ArrowUp(modifier: Modifier = Modifier, color: Color) {
  Canvas(modifier = modifier.size(8.dp, 4.dp)) {
    val path = Path().apply {
      moveTo(size.width / 2f, 0f)
      lineTo(0f, size.height)
      lineTo(size.width, size.height)
      close()
    }
    drawPath(path, color = color)
  }
}
