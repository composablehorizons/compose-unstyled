package com.composeunstyled

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import kotlin.test.Test

class ModalAndroidTest {

    @Test
    fun contentRespectsLocalLayoutDirection() = runTestSuite {
        testCase("content respects LocalLayoutDirection") {
            var showModal by mutableStateOf(true)
            setContent {
                if (showModal) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Modal {
                            val layoutDirection =
                                if (LocalLayoutDirection.current == LayoutDirection.Rtl) "rtl" else "ltr"
                            BasicText(layoutDirection, Modifier.testTag("layout_direction"))
                        }
                    }
                }
            }

            onNodeWithTag("layout_direction").assertTextEquals("rtl")
            showModal = false
            waitForIdle()
        }
    }
}
