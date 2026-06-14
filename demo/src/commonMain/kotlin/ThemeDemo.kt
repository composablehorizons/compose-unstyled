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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme

private val colors = ThemeProperty<Color>("colors")
private val textStyles = ThemeProperty<TextStyle>("textStyles")

private val background = ThemeToken<Color>("background")
private val surface = ThemeToken<Color>("surface")
private val primary = ThemeToken<Color>("primary")
private val onSurface = ThemeToken<Color>("onSurface")
private val onPrimary = ThemeToken<Color>("onPrimary")

private val title = ThemeToken<TextStyle>("title")
private val body = ThemeToken<TextStyle>("body")

private val AppTheme = buildTheme {
  name = "AppTheme"

  properties[colors] = mapOf(
    background to Color(0xFFF8FAFC),
    surface to Color.White,
    primary to Color(0xFF2563EB),
    onSurface to Color(0xFF0F172A),
    onPrimary to Color.White,
  )

  properties[textStyles] = mapOf(
    title to TextStyle(
      fontSize = 22.sp,
      fontWeight = FontWeight.SemiBold,
    ),
    body to TextStyle(
      fontSize = 15.sp,
      lineHeight = 22.sp,
    ),
  )
}

@Composable
fun ThemingDemo() {
  AppTheme {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Theme[colors][background])
        .padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Column(
        modifier = Modifier
          .background(
            color = Theme[colors][surface],
            shape = RoundedCornerShape(12.dp),
          )
          .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        BasicText(
          text = "Create a theme",
          style = Theme[textStyles][title].copy(
            color = Theme[colors][onSurface],
          ),
        )

        BasicText(
          text = "Define theme properties and tokens, assign values in buildTheme {}, " +
            "then read them with Theme[property][token].",
          style = Theme[textStyles][body].copy(
            color = Theme[colors][onSurface],
          ),
        )

        Box(
          modifier = Modifier
            .background(
              color = Theme[colors][primary],
              shape = RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
          BasicText(
            text = "Themed action",
            style = Theme[textStyles][body].copy(
              color = Theme[colors][onPrimary],
              fontWeight = FontWeight.Medium,
            ),
          )
        }
      }
    }
  }
}
