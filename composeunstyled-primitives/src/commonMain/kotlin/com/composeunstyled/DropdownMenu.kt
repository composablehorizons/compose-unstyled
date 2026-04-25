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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

@Composable
fun UnstyledDropdownMenu(
  onExpandRequest: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Box(
    modifier.onKeyEvent { event ->
      if (event.key == Key.DirectionDown) {
        if (event.type == KeyEventType.KeyDown) {
          onExpandRequest()
        }
        true
      } else {
        false
      }
    },
  ) {
    content()
  }
}

sealed interface DropdownPanelAnchor {
  object TopStart : DropdownPanelAnchor
  object TopEnd : DropdownPanelAnchor
  object BottomStart : DropdownPanelAnchor
  object BottomEnd : DropdownPanelAnchor
  object CenterStart : DropdownPanelAnchor
  object CenterEnd : DropdownPanelAnchor
}

@Composable
fun UnstyledDropdownMenuPanel(
  expanded: Boolean,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  anchor: DropdownPanelAnchor = DropdownPanelAnchor.BottomStart,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentPadding: PaddingValues = NoPadding,
  enter: EnterTransition = AppearInstantly,
  exit: ExitTransition = DisappearInstantly,
  verticalArrangement: Arrangement.Vertical = Arrangement.Top,
  horizontalAlignment: Alignment.Horizontal = Alignment.Start,
  content: @Composable ColumnScope.() -> Unit,
) {
  val density = LocalDensity.current
  val positionProvider = MenuContentPositionProvider(density, anchor)
  val transitionState = remember { MutableTransitionState(false) }

  if (expanded || transitionState.currentState || !transitionState.isIdle) {
    val menuFocusRequester = remember { FocusRequester() }

    Popup(
      properties = PopupProperties(
        focusable = true,
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
      ),
      onDismissRequest = onDismissRequest,
      popupPositionProvider = positionProvider,
    ) {
      LaunchedEffect(expanded) {
        transitionState.targetState = expanded
      }
      val currentFocusManager = LocalFocusManager.current

      AnimatedVisibility(
        visibleState = transitionState,
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
                onDismissRequest()
              }
              true
            }

            else -> false
          }
        },
      ) {
        Column(
          modifier
            .focusRequester(menuFocusRequester)
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding),
          horizontalAlignment = horizontalAlignment,
          verticalArrangement = verticalArrangement,
        ) {
          // Request focus when the menu becomes visible
          if (transitionState.currentState) {
            LaunchedEffect(Unit) {
              menuFocusRequester.requestFocus()
            }
          }
            content()
        }
      }
    }
  }
}

@Immutable
internal data class MenuContentPositionProvider(
  val density: Density,
  val anchor: DropdownPanelAnchor,
) : PopupPositionProvider {
  override fun calculatePosition(
    anchorBounds: IntRect,
    windowSize: IntSize,
    layoutDirection: LayoutDirection,
    popupContentSize: IntSize,
  ): IntOffset {
    val x = when (anchor) {
      DropdownPanelAnchor.TopStart, DropdownPanelAnchor.CenterStart, DropdownPanelAnchor.BottomStart -> anchorBounds.left
      DropdownPanelAnchor.TopEnd, DropdownPanelAnchor.CenterEnd, DropdownPanelAnchor.BottomEnd -> anchorBounds.right - popupContentSize.width
    }

    val y = when (anchor) {
      DropdownPanelAnchor.TopStart, DropdownPanelAnchor.TopEnd -> anchorBounds.top - popupContentSize.height
      DropdownPanelAnchor.CenterStart, DropdownPanelAnchor.CenterEnd -> anchorBounds.top - popupContentSize.height / 2
      DropdownPanelAnchor.BottomStart, DropdownPanelAnchor.BottomEnd -> anchorBounds.bottom
    }

    val clampedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
    val clampedY = y.coerceIn(0, windowSize.height - popupContentSize.height)

    return IntOffset(clampedX, clampedY)
  }
}
