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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class PortalTest {

  @Test
  fun rendersContentInsidePortalHost() = runComposeUiTest {
    setContent {
      PortalHost {
        Portal {
          BasicText("Portal content")
        }
      }
    }

    onNodeWithText("Portal content").assertExists()
  }

  @Test
  fun portalContentKeepsCallerCompositionLocals() = runComposeUiTest {
    setContent {
      PortalHost {
        CompositionLocalProvider(LocalPortalTestValue provides "caller") {
          Portal {
            BasicText(LocalPortalTestValue.current)
          }
        }
      }
    }

    onNodeWithText("caller").assertExists()
  }

  @Test
  fun rendersNothingWithoutPortalHost() = runComposeUiTest {
    setContent {
      Portal {
        BasicText("Portal content")
      }
    }

    onNodeWithText("Portal content").assertDoesNotExist()
  }

  @Test
  fun newerPortalContentIsRenderedAboveOlderPortalContentAfterRepeatedMounts() = runComposeUiTest {
    var showTopPortal by mutableStateOf(false)
    var bottomClicks = 0
    var topClicks = 0

    setContent {
      PortalHost(Modifier.size(100.dp).testTag("host")) {
        Portal {
          Box(
            Modifier
              .fillMaxSize()
              .clickable { bottomClicks += 1 },
          )
        }

        if (showTopPortal) {
          Portal {
            Box(
              Modifier
                .fillMaxSize()
                .clickable { topClicks += 1 },
            )
          }
        }
      }
    }

    repeat(50) {
      showTopPortal = true
      waitForIdle()
      onNodeWithTag("host").performTouchInput { click(center) }
      showTopPortal = false
      waitForIdle()
    }

    assertThat(topClicks).isEqualTo(50)
    assertThat(bottomClicks).isEqualTo(0)
  }

  @Test
  fun portalHostDoesNotFillAvailableSizeByDefault() = runComposeUiTest {
    setContent {
      PortalHost(Modifier.testTag("host")) {
        Box(Modifier.size(24.dp))
      }
    }

    onNodeWithTag("host")
      .assertWidthIsEqualTo(24.dp)
      .assertHeightIsEqualTo(24.dp)
  }
}

private val LocalPortalTestValue = staticCompositionLocalOf { "host" }
