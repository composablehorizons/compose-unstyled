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
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.KeyInjectionScope
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class TabGroupTest {
  @Test
  fun givenFocusIsOutsideOfTabList_whenFocusIsMovedIntoTabList_thenMovesFocusToFistTab() =
    runComposeUiTest {
      var selectedTab by mutableStateOf("tab1")

      setContent {
        UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("button")) {
          BasicText("Outside")
        }

        UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
          selectedTab = it
        }, tabs = listOf("tab1", "tab2", "tab3")) {
          TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
            Tab(
              key = "tab1",
              modifier = Modifier.testFocusTag("tab1"),
            ) {
              BasicText("Tab #1")
            }
            Tab(
              key = "tab2",
              modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
            ) {
              BasicText("Tab #2")
            }
            Tab(
              key = "tab3",
              modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
            ) {
              BasicText("Tab #3")
            }
          }
        }
      }
      onNodeWithTag("button").requestFocus()
      onRoot().performKeyInput {
        keyPress(Key.Tab)
      }
      onNodeWithTag("tab1").assertIsFocused()
    }

  @Test
  fun givenFocusIsOutsideOfTabList_whenFocusIsMovedIntoTabList_thenMovesFocusToActivatedTab() =
    runComposeUiTest {
      var selectedTab by mutableStateOf("tab2")

      setContent {
        UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("button")) {
          BasicText("Outside")
        }

        UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
          selectedTab = it
        }, tabs = listOf("tab1", "tab2", "tab3")) {
          TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
            Tab(
              key = "tab1",
              modifier = Modifier.testFocusTag("tab1"),
            ) {
              BasicText("Tab #1")
            }
            Tab(
              key = "tab2",
              modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
            ) {
              BasicText("Tab #2")
            }
            Tab(
              key = "tab3",
              modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
            ) {
              BasicText("Tab #3")
            }
          }
        }
      }
      onNodeWithTag("button").requestFocus()
      onRoot().performKeyInput {
        keyPress(Key.Tab)
      }
      onNodeWithTag("tab2").assertIsFocused()
    }

  @Test
  fun givenTabListHasFocus_whenPressingRightArrowKey_thenMovesFocusToSecondTab() =
    runComposeUiTest {
      var selectedTab by mutableStateOf("tab1")

      setContent {
        UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("button")) {
          BasicText("Outside")
        }
        UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
          selectedTab = it
        }, tabs = listOf("tab1", "tab2", "tab3")) {
          TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
            Tab(
              "tab1",
              modifier = Modifier.testFocusTag("tab1"),
            ) {
              BasicText("Tab #1")
            }
            Tab(
              "tab2",
              modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
            ) {
              BasicText("Tab #2")
            }
            Tab(
              "tab3",
              modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
            ) {
              BasicText("Tab #3")
            }
          }
        }
      }
      onNodeWithTag("tab1").requestFocus()
      onNodeWithTag("tab1").assertIsFocused()

      onRoot().performKeyInput {
        keyPress(Key.DirectionRight)
      }
      onNodeWithTag("tab2").assertIsFocused()
    }

  @Test
  fun givenTabListHasFocus_whenPressingLeftArrowKey_thenMovesFocusToLastTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledButton(
        onClick = {},
        modifier = Modifier.testFocusTag("button").testFocusTag("button"),
      ) {
        BasicText("Outside")
      }

      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1").testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp).testFocusTag("tab2"),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp).testFocusTag("tab3"),
          ) {
            BasicText("Tab #3")
          }
        }
      }
    }
    onNodeWithTag("tab1").requestFocus()
    onRoot().performKeyInput {
      keyPress(Key.DirectionLeft)
    }
    onNodeWithTag("tab3").assertIsFocused()
  }

  @Test
  fun givenTabHasFocus_whenPressingTab_thenMovesFocusToRespectiveTabPanel() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
          ) {
            BasicText("Tab #3")
          }
        }
        TabPanel(key = "tab1", modifier = Modifier.testFocusTag("panelA")) {
          UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("panelAButton")) {
            BasicText("Panel A")
          }
        }
        TabPanel(key = "tab2", modifier = Modifier.testFocusTag("panelB")) {
          UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("panelBButton")) {
            BasicText("Panel B")
          }
        }
        TabPanel(key = "tab3", modifier = Modifier.testFocusTag("panelC")) {
          UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("panelCButton")) {
            BasicText("Panel C")
          }
        }
      }
    }
    onNodeWithTag("tab1").requestFocus()
    onRoot().performKeyInput {
      keyPress(Key.Tab)
    }
    onNodeWithTag("panelA").isDisplayed()
    onNodeWithTag("panelA").assertIsFocused()
    onNodeWithTag("panelB").assertDoesNotExist()
    onNodeWithTag("panelC").assertDoesNotExist()
  }

  @Test
  fun givenTabPanelHasNoFocusableContent_whenPressingTab_thenMovesFocusToTabPanel() =
    runComposeUiTest {
      var selectedTab by mutableStateOf("tab1")

      setContent {
        UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
          selectedTab = it
        }, tabs = listOf("tab1", "tab2")) {
          TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
            Tab(
              key = "tab1",
              modifier = Modifier.testFocusTag("tab1"),
            ) {
              BasicText("Tab #1")
            }
            Tab(
              key = "tab2",
              modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
            ) {
              BasicText("Tab #2")
            }
          }
          TabPanel(
            key = "tab1",
            modifier = Modifier.testFocusTag("panelA"),
          ) {
            BasicText("Panel A")
          }
          TabPanel(
            key = "tab2",
            modifier = Modifier.testFocusTag("panelB"),
          ) {
            BasicText("Panel B")
          }
        }
      }

      onNodeWithTag("tab1").requestFocus()
      onRoot().performKeyInput {
        keyPress(Key.Tab)
      }

      onNodeWithTag("panelA").isDisplayed()
      onNodeWithTag("panelA").assertIsFocused()
      onNodeWithTag("panelB").assertDoesNotExist()
    }

  @Test
  fun givenFocusReturnsFromPanelToTab_whenPressingShiftTab_thenMovesFocusBeforeTabGroup() =
    runComposeUiTest {
      var selectedTab by mutableStateOf("tab1")

      setContent {
        UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("before")) {
          BasicText("Before")
        }

        UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
          selectedTab = it
        }, tabs = listOf("tab1", "tab2", "tab3")) {
          TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
            Tab(
              key = "tab1",
              modifier = Modifier.testFocusTag("tab1"),
            ) {
              BasicText("Tab #1")
            }
            Tab(
              key = "tab2",
              modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
            ) {
              BasicText("Tab #2")
            }
            Tab(
              key = "tab3",
              modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
            ) {
              BasicText("Tab #3")
            }
          }
          TabPanel(key = "tab1", modifier = Modifier.testFocusTag("panelA")) {
            BasicText("Panel A")
          }
          TabPanel(key = "tab2", modifier = Modifier.testFocusTag("panelB")) {
            BasicText("Panel B")
          }
          TabPanel(key = "tab3", modifier = Modifier.testFocusTag("panelC")) {
            BasicText("Panel C")
          }
        }
      }

      onNodeWithTag("before").requestFocus()
      onRoot().performKeyInput {
        keyPress(Key.Tab)
        keyPress(Key.DirectionRight)
        keyPress(Key.DirectionRight)
      }
      waitForIdle()
      onRoot().performKeyInput {
        keyPress(Key.Tab)
      }
      onNodeWithTag("panelC").assertIsFocused()

      onRoot().performKeyInput {
        shiftTab()
      }
      onNodeWithTag("tab3").assertIsFocused()

      onRoot().performKeyInput {
        shiftTab()
      }
      onNodeWithTag("before").assertIsFocused()
    }

  @Test
  fun canFocusSecondTabDirectly() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab2")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
          ) {
            BasicText("Tab #3")
          }
        }
      }
    }

    onNodeWithTag("tab2").requestFocus()
    onNodeWithTag("tab2").assertIsFocused()
  }

  @Test
  fun givenTabHasFocus_whenPressingHomeKey_thenMovesFocusToFirstTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
          ) {
            BasicText("Tab #3")
          }
        }
      }
    }

    // Focus on the second tab first
    onNodeWithTag("tab2").performClick()
    onNodeWithTag("tab2").assertIsFocused()

    // Press Home key
    onRoot().performKeyInput {
      keyPress(Key.Home)
    }

    // Should move focus to the first tab
    onNodeWithTag("tab1").assertIsFocused()
  }

  @Test
  fun givenTabHasFocus_whenPressingEndKey_thenMovesFocusToLastTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
          ) {
            BasicText("Tab #3")
          }
        }
      }
    }

    // Focus on the second tab first
    onNodeWithTag("tab2").requestFocus()
    onNodeWithTag("tab2").assertIsFocused()

    // Press Home key
    onRoot().performKeyInput {
      keyPress(Key.MoveEnd)
    }

    // Should move focus to the first tab
    onNodeWithTag("tab3").assertIsFocused()
  }

  @Test
  fun givenTabHasFocus_whenPressingEnterKey_thenActivatesTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
          ) {
            BasicText("Tab #3")
          }
        }
      }
    }

    // Focus on the second tab first
    onNodeWithTag("tab2").requestFocus()
    onNodeWithTag("tab2").assertIsFocused()

    // Press Home key
    onRoot().performKeyInput {
      keyPress(Key.Enter)
    }

    assertThat(selectedTab).isEqualTo("tab2")
  }

  @Test
  fun givenTabHasFocus_whenPressingSpaceKey_thenActivatesTabAndShowsPanel() = runComposeUiTest {
    // TODO: Test that pressing Space key activates the tab and causes its associated panel to be displayed
    // According to W3C: "activates the tab, causing its associated panel to be displayed"
  }

  @Test
  fun showsPanelOfTheActivatedTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    println("Activated tab: $selectedTab")

    setContent {
      UnstyledTabGroup(
        selectedTab = selectedTab,
        onSelectedTabChange = { selectedTab = it },
        tabs = listOf("tab1", "tab2", "tab3"),
      ) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
          ) {
            BasicText("Tab #1")
          }
          Tab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
          ) {
            BasicText("Tab #2")
          }
          Tab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
          ) {
            BasicText("Tab #3")
          }
        }
        TabPanel(key = "tab1", Modifier.testFocusTag("panelA")) {
          BasicText("Panel A")
        }
        TabPanel(key = "tab2", Modifier.testFocusTag("panelB")) {
          BasicText("Panel B")
        }
        TabPanel(key = "tab3", Modifier.testFocusTag("panelC")) {
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
  fun givenTabReceivesFocus_whenActivateOnFocusIsTrue_thenActivatesTab() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab1")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, onSelectedTabChange = {
        selectedTab = it
      }, tabs = listOf("tab1", "tab2", "tab3")) {
        TabList(Modifier.testFocusTag("tablist").requiredWidth(160.dp)) {
          Tab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            activateOnFocus = true,
          ) {
            BasicText("Tab #1")
          }
          Tab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2").offset(x = 48.dp),
            activateOnFocus = true,
          ) {
            BasicText("Tab #2")
          }
          Tab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3").offset(x = 96.dp),
            activateOnFocus = true,
          ) {
            BasicText("Tab #3")
          }
        }
      }
    }

    // Focus on the second tab using arrow key navigation
    onNodeWithTag("tab1").requestFocus()
    onRoot().performKeyInput {
      keyPress(Key.DirectionRight)
    }

    // Verify that the second tab is focused and activated
    onNodeWithTag("tab2").assertIsFocused()
    assertThat(selectedTab).isEqualTo("tab2")
  }

  fun KeyInjectionScope.keyPress(key: Key) {
    println("👇 Key Down: $key")
    keyDown(key)
    println("👆 Key Up: $key")
    keyUp(key)
  }

  fun KeyInjectionScope.shiftTab() {
    keyDown(Key.ShiftLeft)
    keyPress(Key.Tab)
    keyUp(Key.ShiftLeft)
  }

  fun Modifier.testFocusTag(key: String): Modifier {
    return this.testTag(key).reportFocus(key)
  }

  private fun Modifier.reportFocus(key: String): Modifier {
    return this.onFocusChanged {
      if (it.hasFocus) {
        println("[$key] has focus")
      }
    }
  }
}
