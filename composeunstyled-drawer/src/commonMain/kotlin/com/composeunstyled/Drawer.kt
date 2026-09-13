/*
 * Copyright (c) 2026 Composable Horizons
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.composeunstyled

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.withoutEventHandling
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt

@Composable
fun <T : Any> UnstyledDrawer(
  state: UnstyledDrawerState<T>,
  modifier: Modifier = Modifier,
  placement: DrawerPlacement = DrawerPlacement.Bottom,
  presentation: DrawerPresentation = DrawerPresentation.Modal,
  gesturesEnabled: Boolean = true,
  dismissOnNavigateBack: Boolean = true,
  dismissOnClickOutside: Boolean = true,
  onDismissed: (change: DrawerValueChange<T>) -> Unit = {},
  overlay: (@Composable DrawerOverlayScope<T>.() -> Unit)? = null,
  systemUi: SystemUi = SystemUi(),
  content: @Composable DrawerScope.() -> Unit,
) {
  val animationScope = androidx.compose.runtime.rememberCoroutineScope()
  DisposableEffect(state, animationScope) {
    state.attachAnimationScope(animationScope)
    onDispose {
      state.attachAnimationScope(null)
    }
  }
  val zeroValue = state.zeroValue
  val visible = zeroValue == null ||
    state.hasVisiblePanel ||
    state.currentValue != zeroValue ||
    state.targetValue != zeroValue ||
    state.isDragging ||
    state.isEdgeSwipeInProgress

  LaunchedEffect(state.currentValue, state.targetValue, state.isIdle) {
    val dismissedValue = zeroValue ?: return@LaunchedEffect
    if (
      state.isIdle &&
      state.markDismissed() &&
      state.currentValue == dismissedValue &&
      state.targetValue == dismissedValue
    ) {
      onDismissed(state.lastDismissedChange())
    }
  }

  val drawerScope = remember(
    state,
    placement,
    presentation,
    gesturesEnabled,
    dismissOnClickOutside,
    overlay,
  ) {
    val typedOverlay: (@Composable DrawerOverlayScope<*>.() -> Unit)? =
      overlay?.let { suppliedOverlay ->
        {
          @Suppress("UNCHECKED_CAST")
          suppliedOverlay(this as DrawerOverlayScope<T>)
        }
      }
    DrawerScope(
      drawerState = state,
      placement = placement,
      presentation = presentation,
      gesturesEnabled = gesturesEnabled,
      dismissOnClickOutside = dismissOnClickOutside,
      overlay = typedOverlay,
    )
  }

  when (presentation) {
    DrawerPresentation.Modal -> {
      val modalState = rememberModalState(initiallyVisible = visible)
      SideEffect { modalState.transitionState.targetState = visible }
      LaunchedEffect(state, state.pendingTarget, modalState) {
        val target = state.pendingTarget ?: return@LaunchedEffect
        if (target.shouldAnimate && target.value != zeroValue && state.hasVisiblePanel.not()) {
          modalState.awaitAttachedToWindow()
        }
        state.settlePendingTarget(target)
      }
      DrawerOverlaySourceHost<T>(
        modifier = modifier,
        drawerScope = drawerScope,
        content = content,
        showContent = visible.not() && modalState.hasMountedFragments.not(),
      )
      if (visible || modalState.hasMountedFragments) {
        Modal(
          state = modalState,
          onKeyEvent = drawerDismissKeyEvent(state, dismissOnNavigateBack),
        ) {
          DrawerContent<T>(
            modifier = modifier,
            drawerScope = drawerScope,
            content = content,
            systemUi = systemUi,
            isOverlayContent = true,
            dismissOnNavigateBack = dismissOnNavigateBack,
          )
        }
      }
    }

    DrawerPresentation.Overlay -> {
      LaunchedEffect(state, state.pendingTarget) {
        val target = state.pendingTarget ?: return@LaunchedEffect
        state.settlePendingTarget(target)
      }
      val portalState = remember { DrawerPortalState() }
      val overlaySourceDrawerScope = remember(drawerScope) {
        DrawerScope(
          drawerState = drawerScope.drawerState,
          placement = drawerScope.placement,
          presentation = drawerScope.presentation,
          gesturesEnabled = drawerScope.gesturesEnabled,
          dismissOnClickOutside = drawerScope.dismissOnClickOutside,
          overlay = null,
          isEdgeSwipeSource = true,
        )
      }
      DrawerOverlaySourceHost<T>(
        modifier = modifier,
        drawerScope = overlaySourceDrawerScope,
        content = content,
        showContent = visible.not() || state.isEdgeSwipeInProgress,
      )
      if (visible || portalState.hasMountedFragments) {
        Portal(target = DrawerPortalTarget) {
          CompositionLocalProvider(LocalDrawerPortalState provides portalState) {
            DrawerContent<T>(
              modifier = modifier,
              drawerScope = drawerScope,
              content = content,
              systemUi = systemUi,
              isOverlayContent = true,
              dismissOnNavigateBack = dismissOnNavigateBack,
            )
          }
        }
      }
    }

    DrawerPresentation.Inline -> {
      LaunchedEffect(state, state.pendingTarget) {
        val target = state.pendingTarget ?: return@LaunchedEffect
        state.settlePendingTarget(target)
      }
      DrawerContent<T>(
        modifier = modifier,
        drawerScope = drawerScope,
        content = content,
        systemUi = systemUi,
        isOverlayContent = false,
        dismissOnNavigateBack = dismissOnNavigateBack,
      )
    }
  }
}

@JvmInline
value class DrawerPlacement internal constructor(private val value: Int) {
  companion object {
    val Start = DrawerPlacement(0)
    val End = DrawerPlacement(1)
    val Top = DrawerPlacement(2)
    val Bottom = DrawerPlacement(3)
  }
}

@JvmInline
value class DrawerPresentation internal constructor(private val value: Int) {
  companion object {
    val Modal = DrawerPresentation(0)
    val Overlay = DrawerPresentation(1)
    val Inline = DrawerPresentation(2)
  }
}

class DrawerOverlayScope<T : Any> internal constructor(
  internal val drawerState: UnstyledDrawerState<T>,
)

class DrawerScope internal constructor(
  internal val drawerState: UnstyledDrawerState<*>,
  internal val placement: DrawerPlacement,
  internal val presentation: DrawerPresentation,
  internal val gesturesEnabled: Boolean,
  internal val dismissOnClickOutside: Boolean,
  internal val overlay: (@Composable DrawerOverlayScope<*>.() -> Unit)?,
  internal val isEdgeSwipeSource: Boolean = false,
)

@Composable
private fun <T : Any> DrawerOverlaySourceHost(
  modifier: Modifier,
  drawerScope: DrawerScope,
  content: @Composable DrawerScope.() -> Unit,
  showContent: Boolean,
) {
  var sourceSize by remember { mutableStateOf(IntSize.Zero) }
  val density = androidx.compose.ui.platform.LocalDensity.current

  Box(
    modifier.onGloballyPositioned { coordinates ->
      sourceSize = coordinates.size
    },
  ) {
    if (showContent) {
      DrawerContent<T>(
        modifier = Modifier,
        drawerScope = drawerScope,
        content = content,
        systemUi = SystemUi(),
        isOverlayContent = false,
        dismissOnNavigateBack = false,
      )
    } else if (sourceSize != IntSize.Zero) {
      Spacer(
        Modifier.requiredSize(
          width = with(density) { sourceSize.width.toDp() },
          height = with(density) { sourceSize.height.toDp() },
        ),
      )
    }
  }
}

private fun <T : Any> drawerDismissKeyEvent(
  drawerState: UnstyledDrawerState<T>,
  dismissOnNavigateBack: Boolean,
): (KeyEvent) -> Boolean = { event ->
  val zeroValue = drawerState.zeroValue
  val shouldDismiss = dismissOnNavigateBack &&
    zeroValue != null &&
    event.type == KeyEventType.KeyDown &&
    (event.key == Key.Escape || event.key == Key.Back)
  if (shouldDismiss) {
    drawerState.requestTarget(
      value = zeroValue,
      reason = DrawerValueChange.Reason.NavigateBack,
    )
  }
  shouldDismiss
}

@Composable
private fun <T : Any> DrawerContent(
  modifier: Modifier,
  drawerScope: DrawerScope,
  content: @Composable DrawerScope.() -> Unit,
  systemUi: SystemUi,
  isOverlayContent: Boolean,
  dismissOnNavigateBack: Boolean,
) {
  @Suppress("UNCHECKED_CAST")
  val typedDrawerState = drawerScope.drawerState as UnstyledDrawerState<T>
  if (typedDrawerState.isPresented()) {
    ApplyAndroidSystemUi(
      systemUi = systemUi,
    )
  }

  RegisterDrawerPredictiveBack(
    state = typedDrawerState,
    presentation = drawerScope.presentation,
    dismissOnNavigateBack = dismissOnNavigateBack,
  )

  val zeroValue = typedDrawerState.zeroValue
  if (dismissOnNavigateBack && zeroValue != null && typedDrawerState.isAtZeroValue.not()) {
    EscapeHandler {
      typedDrawerState.requestTarget(
        value = zeroValue,
        reason = DrawerValueChange.Reason.NavigateBack,
      )
    }
  }

  Box(
    modifier.then(
      buildModifier {
        if (isOverlayContent) {
          add(
            Modifier
              .onKeyEvent(drawerDismissKeyEvent(typedDrawerState, dismissOnNavigateBack))
              .semantics { dialog() },
          )
        }
      },
    ),
  ) {
    drawerScope.content()
  }
}

private class DrawerPortalState {
  var mountedFragments by mutableIntStateOf(0)

  val hasMountedFragments: Boolean
    get() = mountedFragments > 0
}

private val DrawerPortalTarget = PortalTarget()

private val LocalDrawerPortalState = staticCompositionLocalOf<DrawerPortalState?> { null }

internal sealed class ResolvedDrawerPlacement(
  val orientation: Orientation,
  val isMinEdge: Boolean,
) {
  data object Start : ResolvedDrawerPlacement(Orientation.Horizontal, true)
  data object End : ResolvedDrawerPlacement(Orientation.Horizontal, false)
  data object Top : ResolvedDrawerPlacement(Orientation.Vertical, true)
  data object Bottom : ResolvedDrawerPlacement(Orientation.Vertical, false)

  val isHorizontal: Boolean
    get() = orientation == Orientation.Horizontal

  fun isOpeningDelta(delta: Float): Boolean {
    return if (isMinEdge) {
      delta > 0f
    } else {
      delta < 0f
    }
  }
}

/**
 * Provides the full-area render destination for [DrawerPresentation.Overlay] drawers.
 *
 * Overlay drawers outside a host render no overlay content.
 */
