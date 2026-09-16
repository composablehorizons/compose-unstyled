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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composeunstyled.DragHandle
import com.composeunstyled.DrawerPanelAlignment
import com.composeunstyled.DrawerPlacement
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Panel
import com.composeunstyled.SystemUi
import com.composeunstyled.SystemUiAppearance
import com.composeunstyled.Text
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledDrawerState
import com.composeunstyled.UnstyledTextField
import com.composeunstyled.Viewport
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.demoColors
import com.composeunstyled.demo.demoContent
import com.composeunstyled.demo.demoSurface
import com.composeunstyled.theme.Theme

private enum class DrawerImeDemoValue {
  Closed,
  Open,
}

@Preview
@UnstyledDemo("drawer-form")
@Composable
fun DrawerImeDemo() {
  val snapPoints = remember {
    DrawerSnapPoints<DrawerImeDemoValue> {
      DrawerImeDemoValue.Closed at DrawerSnapPoint.Zero
      DrawerImeDemoValue.Open at DrawerSnapPoint.ContentSize
    }
  }
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerImeDemoValue.Closed,
      snapPoints = snapPoints,
    )
  }
  val name = rememberTextFieldState()
  val email = rememberTextFieldState()
  val fieldTextStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp)

  Box(Modifier.fillMaxSize().background(Theme[demoColors][demoSurface])) {
    Box(
      modifier = Modifier.fillMaxSize().padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      UnstyledButton(
        onClick = { drawerState.targetValue = DrawerImeDemoValue.Open },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = Modifier.background(
          Theme[demoColors][demoSurface],
        ).border(1.dp, Theme[demoColors][demoContent]),
        indication = LocalIndication.current,
      ) {
        Text("Open drawer")
      }
    }

    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
      placement = DrawerPlacement.Bottom,
      systemUi = SystemUi(
        statusBar = SystemUiAppearance.Light,
        navigationBar = SystemUiAppearance.Dark,
      ),
    ) {
      Box(Modifier.fillMaxSize()) {
        Viewport(
          modifier = Modifier.fillMaxSize(),
          panelAlignment = DrawerPanelAlignment.Center,
          windowInsets = WindowInsets.ime,
        ) {
          Panel(
            modifier = Modifier
              .widthIn(max = 640.dp)
              .fillMaxWidth()
              .background(Theme[demoColors][demoSurface])
              .border(1.dp, Theme[demoColors][demoContent]),
          ) {
            Box {
              Column(
                modifier = Modifier
                  .verticalScroll(rememberScrollState())
                  .windowInsetsPadding(WindowInsets.navigationBars)
                  .padding(start = 24.dp, top = 60.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
              ) {
                Text(
                  "Here is the content of the drawer.",
                  fontSize = 24.sp,
                  lineHeight = 32.sp,
                )
                Text(
                  "Focus a field to test the keyboard inset.",
                  fontSize = 14.sp,
                  lineHeight = 20.sp,
                )

                UnstyledTextField(
                  state = name,
                  modifier = Modifier.fillMaxWidth(),
                  accessibilityLabel = "First field",
                  lineLimits = TextFieldLineLimits.SingleLine,
                  cursorBrush = SolidColor(Theme[demoColors][demoContent]),
                  textStyle = fieldTextStyle,
                ) {
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("First field", style = fieldTextStyle)
                    TextInput(
                      modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Theme[demoColors][demoContent])
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                      placeholder = {
                        Text("Type here", style = fieldTextStyle)
                      },
                    )
                  }
                }

                UnstyledTextField(
                  state = email,
                  modifier = Modifier.fillMaxWidth(),
                  accessibilityLabel = "Second field",
                  lineLimits = TextFieldLineLimits.SingleLine,
                  cursorBrush = SolidColor(Theme[demoColors][demoContent]),
                  textStyle = fieldTextStyle,
                ) {
                  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Second field", style = fieldTextStyle)
                    TextInput(
                      modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Theme[demoColors][demoContent])
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                      placeholder = {
                        Text("Type here", style = fieldTextStyle)
                      },
                    )
                  }
                }

                UnstyledButton(
                  onClick = { drawerState.targetValue = DrawerImeDemoValue.Closed },
                  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme[demoColors][demoSurface])
                    .border(1.dp, Theme[demoColors][demoContent]),
                  indication = LocalIndication.current,
                ) {
                  Text("Close drawer")
                }
              }

              Box(
                modifier = Modifier
                  .align(Alignment.TopCenter)
                  .zIndex(1f)
                  .fillMaxWidth()
                  .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter,
              ) {
                DragHandle {
                  Box(Modifier.width(32.dp).height(4.dp).background(Theme[demoColors][demoContent]))
                }
              }
            }
          }
        }
      }
    }
  }
}
