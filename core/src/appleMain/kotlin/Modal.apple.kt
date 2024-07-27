package com.composables.core

import androidx.compose.ui.window.DialogProperties as ComposeDialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

@Composable
internal actual fun Modal(protectNavBars: Boolean, content: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = {},
        properties = ComposeDialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformInsets = false,
            usePlatformDefaultWidth = false,
            scrimColor = Color.Transparent
        ),
        content = content
    )
}