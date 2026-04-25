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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDialog
import com.composeunstyled.UnstyledDialogPanel
import com.composeunstyled.UnstyledScrim
import com.composeunstyled.rememberDialogState

@Composable
fun DialogDemo() {
  val dialogState = rememberDialogState(initiallyVisible = false)

  Box(
    modifier = Modifier.fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF4A90E2), Color(0xFF50C9C3))))
      .padding(vertical = 40.dp),
    contentAlignment = Alignment.Center,
  ) {
    UnstyledButton(
      onClick = { dialogState.visible = true },
      backgroundColor = Color.White,
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
      shape = RoundedCornerShape(6.dp),
      indication = LocalIndication.current,
    ) {
      Text("Show dialog")
    }
    UnstyledDialog(state = dialogState) {
      UnstyledScrim(scrimColor = Color.Black.copy(0.3f), enter = fadeIn(), exit = fadeOut())
      UnstyledDialogPanel(
        backgroundColor = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .padding(20.dp)
          .displayCutoutPadding()
          .systemBarsPadding()
          .widthIn(max = 560.dp)
          .padding(20.dp),
        enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
        exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
      ) {
        Column {
          Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
            Text("Update Available", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
              text = "A new version of the app is available. Please update to the latest version.",
              style = TextStyle(color = Color(0xFF1A1A1A)),
            )
          }
          Spacer(Modifier.height(24.dp))
          UnstyledButton(
            onClick = { /* TODO */ },
            modifier = Modifier.padding(12.dp).align(Alignment.End),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            indication = LocalIndication.current,
          ) {
            Text("Update", style = TextStyle(color = Color(0xFF0D99FF)))
          }
        }
      }
    }
  }
}
