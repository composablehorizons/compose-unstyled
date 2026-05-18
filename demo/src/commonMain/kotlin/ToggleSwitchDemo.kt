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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.SwitchThumb
import com.composeunstyled.UnstyledSwitch

@Composable
fun ToggleSwitchDemo() {
  var toggled by remember { mutableStateOf(true) }

  Row(
    Modifier
      .width(300.dp)
      .clip(RoundedCornerShape(10.dp))
      .selectable(
        selected = toggled,
        onClick = { toggled = toggled.not() },
        interactionSource = null,
        role = Role.Switch,
      ).padding(8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    BasicText("Airplane Mode", style = TextStyle(fontSize = 18.sp))
    val animatedColor by animateColorAsState(
      if (toggled) Color.Black else Color(0xFFE0E0E0),
    )
    UnstyledSwitch(
      checked = toggled,
      onCheckedChange = null,
      modifier = Modifier
        .width(58.dp)
        .height(32.dp)
        .clip(RoundedCornerShape(100))
        .background(animatedColor, RoundedCornerShape(100))
        .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(100)),
      indication = LocalIndication.current,
    ) {
      SwitchThumb(
        animationSpec = tween(),
        modifier = Modifier
          .padding(4.dp)
          .clip(CircleShape)
          .background(Color(0xFFF8FAFC))
          .border(1.dp, Color(0xFFCACACA), CircleShape)
          .size(24.dp),
      )
    }
  }
}
