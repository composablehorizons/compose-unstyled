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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VectorizedAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilExactlyOneExists
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.test.Test
import androidx.compose.ui.test.runComposeUiTest as runComposeUiTestV1

class DrawerTest {

  @Test
  fun systemUiAppearanceStoresRequestedIconAppearance() {
    val systemUi = SystemUi(
      statusBar = SystemUiAppearance.Light,
      navigationBar = SystemUiAppearance.Dark,
    )

    assertThat(systemUi.statusBar).isEqualTo(SystemUiAppearance.Light)
    assertThat(systemUi.navigationBar).isEqualTo(SystemUiAppearance.Dark)
  }

  @Test
  fun snapPointsRejectDuplicateValues() {
    val result = runCatching {
      DrawerSnapPoints<Boolean> {
        true at DrawerSnapPoint.Zero
        true at DrawerSnapPoint.ContentSize
      }
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun snapPointsRejectEmptyDefinitions() {
    val result = runCatching {
      DrawerSnapPoints<DrawerValue> {}
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun snapPointsRejectMoreThanOneZeroValue() {
    val result = runCatching {
      DrawerSnapPoints<DrawerValue> {
        DrawerValue.Closed at DrawerSnapPoint.Zero
        DrawerValue.Peek at DrawerSnapPoint.Zero
      }
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun stateRejectsInitialValuesMissingFromSnapPoints() {
    val snapPoints = DrawerSnapPoints<DrawerValue> {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Open at DrawerSnapPoint.ContentSize
    }

    val result = runCatching {
      UnstyledDrawerState(DrawerValue.Peek, snapPoints)
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun stateRejectsUnsupportedTargetValueAssignmentsAndJumps() {
    val state = UnstyledDrawerState(
      initialValue = DrawerValue.Closed,
      snapPoints = DrawerSnapPoints {
        DrawerValue.Closed at DrawerSnapPoint.Zero
        DrawerValue.Open at DrawerSnapPoint.ContentSize
      },
    )

    val assignment = runCatching {
      state.targetValue = DrawerValue.Peek
    }
    val jump = runCatching {
      state.jumpTo(DrawerValue.Peek)
    }

    assertThat(assignment.isFailure).isEqualTo(true)
    assertThat(jump.isFailure).isEqualTo(true)
  }

  @Test
  fun normalAnimationSpecAnimatesToNonZeroValues() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    val dismissAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        animationSpec = animationSpec,
        dismissAnimationSpec = dismissAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Open
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(animationSpec.vectorizeCalls).isGreaterThan(0)
    assertThat(dismissAnimationSpec.vectorizeCalls).isEqualTo(0)
  }

  @Test
  fun animationSpecIsUsedForDismissalByDefault() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        animationSpec = animationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Closed
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(animationSpec.vectorizeCalls).isGreaterThan(0)
  }

  @Test
  fun dismissalAnimationSpecAnimatesToZero() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    val dismissAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        animationSpec = animationSpec,
        dismissAnimationSpec = dismissAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Closed
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(animationSpec.vectorizeCalls).isEqualTo(0)
    assertThat(dismissAnimationSpec.vectorizeCalls).isGreaterThan(0)
  }

  @Test
  fun explicitAnimateToSpecOverridesDismissalAnimationSpec() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    val dismissAnimationSpec = RecordingAnimationSpec()
    val overrideAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>
    lateinit var scope: CoroutineScope

    setContent {
      scope = rememberCoroutineScope()
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        animationSpec = animationSpec,
        dismissAnimationSpec = dismissAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    scope.launch {
      state.animateTo(DrawerValue.Closed, animationSpec = overrideAnimationSpec)
    }
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(animationSpec.vectorizeCalls).isEqualTo(0)
    assertThat(dismissAnimationSpec.vectorizeCalls).isEqualTo(0)
    assertThat(overrideAnimationSpec.vectorizeCalls).isGreaterThan(0)
  }

  @Test
  fun dragToZeroUsesDismissalAnimationSpec() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    val dismissAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        animationSpec = animationSpec,
        dismissAnimationSpec = dismissAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      swipe(Offset(centerX, 45f), Offset(centerX, 99f), durationMillis = 500)
    }
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(animationSpec.vectorizeCalls).isEqualTo(0)
    assertThat(dismissAnimationSpec.vectorizeCalls).isGreaterThan(0)
  }

  @Test
  fun animationSpecUpdatesAffectTheNextRequestOnly() = runComposeUiTest {
    val initialAnimationSpec = RecordingAnimationSpec()
    val updatedAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        },
        animationSpec = initialAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    runOnIdle {
      state.targetValue = DrawerValue.Open
      state.animationSpec = updatedAnimationSpec
    }
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(initialAnimationSpec.vectorizeCalls).isGreaterThan(0)
    assertThat(updatedAnimationSpec.vectorizeCalls).isEqualTo(0)

    state.targetValue = DrawerValue.Peek
    waitUntil { state.currentValue == DrawerValue.Peek && state.isIdle }

    assertThat(updatedAnimationSpec.vectorizeCalls).isGreaterThan(0)
  }

  @Test
  fun dismissAnimationSpecUpdatesAffectTheNextRequestOnly() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    val initialDismissAnimationSpec = RecordingAnimationSpec()
    val updatedDismissAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        animationSpec = animationSpec,
        dismissAnimationSpec = initialDismissAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    runOnIdle {
      state.targetValue = DrawerValue.Closed
      state.dismissAnimationSpec = updatedDismissAnimationSpec
    }
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(initialDismissAnimationSpec.vectorizeCalls).isGreaterThan(0)
    assertThat(updatedDismissAnimationSpec.vectorizeCalls).isEqualTo(0)

    state.targetValue = DrawerValue.Open
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }
    state.targetValue = DrawerValue.Closed
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(updatedDismissAnimationSpec.vectorizeCalls).isGreaterThan(0)
  }

