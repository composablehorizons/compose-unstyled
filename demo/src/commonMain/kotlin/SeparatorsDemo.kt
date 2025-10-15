package com.composeunstyled.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.core.HorizontalSeparator
import com.composables.core.VerticalSeparator
import com.composeunstyled.Text

@Composable
fun SeparatorsDemo() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFF8C42), Color(0xFFD65DB1)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .shadow(4.dp, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .width(240.dp)
        ) {
            Text("New Window", modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp))
            HorizontalSeparator(Color.LightGray)
            Text("New Tab", Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp))
            HorizontalSeparator(Color.LightGray)
            Text("New Incognito Tab", Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp))
            HorizontalSeparator(Color.LightGray)
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Text("Copy", modifier = Modifier.padding(8.dp).weight(1f), textAlign = TextAlign.Center)
                VerticalSeparator(Color.LightGray)
                Text("Cut", modifier = Modifier.padding(8.dp).weight(1f), textAlign = TextAlign.Center)
                VerticalSeparator(Color.LightGray)
                Text("Paste", Modifier.padding(8.dp).weight(1f), textAlign = TextAlign.Center)
            }
        }
    }
}
