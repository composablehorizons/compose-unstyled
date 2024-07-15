package com.composables.ui

import androidx.compose.ui.window.DialogProperties as ComposeDialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun NoScrimDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
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