  @Test
  fun jumpToIgnoresAnimationSpecs() = runComposeUiTest {
    val animationSpec = RecordingAnimationSpec()
    val dismissAnimationSpec = RecordingAnimationSpec()
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        animationSpec = animationSpec,
        dismissAnimationSpec = dismissAnimationSpec,
        onState = { state = it },
      )
    }
    waitForIdle()

    state.jumpTo(DrawerValue.Open)
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(animationSpec.vectorizeCalls).isEqualTo(0)
    assertThat(dismissAnimationSpec.vectorizeCalls).isEqualTo(0)
  }

  @Test
  fun progressTracksTheVisiblePanelExtentInEitherDirection() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        onState = { state = it },
      )
    }
    waitForIdle()

    assertThat(state.progress(DrawerValue.Closed, DrawerValue.Open)).isEqualTo(0f)
    assertThat(state.progress(DrawerValue.Open, DrawerValue.Closed)).isEqualTo(1f)

    state.isDragging = true
    state.anchoredDraggableState.dispatchRawDelta(-30f)

    assertThat(state.progress(DrawerValue.Closed, DrawerValue.Open)).isEqualTo(0.5f)
    assertThat(state.progress(DrawerValue.Open, DrawerValue.Closed)).isEqualTo(0.5f)
    state.isDragging = false
  }

  @Test
  fun bottomDrawerPositionsZeroOutsideViewport() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Closed)
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(60)
  }

  @Test
  fun contentSizeSnapPointRevealsMeasuredPanelSize() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Open)
    }

    waitForIdle()

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(viewportBounds.bottom.roundToInt())
    assertThat(panelBounds.height.roundToInt()).isEqualTo(60)
  }

  @Test
  fun oversizedPanelUsesItsMeasuredContentSizeForSnapPoints() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val halfContentSize = DrawerSnapPoint { _, contentSize -> contentSize / 2f }

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at halfContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Peek, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(
            Modifier
              .fillMaxWidth()
              .requiredSize(width = 100.dp, height = 200.dp)
              .testTag(PanelTag),
          ) {}
        }
      }
    }

    waitForIdle()

    val panelBounds = onNodeWithTag(PanelTag).getUnclippedBoundsInRoot()
    assertThat(state.contentSizePx.roundToInt()).isEqualTo(200)
    assertThat(state.offset.roundToInt()).isEqualTo(100)
    assertThat(panelBounds.top).isEqualTo(0.dp)
    assertThat(panelBounds.bottom - panelBounds.top).isEqualTo(200.dp)
  }

  @Test
  fun contentSizeSnapPointsDoNotQueryIntrinsicPanelMeasurement() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth()) {
            Layout(
              content = {},
              measurePolicy = object : MeasurePolicy {
                override fun MeasureScope.measure(
                  measurables: List<Measurable>,
                  constraints: Constraints,
                ): MeasureResult {
                  return layout(width = constraints.maxWidth, height = 60) {}
                }

                override fun IntrinsicMeasureScope.maxIntrinsicHeight(
                  measurables: List<IntrinsicMeasurable>,
                  width: Int,
                ): Int {
                  error("Drawer content-size snap points must not query intrinsic measurement.")
                }
              },
            )
          }
        }
      }
    }

    waitForIdle()
    assertThat(state.offset.roundToInt()).isEqualTo(60)
  }

  @Test
  fun contentSizedDrawerTracksGrowingPanelWithoutChangingItsValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val panelHeight = mutableStateOf(30.dp)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(panelHeight.value).testTag(PanelTag)) {}
        }
      }
    }
    waitUntil { state.offset.roundToInt() == 30 }

    panelHeight.value = 80.dp
    waitUntil { state.offset.roundToInt() == 80 && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun contentSizedDrawerTracksShrinkingPanelWithoutChangingItsValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val panelHeight = mutableStateOf(80.dp)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(panelHeight.value).testTag(PanelTag)) {}
        }
      }
    }
    waitUntil { state.offset.roundToInt() == 80 }

    panelHeight.value = 30.dp
    waitUntil { state.offset.roundToInt() == 30 && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun customSnapPointReceivesViewportAndContentSize() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { viewportSize, contentSize ->
            viewportSize - contentSize
          }
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        },
      )
    }

    waitForIdle()

    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(60)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(120)
  }

  @Test
  fun customSnapPointClampsPositiveInfinityToTheViewport() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> Dp(Float.POSITIVE_INFINITY) }
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    assertThat(state.offset.roundToInt()).isEqualTo(100)
  }

  @Test
  fun customSnapPointsNormalizeInvalidResultsToZero() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> Dp(Float.NaN) }
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    assertThat(state.offset.roundToInt()).isEqualTo(0)
  }

  @Test
  fun customSnapPointsClampNegativeResultsToZero() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> (-1).dp }
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    assertThat(state.offset.roundToInt()).isEqualTo(0)
  }

  @Test
  fun equalPositionSnapPointsChangeLogicalValueWithoutMovingThePanel() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
          DrawerValue.Open at DrawerSnapPoint { _, _ -> 30.dp }
        },
        onState = { state = it },
      )
    }
    waitForIdle()
    val initialBounds = onNodeWithTag(PanelTag).boundsInRoot()

    state.targetValue = DrawerValue.Open
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    val finalBounds = onNodeWithTag(PanelTag).boundsInRoot()
    assertThat(finalBounds.top.roundToInt()).isEqualTo(initialBounds.top.roundToInt())
  }

  @Test
  fun viewportWindowInsetsReduceTheDrawerCoordinateSpace() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        windowInsets = WindowInsets(bottom = 20),
      )
    }

    waitForIdle()

    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(20)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(80)
  }

  @Test
  fun bottomDrawerDragsThroughTheBottomInsetBeforeSettlingAtTheScreenEdge() =
    runComposeUiTest {
      lateinit var state: UnstyledDrawerState<DrawerValue>

      setContent {
        BottomDrawerLayout(
          initialValue = DrawerValue.Open,
          windowInsets = WindowInsets(bottom = 20),
          onState = { state = it },
        )
      }
      waitForIdle()

      state.isDragging = true
      state.anchoredDraggableState.dispatchRawDelta(60f)
      mainClock.advanceTimeByFrame()
      assertThat(
        onNodeWithTag(PanelTag).boundsInRoot().top.roundToInt(),
      ).isEqualTo(80)

      state.anchoredDraggableState.dispatchRawDelta(10f)
      mainClock.advanceTimeByFrame()
      assertThat(
        onNodeWithTag(PanelTag).boundsInRoot().top.roundToInt(),
      ).isEqualTo(90)

      mainClock.autoAdvance = false
      try {
        state.isDragging = false
        state.settleToClosestValue(DrawerValueChange.Reason.Gesture)
        mainClock.advanceTimeByFrame()

        assertThat(state.anchoredDraggableState.offset.roundToInt() == 100).isEqualTo(false)
        assertThat(state.isPanelHidden).isEqualTo(false)
      } finally {
        mainClock.autoAdvance = true
      }
      waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

      assertThat(
        onNodeWithTag(PanelTag).boundsInRoot().top.roundToInt(),
      ).isEqualTo(100)
    }

  @Test
  fun zeroWindowInsetsKeepTheEntireViewportAvailableToTheDrawer() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        windowInsets = WindowInsets(0),
      )
    }

    waitForIdle()

    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()
    assertThat(panelBounds.top.roundToInt()).isEqualTo(40)
    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(100)
  }

  @Test
  fun insetChangesDuringDragPreserveProgressAndProjectedTargetValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val bottomInset = mutableStateOf(0)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint { viewportSize, _ -> viewportSize * 0.5f }
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Closed, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(
          modifier = Modifier.requiredSize(100.dp),
          windowInsets = WindowInsets(bottom = bottomInset.value),
        ) {
          Panel(Modifier.fillMaxWidth().height(100.dp)) {}
        }
      }
    }
    waitForIdle()

    state.isDragging = true
    state.anchoredDraggableState.dispatchRawDelta(-25f)
    assertThat(state.offset).isEqualTo(25f)

    bottomInset.value = 40
    mainClock.advanceTimeByFrame()

    assertThat(state.offset).isEqualTo(15f)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
    state.isDragging = false
  }

  @Test
  fun insetChangesDuringAnimationKeepTheExistingTargetValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val bottomInset = mutableStateOf(0)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Closed, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(
          modifier = Modifier.requiredSize(100.dp),
          windowInsets = WindowInsets(bottom = bottomInset.value),
        ) {
          Panel(Modifier.fillMaxWidth().height(80.dp)) {}
        }
      }
    }
    waitForIdle()

    mainClock.autoAdvance = false
    try {
      state.targetValue = DrawerValue.Open
      mainClock.advanceTimeByFrame()
      mainClock.advanceTimeByFrame()
      bottomInset.value = 50
      mainClock.advanceTimeByFrame()

      assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
      assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    } finally {
      mainClock.autoAdvance = true
    }

    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }
    assertThat(state.offset.roundToInt()).isEqualTo(50)
  }

  @Test
  fun viewportResizeRecalculatesVisibleExtentWithoutChangingLogicalValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val viewportHeight = mutableStateOf(100.dp)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp, viewportHeight.value)) {
          Panel(Modifier.fillMaxWidth().height(80.dp)) {}
        }
      }
    }
    waitUntil { state.offset.roundToInt() == 80 }

    viewportHeight.value = 50.dp
    waitUntil { state.offset.roundToInt() == 50 && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun viewportResizeDuringAnimationKeepsTheExistingTargetValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val viewportHeight = mutableStateOf(100.dp)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Closed, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp, viewportHeight.value)) {
          Panel(Modifier.fillMaxWidth().height(80.dp)) {}
        }
      }
    }
    waitForIdle()

    mainClock.autoAdvance = false
    try {
      state.targetValue = DrawerValue.Open
      mainClock.advanceTimeByFrame()
      viewportHeight.value = 50.dp
      mainClock.advanceTimeByFrame()

      assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
    } finally {
      mainClock.autoAdvance = true
    }

    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(state.offset.roundToInt()).isEqualTo(50)
  }

  @Test
  fun viewportResizeDuringDragPreservesNormalizedProgress() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val viewportHeight = mutableStateOf(100.dp)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint { viewportSize, _ -> viewportSize * 0.5f }
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Closed, snapPoints) }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp, viewportHeight.value)) {
          Panel(Modifier.fillMaxWidth().height(100.dp)) {}
        }
      }
    }
    waitForIdle()

    state.isDragging = true
    state.anchoredDraggableState.dispatchRawDelta(-25f)
    assertThat(state.offset).isEqualTo(25f)

    viewportHeight.value = 200.dp
    mainClock.advanceTimeByFrame()

    assertThat(state.offset).isEqualTo(50f)
    state.isDragging = false
  }

  @Test
  fun startDrawerCanBeSwipedOpenFromZero() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        swipeArea = true,
        onState = { state = it },
      )
    }

    waitForIdle()

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      swipe(
        start = Offset(1f, centerY),
        end = Offset(80f, centerY),
        durationMillis = 500,
      )
    }
    waitUntil {
      state.currentValue == DrawerValue.Open
    }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun edgeSwipeRevealsTheDrawerUnderThePointerBeforeRelease() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        swipeArea = true,
        onState = { state = it },
      )
    }

    waitForIdle()

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      down(Offset(1f, centerY))
      moveTo(Offset(50f, centerY), delayMillis = 16)
    }
    waitUntil { state.offset > 0f }

    assertThat(state.offset).isGreaterThan(40f)

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      cancel()
    }
  }

  @Test
  fun edgeSwipeMakesTheDrawerVisibleBeforeRelease() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        swipeArea = true,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      down(Offset(1f, centerY))
      moveTo(Offset(50f, centerY), delayMillis = 16)
    }
    waitUntil { state.offset > 0f }

    val panelSemantics = onNodeWithTag(PanelTag, useUnmergedTree = true)
      .fetchSemanticsNode()
      .config
    assertThat(panelSemantics.contains(SemanticsProperties.HideFromAccessibility)).isEqualTo(false)

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      cancel()
    }
  }

  @Test
  fun drawerDoesNotOpenFromTheEdgeWithoutSwipeArea() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        onState = { state = it },
      )
    }

    waitForIdle()
    onNodeWithTag(ViewportTag).performTouchInput {
      swipe(
        start = Offset(1f, centerY),
        end = Offset(width - 1f, centerY),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun swipeAreaDoesNotOpenDrawerWhenGesturesAreDisabled() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        gesturesEnabled = false,
        swipeArea = true,
        onState = { state = it },
      )
    }

    waitForIdle()
    onNodeWithTag(SwipeAreaTag).performTouchInput {
      swipe(
        start = Offset(1f, centerY),
        end = Offset(80f, centerY),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun edgeSwipeUsesItsFlingVelocityWhenSettling() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        swipeArea = true,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      down(Offset(1f, centerY))
      moveTo(Offset(29f, centerY), delayMillis = 10)
      moveTo(Offset(30f, centerY), delayMillis = 1)
      up()
    }
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun startDrawerResolvesToTheRightEdgeInRtl() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        StartDrawerLayout(initialValue = DrawerValue.Open)
      }
    }
    waitForIdle()

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.right.roundToInt())
  }

  @Test
  fun endDrawerResolvesToTheRightEdgeInLtr() = runComposeUiTest {
    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Open,
        placement = DrawerPlacement.End,
      )
    }
    waitForIdle()

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.right.roundToInt())
  }

  @Test
  fun endDrawerResolvesToTheLeftEdgeInRtl() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        StartDrawerLayout(
          initialValue = DrawerValue.Open,
          placement = DrawerPlacement.End,
        )
      }
    }
    waitForIdle()

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.left.roundToInt()).isEqualTo(viewportBounds.left.roundToInt())
  }

  @Test
  fun endDrawerOpensFromTheLeftViewportEdgeInRtl() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        StartDrawerLayout(
          initialValue = DrawerValue.Closed,
          placement = DrawerPlacement.End,
          swipeArea = true,
          onState = { state = it },
        )
      }
    }
    waitForIdle()

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      swipe(
        start = Offset(1f, centerY),
        end = Offset(80f, centerY),
        durationMillis = 500,
      )
    }
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun gestureSettlesBackWhenValueChangeIsRejected() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        swipeArea = true,
        confirmValueChange = { change ->
          change.targetValue != DrawerValue.Open
        },
        onState = { state = it },
      )
    }

    waitForIdle()

    onNodeWithTag(SwipeAreaTag).performTouchInput {
      swipe(
        start = Offset(1f, centerY),
        end = Offset(80f, centerY),
        durationMillis = 500,
      )
    }
    waitUntil {
      state.isIdle
    }

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(panelBounds.right.roundToInt()).isEqualTo(viewportBounds.left.roundToInt())
  }

  @Test
  fun jumpToSnapsToTheTargetValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        onState = { state = it },
      )
    }

    waitForIdle()

    state.jumpTo(DrawerValue.Open)
    waitUntil {
      state.currentValue == DrawerValue.Open
    }

    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.top.roundToInt()).isEqualTo(40)
  }

  @Test
  fun programmaticJumpRequestedBeforeFirstLayoutCompletesAfterMeasurement() = runComposeUiTest {
    val snapPoints = DrawerSnapPoints {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Open at DrawerSnapPoint.ContentSize
    }
    val state = UnstyledDrawerState(DrawerValue.Closed, snapPoints)

    state.jumpTo(DrawerValue.Open)

    setContent {
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(60.dp)) {}
        }
      }
    }
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(state.offset.roundToInt()).isEqualTo(60)
  }

  @Test
  fun programmaticRequestsAreConfirmedBeforeTheyChangeTheTarget() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var confirmationCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        confirmValueChange = {
          confirmationCalls += 1
          false
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Open
    state.jumpTo(DrawerValue.Open)

    assertThat(confirmationCalls).isEqualTo(2)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun confirmationRejectsAnimateToWithoutChangingTheDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        confirmValueChange = { false },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.animateTo(DrawerValue.Open)

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.isIdle).isEqualTo(true)
  }

  @Test
  fun latestProgrammaticRequestWinsOverAnEarlierPendingAnimation() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Open
    state.targetValue = DrawerValue.Peek
    waitUntil { state.currentValue == DrawerValue.Peek && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerValue.Peek)
  }

  @Test
  fun callerCancelledAnimationSettlesAtTheNearestSupportedValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    lateinit var scope: CoroutineScope
    lateinit var animation: Job

    setContent {
      scope = rememberCoroutineScope()
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        onState = { state = it },
      )
    }
    waitForIdle()

    mainClock.autoAdvance = false
    try {
      animation = scope.launch {
        state.animateTo(DrawerValue.Open)
      }
      mainClock.advanceTimeBy(32)
      animation.cancel()
      mainClock.advanceTimeBy(0)
    } finally {
      mainClock.autoAdvance = true
    }

    waitUntil { state.isIdle }

    assertThat(animation.isCancelled).isEqualTo(true)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun noOpRequestsDoNotInvokeConfirmation() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var confirmationCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        confirmValueChange = {
          confirmationCalls += 1
          true
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Open
    state.jumpTo(DrawerValue.Open)

    assertThat(confirmationCalls).isEqualTo(0)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun confirmationCannotReenterTheSameDrawerState() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember {
        UnstyledDrawerState(
          initialValue = DrawerValue.Closed,
          snapPoints = snapPoints,
          confirmValueChange = {
            state.targetValue = DrawerValue.Open
            true
          },
        )
      }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(60.dp)) {}
        }
      }
    }
    waitForIdle()

    val result = runCatching {
      state.targetValue = DrawerValue.Open
    }

    assertThat(result.isFailure).isEqualTo(true)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun confirmationExceptionsLeaveDrawerStateUnchanged() {
    val state = UnstyledDrawerState(
      initialValue = DrawerValue.Closed,
      snapPoints = DrawerSnapPoints {
        DrawerValue.Closed at DrawerSnapPoint.Zero
        DrawerValue.Open at DrawerSnapPoint.ContentSize
      },
      confirmValueChange = { error("confirmation failed") },
    )

    val result = runCatching { state.jumpTo(DrawerValue.Open) }

    assertThat(result.isFailure).isEqualTo(true)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun drawerRequiresExactlyOneViewport() = runComposeUiTest {
    val result = runCatching {
      setContent {
        val state = remember {
          UnstyledDrawerState(
            DrawerValue.Closed,
            DrawerSnapPoints { DrawerValue.Closed at DrawerSnapPoint.Zero },
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {}
      }
      waitForIdle()
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun viewportRequiresExactlyOnePanel() = runComposeUiTest {
    val result = runCatching {
      setContent {
        val state = remember {
          UnstyledDrawerState(
            DrawerValue.Closed,
            DrawerSnapPoints { DrawerValue.Closed at DrawerSnapPoint.Zero },
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
          Viewport<DrawerValue>(Modifier.requiredSize(100.dp)) {}
        }
      }
      waitForIdle()
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun viewportRejectsDuplicatePanels() = runComposeUiTest {
    val result = runCatching {
      setContent {
        val state = remember {
          UnstyledDrawerState(
            DrawerValue.Closed,
            DrawerSnapPoints { DrawerValue.Closed at DrawerSnapPoint.Zero },
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(10.dp)) {}
            Panel(Modifier.fillMaxWidth().height(10.dp)) {}
          }
        }
      }
      waitForIdle()
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun viewportRejectsDuplicateOverlays() = runComposeUiTest {
    val result = runCatching {
      setContent {
        val state = remember {
          UnstyledDrawerState(
            DrawerValue.Open,
            DrawerSnapPoints { DrawerValue.Open at DrawerSnapPoint.ContentSize },
          )
        }
        UnstyledDrawer(
          state = state,
          presentation = DrawerPresentation.Inline,
          overlay = {
            Overlay {}
            Overlay {}
          },
        ) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(10.dp)) {}
          }
        }
      }
      waitForIdle()
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun viewportRejectsUnboundedConstraints() = runComposeUiTest {
    val result = runCatching {
      setContent {
        val state = remember {
          UnstyledDrawerState(
            DrawerValue.Open,
            DrawerSnapPoints { DrawerValue.Open at DrawerSnapPoint.ContentSize },
          )
        }
        Layout(
          content = {
            UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
              Viewport {
                Panel(Modifier.fillMaxWidth().height(10.dp)) {}
              }
            }
          },
        ) { measurables, _ ->
          val placeable = measurables.single().measure(
            Constraints(maxWidth = Constraints.Infinity, maxHeight = 100),
          )
          layout(100, 100) {
            placeable.place(0, 0)
          }
        }
      }
      waitForIdle()
    }

    assertThat(result.isFailure).isEqualTo(true)
  }

  @Test
  fun removingTheCurrentSnapPointResolvesToTheNearestRemainingValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        },
        onState = { state = it },
      )
    }
    waitForIdle()
    val physicalOffsetBeforeRemoval = state.offset

    state.updateSnapPoints(
      DrawerSnapPoints {
        DrawerValue.Closed at DrawerSnapPoint.Zero
        DrawerValue.Open at DrawerSnapPoint.ContentSize
      },
    )
    assertThat(state.offset.roundToInt()).isEqualTo(physicalOffsetBeforeRemoval.roundToInt())
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun removingTheTargetSnapPointDuringAnimationResolvesToASupportedValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val initialSnapPoints = DrawerSnapPoints {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
      DrawerValue.Open at DrawerSnapPoint.ContentSize
    }
    val updatedSnapPoints = DrawerSnapPoints {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
    }

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        snapPoints = initialSnapPoints,
        onState = { state = it },
      )
    }
    waitForIdle()

    mainClock.autoAdvance = false
    state.targetValue = DrawerValue.Open
    mainClock.advanceTimeByFrame()
    state.updateSnapPoints(updatedSnapPoints)
    mainClock.autoAdvance = true

    waitUntil { state.isIdle }

    assertThat(updatedSnapPoints.contains(state.currentValue)).isEqualTo(true)
    assertThat(updatedSnapPoints.contains(state.targetValue)).isEqualTo(true)
  }

  @Test
  fun dismissCallbackRunsOnceWhenSettledAtZero() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var dismissedCalls = 0
    var dismissedReason: DrawerValueChange.Reason? = null

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        onState = { state = it },
        onDismissed = { change ->
          dismissedCalls += 1
          dismissedReason = change.reason
        },
      )
    }

    waitForIdle()

    state.jumpTo(DrawerValue.Closed)
    waitUntil {
      state.currentValue == DrawerValue.Closed
    }
    waitForIdle()

    assertThat(dismissedCalls).isEqualTo(1)
    assertThat(dismissedReason).isEqualTo(DrawerValueChange.Reason.Programmatic)
  }

  @Test
  fun supersededDismissalDoesNotInvokeDismissedCallback() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var dismissedCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        onState = { state = it },
        onDismissed = { dismissedCalls += 1 },
      )
    }
    waitForIdle()

    state.targetValue = DrawerValue.Closed
    state.targetValue = DrawerValue.Open
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(dismissedCalls).isEqualTo(0)
  }

  @Test
  fun initiallyZeroValuedDrawerDoesNotInvokeDismissedCallback() = runComposeUiTest {
    var dismissedCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        onDismissed = { dismissedCalls += 1 },
      )
    }
    waitForIdle()

    assertThat(dismissedCalls).isEqualTo(0)
  }

  @Test
  fun dismissCallbackDoesNotRunWhenThereIsNoZeroValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var dismissedCalls = 0
    val snapPoints = DrawerSnapPoints<DrawerValue> {
      DrawerValue.Peek at DrawerSnapPoint { viewportSize, _ -> viewportSize / 2f }
      DrawerValue.Open at DrawerSnapPoint.ContentSize
    }

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        snapPoints = snapPoints,
        onState = { state = it },
        onDismissed = { dismissedCalls += 1 },
      )
    }

    waitForIdle()

    state.jumpTo(DrawerValue.Peek)
    waitUntil {
      state.currentValue == DrawerValue.Peek
    }
    waitForIdle()

    assertThat(dismissedCalls).isEqualTo(0)
  }

  @Test
  fun predictiveBackPreviewKeepsLogicalStateAndRestoresOnCancellation() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var confirmationCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        confirmValueChange = {
          confirmationCalls += 1
          true
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    assertThat(state.startPredictiveBack()).isEqualTo(true)
    state.progressPredictiveBack(0.5f)

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
    assertThat(state.isIdle).isEqualTo(false)
    assertThat(confirmationCalls).isEqualTo(1)

    state.cancelPredictiveBack()
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
    assertThat(confirmationCalls).isEqualTo(1)
  }

  @Test
  fun predictiveBackCommitsWithoutConfirmingTwice() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var confirmationCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        confirmValueChange = {
          confirmationCalls += 1
          true
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.startPredictiveBack()
    state.progressPredictiveBack(0.5f)
    state.invokePredictiveBack()
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(confirmationCalls).isEqualTo(1)
  }

  @Test
  fun rejectedPredictiveBackConsumesTheGestureWithoutChangingDrawerState() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var confirmationCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        confirmValueChange = {
          confirmationCalls += 1
          false
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    assertThat(state.startPredictiveBack()).isEqualTo(true)
    state.progressPredictiveBack(1f)
    state.invokePredictiveBack()
    waitForIdle()

    assertThat(confirmationCalls).isEqualTo(1)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun overlayIsComposedWhenModalDrawerIsVisible() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        overlay = {
          Overlay(Modifier.testTag(OverlayTag))
        },
      )
    }

    waitUntilExactlyOneExists(hasTestTag(OverlayTag))
  }

  @Test
  fun modalOverlayRunsExitTransitionWhenDismissalStarts() = runComposeUiTestV1 {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val overlayHeight = mutableStateOf<Float?>(null)

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        overlay = {
          Overlay(
            modifier = Modifier
              .fillMaxSize()
              .onGloballyPositioned { overlayHeight.value = it.boundsInRoot().height }
              .testTag(OverlayTag),
            exit = shrinkVertically(
              animationSpec = tween(durationMillis = 10_000),
            ),
          )
        },
        onState = { state = it },
      )
    }
    waitUntil { overlayHeight.value != null }
    val restingHeight = overlayHeight.value!!

    mainClock.autoAdvance = false
    try {
      runOnIdle {
        state.targetValue = DrawerValue.Closed
        assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
      }
      mainClock.advanceTimeUntil {
        overlayHeight.value != null && overlayHeight.value!! < restingHeight
      }
      val exitingHeight = overlayHeight.value!!

      assertThat(restingHeight).isGreaterThan(exitingHeight)
    } finally {
      mainClock.autoAdvance = true
    }
  }

  @Test
  fun modalOverlayAppearsAsSoonAsDrawerEntersTheViewport() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Closed,
        presentation = DrawerPresentation.Modal,
        overlay = {
          Overlay(Modifier.testTag(OverlayTag))
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.anchoredDraggableState.dispatchRawDelta(1f)
    waitUntil { state.offset > 0f }

    onNodeWithTag(OverlayTag).assertExists()
  }

  @Test
  fun overlayIsComposedWhenInlineDrawerIsVisible() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Inline,
        overlay = {
          Overlay(Modifier.testTag(OverlayTag))
        },
      )
    }

    waitUntilExactlyOneExists(hasTestTag(OverlayTag))
  }

  @Test
  fun modalDrawerUsesDialogHost() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
      )
    }

    waitForIdle()

    onNode(isDialog()).assertExists()
  }

  @Test
  fun modalPanelReceivesClickWhileOutsideViewportClickDismisses() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var panelClicks = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        panelModifier = Modifier.clickable { panelClicks += 1 },
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      click(Offset(centerX, centerY))
    }
    assertThat(panelClicks).isEqualTo(1)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)

    onNodeWithTag(ViewportTag).performTouchInput {
      click(Offset(centerX, 4f))
    }
    waitUntil { state.currentValue == DrawerValue.Closed }
  }

  @Test
  fun interactiveModalOverlayCanConsumeOutsideTapWithoutDismissingTheDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var overlayClicks = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        overlay = {
          Overlay(
            Modifier
              .fillMaxSize()
              .clickable { overlayClicks += 1 },
          )
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(ViewportTag).performTouchInput {
      click(Offset(centerX, 4f))
    }
    waitForIdle()

    assertThat(overlayClicks).isEqualTo(1)
    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun disabledOutsideClickDismissalKeepsModalDrawerOpen() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        dismissOnClickOutside = false,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(ViewportTag).performTouchInput {
      click(Offset(centerX, 4f))
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun modalHostBlocksOutsidePointerInputWhenOutsideDismissalIsDisabled() = runComposeUiTest {
    var backgroundClicks = 0

    setContent {
      ModalHost(Modifier.requiredSize(100.dp)) {
        Box(
          Modifier
            .fillMaxSize()
            .clickable { backgroundClicks += 1 }
            .testTag(BackgroundTag),
        )
        BottomDrawerLayout(
          initialValue = DrawerValue.Open,
          presentation = DrawerPresentation.Modal,
          dismissOnClickOutside = false,
        )
      }
    }
    waitForIdle()

    onNodeWithTag(BackgroundTag).performTouchInput {
      click(Offset(centerX, 4f))
    }

    assertThat(backgroundClicks).isEqualTo(0)
  }

  @Test
  fun modalHostKeepsOutsidePointerInputBlockedWhileItsOverlayExits() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var backgroundClicks = 0

    setContent {
      ModalHost(Modifier.requiredSize(100.dp)) {
        Box(
          Modifier
            .fillMaxSize()
            .clickable { backgroundClicks += 1 }
            .testTag(BackgroundTag),
        )
        BottomDrawerLayout(
          initialValue = DrawerValue.Open,
          presentation = DrawerPresentation.Modal,
          dismissOnClickOutside = false,
          onState = { state = it },
          overlay = {
            Overlay(exit = fadeOut(tween(durationMillis = 1_000)))
          },
        )
      }
    }
    waitForIdle()

    mainClock.autoAdvance = false
    try {
      state.jumpTo(DrawerValue.Closed)
      mainClock.advanceTimeByFrame()

      onNodeWithTag(BackgroundTag).performTouchInput {
        click(Offset(centerX, 4f))
      }
      assertThat(backgroundClicks).isEqualTo(0)
    } finally {
      mainClock.autoAdvance = true
    }
  }

  @Test
  fun modalDrawerExposesOutsideDismissalToAccessibilityWithoutAnOverlay() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
      )
    }
    waitForIdle()

    onNode(hasSemanticsAction(SemanticsActions.Dismiss)).assertExists()
  }

  @Test
  fun modalDrawerHidesOutsideDismissalAccessibilityActionWhenDisabled() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        dismissOnClickOutside = false,
      )
    }
    waitForIdle()

    onNode(hasSemanticsAction(SemanticsActions.Dismiss)).assertDoesNotExist()
  }

  @Test
  fun programmaticDismissalStillWorksWhenUserDismissalInputsAreDisabled() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        gesturesEnabled = false,
        dismissOnClickOutside = false,
        onState = { state = it },
      )
    }
    waitForIdle()

    state.jumpTo(DrawerValue.Closed)
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun rejectedProgrammaticDismissalDoesNotInvokeDismissedCallback() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    var dismissedCalls = 0

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        confirmValueChange = { false },
        onState = { state = it },
        onDismissed = { dismissedCalls += 1 },
      )
    }
    waitForIdle()

    state.jumpTo(DrawerValue.Closed)
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(dismissedCalls).isEqualTo(0)
  }

  @Test
  fun outsideClickDoesNotDismissModalDrawerWithoutAZeroSnapPoint() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Peek at DrawerSnapPoint { viewportSize, _ -> viewportSize / 2f }
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(ViewportTag).performTouchInput {
      click(Offset(centerX, 4f))
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun modalDrawerReleasesItsHostAfterThePanelClosesAndCanOpenAgain() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        onState = { state = it },
      )
    }
    waitForIdle()
    onNode(isDialog()).assertExists()

    state.jumpTo(DrawerValue.Closed)
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    onNode(isDialog()).assertDoesNotExist()

    state.jumpTo(DrawerValue.Open)
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    onNode(isDialog()).assertExists()
  }

  @Test
  fun modalHostRemainsMountedWhileOverlayExitIsRunning() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        onState = { state = it },
        overlay = {
          Overlay(exit = fadeOut(tween(durationMillis = 1_000)))
        },
      )
    }
    waitForIdle()

    mainClock.autoAdvance = false
    state.jumpTo(DrawerValue.Closed)
    mainClock.advanceTimeByFrame()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
    onNode(isDialog()).assertExists()
    mainClock.autoAdvance = true
  }

  @Test
  fun closedModalDrawerDoesNotBlockUnderlyingPointerInput() = runComposeUiTest {
    var backgroundClicks = 0

    setContent {
      Box(Modifier.requiredSize(100.dp)) {
        Box(
          Modifier
            .fillMaxSize()
            .clickable { backgroundClicks += 1 }
            .testTag(BackgroundTag),
        )
        BottomDrawerLayout(
          initialValue = DrawerValue.Closed,
          presentation = DrawerPresentation.Modal,
        )
      }
    }
    waitForIdle()

    onNodeWithTag(BackgroundTag).performTouchInput {
      click(Offset(centerX, centerY))
    }

    assertThat(backgroundClicks).isEqualTo(1)
  }

  @Test
  fun inlineOverlayDoesNotBlockUnderlyingPointerInput() = runComposeUiTest {
    var backgroundClicks = 0

    setContent {
      Box(Modifier.requiredSize(100.dp)) {
        Box(
          Modifier
            .fillMaxSize()
            .clickable { backgroundClicks += 1 }
            .testTag(BackgroundTag),
        )
        BottomDrawerLayout(
          initialValue = DrawerValue.Open,
          presentation = DrawerPresentation.Inline,
          overlay = {
            Overlay(Modifier.fillMaxSize().testTag(OverlayTag))
          },
        )
      }
    }
    waitForIdle()

    onNodeWithTag(BackgroundTag).performTouchInput {
      click(Offset(centerX, 4f))
    }

    assertThat(backgroundClicks).isEqualTo(1)
  }

  @Test
  fun inlineDrawerDoesNotUseDialogHost() = runComposeUiTest {
    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Inline,
      )
    }

    waitForIdle()

    onNode(isDialog()).assertDoesNotExist()
  }

  @Test
  fun dragHandleExposesOnlyValidMovementActions() = runComposeUiTest {
    setContent {
      val state = remember {
        UnstyledDrawerState(
          initialValue = DrawerValue.Open,
          snapPoints = DrawerSnapPoints {
            DrawerValue.Closed at DrawerSnapPoint.Zero
            DrawerValue.Open at DrawerSnapPoint.ContentSize
          },
        )
      }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(60.dp)) {
            DragHandle(Modifier.testTag(DragHandleTag))
          }
        }
      }
    }

    waitForIdle()

    onNodeWithTag(DragHandleTag)
      .assert(hasSemanticsAction(SemanticsActions.Collapse))
      .assert(hasSemanticsAction(SemanticsActions.Dismiss))
      .assert(hasNoSemanticsAction(SemanticsActions.Expand))
  }

  @Test
  fun dragHandleExposesNoMovementActionsWhenDrawerCannotMove() = runComposeUiTest {
    setContent {
      val state = remember {
        UnstyledDrawerState(
          initialValue = DrawerValue.Open,
          snapPoints = DrawerSnapPoints {
            DrawerValue.Open at DrawerSnapPoint.ContentSize
          },
        )
      }
      UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(60.dp)) {
            DragHandle(Modifier.testTag(DragHandleTag))
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag(DragHandleTag)
      .assert(hasNoSemanticsAction(SemanticsActions.Expand))
      .assert(hasNoSemanticsAction(SemanticsActions.Collapse))
      .assert(hasNoSemanticsAction(SemanticsActions.Dismiss))
  }

  @Test
  fun topDrawerPositionsZeroAboveTheViewport() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerValue.Closed, snapPoints) }
      UnstyledDrawer(
        state,
        placement = DrawerPlacement.Top,
        presentation = DrawerPresentation.Inline,
      ) {
        Viewport(Modifier.requiredSize(100.dp).testTag(ViewportTag)) {
          Panel(Modifier.fillMaxWidth().height(60.dp).testTag(PanelTag)) {}
        }
      }
    }
    waitForIdle()

    val viewportBounds = onNodeWithTag(ViewportTag).boundsInRoot()
    val panelBounds = onNodeWithTag(PanelTag).boundsInRoot()

    assertThat(panelBounds.bottom.roundToInt()).isEqualTo(viewportBounds.top.roundToInt())
  }

  @Test
  fun disabledGesturesDoNotMoveTheDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        gesturesEnabled = false,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      swipe(Offset(centerX, 50f), Offset(centerX, 99f), durationMillis = 500)
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun draggingBottomDrawerToZeroDismissesIt() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Open, onState = { state = it })
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      swipe(Offset(centerX, 45f), Offset(centerX, 99f), durationMillis = 500)
    }
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun targetValueUpdatesWhileDraggingTowardAnotherSnapPoint() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Peek,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Peek at DrawerSnapPoint { _, _ -> 30.dp }
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      down(center)
      moveTo(center.copy(y = center.y - 45f), delayMillis = 100)
    }
    waitUntil { state.isDragging }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Peek)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)

    onNodeWithTag(PanelTag).performTouchInput {
      cancel()
    }
  }

  @Test
  fun panelRemainsRenderedAtZeroWhileTheDragIsStillActive() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Open, onState = { state = it })
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      down(Offset(centerX, 45f))
      moveTo(Offset(centerX, 160f), delayMillis = 500)
    }
    waitUntil { state.isDragging && state.offset <= 0.5f }

    assertThat(state.isPanelHidden).isEqualTo(false)

    onNodeWithTag(PanelTag).performTouchInput {
      cancel()
    }
  }

  @Test
  fun fastClosingFlingSettlesClosedEvenWhenReleaseIsNearOpen() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Open, onState = { state = it })
    }
    waitForIdle()

    state.anchoredDraggableState.dispatchRawDelta(24f)
    state.settleFromFling(240f)
    waitUntil { state.currentValue == DrawerValue.Closed && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun midpointDragSettlesInTheFinalDragDirection() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Closed, onState = { state = it })
    }
    waitForIdle()

    state.anchoredDraggableState.dispatchRawDelta(-30f)
    state.settleToClosestValue(
      reason = DrawerValueChange.Reason.Gesture,
      direction = -1f,
    )
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun modalPanelDragDispatchesToItsOverscrollEffectAtTheOpenBoundary() = runComposeUiTest {
    val overscrollEffect = RecordingOverscrollEffect()

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        overscrollEffect = overscrollEffect,
      )
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      swipe(
        start = Offset(centerX, 45f),
        end = Offset(centerX, 5f),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(overscrollEffect.scrollCalls > 0).isEqualTo(true)
    assertThat(overscrollEffect.flingCalls).isEqualTo(1)
  }

  @Test
  fun programmaticDrawerMovementDoesNotDispatchToThePanelOverscrollEffect() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val overscrollEffect = RecordingOverscrollEffect()

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Closed,
        overscrollEffect = overscrollEffect,
        onState = { state = it },
      )
    }
    waitForIdle()

    state.jumpTo(DrawerValue.Open)
    waitUntil { state.currentValue == DrawerValue.Open && state.isIdle }

    assertThat(overscrollEffect.scrollCalls).isEqualTo(0)
  }

  @Test
  fun modalStartDrawerDragDispatchesToItsOverscrollEffectAtTheOpenBoundary() = runComposeUiTest {
    val overscrollEffect = RecordingOverscrollEffect()

    setContent {
      StartDrawerLayout(
        initialValue = DrawerValue.Open,
        presentation = DrawerPresentation.Modal,
        overscrollEffect = overscrollEffect,
      )
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      swipe(
        start = Offset(45f, centerY),
        end = Offset(95f, centerY),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(overscrollEffect.scrollCalls > 0).isEqualTo(true)
  }

  @Test
  fun modalEndDrawerInRtlDispatchesToItsOverscrollEffectAtTheOpenBoundary() =
    runComposeUiTest {
      val overscrollEffect = RecordingOverscrollEffect()

      setContent {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          StartDrawerLayout(
            initialValue = DrawerValue.Open,
            placement = DrawerPlacement.End,
            presentation = DrawerPresentation.Modal,
            overscrollEffect = overscrollEffect,
          )
        }
      }
      waitForIdle()

      onNodeWithTag(PanelTag).performTouchInput {
        swipe(
          start = Offset(45f, centerY),
          end = Offset(95f, centerY),
          durationMillis = 500,
        )
      }
      waitForIdle()

      assertThat(overscrollEffect.scrollCalls > 0).isEqualTo(true)
    }

  @Test
  fun modalTopDrawerDragDispatchesToItsOverscrollEffectAtTheOpenBoundary() = runComposeUiTest {
    val overscrollEffect = RecordingOverscrollEffect()
    val snapPoints = DrawerSnapPoints {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Open at DrawerSnapPoint.ContentSize
    }

    setContent {
      val state = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
      UnstyledDrawer(
        state = state,
        placement = DrawerPlacement.Top,
        presentation = DrawerPresentation.Modal,
      ) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(
            modifier = Modifier
              .fillMaxWidth()
              .height(60.dp)
              .testTag(PanelTag),
            overscrollEffect = overscrollEffect,
          ) {
            Box(Modifier.requiredSize(1.dp))
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag(PanelTag).performTouchInput {
      swipe(
        start = Offset(centerX, 15f),
        end = Offset(centerX, 55f),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(overscrollEffect.scrollCalls > 0).isEqualTo(true)
  }

  @Test
  fun dragStartingOutsideVisiblePanelDoesNotMoveInlineDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(initialValue = DrawerValue.Open, onState = { state = it })
    }
    waitForIdle()

    onNodeWithTag(ViewportTag).performTouchInput {
      swipe(
        start = Offset(centerX, 4f),
        end = Offset(centerX, 99f),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }

  @Test
  fun lazyColumnWithoutFixedHeightUsesAndScrollsWithinTheAvailableViewport() =
    runComposeUiTest {
      lateinit var drawerState: UnstyledDrawerState<DrawerValue>
      lateinit var listState: androidx.compose.foundation.lazy.LazyListState

      setContent {
        val snapPoints = remember {
          DrawerSnapPoints {
            DrawerValue.Closed at DrawerSnapPoint.Zero
            DrawerValue.Open at DrawerSnapPoint.ContentSize
          }
        }
        drawerState = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
        listState = rememberLazyListState()
        UnstyledDrawer(drawerState, presentation = DrawerPresentation.Inline) {
          Viewport(Modifier.requiredSize(100.dp).testTag(ViewportTag)) {
            Panel(Modifier.fillMaxWidth().testTag(PanelTag)) {
              LazyColumn(Modifier.testTag("lazy-column"), state = listState) {
                items(20) {
                  Box(Modifier.fillMaxWidth().height(20.dp))
                }
              }
            }
          }
        }
      }
      waitForIdle()

      assertThat(onNodeWithTag(PanelTag).boundsInRoot().height)
        .isEqualTo(onNodeWithTag(ViewportTag).boundsInRoot().height)

      onNodeWithTag("lazy-column").performTouchInput {
        swipe(
          start = Offset(centerX, bottomRight.y - 1f),
          end = Offset(centerX, 1f),
          durationMillis = 500,
        )
      }
      waitUntil { listState.firstVisibleItemIndex > 0 }

      assertThat(listState.firstVisibleItemIndex).isGreaterThan(0)
    }

  @Test
  fun scrollablePanelContentHandsOffAtBothDrawerBoundaries() = runComposeUiTest {
    lateinit var drawerState: UnstyledDrawerState<DrawerValue>
    lateinit var listState: androidx.compose.foundation.lazy.LazyListState
    val resetScroll = mutableStateOf(false)

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      drawerState = remember { UnstyledDrawerState(DrawerValue.Open, snapPoints) }
      listState = rememberLazyListState()
      LaunchedEffect(resetScroll.value) {
        if (resetScroll.value) {
          listState.scrollToItem(0)
        }
      }
      UnstyledDrawer(drawerState, presentation = DrawerPresentation.Inline) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth().height(60.dp)) {
            LazyColumn(Modifier.fillMaxSize().testTag("scrollable-panel"), state = listState) {
              items(20) {
                Box(Modifier.fillMaxWidth().height(20.dp))
              }
            }
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("scrollable-panel").performTouchInput {
      swipe(
        start = Offset(centerX, 50f),
        end = Offset(centerX, 5f),
        durationMillis = 500,
      )
    }
    waitUntil { listState.firstVisibleItemIndex > 0 }

    assertThat(listState.firstVisibleItemIndex > 0).isEqualTo(true)

    resetScroll.value = true
    waitUntil {
      listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
    }

    onNodeWithTag("scrollable-panel").performTouchInput {
      swipe(
        start = Offset(centerX, 1f),
        end = Offset(centerX, bottomRight.y - 1f),
        durationMillis = 500,
      )
    }
    waitUntil { drawerState.targetValue == DrawerValue.Closed }
    mainClock.advanceTimeUntil {
      drawerState.currentValue == DrawerValue.Closed && drawerState.isIdle
    }

    assertThat(drawerState.currentValue).isEqualTo(DrawerValue.Closed)
  }

  @Test
  fun updatingSnapPointsRecalculatesTheVisiblePanelSizeWithoutChangingValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        snapPoints = DrawerSnapPoints {
          DrawerValue.Closed at DrawerSnapPoint.Zero
          DrawerValue.Open at DrawerSnapPoint { _, _ -> 30.dp }
        },
        onState = { state = it },
      )
    }
    waitForIdle()

    state.updateSnapPoints(
      DrawerSnapPoints {
        DrawerValue.Closed at DrawerSnapPoint.Zero
        DrawerValue.Open at DrawerSnapPoint { _, _ -> 80.dp }
      },
    )
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.offset.roundToInt()).isEqualTo(80)
  }

  @Test
  fun invalidatingSnapPointsRecalculatesExternalSnapPointInputs() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerValue>
    val visibleSize = mutableStateOf(30.dp)
    val snapPoints = DrawerSnapPoints {
      DrawerValue.Closed at DrawerSnapPoint.Zero
      DrawerValue.Open at DrawerSnapPoint { _, _ -> visibleSize.value }
    }

    setContent {
      BottomDrawerLayout(
        initialValue = DrawerValue.Open,
        snapPoints = snapPoints,
        onState = { state = it },
      )
    }
    waitUntil { state.offset.roundToInt() == 30 }

    visibleSize.value = 45.dp
    state.invalidateSnapPoints()
    waitUntil { state.offset.roundToInt() == 45 && state.isIdle }

    assertThat(state.currentValue).isEqualTo(DrawerValue.Open)
    assertThat(state.targetValue).isEqualTo(DrawerValue.Open)
  }
}

