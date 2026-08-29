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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeWithVelocity
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.test.espresso.Espresso
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import kotlin.test.Test

class DrawerAndroidTest {

  @Test
  @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
  fun closedStartDrawerExcludesItsEdgeHandleFromSystemBack() = runComposeUiTest {
    var composeView: View? = null
    lateinit var drawerState: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      val state = remember {
        UnstyledDrawerState(
          initialValue = AndroidDrawerValue.Closed,
          snapPoints = DrawerSnapPoints {
            AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
            AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
          },
        )
      }
      drawerState = state
      val view = LocalView.current
      SideEffect {
        composeView = view
      }
      UnstyledDrawer(
        state = state,
        placement = DrawerPlacement.Start,
      ) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.width(60.dp).fillMaxHeight()) {
            Box(Modifier.requiredSize(1.dp))
          }
        }
      }
    }

    waitUntil {
      composeView?.systemGestureExclusionRects?.any { rect ->
        rect.left == 0 && rect.width() > 0 && rect.height() > 0
      } == true
    }

    assertThat(
      composeView?.systemGestureExclusionRects?.any { rect ->
        rect.left == 0 && rect.width() > 0 && rect.height() > 0
      },
    ).isEqualTo(true)

    drawerState.jumpTo(AndroidDrawerValue.Open)
    waitUntil {
      composeView?.systemGestureExclusionRects
        ?.any { rect -> rect.left == 0 && rect.width() > 0 && rect.height() > 0 } == false
    }
  }

  @Test
  fun onePixelOfDrawerVisibilityActivatesModalPresentation() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var modalView: View? = null

    setContent {
      val drawerState = remember {
        UnstyledDrawerState(
          initialValue = AndroidDrawerValue.Closed,
          snapPoints = DrawerSnapPoints {
            AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
            AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
          },
        )
      }
      state = drawerState
      Column {
        Box(
          Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(Color.Blue),
        )
        UnstyledDrawer(
          state = drawerState,
          placement = DrawerPlacement.Start,
        ) {
          if (LocalModalState.current.isAttachedToWindow) {
            val view = LocalView.current
            SideEffect {
              modalView = view
            }
          }
          Viewport(Modifier.fillMaxSize().testTag("edge-viewport")) {
            Panel(
              Modifier
                .fillMaxSize()
                .background(Color.Red)
                .testTag("edge-panel"),
            ) {
              Box(Modifier.requiredSize(1.dp))
            }
          }
        }
      }
    }
    waitForIdle()

    runOnIdle {
      state.anchoredDraggableState.dispatchRawDelta(1f)
    }
    waitUntil { state.offset > 0f }
    waitUntil { modalView != null }
    waitForIdle()

    val image = InstrumentationRegistry.getInstrumentation()
      .uiAutomation
      .takeScreenshot()
    assertThat(Color(image.getPixel(10, 60))).isNotEqualTo(Color.Blue)
  }

  @Test
  fun openingAnimationWaitsForModalLayoutAndMovesMonotonically() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    val firstModalFrameOffset = mutableStateOf<Float?>(null)
    val openingOffsets = mutableListOf<Float>()
    val openingPanelTops = mutableListOf<Float>()

    setContent {
      val drawerState = remember {
        UnstyledDrawerState(
          initialValue = AndroidDrawerValue.Closed,
          snapPoints = DrawerSnapPoints {
            AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
            AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
          },
        )
      }
      SideEffect {
        state = drawerState
      }
      LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.offset }
          .collect { offset ->
            if (drawerState.targetValue == AndroidDrawerValue.Open) {
              openingOffsets += offset
            }
          }
      }

      UnstyledDrawer(
        state = drawerState,
        placement = DrawerPlacement.Bottom,
      ) {
        val attachedToWindow = LocalModalState.current.isAttachedToWindow
        LaunchedEffect(attachedToWindow) {
          if (attachedToWindow) {
            withFrameNanos { }
            firstModalFrameOffset.value = drawerState.offset
          }
        }
        Viewport(
          Modifier.fillMaxSize(),
          windowInsets = WindowInsets.ime.union(WindowInsets.navigationBars),
        ) {
          Panel(
            Modifier
              .fillMaxWidth()
              .height(300.dp)
              .onGloballyPositioned { coordinates ->
                if (
                  drawerState.targetValue == AndroidDrawerValue.Open &&
                  drawerState.hasVisiblePanel
                ) {
                  val panelTop = coordinates.positionInRoot().y
                  openingPanelTops += panelTop
                }
              },
          ) {}
        }
      }
    }
    waitForIdle()

    state.targetValue = AndroidDrawerValue.Open
    waitUntil { firstModalFrameOffset.value != null }
    waitUntil { state.currentValue == AndroidDrawerValue.Open && state.isIdle }

    assertThat(firstModalFrameOffset.value).isEqualTo(0f)
    assertThat(
      openingOffsets.zipWithNext().all { (previous, next) -> next >= previous },
    ).isEqualTo(true)
    assertThat(openingPanelTops).isEqualTo(openingPanelTops.sortedDescending())
  }

  @Test
  fun modalDrawerAppliesAndRestoresSystemBarIconAppearance() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    lateinit var hostWindow: Window
    lateinit var modalWindow: Window
    var hasModalWindow = false
    val statusBarAppearance = mutableStateOf(SystemBarIconAppearance.Unspecified)
    val navigationBarAppearance = mutableStateOf(SystemBarIconAppearance.Unspecified)

    setContent {
      val context = LocalContext.current
      val drawerState = remember {
        UnstyledDrawerState(
          initialValue = AndroidDrawerValue.Closed,
          snapPoints = DrawerSnapPoints {
            AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
            AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
          },
        )
      }
      SideEffect {
        state = drawerState
        hostWindow = context.findActivity().window
      }

      UnstyledDrawer(
        state = drawerState,
        placement = DrawerPlacement.Bottom,
        presentation = DrawerPresentation.Modal,
      ) {
        if (
          drawerState.currentValue != AndroidDrawerValue.Closed ||
          drawerState.targetValue != AndroidDrawerValue.Closed
        ) {
          val activeModalWindow = LocalModalWindow.current
          SideEffect {
            modalWindow = activeModalWindow
            hasModalWindow = true
          }
        }
        LaunchedEffect(statusBarAppearance.value, navigationBarAppearance.value) {
          androidSystemUi.setAppearance(
            statusBar = statusBarAppearance.value,
            navigationBar = navigationBarAppearance.value,
          )
        }
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().height(100.dp))
          }
        }
      }
    }

    waitForIdle()

    val hostController = WindowCompat.getInsetsController(hostWindow, hostWindow.decorView)
    val initialStatusBarAppearance = hostController.isAppearanceLightStatusBars
    val initialNavigationBarAppearance = hostController.isAppearanceLightNavigationBars
    val supportsNavigationBarIconAppearance = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    statusBarAppearance.value = if (initialStatusBarAppearance) {
      SystemBarIconAppearance.Light
    } else {
      SystemBarIconAppearance.Dark
    }
    navigationBarAppearance.value = if (supportsNavigationBarIconAppearance) {
      if (initialNavigationBarAppearance) {
        SystemBarIconAppearance.Light
      } else {
        SystemBarIconAppearance.Dark
      }
    } else {
      SystemBarIconAppearance.Unspecified
    }

    state.jumpTo(AndroidDrawerValue.Open)
    waitUntil {
      state.currentValue == AndroidDrawerValue.Open &&
        hasModalWindow &&
        WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
          .isAppearanceLightStatusBars != initialStatusBarAppearance &&
        (
          supportsNavigationBarIconAppearance.not() ||
            WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
              .isAppearanceLightNavigationBars != initialNavigationBarAppearance
          )
    }

    assertThat(hostController.isAppearanceLightStatusBars).isEqualTo(initialStatusBarAppearance)
    assertThat(hostController.isAppearanceLightNavigationBars)
      .isEqualTo(initialNavigationBarAppearance)

    state.jumpTo(AndroidDrawerValue.Closed)
    waitUntil {
      state.currentValue == AndroidDrawerValue.Closed &&
        WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
          .isAppearanceLightStatusBars == initialStatusBarAppearance &&
        (
          supportsNavigationBarIconAppearance.not() ||
            WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
              .isAppearanceLightNavigationBars == initialNavigationBarAppearance
          )
    }
  }

  @Test
  fun viewportConsumesRealImeInsetsWhileTheSoftKeyboardIsVisible() = runComposeUiTest {
    lateinit var imeInsetDrawerState: UnstyledDrawerState<AndroidDrawerValue>
    lateinit var zeroInsetDrawerState: UnstyledDrawerState<AndroidDrawerValue>
    var imeBottomPx = 0

    setContent {
      val activity = LocalContext.current.findActivity()
      val snapPoints = remember {
        DrawerSnapPoints {
          AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      val imeState = remember { UnstyledDrawerState(AndroidDrawerValue.Open, snapPoints) }
      val zeroState = remember { UnstyledDrawerState(AndroidDrawerValue.Open, snapPoints) }
      val imeInsets = WindowInsets.ime
      val currentImeBottomPx = imeInsets.getBottom(LocalDensity.current)
      val text = remember { mutableStateOf("") }
      DisposableEffect(activity) {
        val window = activity.window
        val previousSoftInputMode = window.attributes.softInputMode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        onDispose {
          WindowCompat.setDecorFitsSystemWindows(window, true)
          window.setSoftInputMode(previousSoftInputMode)
        }
      }
      SideEffect {
        imeInsetDrawerState = imeState
        zeroInsetDrawerState = zeroState
        imeBottomPx = currentImeBottomPx
      }

      Box(Modifier.fillMaxSize()) {
        UnstyledDrawer(
          state = imeState,
          modifier = Modifier.fillMaxSize(),
          presentation = DrawerPresentation.Inline,
        ) {
          Viewport(Modifier.fillMaxSize(), windowInsets = imeInsets) {
            Panel(Modifier.fillMaxWidth().height(24.dp)) {}
          }
        }
        UnstyledDrawer(
          state = zeroState,
          modifier = Modifier.fillMaxSize(),
          presentation = DrawerPresentation.Inline,
        ) {
          Viewport(Modifier.fillMaxSize(), windowInsets = WindowInsets()) {
            Panel(Modifier.fillMaxWidth().height(24.dp)) {}
          }
        }
        BasicTextField(
          value = text.value,
          onValueChange = { text.value = it },
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("ime-input"),
        )
      }
    }
    waitForIdle()
    assertThat(imeInsetDrawerState.viewportSizePx)
      .isEqualTo(zeroInsetDrawerState.viewportSizePx)

    try {
      onNodeWithTag("ime-input").performClick()
      waitUntil(timeoutMillis = 10_000) { imeBottomPx > 0 }
      waitUntil {
        zeroInsetDrawerState.viewportSizePx - imeInsetDrawerState.viewportSizePx ==
          imeBottomPx.toFloat()
      }

      assertThat(zeroInsetDrawerState.viewportSizePx - imeInsetDrawerState.viewportSizePx)
        .isEqualTo(imeBottomPx.toFloat())
    } finally {
      Espresso.closeSoftKeyboard()
    }
  }

  @Test
  fun pressingBackClosesOpenDrawerWhenZeroValueExists() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        onState = { state = it },
      )
    }

    Espresso.pressBack()

    waitUntil {
      state.currentValue == AndroidDrawerValue.Closed
    }
    assertThat(state.currentValue).isEqualTo(AndroidDrawerValue.Closed)
  }

  @Test
  fun pressingBackFallsThroughWhenDismissOnNavigateBackIsFalse() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var fallbackBackHandlerCalled = false

    setContent {
      EscapeHandler {
        fallbackBackHandlerCalled = true
      }
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        dismissOnNavigateBack = false,
        onState = { state = it },
      )
    }

    Espresso.pressBack()
    waitForIdle()

    assertThat(fallbackBackHandlerCalled).isEqualTo(true)
    assertThat(state.currentValue).isEqualTo(AndroidDrawerValue.Open)
  }

  @Test
  fun pressingBackFallsThroughWhenThereIsNoZeroValue() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var fallbackBackHandlerCalled = false
    val snapPoints = DrawerSnapPoints<AndroidDrawerValue> {
      AndroidDrawerValue.Peek at DrawerSnapPoint { viewportSize, _ -> viewportSize / 2f }
      AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
    }

    setContent {
      EscapeHandler {
        fallbackBackHandlerCalled = true
      }
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        snapPoints = snapPoints,
        onState = { state = it },
      )
    }

    Espresso.pressBack()
    waitForIdle()

    assertThat(fallbackBackHandlerCalled).isEqualTo(true)
    assertThat(state.currentValue).isEqualTo(AndroidDrawerValue.Open)
  }

  @Test
  fun pressingBackDismissesOnlyTheTopmostModalDrawer() = runComposeUiTest {
    lateinit var lowerDrawerState: UnstyledDrawerState<AndroidDrawerValue>
    lateinit var upperDrawerState: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        onState = { lowerDrawerState = it },
      )
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        onState = { upperDrawerState = it },
      )
    }
    waitForIdle()

    Espresso.pressBack()
    waitUntil {
      upperDrawerState.currentValue == AndroidDrawerValue.Closed && upperDrawerState.isIdle
    }

    assertThat(lowerDrawerState.currentValue).isEqualTo(AndroidDrawerValue.Open)

    Espresso.pressBack()
    waitUntil {
      lowerDrawerState.currentValue == AndroidDrawerValue.Closed && lowerDrawerState.isIdle
    }
  }

  @Test
  fun touchDragDismissesBottomDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(
        start = Offset(centerX, 40f),
        end = Offset(centerX, 99f),
        durationMillis = 500,
      )
    }
    waitUntil { state.currentValue == AndroidDrawerValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(AndroidDrawerValue.Closed)
  }

  @Test
  fun touchDragEnteringBottomPanelFromOutsideDismissesIt() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        dismissOnClickOutside = false,
        viewportHeight = 200.dp,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag("viewport").performTouchInput {
      down(Offset(centerX, 10f))
      moveTo(Offset(centerX, centerY + 1f), delayMillis = 250)
      moveTo(Offset(centerX, bottomRight.y - 10f), delayMillis = 250)
      up()
    }
    waitUntil { state.currentValue == AndroidDrawerValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(AndroidDrawerValue.Closed)
  }

  @Test
  fun fastTouchFlingSettlesClosedEvenWhenReleaseIsNearOpen() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipeWithVelocity(
        start = Offset(centerX, 40f),
        end = Offset(centerX, 64f),
        endVelocity = 300f,
      )
    }
    waitUntil { state.currentValue == AndroidDrawerValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(AndroidDrawerValue.Closed)
  }

  @Test
  fun disabledGesturesIgnoreTouchDrag() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

    setContent {
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        gesturesEnabled = false,
        onState = { state = it },
      )
    }
    waitForIdle()

    onNodeWithTag("panel").performTouchInput {
      swipe(
        start = Offset(centerX, 40f),
        end = Offset(centerX, 99f),
        durationMillis = 500,
      )
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(AndroidDrawerValue.Open)
  }

  @Test
  fun edgeSwipeOpensStartDrawer() = edgeSwipeOpensDrawer(DrawerPlacement.Start)

  @Test
  fun edgeSwipeOpensEndDrawer() = edgeSwipeOpensDrawer(DrawerPlacement.End)

  @Test
  fun edgeSwipeOpensTopDrawer() = edgeSwipeOpensDrawer(DrawerPlacement.Top)

  @Test
  fun edgeSwipeOpensBottomDrawer() = edgeSwipeOpensDrawer(DrawerPlacement.Bottom)

  @Test
  fun panelDragDismissesStartDrawer() = panelDragDismissesDrawer(DrawerPlacement.Start)

  @Test
  fun panelDragDismissesEndDrawer() = panelDragDismissesDrawer(DrawerPlacement.End)

  @Test
  fun panelDragDismissesTopDrawer() = panelDragDismissesDrawer(DrawerPlacement.Top)

  @Test
  fun panelDragDismissesBottomDrawer() = panelDragDismissesDrawer(DrawerPlacement.Bottom)

  @Test
  fun outsideDragEnteringStartPanelDismissesDrawer() =
    outsideDragEnteringPanelDismissesDrawer(DrawerPlacement.Start)

  @Test
  fun outsideDragEnteringEndPanelDismissesDrawer() =
    outsideDragEnteringPanelDismissesDrawer(DrawerPlacement.End)

  @Test
  fun outsideDragEnteringTopPanelDismissesDrawer() =
    outsideDragEnteringPanelDismissesDrawer(DrawerPlacement.Top)

  @Test
  fun outsideDragEnteringBottomPanelDismissesDrawer() =
    outsideDragEnteringPanelDismissesDrawer(DrawerPlacement.Bottom)

  private fun edgeSwipeOpensDrawer(placement: DrawerPlacement) {
    runComposeUiTest {
      lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

      setContent {
        DirectionalDrawerLayout(
          placement = placement,
          initialValue = AndroidDrawerValue.Closed,
          onState = { state = it },
        )
      }
      waitForIdle()

      onNodeWithTag("directional-viewport").performTouchInput {
        val (start, end) = when (placement) {
          DrawerPlacement.Start -> Offset(0f, centerY) to Offset(centerX, centerY)
          DrawerPlacement.End -> Offset(bottomRight.x, centerY) to Offset(centerX, centerY)
          DrawerPlacement.Top -> Offset(centerX, 0f) to Offset(centerX, centerY)
          DrawerPlacement.Bottom -> Offset(centerX, bottomRight.y) to Offset(centerX, centerY)
          else -> error("Unsupported drawer placement: $placement")
        }
        swipe(start = start, end = end, durationMillis = 500)
      }
      waitUntil { state.currentValue == AndroidDrawerValue.Open && state.isIdle }
    }
  }

  private fun panelDragDismissesDrawer(placement: DrawerPlacement) {
    runComposeUiTest {
      lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

      setContent {
        DirectionalDrawerLayout(
          placement = placement,
          initialValue = AndroidDrawerValue.Open,
          onState = { state = it },
        )
      }
      waitForIdle()

      onNodeWithTag("directional-viewport").performTouchInput {
        val (start, end) = when (placement) {
          DrawerPlacement.Start -> Offset(centerX / 2f, centerY) to Offset(0f, centerY)
          DrawerPlacement.End -> Offset(centerX * 1.5f, centerY) to Offset(bottomRight.x, centerY)
          DrawerPlacement.Top -> Offset(centerX, centerY / 2f) to Offset(centerX, 0f)
          DrawerPlacement.Bottom ->
            Offset(centerX, centerY * 1.5f) to Offset(centerX, bottomRight.y)
          else -> error("Unsupported drawer placement: $placement")
        }
        swipeWithVelocity(start = start, end = end, endVelocity = 1_000f)
      }
      waitUntil { state.currentValue == AndroidDrawerValue.Closed && state.isIdle }
    }
  }

  private fun outsideDragEnteringPanelDismissesDrawer(placement: DrawerPlacement) {
    runComposeUiTest {
      lateinit var state: UnstyledDrawerState<AndroidDrawerValue>

      setContent {
        DirectionalDrawerLayout(
          placement = placement,
          initialValue = AndroidDrawerValue.Open,
          dismissOnClickOutside = false,
          onState = { state = it },
        )
      }
      waitForIdle()

      onNodeWithTag("directional-viewport").performTouchInput {
        val (start, entry, end) = when (placement) {
          DrawerPlacement.Start -> Triple(
            Offset(bottomRight.x, centerY),
            Offset(centerX - 1f, centerY),
            Offset(0f, centerY),
          )
          DrawerPlacement.End -> Triple(
            Offset(0f, centerY),
            Offset(centerX + 1f, centerY),
            Offset(bottomRight.x, centerY),
          )
          DrawerPlacement.Top -> Triple(
            Offset(centerX, bottomRight.y),
            Offset(centerX, centerY - 1f),
            Offset(centerX, 0f),
          )
          DrawerPlacement.Bottom -> Triple(
            Offset(centerX, 0f),
            Offset(centerX, centerY + 1f),
            Offset(centerX, bottomRight.y),
          )
          else -> error("Unsupported drawer placement: $placement")
        }
        down(start)
        moveTo(entry, delayMillis = 250)
        moveTo(end, delayMillis = 250)
        up()
      }
      waitUntil { state.currentValue == AndroidDrawerValue.Closed && state.isIdle }
    }
  }
}

