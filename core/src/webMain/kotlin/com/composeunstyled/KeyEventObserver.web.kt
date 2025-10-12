package com.composeunstyled

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@OptIn(InternalComposeUiApi::class)
@Composable
internal actual fun KeyEventObserver(onEvent: (KeyEvent) -> Unit) {
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = { event ->
            event as KeyboardEvent
            onEvent(
                KeyEvent(
                    key = Key(event.keyCode.toLong()),
                    type = when (event.type) {
                        "keydown" -> KeyEventType.KeyDown
                        "keyup" -> KeyEventType.KeyUp
                        else -> KeyEventType.Unknown
                    },
                )
            )
        }

        document.addEventListener("keydown", listener)
        document.addEventListener("keyup", listener)

        onDispose {
            document.removeEventListener("keydown", listener)
            document.removeEventListener("keyup", listener)
        }
    }
}
