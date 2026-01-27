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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composeunstyled.AppearInstantly
import com.composeunstyled.DisappearInstantly
import com.composeunstyled.EscapeHandler
import com.composeunstyled.LocalContentColor
import com.composeunstyled.Modal
import com.composeunstyled.NoPadding
import com.composeunstyled.buildModifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val DoNothing: () -> Unit = {}

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
 * @param anchor The edge from which the sheet appears ([SheetAnchor.Bottom] by default, or [SheetAnchor.Top] for top sheets).
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
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    anchor: SheetAnchor = SheetAnchor.Bottom,
): ModalBottomSheetState {
    val sheetState = rememberBottomSheetState(
        initialDetent = initialDetent,
        detents = detents,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
        decayAnimationSpec = decayAnimationSpec,
        confirmDetentChange = confirmDetentChange,
        anchor = anchor,
    )
    val scope = rememberCoroutineScope()
    return rememberSaveable(
        saver = mapSaver(
            save = { modalBottomSheetState -> mapOf("detent" to modalBottomSheetState.currentDetent.identifier) },
            restore = { map ->
                val restoredDetent = detents.first { it.identifier == map["detent"] }
                ModalBottomSheetState(
                    initialDetent = restoredDetent,
                    bottomSheetState = sheetState,
                    scope = scope
                )
            }
        ),
        init = {
            ModalBottomSheetState(
                initialDetent = initialDetent,
                bottomSheetState = sheetState,
                scope = scope
            )
        }
    )
}

class ModalBottomSheetState(
    initialDetent: SheetDetent,
    internal val bottomSheetState: BottomSheetState,
    val scope: CoroutineScope,
) {
    internal var modalDetent by mutableStateOf(initialDetent)

    internal val scrimState = MutableTransitionState(initialState = initialDetent != SheetDetent.Hidden)

    internal var modalIsAdded by mutableStateOf(false)
    internal var pendingDetentChange: Job? = null

    val anchor: SheetAnchor
        get() = bottomSheetState.anchor

    var currentDetent: SheetDetent
        get() {
            return modalDetent
        }
        @Deprecated(
            message = "This will go away in 2.0. Set the value to targetDetent instead",
            replaceWith = ReplaceWith("targetDetent")
        )
        set(value) {
            targetDetent = value
        }
    var targetDetent: SheetDetent
        get() = bottomSheetState.targetDetent
        set(value) {
            scope.launch {
                animateTo(value)
            }
        }

    val isIdle: Boolean
        get() = bottomSheetState.isIdle

    @Deprecated("This will go away in 2.0. Use the progress function and provide the detents you need instead.")
    val progress: Float by derivedStateOf {
        progress(currentDetent, targetDetent)
    }

    /**
     * A 0 to 1 value representing the progress of a sheet between its [from] and the [to] detents.
     */
    fun progress(from: SheetDetent, to: SheetDetent): Float {
        return bottomSheetState.progress(from, to)
    }

    val offset: Float by derivedStateOf {
        if (modalIsAdded.not()) {
            0f
        } else {
            bottomSheetState.offset
        }
    }

    suspend fun animateTo(value: SheetDetent) {
        val isBottomSheetVisible = bottomSheetState.currentDetent != SheetDetent.Hidden
                || bottomSheetState.targetDetent != SheetDetent.Hidden

        if (isBottomSheetVisible) {
            bottomSheetState.animateTo(value)
        } else {
            modalDetent = value
            pendingDetentChange?.cancel()
            pendingDetentChange = scope.launch {
                awaitModal()
                scrimState.targetState = true
                bottomSheetState.animateTo(value)
            }
            pendingDetentChange?.join()
        }
    }

    fun jumpTo(value: SheetDetent) {
        val isBottomSheetVisible =
            bottomSheetState.currentDetent != SheetDetent.Hidden || bottomSheetState.targetDetent != SheetDetent.Hidden

        modalDetent = value

        if (isBottomSheetVisible) {
            bottomSheetState.jumpTo(value)
        } else {
            pendingDetentChange?.cancel()
            pendingDetentChange = scope.launch {
                awaitModal()
                scrimState.targetState = true
                bottomSheetState.jumpTo(value)
            }
        }
    }

    private suspend fun awaitModal() {
        // Workaround until https://github.com/composablehorizons/compose-unstyled/issues/89 is unblocked
        snapshotFlow { modalIsAdded }.distinctUntilChanged().filter { modalIsAdded }.first()

        // wait until the anchored state is used and is measured
        snapshotFlow { bottomSheetState.anchoredDraggableState.offset.isNaN().not() }.filter { it }.first()
    }

    fun invalidateDetents() {
        bottomSheetState.invalidateDetents()
    }
}

class ModalBottomSheetScope internal constructor(
    internal val modalState: ModalBottomSheetState,
    internal val sheetState: BottomSheetState,
)

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
 * @param onDismiss Callback that is called right before the sheet is about to be dismissed.
 * @param content The content of the modal
 */
@Composable
fun ModalBottomSheet(
    state: ModalBottomSheetState,
    properties: ModalSheetProperties = ModalSheetProperties(),
    onDismiss: () -> Unit = DoNothing,
    content: @Composable (ModalBottomSheetScope.() -> Unit),
) {
    val currentDismissCallback by rememberUpdatedState(onDismiss)
    val scope = remember { ModalBottomSheetScope(state, state.bottomSheetState) }
    val isSheetVisible by remember { derivedStateOf { state.isIdle.not() || state.targetDetent != SheetDetent.Hidden } }
    val isScrimVisible by remember {
        derivedStateOf {
            (state.scrimState.isIdle.not() || state.currentDetent != SheetDetent.Hidden)
        }
    }

    fun onDismissRequest() {
        currentDismissCallback()
        state.scrimState.targetState = false
        state.modalDetent = SheetDetent.Hidden
        scope.sheetState.targetDetent = SheetDetent.Hidden
    }


    if (isSheetVisible || isScrimVisible) {
        Modal {
            DisposableEffect(Unit) {
                state.modalIsAdded = true
                onDispose {
                    state.modalIsAdded = false
                    state.pendingDetentChange?.cancel()
                    state.pendingDetentChange = null
                }
            }
            if (state.modalIsAdded) {
                var hasBeenShown by remember { mutableStateOf(false) }
                if (hasBeenShown.not()) {
                    LaunchedEffect(state.offset > 0f) {
                        hasBeenShown = true
                    }
                } else {
                    LaunchedEffect(state.bottomSheetState.isIdle) {
                        if (state.bottomSheetState.isIdle && state.bottomSheetState.currentDetent == SheetDetent.Hidden) {
                            onDismissRequest()
                        }
                    }
                }
            }

            if (properties.dismissOnBackPress) {
                EscapeHandler {
                    onDismissRequest()
                }
            }
            Box(
                modifier = Modifier.fillMaxSize() then buildModifier {
                    if (properties.dismissOnClickOutside) {
                        add(Modifier.pointerInput(Unit) {
                            detectTapGestures {
                                if (state.bottomSheetState.confirmDetentChange(SheetDetent.Hidden)) {
                                    onDismissRequest()
                                }
                            }
                        })
                    }
                }
            ) {
                scope.content()
            }
        }
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
    contentPadding: PaddingValues = NoPadding,
    imeAware: Boolean = false,
    content: @Composable (BottomSheetScope.() -> Unit)
) {
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
        visibleState = modalState.scrimState,
        enter = enter,
        exit = exit,
    ) {
        // keep the rendered content as a child of animated visibility, because it does not animate itself in
        Box(modifier = modifier.fillMaxSize().background(scrimColor))
    }
}