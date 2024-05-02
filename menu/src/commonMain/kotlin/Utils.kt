import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key

internal data class KeyDownEvent(val key: Key)

@Composable
internal expect fun KeyDownHandler(onEvent: (KeyDownEvent) -> Boolean)