@Composable
fun DrawerHost(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  PortalHost(
    modifier = modifier,
    key = DrawerPortalTarget,
    content = content,
  )
}

@Composable
fun DrawerScope.SwipeArea(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .then(
        buildModifier {
          if (
            gesturesEnabled &&
            presentation == DrawerPresentation.Overlay &&
            drawerState.isAtZeroValue
          ) {
            add(Modifier.systemGestureExclusion())
          }
        },
      )
      .closedEdgeSwipe(
        drawerState = drawerState,
        resolvedPlacement = placement.resolve(LocalLayoutDirection.current),
        gesturesEnabled = gesturesEnabled && presentation == DrawerPresentation.Overlay,
      ),
  )
}

private fun DrawerPlacement.resolve(layoutDirection: LayoutDirection): ResolvedDrawerPlacement {
  return when (this) {
    DrawerPlacement.Top -> ResolvedDrawerPlacement.Top
    DrawerPlacement.Bottom -> ResolvedDrawerPlacement.Bottom
    DrawerPlacement.Start -> if (layoutDirection == LayoutDirection.Ltr) {
      ResolvedDrawerPlacement.Start
    } else {
      ResolvedDrawerPlacement.End
    }

    DrawerPlacement.End -> if (layoutDirection == LayoutDirection.Ltr) {
      ResolvedDrawerPlacement.End
    } else {
      ResolvedDrawerPlacement.Start
    }

    else -> ResolvedDrawerPlacement.Bottom
  }
}

