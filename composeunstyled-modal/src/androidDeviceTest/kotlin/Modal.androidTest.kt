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

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.DialogWindowProvider
import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isSameInstanceAs
import kotlin.test.Test

class ModalAndroidTest {

  @Test
  fun contentCanDiscoverTheModalWindowThroughDialogWindowProvider() = runComposeUiTest {
    var providerWindow: android.view.Window? = null
    var modalWindow: android.view.Window? = null
    val state = ModalState(initiallyVisible = true)
    setContent {
      Modal(state) {
        val provider = LocalView.current.parent as? DialogWindowProvider
        val window = LocalModalWindow.current
        SideEffect {
          providerWindow = provider?.window
          modalWindow = window
        }
        BasicText("Modal")
      }
    }

    waitForIdle()
    assertThat(providerWindow).isNotNull()
    assertThat(providerWindow).isSameInstanceAs(modalWindow)
    state.transitionState.targetState = false
    waitForIdle()
  }

  @Test
  fun content_respects_locallayoutdirection() = runComposeUiTest {
    val state = ModalState(initiallyVisible = true)
    setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Modal(state) {
          val layoutDirection =
            if (LocalLayoutDirection.current == LayoutDirection.Rtl) "rtl" else "ltr"
          BasicText(layoutDirection, Modifier.testTag("layout_direction"))
        }
      }
    }

    onNodeWithTag("layout_direction").assertTextEquals("rtl")
    state.transitionState.targetState = false
    waitForIdle()
  }
}
