package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

@Composable
expect fun Modal(
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable () -> Unit
)