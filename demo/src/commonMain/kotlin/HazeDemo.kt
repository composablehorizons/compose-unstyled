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
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.composables.uripainter.rememberUriPainter
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDialog
import com.composeunstyled.UnstyledDialogPanel
import com.composeunstyled.UnstyledScrim
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun HazeDemo() {
  val backgroundUrl =
    "https://images.unsplash.com/photo-1554629947-334ff61d85dc?q=80&w=2200&auto=format&fit=crop"
  val hazeState = rememberHazeState()
  var dialogVisible by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .hazeSource(hazeState),
    contentAlignment = Alignment.Center,
  ) {
    Image(
      painter = rememberUriPainter(backgroundUrl),
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
    )

    UnstyledButton(
      onClick = { dialogVisible = true },
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
      interactionSource = remember { MutableInteractionSource() },
      indication = LocalIndication.current,
      modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White),
    ) {
      Text("Show dialog")
    }

    UnstyledDialog(
      visible = dialogVisible,
      onDismissRequest = { dialogVisible = false },
    ) {
      UnstyledScrim(
        scrimColor = Color.Black.copy(0.3f),
        enter = fadeIn(),
        exit = fadeOut(),
      )

      UnstyledDialogPanel(
        modifier = Modifier
          .padding(20.dp)
          .displayCutoutPadding()
          .systemBarsPadding()
          .widthIn(max = 560.dp)
          .padding(20.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
          .pointerInput(Unit) { detectTapGestures { } }
          .hazeEffect(hazeState) {
            blurEffect {
              blurRadius = 28.dp
            }
          },
        enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
        exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
      ) {
        Column {
          Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
            Text("Haze x Unstyled", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
              "This is a Compose Unstyled dialog that uses Haze blur effect " +
                "as its panel's background",
              style = TextStyle(color = Color(0xFF1A1A1A)),
            )
          }
          Spacer(Modifier.height(24.dp))
          UnstyledButton(
            onClick = { dialogVisible = false },
            modifier = Modifier.padding(12.dp).align(Alignment.End).clip(RoundedCornerShape(6.dp)),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
          ) {
            Text("Close", style = TextStyle(color = Color(0xFF0D99FF)))
          }
        }
      }
    }
  }
}
