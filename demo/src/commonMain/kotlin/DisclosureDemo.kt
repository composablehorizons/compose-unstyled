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

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.composeunstyled.DisclosedContent
import com.composeunstyled.DisclosureButton
import com.composeunstyled.Text
import com.composeunstyled.UnstyledDisclosure

@Composable
fun DisclosureDemo() {
  var expanded by remember { mutableStateOf(false) }

  UnstyledDisclosure(
    expanded = expanded,
    onExpandedChange = { expanded = it },
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 560.dp)
        .clip(RectangleShape)
        .background(Color.White)
        .border(1.dp, Color.Black, RectangleShape),
    ) {
      DisclosureButton(
        modifier = Modifier.fillMaxWidth(),
        indication = LocalIndication.current,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("WHAT IS COMPOSE UNSTYLED", modifier = Modifier.weight(1f))

          Text(if (expanded) "CLOSE" else "OPEN")
        }
      }
      DisclosedContent(
        enter = expandVertically(
          spring(
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = IntSize.VisibilityThreshold,
          ),
        ),
        exit = shrinkVertically(),
      ) {
        Text(
          "COMPOSE UNSTYLED IS A COLLECTION OF UNSTYLED, ACCESSIBLE UI COMPONENTS FOR COMPOSE " +
            "MULTIPLATFORM. IT PROVIDES THE BUILDING BLOCKS FOR CREATING BEAUTIFUL, CONSISTENT " +
            "USER INTERFACES.",
          modifier = Modifier.padding(16.dp),
        )
      }
    }
  }
}
