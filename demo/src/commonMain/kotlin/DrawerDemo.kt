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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragHandle
import com.composeunstyled.DrawerPlacement
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Overlay
import com.composeunstyled.Panel
import com.composeunstyled.SwipeArea
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledDrawerState
import com.composeunstyled.Viewport

private enum class DrawerDemoValue {
  Closed,
  Open,
}

@Composable
fun DrawerDemo() {
  val snapPoints = remember {
    DrawerSnapPoints<DrawerDemoValue> {
      DrawerDemoValue.Closed at DrawerSnapPoint.Zero
      DrawerDemoValue.Open at DrawerSnapPoint.ContentSize
    }
  }
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerDemoValue.Open,
      snapPoints = snapPoints,
    )
  }

  Box(Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      UnstyledButton(
        onClick = { drawerState.targetValue = DrawerDemoValue.Open },
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.background(Color.White).border(1.dp, Color.Black),
        indication = LocalIndication.current,
      ) {
        BasicText("Open navigation")
      }
    }

    UnstyledDrawer(
      state = drawerState,
      placement = DrawerPlacement.Start,
      overlay = {
        Overlay(
          modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.33f)),
          enter = fadeIn(),
          exit = fadeOut(),
        )
      },
      modifier = Modifier.fillMaxSize(),
    ) {
      Viewport(modifier = Modifier.fillMaxSize()) {
        Panel(
          modifier = Modifier
            .width(288.dp)
            .fillMaxHeight()
            .background(Color.White)
            .border(1.dp, Color.Black)
            .padding(24.dp),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DragHandle {
              Box(
                modifier = Modifier.width(32.dp).border(1.dp, Color.Black),
              )
            }
            BasicText("Navigation")
            BasicText("Inbox")
            BasicText("Archive")
            UnstyledButton(
              onClick = { drawerState.targetValue = DrawerDemoValue.Closed },
              contentPadding = PaddingValues(12.dp),
              modifier = Modifier.background(Color.White).border(1.dp, Color.Black),
              indication = LocalIndication.current,
            ) {
              BasicText("Close")
            }
          }
        }
      }
      SwipeArea(
        modifier = Modifier
          .width(24.dp)
          .fillMaxHeight(),
      )
    }
  }
}
