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
package com.composeunstyled.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

data class DemoScreenshot(
  val name: String,
  val startDestination: String,
)

val BottomSheetDemoScreenshot = DemoScreenshot(
  name = "bottom-sheet-demo",
  startDestination = "bottom-sheet",
)

val ModalBottomSheetDemoScreenshot = DemoScreenshot(
  name = "modal-bottom-sheet-demo",
  startDestination = "modal-bottom-sheet",
)

val IconDemoScreenshot = DemoScreenshot(
  name = "icon-demo",
  startDestination = "icon",
)

val ModalBottomSheetScrollableContentDemoScreenshot = DemoScreenshot(
  name = "modal-bottom-sheet-scrollable-content-demo",
  startDestination = "modal-bottom-sheet-scrollable-content",
)

val DemoScreenshots = listOf(
  BottomSheetDemoScreenshot,
  ModalBottomSheetDemoScreenshot,
  IconDemoScreenshot,
  ModalBottomSheetScrollableContentDemoScreenshot,
)

@OptIn(ExperimentalTestApi::class)
fun assertDemoScreenshotMatches(screenshot: DemoScreenshot) = runComposeUiTest {
  val actual = captureDemoScreenshot(screenshot)

  val reportDir = File("build/reports/screenshot-tests")
  reportDir.mkdirs()
  ImageIO.write(actual, "png", File(reportDir, "${screenshot.name}.actual.png"))

  val expected = screenshotResourcePaths(screenshot)
    .firstNotNullOfOrNull { path ->
      javaClass.classLoader.getResourceAsStream(path)?.use(ImageIO::read)
    }
    ?: fail("Missing expected screenshot. Run `./gradlew :demo:takeScreenshots`.")

  assertEquals(expected.width, actual.width, "Screenshot width changed.")
  assertEquals(expected.height, actual.height, "Screenshot height changed.")

  val diff = diff(expected, actual)
  if (diff.changedPixels > 0) {
    ImageIO.write(diff.image, "png", File(reportDir, "${screenshot.name}.diff.png"))
  }
  assertTrue(
    actual = diff.changedPixels == 0,
    message = "Screenshot changed by ${diff.changedPixels} pixels. " +
      "See ${reportDir.path}/${screenshot.name}.actual.png and " +
      "${reportDir.path}/${screenshot.name}.diff.png.",
  )
}

@OptIn(ExperimentalTestApi::class)
fun updateDemoScreenshot(screenshot: DemoScreenshot) = runComposeUiTest {
  val actual = captureDemoScreenshot(screenshot)
  val expectedFile = File("src/jvmTest/resources/screenshots/${screenshot.name}.png")
  expectedFile.parentFile.mkdirs()
  ImageIO.write(actual, "png", expectedFile)
}

@OptIn(ExperimentalTestApi::class)
private fun ComposeUiTest.captureDemoScreenshot(screenshot: DemoScreenshot): BufferedImage {
  setContent {
    Box(
      modifier = Modifier
        .requiredSize(width = ScreenshotWidth.dp, height = ScreenshotHeight.dp)
        .testTag(ScreenshotTargetTag),
    ) {
      Demo(startDestination = screenshot.startDestination)
    }
  }

  waitForIdle()

  return onNodeWithTag(ScreenshotTargetTag).captureToImage().toAwtImage()
}

private fun screenshotResourcePaths(screenshot: DemoScreenshot): List<String> {
  val platformPath = if (System.getProperty("os.name").orEmpty().startsWith("Linux")) {
    "screenshots/linux/${screenshot.name}.png"
  } else {
    null
  }
  return listOfNotNull(platformPath, "screenshots/${screenshot.name}.png")
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
private const val ScreenshotWidth = 1024
private const val ScreenshotHeight = 600
private const val DiffColor = 0xFFFF00FF.toInt()
