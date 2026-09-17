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

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.DrawerHost
import com.composeunstyled.DrawerPlacement
import com.composeunstyled.DrawerPresentation
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Panel
import com.composeunstyled.SwipeArea
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

private enum class DrawerSwipeAreaDemoValue {
  Closed,
  Open,
}

@Preview
@UnstyledDemo("drawer-swipe-area")
@Composable
fun DrawerSwipeAreaDemo() {
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerSwipeAreaDemoValue.Closed,
      snapPoints = DrawerSnapPoints {
        DrawerSwipeAreaDemoValue.Closed at DrawerSnapPoint.Zero
        DrawerSwipeAreaDemoValue.Open at DrawerSnapPoint.ContentSize
      },
    )
  }

  DrawerHost(Modifier.fillMaxSize()) {
    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
      placement = DrawerPlacement.Start,
      presentation = DrawerPresentation.Overlay,
    ) {
      Box(Modifier.fillMaxSize()) {
        val swipeAreaModifier = Modifier
          .align(Alignment.CenterStart)
          .width(200.dp)
          .fillMaxHeight()

        Box(
          modifier = swipeAreaModifier.border(1.dp, Theme[colors][borderToken]),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            "Start a swipe anywhere inside this area",
            modifier = Modifier.padding(16.dp),
          )
        }

        Viewport(Modifier.fillMaxSize()) {
          Panel(
            modifier = Modifier
              .width(288.dp)
              .fillMaxHeight()
              .background(Theme[colors][inputBackgroundToken])
              .border(1.dp, Theme[colors][borderToken])
              .padding(start = 12.dp, top = 24.dp, end = 24.dp, bottom = 24.dp),
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
              Text("Here is the content of the drawer.")
              UnstyledButton(
                onClick = { drawerState.targetValue = DrawerSwipeAreaDemoValue.Closed },
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier
                  .background(Theme[colors][surfaceToken])
                  .border(1.dp, Theme[colors][borderToken]),
                indication = LocalIndication.current,
              ) {
                Text("Close")
              }
            }
          }
        }

        SwipeArea(swipeAreaModifier)
      }
    }
  }
}
