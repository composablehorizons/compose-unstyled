package com.composables.core

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.CoreAnchoredDraggableState
import androidx.compose.foundation.gestures.CoreDraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.coreAnchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import kotlin.jvm.JvmName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private fun Saver(
    animationSpec: AnimationSpec<Float>,
    density: Density,
    coroutineScope: CoroutineScope,
    sheetDetents: List<SheetDetent>,
): Saver<BottomSheetState, *> = mapSaver(save = { mapOf("detent" to it.currentDetent.identifier) }, restore = { map ->
    val selectedDetentName = map["detent"]
    BottomSheetState(
        initialDetent = sheetDetents.first { it.identifier == selectedDetentName },
        detents = sheetDetents,
        density = density,
        animationSpec = animationSpec,
        coroutineScope = coroutineScope,
    )
})

@Composable
public fun rememberBottomSheetState(
    initialDetent: SheetDetent,
    detents: List<SheetDetent> = listOf(SheetDetent.Hidden, SheetDetent.FullyExpanded),
    animationSpec: AnimationSpec<Float> = tween()
): BottomSheetState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    return rememberSaveable(
        saver = Saver(
            animationSpec = animationSpec,
            density = density,
            sheetDetents = detents,
            coroutineScope = scope,
        )
    ) {
        BottomSheetState(
            initialDetent = initialDetent,
            detents = detents,
            coroutineScope = scope,
            animationSpec = animationSpec,
            density = density,
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
    density: Density,
    animationSpec: AnimationSpec<Float>
) {
    init {
        check(detents.isNotEmpty()) {
            "Tried to create a bottom sheet without any detents. Make sure to pass at least one detent when creating your sheet's state."
        }
        check(detents.contains(initialDetent)) {
            "The initialDetent was not part of the included detents while creating the sheet's state."
        }

        val duplicates = detents.groupBy { it.identifier }
            .filter { it.value.size > 1 }
            .map { it.key }

        check(duplicates.isEmpty()) {
            "Detent identifiers need to be unique, but you passed the following detents multiple times: ${duplicates.joinToString { it }}."
        }
    }

    internal var closestDentToTop: Float by mutableStateOf(Float.NaN)

    internal var containerHeight = Float.NaN

    internal val coreAnchoredDraggableState = CoreAnchoredDraggableState(
        initialValue = initialDetent,
        positionalThreshold = { distance -> with(density) { 56.dp.toPx() } },
        velocityThreshold = { with(density) { 125.dp.toPx() } },
        animationSpec = animationSpec
    )

    public var currentDetent: SheetDetent
        get() = coreAnchoredDraggableState.currentValue
        set(value) {
            check(detents.contains(value)) {
                "Tried to set currentDetent to an unknown detent with identifier ${value.identifier}. Make sure that the detent is passed to the list of detents when instantiating the sheet's state."
            }
            coroutineScope.launch { coreAnchoredDraggableState.animateTo(value, coreAnchoredDraggableState.lastVelocity) }
        }

    public val targetDetent: SheetDetent
        get() = coreAnchoredDraggableState.targetValue

    public val isIdle: Boolean by derivedStateOf {
        progress == 1f && currentDetent == targetDetent
    }

    public val progress: Float
        get() = coreAnchoredDraggableState.progress

    public val offset: Float by derivedStateOf {
        if (coreAnchoredDraggableState.offset.isNaN()) {
            1f
        } else {
            val offsetFromTop = coreAnchoredDraggableState.offset - closestDentToTop
            1f - (offsetFromTop / containerHeight)
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

public class BottomSheetScope internal constructor(internal val state: BottomSheetState, enabled: Boolean) {
    internal val enabled by mutableStateOf(enabled)
}

@Composable
public fun BottomSheet(
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BottomSheetScope.() -> Unit,
) {
    val scope = remember { BottomSheetScope(state, enabled) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        var containerHeight by remember { mutableStateOf(Dp.Unspecified) }
        state.containerHeight = Float.NaN

        val density = LocalDensity.current

        Box(
            modifier = Modifier.matchParentSize().onSizeChanged {
                containerHeight = with(density) { it.height.toDp() }
                state.containerHeight = it.height.toFloat()
            },
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.let {
                    if (containerHeight != Dp.Unspecified) {
                        it.onSizeChanged {
                            val sheetHeight = with(density) { it.height.toDp() }
                            val anchors = CoreDraggableAnchors {
                                with(density) {
                                    state.closestDentToTop = Float.NaN

                                    state.detents.forEach { detent ->
                                        val detentHeight = detent
                                            .calculateDetentHeight(containerHeight, sheetHeight)
                                            .coerceIn(0.dp, sheetHeight)

                                        val offsetDp = containerHeight - detentHeight
                                        val offset = offsetDp.toPx()
                                        if (state.closestDentToTop.isNaN() || state.closestDentToTop > offset) {
                                            state.closestDentToTop = offset
                                        }
                                        detent at offset
                                    }
                                }
                            }
                            val previous = state.coreAnchoredDraggableState.currentValue
                            state.coreAnchoredDraggableState.updateAnchors(anchors, previous)
                        }
                    } else it
                }.offset {
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
                                    state = state.coreAnchoredDraggableState, orientation = Orientation.Vertical
                                )
                            })
                    } else Modifier
                )
                    .coreAnchoredDraggable(state.coreAnchoredDraggableState, Orientation.Vertical, enabled = scope.enabled)
                    .pointerInput(Unit) { detectTapGestures { } }
                    .then(modifier)
            ) {
                scope.content()
            }
        }
    }
}

// Code modified from Material 2's ModalBottomSheet.kt
private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    state: CoreAnchoredDraggableState<*>, orientation: Orientation
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        return if (source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = state.requireOffset()
        return if (toFling < 0 && currentOffset > state.anchors.minAnchor()) {
            state.settle(velocity = toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        state.settle(velocity = available.toFloat())
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
        modifier = modifier
            .onKeyEvent { event ->
                return@onKeyEvent if (event.key == Key.Spacebar && event.type == KeyEventType.KeyUp && enabled) {
                    onIndicationClicked()
                    true
                } else
                    false
            }
            .clickable(
                role = Role.Button,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = indication,
                onClickLabel = onClickLabel,
                onClick = onIndicationClicked
            )
    )
}
