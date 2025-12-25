package com.composeunstyled

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
import com.composables.core.DoNothing

data class DialogProperties(val dismissOnBackPress: Boolean = true, val dismissOnClickOutside: Boolean = true)

@Stable
class DialogState(initiallyVisible: Boolean = false) {

    internal val panelVisibilityState = MutableTransitionState(initiallyVisible)
    internal val scrimVisibilityState = MutableTransitionState(initiallyVisible)

    var innerVisible by mutableStateOf(initiallyVisible)
        private set

    var visible: Boolean = innerVisible
        set(value) {
            innerVisible = value
            if (value.not()) {
                panelVisibilityState.targetState = false
                scrimVisibilityState.targetState = false
            }
            field = value
        }
        get() = innerVisible

}

internal val LocalDialogState = staticCompositionLocalOf { DialogState() }

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

@Composable
fun rememberDialogState(initiallyVisible: Boolean = false): DialogState {
    return rememberSaveable(saver = DialogStateSaver) { DialogState(initiallyVisible) }
}

@Composable
fun Dialog(
    state: DialogState,
    properties: DialogProperties = DialogProperties(),
    onDismiss: () -> Unit = DoNothing,
    content: @Composable (() -> Unit)
) {
    val currentDismiss by rememberUpdatedState(onDismiss)

    CompositionLocalProvider(LocalDialogState provides state) {
        val isAnimatingPanel = state.panelVisibilityState.isIdle.not()
        val isAnimatingScrim = state.scrimVisibilityState.isIdle.not()
        val addModal = state.visible || isAnimatingScrim || isAnimatingPanel

        if (addModal) {
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
                    state.panelVisibilityState.targetState = true
                    state.scrimVisibilityState.targetState = true
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
                    content()
                }
            }
        }
    }
}

@Composable
fun DialogPanel(
    modifier: Modifier = Modifier,
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    content: @Composable () -> Unit
) {
    val state = LocalDialogState.current

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

@Composable
fun Scrim(
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(0.6f),
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
) {
    val state = LocalDialogState.current

    AnimatedVisibility(
        visibleState = state.scrimVisibilityState,
        enter = enter,
        exit = exit
    ) {
        Box(Modifier.fillMaxSize().focusable(false).background(scrimColor).then(modifier))
    }
}
