package core.com.composeunstyled

import android.view.MotionEvent
import androidx.compose.ui.input.pointer.PointerEvent

internal actual val PointerEvent.isDeepPress: Boolean
    get() = classification == MotionEvent.CLASSIFICATION_DEEP_PRESS
