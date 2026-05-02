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
package com.composeunstyled

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

class TabGroupCommonTest {
  @Test
  fun showsPanelOfTheActivatedTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        tabs = listOf("tab1", "tab2", "tab3"),
      ) {
        UnstyledTabList(Modifier.testTag("tablist")) {
          UnstyledTab(
            key = "tab1",
            modifier = Modifier.testTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            key = "tab2",
            modifier = Modifier.testTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            key = "tab3",
            modifier = Modifier.testTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
          ) {
            BasicText("Tab #3")
          }
        }
        UnstyledTabPanel(key = "tab1", Modifier.testTag("panelA")) {
          BasicText("Panel A")
        }
        UnstyledTabPanel(key = "tab2", Modifier.testTag("panelB")) {
          BasicText("Panel B")
        }
        UnstyledTabPanel(key = "tab3", Modifier.testTag("panelC")) {
          BasicText("Panel C")
        }
      }
    }

    onNodeWithTag("tab1").performClick()
    onNodeWithTag("panelA").isDisplayed()
    onNodeWithTag("panelB").assertDoesNotExist()
    onNodeWithTag("panelC").assertDoesNotExist()

    onNodeWithTag("tab2").performClick()
    onNodeWithTag("panelA").assertDoesNotExist()
    onNodeWithTag("panelB").isDisplayed()
    onNodeWithTag("panelC").assertDoesNotExist()

    onNodeWithTag("tab3").performClick()
    onNodeWithTag("panelA").assertDoesNotExist()
    onNodeWithTag("panelB").assertDoesNotExist()
    onNodeWithTag("panelC").isDisplayed()
  }
}
