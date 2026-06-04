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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Modal
import com.composeunstyled.ModalHost
import com.composeunstyled.Scrim
import com.composeunstyled.UnstyledButton
import com.composeunstyled.rememberModalState

@Composable
fun ModalRenderHostDemo() {
  val windowModalState = rememberModalState(initiallyVisible = false)
  val portalModalState = rememberModalState(initiallyVisible = false)

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    ModalHost(Modifier.fillMaxSize()) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          DemoButton(
            text = "Show in modal",
            onClick = {
              windowModalState.transitionState.targetState = true
            },
          )
          DemoButton(
            text = "Show in portal",
            onClick = {
              portalModalState.transitionState.targetState = true
            },
          )
        }
      }

      Modal(state = portalModalState) {
        Scrim(
          modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
              portalModalState.transitionState.targetState = false
            }
          },
          scrimColor = Color.Black.copy(alpha = 0.32f),
        )
      }
    }

    Modal(state = windowModalState) {
      Scrim(
        modifier = Modifier.pointerInput(Unit) {
          detectTapGestures {
            windowModalState.transitionState.targetState = false
          }
        },
        scrimColor = Color.Black.copy(alpha = 0.32f),
      )
    }
  }
}

@Composable
private fun DemoButton(
  text: String,
  onClick: () -> Unit,
) {
  UnstyledButton(
    onClick = onClick,
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF18181B)),
  ) {
    BasicText(
      text,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      style = TextStyle(
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
      ),
    )
  }
}
