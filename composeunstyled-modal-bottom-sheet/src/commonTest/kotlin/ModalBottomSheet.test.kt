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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.test.runComposeUiTest
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class ModalBottomSheetTest {
  private val DensityTolerance = 0.1.dp

  @Test
  fun sheet_is_visible_when_initial_detent_is_fully_expanded() = runComposeUiTest {
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

  @Test
  fun sheet_is_not_visible_when_initial_detent_is_hidden() = runComposeUiTest {
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

  @Test
  fun sheet_is_rested_at_the_height_of_its_content_when_initial_detent_is_fully_expanded() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        40.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun sheet_is_visible_when_detents_do_not_include_hidden() = runComposeUiTest {
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

  @Test
  fun content_respects_locallayoutdirection() = runComposeUiTest {
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

  @Test
  fun scrim_is_visible_when_initial_detent_is_fully_expanded() = runComposeUiTest {
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

  @Test
  fun dialog_exists_when_going_from_hidden_to_visible() = runComposeUiTest {
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

  @Test
  fun sheet_rests_at_the_correct_height_after_entering() = runComposeUiTest {
    lateinit var state: ModalBottomSheetState
    setContent {
      state = rememberModalBottomSheetState(initialDetent = SheetDetent.Hidden)
      UnstyledModalBottomSheet(state, overlay = { Scrim() }) {
        Sheet { Box(Modifier.size(40.dp)) }
      }
    }

    state.targetDetent = SheetDetent.FullyExpanded
    waitForIdle()
    assertThat(state.offset).isCloseTo(
      with(density) {
        40.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun animates_sheet_in_when_entering() = runComposeUiTest {
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

    advanceUntilSheetExists()
    assertThat(state.isIdle).isFalse()
    onNodeWithTag("sheet").assertExists()

    // finish animation. we should be resting
    mainClock.autoAdvance = true
    awaitIdle()
    assertThat(state.bottomSheetState.isIdle).isTrue()
  }

  @Test
  fun animates_scrim_in_when_entering() = runComposeUiTest {
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

    state.targetDetent = SheetDetent.FullyExpanded
    waitForIdle()

    onNodeWithTag("scrim").assertExists()
    assertThat(state.modalState.transitionState.targetState).isTrue()
  }

  @Test
  fun waits_until_exit_animation_ends_before_removing_dialog() = runComposeUiTest {
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

  @Test
  fun starts_sheet_exit_animation_when_exiting() = runComposeUiTest {
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

  @Test
  fun starts_scrim_exit_animation_when_exiting() = runComposeUiTest {
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

  @Test
  fun sheet_is_dismissed_when_tapping_outside_with_dismissonclickoutside_true() = runComposeUiTest {
    setContent {
      UnstyledModalBottomSheet(
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),

        overlay = { Scrim(Modifier.testTag("scrim")) },
      ) {
        Sheet { Box(Modifier.size(40.dp)) }
      }
    }
    onNodeWithTag("scrim").performClick()
    waitForIdle()
    onNode(isDialog()).assertDoesNotExist()
  }

  @Test
  fun modal_sheet_remains_mounted_while_outside_tap_dismiss_animation_is_running() = runComposeUiTest {
    lateinit var state: ModalBottomSheetState
    setContent {
      state = rememberModalBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        animationSpec = tween(durationMillis = 1000),
      )
      UnstyledModalBottomSheet(
        state = state,
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
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

  @Test
  fun outside_tap_dismiss_uses_the_provided_dismiss_animation_spec() = runComposeUiTest {
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
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
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

  @Test
  fun modal_sheet_remains_visible_while_outside_tap_interrupts_enter_animation() = runComposeUiTest {
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
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
        overlay = { Scrim(Modifier.testTag("scrim")) },
      ) {
        Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
      }
    }
    waitForIdle()
    onNode(isDialog()).assertDoesNotExist()

    mainClock.autoAdvance = false
    state.targetDetent = peek
    advanceUntilModalExists()
    onNode(isDialog()).assertExists()
    advanceUntilSheetExists()
    onNodeWithTag("sheet").assertExists()
    var frame = 0
    while (state.offset <= 0f && frame < 30) {
      mainClock.advanceTimeByFrame()
      frame++
    }
    assertThat(state.offset).isGreaterThan(0f)

    onNode(isDialog()).performTouchInput { click(Offset(1f, 1f)) }
    mainClock.advanceTimeByFrame()

    val offsetAfterDismiss = state.offset
    val dialogBottomAfterDismiss = onNode(isDialog()).fetchSemanticsNode().boundsInRoot.bottom
    val sheetTopAfterDismiss = onNodeWithTag("sheet").fetchSemanticsNode().boundsInRoot.top

    assertThat(state.offset).isGreaterThan(0f)
    assertThat(sheetTopAfterDismiss).isLessThan(dialogBottomAfterDismiss)
    onNode(isDialog()).assertExists()
    onNodeWithTag("sheet").assertExists()

    mainClock.advanceTimeBy(250)
    assertThat(state.offset).isLessThan(offsetAfterDismiss)
    mainClock.autoAdvance = true
  }

  @Test
  fun modal_sheet_unmounts_after_outside_tap_dismiss_animation_and_modal_fragment_exit_complete() = runComposeUiTest {
    lateinit var state: ModalBottomSheetState
    setContent {
      state = rememberModalBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        animationSpec = tween(durationMillis = 1000),
      )
      UnstyledModalBottomSheet(
        state = state,
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
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

  @Test
  fun sheet_is_not_dismissed_when_tapping_outside_with_dismissonclickoutside_false() = runComposeUiTest {
    setContent {
      UnstyledModalBottomSheet(
        rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
        properties = ModalBottomSheetProperties(dismissOnClickOutside = false),

        overlay = { Scrim(Modifier.testTag("scrim")) },
      ) {
        Sheet { Box(Modifier.size(40.dp)) }
      }
    }
    onNodeWithTag("scrim").performClick()
    waitForIdle()
    onNode(isDialog()).assertExists()
  }

  @Test
  fun updated_properties_are_used_for_outside_tap_dismissal() = runComposeUiTest {
    var dismissOnClickOutside by mutableStateOf(false)

    setContent {
      UnstyledModalBottomSheet(
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
        properties = ModalBottomSheetProperties(dismissOnClickOutside = dismissOnClickOutside),
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

  @Test
  fun non_fixed_height_sheet_dismisses_with_one_outside_tap() = runComposeUiTest {
    setContent {
      UnstyledModalBottomSheet(
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
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

  @Test
  fun modal_is_dismissed_when_sheet_is_dismissed_programmatically() = runComposeUiTest {
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

  @Test
  fun modal_is_dismissed_when_sheet_is_swiped_to_hidden() = runComposeUiTest {
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

  @Test
  fun sheet_can_be_shown_again_when_caller_observes_that_it_settled_at_hidden() = runComposeUiTest {
    val peek = SheetDetent("peek") { containerHeight, _ ->
      containerHeight * 0.6f
    }
    lateinit var state: ModalBottomSheetState

    setContent {
      state = rememberModalBottomSheetState(
        initialDetent = peek,
        detents = listOf(SheetDetent.Hidden, peek, SheetDetent.FullyExpanded),
      )
      LaunchedEffect(
        state.isIdle,
        state.currentDetent,
      ) {
        if (
          state.isIdle &&
          state.currentDetent == SheetDetent.Hidden
        ) {
          delay(1.seconds)
          state.targetDetent = peek
        }
      }

      UnstyledModalBottomSheet(state, overlay = {
        Scrim(Modifier.testTag("scrim"))
      }) {
        Sheet { Box(Modifier.testTag("sheet").size(400.dp)) }
      }
    }

    onNode(isDialog()).assertExists()
    assertThat(state.currentDetent).isEqualTo(peek)

    onNodeWithTag("sheet").performTouchInput {
      swipeDown()
    }
    waitForIdle()

    onNode(isDialog()).assertDoesNotExist()
    assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

    mainClock.advanceTimeBy(1.seconds.inWholeMilliseconds)
    waitForIdle()

    onNode(isDialog()).assertExists()
    onNodeWithTag("sheet").assertIsDisplayed()
    assertThat(state.currentDetent).isEqualTo(peek)
  }

  @Test
  fun currentdetent_updates_to_hidden_when_targetdetent_changes_to_hidden_during_opening_animation() = runComposeUiTest {
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

  @Test
  fun isidle_becomes_true_when_animation_to_hidden_completes() = runComposeUiTest {
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

  @Test
  fun currentdetent_updates_to_hidden_when_sheet_caught_and_dismissed_with_gesture_during_opening_animatio() = runComposeUiTest {
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
    advanceUntilSheetExists()
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

  @Test
  fun modal_appears_and_sheet_expands_to_fullyexpanded_when_animating_from_hidden() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        600.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun modal_disappears_and_sheet_collapses_to_hidden_when_animating_from_fullyexpanded() = runComposeUiTest {
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

  @Test
  fun sheet_moves_to_custom_detent_when_animating_from_hidden_to_custom_detent() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        300.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun target_detent_updates_during_animation_when_animating_to_fullyexpanded() = runComposeUiTest {
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

  @Test
  fun animateto_behaves_like_targetdetent_setter_when_animating_from_hidden_to_fullyexpanded() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        600.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    onNode(isDialog()).assertExists()
  }

  @Test
  fun scrollable_content_without_fixed_height_does_not_clip_last_item() = runComposeUiTest {
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

  @Test
  fun ondismiss_is_called_when_hiding_after_scrollable_content_was_scrolled() = runComposeUiTest {
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

  @Test
  fun modal_remains_visible_when_content_size_changes_after_showing() = runComposeUiTest {
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

  @Test
  fun given_sheet_is_fully_expanded_and_dismissonclickoutside_is_true_when_tapping_outside_then_ondismiss() = runComposeUiTest {
    var dismissCallCount = 0
    setContent {
      UnstyledModalBottomSheet(
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
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

  @Test
  fun given_sheet_is_fully_expanded_and_dismissonclickoutside_is_false_when_tapping_outside_then_ondismiss() = runComposeUiTest {
    var dismissCallCount = 0
    setContent {
      UnstyledModalBottomSheet(
        state = rememberModalBottomSheetState(initialDetent = SheetDetent.FullyExpanded),
        properties = ModalBottomSheetProperties(dismissOnClickOutside = false),
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

  @Test
  fun given_sheet_is_fully_expanded_when_sheet_is_swiped_to_hidden_then_ondismiss_is_called() = runComposeUiTest {
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

  @Test
  fun given_sheet_started_hidden_and_was_shown_when_clicking_outside_then_ondismiss_is_called() = runComposeUiTest {
    lateinit var state: ModalBottomSheetState
    var dismissCallCount = 0
    setContent {
      state = rememberModalBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
      )
      UnstyledModalBottomSheet(
        state = state,
        properties = ModalBottomSheetProperties(dismissOnClickOutside = true),
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

  @Test
  fun modal_appears_and_sheet_jumps_to_fullyexpanded_immediately_when_jumping_from_hidden() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        0.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )

    scope.launch {
      state.jumpTo(SheetDetent.FullyExpanded)
    }
    waitForIdle()

    onNode(isDialog()).assertExists()
    assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
    assertThat(state.offset).isCloseTo(
      with(density) {
        600.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    assertThat(state.isIdle).isTrue()
    onNodeWithTag("sheet").assertIsDisplayed()
  }

  @Test
  fun modal_disappears_and_sheet_jumps_to_hidden_immediately_when_jumping_from_fullyexpanded() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        0.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    assertThat(state.isIdle).isTrue()
  }

  @Test
  fun sheet_jumps_to_custom_detent_immediately_when_jumping_from_hidden() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        300.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    assertThat(state.isIdle).isTrue()
  }

  @Test
  fun jumpto_changes_state_without_animation_when_jumping_between_detents() = runComposeUiTest {
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
    assertThat(state.offset).isCloseTo(
      with(density) {
        600.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    assertThat(state.isIdle).isTrue()
  }

  @Test
  fun modal_remains_visible_when_jumping_between_non_hidden_detents() = runComposeUiTest {
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

private fun androidx.compose.ui.test.ComposeUiTest.advanceUntilModalSheetGone() {
  repeat(120) {
    val modalGone = runCatching { onNode(isDialog()).assertDoesNotExist() }.isSuccess
    val sheetGone = runCatching { onNodeWithTag("sheet").assertDoesNotExist() }.isSuccess
    val scrimGone = runCatching { onNodeWithTag("scrim").assertDoesNotExist() }.isSuccess
    if (modalGone && sheetGone && scrimGone) return

    mainClock.advanceTimeByFrame()
  }
}

private fun androidx.compose.ui.test.ComposeUiTest.advanceUntilModalExists() {
  repeat(120) {
    val modalExists = runCatching { onNode(isDialog()).assertExists() }.isSuccess
    if (modalExists) return

    mainClock.advanceTimeByFrame()
  }
  onNode(isDialog()).assertExists()
}

private fun androidx.compose.ui.test.ComposeUiTest.advanceUntilSheetExists() {
  repeat(120) {
    val sheetExists = runCatching { onNodeWithTag("sheet").assertExists() }.isSuccess
    if (sheetExists) return

    mainClock.advanceTimeByFrame()
  }
  onNodeWithTag("sheet").assertExists()
}