@Composable
fun <T : Any> DrawerScope.Viewport(
  modifier: Modifier = Modifier,
  panelAlignment: DrawerPanelAlignment = DrawerPanelAlignment.Start,
  windowInsets: WindowInsets = WindowInsets(),
  content: @Composable DrawerViewportScope<T>.() -> Unit,
) {
  @Suppress("UNCHECKED_CAST")
  val typedDrawerState = drawerState as UnstyledDrawerState<T>
  val typedOverlay: (@Composable DrawerOverlayScope<T>.() -> Unit)? =
    overlay?.let { suppliedOverlay ->
      {
        suppliedOverlay(this)
      }
    }
  val hideViewport = isEdgeSwipeSource && drawerState.isEdgeSwipeInProgress
  val interactionSource = remember { MutableInteractionSource() }
  val interactionScope = androidx.compose.runtime.rememberCoroutineScope()
  val currentLayoutDirection = LocalLayoutDirection.current
  val context = remember(
    typedDrawerState,
    placement,
    presentation,
    gesturesEnabled,
    typedOverlay,
    interactionSource,
  ) {
    DrawerContext(
      state = typedDrawerState,
      placement = placement,
      presentation = presentation,
      gesturesEnabled = gesturesEnabled,
      dismissOnClickOutside = dismissOnClickOutside,
      overlay = typedOverlay,
      interactionSource = interactionSource,
    )
  }
  SideEffect {
    context.dismissOnClickOutside = dismissOnClickOutside
  }
  LaunchedEffect(interactionSource) {
    interactionSource.interactions.collect { interaction ->
      when (interaction) {
        is DragInteraction.Start -> drawerState.isDragging = true
        is DragInteraction.Stop,
        is DragInteraction.Cancel,
        -> drawerState.isDragging = false
      }
    }
  }
  val resolvedPlacementForModifier = placement.resolve(currentLayoutDirection)
  val zeroValue = drawerState.zeroValue
  val isInOverlayContent = LocalDrawerPortalState.current != null ||
    LocalModalState.current.isAttachedToWindow
  val visible = zeroValue == null ||
    drawerState.hasVisiblePanel ||
    drawerState.currentValue != zeroValue ||
    drawerState.targetValue != zeroValue
  val panelBounds = remember { DrawerPanelBounds() }

  Layout(
    modifier = modifier
      .then(
        buildModifier {
          if (hideViewport) {
            add(Modifier.drawWithContent {})
          }
        },
      )
      .then(
        buildModifier {
          if (presentation != DrawerPresentation.Inline && isInOverlayContent) {
            add(
              Modifier.consumeOverlayOutsideTap(
                panelBounds = panelBounds,
                onOutsideTap = if (context.dismissOnClickOutside && zeroValue != null) {
                  {
                    drawerState.requestTarget(
                      zeroValue,
                      DrawerValueChange.Reason.ClickOutside,
                    )
                  }
                } else {
                  null
                },
              ),
            )
          }
        },
      )
      .then(
        buildModifier {
          if (
            presentation != DrawerPresentation.Inline &&
            visible &&
            gesturesEnabled &&
            drawerState.hasMultipleValues()
          ) {
            add(
              Modifier.panelSwipe(
                drawerState = drawerState,
                resolvedPlacement = resolvedPlacementForModifier,
                panelBounds = panelBounds,
                interactionSource = interactionSource,
                overscrollEffect = { context.panelOverscrollEffect },
                coroutineScope = interactionScope,
              ),
            )
          }
        },
      ),
    content = {
      CompositionLocalProvider(LocalDrawerContext provides context) {
        if (presentation != DrawerPresentation.Inline && visible) {
          Box(
            Modifier
              .drawerOverlayBarrierParentData()
              .semantics {
                if (context.dismissOnClickOutside && zeroValue != null) {
                  dismiss {
                    drawerState.requestTarget(
                      zeroValue,
                      DrawerValueChange.Reason.ClickOutside,
                    )
                    true
                  }
                }
              },
          )
        }
        context.overlay?.invoke(DrawerOverlayScope(typedDrawerState))
        DrawerViewportScope<T>().content()
      }
    },
  ) { measurables, constraints ->
    check(
      constraints.maxWidth != Constraints.Infinity && constraints.maxHeight != Constraints.Infinity,
    ) {
      "Drawer Viewport requires finite maximum width and height constraints. " +
        "Constrain Viewport before using Drawer."
    }
    var panelIndex = -1
    var panelCount = 0
    var overlayCount = 0
    measurables.forEachIndexed { index, measurable ->
      when (measurable.parentData) {
        is DrawerPanelParentData -> {
          panelIndex = index
          panelCount++
        }
        DrawerOverlayParentData -> overlayCount++
      }
    }
    check(panelCount == 1) {
      "Drawer Viewport must contain exactly one Panel; found $panelCount."
    }
    check(overlayCount <= 1) {
      "Drawer may contain at most one Overlay; found $overlayCount."
    }
    val resolvedPlacement = placement.resolve(layoutDirection)
    val leftInset = windowInsets.getLeft(this, layoutDirection)
    val topInset = windowInsets.getTop(this)
    val rightInset = windowInsets.getRight(this, layoutDirection)
    val bottomInset = windowInsets.getBottom(this)
    val layoutWidth = constraints.maxWidth
    val layoutHeight = constraints.maxHeight
    val usableWidth = (layoutWidth - leftInset - rightInset).coerceAtLeast(0)
    val usableHeight = (layoutHeight - topInset - bottomInset).coerceAtLeast(0)
    val childConstraints = Constraints(
      minWidth = 0,
      maxWidth = usableWidth,
      minHeight = 0,
      maxHeight = usableHeight,
    )
    val placeables = measurables.map { measurable ->
      when (measurable.parentData) {
        DrawerOverlayBarrierParentData,
        DrawerOverlayParentData,
        -> measurable.measure(Constraints.fixed(layoutWidth, layoutHeight))
        else -> measurable.measure(childConstraints)
      }
    }
    val panelPlaceable = placeables[panelIndex]
    val panelMainAxisSizePx = panelPlaceable.mainAxisSize(resolvedPlacement)
    val panelCrossAxisOffset = panelPlaceable.crossAxisOffset(
      resolvedPlacement = resolvedPlacement,
      panelAlignment = panelAlignment,
      usableWidth = usableWidth,
      usableHeight = usableHeight,
      layoutDirection = layoutDirection,
    )
    val viewportMainAxisSizePx = if (resolvedPlacement.isHorizontal) {
      usableWidth
    } else {
      usableHeight
    }.toFloat()
    val hiddenEdgeInsetPx = when (resolvedPlacement) {
      ResolvedDrawerPlacement.Start -> leftInset
      ResolvedDrawerPlacement.End -> rightInset
      ResolvedDrawerPlacement.Top -> topInset
      ResolvedDrawerPlacement.Bottom -> bottomInset
    }.toFloat()
    drawerState.updateViewportSize(
      measuredSizePx = viewportMainAxisSizePx,
      anchoredToMinEdge = resolvedPlacement.isMinEdge,
      hiddenEdgeInsetPx = hiddenEdgeInsetPx,
      density = this,
    )
    drawerState.updateContentSize(panelMainAxisSizePx.toFloat())

    layout(layoutWidth, layoutHeight) {
      val panelMainAxisOffset =
        drawerState.panelMainAxisOffsetPx(resolvedPlacement, viewportMainAxisSizePx)
          .roundToInt()
      val panelX = leftInset +
        if (resolvedPlacement.isHorizontal) panelMainAxisOffset else panelCrossAxisOffset
      val panelY = topInset +
        if (resolvedPlacement.isHorizontal) panelCrossAxisOffset else panelMainAxisOffset
      panelBounds.update(
        left = panelX,
        top = panelY,
        width = panelPlaceable.drawerPanelWidth,
        height = panelPlaceable.drawerPanelHeight,
      )
      placeables.forEach { placeable ->
        if (
          placeable.parentData == DrawerOverlayBarrierParentData ||
          placeable.parentData == DrawerOverlayParentData
        ) {
          placeable.placeRelative(0, 0)
        }
      }
      placeables.forEach { placeable ->
        when (placeable.parentData) {
          DrawerOverlayBarrierParentData, DrawerOverlayParentData -> Unit
          is DrawerPanelParentData -> placePanel(
            placeable = placeable,
            x = panelX,
            y = panelY,
            hidden = drawerState.isPanelHidden,
          )
          else -> placeable.placeRelative(leftInset, topInset)
        }
      }
    }
  }
}

