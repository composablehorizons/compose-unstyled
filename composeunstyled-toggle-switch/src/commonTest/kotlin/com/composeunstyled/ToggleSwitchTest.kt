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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class ToggleSwitchTest {
  @Test
  fun callsOnCheckedChangeWithNextState() = runComposeUiTest {
    var nextValue: Boolean? = null

    setContent {
      UnstyledSwitch(
        checked = false,
        onCheckedChange = { nextValue = it },
        modifier = Modifier.testTag("switch"),
      ) {
        Box(Modifier.size(24.dp))
      }
    }

    onNodeWithTag("switch").performClick()

    assertThat(nextValue).isEqualTo(true)
  }

  @Test
  fun doesNotCallOnCheckedChangeWhenDisabled() = runComposeUiTest {
    var calls = 0

    setContent {
      UnstyledSwitch(
        checked = false,
        onCheckedChange = { calls++ },
        enabled = false,
        modifier = Modifier.testTag("switch"),
      ) {
        Box(Modifier.size(24.dp))
      }
    }

    onNodeWithTag("switch").performClick()

    assertThat(calls).isEqualTo(0)
  }

  @Test
  fun exposesCheckedStateInSemantics() = runComposeUiTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledSwitch(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.testTag("switch"),
      ) {
        Box(Modifier.size(24.dp))
      }
    }

    onNodeWithTag("switch").assertIsOff()

    onNodeWithTag("switch").performClick()

    onNodeWithTag("switch").assertIsOn()
  }

  @Test
  fun exposesAccessibilityLabel() = runComposeUiTest {
    setContent {
      UnstyledSwitch(
        checked = false,
        onCheckedChange = {},
        accessibilityLabel = "Notifications",
      ) {
        Box(Modifier.size(24.dp))
      }
    }

    onNodeWithContentDescription("Notifications").assertIsDisplayed()
  }

  @Test
  fun contentCanReadSwitchState() = runComposeUiTest {
    setContent {
      UnstyledSwitch(
        checked = true,
        onCheckedChange = {},
        enabled = false,
      ) {
        BasicText("checked=$checked enabled=$enabled")
      }
    }

    onNodeWithText("checked=true enabled=false").assertIsDisplayed()
  }

  @Test
  fun thumbDoesNotForceSwitchSize() = runComposeUiTest {
    setContent {
      UnstyledSwitch(
        checked = false,
        onCheckedChange = {},
        modifier = Modifier.testTag("switch"),
      ) {
        SwitchThumb(
          modifier = Modifier
            .width(24.dp)
            .height(16.dp),
        )
      }
    }

    onNodeWithTag("switch")
      .assertWidthIsEqualTo(24.dp)
      .assertHeightIsEqualTo(16.dp)
  }

  @Test
  fun thumbMovesToEndWhenSwitchIsChecked() = runComposeUiTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledSwitch(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier
          .width(58.dp)
          .height(32.dp)
          .testTag("switch"),
      ) {
        SwitchThumb(
          modifier = Modifier.size(24.dp),
        ) {
          Box(Modifier.size(1.dp).testTag("thumb-content"))
        }
      }
    }

    waitForIdle()
    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(0.dp)

    onNodeWithTag("switch").performClick()
    waitForIdle()

    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(34.dp)
  }

  @Test
  fun checkedThumbStartsAtEndWhenUsingAnimationSpec() = runComposeUiTest {
    mainClock.autoAdvance = false
    val thumbPositions = mutableStateListOf<Int>()

    setContent {
      UnstyledSwitch(
        checked = true,
        onCheckedChange = {},
        modifier = Modifier
          .width(58.dp)
          .height(32.dp)
          .testTag("switch"),
      ) {
        SwitchThumb(
          animationSpec = tween(durationMillis = 1_000),
          modifier = Modifier
            .onPlaced { thumbPositions += it.positionInRoot().x.toInt() }
            .size(24.dp),
        ) {
          Box(Modifier.size(1.dp).testTag("thumb-content"))
        }
      }
    }

    mainClock.advanceTimeByFrame()

    assertThat(thumbPositions.distinct()).isEqualTo(listOf(34))
    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(34.dp)
  }

  @Test
  fun paddedThumbMovesToEndUsingItsOuterSize() = runComposeUiTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledSwitch(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier
          .width(58.dp)
          .height(32.dp)
          .testTag("switch"),
      ) {
        SwitchThumb(
          modifier = Modifier
            .padding(4.dp)
            .size(24.dp),
        ) {
          Box(Modifier.size(1.dp).testTag("thumb-content"))
        }
      }
    }

    waitForIdle()
    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(4.dp)

    onNodeWithTag("switch").performClick()
    waitForIdle()

    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(30.dp)
  }

  @Test
  fun trackConstrainsThumbMovementInsideLabeledSwitch() = runComposeUiTest {
    var checked by mutableStateOf(false)

    setContent {
      UnstyledSwitch(
        checked = checked,
        onCheckedChange = { checked = it },
        modifier = Modifier.testTag("switch"),
      ) {
        Row {
          Track(
            modifier = Modifier
              .width(58.dp)
              .height(32.dp),
          ) {
            Thumb(
              modifier = Modifier.size(24.dp),
            ) {
              Box(Modifier.size(1.dp).testTag("thumb-content"))
            }
          }

          BasicText("Notifications")
        }
      }
    }

    waitForIdle()
    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(0.dp)

    onNodeWithTag("switch").performClick()
    waitForIdle()

    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(34.dp)
  }

  @Test
  fun existingSwitchThumbCanBeUsedInsideTrack() = runComposeUiTest {
    setContent {
      UnstyledSwitch(
        checked = true,
        onCheckedChange = {},
      ) {
        Track(
          modifier = Modifier
            .width(58.dp)
            .height(32.dp),
        ) {
          SwitchThumb(
            modifier = Modifier.size(24.dp),
          ) {
            Box(Modifier.size(1.dp).testTag("thumb-content"))
          }
        }
      }
    }

    onNodeWithTag("thumb-content", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(34.dp)
  }
}
