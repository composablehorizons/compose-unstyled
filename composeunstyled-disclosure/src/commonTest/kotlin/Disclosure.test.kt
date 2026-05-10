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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class DisclosureTest {
  @Test
  fun callsOnExpandedChangeWithNextState() = runComposeUiTest {
    var nextValue: Boolean? = null

    setContent {
      UnstyledDisclosure(
        expanded = false,
        onExpandedChange = { nextValue = it },
      ) {
        DisclosureButton(Modifier.testTag("button")) {
          BasicText("Heading")
        }
      }
    }

    onNodeWithTag("button").performClick()

    assertEquals(true, nextValue)
  }

  @Test
  fun showsContentWhenExpanded() = runComposeUiTest {
    var expanded by mutableStateOf(false)

    setContent {
      UnstyledDisclosure(
        expanded = expanded,
        onExpandedChange = { expanded = it },
      ) {
        DisclosureButton(Modifier.testTag("button")) {
          BasicText("Heading")
        }
        DisclosedContent(Modifier.testTag("content")) {
          BasicText("Panel")
        }
      }
    }

    onNodeWithTag("content").assertDoesNotExist()

    onNodeWithTag("button").performClick()

    onNodeWithTag("content").assertExists()
  }

  @Test
  fun disclosureButtonExposesButtonRoleAndExpandActionWhenCollapsed() = runComposeUiTest {
    setContent {
      UnstyledDisclosure(
        expanded = false,
        onExpandedChange = {},
      ) {
        DisclosureButton(Modifier.testTag("button")) {
          BasicText("Heading")
        }
      }
    }

    onNodeWithTag("button")
      .assertHasClickAction()
      .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
      .assert(hasExpandAction())
      .assert(hasNoCollapseAction())
  }

  @Test
  fun disclosureButtonExposesButtonRoleAndCollapseActionWhenExpanded() = runComposeUiTest {
    setContent {
      UnstyledDisclosure(
        expanded = true,
        onExpandedChange = {},
      ) {
        DisclosureButton(Modifier.testTag("button")) {
          BasicText("Heading")
        }
      }
    }

    onNodeWithTag("button")
      .assertHasClickAction()
      .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
      .assert(hasCollapseAction())
      .assert(hasNoExpandAction())
  }

  @Test
  fun disclosureButtonForwardsButtonLayoutParameters() = runComposeUiTest {
    setContent {
      UnstyledDisclosure(
        expanded = false,
        onExpandedChange = {},
      ) {
        DisclosureButton(
          modifier = Modifier.size(100.dp).testTag("button"),
          contentPadding = PaddingValues(10.dp),
          contentAlignment = Alignment.BottomEnd,
        ) {
          Box(Modifier.size(20.dp).testTag("content"))
        }
      }
    }

    onNodeWithTag("content", useUnmergedTree = true)
      .assertLeftPositionInRootIsEqualTo(70.dp)
      .assertTopPositionInRootIsEqualTo(70.dp)
  }
}

private fun hasExpandAction(): SemanticsMatcher {
  return SemanticsMatcher("has expand action") { node ->
    SemanticsActions.Expand in node.config
  }
}

private fun hasCollapseAction(): SemanticsMatcher {
  return SemanticsMatcher("has collapse action") { node ->
    SemanticsActions.Collapse in node.config
  }
}

private fun hasNoExpandAction(): SemanticsMatcher {
  return SemanticsMatcher("has no expand action") { node ->
    (SemanticsActions.Expand in node.config).not()
  }
}

private fun hasNoCollapseAction(): SemanticsMatcher {
  return SemanticsMatcher("has no collapse action") { node ->
    (SemanticsActions.Collapse in node.config).not()
  }
}
