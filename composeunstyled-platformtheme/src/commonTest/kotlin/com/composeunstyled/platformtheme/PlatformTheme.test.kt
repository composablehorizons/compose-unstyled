package com.composeunstyled.platformtheme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composeunstyled.theme.*
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class PlatformThemeTest {

    @Test
    fun canCustomizePlatformTheme() = runComposeUiTest {
        val customProperty = ThemeProperty<String>("customProperty")
        val customToken = ThemeToken<String>("customToken")
        val customValue = "Custom Value"

        val theme = buildPlatformTheme {
            properties[customProperty] = mapOf(
                customToken to customValue
            )
        }

        setContent {
            theme {
                BasicText(
                    text = Theme[customProperty][customToken],
                    modifier = Modifier.testTag("custom-text")
                )
            }
        }

        onNodeWithTag("custom-text")
            .assertTextEquals(customValue)
    }

    @Test
    fun customInteractiveSizeIsAppliedToModifier() = runComposeUiTest {
        val customSize = 64.dp

        val theme = buildPlatformTheme {
            properties[interactiveSizes] = mapOf(
                sizeDefault to customSize
            )
        }

        setContent {
            theme {
                Box(
                    modifier = Modifier
                        .interactiveSize(Theme[interactiveSizes][sizeDefault])
                        .testTag("interactive-box")
                )
            }
        }

        onNodeWithTag("interactive-box")
            .assertWidthIsAtLeast(customSize)
            .assertHeightIsAtLeast(customSize)
    }
}
