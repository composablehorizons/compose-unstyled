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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

class DialogTest {

  @Test
  fun isModal() = runComposeUiTest {
    var visible by mutableStateOf(false)
    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
      ) {
        DialogPanel {
        }
      }
    }
    onNode(isDialog()).assertDoesNotExist()
    visible = true
    onNode(isDialog()).assertExists()
  }

  @Test
  fun visibleDialogShowsThePanel() = runComposeUiTest {
    var visible by mutableStateOf(false)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
      ) {
        DialogPanel(Modifier.testTag("dialog_content")) {
        }
      }
    }

    onNodeWithTag("dialog_content").assertDoesNotExist()

    visible = true

    onNodeWithTag("dialog_content").assertExists()
  }

  @Test
  fun visibleDialogShowsTheOverlay() = runComposeUiTest {
    var visible by mutableStateOf(false)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
        overlay = {
          Box(Modifier.testTag("dialog_overlay"))
        },
      ) {
        DialogPanel(Modifier.testTag("dialog_content")) {
        }
      }
    }

    onNodeWithTag("dialog_overlay").assertDoesNotExist()

    visible = true

    onNodeWithTag("dialog_overlay").assertExists()
  }

  @Test
  fun dialogPanelSetsPaneTitleSemanticsWhenPresent() = runComposeUiTest {
    setContent {
      UnstyledDialog(
        visible = true,
        onDismissRequest = {},
      ) {
        DialogPanel(Modifier.testTag("dialog_panel"), paneTitle = "Dialog") {
        }
      }
    }

    onNodeWithTag("dialog_panel")
      .assert(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Dialog"))
  }

  @Test
  fun dialogPanelDoesNotSetPaneTitleSemanticsWhenAbsent() = runComposeUiTest {
    setContent {
      UnstyledDialog(
        visible = true,
        onDismissRequest = {},
      ) {
        DialogPanel(Modifier.testTag("dialog_panel")) {
        }
      }
    }

    onNodeWithTag("dialog_panel")
      .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.PaneTitle))
  }

  fun visibleDialogShowsTheModalFragment() = runComposeUiTest {
    var visible by mutableStateOf(false)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
      ) {
        TestModalFragment(Modifier.testTag("modal_fragment"))
      }
    }

    onNodeWithTag("modal_fragment").assertDoesNotExist()

    visible = true

    onNodeWithTag("modal_fragment").assertExists()

    visible = false
    waitForIdle()
    onNodeWithTag("modal_fragment").assertDoesNotExist()
  }

  @Test
  fun initiallyVisibleDialogWithoutScrimCanBeDismissed() = runComposeUiTest {
    var visible by mutableStateOf(true)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
      ) {
        DialogPanel(Modifier.testTag("dialog_content")) {
        }
      }
    }

    onNodeWithTag("dialog_content").assertExists()

    visible = false
    waitForIdle()

    onNodeWithTag("dialog_content").assertDoesNotExist()
    onNode(isDialog()).assertDoesNotExist()
  }

  @Test
  fun autoFocusesOnDialog() = runComposeUiTest {
    setContent {
      UnstyledDialog(
        visible = true,
        onDismissRequest = {},
      ) {
        DialogPanel(Modifier.testTag("dialog_content")) {
          BasicTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.testTag("dialog_focus_target"),
          )
        }
      }
    }

    onNodeWithTag("dialog_focus_target").assertIsFocused()
  }

  @Test
  fun pressingEscapeDismissesDialogWhenDismissOnBackPressIsTrue() = runComposeUiTest {
    var visible by mutableStateOf(true)

    setContent {
      var value by remember { mutableStateOf("") }
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
        properties = DialogProperties(dismissOnBackPress = true),
      ) {
        DialogPanel(Modifier.testTag("dialog_content")) {
          BasicTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.testTag("dialog_input"),
          )
        }
      }
    }

    onNodeWithTag("dialog_content").assertExists()
    onNodeWithTag("dialog_input").performClick()
    onNodeWithTag("dialog_input").assertIsFocused()

    onNodeWithTag("dialog_input").performKeyInput {
      pressKey(Key.Escape)
    }
    waitForIdle()

    onNodeWithTag("dialog_content").assertDoesNotExist()
  }

  @Test
  fun pressingEscapeDoesNotDismissDialogWhenDismissOnBackPressIsFalse() = runComposeUiTest {
    var visible by mutableStateOf(true)

    setContent {
      var value by remember { mutableStateOf("") }
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
        properties = DialogProperties(dismissOnBackPress = false),
      ) {
        DialogPanel(Modifier.testTag("dialog_content")) {
          BasicTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.testTag("dialog_input"),
          )
        }
      }
    }

    onNodeWithTag("dialog_content").assertExists()
    onNodeWithTag("dialog_input").performClick()
    onNodeWithTag("dialog_input").assertIsFocused()

    onNodeWithTag("dialog_input").performKeyInput {
      pressKey(Key.Escape)
    }
    waitForIdle()

    onNodeWithTag("dialog_content").assertExists()
  }

  @Test
  fun clickingOutsideDismissesDialogWhenDismissOnClickOutsideIsTrue() = runComposeUiTest {
    var visible by mutableStateOf(true)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
        properties = DialogProperties(dismissOnClickOutside = true),
      ) {
        TestModalFragment(Modifier.testTag("dialog_backdrop"))
        DialogPanel(Modifier.testTag("dialog_content").size(100.dp)) {}
      }
    }

    waitForIdle()
    onNodeWithTag("dialog_content").assertExists()

    onNodeWithTag("dialog_backdrop").performTouchInput {
      click(Offset(1f, 1f))
    }
    waitForIdle()

    onNodeWithTag("dialog_content").assertDoesNotExist()
  }

  @Test
  fun clickingOutsideDoesNotDismissDialogWhenDismissOnClickOutsideIsFalse() = runComposeUiTest {
    var visible by mutableStateOf(true)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
        properties = DialogProperties(dismissOnClickOutside = false),
      ) {
        TestModalFragment(Modifier.testTag("dialog_backdrop"))
        DialogPanel(Modifier.testTag("dialog_content").size(100.dp)) {}
      }
    }

    waitForIdle()
    onNodeWithTag("dialog_content").assertExists()

    onNodeWithTag("dialog_backdrop").performTouchInput {
      click(Offset(1f, 1f))
    }
    waitForIdle()

    onNodeWithTag("dialog_content").assertExists()
  }
}

@Composable
private fun TestModalFragment(modifier: Modifier = Modifier) {
  val modalState = LocalModalState.current
  AnimatedVisibility(visibleState = modalState.transitionState) {
    Box(modifier.modalFragment().fillMaxSize())
  }
}
