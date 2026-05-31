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

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@kotlin.jvm.JvmInline
value class FocusRingVisibility private constructor(private val value: Int) {
  companion object {
    @Stable
    val Focused = FocusRingVisibility(0)

    @Stable
    val FocusVisible = FocusRingVisibility(1)
  }

  internal val isFocusVisibleOnly: Boolean
    get() = this == FocusVisible

  override fun toString(): String {
    return when (this) {
      Focused -> "Focused"
      FocusVisible -> "FocusVisible"
      else -> "Invalid"
    }
  }
}

@kotlin.jvm.JvmInline
internal value class FocusVisibilityMode private constructor(private val value: Int) {
  companion object {
    val Keyboard = FocusVisibilityMode(0)

    val Pointer = FocusVisibilityMode(1)
  }
}

@Stable
internal interface FocusVisibilityManager {
  val focusVisibilityMode: FocusVisibilityMode

  fun notifyKeyboardInput()

  fun notifyPointerInput()
}

internal val LocalFocusVisibilityManager = staticCompositionLocalOf<FocusVisibilityManager> {
  DefaultFocusVisibilityManager(FocusVisibilityMode.Keyboard)
}

@Composable
fun FocusVisibilityProvider(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val manager = remember {
    DefaultFocusVisibilityManager(FocusVisibilityMode.Keyboard)
  }

  CompositionLocalProvider(LocalFocusVisibilityManager provides manager) {
    Box(
      modifier = modifier
        .focusVisibilityInputObserver(manager),
    ) {
      content()
    }
  }
}

@Composable
fun Modifier.focusRing(
  interactionSource: InteractionSource,
  width: Dp,
  color: Color,
  shape: Shape = RectangleShape,
  offset: Dp = 0.dp,
  visibility: FocusRingVisibility = FocusRingVisibility.FocusVisible,
): Modifier {
  val showFocusRing by if (visibility.isFocusVisibleOnly) {
    interactionSource.collectIsFocusVisibleAsState()
  } else {
    interactionSource.collectIsFocusedAsState()
  }

  return this then buildModifier {
    if (showFocusRing) {
      add(Modifier.outline(width = width, color = color, shape = shape, offset = offset))
    }
  }
}

@Composable
fun InteractionSource.collectIsFocusVisibleAsState(): State<Boolean> {
  val focused by collectIsFocusedAsState()
  val manager = LocalFocusVisibilityManager.current

  return rememberUpdatedState(
    focused && manager.focusVisibilityMode == FocusVisibilityMode.Keyboard,
  )
}

private class DefaultFocusVisibilityManager(
  initialFocusVisibilityMode: FocusVisibilityMode,
) : FocusVisibilityManager {
  override var focusVisibilityMode by mutableStateOf(initialFocusVisibilityMode)
    private set

  override fun notifyKeyboardInput() {
    focusVisibilityMode = FocusVisibilityMode.Keyboard
  }

  override fun notifyPointerInput() {
    focusVisibilityMode = FocusVisibilityMode.Pointer
  }
}

private fun Modifier.focusVisibilityInputObserver(
  manager: FocusVisibilityManager,
): Modifier {
  return onPreviewKeyEvent { event ->
    if (event.type == KeyEventType.KeyDown && isNavigationKey(event.key)) {
      manager.notifyKeyboardInput()
    }
    false
  }.pointerInput(manager) {
    awaitPointerEventScope {
      while (true) {
        val event = awaitPointerEvent(PointerEventPass.Initial)
        if (event.type == PointerEventType.Press) {
          manager.notifyPointerInput()
        }
      }
    }
  }
}

private fun isNavigationKey(key: Key): Boolean {
  return when (key) {
    Key.Tab,
    Key.DirectionUp,
    Key.DirectionDown,
    Key.DirectionLeft,
    Key.DirectionRight,
    Key.MoveHome,
    Key.MoveEnd,
    Key.PageUp,
    Key.PageDown,
    -> true

    else -> false
  }
}