private fun hasSemanticsAction(action: androidx.compose.ui.semantics.SemanticsPropertyKey<*>):
  SemanticsMatcher {
  return SemanticsMatcher("has ${action.name} action") { node ->
    action in node.config
  }
}

private fun hasNoSemanticsAction(action: androidx.compose.ui.semantics.SemanticsPropertyKey<*>):
  SemanticsMatcher {
  return SemanticsMatcher("has no ${action.name} action") { node ->
    (action in node.config).not()
  }
}

@Composable
private fun BottomDrawerLayout(
  initialValue: DrawerValue,
  snapPoints: DrawerSnapPoints<DrawerValue> = DrawerSnapPoints {
    DrawerValue.Closed at DrawerSnapPoint.Zero
    DrawerValue.Open at DrawerSnapPoint.ContentSize
  },
  windowInsets: WindowInsets = WindowInsets(),
  presentation: DrawerPresentation = DrawerPresentation.Inline,
  gesturesEnabled: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  overlay: (@Composable DrawerOverlayScope<DrawerValue>.() -> Unit)? = null,
  panelModifier: Modifier = Modifier,
  overscrollEffect: OverscrollEffect? = null,
  animationSpec: AnimationSpec<Float> = tween(),
  dismissAnimationSpec: AnimationSpec<Float> = animationSpec,
  confirmValueChange: (DrawerValueChange<DrawerValue>) -> Boolean = { true },
  onState: (UnstyledDrawerState<DrawerValue>) -> Unit = {},
  onDismissed: (DrawerValueChange<DrawerValue>) -> Unit = {},
) {
  val state = remember {
    UnstyledDrawerState(
      initialValue = initialValue,
      snapPoints = snapPoints,
      animationSpec = animationSpec,
      dismissAnimationSpec = dismissAnimationSpec,
      confirmValueChange = confirmValueChange,
    )
  }
  onState(state)
  UnstyledDrawer(
    state = state,
    placement = DrawerPlacement.Bottom,
    presentation = presentation,
    gesturesEnabled = gesturesEnabled,
    dismissOnClickOutside = dismissOnClickOutside,
    onDismissed = onDismissed,
    overlay = overlay,
  ) {
    Viewport(
      modifier = Modifier
        .requiredSize(100.dp)
        .testTag(ViewportTag),
      windowInsets = windowInsets,
    ) {
      Panel(
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp)
          .background(Color.Red)
          .then(panelModifier)
          .testTag(PanelTag),
        overscrollEffect = overscrollEffect,
      ) {
        Box(Modifier.requiredSize(1.dp))
      }
    }
  }
}

