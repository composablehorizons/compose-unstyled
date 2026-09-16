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

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun <T : Any> RegisterDrawerPredictiveBack(
  state: UnstyledDrawerState<T>,
  presentation: DrawerPresentation,
  dismissOnNavigateBack: Boolean,
) {
  if (dismissOnNavigateBack.not() || state.zeroValue == null) return
  if (state.isAtZeroValue) return
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return

  val view = LocalView.current
  val dispatcher = when (presentation) {
    DrawerPresentation.Modal -> LocalModalWindow.current.onBackInvokedDispatcher
    DrawerPresentation.Overlay,
    DrawerPresentation.InPlace,
    -> view.context.findActivity()?.window?.onBackInvokedDispatcher
    else -> null
  } ?: return

  val callback = remember(state) {
    object : android.window.OnBackAnimationCallback {
      private var isActive = false

      override fun onBackStarted(backEvent: android.window.BackEvent) {
        isActive = state.startPredictiveBack()
      }

      override fun onBackProgressed(backEvent: android.window.BackEvent) {
        if (isActive) {
          state.progressPredictiveBack(backEvent.progress)
        }
      }

      override fun onBackCancelled() {
        if (isActive) {
          state.cancelPredictiveBack()
        }
        isActive = false
      }

      override fun onBackInvoked() {
        if (isActive) {
          state.invokePredictiveBack()
        }
        isActive = false
      }
    }
  }

  DisposableEffect(dispatcher, callback) {
    dispatcher.registerOnBackInvokedCallback(
      android.window.OnBackInvokedDispatcher.PRIORITY_OVERLAY,
      callback,
    )
    onDispose {
      dispatcher.unregisterOnBackInvokedCallback(callback)
    }
  }
}
