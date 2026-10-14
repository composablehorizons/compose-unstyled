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
package com.composeunstyled.demo.appbar

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.BellDot
import com.composables.icons.lucide.Heart
import com.composables.icons.lucide.Lucide
import com.composeunstyled.Text
import com.composeunstyled.UnstyledAppBar
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.demo.UnstyledDemo

@Preview
@UnstyledDemo("app-bar")
@Composable
fun AppBarDemo() {
  var lastAction by remember { mutableStateOf("Tap an icon") }
  Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
    Column {
      UnstyledAppBar(
        title = { BasicText("Messages") },
        modifier = Modifier.fillMaxWidth().background(Color.White).border(1.dp, Color.Black),
        contentPadding = PaddingValues(8.dp),
        navigation = {
          AppBarIconButton(Lucide.ArrowLeft, "Back") { lastAction = "Back pressed" }
        },
        actions = {
          AppBarIconButton(Lucide.Heart, "Favorite") { lastAction = "Favorite pressed" }
          AppBarIconButton(Lucide.BellDot, "Notifications") {
            lastAction = "Notifications pressed"
          }
        },
      )
      Text(lastAction, modifier = Modifier.padding(top = 12.dp))
    }
  }
}

@Composable
private fun AppBarIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
  UnstyledButton(
    onClick = onClick,
    modifier = Modifier.size(40.dp),
    indication = LocalIndication.current,
  ) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      UnstyledIcon(imageVector = icon, contentDescription = label, tint = Color.Black)
    }
  }
}
