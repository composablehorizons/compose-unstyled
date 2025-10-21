package com.composables.core

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.ComponentInteractiveSize
import com.composeunstyled.theme.Theme
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
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
                Text(Theme[strings][text])
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
                Text(Theme[strings][text])
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
