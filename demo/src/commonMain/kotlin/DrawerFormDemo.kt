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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composeunstyled.DragHandle
import com.composeunstyled.DrawerPanelAlignment
import com.composeunstyled.DrawerPlacement
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Overlay
import com.composeunstyled.Panel
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledDrawerState
import com.composeunstyled.UnstyledTextField
import com.composeunstyled.Viewport

private enum class DrawerFormDemoValue {
  Closed,
  Open,
}

@Composable
fun DrawerFormDemo() {
  val snapPoints = remember {
    DrawerSnapPoints<DrawerFormDemoValue> {
      DrawerFormDemoValue.Closed at DrawerSnapPoint.Zero
      DrawerFormDemoValue.Open at DrawerSnapPoint.ContentSize
    }
  }
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerFormDemoValue.Closed,
      snapPoints = snapPoints,
    )
  }
  val name = rememberTextFieldState()
  val email = rememberTextFieldState()
  val fieldTextStyle = TextStyle(
    color = Color.Black,
    fontSize = 16.sp,
    lineHeight = 24.sp,
  )

  Box(Modifier.fillMaxSize().background(Color.White)) {
    Box(
      modifier = Modifier.fillMaxSize().padding(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      UnstyledButton(
        onClick = { drawerState.targetValue = DrawerFormDemoValue.Open },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = Modifier.background(Color.White).border(1.dp, Color.Black),
        indication = LocalIndication.current,
      ) {
        BasicText("Open form")
      }
    }

    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
      placement = DrawerPlacement.Bottom,
      overlay = {
        Overlay(
          modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.33f)),
          enter = fadeIn(),
          exit = fadeOut(),
        )
      },
    ) {
      Viewport(
        modifier = Modifier.fillMaxSize(),
        panelAlignment = DrawerPanelAlignment.Center,
        windowInsets = WindowInsets.ime,
      ) {
        Panel(
          modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color.Black),
        ) {
          Box {
            Column(
              modifier = Modifier
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 24.dp, top = 60.dp, end = 24.dp, bottom = 24.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              BasicText(
                "Contact form",
                style = TextStyle(
                  color = Color.Black,
                  fontSize = 24.sp,
                  lineHeight = 32.sp,
                ),
              )
              BasicText(
                "Focus a field to test the keyboard inset.",
                style = TextStyle(
                  color = Color.Black,
                  fontSize = 14.sp,
                  lineHeight = 20.sp,
                ),
              )

              UnstyledTextField(
                state = name,
                modifier = Modifier.fillMaxWidth(),
                accessibilityLabel = "Name",
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(Color.Black),
                textStyle = fieldTextStyle,
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  BasicText("Name", style = fieldTextStyle)
                  TextInput(
                    modifier = Modifier
                      .fillMaxWidth()
                      .border(1.dp, Color.Black)
                      .padding(horizontal = 12.dp, vertical = 10.dp),
                    placeholder = {
                      BasicText(
                        "Alex",
                        style = fieldTextStyle.copy(color = Color.Black.copy(alpha = 0.5f)),
                      )
                    },
                  )
                }
              }

              UnstyledTextField(
                state = email,
                modifier = Modifier.fillMaxWidth(),
                accessibilityLabel = "Email",
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(Color.Black),
                textStyle = fieldTextStyle,
              ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                  BasicText("Email", style = fieldTextStyle)
                  TextInput(
                    modifier = Modifier
                      .fillMaxWidth()
                      .border(1.dp, Color.Black)
                      .padding(horizontal = 12.dp, vertical = 10.dp),
                    placeholder = {
                      BasicText(
                        "alex@example.com",
                        style = fieldTextStyle.copy(color = Color.Black.copy(alpha = 0.5f)),
                      )
                    },
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                UnstyledButton(
                  onClick = { drawerState.targetValue = DrawerFormDemoValue.Closed },
                  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                  modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
                    .border(1.dp, Color.Black),
                  indication = LocalIndication.current,
                ) {
                  BasicText("Cancel")
                }
                UnstyledButton(
                  onClick = { drawerState.targetValue = DrawerFormDemoValue.Closed },
                  contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                  modifier = Modifier.weight(1f).background(Color.Black),
                  indication = LocalIndication.current,
                ) {
                  BasicText("Submit", style = TextStyle(color = Color.White))
                }
              }
            }

            Box(
              modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f)
                .fillMaxWidth()
                .padding(top = 24.dp),
              contentAlignment = Alignment.TopCenter,
            ) {
              DragHandle {
                Box(Modifier.width(48.dp).height(4.dp).background(Color.Black))
              }
            }
          }
        }
      }
    }
  }
}
