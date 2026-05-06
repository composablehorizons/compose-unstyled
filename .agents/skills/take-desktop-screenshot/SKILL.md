---
name: take-desktop-screenshot
description: Capture a screenshot of a Compose Desktop app so the agent can verify what the app actually rendered by temporarily wrapping the desktop entry composable with a Compose-only screenshot composable, running the app, then cleaning up the instrumentation.
metadata:
  short-description: Capture Compose Desktop screenshots
---

# Take Desktop Screenshot

Use this when asked to take a screenshot of a Compose Desktop app rendered from source, especially when the agent needs to verify the contents of the app visually. This workflow temporarily instruments the desktop entry point, saves a PNG to the Desktop, then removes the instrumentation.

## Workflow

1. Locate the desktop `main()` and the entry composable it launches.
2. Add `TakeScreenshot` in that same desktop entry file.
3. Wrap the entry composable with `TakeScreenshot`.
4. Run the desktop app.
5. Verify the PNG rendered correctly.
6. Clean up: remove the screenshot imports, remove `TakeScreenshot`, and restore the original entry composable call.

## Entry Wrap

Change this:

```kotlin
fun main() = singleWindowApplication(title = "App") {
  App()
}
```

To this temporarily:

```kotlin
fun main() = singleWindowApplication(title = "App") {
  TakeScreenshot {
    App()
  }
}
```

## Composable

Paste this into the same desktop entry file as `main()`:

```kotlin
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toAwtImage
import java.io.File
import javax.imageio.ImageIO
import kotlinx.coroutines.delay

@Composable
fun TakeScreenshot(
  content: @Composable () -> Unit,
) {
  val graphicsLayer = rememberGraphicsLayer()

  LaunchedEffect(graphicsLayer) {
    delay(2_000)
    val image = graphicsLayer.toImageBitmap()
    val desktop = File(System.getProperty("user.home"), "Desktop")
    var output = File(desktop, "screenshot.png")
    var index = 1

    while (output.exists()) {
      output = File(desktop, "screenshot ($index).png")
      index += 1
    }

    ImageIO.write(image.toAwtImage(), "png", output)
    println("Saved Compose screenshot to ${output.absolutePath}")
    kotlin.system.exitProcess(0)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .drawWithContent {
        graphicsLayer.record {
          this@drawWithContent.drawContent()
        }
        drawLayer(graphicsLayer)
      },
  ) {
    content()
  }
}
```

Important: use `graphicsLayer.record { this@drawWithContent.drawContent() }`. Do not use the explicit `density/layoutDirection/size` overload for this workflow; it can capture the layer but miss child content on Desktop.

## Run And Verify

Run the desktop app using the repo's normal command. If it is a Compose Hot Reload demo, prefer the project's configured desktop run command, for example:

```bash
./gradlew :demo-material-impl:hotRunDesktop
```

The app exits itself after saving. Check the newest `~/Desktop/screenshot*.png` with image inspection. If it is blank, keep iterating before cleanup.

## Cleanup

After a good screenshot:

- Restore `main()` to call the entry composable directly.
- Remove `TakeScreenshot`.
- Remove screenshot-only imports.
- Check `git diff` to make sure only intended non-screenshot changes remain.
