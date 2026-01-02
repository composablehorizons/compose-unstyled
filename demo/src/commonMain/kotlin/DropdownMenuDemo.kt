package com.composeunstyled.demo

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.core.Separator
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Clipboard
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Maximize
import com.composables.icons.lucide.Scissors
import com.composables.icons.lucide.Trash2
import com.composeunstyled.Button
import com.composeunstyled.Icon
import com.composeunstyled.LocalContentColor
import com.composeunstyled.Text
import com.composeunstyled.UnstyledDropdownMenu
import com.composeunstyled.UnstyledDropdownMenuPanel
import com.composeunstyled.platformtheme.dimmed
import com.composeunstyled.platformtheme.indications
import com.composeunstyled.platformtheme.interactiveSize
import com.composeunstyled.platformtheme.interactiveSizes
import com.composeunstyled.platformtheme.sizeDefault
import com.composeunstyled.theme.Theme
import kotlinx.coroutines.delay

@Composable
fun DropdownMenuDemo() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFFFED359), Color(0xFFFFBD66))))
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        class DropdownOption(
            val text: String,
            val icon: ImageVector,
            val enabled: Boolean = true,
            val dangerous: Boolean = false,
        )

        val options = listOf(
            DropdownOption("Select All", Lucide.Maximize),
            DropdownOption("Copy", Lucide.Copy),
            DropdownOption("Cut", Lucide.Scissors, enabled = false),
            DropdownOption("Paste", Lucide.Clipboard),
            DropdownOption("Delete", Lucide.Trash2, dangerous = true),
        )
        var expanded by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(500)
            expanded = true
        }

        UnstyledDropdownMenu(onExpandRequest = { expanded = true }) {
            Button(
                shape = RoundedCornerShape(6.dp),
                backgroundColor = Color.White,
                onClick = { expanded = true },
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                indication = Theme[indications][dimmed],
                modifier = Modifier.interactiveSize(Theme[interactiveSizes][sizeDefault])
            ) {
                Text("Options")
                Spacer(Modifier.width(8.dp))
                Icon(Lucide.ChevronDown, null)
            }

            UnstyledDropdownMenuPanel(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                backgroundColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
                    .width(240.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp)),
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 120, easing = LinearOutSlowInEasing),
                    initialScale = 0.8f,
                    transformOrigin = TransformOrigin(0f, 0f)
                ) + fadeIn(tween(durationMillis = 30)),
                exit = scaleOut(animationSpec = tween(durationMillis = 1, delayMillis = 75), targetScale = 1f)
                        + fadeOut(tween(durationMillis = 75))
            ) {
                options.forEachIndexed { index, option ->
                    if (index == 1 || index == options.lastIndex) {
                        Separator(color = Color(0xFFBDBDBD))
                    }
                    Button(
                        onClick = { expanded = false },
                        enabled = option.enabled,
                        modifier = Modifier
                            .padding(4.dp)
                            .interactiveSize(Theme[interactiveSizes][sizeDefault])
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        contentColor = (if (option.dangerous) Color(0xFFC62828) else LocalContentColor.current)
                            .copy(alpha = if (option.enabled) 1f else 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        indication = Theme[indications][dimmed],
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Icon(option.icon, null)
                        Spacer(Modifier.width(12.dp))
                        Text(option.text)
                    }
                }
            }
        }
    }
}
