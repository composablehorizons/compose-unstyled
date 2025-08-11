package com.composables.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.composeunstyled.Text
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

    val strings = ThemeProperty<String>("strings")
    val text = ThemeToken<String>("label")
}
