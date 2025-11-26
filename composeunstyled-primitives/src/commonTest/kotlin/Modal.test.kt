package com.composeunstyled

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ModalTest {

    @Test
    fun semantics() = runTestSuite {
        testCase("add isDialog semantic") {
            var showModal by mutableStateOf(false)

            setContent {
                if (showModal) {
                    Modal(onKeyEvent = { false }) {
                        Box(Modifier.testTag("modal_contents").size(40.dp))
                    }
                }
            }
            onNode(isDialog()).assertDoesNotExist()
            showModal = true
            onNode(isDialog()).assertExists()
        }
    }
}
