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

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.KeyInjectionScope
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isCloseTo
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import kotlin.test.Test

class SliderTest {

  @Test
  fun steppedSliderDoesNotSnapPointSixToHalf() {
    val snappedValue = snapValueToTick(
      current = 0.6f,
      tickFractions = stepsToTickFractions(9),
      min = 0f,
      max = 1f,
    )

    assertThat(snappedValue).isCloseTo(0.6f, 0.001f)
  }

  @Test
  fun slotsReceiveSliderState() = runComposeUiTest {
    var trackState: SliderState? = null
    var thumbState: SliderState? = null

    setContent {
      TestSlider(
        value = 0.6f,
        steps = 4,
        track = { state -> trackState = state },
        thumb = { state -> thumbState = state },
      )
    }

    val capturedTrackState = trackState ?: error("track slot was not rendered")
    val capturedThumbState = thumbState ?: error("thumb slot was not rendered")

    assertThat(capturedTrackState.value).isEqualTo(0.6f)
    assertThat(capturedTrackState.fraction).isCloseTo(0.6f, 0.001f)
    assertThat(
      capturedTrackState.tickFractions.asList(),
    ).containsExactly(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)
    assertThat(capturedThumbState.value).isEqualTo(capturedTrackState.value)
  }

  @Test
  fun exposesSliderRangeSemantics() = runComposeUiTest {
    setContent {
      TestSlider(
        value = 0.5f,
        valueRange = 0f..2f,
        steps = 3,
      )
    }

    val rangeInfo = onNodeWithTag("slider")
      .fetchSemanticsNode()
      .config[SemanticsProperties.ProgressBarRangeInfo]

    assertThat(rangeInfo.current).isEqualTo(0.5f)
    assertThat(rangeInfo.range).isEqualTo(0f..2f)
    assertThat(rangeInfo.steps).isEqualTo(3)
  }

  @Test
  fun disabledSliderExposesDisabledSemantics() = runComposeUiTest {
    setContent {
      TestSlider(
        value = 0.5f,
        enabled = false,
      )
    }

    onNodeWithTag("slider").assertIsNotEnabled()
  }

  @Test
  fun setProgressSemanticsUpdatesValueAndCallsFinished() = runComposeUiTest {
    var value by mutableFloatStateOf(0.2f)
    var finishedCalls = 0

    setContent {
      TestSlider(
        value = value,
        onValueChange = { value = it },
        steps = 4,
        onValueChangeFinished = { finishedCalls++ },
      )
    }

    onNodeWithTag("slider").performSemanticsAction(SemanticsActions.SetProgress) { action ->
      action(0.7f)
    }

    assertThat(value).isCloseTo(0.6f, 0.001f)
    assertThat(finishedCalls).isEqualTo(1)
  }

  @Test
  fun arrowKeysAdjustValueByOneStep() = runComposeUiTest {
    var value by mutableFloatStateOf(0.5f)

    setContent {
      TestSlider(
        value = value,
        onValueChange = { value = it },
        steps = 9,
      )
    }

    onNodeWithTag("slider").requestFocus()
    onNodeWithTag("slider").performKeyInput {
      keyPress(Key.DirectionRight)
    }

    assertThat(value).isCloseTo(0.6f, 0.001f)

    onNodeWithTag("slider").performKeyInput {
      keyPress(Key.DirectionLeft)
    }

    assertThat(value).isCloseTo(0.5f, 0.001f)
  }

  @Test
  fun homeAndEndKeysMoveToRangeBounds() = runComposeUiTest {
    var value by mutableFloatStateOf(0.5f)

    setContent {
      TestSlider(
        value = value,
        onValueChange = { value = it },
      )
    }

    onNodeWithTag("slider").requestFocus()
    onNodeWithTag("slider").performKeyInput {
      keyPress(Key.MoveEnd)
    }

    assertThat(value).isEqualTo(1f)

    onNodeWithTag("slider").performKeyInput {
      keyPress(Key.MoveHome)
    }

    assertThat(value).isEqualTo(0f)
  }

  @Test
  fun pageKeysAdjustValueByLargerStepAndCallFinishedOnKeyUp() = runComposeUiTest {
    var value by mutableFloatStateOf(0.5f)
    var finishedCalls = 0

    setContent {
      TestSlider(
        value = value,
        onValueChange = { value = it },
        steps = 9,
        onValueChangeFinished = { finishedCalls++ },
      )
    }

    onNodeWithTag("slider").requestFocus()
    onNodeWithTag("slider").performKeyInput {
      keyPress(Key.PageUp)
    }

    assertThat(value).isEqualTo(1f)
    assertThat(finishedCalls).isEqualTo(1)

    onNodeWithTag("slider").performKeyInput {
      keyPress(Key.PageDown)
    }

    assertThat(value).isEqualTo(0f)
    assertThat(finishedCalls).isEqualTo(2)
  }

