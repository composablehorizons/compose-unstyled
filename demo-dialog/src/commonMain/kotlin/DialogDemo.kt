package com.composeunstyled.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.rememberDialogState
import com.composeunstyled.Button
import com.composeunstyled.Text

@Composable
fun DialogDemo() {
    val dialogState = rememberDialogState(initiallyVisible = false)

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF4A90E2), Color(0xFF50C9C3))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { dialogState.visible = true },
            backgroundColor = Color.White,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("Show dialog", fontWeight = FontWeight(500),color = Color(0xFF1A1A1A))
        }
        Dialog(state = dialogState) {
            DialogPanel(
                modifier = Modifier
                    .displayCutoutPadding()
                    .systemBarsPadding()
                    .widthIn(min = 280.dp, max = 560.dp)
                    .padding(20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
                exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
            ) {
                Column {
                    Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                        Text("Update Available", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "A new version of the app is available. Please update to the latest version.",
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { /* TODO */ }, modifier = Modifier.padding(12.dp).align(Alignment.End),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text("Update", color = Color(0xFF0D99FF),fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
