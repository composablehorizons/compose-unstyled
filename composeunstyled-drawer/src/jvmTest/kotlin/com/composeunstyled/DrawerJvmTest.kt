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

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class DrawerJvmTest {
  @Test
  fun drawerRejectsDuplicateViewports() = runComposeUiTest {
    val result = runCatching {
      setContent {
        val state = remember {
          UnstyledDrawerState(
            DrawerJvmValue.Closed,
            DrawerSnapPoints { DrawerJvmValue.Closed at DrawerSnapPoint.Zero },
          )
        }
        UnstyledDrawer(state, presentation = DrawerPresentation.Inline) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.fillMaxWidth().height(10.dp)) {}
          }
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
  fun escapeDismissesAnOpenModalDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerJvmValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
      UnstyledDrawer(
        state = state,
        placement = DrawerPlacement.Start,
        presentation = DrawerPresentation.Modal,
      ) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.width(60.dp).fillMaxHeight().testTag("panel")) {
            Box(Modifier.requiredSize(1.dp).focusable().testTag("focus-target"))
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("focus-target").requestFocus()
    onNodeWithTag("focus-target").performKeyInput {
      keyDown(Key.Escape)
      keyUp(Key.Escape)
    }
    waitUntil { state.currentValue == DrawerJvmValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerJvmValue.Closed)
  }

  @Test
  fun openingAModalDrawerDoesNotMoveFocusIntoTheDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerJvmValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerJvmValue.Closed, snapPoints) }
      Box(Modifier.requiredSize(100.dp)) {
        Box(Modifier.focusable().testTag("outside-focus-target"))
        UnstyledDrawer(
          state = state,
          placement = DrawerPlacement.Start,
          presentation = DrawerPresentation.Modal,
        ) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.width(60.dp).fillMaxHeight().testTag("panel")) {
              Box(Modifier.focusable().testTag("drawer-focus-target"))
            }
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("outside-focus-target").requestFocus()
    onNodeWithTag("outside-focus-target").assertIsFocused()

    state.jumpTo(DrawerJvmValue.Open)
    waitUntil { state.currentValue == DrawerJvmValue.Open && state.isIdle }

    onNodeWithTag("outside-focus-target").assertIsFocused()
  }

  @Test
  fun zeroSizedInlineDrawerContentIsNotReachableByKeyboardFocus() = runComposeUiTest {
    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      val state = remember { UnstyledDrawerState(DrawerJvmValue.Closed, snapPoints) }
      Box(Modifier.requiredSize(100.dp)) {
        Box(Modifier.focusable().testTag("outside-focus-target"))
        UnstyledDrawer(
          state = state,
          placement = DrawerPlacement.Start,
          presentation = DrawerPresentation.Inline,
        ) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.width(60.dp).fillMaxHeight()) {
              Box(Modifier.focusable().testTag("hidden-drawer-focus-target"))
            }
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("outside-focus-target").requestFocus()
    onNodeWithTag("outside-focus-target").performKeyInput {
      keyDown(Key.Tab)
      keyUp(Key.Tab)
    }

    onNodeWithTag("hidden-drawer-focus-target").assertIsNotFocused()
  }

  @Test
  fun visibleInlineDrawerContentRemainsInTheNormalTabOrder() = runComposeUiTest {
    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      val state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
      Box(Modifier.requiredSize(100.dp)) {
        Box(Modifier.focusable().testTag("outside-focus-target"))
        UnstyledDrawer(
          state = state,
          placement = DrawerPlacement.Start,
          presentation = DrawerPresentation.Inline,
        ) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.width(60.dp).fillMaxHeight()) {
              Box(Modifier.focusable().testTag("drawer-focus-target"))
            }
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("outside-focus-target").requestFocus()
    onNodeWithTag("outside-focus-target").performKeyInput {
      keyDown(Key.Tab)
      keyUp(Key.Tab)
    }

    onNodeWithTag("drawer-focus-target").assertIsFocused()
  }

  @Test
  fun dismissingAModalDrawerRestoresFocusAfterFocusEnteredTheDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerJvmValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
      Box(Modifier.requiredSize(100.dp)) {
        Box(Modifier.focusable().testTag("outside-focus-target"))
        UnstyledDrawer(
          state = state,
          placement = DrawerPlacement.Start,
          presentation = DrawerPresentation.Modal,
        ) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.width(60.dp).fillMaxHeight().testTag("panel")) {
              Box(Modifier.focusable().testTag("drawer-focus-target"))
            }
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("outside-focus-target").requestFocus()
    onNodeWithTag("drawer-focus-target").requestFocus()
    onNodeWithTag("drawer-focus-target").assertIsFocused()

    state.jumpTo(DrawerJvmValue.Closed)
    waitUntil { state.currentValue == DrawerJvmValue.Closed && state.isIdle }

    onNodeWithTag("outside-focus-target").assertIsFocused()
  }

  @Test
  fun tabFromAModalDrawerDoesNotReachSurroundingContent() = runComposeUiTest {
    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      val state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
      Box(Modifier.requiredSize(100.dp)) {
        Box(Modifier.focusable().testTag("outside-focus-target"))
        UnstyledDrawer(
          state = state,
          placement = DrawerPlacement.Start,
          presentation = DrawerPresentation.Modal,
        ) {
          Viewport(Modifier.requiredSize(100.dp)) {
            Panel(Modifier.width(60.dp).fillMaxHeight()) {
              Box(Modifier.focusable().testTag("drawer-first-focus-target"))
              Box(Modifier.focusable().testTag("drawer-last-focus-target"))
            }
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("drawer-last-focus-target").requestFocus()
    onNodeWithTag("drawer-last-focus-target").performKeyInput {
      keyDown(Key.Tab)
      keyUp(Key.Tab)
    }

    onNodeWithTag("outside-focus-target").assertIsNotFocused()
  }

  @Test
  fun primaryMouseDragDismissesAVisibleDrawer() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerJvmValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
      UnstyledDrawer(
        state = state,
        placement = DrawerPlacement.Start,
        presentation = DrawerPresentation.Inline,
      ) {
        Viewport(Modifier.requiredSize(100.dp)) {
          Panel(Modifier.width(60.dp).fillMaxHeight().testTag("panel")) {
            Box(Modifier.requiredSize(1.dp))
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("panel").performMouseInput {
      updatePointerTo(Offset(45f, centerY))
      press()
      moveTo(Offset(1f, centerY))
      release()
    }
    waitUntil { state.currentValue == DrawerJvmValue.Closed && state.isIdle }

    assertThat(state.targetValue).isEqualTo(DrawerJvmValue.Closed)
  }

  @Test
  fun closedEdgeSwipeDoesNotOpenTheDrawerWithAMouse() = runComposeUiTest {
    lateinit var state: UnstyledDrawerState<DrawerJvmValue>

    setContent {
      val snapPoints = remember {
        DrawerSnapPoints {
          DrawerJvmValue.Closed at DrawerSnapPoint.Zero
          DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
        }
      }
      state = remember { UnstyledDrawerState(DrawerJvmValue.Closed, snapPoints) }
      UnstyledDrawer(
        state = state,
        placement = DrawerPlacement.Start,
        presentation = DrawerPresentation.Inline,
      ) {
        Viewport(Modifier.requiredSize(100.dp).testTag("viewport")) {
          Panel(
            Modifier
              .width(60.dp)
              .fillMaxHeight()
              .background(Color.Red),
          ) {
            Box(Modifier.requiredSize(1.dp))
          }
        }
      }
    }
    waitForIdle()

    onNodeWithTag("viewport").performMouseInput {
      updatePointerTo(Offset(1f, centerY))
      press()
      moveTo(Offset(width - 1f, centerY))
      release()
    }
    waitForIdle()

    assertThat(state.currentValue).isEqualTo(DrawerJvmValue.Closed)
  }

  @Test
  fun panelPaddingDismissesModalDrawerWhenOutsideClicksAreDisabled() =
    runComposeUiTest {
      lateinit var state: UnstyledDrawerState<DrawerJvmValue>

      setContent {
        val snapPoints = remember {
          DrawerSnapPoints {
            DrawerJvmValue.Closed at DrawerSnapPoint.Zero
            DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
          }
        }
        state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
        Box(Modifier.requiredSize(100.dp).testTag("root")) {
          UnstyledDrawer(
            state = state,
            placement = DrawerPlacement.Start,
            presentation = DrawerPresentation.Modal,
            dismissOnClickOutside = false,
          ) {
            Viewport(Modifier.requiredSize(100.dp).testTag("viewport")) {
              Panel(
                Modifier
                  .width(60.dp)
                  .fillMaxHeight()
                  .background(Color.Red)
                  .padding(12.dp),
              ) {
                Box(Modifier.requiredSize(1.dp))
              }
            }
          }
        }
      }
      waitForIdle()

      onNodeWithTag("root").performMouseInput {
        // Start in the panel's visible trailing padding, as callers commonly do.
        updatePointerTo(Offset(52f, centerY))
        press()
        moveTo(Offset(1f, centerY))
        release()
      }
      waitForIdle()

      assertThat(state.currentValue).isEqualTo(DrawerJvmValue.Closed)
    }

  @Test
  fun tappingPanelTrailingEdgeDoesNotDismissModalDrawer() =
    runComposeUiTest {
      lateinit var state: UnstyledDrawerState<DrawerJvmValue>

      setContent {
        val snapPoints = remember {
          DrawerSnapPoints {
            DrawerJvmValue.Closed at DrawerSnapPoint.Zero
            DrawerJvmValue.Open at DrawerSnapPoint.ContentSize
          }
        }
        state = remember { UnstyledDrawerState(DrawerJvmValue.Open, snapPoints) }
        Box(Modifier.requiredSize(100.dp).testTag("root")) {
          UnstyledDrawer(
            state = state,
            placement = DrawerPlacement.Start,
            presentation = DrawerPresentation.Modal,
          ) {
            Viewport(Modifier.requiredSize(100.dp).testTag("viewport")) {
              Panel(
                Modifier
                  .width(60.dp)
                  .fillMaxHeight()
                  .background(Color.Red),
              ) {
                Box(Modifier.requiredSize(1.dp))
              }
            }
          }
        }
      }
      waitForIdle()

      onNodeWithTag("root").performMouseInput {
        updatePointerTo(Offset(60f, centerY))
        press()
        release()
      }
      waitForIdle()

      assertThat(state.currentValue).isEqualTo(DrawerJvmValue.Open)
      assertThat(state.targetValue).isEqualTo(DrawerJvmValue.Open)
    }
}

private enum class DrawerJvmValue {
  Closed,
  Open,
}
