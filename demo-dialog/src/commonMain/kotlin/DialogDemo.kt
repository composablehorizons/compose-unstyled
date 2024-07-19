package com.composables.core.demo

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.composetheme.ComposeTheme
import com.composables.composetheme.base
import com.composables.composetheme.blue500
import com.composables.composetheme.buildComposeTheme
import com.composables.composetheme.colors
import com.composables.composetheme.gray100
import com.composables.composetheme.gray900
import com.composables.composetheme.round
import com.composables.composetheme.roundXL
import com.composables.composetheme.shapes
import com.composables.composetheme.textStyles
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.DialogProperties
import com.composables.core.rememberDialogState

val AppTheme = buildComposeTheme { }

@Composable
fun DialogDemo() {
    AppTheme {
        val dialogState = rememberDialogState()

        Box(
            modifier = Modifier.fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF4A90E2), Color(0xFF50C9C3))))
                .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(Modifier.clip(RoundedCornerShape(6.dp))
                .clickable(role = Role.Button) { dialogState.visible = true }
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)) {
                BasicText("Show dialog", style = TextStyle.Default.copy(fontWeight = FontWeight(500)))
            }
            Dialog(state = dialogState) {
                DialogPanel(
                    modifier = Modifier.systemBarsPadding()
                        .widthIn(min = 280.dp, max = 560.dp)
                        .padding(20.dp)
                        .clip(ComposeTheme.shapes.roundXL)
                        .border(1.dp, ComposeTheme.colors.gray100, ComposeTheme.shapes.roundXL)
                        .background(Color.White),
                    enter = scaleIn(initialScale = 0.8f) + fadeIn(tween(durationMillis = 250)),
                    exit = scaleOut(targetScale = 0.6f) + fadeOut(tween(durationMillis = 150)),
                ) {
                    Column {
                        Column(Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp)) {
                            BasicText(
                                text = "Update Available",
                                style = ComposeTheme.textStyles.base.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(Modifier.height(8.dp))
                            BasicText(
                                text = "A new version of the app is available. Please update to the latest version.",
                                style = ComposeTheme.textStyles.base.copy(color = ComposeTheme.colors.gray900)
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Box(Modifier.padding(12.dp)
                            .align(Alignment.End)
                            .clip(ComposeTheme.shapes.round)
                            .clickable(role = Role.Button) { /* TODO */ }
                            .padding(horizontal = 12.dp, vertical = 8.dp)) {
                            BasicText(
                                text = "Update",
                                style = ComposeTheme.textStyles.base.copy(color = ComposeTheme.colors.blue500)
                            )
                        }
                    }
                }
            }
        }
    }
}
