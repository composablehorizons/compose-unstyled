package com.composables.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import internal.ComposeDialog

@Composable
internal actual fun Modal(
    protectNavBars: Boolean,
    onKeyEvent: (KeyEvent) -> Boolean,
    content: @Composable () -> Unit
) = ComposeDialog(onKeyEvent, content)
