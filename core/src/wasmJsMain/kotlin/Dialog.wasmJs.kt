package com.composables.core

import androidx.compose.ui.window.DialogProperties as ComposeDialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun NoScrimDialog(content: @Composable () -> Unit) {
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