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

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.UnstyledTextField

@Composable
fun TextFieldDemo() {
  val email = rememberTextFieldState()
  val password = rememberTextFieldState()
  var showPassword by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier.fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF9D50BB), Color(0xFF6E48AA))))
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp)
      .imePadding(),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = 500.dp)
        .shadow(8.dp, RoundedCornerShape(16.dp))
        .background(Color.White, RoundedCornerShape(16.dp))
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
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
          ),
        ) {
          Text(
            text = "Email",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
          )
          TextInput(
            Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp))
              .background(Color.White, RoundedCornerShape(8.dp))
              .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = {
              Text(
                "email@example.com",
                style = MaterialTheme.typography.bodyMedium.merge(
                  TextStyle(
                    color = Color.Black.copy(
                      0.6f,
                    ),
                  ),
                ),
              )
            },
          )
        }

        UnstyledTextField(
          state = password,
          modifier = Modifier.fillMaxWidth(),
          lineLimits = TextFieldLineLimits.SingleLine,
          outputTransformation = if (showPassword) {
            null
          } else {
            PasswordOutputTransformation
          },
        ) {
          Text(
            "Password",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
          )
          TextInput(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp))
              .background(Color.White, RoundedCornerShape(8.dp))
              .padding(vertical = 4.dp)
              .padding(start = 16.dp, end = 4.dp),
            placeholder = {
              Text(
                text = "8-12 characters",
                style = MaterialTheme.typography.bodyMedium.merge(
                  TextStyle(
                    color = Color.Black.copy(
                      0.6f,
                    ),
                  ),
                ),
              )
            },
            trailing = {
              UnstyledButton(
                onClick = { showPassword = !showPassword },
                backgroundColor = Color.Transparent,
                contentPadding = PaddingValues(4.dp),
                shape = RoundedCornerShape(4.dp),
                indication = LocalIndication.current,
              ) {
                UnstyledIcon(
                  imageVector = if (showPassword) Lucide.EyeOff else Lucide.Eye,
                  contentDescription = if (showPassword) "Hide password" else "Show password",
                  tint = Color(0xFF757575),
                )
              }
            },
          )
        }

        UnstyledButton(
          onClick = { /* TODO */ },
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = Color(0xFF8E44AD),
          contentPadding = PaddingValues(12.dp),
          shape = RoundedCornerShape(8.dp),
        ) {
          Text(
            text = "Submit",
            color = Color.White,
          )
        }
      }
    }
  }
}

private val PasswordOutputTransformation = object : OutputTransformation {
  override fun TextFieldBuffer.transformOutput() {
    for (index in 0 until length) {
      replace(index, index + 1, "•")
    }
  }
}
