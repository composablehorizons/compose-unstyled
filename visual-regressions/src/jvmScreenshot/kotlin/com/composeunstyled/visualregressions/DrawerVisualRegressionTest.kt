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

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.runComposeUiTest
import assertk.assertThat
import assertk.assertions.isGreaterThan
import assertk.assertions.isLessThan
import assertk.fail
import com.composeunstyled.demo.ElasticOverscrollEffect
import org.junit.Test
import java.io.File
import javax.imageio.ImageIO

@OptIn(ExperimentalTestApi::class)
class DrawerVisualRegressionTest {

  @Test
  fun overscrollVisualExtendsPastPanelBounds() = runComposeUiTest {
    val overscrollEffect = ElasticOverscrollEffect()

    val actual = captureMovedDrawerImage(overscrollEffect)

    assertThat(-overscrollEffect.offsetPx).isGreaterThan(48f)

    val visualBounds = nonBackgroundBounds(actual)
    val restingPanelTop = actual.height - PanelHeightPx

    assertThat(restingPanelTop - visualBounds.top).isGreaterThan(48)
    assertThat(actual.height - visualBounds.bottom).isGreaterThan(40)
  }

  @Test
  fun overscrollVisualMatchesScreenshot() = runComposeUiTest {
    val overscrollEffect = ElasticOverscrollEffect()

    val actual = captureMovedDrawerImage(overscrollEffect)

    val reportDir = File("build/reports/screenshot-tests")
    reportDir.mkdirs()
    ImageIO.write(
      actual,
      "png",
      File(reportDir, "$DrawerMovingOverscrollScreenshotName.actual.png"),
    )

    val expected = javaClass.classLoader
      .getResourceAsStream("screenshots/$DrawerMovingOverscrollScreenshotName.png")
      ?.use(ImageIO::read)
      ?: fail("Missing expected screenshot. Run `./gradlew :visual-regressions:takeScreenshots`.")

    assertThat(actual.width).isGreaterThan(0)
    assertThat(actual.height).isGreaterThan(0)

    val diff = countChangedPixels(expected, actual)
    assertThat(diff).isLessThan(21)
  }

  @Test
  fun drawingOverscrollVisualExtendsPastPanelBounds() = runComposeUiTest {
    val overscrollEffect = DrawingOverscrollEffect()

    val actual = captureDrawnDrawerImage(overscrollEffect)

    assertThat(-overscrollEffect.overscrollPx).isGreaterThan(48f)

    val panelBounds = nonBackgroundBounds(actual)
    val restingPanelTop = actual.height - PanelHeightPx
    val drawingTop = topMostColor(actual, DrawerOverscrollDrawingColor.toArgb())
      ?: fail("Expected overscroll drawing to be visible.")

    assertThat(restingPanelTop - drawingTop).isGreaterThan(48)
    assertThat(panelBounds.bottom).isGreaterThan(actual.height - 4)
  }

  @Test
  fun drawingOverscrollVisualMatchesScreenshot() = runComposeUiTest {
    val overscrollEffect = DrawingOverscrollEffect()

    val actual = captureDrawnDrawerImage(overscrollEffect)

    val reportDir = File("build/reports/screenshot-tests")
    reportDir.mkdirs()
    ImageIO.write(
      actual,
      "png",
      File(reportDir, "$DrawerDrawingOverscrollScreenshotName.actual.png"),
    )

    val expected = javaClass.classLoader
      .getResourceAsStream("screenshots/$DrawerDrawingOverscrollScreenshotName.png")
      ?.use(ImageIO::read)
      ?: fail("Missing expected screenshot. Run `./gradlew :visual-regressions:takeScreenshots`.")

    assertThat(actual.width).isGreaterThan(0)
    assertThat(actual.height).isGreaterThan(0)

    val diff = countChangedPixels(expected, actual)
    assertThat(diff).isLessThan(21)
  }

  private fun ComposeUiTest.captureMovedDrawerImage(
    overscrollEffect: ElasticOverscrollEffect,
  ) = captureVisualRegressionImage(
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

  private fun ComposeUiTest.captureDrawnDrawerImage(
    overscrollEffect: DrawingOverscrollEffect,
  ) = captureVisualRegressionImage(
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

  private fun nonBackgroundBounds(image: java.awt.image.BufferedImage): PixelBounds {
    var top = image.height
    var bottom = 0

    for (y in 0 until image.height) {
      for (x in 0 until image.width) {
        if (image.getRGB(x, y) != BackgroundColor) {
          top = minOf(top, y)
          bottom = maxOf(bottom, y)
        }
      }
    }

    return PixelBounds(top = top, bottom = bottom)
  }

  private fun countChangedPixels(
    expected: java.awt.image.BufferedImage,
    actual: java.awt.image.BufferedImage,
  ): Int {
    if (expected.width != actual.width || expected.height != actual.height) {
      return Int.MAX_VALUE
    }

    var changedPixels = 0
    for (y in 0 until expected.height) {
      for (x in 0 until expected.width) {
        if (expected.getRGB(x, y) != actual.getRGB(x, y)) {
          changedPixels++
        }
      }
    }
    return changedPixels
  }

  private fun topMostColor(
    image: java.awt.image.BufferedImage,
    color: Int,
  ): Int? {
    for (y in 0 until image.height) {
      for (x in 0 until image.width) {
        if (image.getRGB(x, y) == color) {
          return y
        }
      }
    }
    return null
  }
}

private data class PixelBounds(
  val top: Int,
  val bottom: Int,
)

private const val PanelHeightPx = 220
private const val BackgroundColor = 0xFFE5E7EB.toInt()
