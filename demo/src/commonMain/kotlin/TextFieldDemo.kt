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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.LocalTextStyle
import com.composeunstyled.Text
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledTextField
import com.composeunstyled.focusRing

@Composable
fun TextFieldDemo() {
  val displayName = rememberTextFieldState()
  val fieldShape = RectangleShape
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp)
      .imePadding(),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 500.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        UnstyledTextField(
          state = displayName,
          interactionSource = interactionSource,
          modifier = Modifier.fillMaxWidth(),
          lineLimits = TextFieldLineLimits.SingleLine,
          cursorBrush = SolidColor(Color.Black),
          textStyle = LocalTextStyle.current.copy(
            color = Color.Black,
            fontSize = 14.sp,
            lineHeight = 20.sp,
          ),
        ) {
          Column {
            Text(
              "DISPLAY NAME",
              modifier = Modifier.padding(bottom = 8.dp),
              color = Color.Black,
              fontSize = 16.sp,
              lineHeight = 24.sp,
            )
            TextInput(
              Modifier
                .fillMaxWidth()
                .background(Color.White, fieldShape)
                .border(1.dp, Color.Black, fieldShape)
                .focusRing(interactionSource, 2.dp, Color.Black, fieldShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
              placeholder = {
                Text(
                  "ALEX",
                  color = Color.Black,
                  fontSize = 14.sp,
                  lineHeight = 20.sp,
                )
              },
            )
          }
        }
      }
    }
  }
}
