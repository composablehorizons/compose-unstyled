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

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import kotlin.test.Test

class ModalTest {

  @Test
  fun hostedModalStopsKeyboardInputReachingBackground() = runComposeUiTest {
    val state = ModalState()
    var backgroundKeyEvents = 0
    setContent {
      ModalHost(Modifier.size(200.dp)) {
        Box(
          Modifier.size(40.dp).onKeyEvent {
            backgroundKeyEvents++
            true
          }
            .focusable().testTag("background"),
        )
        Modal(state = state) {
          if (state.transitionState.targetState) {
            Box(Modifier.size(40.dp).modalFragment().testTag("modal"))
          }
        }
      }
    }

    onNodeWithTag("background").requestFocus()
    state.transitionState.targetState = true
    onNode(isDialog()).performKeyInput { pressKey(Key.A) }
    runOnIdle { assertThat(backgroundKeyEvents).isEqualTo(0) }
    state.transitionState.targetState = false
    onNodeWithTag("modal").assertDoesNotExist()
    onNodeWithTag("background").assertIsFocused()
    onNodeWithTag("background").performKeyInput { pressKey(Key.A) }
    runOnIdle { assertThat(backgroundKeyEvents).isEqualTo(2) }
  }

  @Test
  fun hostedModalKeepsTabFocusInsideItsContent() = runComposeUiTest {
    setContent {
      ModalHost(Modifier.size(200.dp)) {
        Box(Modifier.size(40.dp).focusable().testTag("background"))
        Modal(state = rememberModalState(initiallyVisible = true)) {
          Box(Modifier.size(80.dp).modalFragment()) {
            Box(Modifier.size(20.dp).focusable().testTag("first"))
            Box(Modifier.size(40.dp).focusable().testTag("last"))
          }
        }
      }
    }

    onNodeWithTag("last").requestFocus()
    onNode(isDialog()).performKeyInput { pressKey(Key.Tab) }
    onNodeWithTag("first").assertIsFocused()
    onNode(isDialog()).performKeyInput {
      keyDown(Key.ShiftLeft)
      pressKey(Key.Tab)
      keyUp(Key.ShiftLeft)
    }
    onNodeWithTag("last").assertIsFocused()
  }

  @Test
  fun hostedModalWrapsTabInTraversalOrderWhenControlsHaveOffsets() = runComposeUiTest {
    setContent {
      ModalHost(Modifier.size(200.dp)) {
        Box(Modifier.size(40.dp).focusable().testTag("background"))
        Modal(state = rememberModalState(initiallyVisible = true)) {
          Box(Modifier.size(100.dp).modalFragment()) {
            Box(Modifier.offset(y = 60.dp).size(20.dp).focusable().testTag("first"))
            Box(Modifier.size(20.dp).focusable().testTag("last"))
          }
        }
      }
    }

    onNodeWithTag("last").requestFocus()
    onNode(isDialog()).performKeyInput { pressKey(Key.Tab) }
    onNodeWithTag("first").assertIsFocused()
    onNode(isDialog()).performKeyInput {
      keyDown(Key.ShiftLeft)
      pressKey(Key.Tab)
      keyUp(Key.ShiftLeft)
    }
    onNodeWithTag("last").assertIsFocused()
  }

  @Test
  fun hostedModalRestoresFocusToTheModalUnderneathIt() = runComposeUiTest {
    var showTopModal by mutableStateOf(false)
    setContent {
      ModalHost(Modifier.size(200.dp)) {
        Modal(state = rememberModalState(initiallyVisible = true)) {
          Box(Modifier.size(40.dp).focusable().testTag("lower"))
        }
        if (showTopModal) {
          Modal(state = rememberModalState(initiallyVisible = true)) {
            Box(Modifier.size(40.dp).focusable().testTag("upper"))
          }
        }
      }
    }

    onNodeWithTag("lower").requestFocus()
    showTopModal = true
    waitForIdle()
    onNodeWithTag("upper").assertIsFocused()
    onNodeWithTag("lower").requestFocus()
    onNodeWithTag("upper").assertIsFocused()
    showTopModal = false
    onNodeWithTag("upper").assertDoesNotExist()
    onNodeWithTag("lower").assertIsFocused()
  }

  @Test
  fun removingModalHostWithAnOpenModalReleasesFocus() = runComposeUiTest {
    var showHost by mutableStateOf(true)
    setContent {
      if (showHost) {
        ModalHost(Modifier.size(200.dp).testTag("host")) {
          Box(Modifier.size(40.dp).focusable().testTag("background"))
          Modal(state = rememberModalState(initiallyVisible = true)) {
            Box(Modifier.size(40.dp).focusable())
          }
        }
      }
    }

    onNodeWithTag("host").assertExists()
    showHost = false
    onNodeWithTag("host").assertDoesNotExist()
  }