@Composable
private fun StartDrawerLayout(
  initialValue: DrawerValue,
  placement: DrawerPlacement = DrawerPlacement.Start,
  presentation: DrawerPresentation = DrawerPresentation.Inline,
  gesturesEnabled: Boolean = true,
  swipeArea: Boolean = false,
  overlay: (@Composable DrawerOverlayScope<DrawerValue>.() -> Unit)? = null,
  overscrollEffect: OverscrollEffect? = null,
  confirmValueChange: (DrawerValueChange<DrawerValue>) -> Boolean = { true },
  onState: (UnstyledDrawerState<DrawerValue>) -> Unit = {},
) {
  val snapPoints = DrawerSnapPoints<DrawerValue> {
    DrawerValue.Closed at DrawerSnapPoint.Zero
    DrawerValue.Open at DrawerSnapPoint.ContentSize
  }
  val state = remember {
    UnstyledDrawerState(
      initialValue = initialValue,
      snapPoints = snapPoints,
      confirmValueChange = confirmValueChange,
    )
  }
  onState(state)
  UnstyledDrawer(
    state = state,
    placement = placement,
    presentation = presentation,
    gesturesEnabled = gesturesEnabled,
    overlay = overlay,
  ) {
    Viewport(
      modifier = Modifier
        .requiredSize(100.dp)
        .testTag(ViewportTag),
    ) {
      Panel(
        modifier = Modifier
          .width(60.dp)
          .fillMaxHeight()
          .background(Color.Red)
          .testTag(PanelTag),
        overscrollEffect = overscrollEffect,
      ) {
        Box(Modifier.requiredSize(1.dp))
      }
    }
    if (swipeArea) {
      SwipeArea(
        modifier = Modifier
          .requiredSize(width = 24.dp, height = 100.dp)
          .testTag(SwipeAreaTag),
      )
    }
  }
}

