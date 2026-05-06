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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class TriStateCheckboxTest {

  @Test
  fun clickInvokesOnClickWhenEnabled() = runComposeUiTest {
    var clicks = 0
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.Off,
        onClick = { clicks++ },
        modifier = Modifier.testTag("checkbox"),
      ) {
        StateIndicator(Modifier.size(24.dp)) {
          Box(Modifier.size(8.dp).testTag("control_content"))
        }
      }
    }

    onNodeWithTag("checkbox").performClick()

    assertEquals(1, clicks)
  }

  @Test
  fun clickDoesNotInvokeOnClickWhenDisabled() = runComposeUiTest {
    var clicks = 0
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.Off,
        onClick = { clicks++ },
        modifier = Modifier.testTag("checkbox"),
        enabled = false,
      ) {
        StateIndicator(Modifier.size(24.dp)) {
          Box(Modifier.size(8.dp).testTag("control_content"))
        }
      }
    }

    onNodeWithTag("checkbox").performClick()

    assertEquals(0, clicks)
  }

  @Test
  fun exposesOffSemantics() = runComposeUiTest {
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.Off,
        onClick = {},
        modifier = Modifier.testTag("checkbox"),
      ) {
        StateIndicator(Modifier.size(24.dp)) {
          Box(Modifier.size(8.dp).testTag("control_content"))
        }
      }
    }

    onNodeWithTag("checkbox").assertIsOff()
  }

  @Test
  fun exposesOnSemantics() = runComposeUiTest {
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.On,
        onClick = {},
        modifier = Modifier.testTag("checkbox"),
      ) {
        StateIndicator(Modifier.size(24.dp)) {
          Box(Modifier.size(8.dp).testTag("control_content"))
        }
      }
    }

    onNodeWithTag("checkbox").assertIsOn()
  }

  @Test
  fun exposesAccessibilityLabel() = runComposeUiTest {
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.Off,
        onClick = {},
        accessibilityLabel = "Select all",
      ) {
        StateIndicator(Modifier.size(24.dp)) {
          Box(Modifier.size(8.dp))
        }
      }
    }

    onNodeWithContentDescription("Select all").assertExists()
  }

  @Test
  fun contentCanReadValue() = runComposeUiTest {
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.Indeterminate,
        onClick = {},
        modifier = Modifier.testTag("checkbox"),
      ) {
        StateIndicator(Modifier.size(24.dp)) { state ->
          Box(Modifier.size(8.dp).testTag(state.name))
        }
      }
    }

    onNodeWithTag("Indeterminate", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun stateIndicatorContentCanReadOffValue() = runComposeUiTest {
    setContent {
      UnstyledTriStateCheckbox(
        value = ToggleableState.Off,
        onClick = {},
        modifier = Modifier.testTag("checkbox"),
      ) {
        StateIndicator(Modifier.size(24.dp)) { state ->
          Box(Modifier.size(8.dp).testTag(state.name))
        }
      }
    }

    onNodeWithTag("Off", useUnmergedTree = true).assertIsDisplayed()
  }
}
