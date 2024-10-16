package com.composables.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

@Composable
internal expect fun Modal(
    protectNavBars: Boolean = false,
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable () -> Unit
)