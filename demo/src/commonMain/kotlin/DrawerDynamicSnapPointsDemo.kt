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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composeunstyled.CheckedIndicator
import com.composeunstyled.DrawerSide
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.Panel
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledCheckbox
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledIcon
import com.composeunstyled.Viewport
import com.composeunstyled.rememberDrawerState

@Composable
fun DrawerDynamicSnapPointsDemo() {
  val ninetyPercent = remember {
    DrawerSnapPoint("90%") { containerSize, _ ->
      containerSize * 0.9f
    }
  }
  val fiftyPercent = remember {
    DrawerSnapPoint("50%") { containerSize, _ ->
      containerSize * 0.5f
    }
  }
  val twentyFivePercent = remember {
    DrawerSnapPoint("25%") { containerSize, _ ->
      containerSize * 0.25f
    }
  }
  var checkedSnapPoints by remember {
    mutableStateOf(
      mapOf(
        DrawerSnapPoint.Open to true,
        ninetyPercent to true,
        fiftyPercent to true,
        twentyFivePercent to true,
        DrawerSnapPoint.Closed to true,
      ),
    )
  }
  val snapPointOptions = listOf(
    DrawerSnapPoint.Open,
    ninetyPercent,
    fiftyPercent,
    twentyFivePercent,
    DrawerSnapPoint.Closed,
  )
  val snapPoints = snapPointOptions.filter { snapPoint ->
    checkedSnapPoints[snapPoint] == true
  }
  val nextVisibleSnapPoint =
    snapPoints.firstOrNull { snapPoint -> snapPoint != DrawerSnapPoint.Closed }
      ?: snapPoints.first()

  val drawerState = rememberDrawerState(
    initialSnapPoint = DrawerSnapPoint.Open,
    snapPoints = {
      snapPoints
    },
  )

  UnstyledButton(
    onClick = {
      drawerState.targetSnapPoint = nextVisibleSnapPoint
    },
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .heightIn(32.dp)
      .background(Color.White)
      .border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
    contentPadding = PaddingValues(horizontal = 10.dp),
    indication = LocalIndication.current,
  ) {
    BasicText("Open drawer")
  }

  UnstyledDrawer(
    state = drawerState,
    modifier = Modifier.fillMaxSize().background(Color.Yellow.copy(0.22f)),
    side = DrawerSide.Bottom,
  ) {
    Viewport(Modifier.fillMaxSize()) {
      Panel(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.White)
          .border(1.dp, Color.Black)
          .padding(12.dp),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          BasicText("Enabled Snap Points")

          snapPointOptions.forEach { snapPoint ->
            val checked = checkedSnapPoints[snapPoint] == true
            UnstyledCheckbox(
              checked = checked,
              onCheckedChange = { nextChecked ->
                val checkedCount = checkedSnapPoints.values.count { it }
                if (nextChecked || checkedCount > 1) {
                  checkedSnapPoints = checkedSnapPoints + (snapPoint to nextChecked)
                }
              },
              modifier = Modifier.fillMaxWidth(),
              accessibilityLabel = snapPoint.identifier,
              indication = LocalIndication.current,
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .border(1.dp, Color.Black)
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Box(
                  modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, Color.Black),
                ) {
                  CheckedIndicator(
                    modifier = Modifier.fillMaxSize(),
                    indication = LocalIndication.current,
                  ) {
                    UnstyledIcon(Lucide.Check, contentDescription = null, tint = Color.Black)
                  }
                }

                Spacer(Modifier.width(12.dp))
                BasicText(snapPoint.identifier)
              }
            }
          }
        }
      }
    }
  }
}
