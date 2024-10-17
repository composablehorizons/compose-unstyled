package com.composables.core.demo

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.core.*
import kotlin.time.Duration.Companion.seconds

@Composable
fun ScrollAreaDemo() {
    HorizontalScrollAreaDemo()
}


@Composable
fun VerticalScrollAreaDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val desserts = listOf(
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
                desserts.forEach { i ->
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

@Composable
fun HorizontalScrollAreaDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val state = rememberScrollState()

        ScrollArea(
            state = rememberScrollAreaState(state),
            modifier = Modifier
                .widthIn(max = 400.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .border(Dp.Hairline, Color.Black.copy(0.1f), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .wrapContentHeight()
        ) {
            Row(
                Modifier.horizontalScroll(state)
                    .systemBarsPadding()
                    .navigationBarsPadding()
                    .padding(start = 4.dp, end = 16.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..100).forEach { i ->
                    Box(Modifier.size(90.dp).clip(CircleShape).background(Color.Red))
                }
            }
            HorizontalScrollbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(12.dp)
                    .fillMaxWidth(),
            ) {
                Thumb(
                    modifier = Modifier
                        .padding(2.dp)
                        .width(12.dp)
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