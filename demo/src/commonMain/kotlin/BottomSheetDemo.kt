package com.composeunstyled.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.composables.core.BottomSheet
import com.composables.core.SheetDetent
import com.composables.core.rememberBottomSheetState
import com.composeunstyled.Text

private val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

@Composable
fun BottomSheetDemo() {
    val halfExpandedDetent = SheetDetent("half") { containerHeight, _ ->
        containerHeight * 0.5f
    }
    val state = rememberBottomSheetState(
        initialDetent = halfExpandedDetent,
        detents = listOf(halfExpandedDetent, SheetDetent.FullyExpanded)
    )

    BottomSheet(state = state, modifier = Modifier.testTag("sheet")) {
        Column(
            Modifier
                .testTag("scrollable_content")
                .verticalScroll(rememberScrollState())
        ) {
            repeat(10) { index ->
                Text(
                    "item_$index",
                    Modifier
                        .testTag("item_$index")
                        .height(100.dp)
                )
            }
        }
    }
}
