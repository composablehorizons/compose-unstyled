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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
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
    detents: List<SheetDetent>,
    private val coroutineScope: CoroutineScope,
    animationSpec: AnimationSpec<Float>,
    velocityThreshold: () -> Float,
    positionalThreshold: (totalDistance: Float) -> Float,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    internal val confirmDetentChange: (SheetDetent) -> Boolean,
    private val density: () -> Density
) {
    init {
        checkValidDetents(detents)
        check(detents.contains(initialDetent)) {
            "The initialDetent ${initialDetent.identifier} was not part of the included detents while creating the sheet's state."
        }
    }

    private fun checkValidDetents(detents: List<SheetDetent>) {
        check(detents.isNotEmpty()) {
            "Tried to create a bottom sheet without any detents. Make sure to pass at least one detent when creating your sheet's state."
        }

        val duplicates = detents.groupBy { it.identifier }.filter { it.value.size > 1 }.map { it.key }

        check(duplicates.isEmpty()) {
            "Detent identifiers need to be unique, but you passed the following detents multiple times: ${duplicates.joinToString { it }}."
        }
    }

    private var innerDetents: List<SheetDetent> by mutableStateOf(detents)

    var detents: List<SheetDetent>
        get() {
            return innerDetents
        }
        set(value) {
            checkValidDetents(value)
            innerDetents = value
            invalidateDetents()
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

    private val derivedTargetDetent: SheetDetent by derivedStateOf {
        // If we have a drag target, use that
        if (anchoredDraggableState.dragTarget != null) {
            return@derivedStateOf anchoredDraggableState.dragTarget as SheetDetent
        }
        // Otherwise determine target based on current offset and direction
        val currentOffset = anchoredDraggableState.offset
        if (currentOffset.isNaN()) return@derivedStateOf currentDetent

        val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
        val isMovingUp = currentOffset < currentPosition

        return@derivedStateOf anchoredDraggableState.anchors.closestAnchor(currentOffset, !isMovingUp) ?: currentDetent
    }

    /**
     * The [SheetDetent] that the sheet is heading towards, in case of an animation or drag events.
     */
    var targetDetent: SheetDetent
        get() {
            return derivedTargetDetent
        }
        set(value) {
            check(innerDetents.contains(value)) {
                "Cannot set targetDetent to a detent (${value.identifier}) that is not part of the detents of the sheet's state. Current detents are: ${innerDetents.joinToString()}"
            }
            coroutineScope.launch {
                anchoredDraggableState.animateTo(value)
            }
        }

    /**
     * Whether the sheet is currently rested at a detent.
     */
    val isIdle: Boolean by derivedStateOf {
        anchoredDraggableState.isDragging.not() && anchoredDraggableState.isAnimationRunning.not()
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
        check(innerDetents.contains(value)) {
            "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
        }
        anchoredDraggableState.animateTo(value)
    }

    /**
     * Instantly moves the sheet to the given [SheetDetent] without any animations.
     */
    fun jumpTo(value: SheetDetent) {
        check(innerDetents.contains(value)) {
            "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
        }
        coroutineScope.launch { anchoredDraggableState.snapTo(value) }
    }

    fun invalidateDetents() {
        val density = density()
        val containerHeight = with(density) { containerHeightPx.toDp() }
        val sheetHeight = with(density) { contentHeightPx.toDp() }

        // We are about to update detents, and we need to figure out the new target for the sheet
        // If anchors haven't been initialized yet (offset is NaN), always use currentDetent to establish the starting position
        // Otherwise, if we're idle, use currentDetent to maintain position
        // If we're animating/dragging, use targetDetent
        val newTarget = if (anchoredDraggableState.offset.isNaN()) {
            currentDetent
        } else if (isIdle) {
            currentDetent
        } else {
            targetDetent
        }

        // Capture the direction we were moving BEFORE updating anchors
        // We determine this by comparing the target position to the current detent position
        val wasMovingUp = if (!isIdle && newTarget != currentDetent) {
            val targetPosition = anchoredDraggableState.anchors.positionOf(newTarget)
            val currentPosition = anchoredDraggableState.anchors.positionOf(currentDetent)
            targetPosition < currentPosition
        } else {
            false
        }

        val anchors = UnstyledDraggableAnchors {
            with(density) {
                closestDetentToTopPx = Float.NaN

                innerDetents.forEach { detent ->
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

        val overridden = if (innerDetents.contains(newTarget)) {
            newTarget
        } else {
            // Find the closest detent in the direction of movement
            val currentOffset = anchoredDraggableState.offset
            val closestDetent = anchors.closestDetent(currentOffset, searchUpwards = wasMovingUp)

            (closestDetent ?: innerDetents.first()).also {
                // the anchored draggable state does not update its target value
                // so we have to force it in order to update
                targetDetent = it
            }
        }

        anchoredDraggableState.updateAnchors(anchors, newTarget = overridden)
    }
}

/**
 * Finds the closest detent to the given offset in the specified direction.
 *
 * For bottom sheets:
 * - Moving up means we want smaller offset values (closer to top of screen)
 * - Moving down means we want larger offset values (closer to bottom of screen)
 *
 * @param offset The current offset position
 * @param searchUpwards Whether to search upwards (true) or downwards (false)
 * @return The closest detent in the specified direction, or null if none found
 */
private fun UnstyledDraggableAnchors<SheetDetent>.closestDetent(
    offset: Float,
    searchUpwards: Boolean
): SheetDetent? {
    var closestDetent: SheetDetent? = null
    var closestDistance = Float.POSITIVE_INFINITY

    forEach { detent, position ->
        if (searchUpwards) {
            // Moving up: we want positions smaller than current offset
            if (position < offset) {
                val distance = offset - position
                if (distance < closestDistance) {
                    closestDetent = detent
                    closestDistance = distance
                }
            }
        } else {
            // Moving down: we want positions larger than current offset
            if (position > offset) {
                val distance = position - offset
                if (distance < closestDistance) {
                    closestDetent = detent
                    closestDistance = distance
                }
            }
        }
    }

    return closestDetent
}


class BottomSheetScope internal constructor(
    internal val state: BottomSheetState,
    enabled: Boolean
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
        }
    }
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
    modifier: Modifier = Modifier,
    indication: Indication = rememberFocusRingIndication(
        ringColor = Color.Blue,
        ringWidth = 4.dp,
        paddingValues = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
        cornerRadius = 8.dp
    ),
    interactionSource: MutableInteractionSource? = null,
    onClickLabel: String? = "Toggle sheet"
) {
    DragIndication(
        modifier = modifier,
        indication = indication,
        interactionSource = interactionSource
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

    val canExpand by remember {
        derivedStateOf {
            if (state.detents.size <= 1) return@derivedStateOf false

            val currentPosition = state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
            if (currentPosition.isNaN()) return@derivedStateOf false

            state.detents.any { detent ->
                val position = state.anchoredDraggableState.anchors.positionOf(detent)
                position.isNaN().not() && position < currentPosition
            }
        }
    }

    val canCollapse by remember {
        derivedStateOf {
            if (state.detents.size <= 1) return@derivedStateOf false

            val currentPosition = state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
            if (currentPosition.isNaN()) return@derivedStateOf false

            state.detents.any { detent ->
                val position = state.anchoredDraggableState.anchors.positionOf(detent)
                position.isNaN().not() && position > currentPosition
            }
        }
    }

    val canDismiss by remember {
        derivedStateOf {
            state.detents.contains(SheetDetent.Hidden) && state.currentDetent != SheetDetent.Hidden
        }
    }

    val coroutineScope = rememberCoroutineScope()

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
        modifier = modifier
            .semantics(mergeDescendants = false) {
                if (canExpand) {
                    expand {
                        // Find next detent with LOWER position value (more expanded = higher on screen)
                        val currentPosition = state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
                        val nextDetent = state.detents
                            .filter { detent ->
                                val pos = state.anchoredDraggableState.anchors.positionOf(detent)
                                pos < currentPosition
                            }
                            .maxByOrNull { detent ->
                                // Get the closest one (highest position that's still < current)
                                state.anchoredDraggableState.anchors.positionOf(detent)
                            }

                        if (nextDetent != null) {
                            coroutineScope.launch {
                                state.animateTo(nextDetent)
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
                if (canCollapse) {
                    collapse {
                        // Find next detent with HIGHER position value (less expanded = lower on screen)
                        val currentPosition = state.anchoredDraggableState.anchors.positionOf(state.currentDetent)
                        val nextDetent = state.detents
                            .filter { detent ->
                                val pos = state.anchoredDraggableState.anchors.positionOf(detent)
                                pos > currentPosition
                            }
                            .minByOrNull { detent ->
                                // Get the closest one (lowest position that's still > current)
                                state.anchoredDraggableState.anchors.positionOf(detent)
                            }

                        if (nextDetent != null) {
                            coroutineScope.launch {
                                state.animateTo(nextDetent)
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
                if (canDismiss) {
                    dismiss {
                        coroutineScope.launch {
                            state.animateTo(SheetDetent.Hidden)
                        }
                        true
                    }
                }
            }
            .clickable(
                role = Role.Button,
                enabled = enabled && state.detents.size > 1,
                interactionSource = interactionSource,
                indication = indication,
                onClick = onIndicationClicked
            )
    )
}
