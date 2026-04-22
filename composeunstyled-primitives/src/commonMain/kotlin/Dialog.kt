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
package com.composables.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.composeunstyled.AppearInstantly
import com.composeunstyled.DisappearInstantly
import com.composeunstyled.LocalContentColor
import com.composeunstyled.NoPadding
import com.composeunstyled.UnstyledDialog
import com.composeunstyled.UnstyledDialogPanel
import com.composeunstyled.UnstyledScrim

@Deprecated("This will go away in 2.0. Use DialogProperties from the com.composeunstyled package")
data class DialogProperties(
  val dismissOnBackPress: Boolean = true,
  val dismissOnClickOutside: Boolean = true,
)

@Stable
class DialogState(initiallyVisible: Boolean = false) {

  @Deprecated(
    "This will go away in 2.0. Use rememberDialogState(visible)",
    ReplaceWith("rememberDialogState(visible)"),
  )
  constructor(
    visible: Boolean = false,
    ____deprecated_constructor: Unit,
  ) : this(initiallyVisible = visible)

  internal val state = com.composeunstyled.DialogState(initiallyVisible)

  var visible: Boolean
    set(value) {
      state.visible = value
    }
    get() = state.visible
}

@Stable
class DialogScope internal constructor()

private val DialogStateSaver = run {
  mapSaver(
    save = {
      mapOf("visible" to it.visible)
    },
    restore = {
      DialogState(initiallyVisible = it["visible"] as Boolean)
    },
  )
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun rememberDialogState(initiallyVisible: Boolean = false): DialogState {
  return rememberSaveable(saver = DialogStateSaver) { DialogState(initiallyVisible) }
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun Dialog(
  state: DialogState,
  properties: DialogProperties = DialogProperties(),
  onDismiss: () -> Unit = DoNothing,
  content: @Composable (DialogScope.() -> Unit),
) {
  val scope = remember { DialogScope() }
  UnstyledDialog(
    state = state.state,
    properties = com.composeunstyled.DialogProperties(
      dismissOnBackPress = properties.dismissOnBackPress,
      dismissOnClickOutside = properties.dismissOnClickOutside,
    ),
    onDismiss = onDismiss,
  ) {
    scope.content()
  }
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun DialogScope.DialogPanel(
  modifier: Modifier = Modifier,
  enter: EnterTransition = AppearInstantly,
  exit: ExitTransition = DisappearInstantly,
  shape: Shape = RectangleShape,
  backgroundColor: Color = Color.Unspecified,
  contentColor: Color = LocalContentColor.current,
  contentPadding: PaddingValues = NoPadding,
  content: @Composable () -> Unit,
) {
  UnstyledDialogPanel(
    modifier = modifier,
    enter = enter,
    exit = exit,
    shape = shape,
    backgroundColor = backgroundColor,
    contentColor = contentColor,
    contentPadding = contentPadding,
    content = content,
  )
}

@Deprecated("This will go away in 2.0. Use Dialog from the com.composeunstyled package")
@Composable
fun DialogScope.Scrim(
  modifier: Modifier = Modifier,
  scrimColor: Color = Color.Black.copy(0.6f),
  enter: EnterTransition = AppearInstantly,
  exit: ExitTransition = DisappearInstantly,
) {
  UnstyledScrim(
    modifier = modifier,
    scrimColor = scrimColor,
    enter = enter,
    exit = exit,
  )
}
