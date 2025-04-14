package com.composables.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun Modal(
    onKeyEvent: (KeyEvent) -> Boolean,
    content: @Composable () -> Unit
) = androidx.compose.ui.window.Dialog(
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
        val focusRequester = remember { FocusRequester() }
        Box(Modifier.focusRequester(focusRequester).fillMaxSize().onKeyEvent(onKeyEvent)) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            content()
        }
    }
)

