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
package com.composeunstyled.visualregressions

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.composeunstyled.Content
import com.composeunstyled.DrawerSnapPoint
import com.composeunstyled.Panel
import com.composeunstyled.UnstyledDrawer
import com.composeunstyled.Viewport
import com.composeunstyled.demo.ElasticOverscrollEffect
import com.composeunstyled.demo.rememberElasticOverscrollEffect
import com.composeunstyled.rememberDrawerState
import java.io.File
import javax.imageio.ImageIO

internal const val DrawerMovingOverscrollScreenshotName =
  "drawer-overscroll-moves-panel-outside-bounds"
internal const val DrawerDrawingOverscrollScreenshotName =
  "drawer-overscroll-draws-outside-panel-bounds"
internal const val DrawerPanelTag = "drawer-panel"
internal val DrawerOverscrollBackgroundColor = Color(0xFFE5E7EB)
internal val DrawerOverscrollDrawingColor = Color(0xFFFF2D55)

@Composable
internal fun DrawerOverscrollRegression(
  overscrollEffect: OverscrollEffect = rememberElasticOverscrollEffect(),
) {
  val drawerState = rememberDrawerState(initialSnapPoint = DrawerSnapPoint.Open)

  UnstyledDrawer(
    state = drawerState,
    modifier = Modifier.fillMaxSize(),
  ) {
    Viewport(
      modifier = Modifier.fillMaxSize(),
    ) {
      Panel(
        modifier = Modifier
          .dropShadow(
            shape = RectangleShape,
            shadow = Shadow(
              radius = 0.dp,
              color = Color.Black,
              spread = 0.dp,
              offset = DpOffset(8.dp, 8.dp),
              alpha = 0.33f,
            ),
          )
          .background(Color.White)
          .border(1.dp, Color.Black)
          .fillMaxWidth()
          .testTag(DrawerPanelTag),
        overscrollEffect = overscrollEffect,
      ) {
        Content(
          modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        ) {
          Box(modifier = Modifier.fillMaxSize())
        }
      }
    }
  }
}

@OptIn(ExperimentalTestApi::class)
internal fun updateDrawerOverscrollRegressionScreenshot() = runComposeUiTest {
  val overscrollEffect = ElasticOverscrollEffect()
  val actual = captureVisualRegressionImage(
    width = 360,
    height = 640,
    backgroundColor = DrawerOverscrollBackgroundColor,
    content = { DrawerOverscrollRegression(overscrollEffect = overscrollEffect) },
    settleAfterInteract = false,
    interact = {
      onNodeWithTag(DrawerPanelTag).performTouchInput {
        down(center)
        moveTo(topCenter)
      }
    },
  )
  val expectedFile =
    File("src/jvmTest/resources/screenshots/$DrawerMovingOverscrollScreenshotName.png")
  expectedFile.parentFile.mkdirs()
  ImageIO.write(actual, "png", expectedFile)
}

@OptIn(ExperimentalTestApi::class)
internal fun updateDrawerDrawingOverscrollRegressionScreenshot() = runComposeUiTest {
  val overscrollEffect = DrawingOverscrollEffect()
  val actual = captureVisualRegressionImage(
    width = 360,
    height = 640,
    backgroundColor = DrawerOverscrollBackgroundColor,
    content = { DrawerOverscrollRegression(overscrollEffect = overscrollEffect) },
    interact = {
      onNodeWithTag(DrawerPanelTag).performTouchInput {
        down(center)
        moveTo(topCenter)
      }
    },
  )
  val expectedFile =
    File("src/jvmTest/resources/screenshots/$DrawerDrawingOverscrollScreenshotName.png")
  expectedFile.parentFile.mkdirs()
  ImageIO.write(actual, "png", expectedFile)
}

internal class DrawingOverscrollEffect(
  initialOverscrollPx: Float = 0f,
) : OverscrollEffect {
  var overscrollPx: Float by mutableFloatStateOf(initialOverscrollPx)
    private set

  override fun applyToScroll(
    delta: Offset,
    source: androidx.compose.ui.input.nestedscroll.NestedScrollSource,
    performScroll: (Offset) -> Offset,
  ): Offset {
    val consumed = performScroll(delta)
    val overscroll = delta - consumed
    overscrollPx = (overscrollPx + overscroll.y * OffsetMultiplier)
      .coerceIn(-MaximumOffsetPx, MaximumOffsetPx)
    return consumed
  }

  override suspend fun applyToFling(
    velocity: Velocity,
    performFling: suspend (Velocity) -> Velocity,
  ) {
    performFling(velocity)
  }

  override val isInProgress: Boolean
    get() = overscrollPx != 0f

  override val node: DelegatableNode = object : Modifier.Node(), DrawModifierNode {
    override fun ContentDrawScope.draw() {
      drawContent()
      val overscrollHeight = -overscrollPx
      if (overscrollHeight <= 0f) return

      drawRect(
        color = DrawerOverscrollDrawingColor,
        topLeft = Offset(x = 0f, y = -overscrollHeight),
        size = androidx.compose.ui.geometry.Size(
          width = size.width,
          height = overscrollHeight,
        ),
      )
    }
  }
}

private const val OffsetMultiplier = 0.55f
private const val MaximumOffsetPx = 96f
