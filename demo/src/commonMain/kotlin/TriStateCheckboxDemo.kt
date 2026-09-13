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
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.composeunstyled.CheckedIndicator
import com.composeunstyled.StateIndicator
import com.composeunstyled.Text
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.UnstyledTriStateCheckbox

@Composable
fun TriStateCheckboxDemo() {
  val checkboxOptions = listOf("OPTION 1", "OPTION 2", "OPTION 3", "OPTION 4")
  var selected by remember { mutableStateOf(listOf(true, true, false, false)) }

  val triState = when {
    selected.all { it } -> ToggleableState.On
    selected.none { it } -> ToggleableState.Off
    else -> ToggleableState.Indeterminate
  }

  Column(
    modifier = Modifier
      .widthIn(max = 300.dp)
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    val triStateShape = RectangleShape
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
      modifier = Modifier.fillMaxWidth(),
      accessibilityLabel = "SELECT ALL OPTIONS",
      indication = null,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        StateIndicator(
          modifier = Modifier
            .clip(triStateShape)
            .size(24.dp)
            .background(Color.White, triStateShape)
            .border(1.dp, Color.Black, triStateShape),
          indication = LocalIndication.current,
        ) { state ->
          when (state) {
            ToggleableState.On -> Box(
              Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center,
            ) { Text("X") }

            ToggleableState.Indeterminate -> Box(
              Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center,
            ) {
              Text("-")
            }

            ToggleableState.Off -> Unit
          }
        }

        Spacer(Modifier.width(12.dp))
        Text(
          "SELECT ALL",
          color = Color.Black,
        )
      }
    }

    val checkboxShape = RectangleShape
    checkboxOptions.forEachIndexed { index, option ->
      UnstyledCheckbox(
        checked = selected[index],
        onCheckedChange = { checked ->
          selected = selected.toMutableList().apply {
            this[index] = checked
          }
        },
        modifier = Modifier.fillMaxWidth(),
        accessibilityLabel = option,
        indication = null,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(start = 36.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          CheckedIndicator(
            modifier = Modifier
              .clip(checkboxShape)
              .size(24.dp)
              .background(Color.White, checkboxShape)
              .border(1.dp, Color.Black, checkboxShape),
            indication = LocalIndication.current,
          ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("X") }
          }

          Spacer(Modifier.width(12.dp))
          Text(option, color = Color.Black)
        }
      }
    }
  }
}
