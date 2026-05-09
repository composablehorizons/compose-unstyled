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
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class RadioGroupTest {

  @Test
  fun clickSelectsRadioButtonValue() = runComposeUiTest {
    var selectedValue: String? = null
    setContent {
      UnstyledRadioGroup(
        value = selectedValue,
        onValueChange = { selectedValue = it },
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = "option",
          modifier = Modifier.testTag("radio"),
        ) {
          Box(Modifier.size(20.dp))
        }
      }
    }

    onNodeWithTag("radio").performClick()

    assertEquals("option", selectedValue)
  }

  @Test
  fun supportsNonStringRadioButtonValues() = runComposeUiTest {
    var selectedValue: Int? = null
    setContent {
      UnstyledRadioGroup(
        value = selectedValue,
        onValueChange = { selectedValue = it },
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = 1,
          modifier = Modifier.testTag("radio"),
        ) {
          Box(Modifier.size(20.dp))
        }
      }
    }

    onNodeWithTag("radio").performClick()

    assertEquals(1, selectedValue)
  }

  @Test
  fun clickDoesNotSelectRadioButtonWhenDisabled() = runComposeUiTest {
    var selectedValue: String? = null
    setContent {
      UnstyledRadioGroup(
        value = selectedValue,
        onValueChange = { selectedValue = it },
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = "option",
          modifier = Modifier.testTag("radio"),
          enabled = false,
        ) {
          Box(Modifier.size(20.dp))
        }
      }
    }

    onNodeWithTag("radio").performClick()

    assertEquals(null, selectedValue)
  }

  @Test
  fun exposesUnselectedSemantics() = runComposeUiTest {
    setContent {
      UnstyledRadioGroup(
        value = null,
        onValueChange = {},
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = "option",
          modifier = Modifier.testTag("radio"),
        ) {
          Box(Modifier.size(20.dp))
        }
      }
    }

    onNodeWithTag("radio").assertIsOff()
  }

  @Test
  fun exposesSelectedSemantics() = runComposeUiTest {
    setContent {
      UnstyledRadioGroup(
        value = "option",
        onValueChange = {},
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = "option",
          modifier = Modifier.testTag("radio"),
        ) {
          Box(Modifier.size(20.dp))
        }
      }
    }

    onNodeWithTag("radio").assertIsOn()
  }

  @Test
  fun selectedIndicatorContentIsHiddenWhenUnselected() = runComposeUiTest {
    setContent {
      UnstyledRadioGroup(
        value = null,
        onValueChange = {},
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = "option",
          modifier = Modifier.testTag("radio"),
        ) {
          SelectedIndicator {
            Box(Modifier.size(8.dp).testTag("selected_indicator"))
          }
        }
      }
    }

    onAllNodesWithTag("selected_indicator", useUnmergedTree = true).assertCountEquals(0)
  }

  @Test
  fun selectedIndicatorContentIsShownWhenSelected() = runComposeUiTest {
    setContent {
      UnstyledRadioGroup(
        value = "option",
        onValueChange = {},
        contentDescription = null,
      ) {
        UnstyledRadioButton(
          value = "option",
          modifier = Modifier.testTag("radio"),
        ) {
          SelectedIndicator {
            Box(Modifier.size(8.dp).testTag("selected_indicator"))
          }
        }
      }
    }

    onNodeWithTag("selected_indicator", useUnmergedTree = true).assertIsDisplayed()
  }
}
