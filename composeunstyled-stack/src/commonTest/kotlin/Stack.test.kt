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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class StackTest {

  @Test
  fun horizontalOrientationPlacesItemsHorizontally() = runComposeUiTest {
    setContent {
      Stack(orientation = StackOrientation.Horizontal, modifier = Modifier.testTag("stack")) {
        Box(
          modifier = Modifier
            .size(50.dp)
            .testTag("item1"),
        )
        Box(
          modifier = Modifier
            .size(50.dp)
            .testTag("item2"),
        )
      }
    }

    val item1Bounds = onNodeWithTag("item1").getBoundsInRoot()
    val item2Bounds = onNodeWithTag("item2").getBoundsInRoot()

    // Items should be placed horizontally (item2's left edge should be at or after item1's right edge)
    assertThat(item2Bounds.left).isGreaterThanOrEqualTo(item1Bounds.right)
  }

  @Test
  fun verticalOrientationPlacesItemsVertically() = runComposeUiTest {
    setContent {
      Stack(orientation = StackOrientation.Vertical, modifier = Modifier.testTag("stack")) {
        Box(
          modifier = Modifier
            .size(50.dp)
            .testTag("item1"),
        )
        Box(
          modifier = Modifier
            .size(50.dp)
            .testTag("item2"),
        )
      }
    }

    val item1Bounds = onNodeWithTag("item1").getBoundsInRoot()
    val item2Bounds = onNodeWithTag("item2").getBoundsInRoot()

    // Items should be placed vertically (item2's top edge should be at or below item1's bottom edge)
    assertThat(item2Bounds.top).isGreaterThanOrEqualTo(item1Bounds.bottom)
  }

  @Test
  fun changingOrientationChangesPlacement() = runComposeUiTest {
    var orientation by mutableStateOf(StackOrientation.Horizontal)

    setContent {
      Stack(orientation = orientation, modifier = Modifier.testTag("stack")) {
        Box(
          modifier = Modifier
            .size(50.dp)
            .testTag("item1"),
        )
        Box(
          modifier = Modifier
            .size(50.dp)
            .testTag("item2"),
        )
      }
    }

    // Initial horizontal placement
    val horizontalItem1Bounds = onNodeWithTag("item1").getBoundsInRoot()
    val horizontalItem2Bounds = onNodeWithTag("item2").getBoundsInRoot()
    assertThat(horizontalItem2Bounds.left >= horizontalItem1Bounds.right).isTrue()

    // Change to vertical
    orientation = StackOrientation.Vertical

    // Verify vertical placement
    val verticalItem1Bounds = onNodeWithTag("item1").getBoundsInRoot()
    val verticalItem2Bounds = onNodeWithTag("item2").getBoundsInRoot()
    assertThat(verticalItem2Bounds.top >= verticalItem1Bounds.bottom).isTrue()
  }
}
