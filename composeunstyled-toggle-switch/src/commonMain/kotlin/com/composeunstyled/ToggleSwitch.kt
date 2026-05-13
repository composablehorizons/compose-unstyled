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
import androidx.compose.animation.core.snap
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt

@Stable
interface SwitchScope {
  val checked: Boolean
  val enabled: Boolean
  val interactionSource: MutableInteractionSource
}

private class SwitchScopeImpl(
  override val checked: Boolean,
  override val enabled: Boolean,
  override val interactionSource: MutableInteractionSource,
) : SwitchScope

@Composable
fun UnstyledSwitch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)?,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource? = null,
  indication: Indication? = LocalIndication.current,
  content: @Composable SwitchScope.() -> Unit,
) {
  val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

  Layout(
    modifier = modifier
      .then(
        buildModifier {
          if (onCheckedChange != null) {
            add(
              Modifier.toggleable(
                value = checked,
                enabled = enabled,
                interactionSource = resolvedInteractionSource,
                indication = indication,
                role = Role.Switch,
                onValueChange = onCheckedChange,
              ),
            )
          }
        },
      ),
    content = {
      val scope = SwitchScopeImpl(
        checked = checked,
        enabled = enabled,
        interactionSource = resolvedInteractionSource,
      )
      scope.content()
    },
  ) {
      measurables,
      constraints,
    ->
    val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
    val placeables = measurables.map { it.measure(looseConstraints) }
    val contentWidth = placeables.maxOfOrNull { it.width } ?: 0
    val contentHeight = placeables.maxOfOrNull { it.height } ?: 0
    val width = contentWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
    val height = contentHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

    layout(width, height) {
      placeables.forEach { placeable ->
        val thumbData = placeable.parentData as? SwitchThumbParentData
        val x = thumbData?.let {
          ((width - placeable.width) * it.offsetFraction).roundToInt()
        } ?: 0
        placeable.placeRelative(x = max(0, x), y = 0)
      }
    }
  }
}

@Composable
fun SwitchScope.SwitchThumb(
  modifier: Modifier = Modifier,
  animationSpec: FiniteAnimationSpec<Dp> = snap(),
  content: @Composable () -> Unit = {},
) {
  val offsetFraction by animateDpAsState(
    targetValue = if (checked) 1.dp else 0.dp,
    animationSpec = animationSpec,
  )

  Box(
    modifier = modifier
      .then(SwitchThumbParentDataModifier(offsetFraction.value)),
  ) {
    content()
  }
}

private data class SwitchThumbParentData(
  val offsetFraction: Float,
)

private data class SwitchThumbParentDataModifier(
  val offsetFraction: Float,
) : ParentDataModifier {
  override fun Density.modifyParentData(
    parentData: Any?,
  ): Any = SwitchThumbParentData(offsetFraction)
}
