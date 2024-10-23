package com.composables.core

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.*
import kotlin.jvm.JvmName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private fun Saver(
    animationSpec: AnimationSpec<Float>,
    density: Density,
    coroutineScope: CoroutineScope,
    sheetDetents: List<SheetDetent>,
    velocityThreshold: () -> Float,
    positionalThreshold: (totalDistance: Float) -> Float,
): Saver<BottomSheetState, *> = mapSaver(save = { mapOf("detent" to it.currentDetent.identifier) }, restore = { map ->
    val selectedDetentName = map["detent"]
    BottomSheetState(
        initialDetent = sheetDetents.first { it.identifier == selectedDetentName },
        detents = sheetDetents,
        coroutineScope = coroutineScope,
        animationSpec = animationSpec,
        velocityThreshold = velocityThreshold,
        positionalThreshold = positionalThreshold,
    )
})

@Composable
public fun rememberBottomSheetState(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween(),
    velocityThreshold: () -> Dp = { 125.dp },
    positionalThreshold: (totalDistance: Dp) -> Dp = { 56.dp }
): BottomSheetState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return rememberSaveable(
        saver = Saver(
            animationSpec = animationSpec,
            density = density,
            sheetDetents = detents,
            coroutineScope = scope,
            velocityThreshold = {
                with(density) {
                    velocityThreshold().toPx()
                }
            },
            positionalThreshold = { totalDistance ->
                with(density) {
                    positionalThreshold(totalDistance.toDp()).toPx()
                }
            }
        )
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
            }
        )
    }
}