  @Test
  fun nestedGenericHostDoesNotInterceptModalContent() = runComposeUiTest {
    setContent {
      ModalHost(Modifier.size(100.dp)) {
        PortalHost(Modifier.size(60.dp)) {
          Modal(state = rememberModalState(initiallyVisible = true)) {
            Box(Modifier.fillMaxSize().testTag("modal"))
          }
          Portal { Box(Modifier.fillMaxSize().testTag("generic")) }
        }
      }
    }

    onNodeWithTag("modal").assertWidthIsEqualTo(100.dp)
    onNodeWithTag("generic").assertWidthIsEqualTo(60.dp)
    onNode(isDialog()).assertExists()
  }

  @Test
  fun modalHostDoesNotAcceptUntargetedPortals() = runComposeUiTest {
    setContent {
      ModalHost {
        Portal { Box(Modifier.testTag("generic")) }
      }
    }

    onNodeWithTag("generic").assertDoesNotExist()
  }

  @Test
  fun add_isdialog_semantic() = runComposeUiTest {
    var showModal by mutableStateOf(false)

    setContent {
      if (showModal) {
        Modal(
          state = rememberModalState(initiallyVisible = true),
          onKeyEvent = { false },
        ) {
          Box(Modifier.testTag("modal_contents").size(40.dp))
        }
      }
    }
    onNode(isDialog()).assertDoesNotExist()
    showModal = true
    onNode(isDialog()).assertExists()
  }

  @Test
  fun content_respects_locallayoutdirection() = runComposeUiTest {
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Modal(state = rememberModalState(initiallyVisible = true)) {
          val layoutDirection =
            if (LocalLayoutDirection.current == LayoutDirection.Rtl) "rtl" else "ltr"
          BasicText(layoutDirection, Modifier.testTag("layout_direction"))
        }
      }
    }

    onNodeWithTag("layout_direction").assertTextEquals("rtl")
  }

  @Test
  fun modal_host_renders_content_inside_portal_host() = runComposeUiTest {
    setContent {
      ModalHost(Modifier.testTag("modal_host")) {
        Modal(state = rememberModalState(initiallyVisible = true)) {
          Box(Modifier.testTag("modal_content").modalFragment())
        }
      }
    }

    onNodeWithTag("modal_host").assertExists()
    onNodeWithTag("modal_content").assertExists()
  }

  @Test
  fun inspection_mode_renders_content_without_window_attachment() = runComposeUiTest {
    lateinit var state: ModalState

    setContent {
      state = rememberModalState(initiallyVisible = true)
      CompositionLocalProvider(LocalInspectionMode provides true) {
        ModalHost {
          Modal(state = state) {
            Box(Modifier.testTag("modal_content"))
          }
        }
      }
    }

    waitForIdle()
    runOnIdle {
      state.attachedToWindow = false
    }

    onNodeWithTag("modal_content").assertExists()
  }

  @Test
  fun modal_host_modal_resumes_after_attaching() = runComposeUiTest {
    lateinit var state: ModalState
    var attachedToWindow by mutableStateOf(false)

    setContent {
      state = rememberModalState(initiallyVisible = true)
      ModalHost {
        Modal(state = state) {
          LaunchedEffect(state.transitionState.targetState) {
            if (state.transitionState.targetState) {
              state.awaitAttachedToWindow()
              attachedToWindow = true
            }
          }
          Box(Modifier.testTag("modal_content").modalFragment())
        }
      }
    }

    waitUntil { attachedToWindow }

    onNodeWithTag("modal_content").assertExists()
    assertThat(state.attachedToWindow).isTrue()
  }

  @Test
  fun modal_uses_window_when_outside_modal_host() = runComposeUiTest {
    lateinit var state: ModalState
    var attachedToWindow by mutableStateOf(false)

    setContent {
      state = rememberModalState(initiallyVisible = true)
      ModalHost {
        Box(Modifier.testTag("modal_host"))
      }

      Modal(state = state) {
        LaunchedEffect(state.transitionState.targetState) {
          if (state.transitionState.targetState) {
            state.awaitAttachedToWindow()
            attachedToWindow = true
          }
        }
        Box(Modifier.testTag("window_modal_content").modalFragment())
      }
    }

    waitUntil { attachedToWindow }

    onNodeWithTag("modal_host").assertExists()
    onNodeWithTag("window_modal_content").assertExists()
    assertThat(state.attachedToWindow).isTrue()
  }

  @Test
  fun awaitAttachedToWindow_resumes_when_modal_attaches() = runComposeUiTest {
    lateinit var state: ModalState
    var attachedToWindow by mutableStateOf(false)

    setContent {
      state = rememberModalState(initiallyVisible = false)
      Modal(state = state) {
        LaunchedEffect(state.transitionState.targetState) {
          if (state.transitionState.targetState) {
            state.awaitAttachedToWindow()
            attachedToWindow = true
          }
        }
        Box(Modifier.testTag("modal_content").modalFragment())
      }
    }

    state.transitionState.targetState = true
    waitUntil { attachedToWindow }

    onNodeWithTag("modal_content").assertExists()
    assertThat(state.attachedToWindow).isTrue()
  }
}
