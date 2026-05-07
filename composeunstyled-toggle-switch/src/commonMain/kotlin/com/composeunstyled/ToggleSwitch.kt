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

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Stable
interface UnstyledSwitchScope : BoxScope {
  val checked: Boolean
  val enabled: Boolean
  val interactionSource: MutableInteractionSource
}

private class UnstyledSwitchScopeImpl(
  override val checked: Boolean,
  override val enabled: Boolean,
  override val interactionSource: MutableInteractionSource,
  private val boxScope: BoxScope,
) : UnstyledSwitchScope,
  BoxScope by boxScope

@Composable
fun UnstyledSwitch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  content: @Composable UnstyledSwitchScope.() -> Unit,
) {
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

  Box(
    modifier = modifier
      .then(
        if (onCheckedChange != null) {
          Modifier.toggleable(
            value = checked,
            enabled = enabled,
            interactionSource = resolvedInteractionSource,
            indication = indication,
            role = Role.Switch,
            onValueChange = onCheckedChange,
          )
        } else {
          Modifier
        },
      ),
  ) {
    val scope = UnstyledSwitchScopeImpl(
      checked = checked,
      enabled = enabled,
      interactionSource = resolvedInteractionSource,
      boxScope = this,
    )
    scope.content()
  }
}

@Composable
fun UnstyledSwitchScope.UnstyledSwitchThumb(
  modifier: Modifier = Modifier,
  animationSpec: FiniteAnimationSpec<Dp> = tween(),
  contentAlignment: Alignment = Alignment.Center,
  content: @Composable BoxScope.() -> Unit = {},
) {
  var trackWidth by remember { mutableStateOf(0.dp) }
  var thumbWidth by remember { mutableStateOf(0.dp) }
  val hasMeasured = trackWidth > 0.dp && thumbWidth > 0.dp
  val targetOffset = if (checked) trackWidth - thumbWidth else 0.dp
  val offset by if (hasMeasured) {
    animateDpAsState(targetValue = targetOffset, animationSpec = animationSpec)
  } else {
    remember { mutableStateOf(0.dp) }
  }
  val density = LocalDensity.current

  Box(
    Modifier
      .fillMaxSize()
      .onSizeChanged { trackWidth = with(density) { it.width.toDp() } },
  ) {
    Box(
      modifier = Modifier
        .offset { IntOffset(offset.roundToPx(), 0) }
        .onSizeChanged { thumbWidth = with(density) { it.width.toDp() } }
        .alpha(if (hasMeasured) 1f else 0f)
        .then(modifier),
      contentAlignment = contentAlignment,
      content = content,
    )
  }
}
