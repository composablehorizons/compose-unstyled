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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.outline

@Composable
fun OutlineDemo() {
  Stack(
    orientation = StackOrientation.Vertical,
    mainAxisArrangement = MainAxisArrangement.Center,
    crossAxisAlignment = CrossAxisAlignment.Center,
    spacing = 32.dp,
  ) {
    OutlineBasicDemo()
    OutlineWidthDemo()
    OutlineShapeDemo()
    OutlineOffsetDemo()
    OutlineColorDemo()
  }
}

@Composable
private fun OutlineBasicDemo() {
  SimpleButton(
    modifier = Modifier.outline(
      width = 2.dp,
      color = Color(0xFF3B82F6),
      shape = RoundedCornerShape(8.dp),
      offset = 2.dp,
    ),
  )
}

@Composable
private fun OutlineWidthDemo() {
  ModifierDemoRow {
    SimpleButton(
      modifier = Modifier.outline(
        width = 1.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 4.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
  }
}

@Composable
private fun OutlineShapeDemo() {
  ModifierDemoRow {
    SimpleButton(
      shape = RectangleShape,
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RectangleShape,
        offset = 2.dp,
      ),
    )
    SimpleButton(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      shape = CircleShape,
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = CircleShape,
        offset = 2.dp,
      ),
    )
  }
}

@Composable
private fun OutlineOffsetDemo() {
  ModifierDemoRow {
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 0.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 4.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF3B82F6),
        shape = RoundedCornerShape(8.dp),
        offset = 8.dp,
      ),
    )
  }
}

@Composable
private fun OutlineColorDemo() {
  ModifierDemoRow {
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFFEF4444), // red-500
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF10B981), // emerald-500
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Color(0xFF8B5CF6), // violet-500
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
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
