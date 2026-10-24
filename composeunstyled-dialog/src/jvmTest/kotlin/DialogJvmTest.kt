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
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThan
import assertk.assertions.isTrue
import kotlin.test.Test

class DialogJvmTest {

  @Test
  fun hostedNonDismissibleDialogBlocksBackgroundHover() =
    hostedDialogBlocksBackgroundHover(dismissOnClickOutside = false)

  @Test
  fun hostedDismissibleDialogBlocksBackgroundHover() =
    hostedDialogBlocksBackgroundHover(dismissOnClickOutside = true)

  @Test
  fun hostedNonDismissibleDialogBlocksBackgroundClicksAndScrolling() =
    hostedDialogBlocksBackgroundClicksAndScrolling(dismissOnClickOutside = false)

  @Test
  fun hostedDismissibleDialogBlocksBackgroundClicksAndScrolling() =
    hostedDialogBlocksBackgroundClicksAndScrolling(dismissOnClickOutside = true)

  private fun hostedDialogBlocksBackgroundClicksAndScrolling(
    dismissOnClickOutside: Boolean,
  ) = runComposeUiTest {
    var visible by mutableStateOf(true)
    var backgroundClicks = 0
    var panelClicks = 0
    var dismissRequests = 0
    val scrollState = ScrollState(0)

    setContent {
      DialogHost(Modifier.size(240.dp)) {
        Box(Modifier.size(80.dp).verticalScroll(scrollState).testTag("background")) {
          Box(Modifier.size(width = 80.dp, height = 480.dp).clickable { backgroundClicks++ })
        }
        UnstyledDialog(
          visible = visible,
          onDismissRequest = { dismissRequests++ },
          properties = DialogProperties(dismissOnClickOutside = dismissOnClickOutside),
        ) {
          Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            DialogPanel(Modifier.size(80.dp)) {
              Box(Modifier.fillMaxSize().clickable { panelClicks++ }.testTag("panel_button"))
            }
          }
        }
      }
    }

    onNodeWithTag("background").performMouseInput {
      enter(center)
      scroll(100f)
    }
    runOnIdle { assertThat(scrollState.value).isEqualTo(0) }
    onNodeWithTag("panel_button").performMouseInput { click() }
    runOnIdle {
      assertThat(panelClicks).isEqualTo(1)
      assertThat(dismissRequests).isEqualTo(0)
    }
    onNodeWithTag("background").performMouseInput { click() }
    runOnIdle {
      assertThat(backgroundClicks).isEqualTo(0)
      assertThat(dismissRequests).isEqualTo(if (dismissOnClickOutside) 1 else 0)
      visible = false
    }
    onNodeWithTag("panel_button").assertDoesNotExist()

    onNodeWithTag("background").performMouseInput {
      click()
      scroll(100f)
    }
    runOnIdle {
      assertThat(backgroundClicks).isEqualTo(1)
      assertThat(scrollState.value).isGreaterThan(0)
    }
  }

  private fun hostedDialogBlocksBackgroundHover(dismissOnClickOutside: Boolean) = runComposeUiTest {
    var visible by mutableStateOf(false)
    var backgroundHovered = false

    setContent {
      DialogHost(Modifier.size(240.dp)) {
        Box(
          Modifier.size(80.dp).pointerInput(Unit) {
            awaitPointerEventScope {
              while (true) {
                when (awaitPointerEvent().type) {
                  PointerEventType.Enter -> backgroundHovered = true
                  PointerEventType.Exit -> backgroundHovered = false
                }
              }
            }
          }.testTag("background"),
        )
        UnstyledDialog(
          visible = visible,
          onDismissRequest = { visible = false },
          properties = DialogProperties(dismissOnClickOutside = dismissOnClickOutside),
        ) {
          Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            DialogPanel(Modifier.size(80.dp).testTag("panel")) {}
          }
        }
      }
    }

    onNodeWithTag("background").performMouseInput { enter(center) }
    waitForIdle()
    runOnIdle { assertThat(backgroundHovered).isTrue() }
    onNodeWithTag("background").performMouseInput { exit() }
    runOnIdle { visible = true }
    onNodeWithTag("panel").assertExists()

    onNodeWithTag("background").performMouseInput { enter(center) }
    waitForIdle()
    runOnIdle { assertThat(backgroundHovered).isFalse() }

    onNodeWithTag("background").performMouseInput { exit() }
    runOnIdle { visible = false }
    onNodeWithTag("panel").assertDoesNotExist()
    onNodeWithTag("background").performMouseInput { enter(center) }
    waitForIdle()
    runOnIdle { assertThat(backgroundHovered).isTrue() }
  }

  @Test
  fun dialogPanelRunsEnterTransitionWhenShown() = runComposeUiTest {
    var visible by mutableStateOf(false)

    setContent {
      UnstyledDialog(
        visible = visible,
        onDismissRequest = { visible = false },
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          DialogPanel(
            modifier = Modifier
              .size(100.dp)
              .testTag("dialog_content"),
            enter = slideInVertically(
              animationSpec = tween(durationMillis = 10_000),
              initialOffsetY = { it },
            ),
          ) {
          }
        }
      }
    }

    waitForIdle()
    mainClock.autoAdvance = false
    try {
      runOnIdle { visible = true }
      mainClock.advanceTimeUntil {
        onAllNodes(hasTestTag("dialog_content")).fetchSemanticsNodes().isNotEmpty()
      }
      waitUntilExactlyOneExists(hasTestTag("dialog_content"))

      val enteringTop = onNodeWithTag("dialog_content").fetchSemanticsNode().boundsInRoot.top

      mainClock.advanceTimeBy(10_000)
      val restingTop = onNodeWithTag("dialog_content").fetchSemanticsNode().boundsInRoot.top

      assertThat(enteringTop).isGreaterThan(restingTop)
    } finally {
      mainClock.autoAdvance = true
    }
  }
}