@Immutable
public class SheetDetent(
    public val identifier: String,
    public val calculateDetentHeight: (containerHeight: Dp, sheetHeight: Dp) -> Dp
) {
    public companion object {
        public val FullyExpanded: SheetDetent =
            SheetDetent("fully-expanded") { containerHeight, sheetHeight -> sheetHeight }
        public val Hidden: SheetDetent = SheetDetent("hidden") { containerHeight, sheetHeight -> 0.dp }
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
}

public class BottomSheetState internal constructor(
    initialDetent: SheetDetent,
    internal val detents: List<SheetDetent>,
    private val coroutineScope: CoroutineScope,
    animationSpec: AnimationSpec<Float>,
    velocityThreshold: () -> Float,
    positionalThreshold: (totalDistance: Float) -> Float
) {
    init {
        check(detents.isNotEmpty()) {
            "Tried to create a bottom sheet without any detents. Make sure to pass at least one detent when creating your sheet's state."
        }
        check(detents.contains(initialDetent)) {
            "The initialDetent ${initialDetent.identifier} was not part of the included detents while creating the sheet's state."
        }

        val duplicates = detents.groupBy { it.identifier }
            .filter { it.value.size > 1 }
            .map { it.key }

        check(duplicates.isEmpty()) {
            "Detent identifiers need to be unique, but you passed the following detents multiple times: ${duplicates.joinToString { it }}."
        }
    }

    internal var closestDentToTop: Float by mutableStateOf(Float.NaN)

    internal var fullContentHeight = Float.NaN

    internal val coreAnchoredDraggableState = CoreAnchoredDraggableState(
        initialValue = initialDetent,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        animationSpec = animationSpec
    )

    public var currentDetent: SheetDetent
        get() = coreAnchoredDraggableState.currentValue
        set(value) {
            check(detents.contains(value)) {
                "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
            }
            coroutineScope.launch {
                coreAnchoredDraggableState.animateTo(
                    value,
                    coreAnchoredDraggableState.lastVelocity
                )
            }
        }

    public val targetDetent: SheetDetent
        get() = coreAnchoredDraggableState.targetValue

    public val isIdle: Boolean by derivedStateOf {
        progress == 1f && currentDetent == targetDetent && coreAnchoredDraggableState.isAnimationRunning.not()
    }

    public val progress: Float
        get() = coreAnchoredDraggableState.progress

    public val offset: Float by derivedStateOf {
        if (coreAnchoredDraggableState.offset.isNaN() || closestDentToTop.isNaN()) {
            1f
        } else {
            val offsetFromTop = coreAnchoredDraggableState.offset - closestDentToTop
            fullContentHeight - offsetFromTop
        }
    }

    public suspend fun animateTo(value: SheetDetent, velocity: Float = coreAnchoredDraggableState.lastVelocity) {
        check(detents.contains(value)) {
            "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
        }
        coreAnchoredDraggableState.animateTo(value, velocity)
    }

    public fun jumpTo(value: SheetDetent) {
        check(detents.contains(value)) {
            "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
        }
        coroutineScope.launch { coreAnchoredDraggableState.snapTo(value) }
    }
}

public class BottomSheetScope internal constructor(
    internal val state: BottomSheetState,
    enabled: Boolean
) {
    internal var enabled by mutableStateOf(enabled)
}

@Composable
public fun BottomSheet(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BottomSheetScope.() -> Unit,
) {
    val scope = remember { BottomSheetScope(state, enabled) }
    scope.enabled = enabled

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        var containerHeight by remember { mutableStateOf(Dp.Unspecified) }
        state.fullContentHeight = Float.NaN

        val density = LocalDensity.current

        Box(
            modifier = Modifier.matchParentSize()
                .onSizeChanged { containerHeight = with(density) { it.height.toDp() } }
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .let {
                        if (containerHeight != Dp.Unspecified) {
                            it.onSizeChanged { sheetSize ->
                                val sheetHeight = with(density) { sheetSize.height.toDp() }
                                state.fullContentHeight = sheetSize.height.toFloat()
                                val anchors = CoreDraggableAnchors {
                                    with(density) {
                                        state.closestDentToTop = Float.NaN

                                        state.detents.forEach { detent ->
                                            val contentHeight = detent
                                                .calculateDetentHeight(containerHeight, sheetHeight)
                                                .coerceIn(0.dp, sheetHeight)

                                            val offsetDp = containerHeight - contentHeight
                                            val offset = offsetDp.toPx()
                                            if (state.closestDentToTop.isNaN() || state.closestDentToTop > offset) {
                                                state.closestDentToTop = offset
                                            }
                                            detent at offset
                                        }
                                    }
                                }
                                val newTarget = if (state.isIdle) {
                                    state.coreAnchoredDraggableState.currentValue
                                } else {
                                    state.coreAnchoredDraggableState.targetValue
                                }

                                state.coreAnchoredDraggableState.updateAnchors(anchors, newTarget)
                            }
                        } else it
                    }
                    .layout { measurable, constraints ->
                        val maxDetentHeight = if (containerHeight == Dp.Unspecified) {
                            constraints.maxHeight
                        } else {
                            state.detents.maxOf { detent ->
                                detent.calculateDetentHeight(containerHeight, with(density) {
                                    constraints.maxHeight.toDp()
                                })
                            }.roundToPx()
                        }
                        val placeable = measurable.measure(
                            constraints.copy(maxHeight = maxDetentHeight)
                        )
                        layout(placeable.width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
                    .offset {
                        if (state.coreAnchoredDraggableState.offset.isNaN().not()) {
                            val requireOffset = state.coreAnchoredDraggableState.requireOffset()
                            val y = requireOffset.toInt()
                            IntOffset(x = 0, y = y)
                        } else {
                            IntOffset(x = 0, y = containerHeight.roundToPx())
                        }
                    }.then(
                        if (scope.enabled) {
                            Modifier.nestedScroll(
                                remember(state.coreAnchoredDraggableState, Orientation.Vertical) {
                                    ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                                        orientation = Orientation.Vertical,
                                        sheetState = state,
                                        draggableState = state.coreAnchoredDraggableState
                                    )
                                })
                        } else Modifier
                    )
                    .coreAnchoredDraggable(
                        state.coreAnchoredDraggableState,
                        Orientation.Vertical,
                        enabled = scope.enabled
                    )
                    .pointerInput(Unit) { detectTapGestures { } }
                    .align(Alignment.TopCenter)
                    .then(modifier)
            ) {
                scope.content()
            }
        }
    }
}

private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    draggableState: CoreAnchoredDraggableState<*>,
    orientation: Orientation,
    sheetState: BottomSheetState
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        if (source == NestedScrollSource.Drag) {
            val delta = available.toFloat()

            val canDragSheetUp = delta < 0 && sheetState.offset > 0f
            val canDragSheetDown = delta > 0 && sheetState.offset < 1f

            if (canDragSheetUp || canDragSheetDown) {
                return draggableState.dispatchRawDelta(delta).toOffset()
            }
        }
        return Offset.Zero
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        return if (source == NestedScrollSource.Drag) {
            draggableState.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = draggableState.requireOffset()
        return if (toFling < 0 && currentOffset > draggableState.anchors.minAnchor()) {
            draggableState.settle(velocity = toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        draggableState.settle(velocity = available.toFloat())
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

@Composable
public fun BottomSheetScope.DragIndication(
    modifier: Modifier = Modifier,
    indication: Indication = rememberFocusRingIndication(
        ringColor = Color.Blue,
        ringWidth = 4.dp,
        paddingValues = PaddingValues(horizontal = 8.dp, vertical = 14.dp),
        cornerRadius = 8.dp
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClickLabel: String? = "Toggle sheet"
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
        state.currentDetent = detent
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
