package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

@Composable
internal actual fun KeyEventObserver(onEvent: (KeyEvent) -> Unit) {
    // NO-OP iOS does not support this
}