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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.UnstyledTextField
import androidx.compose.foundation.text.BasicText as Text

@Composable
fun TextFieldDemo() {
  val email = rememberTextFieldState()
  val password = rememberTextFieldState()
  var showPassword by remember { mutableStateOf(false) }
  val fieldShape = RoundedCornerShape(8.dp)
  val bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF1A1A1A))
  val bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, color = Color(0xFF1A1A1A))

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp)
      .imePadding(),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 500.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White)
        .demoOutline(RoundedCornerShape(16.dp))
        .padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        UnstyledTextField(
          state = email,
          modifier = Modifier.fillMaxWidth(),
          lineLimits = TextFieldLineLimits.SingleLine,
          cursorBrush = SolidColor(Color.Black),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
          ),
          textStyle = bodyMedium,
        ) {
          Column {
            Text(
              "Email",
              modifier = Modifier.padding(bottom = 8.dp),
              style = bodyLarge,
            )
            TextInput(
              Modifier
                .fillMaxWidth()
                .background(Color.White, fieldShape)
                .demoOutline(fieldShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
              placeholder = {
                Text(
                  "email@example.com",
                  style = bodyMedium.copy(color = Color.Black.copy(0.6f)),
                )
              },
            )
          }
        }

        UnstyledTextField(
          state = password,
          modifier = Modifier.fillMaxWidth(),
          lineLimits = TextFieldLineLimits.SingleLine,
          cursorBrush = SolidColor(Color.Black),
          outputTransformation = if (showPassword) null else PasswordOutputTransformation,
          textStyle = bodyMedium,
        ) {
          Column {
            Text(
              "Password",
              modifier = Modifier.padding(bottom = 8.dp),
              style = bodyLarge,
            )
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, fieldShape)
                .demoOutline(fieldShape)
                .padding(vertical = 4.dp)
                .padding(start = 16.dp, end = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              TextInput(
                modifier = Modifier.weight(1f),
                placeholder = {
                  Text(
                    text = "8-12 characters",
                    style = bodyMedium.copy(color = Color.Black.copy(0.6f)),
                  )
                },
              )
              UnstyledButton(
                onClick = { showPassword = showPassword.not() },
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .demoOutline(RoundedCornerShape(4.dp)),
              ) {
                Box(Modifier.padding(4.dp)) {
                  UnstyledIcon(
                    imageVector = if (showPassword) Lucide.EyeOff else Lucide.Eye,
                    contentDescription = if (showPassword) "Hide password" else "Show password",
                    tint = Color(0xFF757575),
                  )
                }
              }
            }
          }
        }

        UnstyledButton(
          onClick = { /* TODO */ },
          modifier = Modifier
            .fillMaxWidth()
            .clip(fieldShape)
            .background(Color.Black)
            .demoOutline(fieldShape),
        ) {
          Text(
            "Submit",
            modifier = Modifier.padding(12.dp),
            style = bodyMedium.merge(
              TextStyle(fontWeight = FontWeight.Medium, color = Color.White),
            ),
          )
        }
      }
    }
  }
}

private val PasswordOutputTransformation = object : OutputTransformation {
  override fun TextFieldBuffer.transformOutput() {
    for (index in 0 until length) {
      replace(index, index + 1, "*")
    }
  }
}
