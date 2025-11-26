package com.composables.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.composeunstyled.runTestSuite
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class BottomSheet {

    @Test
    fun stateChangesDuringInteractions() = runTestSuite {
        testCase("isIdle is false, when dragging sheet with scrollable content") {
            lateinit var sheetState: BottomSheetState
            setContent {
                val Peek = SheetDetent(identifier = "peek") { containerHeight, _ ->
                    containerHeight * 0.6f
                }
                sheetState = rememberBottomSheetState(
                    initialDetent = Peek,
                    detents = listOf(Peek, SheetDetent.FullyExpanded)
                )

                BottomSheet(
                    state = sheetState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .testTag("sheet")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        LazyColumn {
                            repeat(50) {
                                item {
                                    Text(
                                        text = "Item #${(it + 1)}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Drag the sheet
            onNodeWithTag("sheet").performTouchInput {
                swipeDown(
                    startY = centerY,
                    endY = centerY + 200f
                )
            }

            assertFalse(sheetState.isIdle)
        }
    }
}
