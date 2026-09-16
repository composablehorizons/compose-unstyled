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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragHandle
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Panel
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledDrawerState
import com.composeunstyled.Viewport
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.demoColors
import com.composeunstyled.demo.demoContent
import com.composeunstyled.demo.demoSurface
import com.composeunstyled.theme.Theme

private enum class DrawerDynamicContentDemoValue {
  Closed,
  Open,
}

@Preview
@UnstyledDemo("drawer-dynamic-content")
@Composable
fun DrawerDynamicContentDemo() {
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerDynamicContentDemoValue.Open,
      snapPoints = DrawerSnapPoints {
        DrawerDynamicContentDemoValue.Closed at DrawerSnapPoint.Zero
        DrawerDynamicContentDemoValue.Open at DrawerSnapPoint.ContentSize
      },
    )
  }
  var showDetails by remember { mutableStateOf(false) }

  Box(Modifier.fillMaxSize().background(Theme[demoColors][demoSurface])) {
    UnstyledButton(
      onClick = { drawerState.targetValue = DrawerDynamicContentDemoValue.Open },
      contentPadding = PaddingValues(12.dp),
      modifier = Modifier
        .align(Alignment.Center)
        .background(Theme[demoColors][demoSurface])
        .border(1.dp, Theme[demoColors][demoContent]),
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
            .fillMaxWidth()
            .animateContentSize()
            .background(Theme[demoColors][demoSurface])
            .border(1.dp, Theme[demoColors][demoContent])
            .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            DragHandle {
              Box(
                Modifier
                  .width(32.dp)
                  .height(4.dp)
                  .background(Theme[demoColors][demoContent]),
              )
            }
            Text("Here is the content of the drawer.")
            if (showDetails) {
              Text("Additional content.")
            }
            UnstyledButton(
              onClick = { showDetails = showDetails.not() },
              contentPadding = PaddingValues(12.dp),
              modifier = Modifier
                .background(Theme[demoColors][demoSurface])
                .border(1.dp, Theme[demoColors][demoContent]),
              indication = LocalIndication.current,
            ) {
              Text(if (showDetails) "Remove content" else "Add content")
            }
          }
        }
      }
    }
  }
}
