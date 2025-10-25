@file:OptIn(ExperimentalFoundationApi::class)

package com.composables.core

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.*
import com.composables.core.androidx.compose.foundation.gestures.*
import com.composeunstyled.LocalContentColor
import com.composeunstyled.NoPadding
import com.composeunstyled.ProvideContentColor
import com.composeunstyled.buildModifier
import kotlin.jvm.JvmName
import kotlin.math.roundToInt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private fun Saver(
    animationSpec: AnimationSpec<Float>,
    coroutineScope: CoroutineScope,
    sheetDetents: List<SheetDetent>,
    velocityThreshold: () -> Float,
    positionalThreshold: (totalDistance: Float) -> Float,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    confirmDetentChange: (SheetDetent) -> Boolean,
    localDensity: () -> Density,
): Saver<BottomSheetState, *> = mapSaver(save = { mapOf("detent" to it.currentDetent.identifier) }, restore = { map ->
    val selectedDetentName = map["detent"]
    BottomSheetState(
        initialDetent = sheetDetents.first { it.identifier == selectedDetentName },
        detents = sheetDetents,
        coroutineScope = coroutineScope,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
        decayAnimationSpec = decayAnimationSpec,
        confirmDetentChange = confirmDetentChange,
        density = localDensity
    )
})

/**
 * Creates a [BottomSheetState]
 *
 * @param initialDetent The initial detent of the sheet.
 * @param detents The list of detents that the sheet can snap to.
 * @param animationSpec The animation spec to use for animating between detents.
 * @param confirmDetentChange Callback to confirm whether a detent change should be allowed.
 * @param decayAnimationSpec The animation spec to use for decay animations.
 * @param velocityThreshold The velocity threshold for determining whether to snap to the next detent.
 * @param positionalThreshold The positional threshold for determining whether to snap to the next detent.
 * @return A remembered [BottomSheetState] instance.
 */
@Composable
fun rememberBottomSheetState(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
    confirmDetentChange: (SheetDetent) -> Boolean = { true },
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    velocityThreshold: () -> Dp = { 125.dp },
    positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp },
): BottomSheetState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return rememberSaveable(
        saver = Saver(
            animationSpec = animationSpec,
            coroutineScope = scope,
            sheetDetents = detents,
            velocityThreshold = {
                with(density) {
                    velocityThreshold().toPx()
                }
            },
            positionalThreshold = { totalDistance ->
                with(density) {
                    positionalThreshold(totalDistance.toDp()).toPx()
                }
            },
            decayAnimationSpec = decayAnimationSpec,
            confirmDetentChange = confirmDetentChange,
            localDensity = { density })
    ) {
        BottomSheetState(
            initialDetent = initialDetent,
            detents = detents,
            coroutineScope = scope,
            animationSpec = animationSpec,
            velocityThreshold = {
                with(density) {
                    velocityThreshold().toPx()
                }
            },
            positionalThreshold = { totalDistance ->
                with(density) {
                    positionalThreshold(totalDistance.toDp()).toPx()
                }
            },
            decayAnimationSpec = decayAnimationSpec,
            confirmDetentChange = confirmDetentChange,
            density = { density })
    }
}

/**
 * A detent represents a stopping point for a bottom sheet.
 *
 * @property identifier A unique identifier for this detent.
 * @property calculateDetentHeight A function that calculates the height of this detent based on the container and sheet heights.
 */
@Immutable
class SheetDetent(
    val identifier: String, val calculateDetentHeight: (containerHeight: Dp, sheetHeight: Dp) -> Dp
) {
    companion object {
        /**
         * A detent that expands the sheet to its full height.
         */
        val FullyExpanded: SheetDetent = SheetDetent("fully-expanded") { containerHeight, sheetHeight -> sheetHeight }

        /**
         * A detent that hides the sheet.
         */
        val Hidden: SheetDetent = SheetDetent("hidden") { containerHeight, sheetHeight -> 0.dp }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SheetDetent

        return identifier == other.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }

    override fun toString(): String {
        return "SheetDetent(identifier='$identifier')"
    }
}

