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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@Stable
internal class DropdownMenuState(
  val onExpandedChange: (Boolean) -> Unit,
  val transitionState: MutableTransitionState<Boolean>,
)

interface DropdownMenuScope

private object DropdownMenuScopeInstance : DropdownMenuScope

class DropdownMenuPanelScope internal constructor() {
  internal val itemFocusRequesters = mutableStateListOf<FocusRequester>()
}

internal val LocalDropdownMenuState = staticCompositionLocalOf<DropdownMenuState> {
  DropdownMenuState(
    onExpandedChange = {},
    transitionState = MutableTransitionState(false),
  )
}

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
  val density = LocalDensity.current
  val positionProvider = MenuContentPositionProvider(
    density = density,
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
  )
  val transitionState = remember { MutableTransitionState(expanded) }
  SideEffect { transitionState.targetState = expanded }
  val state = remember(onExpandedChange, transitionState) {
    DropdownMenuState(
      onExpandedChange = onExpandedChange,
      transitionState = transitionState,
    )
  }

  Box(
    modifier.onPreviewKeyEvent { event ->
      if (event.isKeyDown.not()) {
        return@onPreviewKeyEvent false
      }
      when (event.key) {
        Key.DirectionDown, Key.Enter, Key.Spacebar -> {
          onExpandedChange(true)
          true
        }

        Key.DirectionUp -> {
          onExpandedChange(true)
          true
        }

        else -> false
      }
    },
  ) {
    anchor()

    if (expanded || transitionState.currentState || transitionState.isIdle.not()) {
      Popup(
        properties = PopupProperties(
          focusable = true,
          dismissOnBackPress = true,
          dismissOnClickOutside = true,
        ),
        onDismissRequest = {
          onExpandedChange(false)
        },
        popupPositionProvider = positionProvider,
      ) {
        CompositionLocalProvider(LocalDropdownMenuState provides state) {
          DropdownMenuScopeInstance.panel()
        }
      }
    }
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
  val menuFocusRequester = remember { FocusRequester() }
  val currentFocusManager = LocalFocusManager.current
  val scope = remember { DropdownMenuPanelScope() }

  AnimatedVisibility(
    visibleState = state.transitionState,
    enter = enter,
    exit = exit,
    modifier = Modifier.onKeyEvent { event ->
      when (event.key) {
        Key.DirectionDown -> {
          if (event.isKeyDown) {
            currentFocusManager.moveFocus(FocusDirection.Next)
          }
          true
        }

        Key.DirectionUp -> {
          if (event.isKeyDown) {
            currentFocusManager.moveFocus(FocusDirection.Previous)
          }
          true
        }

        Key.Home -> {
          if (event.isKeyDown) {
            scope.itemFocusRequesters.firstOrNull()?.requestFocus()
          }
          true
        }

        Key.MoveEnd -> {
          if (event.isKeyDown) {
            scope.itemFocusRequesters.lastOrNull()?.requestFocus()
          }
          true
        }

        Key.Escape -> {
          if (event.isKeyDown) {
            state.onExpandedChange(false)
          }
          true
        }

        else -> false
      }
    },
  ) {
    Box(
      modifier = modifier.focusRequester(menuFocusRequester),
    ) {
      // Request focus when the menu becomes visible
      if (state.transitionState.currentState) {
        LaunchedEffect(Unit) {
          menuFocusRequester.requestFocus()
        }
      }
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
  indication: Indication? = LocalIndication.current,
  content: @Composable () -> Unit,
) {
  val state = LocalDropdownMenuState.current
  val focusRequester = remember { FocusRequester() }

  DisposableEffect(focusRequester) {
    itemFocusRequesters.add(focusRequester)
    onDispose {
      itemFocusRequesters.remove(focusRequester)
    }
  }

  Box(
    modifier = modifier then buildModifier {
      add(Modifier.focusRequester(focusRequester))
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

@Immutable
internal data class MenuContentPositionProvider(
  val density: Density,
  val side: AnchorSide,
  val alignment: AnchorAlignment,
  val sideOffset: Dp,
  val alignmentOffset: Dp,
) : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize,
  ): IntOffset = calculateFloatingPlacement(
    density = density,
    anchorBounds = anchorBounds,
    windowSize = windowSize,
    layoutDirection = layoutDirection,
    contentSize = popupContentSize,
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
  ).position
}

private val KeyEvent.isKeyDown: Boolean
  get() = type == KeyEventType.KeyDown
