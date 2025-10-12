package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager

@OptIn(InternalComposeUiApi::class)
@Composable
internal actual fun KeyEventObserver(onEvent: (KeyEvent) -> Unit) {
    DisposableEffect(Unit) {
        val dispatcher = KeyEventDispatcher { awtEvent ->
            // we are getting
            val composeKeyEvent = KeyEvent(
                key = Key(awtEvent.keyCode, nativeKeyLocation = awtEvent.keyLocation),
                type = when (awtEvent.id) {
                    java.awt.event.KeyEvent.KEY_PRESSED -> KeyEventType.KeyDown
                    java.awt.event.KeyEvent.KEY_RELEASED -> KeyEventType.KeyUp
                    else -> KeyEventType.Unknown
                },
            )
            onEvent(composeKeyEvent)
            false
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher)

        onDispose {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher)
        }
    }
}