  @Test
  fun verticalSliderPlacesLowestValueBelowHighestValue() = runComposeUiTest {
    var value by mutableFloatStateOf(0f)

    setContent {
      TestSlider(
        value = value,
        orientation = Orientation.Vertical,
        thumb = {
          Box(Modifier.size(20.dp).testTag("thumb"))
        },
      )
    }

    val bottomValueThumbTop = onNodeWithTag("thumb", useUnmergedTree = true)
      .fetchSemanticsNode()
      .boundsInRoot
      .top

    value = 1f
    waitForIdle()

    val topValueThumbTop = onNodeWithTag("thumb", useUnmergedTree = true)
      .fetchSemanticsNode()
      .boundsInRoot
      .top

    assertThat(bottomValueThumbTop).isGreaterThan(topValueThumbTop)
  }

  @Test
  fun thumbStartsAtInitialValuePosition() = runComposeUiTest {
    mainClock.autoAdvance = false
    val thumbPositions = mutableStateListOf<Int>()

    setContent {
      TestSlider(
        value = 0.45f,
        thumb = {
          Box(
            Modifier
              .onGloballyPositioned { thumbPositions += it.positionInRoot().x.toInt() }
              .size(20.dp)
              .testTag("thumb"),
          )
        },
      )
    }

    mainClock.advanceTimeByFrame()

    assertThat(thumbPositions.distinct()).containsExactly(81)
    onNodeWithTag("thumb", useUnmergedTree = true).assertLeftPositionInRootIsEqualTo(81.dp)
  }

  @Test
  fun verticalArrowKeysAdjustValueByPhysicalDirectionWhenReverseDirectionIsTrue() =
    runComposeUiTest {
      var value by mutableFloatStateOf(0.5f)

      setContent {
        TestSlider(
          value = value,
          onValueChange = { value = it },
          steps = 9,
          orientation = Orientation.Vertical,
          reverseDirection = true,
        )
      }

      onNodeWithTag("slider").requestFocus()
      onNodeWithTag("slider").performKeyInput {
        keyPress(Key.DirectionUp)
      }

      assertThat(value).isCloseTo(0.6f, 0.001f)

      onNodeWithTag("slider").performKeyInput {
        keyPress(Key.DirectionDown)
      }

      assertThat(value).isCloseTo(0.5f, 0.001f)
    }

  @Test
  fun verticalDragAdjustsValueByPhysicalDirectionWhenReverseDirectionIsTrue() =
    runComposeUiTest {
      var value by mutableFloatStateOf(0.5f)

      setContent {
        TestSlider(
          value = value,
          onValueChange = { value = it },
          orientation = Orientation.Vertical,
          reverseDirection = true,
        )
      }

      onNodeWithTag("slider").performTouchInput {
        down(center)
        moveBy(Offset(x = 0f, y = -20f))
        up()
      }

      assertThat(value).isGreaterThan(0.5f)

      onNodeWithTag("slider").performTouchInput {
        down(center)
        moveBy(Offset(x = 0f, y = 20f))
        up()
      }

      assertThat(value).isLessThan(0.5f)
    }

  private fun KeyInjectionScope.keyPress(key: Key) {
    keyDown(key)
    keyUp(key)
  }

  @Composable
  private fun TestSlider(
    value: Float,
    onValueChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    orientation: Orientation = Orientation.Horizontal,
    reverseDirection: Boolean = false,
    track: @Composable (SliderState) -> Unit = {
      Box(Modifier.fillMaxWidth().height(4.dp))
    },
    thumb: @Composable (SliderState) -> Unit = {
      Box(Modifier.size(20.dp))
    },
  ) {
    UnstyledSlider(
      value = value,
      onValueChange = onValueChange,
      modifier = modifier.width(200.dp).height(40.dp).testTag("slider"),
      enabled = enabled,
      valueRange = valueRange,
      steps = steps,
      onValueChangeFinished = onValueChangeFinished,
      orientation = orientation,
      reverseDirection = reverseDirection,
      track = track,
      thumb = thumb,
    )
  }
}
