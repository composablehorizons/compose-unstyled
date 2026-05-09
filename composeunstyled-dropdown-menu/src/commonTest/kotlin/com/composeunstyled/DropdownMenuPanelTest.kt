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

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DropdownMenuPanelTest {
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
            MenuItem(
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

    assertTrue(clicked)
    assertFalse(expanded)
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
            MenuItem(
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

    assertTrue(clicked)
    assertTrue(expanded)
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
            MenuItem(
              onClick = {},
              modifier = Modifier.testTag("first"),
            ) {
              BasicText("First")
            }
            MenuItem(onClick = {}, modifier = Modifier.testTag("last")) {
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

    assertTrue(expanded)
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
            MenuItem(onClick = {}, modifier = Modifier.testTag("first")) {
              BasicText("First")
            }
            MenuItem(
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

    assertTrue(expanded)
    onNodeWithTag("last").assertExists()
  }
}

private object FakeDropdownMenuScope : DropdownMenuScope