@JvmInline
value class DrawerPanelAlignment internal constructor(private val value: Int) {
  companion object {
    val Start = DrawerPanelAlignment(0)
    val Center = DrawerPanelAlignment(1)
    val End = DrawerPanelAlignment(2)
  }
}

class DrawerViewportScope<T : Any> internal constructor()

private class DrawerContext<T : Any>(
  internal val state: UnstyledDrawerState<T>,
  internal val placement: DrawerPlacement,
  internal val presentation: DrawerPresentation,
  internal val gesturesEnabled: Boolean,
  dismissOnClickOutside: Boolean,
  internal val overlay: (@Composable DrawerOverlayScope<T>.() -> Unit)?,
  internal val interactionSource: MutableInteractionSource,
) {
  internal var dismissOnClickOutside by mutableStateOf(dismissOnClickOutside)
  internal var panelOverscrollEffect: OverscrollEffect? by mutableStateOf(null)
}

private val LocalDrawerContext: ProvidableCompositionLocal<DrawerContext<*>?> =
  compositionLocalOf { null }

private fun Modifier.drawerOverlayBarrierParentData(): Modifier {
  return then(
    object : ParentDataModifier {
      override fun Density.modifyParentData(parentData: Any?): Any {
        return DrawerOverlayBarrierParentData
      }
    },
  )
}

