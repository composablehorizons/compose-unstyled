package com.composables.core.demo

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.core.HorizontalDivider
import com.composables.core.Menu
import com.composables.core.MenuButton
import com.composables.core.MenuContent
import com.composables.core.MenuItem
import com.composables.core.rememberMenuState
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Clipboard
import com.composables.icons.lucide.ClipboardPaste
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Maximize
import com.composables.icons.lucide.Scissors
import com.composables.icons.lucide.Trash
import com.composables.icons.lucide.Trash2

@Composable
fun MenuDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Menu(state = rememberMenuState(expanded = true)) {
            Box(Modifier.width(240.dp)) {
                MenuButton(Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White)) {
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
                    onClick = { /* TODO */ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(Lucide.Maximize, null)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Select all",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                HorizontalDivider(color = Color(0xFFBDBDBD))
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    onClick = { /* TODO */ }) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Image(Lucide.Copy, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Copy",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    enabled = false,
                    onClick = { /* TODO */ }) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(Lucide.Scissors, null, colorFilter = ColorFilter.tint(Color(0xFF9E9E9E)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Cut",
                            style = TextStyle.Default.copy(color = Color(0xFF9E9E9E)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                }
                MenuItem(
                    modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    enabled = false,
                    onClick = { /* TODO */ }) {
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
                HorizontalDivider(color = Color(0xFFBDBDBD))
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

@Composable
fun Text(text: String, style: TextStyle = TextStyle.Default, modifier: Modifier = Modifier) {
    BasicText(
        text,
        style = style,
        modifier = modifier
    )
}
