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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.focusRing

@Composable
fun FocusRingDemo() {
  Stack(
    orientation = StackOrientation.Vertical,
    mainAxisArrangement = MainAxisArrangement.Center,
    crossAxisAlignment = CrossAxisAlignment.Center,
    spacing = 32.dp,
  ) {
    FocusRingBasicDemo()
    FocusRingWidthDemo()
    FocusRingShapeDemo()
    FocusRingOffsetDemo()
    FocusRingColorDemo()
  }
}

@Composable
private fun FocusRingBasicDemo() {
  val interactionSource = remember { MutableInteractionSource() }

  SimpleButton(
    modifier = Modifier.focusRing(
      interactionSource = interactionSource,
      width = 2.dp,
      color = Color(0xFF3B82F6),
      shape = RoundedCornerShape(8.dp),
      offset = 2.dp,
    ),
    interactionSource = interactionSource,
  )
}

@Composable
private fun FocusRingWidthDemo() {
  val interactionSource1 = remember { MutableInteractionSource() }
  val interactionSource2 = remember { MutableInteractionSource() }
  val interactionSource3 = remember { MutableInteractionSource() }

  ModifierDemoRow {
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource1,
        width = 1.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource1,
    )
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource2,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource2,
    )
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource3,
        width = 4.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource3,
    )
  }
}

@Composable
private fun FocusRingShapeDemo() {
  val interactionSource1 = remember { MutableInteractionSource() }
  val interactionSource2 = remember { MutableInteractionSource() }
  val interactionSource3 = remember { MutableInteractionSource() }

  ModifierDemoRow {
    SimpleButton(
      shape = RectangleShape,
      modifier = Modifier.focusRing(
        interactionSource = interactionSource1,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RectangleShape,
        offset = 2.dp,
      ),
      interactionSource = interactionSource1,
    )
    SimpleButton(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.focusRing(
        interactionSource = interactionSource2,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource2,
    )
    SimpleButton(
      shape = CircleShape,
      modifier = Modifier.focusRing(
        interactionSource = interactionSource3,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = CircleShape,
        offset = 2.dp,
      ),
      interactionSource = interactionSource3,
    )
  }
}

@Composable
private fun FocusRingOffsetDemo() {
  val interactionSource1 = remember { MutableInteractionSource() }
  val interactionSource2 = remember { MutableInteractionSource() }
  val interactionSource3 = remember { MutableInteractionSource() }

  ModifierDemoRow {
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource1,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 0.dp,
      ),
      interactionSource = interactionSource1,
    )
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource2,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 4.dp,
      ),
      interactionSource = interactionSource2,
    )
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource3,
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 8.dp,
      ),
      interactionSource = interactionSource3,
    )
  }
}

@Composable
private fun FocusRingColorDemo() {
  val interactionSource1 = remember { MutableInteractionSource() }
  val interactionSource2 = remember { MutableInteractionSource() }
  val interactionSource3 = remember { MutableInteractionSource() }

  ModifierDemoRow {
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource1,
        width = 2.dp,
        color = Color(0xFFEF4444),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource1,
    )
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource2,
        width = 2.dp,
        color = Color(0xFF10B981),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource2,
    )
    SimpleButton(
      modifier = Modifier.focusRing(
        interactionSource = interactionSource3,
        width = 2.dp,
        color = Color(0xFF8B5CF6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
      interactionSource = interactionSource3,
    )
  }
}

@Composable
private fun ModifierDemoRow(content: @Composable () -> Unit) {
  Stack(
    orientation = StackOrientation.Horizontal,
    mainAxisArrangement = MainAxisArrangement.Center,
    crossAxisAlignment = CrossAxisAlignment.Center,
    spacing = 32.dp,
  ) {
    content()
  }
}
