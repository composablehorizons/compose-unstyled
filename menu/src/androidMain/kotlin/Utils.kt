package com.composables.ui

import android.view.KeyEvent
import android.view.View.OnKeyListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun KeyDownHandler(onEvent: (KeyDownEvent) -> Boolean) {
    val view = LocalView.current

    DisposableEffect(Unit) {
        val listener = OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                onEvent(KeyDownEvent(Key(keyCode)))
            } else {
                false
            }
        }
        view.setOnKeyListener(listener)
        onDispose {
            view.setOnKeyListener(null)
        }
    }
}
