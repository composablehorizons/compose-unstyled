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
import org.assertj.core.api.Assertions.assertThat
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

        UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
          UnstyledTabList(Modifier.testFocusTag("tablist")) {
            UnstyledTab(
              key = "tab1",
              modifier = Modifier.testFocusTag("tab1"),
              selected = selectedTab == "tab1",
              onSelected = { selectedTab = "tab1" },
            ) {
              BasicText("Tab #1")
            }
            UnstyledTab(
              key = "tab2",
              modifier = Modifier.testFocusTag("tab2"),
              selected = selectedTab == "tab2",
              onSelected = { selectedTab = "tab2" },
            ) {
              BasicText("Tab #2")
            }
            UnstyledTab(
              key = "tab3",
              modifier = Modifier.testFocusTag("tab3"),
              selected = selectedTab == "tab3",
              onSelected = { selectedTab = "tab3" },
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

        UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
          UnstyledTabList(Modifier.testFocusTag("tablist")) {
            UnstyledTab(
              key = "tab1",
              modifier = Modifier.testFocusTag("tab1"),
              selected = selectedTab == "tab1",
              onSelected = { selectedTab = "tab1" },
            ) {
              BasicText("Tab #1")
            }
            UnstyledTab(
              key = "tab2",
              modifier = Modifier.testFocusTag("tab2"),
              selected = selectedTab == "tab2",
              onSelected = { selectedTab = "tab2" },
            ) {
              BasicText("Tab #2")
            }
            UnstyledTab(
              key = "tab3",
              modifier = Modifier.testFocusTag("tab3"),
              selected = selectedTab == "tab3",
              onSelected = { selectedTab = "tab3" },
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
        UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
          UnstyledTabList(Modifier.testFocusTag("tablist")) {
            UnstyledTab(
              "tab1",
              modifier = Modifier.testFocusTag("tab1"),
              selected = selectedTab == "tab1",
              onSelected = { selectedTab = "tab1" },
            ) {
              BasicText("Tab #1")
            }
            UnstyledTab(
              "tab2",
              modifier = Modifier.testFocusTag("tab2"),
              selected = selectedTab == "tab2",
              onSelected = { selectedTab = "tab2" },
            ) {
              BasicText("Tab #2")
            }
            UnstyledTab(
              "tab3",
              modifier = Modifier.testFocusTag("tab3"),
              selected = selectedTab == "tab3",
              onSelected = { selectedTab = "tab3" },
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
      UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("button").testFocusTag("button")) {
        BasicText("Outside")
      }

      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist").testFocusTag("tablist")) {
          UnstyledTab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1").testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2").testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3").testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
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
      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
          ) {
            BasicText("Tab #3")
          }
        }
        UnstyledTabPanel(key = "tab1") {
          UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("panelA")) {
            BasicText("Panel A")
          }
        }
        UnstyledTabPanel(key = "tab2") {
          UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("panelB")) {
            BasicText("Panel B")
          }
        }
        UnstyledTabPanel(key = "tab3") {
          UnstyledButton(onClick = {}, modifier = Modifier.testFocusTag("panelC")) {
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
  fun canFocusSecondTabDirectly() = runComposeUiTest {
    var selectedTab by mutableStateOf("tab2")

    setContent {
      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
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
      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
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
      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
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
      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
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
        tabs = listOf("tab1", "tab2", "tab3"),
      ) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
          ) {
            BasicText("Tab #3")
          }
        }
        UnstyledTabPanel(key = "tab1", Modifier.testFocusTag("panelA")) {
          BasicText("Panel A")
        }
        UnstyledTabPanel(key = "tab2", Modifier.testFocusTag("panelB")) {
          BasicText("Panel B")
        }
        UnstyledTabPanel(key = "tab3", Modifier.testFocusTag("panelC")) {
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
      UnstyledTabGroup(selectedTab = selectedTab, tabs = listOf("tab1", "tab2", "tab3")) {
        UnstyledTabList(Modifier.testFocusTag("tablist")) {
          UnstyledTab(
            key = "tab1",
            modifier = Modifier.testFocusTag("tab1"),
            selected = selectedTab == "tab1",
            onSelected = { selectedTab = "tab1" },
            activateOnFocus = true,
          ) {
            BasicText("Tab #1")
          }
          UnstyledTab(
            key = "tab2",
            modifier = Modifier.testFocusTag("tab2"),
            selected = selectedTab == "tab2",
            onSelected = { selectedTab = "tab2" },
            activateOnFocus = true,
          ) {
            BasicText("Tab #2")
          }
          UnstyledTab(
            key = "tab3",
            modifier = Modifier.testFocusTag("tab3"),
            selected = selectedTab == "tab3",
            onSelected = { selectedTab = "tab3" },
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
