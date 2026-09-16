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
import androidx.compose.ui.Alignment
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
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import kotlin.test.Test

class DrawerAndroidTest {
  @Test
  @SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
  fun overlayFadesInWhileDrawerOpens() = assertOverlayFadesInWhileDrawerOpens()

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
      DrawerHost {
        UnstyledDrawer(
          state = state,
          placement = DrawerPlacement.Start,
          presentation = DrawerPresentation.Overlay,
        ) {
          SwipeArea(Modifier.requiredSize(width = 24.dp, height = 100.dp))
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.width(60.dp).fillMaxHeight()) {
              Box(Modifier.requiredSize(1.dp))
            }
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
    drawerState.jumpTo(AndroidDrawerValue.Closed)
    waitUntil("closed overlay restores edge gesture exclusion") {
      composeView?.systemGestureExclusionRects?.any { rect ->
        rect.left == 0 && rect.width() > 0 && rect.height() > 0
      } == true
    }
  }

  @Test
  @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
  fun modalSwipeAreaDoesNotExcludeSystemGestures() = assertInactiveSwipeArea(
    DrawerPresentation.Modal,
  )

  @Test
  @SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
  fun inlineSwipeAreaDoesNotExcludeSystemGestures() = assertInactiveSwipeArea(
    DrawerPresentation.InPlace,
  )

