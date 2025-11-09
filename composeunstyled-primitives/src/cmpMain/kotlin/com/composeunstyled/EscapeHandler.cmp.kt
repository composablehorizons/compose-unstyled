package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key

@Composable
internal actual fun EscapeHandler(callback: () -> Unit) {
    KeyEventObserver { event ->
        if (event.isKeyDown && (event.key == Key.Escape || event.key == Key.Back)) {
            callback()
            true
        } else {
            false
        }
    }
}