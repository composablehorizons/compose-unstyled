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
package com.composeunstyled.demo.outline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.CrossAxisAlignment
import com.composeunstyled.MainAxisArrangement
import com.composeunstyled.Stack
import com.composeunstyled.StackOrientation
import com.composeunstyled.demo.SimpleButton
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.errorToken
import com.composeunstyled.demo.focusToken
import com.composeunstyled.demo.highlightToken
import com.composeunstyled.demo.successToken
import com.composeunstyled.outline
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("outline")
@Composable
fun OutlineDemo() {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
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
}

@Composable
private fun OutlineBasicDemo() {
  SimpleButton(
    modifier = Modifier.outline(
      width = 2.dp,
      color = Theme[colors][focusToken],
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
        color = Theme[colors][focusToken],
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][focusToken],
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 4.dp,
        color = Theme[colors][focusToken],
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
        color = Theme[colors][focusToken],
        shape = RectangleShape,
        offset = 2.dp,
      ),
    )
    SimpleButton(
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][focusToken],
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      shape = CircleShape,
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][focusToken],
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
        color = Theme[colors][focusToken],
        shape = RoundedCornerShape(8.dp),
        offset = 0.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][focusToken],
        shape = RoundedCornerShape(8.dp),
        offset = 4.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][focusToken],
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
        color = Theme[colors][errorToken], // red-500
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][successToken], // emerald-500
        shape = RoundedCornerShape(8.dp),
        offset = 2.dp,
      ),
    )
    SimpleButton(
      modifier = Modifier.outline(
        width = 2.dp,
        color = Theme[colors][highlightToken], // violet-500
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
