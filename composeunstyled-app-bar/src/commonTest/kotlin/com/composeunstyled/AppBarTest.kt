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
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import kotlin.test.Test

class AppBarTest {
  @Test
  fun itemSpacingSeparatesNavigationTitleAndActions() = runComposeUiTest {
    setContent {
      UnstyledAppBar(
        title = { Box(Modifier.size(40.dp).testTag("title")) },
        modifier = Modifier.size(240.dp, 56.dp),
        itemSpacing = 12.dp,
        navigation = { Box(Modifier.size(40.dp).testTag("navigation")) },
        actions = {
          Box(Modifier.size(24.dp).testTag("firstAction"))
          Box(Modifier.size(24.dp).testTag("secondAction"))
        },
      )
    }

    val navigation = onNodeWithTag("navigation").getBoundsInRoot()
    val title = onNodeWithTag("title").getBoundsInRoot()
    val firstAction = onNodeWithTag("firstAction").getBoundsInRoot()
    val secondAction = onNodeWithTag("secondAction").getBoundsInRoot()
    assertThat(title.left - navigation.right).isEqualTo(12.dp)
    assertThat(secondAction.left - firstAction.right).isEqualTo(12.dp)
  }

  @Test
  fun placesSlotsAndPreservesChildSemantics() = runComposeUiTest {
    setContent {
      UnstyledAppBar(
        title = { Box(Modifier.size(40.dp).testTag("title")) },
        modifier = Modifier.size(240.dp, 56.dp).testTag("bar"),
        navigation = { Box(Modifier.size(48.dp).testTag("navigation")) },
        actions = { Box(Modifier.size(72.dp).testTag("action")) },
      )
    }

    onNodeWithTag("navigation").assertExists()
    onNodeWithTag("action").assertExists()
    val navigation = onNodeWithTag("navigation").getBoundsInRoot()
    val title = onNodeWithTag("title").getBoundsInRoot()
    val action = onNodeWithTag("action").getBoundsInRoot()
    assertThat(title.left).isGreaterThanOrEqualTo(navigation.right)
    assertThat(action.left).isGreaterThanOrEqualTo(title.right)
  }
}
