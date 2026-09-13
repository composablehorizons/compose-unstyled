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

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.composeunstyled.DropdownMenuPanel
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.UnstyledDropdownMenuItem
import com.composeunstyled.UnstyledHorizontalSeparator

@Composable
fun DropdownMenuDemo() {
  class DropdownOption(
    val text: String,
    val enabled: Boolean = true,
  )

  val options = listOf(
    DropdownOption("SELECT ALL"),
    DropdownOption("COPY"),
    DropdownOption("CUT", enabled = false),
    DropdownOption("PASTE"),
    DropdownOption("DELETE"),
  )
  var expanded by remember { mutableStateOf(true) }

  UnstyledDropdownMenu(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    sideOffset = 4.dp,
    panel = {
      DropdownMenuPanel(
        modifier = Modifier
          .width(240.dp)
          .clip(RectangleShape)
          .background(Color.White)
          .border(1.dp, Color.Black, RectangleShape),
        enter = scaleIn(
          animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
          initialScale = 0.8f,
          transformOrigin = TransformOrigin(0f, 0f),
        ) + fadeIn(tween(durationMillis = 30)),
        exit = scaleOut(
          animationSpec = tween(durationMillis = 75),
          targetScale = 0.8f,
          transformOrigin = TransformOrigin(0f, 0f),
        ) +
          fadeOut(tween(durationMillis = 75)),
      ) {
        options.forEachIndexed { index, option ->
          if (index == 1 || index == options.lastIndex) {
            UnstyledHorizontalSeparator(color = Color.Black)
          }
          UnstyledDropdownMenuItem(
            onClick = {},
            enabled = option.enabled,
            indication = LocalIndication.current,
            modifier = Modifier
              .padding(4.dp)
              .sizeIn(minWidth = 40.dp, minHeight = 40.dp)
              .clip(RectangleShape)
              .fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.Start,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = option.text,
                color = Color.Black,
                textDecoration = if (option.enabled) null else TextDecoration.LineThrough,
              )
            }
          }
        }
      }
    },
    anchor = {
      UnstyledButton(
        onClick = { expanded = expanded.not() },
        modifier = Modifier
          .sizeIn(minWidth = 40.dp, minHeight = 40.dp)
          .clip(RectangleShape)
          .background(Color.White)
          .border(1.dp, Color.Black, RectangleShape),
        indication = LocalIndication.current,
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("OPTIONS")
          Spacer(Modifier.width(8.dp))
          Text(if (expanded) "CLOSE" else "OPEN")
        }
      }
    },
  )
}
