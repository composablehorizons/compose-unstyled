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
package com.composeunstyled.demo.drawer

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Panel
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledDrawerState
import com.composeunstyled.Viewport
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.borderToken
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.inputBackgroundToken
import com.composeunstyled.demo.surfaceToken
import com.composeunstyled.theme.Theme

private enum class DrawerAnimatedContentDemoValue {
  Closed,
  Open,
}

@Preview
@UnstyledDemo("drawer-animated-content", name = "Drawer Animated Content")
@Composable
fun DrawerAnimatedContentDemo() {
  val snapPoints = remember {
    DrawerSnapPoints<DrawerAnimatedContentDemoValue> {
      DrawerAnimatedContentDemoValue.Closed at DrawerSnapPoint.Zero
      DrawerAnimatedContentDemoValue.Open at DrawerSnapPoint.ContentSize
    }
  }
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerAnimatedContentDemoValue.Closed,
      snapPoints = snapPoints,
    )
  }
  var selectedTab by remember { mutableIntStateOf(0) }

  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    UnstyledButton(
      onClick = { drawerState.targetValue = DrawerAnimatedContentDemoValue.Open },
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      modifier = Modifier
        .background(Theme[colors][surfaceToken])
        .border(1.dp, Theme[colors][borderToken]),
      indication = LocalIndication.current,
    ) {
      Text("Open drawer")
    }

    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
    ) {
      Viewport(Modifier.fillMaxSize()) {
        Panel(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .background(Theme[colors][surfaceToken])
            .border(1.dp, Theme[colors][borderToken]),
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .animateContentSize(tween()),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              UnstyledButton(
                onClick = { selectedTab = 0 },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                  .background(Theme[colors][surfaceToken])
                  .border(1.dp, Theme[colors][borderToken]),
                indication = LocalIndication.current,
              ) {
                Text("List")
              }
              UnstyledButton(
                onClick = { selectedTab = 1 },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                  .background(Theme[colors][surfaceToken])
                  .border(1.dp, Theme[colors][borderToken]),
                indication = LocalIndication.current,
              ) {
                Text("Text")
              }
            }

            if (selectedTab == 0) {
              LazyColumn {
                items(10) { index ->
                  Text(
                    text = "Item ${index + 1}",
                    modifier = Modifier
                      .fillMaxWidth()
                      .background(Theme[colors][inputBackgroundToken])
                      .padding(20.dp),
                  )
                }
              }
            } else {
              Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Text("Header")
                Text("Some text")
              }
            }
          }
        }
      }
    }
  }
}
