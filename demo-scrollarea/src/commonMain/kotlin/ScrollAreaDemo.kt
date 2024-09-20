package com.composables.core.demo

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.core.ScrollArea
import com.composables.core.Thumb
import com.composables.core.ThumbVisibility
import com.composables.core.VerticalScrollbar
import com.composables.core.rememberScrollAreaState
import kotlin.time.Duration.Companion.seconds

@Composable
fun ScrollAreaDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val items = listOf(
            "Cupcake",
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow",
            "Nougat",
            "Oreo",
            "Pie",
            "Quince",
            "Red Velvet Cake",
            "Snow Cone",
            "Tiramisu",
            "Upside-down Cake",
            "Vanilla Custard",
            "Waffle",
            "Xmas Pudding",
            "Yogurt Parfait",
            "Zabaglione"
        )

        val state = rememberScrollState()

        ScrollArea(
            state = rememberScrollAreaState(state),
            modifier = Modifier
                .widthIn(max = 400.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .border(Dp.Hairline, Color.Black.copy(0.1f), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .fillMaxSize()
        ) {
            Column(
                Modifier.verticalScroll(state)
                    .systemBarsPadding()
                    .navigationBarsPadding()
                    .padding(start = 4.dp, end = 16.dp)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                BasicText(
                    text = "Deserts",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(Modifier.height(12.dp))
                items.forEach { i ->
                    BasicText(i, modifier = Modifier.padding(4.dp).fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                }
            }
            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(12.dp)
                    .fillMaxHeight(),
            ) {
                Thumb(
                    modifier = Modifier
                        .padding(2.dp)
                        .height(12.dp)
                        .background(Color.Black.copy(0.33f), RoundedCornerShape(100)),
                    thumbVisibility = ThumbVisibility.HideWhileIdle(
                        enter = fadeIn(),
                        exit = fadeOut(),
                        hideDelay = 1.seconds
                    ),
                )
            }
        }
    }
}
