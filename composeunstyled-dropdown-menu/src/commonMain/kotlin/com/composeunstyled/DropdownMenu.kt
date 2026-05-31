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
@file:Suppress("ktlint:standard:max-line-length")

package com.composeunstyled

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
internal class DropdownMenuState(
  val onExpandedChange: (Boolean) -> Unit,
  val transitionState: MutableTransitionState<Boolean>,
  val initialFocusDirection: FocusDirection? = null,
  val onExitMenu: (FocusDirection) -> Unit = {},
)

interface DropdownMenuScope

private object DropdownMenuScopeInstance : DropdownMenuScope

class DropdownMenuPanelScope internal constructor() {
  internal var itemFocusTargets: List<DropdownMenuItemFocusTarget> = emptyList()
  internal var firstItemFocusTarget by mutableStateOf<DropdownMenuItemFocusTarget?>(null)
}

internal val LocalDropdownMenuState = staticCompositionLocalOf<DropdownMenuState> {
  DropdownMenuState(
    onExpandedChange = {},
    transitionState = MutableTransitionState(false),
  )
}

internal class DropdownMenuItemFocusTarget(
  val focusRequester: FocusRequester,
  var focused: Boolean = false,
)

@Composable
fun UnstyledDropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  side: AnchorSide = AnchorSide.Bottom,
  alignment: AnchorAlignment = AnchorAlignment.Start,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
  panel: @Composable DropdownMenuScope.() -> Unit,
  anchor: @Composable () -> Unit,
) {
  val modalState = rememberModalState(initiallyVisible = expanded)
  val focusManager = LocalFocusManager.current
  val anchorPreviousExitFocusRequester = remember { FocusRequester() }
  val anchorNextExitFocusRequester = remember { FocusRequester() }
  var initialFocusDirection by remember { mutableStateOf<FocusDirection?>(null) }
  var pendingExitFocusDirection by remember { mutableStateOf<FocusDirection?>(null) }
  SideEffect { modalState.transitionState.targetState = expanded }
  LaunchedEffect(expanded) {
    if (expanded.not()) {
      initialFocusDirection = null
    }
  }
  LaunchedEffect(expanded, pendingExitFocusDirection) {
    val direction = pendingExitFocusDirection
    if (expanded.not() && direction != null) {
      when (direction) {
        FocusDirection.Previous -> anchorPreviousExitFocusRequester.requestFocus()
        else -> anchorNextExitFocusRequester.requestFocus()
      }
      focusManager.moveFocus(direction)
      pendingExitFocusDirection = null
    }
  }
  val state = remember(onExpandedChange, modalState.transitionState, initialFocusDirection) {
    DropdownMenuState(
      onExpandedChange = onExpandedChange,
      transitionState = modalState.transitionState,
      initialFocusDirection = initialFocusDirection,
      onExitMenu = { direction ->
        pendingExitFocusDirection = direction
        onExpandedChange(false)
      },
    )
  }

  Box(
    modifier.onPreviewKeyEvent { event ->
      if (event.isKeyDown.not()) {
        return@onPreviewKeyEvent false
      }
      when (event.key) {
        Key.DirectionDown -> {
          initialFocusDirection = FocusDirection.Next
          if (expanded.not()) {
            onExpandedChange(true)
          }
          true
        }

        Key.DirectionUp -> {
          initialFocusDirection = FocusDirection.Previous
          if (expanded.not()) {
            onExpandedChange(true)
          }
          true
        }

        Key.Enter, Key.Spacebar -> {
          if (expanded) {
            false
          } else {
            initialFocusDirection = FocusDirection.Next
            onExpandedChange(true)
            true
          }
        }

        else -> false
      }
    },
  ) {
    AnchoredFloatingContent(
      layer = { content ->
        Modal(state = modalState) {
          if (expanded) {
            EscapeHandler {
              onExpandedChange(false)
            }
          }
          content()
        }
      },
      content = {
        CompositionLocalProvider(LocalDropdownMenuState provides state) {
          DropdownMenuScopeInstance.panel()
        }
      },
      contentModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTapGestures {
            onExpandedChange(false)
          }
        },
      side = side,
      alignment = alignment,
      sideOffset = sideOffset,
      alignmentOffset = alignmentOffset,
      anchor = {
        Box {
          // Hidden focus sentinels let Tab/Shift+Tab close the menu and continue traversal
          // from the anchor's position without requiring the caller's anchor to expose a modifier.
          Box(
            Modifier
              .size(0.dp)
              .focusRequester(anchorPreviousExitFocusRequester)
              .focusable(enabled = pendingExitFocusDirection == FocusDirection.Previous),
          )
          anchor()
          Box(
            Modifier
              .size(0.dp)
              .focusRequester(anchorNextExitFocusRequester)
              .focusable(enabled = pendingExitFocusDirection == FocusDirection.Next),
          )
        }
      },
    )
  }
}

