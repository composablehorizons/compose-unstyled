package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

public data class ModalSheetProperties(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true
)

@Composable
public fun rememberModalBottomSheetState(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
): ModalBottomSheetState {
    return rememberSaveable(
        saver = mapSaver(
            save = { modalBottomSheetState -> mapOf("detent" to modalBottomSheetState.currentDetent.identifier) },
            restore = { map ->
                val restoredDetent = detents.first { it.identifier == map["detent"] }
                ModalBottomSheetState(
                    bottomSheetDetent = restoredDetent,
                    sheetDetents = detents,
                    animationSpec = animationSpec
                )
            }
        )
    ) {
        ModalBottomSheetState(
            bottomSheetDetent = initialDetent,
            sheetDetents = detents,
            animationSpec = animationSpec,
        )
    }
}

public class ModalBottomSheetState internal constructor(
    internal val bottomSheetDetent: SheetDetent,
    internal val sheetDetents: List<SheetDetent>,
    internal val animationSpec: AnimationSpec<Float>
) {
    internal var modalDetent by mutableStateOf(bottomSheetDetent)

    public var currentDetent: SheetDetent
        get() {
            return modalDetent
        }
        set(value) {
            val isBottomSheetVisible = bottomSheetState != null &&
                    (bottomSheetState!!.currentDetent != SheetDetent.Hidden || bottomSheetState!!.targetDetent != SheetDetent.Hidden)

            if (isBottomSheetVisible) {
                bottomSheetState!!.currentDetent = value
            } else {
                modalDetent = value
            }
        }

    public val targetDetent: SheetDetent by derivedStateOf {
        bottomSheetState?.targetDetent ?: modalDetent
    }
    public val isIdle: Boolean by derivedStateOf {
        bottomSheetState?.isIdle ?: true
    }
    public val progress: Float by derivedStateOf {
        bottomSheetState?.progress ?: 0f
    }
    public val offset: Float by derivedStateOf {
        bottomSheetState?.offset ?: 1f
    }
    internal var bottomSheetState by mutableStateOf<BottomSheetState?>(null)

    public suspend fun animateTo(value: SheetDetent) {
        val isBottomSheetVisible = bottomSheetState != null &&
                (bottomSheetState!!.currentDetent != SheetDetent.Hidden || bottomSheetState!!.targetDetent != SheetDetent.Hidden)

        if (isBottomSheetVisible) {
            bottomSheetState!!.animateTo(value)
        } else {
            modalDetent = value
        }
    }

    public fun jumpTo(value: SheetDetent) {
        val isBottomSheetVisible = bottomSheetState != null &&
                (bottomSheetState!!.currentDetent != SheetDetent.Hidden || bottomSheetState!!.targetDetent != SheetDetent.Hidden)

        if (isBottomSheetVisible) {
            bottomSheetState!!.jumpTo(value)
        } else {
            modalDetent = value
        }
    }
}

public class ModalBottomSheetScope internal constructor(
    internal val modalState: ModalBottomSheetState,
    internal val properties: ModalSheetProperties
) {
    internal val visibleState = MutableTransitionState(false)
}

@Composable
public fun ModalBottomSheet(
    state: ModalBottomSheetState,
    properties: ModalSheetProperties = ModalSheetProperties(),
    content: @Composable() (ModalBottomSheetScope.() -> Unit),
) {
    val scope = remember { ModalBottomSheetScope(state, properties) }
    scope.visibleState.targetState = state.currentDetent != SheetDetent.Hidden

    if (scope.visibleState.currentState || scope.visibleState.targetState || scope.visibleState.isIdle.not()) {
        Modal(protectNavBars = true) {
            Box(Modifier
                .fillMaxSize()
                .let {
                    if (properties.dismissOnClickOutside) {
                        it.pointerInput(Unit) { detectTapGestures { state.currentDetent = SheetDetent.Hidden } }
                    } else it
                }
            ) {
                scope.content()
            }
        }
    }
}

@Composable
public fun ModalBottomSheetScope.Scrim(
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

@Composable
public fun ModalBottomSheetScope.Sheet(
    modifier: Modifier = Modifier,
    content: @Composable() (BottomSheetScope.() -> Unit)
) {
    val sheetState = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = modalState.sheetDetents,
        animationSpec = modalState.animationSpec
    )
    modalState.bottomSheetState = sheetState

    var hasBeenIntroduced by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // waiting for the dialog to settle: can't just start animation here
        delay(50)
        sheetState.currentDetent = modalState.modalDetent
        hasBeenIntroduced = true
    }

    if (hasBeenIntroduced) {
        LaunchedEffect(sheetState.isIdle) {
            if (sheetState.isIdle) {
                if (sheetState.currentDetent == SheetDetent.Hidden) {
                    modalState.modalDetent = SheetDetent.Hidden
                } else {
                    modalState.modalDetent = sheetState.currentDetent
                }
            }
        }
    }

    if (sheetState.isIdle && properties.dismissOnBackPress) {
        // AnchoredDraggableState jumps to 1.0f progress as soon as we change the current value
        // while moving. This causes the sheet to disappear instead of animating away nicely.
        // Because of this, we only manage back presses when the sheet is idle
        KeyDownHandler { event ->
            return@KeyDownHandler when (event.key) {
                Key.Back, Key.Escape -> {
                    sheetState.currentDetent = SheetDetent.Hidden
                    true
                }

                else -> false
            }
        }
    }

    if (hasBeenIntroduced) {
        LaunchedEffect(sheetState.currentDetent) {
            if (sheetState.currentDetent == SheetDetent.Hidden && sheetState.targetDetent == sheetState.currentDetent) {
                modalState.currentDetent = SheetDetent.Hidden
            }
        }
    }
    BottomSheet(
        state = sheetState,
        modifier = modifier,
        content = content
    )
}