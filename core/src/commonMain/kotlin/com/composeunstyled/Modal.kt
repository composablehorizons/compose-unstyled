package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

/**
 * Modals are the building blocks for components such as dialogs, alerts and modal bottom sheets.
 *
 * They create their own window and block interaction with the rest of the interface until removed from the composition.
 *
 * @param onKeyEvent Allows the caller to intercept [KeyEvent]s happening within the contents of the modal. Return `true` if you handled the event.
 * @param content The content of the modal
 */
@Composable
expect fun Modal(
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable () -> Unit
)