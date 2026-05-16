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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import core.com.composeunstyled.interceptingLongClickable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class TooltipState {
  var show by mutableStateOf(false)
  var placement by mutableStateOf(TooltipPlacement())
}

@Immutable
data class TooltipPlacement(
  val side: AnchorSide = AnchorSide.Top,
  val alignment: AnchorAlignment = AnchorAlignment.Center,
  val positionAdjustment: IntOffset = IntOffset.Zero,
)

interface TooltipScope

private object TooltipScopeInstance : TooltipScope

private val LocalTooltipState = staticCompositionLocalOf<TooltipState> {
  TooltipState()
}

private enum class TooltipFocusTrigger {
  Keyboard, Pointer
}

@Composable
fun UnstyledTooltip(
  enabled: Boolean = true,
  panel: @Composable TooltipScope.() -> Unit,
  side: AnchorSide = AnchorSide.Top,
  alignment: AnchorAlignment = AnchorAlignment.Center,
  sideOffset: Dp = 0.dp,
  alignmentOffset: Dp = 0.dp,
  longPressShowDurationMillis: Long = 1500L,
  hoverDelayMillis: Long = 0L,
  anchor: @Composable () -> Unit,
) {
  val state = remember { TooltipState() }
  var focused by remember { mutableStateOf(false) }
  var focusTrigger by remember { mutableStateOf<TooltipFocusTrigger?>(null) }
  var hovered by remember { mutableStateOf(false) }
  var nextFocusTrigger by remember { mutableStateOf<TooltipFocusTrigger?>(null) }
  val scope = rememberCoroutineScope()

  var timerJob: Job? by remember { mutableStateOf(null) }
  var hoverDelayJob: Job? by remember { mutableStateOf(null) }
  val keyboardFocusShowsTooltip = focused && focusTrigger == TooltipFocusTrigger.Keyboard

  fun showTooltip(duration: Long) {
    timerJob = scope.launch {
      state.show = true
      delay(duration)
      state.show = false
    }
  }

  fun dismissTooltip() {
    hovered = false
    focused = false
    focusTrigger = null
    nextFocusTrigger = null
    hoverDelayJob?.cancel()
    timerJob?.cancel()
    hoverDelayJob = null
    timerJob = null
    state.show = false
  }

  if (state.show) {
    EscapeHandler {
      dismissTooltip()
    }
  }

  SideEffect {
    state.placement = state.placement.copy(
      side = side,
      alignment = alignment,
    )
  }

  // focus handling - show instantly when focused
  LaunchedEffect(focused, focusTrigger, hovered, enabled) {
    if (enabled && keyboardFocusShowsTooltip) {
      state.show = true

      hoverDelayJob?.cancel()
      timerJob?.cancel()

      hoverDelayJob = null
      timerJob = null
    } else if (keyboardFocusShowsTooltip.not() && hovered.not()) {
      state.show = false
    }
  }

  // hover handling - show after hoverDelayMillis
  LaunchedEffect(hovered, enabled, hoverDelayMillis) {
    if (enabled && hovered) {
      hoverDelayJob?.cancel()

      hoverDelayJob = scope.launch {
        delay(hoverDelayMillis)
        state.show = true
      }
    } else {
      // Mouse left - cancel hover delay and hide tooltip
      hoverDelayJob?.cancel()
      hoverDelayJob = null
      if (keyboardFocusShowsTooltip.not()) {
        state.show = false
      }
    }
  }

  FloatingContent(
    modifier = Modifier
      .interceptingLongClickable(
        onLongPress = {
          showTooltip(longPressShowDurationMillis)
        },
      )
      .onPreviewKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown && event.key == Key.Escape && state.show) {
          dismissTooltip()
          true
        } else {
          false
        }
      }
      .onFocusChanged {
        if (it.hasFocus && focused.not()) {
          focusTrigger = nextFocusTrigger ?: if (hovered) {
            TooltipFocusTrigger.Pointer
          } else {
            TooltipFocusTrigger.Keyboard
          }
          nextFocusTrigger = null
        } else if (it.hasFocus.not()) {
          focusTrigger = null
          nextFocusTrigger = null
        }
        focused = it.hasFocus
      }
      .pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            val event = awaitPointerEvent()
            when (event.type) {
              PointerEventType.Press -> {
                nextFocusTrigger = TooltipFocusTrigger.Pointer
                if (focused) {
                  focusTrigger = TooltipFocusTrigger.Pointer
                }
              }

              PointerEventType.Enter -> {
                hovered = true
              }

              PointerEventType.Exit -> {
                hovered = false
                nextFocusTrigger = null
              }
            }
          }
        }
      },
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
    onPlaced = {
      state.placement = state.placement.copy(positionAdjustment = it.positionAdjustment)
    },
    floatingContent = {
      CompositionLocalProvider(LocalTooltipState provides state) {
        TooltipScopeInstance.panel()
      }
    },
    anchor = anchor,
  )
}

@Composable
fun TooltipScope.TooltipPanel(
  modifier: Modifier = Modifier,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable (TooltipPlacement) -> Unit,
) {
  val state = LocalTooltipState.current
  val showTooltip = state.show
  val placement = state.placement

  AnimatedVisibility(
    visible = showTooltip,
    enter = enter,
    exit = exit,
    modifier = modifier.tooltipPanelSemantics(showTooltip),
  ) {
    content(placement)
  }
}

private fun Modifier.tooltipPanelSemantics(showTooltip: Boolean): Modifier =
  this then Modifier.semantics {
    if (showTooltip) {
      liveRegion = LiveRegionMode.Assertive
    }
  }
