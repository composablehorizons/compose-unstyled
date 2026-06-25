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

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.KeyInjectionScope
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class DropdownMenuPanelTest {
  @Test
  fun expandedMenuPlacesPanelAtAnchorPositionWhenFirstPlaced() = runComposeUiTest {
    val anchorWidth = 80.dp
    val panelWidth = 60.dp
    val panelPositions = mutableListOf<Int>()
    var expectedPanelX = 0

    setContent {
      val density = LocalDensity.current
      SideEffect {
        expectedPanelX = with(density) {
          anchorWidth.roundToPx() - panelWidth.roundToPx()
        }
      }
      UnstyledDropdownMenu(
        expanded = true,
        onExpandedChange = {},
        alignment = AnchorAlignment.End,
        panel = {
          DropdownMenuPanel(
            enter = EnterTransition.None,
            exit = ExitTransition.None,
          ) {
            BasicText(
              text = "Menu content",
              modifier = Modifier
                .size(width = panelWidth, height = 40.dp)
                .onPlaced { coordinates ->
                  panelPositions += coordinates.positionInWindow().x.toInt()
                },
            )
          }
        },
        anchor = {
          BasicText(
            text = "Anchor",
            modifier = Modifier.size(width = anchorWidth, height = 40.dp),
          )
        },
      )
    }
    waitUntil { panelPositions.isNotEmpty() }

    assertThat(panelPositions.first()).isEqualTo(expectedPanelX)
  }

  @Test
  fun panelWithoutMenuStateIsHidden() = runComposeUiTest {
    setContent {
      with(FakeDropdownMenuScope) {
        DropdownMenuPanel {
          BasicText("Menu content")
        }
      }
    }

    onNodeWithText("Menu content").assertDoesNotExist()
  }

  @Test
  fun menuItemInvokesClickAndClosesMenu() = runComposeUiTest {
    var clicked = false
    var expanded = true

    setContent {
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = { expanded = it },
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(
              onClick = { clicked = true },
              modifier = Modifier.testTag("item"),
            ) {
              BasicText("Item")
            }
          }
        }
      }
    }

    onNodeWithTag("item").performClick()

    assertThat(clicked).isTrue()
    assertThat(expanded).isFalse()
  }

  @Test
  fun menuItemCanKeepMenuOpenOnClick() = runComposeUiTest {
    var clicked = false
    var expanded = true

    setContent {
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = { expanded = it },
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(
              onClick = { clicked = true },
              closeOnClick = false,
              modifier = Modifier.testTag("item"),
            ) {
              BasicText("Item")
            }
          }
        }
      }
    }

    onNodeWithTag("item").performClick()

    assertThat(clicked).isTrue()
    assertThat(expanded).isTrue()
  }

  @Test
  fun menuItemInExpandedMenuInvokesClickAndClosesMenu() = runComposeUiTest {
    var clicked = false
    var expanded by mutableStateOf(true)

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(
              onClick = { clicked = true },
              modifier = Modifier.testTag("item"),
            ) {
              BasicText("Item")
            }
          }
        },
        anchor = {
          BasicText("Anchor")
        },
      )
    }

    onNodeWithTag("item").performClick()

    assertThat(clicked).isTrue()
    assertThat(expanded).isFalse()
  }

  @Test
  fun enterKeyOpensMenu() = runComposeUiTest {
    var expanded by mutableStateOf(false)

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(
              onClick = {},
              modifier = Modifier.testTag("first"),
            ) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        },
        anchor = {
          BasicText("Anchor", Modifier.testTag("anchor").focusable())
        },
      )
    }

    onNodeWithTag("anchor").requestFocus()
    onNodeWithTag("anchor").performKeyInput {
      pressKey(Key.Enter)
    }

    assertThat(expanded).isTrue()
    onNodeWithTag("first").assertExists()
  }

  @Test
  fun upArrowOpensMenu() = runComposeUiTest {
    var expanded by mutableStateOf(false)

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(
              onClick = {},
              modifier = Modifier.testTag("last"),
            ) {
              BasicText("Last")
            }
          }
        },
        anchor = {
          BasicText("Anchor", Modifier.testTag("anchor").focusable())
        },
      )
    }

    onNodeWithTag("anchor").requestFocus()
    onNodeWithTag("anchor").performKeyInput {
      pressKey(Key.DirectionUp)
    }

    assertThat(expanded).isTrue()
    onNodeWithTag("last").assertExists()
  }

  @Test
  fun openingMenuRequestsFocusBeforeEnterTransitionCompletes() = runComposeUiTest {
    mainClock.autoAdvance = false

    var expanded by mutableStateOf(false)

    setContent {
      UseKeyboardInputMode()
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel(
            enter = fadeIn(animationSpec = tween(durationMillis = 1_000)),
          ) {
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
          }
        },
        anchor = {
          BasicText("Anchor", Modifier.testTag("anchor").focusable())
        },
      )
    }

    onNodeWithTag("anchor").requestFocus()
    onNodeWithTag("anchor").performKeyInput {
      pressKey(Key.Enter)
    }
    mainClock.advanceTimeBy(100)

    assertThat(expanded).isTrue()
    onNodeWithTag("first").assertIsFocused()

    mainClock.autoAdvance = true
  }

  @Test
  fun clickingAnchorOpensMenuWithoutFocusingFirstItem() = runComposeUiTest {
    var expanded by mutableStateOf(false)
    var firstItemClicked = false

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel(modifier = Modifier.testTag("panel")) {
            UnstyledDropdownMenuItem(
              onClick = {
                firstItemClicked = true
              },
              modifier = Modifier.testTag("first"),
            ) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        },
        anchor = {
          BasicText(
            text = "Anchor",
            modifier = Modifier
              .testTag("anchor")
              .focusable()
              .clickable { expanded = true },
          )
        },
      )
    }

    onNodeWithTag("anchor").performClick()
    waitForIdle()

    assertThat(expanded).isTrue()
    onNodeWithTag("panel").assertIsFocused()
    onNodeWithTag("first").assertIsNotFocused()

    onNodeWithTag("panel").performKeyInput {
      pressKey(Key.Spacebar)
    }
    waitForIdle()

    assertThat(firstItemClicked).isFalse()
    onNodeWithTag("first").assertIsNotFocused()
  }

  @Test
  fun downArrowAfterClickingAnchorFocusesFirstItem() = runComposeUiTest {
    var expanded by mutableStateOf(false)

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel(modifier = Modifier.testTag("panel")) {
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        },
        anchor = {
          BasicText(
            text = "Anchor",
            modifier = Modifier
              .testTag("anchor")
              .focusable()
              .clickable { expanded = true },
          )
        },
      )
    }

    onNodeWithTag("anchor").performClick()
    waitForIdle()
    onNodeWithTag("panel").assertIsFocused()
    onNodeWithTag("first").assertIsNotFocused()
    onNodeWithTag("panel").performKeyInput {
      pressKey(Key.DirectionDown)
    }
    waitForIdle()

    onNodeWithTag("first").assertIsFocused()
  }

  @Test
  fun upArrowAfterClickingAnchorFocusesLastItem() = runComposeUiTest {
    var expanded by mutableStateOf(false)

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel(modifier = Modifier.testTag("panel")) {
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        },
        anchor = {
          BasicText(
            text = "Anchor",
            modifier = Modifier
              .testTag("anchor")
              .focusable()
              .clickable { expanded = true },
          )
        },
      )
    }

    onNodeWithTag("anchor").performClick()
    waitForIdle()
    onNodeWithTag("panel").assertIsFocused()
    onNodeWithTag("first").assertIsNotFocused()
    onNodeWithTag("panel").performKeyInput {
      pressKey(Key.DirectionUp)
    }
    waitForIdle()

    onNodeWithTag("last").assertIsFocused()
  }

  @Test
  fun spaceKeyActivatesFocusedMenuItemAfterClickingAnchorAndArrowNavigation() = runComposeUiTest {
    var expanded by mutableStateOf(false)
    var firstItemClicked = false

    setContent {
      UnstyledDropdownMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        panel = {
          DropdownMenuPanel(modifier = Modifier.testTag("panel")) {
            UnstyledDropdownMenuItem(
              onClick = {
                firstItemClicked = true
              },
              modifier = Modifier.testTag("first"),
            ) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        },
        anchor = {
          BasicText(
            text = "Anchor",
            modifier = Modifier
              .testTag("anchor")
              .focusable()
              .clickable { expanded = true },
          )
        },
      )
    }

    onNodeWithTag("anchor").performClick()
    waitForIdle()
    onNodeWithTag("panel").performKeyInput {
      pressKey(Key.DirectionDown)
    }
    waitForIdle()
    onNodeWithTag("first").assertIsFocused()

    onNodeWithTag("first").performKeyInput {
      pressKey(Key.Spacebar)
    }
    waitForIdle()

    assertThat(firstItemClicked).isTrue()
  }

  @Test
  fun tabKeyClosesMenuAndMovesFocusToNextFocusableAfterAnchor() = runComposeUiTest {
    var expanded by mutableStateOf(true)

    setContent {
      UseKeyboardInputMode()
      Column {
        BasicText("Before", Modifier.testTag("before").focusable())
        UnstyledDropdownMenu(
          expanded = expanded,
          onExpandedChange = { expanded = it },
          panel = {
            DropdownMenuPanel {
              UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
                BasicText("First")
              }
              UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
                BasicText("Last")
              }
            }
          },
          anchor = {
            BasicText("Anchor", Modifier.testTag("anchor").focusable())
          },
        )
        BasicText("After", Modifier.testTag("after").focusable())
      }
    }

    waitForIdle()
    onNodeWithTag("first").requestFocus()
    waitForIdle()
    onNodeWithTag("first").assertIsFocused()

    onNodeWithTag("first").performKeyInput {
      pressKey(Key.Tab)
    }
    waitForIdle()

    assertThat(expanded).isFalse()
    onNodeWithTag("after").assertIsFocused()
  }

  @Test
  fun shiftTabKeyClosesMenuAndMovesFocusToPreviousFocusableBeforeAnchor() = runComposeUiTest {
    var expanded by mutableStateOf(true)

    setContent {
      UseKeyboardInputMode()
      Column {
        BasicText("Before", Modifier.testTag("before").focusable())
        UnstyledDropdownMenu(
          expanded = expanded,
          onExpandedChange = { expanded = it },
          panel = {
            DropdownMenuPanel {
              UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
                BasicText("First")
              }
              UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
                BasicText("Last")
              }
            }
          },
          anchor = {
            BasicText("Anchor", Modifier.testTag("anchor").focusable())
          },
        )
        BasicText("After", Modifier.testTag("after").focusable())
      }
    }

    waitForIdle()
    onNodeWithTag("first").requestFocus()
    waitForIdle()
    onNodeWithTag("first").assertIsFocused()

    onNodeWithTag("first").performKeyInput {
      shiftTab()
    }
    waitForIdle()

    assertThat(expanded).isFalse()
    onNodeWithTag("before").assertIsFocused()
  }

  @Test
  fun panelPlacesDirectChildrenVertically() = runComposeUiTest {
    val itemHeight = 24.dp
    var firstItemY = 0
    var secondItemY = 0
    var expectedSecondItemY = 0

    setContent {
      val density = LocalDensity.current
      SideEffect {
        expectedSecondItemY = with(density) {
          itemHeight.roundToPx()
        }
      }
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = {},
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(
              onClick = {},
              modifier = Modifier
                .testTag("first")
                .size(itemHeight)
                .onPlaced { firstItemY = it.positionInWindow().y.toInt() },
            ) {
              BasicText("First")
            }
            BasicText(
              text = "Separator",
              modifier = Modifier.size(itemHeight),
            )
            UnstyledDropdownMenuItem(
              onClick = {},
              modifier = Modifier
                .testTag("second")
                .size(itemHeight)
                .onPlaced { secondItemY = it.positionInWindow().y.toInt() },
            ) {
              BasicText("Second")
            }
          }
        }
      }
    }

    waitForIdle()

    assertThat(secondItemY - firstItemY).isEqualTo(expectedSecondItemY * 2)
  }

  @Test
  fun homeKeyMovesFocusToFirstMenuItem() = runComposeUiTest {
    setContent {
      UseKeyboardInputMode()
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = {},
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("middle")) {
              BasicText("Middle")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        }
      }
    }

    waitForIdle()
    onNodeWithTag("middle").requestFocus()
    waitForIdle()
    onNodeWithTag("middle").assertIsFocused()

    onNodeWithTag("middle").performKeyInput {
      pressKey(Key.MoveHome)
    }

    onNodeWithTag("first").assertIsFocused()
  }

  @Test
  fun endKeyMovesFocusToLastMenuItem() = runComposeUiTest {
    setContent {
      UseKeyboardInputMode()
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = {},
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("middle")) {
              BasicText("Middle")
            }
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
          }
        }
      }
    }

    waitForIdle()
    onNodeWithTag("middle").requestFocus()
    waitForIdle()
    onNodeWithTag("middle").assertIsFocused()

    onNodeWithTag("middle").performKeyInput {
      pressKey(Key.MoveEnd)
    }

    onNodeWithTag("last").assertIsFocused()
  }

  @Test
  fun homeAndEndKeysSkipDirectChildrenThatAreNotMenuItems() = runComposeUiTest {
    setContent {
      UseKeyboardInputMode()
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = {},
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            BasicText("Before")
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            BasicText("Separator")
            UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
              BasicText("Last")
            }
            BasicText("After")
          }
        }
      }
    }

    waitForIdle()
    onNodeWithTag("first").requestFocus()
    waitForIdle()

    onNodeWithTag("first").performKeyInput {
      pressKey(Key.MoveEnd)
    }

    onNodeWithTag("last").assertIsFocused()

    onNodeWithTag("last").performKeyInput {
      pressKey(Key.MoveHome)
    }

    onNodeWithTag("first").assertIsFocused()
  }

  @Test
  fun homeAndEndKeysUseCurrentMenuItemOrderAfterReordering() = runComposeUiTest {
    var reversed by mutableStateOf(false)

    setContent {
      UseKeyboardInputMode()
      with(FakeDropdownMenuScope) {
        CompositionLocalProvider(
          LocalDropdownMenuState provides DropdownMenuState(
            onExpandedChange = {},
            transitionState = MutableTransitionState(true),
          ),
        ) {
          DropdownMenuPanel {
            if (reversed) {
              ReorderableMenuItem(tag = "last", text = "Last")
              ReorderableMenuItem(tag = "middle", text = "Middle")
              ReorderableMenuItem(tag = "first", text = "First")
            } else {
              ReorderableMenuItem(tag = "first", text = "First")
              ReorderableMenuItem(tag = "middle", text = "Middle")
              ReorderableMenuItem(tag = "last", text = "Last")
            }
          }
        }
      }
    }

    waitForIdle()
    onNodeWithTag("middle").requestFocus()
    waitForIdle()

    onNodeWithTag("middle").performKeyInput {
      pressKey(Key.MoveHome)
    }

    onNodeWithTag("first").assertIsFocused()

    runOnIdle {
      reversed = true
    }
    waitForIdle()
    onNodeWithTag("middle").requestFocus()
    waitForIdle()

    onNodeWithTag("middle").performKeyInput {
      pressKey(Key.MoveHome)
    }

    onNodeWithTag("last").assertIsFocused()

    onNodeWithTag("middle").requestFocus()
    waitForIdle()

    onNodeWithTag("middle").performKeyInput {
      pressKey(Key.MoveEnd)
    }

    onNodeWithTag("first").assertIsFocused()
  }
}

@Composable
private fun UseKeyboardInputMode() {
  val inputModeManager = LocalInputModeManager.current

  LaunchedEffect(inputModeManager) {
    inputModeManager.requestInputMode(InputMode.Keyboard)
  }
}

@Composable
private fun ReorderableMenuItem(tag: String, text: String) {
  UnstyledDropdownMenuItem(onClick = {}, modifier = Modifier.testTag(tag)) {
    BasicText(text)
  }
}

private fun KeyInjectionScope.shiftTab() {
  keyDown(Key.ShiftLeft)
  pressKey(Key.Tab)
  keyUp(Key.ShiftLeft)
}

private object FakeDropdownMenuScope : DropdownMenuScope
