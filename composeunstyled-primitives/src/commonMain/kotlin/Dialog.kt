package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import com.composeunstyled.*


@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
data class DialogProperties(val dismissOnBackPress: Boolean = true, val dismissOnClickOutside: Boolean = true)

@Stable
@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
class DialogState(initiallyVisible: Boolean = false) {

    constructor(visible: Boolean = false, ____deprecated_constructor: Unit) : this(initiallyVisible = visible)

    internal val panelVisibilityState = MutableTransitionState(initiallyVisible)
    internal val scrimVisibilityState = MutableTransitionState(initiallyVisible)

    var visible: Boolean
        set(value) {
            panelVisibilityState.targetState = value
            scrimVisibilityState.targetState = value
        }
        get() {
            return panelVisibilityState.currentState || scrimVisibilityState.currentState
        }
}

@Stable
@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
class DialogScope internal constructor(state: DialogState) {
    internal var state by mutableStateOf(state)
}

private val DialogStateSaver = run {
    mapSaver(
        save = {
            mapOf("visible" to it.visible)
        },
        restore = {
            DialogState(initiallyVisible = it["visible"] as Boolean)
        }
    )
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun rememberDialogState(initiallyVisible: Boolean = false): DialogState {
    return rememberSaveable(saver = DialogStateSaver) { DialogState(initiallyVisible) }
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun Dialog(
    state: DialogState,
    properties: DialogProperties = DialogProperties(),
    onDismiss: () -> Unit = DoNothing,
    content: @Composable (DialogScope.() -> Unit)
) {
    val scope = remember { DialogScope(state) }

    val currentDismiss by rememberUpdatedState(onDismiss)

    val isPanelVisible = state.panelVisibilityState.isIdle.not() || state.panelVisibilityState.currentState
    val isScrimVisible = state.scrimVisibilityState.isIdle.not() || state.scrimVisibilityState.currentState
    if (isScrimVisible || isPanelVisible) {
        val onKeyEvent = if (properties.dismissOnBackPress) {
            { event: KeyEvent ->
                if (event.type == KeyEventType.KeyDown && (event.key == Key.Back || event.key == Key.Escape)) {
                    currentDismiss()
                    state.visible = false
                    true
                } else false
            }
        } else {
            { false }
        }
        Modal(onKeyEvent = onKeyEvent) {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester)
                    .then(
                        if (properties.dismissOnClickOutside) {
                            Modifier.pointerInput(Unit) {
                                detectTapGestures {
                                    currentDismiss()
                                    state.visible = false
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

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun DialogScope.DialogPanel(
    modifier: Modifier = Modifier,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visibleState = state.panelVisibilityState,
        enter = enter,
        exit = exit,
    ) {
        Box(
            modifier
                .clip(shape)
                .background(backgroundColor)
                .pointerInput(Unit) { detectTapGestures { } }
                .padding(contentPadding)
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
    }
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun DialogScope.Scrim(
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(0.6f),
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
) {
    AnimatedVisibility(
        visibleState = state.scrimVisibilityState,
        enter = enter,
        exit = exit
    ) {
        Box(Modifier.fillMaxSize().focusable(false).background(scrimColor).then(modifier))
    }
}