private class DrawerPanelParentData {
  var measuredWidth: Int = 0
    private set

  var measuredHeight: Int = 0
    private set

  fun update(width: Int, height: Int) {
    measuredWidth = width
    measuredHeight = height
  }
}

private object DrawerOverlayParentData

private object DrawerOverlayBarrierParentData

private fun Placeable.mainAxisSize(resolvedPlacement: ResolvedDrawerPlacement): Int {
  return if (resolvedPlacement.isHorizontal) {
    drawerPanelWidth
  } else {
    drawerPanelHeight
  }
}

private val Placeable.drawerPanelWidth: Int
  get() {
    val panelParentData = parentData as DrawerPanelParentData
    return maxOf(width, measuredWidth, panelParentData.measuredWidth)
  }

private val Placeable.drawerPanelHeight: Int
  get() {
    val panelParentData = parentData as DrawerPanelParentData
    return maxOf(height, measuredHeight, panelParentData.measuredHeight)
  }

private fun Placeable.crossAxisOffset(
  resolvedPlacement: ResolvedDrawerPlacement,
  panelAlignment: DrawerPanelAlignment,
  usableWidth: Int,
  usableHeight: Int,
  layoutDirection: LayoutDirection,
): Int {
  val available = if (resolvedPlacement.isHorizontal) {
    usableHeight - drawerPanelHeight
  } else {
    usableWidth - drawerPanelWidth
  }.coerceAtLeast(0)
  return when (panelAlignment) {
    DrawerPanelAlignment.Center -> available / 2
    DrawerPanelAlignment.End -> available
    else -> 0
  }.let { offset ->
    if (
      resolvedPlacement.isHorizontal.not() &&
      layoutDirection == LayoutDirection.Rtl &&
      panelAlignment == DrawerPanelAlignment.Start
    ) {
      available
    } else if (
      resolvedPlacement.isHorizontal.not() &&
      layoutDirection == LayoutDirection.Rtl &&
      panelAlignment == DrawerPanelAlignment.End
    ) {
      0
    } else {
      offset
    }
  }
}