class BottomSheetState(
    initialDetent: SheetDetent,
    internal val detents: List<SheetDetent>,
    private val coroutineScope: CoroutineScope,
    animationSpec: AnimationSpec<Float>,
    velocityThreshold: () -> Float,
    positionalThreshold: (totalDistance: Float) -> Float,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    internal val confirmDetentChange: (SheetDetent) -> Boolean,
    private val density: () -> Density
) {
    init {
        check(detents.isNotEmpty()) {
            "Tried to create a bottom sheet without any detents. Make sure to pass at least one detent when creating your sheet's state."
        }
        check(detents.contains(initialDetent)) {
            "The initialDetent ${initialDetent.identifier} was not part of the included detents while creating the sheet's state."
        }

        val duplicates = detents.groupBy { it.identifier }.filter { it.value.size > 1 }.map { it.key }

        check(duplicates.isEmpty()) {
            "Detent identifiers need to be unique, but you passed the following detents multiple times: ${duplicates.joinToString { it }}."
        }
    }

    internal var closestDetentToTopPx: Float by mutableStateOf(Float.NaN)
    internal var contentHeightPx: Float by mutableStateOf(Float.NaN)
    internal var containerHeightPx: Float by mutableStateOf(Float.NaN)

    internal val anchoredDraggableState = UnstyledAnchoredDraggableState(
        initialValue = initialDetent,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        snapAnimationSpec = animationSpec,
        decayAnimationSpec = decayAnimationSpec,
        confirmValueChange = confirmDetentChange,
    )

    val currentDetent: SheetDetent
        get() {
            return anchoredDraggableState.settledValue
        }

    /**
     * The [SheetDetent] that the sheet is heading towards, in case of an animation or drag events.
     */
    var targetDetent: SheetDetent
        get() {
            // If we have a drag target, use that
            if (anchoredDraggableState.dragTarget != null) {
                return anchoredDraggableState.dragTarget as SheetDetent
            }
            // Otherwise determine target based on current offset and direction
            val currentOffset = anchoredDraggableState.offset
            if (currentOffset.isNaN()) return currentDetent

            val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
            val isMovingUp = currentOffset < currentPosition
            return anchoredDraggableState.anchors.closestAnchor(currentOffset, !isMovingUp) ?: currentDetent
        }
        set(value) {
            check(detents.contains(value)) {
                "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
            }
            coroutineScope.launch {
                anchoredDraggableState.animateTo(value)
            }
        }


    /**
     * Whether the sheet is currently rested at a detent.
     */
    val isIdle: Boolean by derivedStateOf {
        currentDetent == targetDetent && anchoredDraggableState.isAnimationRunning.not()
    }

    /**
     * A 0 to 1 value representing the progress of a sheet between its [currentDetent] and the [targetDetent].
     */
    @Deprecated("This will go away in 2.0. Use the progress function and provide the detents you need instead.")
    val progress: Float
        get() = anchoredDraggableState.progress(currentDetent, targetDetent)

    /**
     * A 0 to 1 value representing the progress of a sheet between its [from] and the [to] detents.
     */
    fun progress(from: SheetDetent, to: SheetDetent): Float {
        return anchoredDraggableState.progress(from, to)
    }

    /**
     * The amount the sheet has travelled from the bottom of its container in pixels.
     *
     */
    val offset: Float by derivedStateOf {
        if (anchoredDraggableState.offset.isNaN() || closestDetentToTopPx.isNaN()) {
            0f
        } else {
            val offsetFromTop = anchoredDraggableState.offset
            val diff = containerHeightPx - offsetFromTop
            diff.coerceAtLeast(0f)
        }
    }

    /**
     * Animates the sheet to the given [SheetDetent]
     */
    suspend fun animateTo(value: SheetDetent) {
        check(detents.contains(value)) {
            "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
        }
        anchoredDraggableState.animateTo(value)
    }

    /**
     * Instantly moves the sheet to the given [SheetDetent] without any animations.
     */
    fun jumpTo(value: SheetDetent) {
        check(detents.contains(value)) {
            "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
        }
        coroutineScope.launch { anchoredDraggableState.snapTo(value) }
    }

    fun invalidateDetents() {
        val density = density()
        val containerHeight = with(density) { containerHeightPx.toDp() }
        val sheetHeight = with(density) { contentHeightPx.toDp() }

        val anchors = UnstyledDraggableAnchors {
            with(density) {
                closestDetentToTopPx = Float.NaN

                detents.forEach { detent ->
                    val contentHeight =
                        detent.calculateDetentHeight(containerHeight, sheetHeight).coerceIn(0.dp, sheetHeight)

                    val offsetDp = containerHeight - contentHeight
                    val offset = offsetDp.toPx()
                    if (closestDetentToTopPx.isNaN() || closestDetentToTopPx > offset) {
                        closestDetentToTopPx = offset
                    }
                    detent at offset
                }
            }
        }
        val newTarget = if (isIdle) {
            anchoredDraggableState.currentValue
        } else {
            anchoredDraggableState.targetValue
        }

        anchoredDraggableState.updateAnchors(anchors, newTarget)
    }
}