  private fun assertInactiveSwipeArea(presentation: DrawerPresentation) = runComposeUiTest {
    var view: View? = null
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
      val localView = LocalView.current
      SideEffect { view = localView }
      UnstyledDrawer(state, presentation = presentation) {
        SwipeArea(Modifier.requiredSize(24.dp, 100.dp))
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.requiredSize(60.dp)) {}
        }
      }
    }
    waitForIdle()
    assertThat(view?.systemGestureExclusionRects?.isEmpty()).isEqualTo(true)
  }

  @Test
  fun onePixelOfDrawerVisibilityActivatesModalPresentation() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var hostView: View? = null
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
      hostView = LocalView.current
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

    assertThat(modalView).isNotEqualTo(hostView)
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
        val attachedToWindow = true
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
    val requestedStatusBarAppearance = SystemUiAppearance.Dark
    val requestedNavigationBarAppearance = SystemUiAppearance.Light
    val statusBarAppearance = mutableStateOf(requestedStatusBarAppearance)
    val navigationBarAppearance = mutableStateOf(requestedNavigationBarAppearance)

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
        systemUi = SystemUi(
          statusBar = statusBarAppearance.value,
          navigationBar = navigationBarAppearance.value,
        ),
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

    state.jumpTo(AndroidDrawerValue.Open)
    waitUntil {
      hasModalWindow &&
        state.currentValue == AndroidDrawerValue.Open &&
        WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
          .isAppearanceLightStatusBars ==
        (requestedStatusBarAppearance == SystemUiAppearance.Dark) &&
        WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
          .isAppearanceLightNavigationBars ==
        (requestedNavigationBarAppearance == SystemUiAppearance.Dark)
    }

    assertThat(hostController.isAppearanceLightStatusBars).isEqualTo(initialStatusBarAppearance)
    assertThat(hostController.isAppearanceLightNavigationBars)
      .isEqualTo(initialNavigationBarAppearance)

    state.jumpTo(AndroidDrawerValue.Closed)
    waitUntil {
      state.currentValue == AndroidDrawerValue.Closed &&
        WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
          .isAppearanceLightStatusBars == initialStatusBarAppearance &&
        WindowCompat.getInsetsController(modalWindow, modalWindow.decorView)
          .isAppearanceLightNavigationBars == initialNavigationBarAppearance
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
          presentation = DrawerPresentation.InPlace,
        ) {
          Viewport(Modifier.fillMaxSize(), windowInsets = imeInsets) {
            Panel(Modifier.fillMaxWidth().height(24.dp)) {}
          }
        }
        UnstyledDrawer(
          state = zeroState,
          modifier = Modifier.fillMaxSize(),
          presentation = DrawerPresentation.InPlace,
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
  fun modalDrawerDismissesAndReopensWhileKeyboardIsVisible() = runComposeUiTest {
    val state = UnstyledDrawerState(
      initialValue = AndroidDrawerValue.Open,
      snapPoints = DrawerSnapPoints {
        AndroidDrawerValue.Closed at DrawerSnapPoint.Zero
        AndroidDrawerValue.Open at DrawerSnapPoint.ContentSize
      },
    )
    var modalState: ModalState? = null
    var imeBottomPx = 0
    var dismissals = 0

    setContent {
      UnstyledDrawer(state, onDismissed = { dismissals++ }) {
        val hostState = LocalModalState.current
        val imeInsets = WindowInsets.ime
        val bottom = imeInsets.getBottom(LocalDensity.current)
        SideEffect {
          if (hostState.isAttachedToWindow) {
            modalState = hostState
            imeBottomPx = bottom
          }
        }
        Viewport(Modifier.fillMaxSize(), windowInsets = imeInsets) {
          Panel(Modifier.fillMaxWidth().height(200.dp)) {
            val text = remember { mutableStateOf("") }
            BasicTextField(
              value = text.value,
              onValueChange = { text.value = it },
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("form-input"),
            )
          }
        }
      }
    }

    try {
      // Exercise two distinct form sessions, not a frame-count-based readiness wait.
      repeat(2) { session ->
        waitUntil("modal form attached") { modalState?.isAttachedToWindow == true && state.isIdle }
        onNodeWithTag("form-input").performClick()
        waitUntil("form keyboard visible", timeoutMillis = 10_000) { imeBottomPx > 0 }
        state.targetValue = AndroidDrawerValue.Closed
        waitUntil("form dismissed and modal detached") {
          state.currentValue == AndroidDrawerValue.Closed && state.isIdle &&
            modalState?.isAttachedToWindow == false
        }
        assertThat(dismissals).isEqualTo(session + 1)
        if (session == 0) {
          imeBottomPx = 0
          state.targetValue = AndroidDrawerValue.Open
        }
      }
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
  fun closedInlineDrawerLetsBackReachTheHostAfterOpeningAndClosing() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var hostBackCount = 0
    setContent {
      EscapeHandler { hostBackCount += 1 }
      DrawerLayout(
        initialValue = AndroidDrawerValue.Closed,
        presentation = DrawerPresentation.InPlace,
        onState = { state = it },
      )
    }
    waitForIdle()
    Espresso.pressBack()
    waitForIdle()
    assertThat(hostBackCount).isEqualTo(1)
    state.targetValue = AndroidDrawerValue.Open
    waitUntil(
      "inline drawer open",
    ) { state.currentValue == AndroidDrawerValue.Open && state.isIdle }
    Espresso.pressBack()
    waitUntil("inline drawer closed") { state.isAtZeroValue && state.isIdle }
    assertThat(hostBackCount).isEqualTo(1)
    Espresso.pressBack()
    waitForIdle()
    assertThat(hostBackCount).isEqualTo(2)
  }

  @Test
  fun overlayBackFallsThroughWhenDismissOnNavigateBackIsFalse() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var fallbackBackHandlerCalled = false

    setContent {
      EscapeHandler {
        fallbackBackHandlerCalled = true
      }
      DrawerLayout(
        initialValue = AndroidDrawerValue.Open,
        presentation = DrawerPresentation.Overlay,
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
  fun overlayBackFallsThroughWhenThereIsNoZeroValue() = runComposeUiTest {
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
        presentation = DrawerPresentation.Overlay,
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

    Espresso.pressBackUnconditionally()
    waitUntil {
      upperDrawerState.currentValue == AndroidDrawerValue.Closed && upperDrawerState.isIdle
    }

    assertThat(lowerDrawerState.currentValue).isEqualTo(AndroidDrawerValue.Open)

    Espresso.pressBackUnconditionally()
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

    onNodeWithTag("viewport").performTouchInput {
      swipe(
        start = Offset(centerX, 40f),
        end = Offset(centerX, bottomRight.y - 1f),
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

    onNodeWithTag("viewport").performTouchInput {
      swipeWithVelocity(
        start = Offset(centerX, 40f),
        end = Offset(centerX, 140f),
        endVelocity = 1_000f,
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

    onNodeWithTag("viewport").performTouchInput {
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
  fun overlayRemainsVisibleWhenEdgeDragReturnsToClosedBeforeRelease() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<AndroidDrawerValue>
    var overlayVisibleAtClosedDragPosition = false
    setContent {
      DirectionalDrawerLayout(
        placement = DrawerPlacement.Start,
        initialValue = AndroidDrawerValue.Closed,
        presentation = DrawerPresentation.Overlay,
        overlay = {
          Overlay {
            SideEffect {
              overlayVisibleAtClosedDragPosition =
                state.isEdgeSwipeInProgress && state.offset <= 0.5f
            }
          }
        },
        onState = { state = it },
      )
    }
    waitForIdle()
    mainClock.autoAdvance = false
    try {
      state.isEdgeSwipeInProgress = true
      state.anchoredDraggableState.dispatchRawDelta(1f)
      state.anchoredDraggableState.dispatchRawDelta(-1f)
      waitUntil("overlay visible while edge drag remains held at closed") {
        mainClock.advanceTimeByFrame()
        overlayVisibleAtClosedDragPosition
      }
      assertThat(overlayVisibleAtClosedDragPosition).isEqualTo(true)
    } finally {
      state.isEdgeSwipeInProgress = false
      state.jumpTo(AndroidDrawerValue.Closed)
      mainClock.autoAdvance = true
    }
    waitUntil("drawer settled after releasing edge drag") { state.isIdle }
  }

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
          presentation = DrawerPresentation.Overlay,
          onState = { state = it },
        )
      }
      waitForIdle()

      onNodeWithTag("swipe-area").performTouchInput {
        val (start, end) = when (placement) {
          DrawerPlacement.Start -> Offset(0f, centerY) to Offset(100f, centerY)
          DrawerPlacement.End -> Offset(bottomRight.x, centerY) to Offset(-100f, centerY)
          DrawerPlacement.Top -> Offset(centerX, 0f) to Offset(centerX, 100f)
          DrawerPlacement.Bottom -> Offset(centerX, bottomRight.y) to Offset(centerX, -100f)
          else -> error("Unsupported drawer placement: $placement")
        }
        if (placement == DrawerPlacement.Bottom) {
          // Keep the initiating stream alive after the drawer becomes visible. A modal
          // drawer must not replace the SwipeArea with a touchable host mid-drag.
          down(start)
          repeat(10) { index ->
            val progress = (index + 1) * 0.05f
            moveTo(start + (end - start) * progress, delayMillis = 100)
          }
          moveTo(end, delayMillis = 500)
          up()
        } else {
          swipe(start = start, end = end, durationMillis = 500)
        }
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
  presentation: DrawerPresentation = DrawerPresentation.Modal,
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
  DrawerHost(Modifier.requiredSize(width = 100.dp, height = viewportHeight)) {
    UnstyledDrawer(
      state = state,
      placement = DrawerPlacement.Bottom,
      presentation = presentation,
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
}

@Composable
private fun DirectionalDrawerLayout(
  placement: DrawerPlacement,
  initialValue: AndroidDrawerValue,
  presentation: DrawerPresentation = DrawerPresentation.Modal,
  dismissOnClickOutside: Boolean = true,
  overlay: (@Composable DrawerOverlayScope<AndroidDrawerValue>.() -> Unit)? = null,
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
  DrawerHost(Modifier.requiredSize(200.dp)) {
    UnstyledDrawer(
      state = state,
      placement = placement,
      presentation = presentation,
      dismissOnClickOutside = dismissOnClickOutside,
      overlay = overlay,
    ) {
      Box(Modifier.requiredSize(200.dp)) {
        val swipeAreaModifier = when (placement) {
          DrawerPlacement.Start ->
            Modifier
              .align(Alignment.CenterStart)
              .requiredSize(width = 24.dp, height = 200.dp)
          DrawerPlacement.End ->
            Modifier
              .align(Alignment.CenterEnd)
              .requiredSize(width = 24.dp, height = 200.dp)
          DrawerPlacement.Top ->
            Modifier
              .align(Alignment.TopCenter)
              .requiredSize(width = 200.dp, height = 24.dp)
          DrawerPlacement.Bottom ->
            Modifier
              .align(Alignment.BottomCenter)
              .requiredSize(width = 200.dp, height = 24.dp)
          else -> error("Unsupported drawer placement: $placement")
        }
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
        SwipeArea(swipeAreaModifier.testTag("swipe-area"))
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