@Composable
fun DropdownMenuScope.DropdownMenuPanel(
  modifier: Modifier = Modifier,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable DropdownMenuPanelScope.() -> Unit,
) {
  val state = LocalDropdownMenuState.current
  val modalState = LocalModalState.current
  val menuFocusRequester = remember { FocusRequester() }
  val currentFocusManager = LocalFocusManager.current
  val scope = remember { DropdownMenuPanelScope() }
  var placed by remember { mutableStateOf(false) }

  LaunchedEffect(
    modalState,
    placed,
    state.initialFocusDirection,
    state.transitionState.targetState,
  ) {
    if (placed && state.initialFocusDirection == null && state.transitionState.targetState) {
      modalState.awaitAttachedToWindow()
      menuFocusRequester.requestFocus()
    }
  }

  AnimatedVisibility(
    visibleState = state.transitionState,
    enter = enter,
    exit = exit,
    modifier = Modifier
      .onPreviewKeyEvent { event ->
        if (event.isKeyDown && event.key == Key.Tab) {
          state.onExitMenu(
            if (event.isShiftPressed) {
              FocusDirection.Previous
            } else {
              FocusDirection.Next
            },
          )
          true
        } else {
          false
        }
      }
      .onKeyEvent { event ->
        when (event.key) {
          Key.DirectionDown -> {
            if (event.isKeyDown) {
              if (scope.hasFocusedItem()) {
                currentFocusManager.moveFocus(FocusDirection.Next)
              } else {
                scope.itemFocusTargets.firstOrNull()?.focusRequester?.requestFocus()
              }
            }
            true
          }

          Key.DirectionUp -> {
            if (event.isKeyDown) {
              if (scope.hasFocusedItem()) {
                currentFocusManager.moveFocus(FocusDirection.Previous)
              } else {
                scope.itemFocusTargets.lastOrNull()?.focusRequester?.requestFocus()
              }
            }
            true
          }

          Key.MoveHome -> {
            if (event.isKeyDown) {
              scope.itemFocusTargets.firstOrNull()?.focusRequester?.requestFocus()
            }
            true
          }

          Key.MoveEnd -> {
            if (event.isKeyDown) {
              scope.itemFocusTargets.lastOrNull()?.focusRequester?.requestFocus()
            }
            true
          }

          else -> false
        }
      },
  ) {
    DropdownMenuPanelLayout(
      scope = scope,
      modifier = modifier
        .modalFragment()
        .onPlaced { placed = true }
        .focusRequester(menuFocusRequester)
        .focusable(),
    ) {
      scope.content()
    }
  }
}

@Composable
fun DropdownMenuPanelScope.MenuItem(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  closeOnClick: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = null,
  content: @Composable () -> Unit,
) {
  val state = LocalDropdownMenuState.current
  val modalState = LocalModalState.current
  val focusRequester = remember { FocusRequester() }
  val focusTarget = remember(focusRequester) {
    DropdownMenuItemFocusTarget(focusRequester = focusRequester)
  }
  var placed by remember { mutableStateOf(false) }
  val isFirstItem = firstItemFocusTarget == focusTarget
  val shouldRequestInitialFocus = when (state.initialFocusDirection) {
    FocusDirection.Next -> isFirstItem
    FocusDirection.Previous -> itemFocusTargets.lastOrNull() == focusTarget
    else -> false
  }

  LaunchedEffect(
    modalState,
    focusRequester,
    shouldRequestInitialFocus,
    placed,
    state.transitionState.targetState,
  ) {
    if (shouldRequestInitialFocus && placed && state.transitionState.targetState) {
      modalState.awaitAttachedToWindow()
      focusRequester.requestFocus()
    }
  }

  Box(
    modifier = modifier then buildModifier {
      add(DropdownMenuItemParentDataModifier(focusTarget))
      add(Modifier.onPlaced { placed = true })
      add(Modifier.focusRequester(focusRequester))
      add(Modifier.onFocusChanged { focusTarget.focused = it.hasFocus })
      add(
        Modifier.clickable(
          onClick = {
            onClick()
            if (closeOnClick) {
              state.onExpandedChange(false)
            }
          },
          enabled = enabled,
          interactionSource = interactionSource,
          indication = indication,
        ),
      )
      if (enabled) {
        add(Modifier.pointerHoverIcon(PointerIcon.Default))
      }
    },
  ) {
    content()
  }
}

private val KeyEvent.isKeyDown: Boolean
  get() = type == KeyEventType.KeyDown

@Composable
private fun DropdownMenuPanelLayout(
  scope: DropdownMenuPanelScope,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  Layout(
    modifier = modifier,
    content = content,
  ) { measurables, constraints ->
    val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { it.measure(looseConstraints) }
    val width = placeables
      .maxOfOrNull { it.width }
      ?.coerceIn(constraints.minWidth, constraints.maxWidth)
      ?: constraints.minWidth
    val contentHeight = placeables.sumOf { it.height }
    val height = contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)
    val orderedFocusTargets = measurables.mapNotNull {
      (it.parentData as? DropdownMenuItemParentData)?.focusTarget
    }

    scope.updateItemFocusTargets(orderedFocusTargets)

    layout(width, height) {
      var y = 0
      placeables.forEach { placeable ->
        placeable.placeRelative(x = 0, y = y)
        y += placeable.height
      }
    }
  }
}

private fun DropdownMenuPanelScope.updateItemFocusTargets(
  orderedFocusTargets: List<DropdownMenuItemFocusTarget>,
) {
  if (itemFocusTargets == orderedFocusTargets) {
    return
  }
  itemFocusTargets = orderedFocusTargets
  firstItemFocusTarget = orderedFocusTargets.firstOrNull()
}

private fun DropdownMenuPanelScope.hasFocusedItem(): Boolean {
  return itemFocusTargets.any { it.focused }
}

private data class DropdownMenuItemParentData(
  val focusTarget: DropdownMenuItemFocusTarget,
)

private data class DropdownMenuItemParentDataModifier(
  val focusTarget: DropdownMenuItemFocusTarget,
) : ParentDataModifier {
  override fun Density.modifyParentData(parentData: Any?): Any {
    return DropdownMenuItemParentData(focusTarget)
  }
}
