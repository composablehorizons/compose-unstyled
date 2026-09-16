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
package com.composeunstyled.demo.radiogroup

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.SelectedIndicator
import com.composeunstyled.Text
import com.composeunstyled.UnstyledRadioButton
import com.composeunstyled.UnstyledRadioGroup
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.demoColors
import com.composeunstyled.demo.demoContent
import com.composeunstyled.demo.demoOutline
import com.composeunstyled.demo.demoSurface
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("radiogroup")
@Composable
fun RadioGroupDemo() {
  val values = listOf("Light", "Dark", "System")
  var selectedValue by remember { mutableStateOf("Light") }

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier
        .width(300.dp)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      UnstyledRadioGroup(
        value = selectedValue,
        onValueChange = { selectedValue = it },
        modifier = Modifier.fillMaxWidth(),
        accessibilityLabel = "Theme selection",
      ) {
        Column(
          horizontalAlignment = Alignment.Start,
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          values.forEach { value ->
            val selected = selectedValue == value
            val itemShape = RoundedCornerShape(14.dp)
            UnstyledRadioButton(
              value = value,
              modifier = Modifier
                .fillMaxWidth()
                .clip(itemShape),
              indication = LocalIndication.current,
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                      if (selected) {
                        Theme[demoColors][demoContent]
                      } else {
                        Theme[demoColors][demoSurface]
                      },
                    )
                    .border(1.dp, Theme[demoColors][demoOutline], CircleShape),
                  contentAlignment = Alignment.Center,
                ) {
                  SelectedIndicator(
                    indication = LocalIndication.current,
                  ) {
                    Box(
                      Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Theme[demoColors][demoSurface]),
                    )
                  }
                }
                Spacer(Modifier.width(16.dp))
                Text(value)
              }
            }
          }
        }
      }
    }
  }
}
