package com.composables.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

public data class ModalSheetProperties(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
)

@Composable
public fun rememberModalBottomSheetState(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
    velocityThreshold: () -> Dp = { 125.dp },
    positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay()
): ModalBottomSheetState {
    val actualDetents = (setOf(SheetDetent.Hidden) + detents).toList()
    val sheetState = rememberBottomSheetState(
        initialDetent = SheetDetent.Hidden,
        detents = actualDetents,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
        decayAnimationSpec = decayAnimationSpec,
    )
    return rememberSaveable(
        saver = mapSaver(
            save = { modalBottomSheetState -> mapOf("detent" to modalBottomSheetState.currentDetent.identifier) },
            restore = { map ->
                val restoredDetent = actualDetents.first { it.identifier == map["detent"] }
                ModalBottomSheetState(
                    bottomSheetDetent = restoredDetent,
                    sheetState = sheetState
                )
            }
        )
    ) {
        ModalBottomSheetState(
            bottomSheetDetent = initialDetent,
            sheetState = sheetState
        )
    }
}

public class ModalBottomSheetState internal constructor(
    internal val bottomSheetDetent: SheetDetent,
    sheetState: BottomSheetState
) {
    internal val bottomSheetState by mutableStateOf<BottomSheetState>(sheetState)

    internal var modalDetent by mutableStateOf(bottomSheetDetent)

    public var currentDetent: SheetDetent
        get() {
            return modalDetent
        }
        set(value) {
            val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden
                    || bottomSheetState.targetDetent != SheetDetent.Hidden

            if (isBottomSheetVisible) {
                bottomSheetState.currentDetent = value
            } else {
                modalDetent = value
            }
        }
    public val targetDetent: SheetDetent by derivedStateOf {
        bottomSheetState.targetDetent
    }
    public val isIdle: Boolean by derivedStateOf {
        bottomSheetState.isIdle
    }
    public val progress: Float by derivedStateOf {
        bottomSheetState.progress
    }
    public val offset: Float by derivedStateOf {
        bottomSheetState.offset
    }

    public suspend fun animateTo(value: SheetDetent) {
        val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden
                || bottomSheetState.targetDetent != SheetDetent.Hidden

        if (isBottomSheetVisible) {
            bottomSheetState.animateTo(value)
        } else {
            modalDetent = value
        }
    }

    public fun jumpTo(value: SheetDetent) {
        val isBottomSheetVisible =
            bottomSheetState.currentDetent != SheetDetent.Hidden || bottomSheetState.targetDetent != SheetDetent.Hidden

        if (isBottomSheetVisible) {
            bottomSheetState.jumpTo(value)
        } else {
            modalDetent = value
        }
    }
}

public class ModalBottomSheetScope internal constructor(
    internal val modalState: ModalBottomSheetState,
    internal val sheetState: BottomSheetState,
) {
    internal val visibleState = MutableTransitionState(false)
}

private class ModalContext(val onDismissRequest: () -> Unit)

private val LocalModalContext = compositionLocalOf<ModalContext> {
    error("Modal not initialized")
}
val DoNothing: () -> Unit = {}

@Composable
public fun ModalBottomSheet(
    state: ModalBottomSheetState,
    properties: ModalSheetProperties = ModalSheetProperties(),
    onDismiss: () -> Unit = DoNothing,
    content: @Composable (ModalBottomSheetScope.() -> Unit),
) {
    val currentCallback by rememberUpdatedState(onDismiss)

    CompositionLocalProvider(LocalModalContext provides ModalContext(currentCallback)) {
        val scope = remember { ModalBottomSheetScope(state, state.bottomSheetState) }
        scope.visibleState.targetState = state.currentDetent != SheetDetent.Hidden

        if (scope.visibleState.currentState || scope.visibleState.targetState || scope.visibleState.isIdle.not()) {
            val onKeyEvent = if (properties.dismissOnBackPress) {
                { event: KeyEvent ->
                    if (event.type == KeyEventType.KeyDown && (event.key == Key.Back || event.key == Key.Escape)) {
                        scope.sheetState.currentDetent = SheetDetent.Hidden
                        true
                    } else false
                }
            } else {
                { false }
            }

            Modal(onKeyEvent = onKeyEvent) {
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
    enabled: Boolean = true,
    content: @Composable (BottomSheetScope.() -> Unit)
) {
    var hasBeenIntroduced by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // waiting for the dialog to settle: can't just start animation here
        delay(50)
        sheetState.currentDetent = modalState.modalDetent
        hasBeenIntroduced = true
    }

    if (hasBeenIntroduced) {
        val context = LocalModalContext.current
        LaunchedEffect(sheetState.isIdle) {
            if (sheetState.isIdle) {
                if (sheetState.currentDetent == SheetDetent.Hidden) {
                    context.onDismissRequest()
                    modalState.modalDetent = SheetDetent.Hidden
                } else {
                    modalState.modalDetent = sheetState.currentDetent
                }
            }
        }
    }
    BottomSheet(
        state = sheetState,
        enabled = enabled,
        modifier = modifier,
        content = content
    )
}
