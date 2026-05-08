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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

class TabGroupCommonTest {
  private enum class SettingsTab {
    Account,
    Billing,
  }

  @Test
  fun showsPanelOfTheActivatedTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = listOf("tab1", "tab2", "tab3"),
      ) {
        TabList(Modifier.testTag("tablist")) {
          Tab(
            key = "tab1",
            modifier = Modifier.testTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            key = "tab2",
            modifier = Modifier.testTag("tab2"),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            key = "tab3",
            modifier = Modifier.testTag("tab3"),
          ) {
            BasicText("Tab #3")
          }
        }
        TabPanel(key = "tab1", Modifier.testTag("panelA")) {
          BasicText("Panel A")
        }
        TabPanel(key = "tab2", Modifier.testTag("panelB")) {
          BasicText("Panel B")
        }
        TabPanel(key = "tab3", Modifier.testTag("panelC")) {
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

  @Test
  fun supportsTypedTabKeys() = runComposeUiTest {
    var selectedTab by mutableStateOf(SettingsTab.Account)

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = SettingsTab.entries,
      ) {
        TabList {
          Tab(
            key = SettingsTab.Account,
            modifier = Modifier.testTag("account"),
          ) {
            BasicText("Account")
          }
          Tab(
            key = SettingsTab.Billing,
            modifier = Modifier.testTag("billing"),
          ) {
            BasicText("Billing")
          }
        }
        TabPanel(key = SettingsTab.Account) {
          BasicText("Account panel")
        }
        TabPanel(key = SettingsTab.Billing) {
          BasicText("Billing panel")
        }
      }
    }

    onNodeWithTag("billing").performClick()

    onNodeWithText("Account panel").assertDoesNotExist()
    onNodeWithText("Billing panel").isDisplayed()
  }
}
