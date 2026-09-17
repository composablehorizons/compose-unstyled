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
package com.composeunstyled.demo.drawer

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.composeunstyled.DragHandle
import com.composeunstyled.DrawerPlacement
import com.composeunstyled.DrawerPresentation
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.DrawerSnapPoints
import com.composeunstyled.Panel
import com.composeunstyled.Text
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.UnstyledDrawerState
import com.composeunstyled.Viewport
import com.composeunstyled.demo.UnstyledDemo
import com.composeunstyled.demo.borderToken
import com.composeunstyled.demo.colors
import com.composeunstyled.demo.contentToken
import com.composeunstyled.demo.surfaceToken
import com.composeunstyled.theme.Theme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class DrawerOverscrollDemoValue {
  Closed,
  Open,
}

@Preview
@UnstyledDemo("drawer-overscroll")
@Composable
fun DrawerOverscrollDemo() {
  val drawerState = remember {
    UnstyledDrawerState(
      initialValue = DrawerOverscrollDemoValue.Open,
      snapPoints = DrawerSnapPoints {
        DrawerOverscrollDemoValue.Closed at DrawerSnapPoint.Zero
        DrawerOverscrollDemoValue.Open at DrawerSnapPoint.ContentSize
      },
    )
  }
  val overscrollEffect = remember { ElasticOverscrollEffect() }

  Box(Modifier.fillMaxSize()) {
    UnstyledDrawer(
      state = drawerState,
      modifier = Modifier.fillMaxSize(),
      placement = DrawerPlacement.Bottom,
      presentation = DrawerPresentation.InPlace,
    ) {
      Viewport(Modifier.fillMaxSize()) {
        Panel(
          modifier = Modifier
            .fillMaxWidth()
            .background(Theme[colors][surfaceToken])
            .border(1.dp, Theme[colors][borderToken])
            .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp),
          overscrollEffect = overscrollEffect,
        ) {
          Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            DragHandle {
              Box(
                Modifier
                  .width(32.dp)
                  .height(4.dp)
                  .background(Theme[colors][contentToken]),
              )
            }
            Text("Here is the content of the drawer.")
            Text("Pull upward past the open limit")
          }
        }
      }
    }
  }
}

private class ElasticOverscrollEffect : OverscrollEffect {
  var offsetPx: Float by mutableFloatStateOf(0f)
    private set

  override fun applyToScroll(
    delta: Offset,
    source: androidx.compose.ui.input.nestedscroll.NestedScrollSource,
    performScroll: (Offset) -> Offset,
  ): Offset {
    val consumed = performScroll(delta)
    val overscroll = delta - consumed
    offsetPx += overscroll.y * ElasticOverscrollOffsetMultiplier
    return consumed
  }

  override suspend fun applyToFling(
    velocity: Velocity,
    performFling: suspend (Velocity) -> Velocity,
  ) {
    val releaseOffset = offsetPx
    if (releaseOffset == 0f) {
      performFling(velocity)
      return
    }

    coroutineScope {
      val rebound = launch {
        animate(
          initialValue = releaseOffset,
          targetValue = 0f,
          initialVelocity = velocity.y * ElasticOverscrollOffsetMultiplier,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
          ),
        ) { value, _ ->
          offsetPx = value
        }
      }

      try {
        performFling(velocity)
      } finally {
        rebound.join()
      }
    }
  }

  override val isInProgress: Boolean
    get() = offsetPx != 0f

  override val node: DelegatableNode = object : Modifier.Node(), LayoutModifierNode {
    override fun MeasureScope.measure(
      measurable: Measurable,
      constraints: Constraints,
    ): MeasureResult {
      val placeable = measurable.measure(constraints)
      return layout(placeable.width, placeable.height) {
        val offset = IntOffset(x = 0, y = offsetPx.roundToInt())
        placeable.placeRelativeWithLayer(offset.x, offset.y)
      }
    }
  }
}

private const val ElasticOverscrollOffsetMultiplier = 0.55f
