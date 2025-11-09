package com.composeunstyled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composeunstyled.theme.*
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ThemeTest {

    @Test
    fun canSetAndReadStringValueFromTheme() = runComposeUiTest {
        val testTheme = buildTheme {
            properties[strings] = mapOf(
                text to "Hello Theme"
            )
        }

        setContent {
            testTheme {
                BasicText(Theme[strings][text])
            }
        }

        onNodeWithText("Hello Theme").assertExists()
    }

    @Test
    fun themeTokenValuesUpdateDisplayedText() = runComposeUiTest {
        var themeValue by mutableStateOf("Hello")

        setContent {
            val testTheme = buildTheme {
                properties[strings] = mapOf(
                    text to themeValue
                )
            }

            testTheme {
                BasicText(Theme[strings][text])
            }
        }

        onNodeWithText("Hello").assertExists()

        themeValue = "World"

        onNodeWithText("World").assertExists()
        onNodeWithText("Hello").assertDoesNotExist()
    }

    @Test
    fun defaultComponentInteractiveSizeIsPropagatedToModifier() = runComposeUiTest {
        val testTheme = buildTheme {
            defaultComponentInteractiveSize = ComponentInteractiveSize(
                touchInteractionSize = 48.dp,
                nonTouchInteractionSize = 32.dp
            )
        }

        setContent {
            testTheme {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("interactive-box")
                )
            }
        }

        onNodeWithTag("interactive-box")
            .assertWidthIsEqualTo(32.dp)
            .assertHeightIsEqualTo(32.dp)
    }

    val strings = ThemeProperty<String>("strings")
    val text = ThemeToken<String>("label")
}
