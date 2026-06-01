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
package com.composeunstyled.platformtheme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import kotlin.test.Test

class PlatformThemeTest {

  @Test
  fun canCustomizePlatformTheme() = runComposeUiTest {
    val customProperty = ThemeProperty<String>("customProperty")
    val customToken = ThemeToken<String>("customToken")
    val customValue = "Custom Value"

    val theme = buildPlatformTheme {
      properties[customProperty] = mapOf(
        customToken to customValue,
      )
    }

    setContent {
      theme {
        BasicText(
          text = Theme[customProperty][customToken],
          modifier = Modifier.testTag("custom-text"),
        )
      }
    }

    onNodeWithTag("custom-text")
      .assertTextEquals(customValue)
  }

  @Test
  fun customInteractiveSizeIsAppliedToModifier() = runComposeUiTest {
    val customSize = 64.dp

    val theme = buildPlatformTheme {
      properties[interactiveSizes] = mapOf(
        sizeDefault to customSize,
      )
    }

    setContent {
      theme {
        Box(
          modifier = Modifier
            .interactiveSize(Theme[interactiveSizes][sizeDefault])
            .testTag("interactive-box"),
        )
      }
    }

    onNodeWithTag("interactive-box")
      .assertWidthIsAtLeast(customSize)
      .assertHeightIsAtLeast(customSize)
  }

  @Test
  fun platformIndicationUpdatesWhenColorChanges() = runComposeUiTest {
    var color by mutableStateOf(Color.Black)
    val indications = mutableListOf<Indication>()

    setContent {
      val indication = platformIndication(color)
      val interactionSource = remember { MutableInteractionSource() }
      SideEffect {
        indications += indication
      }
      Box(
        modifier = Modifier
          .indication(interactionSource, indication)
          .testTag("indication-host"),
      )
    }

    onNodeWithTag("indication-host").assertExists()

    color = Color.White
    waitForIdle()

    assertThat(indications.size).isEqualTo(2)
    assertThat(indications[0] === indications[1]).isFalse()
  }
}
