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
package com.composeunstyled.demo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.RelativeAlignment
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledText
import com.composeunstyled.UnstyledTooltip
import com.composeunstyled.UnstyledTooltipPanel
import com.composeunstyled.platformtheme.buildPlatformTheme

class DemoAndroidActivity : ComponentActivity() {

  val AppTheme = buildPlatformTheme()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        TooltipCrashRepro()
      }
    }
  }
}

@Composable
private fun TooltipCrashRepro() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFEFF4FF)),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledTooltip(
      placement = RelativeAlignment.TopCenter,
      panel = {
        UnstyledTooltipPanel(
          modifier = Modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
          contentColor = Color.White,
        ) {
          BasicText(
            "Tooltip panel",
            style = TextStyle(color = Color.White, fontSize = 14.sp),
          )
        }
      },
    ) {
      UnstyledButton(
        onClick = { },
        backgroundColor = Color(0xFF1D4ED8),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
      ) {
        UnstyledText(
          "Long press me",
          style = TextStyle(color = Color.White, fontSize = 16.sp),
        )
      }
    }
  }
}
