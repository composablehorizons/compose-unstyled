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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.AppearInstantly
import com.composeunstyled.DisappearInstantly
import com.composeunstyled.LocalContentColor
import com.composeunstyled.Modal
import kotlinx.coroutines.delay

/**
 * Properties that control the behavior of a [ModalBottomSheet].
 *
 * @param dismissOnBackPress Whether the sheet should be dismissed when the back button is pressed.
 * @param dismissOnClickOutside Whether the sheet should be dismissed when clicking outside of it.
 */
data class ModalSheetProperties(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
)

/**
 * Creates a [ModalBottomSheetState] that is remembered across compositions.
 *
 * @param initialDetent The initial detent of the sheet.
 * @param detents The list of detents that the sheet can snap to.
 * @param animationSpec The animation spec to use for animating between detents.
 * @param velocityThreshold The velocity threshold for determining whether to snap to the next detent.
 * @param positionalThreshold The positional threshold for determining whether to snap to the next detent.
 * @param confirmDetentChange Callback to confirm whether a detent change should be allowed.
 * @param decayAnimationSpec The animation spec to use for decay animations.
 * @return A remembered [ModalBottomSheetState] instance.
 */
@Composable
fun rememberModalBottomSheetState(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
    velocityThreshold: () -> Dp = { 125.dp },
    positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
    confirmDetentChange: (SheetDetent) -> Boolean = { true },
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay()
): ModalBottomSheetState {
    val sheetState = rememberBottomSheetState(
        initialDetent = initialDetent,
        detents = detents,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
        decayAnimationSpec = decayAnimationSpec,
        confirmDetentChange = confirmDetentChange,
    )
    return rememberSaveable(
        saver = mapSaver(
            save = { modalBottomSheetState -> mapOf("detent" to modalBottomSheetState.currentDetent.identifier) },
            restore = { map ->
                val restoredDetent = detents.first { it.identifier == map["detent"] }
                ModalBottomSheetState(
                    initialDetent = restoredDetent,
                    bottomSheetState = sheetState
                )
            }
        ),
        init = {
            ModalBottomSheetState(
                initialDetent = initialDetent,
                bottomSheetState = sheetState
            )
        }
    )
}

class ModalBottomSheetState(
    internal val initialDetent: SheetDetent,
    internal val bottomSheetState: BottomSheetState
) {

    internal var modalDetent by mutableStateOf(initialDetent)

    var currentDetent: SheetDetent
        get() {
            return modalDetent
        }
        @Deprecated(
            message = "This will go away in 2.0. Set the value to targetDetent instead",
            replaceWith = ReplaceWith("targetDetent")
        )
        set(value) {
            val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden
                    || bottomSheetState.targetDetent != SheetDetent.Hidden

            if (isBottomSheetVisible) {
                bottomSheetState.targetDetent = value
            } else {
                modalDetent = value
            }
        }
    var targetDetent: SheetDetent
        get() = bottomSheetState.targetDetent
        set(value) {
            val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden
                    || bottomSheetState.targetDetent != SheetDetent.Hidden

            if (isBottomSheetVisible) {
                bottomSheetState.targetDetent = value
            } else {
                modalDetent = value
            }
        }

    val isIdle: Boolean by derivedStateOf {
        currentDetent == targetDetent && bottomSheetState.anchoredDraggableState.isAnimationRunning.not()
    }

    @Deprecated("This will go away in 2.0. Use the progress function and provide the detents you need instead.")
    val progress: Float by derivedStateOf {
        bottomSheetState.progress
    }

    /**
     * A 0 to 1 value representing the progress of a sheet between its [from] and the [to] detents.
     */
    fun progress(from: SheetDetent, to: SheetDetent): Float {
        return bottomSheetState.progress(from, to)
    }

    val offset: Float by derivedStateOf {
        bottomSheetState.offset
    }

    suspend fun animateTo(value: SheetDetent) {
        val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden
                || bottomSheetState.targetDetent != SheetDetent.Hidden

        if (isBottomSheetVisible) {
            bottomSheetState.animateTo(value)
        } else {
            modalDetent = value
        }
    }

    fun jumpTo(value: SheetDetent) {
        val isBottomSheetVisible =
            bottomSheetState.currentDetent != SheetDetent.Hidden || bottomSheetState.targetDetent != SheetDetent.Hidden

        if (isBottomSheetVisible) {
            bottomSheetState.jumpTo(value)
        } else {
            modalDetent = value
        }
    }

    fun invalidateDetents() {
        bottomSheetState.invalidateDetents()
    }
}

class ModalBottomSheetScope internal constructor(
    internal val modalState: ModalBottomSheetState,
    internal val sheetState: BottomSheetState,
) {
    internal val scrimVisibilityState = MutableTransitionState(false)
}

private class ModalContext(val onDismissRequest: () -> Unit)

private val LocalModalContext = compositionLocalOf<ModalContext> {
    error("Modal not initialized")
}
val DoNothing: () -> Unit = {}

