package com.composeunstyled.demo

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.*
import com.composables.icons.lucide.*
import com.composeunstyled.Text

@Composable
fun DropdownMenuDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Menu(state = rememberMenuState(expanded = true)) {
            Box(Modifier.width(240.dp)) {
                MenuButton(
                    shape = RoundedCornerShape(6.dp),
                    backgroundColor = Color.White,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Options",
                            style = TextStyle.Default.copy(fontWeight = FontWeight(500))
                        )
                        Spacer(Modifier.width(8.dp))
                        Image(Lucide.ChevronDown, null)
                    }
                }
            }

            MenuContent(
                modifier = Modifier.padding(vertical = 4.dp)
                    .width(240.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
                    initialScale = 0.8f,
                    transformOrigin = TransformOrigin(0f, 0f)
                ) + fadeIn(tween(durationMillis = 30)),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 1, delayMillis = 75),
                    targetScale = 1f
                ) + fadeOut(tween(durationMillis = 75))
            ) {
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Image(Lucide.Maximize, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Select all", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                }
                HorizontalSeparator(color = Color(0xFFBDBDBD))
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Image(Lucide.Copy, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Copy", modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp))
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    enabled = false,
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Image(Lucide.Scissors, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cut",
                        style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    enabled = false,
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(Lucide.Clipboard, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Paste",
                            style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                HorizontalSeparator(color = Color(0xFFBDBDBD))
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    onClick = { /* TODO */ }) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(Lucide.Trash2, null, colorFilter = ColorFilter.tint(Color(0xFFC62828)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Delete",
                            style = TextStyle.Default.copy(color = Color(0xFFC62828)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
