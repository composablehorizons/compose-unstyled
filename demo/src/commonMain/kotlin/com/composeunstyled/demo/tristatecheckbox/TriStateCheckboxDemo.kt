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
package com.composeunstyled.demo.tristatecheckbox

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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Minus
import com.composeunstyled.CheckedIndicator
import com.composeunstyled.StateIndicator
import com.composeunstyled.Text
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.UnstyledTriStateCheckbox
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.borderToken
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.contentToken
import com.composeunstyled.demo.surfaceToken
import com.composeunstyled.theme.Theme

@Preview
@UnstyledDemo("tristatecheckbox", name = "Tristate Checkbox")
@Composable
fun TriStateCheckboxDemo() {
  val checkboxOptions = listOf("Option 1", "Option 2", "Option 3", "Option 4")
  var selected by remember { mutableStateOf(listOf(true, true, false, false)) }
  val triState = when {
    selected.all { it } -> ToggleableState.On
    selected.none { it } -> ToggleableState.Off
    else -> ToggleableState.Indeterminate
  }

  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 300.dp)
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
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
        modifier = Modifier.fillMaxWidth(),
        accessibilityLabel = "Select all options",
        indication = null,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          StateIndicator(
            modifier = Modifier
              .clip(RectangleShape)
              .size(24.dp)
              .background(Theme[colors][surfaceToken], RectangleShape)
              .border(1.dp, Theme[colors][borderToken], RectangleShape),
            indication = LocalIndication.current,
          ) { state ->
            when (state) {
              ToggleableState.On -> UnstyledIcon(
                Lucide.Check,
                contentDescription = null,
                tint = Theme[colors][contentToken],
              )

              ToggleableState.Indeterminate -> UnstyledIcon(
                Lucide.Minus,
                contentDescription = null,
                tint = Theme[colors][contentToken],
              )

              ToggleableState.Off -> Unit
            }
          }

          Spacer(Modifier.width(12.dp))
          Text(
            "Select All",
            color = Theme[colors][contentToken],
          )
        }
      }

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
                .clip(RectangleShape)
                .size(24.dp)
                .background(Theme[colors][surfaceToken], RectangleShape)
                .border(1.dp, Theme[colors][borderToken], RectangleShape),
              indication = LocalIndication.current,
            ) {
              UnstyledIcon(
                Lucide.Check,
                contentDescription = null,
                tint = Theme[colors][contentToken],
              )
            }

            Spacer(Modifier.width(12.dp))
            Text(option, color = Theme[colors][contentToken])
          }
        }
      }
    }
  }
}
