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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckboxTest {
  @Test
  fun callsOnCheckedChangeWithNextState() = runComposeUiTest {
    var nextValue: Boolean? = null

    setContent {
      UnstyledCheckbox(
        checked = false,
        onCheckedChange = { nextValue = it },
        modifier = Modifier.testTag("checkbox"),
      ) {
        CheckedIndicator(Modifier.size(24.dp)) {
          BasicText("Accept")
        }
      }
    }

    onNodeWithTag("checkbox").performClick()

    assertEquals(true, nextValue)
  }

  @Test
  fun doesNotCallOnCheckedChangeWhenDisabled() = runComposeUiTest {
    var calls = 0

    setContent {
      UnstyledCheckbox(
        checked = false,
        onCheckedChange = { calls++ },
        modifier = Modifier.testTag("checkbox"),
        enabled = false,
      ) {
        CheckedIndicator(Modifier.size(24.dp)) {
          BasicText("Accept")
        }
      }
    }

    onNodeWithTag("checkbox").performClick()

    assertEquals(0, calls)
  }

  @Test
  fun exposesCheckedStateInSemantics() = runComposeUiTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledCheckbox(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.testTag("checkbox"),
      ) {
        CheckedIndicator(Modifier.size(24.dp)) {
          BasicText("Accept")
        }
      }
    }

    onNodeWithTag("checkbox").assertIsOff()

    onNodeWithTag("checkbox").performClick()

    onNodeWithTag("checkbox").assertIsOn()
  }

  @Test
  fun exposesAccessibilityLabel() = runComposeUiTest {
    setContent {
      UnstyledCheckbox(
        checked = false,
        onCheckedChange = {},
        accessibilityLabel = "Accept terms",
      ) {
        CheckedIndicator(Modifier.size(24.dp)) {
          BasicText("Accept")
        }
      }
    }

    onNodeWithContentDescription("Accept terms").assertIsDisplayed()
  }

  @Test
  fun rendersSlotContent() = runComposeUiTest {
    setContent {
      UnstyledCheckbox(
        checked = true,
        onCheckedChange = {},
        modifier = Modifier.testTag("checkbox"),
      ) {
        CheckedIndicator(Modifier.size(24.dp)) {
          BasicText("Selected", modifier = Modifier.testTag("slot_content"))
        }
      }
    }

    onNodeWithTag("slot_content", useUnmergedTree = true).assertExists()
    onNodeWithText("Selected", useUnmergedTree = true).assertExists()
  }

  @Test
  fun controlBoxContentIsHiddenWhenUnchecked() = runComposeUiTest {
    setContent {
      UnstyledCheckbox(
        checked = false,
        onCheckedChange = {},
        modifier = Modifier.testTag("checkbox"),
      ) {
        CheckedIndicator(Modifier.size(24.dp)) {
          Box(Modifier.size(8.dp).testTag("control_content"))
        }
      }
    }

    onAllNodesWithTag("control_content", useUnmergedTree = true).assertCountEquals(0)
  }
}
