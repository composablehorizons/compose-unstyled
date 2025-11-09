package com.composeunstyled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modal(
    onKeyEvent: (KeyEvent) -> Boolean,
    content: @Composable () -> Unit
) = Dialog(
    onDismissRequest = {},
    properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        usePlatformInsets = false,
        useSoftwareKeyboardInset = false,
        usePlatformDefaultWidth = false,
        scrimColor = Color.Transparent
    ),
    content = {
        Box(Modifier.fillMaxSize().onKeyEvent(onKeyEvent)) {
            content()
        }
    }
)