private fun androidx.compose.ui.layout.Placeable.PlacementScope.placePanel(
  placeable: Placeable,
  x: Int,
  y: Int,
  hidden: Boolean,
) {
  val placementX = x - (placeable.width - placeable.drawerPanelWidth) / 2
  val placementY = y - (placeable.height - placeable.drawerPanelHeight) / 2
  if (hidden) {
    placeable.placeWithLayer(placementX, placementY) {
      alpha = 0f
    }
  } else {
    placeable.place(placementX, placementY)
  }
}

@Composable
@Suppress("UNCHECKED_CAST")
fun <T : Any> DrawerViewportScope<T>.Panel(
  modifier: Modifier = Modifier,
  overscrollEffect: OverscrollEffect? = null,
  content: @Composable DrawerPanelScope<T>.() -> Unit,
) {
  val context = LocalDrawerContext.current as? DrawerContext<T>
    ?: error("Drawer Panel must be placed inside Drawer Viewport.")
  val state = context.state
  val layoutDirection = LocalLayoutDirection.current
  val resolvedPlacement = context.placement.resolve(layoutDirection)
  val orientation = resolvedPlacement.orientation
  val panelOverscrollEffect = remember(overscrollEffect) {
    overscrollEffect?.withoutVisualEffect()
  }
  val panelOverscrollVisualEffect = remember(overscrollEffect) {
    overscrollEffect?.withoutEventHandling()
  }
  val panelParentData = remember { DrawerPanelParentData() }
  SideEffect {
    context.panelOverscrollEffect = panelOverscrollEffect
  }
  DisposableEffect(context) {
    onDispose {
      context.panelOverscrollEffect = null
    }
  }

  Layout(
    modifier = modifier
      .zIndex(1f)
      .then(
        buildModifier {
          if (state.isPanelHidden) {
            add(
              Modifier
                .semantics { hideFromAccessibility() }
                .focusProperties { canFocus = false },
            )
          }
        },
      )
      .then(
        buildModifier {
          if (panelOverscrollVisualEffect != null) {
            add(Modifier.overscroll(panelOverscrollVisualEffect))
          }
        },
      )
      .then(
        buildModifier {
          if (
            context.gesturesEnabled &&
            state.hasMultipleValues()
          ) {
            add(
              Modifier
                .anchoredDraggable(
                  state = state.anchoredDraggableState,
                  orientation = orientation,
                  enabled = true,
                  interactionSource = context.interactionSource,
                  overscrollEffect = panelOverscrollEffect,
                  flingBehavior = remember(state) {
                    DrawerFlingBehavior(state)
                  },
                )
                .nestedScroll(
                  remember(state, resolvedPlacement) {
                    DrawerNestedScrollConnection(
                      drawerState = state,
                      resolvedPlacement = resolvedPlacement,
                    )
                  },
                ),
            )
          }
        },
      )
      .drawerPanelParentData(panelParentData),
    content = {
      DrawerPanelScope(state).content()
    },
  ) { measurables, constraints ->
    val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { measurable ->
      measurable.measure(childConstraints)
    }
    val width = maxOf(
      constraints.minWidth,
      placeables.maxOfOrNull { it.measuredWidth } ?: 0,
    )
    val height = maxOf(
      constraints.minHeight,
      placeables.maxOfOrNull { it.measuredHeight } ?: 0,
    )
    panelParentData.update(width = width, height = height)

    layout(width, height) {
      placeables.forEach { placeable ->
        placeable.placeRelative(0, 0)
      }
    }
  }
}