/**
 * A foundational component used to build modal bottom sheets.
 *
 * For interactive preview & code examples, visit [Modal Bottom Sheet Documentation](https://composeunstyled.com/modalbottomsheet).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val sheetState = rememberModalBottomSheetState(
 *     initialDetent = Hidden,
 * )
 *
 * Button(onClick = { sheetState.currentDetent = FullyExpanded }) {
 *     Text("Show Sheet")
 * }
 *
 * ModalBottomSheet(state = sheetState) {
 *     Sheet(modifier = Modifier.fillMaxWidth().background(Color.White)) {
 *         Box(
 *             modifier = Modifier.fillMaxWidth().height(1200.dp),
 *             contentAlignment = Alignment.TopCenter
 *         ) {
 *             DragIndication()
 *         }
 *     }
 * }
 * ```
 *
 * @param state The [ModalBottomSheetState] that controls the sheet.
 * @param properties The [ModalSheetProperties] that control the behavior of the sheet.
 * @param onDismiss Callback that is called when the sheet is dismissed.
 * @param content The content of the modal
 */
@Composable
fun ModalBottomSheet(
    state: ModalBottomSheetState,
    properties: ModalSheetProperties = ModalSheetProperties(),
    onDismiss: () -> Unit = DoNothing,
    content: @Composable (ModalBottomSheetScope.() -> Unit),
) {
    val currentCallback by rememberUpdatedState(onDismiss)

    CompositionLocalProvider(LocalModalContext provides ModalContext(currentCallback)) {
        val scope = remember { ModalBottomSheetScope(state, state.bottomSheetState) }

        val isSheetVisible = state.isIdle.not() || state.targetDetent != SheetDetent.Hidden

        val isScrimVisible = scope.scrimVisibilityState.isIdle.not() || scope.scrimVisibilityState.currentState

        if (isSheetVisible || isScrimVisible) {
            if (scope.sheetState.isIdle) {
                LaunchedEffect(Unit) {
                    // the sheet got dismissed by a gesture
                    // dismiss the scrim
                    if (scope.sheetState.currentDetent == SheetDetent.Hidden) {
                        scope.scrimVisibilityState.targetState = false
                    }
                }
            }
            fun onDismissRequest() {
                scope.scrimVisibilityState.targetState = false
                scope.sheetState.targetDetent = SheetDetent.Hidden
            }

            val onKeyEvent = if (properties.dismissOnBackPress) {
                { event: KeyEvent ->
                    if (
                        event.type == KeyEventType.KeyDown
                        && (event.key == Key.Back || event.key == Key.Escape)
                        && state.bottomSheetState.confirmDetentChange(SheetDetent.Hidden)
                    ) {
                        onDismissRequest()
                        true
                    } else false
                }
            } else {
                { false }
            }

            Modal(onKeyEvent = onKeyEvent) {
                LaunchedEffect(Unit) {
                    // modal entered the composition
                    // start the scrim animation
                    scope.scrimVisibilityState.targetState = true
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .let {
                            if (properties.dismissOnClickOutside) {
                                it.pointerInput(Unit) {
                                    detectTapGestures {
                                        if (state.bottomSheetState.confirmDetentChange(SheetDetent.Hidden)) {
                                            onDismissRequest()
                                        }
                                    }
                                }
                            } else it
                        }
                ) {
                    scope.content()
                }
            }
        }
    }
}

/**
 * A scrim is used to darken the content behind the sheet in order to signify that the rest of the screen is not interactive.
 *
 * @param modifier Modifier to be applied to the scrim.
 * @param scrimColor The color of the scrim.
 * @param enter The enter transition for the scrim.
 * @param exit The exit transition for the scrim.
 */
@Composable
fun ModalBottomSheetScope.Scrim(
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(0.6f),
    enter: EnterTransition = AppearInstantly,
    exit: ExitTransition = DisappearInstantly,
) {
    AnimatedVisibility(
        visibleState = scrimVisibilityState,
        enter = enter,
        exit = exit
    ) {
        Box(modifier.fillMaxSize().focusable(false).background(scrimColor))
    }
}

/**
 * The main content area of the modal bottom sheet.
 *
 * @param modifier Modifier to be applied to the sheet.
 * @param enabled Whether the sheet is enabled.
 * @param imeAware Automatically move the sheet according to the soft keyboard's height.
 * @param content The content of the sheet.
 */
@Composable
fun ModalBottomSheetScope.Sheet(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    imeAware: Boolean = false,
    content: @Composable (BottomSheetScope.() -> Unit)
) {
    var hasBeenIntroduced by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // waiting for the dialog to settle: can't just start animation here
        delay(50)
        sheetState.targetDetent = modalState.modalDetent
        hasBeenIntroduced = true
    }

    if (hasBeenIntroduced) {
        val context = LocalModalContext.current
        LaunchedEffect(sheetState.isIdle) {
            if (sheetState.isIdle) {
                if (sheetState.targetDetent == SheetDetent.Hidden) {
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
        content = content,
        contentPadding = contentPadding,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        imeAware = imeAware
    )
}
