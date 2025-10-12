package com.composeunstyled

import android.os.Build
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun KeyEventObserver(onEvent: (KeyEvent) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val view = LocalView.current

        DisposableEffect(view) {
            val listener = View.OnUnhandledKeyEventListener { v, event ->
                onEvent(KeyEvent(event))
                false
            }
            view.addOnUnhandledKeyEventListener(listener)
            onDispose { view.removeOnUnhandledKeyEventListener(listener) }
        }
    }
}