class BottomSheetScope internal constructor(
    internal val state: BottomSheetState, enabled: Boolean
) {
    internal var enabled by mutableStateOf(enabled)
}

/**
 * A foundational component used to build bottom sheets.
 *
 * For interactive preview & code examples, visit [Bottom Sheet Documentation](https://composeunstyled.com/bottomsheet).
 *
 * ## Basic Example
 *
 * ```kotlin
 * val sheetState = rememberBottomSheetState(
 *     initialDetent = Hidden,
 * )
 *
 * Button(onClick = { sheetState.currentDetent = FullyExpanded }) {
 *     Text("Show Sheet")
 * }
 *
 * BottomSheet(
 *     state = sheetState,
 *     modifier = Modifier.fillMaxWidth(),
 * ) {
 *     Box(
 *         modifier = Modifier.fillMaxWidth().height(1200.dp),
 *         contentAlignment = Alignment.TopCenter
 *     ) {
 *         DragIndication(Modifier.width(32.dp).height(4.dp))
 *     }
 * }
 * ```
 *
 * @param state The [BottomSheetState] that controls the sheet.
 * @param modifier Modifier to be applied to the sheet.
 * @param enabled Whether the sheet is enabled.
 * @param imeAware Automatically move the sheet according to the soft keyboard's height.
 * @param content The content of the sheet.
 */
@Composable
fun BottomSheet(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
    contentPadding: PaddingValues = NoPadding,
    imeAware: Boolean = false,
    content: @Composable (BottomSheetScope.() -> Unit),
) {

    val scope = remember { BottomSheetScope(state, enabled) }
    SideEffect { scope.enabled = enabled }

    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().onSizeChanged {
            state.containerHeightPx = it.height.toFloat()
            state.invalidateDetents()
        },
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = buildModifier {
                add(
                    Modifier
                        .onSizeChanged {
                            state.contentHeightPx = it.height.toFloat()
                            state.invalidateDetents()
                        }
                        .sheetOffset(state = state, imeAware = imeAware)
                )
                if (scope.enabled && state.detents.size > 1) {
                    add(
                        Modifier
                            .unstyledAnchoredDraggable(
                                state = state.anchoredDraggableState,
                                orientation = Orientation.Vertical,
                                enabled = scope.enabled
                            )
                            .nestedScroll(
                                remember(state.anchoredDraggableState, Orientation.Vertical) {
                                    ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                                        orientation = Orientation.Vertical,
                                        sheetState = state.anchoredDraggableState,
                                        onFling = {
                                            coroutineScope.launch { state.anchoredDraggableState.settle(it) }
                                        }
                                    )
                                }
                            )
                    )
                }
                add(
                    modifier
                        .pointerInput(Unit) { detectTapGestures { } }
                        .clip(shape)
                        .background(backgroundColor)
                        .padding(contentPadding)
                )
            }
        ) {
            ProvideContentColor(contentColor) {
                scope.content()
            }

            OutOfBoundsSpacer(state)
        }
    }
}

@Composable
private fun OutOfBoundsSpacer(state: BottomSheetState) {
    Spacer(
        Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)

            // Calculate the amount of content that's outside the container
            val height = if (state.contentHeightPx.isNaN()) {
                placeable.height
            } else {
                (state.contentHeightPx - state.offset).coerceAtLeast(0f).roundToInt()
            }

            layout(placeable.width, height) {
                placeable.place(0, 0)
            }
        }
    )
}

