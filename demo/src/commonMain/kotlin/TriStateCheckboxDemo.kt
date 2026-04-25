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

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composeunstyled.UnstyledIcon
import androidx.compose.material3.Text
import com.composeunstyled.UnstyledTriStateCheckbox
import com.composeunstyled.UnstyledCheckbox

@Composable
fun TriStateCheckboxDemo() {
  Box(
    modifier = Modifier.fillMaxSize()
      .background(Brush.linearGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))),
    contentAlignment = Alignment.Center,
  ) {
    val checkboxOptions = listOf("Option 1", "Option 2", "Option 3", "Option 4")
    var selected by remember { mutableStateOf(List(checkboxOptions.size) { false }) }

    val triState = when {
      selected.all { it } -> ToggleableState.On
      selected.none { it } -> ToggleableState.Off
      else -> ToggleableState.Indeterminate
    }

    Column(
      modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        UnstyledTriStateCheckbox(
          value = triState,
          onClick = {
            val newState = when (triState) {
              ToggleableState.Off -> true
              ToggleableState.Indeterminate -> true
              ToggleableState.On -> false
            }
            selected = List(checkboxOptions.size) { newState }
          },
          shape = RoundedCornerShape(4.dp),
          backgroundColor = Color.White,
          borderWidth = 1.dp,
          borderColor = Color.Black.copy(0.33f),
          modifier = Modifier.size(24.dp),
          contentDescription = "Select all options",
          indication = LocalIndication.current,
        ) {
          when (triState) {
            ToggleableState.On -> UnstyledIcon(Lucide.Check, contentDescription = null)
            ToggleableState.Indeterminate -> UnstyledIcon(Lucide.Minus, contentDescription = null)
            ToggleableState.Off -> Unit
          }
        }

        Spacer(Modifier.width(12.dp))
        Text("Select All", style = TextStyle(color = Color.White))
      }

      checkboxOptions.forEachIndexed { index, option ->
        Row(
          modifier = Modifier.fillMaxWidth().padding(start = 36.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          UnstyledCheckbox(
            checked = selected[index],
            onCheckedChange = { checked ->
              selected = selected.toMutableList().apply {
                this[index] = checked
              }
            },
            shape = RoundedCornerShape(4.dp),
            backgroundColor = Color.White,
            borderWidth = 1.dp,
            borderColor = Color.Black.copy(0.33f),
            modifier = Modifier.size(24.dp),
            contentDescription = option,
          ) {
            UnstyledIcon(Lucide.Check, contentDescription = null)
          }

          Spacer(Modifier.width(12.dp))
          Text(option, style = TextStyle(color = Color.White))
        }
      }
    }
  }
}
