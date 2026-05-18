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
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThan
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test

class BottomSheetCommonTest {
  private val DensityTolerance = 0.5.dp

  @Test
  fun sheet_without_bottom_sheet_state_renders_content() = runComposeUiTest {
    setContent {
      with(FakeBottomSheetScope) {
        Sheet {
          BasicText("Sheet content")
        }
      }
    }

    onNodeWithText("Sheet content").assertIsDisplayed()
  }

  @Test
  fun sheet_panel_is_anchored_to_bottom_of_bottom_sheet_container() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      Box(
        modifier = Modifier
          .requiredSize(300.dp)
          .testTag("root"),
      ) {
        state = rememberBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.fillMaxSize(),
        ) {
          Sheet(
            modifier = Modifier
              .requiredSize(width = 120.dp, height = 80.dp)
              .testTag("panel"),
          ) {}
        }
      }
    }

    waitForIdle()

    val rootBounds = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
    val panelBounds = onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot

    assertThat(panelBounds.bottom).isEqualTo(rootBounds.bottom)
  }

  @Test
  fun bottom_sheet_does_not_fill_parent_without_caller_sizing_modifier() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(300.dp)) {
        state = rememberBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.testTag("bottom_sheet"),
        ) {
          Sheet(Modifier.size(80.dp)) {}
        }
      }
    }

    waitForIdle()

    onNodeWithTag("bottom_sheet").assertHeightIsEqualTo(80.dp)
  }

  @Test
  fun throws_exception_when_creating_state_without_detents() {
    assertFailure {
      runComposeUiTest {
        setContent {
          BottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
            detents = emptyList(),
            coroutineScope = rememberCoroutineScope(),
            animationSpec = tween(),
            density = density,
            velocityThreshold = { 0.dp },
            positionalThreshold = { 0.dp },
            confirmDetentChange = { true },
            decayAnimationSpec = rememberSplineBasedDecay(),
          )
        }
      }
    }.isInstanceOf<IllegalStateException>()
  }

  @Test
  fun throws_exception_when_initial_detent_not_in_detents_list() {
    assertFailure {
      runComposeUiTest {
        val customDetent = SheetDetent("custom") { _, _ -> 0.dp }
        setContent {
          BottomSheetState(
            initialDetent = customDetent,
            detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
            coroutineScope = rememberCoroutineScope(),
            animationSpec = tween(),
            density = density,
            velocityThreshold = { 0.dp },
            positionalThreshold = { 0.dp },
            confirmDetentChange = { true },
            decayAnimationSpec = rememberSplineBasedDecay(),
          )
        }
      }
    }.isInstanceOf<IllegalStateException>()
  }

  @Test
  fun throws_exception_when_creating_state_with_duplicate_detents() {
    assertFailure {
      runComposeUiTest {
        setContent {
          BottomSheetState(
            initialDetent = SheetDetent.Hidden,
            detents = listOf(SheetDetent.Hidden, SheetDetent.Hidden),
            coroutineScope = rememberCoroutineScope(),
            animationSpec = tween(),
            density = density,
            velocityThreshold = { 0.dp },
            positionalThreshold = { 0.dp },
            confirmDetentChange = { true },
            decayAnimationSpec = rememberSplineBasedDecay(),
          )
        }
      }
    }.isInstanceOf<IllegalStateException>()
  }

  @Test
  fun throws_exception_when_creating_state_with_rememberbottomsheetstate_without_detents() {
    assertFailure {
      runComposeUiTest {
        setContent {
          rememberBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
            detents = emptyList(),
          )
        }
      }
    }.isInstanceOf<IllegalStateException>()
  }

  @Test
  fun throws_exception_when_creating_state_with_rememberbottomsheetstate_with_initial_detent_not_in_list() {
    assertFailure {
      runComposeUiTest {
        setContent {
          rememberBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
            detents = listOf(SheetDetent.Hidden),
          )
        }
      }
    }.isInstanceOf<IllegalStateException>()
  }

  @Test
  fun setting_state_enabled_to_false_blocks_dragging() = runComposeUiTest {
    val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
      containerHeight * 0.5f
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.fillMaxSize()) {
        state = rememberBottomSheetState(
          initialDetent = halfExpandedDetent,
          detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          enabled = false,
          modifier = Modifier.testTag("sheet"),
        ) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxWidth()
                .height(200.dp),
            )
          }
        }
      }
    }

    // Initially at half expanded
    val initialOffset = state.offset

    // Try to swipe up - should not move the sheet
    onNodeWithTag("sheet").performTouchInput {
      swipeUp()
    }
    waitForIdle()

    // Sheet should remain at the same detent and offset
    assertThat(state.currentDetent).isEqualTo(halfExpandedDetent)
    assertThat(state.offset).isEqualTo(initialOffset)
  }

  @Test
  fun isidle_is_false_when_dragging_sheet() = runComposeUiTest {
    val halfDetent = SheetDetent("half") { _, _ ->
      100.dp
    }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = halfDetent,
        detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
      )

      UnstyledBottomSheet(
        state,
        Modifier.fillMaxSize().testTag("sheet"),
      ) {
        Sheet {
          Box(
            Modifier
              .testTag("sheet_contents")
              .fillMaxWidth()
              .height(150.dp),
          )
        }
      }
    }

    waitForIdle()
    assertThat(state.isIdle).isTrue()

    mainClock.autoAdvance = false

    // Start dragging and move
    onNodeWithTag("sheet_contents").performTouchInput {
      down(center)
      moveTo(center.copy(y = center.y - 50f))
    }
    mainClock.advanceTimeBy(50)

    assertThat(state.isIdle).isFalse()
  }

  @Test
  fun isidle_is_false_during_animation_when_sheet_animates_to_new_detent() = runComposeUiTest {
    val halfDetent = SheetDetent("half") { _, _ ->
      100.dp
    }

    lateinit var state: BottomSheetState
    lateinit var scope: CoroutineScope

    setContent {
      scope = rememberCoroutineScope()
      state = rememberBottomSheetState(
        initialDetent = halfDetent,
        detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
        animationSpec = tween(1000),
      )

      UnstyledBottomSheet(
        state,
        Modifier.fillMaxSize().testTag("sheet"),
      ) {
        Sheet {
          Box(
            Modifier
              .testTag("sheet_contents")
              .fillMaxWidth()
              .height(150.dp),
          )
        }
      }
    }

    waitForIdle()
    mainClock.autoAdvance = false

    scope.launch {
      state.animateTo(SheetDetent.FullyExpanded)
    }
    mainClock.advanceTimeBy(50)

    assertThat(state.isIdle).isFalse()

    mainClock.autoAdvance = true
    waitForIdle()

    assertThat(state.isIdle).isTrue()
  }

  @Test
  fun offset_is_0_when_sheet_is_created_with_hidden_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState
    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
      )
      UnstyledBottomSheet(state = state, modifier = Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(40.dp))
        }
      }
    }

    onNodeWithTag("sheet_contents").assertIsNotDisplayed()
    assertThat(state.offset).isEqualTo(0f)
  }

  @Test
  fun offset_is_height_of_content_when_sheet_is_created_with_hidden_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
      )
      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(40.dp))
        }
      }
    }

    onNodeWithTag("sheet_contents").assertIsDisplayed()
    assertThat(state.offset).isCloseTo(
      with(density) {
        40.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun offset_is_custom_detent_height_when_creating_sheet_with_custom_initial_detent() = runComposeUiTest {
    val customDetent = SheetDetent("custom") { _, _ -> 50.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = customDetent,
        detents = listOf(SheetDetent.Hidden, customDetent, SheetDetent.FullyExpanded),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(60.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(customDetent)
    assertThat(state.offset).isCloseTo(
      with(density) {
        50.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun fixed_height_content_does_not_use_container_height_for_content_dependent_detents() = runComposeUiTest {
    val peekDetent = SheetDetent("peek") { containerHeight, _ ->
      containerHeight * 0.6f
    }
    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(width = 400.dp, height = 800.dp)) {
        state = rememberBottomSheetState(
          initialDetent = peekDetent,
          detents = listOf(SheetDetent.Hidden, peekDetent, SheetDetent.FullyExpanded),
        )
        UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
          Sheet {
            Box(Modifier.height(600.dp).fillMaxWidth())
          }
        }
      }
    }

    waitForIdle()

    assertThat(state.contentHeightPx).isCloseTo(
      with(density) { 600.dp.toPx() },
      with(density) { DensityTolerance.toPx() },
    )
    assertThat(state.offset).isCloseTo(
      with(density) { 480.dp.toPx() },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun offset_is_zero_when_sheet_is_created_at_hidden_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState
    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
      )
      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(40.dp))
        }
      }
    }

    assertThat(state.offset).isEqualTo(0f)
  }

  @Test
  fun offset_equals_content_height_when_sheet_is_created_at_fully_expanded_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState
    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
      )
      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(40.dp))
        }
      }
    }

    assertThat(state.offset).isCloseTo(
      with(density) {
        40.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun sheet_stops_at_height_of_content_when_target_detent_set_to_fully_expanded_from_hidden() = runComposeUiTest {
    lateinit var state: BottomSheetState
    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(40.dp))
        }
      }
    }
    state.targetDetent = SheetDetent.FullyExpanded
    onNodeWithTag("sheet_contents").assertIsDisplayed()
    assertThat(state.offset).isCloseTo(
      with(density) {
        40.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun sheet_stops_at_detent_fixed_height_when_target_detent_set_to_detent_with_fixed_height_from_hidden() = runComposeUiTest {
    val customDetent = SheetDetent("fixed") { _, _ -> 40.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = listOf(SheetDetent.Hidden, customDetent),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("content").height(70.dp).fillMaxWidth())
        }
      }
    }

    // Set to custom detent
    state.targetDetent = customDetent

    // Content should be displayed
    onNodeWithTag("content").assertIsDisplayed()
    assertThat(state.offset).isCloseTo(
      with(density) {
        40.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun isidle_correctly_reflects_animation_state_when_sheet_settles_at_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(100.dp))
        }
      }
    }

    // Start animation to FullyExpanded
    state.targetDetent = SheetDetent.FullyExpanded

    // Should not be idle during animation
    mainClock.advanceTimeBy(50)
    assertThat(state.isIdle).isFalse()
  }

  @Test
  fun isidle_becomes_true_after_settling_when_detents_are_updated() = runComposeUiTest {
    val detent25 = SheetDetent("25") { containerHeight, _ -> containerHeight * 0.25f }
    val detent50 = SheetDetent("50") { containerHeight, _ -> containerHeight * 0.5f }
    val detent75 = SheetDetent("75") { containerHeight, _ -> containerHeight * 0.75f }

    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(100.dp)) {
        state = rememberBottomSheetState(
          initialDetent = detent50,
          detents = listOf(detent25, detent50, detent75),
        )

        UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
          Sheet {
            Box(Modifier.fillMaxWidth().height(100.dp))
          }
        }
      }
    }

    // Update detents while at 50% - removing the current detent
    state.detents = listOf(detent25, detent75)

    // Should eventually settle and become idle
    waitUntil { state.isIdle }
    assertThat(state.isIdle).isTrue()
  }

  @Test
  fun current_and_target_detents_update_correctly_when_dragging() = runComposeUiTest {
    val halfDetent = SheetDetent("half") { _, _ ->
      150.dp
    }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = halfDetent,
        detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
      )

      UnstyledBottomSheet(
        state,
        Modifier.fillMaxSize(),
      ) {
        Sheet {
          Box(
            Modifier
              .testTag("sheet_contents")
              .fillMaxWidth()
              .height(300.dp),
          )
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(halfDetent)
    assertThat(state.targetDetent).isEqualTo(halfDetent)

    mainClock.autoAdvance = false
    val dragDistance = with(density) { 150.dp.toPx() }

    // Start dragging upward
    onNodeWithTag("sheet_contents").performTouchInput {
      down(center)
    }
    mainClock.advanceTimeBy(50)

    onNodeWithTag("sheet_contents").performTouchInput {
      moveTo(center.copy(y = center.y - dragDistance))
    }
    mainClock.advanceTimeBy(50)

    // During drag, target should change to FullyExpanded, current stays at half
    assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
    assertThat(state.currentDetent).isEqualTo(halfDetent)

    // Release
    onNodeWithTag("sheet_contents").performTouchInput {
      up()
    }

    mainClock.autoAdvance = true
    waitForIdle()

    // After settling, both should be FullyExpanded
    assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
    assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
  }

  @Test
  fun sheet_matches_container_size_when_using_container_based_detent() = runComposeUiTest {
    val containerDetent = SheetDetent("container") { containerHeight, _ ->
      containerHeight
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(300.dp)) {
        state = rememberBottomSheetState(
          initialDetent = containerDetent,
          detents = listOf(containerDetent),
        )

        UnstyledBottomSheet(
          state,
          Modifier.testTag("sheet"),
        ) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxSize(),
            )
          }
        }
      }
    }

    waitForIdle()

    assertThat(state.offset).isCloseTo(
      with(density) {
        300.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    onNodeWithTag("sheet").assertHeightIsEqualTo(300.dp)
  }

  @Test
  fun sheet_matches_sheet_content_height_when_using_content_sized_detent() = runComposeUiTest {
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = contentDetent,
        detents = listOf(contentDetent),
      )

      UnstyledBottomSheet(
        state,
        Modifier.testTag("sheet"),
      ) {
        Sheet {
          Box(
            Modifier
              .testTag("sheet_contents")
              .fillMaxWidth()
              .height(200.dp),
          )
        }
      }
    }

    waitForIdle()

    assertThat(state.offset).isCloseTo(
      with(density) {
        200.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    onNodeWithTag("sheet").assertHeightIsEqualTo(200.dp)
  }

  @Test
  fun sheet_measurement_counter_baseline() = runComposeUiTest {
    class Counters {
      var measureCalls = 0
      var maxIntrinsicHeightCalls = 0
      var detentHeightCalls = 0

      fun reset() {
        measureCalls = 0
        maxIntrinsicHeightCalls = 0
        detentHeightCalls = 0
      }
    }

    val counters = Counters()
    var contentHeight by mutableStateOf(200.dp)
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      counters.detentHeightCalls++
      sheetHeight
    }

    setContent {
      Box(Modifier.requiredSize(400.dp)) {
        val state = rememberBottomSheetState(
          initialDetent = contentDetent,
          detents = listOf(contentDetent),
        )

        UnstyledBottomSheet(
          state,
          Modifier.testTag("sheet"),
        ) {
          Sheet {
            Layout(
              modifier = Modifier.testTag("sheet_contents"),
              content = {},
              measurePolicy = object : MeasurePolicy {
                override fun MeasureScope.measure(
                  measurables: List<Measurable>,
                  constraints: Constraints,
                ): MeasureResult {
                  counters.measureCalls++
                  return layout(
                    width = 1.coerceIn(constraints.minWidth, constraints.maxWidth),
                    height = contentHeight.roundToPx().coerceIn(
                      constraints.minHeight,
                      constraints.maxHeight,
                    ),
                  ) {
                  }
                }

                override fun IntrinsicMeasureScope.maxIntrinsicHeight(
                  measurables: List<IntrinsicMeasurable>,
                  width: Int,
                ): Int {
                  counters.maxIntrinsicHeightCalls++
                  return contentHeight.roundToPx()
                }
              },
            )
          }
        }
      }
    }

    waitForIdle()

    assertThat(counters.measureCalls).isEqualTo(2)
    assertThat(counters.maxIntrinsicHeightCalls).isEqualTo(2)
    assertThat(counters.detentHeightCalls).isEqualTo(9)

    counters.reset()
    contentHeight = 250.dp
    waitForIdle()

    assertThat(counters.measureCalls).isEqualTo(2)
    assertThat(counters.maxIntrinsicHeightCalls).isEqualTo(2)
    assertThat(counters.detentHeightCalls).isEqualTo(13)
  }

  @Test
  fun sheet_uses_fixed_height_when_using_fixed_height_detent() = runComposeUiTest {
    // Fixed height detent - always returns 150.dp regardless of container or content
    val fixedDetent = SheetDetent("fixed") { _, _ ->
      150.dp
    }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = fixedDetent,
        detents = listOf(fixedDetent),
      )

      UnstyledBottomSheet(
        state,
        Modifier.testTag("sheet"),
      ) {
        Sheet {
          Column(Modifier.testTag("sheet_contents")) {
            Box(Modifier.testTag("visible_content").height(150.dp).fillMaxWidth())
            Box(Modifier.testTag("hidden_content").height(100.dp).fillMaxWidth())
          }
        }
      }
    }

    waitForIdle()

    // Sheet should be at exactly 150.dp
    assertThat(state.offset).isCloseTo(
      with(density) {
        150.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun sheet_takes_full_container_size_when_using_content_sized_detent_and_content_is_fillmaxsize() = runComposeUiTest {
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(300.dp)) {
        state = rememberBottomSheetState(
          initialDetent = contentDetent,
          detents = listOf(contentDetent),
        )

        UnstyledBottomSheet(
          state,
          Modifier.testTag("sheet"),
        ) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxSize(),
            )
          }
        }
      }
    }

    waitForIdle()

    assertThat(state.offset).isCloseTo(
      with(density) {
        300.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    onNodeWithTag("sheet").assertHeightIsEqualTo(300.dp)
  }

  @Test
  fun offset_matches_sheet_content_height_when_sheet_contents_grow() = runComposeUiTest {
    var contentSize by mutableStateOf(40.dp)

    setContent {
      val state = rememberBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
      )

      UnstyledBottomSheet(
        state,
        Modifier.fillMaxSize(),
      ) {
        Sheet(Modifier.testTag("sheet")) {
          Box(Modifier.testTag("sheet_contents").size(contentSize))
        }
      }
    }

    mainClock.advanceTimeBy(50)
    contentSize = 150.dp
    mainClock.advanceTimeByFrame()

    onNodeWithTag("sheet").assertHeightIsEqualTo(150.dp)
  }

  @Test
  fun offset_matches_sheet_content_height_when_sheet_contents_shrink() = runComposeUiTest {
    var contentHeight by mutableStateOf(200.dp)
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
      )

      UnstyledBottomSheet(
        state,
        Modifier.testTag("sheet"),
      ) {
        Sheet {
          Box(
            Modifier
              .testTag("sheet_contents")
              .fillMaxWidth()
              .height(contentHeight),
          )
        }
      }
    }

    // Shrink content
    contentHeight = 100.dp

    onNodeWithTag("sheet").assertHeightIsEqualTo(100.dp)
  }

  @Test
  fun sheet_content_keeps_its_height_when_dragged_toward_hidden() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(800.dp)) {
        state = rememberBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state,
          Modifier.testTag("sheet"),
        ) {
          Sheet(Modifier.testTag("panel")) {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxWidth()
                .height(400.dp),
            )
          }
        }
      }
    }

    waitForIdle()
    onNodeWithTag("panel").assertHeightIsEqualTo(400.dp)

    mainClock.autoAdvance = false
    val dragDistance = with(density) { 200.dp.toPx() }

    onNodeWithTag("sheet").performTouchInput {
      down(center)
      moveTo(center.copy(y = center.y + dragDistance))
    }
    mainClock.advanceTimeByFrame()

    onNodeWithTag("panel").assertHeightIsEqualTo(400.dp)

    onNodeWithTag("sheet").performTouchInput {
      up()
    }
    mainClock.autoAdvance = true
  }

  @Test
  fun offset_matches_detent_fixed_height_when_sheet_contents_changes() = runComposeUiTest {
    // Fixed height detent - always 100.dp
    val fixedDetent = SheetDetent("fixed100") { _, _ ->
      100.dp
    }

    var containerSize by mutableStateOf(200.dp)
    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(containerSize)) {
        state = rememberBottomSheetState(
          initialDetent = fixedDetent,
          detents = listOf(fixedDetent),
        )

        UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxWidth()
                .height(150.dp),
            )
          }
        }
      }
    }

    // Change container size to 400.dp
    containerSize = 400.dp
    state.invalidateDetents()
    mainClock.advanceTimeByFrame()
    waitForIdle()

    // Even though container changed from 200.dp to 400.dp,
    // fixed detent should still be at 100.dp (not affected by container size)
    assertThat(state.offset).isCloseTo(
      with(density) {
        100.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun dynamic_content_sizing_sheet_takes_full_container_size_when_using_content_sized_detent_and_content_is_fillmaxsize() = runComposeUiTest {
    // Content-sized detent that uses the sheet's content height
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(400.dp)) {
        state = rememberBottomSheetState(
          initialDetent = contentDetent,
          detents = listOf(contentDetent),
        )

        UnstyledBottomSheet(
          state,
          Modifier.testTag("sheet"),
        ) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxSize()
                .background(Color.White),
            )
          }
        }
      }
    }

    waitForIdle()

    // When content uses fillMaxSize(), it should match the container height (400.dp)
    assertThat(state.offset).isCloseTo(
      with(density) {
        400.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
    onNodeWithTag("sheet").assertHeightIsEqualTo(400.dp)
  }

  @Test
  fun current_and_target_detents_update_correctly_when_sheet_animates_to_new_position() = runComposeUiTest {
    lateinit var state: BottomSheetState
    lateinit var scope: CoroutineScope
    val settleDuration = 5000L

    setContent {
      scope = rememberCoroutineScope()
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        animationSpec = tween(settleDuration.toInt()),
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
      )
      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.testTag("sheet_contents").size(40.dp))
        }
      }
    }

    // sheet starting moving towards at FullyExpanded
    scope.launch {
      state.animateTo(SheetDetent.FullyExpanded)
    }
    mainClock.advanceTimeBy(1000L)

    // During animation, target should be FullyExpanded
    assertThat(state.targetDetent).isEqualTo(SheetDetent.FullyExpanded)
  }

  @Test
  fun waits_for_anchors_when_animating_before_sheet_is_mounted() = runComposeUiTest {
    lateinit var state: BottomSheetState
    lateinit var scope: CoroutineScope
    var showSheet by mutableStateOf(false)
    var animationCompleted = false

    setContent {
      scope = rememberCoroutineScope()
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
      )

      if (showSheet) {
        Box(Modifier.requiredSize(600.dp)) {
          UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
            Sheet {
              Box(Modifier.testTag("sheet_contents").size(600.dp))
            }
          }
        }
      }
    }

    scope.launch {
      state.animateTo(SheetDetent.FullyExpanded)
      animationCompleted = true
    }
    mainClock.advanceTimeByFrame()

    assertThat(animationCompleted).isFalse()
    assertThat(state.currentDetent).isEqualTo(SheetDetent.Hidden)

    showSheet = true
    waitForIdle()

    assertThat(animationCompleted).isTrue()
    assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
    assertThat(state.offset).isCloseTo(
      with(density) {
        600.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun detent_updates_when_invalidatedetents_is_called_on_dynamic_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState
    var detentHeight by mutableStateOf(50.dp)
    val dynamicDetent = SheetDetent("dynamic") { _, _ ->
      detentHeight
    }
    setContent {
      state = rememberBottomSheetState(
        initialDetent = dynamicDetent,
        detents = listOf(dynamicDetent),
      )
      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Column(Modifier.testTag("sheet_contents").size(100.dp)) {
            BasicText("Top")
            Spacer(Modifier.weight(1f))
            BasicText("Bottom")
          }
        }
      }
    }
    detentHeight += 50.dp
    state.invalidateDetents()
    onNodeWithText("Bottom").assertIsDisplayed()
  }

  @Test
  fun target_detent_stays_the_same_when_invalidating_detents_mid_drag() = runComposeUiTest {
    val halfDetent = SheetDetent("half") { containerHeight, _ ->
      containerHeight * 0.5f
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(400.dp)) {
        state = rememberBottomSheetState(
          initialDetent = halfDetent,
          detents = listOf(SheetDetent.Hidden, halfDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state,
          Modifier.testTag("sheet"),
        ) {
          Sheet {
            Box(
              Modifier
                .testTag("sheet_contents")
                .fillMaxWidth()
                .height(100.dp),
            )
          }
        }
      }
    }

    mainClock.autoAdvance = false

    // Start manual drag gesture
    onNodeWithTag("sheet").performTouchInput {
      down(bottomCenter)
    }

    mainClock.advanceTimeBy(50)

    // Move up to trigger upward gesture
    onNodeWithTag("sheet").performTouchInput {
      moveTo(center.copy(y = center.y - 300f))
    }

    mainClock.advanceTimeBy(100)

    // Capture target detent before invalidation
    val originalDetent = state.targetDetent

    // Invalidate detents mid-drag
    state.invalidateDetents()
    mainClock.advanceTimeByFrame()

    // Target detent should remain the same after invalidation
    assertThat(state.targetDetent).isEqualTo(originalDetent)
  }

  @Test
  fun sheet_moves_with_content_when_content_changes_at_content_based_detent() = runComposeUiTest {
    var contentHeight by mutableStateOf(100.dp)

    // Content-based detent that returns the content height
    val contentDetent = SheetDetent("content") { _, sheetHeight ->
      sheetHeight
    }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = contentDetent,
        detents = listOf(contentDetent),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(
            Modifier
              .testTag("sheet_contents")
              .fillMaxWidth()
              .height(contentHeight),
          )
        }
      }
    }

    // Grow content
    contentHeight = 200.dp
    state.invalidateDetents()
    mainClock.advanceTimeByFrame()
    waitForIdle()

    // Offset should move with content to new height
    assertThat(state.offset).isCloseTo(
      with(density) {
        200.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun state_detents_updates_when_setting_new_detents_list() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.detents).isEqualTo(listOf(detent100, detent200))

    state.detents = listOf(detent100, detent200, detent300)
    waitForIdle()

    assertThat(state.detents).isEqualTo(listOf(detent100, detent200, detent300))
  }

  @Test
  fun sheet_moves_to_closest_detent_upward_when_current_detent_removed_while_moving_up() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }
    val detent400 = SheetDetent("400") { _, _ -> 400.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200, detent300, detent400),
        animationSpec = tween(1),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitUntil { state.isIdle }

    // Start moving up toward detent200
    state.anchoredDraggableState.dispatchRawDelta(
      delta = (
        state.anchoredDraggableState.anchors.positionOf(detent200) -
          state.anchoredDraggableState.anchors.positionOf(detent100)
        ) * 0.75f,
    )

    // Verify we're actually moving toward detent200
    assertThat(state.targetDetent).isEqualTo(detent200)
    assertThat(state.isIdle).isFalse()

    // Remove detent200 while animating toward it
    state.detents = listOf(detent100, detent300, detent400)

    // Should move to detent300 (closest upward)
    assertThat(state.targetDetent).isEqualTo(detent300)
    mainClock.advanceTimeBy(50)
    waitUntil { state.isIdle }
    assertThat(state.currentDetent).isEqualTo(detent300)
  }

  @Test
  fun sheet_moves_to_closest_detent_downward_when_current_detent_removed_while_moving_down() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }
    val detent400 = SheetDetent("400") { _, _ -> 400.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent400,
        detents = listOf(detent100, detent200, detent300, detent400),
        animationSpec = tween(1),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(500.dp))
        }
      }
    }

    waitUntil { state.isIdle }

    // Start moving down toward detent300
    state.anchoredDraggableState.dispatchRawDelta(
      delta = (
        state.anchoredDraggableState.anchors.positionOf(detent300) -
          state.anchoredDraggableState.anchors.positionOf(detent400)
        ) * 0.75f,
    )

    // Verify we're actually moving toward detent300
    assertThat(state.targetDetent).isEqualTo(detent300)
    assertThat(state.isIdle).isFalse()

    // Remove detent300 while animating toward it
    state.detents = listOf(detent100, detent200, detent400)

    // Should move to detent200 (closest downward)
    assertThat(state.targetDetent).isEqualTo(detent200)
    mainClock.advanceTimeBy(50)
    waitUntil { state.isIdle }
    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun throws_exception_when_setting_detents_to_empty_list() {
    assertFailure {
      runComposeUiTest {
        lateinit var state: BottomSheetState

        setContent {
          state = rememberBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
          )

          UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
            Sheet {
              Box(Modifier.fillMaxWidth().height(400.dp))
            }
          }
        }

        state.detents = emptyList()
      }
    }.isInstanceOf<IllegalStateException>()
  }

  @Test
  fun sheet_moves_to_first_detent_when_no_detent_found_in_search_direction() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }
    val detent400 = SheetDetent("400") { _, _ -> 400.dp }
    val detent500 = SheetDetent("500") { _, _ -> 500.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent400,
        detents = listOf(detent100, detent200, detent300, detent400, detent500),
        animationSpec = tween(1),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(500.dp))
        }
      }
    }

    waitUntil { state.isIdle }

    // Start moving down toward detent300
    state.anchoredDraggableState.dispatchRawDelta(
      delta = (
        state.anchoredDraggableState.anchors.positionOf(detent300) -
          state.anchoredDraggableState.anchors.positionOf(detent400)
        ) * 0.75f,
    )

    // Verify we're actually moving toward detent300
    assertThat(state.targetDetent).isEqualTo(detent300)
    assertThat(state.isIdle).isFalse()

    // Remove all detents below detent400, leaving only detent400 and detent500
    // No valid detent in downward direction - should fall back to first detent
    state.detents = listOf(detent400, detent500)

    // Should move to first detent (detent400)
    assertThat(state.targetDetent).isEqualTo(detent400)
    mainClock.advanceTimeBy(50)
    waitUntil { state.isIdle }
    assertThat(state.currentDetent).isEqualTo(detent400)
  }

  @Test
  fun detents_update_successfully_when_new_list_has_same_length_but_different_detents() = runComposeUiTest {
    val Header = SheetDetent(identifier = "Header") { _, _ -> 150.dp }
    val Middle = SheetDetent(identifier = "Middle") { _, _ -> 500.dp }
    val Tall = SheetDetent(identifier = "Tall") { _, _ -> 1000.dp }

    lateinit var sheetState: BottomSheetState
    setContent {
      sheetState = rememberBottomSheetState(
        initialDetent = Header,
        detents = listOf(Header, Middle),
      )

      UnstyledBottomSheet(
        state = sheetState,
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White),
      ) {
        Sheet {
          Column(modifier = Modifier.fillMaxWidth().height(1200.dp)) {
            BasicText("Test")
          }
        }
      }
    }

    waitForIdle()

    // Update detents to same length but with different detents
    sheetState.detents = listOf(Header, Tall)

    waitForIdle()

    // If we reach here without crashing, the test passes
    assertThat(sheetState.detents.size).isEqualTo(2)
    assertThat(sheetState.detents).contains(Header)
    assertThat(sheetState.detents).contains(Tall)
  }

  @Test
  fun drag_indication_has_expand_action_when_sheet_can_be_expanded() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    onNodeWithTag("drag_indication")
      .assert(hasExpandAction())
  }

  @Test
  fun drag_indication_has_collapse_action_when_sheet_is_expanded() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    onNodeWithTag("drag_indication")
      .assert(hasCollapseAction())
  }

  @Test
  fun drag_indication_has_no_expand_action_when_sheet_is_at_topmost_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.FullyExpanded,
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    onNodeWithTag("drag_indication")
      .assert(hasNoExpandAction())
  }

  @Test
  fun drag_indication_has_no_collapse_action_when_sheet_is_at_bottommost_detent() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    onNodeWithTag("drag_indication")
      .assert(hasNoCollapseAction())
  }

  @Test
  fun scrolls_content_when_only_one_detent() = runComposeUiTest {
    val customDetent = SheetDetent("custom") { _, _ -> 70.dp }
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = customDetent,
        detents = listOf(customDetent), // Only ONE detent
      )

      UnstyledBottomSheet(
        state = state,
        modifier = Modifier.testTag("sheet"),
      ) {
        Sheet {
          Column(
            Modifier
              .testTag("scrollable_content")
              .verticalScroll(rememberScrollState()),
          ) {
            repeat(10) { index ->
              Box(
                Modifier
                  .testTag("item_$index")
                  .size(width = 100.dp, height = 100.dp),
              )
            }
          }
        }
      }
    }

    waitUntilExactlyOneExists(hasTestTag("sheet"))

    // Sheet should be at fixed detent
    val initialOffset = state.offset

    // Scroll to last item
    onNodeWithTag("item_9").performScrollTo()

    // Sheet offset should NOT change - only content scrolls
    assertThat(state.offset).isEqualTo(initialOffset)
  }

  @Test
  fun supports_lazycolumn_content() = runComposeUiTest {
    val customDetent = SheetDetent("custom") { _, _ -> 280.dp }
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = customDetent,
        detents = listOf(customDetent),
      )

      UnstyledBottomSheet(
        state = state,
      ) {
        Sheet {
          LazyColumn(
            Modifier
              .testTag("lazy_content")
              .fillMaxWidth(),
          ) {
            items(10) { index ->
              BasicText(
                text = "item_$index",
                modifier = Modifier
                  .testTag("item_$index")
                  .fillMaxWidth()
                  .height(56.dp),
              )
            }
          }
        }
      }
    }

    waitUntilExactlyOneExists(hasTestTag("lazy_content"))
    onNodeWithTag("item_0").assertIsDisplayed()

    val initialOffset = state.offset

    onNodeWithTag("lazy_content").performScrollToIndex(9)

    onNodeWithTag("item_9").assertIsDisplayed()
    assertThat(state.offset).isEqualTo(initialOffset)
  }

  @Test
  fun fullyexpanded_lazy_content_accounts_for_sheet_top_padding() = runComposeUiTest {
    val topPadding = 48.dp
    lateinit var state: BottomSheetState

    setContent {
      Box(
        Modifier
          .testTag("root")
          .requiredSize(width = 400.dp, height = 600.dp),
      ) {
        state = rememberBottomSheetState(initialDetent = SheetDetent.FullyExpanded)
        UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
          Sheet(
            modifier = Modifier
              .testTag("sheet")
              .padding(top = topPadding),
          ) {
            LazyColumn(
              modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            ) {
              items(80) { index ->
                BasicText(
                  text = "index = $index",
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                )
              }
            }
          }
        }
      }
    }

    waitForIdle()

    val containerHeight = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot.height
    assertThat(state.offset).isCloseTo(
      containerHeight,
      with(density) { DensityTolerance.toPx() },
    )
  }

  @Test
  fun expands_to_fullyexpanded_with_weighted_lazycolumn_content() = runComposeUiTest {
    val miniDetent = SheetDetent("mini") { _, _ -> 74.dp }
    lateinit var state: BottomSheetState

    setContent {
      Box(Modifier.requiredSize(400.dp)) {
        state = rememberBottomSheetState(
          initialDetent = miniDetent,
          detents = listOf(miniDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(state = state, modifier = Modifier.fillMaxSize()) {
          Sheet {
            Column(Modifier.fillMaxWidth()) {
              Box(
                Modifier
                  .testTag("header")
                  .fillMaxWidth()
                  .height(74.dp),
              )

              LazyColumn(
                Modifier
                  .testTag("lazy_content")
                  .fillMaxWidth()
                  .weight(1f),
              ) {
                items(10) { index ->
                  BasicText(
                    text = "item_$index",
                    modifier = Modifier
                      .testTag("item_$index")
                      .fillMaxWidth()
                      .height(56.dp),
                  )
                }
              }
            }
          }
        }
      }
    }

    waitUntilExactlyOneExists(hasTestTag("header"))
    waitForIdle()

    val initialOffset = state.offset
    assertThat(initialOffset).isCloseTo(
      with(density) {
        74.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )

    state.targetDetent = SheetDetent.FullyExpanded
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
    assertThat(state.offset).isGreaterThan(initialOffset + with(density) { 200.dp.toPx() })
  }

  @Test
  fun does_not_change_content_height_while_dragging_between_detents() = runComposeUiTest {
    val miniDetent = SheetDetent("mini") { _, _ -> 74.dp }
    lateinit var state: BottomSheetState
    var measuredContentHeight = 0

    setContent {
      Box(Modifier.requiredSize(400.dp)) {
        state = rememberBottomSheetState(
          initialDetent = miniDetent,
          detents = listOf(miniDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.fillMaxSize().testTag("sheet"),
        ) {
          Sheet {
            Column(
              Modifier
                .fillMaxWidth()
                .onSizeChanged { measuredContentHeight = it.height },
            ) {
              Box(
                Modifier
                  .testTag("header")
                  .fillMaxWidth()
                  .height(74.dp),
              )

              LazyColumn(
                Modifier
                  .fillMaxWidth()
                  .weight(1f),
              ) {
                items(10) { index ->
                  BasicText(
                    text = "item_$index",
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(56.dp),
                  )
                }
              }
            }
          }
        }
      }
    }

    waitUntilExactlyOneExists(hasTestTag("header"))
    waitForIdle()

    val expandedContentHeight = measuredContentHeight
    assertThat(expandedContentHeight.toFloat()).isCloseTo(
      with(density) {
        400.dp.toPx()
      },
      with(density) { DensityTolerance.toPx() },
    )

    mainClock.autoAdvance = false
    onNodeWithTag("sheet").performTouchInput {
      down(center)
      moveTo(center.copy(y = center.y - with(density) { 150.dp.toPx() }))
    }
    mainClock.advanceTimeByFrame()

    assertThat(measuredContentHeight).isEqualTo(expandedContentHeight)

    onNodeWithTag("sheet").performTouchInput {
      up()
    }
    mainClock.autoAdvance = true
  }

  @Test
  fun expands_sheet_before_scrolling_content_when_swiping_up_with_multiple_detents() = runComposeUiTest {
    val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
      containerHeight * 0.5f
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(
        Modifier
          .fillMaxSize()
          .background(Color.Black),
      ) {
        state = rememberBottomSheetState(
          initialDetent = halfExpandedDetent,
          detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.fillMaxSize(),
        ) {
          Sheet(
            Modifier
              .background(Color.White)
              .testTag("sheet")
              .verticalScroll(rememberScrollState()),
          ) {
            Column {
              repeat(5) { index ->
                BasicText(
                  text = "item_$index",
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
    }

    // Swipe up on the sheet - this should move the sheet to FullyExpanded first
    onNodeWithTag("sheet").performTouchInput {
      swipeUp()
    }
    awaitIdle()
    assertThat(state.currentDetent).isEqualTo(SheetDetent.FullyExpanded)
  }

  @Test
  fun bottom_of_scrollable_sheet_is_not_clipped() = runComposeUiTest {
    val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
      containerHeight * 0.5f
    }

    lateinit var state: BottomSheetState

    setContent {
      Box(
        Modifier
          .fillMaxSize()
          .background(Color.Black),
      ) {
        state = rememberBottomSheetState(
          initialDetent = halfExpandedDetent,
          detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.fillMaxSize(),
        ) {
          Sheet(
            Modifier
              .background(Color.White)
              .testTag("sheet")
              .verticalScroll(rememberScrollState()),
          ) {
            Column {
              repeat(5) { index ->
                BasicText(
                  text = "item_$index",
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
    }

    // Swipe up on the sheet - this should move the sheet to FullyExpanded first
    onNodeWithTag("sheet").performTouchInput {
      swipeUp()
    }
    awaitIdle()
    onNodeWithTag("item_4").assertIsDisplayed()
  }

  @Test
  fun scrollable_column_expands_to_visible_sheet_height() = runComposeUiTest {
    val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
      containerHeight * 0.5f
    }

    setContent {
      Box(
        Modifier
          .requiredSize(400.dp)
          .testTag("root"),
      ) {
        val state = rememberBottomSheetState(
          initialDetent = halfExpandedDetent,
          detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.fillMaxSize().testTag("sheet"),
        ) {
          Sheet(Modifier.testTag("panel")) {
            Column(
              Modifier
                .testTag("scrollable_content")
                .verticalScroll(rememberScrollState()),
            ) {
              repeat(6) { index ->
                BasicText(
                  text = "item_$index",
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                )
              }
            }
          }
        }
      }
    }

    waitUntilExactlyOneExists(hasTestTag("sheet"))

    val rootBounds = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
    val panelBounds = onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot
    val scrollableContentBounds = onNodeWithTag(
      "scrollable_content",
    ).fetchSemanticsNode().boundsInRoot
    val visibleSheetHeight = rootBounds.bottom - panelBounds.top

    assertThat(visibleSheetHeight).isEqualTo(rootBounds.height / 2f)
    assertThat(scrollableContentBounds.height).isEqualTo(visibleSheetHeight)
  }

  @Test
  fun scrollable_column_without_fixed_height_does_not_clip_last_item() = runComposeUiTest {
    val expanded = SheetDetent(identifier = "peek3") { containerHeight, _ ->
      containerHeight * 0.7f
    }

    setContent {
      Box(
        Modifier
          .requiredSize(400.dp)
          .testTag("root"),
      ) {
        val sheetState = rememberBottomSheetState(
          initialDetent = expanded,
          detents = listOf(
            SheetDetent.Hidden,
            expanded,
          ),
        )

        UnstyledBottomSheet(
          state = sheetState,
        ) {
          Sheet(Modifier.background(Color.White)) {
            Column(
              Modifier
                .testTag("scrollable_content")
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
    }

    waitUntilExactlyOneExists(hasTestTag("root"))

    onNodeWithTag("item_9").performScrollTo()

    val rootBounds = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
    val lastItemBounds = onNodeWithTag("item_9").fetchSemanticsNode().boundsInRoot

    assertThat(lastItemBounds.bottom <= rootBounds.bottom).isTrue()
  }

  @Test
  fun fully_expanded_sheet_with_tall_content_anchors_to_container_top() = runComposeUiTest {
    lateinit var state: BottomSheetState

    setContent {
      Box(
        Modifier
          .requiredSize(400.dp)
          .testTag("root"),
      ) {
        state = rememberBottomSheetState(
          initialDetent = SheetDetent.FullyExpanded,
          detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
        )

        UnstyledBottomSheet(
          state = state,
          modifier = Modifier.testTag("sheet"),
        ) {
          Sheet(Modifier.testTag("panel")) {
            Column(
              Modifier
                .testTag("scrollable_content")
                .verticalScroll(rememberScrollState()),
            ) {
              repeat(6) { index ->
                BasicText(
                  text = "item_$index",
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                )
              }
            }
          }
        }
      }
    }

    waitUntilExactlyOneExists(hasTestTag("sheet"))

    val rootBounds = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
    val panelBounds = onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot

    assertThat(panelBounds.top).isEqualTo(rootBounds.top)
    assertThat(state.offset).isEqualTo(rootBounds.height)
  }

  @Test
  fun fully_expanded_sheet_with_short_lazycolumn_content_anchors_to_container_bottom() =
    runComposeUiTest {
      lateinit var state: BottomSheetState

      setContent {
        Box(
          Modifier
            .requiredSize(400.dp)
            .testTag("root"),
        ) {
          state = rememberBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
            detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          )

          UnstyledBottomSheet(
            state = state,
            modifier = Modifier.fillMaxSize(),
          ) {
            Sheet(Modifier.testTag("panel")) {
              LazyColumn(Modifier.fillMaxWidth()) {
                items(3) {
                  Box(
                    Modifier
                      .fillMaxWidth()
                      .height(48.dp),
                  )
                }
              }
            }
          }
        }
      }

      waitUntilExactlyOneExists(hasTestTag("panel"))
      waitForIdle()

      val rootBounds = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
      val panelBounds = onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot

      assertThat(panelBounds.bottom).isCloseTo(
        rootBounds.bottom,
        with(density) { DensityTolerance.toPx() },
      )
      assertThat(state.offset).isCloseTo(
        panelBounds.height,
        with(density) { DensityTolerance.toPx() },
      )
    }

  @Test
  fun fully_expanded_sheet_with_short_top_padded_content_does_not_clip_bottom_content() =
    runComposeUiTest {
      setContent {
        Box(
          Modifier
            .requiredSize(400.dp)
            .testTag("root"),
        ) {
          val state = rememberBottomSheetState(
            initialDetent = SheetDetent.FullyExpanded,
            detents = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
          )

          UnstyledBottomSheet(
            state = state,
            modifier = Modifier.fillMaxSize(),
          ) {
            Sheet(
              Modifier
                .padding(top = 64.dp)
                .testTag("panel"),
            ) {
              Column(
                Modifier
                  .fillMaxWidth()
                  .height(260.dp)
                  .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp),
              ) {
                repeat(3) { index ->
                  Box(
                    Modifier
                      .fillMaxWidth()
                      .height(48.dp)
                      .testTag("item_$index"),
                  )
                  if (index < 2) {
                    Spacer(Modifier.height(10.dp))
                  }
                }
              }
            }
          }
        }
      }

      waitUntilExactlyOneExists(hasTestTag("panel"))
      waitForIdle()

      val rootBounds = onNodeWithTag("root").fetchSemanticsNode().boundsInRoot
      val panelBounds = onNodeWithTag("panel").fetchSemanticsNode().boundsInRoot
      val lastItemBounds = onNodeWithTag("item_2").fetchSemanticsNode().boundsInRoot

      assertThat(panelBounds.bottom).isCloseTo(
        rootBounds.bottom,
        with(density) { DensityTolerance.toPx() },
      )
      assertThat(lastItemBounds.bottom <= rootBounds.bottom).isTrue()
    }

  @Test
  fun detent_change_is_blocked_when_confirmdetentchange_returns_false_for_targetdetent() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { false },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()
    state.targetDetent = detent200
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent100)
  }

  @Test
  fun detent_change_proceeds_when_confirmdetentchange_returns_true() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { true },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()
    state.targetDetent = detent200
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun sheet_returns_to_original_detent_when_drag_is_blocked_by_confirmdetentchange() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { newDetent -> newDetent != detent200 },
      )

      UnstyledBottomSheet(
        state,
        Modifier.testTag("sheet"),
      ) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()
    val initialOffset = state.offset

    // Try to drag upward toward detent200
    onNodeWithTag("sheet").performTouchInput {
      swipeUp()
    }
    waitForIdle()

    // Should return to original detent
    assertThat(state.currentDetent).isEqualTo(detent100)
    assertThat(state.offset).isEqualTo(initialOffset)
  }

  @Test
  fun programmatic_detent_change_is_blocked_when_confirmdetentchange_returns_false_for_animateto() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    lateinit var state: BottomSheetState
    lateinit var scope: CoroutineScope

    setContent {
      scope = rememberCoroutineScope()
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { newDetent -> newDetent != detent200 },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    scope.launch {
      state.animateTo(detent200)
    }
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent100)
  }

  @Test
  fun sheet_stays_at_current_detent_when_confirmdetentchange_blocks_all_other_detents() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent200,
        detents = listOf(detent100, detent200, detent300),
        confirmDetentChange = { newDetent -> newDetent == detent200 },
      )

      UnstyledBottomSheet(
        state,
        Modifier.testTag("sheet"),
      ) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()

    // Try to swipe up
    onNodeWithTag("sheet").performTouchInput { swipeUp() }
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent200)

    // Try to swipe down
    onNodeWithTag("sheet").performTouchInput { swipeDown() }
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun confirmdetentchange_is_called_with_correct_detent_when_dragging_upward() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    var receivedDetent: SheetDetent? = null
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { newDetent ->
          receivedDetent = newDetent
          true
        },
      )

      UnstyledBottomSheet(
        state,
        Modifier.testTag("sheet"),
      ) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    onNodeWithTag("sheet").performTouchInput { swipeUp() }
    waitForIdle()

    assertThat(receivedDetent).isEqualTo(detent200)
  }

  @Test
  fun confirmdetentchange_is_called_with_correct_detent_when_using_targetdetent_setter() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    var receivedDetent: SheetDetent? = null
    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { newDetent ->
          receivedDetent = newDetent
          true
        },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()
    state.targetDetent = detent200
    waitForIdle()

    assertThat(receivedDetent).isEqualTo(detent200)
  }

  @Test
  fun confirmdetentchange_is_called_with_correct_detent_when_using_animateto() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    var receivedDetent: SheetDetent? = null
    lateinit var state: BottomSheetState
    lateinit var scope: CoroutineScope

    setContent {
      scope = rememberCoroutineScope()
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
        confirmDetentChange = { newDetent ->
          receivedDetent = newDetent
          true
        },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          Box(Modifier.fillMaxWidth().height(300.dp))
        }
      }
    }

    waitForIdle()

    scope.launch {
      state.animateTo(detent200)
    }
    waitForIdle()

    assertThat(receivedDetent).isEqualTo(detent200)
  }

  @Test
  fun sheet_moves_to_next_detent_up_when_drag_indication_clicked_from_bottom() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200, detent300),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent100)

    onNodeWithTag("drag_indication").performClick()
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun sheet_moves_to_next_detent_down_when_drag_indication_clicked_from_top() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent300,
        detents = listOf(detent100, detent200, detent300),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent300)

    onNodeWithTag("drag_indication").performClick()
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun drag_indication_is_disabled_when_only_one_detent() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()
    onNodeWithTag("drag_indication").assertIsNotEnabled()
  }

  @Test
  fun sheet_cycles_through_all_detents_upward_when_clicking_drag_indication_multiple_times() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200, detent300),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent100)

    // Click to move to detent200
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent200)

    // Click to move to detent300
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent300)
  }

  @Test
  fun sheet_reverses_direction_when_reaching_top_detent() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent200,
        detents = listOf(detent100, detent200, detent300),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()

    // Click to move up to detent300
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent300)

    // Click again - should reverse and go down to detent200
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun sheet_reverses_direction_when_reaching_bottom_detent() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent200,
        detents = listOf(detent100, detent200, detent300),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()

    // First move to top
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()

    // Then start going down
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent200)

    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent100)

    // Click again - should reverse and go up to detent200
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent200)
  }

  @Test
  fun drag_indication_respects_confirmdetentchange_when_moving_to_blocked_detent() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200, detent300),
        confirmDetentChange = { newDetent ->
          newDetent != detent200
        },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent100)

    // Try to click - should be blocked from moving to detent200
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent100)
  }

  @Test
  fun drag_indication_stays_at_current_detent_when_next_detent_is_blocked_by_confirmdetentchange() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }
    val detent300 = SheetDetent("300") { _, _ -> 300.dp }
    val detent400 = SheetDetent("400") { _, _ -> 400.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200, detent300, detent400),
        confirmDetentChange = { newDetent ->
          // Block detent200, allow all others
          newDetent != detent200
        },
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(500.dp))
        }
      }
    }

    waitForIdle()
    assertThat(state.currentDetent).isEqualTo(detent100)

    // Click drag indication - detent200 is blocked, so sheet stays at detent100
    onNodeWithTag("drag_indication").performClick()
    waitForIdle()

    assertThat(state.currentDetent).isEqualTo(detent100)
  }

  @Test
  fun drag_indication_has_button_role_when_multiple_detents_exist() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }
    val detent200 = SheetDetent("200") { _, _ -> 200.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100, detent200),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()

    // Verify drag indication has button role for accessibility
    onNodeWithTag("drag_indication")
      .assertHasClickAction()
      .assertIsEnabled()
  }

  @Test
  fun drag_indication_is_not_enabled_when_only_one_detent_exists() = runComposeUiTest {
    val detent100 = SheetDetent("100") { _, _ -> 100.dp }

    lateinit var state: BottomSheetState

    setContent {
      state = rememberBottomSheetState(
        initialDetent = detent100,
        detents = listOf(detent100),
      )

      UnstyledBottomSheet(state, Modifier.fillMaxSize()) {
        Sheet {
          DragIndication(Modifier.testTag("drag_indication").size(32.dp))
          Box(Modifier.fillMaxWidth().height(400.dp))
        }
      }
    }

    waitForIdle()

    // Verify drag indication is disabled when only one detent
    onNodeWithTag("drag_indication")
      .assertIsNotEnabled()
  }

  // Helper functions for semantic assertions
  private fun hasExpandAction(): SemanticsMatcher {
    return SemanticsMatcher("has expand action") { node ->
      androidx.compose.ui.semantics.SemanticsActions.Expand in node.config
    }
  }

  private fun hasCollapseAction(): SemanticsMatcher {
    return SemanticsMatcher("has collapse action") { node ->
      androidx.compose.ui.semantics.SemanticsActions.Collapse in node.config
    }
  }

  private fun hasNoExpandAction(): SemanticsMatcher {
    return SemanticsMatcher("has no expand action") { node ->
      (androidx.compose.ui.semantics.SemanticsActions.Expand in node.config).not()
    }
  }

  private fun hasNoCollapseAction(): SemanticsMatcher {
    return SemanticsMatcher("has no collapse action") { node ->
      (androidx.compose.ui.semantics.SemanticsActions.Collapse in node.config).not()
    }
  }
}

private object FakeBottomSheetScope : BottomSheetScope