private fun SemanticsNodeInteraction.boundsInRoot(): Rect {
  return fetchSemanticsNode().boundsInRoot
}

private class RecordingOverscrollEffect : OverscrollEffect {
  var scrollCalls = 0
  var flingCalls = 0

  override fun applyToScroll(
    delta: Offset,
    source: androidx.compose.ui.input.nestedscroll.NestedScrollSource,
    performScroll: (Offset) -> Offset,
  ): Offset {
    scrollCalls += 1
    return performScroll(delta)
  }

  override suspend fun applyToFling(
    velocity: Velocity,
    performFling: suspend (Velocity) -> Velocity,
  ) {
    flingCalls += 1
    performFling(velocity)
  }

  override val isInProgress: Boolean = false
}

private class RecordingAnimationSpec(
  private val delegate: AnimationSpec<Float> = snap(),
) : AnimationSpec<Float> {
  var vectorizeCalls = 0
    private set

  override fun <V : AnimationVector> vectorize(
    converter: TwoWayConverter<Float, V>,
  ): VectorizedAnimationSpec<V> {
    vectorizeCalls += 1
    return delegate.vectorize(converter)
  }
}

private enum class DrawerValue {
  Closed,
  Peek,
  Open,
}

private const val ViewportTag = "viewport"
private const val PanelTag = "panel"
private const val SwipeAreaTag = "swipe-area"
private const val OverlayTag = "overlay"
private const val DragHandleTag = "drag-handle"
private const val BackgroundTag = "background"
