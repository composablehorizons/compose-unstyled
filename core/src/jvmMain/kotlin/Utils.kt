@file:JvmName("CommonUtilsKt")

package com.composables.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.Key
import java.awt.KeyEventDispatcher
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent

@Composable
internal actual fun KeyDownHandler(onEvent: (KeyDownEvent) -> Boolean) {
    DisposableEffect(Unit) {
        val dispatcher = KeyEventDispatcher { keyEvent ->
            if (keyEvent.id == KeyEvent.KEY_PRESSED) {
                val keyDownEvent = KeyDownEvent(Key(keyEvent.keyCode))
                onEvent(keyDownEvent)
            } else {
                false
            }
        }
        val keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager()
        keyboardFocusManager.addKeyEventDispatcher(dispatcher)
        onDispose {
            keyboardFocusManager.removeKeyEventDispatcher(dispatcher)
        }
    }
}
