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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Content
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoint.Companion.Closed
import com.composeunstyled.DrawerSnapPoint.Companion.Open
import com.composeunstyled.Panel
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.Viewport
import com.composeunstyled.rememberDrawerState

@Composable
fun DrawerDynamicSnapPointsDemo() {
  val cartSummary = remember {
    DrawerSnapPoint("cart-summary") { _, _ ->
      88.dp
    }
  }
  var bagelCount by remember { mutableIntStateOf(0) }
  var coffeeCount by remember { mutableIntStateOf(0) }
  val itemCount = bagelCount + coffeeCount
  val hasItems = itemCount > 0
  val drawerState = rememberDrawerState(
    initialSnapPoint = Closed,
    snapPoints = {
      if (hasItems) {
        listOf(Closed, cartSummary, Open)
      } else {
        listOf(Closed, Open)
      }
    },
  )

  LaunchedEffect(itemCount) {
    if (itemCount == 0) {
      drawerState.animateTo(Closed)
    } else {
      drawerState.animateTo(cartSummary)
    }
  }

  Box(Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      BasicText(
        text = "Bakery pickup",
        style = TextStyle(
          color = Color.Black,
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium,
        ),
      )
      ProductRow(
        name = "Sesame bagel",
        price = "\$4",
        count = bagelCount,
        onAdd = {
          bagelCount += 1
        },
        onRemove = {
          bagelCount = (bagelCount - 1).coerceAtLeast(0)
        },
      )
      ProductRow(
        name = "Iced coffee",
        price = "\$5",
        count = coffeeCount,
        onAdd = {
          coffeeCount += 1
        },
        onRemove = {
          coffeeCount = (coffeeCount - 1).coerceAtLeast(0)
        },
      )
    }

    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
    ) {
      Viewport(
        modifier = Modifier.fillMaxSize(),
      ) {
        Panel(
          modifier = Modifier
            .dropShadow(
              shape = RectangleShape,
              shadow = Shadow(
                radius = 0.dp,
                color = Color.Black,
                spread = 0.dp,
                offset = DpOffset(8.dp, 8.dp),
                alpha = 0.33f,
              ),
            )
            .background(Color.White)
            .border(1.dp, Color.Black)
            .fillMaxWidth(),
        ) {
          Content(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
          ) {
            Column(
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                BasicText(
                  text = "$itemCount item${if (itemCount == 1) "" else "s"}",
                  style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                  ),
                )
                Spacer(Modifier.weight(1f))
                DemoButton(
                  text = "Review",
                  onClick = {
                    drawerState.targetSnapPoint = Open
                  },
                )
              }
              CartLine("Sesame bagel", bagelCount)
              CartLine("Iced coffee", coffeeCount)
              DemoButton(
                text = "Clear cart",
                onClick = {
                  bagelCount = 0
                  coffeeCount = 0
                },
              )
              Spacer(Modifier.height(360.dp))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ProductRow(
  name: String,
  price: String,
  count: Int,
  onAdd: () -> Unit,
  onRemove: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.White)
      .border(1.dp, Color(0xFFE5E7EB))
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column {
      BasicText(
        text = name,
        style = TextStyle(color = Color.Black, fontWeight = FontWeight.Medium),
      )
      BasicText(
        text = price,
        style = TextStyle(color = Color(0xFF4B5563)),
      )
    }
    Spacer(Modifier.weight(1f))
    DemoButton(
      text = "-",
      onClick = onRemove,
      enabled = count > 0,
    )
    BasicText(
      text = count.toString(),
      modifier = Modifier.width(28.dp),
      style = TextStyle(color = Color.Black),
    )
    DemoButton(
      text = "+",
      onClick = onAdd,
    )
  }
}

@Composable
private fun CartLine(
  name: String,
  count: Int,
) {
  if (count > 0) {
    Row(Modifier.fillMaxWidth()) {
      BasicText(
        text = name,
        style = TextStyle(color = Color.Black),
      )
      Spacer(Modifier.weight(1f))
      BasicText(
        text = "x$count",
        style = TextStyle(color = Color.Black),
      )
    }
  }
}

@Composable
private fun DemoButton(
  text: String,
  onClick: () -> Unit,
  enabled: Boolean = true,
) {
  UnstyledButton(
    onClick = onClick,
    enabled = enabled,
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .heightIn(32.dp)
      .background(if (enabled) Color(0xFFF8FAFC) else Color(0xFFE5E7EB))
      .border(1.dp, Color(0xFFCACACA), RoundedCornerShape(10.dp)),
    contentPadding = PaddingValues(horizontal = 10.dp),
    indication = LocalIndication.current,
  ) {
    BasicText(text)
  }
}