class DrawerPanelScope<T : Any> internal constructor(
  internal val drawerState: UnstyledDrawerState<T>,
)

private fun Modifier.drawerPanelParentData(drawerPanelParentData: DrawerPanelParentData): Modifier {
  return then(
    object : ParentDataModifier {
      override fun Density.modifyParentData(parentData: Any?): Any {
        return drawerPanelParentData
      }
    },
  )
}

@Composable
fun <T : Any> DrawerPanelScope<T>.DragHandle(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit = {},
) {
  val expandTarget = drawerState.nextMoreVisibleValue()
  val collapseTarget = drawerState.nextLessVisibleValue()
  val dismissTarget = drawerState.zeroValue
    ?.takeIf { it != drawerState.currentValue }
  val hasAction = expandTarget != null || collapseTarget != null || dismissTarget != null

  Box(
    modifier
      .then(
        buildModifier {
          if (hasAction) {
            add(Modifier.focusable())
          }
        },
      )
      .semantics(mergeDescendants = false) {
        if (expandTarget != null) {
          expand {
            drawerState.requestTarget(
              value = expandTarget,
              reason = DrawerValueChange.Reason.AccessibilityAction,
            )
            true
          }
        }
        if (collapseTarget != null) {
          collapse {
            drawerState.requestTarget(
              value = collapseTarget,
              reason = DrawerValueChange.Reason.AccessibilityAction,
            )
            true
          }
        }
        if (dismissTarget != null) {
          dismiss {
            drawerState.requestTarget(
              value = dismissTarget,
              reason = DrawerValueChange.Reason.AccessibilityAction,
            )
            true
          }
        }
      },
  ) {
    content()
  }
}

