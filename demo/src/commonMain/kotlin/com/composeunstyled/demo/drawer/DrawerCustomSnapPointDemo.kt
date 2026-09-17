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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.contentToken
import com.composeunstyled.demo.surfaceToken
import com.composeunstyled.theme.Theme

private enum class DrawerCustomSnapPointDemoValue {
  Closed,
  Half,
  Open,
}

@Preview
@UnstyledDemo("drawer-custom-snap-point")
@Composable
fun DrawerCustomSnapPointDemo() {
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerCustomSnapPointDemoValue.Half,
      snapPoints = DrawerSnapPoints {
        DrawerCustomSnapPointDemoValue.Closed at DrawerSnapPoint.Zero
        DrawerCustomSnapPointDemoValue.Half at DrawerSnapPoint { viewportSize, _ ->
          viewportSize * 0.5f
        }
        DrawerCustomSnapPointDemoValue.Open at DrawerSnapPoint.ContentSize
      },
    )
  }

  Box(Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier.align(Alignment.Center),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text("The half state uses half of the viewport height.")
      UnstyledButton(
        onClick = { drawerState.targetValue = DrawerCustomSnapPointDemoValue.Half },
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier
          .background(Theme[colors][surfaceToken])
          .border(1.dp, Theme[colors][contentToken]),
        indication = LocalIndication.current,
      ) {
        Text("Show half")
      }
    }

    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
    ) {
      Viewport(Modifier.fillMaxSize()) {
        Panel(
          modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Theme[colors][surfaceToken])
            .border(1.dp, Theme[colors][contentToken])
            .padding(24.dp),
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            Text("Here is the content of the drawer.")
            UnstyledButton(
              onClick = { drawerState.targetValue = DrawerCustomSnapPointDemoValue.Open },
              contentPadding = PaddingValues(12.dp),
              modifier = Modifier
                .background(Theme[colors][surfaceToken])
                .border(1.dp, Theme[colors][contentToken]),
              indication = LocalIndication.current,
            ) {
              Text("Open fully")
            }
            UnstyledButton(
              onClick = { drawerState.targetValue = DrawerCustomSnapPointDemoValue.Closed },
              contentPadding = PaddingValues(12.dp),
              modifier = Modifier
                .background(Theme[colors][surfaceToken])
                .border(1.dp, Theme[colors][contentToken]),
              indication = LocalIndication.current,
            ) {
              Text("Close")
            }
          }
        }
      }
    }
  }
}
