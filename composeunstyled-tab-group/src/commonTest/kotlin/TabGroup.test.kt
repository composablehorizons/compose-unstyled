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

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
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
        TabList(Modifier.testTag("tablist").requiredWidth(160.dp)) {
          Tab(
            key = "tab1",
            modifier = Modifier.testTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            key = "tab2",
            modifier = Modifier.testTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            key = "tab3",
            modifier = Modifier.testTag("tab3").offset(x = 96.dp),
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

  @Test
  fun tabWithUnknownKeyRendersDisabledUnselectedContent() = runComposeUiTest {
    var selectedTab by mutableStateOf("known")

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = listOf("known"),
      ) {
        TabList {
          Tab(
            key = "unknown",
            modifier = Modifier.testTag("unknown"),
          ) {
            BasicText(
              text = when {
                selected -> "selected"
                enabled.not() -> "disabled"
                else -> "unselected"
              },
            )
          }
        }
      }
    }

    onNodeWithTag("unknown").performClick()

    assertThat(selectedTab).isEqualTo("known")
    onNodeWithText("disabled").isDisplayed()
  }

  @Test
  fun unscopedTabsShowPanelOfTheActivatedTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = listOf("tab1", "tab2", "tab3"),
      ) {
        UnstyledTabList(Modifier.testTag("tablist").requiredWidth(160.dp)) {
          UnstyledTab(
            key = "tab1",
            modifier = Modifier.testTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            key = "tab2",
            modifier = Modifier.testTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            key = "tab3",
            modifier = Modifier.testTag("tab3").offset(x = 96.dp),
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

  @Test
  fun unscopedTabsSupportTypedTabKeys() = runComposeUiTest {
    var selectedTab by mutableStateOf(SettingsTab.Account)

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = SettingsTab.entries,
      ) {
        UnstyledTabList {
          UnstyledTab(
            key = SettingsTab.Account,
            modifier = Modifier.testTag("account"),
          ) {
            BasicText("Account")
          }
          UnstyledTab(
            key = SettingsTab.Billing,
            modifier = Modifier.testTag("billing"),
          ) {
            BasicText("Billing")
          }
        }
        UnstyledTabPanel(key = SettingsTab.Account) {
          BasicText("Account panel")
        }
        UnstyledTabPanel(key = SettingsTab.Billing) {
          BasicText("Billing panel")
        }
      }
    }

    onNodeWithTag("billing").performClick()

    assertThat(selectedTab).isEqualTo(SettingsTab.Billing)
    onNodeWithText("Account panel").assertDoesNotExist()
    onNodeWithText("Billing panel").isDisplayed()
  }

  @Test
  fun unscopedTabWithUnknownKeyRendersDisabledUnselectedContent() = runComposeUiTest {
    var selectedTab by mutableStateOf("known")

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = listOf("known"),
      ) {
        UnstyledTabList {
          UnstyledTab(
            key = "unknown",
            modifier = Modifier.testTag("unknown"),
          ) {
            BasicText(
              text = when {
                selected -> "selected"
                enabled.not() -> "disabled"
                else -> "unselected"
              },
            )
          }
        }
      }
    }

    onNodeWithTag("unknown").performClick()

    assertThat(selectedTab).isEqualTo("known")
    onNodeWithText("disabled").isDisplayed()
  }

  @Test
  fun unscopedTabListOutsideTabGroupRendersContent() = runComposeUiTest {
    setContent {
      UnstyledTabList {
        BasicText("Tab list")
      }
    }

    onNodeWithText("Tab list").isDisplayed()
  }

  @Test
  fun unscopedTabOutsideTabListRendersDisabledUnselectedContent() = runComposeUiTest {
    setContent {
      UnstyledTab(
        key = "tab",
      ) {
        BasicText(
          text = when {
            selected -> "selected"
            enabled.not() -> "disabled"
            else -> "unselected"
          },
        )
      }
    }

    onNodeWithText("disabled").isDisplayed()
  }

  @Test
  fun unscopedTabInsideTabGroupWithoutTabListRendersDisabledUnselectedContent() = runComposeUiTest {
    setContent {
      UnstyledTabGroup(
        selectedTab = "tab",
        onSelectedTabChange = {},
        tabs = listOf("tab"),
      ) {
        UnstyledTab(
          key = "tab",
        ) {
          BasicText(
            text = when {
              selected -> "selected"
              enabled.not() -> "disabled"
              else -> "unselected"
            },
          )
        }
      }
    }

    onNodeWithText("disabled").isDisplayed()
  }

  @Test
  fun unscopedTabPanelOutsideTabGroupRendersNothing() = runComposeUiTest {
    setContent {
      UnstyledTabPanel(key = "tab") {
        BasicText("Panel")
      }
    }

    onNodeWithText("Panel").assertDoesNotExist()
  }
}
