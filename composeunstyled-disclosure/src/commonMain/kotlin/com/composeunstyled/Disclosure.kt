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
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val NoPadding = PaddingValues(0.dp)

@Stable
class DisclosureScope internal constructor(
  val expanded: Boolean,
  internal val onExpandedChange: (Boolean) -> Unit,
)

@Composable
private fun rememberDisclosureScope(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
): DisclosureScope {
  return remember(expanded, onExpandedChange) {
    DisclosureScope(expanded, onExpandedChange)
  }
}

@Composable
fun UnstyledDisclosure(
  expanded: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable DisclosureScope.() -> Unit,
) {
  val scope = rememberDisclosureScope(expanded, onExpandedChange)

  Column(modifier) {
    scope.content()
  }
}

@Composable
fun DisclosureScope.UnstyledDisclosureHeading(
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  contentPadding: PaddingValues = NoPadding,
  indication: Indication = LocalIndication.current,
  interactionSource: MutableInteractionSource? = null,
  verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  content: @Composable () -> Unit,
) {
  UnstyledButton(
    modifier = modifier.semantics {
      heading()
      if (expanded) {
        collapse {
          onExpandedChange(false)
          true
        }
      } else {
        expand {
          onExpandedChange(true)
          true
        }
      }
    },
    onClick = { onExpandedChange(expanded.not()) },
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    contentPadding = contentPadding,
    verticalAlignment = verticalAlignment,
    horizontalArrangement = horizontalArrangement,
  ) {
    content()
  }
}

@Composable
fun DisclosureScope.UnstyledDisclosureHeading(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  contentPadding: PaddingValues = NoPadding,
  indication: Indication = LocalIndication.current,
  interactionSource: MutableInteractionSource? = null,
  verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
  content: @Composable () -> Unit,
) {
  UnstyledButton(
    modifier = modifier,
    onClick = onClick,
    interactionSource = interactionSource,
    indication = indication,
    enabled = enabled,
    contentPadding = contentPadding,
    verticalAlignment = verticalAlignment,
    horizontalArrangement = horizontalArrangement,
  ) {
    content()
  }
}

@Composable
fun DisclosureScope.UnstyledDisclosurePanel(
  modifier: Modifier = Modifier,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
  content: @Composable () -> Unit,
) {
  AnimatedVisibility(
    modifier = modifier,
    visible = expanded,
    enter = enter,
    exit = exit,
  ) {
    content()
  }
}
