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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
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
  var arrowDirection by mutableStateOf(TooltipArrowDirection.Down)
  var arrowOffset by mutableStateOf(IntOffset.Zero)
  var side by mutableStateOf(AnchorSide.Top)
  var alignment by mutableStateOf(AnchorAlignment.Center)
}

enum class TooltipArrowDirection {
  Up, Down, Left, Right
}

interface TooltipScope

private object TooltipScopeInstance : TooltipScope

internal val LocalTooltipState = staticCompositionLocalOf<TooltipState> {
  TooltipState()
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
  var focusKeepsTooltipVisible by remember { mutableStateOf(false) }
  var entered by remember { mutableStateOf(false) }
  var pointerFocusPending by remember { mutableStateOf(false) }
  var focusedByPointer by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  var timerJob: Job? by remember { mutableStateOf(null) }
  var hoverDelayJob: Job? by remember { mutableStateOf(null) }

  fun showTooltip(duration: Long) {
    timerJob = scope.launch {
      state.show = true
      delay(duration)
      state.show = false
    }
  }

  fun dismissTooltip() {
    entered = false
    focused = false
    focusKeepsTooltipVisible = false
    focusedByPointer = false
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
    state.arrowDirection = arrowDirection(side)
    state.side = side
    state.alignment = alignment
  }

  // focus handling - show instantly when focused
  LaunchedEffect(focused, focusKeepsTooltipVisible, enabled) {
    if (enabled && focused && focusKeepsTooltipVisible) {
      state.show = true

      hoverDelayJob?.cancel()
      timerJob?.cancel()

      hoverDelayJob = null
      timerJob = null
    } else if ((!focused || !focusKeepsTooltipVisible) && !entered) {
      state.show = false
    }
  }

  // hover handling - show after hoverDelayMillis
  LaunchedEffect(entered, enabled, hoverDelayMillis) {
    if (enabled && entered) {
      hoverDelayJob?.cancel()

      hoverDelayJob = scope.launch {
        delay(hoverDelayMillis)
        state.show = true
      }
    } else {
      // Mouse left - cancel hover delay and hide tooltip
      hoverDelayJob?.cancel()
      hoverDelayJob = null
      if (!focused || !focusKeepsTooltipVisible || focusedByPointer) {
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
        if (it.hasFocus && !focused) {
          focusKeepsTooltipVisible = !pointerFocusPending && !entered
          focusedByPointer = pointerFocusPending
          pointerFocusPending = false
        } else if (!it.hasFocus) {
          focusKeepsTooltipVisible = false
          focusedByPointer = false
        }
        focused = it.hasFocus
      }
      .pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            val event = awaitPointerEvent()
            when (event.type) {
              PointerEventType.Press -> {
                pointerFocusPending = true
                focusedByPointer = true
                focusKeepsTooltipVisible = false
              }

              PointerEventType.Enter -> {
                pointerFocusPending = true
                entered = true
              }

              PointerEventType.Exit -> {
                entered = false
                pointerFocusPending = false
                if (focusedByPointer) {
                  state.show = false
                }
              }
            }
          }
        }
      },
    side = side,
    alignment = alignment,
    sideOffset = sideOffset,
    alignmentOffset = alignmentOffset,
    onOffsetFromIdealPositionChanged = {
      state.arrowOffset = it
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
  content: @Composable () -> Unit,
) {
  val state = LocalTooltipState.current
  val showTooltip = state.show

  AnimatedVisibility(
    visible = showTooltip,
    enter = enter,
    exit = exit,
    modifier = modifier.tooltipPanelSemantics(showTooltip),
  ) {
    content()
  }
}

@Composable
fun TooltipScope.TooltipPanel(
  modifier: Modifier = Modifier,
  arrow: @Composable (TooltipArrowDirection) -> Unit,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable () -> Unit,
) {
  val state = LocalTooltipState.current
  val showTooltip = state.show
  val arrowDirection = state.arrowDirection
  val arrowOffset = state.arrowOffset

  AnimatedVisibility(
    visible = showTooltip,
    enter = enter,
    exit = exit,
    modifier = modifier.tooltipPanelSemantics(showTooltip),
  ) {
    when (arrowDirection) {
      TooltipArrowDirection.Up -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.offset { IntOffset(-arrowOffset.x, 0) }) {
          arrow(arrowDirection)
        }
        content()
      }

      TooltipArrowDirection.Down -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content()
        Box(modifier = Modifier.offset { IntOffset(-arrowOffset.x, 0) }) {
          arrow(arrowDirection)
        }
      }

      TooltipArrowDirection.Left -> Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.offset { IntOffset(0, -arrowOffset.y) }) {
          arrow(arrowDirection)
        }
        content()
      }

      TooltipArrowDirection.Right -> Row(verticalAlignment = Alignment.CenterVertically) {
        content()
        Box(modifier = Modifier.offset { IntOffset(0, -arrowOffset.y) }) {
          arrow(arrowDirection)
        }
      }
    }
  }
}

private fun arrowDirection(side: AnchorSide): TooltipArrowDirection {
  return when (side) {
    AnchorSide.Top -> TooltipArrowDirection.Down
    AnchorSide.Bottom -> TooltipArrowDirection.Up
    AnchorSide.Start -> TooltipArrowDirection.Right
    AnchorSide.End -> TooltipArrowDirection.Left
  }
}

private fun Modifier.tooltipPanelSemantics(showTooltip: Boolean): Modifier =
  this then Modifier.semantics {
    if (showTooltip) {
      liveRegion = LiveRegionMode.Assertive
    }
  }
