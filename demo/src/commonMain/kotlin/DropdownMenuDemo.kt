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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Clipboard
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Maximize
import com.composables.icons.lucide.Scissors
import com.composables.icons.lucide.Trash2
import com.composeunstyled.LocalContentColor
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.UnstyledDropdownMenuPanel
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.UnstyledSeparator
import kotlinx.coroutines.delay

@Composable
fun DropdownMenuDemo() {
  Box(
    modifier = Modifier.fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66))))
      .padding(vertical = 40.dp),
    contentAlignment = Alignment.TopCenter,
  ) {
    class DropdownOption(
      val text: String,
      val icon: ImageVector,
      val enabled: Boolean = true,
      val dangerous: Boolean = false,
    )

    val options = listOf(
      DropdownOption("Select All", Lucide.Maximize),
      DropdownOption("Copy", Lucide.Copy),
      DropdownOption("Cut", Lucide.Scissors, enabled = false),
      DropdownOption("Paste", Lucide.Clipboard),
      DropdownOption("Delete", Lucide.Trash2, dangerous = true),
    )
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
      delay(500)
      expanded = true
    }

    UnstyledDropdownMenu(onExpandRequest = { expanded = true }) {
      UnstyledButton(
        shape = RoundedCornerShape(6.dp),
        backgroundColor = Color.White,
        onClick = { expanded = true },
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        indication = LocalIndication.current,
        modifier = Modifier.sizeIn(minWidth = 40.dp, minHeight = 40.dp),
      ) {
        Text("Options")
        Spacer(Modifier.width(8.dp))
        UnstyledIcon(Lucide.ChevronDown, null)
      }

      UnstyledDropdownMenuPanel(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
          .width(240.dp)
          .shadow(4.dp, RoundedCornerShape(8.dp)),
        enter = scaleIn(
          animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
          initialScale = 0.8f,
          transformOrigin = TransformOrigin(0f, 0f),
        ) + fadeIn(tween(durationMillis = 30)),
        exit = scaleOut(
          animationSpec = tween(durationMillis = 1, delayMillis = 75),
          targetScale = 1f,
        ) +
          fadeOut(tween(durationMillis = 75)),
      ) {
        options.forEachIndexed { index, option ->
          if (index == 1 || index == options.lastIndex) {
            UnstyledSeparator(color = Color(0xFFBDBDBD))
          }
          UnstyledButton(
            onClick = { expanded = false },
            enabled = option.enabled,
            modifier = Modifier
              .padding(4.dp)
              .sizeIn(minWidth = 40.dp, minHeight = 40.dp)
              .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            indication = LocalIndication.current,
            horizontalArrangement = Arrangement.Start,
          ) {
            UnstyledIcon(option.icon, null)
            Spacer(Modifier.width(12.dp))
            Text(
              text = option.text,
              color = (if (option.dangerous) Color(0xFFC62828) else LocalContentColor.current)
                .copy(alpha = if (option.enabled) 1f else 0.5f),
            )
          }
        }
      }
    }
  }
}
