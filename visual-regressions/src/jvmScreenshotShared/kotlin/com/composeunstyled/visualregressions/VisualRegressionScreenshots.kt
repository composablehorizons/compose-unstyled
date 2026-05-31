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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isLessThanOrEqualTo
import assertk.fail
import com.composeunstyled.demo.Demo
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val CheckboxCustomCheckedIndicatorScreenshot = VisualRegressionScreenshot(
  name = "checkbox-custom-checked-indicator",
  content = { CheckboxCustomCheckedIndicatorRegression() },
)

val CheckboxExtendedIndicatorBoundsScreenshot = VisualRegressionScreenshot(
  name = "checkbox-extended-indicator-bounds",
  content = { CheckboxExtendedIndicatorBoundsRegression() },
)

val DedicatedRegressionScreenshots = ModalBottomSheetRegressionScreenshots + listOf(
  CheckboxCustomCheckedIndicatorScreenshot,
  CheckboxExtendedIndicatorBoundsScreenshot,
) + BottomSheetRegressionScreenshots

val DemoRegressionScreenshots = listOf(
  demoScreenshot(name = "bottom-sheet-demo", startDestination = "bottom-sheet"),
  demoScreenshot(name = "modal-bottom-sheet-demo", startDestination = "modal-bottom-sheet"),
  demoScreenshot(name = "button-demo", startDestination = "button"),
  demoScreenshot(name = "checkbox-demo", startDestination = "checkbox"),
  demoScreenshot(name = "tristatecheckbox-demo", startDestination = "tristatecheckbox"),
  demoScreenshot(name = "dialog-demo", startDestination = "dialog"),
  demoScreenshot(name = "disclosure-demo", startDestination = "disclosure"),
  demoScreenshot(name = "dropdown-menu-demo", startDestination = "dropdown-menu"),
  demoScreenshot(name = "icon-demo", startDestination = "icon"),
  demoScreenshot(name = "progressindicator-demo", startDestination = "progressindicator"),
  demoScreenshot(name = "radiogroup-demo", startDestination = "radiogroup"),
  demoScreenshot(name = "scrollbars-demo", startDestination = "scrollbars"),
  demoScreenshot(name = "separators-demo", startDestination = "separators"),
  demoScreenshot(name = "slider-demo", startDestination = "slider"),
  demoScreenshot(name = "tabgroup-demo", startDestination = "tabgroup"),
  demoScreenshot(name = "textfield-demo", startDestination = "textfield"),
  demoScreenshot(name = "toggleswitch-demo", startDestination = "toggleswitch"),
)

val VisualRegressionScreenshots = DedicatedRegressionScreenshots + DemoRegressionScreenshots

private fun demoScreenshot(
  name: String,
  startDestination: String,
) = VisualRegressionScreenshot(
  name = name,
  content = { Demo(startDestination = startDestination) },
)

@OptIn(ExperimentalTestApi::class)
fun assertVisualRegressionScreenshotMatches(
  screenshot: VisualRegressionScreenshot,
) = runComposeUiTest {
  val actual = captureVisualRegressionScreenshot(screenshot)

  val reportDir = File("build/reports/screenshot-tests")
  reportDir.mkdirs()
  ImageIO.write(actual, "png", File(reportDir, "${screenshot.name}.actual.png"))

  val expected = javaClass.classLoader
    .getResourceAsStream("screenshots/${screenshot.name}.png")
    ?.use(ImageIO::read)
    ?: fail("Missing expected screenshot. Run `./gradlew :visual-regressions:takeScreenshots`.")

  assertThat(actual.width).isEqualTo(expected.width)
  assertThat(actual.height).isEqualTo(expected.height)

  val diff = diff(expected, actual)
  if (diff.changedPixels > AllowedChangedPixels) {
    ImageIO.write(diff.image, "png", File(reportDir, "${screenshot.name}.diff.png"))
  }
  assertThat(diff.changedPixels).isLessThanOrEqualTo(AllowedChangedPixels)
}

@OptIn(ExperimentalTestApi::class)
fun updateVisualRegressionScreenshot(screenshot: VisualRegressionScreenshot) = runComposeUiTest {
  val actual = captureVisualRegressionScreenshot(screenshot)
  val expectedFile = File("src/jvmTest/resources/screenshots/${screenshot.name}.png")
  expectedFile.parentFile.mkdirs()
  ImageIO.write(actual, "png", expectedFile)
}

@OptIn(ExperimentalTestApi::class)
private fun ComposeUiTest.captureVisualRegressionScreenshot(
  screenshot: VisualRegressionScreenshot,
): BufferedImage {
  setContent {
    Box(
      modifier = Modifier
        .requiredSize(width = screenshot.width.dp, height = screenshot.height.dp)
        .background(Color.White)
        .testTag(ScreenshotTargetTag),
      contentAlignment = Alignment.Center,
    ) {
      screenshot.content()
    }
  }

  waitForIdle()
  onNodeWithTag(ScreenshotTargetTag).performMouseInput {
    moveTo(Offset.Zero)
  }
  waitForIdle()

  return onNodeWithTag(ScreenshotTargetTag).captureToImage().toAwtImage()
}

private fun diff(expected: BufferedImage, actual: BufferedImage): ScreenshotDiff {
  val diff = BufferedImage(expected.width, expected.height, BufferedImage.TYPE_INT_ARGB)
  var changedPixels = 0

  for (y in 0 until expected.height) {
    for (x in 0 until expected.width) {
      val expectedRgb = expected.getRGB(x, y)
      val actualRgb = actual.getRGB(x, y)
      if (expectedRgb == actualRgb) {
        diff.setRGB(x, y, expectedRgb)
      } else {
        changedPixels++
        diff.setRGB(x, y, DiffColor)
      }
    }
  }

  return ScreenshotDiff(diff, changedPixels)
}

private data class ScreenshotDiff(
  val image: BufferedImage,
  val changedPixels: Int,
)

private const val ScreenshotTargetTag = "screenshot-target"
private const val DiffColor = 0xFFFF00FF.toInt()

// Allows tiny renderer drift between local and CI machines without hiding real layout changes.
private const val AllowedChangedPixels = 20
