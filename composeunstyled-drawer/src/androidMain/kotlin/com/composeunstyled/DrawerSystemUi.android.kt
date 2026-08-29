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
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
internal actual fun ApplyAndroidSystemUi(
  systemUi: SystemUi,
  enabled: Boolean,
) {
  if (enabled.not()) return

  val view = LocalView.current
  val window = LocalModalWindowOrNull.current ?: view.context.findActivity()?.window ?: return

  ApplyStatusBarIconAppearance(
    window = window,
    view = view,
    appearance = systemUi.statusBarIconAppearance,
  )
  ApplyNavigationBarIconAppearance(
    window = window,
    view = view,
    appearance = systemUi.navigationBarIconAppearance,
  )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

internal actual fun Modifier.excludeDrawerEdgeFromSystemGesture(): Modifier {
  return systemGestureExclusion()
}

@Composable
private fun ApplyStatusBarIconAppearance(
  window: android.view.Window,
  view: android.view.View,
  appearance: SystemBarIconAppearance,
) {
  if (appearance == SystemBarIconAppearance.Unspecified) return

  DisposableEffect(window, appearance) {
    val controller = WindowCompat.getInsetsController(window, view)
    val previousAppearance = controller.isAppearanceLightStatusBars
    controller.isAppearanceLightStatusBars = appearance == SystemBarIconAppearance.Dark
    onDispose {
      controller.isAppearanceLightStatusBars = previousAppearance
    }
  }
}

@Composable
private fun ApplyNavigationBarIconAppearance(
  window: android.view.Window,
  view: android.view.View,
  appearance: SystemBarIconAppearance,
) {
  if (appearance == SystemBarIconAppearance.Unspecified) return

  DisposableEffect(window, appearance) {
    val controller = WindowCompat.getInsetsController(window, view)
    val previousAppearance = controller.isAppearanceLightNavigationBars
    controller.isAppearanceLightNavigationBars = appearance == SystemBarIconAppearance.Dark
    onDispose {
      controller.isAppearanceLightNavigationBars = previousAppearance
    }
  }
}
