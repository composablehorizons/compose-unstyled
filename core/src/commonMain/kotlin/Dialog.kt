package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import com.composeunstyled.AppearInstantly
import com.composeunstyled.DisappearInstantly
import com.composeunstyled.Modal

public data class DialogProperties(val dismissOnBackPress: Boolean = true, val dismissOnClickOutside: Boolean = true)

@Stable
public class DialogState internal constructor(initiallyVisible: Boolean = false) {

    @Deprecated(
        "This constructor will go away in future versions of the library. Use the respective remember function instead",
        ReplaceWith("rememberDialogState(visible)")
    )
    constructor(visible: Boolean = false, ____deprecated_constructor: Unit) : this(initiallyVisible = visible)

    public var visible: Boolean by mutableStateOf(initiallyVisible)
}

@Stable
public class DialogScope internal constructor(state: DialogState) {
    internal var dialogState by mutableStateOf(state)
    internal val visibleState = MutableTransitionState(false)
}

private val DialogStateSaver = run {
    mapSaver(
        save = {
            mapOf("visible" to it.visible)
        },
        restore = {
            DialogState(it["visible"] as Boolean)
        }
    )
}

@Composable
@Deprecated(
    "This function is going away soon. Use the updated function with renamed parameters",
    ReplaceWith("rememberDialogState(initiallyVisible = visible)")
)
public fun rememberDialogState(visible: Boolean = false, ____deprecated_constructor: Unit = Unit): DialogState {
    return rememberDialogState(initiallyVisible = visible)
}

@Composable
public fun rememberDialogState(initiallyVisible: Boolean): DialogState {
    return rememberSaveable(saver = DialogStateSaver) { DialogState(initiallyVisible) }
}

@Composable
fun Dialog(
    state: DialogState,
    properties: DialogProperties = DialogProperties(),
    onDismiss: () -> Unit = DoNothing,
    content: @Composable (DialogScope.() -> Unit)
) {
    val scope = remember { DialogScope(state) }
    scope.visibleState.targetState = state.visible

    val currentDismiss by rememberUpdatedState(onDismiss)

    if (scope.visibleState.currentState || scope.visibleState.targetState || scope.visibleState.isIdle.not()) {
        val onKeyEvent = if (properties.dismissOnBackPress) {
            { event: KeyEvent ->
                if (event.type == KeyEventType.KeyDown && (event.key == Key.Back || event.key == Key.Escape)) {
                    currentDismiss()
                    scope.dialogState.visible = false
                    true
                } else false
            }
        } else {
            { false }
        }
        Modal(onKeyEvent = onKeyEvent) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .then(
                        if (properties.dismissOnClickOutside) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures {
                                currentDismiss()
                                scope.dialogState.visible = false
                            }
                        }
                    } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                scope.content()
            }
        }
    }
}

@Composable
public fun DialogScope.DialogPanel(
    modifier: Modifier = Modifier,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = enter,
        exit = exit,
    ) {
        Box(modifier.semantics { dialog() }
            .pointerInput(Unit) { detectTapGestures { } }) {
            content()
        }
    }
}

@Composable
public fun DialogScope.Scrim(
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(0.6f),
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
) {
    AnimatedVisibility(
        visibleState = visibleState,
        enter = enter,
        exit = exit
    ) {
        Box(Modifier.fillMaxSize().focusable(false).background(scrimColor).then(modifier))
    }
}
