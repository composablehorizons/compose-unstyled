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

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import kotlin.test.Test

class FocusRingTest {
  @Test
  fun focusVisibleIsFalseWhenInteractionSourceIsNotFocused() = runComposeUiTest {
    val interactionSource = MutableInteractionSource()
    val manager = TestFocusVisibilityManager(FocusVisibilityMode.Keyboard)
    var focusVisible = true

    setContent {
      CompositionLocalProvider(LocalFocusVisibilityManager provides manager) {
        focusVisible = interactionSource.collectIsFocusVisibleAsState().value
      }
    }

    runOnIdle {
      assertThat(focusVisible).isFalse()
    }
  }

  @Test
  fun focusVisibleIsTrueWhenFocusedFromKeyboardMode() = runComposeUiTest {
    val interactionSource = MutableInteractionSource()
    val manager = TestFocusVisibilityManager(FocusVisibilityMode.Keyboard)
    val focus = FocusInteraction.Focus()
    var focusVisible = false

    setContent {
      CompositionLocalProvider(LocalFocusVisibilityManager provides manager) {
        focusVisible = interactionSource.collectIsFocusVisibleAsState().value
      }
    }

    runOnIdle {
      interactionSource.tryEmit(focus)
    }

    runOnIdle {
      assertThat(focusVisible).isTrue()
    }
  }

  @Test
  fun focusVisibleIsFalseWhenFocusedFromPointerMode() = runComposeUiTest {
    val interactionSource = MutableInteractionSource()
    val manager = TestFocusVisibilityManager(FocusVisibilityMode.Pointer)
    val focus = FocusInteraction.Focus()
    var focusVisible = true

    setContent {
      CompositionLocalProvider(LocalFocusVisibilityManager provides manager) {
        focusVisible = interactionSource.collectIsFocusVisibleAsState().value
      }
    }

    runOnIdle {
      interactionSource.tryEmit(focus)
    }

    runOnIdle {
      assertThat(focusVisible).isFalse()
    }
  }

  @Test
  fun focusVisibleUpdatesWhenInputModeChanges() = runComposeUiTest {
    val interactionSource = MutableInteractionSource()
    val manager = TestFocusVisibilityManager(FocusVisibilityMode.Pointer)
    val focus = FocusInteraction.Focus()
    var focusVisible = true

    setContent {
      CompositionLocalProvider(LocalFocusVisibilityManager provides manager) {
        focusVisible = interactionSource.collectIsFocusVisibleAsState().value
      }
    }

    runOnIdle {
      interactionSource.tryEmit(focus)
    }

    runOnIdle {
      assertThat(focusVisible).isFalse()
      manager.notifyKeyboardInput()
    }

    runOnIdle {
      assertThat(focusVisible).isTrue()
    }
  }
}

private class TestFocusVisibilityManager(
  initialFocusVisibilityMode: FocusVisibilityMode,
) : FocusVisibilityManager {
  override var focusVisibilityMode by mutableStateOf(initialFocusVisibilityMode)
    private set

  override fun notifyKeyboardInput() {
    focusVisibilityMode = FocusVisibilityMode.Keyboard
  }

  override fun notifyPointerInput() {
    focusVisibilityMode = FocusVisibilityMode.Pointer
  }
}