@Composable
private fun Modifier.sheetOffset(state: BottomSheetState, imeAware: Boolean): Modifier {
    val density = LocalDensity.current
    val ime = WindowInsets.ime
    val imeHeight by remember {
        derivedStateOf {
            if (imeAware) ime.getBottom(density) else 0
        }
    }

    return this then buildModifier {
        add(Modifier.offset {
            when {
                state.containerHeightPx.isNaN() || state.containerHeightPx.isNaN() -> {
                    // hasn't been initialized
                    IntOffset(x = 0, y = 0)
                }

                state.anchoredDraggableState.offset.isNaN() -> {
                    // draggable state is not ready
                    // let the sheet take the height of the container
                    val y = state.containerHeightPx.roundToInt()
                    IntOffset(x = 0, y = y)
                }

                else -> {
                    val calculatedOffset = state.anchoredDraggableState.requireOffset() - imeHeight
                    // do not let the sheet's top go out of screen bounds
                    val y = calculatedOffset.coerceAtLeast(0f).toInt()
                    IntOffset(x = 0, y = y)
                }
            }
        })
        if (imeAware) {
            add(Modifier.consumeWindowInsets(ime))
        }
    }
}

private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    sheetState: UnstyledAnchoredDraggableState<SheetDetent>,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.UserInput) {
            sheetState.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        return if (source == NestedScrollSource.UserInput) {
            sheetState.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = sheetState.requireOffset()
        val minAnchor = sheetState.anchors.minAnchor()
        return if (toFling < 0 && currentOffset > minAnchor) {
            onFling(toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        onFling(available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

/**
 * A drag indication that can be used to control the bottom sheet.
 *
 * It is strongly advised to use this component within your [BottomSheet]. Sheets are not by default accessible, and the [DragIndication] allows toggling of the sheet via the keyboard.
 *
 * @param modifier Modifier to be applied to the drag indication.
 * @param indication The indication to be shown when the drag indication is interacted with.
 * @param interactionSource The interaction source for the drag indication.
 * @param onClickLabel The label for the click action.
 */
@Deprecated("This will go away in 2.0. Instead of use the overload that does not use the RingIndication as indication argument. Instead, style the focus ring yourself using the Modifier.focusRing().")
@Composable
fun BottomSheetScope.DragIndication(
    modifier: Modifier = Modifier, indication: Indication = rememberFocusRingIndication(
        ringColor = Color.Blue,
        ringWidth = 4.dp,
        paddingValues = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
        cornerRadius = 8.dp
    ), interactionSource: MutableInteractionSource? = null, onClickLabel: String? = "Toggle sheet"
) {
    var detentIndex by rememberSaveable { mutableStateOf(-1) }
    var goUp by rememberSaveable { mutableStateOf(true) }

    val onIndicationClicked: () -> Unit = {
        if (detentIndex == -1) {
            detentIndex = state.detents.indexOf(state.currentDetent)
        }
        if (detentIndex == state.detents.size - 1) goUp = false
        if (detentIndex == 0) goUp = true

        if (goUp) detentIndex++ else detentIndex--

        val detent = state.detents[detentIndex]
        state.targetDetent = detent
    }

    Box(
        modifier = modifier.clickable(
            role = Role.Button,
            enabled = enabled && state.detents.size > 1,
            interactionSource = interactionSource,
            indication = indication,
            onClickLabel = onClickLabel,
            onClick = onIndicationClicked
        )
    )
}

@Composable
fun BottomSheetScope.DragIndication(
    modifier: Modifier = Modifier,
    indication: Indication = LocalIndication.current,
    interactionSource: MutableInteractionSource? = null,
) {
    var detentIndex by rememberSaveable { mutableStateOf(-1) }
    var goUp by rememberSaveable { mutableStateOf(true) }

    val onIndicationClicked: () -> Unit = {
        if (detentIndex == -1) {
            detentIndex = state.detents.indexOf(state.currentDetent)
        }
        if (detentIndex == state.detents.size - 1) goUp = false
        if (detentIndex == 0) goUp = true

        if (goUp) detentIndex++ else detentIndex--

        val detent = state.detents[detentIndex]
        state.targetDetent = detent
    }

    Box(
        modifier = modifier.clickable(
            role = Role.Button,
            enabled = enabled && state.detents.size > 1,
            interactionSource = interactionSource,
            indication = indication,
            onClick = onIndicationClicked
        )
    )
}
