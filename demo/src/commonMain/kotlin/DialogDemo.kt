package com.composeunstyled.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.Scrim
import com.composables.core.rememberDialogState
import com.composeunstyled.Button
import com.composeunstyled.Text
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.platformtheme.text5
import com.composeunstyled.platformtheme.textStyles
import com.composeunstyled.theme.Theme

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
            shape = RoundedCornerShape(6.dp),
            indication = Theme[indications][dimmed]
        ) {
            Text("Show dialog")
        }
        Dialog(state = dialogState) {
            Scrim(scrimColor = Color.Black.copy(0.3f), enter = fadeIn(), exit = fadeOut())
            DialogPanel(
                backgroundColor = Color.White,
                contentColor = Color.Black,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(20.dp)
                    .displayCutoutPadding()
                    .systemBarsPadding()
                    .widthIn(max = 560.dp)
                    .padding(20.dp),
                enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
                exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
            ) {
                Column {
                    Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                        Text("Update Available", style = Theme[textStyles][text5])
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
                        indication = Theme[indications][dimmed]
                    ) {
                        Text("Update", color = Color(0xFF0D99FF))
                    }
                }
            }
        }
    }
}
