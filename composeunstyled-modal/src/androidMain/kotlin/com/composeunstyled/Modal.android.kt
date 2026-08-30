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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.ComponentDialog
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import androidx.core.view.WindowCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.composeunstyled.modal.R
import java.util.UUID
import android.view.KeyEvent as AndroidKeyEvent

@Composable
internal actual fun PlatformModal(
  state: ModalState,
  onKeyEvent: (KeyEvent) -> Boolean,
  content: @Composable ModalScope.() -> Unit,
) {
  val parentView = LocalView.current
  val context = LocalContext.current
  val layoutDirection = LocalLayoutDirection.current
  val composition = rememberCompositionContext()
  val id = rememberSaveable { UUID.randomUUID() }
  val hostBackDispatcher = (context.findActivity() as? ComponentActivity)?.onBackPressedDispatcher

  DisposableEffect(parentView) {
    val contentView: ComposeView

    val dialog = ComponentDialog(context, R.style.Modal).apply {
      contentView = ComposeView(context).apply {
        setTag(androidx.compose.ui.R.id.compose_view_saveable_id_tag, "modal_$id")
        setParentCompositionContext(composition)
        setContent {
          val localWindow = window
            ?: error("Attempted to get the dialog's window without content. This should never happen and it's a bug in the library. Kindly open an issue with the steps to reproduce so that we fix it ASAP: https://github.com/composablehorizons/compose-unstyled/issues/new")

          Box(
            Modifier
              .fillMaxSize()
              .onKeyEvent(onKeyEvent)
              .semantics { dialog() },
          ) {
            CompositionLocalProvider(
              LocalModalState provides state,
              LocalModalWindow provides localWindow,
              LocalModalWindowOrNull provides localWindow,
              LocalLayoutDirection provides layoutDirection,
            ) {
              ModalScopeContent(state = state, content = content)
            }
          }
        }
      }

      setContentView(contentView)

      contentView.setViewTreeLifecycleOwner(parentView.findViewTreeLifecycleOwner())
      contentView.setViewTreeViewModelStoreOwner(parentView.findViewTreeViewModelStoreOwner())
      contentView.setViewTreeSavedStateRegistryOwner(
        parentView.findViewTreeSavedStateRegistryOwner(),
      )

      setCancelable(false)
      setCanceledOnTouchOutside(false)
    }

    val window = requireNotNull(dialog.window) {
      "Tried to use a Modal without a window. Is your parent composable attached to an Activity?"
    }
    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    window.setDimAmount(0f)
    WindowCompat.setDecorFitsSystemWindows(window, false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    } else {
      @Suppress("DEPRECATION")
      window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    val hostWindow = context.findActivity()?.window
    if (hostWindow != null) {
      syncSystemUiAppearance(from = hostWindow, to = window)
    }

    val backCallback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        val handled = onKeyEvent(
          KeyEvent(
            AndroidKeyEvent(
              AndroidKeyEvent.ACTION_DOWN,
              AndroidKeyEvent.KEYCODE_BACK,
            ),
          ),
        )
        if (handled.not()) {
          isEnabled = false
          hostBackDispatcher?.onBackPressed()
          isEnabled = true
        }
      }
    }
    dialog.onBackPressedDispatcher.addCallback(backCallback)
    dialog.show()

    val unregisterPredictiveBack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      PredictiveBack.register(
        dispatcher = dialog.onBackInvokedDispatcher,
        state = state,
        fallback = backCallback::handleOnBackPressed,
      )
    } else {
      null
    }

    onDispose {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        unregisterPredictiveBack?.invoke()
      }
      backCallback.remove()
      contentView.disposeComposition()
      dialog.dismiss()
    }
  }
}

val LocalModalWindow = staticCompositionLocalOf<Window> {
  error(
    "CompositionLocal LocalModalWindow not present – did you try to access the modal window without a modal visible on the screen?",
  )
}

val LocalModalWindowOrNull = staticCompositionLocalOf<Window?> { null }

private tailrec fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

private fun syncSystemUiAppearance(from: Window, to: Window) {
  val hostController = WindowCompat.getInsetsController(from, from.decorView)
  val modalController = WindowCompat.getInsetsController(to, to.decorView)
  modalController.isAppearanceLightStatusBars = hostController.isAppearanceLightStatusBars
  modalController.isAppearanceLightNavigationBars = hostController.isAppearanceLightNavigationBars
}

@androidx.annotation.RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
private object PredictiveBack {
  fun register(
    dispatcher: android.window.OnBackInvokedDispatcher,
    state: ModalState,
    fallback: () -> Unit,
  ): () -> Unit {
    val callback = object : android.window.OnBackAnimationCallback {
      private var isHandlingDrawerBack = false

      override fun onBackStarted(backEvent: android.window.BackEvent) {
        isHandlingDrawerBack = state.predictiveBackHandler?.onBackStarted() == true
      }

      override fun onBackProgressed(backEvent: android.window.BackEvent) {
        if (isHandlingDrawerBack) {
          state.predictiveBackHandler?.onBackProgressed(backEvent.progress)
        }
      }

      override fun onBackCancelled() {
        if (isHandlingDrawerBack) {
          state.predictiveBackHandler?.onBackCancelled()
        }
        isHandlingDrawerBack = false
      }

      override fun onBackInvoked() {
        if (isHandlingDrawerBack) {
          state.predictiveBackHandler?.onBackInvoked()
        } else {
          fallback()
        }
        isHandlingDrawerBack = false
      }
    }
    dispatcher.registerOnBackInvokedCallback(
      android.window.OnBackInvokedDispatcher.PRIORITY_OVERLAY,
      callback,
    )
    return {
      dispatcher.unregisterOnBackInvokedCallback(callback)
    }
  }
}