@Composable
private fun DrawerLayout(
  initialValue: AndroidDrawerValue,
  snapPoints: DrawerSnapPoints<AndroidDrawerValue> = DrawerSnapPoints {
    AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
    AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
  },
  dismissOnNavigateBack: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  gesturesEnabled: Boolean = true,
  viewportHeight: Dp = 100.dp,
  onState: (UnstyledDrawerState<AndroidDrawerValue>) -> Unit = {},
) {
  val state = remember {
    UnstyledDrawerState(
      initialValue = initialValue,
      snapPoints = snapPoints,
    )
  }
  onState(state)
  UnstyledDrawer(
    state = state,
    placement = DrawerPlacement.Bottom,
    gesturesEnabled = gesturesEnabled,
    dismissOnNavigateBack = dismissOnNavigateBack,
    dismissOnClickOutside = dismissOnClickOutside,
  ) {
    Viewport(Modifier.requiredSize(width = 100.dp, height = viewportHeight).testTag("viewport")) {
      Panel(Modifier.fillMaxWidth().testTag("panel")) {
        Box(Modifier.fillMaxWidth().height(100.dp))
      }
    }
  }
}

@Composable
private fun DirectionalDrawerLayout(
  placement: DrawerPlacement,
  initialValue: AndroidDrawerValue,
  dismissOnClickOutside: Boolean = true,
  onState: (UnstyledDrawerState<AndroidDrawerValue>) -> Unit = {},
) {
  val state = remember {
    UnstyledDrawerState(
      initialValue = initialValue,
      snapPoints = DrawerSnapPoints {
        AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
        AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
      },
    )
  }
  onState(state)
  UnstyledDrawer(
    state = state,
    placement = placement,
    dismissOnClickOutside = dismissOnClickOutside,
  ) {
    Viewport(
      Modifier
        .requiredSize(200.dp)
        .testTag("directional-viewport"),
    ) {
      val panelModifier = if (
        placement == DrawerPlacement.Start || placement == DrawerPlacement.End
      ) {
        Modifier.width(100.dp).fillMaxHeight()
      } else {
        Modifier.fillMaxWidth().height(100.dp)
      }
      Panel(panelModifier.testTag("directional-panel")) {
        Box(Modifier.requiredSize(1.dp))
      }
    }
  }
}

private enum class AndroidDrawerValue {
  Closed,
  Peek,
  Open,
}

private tailrec fun Context.findActivity(): Activity = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> error("Drawer tests require an Activity context.")
}
