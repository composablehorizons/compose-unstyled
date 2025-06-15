package com.composeunstyled.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.BottomSheet
import com.composables.core.DragIndication
import com.composables.core.SheetDetent
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberBottomSheetState
import com.composeunstyled.Button
import com.composeunstyled.Text

private val Peek = SheetDetent("peek") { containerHeight, sheetHeight ->
    containerHeight * 0.6f
}

@Composable
fun BottomSheetDemo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF800080), Color(0xFFDA70D6))))
            .padding(top = 12.dp)
            .statusBarsPadding()
            .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues()),
    ) {
        val sheetState = rememberBottomSheetState(
            initialDetent = Peek,
            detents = listOf(Hidden, Peek, FullyExpanded),
            animationSpec = tween(5000)

        )

        Button(
            onClick = { sheetState.targetDetent = Peek },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues()),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            backgroundColor = Color.White
        ) {
            Text("Show Sheet", fontWeight = FontWeight(500))
        }

        BottomSheet(
            state = sheetState,
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .imePadding(),
        ) {
            Box(Modifier.fillMaxWidth().height(600.dp), contentAlignment = Alignment.TopCenter) {
                DragIndication(
                    modifier = Modifier.padding(top = 22.dp)
                        .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                        .width(32.dp)
                        .height(4.dp)
                )
            }
        }
    }
}
