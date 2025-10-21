package com.composables.core

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.unit.dp
import com.composeunstyled.minimumInteractiveComponentSize
import com.composeunstyled.theme.ComponentInteractiveSize
import com.composeunstyled.theme.buildTheme
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ThemeTest {
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
            .assertWidthIsEqualTo(48.dp)
            .assertHeightIsEqualTo(48.dp)
    }
}