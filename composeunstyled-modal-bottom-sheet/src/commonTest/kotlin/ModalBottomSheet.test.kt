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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.assertions.isTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test

class ModalBottomSheetTest {
  private val DensityTolerance = 0.1.dp

  @Test
  fun initial_state() = runTestSuite {
    testCase("sheet is visible, when initial detent is fully expanded") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(
          state = state,
          overlay = {
            Scrim()
          },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
        }
      }
      onNodeWithTag("sheet").assertExists()
    }

    testCase("sheet is not visible, when initial detent is hidden") {
      setContent {
        UnstyledModalBottomSheet(
          rememberModalBottomSheetState(
            initialDetent = SheetDetent.Hidden,
          ),
          overlay = {
            Scrim()
          },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
        }
      }
      onNodeWithTag("sheet").assertDoesNotExist()
    }

    testCase(
      "sheet is rested at the height of its content, when initial detent is fully expanded",
    ) {
      lateinit var state: ModalBottomSheetState

      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
        }
      }
      onNodeWithTag("sheet").assertExists()
      assertThat(state.offset).isCloseTo(40.dp.toPx(), DensityTolerance.toPx())
    }

    testCase("sheet is visible, when detents do not include Hidden") {
      val peek = SheetDetent("peek") { _, _ -> 100.dp }

      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(
            initialDetent = peek,
            detents = listOf(peek, SheetDetent.FullyExpanded),
          ),
          overlay = { Scrim() },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
        }
      }

      onNodeWithTag("sheet").assertExists()
    }

    testCase("content respects LocalLayoutDirection") {
      setContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          UnstyledModalBottomSheet(
            state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
            overlay = { Scrim() },
          ) {
            Sheet {
              val layoutDirection =
                if (LocalLayoutDirection.current == LayoutDirection.Rtl) "rtl" else "ltr"
              BasicText(layoutDirection, Modifier.testTag("layout_direction"))
            }
          }
        }
      }

      onNodeWithTag("layout_direction").assertTextEquals("rtl")
    }

    testCase("scrim is visible, when initial detent is fully expanded") {
      setContent {
        UnstyledModalBottomSheet(
          rememberModalBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
          ),
          overlay = {
            Scrim(Modifier.testTag("scrim"))
          },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").assertExists()
    }
  }

  @Test
  fun enters() = runTestSuite {
    testCase("dialog exists, when going from hidden to visible") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNode(isDialog()).assertDoesNotExist()
      state.targetDetent = SheetDetent.FullyExpanded
      waitForIdle()
      onNode(isDialog()).assertExists()
    }

    testCase("sheet rests at the correct height,after entering") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }

      state.targetDetent = SheetDetent.FullyExpanded
      waitForIdle()
      assertThat(state.offset).isCloseTo(40.dp.toPx(), DensityTolerance.toPx())
    }

    testCase("animates sheet in, when entering") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet {
            Box(Modifier.testTag("sheet").fillMaxSize())
          }
        }
      }
      onNodeWithTag("sheet").assertDoesNotExist()

      // start animation
      // we should now be in motion
      mainClock.autoAdvance = false
      state.targetDetent = SheetDetent.FullyExpanded

      // wait for the modal content to be added first
      mainClock.advanceTimeBy(1)
      assertThat(state.isIdle).isFalse()
      onNodeWithTag("sheet").assertExists()

      // finish animation. we should be resting
      mainClock.autoAdvance = true
      awaitIdle()
      assertThat(state.bottomSheetState.isIdle).isTrue()
    }

    testCase("animates scrim in, when entering") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
        UnstyledModalBottomSheet(state, overlay = {
          Scrim(
            modifier = Modifier.testTag("scrim"),
            enter = fadeIn(tween(durationMillis = 300)),
          )
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").assertDoesNotExist()

      mainClock.autoAdvance = false
      state.targetDetent = SheetDetent.FullyExpanded

      // we need to wait for the modal to enter first
      mainClock.advanceTimeBy(1)
      // Scrim should start appearing now

      // Check that the scrim animation is running (not instantly completed)
      onNodeWithTag("scrim").assertExists()
      assertThat(state.modalState.transitionState.isIdle).isFalse()

      // Advance halfway through animation
      mainClock.advanceTimeBy(150)
      assertThat(state.modalState.transitionState.isIdle).isFalse()

      // Complete the animation
      mainClock.advanceTimeBy(150)
      waitForIdle()
      assertThat(state.modalState.transitionState.isIdle).isTrue()
      assertThat(state.modalState.transitionState.targetState).isTrue()
    }
  }

  @Test
  fun exits() = runTestSuite {
    testCase("waits until exit animation ends before removing dialog") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNode(isDialog()).assertExists()
      state.targetDetent = SheetDetent.Hidden
      waitForIdle()
      onNode(isDialog()).assertDoesNotExist()
    }

    testCase("starts sheet exit animation, when exiting") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(40.dp)) }
        }
      }
      onNodeWithTag("sheet").assertExists()
      state.targetDetent = SheetDetent.Hidden
      waitForIdle()
      onNodeWithTag("sheet").assertDoesNotExist()
    }

    testCase("starts scrim exit animation, when exiting") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(state, overlay = {
          Scrim(Modifier.testTag("scrim"))
        }) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").assertExists()
      state.targetDetent = SheetDetent.Hidden
      waitForIdle()
      onNodeWithTag("scrim").assertDoesNotExist()
    }
  }

  @Test
  fun dismiss_interactions() = runTestSuite {
    testCase("sheet is dismissed, when tapping outside with dismissOnClickOutside true") {
      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnClickOutside = true),

          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").performClick()
      waitForIdle()
      onNode(isDialog()).assertDoesNotExist()
    }

    testCase("modal sheet remains mounted while outside tap dismiss animation is running") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          animationSpec = tween(durationMillis = 1000),
        )
        UnstyledModalBottomSheet(
          state = state,
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }
      waitForIdle()
      onNode(isDialog()).assertExists()
      onNodeWithTag("sheet").assertExists()

      mainClock.autoAdvance = false
      onNode(isDialog()).performTouchInput { click(Offset(1f, 1f)) }
      mainClock.advanceTimeByFrame()
      mainClock.advanceTimeBy(500)

      assertThat(state.bottomSheetState.isIdle).isFalse()
      onNode(isDialog()).assertExists()
      onNodeWithTag("sheet").assertExists()
    }

    testCase("outside tap dismiss uses the provided dismiss animation spec") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          animationSpec = tween(durationMillis = 1000),
          dismissAnimationSpec = tween(durationMillis = 100),
        )
        UnstyledModalBottomSheet(
          state = state,
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }
      waitForIdle()
      onNode(isDialog()).assertExists()

      mainClock.autoAdvance = false
      onNode(isDialog()).performTouchInput { click(Offset(1f, 1f)) }
      mainClock.advanceTimeByFrame()
      mainClock.advanceTimeBy(150)

      assertThat(state.bottomSheetState.isIdle).isTrue()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      mainClock.autoAdvance = true
    }

    testCase("modal sheet remains visible while outside tap interrupts enter animation") {
      val peek = SheetDetent("peek") { containerHeight, _ ->
        containerHeight * 0.6f
      }
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, peek, SheetDetent.FullyExpanded),
          animationSpec = tween(durationMillis = 1000),
          dismissAnimationSpec = tween(durationMillis = 1000),
        )
        UnstyledModalBottomSheet(
          state = state,
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }
      waitForIdle()
      onNode(isDialog()).assertDoesNotExist()

      mainClock.autoAdvance = false
      state.targetDetent = peek
      var frame = 0
      while (runCatching { onNode(isDialog()).assertExists() }.isFailure && frame < 30) {
        mainClock.advanceTimeByFrame()
        frame++
      }
      onNode(isDialog()).assertExists()
      onNodeWithTag("sheet").assertExists()
      frame = 0
      while (state.offset <= 0f && frame < 30) {
        mainClock.advanceTimeByFrame()
        frame++
      }
      assertThat(state.offset).isGreaterThan(0f)

      onNode(isDialog()).performTouchInput { click(Offset(1f, 1f)) }
      mainClock.advanceTimeByFrame()

      val dialogBottomAfterDismiss = onNode(isDialog()).fetchSemanticsNode().boundsInRoot.bottom
      val sheetTopAfterDismiss = onNodeWithTag("sheet").fetchSemanticsNode().boundsInRoot.top

      assertThat(state.offset).isGreaterThan(0f)
      assertThat(sheetTopAfterDismiss).isLessThan(dialogBottomAfterDismiss)
      onNode(isDialog()).assertExists()
      onNodeWithTag("sheet").assertExists()
      mainClock.autoAdvance = true
    }

    testCase(
      "modal sheet unmounts after outside tap dismiss animation and modal fragment exit complete",
    ) {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          animationSpec = tween(durationMillis = 1000),
        )
        UnstyledModalBottomSheet(
          state = state,
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          overlay = {
            Scrim(
              modifier = Modifier.testTag("scrim"),
              exit = androidx.compose.animation.fadeOut(tween(durationMillis = 300)),
            )
          },
        ) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }
      waitForIdle()
      onNode(isDialog()).assertExists()

      mainClock.autoAdvance = false
      onNode(isDialog()).performTouchInput { click(Offset(1f, 1f)) }
      mainClock.advanceTimeByFrame()
      advanceUntilModalSheetGone()

      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      onNode(isDialog()).assertDoesNotExist()
      onNodeWithTag("sheet").assertDoesNotExist()
      onNodeWithTag("scrim").assertDoesNotExist()
    }

    testCase("sheet is not dismissed, when tapping outside with dismissOnClickOutside false") {
      setContent {
        UnstyledModalBottomSheet(
          rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnClickOutside = false),

          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").performClick()
      waitForIdle()
      onNode(isDialog()).assertExists()
    }

    testCase("updated properties are used for outside tap dismissal") {
      var dismissOnClickOutside by mutableStateOf(false)

      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnClickOutside = dismissOnClickOutside),
          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }

      onNodeWithTag("scrim").performClick()
      waitForIdle()
      onNode(isDialog()).assertExists()

      dismissOnClickOutside = true
      waitForIdle()

      onNodeWithTag("scrim").performClick()
      waitForIdle()
      onNode(isDialog()).assertDoesNotExist()
    }

    testCase("non-fixed height sheet dismisses with one outside tap") {
      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet {
            Column(Modifier.fillMaxWidth()) {
              DragIndication()
              BasicText("Some text")
            }
          }
        }
      }

      onNodeWithTag("scrim").performClick()
      waitForIdle()

      onNode(isDialog()).assertDoesNotExist()
    }

    testCase("modal is dismissed, when sheet is dismissed programmatically") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(state, overlay = {
          Scrim(Modifier.testTag("scrim"))
        }) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").assertExists()
      onNode(isDialog()).assertExists()

      state.targetDetent = SheetDetent.Hidden
      waitForIdle()

      onNode(isDialog()).assertDoesNotExist()
      onNodeWithTag("scrim").assertDoesNotExist()
    }

    testCase("modal is dismissed, when sheet is swiped to hidden") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim(Modifier.testTag("scrim"))
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }
      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

      onNodeWithTag("sheet").performTouchInput {
        swipeDown()
      }
      waitForIdle()

      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      onNode(isDialog()).assertDoesNotExist()
      onNodeWithTag("scrim").assertDoesNotExist()
    }
  }

  @Test
  fun state_changes_during_interactions() = runTestSuite {
    testCase(
      "currentDetent updates to Hidden, when targetDetent changes to Hidden during opening animation",
    ) {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          animationSpec = tween(2000),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(100.dp)) }
        }
      }

      waitForIdle()
      mainClock.autoAdvance = false

      // Start opening animation
      state.targetDetent = SheetDetent.FullyExpanded
      mainClock.advanceTimeBy(500)

      // Change target to Hidden mid-animation
      state.targetDetent = SheetDetent.Hidden

      mainClock.autoAdvance = true
      waitForIdle()

      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
    }

    testCase("isIdle becomes true, when animation to Hidden completes") {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          animationSpec = tween(1000),
        )
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet { Box(Modifier.size(100.dp)) }
        }
      }

      waitForIdle()
      assertThat(state.isIdle).isTrue()

      mainClock.autoAdvance = false

      // Start closing animation
      state.targetDetent = SheetDetent.Hidden
      mainClock.advanceTimeBy(500)

      // Should not be idle during animation
      assertThat(state.isIdle).isFalse()

      // Complete animation
      mainClock.autoAdvance = true
      waitForIdle()

      // Should be idle after animation completes
      assertThat(state.isIdle).isTrue()
    }

    testCase(
      "currentDetent updates to Hidden, when sheet caught and dismissed with gesture during opening animation",
    ) {
      lateinit var state: ModalBottomSheetState
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          animationSpec = tween(2000),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(300.dp)) }
        }
      }

      waitForIdle()
      mainClock.autoAdvance = false

      // Start opening animation programmatically
      state.targetDetent = SheetDetent.FullyExpanded
      mainClock.advanceTimeBy(500)

      // Catch the sheet mid-animation and swipe it down to dismiss (gesture-driven)
      onNodeWithTag("sheet").performTouchInput {
        swipeDown()
      }

      mainClock.autoAdvance = true
      waitForIdle()

      // currentDetent should update to Hidden immediately after gesture completes
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
    }
  }

  @Test
  fun animateTo() = runTestSuite {
    testCase("modal appears and sheet expands to FullyExpanded, when animating from Hidden") {
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim(Modifier.testTag("scrim"))
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      onNode(isDialog()).assertDoesNotExist()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

      scope.launch {
        state.animateTo(SheetDetent.FullyExpanded)
      }
      waitForIdle()

      onNode(isDialog()).assertExists()
      onNodeWithTag("sheet").assertIsDisplayed()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
      assertThat(state.offset).isCloseTo(600.dp.toPx(), DensityTolerance.toPx())
    }

    testCase("modal disappears and sheet collapses to Hidden, when animating from FullyExpanded") {
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim(Modifier.testTag("scrim"))
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

      scope.launch {
        state.animateTo(SheetDetent.Hidden)
      }
      waitForIdle()

      onNode(isDialog()).assertDoesNotExist()
      onNodeWithTag("sheet").assertDoesNotExist()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
    }

    testCase("sheet moves to custom detent, when animating from Hidden to custom detent") {
      val customDetent = SheetDetent("custom") { _, _ -> 300.dp }
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, customDetent, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

      scope.launch {
        state.animateTo(customDetent)
      }
      waitForIdle()

      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(customDetent)
      assertThat(state.offset).isCloseTo(300.dp.toPx(), DensityTolerance.toPx())
    }

    testCase("target detent updates during animation, when animating to FullyExpanded") {
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope
      val settleDuration = 5000L

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          animationSpec = tween(settleDuration.toInt()),
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }

      waitForIdle()

      mainClock.autoAdvance = false
      scope.launch {
        state.animateTo(SheetDetent.FullyExpanded)
      }

      // wait for modal content to be added first
      mainClock.advanceTimeBy(1000L)

      // During animation, target should be FullyExpanded
      assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
      assertThat(state.isIdle).isFalse()
    }

    testCase(
      "animateTo behaves like targetDetent setter, when animating from Hidden to FullyExpanded",
    ) {
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

      scope.launch {
        state.animateTo(SheetDetent.FullyExpanded)
      }
      waitForIdle()

      // Should reach FullyExpanded
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
      assertThat(state.offset).isCloseTo(600.dp.toPx(), DensityTolerance.toPx())
      onNode(isDialog()).assertExists()
    }
  }

  @Test
  fun scrollable_content() = runTestSuite {
    testCase("scrollable content without fixed height does not clip last item") {
      val expanded = SheetDetent("expanded") { containerHeight, _ ->
        containerHeight * 0.7f
      }

      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(
            initialDetent = expanded,
            detents = listOf(SheetDetent.Hidden, expanded),
          ),
          overlay = { Scrim() },
        ) {
          Sheet {
            Column(
              Modifier
                .testTag("scrollable_content")
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            ) {
              repeat(10) { index ->
                BasicText(
                  text = "index = $index",
                  modifier = Modifier
                    .testTag("item_$index")
                    .fillMaxWidth()
                    .height(100.dp),
                )
              }
            }
          }
        }
      }

      onNodeWithTag("item_9").performScrollTo()
      onNodeWithTag("item_9").assertIsDisplayed()
    }

    testCase("onDismiss is called, when hiding after scrollable content was scrolled") {
      lateinit var state: ModalBottomSheetState
      var dismissCallCount = 0

      setContent {
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledModalBottomSheet(
          state = state,
          onDismiss = { dismissCallCount++ },
          overlay = { Scrim() },
        ) {
          Sheet {
            Column(
              Modifier
                .testTag("scrollable_content")
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            ) {
              repeat(10) { index ->
                BasicText(
                  text = "index = $index",
                  modifier = Modifier
                    .testTag("item_$index")
                    .fillMaxWidth()
                    .height(100.dp),
                )
              }
            }
          }
        }
      }

      onNodeWithTag("item_9").performScrollTo()
      state.targetDetent = SheetDetent.Hidden
      waitForIdle()

      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      assertThat(dismissCallCount).isEqualTo(1)
    }

    testCase("modal remains visible, when content size changes after showing") {
      var linesOfText by mutableStateOf(10)

      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet {
            Column(Modifier.fillMaxWidth()) {
              DragIndication()
              repeat(linesOfText) { index ->
                BasicText("line $index", Modifier.testTag("line_$index"))
              }
            }
          }
        }
      }

      onNodeWithTag("scrim").assertExists()

      linesOfText = 5
      waitForIdle()

      onNode(isDialog()).assertExists()
      onNodeWithTag("scrim").assertExists()
      onNodeWithTag("line_4").assertExists()
    }
  }

  @Test
  fun onDismiss() = runTestSuite {
    testCase(
      "given sheet is fully expanded and dismissOnClickOutside is true, when tapping outside, then onDismiss is called",
    ) {
      var dismissCallCount = 0
      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          onDismiss = { dismissCallCount++ },

          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").performClick()
      waitForIdle()
      assertThat(dismissCallCount).isEqualTo(1)
    }

    testCase(
      "given sheet is fully expanded and dismissOnClickOutside is false, when tapping outside, then onDismiss is not called",
    ) {
      var dismissCallCount = 0
      setContent {
        UnstyledModalBottomSheet(
          state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
          properties = ModalSheetProperties(dismissOnClickOutside = false),
          onDismiss = { dismissCallCount++ },

          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }
      onNodeWithTag("scrim").performClick()
      waitForIdle()
      assertThat(dismissCallCount).isEqualTo(0)
    }

    testCase(
      "given sheet is fully expanded, when sheet is swiped to hidden, then onDismiss is called",
    ) {
      lateinit var state: ModalBottomSheetState
      var dismissCallCount = 0
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state = state, onDismiss = {
          dismissCallCount++
        }, overlay = {
          Scrim(Modifier.testTag("scrim"))
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
        }
      }
      onNodeWithTag("scrim").assertExists()

      onNodeWithTag("sheet").performTouchInput {
        swipeDown()
      }

      waitForIdle()

      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      assertThat(dismissCallCount).isEqualTo(1)
    }

    testCase(
      "given sheet started hidden and was shown, when clicking outside, then onDismiss is called",
    ) {
      lateinit var state: ModalBottomSheetState
      var dismissCallCount = 0
      setContent {
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(
          state = state,
          properties = ModalSheetProperties(dismissOnClickOutside = true),
          onDismiss = { dismissCallCount++ },

          overlay = { Scrim(Modifier.testTag("scrim")) },
        ) {
          Sheet { Box(Modifier.size(40.dp)) }
        }
      }

      // Show the sheet
      state.targetDetent = SheetDetent.FullyExpanded
      waitForIdle()
      onNode(isDialog()).assertExists()

      // Dismiss by clicking outside
      println("--- CLICKING OUTSIDE")
      onNodeWithTag("scrim").performClick()

      waitForIdle()
      println("--- ASSERTING")
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      assertThat(dismissCallCount).isEqualTo(1)
    }
  }

  @Test
  fun jumpTo() = runTestSuite {
    testCase(
      "modal appears and sheet jumps to FullyExpanded immediately, when jumping from Hidden",
    ) {
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      onNode(isDialog()).assertDoesNotExist()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      assertThat(state.offset).isCloseTo(0.dp.toPx(), DensityTolerance.toPx())

      scope.launch {
        state.jumpTo(SheetDetent.FullyExpanded)
      }
      waitForIdle()

      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
      assertThat(state.offset).isCloseTo(600.dp.toPx(), DensityTolerance.toPx())
      assertThat(state.isIdle).isTrue()
      onNodeWithTag("sheet").assertIsDisplayed()
    }

    testCase(
      "modal disappears and sheet jumps to Hidden immediately, when jumping from FullyExpanded",
    ) {
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)

      scope.launch {
        state.jumpTo(SheetDetent.Hidden)
      }
      waitForIdle()

      onNode(isDialog()).assertDoesNotExist()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)
      assertThat(state.offset).isCloseTo(0.dp.toPx(), DensityTolerance.toPx())
      assertThat(state.isIdle).isTrue()
    }

    testCase("sheet jumps to custom detent immediately, when jumping from Hidden") {
      val customDetent = SheetDetent("custom") { _, _ -> 300.dp }
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = SheetDetent.Hidden,
          detents = listOf(SheetDetent.Hidden, customDetent, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = {
          Scrim()
        }) {
          Sheet { Box(Modifier.testTag("sheet").size(600.dp)) }
        }
      }

      waitForIdle()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

      scope.launch {
        state.jumpTo(customDetent)
      }
      waitForIdle()

      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(customDetent)
      assertThat(state.offset).isCloseTo(300.dp.toPx(), DensityTolerance.toPx())
      assertThat(state.isIdle).isTrue()
    }

    testCase("jumpTo changes state without animation, when jumping between detents") {
      val halfDetent = SheetDetent("half") { _, _ -> 300.dp }
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = halfDetent,
          detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
          animationSpec = tween(5000), // Long animation to verify jumpTo is instant
        )
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet { Box(Modifier.size(600.dp)) }
        }
      }

      waitForIdle()
      mainClock.autoAdvance = false

      scope.launch {
        state.jumpTo(SheetDetent.FullyExpanded)
      }
      mainClock.advanceTimeByFrame()

      // Should be at FullyExpanded immediately, without animation
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
      assertThat(state.offset).isCloseTo(600.dp.toPx(), DensityTolerance.toPx())
      assertThat(state.isIdle).isTrue()
    }

    testCase("modal remains visible, when jumping between non-Hidden detents") {
      val halfDetent = SheetDetent("half") { _, _ -> 300.dp }
      lateinit var state: ModalBottomSheetState
      lateinit var scope: CoroutineScope

      setContent {
        scope = androidx.compose.runtime.rememberCoroutineScope()
        state = rememberModalBottomSheetState(
          initialDetent = halfDetent,
          detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
        )
        UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
          Sheet { Box(Modifier.size(600.dp)) }
        }
      }

      waitForIdle()
      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(halfDetent)

      scope.launch {
        state.jumpTo(SheetDetent.FullyExpanded)
      }
      waitForIdle()

      // Modal should still be visible
      onNode(isDialog()).assertExists()
      assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
    }
  }
}

private fun androidx.compose.ui.test.ComposeUiTest.advanceUntilModalSheetGone() {
  repeat(120) {
    val modalGone = runCatching { onNode(isDialog()).assertDoesNotExist() }.isSuccess
    val sheetGone = runCatching { onNodeWithTag("sheet").assertDoesNotExist() }.isSuccess
    val scrimGone = runCatching { onNodeWithTag("scrim").assertDoesNotExist() }.isSuccess
    if (modalGone && sheetGone && scrimGone) return

    mainClock.advanceTimeByFrame()
  }
}
