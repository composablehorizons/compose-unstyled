package com.composeunstyled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TextTest {

    @BeforeTest
    fun setUp() {
        ComposeUnstyledFlags.strictTextColorResolutionOrder = true
    }

    @Test
    fun defaultColorIsUnspecified() = runComposeUiTest {
        setContent {
            Text("Hello World")
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Unspecified }
    }

    @Test
    fun setsColorViaProperty() = runComposeUiTest {
        setContent {
            Text("Hello World", color = Color.Red)
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun setsColorViaTextStyle() = runComposeUiTest {
        setContent {
            Text("Hello World", style = TextStyle(color = Color.Red))
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun setsColorViaTextStyleLocal() = runComposeUiTest {
        setContent {
            ProvideTextStyle(TextStyle(color = Color.Red)) {
                Text("Hello World")
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun setsColorViaColorLocal() = runComposeUiTest {
        setContent {
            ProvideContentColor(Color.Red) {
                Text("Hello World")
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun colorParameterOverridesTextStyleColor() = runComposeUiTest {
        setContent {
            Text("Hello World", color = Color.Red, style = TextStyle(color = Color.Blue))
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun colorParameterOverridesLocalTextStyleColor() = runComposeUiTest {
        setContent {
            ProvideTextStyle(TextStyle(color = Color.Blue)) {
                Text("Hello World", color = Color.Red)
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun colorParameterOverridesLocalContentColor() = runComposeUiTest {
        setContent {
            ProvideContentColor(Color.Blue) {
                Text("Hello World", color = Color.Red)
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun textStyleColorOverridesLocalTextStyleColor() = runComposeUiTest {
        setContent {
            ProvideTextStyle(TextStyle(color = Color.Blue)) {
                Text("Hello World", style = TextStyle(color = Color.Red))
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun textStyleColorOverridesLocalContentColor() = runComposeUiTest {
        setContent {
            ProvideContentColor(Color.Blue) {
                Text("Hello World", style = TextStyle(color = Color.Red))
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }

    @Test
    fun localTextStyleColorOverridesLocalContentColor() = runComposeUiTest {
        setContent {
            ProvideTextStyle(TextStyle(color = Color.Red)) {
                ProvideContentColor(Color.Blue) {
                    Text("Hello World")
                }
            }
        }

        onNodeWithText("Hello World")
            .assertTextStyle { it.color == Color.Red }
    }



    private fun SemanticsNodeInteraction.assertTextStyle(matcher: (TextStyle) -> Boolean): SemanticsNodeInteraction {
        return this.assert(
            SemanticsMatcher("TextStyle") { node ->
                val textLayoutResults = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(textLayoutResults)

                if (textLayoutResults.isNotEmpty()) {
                    val textStyle = textLayoutResults[0].layoutInput.style
                    matcher(textStyle)
                } else {
                    false
                }
            }
        )
    }
}