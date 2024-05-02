import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.key.Key
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

@Composable
internal actual fun KeyDownHandler(onEvent: (KeyDownEvent) -> Boolean) {
    DisposableEffect(Unit) {
        // workaround until KT-64565 is fixed: https://youtrack.jetbrains.com/issue/KT-64565/Kotlin-wasm-removeEventListener-function-did-not-remove-the-event-listener
        var isDisabled = false

        val listener: (Event) -> Unit = { e ->
            if (isDisabled.not()) {
                val keyboardEvent = e as KeyboardEvent
                val result = onEvent(KeyDownEvent(Key(keyboardEvent.keyCode.toLong())))
                if (result) {
                    e.preventDefault()
                }
            }
        }
        document.addEventListener("keydown", listener, true)
        onDispose {
            document.removeEventListener("keydown", listener, true)
            isDisabled = true
        }
    }
}