@Composable
fun <T : Any> DrawerOverlayScope<T>.Overlay(
  modifier: Modifier = Modifier,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable () -> Unit = {},
) {
  val context = LocalDrawerContext.current
    ?: error("Drawer Overlay must be placed inside Drawer Viewport.")
  val isModalPresentation = context.presentation == DrawerPresentation.Modal
  val isOverlayPresentation = context.presentation == DrawerPresentation.Overlay
  val targetVisible = drawerState.isPresented()
  val visibilityState = remember(drawerState, isOverlayPresentation) {
    MutableTransitionState(false)
  }
  SideEffect {
    visibilityState.targetState = targetVisible
  }
  AnimatedVisibility(
    visibleState = visibilityState,
    modifier = Modifier.drawerOverlayParentData(),
    enter = enter,
    exit = exit,
  ) {
    Box(
      modifier.then(
        buildModifier {
          when {
            isModalPresentation -> add(Modifier.modalFragment())
            isOverlayPresentation -> add(Modifier.drawerPortalFragment())
          }
        },
      ),
    ) {
      content()
    }
  }
}

internal fun <T : Any> UnstyledDrawerState<T>.isPresented(): Boolean {
  val zeroValue = this.zeroValue
  return zeroValue == null ||
    targetValue != zeroValue ||
    isDragging ||
    isEdgeSwipeInProgress ||
    (currentValue == zeroValue && hasVisiblePanel)
}

private fun Modifier.drawerOverlayParentData(): Modifier {
  return then(
    object : ParentDataModifier {
      override fun Density.modifyParentData(parentData: Any?): Any {
        return DrawerOverlayParentData
      }
    },
  )
}

@Composable
private fun Modifier.drawerPortalFragment(): Modifier {
  val state = LocalDrawerPortalState.current
  if (state == null) return this

  DisposableEffect(state) {
    state.mountedFragments += 1
    onDispose {
      state.mountedFragments -= 1
    }
  }
  return this
}
