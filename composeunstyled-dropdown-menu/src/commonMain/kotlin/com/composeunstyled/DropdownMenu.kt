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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
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
import androidx.compose.ui.input.key.type
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

internal val LocalDropdownMenuState = staticCompositionLocalOf<DropdownMenuState?> { null }

@Composable
fun UnstyledDropdownMenu(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  side: AnchorSide = AnchorSide.Bottom,
  alignment: AnchorAlignment = AnchorAlignment.Start,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
  panel: @Composable () -> Unit,
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
  val transitionState = remember { MutableTransitionState(false) }
  val state = remember(onExpandedChange, transitionState) {
    DropdownMenuState(
      onExpandedChange = onExpandedChange,
      transitionState = transitionState,
    )
  }

  LaunchedEffect(expanded) {
    transitionState.targetState = expanded
  }

  Box(
    modifier.onKeyEvent { event ->
      if (event.key == Key.DirectionDown) {
        if (event.type == KeyEventType.KeyDown) {
          onExpandedChange(true)
        }
        true
      } else {
        false
      }
    },
  ) {
    anchor()

    if (expanded || transitionState.currentState || !transitionState.isIdle) {
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
          panel()
        }
      }
    }
  }
}

@Composable
fun UnstyledDropdownMenuPanel(
  modifier: Modifier = Modifier,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable ColumnScope.() -> Unit,
) {
  val state = LocalDropdownMenuState.current

  if (state != null) {
    val menuFocusRequester = remember { FocusRequester() }
    val currentFocusManager = LocalFocusManager.current

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
      Column(
        modifier = modifier.focusRequester(menuFocusRequester),
      ) {
        // Request focus when the menu becomes visible
        if (state.transitionState.currentState) {
          LaunchedEffect(Unit) {
            menuFocusRequester.requestFocus()
          }
        }
        content()
      }
    }
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
  ): IntOffset = calculateAnchoredPosition(
    density = density,
    anchorBounds = anchorBounds,
    windowSize = windowSize,
    layoutDirection = layoutDirection,
    contentSize = popupContentSize,
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
  )
}

private val KeyEvent.isKeyDown: Boolean
  get() = type == KeyEventType.KeyDown
