package core.com.composeunstyled

import androidx.compose.ui.input.pointer.PointerEvent

internal actual val PointerEvent.isDeepPress: Boolean
    get() = false